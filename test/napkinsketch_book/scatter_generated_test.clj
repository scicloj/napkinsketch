(ns
 napkinsketch-book.scatter-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l18
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l25
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/plot))


(deftest
 t6_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v5_l25)))


(def
 v8_l39
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t9_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v8_l39)))


(def
 v11_l53
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t12_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v11_l53)))


(def
 v14_l67
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color "#E74C3C"})
  sk/plot))


(deftest
 t15_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v14_l67)))


(def
 v17_l80
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day})
  (sk/plot
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t18_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 244 (:points s))
      (>= (:width s) 700)
      (some #{"Tips by Day"} (:texts s)))))
   v17_l80)))


(def
 v20_l99
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day, :size :size})
  sk/plot))


(deftest
 t21_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v20_l99)))


(def
 v23_l110
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day, :size :size, :alpha 0.6})
  sk/plot))


(deftest
 t24_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v23_l110)))


(def
 v26_l124
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay-point {:jitter true})
  sk/plot))


(deftest
 t27_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l124)))


(def
 v29_l135
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay-point {:jitter 10, :alpha 0.5})
  sk/plot))


(deftest
 t30_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l135)))


(def
 v32_l149
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :petal_length})
  sk/plot))


(deftest
 t33_l154
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v32_l149)))


(def
 v35_l161
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :petal_length, :size :petal_width, :alpha 0.7})
  sk/plot))


(deftest
 t36_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v35_l161)))
