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

;; ## Your First Plot
;;
;; Load the classic [iris](https://en.wikipedia.org/wiki/Iris_flower_data_set) dataset and scatter two columns. That is all
;; it takes — one `def` and one pipeline:

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; - `tc/dataset` loads a CSV into a
;; [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) dataset
;; (which we typically use through
;; [Tablecloth](https://scicloj.github.io/tablecloth/)).
;; - The `:key-fn keyword` option converts
;; the CSV header strings to Clojure keywords, which is conventional.
;; - `sk/xkcd7-lay-point` adds a scatter layer — each row becomes a dot.

;; ## Plain Data
;;
;; You do not need to load a CSV — Napkinsketch accepts plain Clojure
;; data and coerces it into a dataset internally.
;; A map of columns works directly:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; When the dataset has few columns, you can skip the column names —
;; napkinsketch infers them from the dataset shape:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; See [**Core Concepts**](./napkinsketch_book.core_concepts.html) for more input formats.

;; String column names also work — keywords are conventional but not
;; required:

(-> {"x" [1 2 3 4 5] "y" [2 4 3 5 4]}
    (sk/xkcd7-lay-point "x" "y"))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## Color

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s))
                                (some #{"sepal length"} (:texts s)))))])

;; ## More Chart Types
;;
;; Each `sk/xkcd7-lay-*` function adds a different chart type.
;;
;; **Histogram** — pass a single column for automatic binning:

(-> iris
    (sk/xkcd7-lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s))
                                (zero? (:points s)))))])

;; **Bar chart** — count occurrences of a categorical column:

(-> iris
    (sk/xkcd7-lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; **Horizontal bars** — flip with `sk/xkcd7-coord`:

(-> iris
    (sk/xkcd7-lay-bar :species)
    (sk/xkcd7-coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; **Line chart** — connect points in order:

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/xkcd7-lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:lines s))
                                (zero? (:points s)))))])

;; **Boxplot** — compare distributions across categories:

(-> iris
    (sk/xkcd7-lay-boxplot :species :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; See the [**Methods**](./napkinsketch_book.methods.html) chapter for the full list of 25 chart types.

;; ## Inference
;;
;; `sk/xkcd7-view` can pick the chart type for you based on column types.
;; Two numerical columns produce a scatter plot:

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single categorical column produces a bar chart:

(-> iris
    (sk/xkcd7-view :species))

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> iris
    (sk/xkcd7-view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; See the [**Inference Rules**](./napkinsketch_book.inference_rules.html) chapter for the full set of rules.

;; ## Multiple Layers
;;
;; Use `sk/xkcd7-sketch` to set shared aesthetics and `sk/xkcd7-view`
;; to set column mappings, then add layers with `sk/xkcd7-lay-*`.
;; All layers inherit the shared mappings.
;; Here `sk/xkcd7-lay-lm` adds a linear model (regression line) per group:

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Titles and Labels
;;
;; Use `sk/xkcd7-options` for width, height, title, and axis labels:

(-> iris
    (sk/xkcd7-lay-point :petal_length :petal_width {:color :species})
    (sk/xkcd7-options {:width 500 :height 350
                       :title "Iris Petals"
                       :x-label "Petal Length (cm)"
                       :y-label "Petal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Petals"} (:texts s))
                                (some #{"Petal Length (cm)"} (:texts s)))))])

;; ## Dashboards
;;
;; Combine multiple plots with `sk/arrange`:

(sk/arrange [(sk/xkcd7-lay-point iris :sepal_length :sepal_width {:color :species})
             (sk/xkcd7-lay-histogram iris :sepal_length {:color :species})]
            {:cols 2})

(kind/test-last [(fn [v] (vector? v))])

;; ## Export

;; Save a plot to SVG with `sk/save`:
;;
;; ```clojure
;; (-> iris
;;     (sk/xkcd7-lay-point :sepal_length :sepal_width)
;;     (sk/save "my-plot.svg"))
;; ```
;;
;; ## What's Next
;;
;; - [**Composable Plotting**](./napkinsketch_book.composability.html) — how sketches, views, and layers compose
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) — data formats, marks, stats, color, grouping, coordinates
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — the most common starting point for chart types
;; - [**Cookbook**](./napkinsketch_book.cookbook.html) — recipes for common multi-layer plots
