;; # Layers
;;
;; Composing multiple marks on the same plot.
;; The `lay` function applies layers to views — each layer adds
;; a mark or statistical transform.

(ns napkinsketch-book.layers
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; ## Point + Line

;; Scatter points connected by a line.

(-> {:x [1 2 3 4 5 6 7]
     :y [2 4 3 6 5 8 7]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point) (ns/line))
    ns/plot)

;; ## Points + Regression

;; Overlay a linear model on scatter data.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point) (ns/lm))
    ns/plot)

;; ## Per-Group Regression

;; Color both points and regression lines by species.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    ns/plot)

;; ## Fixed Color on One Layer

;; Points colored by group, but a single overall regression line.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm))
    ns/plot)

;; ## Multiple Column Pairs

;; Comparing sepal and petal measurements side by side.
;; Each pair gets its own column binding.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    (ns/plot {:title "Sepal: Length vs Width"}))

(-> iris
    (ns/view [[:petal_length :petal_width]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    (ns/plot {:title "Petal: Length vs Width"}))

;; ## Tips: Bill vs Tip with Regression

;; Multiple aesthetics combined: scatter + per-group regression.

(-> tips
    (ns/view [[:total_bill :tip]])
    (ns/lay (ns/point {:color :smoker})
            (ns/lm {:color :smoker}))
    (ns/plot {:title "Tipping Behavior"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

;; ## Line + Points with Colors

;; A multi-group line plot with points overlaid.

(def growth
  (tc/dataset {:day   [1 2 3 4 5 1 2 3 4 5]
               :value [10 15 13 18 22 8 12 11 16 19]
               :group [:a :a :a :a :a :b :b :b :b :b]}))

(-> growth
    (ns/view [[:day :value]])
    (ns/lay (ns/line {:color :group})
            (ns/point {:color :group}))
    (ns/plot {:title "Growth Over Time"}))
