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


(def
 v6_l27
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t7_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l27)))


(def
 v9_l41
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l41)))


(def
 v12_l55
 (-> iris (sk/view :petal_width) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t13_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l55)))


(def
 v15_l67
 (->
  tips
  (sk/view :total_bill)
  (sk/lay (sk/histogram))
  (sk/plot
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__93773#] (= "Distribution of Total Bill" p1__93773#))
       (:texts s)))))
   v15_l67)))


(def
 v18_l85
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay
   (sk/histogram {:normalize :density, :alpha 0.5})
   (sk/density))
  sk/plot))


(deftest
 t19_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l85)))


(def
 v21_l99
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t22_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v21_l99)))


(def
 v24_l113
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t25_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v24_l113)))


(def
 v27_l127
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t28_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v27_l127)))


(def
 v30_l141
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t31_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l141)))


(def
 v33_l156
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t34_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v33_l156)))


(def
 v36_l169
 (let
  [sk
   (sk/sketch
    (->
     tips
     (sk/view [[:day :total_bill]])
     (sk/lay (sk/boxplot {:color :smoker}))))
   panel
   (first (:panels sk))
   box-layer
   (first
    (filter
     (fn* [p1__93774#] (= :boxplot (:mark p1__93774#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t37_l178 (is ((fn [v] (= 2 v)) v36_l169)))


(def
 v39_l185
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  (sk/plot {:coord :flip})))


(deftest
 t40_l190
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v39_l185)))


(def
 v42_l201
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t43_l206
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v42_l201)))


(def
 v45_l215
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t46_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v45_l215)))


(def
 v48_l227
 (let
  [sk
   (sk/sketch
    (->
     tips
     (sk/view [[:day :total_bill]])
     (sk/lay (sk/violin {:color :smoker}))))
   panel
   (first (:panels sk))
   viol-layer
   (first
    (filter
     (fn* [p1__93775#] (= :violin (:mark p1__93775#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t49_l236 (is ((fn [v] (= 2 v)) v48_l227)))


(def
 v51_l241
 (->
  iris
  (sk/view [[:species :petal_length]])
  (sk/lay (sk/violin))
  (sk/coord :flip)
  sk/plot))


(deftest
 t52_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v51_l241)))


(def
 v54_l257
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t55_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v54_l257)))


(def
 v57_l271
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline {:color :species}))
  sk/plot))


(deftest
 t58_l276
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v57_l271)))
