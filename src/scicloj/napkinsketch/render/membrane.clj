(ns scicloj.napkinsketch.render.membrane
  "Build a membrane drawable tree from a sketch.
   Sketch → membrane is format-agnostic: the drawable tree can be converted
   to SVG, PNG, or any other format membrane supports."
  (:require [membrane.ui :as ui]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.render.panel :as panel]))

;; ---- Legend ----

(defn- render-legend-from-sketch
  "Render legend from sketch legend data as membrane drawables."
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

(defn- render-legend-horizontal
  "Render a horizontal legend (for :top or :bottom positioning).
   Swatches and labels laid out left to right in a single row."
  [legend x y]
  (let [{:keys [title entries]} legend
        fsize 10
        title-color [0.2 0.2 0.2 1.0]
        sw defaults/legend-swatch-size
        sw-r (/ sw 2.0)]
    (if (= :continuous (:type legend))
      ;; For continuous legends, fall back to vertical rendering
      (render-legend-from-sketch legend x y)
      ;; Horizontal categorical swatches
      (let [title-w (if title (* (count (defaults/fmt-name title)) 6) 0)
            start-x (if title (+ title-w 8) 0)]
        (vec
         (concat
          (when title
            [(ui/translate x (- y 1)
                           (ui/with-color title-color
                             (ui/label (defaults/fmt-name title) (ui/font nil 9))))])
          (let [{:keys [elems]}
                (reduce (fn [{:keys [elems cur-x]} {:keys [label color]}]
                          (let [[cr cg cb _] color
                                label-w (* (count label) 6)
                                elem [(ui/translate (+ x cur-x) (- y 1)
                                                    (ui/with-color [cr cg cb 1.0]
                                                      (ui/with-style ::ui/style-fill
                                                        (ui/rounded-rectangle sw sw sw-r))))
                                      (ui/translate (+ x cur-x 10) (- y 1)
                                                    (ui/with-color title-color
                                                      (ui/label label (ui/font nil fsize))))]]
                            {:elems (into elems elem)
                             :cur-x (+ cur-x 10 label-w 12)}))
                        {:elems [] :cur-x start-x}
                        entries)]
            elems)))))))

;; ---- Labels ----

(defn- render-x-label
  "Render x-axis label centered below the plot area."
  [label center-x y-pos]
  (let [fsize (:label-font-size defaults/defaults)]
    (ui/translate center-x y-pos
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (assoc (ui/label label (ui/font nil fsize))
                           :text-anchor "middle")))))

(defn- render-y-label
  "Render y-axis label. Uses a Rotate to place vertically."
  [label center-y x-pos]
  (let [fsize (:label-font-size defaults/defaults)]
    (ui/translate x-pos center-y
                  (membrane.ui.Rotate. -90
                                       (ui/with-color [0.2 0.2 0.2 1.0]
                                         (assoc (ui/label label (ui/font nil fsize))
                                                :text-anchor "middle"))))))

(defn- render-title
  "Render plot title centered above the plot area."
  [title center-x]
  (let [fsize (:title-font-size defaults/defaults)]
    (ui/translate center-x 14
                  (ui/with-color [0.2 0.2 0.2 1.0]
                    (assoc (ui/label title (ui/font nil fsize))
                           :text-anchor "middle")))))

;; ---- Sketch → Membrane ----

