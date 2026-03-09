;; # Distributions
;;
;; Histograms and bar charts — visualizing distributions
;; of numerical and categorical data.

(ns napkinsketch-book.distributions
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; ## Histogram

;; Distribution of sepal length across all species.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    sk/plot)

;; ## Colored Histogram

;; Split by species — each group gets its own color.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram {:color :species}))
    sk/plot)

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> iris
    (sk/view :petal_width)
    (sk/lay (sk/histogram))
    sk/plot)

;; ## Bar Chart

;; Count species occurrences.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

;; ## Colored Bar Chart

;; Tips dataset: count by day, colored by smoking status.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :smoker}))
    sk/plot)

;; ## Stacked Bar Chart

;; Same data, but stacked instead of dodged.

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :smoker}))
    sk/plot)

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :time}))
    (sk/coord :flip)
    sk/plot)

;; ## Histogram with Custom Title

(-> tips
    (sk/view :total_bill)
    (sk/lay (sk/histogram))
    (sk/plot {:title "Distribution of Total Bill"
              :x-label "Amount ($)"}))
