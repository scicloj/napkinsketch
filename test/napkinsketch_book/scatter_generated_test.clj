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
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-point))


(deftest
 t6_l29
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v5_l25)))


(def
 v8_l38
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t9_l42
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v8_l38)))


(def
 v11_l51
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t12_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v11_l51)))


(def
 v14_l64
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color "#E74C3C"})))


(deftest
 t15_l68
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v14_l64)))


(def
 v17_l76
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day})
  (sk/options
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t18_l84
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
   v17_l76)))


(def
 v20_l95
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day, :size :size})))


(deftest
 t21_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v20_l95)))


(def
 v23_l105
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :day, :size :size, :alpha 0.6})))


(deftest
 t24_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v23_l105)))


(def
 v26_l118
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay-point {:jitter true})))


(deftest
 t27_l122
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l118)))


(def
 v29_l128
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay-point {:jitter 10, :alpha 0.5})))


(deftest
 t30_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l128)))


(def
 v32_l141
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :petal_length})))


(deftest
 t33_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v32_l141)))


(def
 v35_l152
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point
   {:color :petal_length, :size :petal_width, :alpha 0.7})))


(deftest
 t36_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v35_l152)))
