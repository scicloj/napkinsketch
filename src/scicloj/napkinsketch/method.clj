(ns scicloj.napkinsketch.method
  "Method registry — keyword → method map (mark + stat + position).
   Methods are plain data maps. The registry makes them discoverable
   and extensible. Use `lookup` to get a method by keyword, `registered`
   to enumerate all methods, and `register!` to add new ones."
  (:require [scicloj.napkinsketch.impl.view :as view]))

;; ---- Registry ----

(def universal-layer-options
  "Layer options accepted by all methods."
  [:color :alpha :group :position])

(def layer-option-docs
  "Documentation for layer option keys. Maps key to description string."
  {:color "Column keyword (categorical grouping) or literal color string"
   :alpha "Column keyword (per-point opacity) or fixed number 0.0–1.0"
   :group "Column keyword for grouping without color"
   :position "Position adjustment keyword — how overlapping groups are arranged (see sk/position-doc)"
   :nudge-x "Shift all x-coordinates by this data-space amount"
   :nudge-y "Shift all y-coordinates by this data-space amount"
   :size "Column keyword or fixed number — point radius or stroke width"
   :shape "Column keyword for per-point shape"
   :jitter "true or pixel amount — random offset to reduce overplotting"
   :text "Column keyword for label content"
   :se "true to show SE (standard error) confidence ribbon around fitted line"
   :se-boot "Number of bootstrap resamples for LOESS confidence ribbon (default 200)"
   :bandwidth "Smoothing bandwidth for density and LOESS methods"
   :normalize "Histogram normalization — :density (area integrates to 1) or nil"
   :levels "Number of contour iso-levels (default 5)"
   :fill "Column keyword for tile fill values (pre-computed heatmap)"
   :ymin "Column keyword for lower error bound"
   :ymax "Column keyword for upper error bound"
   :side "Rug tick position — :x (default), :y, or :both"
   :kde2d-grid "2D KDE grid resolution — number of bins per axis (default 25)"})

(def ^:private registry*
  "Atom holding keyword → method entry map."
  (atom {}))

(defn register!
  "Register a method. `k` is a keyword, `entry` is a map with
   :mark, :stat, and optionally :position and :doc.
   Position defaults to nil (identity) — only :dodge, :stack, :fill are explicit."
  [k entry]
  (swap! registry* assoc k (view/map->Method entry))
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

(def method-order
  "Canonical display order for built-in methods."
  [:point :line :step :area :stacked-area
   :histogram :bar :stacked-bar :stacked-bar-fill :value-bar
   :lm :loess :density
   :tile :density2d :contour
   :boxplot :violin :ridgeline
   :summary :errorbar :lollipop
   :text :label :rug])

;; ---- Built-in methods ----

(register! :point {:mark :point :stat :identity :accepts [:size :shape :jitter :text :nudge-x :nudge-y] :doc "Scatter — individual data points."})
(register! :line {:mark :line :stat :identity :accepts [:size :nudge-x :nudge-y] :doc "Line — connects data points in order."})
(register! :step {:mark :step :stat :identity :accepts [:size] :doc "Step — horizontal-then-vertical connected points."})
(register! :area {:mark :area :stat :identity :accepts [] :doc "Area — filled region under a line."})
(register! :stacked-area {:mark :area :stat :identity :position :stack :accepts [] :doc "Stacked area — filled regions stacked cumulatively."})
(register! :histogram {:mark :bar :stat :bin :x-only true :accepts [:normalize :bins :binwidth] :doc "Histogram — bins numerical data into bars."})
(register! :bar {:mark :rect :stat :count :x-only true :accepts [] :doc "Bar — counts categorical values."})
(register! :stacked-bar {:mark :rect :stat :count :position :stack :x-only true :accepts [] :doc "Stacked bar — counts categorical values, stacked."})
(register! :stacked-bar-fill {:mark :rect :stat :count :position :fill :x-only true :accepts [] :doc "Percentage stacked bar — proportions sum to 1.0."})
(register! :value-bar {:mark :rect :stat :identity :accepts [] :doc "Value bar — categorical x with pre-computed y."})
(register! :lm {:mark :line :stat :lm :accepts [:se :size :nudge-x :nudge-y] :doc "Linear model (lm) — ordinary least squares (OLS) regression line."})
(register! :loess {:mark :line :stat :loess :accepts [:se :se-boot :bandwidth :size :nudge-x :nudge-y] :doc "LOESS (local regression) — smooth curve fitted to nearby data."})
(register! :density {:mark :area :stat :kde :x-only true :accepts [:bandwidth] :doc "Density — KDE (kernel density estimation) as filled area."})
(register! :tile {:mark :tile :stat :bin2d :accepts [:fill :kde2d-grid] :doc "Tile/heatmap — 2D grid binning."})
(register! :density2d {:mark :tile :stat :kde2d :accepts [:kde2d-grid] :doc "2D density — kernel density estimation (KDE) smoothed heatmap."})
(register! :contour {:mark :contour :stat :kde2d :accepts [:levels :size] :doc "Contour — iso-density contour lines."})
(register! :boxplot {:mark :boxplot :stat :boxplot :accepts [:size] :doc "Boxplot — median, quartiles, whiskers, outliers."})
(register! :violin {:mark :violin :stat :violin :accepts [:bandwidth :size] :doc "Violin — mirrored density curve per category."})
(register! :ridgeline {:mark :ridgeline :stat :violin :accepts [:bandwidth] :doc "Ridgeline — stacked density curves per category."})
(register! :summary {:mark :pointrange :stat :summary :accepts [:size] :doc "Summary — mean ± standard error per category."})
(register! :errorbar {:mark :errorbar :stat :identity :accepts [:ymin :ymax :size :nudge-x :nudge-y] :doc "Errorbar — vertical error bars."})
(register! :lollipop {:mark :lollipop :stat :identity :accepts [:size] :doc "Lollipop — stem with dot."})
(register! :text {:mark :text :stat :identity :accepts [:text :nudge-x :nudge-y] :doc "Text — data-driven labels."})
(register! :label {:mark :label :stat :identity :accepts [:text :nudge-x :nudge-y] :doc "Label — text with background box."})
(register! :rug {:mark :rug :stat :identity :x-only true :accepts [:side] :doc "Rug — axis-margin tick marks."})

