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


(def v6_l27 (-> iris (sk/view :sepal_length) sk/lay-histogram))


(deftest
 t7_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l27)))


(def
 v9_l40
 (-> iris (sk/view :sepal_length) (sk/lay-histogram {:color :species})))


(deftest
 t10_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l40)))


(def v12_l53 (-> iris (sk/view :petal_width) sk/lay-histogram))


(deftest
 t13_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l53)))


(def
 v15_l64
 (->
  tips
  (sk/view :total_bill)
  sk/lay-histogram
  (sk/options
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t16_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__85878#] (= "Distribution of Total Bill" p1__85878#))
       (:texts s)))))
   v15_l64)))


(def
 v18_l82
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay-histogram {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l82)))


(def v21_l95 (-> iris (sk/view [[:sepal_length]]) sk/lay-density))


(deftest
 t22_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v21_l95)))


(def
 v24_l108
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay-density {:color :species})))


(deftest
 t25_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v24_l108)))


(def
 v27_l121
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay-density {:bandwidth 0.3})))


(deftest
 t28_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v27_l121)))


(def
 v30_l134
 (-> iris (sk/view [[:species :sepal_width]]) sk/lay-boxplot))


(deftest
 t31_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l134)))


(def
 v33_l148
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay-boxplot {:color :smoker})))


(deftest
 t34_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v33_l148)))


(def
 v36_l160
 (let
  [sk
   (->
    tips
    (sk/view [[:day :total_bill]])
    (sk/lay-boxplot {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   box-layer
   (first
    (filter
     (fn* [p1__85879#] (= :boxplot (:mark p1__85879#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t37_l169 (is ((fn [v] (= 2 v)) v36_l160)))


(def
 v39_l176
 (->
  iris
  (sk/view [[:species :sepal_width]])
  sk/lay-boxplot
  (sk/options {:coord :flip})))


(deftest
 t40_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v39_l176)))


(def v42_l192 (-> tips (sk/view [[:day :total_bill]]) sk/lay-violin))


(deftest
 t43_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v42_l192)))


(def
 v45_l205
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay-violin {:color :smoker})))


(deftest
 t46_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v45_l205)))


(def
 v48_l216
 (let
  [sk
   (->
    tips
    (sk/view [[:day :total_bill]])
    (sk/lay-violin {:color :smoker})
    sk/sketch)
   panel
   (first (:panels sk))
   viol-layer
   (first
    (filter
     (fn* [p1__85880#] (= :violin (:mark p1__85880#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t49_l225 (is ((fn [v] (= 2 v)) v48_l216)))


(def
 v51_l230
 (->
  iris
  (sk/view [[:species :petal_length]])
  sk/lay-violin
  (sk/coord :flip)))


(deftest
 t52_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v51_l230)))


(def
 v54_l245
 (-> iris (sk/view [[:species :sepal_length]]) sk/lay-ridgeline))


(deftest
 t55_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v54_l245)))


(def
 v57_l258
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay-ridgeline {:color :species})))


(deftest
 t58_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v57_l258)))
