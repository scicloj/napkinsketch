;; # Scatter
;;
;; Point mark variations — color, size, alpha, shape, jitter,
;; and continuous color scale.

(ns napkinsketch-book.scatter
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Basic Scatter

;; Sepal dimensions, no color — the default mark.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Colored by Species

;; Adding `:color :species` groups points by species with distinct colors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Petal Dimensions

;; Petal length vs width — a strongly correlated pair.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (zero? (:lines s)))))])

;; ## Fixed Color

;; A fixed color string (not a column reference) applies to all points.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color "#E74C3C"}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Custom Dimensions

;; Wider plot with custom title and labels.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :day}))
    (sk/plot {:width 700 :height 300
              :title "Tips by Day"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 244 (:points s))
                                (>= (:width s) 700)
                                (some #{"Tips by Day"} (:texts s)))))])

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

;; Combine size with alpha for dense data.

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

;; Control the jitter amount in pixels.

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/point {:jitter 10 :alpha 0.5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Continuous Color
;;
;; When `:color` maps to a numeric column, Napkinsketch uses a
;; viridis gradient instead of discrete palette colors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :petal_length}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])

;; Continuous color with size — a color-size bubble plot.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :petal_length :size :petal_width :alpha 0.7}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"petal length"} (:texts s)))))])