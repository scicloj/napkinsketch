(ns scicloj.napkinsketch.impl.sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output."
  (:require [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]))

;; ---- Color Resolution (data-space) ----

(defn resolve-color
  "Resolve a color value to [r g b a]. Handles column values, fixed colors, and defaults."
  [all-colors color-val fixed-color cfg]
  (cond
    color-val (defaults/color-for all-colors color-val)
    fixed-color (if (string? fixed-color) (defaults/hex->rgba fixed-color) fixed-color)
    :else (defaults/hex->rgba (:default-color (or cfg defaults/defaults)))))

;; ---- Geometry Extraction (stat → layer descriptors) ----

(defmulti extract-layer
  "Extract data-space geometry from a resolved view and its stat result.
   Returns a layer descriptor map."
  (fn [view stat all-colors cfg] (:mark view)))

(defmethod extract-layer :point [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
    {:mark :point
     :style {:opacity (or (:fixed-alpha view) (:point-opacity cfg))
             :radius (or (:fixed-size view) (:point-radius cfg))}
     :groups (vec
              (for [{:keys [color xs ys sizes alphas shapes row-indices]} (:points stat)]
                (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                         :xs (vec xs) :ys (vec ys)}
                  sizes (assoc :sizes (vec sizes))
                  alphas (assoc :alphas (vec alphas))
                  shapes (assoc :shapes (vec shapes))
                  row-indices (assoc :row-indices (vec row-indices)))))}))

(defmethod extract-layer :bar [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
    {:mark :bar
     :style {:opacity (or (:fixed-alpha view) (:bar-opacity cfg))}
     :groups (vec
              (for [{:keys [color bin-maps]} (:bins stat)]
                {:color (resolve-color all-colors color (:fixed-color view) cfg)
                 :bars (vec (for [{:keys [min max count]} bin-maps]
                              {:lo min :hi max :count count}))}))}))

(defmethod extract-layer :line [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
    {:mark :line
     :style {:stroke-width (or (:fixed-size view) (:line-width cfg))}
     :stat-origin (or (:stat view) :identity)
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
                    :xs (vec xs) :ys (vec ys)}))))}))

(defmethod extract-layer :rect [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
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
                   :xs (vec xs) :ys (vec ys)}))})))

(defmethod extract-layer :text [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
    {:mark :text
     :style {:font-size (or (:font-size view) 10)}
     :groups (vec
              (for [{:keys [color xs ys labels]} (:points stat)]
                (cond-> {:color (resolve-color all-colors color (:fixed-color view) cfg)
                         :xs (vec xs) :ys (vec ys)}
                  labels (assoc :labels (vec (map str labels))))))}))

(defmethod extract-layer :area [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)]
    {:mark :area
     :style {:opacity (or (:fixed-alpha view) 0.5)}
     :groups (vec
              (for [{:keys [color xs ys]} (:points stat)]
                {:color (resolve-color all-colors color (:fixed-color view) cfg)
                 :xs (vec xs) :ys (vec ys)}))}))

