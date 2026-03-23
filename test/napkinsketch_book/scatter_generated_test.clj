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


(def v5_l25 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t6_l28
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v5_l25)))


(def
 v8_l37
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t9_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v8_l37)))


(def
 v11_l49
 (-> iris (sk/lay-point :petal_length :petal_width {:color :species})))


(deftest
 t12_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v11_l49)))


(def
 v14_l61
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color "#E74C3C"})))


(deftest
 t15_l64
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v14_l61)))


(def
 v17_l72
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :day})
  (sk/options
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t18_l79
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
   v17_l72)))


(def
 v20_l90
 (-> tips (sk/lay-point :total_bill :tip {:color :day, :size :size})))


(deftest
 t21_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v20_l90)))


(def
 v23_l99
 (->
  tips
  (sk/lay-point
   :total_bill
   :tip
   {:color :day, :size :size, :alpha 0.6})))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v23_l99)))


(def
 v26_l111
 (-> iris (sk/lay-point :species :sepal_width {:jitter true})))


(deftest
 t27_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l111)))


(def
 v29_l120
 (->
  iris
  (sk/lay-point :species :sepal_width {:jitter 10, :alpha 0.5})))


(deftest
 t30_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l120)))


(def
 v32_l132
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t33_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v32_l132)))


(def
 v35_l142
 (->
  iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :petal_length, :size :petal_width, :alpha 0.7})))


(deftest
 t36_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v35_l142)))
