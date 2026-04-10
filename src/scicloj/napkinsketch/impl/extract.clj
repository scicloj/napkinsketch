(ns scicloj.napkinsketch.impl.extract
  "Extract data-space geometry from resolved views and stat results.
   Produces layer descriptor maps — plain Clojure maps with mark type,
   style, and groups of data-space coordinates."
  (:require [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [tech.v3.datatype.functional :as dfn]
            [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]))

;; ---- Color Resolution (data-space) ----

(defn resolve-color
  "Resolve a color value to [r g b a]. Handles column values, fixed colors, and defaults."
  [all-colors color-val fixed-color cfg]
  (cond
    (some? color-val) (defaults/color-for all-colors color-val (:palette cfg))
    fixed-color (if (string? fixed-color) (defaults/hex->rgba fixed-color) fixed-color)
    :else (defaults/hex->rgba (:default-color cfg))))

(defn- apply-nudge
  "Apply nudge-x/nudge-y offsets to a layer's groups.
   Nudge shifts data coordinates by a constant amount — orthogonal to
   position adjustment (dodge/stack). Works on any layer with groups
   containing :xs/:ys numeric buffers."
  [layer {:keys [nudge-x nudge-y]}]
  (if (or nudge-x nudge-y)
    (update layer :groups
            (fn [gs]
              (mapv (fn [g]
                      (cond-> g
                        (and nudge-x (:xs g))
                        (update :xs dfn/+ (double nudge-x))
                        (and nudge-y (:ys g))
                        (update :ys dfn/+ (double nudge-y))
                        (and nudge-y (:ymins g))
                        (update :ymins dfn/+ (double nudge-y))
                        (and nudge-y (:ymaxs g))
                        (update :ymaxs dfn/+ (double nudge-y))))
                    gs)))
    layer))

(defn- default-position
  "Default position for marks that normally dodge.
   When :color is the same column as :x, dodge is suppressed — each x-band
   already contains exactly one color group, so dodging just shrinks and
   offsets the mark unnecessarily."
  [view]
  (or (:position view)
      (if (and (:color view) (= (:color view) (:x view)))
        :identity
        :dodge)))

(defn- check-stat
  "Assert that stat result contains expected key for the given mark.
   Raises a clear error instead of letting nil propagate to the renderer."
  [stat expected-key mark]
  (when-not (contains? stat expected-key)
    (throw (ex-info (str "Stat result for :" (name mark)
                         " mark must contain :" (name expected-key)
                         ", got keys: " (pr-str (keys stat)))
                    {:mark mark :expected expected-key :stat-keys (keys stat)}))))

(defn- extract-xy-groups
  "Extract groups from stat :points, resolving colors. Common to most mark types.
   Options:
     :with-range? — include :ymins/:ymaxs (errorbar, pointrange)
     :with-labels? — include :labels from :labels key (text, label marks)"
  [view stat all-colors cfg & {:keys [with-range? with-labels?]}]
  (let [groups (vec
                (for [{:keys [color xs ys ymins ymaxs labels]} (:points stat)]
                  (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                           :xs xs :ys ys}
                    (some? color) (assoc :label (defaults/fmt-category-label color))
                    (and with-range? ymins) (assoc :ymins ymins)
                    (and with-range? ymaxs) (assoc :ymaxs ymaxs)
                    (and with-labels? labels) (assoc :labels (mapv defaults/fmt-category-label labels)))))]
    (when (and with-range? (seq groups)
               (not-any? :ymins groups))
      (throw (ex-info (str "errorbar/pointrange requires :ymin and :ymax columns. "
                           "Pass them as options: (sk/lay-errorbar :x :y {:ymin :lo :ymax :hi})")
                      {:mark (:mark view)})))
    (when (and with-labels? (seq groups)
               (not-any? :labels groups))
      (throw (ex-info (str "text/label mark requires a :text column. "
                           "Pass it as an option: (sk/lay-text :x :y {:text :label-column})")
                      {:mark (:mark view)})))
    groups))

;; ---- Geometry Extraction (stat → layer descriptors) ----

(defmulti extract-layer
  "Extract data-space geometry from a resolved view and its stat result.
   Returns a layer descriptor map."
  (fn [view stat all-colors cfg] (:mark view)))

;; ---- Doc methods (dispatching on [mark-key :doc]) ----

