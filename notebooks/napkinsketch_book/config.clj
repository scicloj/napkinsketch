;; # Configuration
;;
;; Customizing plots: dimensions, labels, titles, scales, and visual options.

(ns napkinsketch-book.config
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

;; ## Custom Width and Height

;; A wide, short plot.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 800 :height 250}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs)
                     (>= (:width attrs) 800)))))])

;; A tall, narrow plot.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 300 :height 500}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs)
                     (>= (:width attrs) 300)))))])

;; ## Titles and Labels

;; Override axis labels and add a title.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Iris Sepal Measurements"
              :x-label "Length (cm)"
              :y-label "Width (cm)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Log Scale

;; Use a log scale for data spanning orders of magnitude.

(def exponential-data
  (tc/dataset {:x (range 1 50)
               :y (mapv #(* 2 (Math/pow 1.1 %)) (range 1 50))}))

;; Linear scale — hard to see the structure.

(-> exponential-data
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    (sk/plot {:title "Linear Scale"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; Log y-scale — reveals the exponential trend.

(-> exponential-data
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    (sk/scale :y :log)
    (sk/plot {:title "Log Y Scale"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Fixed Scale Domain

;; Lock the y-axis to a specific range.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/scale :y {:type :linear :domain [0 6]})
    (sk/plot {:title "Fixed Y Domain [0, 6]"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Custom Config

;; Pass visual configuration overrides via `:config`.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:config {:point-radius 5
                       :point-opacity 0.5}}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Value Bar

;; Pre-computed values (no counting), using `value-bar`.

(def summary
  (tc/dataset {:category [:a :b :c :d]
               :value [42 28 35 19]}))

(-> summary
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    (sk/plot {:title "Pre-computed Values"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Value Bars Flipped

(-> summary
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    (sk/coord :flip)
    (sk/plot {:title "Horizontal Value Bars"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])
