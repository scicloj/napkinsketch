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

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Colored by Species

;; Adding `:color :species` groups points by species with distinct colors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Petal Dimensions

;; Petal length vs width — a strongly correlated pair.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Scatter with Regression Lines

;; Overlay per-group regression on the same data.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
               (let [body (nth v 2)]
                 (and (vector? body) (= :g (first body))))))])

;; ## Tips Dataset

;; Restaurant tipping data — total bill vs tip, colored by smoking status.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## MPG Dataset

;; Horsepower vs miles per gallon, colored by origin.

(-> mpg
    (sk/view [[:horsepower :mpg]])
    (sk/lay (sk/point {:color :origin}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Fixed Color

;; A fixed color string (not a column reference) applies to all points.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color "#E74C3C"}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Small Dataset

;; Even a three-point dataset should render cleanly.

(-> {:x [1 5 9] :y [2 8 3]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Single Point

;; Edge case: just one data point.

(-> {:x [3] :y [7]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (map? (second v))
               (vector? (nth v 2))))])

;; ## Custom Dimensions

;; Wider plot with custom title and labels.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :day}))
    (sk/plot {:width 700 :height 300
              :title "Tips by Day"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
               (let [attrs (second v)]
                 (and (map? attrs)
                      (>= (:width attrs) 700)))))])

;; ## Bubble Plot
;;
;; Map `:size` to a numeric column to create a bubble plot.
;; Each point's radius reflects the column value.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :day :size :size}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s)))))])

;; Combine size with alpha for dense data:

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :day :size :size :alpha 0.6}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s)))))])

;; ## Jitter
;;
;; When plotting a numeric column against a categorical column,
;; points overlap. Use `:jitter true` to add random pixel offsets.

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/point {:jitter true}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Control the jitter amount in pixels:

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/point {:jitter 10 :alpha 0.5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Continuous Color
;;
;; When `:color` maps to a numeric column, napkinsketch uses a
;; viridis gradient instead of discrete palette colors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :petal_length}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])

;; Continuous color with size — a color-size bubble plot:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :petal_length :size :petal_width :alpha 0.7}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])