(defmethod extract-layer :boxplot [view stat all-colors cfg]
  (let [cfg (or cfg defaults/defaults)
        color-cats (:color-categories stat)]
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
                 (seq (:outliers b)) (assoc :outliers (vec (:outliers b))))))}))

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
  "Compute global y-domain, handling stacked bar accumulation."
  [stat-results views scale-spec]
  (let [has-stacked? (some #(= :stack (:position %)) views)]
    (if has-stacked?
      (let [count-stats (filter :categories stat-results)
            all-cats (distinct (mapcat :categories count-stats))
            max-stack (if (seq all-cats)
                        (reduce max 0
                                (for [cat all-cats]
                                  (reduce + (for [sr count-stats
                                                  {:keys [counts]} (:bars sr)
                                                  {:keys [category count]} counts
                                                  :when (= category cat)]
                                              count))))
                        0)
            other-yd (mapcat (fn [sr]
                               (when-not (:categories sr)
                                 (:y-domain sr)))
                             stat-results)
            hi (if (seq other-yd)
                 (max max-stack (reduce max other-yd))
                 max-stack)]
        (if (pos? hi)
          (scale/pad-domain [0 hi] scale-spec)
          [0 1]))
      (collect-domain stat-results :y-domain scale-spec))))

;; ---- Tick Computation ----

(defn compute-ticks
  "Compute tick values and labels for a domain+pixel range, using wadogo transiently."
  [domain pixel-range scale-spec spacing]
  (if (scale/categorical-domain? domain)
    (let [s (scale/make-scale domain pixel-range scale-spec)]
      {:values (vec (ws/ticks s))
       :labels (mapv str (ws/ticks s))
       :categorical? true})
    (let [s (scale/make-scale domain pixel-range scale-spec)
          n (scale/tick-count (Math/abs (double (- (second pixel-range) (first pixel-range)))) spacing)
          ticks (ws/ticks s n)
          labels (scale/format-ticks s ticks)]
      {:values (vec ticks)
       :labels (vec labels)
       :categorical? false})))

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
   Returns {:resolved [...] :stat-results [...] :layers [...]}."
  [panel-views all-colors cfg]
  (let [resolved (mapv view/resolve-view panel-views)
        stat-results (mapv #(stat/compute-stat (assoc % :cfg (merge cfg (:cfg %)))) resolved)
        layers (vec (map (fn [rv sr]
                           (extract-layer rv sr all-colors cfg))
                         resolved stat-results))]
    {:resolved resolved :stat-results stat-results :layers layers}))

;; ---- Main Entry Point ----

(defn resolve-sketch
  "Resolve views + options into a sketch — a fully resolved plot specification
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output."
  ([views] (resolve-sketch views {}))
  ([views {:keys [width height config x-label y-label title scales] :as opts}]
   (let [cfg (merge defaults/defaults config)
         width (or width (:width cfg))
         height (or height (:height cfg))
         views (if (map? views) [views] views)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)
         layout-type (infer-layout non-ann-views)
         scale-mode (or scales :shared)

         ;; Collect color categories across all views
         resolved-all (mapv view/resolve-view non-ann-views)
         all-colors (let [color-views (filter #(and (view/column-ref? (:color %))
                                                    (:data %)) resolved-all)]
                      (when (seq color-views)
                        (vec (distinct (mapcat #((:data %) (:color %)) color-views)))))
         color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved-all))

         ;; Scale specs (from first view)
         x-scale-spec (or (:x-scale (first non-ann-views)) {:type :linear})
         y-scale-spec (or (:y-scale (first non-ann-views)) {:type :linear})

         ;; Coord type
         coord-type (or (:coord (first non-ann-views)) :cartesian)

         ;; Annotations
         annotations (vec (for [a ann-views]
                            (select-keys a [:mark :intercept :lo :hi])))

         ;; --- Layout-specific panel grouping ---
         x-vars (distinct (map :x non-ann-views))
         y-vars (distinct (map :y non-ann-views))

         facet-row-vals (when (= layout-type :facet-grid)
                          (vec (distinct (remove #(= "_" %) (map :facet-row non-ann-views)))))
         facet-col-vals (when (= layout-type :facet-grid)
                          (vec (distinct (remove #(= "_" %) (map :facet-col non-ann-views)))))

         ;; Grid dimensions
         [grid-rows grid-cols]
         (case layout-type
           :single [1 1]
           :facet-grid [(max 1 (count (or facet-row-vals ["_"])))
                        (max 1 (count (or facet-col-vals ["_"])))]
           :multi-variable [(count y-vars) (count x-vars)])

         ;; Per-panel pixel dimensions
         multi? (and (= layout-type :multi-variable) (> grid-cols 1) (> grid-rows 1))
         m (if multi? (:margin-multi cfg) (:margin cfg))
         pw (if multi?
              (double (:panel-size cfg))
              (double (/ width grid-cols)))
         ph (if multi?
              (double (:panel-size cfg))
              (double (/ height grid-rows)))

         ;; --- Group views into panels ---
         panel-groups
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
              :row-label (defaults/fmt-name yv)}))

         ;; --- Resolve each panel ---
         panel-data (mapv (fn [pg]
                            (if (seq (:views pg))
                              (merge pg (resolve-panel-views (:views pg) all-colors cfg))
                              pg))
                          panel-groups)

         ;; --- Compute global domains (for domain sharing) ---
         all-stat-results (mapcat :stat-results panel-data)

         global-x-dom (or (:domain x-scale-spec)
                          (collect-domain all-stat-results :x-domain x-scale-spec))
         global-y-dom (or (:domain y-scale-spec)
                          (compute-global-y-domain all-stat-results views y-scale-spec))

         ;; For multi-variable: per-column x domain, per-row y domain
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
                                            (collect-domain srs :y-domain y-scale-spec)))])))

         ;; --- Build panel maps with domains and ticks ---
         panels
         (vec
          (for [pd panel-data
                :when (seq (:views pd))]
            (let [;; Per-panel domains from stats
                  local-srs (:stat-results pd)
                  local-x-dom (collect-domain local-srs :x-domain x-scale-spec)
                  local-y-dom (compute-global-y-domain local-srs (:views pd) y-scale-spec)

                  ;; Pick domains based on layout + scale mode
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

                  ;; Flip domains for coord flip
                  [x-dom' y-dom'] (if (= coord-type :flip)
                                    [eff-y-dom eff-x-dom]
                                    [eff-x-dom eff-y-dom])
                  [x-sspec' y-sspec'] (if (= coord-type :flip)
                                        [y-scale-spec x-scale-spec]
                                        [x-scale-spec y-scale-spec])

                  ;; Ticks
                  x-px [m (- pw m)]
                  y-px [(- ph m) m]
                  x-ticks (when x-dom' (compute-ticks x-dom' x-px x-sspec' (:tick-spacing-x cfg)))
                  y-ticks (when y-dom' (compute-ticks y-dom' y-px y-sspec' (:tick-spacing-y cfg)))]

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
                (:col-label pd) (assoc :col-label (:col-label pd))))))

         ;; --- Labels ---
         auto-label? (not multi?)
         view-title (:title (first non-ann-views))
         view-x-label (:x-label (first non-ann-views))
         view-y-label (:y-label (first non-ann-views))
         eff-title (or title view-title)
         eff-x-label (or x-label
                         view-x-label
                         (:label x-scale-spec)
                         (when auto-label?
                           (when-let [x (first x-vars)] (defaults/fmt-name x))))
         eff-y-label (or y-label
                         view-y-label
                         (:label y-scale-spec)
                         (when auto-label?
                           (when-let [y (first y-vars)]
                             (when (not= y (first x-vars))
                               (defaults/fmt-name y)))))

         ;; Legend
         legend (when all-colors
                  {:title (first color-cols)
                   :entries (vec (for [cat all-colors]
                                   {:label (str cat)
                                    :color (defaults/color-for all-colors cat)}))})

         ;; Layout dimensions
         x-label-pad (if eff-x-label (:label-offset cfg) 0)
         y-label-pad (if eff-y-label (:label-offset cfg) 0)
         title-pad (if eff-title (:title-offset cfg) 0)
         legend-w (if legend (:legend-width cfg) 0)
         has-col-strips? (or (and (= layout-type :facet-grid) (seq facet-col-vals))
                             multi?)
         has-row-strips? (or (and (= layout-type :facet-grid) (seq facet-row-vals))
                             multi?)
         strip-h (if has-col-strips? (:strip-height cfg 16) 0)
         strip-w (if has-row-strips? 60 0)
         total-w (+ y-label-pad (* grid-cols pw) strip-w legend-w)
         total-h (+ title-pad strip-h (* grid-rows ph) x-label-pad)]

     {:width width :height height :margin m
      :total-width total-w :total-height total-h
      :panel-width pw :panel-height ph
      :grid {:rows grid-rows :cols grid-cols}
      :layout-type layout-type
      :title eff-title
      :x-label eff-x-label :y-label eff-y-label
      :config cfg
      :legend legend
      :panels panels
      :layout {:x-label-pad x-label-pad
               :y-label-pad y-label-pad
               :title-pad title-pad
               :legend-w legend-w
               :strip-h strip-h
               :strip-w strip-w}})))
