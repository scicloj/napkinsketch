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

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Points + Regression

;; Overlay a linear model on scatter data.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])

;; ## Per-Group Regression

;; Color both points and regression lines by species.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Fixed Color on One Layer

;; Points colored by group, but a single overall regression line.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Multiple Column Pairs

;; Comparing sepal and petal measurements side by side.
;; Each pair gets its own column binding.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Sepal: Length vs Width"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Petal: Length vs Width"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Tips: Bill vs Tip with Regression

;; Multiple aesthetics combined: scatter + per-group regression.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    (sk/plot {:title "Tipping Behavior"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])

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

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])
