(ns scicloj.napkinsketch.impl.sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output."
  (:require [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]))

;; ---- Color Resolution (data-space) ----

(defn resolve-color
  "Resolve a color value to [r g b a]. Handles column values, fixed colors, and defaults."
  [all-colors color-val fixed-color cfg]
  (cond
    color-val (defaults/color-for all-colors color-val (:palette cfg))
    fixed-color (if (string? fixed-color) (defaults/hex->rgba fixed-color) fixed-color)
    :else (defaults/hex->rgba (:default-color cfg))))

;; ---- Geometry Extraction (stat → layer descriptors) ----

(defmulti extract-layer
  "Extract data-space geometry from a resolved view and its stat result.
   Returns a layer descriptor map."
  (fn [view stat all-colors cfg] (:mark view)))

(defmethod extract-layer :point [view stat all-colors cfg]
  (let [numeric-color? (= (:color-type view) :numerical)
        ;; For numeric color: compute global min/max for normalization
        all-color-vals (when numeric-color?
                         (mapcat :color-values (:points stat)))
        c-min (when (seq all-color-vals) (reduce min all-color-vals))
        c-max (when (seq all-color-vals) (reduce max all-color-vals))
        c-range (when (and c-min c-max) (- (double c-max) (double c-min)))]
    {:mark :point
     :style (cond-> {:opacity (or (:fixed-alpha view) (:point-opacity cfg))
                     :radius (or (:fixed-size view) (:point-radius cfg))}
              (:jitter view) (assoc :jitter (:jitter view)))
     :groups (vec
              (for [{:keys [color xs ys sizes alphas shapes row-indices color-values]} (:points stat)]
                (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                         :xs (vec xs) :ys (vec ys)}
                  color (assoc :color-label (str color))
                  (and numeric-color? color-values)
                  (assoc :colors (vec (map (fn [v]
                                             (let [t (if (and c-range (pos? c-range))
                                                       (/ (- (double v) (double c-min)) c-range)
                                                       0.5)]
                                               (defaults/gradient-color t)))
                                           color-values)))
                  sizes (assoc :sizes (vec sizes))
                  alphas (assoc :alphas (vec alphas))
                  shapes (assoc :shapes (vec shapes))
                  row-indices (assoc :row-indices (vec row-indices)))))}))

(defmethod extract-layer :bar [view stat all-colors cfg]
  {:mark :bar
   :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
   :groups (vec
            (for [{:keys [color bin-maps]} (:bins stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :bars (vec (for [{:keys [min max count]} bin-maps]
                            {:lo min :hi max :count count}))}))})

(defmethod extract-layer :line [view stat all-colors cfg]
  (cond-> {:mark :line
           :style {:stroke-width (or (:fixed-size view) (:line-width cfg))}
           :groups (vec
                    (concat
                     ;; Regression lines
                     (when-let [lines (:lines stat)]
                       (for [{:keys [color x1 y1 x2 y2]} lines]
                         {:color (resolve-color all-colors color (:fixed-color view) cfg)
                          :x1 x1 :y1 y1 :x2 x2 :y2 y2}))
                     ;; Polylines
                     (when-let [pts (:points stat)]
                       (for [{:keys [color xs ys]} pts]
                         {:color (resolve-color all-colors color (:fixed-color view) cfg)
                          :xs (vec xs) :ys (vec ys)}))))}
    ;; Confidence ribbons from :lm {:se true}
    (:ribbons stat)
    (assoc :ribbons (vec
                     (for [{:keys [color xs ymins ymaxs]} (:ribbons stat)]
                       {:color (resolve-color all-colors color (:fixed-color view) cfg)
                        :xs (vec xs) :ymins (vec ymins) :ymaxs (vec ymaxs)})))))

(defmethod extract-layer :step [view stat all-colors cfg]
  {:mark :step
   :style {:stroke-width (or (:fixed-size view) (:line-width cfg))}
   :groups (vec
            (for [{:keys [color xs ys]} (:points stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :xs (vec xs) :ys (vec ys)}))})

(defmethod extract-layer :rect [view stat all-colors cfg]
  (if (:bars stat)
    ;; Categorical bars (from :count stat)
    (let [position (or (:position view) :dodge)
          raw-groups (vec
                      (for [{:keys [color counts]} (:bars stat)]
                        {:color (resolve-color all-colors color (:fixed-color view) cfg)
                         :label (str color)
                         :counts (vec counts)}))
          ;; For :fill position, normalize counts per category to sum to 1.0
          groups (if (= position :fill)
                   (let [cat-totals (reduce (fn [acc g]
                                              (reduce (fn [a {:keys [category count]}]
                                                        (update a category (fnil + 0) count))
                                                      acc
                                                      (:counts g)))
                                            {}
                                            raw-groups)]
                     (mapv (fn [g]
                             (update g :counts
                                     (fn [counts]
                                       (mapv (fn [{:keys [category count]}]
                                               (let [total (get cat-totals category 1)]
                                                 {:category category
                                                  :count (if (pos? total)
                                                           (/ (double count) (double total))
                                                           0.0)}))
                                             counts))))
                           raw-groups))
                   raw-groups)]
      {:mark :rect
       :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
       :position position
       :categories (vec (:categories stat))
       :groups groups})
    ;; Value bars (from :identity stat)
    {:mark :rect
     :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
     :position (or (:position view) :dodge)
     :groups (vec
              (for [{:keys [color xs ys]} (:points stat)]
                {:color (resolve-color all-colors color (:fixed-color view) cfg)
                 :xs (vec xs) :ys (vec ys)}))}))

(defmethod extract-layer :text [view stat all-colors cfg]
  {:mark :text
   :style {:font-size (or (:font-size view) 10)}
   :groups (vec
            (for [{:keys [color xs ys labels]} (:points stat)]
              (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                       :xs (vec xs) :ys (vec ys)}
                labels (assoc :labels (vec (map str labels))))))})

(defmethod extract-layer :label [view stat all-colors cfg]
  {:mark :label
   :style {:font-size (or (:font-size view) 10)}
   :groups (vec
            (for [{:keys [color xs ys labels]} (:points stat)]
              (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                       :xs (vec xs) :ys (vec ys)}
                labels (assoc :labels (vec (map str labels))))))})

