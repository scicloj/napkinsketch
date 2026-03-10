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

;; ---- Main Entry Point ----

(defn resolve-sketch
  "Resolve views + options into a sketch — a fully resolved plot specification
   with data-space geometry, domains, ticks, legend, and layout info.
   No membrane types, no datasets in the output."
  ([views] (resolve-sketch views {}))
  ([views {:keys [width height config x-label y-label title] :as opts}]
   (let [cfg (merge defaults/defaults config)
         width (or width (:width cfg))
         height (or height (:height cfg))
         views (if (map? views) [views] views)
         m (:margin cfg)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)

         ;; Resolve views and compute stats
         resolved (mapv view/resolve-view non-ann-views)
         stat-results (mapv #(stat/compute-stat (assoc % :cfg cfg)) resolved)

         ;; Collect color categories
         all-colors (let [color-views (filter #(and (view/column-ref? (:color %))
                                                    (:data %)) resolved)]
                      (when (seq color-views)
                        (vec (distinct (mapcat #((:data %) (:color %)) color-views)))))
         color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved))

         ;; Scale specs
         x-scale-spec (or (:x-scale (first non-ann-views)) {:type :linear})
         y-scale-spec (or (:y-scale (first non-ann-views)) {:type :linear})

         ;; Global domains
         global-x-dom (or (:domain x-scale-spec)
                          (collect-domain stat-results :x-domain x-scale-spec))
         global-y-dom (or (:domain y-scale-spec)
                          (compute-global-y-domain stat-results views y-scale-spec))

         ;; Coord type
         coord-type (or (:coord (first non-ann-views)) :cartesian)

         ;; Swap domains for flip
         [x-dom' y-dom'] (if (= coord-type :flip)
                           [global-y-dom global-x-dom]
                           [global-x-dom global-y-dom])
         [x-sspec' y-sspec'] (if (= coord-type :flip)
                               [y-scale-spec x-scale-spec]
                               [x-scale-spec y-scale-spec])

         ;; Compute ticks (using transient wadogo scales)
         x-px [m (- width m)]
         y-px [(- height m) m]
         x-ticks (compute-ticks x-dom' x-px x-sspec' (:tick-spacing-x cfg))
         y-ticks (compute-ticks y-dom' y-px y-sspec' (:tick-spacing-y cfg))

         ;; Extract layers (data-space geometry)
         layers (vec
                 (map (fn [rv sr]
                        (extract-layer rv sr all-colors cfg))
                      resolved stat-results))

         ;; Axis labels (opts > view-level labs > scale label > auto-inferred)
         view-title (:title (first non-ann-views))
         view-x-label (:x-label (first non-ann-views))
         view-y-label (:y-label (first non-ann-views))
         x-vars (distinct (map :x non-ann-views))
         y-vars (distinct (map :y non-ann-views))
         eff-title (or title view-title)
         eff-x-label (or x-label
                         view-x-label
                         (:label x-scale-spec)
                         (when-let [x (first x-vars)] (defaults/fmt-name x)))
         eff-y-label (or y-label
                         view-y-label
                         (:label y-scale-spec)
                         (when-let [y (first y-vars)]
                           (when (not= y (first x-vars))
                             (defaults/fmt-name y))))

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
         total-w (+ y-label-pad width legend-w)
         total-h (+ title-pad height x-label-pad)

         ;; Annotations (plain data maps stored in sketch)
         annotations (vec (for [a ann-views]
                            (select-keys a [:mark :intercept :lo :hi])))]

     {:width width :height height :margin m
      :total-width total-w :total-height total-h
      :title eff-title
      :x-label eff-x-label :y-label eff-y-label
      :config cfg
      :legend legend
      :panels [(cond-> {:x-domain (vec (if (sequential? x-dom') x-dom' [x-dom']))
                        :y-domain (vec (if (sequential? y-dom') y-dom' [y-dom']))
                        :x-scale x-sspec'
                        :y-scale y-sspec'
                        :coord coord-type
                        :x-ticks x-ticks
                        :y-ticks y-ticks
                        :layers layers}
                 (seq annotations) (assoc :annotations annotations))]
      ;; Layout offsets for rendering
      :layout {:x-label-pad x-label-pad
               :y-label-pad y-label-pad
               :title-pad title-pad
               :legend-w legend-w}})))
