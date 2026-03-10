;; # Layers
;;
;; Composing multiple marks on the same plot.
;; The `lay` function applies layers to views — each layer adds
;; a mark or statistical transform.

(ns napkinsketch-book.layers
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; ## Point + Line

;; Scatter points connected by a line.

(-> {:x [1 2 3 4 5 6 7]
     :y [2 4 3 6 5 8 7]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/line))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 7 (:points s))
                                (= 1 (:lines s)))))])

;; ## Points + Regression

;; Overlay a linear model on scatter data.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Per-Group Regression

;; Color both points and regression lines by species.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (some #{"setosa"} (:texts s)))))])

;; ## Fixed Color on One Layer

;; Points colored by group, but a single overall regression line.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Multiple Column Pairs

;; Comparing sepal and petal measurements side by side.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Sepal: Length vs Width"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (some #{"Sepal: Length vs Width"} (:texts s)))))])

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Petal: Length vs Width"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Tips: Bill vs Tip with Regression

;; Multiple aesthetics combined: scatter + per-group regression.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    (sk/plot {:title "Tipping Behavior"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (= 2 (:lines s))
                                (some #{"Tipping Behavior"} (:texts s))
                                (some #{"Total Bill ($)"} (:texts s)))))])

;; ## Line + Points with Colors

;; A multi-group line plot with points overlaid.

(def growth
  (tc/dataset {:day   [1 2 3 4 5 1 2 3 4 5]
               :value [10 15 13 18 22 8 12 11 16 19]
               :group [:a :a :a :a :a :b :b :b :b :b]}))

(-> growth
    (sk/view [[:day :value]])
    (sk/lay (sk/line {:color :group})
            (sk/point {:color :group}))
    (sk/plot {:title "Growth Over Time"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 2 (:lines s))
                                (some #{"Growth Over Time"} (:texts s)))))])

;; ## Points with Error Bars
;;
;; Combining `point` and `errorbar` layers shows
;; measurements with uncertainty.

(def experiment
  (tc/dataset {:condition ["A" "B" "C" "D"]
               :mean [10.0 15.0 12.0 18.0]
               :ci_lo [8.0 12.0 9.5 15.5]
               :ci_hi [12.0 18.0 14.5 20.5]}))

(-> experiment
    (sk/view [[:condition :mean]])
    (sk/lay (sk/point {:size 5})
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

;; ## Lollipop Chart
;;
;; A lighter alternative to bar charts.

(-> experiment
    (sk/view [[:condition :mean]])
    (sk/lay (sk/lollipop))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## Lollipop with Error Bars
;;
;; Composing lollipop stems with error bars:

(-> experiment
    (sk/view [[:condition :mean]])
    (sk/lay (sk/lollipop)
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                ;; 4 stems + 12 errorbars
                                (= 16 (:lines s)))))])