(defmethod extract-layer [:point :doc] [_ _ _ _] "Filled circle")
(defmethod extract-layer [:line :doc] [_ _ _ _] "Connected path")
(defmethod extract-layer [:step :doc] [_ _ _ _] "Horizontal-then-vertical path")
(defmethod extract-layer [:bar :doc] [_ _ _ _] "Vertical rectangles (binned)")
(defmethod extract-layer [:rect :doc] [_ _ _ _] "Positioned rectangles")
(defmethod extract-layer [:area :doc] [_ _ _ _] "Filled region under a curve")
(defmethod extract-layer [:tile :doc] [_ _ _ _] "Grid of colored cells")
(defmethod extract-layer [:contour :doc] [_ _ _ _] "Iso-value polylines")
(defmethod extract-layer [:boxplot :doc] [_ _ _ _] "Box-and-whisker")
(defmethod extract-layer [:violin :doc] [_ _ _ _] "Mirrored density shape")
(defmethod extract-layer [:ridgeline :doc] [_ _ _ _] "Stacked density curves")
(defmethod extract-layer [:pointrange :doc] [_ _ _ _] "Point with error bar")
(defmethod extract-layer [:errorbar :doc] [_ _ _ _] "Vertical error bar")
(defmethod extract-layer [:lollipop :doc] [_ _ _ _] "Stem with dot")
(defmethod extract-layer [:text :doc] [_ _ _ _] "Data-driven label")
(defmethod extract-layer [:label :doc] [_ _ _ _] "Label with background box")
(defmethod extract-layer [:rug :doc] [_ _ _ _] "Axis-margin tick marks")
(defmethod extract-layer :point [view stat all-colors cfg]
  (let [numeric-color? (= (:color-type view) :numerical)
        ;; For numeric color: compute global min/max for normalization
        all-color-buf (when numeric-color?
                        (let [bufs (keep :color-values (:points stat))]
                          (when (seq bufs) (dtype/concat-buffers bufs))))
        c-min (when all-color-buf (dfn/reduce-min all-color-buf))
        c-max (when all-color-buf (dfn/reduce-max all-color-buf))]
    (-> {:mark :point
         :style (cond-> {:opacity (or (:fixed-alpha view) (:point-opacity cfg))
                         :radius (or (:fixed-size view) (:point-radius cfg))}
                  (:jitter view) (assoc :jitter (:jitter view))
                  (and (:point-stroke cfg)
                       (not= (:point-stroke cfg) "none"))
                  (assoc :stroke (:point-stroke cfg)
                         :stroke-width (or (:point-stroke-width cfg) 0)))
         :groups (vec
                  (for [{:keys [color xs ys sizes alphas shapes row-indices color-values]} (:points stat)]
                    (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                             :xs xs :ys ys}
                      (some? color) (assoc :label (defaults/fmt-category-label color))
                      (and numeric-color? color-values)
                      (assoc :colors (vec (map (fn [v]
                                                 (let [t (defaults/normalize-midpoint v (or c-min 0) (or c-max 1) (:color-midpoint cfg))
                                                       grad-fn (:gradient-fn cfg)]
                                                   (grad-fn t)))
                                               color-values)))
                      sizes (assoc :sizes sizes)
                      alphas (assoc :alphas alphas)
                      shapes (assoc :shapes (vec shapes))
                      row-indices (assoc :row-indices row-indices))))}
        (cond-> (:position view) (assoc :position (:position view)))
        (apply-nudge view))))

(defmethod extract-layer :bar [view stat all-colors cfg]
  (check-stat stat :bins :bar)
  {:mark :bar
   :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
   :groups (vec
            (for [{:keys [color bin-maps]} (:bins stat)]
              {:color (resolve-color all-colors color (:fixed-color view) cfg)
               :bars (vec (for [{:keys [min max count]} bin-maps]
                            {:lo min :hi max :count count}))}))})

(defmethod extract-layer :line [view stat all-colors cfg]
  (-> (cond-> {:mark :line
               :style {:stroke-width (or (:fixed-size view) (:line-width cfg))
                       :opacity (or (:fixed-alpha view) 1.0)}
               :groups (vec
                        (concat
                         ;; Regression lines
                         (when-let [lines (:lines stat)]
                           (for [{:keys [color x1 y1 x2 y2]} lines]
                             {:color (resolve-color all-colors color (:fixed-color view) cfg)
                              :label (defaults/fmt-category-label color)
                              :x1 x1 :y1 y1 :x2 x2 :y2 y2}))
                         ;; Polylines
                         (when-let [pts (:points stat)]
                           (for [{:keys [color xs ys]} pts]
                             {:color (resolve-color all-colors color (:fixed-color view) cfg)
                              :label (defaults/fmt-category-label color)
                              :xs xs :ys ys}))))}
        ;; Confidence ribbons from :lm {:se true}
        (:ribbons stat)
        (assoc :ribbons (vec
                         (for [{:keys [color xs ymins ymaxs]} (:ribbons stat)]
                           {:color (resolve-color all-colors color (:fixed-color view) cfg)
                            :xs xs :ymins ymins :ymaxs ymaxs})))
        (:position view)
        (assoc :position (:position view)))
      (apply-nudge view)))

