(ns scicloj.napkinsketch.impl.defaults
  (:require [clojure.string :as str]))

;; ---- Palette and Theme ----

(def ggplot-palette
  ["#F8766D" "#00BA38" "#619CFF" "#A855F7" "#F97316" "#14B8A6" "#EF4444" "#6B7280"])

(def named-palettes
  "Predefined color palettes indexed by keyword."
  {:set1 ["#E41A1C" "#377EB8" "#4DAF4A" "#984EA3" "#FF7F00" "#FFFF33" "#A65628" "#F781BF" "#999999"]
   :set2 ["#66C2A5" "#FC8D62" "#8DA0CB" "#E78AC3" "#A6D854" "#FFD92F" "#E5C494" "#B3B3B3"]
   :set3 ["#8DD3C7" "#FFFFB3" "#BEBADA" "#FB8072" "#80B1D3" "#FDB462" "#B3DE69" "#FCCDE5" "#D9D9D9" "#BC80BD" "#CCEBC5" "#FFED6F"]
   :pastel1 ["#FBB4AE" "#B3CDE3" "#CCEBC5" "#DECBE4" "#FED9A6" "#FFFFCC" "#E5D8BD" "#FDDAEC" "#F2F2F2"]
   :pastel2 ["#B3E2CD" "#FDCDAC" "#CBD5E8" "#F4CAE4" "#E6F5C9" "#FFF2AE" "#F1E2CC" "#CCCCCC"]
   :dark2 ["#1B9E77" "#D95F02" "#7570B3" "#E7298A" "#66A61E" "#E6AB02" "#A6761D" "#666666"]
   :paired ["#A6CEE3" "#1F78B4" "#B2DF8A" "#33A02C" "#FB9A99" "#E31A1C" "#FDBF6F" "#FF7F00" "#CAB2D6" "#6A3D9A" "#FFFF99" "#B15928"]
   :accent ["#7FC97F" "#BEAED4" "#FDC086" "#FFFF99" "#386CB0" "#F0027F" "#BF5B17" "#666666"]
   :tableau10 ["#4E79A7" "#F28E2B" "#E15759" "#76B7B2" "#59A14F" "#EDC948" "#B07AA1" "#FF9DA7" "#9C755F" "#BAB0AC"]
   :category10 ["#1F77B4" "#FF7F0E" "#2CA02C" "#D62728" "#9467BD" "#8C564B" "#E377C2" "#7F7F7F" "#BCBD22" "#17BECF"]})

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
   :annotation-stroke "#333" :annotation-dash [4 3] :band-opacity 0.15
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
  #{:x :y :color :size :alpha :shape :group :text :ymin :ymax})

;; ---- Shape Symbols ----

(def shape-syms [:circle :square :triangle :diamond])

(def legend-swatch-size
  "Side length of legend color swatches (square, in pixels)."
  8)

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
  Returns [r g b a] vector. Accepts an optional custom palette —
  a keyword (named preset) or a vector of hex strings."
  ([categories val]
   (color-for categories val nil))
  ([categories val palette]
   (let [pal (cond
               (keyword? palette) (get named-palettes palette ggplot-palette)
               (sequential? palette) palette
               :else ggplot-palette)
         idx (.indexOf ^java.util.List (vec categories) val)]
     (hex->rgba (nth pal (mod (if (neg? idx) 0 idx) (count pal)))))))

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
        n (count viridis-stops)
        idx (-> (dec (count (take-while #(<= (first %) t) viridis-stops)))
                (max 0) (min (- n 2)))
        [t0 r0 g0 b0] (nth viridis-stops idx)
        [t1 r1 g1 b1] (nth viridis-stops (inc idx))
        f (/ (- t t0) (max 1e-10 (- t1 t0)))]
    [(+ r0 (* f (- r1 r0)))
     (+ g0 (* f (- g1 g0)))
     (+ b0 (* f (- b1 b0)))
     1.0]))

;; ---- Name Formatting ----

(defn fmt-name
  "Format a keyword as a readable name: :sepal-length -> \"sepal length\"."
  [k]
  (str/replace (name k) #"[-_]" " "))