(defmethod extract-layer :area [view stat all-colors cfg]
  (cond-> {:mark :area
           :style {:opacity (or (:fixed-alpha view) 0.5)}
           :groups (vec
                    (for [{:keys [color xs ys]} (:points stat)]
                      {:color (resolve-color all-colors color (:fixed-color view) cfg)
                       :xs (vec xs) :ys (vec ys)}))}
    (:position view) (assoc :position (:position view))))

(defmethod extract-layer :errorbar [view stat all-colors cfg]
  {:mark :errorbar
   :style {:stroke-width (or (:fixed-size view) 1.5)
           :cap-width (or (:cap-width view) 6)}
   :groups (vec
            (for [{:keys [color xs ys ymins ymaxs]} (:points stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :xs (vec xs) :ys (vec ys)
               :ymins (vec ymins) :ymaxs (vec ymaxs)}))})

(defmethod extract-layer :lollipop [view stat all-colors cfg]
  {:mark :lollipop
   :style {:radius (or (:fixed-size view) (:point-radius cfg))
           :stroke-width 1.5}
   :groups (vec
            (for [{:keys [color xs ys]} (:points stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :xs (vec xs) :ys (vec ys)}))})

(defmethod extract-layer :boxplot [view stat all-colors cfg]
  (let [color-cats (:color-categories stat)]
    {:mark :boxplot
     :style {:box-width (or (:box-width view) 0.6)
             :stroke-width (or (:fixed-size view) 1.5)}
     :color-categories color-cats
     :boxes (vec
             (for [b (:boxes stat)]
               (cond-> {:category (:category b)
                        :color (resolve-color all-colors (:color b) (:fixed-color view) cfg)
                        :median (:median b) :q1 (:q1 b) :q3 (:q3 b)
                        :whisker-lo (:whisker-lo b) :whisker-hi (:whisker-hi b)}
                 (:color b) (assoc :color-category (:color b))
                 (seq (:outliers b)) (assoc :outliers (vec (:outliers b))))))}))

(defmethod extract-layer :violin [view stat all-colors cfg]
  (let [color-cats (:color-categories stat)]
    {:mark :violin
     :style {:opacity (or (:fixed-alpha view) 0.7)
             :stroke-width (or (:fixed-size view) 1.0)}
     :color-categories color-cats
     :violins (vec
               (for [v (:violins stat)]
                 (cond-> {:category (:category v)
                          :color (resolve-color all-colors (:color v) (:fixed-color view) cfg)
                          :ys (vec (:ys v))
                          :densities (vec (:densities v))}
                   (:color v) (assoc :color-category (:color v)))))}))

(defmethod extract-layer :tile [view stat all-colors cfg]
  (let [fill-col (:fill view)
        ;; Two paths: bin2d stat produces :tiles directly;
        ;; identity stat with :fill uses point groups
        tiles (if (:tiles stat)
                ;; bin2d path
                (let [[f-lo f-hi] (:fill-range stat)
                      f-span (max 1e-10 (- (double f-hi) (double f-lo)))]
                  (vec (for [{:keys [x-lo x-hi y-lo y-hi fill]} (:tiles stat)
                             :let [t (/ (- (double fill) (double f-lo)) f-span)]]
                         {:x-lo x-lo :x-hi x-hi :y-lo y-lo :y-hi y-hi
                          :color (defaults/gradient-color t)})))
                ;; identity path — each point is a tile at (x, y) with fill value
                (let [data (:data view)
                      fill-vals (when fill-col (data fill-col))
                      f-lo (when (seq fill-vals) (reduce min fill-vals))
                      f-hi (when (seq fill-vals) (reduce max fill-vals))
                      f-span (max 1e-10 (- (double (or f-hi 1)) (double (or f-lo 0))))]
                  (vec (for [{:keys [xs ys]} (:points stat)
                             i (range (count xs))
                             :let [xv (nth xs i)
                                   yv (nth ys i)]]
                         {:x-lo xv :x-hi xv :y-lo yv :y-hi yv
                          :color (if fill-vals
                                   (let [fv (nth fill-vals i)
                                         t (/ (- (double fv) (double f-lo)) f-span)]
                                     (defaults/gradient-color t))
                                   (defaults/gradient-color 0.5))}))))]
    {:mark :tile
     :style {:opacity (or (:fixed-alpha view) 1.0)}
     :tiles tiles}))

(defn- marching-squares-segments
  "Apply marching squares to a density grid for a given threshold.
   Returns a sequence of line segments [[x1 y1] [x2 y2]] in data coordinates.
   Grid is n×n with densities[i*n+j] at cell (i,j)."
  [densities n-grid threshold x-lo y-lo x-step y-step]
  (let [grid-val (fn [i j]
                   (if (and (< i n-grid) (< j n-grid) (>= i 0) (>= j 0))
                     (nth densities (+ (* i n-grid) j))
                     0.0))
        interp (fn [v1 v2 p1 p2]
                 ;; Linear interpolation between two points based on threshold.
                 ;; Clamp t to [0,1] to prevent numerical blowup when v1 ≈ v2.
                 (let [t (/ (- threshold v1) (max 1e-15 (- v2 v1)))
                       t (max 0.0 (min 1.0 t))]
                   (+ p1 (* t (- p2 p1)))))]
    (loop [i 0 segments (transient [])]
      (if (>= i (dec n-grid))
        (persistent! segments)
        (let [segs (loop [j 0 segs segments]
                     (if (>= j (dec n-grid))
                       segs
                       ;; Four corners of the cell: TL(i,j) TR(i+1,j) BR(i+1,j+1) BL(i,j+1)
                       (let [tl (grid-val i j)
                             tr (grid-val (inc i) j)
                             br (grid-val (inc i) (inc j))
                             bl (grid-val i (inc j))
                             ;; Corner coordinates in data space (cell center positions)
                             x0 (+ x-lo (* (+ i 0.5) x-step))
                             x1 (+ x-lo (* (+ i 1.5) x-step))
                             y0 (+ y-lo (* (+ j 0.5) y-step))
                             y1 (+ y-lo (* (+ j 1.5) y-step))
                             ;; Cell index (4 bits: TL TR BR BL)
                             idx (bit-or (if (>= tl threshold) 8 0)
                                         (if (>= tr threshold) 4 0)
                                         (if (>= br threshold) 2 0)
                                         (if (>= bl threshold) 1 0))
                             ;; Edge midpoints with interpolation
                             top [(interp tl tr x0 x1) y0]
                             right [x1 (interp tr br y0 y1)]
                             bottom [(interp bl br x0 x1) y1]
                             left [x0 (interp tl bl y0 y1)]]
                         (recur (inc j)
                                (case (int idx)
                                  (0 15) segs
                                  (1 14) (conj! segs [left bottom])
                                  (2 13) (conj! segs [bottom right])
                                  (3 12) (conj! segs [left right])
                                  (4 11) (conj! segs [top right])
                                  5 (-> segs (conj! [left top]) (conj! [bottom right]))
                                  (6 9) (conj! segs [top bottom])
                                  (7 8) (conj! segs [left top])
                                  10 (-> segs (conj! [top right]) (conj! [left bottom]))
                                  segs)))))]
          (recur (inc i) segs))))))

(defn- join-segments
  "Join line segments into polylines by connecting shared endpoints.
   Segments are [[x1 y1] [x2 y2]] pairs. Uses tolerance-based matching
   for floating-point coordinates. Returns a sequence of polylines,
   each a vector of [x y] points."
  [segments]
  (if (empty? segments)
    []
    (let [;; Round coords to 6 decimal places for matching
          round6 (fn [v] (/ (Math/round (* v 1e6)) 1e6))
          snap (fn [[x y]] [(round6 x) (round6 y)])
          ;; Build adjacency: endpoint -> list of (other-endpoint, segment-index)
          adj (reduce
               (fn [m [idx [p1 p2]]]
                 (let [k1 (snap p1) k2 (snap p2)]
                   (-> m
                       (update k1 (fnil conj []) {:other p2 :other-key k2 :idx idx})
                       (update k2 (fnil conj []) {:other p1 :other-key k1 :idx idx}))))
               {}
               (map-indexed vector segments))
          ;; Walk chains greedily
          used (boolean-array (count segments))]
      (loop [remaining (range (count segments))
             polylines []]
        (let [start-idx (first (drop-while #(aget used %) remaining))]
          (if (nil? start-idx)
            polylines
            (do
              (aset used start-idx true)
              (let [[p1 p2] (nth segments start-idx)
                    ;; Walk forward from p2
                    forward
                    (loop [chain [p2]
                           cur-key (snap p2)]
                      (let [neighbors (get adj cur-key [])
                            next-seg (first (filter #(not (aget used (:idx %))) neighbors))]
                        (if next-seg
                          (do
                            (aset used (:idx next-seg) true)
                            (recur (conj chain (:other next-seg))
                                   (:other-key next-seg)))
                          chain)))
                    ;; Walk backward from p1
                    backward
                    (loop [chain []
                           cur-key (snap p1)]
                      (let [neighbors (get adj cur-key [])
                            next-seg (first (filter #(not (aget used (:idx %))) neighbors))]
                        (if next-seg
                          (do
                            (aset used (:idx next-seg) true)
                            (recur (conj chain (:other next-seg))
                                   (:other-key next-seg)))
                          chain)))
                    polyline (vec (concat (rseq backward) [p1] forward))]
                (recur remaining (conj polylines polyline))))))))))

(defmethod extract-layer :contour [view stat all-colors cfg]
  (let [{:keys [grid fill-range]} stat]
    (if (nil? grid)
      {:mark :contour :levels []}
      (let [{:keys [densities n-grid x-lo x-hi y-lo y-hi x-step y-step max-d]} grid
            n-levels (or (:levels view) 5)
            ;; Threshold levels at evenly spaced fractions of max density
            ;; Skip very low (< 10%) to avoid noise contours
            thresholds (vec (for [i (range 1 (inc n-levels))]
                              (* max-d (/ (double i) (inc n-levels)))))
            levels (vec (for [threshold thresholds
                              :let [t (/ threshold max-d)
                                    segments (marching-squares-segments
                                              densities n-grid threshold
                                              x-lo y-lo x-step y-step)
                                    polylines (join-segments segments)]
                              :when (seq polylines)]
                          {:threshold threshold
                           :t t
                           :color (defaults/gradient-color t)
                           :polylines (vec polylines)}))]
        {:mark :contour
         :levels levels
         :style {:stroke-width (or (:fixed-size view) 1.5)
                 :opacity (or (:fixed-alpha view) 0.8)}
         :x-domain [x-lo x-hi]
         :y-domain [y-lo y-hi]}))))

(defmethod extract-layer :ridgeline [view stat all-colors cfg]
  (let [violins (:violins stat)
        categories (:categories stat)]
    {:mark :ridgeline
     :style {:opacity (or (:fixed-alpha view) 0.7)}
     :ridges (vec (for [v violins]
                    (cond-> {:category (:category v)
                             :color (resolve-color all-colors (:color v) (:fixed-color view) cfg)
                             :ys (vec (:ys v))
                             :densities (vec (:densities v))}
                      (:color v) (assoc :color-category (:color v)))))
     :categories (vec categories)}))

(defmethod extract-layer :rug [view stat all-colors cfg]
  {:mark :rug
   :style {:length (or (:length view) 6)
           :stroke-width (or (:fixed-size view) 1.0)
           :opacity (or (:fixed-alpha view) 0.5)}
   :side (or (:side view) :x)
   :groups (vec
            (for [{:keys [color xs ys]} (:points stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :xs (vec xs) :ys (vec ys)}))})

(defmethod extract-layer :pointrange [view stat all-colors cfg]
  {:mark :pointrange
   :style {:radius (or (:fixed-size view) 3.5)
           :stroke-width 1.5}
   :groups (vec
            (for [{:keys [color xs ys ymins ymaxs]} (:points stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :xs (vec xs) :ys (vec ys)
               :ymins (vec ymins) :ymaxs (vec ymaxs)}))})

(defmethod extract-layer :default [view stat all-colors cfg]
  (extract-layer (assoc view :mark :point) stat all-colors cfg))

;; ---- Domain Helpers ----

(defn collect-domain
  "Collect and merge domains from stat results along axis-key."
  [stat-results axis-key scale-spec]
  (let [vals (mapcat (fn [sr]
                       (let [d (axis-key sr)]
                         (if (and (= 2 (count d)) (number? (first d)))
                           d (map str d))))
                     stat-results)]
    (when (seq vals)
      (if (number? (first vals))
        (scale/pad-domain [(reduce min vals) (reduce max vals)] scale-spec)
        (distinct vals)))))

(defn compute-global-y-domain
  "Compute global y-domain, handling stacked bar/area accumulation.
  Extends domain to include 0 for marks that draw stems from baseline
  (lollipop, value-bar)."
  [stat-results views scale-spec]
  (let [fill-views (filter #(= :fill (:position %)) views)
        stacked-views (filter #(= :stack (:position %)) views)
        has-fill? (seq fill-views)
        has-stacked? (seq stacked-views)
        zero-baseline-marks #{:lollipop :value-bar}
        needs-zero? (some #(zero-baseline-marks (:mark %)) views)]
    (cond
      ;; Fill mode: counts are normalized to [0, 1]
      has-fill?
      [0.0 1.0]

      has-stacked?
      (let [;; Stacked bars: accumulate counts per category
            count-stats (filter :categories stat-results)
            all-cats (distinct (mapcat :categories count-stats))
            max-bar-stack (if (seq all-cats)
                            (reduce max 0
                                    (for [cat all-cats]
                                      (reduce + (for [sr count-stats
                                                      {:keys [counts]} (:bars sr)
                                                      {:keys [category count]} counts
                                                      :when (= category cat)]
                                                  count))))
                            0)
            ;; Stacked area: accumulate y-values per x across groups
            area-views (filter #(and (= :stack (:position %)) (= :area (:mark %))) views)
            area-stats (mapv (fn [v] (nth stat-results (.indexOf ^java.util.List (vec views) v)))
                             area-views)
            max-area-stack (if (seq area-stats)
                             (let [all-groups (mapcat :points area-stats)
                                   x->y-sum (reduce (fn [acc {:keys [xs ys]}]
                                                      (reduce (fn [a [x y]]
                                                                (update a x (fnil + 0) y))
                                                              acc
                                                              (map vector xs ys)))
                                                    {}
                                                    all-groups)]
                               (if (seq x->y-sum)
                                 (reduce max 0 (vals x->y-sum))
                                 0))
                             0)
            other-yd (mapcat (fn [sr]
                               (when-not (or (:categories sr)
                                             (some #{sr} area-stats))
                                 (:y-domain sr)))
                             stat-results)
            hi (max max-bar-stack max-area-stack
                    (if (seq other-yd) (reduce max 0 other-yd) 0))]
        (if (pos? hi)
          (scale/pad-domain [0 hi] scale-spec)
          [0 1]))

      :else
      (let [dom (collect-domain stat-results :y-domain scale-spec)]
        (if (and needs-zero? (sequential? dom) (number? (first dom)))
          (scale/pad-domain [(min 0.0 (double (first dom)))
                             (max 0.0 (double (second dom)))]
                            scale-spec)
          dom)))))

;; ---- Tick Computation ----

(def ^:private date-fmt-year
  (java.time.format.DateTimeFormatter/ofPattern "yyyy"))

(def ^:private date-fmt-month-year
  (java.time.format.DateTimeFormatter/ofPattern "MMM yyyy"))

(def ^:private date-fmt-month-day
  (java.time.format.DateTimeFormatter/ofPattern "MMM d"))

(defn- format-date-tick
  "Format an epoch-day as a date string, choosing granularity based on
   the total span of the domain (in days)."
  [epoch-day span-days]
  (let [d (java.time.LocalDate/ofEpochDay (long epoch-day))]
    (cond
      (> span-days 1500) (.format d date-fmt-year)
      (> span-days 180) (.format d date-fmt-month-year)
      (> span-days 30) (.format d date-fmt-month-day)
      :else (str d))))

(defn compute-ticks
  "Compute tick values and labels for a domain+pixel range, using wadogo transiently.
   When temporal? is true, tick values are epoch-days and labels are date strings."
  ([domain pixel-range scale-spec spacing]
   (compute-ticks domain pixel-range scale-spec spacing false))
  ([domain pixel-range scale-spec spacing temporal?]
   (if (scale/categorical-domain? domain)
     (let [s (scale/make-scale domain pixel-range scale-spec)]
       {:values (vec (ws/ticks s))
        :labels (mapv str (ws/ticks s))
        :categorical? true})
     (let [s (scale/make-scale domain pixel-range scale-spec)
           n (scale/tick-count (Math/abs (double (- (second pixel-range) (first pixel-range)))) spacing)
           ticks (ws/ticks s n)
           labels (if temporal?
                    (let [span-days (- (double (second domain)) (double (first domain)))]
                      (mapv #(format-date-tick % span-days) ticks))
                    (scale/format-ticks s ticks))]
       {:values (vec ticks)
        :labels (vec labels)
        :categorical? false}))))

;; ---- Layout Detection ----

(defn infer-layout
  "Determine layout type from views."
  [views]
  (let [facet-rows (seq (remove nil? (map :facet-row views)))
        facet-cols (seq (remove nil? (map :facet-col views)))]
    (cond
      (or facet-rows facet-cols) :facet-grid
      :else (let [x-vars (distinct (map :x views))
                  y-vars (distinct (map :y views))]
              (if (or (> (count x-vars) 1) (> (count y-vars) 1))
                :multi-variable
                :single)))))

;; ---- Per-Panel Resolution ----

(defn resolve-panel-views
  "Resolve views and compute stats for a group of views belonging to one panel.
   If pre-resolved views are provided, skips resolve-view.
   Returns {:resolved [...] :stat-results [...] :layers [...]}."
  [panel-views all-colors cfg & {:keys [resolved]}]
  (let [resolved (or resolved (mapv view/resolve-view panel-views))
        stat-results (mapv #(stat/compute-stat (assoc % :cfg (merge cfg (:cfg %)))) resolved)
        layers (vec (map (fn [rv sr]
                           (extract-layer rv sr all-colors cfg))
                         resolved stat-results))]
    {:resolved resolved :stat-results stat-results :layers layers}))

(defn- collect-colors
  "Resolve views and collect color categories across all views.
   Attaches :__resolved to each view for downstream re-use.
   Returns {:resolved-all :numeric-color? :all-colors :color-cols :tagged-views}."
  [non-ann-views]
  (let [resolved-all (mapv view/resolve-view non-ann-views)
        tagged-views (mapv (fn [v rv] (assoc v :__resolved rv)) non-ann-views resolved-all)
        numeric-color? (some #(= :numerical (:color-type %)) resolved-all)
        all-colors (when-not numeric-color?
                     (let [color-views (filter #(and (view/column-ref? (:color %))
                                                     (:data %)) resolved-all)]
                       (when (seq color-views)
                         (vec (distinct (mapcat #((:data %) (:color %)) color-views))))))
        color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved-all))]
    {:resolved-all resolved-all
     :numeric-color? numeric-color?
     :all-colors all-colors
     :color-cols color-cols
     :tagged-views tagged-views}))

(defn- compute-grid
  "Compute grid dimensions and facet/variable lists from layout type.
   Returns {:grid-rows :grid-cols :facet-row-vals :facet-col-vals :x-vars :y-vars}."
  [non-ann-views layout-type]
  (let [x-vars (distinct (map :x non-ann-views))
        y-vars (distinct (map :y non-ann-views))
        facet-row-vals (when (= layout-type :facet-grid)
                         (vec (distinct (remove #(= "_" %) (map :facet-row non-ann-views)))))
        facet-col-vals (when (= layout-type :facet-grid)
                         (vec (distinct (remove #(= "_" %) (map :facet-col non-ann-views)))))
        [grid-rows grid-cols]
        (case layout-type
          :single [1 1]
          :facet-grid [(max 1 (count (or facet-row-vals ["_"])))
                       (max 1 (count (or facet-col-vals ["_"])))]
          :multi-variable [(count y-vars) (count x-vars)])]
    {:grid-rows grid-rows :grid-cols grid-cols
     :facet-row-vals facet-row-vals :facet-col-vals facet-col-vals
     :x-vars x-vars :y-vars y-vars}))

(defn- compute-panel-dims
  "Compute per-panel pixel dimensions and margin."
  [cfg layout-type grid-rows grid-cols width height]
  (let [multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
        m (if multi? (:margin-multi cfg) (:margin cfg))
        pw (if multi?
             (double (:panel-size cfg))
             (double (/ width grid-cols)))
        ph (if multi?
             (double (:panel-size cfg))
             (double (/ height grid-rows)))]
    {:m m :pw pw :ph ph}))

(defn- group-panels
  "Group views into panel descriptors by layout type."
  [non-ann-views layout-type facet-row-vals facet-col-vals x-vars y-vars]
  (case layout-type
    :single
    [{:views non-ann-views :row 0 :col 0}]

    :facet-grid
    (let [rvs (or (seq facet-row-vals) ["_"])
          cvs (or (seq facet-col-vals) ["_"])]
      (for [[ri rv] (map-indexed vector rvs)
            [ci cv] (map-indexed vector cvs)]
        {:views (filterv #(and (= (or (:facet-row %) "_") (str rv))
                               (= (or (:facet-col %) "_") (str cv)))
                         non-ann-views)
         :row ri :col ci
         :row-label (when (not= rv "_") (str rv))
         :col-label (when (not= cv "_") (str cv))}))

    :multi-variable
    (for [[ri yv] (map-indexed vector y-vars)
          [ci xv] (map-indexed vector x-vars)]
      {:views (filterv #(and (= xv (:x %)) (= yv (:y %))) non-ann-views)
       :row ri :col ci
       :var-x xv :var-y yv
       :col-label (defaults/fmt-name xv)
       :row-label (defaults/fmt-name yv)})))

(defn- build-panels
  "Build panel specs with domains, ticks, and annotations."
  [panel-data layout-type scale-mode coord-type
   x-scale-spec y-scale-spec annotations
   x-vars y-vars pw ph m cfg]
  (let [all-stat-results (mapcat :stat-results panel-data)
        global-x-dom (or (:domain x-scale-spec)
                         (collect-domain all-stat-results :x-domain x-scale-spec))
        global-y-dom (or (:domain y-scale-spec)
                         (compute-global-y-domain all-stat-results
                                                  (mapcat :views panel-data) y-scale-spec))
        mv-col-x-doms (when (= layout-type :multi-variable)
                        (into {} (for [xv x-vars]
                                   [xv (let [pds (filter #(= xv (:var-x %)) panel-data)
                                             scatter-pds (filter #(not= (:var-x %) (:var-y %)) pds)
                                             srs (mapcat :stat-results scatter-pds)]
                                         (when (seq srs)
                                           (collect-domain srs :x-domain x-scale-spec)))])))
        mv-row-y-doms (when (= layout-type :multi-variable)
                        (into {} (for [yv y-vars]
                                   [yv (let [pds (filter #(= yv (:var-y %)) panel-data)
                                             scatter-pds (filter #(not= (:var-x %) (:var-y %)) pds)
                                             srs (mapcat :stat-results scatter-pds)]
                                         (when (seq srs)
                                           (collect-domain srs :y-domain y-scale-spec)))])))]
    (vec
     (for [pd panel-data
           :when (seq (:views pd))]
       (let [local-srs (:stat-results pd)
             local-x-dom (collect-domain local-srs :x-domain x-scale-spec)
             local-y-dom (compute-global-y-domain local-srs (:views pd) y-scale-spec)
             [eff-x-dom eff-y-dom]
             (case layout-type
               :single
               [global-x-dom global-y-dom]
               :facet-grid
               [(case scale-mode
                  (:shared :free-y) global-x-dom
                  local-x-dom)
                (case scale-mode
                  (:shared :free-x) global-y-dom
                  local-y-dom)]
               :multi-variable
               (let [diagonal? (= (:var-x pd) (:var-y pd))]
                 [(or (get mv-col-x-doms (:var-x pd)) local-x-dom)
                  (if diagonal? local-y-dom
                      (or (get mv-row-y-doms (:var-y pd)) local-y-dom))]))
             [x-dom' y-dom'] (if (= coord-type :flip)
                               [eff-y-dom eff-x-dom]
                               [eff-x-dom eff-y-dom])
             [x-sspec' y-sspec'] (if (= coord-type :flip)
                                   [y-scale-spec x-scale-spec]
                                   [x-scale-spec y-scale-spec])
             ;; Detect temporal axes from view flags
             resolved-views (:resolved pd)
             x-temporal? (some :x-temporal? resolved-views)
             y-temporal? (some :y-temporal? resolved-views)
             [x-temp? y-temp?] (if (= coord-type :flip)
                                 [y-temporal? x-temporal?]
                                 [x-temporal? y-temporal?])
             x-px [m (- pw m)]
             y-px [(- ph m) m]
             x-ticks (when x-dom' (compute-ticks x-dom' x-px x-sspec' (:tick-spacing-x cfg) x-temp?))
             y-ticks (when y-dom' (compute-ticks y-dom' y-px y-sspec' (:tick-spacing-y cfg) y-temp?))]
         (cond-> {:x-domain (vec (if (sequential? x-dom') x-dom' [x-dom']))
                  :y-domain (vec (if (sequential? y-dom') y-dom' [y-dom']))
                  :x-scale x-sspec'
                  :y-scale y-sspec'
                  :coord coord-type
                  :x-ticks (or x-ticks {:values [] :labels [] :categorical? false})
                  :y-ticks (or y-ticks {:values [] :labels [] :categorical? false})
                  :layers (or (:layers pd) [])
                  :row (:row pd)
                  :col (:col pd)}
           (seq annotations) (assoc :annotations annotations)
           (:row-label pd) (assoc :row-label (:row-label pd))
           (:col-label pd) (assoc :col-label (:col-label pd))))))))

(defn- resolve-labels
  "Resolve effective title and axis labels."
  [non-ann-views x-vars y-vars x-scale-spec y-scale-spec
   title x-label y-label auto-label?]
  (let [view-title (:title (first non-ann-views))
        view-x-label (:x-label (first non-ann-views))
        view-y-label (:y-label (first non-ann-views))]
    {:eff-title (or title view-title)
     :eff-x-label (or x-label
                      view-x-label
                      (:label x-scale-spec)
                      (when auto-label?
                        (when-let [x (first x-vars)] (defaults/fmt-name x))))
     :eff-y-label (or y-label
                      view-y-label
                      (:label y-scale-spec)
                      (when auto-label?
                        (when-let [y (first y-vars)]
                          (when (not= y (first x-vars))
                            (defaults/fmt-name y)))))}))

(defn- build-legend
  "Build legend from resolved views and color info."
  [resolved-all numeric-color? all-colors color-cols cfg]
  (cond
    numeric-color?
    (let [color-views (filter #(and (view/column-ref? (:color %))
                                    (:data %)) resolved-all)
          all-vals (mapcat #((:data %) (:color %)) color-views)
          c-min (reduce min all-vals)
          c-max (reduce max all-vals)]
      {:title (first color-cols)
       :type :continuous
       :min c-min :max c-max
       :stops (vec (for [t (range 0.0 1.01 0.25)]
                     {:t t :color (defaults/gradient-color t)}))})
    all-colors
    {:title (first color-cols)
     :entries (vec (for [cat all-colors]
                     {:label (str cat)
                      :color (defaults/color-for all-colors cat (:palette cfg))}))}))

(defn- compute-layout-dims
  "Compute layout dimensions: padding, legend width, total size."
  [cfg layout-type eff-title eff-x-label eff-y-label
   subtitle caption
   legend facet-row-vals facet-col-vals
   grid-rows grid-cols pw ph multi? panels legend-position]
  (let [x-label-pad (if eff-x-label (:label-offset cfg) 0)
        ;; y-label-pad must account for y-tick label width (e.g. category names)
        tick-fsize (:font-size defaults/theme)
        max-y-tick-len (reduce max 0
                               (for [p panels
                                     :let [labels (get-in p [:y-ticks :labels])]
                                     label (or labels [])]
                                 (count label)))
        y-tick-width (* max-y-tick-len (/ (double tick-fsize) 2.0))
        y-label-pad (if eff-y-label
                      (+ (:label-offset cfg) (max 0.0 (- y-tick-width 12.0)))
                      0)
        title-pad (if eff-title (:title-offset cfg) 0)
        subtitle-pad (if subtitle 16 0)
        caption-pad (if caption 18 0)
        legend-pos (or legend-position :right)
        legend-w (if (and legend (= legend-pos :right)) (:legend-width cfg) 0)
        legend-h (if (and legend (#{:bottom :top} legend-pos)) 30 0)
        has-col-strips? (or (and (= layout-type :facet-grid) (seq facet-col-vals))
                            multi?)
        has-row-strips? (or (and (= layout-type :facet-grid) (seq facet-row-vals))
                            multi?)
        strip-h (if has-col-strips? (:strip-height cfg 16) 0)
        strip-w (if has-row-strips? 60 0)
        top-legend-pad (if (= legend-pos :top) legend-h 0)]
    {:x-label-pad x-label-pad
     :y-label-pad y-label-pad
     :title-pad (+ title-pad subtitle-pad top-legend-pad)
     :subtitle-pad subtitle-pad
     :caption-pad caption-pad
     :legend-w legend-w
     :legend-h legend-h
     :strip-h strip-h
     :strip-w strip-w
     :total-w (+ y-label-pad (* grid-cols pw) strip-w legend-w)
     :total-h (+ title-pad subtitle-pad top-legend-pad strip-h (* grid-rows ph) x-label-pad
                 caption-pad
                 (if (= legend-pos :bottom) legend-h 0))}))

;; ---- Main Entry Point ----

(def ^:private polar-supported-marks
  "Marks that render correctly under polar coordinates."
  #{:point :bar :rect :text :rug})

(defn- validate-polar-marks
  "Check that all resolved views use marks compatible with polar coordinates.
   Throws an ex-info with details when an unsupported mark is found."
  [resolved-views coord-type]
  (when (= coord-type :polar)
    (doseq [v resolved-views
            :let [m (:mark v)]
            :when (and m (not (polar-supported-marks m)))]
      (throw (ex-info (str "Mark :" (name m) " is not supported with polar coordinates. "
                           "Supported polar marks: " (sort polar-supported-marks))
                      {:mark m :supported polar-supported-marks})))))

(defn views->sketch
  "Resolve views + options into a sketch — a fully resolved plot specification
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output."
  ([views] (views->sketch views {}))
  ([views {:keys [width height config x-label y-label title subtitle caption
                  scales palette theme legend-position] :as opts}]
   (let [cfg (cond-> (merge defaults/defaults config) palette (assoc :palette palette))
         resolved-theme (merge defaults/theme theme)
         width (or width (:width cfg))
         height (or height (:height cfg))
         views (if (map? views) [views] views)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)
         layout-type (infer-layout non-ann-views)
         scale-mode (or scales :shared)

         ;; Colors (also resolves all views once — tagged for reuse)
         {:keys [resolved-all numeric-color? all-colors color-cols tagged-views]}
         (collect-colors non-ann-views)

         ;; Scale & coord specs
         x-scale-spec (or (:x-scale (first non-ann-views)) {:type :linear})
         y-scale-spec (or (:y-scale (first non-ann-views)) {:type :linear})
         coord-type (or (:coord (first non-ann-views)) :cartesian)
         ;; Polar+mark compatibility check
         _ (validate-polar-marks resolved-all coord-type)

         ;; Annotations
         annotations (vec (for [a ann-views]
                            (select-keys a [:mark :intercept :lo :hi :alpha])))

         ;; Grid
         {:keys [grid-rows grid-cols facet-row-vals facet-col-vals x-vars y-vars]}
         (compute-grid non-ann-views layout-type)

         ;; Panel pixel dimensions
         {:keys [m pw ph]}
         (compute-panel-dims cfg layout-type grid-rows grid-cols width height)

         ;; Group tagged views into panels and resolve stats/layers
         ;; (tagged views carry :__resolved so resolve-view isn't called again)
         panel-groups (group-panels tagged-views layout-type
                                    facet-row-vals facet-col-vals x-vars y-vars)
         panel-data (mapv (fn [pg]
                            (if (seq (:views pg))
                              (let [pre-resolved (mapv :__resolved (:views pg))]
                                (merge pg (resolve-panel-views (:views pg) all-colors cfg
                                                               :resolved pre-resolved)))
                              pg))
                          panel-groups)

         ;; Build panel specs with domains, ticks, annotations
         panels (build-panels panel-data layout-type scale-mode coord-type
                              x-scale-spec y-scale-spec annotations
                              x-vars y-vars pw ph m cfg)

         ;; Ridgeline renders categories vertically and numeric horizontally
         ;; (manual axis transposition). Swap domains, ticks, and scale specs in
         ;; the panel so that sx becomes numeric (for bottom ticks) and sy becomes
         ;; categorical (for left ticks), matching the visual layout.
         has-ridgeline? (some #(= :ridgeline (:mark %)) non-ann-views)
         panels (if has-ridgeline?
                  (mapv (fn [p]
                          (-> p
                              (assoc :x-domain (:y-domain p)
                                     :y-domain (:x-domain p)
                                     :x-ticks (:y-ticks p)
                                     :y-ticks (:x-ticks p)
                                     :x-scale (:y-scale p)
                                     :y-scale (:x-scale p))))
                        panels)
                  panels)

         ;; Labels — suppress auto-labels for multi-variable grids and polar coords
         multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
         auto-label? (and (not multi?) (coord/show-ticks? coord-type))
         {:keys [eff-title eff-x-label eff-y-label]}
         (resolve-labels non-ann-views x-vars y-vars x-scale-spec y-scale-spec
                         title x-label y-label auto-label?)

;; Subtitle and caption — opts override view-level
         subtitle (or subtitle (:subtitle (first non-ann-views)))
         caption (or caption (:caption (first non-ann-views)))
         ;; Swap labels when axes are visually transposed
         swap-labels? (or (= coord-type :flip)
                          has-ridgeline?)
         [eff-x-label eff-y-label] (if swap-labels?
                                     [eff-y-label eff-x-label]
                                     [eff-x-label eff-y-label])

         ;; Legend
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg)

         ;; Layout dimensions
         layout-dims (compute-layout-dims cfg layout-type eff-title eff-x-label eff-y-label
                                          subtitle caption
                                          legend facet-row-vals facet-col-vals
                                          grid-rows grid-cols pw ph multi? panels legend-position)]

     {:width width :height height :margin m
      :total-width (:total-w layout-dims) :total-height (:total-h layout-dims)
      :panel-width pw :panel-height ph
      :grid {:rows grid-rows :cols grid-cols}
      :layout-type layout-type
      :title eff-title
      :subtitle subtitle
      :caption caption
      :x-label eff-x-label :y-label eff-y-label
      :legend legend
      :legend-position (or legend-position :right)
      :panels panels
      :theme resolved-theme
      :layout (select-keys layout-dims [:x-label-pad :y-label-pad :title-pad
                                        :subtitle-pad :caption-pad
                                        :legend-w :legend-h :strip-h :strip-w])})))
