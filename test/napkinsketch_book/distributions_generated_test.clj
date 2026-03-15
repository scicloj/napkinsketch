(ns
 napkinsketch-book.distributions-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l14
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l17
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v6_l24
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t7_l29
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l24)))


(def
 v9_l38
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t10_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l38)))


(def
 v12_l52
 (-> iris (sk/view :petal_width) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t13_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l52)))


(def
 v15_l64
 (->
  tips
  (sk/view :total_bill)
  (sk/lay (sk/histogram))
  (sk/plot
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
       (fn* [p1__79984#] (= "Distribution of Total Bill" p1__79984#))
       (:texts s)))))
   v15_l64)))


(def
 v18_l81
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t19_l86
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v18_l81)))


(def
 v21_l95
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t22_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l95)))


(def
 v24_l109
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t25_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l109)))


(def
 v27_l123
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t28_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v27_l123)))


(def
 v30_l138
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t31_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v30_l138)))


(def
 v33_l151
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
     (fn* [p1__79985#] (= :boxplot (:mark p1__79985#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t34_l160 (is ((fn [v] (= 2 v)) v33_l151)))


(def
 v36_l167
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  (sk/plot {:coord :flip})))


(deftest
 t37_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v36_l167)))


(def
 v39_l183
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t40_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v39_l183)))


(def
 v42_l197
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t43_l202
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v42_l197)))


(def
 v45_l209
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
     (fn* [p1__79986#] (= :violin (:mark p1__79986#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t46_l218 (is ((fn [v] (= 2 v)) v45_l209)))


(def
 v48_l223
 (->
  iris
  (sk/view [[:species :petal_length]])
  (sk/lay (sk/violin))
  (sk/coord :flip)
  sk/plot))


(deftest
 t49_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v48_l223)))


(def
 v51_l239
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t52_l244
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v51_l239)))


(def
 v54_l253
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline {:color :species}))
  sk/plot))


(deftest
 t55_l258
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v54_l253)))