(defmethod extract-layer :step [view stat all-colors cfg]
  {:mark :step
   :style {:stroke-width (or (:fixed-size view) (:line-width cfg))
           :opacity (or (:fixed-alpha view) 1.0)}
   :groups (extract-xy-groups view stat all-colors cfg)})

(defmethod extract-layer :rect [view stat all-colors cfg]
  (if (:bars stat)
    ;; Categorical bars (from :count stat) -- :count stat already validated
    {:mark :rect
     :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
     :position (default-position view)
     :categories (vec (:categories stat))
     :groups (vec
              (for [{:keys [color counts]} (:bars stat)]
                {:color (resolve-color all-colors color (:fixed-color view) cfg)
                 :label (defaults/fmt-category-label color)
                 :counts (vec counts)}))}
    ;; Value bars (from :identity stat) -- need categorical x for band layout
    (do
      (when-not (= (:x-type view) :categorical)
        (throw (ex-info (str "Mark :rect (lay-value-bar) requires a categorical column for :x, "
                             "but " (:x view) " is " (name (or (:x-type view) :unknown))
                             ". Use lay-line/lay-point for numeric x, or convert " (:x view)
                             " to a string column.")
                        {:mark :rect :x (:x view) :x-type (:x-type view)})))
      {:mark :rect
       :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
       :position (default-position view)
       :groups (vec
                (for [{:keys [color xs ys]} (:points stat)]
                  {:color (resolve-color all-colors color (:fixed-color view) cfg)
                   :label (defaults/fmt-category-label color)
                   :xs xs :ys ys}))})))

(defmethod extract-layer :text [view stat all-colors cfg]
  (-> {:mark :text
       :style {:font-size (or (:font-size view) 10)
               :opacity (or (:fixed-alpha view) 1.0)}
       :groups (extract-xy-groups view stat all-colors cfg :with-labels? true)}
      (apply-nudge view)))

(defmethod extract-layer :label [view stat all-colors cfg]
  (-> {:mark :label
       :style {:font-size (or (:font-size view) 10)
               :opacity (or (:fixed-alpha view) 1.0)}
       :groups (extract-xy-groups view stat all-colors cfg :with-labels? true)}
      (apply-nudge view)))

(defmethod extract-layer :area [view stat all-colors cfg]
  (cond-> {:mark :area
           :style {:opacity (or (:fixed-alpha view) 0.5)}
           :groups (extract-xy-groups view stat all-colors cfg)}
    (:position view) (assoc :position (:position view))))

(defmethod extract-layer :errorbar [view stat all-colors cfg]
  (-> {:mark :errorbar
       :style {:stroke-width (or (:fixed-size view) 1.5)
               :cap-width (or (:cap-width view) 6)
               :opacity (or (:fixed-alpha view) 1.0)}
       :groups (extract-xy-groups view stat all-colors cfg :with-range? true)}
      (cond-> (:position view) (assoc :position (:position view)))
      (apply-nudge view)))

(defmethod extract-layer :lollipop [view stat all-colors cfg]
  {:mark :lollipop
   :style {:radius (or (:fixed-size view) (:point-radius cfg))
           :stroke-width 1.5
           :opacity (or (:fixed-alpha view) 1.0)}
   :position (default-position view)
   :groups (extract-xy-groups view stat all-colors cfg)})

