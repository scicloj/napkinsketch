;; # Quickstart
;;
;; A minimal introduction to napkinsketch — composable plotting in Clojure.

(ns napkinsketch-book.quickstart
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Loading Data

;; We use the classic iris dataset throughout these examples.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(tc/head iris)

;; ## Scatter Plot

;; The simplest plot: map columns to x and y, then apply a point mark.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Colored Scatter

;; Bind `:color` to a column to color points by group.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    ns/plot)

;; ## Scatter with Regression

;; Layer multiple marks: points and linear regression lines.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    ns/plot)

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (ns/view :sepal_length)
    (ns/lay (ns/histogram))
    ns/plot)

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (ns/view :species)
    (ns/lay (ns/bar))
    ns/plot)

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (ns/view :species)
    (ns/lay (ns/bar))
    (ns/coord :flip)
    ns/plot)

;; ## Line Plot

;; Connect points with lines. Here we use a simple time-series-like dataset.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (ns/view [[:x :y]])
    (ns/lay (ns/line))
    ns/plot)

;; ## Custom Options

;; Pass options to `plot` for width, height, title, and axis labels.

(-> iris
    (ns/view [[:petal_length :petal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:width 500 :height 350
              :title "Iris Petals"
              :x-label "Petal Length (cm)"
              :y-label "Petal Width (cm)"}))
