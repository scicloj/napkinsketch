(ns scicloj.napkinsketch.method
  "Method registry — keyword → method map (mark + stat + position).
   Methods are plain data maps. The registry makes them discoverable
   and extensible. Use `lookup` to get a method by keyword, `registered`
   to enumerate all methods, and `register!` to add new ones."
  (:require [scicloj.napkinsketch.impl.view :as view]))

;; ---- Registry ----

(def ^:private registry*
  "Atom holding keyword → method entry map."
  (atom {}))

(defn register!
  "Register a method. `k` is a keyword, `entry` is a map with
   :mark, :stat, and optionally :position and :doc.
   Position defaults to nil (identity) — only :dodge, :stack, :fill are explicit."
  [k entry]
  (swap! registry* assoc k entry)
  k)

(defn lookup
  "Look up a registered method by keyword. Returns the method map
   (with :mark, :stat, :position, :doc), or nil if not found."
  [k]
  (get @registry* k))

(defn registered
  "Return all registered methods as a map of keyword → entry."
  []
  @registry*)

;; ---- Built-in methods ----

(register! :point {:mark :point :stat :identity :doc "Scatter — individual data points."})
(register! :line {:mark :line :stat :identity :doc "Line — connects data points in order."})
(register! :step {:mark :step :stat :identity :doc "Step — horizontal-then-vertical connected points."})
(register! :area {:mark :area :stat :identity :doc "Area — filled region under a line."})
(register! :stacked-area {:mark :area :stat :identity :position :stack :doc "Stacked area — filled regions stacked cumulatively."})
(register! :histogram {:mark :bar :stat :bin :doc "Histogram — bins numerical data into bars."})
(register! :bar {:mark :rect :stat :count :doc "Bar — counts categorical values."})
(register! :stacked-bar {:mark :rect :stat :count :position :stack :doc "Stacked bar — counts categorical values, stacked."})
(register! :stacked-bar-fill {:mark :rect :stat :count :position :fill :doc "Percentage stacked bar — proportions sum to 1.0."})
(register! :value-bar {:mark :rect :stat :identity :doc "Value bar — categorical x with pre-computed y."})
(register! :lm {:mark :line :stat :lm :doc "Linear regression — OLS fit line."})
(register! :loess {:mark :line :stat :loess :doc "LOESS — local regression smoothing."})
(register! :density {:mark :area :stat :kde :doc "Density — kernel density estimation as filled area."})
(register! :tile {:mark :tile :stat :bin2d :doc "Tile/heatmap — 2D grid binning."})
(register! :density2d {:mark :tile :stat :kde2d :doc "2D density — KDE-smoothed heatmap."})
(register! :contour {:mark :contour :stat :kde2d :doc "Contour — iso-density contour lines."})
(register! :boxplot {:mark :boxplot :stat :boxplot :doc "Boxplot — median, quartiles, whiskers, outliers."})
(register! :violin {:mark :violin :stat :violin :doc "Violin — mirrored density curve per category."})
(register! :ridgeline {:mark :ridgeline :stat :violin :doc "Ridgeline — stacked density curves per category."})
(register! :summary {:mark :pointrange :stat :summary :doc "Summary — mean ± standard error per category."})
(register! :errorbar {:mark :errorbar :stat :identity :doc "Errorbar — vertical error bars."})
(register! :lollipop {:mark :lollipop :stat :identity :doc "Lollipop — stem with dot."})
(register! :text {:mark :text :stat :identity :doc "Text — data-driven labels."})
(register! :label {:mark :label :stat :identity :doc "Label — text with background box."})
(register! :rug {:mark :rug :stat :identity :doc "Rug — axis-margin tick marks."})

;; ---- Annotation constructors ----
;; These are not methods (no mark+stat+position). They take arguments
;; and return annotation maps. Kept as functions.

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
