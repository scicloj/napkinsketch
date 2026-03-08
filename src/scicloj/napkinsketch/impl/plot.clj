(ns scicloj.napkinsketch.impl.plot
  (:require [membrane.ui :as ui]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.panel :as panel]
            [scicloj.napkinsketch.render.svg :as svg]))

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

;; ---- Legend ----

(defn render-legend
  "Render a legend as membrane scene tree."
  [categories color-fn x y title]
  (let [fsize 10
        title-color [0.2 0.2 0.2 1.0]]
    (vec
     (concat
      (when title
        [(ui/translate x (- y 12)
           (ui/with-color title-color
             (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
      (for [[i cat] (map-indexed vector categories)
            :let [rgba (color-fn cat)
                  [cr cg cb _] rgba]]
        (ui/translate x (+ y (* i 16))
          [(ui/translate 0 0
             (ui/with-color [cr cg cb 1.0]
               (ui/with-style ::ui/style-fill
                 (ui/rounded-rectangle 8 8 4))))
           (ui/translate 12 0
             (ui/with-color title-color
               (ui/label (str cat) (ui/font nil fsize))))]))))))

;; ---- Axis Labels ----

(defn render-x-label
  "Render x-axis label centered below the plot area."
  [label total-w y-pos]
  (let [fsize (:label-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) y-pos
      (ui/with-color [0.2 0.2 0.2 1.0]
        (ui/label label (ui/font nil fsize))))))

(defn render-y-label
  "Render y-axis label. Uses a Rotate to place vertically."
  [label total-h x-pos]
  (let [fsize (:label-font-size defaults/defaults)
        cy (/ total-h 2.0)]
    (ui/translate x-pos cy
      (membrane.ui.Rotate. -90
        (ui/with-color [0.2 0.2 0.2 1.0]
          (ui/label label (ui/font nil fsize)))))))

(defn render-title
  "Render plot title centered above the plot area."
  [title total-w]
  (let [fsize (:title-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) 14
      (ui/with-color [0.2 0.2 0.2 1.0]
        (ui/label title (ui/font nil fsize))))))

;; ---- Plot ----

(defn plot
  "Render views as SVG hiccup, wrapped with kind/hiccup for Clay/Kindly."
  ([views] (plot views {}))
  ([views {:keys [width height config x-label y-label title] :as opts}]
   (let [cfg (merge defaults/defaults config)
         width (or width (:width cfg))
         height (or height (:height cfg))
         views (if (map? views) [views] views)
         ann-views (filter #(view/annotation-marks (:mark %)) views)
         non-ann-views (remove #(view/annotation-marks (:mark %)) views)
         m (:margin cfg)

         ;; Resolve views and compute stats
         resolved (mapv view/resolve-view non-ann-views)
         stat-results (mapv #(stat/compute-stat (assoc % :cfg cfg)) resolved)

         ;; Collect colors
         all-colors (let [color-views (filter #(and (view/column-ref? (:color %))
                                                    (:data %)) resolved)]
                      (when (seq color-views)
                        (distinct (mapcat #((:data %) (:color %)) color-views))))
         color-cols (distinct (keep #(when (view/column-ref? (:color %)) (:color %)) resolved))

         ;; Scale specs
         x-scale-spec (or (:x-scale (first non-ann-views)) {:type :linear})
         y-scale-spec (or (:y-scale (first non-ann-views)) {:type :linear})

         ;; Global domains
         global-x-doms (or (:domain x-scale-spec)
                           (collect-domain stat-results :x-domain x-scale-spec))
         global-y-doms (or (:domain y-scale-spec)
                           (compute-global-y-domain stat-results views y-scale-spec))

         ;; Axis labels
         x-vars (distinct (map :x non-ann-views))
         y-vars (distinct (map :y non-ann-views))
         eff-x-label (or x-label
                         (:label x-scale-spec)
                         (when-let [x (first x-vars)] (defaults/fmt-name x)))
         eff-y-label (or y-label
                         (:label y-scale-spec)
                         (when-let [y (first y-vars)]
                           (when (not= y (first x-vars))
                             (defaults/fmt-name y))))
         eff-title title

         ;; Layout dimensions
         x-label-pad (if eff-x-label (:label-offset cfg) 0)
         y-label-pad (if eff-y-label (:label-offset cfg) 0)
         title-pad (if eff-title (:title-offset cfg) 0)
         legend-w (if all-colors (:legend-width cfg) 0)
         total-w (+ y-label-pad width legend-w)
         total-h (+ title-pad height x-label-pad)

         ;; Panel views
         panel-views (concat non-ann-views ann-views)

         ;; Render the panel
         panel-scene (panel/render-panel
                      panel-views width height m
                      :all-colors all-colors
                      :x-domain global-x-doms
                      :y-domain global-y-doms
                      :cfg cfg)

         ;; Build full scene
         scene (vec
                (concat
                 ;; Title
                 (when eff-title
                   [(render-title eff-title total-w)])
                 ;; Y-axis label
                 (when eff-y-label
                   [(render-y-label eff-y-label total-h 12)])
                 ;; X-axis label
                 (when eff-x-label
                   [(render-x-label eff-x-label total-w (- total-h 3))])
                 ;; Legend
                 (when all-colors
                   (render-legend all-colors
                                 #(defaults/color-for all-colors %)
                                 (+ y-label-pad width 10)
                                 (+ title-pad 20)
                                 (first color-cols)))
                 ;; Panel (offset by labels)
                 [(ui/translate y-label-pad title-pad panel-scene)]))

         ;; Convert membrane scene → SVG hiccup
         svg-body (svg/scene->svg scene)
         svg (svg/wrap-svg total-w total-h svg-body)]
     (kind/hiccup svg))))
