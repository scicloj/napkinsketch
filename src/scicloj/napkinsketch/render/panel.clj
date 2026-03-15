(ns scicloj.napkinsketch.render.panel
  (:require [membrane.ui :as ui]
            [wadogo.scale :as ws]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.render.mark :as mark]))

;; ---- Grid Lines ----

(defn render-grid-from-ticks
  "Render grid lines using pre-computed tick values from a sketch.
   Skips grid lines for categorical axes (like ggplot2)."
  [sx sy x-ticks y-ticks pw ph m & {:keys [theme]}]
  (let [theme (or theme defaults/theme)
        grid-rgba (defaults/hex->rgba (:grid theme))
        grid-w (:grid-stroke-width defaults/defaults)]
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

(defn render-polar-grid
  "Render polar grid: concentric circles and radial spokes."
  [pw ph m & {:keys [theme]}]
  (let [theme (or theme defaults/theme)
        grid-rgba (defaults/hex->rgba (:grid theme))
        grid-w (:grid-stroke-width defaults/defaults)
        cx (/ pw 2.0) cy (/ ph 2.0)
        r-max (- (min cx cy) (double m))
        ;; Concentric circles (approximated as 40-segment polygons)
        n-seg 40
        circles (for [i (range 1 6)
                      :let [r (* r-max (/ (double i) 5.0))]]
                  (ui/with-color grid-rgba
                    (ui/with-stroke-width grid-w
                      (ui/with-style ::ui/style-stroke
                        (apply ui/path
                               (for [j (range (inc n-seg))
                                     :let [a (* 2.0 Math/PI (/ (double j) n-seg))]]
                                 [(+ cx (* r (Math/cos a)))
                                  (+ cy (* r (Math/sin a)))]))))))
        ;; Radial spokes (8 lines from center to edge)
        spokes (for [i (range 8)
                     :let [a (* (double i) (/ Math/PI 4.0))]]
                 (ui/with-color grid-rgba
                   (ui/with-stroke-width grid-w
                     (ui/with-style ::ui/style-stroke
                       (ui/path [cx cy]
                                [(+ cx (* r-max (Math/cos a)))
                                 (+ cy (* r-max (Math/sin a)))])))))]
    (vec (concat circles spokes))))

;; ---- Tick Labels ----

(defn render-tick-labels
  "Render tick labels from pre-computed tick info in a sketch."
  [axis tick-info scale pw ph m & {:keys [theme]}]
  (let [{:keys [values labels]} tick-info
        fsize (:font-size (or theme defaults/theme))
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

(defn panel->membrane
  "Convert a sketch panel into a membrane drawable tree.
   Takes a panel map from views->sketch and pixel dimensions.
   show-x? and show-y? control whether tick labels are drawn
   (grid lines always render)."
  [panel pw ph m & {:keys [show-x? show-y? tooltip x-col-name y-col-name theme]
                    :or {show-x? true show-y? true}}]
  (let [{:keys [x-domain y-domain x-scale y-scale coord
                x-ticks y-ticks layers annotations]} panel
        coord-type (or coord :cartesian)
        theme (or theme defaults/theme)

        ;; Build wadogo scales from domains + pixel ranges
        x-px [m (- pw m)]
        y-px [(- ph m) m]
        sx (scale/make-scale x-domain x-px x-scale)
        sy (scale/make-scale y-domain y-px y-scale)

        ;; Coord function
        coord-fn (coord/make-coord coord-type sx sy pw ph m)

        ;; Pixel-space reprojection for arc interpolation (polar)
        coord-px (coord/make-coord-px coord-type sx sy pw ph m)

        ;; Suppress ticks for polar coordinates
        show-x? (and show-x? (coord/show-ticks? coord-type))
        show-y? (and show-y? (coord/show-ticks? coord-type))

        ;; y-domain minimum for area baseline
        y-domain-min (if (number? (first y-domain))
                       (first y-domain)
                       0)

        ;; Rendering context for mark/layer->membrane
        ctx (cond-> {:coord-fn coord-fn :sx sx :sy sy
                     :coord-type coord-type
                     :coord-px coord-px
                     :y-domain-min y-domain-min
                     :panel-width pw :panel-height ph :margin m}
              tooltip (assoc :tooltip true
                             :x-col-name x-col-name
                             :y-col-name y-col-name))

        ;; Background
        bg-rgba (defaults/hex->rgba (:bg theme))
        background (ui/with-color bg-rgba
                     (ui/with-style ::ui/style-fill
                       (ui/translate m m (ui/rectangle (- pw m m) (- ph m m)))))

        ;; Grid — polar gets circles + spokes; cartesian/flip get tick-aligned lines
        grid (if (= coord-type :polar)
               (render-polar-grid pw ph m :theme theme)
               (render-grid-from-ticks sx sy x-ticks y-ticks pw ph m :theme theme))

        ;; Data marks from sketch layers
        marks (vec (mapcat #(mark/layer->membrane % ctx) layers))

        ;; Annotation marks
        ann-marks
        (when (seq annotations)
          (let [ann-color (defaults/hex->rgba (:annotation-stroke defaults/defaults))
                band-alpha (:band-opacity defaults/defaults)]
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
                               (ui/translate (min px1 px2) m
                                             (ui/rectangle (Math/abs (double (- px2 px1)))
                                                           (- ph m m))))))
                 :band-h (let [[_ py1] (coord-fn 0 (:lo a))
                               [_ py2] (coord-fn 0 (:hi a))]
                           (ui/with-color [0.5 0.5 0.5 band-alpha]
                             (ui/with-style ::ui/style-fill
                               (ui/translate m (min py1 py2)
                                             (ui/rectangle (- pw m m)
                                                           (Math/abs (double (- py2 py1))))))))
                 nil)))))

        ;; Tick labels (conditional)
        ;; For ridgeline panels, y-tick labels must use ridgeline band positions
        ;; (with overlap padding) instead of the categorical scale positions.
        has-ridgeline? (some #(= :ridgeline (:mark %)) layers)
        x-tick-elems (when show-x? (render-tick-labels :x x-ticks sx pw ph m :theme theme))
        y-tick-elems (if (and has-ridgeline? show-y?)
                       (let [{:keys [values labels]} y-ticks
                             fsize (:font-size theme)
                             tick-color [0.4 0.4 0.4 1.0]
                             ridge-cats (:categories (first (filter #(= :ridgeline (:mark %)) layers)))
                             ridge-pos (mark/ridgeline-positions ridge-cats ph m)]
                         (when (seq values)
                           (vec
                            (map (fn [t label]
                                   (let [pos (get ridge-pos t)
                                         py (if pos (:mid pos) (sy t))
                                         lw (* (count label) (/ fsize 2.0))]
                                     (ui/translate (- (double m) lw 3)
                                                   (- (double py) (/ fsize 2.0))
                                                   (ui/with-color tick-color
                                                     (ui/label label (ui/font nil fsize))))))
                                 values labels))))
                       (when show-y? (render-tick-labels :y y-ticks sy pw ph m :theme theme)))]

    (vec (concat [background] grid
                 (or ann-marks []) marks
                 (or x-tick-elems []) (or y-tick-elems [])))))
