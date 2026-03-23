;; # Quickstart
;;
;; A minimal introduction to Napkinsketch

;; ## Setup
;;
;; Add Napkinsketch to your `deps.edn`:
;;
;; ```clojure
;; {:deps {org.scicloj/napkinsketch {:mvn/version "..."}}}
;; ```
;;
;; Then require the API:

(ns napkinsketch-book.quickstart
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; Use [Clay](https://scicloj.github.io/clay/) or other
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible tools
;; to visualize the examples below.

;; ## Loading Data
;;
;; We use the classic iris dataset throughout these examples.
;; `{:key-fn keyword}` converts CSV string column names to keywords,
;; which is conventional in Clojure. (Napkinsketch also accepts string
;; column names, but keywords are idiomatic.)

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

iris

(kind/test-last [(fn [v] (= 150 (count (tablecloth.api/rows v))))])

;; ## Input Data
;;
;; `view` coerces its first argument to a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset.
;; You can pass a Clojure map of columns, a sequence of row maps,
;; a CSV path or URL, or an existing dataset.
;;
;; A map of columns (keyword → vector) — the simplest inline form:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/view :x :y)
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; A sequence of row maps — Tablecloth pivots rows into columns:

(-> [{:x 1 :y 2 :g "a"} {:x 3 :y 4 :g "a"} {:x 5 :y 6 :g "b"}]
    (sk/view :x :y)
    (sk/lay-point {:color :g}))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; A Tablecloth dataset from a CSV file:

(-> (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                {:key-fn keyword})
    (sk/view :total_bill :tip)
    sk/lay-point)

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ## Two Usage Styles
;;
;; Napkinsketch supports two equivalent styles for building plots.
;;
;; **Data shortcut** — compact, good for single-layer plots.
;; `sk/lay-point` accepts raw data and column names directly:

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Pipeline style** — compositional, good for multi-layer plots
;; and when you need `coord`, `scale`, or `facet`. Build a view
;; first, then add layers:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Both produce identical results. The pipeline style composes better
;; when you add `coord`, `scale`, or multiple layers. The data
;; shortcut is shorter for one-off plots.
;;
;; `view` accepts column arguments in two forms:
;;
;; - `(sk/view ds :x :y)` — two keyword arguments, for a single panel
;; - `(sk/view ds [[:x :y]])` — a vector of column pairs; multiple pairs
;;   produce multiple panels (see the Core Concepts chapter)
;;
;; Both forms are equivalent for single-panel plots.
;; The rest of this quickstart uses the pipeline style.

;; ## Scatter Plot

;; The simplest plot: map columns to x and y.

(-> iris
    (sk/view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Napkinsketch infers the scatter method from the column types.
;; You can also choose the method explicitly — `sk/lay-point` says
;; "use a scatter plot regardless of what inference would choose":

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Colored Scatter

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s))
                                (some #{"sepal length"} (:texts s)))))])

;; ## Scatter with Regression

;; Layer multiple methods: points and linear regression.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    (sk/lay-lm {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (sk/view :sepal_length)
    sk/lay-histogram)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s))
                                (zero? (:points s)))))])

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/view :species)
    sk/lay-bar)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (sk/view :species)
    sk/lay-bar
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; ## Line Plot

;; Connect points with lines. Here we use a simple time-series-like dataset.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/view [[:x :y]])
    sk/lay-line)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:lines s))
                                (zero? (:points s)))))])

;; ## Custom Options

;; Pass options to `plot` for width, height, title, and axis labels.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay-point {:color :species})
    (sk/options {:width 500 :height 350
                 :title "Iris Petals"
                 :x-label "Petal Length (cm)"
                 :y-label "Petal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Petals"} (:texts s))
                                (some #{"Petal Length (cm)"} (:texts s)))))])

;; ## Next Steps
;;
;; - Combine multiple plots into a dashboard with `sk/arrange`
;;   (see the Cookbook)
;; - Explore faceting, configuration, and customization in the
;;   How-to Guides