(defn sketch->membrane
  "Build a membrane drawable tree from a sketch.
   Returns a vector of membrane drawables representing the complete plot.
   Optional kwargs:
     :tooltip — when truthy, enables tooltip text generation on data marks."
  [sketch & {:keys [tooltip]}]
  (let [{:keys [margin total-width total-height panel-width panel-height
                title subtitle caption x-label y-label legend legend-position panels layout grid theme]} sketch
        {:keys [x-label-pad y-label-pad title-pad subtitle-pad caption-pad legend-w legend-h strip-h strip-w]} layout
        theme (or theme defaults/theme)
        legend-pos (or legend-position :right)
        grid-rows (:rows grid)
        grid-cols (:cols grid)
        pw panel-width
        ph panel-height

        ;; Font sizes from theme or defaults
        label-fsize (or (:label-font-size theme) (:label-font-size defaults/defaults))
        title-fsize (or (:title-font-size theme) (:title-font-size defaults/defaults))
        strip-fsize (or (:strip-font-size theme) (:strip-font-size defaults/defaults) 10)
        text-color (if-let [tc (:text-color theme)]
                     (defaults/hex->rgba tc)
                     [0.2 0.2 0.2 1.0])

        ;; Render each panel, positioned in the grid
        panel-elems
        (vec
         (for [p panels
               :let [ri (:row p)
                     ci (:col p)
                     show-x? (= ri (dec grid-rows))
                     show-y? (zero? ci)
                     x-off (+ y-label-pad (* ci pw))
                     y-off (+ title-pad strip-h (* ri ph))]]
           (ui/translate x-off y-off
                         (panel/panel->membrane p pw ph margin
                                                :show-x? show-x?
                                                :show-y? show-y?
                                                :tooltip tooltip
                                                :x-col-name (or x-label "x")
                                                :y-col-name (or y-label "y")
                                                :theme theme))))

        ;; Strip labels (column headers on top, row headers on right)
        strip-label-color text-color

        col-strips
        (when (pos? strip-h)
          (vec
           (for [p panels
                 :when (and (zero? (:row p)) (:col-label p))]
             (let [cx (+ y-label-pad (* (:col p) pw) (/ pw 2.0))
                   label (:col-label p)]
               (ui/translate cx
                             (+ title-pad 2)
                             (ui/with-color strip-label-color
                               (assoc (ui/label label (ui/font nil strip-fsize))
                                      :text-anchor "middle")))))))

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

    ;; Build full membrane tree
    (vec
     (concat
      ;; Title
      (when title
        (let [fsize title-fsize]
          [(ui/translate (+ y-label-pad (/ (* grid-cols pw) 2.0)) 14
                         (ui/with-color text-color
                           (assoc (ui/label title (ui/font nil fsize))
                                  :text-anchor "middle")))]))
      ;; Subtitle
      (when subtitle
        [(ui/translate (+ y-label-pad (/ (* grid-cols pw) 2.0)) 30
                       (ui/with-color [0.4 0.4 0.4 1.0]
                         (assoc (ui/label subtitle (ui/font nil (- title-fsize 2)))
                                :text-anchor "middle")))])
      ;; Y-axis label
      (when y-label
        (let [fsize label-fsize]
          [(ui/translate 12 (+ title-pad strip-h (/ (* grid-rows ph) 2.0))
                         (membrane.ui.Rotate. -90
                                              (ui/with-color text-color
                                                (assoc (ui/label y-label (ui/font nil fsize))
                                                       :text-anchor "middle"))))]))
      ;; X-axis label
      (when x-label
        (let [fsize label-fsize]
          [(ui/translate (+ y-label-pad (/ (* grid-cols pw) 2.0)) (- total-height x-label-pad -2)
                         (ui/with-color text-color
                           (assoc (ui/label x-label (ui/font nil fsize))
                                  :text-anchor "middle")))]))
      ;; Legend — positioned based on legend-position
      (when (and legend (not= legend-pos :none))
        (case legend-pos
          :right
          (render-legend-from-sketch legend
                                     (+ y-label-pad (* grid-cols pw) strip-w 10)
                                     (+ title-pad strip-h 20))
          :top
          (let [plots-start-y title-pad
                legend-y (- plots-start-y (or legend-h 30) -5)]
            (render-legend-horizontal legend
                                      (+ y-label-pad 10)
                                      legend-y))
          :bottom
          (let [bottom-y (- total-height (or legend-h 30) -8)]
            (render-legend-horizontal legend
                                      (+ y-label-pad 10)
                                      bottom-y))
          ;; Fallback to right
          (render-legend-from-sketch legend
                                     (+ y-label-pad (* grid-cols pw) strip-w 10)
                                     (+ title-pad strip-h 20))))
      ;; Panels
      panel-elems
      ;; Strip labels
      (or col-strips [])
      (or row-strips [])
      ;; Caption
      (when caption
        [(ui/translate (+ y-label-pad (* grid-cols pw) -10)
                       (- total-height 6)
                       (ui/with-color [0.5 0.5 0.5 1.0]
                         (assoc (ui/label caption (ui/font nil 9))
                                :text-anchor "end")))])))))
