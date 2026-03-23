(ns
 napkinsketch-book.ranking-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l14
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l17
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v4_l20
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v5_l23
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v7_l30 (-> iris (sk/view :species) sk/lay-bar))


(deftest
 t8_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l30)))


(def v10_l43 (-> tips (sk/view :day) (sk/lay-bar {:color :smoker})))


(deftest
 t11_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l43)))


(def
 v13_l56
 (-> tips (sk/view :day) (sk/lay-stacked-bar {:color :smoker})))


(deftest
 t14_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l56)))


(def
 v16_l69
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})))


(deftest
 t17_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l69)))


(def v19_l81 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip)))


(deftest
 t20_l86
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l81)))


(def
 v22_l95
 (-> tips (sk/view :day) (sk/lay-bar {:color :time}) (sk/coord :flip)))


(deftest
 t23_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v22_l95)))


(def v25_l109 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t26_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l109)))


(def
 v28_l120
 (->
  sales
  (sk/view [[:product :revenue]])
  sk/lay-value-bar
  (sk/coord :flip)))


(deftest
 t29_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v28_l120)))


(def
 v31_l133
 (-> sales (sk/view [[:product :revenue]]) sk/lay-lollipop))


(deftest
 t32_l137
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l133)))


(def
 v34_l145
 (->
  sales
  (sk/view [[:product :revenue]])
  sk/lay-lollipop
  (sk/coord :flip)))


(deftest
 t35_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v34_l145)))
