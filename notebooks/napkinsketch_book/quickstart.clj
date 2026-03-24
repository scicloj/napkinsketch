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
;; Napkinsketch coerces its data argument to a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset.
;; You can pass a Clojure map of columns, a sequence of row maps,
;; a CSV path or URL, or an existing dataset.
;;
;; A map of columns (keyword → vector) — the simplest inline form:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; A sequence of row maps — Tablecloth pivots rows into columns:

(-> [{:x 1 :y 2 :g "a"} {:x 3 :y 4 :g "a"} {:x 5 :y 6 :g "b"}]
    (sk/lay-point :x :y {:color :g}))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; A Tablecloth dataset from a CSV file:

(-> (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                {:key-fn keyword})
    (sk/lay-point :total_bill :tip))

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ## Colored Scatter

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

;; Connect points with lines. Here we use a simple time-series-like dataset.

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
;; - Combine multiple plots into a dashboard with `sk/arrange`
;;   (see the Cookbook)
;; - Explore faceting, configuration, and customization in the
;;   How-to Guides
