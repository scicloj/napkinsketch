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
       (fn* [p1__84230#] (= "Distribution of Total Bill" p1__84230#))
       (:texts s)))))
   v15_l61)))


(def
 v18_l78
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay-histogram {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t19_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l78)))


(def v21_l91 (-> iris (sk/lay-density :sepal_length)))


(deftest
 t22_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v21_l91)))


(def
 v24_l103
 (-> iris (sk/lay-density :sepal_length {:color :species})))


(deftest
 t25_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v24_l103)))


(def v27_l115 (-> iris (sk/lay-density :sepal_length {:bandwidth 0.3})))


(deftest
 t28_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v27_l115)))


(def v30_l127 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t31_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l127)))


(def
 v33_l140
 (-> tips (sk/lay-boxplot :day :total_bill {:color :smoker})))


(deftest
 t34_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v33_l140)))


(def
 v36_l151
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
     (fn* [p1__84231#] (= :boxplot (:mark p1__84231#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t37_l159 (is ((fn [v] (= 2 v)) v36_l151)))


(def
 v39_l166
 (->
  iris
  (sk/lay-boxplot :species :sepal_width)
  (sk/options {:coord :flip})))


(deftest
 t40_l170
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v39_l166)))


(def v42_l181 (-> tips (sk/lay-violin :day :total_bill)))


(deftest
 t43_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v42_l181)))


(def
 v45_l193
 (-> tips (sk/lay-violin :day :total_bill {:color :smoker})))


(deftest
 t46_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v45_l193)))


(def
 v48_l203
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
     (fn* [p1__84232#] (= :violin (:mark p1__84232#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t49_l211 (is ((fn [v] (= 2 v)) v48_l203)))


(def
 v51_l216
 (-> iris (sk/lay-violin :species :petal_length) (sk/coord :flip)))


(deftest
 t52_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v51_l216)))


(def v54_l230 (-> iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t55_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v54_l230)))


(def
 v57_l242
 (-> iris (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t58_l245
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v57_l242)))
