;; # Configuration
;;
;; Customizing plots: dimensions, labels, titles, scales, and visual options.

(ns napkinsketch-book.config
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

;; ## Custom Width and Height

;; A wide, short plot.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:width 800 :height 250}))

;; A tall, narrow plot.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:width 300 :height 500}))

;; ## Titles and Labels

;; Override axis labels and add a title.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:title "Iris Sepal Measurements"
              :x-label "Length (cm)"
              :y-label "Width (cm)"}))

;; ## Log Scale

;; Use a log scale for data spanning orders of magnitude.

(def exponential-data
  (tc/dataset {:x (range 1 50)
               :y (mapv #(* 2 (Math/pow 1.1 %)) (range 1 50))}))

;; Linear scale — hard to see the structure.

(-> exponential-data
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    (ns/plot {:title "Linear Scale"}))

;; Log y-scale — reveals the exponential trend.

(-> exponential-data
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    (ns/scale :y :log)
    (ns/plot {:title "Log Y Scale"}))

;; ## Fixed Scale Domain

;; Lock the y-axis to a specific range.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/scale :y {:type :linear :domain [0 6]})
    (ns/plot {:title "Fixed Y Domain [0, 6]"}))

;; ## Custom Config

;; Pass visual configuration overrides via `:config`.

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:config {:point-radius 5
                       :point-opacity 0.5}}))

;; ## Value Bar

;; Pre-computed values (no counting), using `value-bar`.

(def summary
  (tc/dataset {:category [:a :b :c :d]
               :value [42 28 35 19]}))

(-> summary
    (ns/view [[:category :value]])
    (ns/lay (ns/value-bar))
    (ns/plot {:title "Pre-computed Values"}))

;; ## Value Bars Flipped

(-> summary
    (ns/view [[:category :value]])
    (ns/lay (ns/value-bar))
    (ns/coord :flip)
    (ns/plot {:title "Horizontal Value Bars"}))