(defmethod extract-layer :boxplot [view stat all-colors cfg]
  (check-stat stat :boxes :boxplot)
  (let [color-cats (:color-categories stat)]
    {:mark :boxplot
     :style {:box-width (or (:box-width view) 0.6)
             :stroke-width (or (:fixed-size view) 1.5)
             :opacity (or (:fixed-alpha view) 1.0)}
     :position (default-position view)
     :color-categories color-cats
     :boxes (vec
             (for [b (:boxes stat)]
               (cond-> {:category (:category b)
                        :color (resolve-color all-colors (:color b) (:fixed-color view) cfg)
                        :median (:median b) :q1 (:q1 b) :q3 (:q3 b)
                        :whisker-lo (:whisker-lo b) :whisker-hi (:whisker-hi b)}
                 (:color b) (assoc :color-category (:color b))
                 (seq (:outliers b)) (assoc :outliers (:outliers b)))))}))

(defmethod extract-layer :violin [view stat all-colors cfg]
  (check-stat stat :violins :violin)
  (let [color-cats (:color-categories stat)]
    {:mark :violin
     :style {:opacity (or (:fixed-alpha view) 0.7)
             :stroke-width (or (:fixed-size view) 1.0)}
     :position (default-position view)
     :color-categories color-cats
     :violins (vec
               (for [v (:violins stat)]
                 (cond-> {:category (:category v)
                          :color (resolve-color all-colors (:color v) (:fixed-color view) cfg)
                          :ys (:ys v)
                          :densities (:densities v)}
                   (:color v) (assoc :color-category (:color v)))))}))

(defn- min-step
  "Minimum step between sorted distinct values in a sequence."
  [vals]
  (let [sorted (vec (sort (distinct vals)))
        n (count sorted)]
    (if (<= n 1)
      1.0
      (reduce min (map #(- (double (sorted (inc %)))
                           (double (sorted %)))
                       (range (dec n)))))))

(defmethod extract-layer :tile [view stat all-colors cfg]
  (let [;; Accept :color as a synonym for :fill on tiles -- users coming
        ;; from the other marks reach for :color by default. If both are
        ;; set, :fill wins (explicit).
        fill-col (or (:fill view)
                     (when (let [c (:color view)] (and c (or (keyword? c) (string? c))))
                       (:color view)))
        grad-fn (:gradient-fn cfg)
        midpoint (:color-midpoint cfg)
        ;; Two paths: bin2d/kde2d stat produces :tiles as a dataset;
        ;; identity stat with :fill uses point groups
        tiles (if (and (:tiles stat)
                       (or (and (tc/dataset? (:tiles stat)) (pos? (tc/row-count (:tiles stat))))
                           (and (not (tc/dataset? (:tiles stat))) (seq (:tiles stat)))))
                ;; bin2d/kde2d path — :tiles is a dataset with :x-lo :x-hi :y-lo :y-hi :fill
                (let [tile-ds (:tiles stat)
                      [f-lo f-hi] (:fill-range stat)
                      ;; Derive :color column from :fill using gradient function
                      with-color (tc/add-column tile-ds :color
                                                (fn [ds]
                                                  (mapv (fn [f]
                                                          (grad-fn (defaults/normalize-midpoint
                                                                    f f-lo f-hi midpoint)))
                                                        (ds :fill))))]
                  (vec (tc/rows (tc/select-columns with-color
                                                   [:x-lo :x-hi :y-lo :y-hi :color])
                                :as-maps)))
                ;; identity path — derive tile bounds from point coordinates
                (let [data (:data view)
                      ;; Resolve col ref against the dataset so keyword/string
                      ;; mismatches still find the column.
                      resolved-fill (when fill-col (resolve/resolve-col-name data fill-col))
                      fill-vals (when resolved-fill (data resolved-fill))
                      f-lo (when (seq fill-vals) (dfn/reduce-min fill-vals))
                      f-hi (when (seq fill-vals) (dfn/reduce-max fill-vals))
                      all-xs (mapcat :xs (:points stat))
                      all-ys (mapcat :ys (:points stat))
                      x-half (/ (min-step all-xs) 2.0)
                      y-half (/ (min-step all-ys) 2.0)
                      ;; Build a dataset from the parallel buffers
                      all-x-vals (vec (mapcat :xs (:points stat)))
                      all-y-vals (vec (mapcat :ys (:points stat)))
                      pt-ds (tc/dataset {:x all-x-vals :y all-y-vals})
                      ;; Derive tile bounds as columns
                      with-bounds (-> pt-ds
                                      (tc/add-column :x-lo (dfn/- (pt-ds :x) x-half))
                                      (tc/add-column :x-hi (dfn/+ (pt-ds :x) x-half))
                                      (tc/add-column :y-lo (dfn/- (pt-ds :y) y-half))
                                      (tc/add-column :y-hi (dfn/+ (pt-ds :y) y-half)))
                      ;; Derive color column
                      with-color (tc/add-column with-bounds :color
                                                (fn [ds]
                                                  (if fill-vals
                                                    (mapv (fn [f]
                                                            (grad-fn (defaults/normalize-midpoint
                                                                      f (or f-lo 0) (or f-hi 1) midpoint)))
                                                          fill-vals)
                                                    (vec (repeat (tc/row-count ds)
                                                                 (grad-fn 0.5))))))]
                  (vec (tc/rows (tc/select-columns with-color
                                                   [:x-lo :x-hi :y-lo :y-hi :color])
                                :as-maps))))]
    {:mark :tile
     :style {:opacity (or (:fixed-alpha view) 1.0)}
     :tiles tiles}))

