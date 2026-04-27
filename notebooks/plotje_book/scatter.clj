;; # Scatter Plots
;;
;; Point mark variations -- color, size, alpha, shape, jitter,
;; and continuous color scale.

(ns plotje-book.scatter
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## Basic Scatter

;; Sepal dimensions, no color -- the default mark.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Colored by Species

;; Adding `:color :species` groups points by species with distinct colors.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Petal Dimensions

;; Petal length vs width -- a strongly correlated pair.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Fixed Color

;; A fixed color string (not a column reference) applies to all points.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color "#E74C3C"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (contains? (:colors s) "rgb(231,76,60)"))))])

;; ## Custom Dimensions

;; Wider plot with custom title and labels.

(-> (rdatasets/reshape2-tips)
    (pj/lay-point :total-bill :tip {:color :day})
    (pj/options {:width 700 :height 300
                 :title "Tips by Day"
                 :x-label "Total Bill ($)"
                 :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 244 (:points s))
                                (>= (:width s) 700)
                                (some #{"Tips by Day"} (:texts s)))))])

;; ## Bubble Plot
;;
;; Map `:size` to a numeric column to create a bubble plot.
;; Each point's radius reflects the column value.

(-> (rdatasets/reshape2-tips)
    (pj/lay-point :total-bill :tip {:color :day :size :size}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s)))))])

;; Combine size with alpha for dense data.

(-> (rdatasets/reshape2-tips)
    (pj/lay-point :total-bill :tip {:color :day :size :size :alpha 0.6}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s)))))])

;; ## Jitter
;;
;; When plotting a numeric column against a categorical column,
;; points overlap. Use `:jitter true` to add random pixel offsets.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :species :sepal-width {:jitter true}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Control the jitter amount in pixels.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :species :sepal-width {:jitter 10 :alpha 0.5}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Continuous Color
;;
;; When `:color` maps to a numeric column, Plotje uses a
;; continuous blue gradient instead of discrete palette colors.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :petal-length}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])

;; Continuous color with size -- a color-size bubble plot.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :petal-length :size :petal-width :alpha 0.7}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])

;; ## Scatter Plot Matrix (SPLOM)
;;
;; `pj/cross` generates all combinations of two lists. Passing
;; column names produces a grid of scatter plots -- one per pair of
;; variables. The diagonal shows histograms (automatic inference
;; for same-column pairs).

(def cols [:sepal-length :sepal-width :petal-length :petal-width])

(-> (rdatasets/datasets-iris)
    (pj/pose {:color :species})
    (pj/pose (pj/cross cols cols)))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 16 (:panels s))
                                (= (* 12 150) (:points s))
                                (pos? (:polygons s)))))])

;; Per-cell inference picks the layer type for each panel: diagonal
;; cells (x = y) get histograms; off-diagonal cells get scatter
;; plots. All panels share the color aesthetic set at the composite
;; root.
;;
;; See the [Faceting](./plotje_book.faceting.html) chapter for more
;; SPLOM variations, and the [Customization](./plotje_book.customization.html)
;; chapter for brush selection.

;; ## What's Next
;;
;; - [**Distributions**](./plotje_book.distributions.html) -- histograms, density curves, boxplots, violins
;; - [**Relationships**](./plotje_book.relationships.html) -- heatmaps, contours, and 2D density
;; - [**Customization**](./plotje_book.customization.html) -- colors, palettes, themes, and annotations
