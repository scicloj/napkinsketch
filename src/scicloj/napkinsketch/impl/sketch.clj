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
  {:mark :line
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
                  :xs (vec xs) :ys (vec ys)}))))})

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
    {:mark :rect
     :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
     :position (or (:position view) :dodge)
     :categories (vec (:categories stat))
     :groups (vec
              (for [{:keys [color counts]} (:bars stat)]
                {:color (resolve-color all-colors color (:fixed-color view) cfg)
                 :label (str color)
                 :counts (vec counts)}))}
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
  "Compute global y-domain, handling stacked bar/area accumulation."
  [stat-results views scale-spec]
  (let [stacked-views (filter #(= :stack (:position %)) views)
        has-stacked? (seq stacked-views)]
    (if has-stacked?
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
      (collect-domain stat-results :y-domain scale-spec))))

;; ---- Tick Computation ----

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
                    (mapv (fn [epoch-day]
                            (str (java.time.LocalDate/ofEpochDay (long epoch-day))))
                          ticks)
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
   legend facet-row-vals facet-col-vals
   grid-rows grid-cols pw ph multi?]
  (let [x-label-pad (if eff-x-label (:label-offset cfg) 0)
        y-label-pad (if eff-y-label (:label-offset cfg) 0)
        title-pad (if eff-title (:title-offset cfg) 0)
        legend-w (if legend (:legend-width cfg) 0)
        has-col-strips? (or (and (= layout-type :facet-grid) (seq facet-col-vals))
                            multi?)
        has-row-strips? (or (and (= layout-type :facet-grid) (seq facet-row-vals))
                            multi?)
        strip-h (if has-col-strips? (:strip-height cfg 16) 0)
        strip-w (if has-row-strips? 60 0)]
    {:x-label-pad x-label-pad
     :y-label-pad y-label-pad
     :title-pad title-pad
     :legend-w legend-w
     :strip-h strip-h
     :strip-w strip-w
     :total-w (+ y-label-pad (* grid-cols pw) strip-w legend-w)
     :total-h (+ title-pad strip-h (* grid-rows ph) x-label-pad)}))

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

(defn resolve-sketch
  "Resolve views + options into a sketch — a fully resolved plot specification
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output."
  ([views] (resolve-sketch views {}))
  ([views {:keys [width height config x-label y-label title scales palette] :as opts}]
   (let [cfg (cond-> (merge defaults/defaults config) palette (assoc :palette palette))
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
                            (select-keys a [:mark :intercept :lo :hi])))

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

         ;; Labels — suppress auto-labels for multi-variable grids and polar coords
         multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
         auto-label? (and (not multi?) (coord/show-ticks? coord-type))
         {:keys [eff-title eff-x-label eff-y-label]}
         (resolve-labels non-ann-views x-vars y-vars x-scale-spec y-scale-spec
                         title x-label y-label auto-label?)

         ;; Legend
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg)

         ;; Layout dimensions
         layout-dims (compute-layout-dims cfg layout-type eff-title eff-x-label eff-y-label
                                          legend facet-row-vals facet-col-vals
                                          grid-rows grid-cols pw ph multi?)]

     {:width width :height height :margin m
      :total-width (:total-w layout-dims) :total-height (:total-h layout-dims)
      :panel-width pw :panel-height ph
      :grid {:rows grid-rows :cols grid-cols}
      :layout-type layout-type
      :title eff-title
      :x-label eff-x-label :y-label eff-y-label
      :legend legend
      :panels panels
      :layout (select-keys layout-dims [:x-label-pad :y-label-pad :title-pad
                                        :legend-w :strip-h :strip-w])})))
