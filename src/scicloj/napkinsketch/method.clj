(ns scicloj.napkinsketch.method
  "Method constructors — mark + stat + position bundles.
   Each function returns a method map that can be passed to `sk/lay`,
   or inspected to see the mark, stat, and position components."
  (:require [scicloj.napkinsketch.impl.view :as view]))

(defn point
  "Scatter method — shows individual data points."
  ([] (view/point))
  ([opts] (view/point opts)))

(defn line
  "Line method — connects data points in order."
  ([] (view/line))
  ([opts] (view/line opts)))

(defn step
  "Step method — horizontal-then-vertical connected points."
  ([] (view/step))
  ([opts] (view/step opts)))

(defn histogram
  "Histogram method — bins numerical data into counted bars."
  ([] (view/histogram))
  ([opts] (view/histogram opts)))

(defn bar
  "Bar method — counts categorical values."
  ([] (view/bar))
  ([opts] (view/bar opts)))

(defn stacked-bar
  "Stacked bar method — counts categorical values (position :stack)."
  ([] (view/stacked-bar))
  ([opts] (view/stacked-bar opts)))

(defn stacked-bar-fill
  "Percentage stacked bar method — proportions sum to 1.0 (position :fill)."
  ([] (view/stacked-bar-fill))
  ([opts] (view/stacked-bar-fill opts)))

(defn value-bar
  "Value bar method — categorical x with pre-computed numeric y."
  ([] (view/value-bar))
  ([opts] (view/value-bar opts)))

(defn lm
  "Linear regression method — fits a straight line to data."
  ([] (view/lm))
  ([opts] (view/lm opts)))

(defn loess
  "LOESS method — local regression smoothing."
  ([] (view/loess))
  ([opts] (view/loess opts)))

(defn text
  "Text method — data-driven labels at (x, y) positions."
  ([] (view/text))
  ([opts] (view/text opts)))

(defn label
  "Label method — text with a filled background box."
  ([] (view/label))
  ([opts] (view/label opts)))

(defn area
  "Area method — filled region under a line."
  ([] (view/area))
  ([opts] (view/area opts)))

(defn stacked-area
  "Stacked area method — filled regions stacked cumulatively."
  ([] (view/stacked-area))
  ([opts] (view/stacked-area opts)))

(defn density
  "Density method — kernel density estimation rendered as filled area."
  ([] (view/density))
  ([opts] (view/density opts)))

(defn tile
  "Tile/heatmap method — filled rectangles colored by a numeric value."
  ([] (view/tile))
  ([opts] (view/tile opts)))

(defn density2d
  "2D density method — KDE-smoothed heatmap."
  ([] (view/density2d))
  ([opts] (view/density2d opts)))

(defn contour
  "Contour method — iso-density contour lines from 2D KDE."
  ([] (view/contour))
  ([opts] (view/contour opts)))

(defn ridgeline
  "Ridgeline method — vertically stacked density curves per category."
  ([] (view/ridgeline))
  ([opts] (view/ridgeline opts)))

(defn boxplot
  "Boxplot method — displays median, quartiles, whiskers, and outliers."
  ([] (view/boxplot))
  ([opts] (view/boxplot opts)))

(defn violin
  "Violin method — mirrored density curve per category."
  ([] (view/violin))
  ([opts] (view/violin opts)))

(defn rug
  "Rug method — tick marks along axis margins."
  ([] (view/rug))
  ([opts] (view/rug opts)))

(defn summary
  "Summary method — mean ± standard error per category."
  ([] (view/summary))
  ([opts] (view/summary opts)))

(defn errorbar
  "Errorbar method — vertical error bars at (x, y) positions."
  ([] (view/errorbar))
  ([opts] (view/errorbar opts)))

(defn lollipop
  "Lollipop method — stem + dot at (x, y) positions."
  ([] (view/lollipop))
  ([opts] (view/lollipop opts)))

;; ---- Annotations ----

(defn rule-v
  "Vertical reference line at x = intercept."
  [intercept]
  (view/rule-v intercept))

(defn rule-h
  "Horizontal reference line at y = intercept."
  [intercept]
  (view/rule-h intercept))

(defn band-v
  "Vertical shaded band from x = lo to x = hi."
  ([lo hi] (view/band-v lo hi))
  ([lo hi opts] (view/band-v lo hi opts)))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi."
  ([lo hi] (view/band-h lo hi))
  ([lo hi opts] (view/band-h lo hi opts)))
