(ns scicloj.napkinsketch.impl.coord)

(defn- polar-project
  "Project pixel-space (px, py) to polar coordinates.
   Shared core for make-coord :polar and make-coord-px :polar."
  [cx cy r-max x-lo x-span y-lo y-span px py]
  (let [t-angle (/ (- px x-lo) (max 1.0 x-span))
        t-radius (/ (- (+ y-lo y-span) py) (max 1.0 y-span))
        angle (* 2.0 Math/PI t-angle)
        radius (* r-max t-radius)]
    [(+ cx (* radius (Math/cos (- angle (/ Math/PI 2.0)))))
     (+ cy (* radius (Math/sin (- angle (/ Math/PI 2.0)))))]))

(defmulti make-coord
  "Build a coordinate function: (coord data-x data-y) -> [pixel-x pixel-y]."
  (fn [coord-type sx sy pw ph m] coord-type))

(defmethod make-coord :cartesian [_ sx sy pw ph m]
  (fn [dx dy] [(sx dx) (sy dy)]))

(defmethod make-coord :fixed [_ sx sy pw ph m]
  (fn [dx dy] [(sx dx) (sy dy)]))

(defmethod make-coord :flip [_ sx sy pw ph m]
  (fn [dx dy] [(sx dy) (sy dx)]))

(defmethod make-coord :polar [_ sx sy pw ph m]
  (let [cx (/ pw 2.0) cy (/ ph 2.0)
        r-max (- (min cx cy) m)
        x-lo (double m) x-span (double (- pw m m))
        y-lo (double m) y-span (double (- ph m m))]
    (fn [dx dy]
      (polar-project cx cy r-max x-lo x-span y-lo y-span (sx dx) (sy dy)))))

;; ---- Pixel-space reprojection (for arc interpolation) ----

(defmulti make-coord-px
  "Build a pixel-space reprojection function for coordinate systems that need
   arc interpolation (e.g. polar). Returns nil for systems where bars can be
   drawn as simple rectangles."
  (fn [coord-type sx sy pw ph m] coord-type))

(defmethod make-coord-px :default [_ _ _ _ _ _] nil)

(defmethod make-coord-px :polar [_ sx sy pw ph m]
  (let [cx (/ pw 2.0) cy (/ ph 2.0)
        r-max (- (min cx cy) m)
        x-lo (double m) x-span (double (- pw m m))
        y-lo (double m) y-span (double (- ph m m))]
    (fn [px py]
      (polar-project cx cy r-max x-lo x-span y-lo y-span px py))))

;; ---- Tick visibility ----

(defmulti show-ticks?
  "Whether to show tick labels for this coordinate system."
  (fn [coord-type] coord-type))

(defmethod show-ticks? :default [_] true)

(defmethod show-ticks? :fixed [_] true)
(defmethod show-ticks? :polar [_] false)
