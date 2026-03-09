
(ns scicloj.napkinsketch.impl.plot
  (:require [membrane.ui :as ui]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.panel :as panel]
            [scicloj.napkinsketch.impl.sketch :as sketch]
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

(defn render-legend-from-sketch
  "Render legend from sketch legend data."
  [legend x y]
  (let [{:keys [title entries]} legend
        fsize 10
        title-color [0.2 0.2 0.2 1.0]]
    (vec
     (concat
      (when title
        [(ui/translate x (- y 12)
                       (ui/with-color title-color
                         (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
      (for [[i {:keys [label color]}] (map-indexed vector entries)
            :let [[cr cg cb _] color]]
        (ui/translate x (+ y (* i 16))
                      [(ui/translate 0 0
                                     (ui/with-color [cr cg cb 1.0]
                                       (ui/with-style ::ui/style-fill
                                         (ui/rounded-rectangle 8 8 4))))
                       (ui/translate 12 0
                                     (ui/with-color title-color
                                       (ui/label label (ui/font nil fsize))))]))))))

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
  "Render views as SVG hiccup, wrapped with kind/hiccup for Clay/Kindly.
   Internally: views → sketch → membrane scene → SVG → kind/hiccup."
  ([views] (plot views {}))
  ([views opts]
   (let [views (if (map? views) [views] views)
         sk (sketch/resolve-sketch views opts)
         {:keys [width height margin total-width total-height
                 title x-label y-label config legend panels layout]} sk
         {:keys [x-label-pad y-label-pad title-pad legend-w]} layout
         cfg config

         ;; Render the panel from sketch
         panel-scene (panel/render-panel-from-sketch (first panels) width height margin cfg)

         ;; Build full scene
         scene (vec
                (concat
                 ;; Title
                 (when title
                   [(render-title title total-width)])
                 ;; Y-axis label
                 (when y-label
                   [(render-y-label y-label total-height 12)])
                 ;; X-axis label
                 (when x-label
                   [(render-x-label x-label total-width (- total-height 3))])
                 ;; Legend
                 (when legend
                   (render-legend-from-sketch legend
                                              (+ y-label-pad width 10)
                                              (+ title-pad 20)))
                 ;; Panel (offset by labels)
                 [(ui/translate y-label-pad title-pad panel-scene)]))

         ;; Convert membrane scene → SVG hiccup
         svg-body (svg/scene->svg scene)
         svg (svg/wrap-svg total-width total-height svg-body)]
     (kind/hiccup svg))))

