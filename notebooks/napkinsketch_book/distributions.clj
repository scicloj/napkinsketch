;; # Distributions
;;
;; Histograms and bar charts — visualizing distributions
;; of numerical and categorical data.

(ns napkinsketch-book.distributions
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; ## Histogram

;; Distribution of sepal length across all species.

(-> iris
    (ns/view :sepal_length)
    (ns/lay (ns/histogram))
    ns/plot)

;; ## Colored Histogram

;; Split by species — each group gets its own color.

(-> iris
    (ns/view :sepal_length)
    (ns/lay (ns/histogram {:color :species}))
    ns/plot)

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> iris
    (ns/view :petal_width)
    (ns/lay (ns/histogram))
    ns/plot)

;; ## Bar Chart

;; Count species occurrences.

(-> iris
    (ns/view :species)
    (ns/lay (ns/bar))
    ns/plot)

;; ## Colored Bar Chart

;; Tips dataset: count by day, colored by smoking status.

(-> tips
    (ns/view :day)
    (ns/lay (ns/bar {:color :smoker}))
    ns/plot)

;; ## Stacked Bar Chart

;; Same data, but stacked instead of dodged.

(-> tips
    (ns/view :day)
    (ns/lay (ns/stacked-bar {:color :smoker}))
    ns/plot)

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> iris
    (ns/view :species)
    (ns/lay (ns/bar))
    (ns/coord :flip)
    ns/plot)

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> tips
    (ns/view :day)
    (ns/lay (ns/bar {:color :time}))
    (ns/coord :flip)
    ns/plot)

;; ## Histogram with Custom Title

(-> tips
    (ns/view :total_bill)
    (ns/lay (ns/histogram))
    (ns/plot {:title "Distribution of Total Bill"
              :x-label "Amount ($)"}))
