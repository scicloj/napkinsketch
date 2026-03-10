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

(defn color-for-hex
  "Look up the hex color for a categorical value."
  [categories val]
  (let [idx (.indexOf ^java.util.List (vec categories) val)]
    (nth ggplot-palette (mod (if (neg? idx) 0 idx) (count ggplot-palette)))))

;; ---- Name Formatting ----

(defn fmt-name
  "Format a keyword as a readable name: :sepal-length -> \"sepal length\"."
  [k]
  (str/replace (name k) #"[-_]" " "))
