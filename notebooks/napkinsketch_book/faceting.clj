
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
;; `sk/xkcd7-facet` splits views by one categorical column.
;; The default layout is a horizontal row of panels:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Each species gets its own panel with a strip label on top.
;; Scales are shared by default — all panels use the same x and y range,
;; making direct comparison easy.

;; ## Vertical Facet
;;
;; Pass `:col` as the direction for a vertical column of panels:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species :col))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; ## Facet Grid
;;
;; `sk/xkcd7-facet-grid` splits by two columns — one for rows, one for columns:

(-> data/tips
    (sk/xkcd7-lay-point :total_bill :tip {:color :sex})
    (sk/xkcd7-facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; Row labels appear on the right, column labels on top.

;; ## Faceted Histogram

(-> data/iris
    (sk/xkcd7-lay-histogram :sepal_length {:color :species})
    (sk/xkcd7-facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Regression
;;
;; Layers compose with faceting — scatter plus regression per panel:

(-> data/tips
    (sk/xkcd7-view :total_bill :tip {:color :sex})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm
    (sk/xkcd7-facet-grid :smoker :sex))

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
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species)
    (sk/xkcd7-options {:scales :shared}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Free y — each panel has its own y-range:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species)
    (sk/xkcd7-options {:scales :free-y}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Other options: `:free-x`, `:free` (both axes free).

;; ## Facet Plan Structure
;;
;; Under the hood, faceting produces multiple panels in the plan:

(def faceted-pl
  (-> data/iris
      (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
      (sk/xkcd7-facet :species)
      sk/xkcd7-plan))

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
    (sk/xkcd7-view (sk/cross cols cols))
    (sk/xkcd7-lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 16 (:panels s))
                                (= 2400 (:points s)))))])

;; Diagonal panels (where x = y) show points along the identity line
;; since every row has the same value for both axes. Off-diagonal
;; panels share scales per column (x) and per row (y), so each column
;; of plots has the same x-axis and each row has the same y-axis.

;; ## Distribution Helper
;;
;; `sk/xkcd7-distribution` creates diagonal views — one histogram per column:

(-> data/iris
    (sk/xkcd7-distribution :sepal_length :sepal_width :petal_length)
    (sk/xkcd7-lay-histogram {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Bar Chart

(-> data/penguins
    (sk/xkcd7-lay-bar :species {:color :species})
    (sk/xkcd7-facet :island))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 5 (:polygons s)))))])

;; ## Labels and Faceting
;;
;; `sk/xkcd7-options` works with faceted plots:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species)
    (sk/xkcd7-options {:title "Iris by Species"
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
