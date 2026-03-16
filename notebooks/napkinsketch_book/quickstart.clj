;; # Quickstart
;;
;; A minimal introduction to Napkinsketch — composable plotting in Clojure.

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
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Loading Data
;;
;; We use the classic iris dataset throughout these examples.
;; Note `{:key-fn keyword}` — this converts CSV column names to
;; keywords, which Napkinsketch requires for column references.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(tc/head iris)

(kind/test-last [(fn [v] (= 5 (count (tablecloth.api/rows v))))])

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
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; A sequence of row maps — Tablecloth pivots rows into columns:

(-> [{:x 1 :y 2 :g "a"} {:x 3 :y 4 :g "a"} {:x 5 :y 6 :g "b"}]
    (sk/view :x :y)
    (sk/lay (sk/point {:color :g}))
    sk/plot)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; A CSV URL works directly — `{:key-fn keyword}` converts string
;; column names to keywords:

(-> (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                {:key-fn keyword})
    (sk/view :total_bill :tip)
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ## Two Usage Styles
;;
;; Napkinsketch supports two equivalent styles for building plots.
;;
;; **Pipeline style** — compositional, good for exploration and
;; multi-layer plots:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Direct style** — compact, good for single-layer plots:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                     :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Both produce identical results. The pipeline style composes better
;; when you add `coord`, `scale`, or multiple views. The direct style
;; is shorter for one-off plots.
;;
;; The rest of this quickstart uses the pipeline style.

;; ## Scatter Plot

;; The simplest plot: map columns to x and y, then apply a point mark.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Colored Scatter

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s))
                                (some #{"sepal length"} (:texts s)))))])

;; ## Scatter with Regression

;; Layer multiple marks: points and linear regression lines.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s))
                                (zero? (:points s)))))])

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; ## Line Plot

;; Connect points with lines. Here we use a simple time-series-like dataset.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/view [[:x :y]])
    (sk/lay (sk/line))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:lines s))
                                (zero? (:points s)))))])

;; ## Custom Options

;; Pass options to `plot` for width, height, title, and axis labels.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 500 :height 350
              :title "Iris Petals"
              :x-label "Petal Length (cm)"
              :y-label "Petal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Petals"} (:texts s))
                                (some #{"Petal Length (cm)"} (:texts s)))))])
