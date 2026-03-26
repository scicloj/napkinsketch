(ns
 napkinsketch-book.distributions-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l21 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t4_l24
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v3_l21)))


(def
 v6_l33
 (-> data/iris (sk/lay-histogram :sepal_length {:color :species})))


(deftest
 t7_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l33)))


(def v9_l45 (-> data/iris (sk/lay-histogram :petal_width)))


(deftest
 t10_l48
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l45)))


(def
 v12_l55
 (->
  data/tips
  (sk/lay-histogram :total_bill)
  (sk/options
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t13_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__101059#] (= "Distribution of Total Bill" p1__101059#))
       (:texts s)))))
   v12_l55)))


(def
 v15_l72
 (->
  data/iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t16_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l72)))


(def v18_l84 (-> data/iris (sk/lay-density :sepal_length)))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v18_l84)))


(def
 v21_l96
 (-> data/iris (sk/lay-density :sepal_length {:color :species})))


(deftest
 t22_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l96)))


(def
 v24_l108
 (-> data/iris (sk/lay-density :sepal_length {:bandwidth 0.3})))


(deftest
 t25_l111
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l108)))


(def v27_l120 (-> data/iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t28_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v27_l120)))


(def
 v30_l133
 (-> data/tips (sk/lay-boxplot :day :total_bill {:color :smoker})))


(deftest
 t31_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v30_l133)))


(def
 v33_l144
 (let
  [sk
   (->
    data/tips
    (sk/lay-boxplot :day :total_bill {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   box-layer
   (first
    (filter
     (fn* [p1__101060#] (= :boxplot (:mark p1__101060#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t34_l152 (is ((fn [v] (= 2 v)) v33_l144)))


(def
 v36_l159
 (->
  data/iris
  (sk/lay-boxplot :species :sepal_width)
  (sk/options {:coord :flip})))


(deftest
 t37_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v36_l159)))


(def v39_l174 (-> data/tips (sk/lay-violin :day :total_bill)))


(deftest
 t40_l177
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v39_l174)))


(def
 v42_l186
 (-> data/tips (sk/lay-violin :day :total_bill {:color :smoker})))


(deftest
 t43_l189
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v42_l186)))


(def
 v45_l196
 (let
  [sk
   (->
    data/tips
    (sk/lay-violin :day :total_bill {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   viol-layer
   (first
    (filter
     (fn* [p1__101061#] (= :violin (:mark p1__101061#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t46_l204 (is ((fn [v] (= 2 v)) v45_l196)))


(def
 v48_l209
 (-> data/iris (sk/lay-violin :species :petal_length) (sk/coord :flip)))


(deftest
 t49_l213
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v48_l209)))


(def v51_l223 (-> data/iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t52_l226
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v51_l223)))


(def
 v54_l235
 (->
  data/iris
  (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t55_l238
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v54_l235)))
