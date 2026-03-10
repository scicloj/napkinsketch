;; # Faceting
;;
;; Faceting splits data into subsets and draws each in its own panel.
;; This is one of the most powerful ways to explore relationships
;; across groups.

(ns napkinsketch-book.faceting
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
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
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Each species gets its own panel with a strip label on top.
;; Scales are shared by default — all panels use the same x and y range,
;; making direct comparison easy.

;; ## Vertical Facet
;;
;; Pass `:col` as the direction for a vertical column of panels:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species :col)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Facet Grid
;;
;; `sk/facet-grid` splits by two columns — one for rows, one for columns:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay (sk/point {:color :sex}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Row labels appear on the right, column labels on top.

;; ## Faceted Histogram

(-> iris
    (sk/view :sepal_length)
    (sk/facet :species)
    (sk/lay (sk/histogram {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Faceted Regression
;;
;; Layers compose with faceting — scatter plus regression per panel:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay (sk/point {:color :sex})
            (sk/lm {:color :sex}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Free Scales
;;
;; By default all panels share the same axis ranges. Use the `:scales`
;; option to let axes vary per panel.
;;
;; Shared (default) — all panels have the same y-range:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:scales :shared}))

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Free y — each panel has its own y-range:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:scales :free-y}))

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Other options: `:free-x`, `:free` (both axes free).

;; ## Facet Sketch Structure
;;
;; Under the hood, faceting produces multiple panels in the sketch:

(def faceted-sk
  (-> iris
      (sk/view [[:sepal_length :sepal_width]])
      (sk/facet :species)
      (sk/lay (sk/point {:color :species}))
      sk/sketch))

(:grid faceted-sk)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

(count (:panels faceted-sk))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and a strip label:

(mapv #(select-keys % [:row :col :col-label])
      (:panels faceted-sk))

(kind/test-last [(fn [ps] (= 3 (count ps)))])

;; ## SPLOM (Scatter Plot Matrix)
;;
;; `sk/cross` generates all pairs of columns. Combined with the
;; multi-variable layout, this produces a scatter plot matrix (SPLOM):

(def cols [:sepal_length :sepal_width :petal_length :petal_width])

(-> iris
    (sk/view (sk/cross cols cols))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; Diagonal panels (where x = y) automatically become histograms.
;; Off-diagonal panels share scales per column (x) and per row (y),
;; so each column of plots has the same x-axis and each row has the
;; same y-axis.

;; ## Upper-Triangle Pairs
;;
;; `sk/pairs` returns only the upper-triangle pairs — no diagonal,
;; no mirrored pairs:

(sk/pairs [:a :b :c :d])

(kind/test-last [(fn [v] (= [[:a :b] [:a :c] [:a :d] [:b :c] [:b :d] [:c :d]] v))])

(-> iris
    (sk/view (sk/pairs cols))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Distribution Helper
;;
;; `sk/distribution` creates diagonal views — one histogram per column:

(-> (sk/distribution iris :sepal_length :sepal_width :petal_length)
    (sk/lay (sk/histogram {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Faceted Bar Chart

(-> penguins
    (sk/view :species)
    (sk/facet :island)
    (sk/lay (sk/bar {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## Labels and Faceting
;;
;; `sk/labs` works with faceted plots:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    (sk/labs {:title "Iris by Species"
              :x "Sepal Length (cm)"
              :y "Sepal Width (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])
