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

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Colored Histogram

;; Split by species — each group gets its own color.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> iris
    (sk/view :petal_width)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Bar Chart

;; Count species occurrences.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Colored Bar Chart

;; Tips dataset: count by day, colored by smoking status.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Stacked Bar Chart

;; Same data, but stacked instead of dodged.

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :time}))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Histogram with Custom Title

(-> tips
    (sk/view :total_bill)
    (sk/lay (sk/histogram))
    (sk/plot {:title "Distribution of Total Bill"
              :x-label "Amount ($)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])
