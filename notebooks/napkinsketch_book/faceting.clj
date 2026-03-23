;; # Faceting
;;
;; Faceting splits data into subsets and draws each in its own panel.
;; This is one of the most powerful ways to explore relationships
;; across groups.

(ns napkinsketch-book.faceting
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Sample Data

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

;; ## Facet Wrap
;;
;; `sk/facet` splits views by one categorical column.
;; The default layout is a horizontal row of panels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Each species gets its own panel with a strip label on top.
;; Scales are shared by default — all panels use the same x and y range,
;; making direct comparison easy.

;; ## Vertical Facet
;;
;; Pass `:col` as the direction for a vertical column of panels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species :col)
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; ## Facet Grid
;;
;; `sk/facet-grid` splits by two columns — one for rows, one for columns:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay-point {:color :sex}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; Row labels appear on the right, column labels on top.

;; ## Faceted Histogram

(-> iris
    (sk/view :sepal_length)
    (sk/facet :species)
    (sk/lay-histogram {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Regression
;;
;; Layers compose with faceting — scatter plus regression per panel:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay-point {:color :sex})
    (sk/lay-lm {:color :sex}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s))
                                (= 4 (:lines s)))))])

;; ## Free Scales
;;
;; By default all panels share the same axis ranges. Use the `:scales`
;; option to let axes vary per panel.
;;
;; Shared (default) — all panels have the same y-range:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay-point {:color :species})
    (sk/options {:scales :shared}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Free y — each panel has its own y-range:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay-point {:color :species})
    (sk/options {:scales :free-y}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Other options: `:free-x`, `:free` (both axes free).

;; ## Facet Sketch Structure
;;
;; Under the hood, faceting produces multiple panels in the sketch:

(def faceted-sk
  (-> iris
      (sk/view [[:sepal_length :sepal_width]])
      (sk/facet :species)
      (sk/lay-point {:color :species})
      sk/sketch))

(:grid faceted-sk)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

(count (:panels faceted-sk))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and a strip label:

(:panels faceted-sk)

(kind/test-last [(fn [ps] (= 3 (count ps)))])

;; ## SPLOM (Scatter Plot Matrix)
;;
;; `sk/cross` generates all pairs of columns. Combined with the
;; multi-variable layout, this produces a scatter plot matrix (SPLOM):

(def cols [:sepal_length :sepal_width :petal_length :petal_width])

(-> iris
    (sk/view (sk/cross cols cols))
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 16 (:panels s))
                                (= 2400 (:points s)))))])

;; Diagonal panels (where x = y) show points along the identity line
;; since every row has the same value for both axes. Off-diagonal
;; panels share scales per column (x) and per row (y), so each column
;; of plots has the same x-axis and each row has the same y-axis.

;; ## Distribution Helper
;;
;; `sk/distribution` creates diagonal views — one histogram per column:

(-> (sk/distribution iris :sepal_length :sepal_width :petal_length)
    (sk/lay-histogram {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceted Bar Chart

(-> penguins
    (sk/view :species)
    (sk/facet :island)
    (sk/lay-bar {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 9 (:polygons s)))))])

;; ## Labels and Faceting
;;
;; `sk/labs` works with faceted plots:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay-point {:color :species})
    (sk/labs {:title "Iris by Species"
              :x "Sepal Length (cm)"
              :y "Sepal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s))
                                (some #{"Sepal Length (cm)"} (:texts s)))))])