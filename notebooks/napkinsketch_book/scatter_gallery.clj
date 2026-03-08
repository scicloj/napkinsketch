;; # Scatter Gallery
;;
;; Diverse scatter plot examples that challenge the API
;; with different data shapes, aesthetics, and edge cases.

(ns napkinsketch-book.scatter-gallery
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

(def mpg (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
                      {:key-fn keyword}))

;; ## Basic Scatter

;; Sepal dimensions, no color — the default mark.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Colored by Species

;; Adding `:color :species` groups points by species with distinct colors.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    ns/plot)

;; ## Petal Dimensions

;; Petal length vs width — a strongly correlated pair.

(-> iris
    (ns/view [[:petal_length :petal_width]])
    (ns/lay (ns/point {:color :species}))
    ns/plot)

;; ## Scatter with Regression Lines

;; Overlay per-group regression on the same data.

(-> iris
    (ns/view [[:petal_length :petal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    ns/plot)

;; ## Tips Dataset

;; Restaurant tipping data — total bill vs tip, colored by smoking status.

(-> tips
    (ns/view [[:total_bill :tip]])
    (ns/lay (ns/point {:color :smoker}))
    ns/plot)

;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> tips
    (ns/view [[:total_bill :tip]])
    (ns/lay (ns/point {:color :smoker})
            (ns/lm {:color :smoker}))
    ns/plot)

;; ## MPG Dataset

;; Horsepower vs miles per gallon, colored by origin.

(-> mpg
    (ns/view [[:horsepower :mpg]])
    (ns/lay (ns/point {:color :origin}))
    ns/plot)

;; ## Fixed Color

;; A fixed color string (not a column reference) applies to all points.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color "#E74C3C"}))
    ns/plot)

;; ## Small Dataset

;; Even a three-point dataset should render cleanly.

(-> {:x [1 5 9] :y [2 8 3]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Single Point

;; Edge case: just one data point.

(-> {:x [3] :y [7]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Custom Dimensions

;; Wider plot with custom title and labels.

(-> tips
    (ns/view [[:total_bill :tip]])
    (ns/lay (ns/point {:color :day}))
    (ns/plot {:width 700 :height 300
              :title "Tips by Day"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))
