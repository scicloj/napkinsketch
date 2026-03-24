(ns
 napkinsketch-book.distributions-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l20
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def v6_l27 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t7_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l27)))


(def
 v9_l39
 (-> iris (sk/lay-histogram :sepal_length {:color :species})))


(deftest
 t10_l42
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l39)))


(def v12_l51 (-> iris (sk/lay-histogram :petal_width)))


(deftest
 t13_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l51)))


(def
 v15_l61
 (->
  tips
  (sk/lay-histogram :total_bill)
  (sk/options
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t16_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__74603#] (= "Distribution of Total Bill" p1__74603#))
       (:texts s)))))
   v15_l61)))


(def
 v18_l78
 (->
  iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t19_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l78)))


(def v21_l90 (-> iris (sk/lay-density :sepal_length)))


(deftest
 t22_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v21_l90)))


(def
 v24_l102
 (-> iris (sk/lay-density :sepal_length {:color :species})))


(deftest
 t25_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v24_l102)))


(def v27_l114 (-> iris (sk/lay-density :sepal_length {:bandwidth 0.3})))


(deftest
 t28_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v27_l114)))


(def v30_l126 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t31_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l126)))


(def
 v33_l139
 (-> tips (sk/lay-boxplot :day :total_bill {:color :smoker})))


(deftest
 t34_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v33_l139)))


(def
 v36_l150
 (let
  [sk
   (->
    tips
    (sk/lay-boxplot :day :total_bill {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   box-layer
   (first
    (filter
     (fn* [p1__74604#] (= :boxplot (:mark p1__74604#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t37_l158 (is ((fn [v] (= 2 v)) v36_l150)))


(def
 v39_l165
 (->
  iris
  (sk/lay-boxplot :species :sepal_width)
  (sk/options {:coord :flip})))


(deftest
 t40_l169
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v39_l165)))


(def v42_l180 (-> tips (sk/lay-violin :day :total_bill)))


(deftest
 t43_l183
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v42_l180)))


(def
 v45_l192
 (-> tips (sk/lay-violin :day :total_bill {:color :smoker})))


(deftest
 t46_l195
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v45_l192)))


(def
 v48_l202
 (let
  [sk
   (->
    tips
    (sk/lay-violin :day :total_bill {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   viol-layer
   (first
    (filter
     (fn* [p1__74605#] (= :violin (:mark p1__74605#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t49_l210 (is ((fn [v] (= 2 v)) v48_l202)))


(def
 v51_l215
 (-> iris (sk/lay-violin :species :petal_length) (sk/coord :flip)))


(deftest
 t52_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v51_l215)))


(def v54_l229 (-> iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t55_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v54_l229)))


(def
 v57_l241
 (-> iris (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t58_l244
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v57_l241)))
