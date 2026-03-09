;; # Quickstart
;;
;; A minimal introduction to napkinsketch — composable plotting in Clojure.

;; ## Setup
;;
;; Add napkinsketch to your `deps.edn`:
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
;; keywords, which napkinsketch requires for column references.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(tc/head iris)

;; ## Two Usage Styles
;;
;; napkinsketch supports two equivalent styles for building plots.
;;
;; **Pipeline style** — compositional, good for exploration and
;; multi-layer plots:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; **Direct style** — compact, good for single-layer plots:

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width
                      :color :species})])

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

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

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Colored Scatter

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Scatter with Regression

;; Layer multiple marks: points and linear regression lines.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Line Plot

;; Connect points with lines. Here we use a simple time-series-like dataset.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/view [[:x :y]])
    (sk/lay (sk/line))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Custom Options

;; Pass options to `plot` for width, height, title, and axis labels.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 500 :height 350
              :title "Iris Petals"
              :x-label "Petal Length (cm)"
              :y-label "Petal Width (cm)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs)
                      (>= (:width attrs) 500)))))])
