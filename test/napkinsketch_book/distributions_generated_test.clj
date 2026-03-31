(ns
 napkinsketch-book.distributions-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l19 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t4_l22
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v3_l19)))


(def
 v6_l31
 (-> data/iris (sk/lay-histogram :sepal_length {:color :species})))


(deftest
 t7_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l31)))


(def v9_l43 (-> data/iris (sk/lay-histogram :petal_width)))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l43)))


(def
 v12_l53
 (->
  data/tips
  (sk/lay-histogram :total_bill)
  (sk/options
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__98827#] (= "Distribution of Total Bill" p1__98827#))
       (:texts s)))))
   v12_l53)))


(def
 v15_l70
 (->
  data/iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l70)))


(def v18_l82 (-> data/iris (sk/lay-density :sepal_length)))


(deftest
 t19_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v18_l82)))


(def
 v21_l94
 (-> data/iris (sk/lay-density :sepal_length {:color :species})))


(deftest
 t22_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l94)))


(def
 v24_l106
 (-> data/iris (sk/lay-density :sepal_length {:bandwidth 0.3})))


(deftest
 t25_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l106)))


(def v27_l118 (-> data/iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t28_l121
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v27_l118)))


(def
 v30_l131
 (-> data/tips (sk/lay-boxplot :day :total_bill {:color :smoker})))


(deftest
 t31_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v30_l131)))


(def
 v33_l142
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
     (fn* [p1__98828#] (= :boxplot (:mark p1__98828#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t34_l150 (is ((fn [v] (= 2 v)) v33_l142)))


(def
 v36_l157
 (-> data/iris (sk/lay-boxplot :species :sepal_width) (sk/coord :flip)))


(deftest
 t37_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v36_l157)))


(def v39_l172 (-> data/tips (sk/lay-violin :day :total_bill)))


(deftest
 t40_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v39_l172)))


(def
 v42_l184
 (-> data/tips (sk/lay-violin :day :total_bill {:color :smoker})))


(deftest
 t43_l187
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v42_l184)))


(def
 v45_l194
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
     (fn* [p1__98829#] (= :violin (:mark p1__98829#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t46_l202 (is ((fn [v] (= 2 v)) v45_l194)))


(def
 v48_l207
 (-> data/iris (sk/lay-violin :species :petal_length) (sk/coord :flip)))


(deftest
 t49_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v48_l207)))


(def v51_l221 (-> data/iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t52_l224
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v51_l221)))


(def
 v54_l233
 (->
  data/iris
  (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t55_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v54_l233)))


(def
 v57_l247
 (->
  (sk/distribution data/iris :sepal_length :sepal_width :petal_length)
  sk/lay-histogram))


(deftest
 t58_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v57_l247)))


(def
 v60_l257
 (->
  (sk/distribution data/iris :sepal_length :sepal_width :petal_length)
  (sk/lay-density {:color :species})))


(deftest
 t61_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v60_l257)))
