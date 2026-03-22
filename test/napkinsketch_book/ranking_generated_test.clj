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


(def v7_l30 (-> iris (sk/view :species) sk/lay-bar sk/plot))


(deftest
 t8_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l30)))


(def
 v10_l44
 (-> tips (sk/view :day) (sk/lay-bar {:color :smoker}) sk/plot))


(deftest
 t11_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l44)))


(def
 v13_l58
 (-> tips (sk/view :day) (sk/lay-stacked-bar {:color :smoker}) sk/plot))


(deftest
 t14_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l58)))


(def
 v16_l72
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})
  sk/plot))


(deftest
 t17_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l72)))


(def
 v19_l85
 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip) sk/plot))


(deftest
 t20_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l85)))


(def
 v22_l100
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :time})
  (sk/coord :flip)
  sk/plot))


(deftest
 t23_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v22_l100)))


(def v25_l115 (-> sales (sk/lay-value-bar :product :revenue) sk/plot))


(deftest
 t26_l119
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l115)))


(def
 v28_l127
 (->
  sales
  (sk/view [[:product :revenue]])
  sk/lay-value-bar
  (sk/coord :flip)
  sk/plot))


(deftest
 t29_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v28_l127)))


(def
 v31_l141
 (-> sales (sk/view [[:product :revenue]]) sk/lay-lollipop sk/plot))


(deftest
 t32_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l141)))


(def
 v34_l154
 (->
  sales
  (sk/view [[:product :revenue]])
  sk/lay-lollipop
  (sk/coord :flip)
  sk/plot))


(deftest
 t35_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v34_l154)))