(defn- marching-squares-segments
  "Apply marching squares to a density grid for a given threshold.
   Returns a sequence of line segments [[x1 y1] [x2 y2]] in data coordinates.
   Grid is n×n with densities[i*n+j] at cell (i,j)."
  [^doubles densities n-grid threshold x-lo y-lo x-step y-step]
  (let [grid-val (fn [i j]
                   (if (and (< i n-grid) (< j n-grid) (>= i 0) (>= j 0))
                     (aget densities (+ (* i n-grid) j))
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
      {:mark :contour :levels [] :style {:stroke-width 1.5 :opacity 0.8}}
      (let [{:keys [densities n-grid x-lo x-hi y-lo y-hi x-step y-step max-d]} grid
            n-levels (or (:levels view) 5)
            max-d (if (and max-d (Double/isFinite max-d) (pos? max-d)) max-d 1.0)
            ;; Threshold levels evenly spaced from 5% to 95% of max density.
            ;; ggplot2 uses a similar approach via pretty() over the density
            ;; range. Starting at 5% (not 0%) avoids noise contours at the
            ;; very edge of the density falloff.
            thresholds (vec (for [i (range n-levels)]
                              (let [frac (+ 0.05 (* 0.9 (/ (double i) (max 1 (dec n-levels)))))]
                                (* max-d frac))))
            levels (vec (for [threshold thresholds
                              :let [t (/ threshold max-d)
                                    segments (marching-squares-segments
                                              densities n-grid threshold
                                              x-lo y-lo x-step y-step)
                                    polylines (join-segments segments)]
                              :when (seq polylines)]
                          {:threshold threshold
                           :t t
                           :color ((:gradient-fn cfg) t)
                           :polylines (vec polylines)}))]
        {:mark :contour
         :levels levels
         :style {:stroke-width (or (:fixed-size view) 1.5)
                 :opacity (or (:fixed-alpha view) 0.8)}
         :x-domain [x-lo x-hi]
         :y-domain [y-lo y-hi]}))))

(defmethod extract-layer :ridgeline [view stat all-colors cfg]
  (check-stat stat :violins :ridgeline)
  (let [violins (:violins stat)
        categories (:categories stat)]
    {:mark :ridgeline
     :style {:opacity (or (:fixed-alpha view) 0.7)}
     :ridges (vec (for [v violins]
                    (cond-> {:category (:category v)
                             :color (resolve-color all-colors (:color v) (:fixed-color view) cfg)
                             :ys (:ys v)
                             :densities (:densities v)}
                      (:color v) (assoc :color-category (:color v)))))
     :categories (vec categories)}))

(defmethod extract-layer :rug [view stat all-colors cfg]
  {:mark :rug
   :style {:length (or (:length view) 6)
           :stroke-width (or (:fixed-size view) 1.0)
           :opacity (or (:fixed-alpha view) 0.5)}
   :side (or (:side view) :x)
   :groups (extract-xy-groups view stat all-colors cfg)})

(defmethod extract-layer :pointrange [view stat all-colors cfg]
  {:mark :pointrange
   :style {:radius (or (:fixed-size view) 3.5)
           :stroke-width 1.5
           :opacity (or (:fixed-alpha view) 1.0)}
   :groups (extract-xy-groups view stat all-colors cfg :with-range? true)})

(defmethod extract-layer :default [view _stat _all-colors _cfg]
  (let [mark (:mark view)
        registered (sort (filter keyword?
                                 (remove #(or (vector? %) (= :default %))
                                         (keys (methods extract-layer)))))]
    (throw (ex-info (str "Unknown mark: " (pr-str mark)
                         ". Supported marks: " (vec registered))
                    {:mark mark :supported (vec registered)}))))

