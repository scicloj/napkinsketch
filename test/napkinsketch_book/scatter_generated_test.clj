(ns
 napkinsketch-book.scatter-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l12
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l15
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l22
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t6_l27
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v5_l22)))


(def
 v8_l36
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t9_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v8_l36)))


(def
 v11_l50
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t12_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v11_l50)))


(def
 v14_l64
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color "#E74C3C"}))
  sk/plot))


(deftest
 t15_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v14_l64)))


(def
 v17_l77
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day}))
  (sk/plot
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t18_l85
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
   v17_l77)))


(def
 v20_l96
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day, :size :size}))
  sk/plot))


(deftest
 t21_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v20_l96)))


(def
 v23_l107
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day, :size :size, :alpha 0.6}))
  sk/plot))


(deftest
 t24_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v23_l107)))


(def
 v26_l121
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/point {:jitter true}))
  sk/plot))


(deftest
 t27_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l121)))


(def
 v29_l132
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/point {:jitter 10, :alpha 0.5}))
  sk/plot))


(deftest
 t30_l137
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l132)))


(def
 v32_l146
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t33_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v32_l146)))


(def
 v35_l158
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :petal_length, :size :petal_width, :alpha 0.7}))
  sk/plot))


(deftest
 t36_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v35_l158)))
