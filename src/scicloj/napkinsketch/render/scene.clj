(ns scicloj.napkinsketch.render.scene
  "Build a membrane scene tree from a sketch.
   Sketch → scene is format-agnostic: the scene tree can be rendered
   to SVG, PNG, or any other format membrane supports."
  (:require [membrane.ui :as ui]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.render.panel :as panel]))

;; ---- Legend ----

(defn- render-legend-from-sketch
  "Render legend from sketch legend data as a membrane scene."
  [legend x y]
  (let [{:keys [title]} legend
        fsize 10
        title-color [0.2 0.2 0.2 1.0]
        sw defaults/legend-swatch-size
        sw-r (/ sw 2.0)]
    (if (= :continuous (:type legend))
      ;; Continuous gradient legend
      (let [{:keys [min max stops]} legend
            bar-h 120 bar-w 12 n-stops 20]
        (vec
         (concat
          (when title
            [(ui/translate x (- y 12)
                           (ui/with-color title-color
                             (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
          ;; Gradient bar: stack of small colored rectangles
          (for [i (range n-stops)
                :let [t (/ (double i) (dec n-stops))
                      c (defaults/gradient-color t)
                      [cr cg cb _] c
                      ry (+ y (* (- 1.0 t) bar-h))]]
            (ui/translate x ry
                          (ui/with-color [cr cg cb 1.0]
                            (ui/with-style ::ui/style-fill
                              (ui/rectangle bar-w (/ bar-h n-stops))))))
          ;; Min/max labels
          [(ui/translate (+ x bar-w 4) (+ y bar-h -4)
                         (ui/with-color title-color
                           (ui/label (format "%.4g" (double min)) (ui/font nil 8))))
           (ui/translate (+ x bar-w 4) (+ y 6)
                         (ui/with-color title-color
                           (ui/label (format "%.4g" (double max)) (ui/font nil 8))))])))
      ;; Categorical swatch legend
      (let [{:keys [entries]} legend]
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
                                             (ui/rounded-rectangle sw sw sw-r))))
                           (ui/translate 12 0
                                         (ui/with-color title-color
                                           (ui/label label (ui/font nil fsize))))]))))))))

;; ---- Labels ----

(defn- render-x-label
  "Render x-axis label centered below the plot area."
  [label total-w y-pos]
  (let [fsize (:label-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) y-pos
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (ui/label label (ui/font nil fsize))))))

(defn- render-y-label
  "Render y-axis label. Uses a Rotate to place vertically."
  [label total-h x-pos]
  (let [fsize (:label-font-size defaults/defaults)
        cy (/ total-h 2.0)]
    (ui/translate x-pos cy
                  (membrane.ui.Rotate. -90
                                       (ui/with-color [0.2 0.2 0.2 1.0]
                                         (ui/label label (ui/font nil fsize)))))))

(defn- render-title
  "Render plot title centered above the plot area."
  [title total-w]
  (let [fsize (:title-font-size defaults/defaults)]
    (ui/translate (/ total-w 2.0) 14
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (ui/label title (ui/font nil fsize))))))

;; ---- Sketch → Scene ----

(defn sketch->scene
  "Build a membrane scene tree from a sketch.
   Returns a vector of membrane drawables representing the complete plot."
  [sketch]
  (let [{:keys [margin total-width total-height panel-width panel-height
                title x-label y-label legend panels layout grid]} sketch
        {:keys [x-label-pad y-label-pad title-pad legend-w strip-h strip-w]} layout
        grid-rows (:rows grid)
        grid-cols (:cols grid)
        pw panel-width
        ph panel-height

        ;; Render each panel, positioned in the grid
        panel-scenes
        (vec
         (for [p panels
               :let [ri (:row p)
                     ci (:col p)
                     show-x? (= ri (dec grid-rows))
                     show-y? (zero? ci)
                     x-off (+ y-label-pad (* ci pw))
                     y-off (+ title-pad strip-h (* ri ph))]]
           (ui/translate x-off y-off
                         (panel/render-panel-from-sketch p pw ph margin
                                                         :show-x? show-x?
                                                         :show-y? show-y?))))

        ;; Strip labels (column headers on top, row headers on right)
        strip-label-color [0.2 0.2 0.2 1.0]
        strip-fsize (or (:strip-font-size defaults/defaults) 10)

        col-strips
        (when (pos? strip-h)
          (vec
           (for [p panels
                 :when (and (zero? (:row p)) (:col-label p))]
             (let [cx (+ y-label-pad (* (:col p) pw) (/ pw 2.0))
                   label (:col-label p)]
               (ui/translate (- cx (* (count label) (/ strip-fsize 3.5)))
                             (+ title-pad 2)
                             (ui/with-color strip-label-color
                               (ui/label label (ui/font nil strip-fsize))))))))

        row-strips
        (when (pos? strip-w)
          (vec
           (for [p panels
                 :when (and (= (:col p) (dec grid-cols)) (:row-label p))]
             (let [cy (+ title-pad strip-h (* (:row p) ph) (/ ph 2.0))]
               (ui/translate (+ y-label-pad (* grid-cols pw) 5)
                             (- cy 5)
                             (ui/with-color strip-label-color
                               (ui/label (:row-label p) (ui/font nil strip-fsize))))))))]

    ;; Build full scene
    (vec
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
                                   (+ y-label-pad (* grid-cols pw) strip-w 10)
                                   (+ title-pad strip-h 20)))
      ;; Panels
      panel-scenes
      ;; Strip labels
      (or col-strips [])
      (or row-strips [])))))
