(ns scicloj.napkinsketch.impl.defaults
  (:require [clojure.string :as str]))

;; ---- Palette and Theme ----

(def ggplot-palette
  ["#F8766D" "#00BA38" "#619CFF" "#A855F7" "#F97316" "#14B8A6" "#EF4444" "#6B7280"])

(def theme {:bg "#EBEBEB" :grid "#FFFFFF" :font-size 8})

;; ---- Visual Defaults ----

(def defaults
  {;; Layout
   :width 600 :height 400
   :margin 25 :margin-multi 30 :panel-size 200 :legend-width 100
   ;; Ticks
   :tick-spacing-x 60 :tick-spacing-y 40
   ;; Points
   :point-radius 2.5 :point-opacity 0.7
   :point-stroke "none" :point-stroke-width 0
   ;; Bars and lines
   :bar-opacity 0.7 :line-width 2 :grid-stroke-width 1.5
   ;; Annotations
   :annotation-stroke "#333" :annotation-dash [4 3] :band-opacity 0.08
   ;; Statistics
   :bin-method :sturges
   :domain-padding 0.05
   ;; Labels and titles
   :label-font-size 11 :title-font-size 13
   :label-offset 18 :title-offset 18
   ;; Facet strips
   :strip-font-size 10 :strip-height 16
   ;; Fallback
   :default-color "#333"})

;; ---- Column Keys ----

(def column-keys
  #{:x :y :color :size :alpha :shape :group})

;; ---- Shape Symbols ----

(def shape-syms [:circle :square :triangle :diamond])

;; ---- Color Helpers ----

(defn hex->rgba
  "Convert hex color string to [r g b a] vector with values 0-1.
  Supports #RGB, #RRGGBB, and #RRGGBBAA."
  [hex]
  (let [hex (if (str/starts-with? hex "#") (subs hex 1) hex)
        hex (if (= 3 (count hex))
              (apply str (mapcat #(vector % %) hex))
              hex)
        r (/ (Integer/parseInt (subs hex 0 2) 16) 255.0)
        g (/ (Integer/parseInt (subs hex 2 4) 16) 255.0)
        b (/ (Integer/parseInt (subs hex 4 6) 16) 255.0)
        a (if (>= (count hex) 8)
            (/ (Integer/parseInt (subs hex 6 8) 16) 255.0)
            1.0)]
    [r g b a]))

(defn color-for
  "Look up the color for a categorical value from the palette.
  Returns [r g b a] vector."
  [categories val]
  (let [idx (.indexOf ^java.util.List (vec categories) val)]
    (hex->rgba (nth ggplot-palette (mod (if (neg? idx) 0 idx) (count ggplot-palette))))))

;; ---- Continuous Color ----

(def viridis-stops
  "Viridis colormap sampled at 5 evenly-spaced stops. Each entry is [t r g b]."
  [[0.0 0.267 0.004 0.329]
   [0.25 0.282 0.141 0.458]
   [0.5 0.127 0.567 0.551]
   [0.75 0.544 0.773 0.247]
   [1.0 0.993 0.906 0.144]])

(defn gradient-color
  "Interpolate a color from viridis stops for t in [0,1]. Returns [r g b a]."
  [t]
  (let [t (max 0.0 (min 1.0 (double t)))
        stops viridis-stops
        n (count stops)]
    (if (<= t 0.0)
      (let [[_ r g b] (first stops)] [r g b 1.0])
      (if (>= t 1.0)
        (let [[_ r g b] (last stops)] [r g b 1.0])
        (let [;; Find the two surrounding stops
              idx (dec (count (take-while #(<= (first %) t) stops)))
              idx (max 0 (min idx (- n 2)))
              [t0 r0 g0 b0] (nth stops idx)
              [t1 r1 g1 b1] (nth stops (inc idx))
              f (/ (- t t0) (- t1 t0))]
          [(+ r0 (* f (- r1 r0)))
           (+ g0 (* f (- g1 g0)))
           (+ b0 (* f (- b1 b0)))
           1.0])))))

;; ---- Name Formatting ----

(defn fmt-name
  "Format a keyword as a readable name: :sepal-length -> \"sepal length\"."
  [k]
  (str/replace (name k) #"[-_]" " "))
