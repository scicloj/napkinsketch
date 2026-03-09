;; # Quickstart
;;
;; A minimal introduction to napkinsketch — composable plotting in Clojure.

(ns napkinsketch-book.quickstart
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Loading Data

;; We use the classic iris dataset throughout these examples.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(tc/head iris)

;; ## Scatter Plot

;; The simplest plot: map columns to x and y, then apply a point mark.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    sk/plot)

;; ## Colored Scatter

;; Bind `:color` to a column to color points by group.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

;; ## Scatter with Regression

;; Layer multiple marks: points and linear regression lines.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

;; ## Histogram

;; Pass a single column to get a histogram (automatic binning).

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    sk/plot)

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

;; ## Flipped Bar Chart

;; Use `coord :flip` for horizontal bars.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

;; ## Line Plot

;; Connect points with lines. Here we use a simple time-series-like dataset.

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/view [[:x :y]])
    (sk/lay (sk/line))
    sk/plot)

;; ## Custom Options

;; Pass options to `plot` for width, height, title, and axis labels.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 500 :height 350
              :title "Iris Petals"
              :x-label "Petal Length (cm)"
              :y-label "Petal Width (cm)"}))
