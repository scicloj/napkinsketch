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


(def v15_l66 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t16_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l66)))


(def
 v18_l80
 (-> tips (sk/view :day) (sk/lay (sk/bar {:color :smoker})) sk/plot))


(deftest
 t19_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l80)))


(def
 v21_l94
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :smoker}))
  sk/plot))


(deftest
 t22_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v21_l94)))


(def
 v24_l108
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t25_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v24_l108)))


(def
 v27_l123
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :time}))
  (sk/coord :flip)
  sk/plot))


(deftest
 t28_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v27_l123)))


(def
 v30_l136
 (->
  tips
  (sk/view :total_bill)
  (sk/lay (sk/histogram))
  (sk/plot
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t31_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__85823#] (= "Distribution of Total Bill" p1__85823#))
       (:texts s)))))
   v30_l136)))


(def
 v33_l153
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t34_l158
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v33_l153)))


(def
 v36_l167
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t37_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v36_l167)))


(def
 v39_l181
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t40_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v39_l181)))


(def
 v42_l195
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t43_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v42_l195)))


(def
 v45_l210
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t46_l215
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v45_l210)))


(def
 v48_l225
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  (sk/plot {:coord :flip})))


(deftest
 t49_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v48_l225)))
