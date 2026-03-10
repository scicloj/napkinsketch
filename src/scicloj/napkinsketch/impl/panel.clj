(ns scicloj.napkinsketch.impl.panel
  (:require [membrane.ui :as ui]
            [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.impl.mark :as mark]))

;; ---- Grid Lines ----

(defn render-grid-from-ticks
  "Render grid lines using pre-computed tick values from a sketch.
   Skips grid lines for categorical axes (like ggplot2)."
  [sx sy x-ticks y-ticks pw ph m cfg]
  (let [{:keys [grid]} defaults/theme
        grid-rgba (defaults/hex->rgba grid)
        grid-w (:grid-stroke-width cfg)]
    (vec
     (concat
      (when-not (:categorical? x-ticks)
        (for [t (:values x-ticks) :let [px (sx t)]]
          (ui/with-color grid-rgba
            (ui/with-stroke-width grid-w
              (ui/with-style ::ui/style-stroke
                (ui/path [px m] [px (- ph m)]))))))
      (when-not (:categorical? y-ticks)
        (for [t (:values y-ticks) :let [py (sy t)]]
          (ui/with-color grid-rgba
            (ui/with-stroke-width grid-w
              (ui/with-style ::ui/style-stroke
                (ui/path [m py] [(- pw m) py]))))))))))

;; ---- Tick Labels ----

(defn render-tick-labels
  "Render tick labels from pre-computed tick info in a sketch."
  [axis tick-info scale pw ph m]
  (let [{:keys [values labels]} tick-info
        fsize (:font-size defaults/theme)
        tick-color [0.4 0.4 0.4 1.0]]
    (when (seq values)
      (vec
       (map (fn [t label]
              (if (= axis :x)
                (let [px (scale t)]
                  (ui/translate (- (double px) (* (count label) (/ fsize 3.5)))
                                (- (double ph) (double fsize) 1)
                                (ui/with-color tick-color
                                  (ui/label label (ui/font nil fsize)))))
                (let [py (scale t)
                      lw (* (count label) (/ fsize 2.0))]
                  (ui/translate (- (double m) lw 3)
                                (- (double py) (/ fsize 2.0))
                                (ui/with-color tick-color
                                  (ui/label label (ui/font nil fsize)))))))
            values labels)))))

;; ---- Panel Rendering ----

(defn render-panel-from-sketch
  "Render a sketch panel as a membrane scene tree.
   Takes a panel map from resolve-sketch and pixel dimensions.
   show-x? and show-y? control whether tick labels are drawn
   (grid lines always render)."
  [panel pw ph m cfg & {:keys [show-x? show-y?] :or {show-x? true show-y? true}}]
  (let [{:keys [x-domain y-domain x-scale y-scale coord
                x-ticks y-ticks layers annotations]} panel
        coord-type (or coord :cartesian)

        ;; Build wadogo scales from domains + pixel ranges
        x-px [m (- pw m)]
        y-px [(- ph m) m]
        sx (scale/make-scale x-domain x-px x-scale)
        sy (scale/make-scale y-domain y-px y-scale)

        ;; Coord function
        coord-fn (coord/make-coord coord-type sx sy pw ph m)

        ;; Rendering context for mark/render-layer
        ctx {:coord-fn coord-fn :sx sx :sy sy
             :coord-type coord-type :cfg cfg}

        ;; Background
        bg-rgba (defaults/hex->rgba (:bg defaults/theme))
        background (ui/with-color bg-rgba
                     (ui/with-style ::ui/style-fill
                       (ui/rectangle pw ph)))

        ;; Grid
        grid (render-grid-from-ticks sx sy x-ticks y-ticks pw ph m cfg)

        ;; Data marks from sketch layers
        marks (vec (mapcat #(mark/render-layer % ctx) layers))

        ;; Annotation marks
        ann-marks
        (when (seq annotations)
          (let [ann-cfg (or cfg defaults/defaults)
                ann-color (defaults/hex->rgba (:annotation-stroke ann-cfg))
                band-alpha (:band-opacity ann-cfg)]
            (vec
             (for [a annotations]
               (case (:mark a)
                 :rule-v (let [[px _] (coord-fn (:intercept a) 0)]
                           (ui/with-color ann-color
                             (ui/with-stroke-width 1.5
                               (ui/with-style ::ui/style-stroke
                                 (ui/path [px m] [px (- ph m)])))))
                 :rule-h (let [[_ py] (coord-fn 0 (:intercept a))]
                           (ui/with-color ann-color
                             (ui/with-stroke-width 1.5
                               (ui/with-style ::ui/style-stroke
                                 (ui/path [m py] [(- pw m) py])))))
                 :band-v (let [[px1 _] (coord-fn (:lo a) 0)
                               [px2 _] (coord-fn (:hi a) 0)]
                           (ui/with-color [0.5 0.5 0.5 band-alpha]
                             (ui/with-style ::ui/style-fill
                               (ui/path [px1 m] [px2 m]
                                        [px2 (- ph m)] [px1 (- ph m)]
                                        [px1 m]))))
                 :band-h (let [[_ py1] (coord-fn 0 (:lo a))
                               [_ py2] (coord-fn 0 (:hi a))]
                           (ui/with-color [0.5 0.5 0.5 band-alpha]
                             (ui/with-style ::ui/style-fill
                               (ui/path [m py1] [(- pw m) py1]
                                        [(- pw m) py2] [m py2]
                                        [m py1]))))
                 nil)))))

        ;; Tick labels (conditional)
        x-tick-scene (when show-x? (render-tick-labels :x x-ticks sx pw ph m))
        y-tick-scene (when show-y? (render-tick-labels :y y-ticks sy pw ph m))]

    (vec (concat [background] grid
                 (or ann-marks []) marks
                 (or x-tick-scene []) (or y-tick-scene [])))))
