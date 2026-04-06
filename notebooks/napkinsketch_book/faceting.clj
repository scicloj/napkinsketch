
;; # Faceting
;;
;; Faceting splits data into subsets and draws each in its own panel,
;; making it easy to compare patterns across groups.

(ns napkinsketch-book.faceting
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Facet Wrap
;;
;; `sk/facet` splits views by one categorical column.
;; The default direction is `:col` — a horizontal row of panels:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Each species gets its own panel with a strip label on top.
;; Scales are shared by default — all panels use the same x and y range,
;; making direct comparison easy.

;; ## Vertical Facet
;;
;; Pass `:row` as the direction for a vertical column of panels:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species :row))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; ## Facet Grid
;;
;; `sk/facet-grid` splits by two columns — one for rows, one for columns:

(-> data/tips
    (sk/lay-point :total_bill :tip {:color :sex})
    (sk/facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; Row labels appear on the right, column labels on top.

;; ## Faceted Histogram

(-> data/iris
    (sk/lay-histogram :sepal_length {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Regression
;;
;; Layers compose with faceting — scatter plus regression per panel:

(-> data/tips
    (sk/view :total_bill :tip {:color :sex})
    sk/lay-point
    sk/lay-lm
    (sk/facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s))
                                (= 4 (:lines s)))))])

;; ## Free Scales (Independent Axis Ranges)
;;
;; By default all panels share the same axis ranges. Use the `:scales`
;; option to let axes vary per panel.
;;
;; Shared (default) — all panels have the same y-range:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species)
    (sk/options {:scales :shared}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Free y — each panel has its own y-range:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species)
    (sk/options {:scales :free-y}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Other options: `:free-x`, `:free` (both axes free).

;; ## Facet Plan Structure
;;
;; Under the hood, faceting produces multiple panels in the plan:

(def faceted-pl
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width {:color :species})
      (sk/facet :species)
      sk/plan))

(:grid faceted-pl)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

(count (:panels faceted-pl))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and a strip label:

(:panels faceted-pl)

(kind/test-last [(fn [ps] (= 3 (count ps)))])

;; ## [SPLOM](https://en.wikipedia.org/wiki/Scatter_plot#Scatter_plot_matrices) (Scatter Plot Matrix)
;;
;; `sk/cross` generates all pairs of columns. Combined with the
;; multi-variable layout, this produces a scatter plot matrix (SPLOM):

(def cols [:sepal_length :sepal_width :petal_length :petal_width])

(-> data/iris
    (sk/view (sk/cross cols cols) {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 16 (:panels s))
                                (= (* 12 150) (:points s))
                                (pos? (:polygons s)))))])

;; Diagonal panels (where x = y) show histograms — inference detects
;; same-column pairs. Off-diagonal panels show scatter plots. All
;; panels are colored by species (shared via `view`).

;; ## Comparing Multiple Columns
;;
;; Pass a vector of column names to create one panel per column:

(sk/lay-histogram data/iris [:sepal_length :sepal_width :petal_length] {:color :species})

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Bar Chart

(-> data/penguins
    (sk/lay-bar :species {:color :species})
    (sk/facet :island))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 5 (:polygons s)))))])

;; ## Labels and Faceting
;;
;; `sk/options` works with faceted plots:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species)
    (sk/options {:title "Iris by Species"
                 :x-label "Sepal Length (cm)" :y-label "Sepal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s))
                                (some #{"Sepal Length (cm)"} (:texts s)))))])

;; ## What's Next
;;
;; - [**Troubleshooting**](./napkinsketch_book.troubleshooting.html) — common issues and how to fix them
;; - [**API Reference**](./napkinsketch_book.api_reference.html) — complete function listing with docstrings
