;; # Scatter Gallery
;;
;; Diverse scatter plot examples that challenge the API
;; with different data shapes, aesthetics, and edge cases.

(ns napkinsketch-book.scatter-gallery
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

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
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    sk/plot)

;; ## Colored by Species

;; Adding `:color :species` groups points by species with distinct colors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

;; ## Petal Dimensions

;; Petal length vs width — a strongly correlated pair.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

;; ## Scatter with Regression Lines

;; Overlay per-group regression on the same data.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

;; ## Tips Dataset

;; Restaurant tipping data — total bill vs tip, colored by smoking status.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker}))
    sk/plot)

;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    sk/plot)

;; ## MPG Dataset

;; Horsepower vs miles per gallon, colored by origin.

(-> mpg
    (sk/view [[:horsepower :mpg]])
    (sk/lay (sk/point {:color :origin}))
    sk/plot)

;; ## Fixed Color

;; A fixed color string (not a column reference) applies to all points.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color "#E74C3C"}))
    sk/plot)

;; ## Small Dataset

;; Even a three-point dataset should render cleanly.

(-> {:x [1 5 9] :y [2 8 3]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

;; ## Single Point

;; Edge case: just one data point.

(-> {:x [3] :y [7]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

;; ## Custom Dimensions

;; Wider plot with custom title and labels.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :day}))
    (sk/plot {:width 700 :height 300
              :title "Tips by Day"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))
