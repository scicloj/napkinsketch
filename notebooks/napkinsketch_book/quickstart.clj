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
    (sk/lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `tc/dataset` loads a CSV into a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset (a
;; wrapper around [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset)). `{:key-fn keyword}` converts
;; the CSV header strings to Clojure keywords, which is conventional.
;; `sk/lay-point` adds a scatter layer — each row becomes a dot.

;; ## Plain Data
;;
;; You do not need to load a CSV — Napkinsketch accepts plain Clojure
;; data and coerces it to a [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) dataset (which we
;; typically use through
;; [Tablecloth](https://scicloj.github.io/tablecloth/)).
;; A map of columns works directly:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; See the Core Concepts chapter for more input formats.

;; String column names also work — keywords are conventional but not
;; required:

(-> {"x" [1 2 3 4 5] "y" [2 4 3 5 4]}
    (sk/lay-point "x" "y"))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## Color

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s))
                                (some #{"sepal length"} (:texts s)))))])

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s))
                                (zero? (:points s)))))])

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (sk/lay-bar :species)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; ## Line Plot

;; Connect points with lines.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:lines s))
                                (zero? (:points s)))))])

;; ## Inference
;;
;; `sk/view` can pick the chart type for you based on column types.
;; Two numerical columns produce a scatter plot:

(-> iris
    (sk/view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single categorical column produces a bar chart:

(-> iris
    (sk/view :species))

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> iris
    (sk/view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Boxplot
;;
;; A categorical column paired with a numerical column — use
;; `sk/lay-boxplot` to compare distributions across groups:

(-> iris
    (sk/lay-boxplot :species :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; See the Inference Rules chapter for the full set of rules.

;; ## Multiple Layers
;;
;; Use `sk/view` to set shared column mappings and aesthetics, then
;; add layers with `sk/lay-*`. All layers inherit the view's mappings:

(-> iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Custom Options

;; Use `sk/options` for width, height, title, and axis labels.

(-> iris
    (sk/lay-point :petal_length :petal_width {:color :species})
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
;; Combine multiple plots into a dashboard with `sk/arrange`:

(sk/arrange [(sk/lay-point iris :sepal_length :sepal_width {:color :species})
             (sk/lay-histogram iris :sepal_length {:color :species})]
            {:cols 2})

(kind/test-last [(fn [v] (vector? v))])

;; Save a plot to SVG with `sk/save`:
;;
;; ```clojure
;; (-> iris
;;     (sk/lay-point :sepal_length :sepal_width)
;;     (sk/save "my-plot.svg"))
;; ```
;;
;; Explore the rest of the book:
;;
;; - **Core Concepts** — views, methods, layers, faceting
;; - **Chart Types** — scatter plots, distributions, bar charts,
;;   time series, polar charts
;; - **How-to Guides** — faceting, configuration, customization,
;;   and recipes for common tasks
