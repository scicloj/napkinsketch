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


(def v7_l30 (-> iris (sk/lay-bar :species)))


(deftest
 t8_l33
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l30)))


(def v10_l42 (-> tips (sk/lay-bar :day {:color :smoker})))


(deftest
 t11_l45
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l42)))


(def v13_l54 (-> tips (sk/lay-stacked-bar :day {:color :smoker})))


(deftest
 t14_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l54)))


(def
 v16_l66
 (-> penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t17_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l66)))


(def v19_l77 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t20_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l77)))


(def
 v22_l90
 (-> tips (sk/lay-bar :day {:color :time}) (sk/coord :flip)))


(deftest
 t23_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v22_l90)))


(def v25_l103 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t26_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l103)))


(def
 v28_l114
 (-> sales (sk/lay-value-bar :product :revenue) (sk/coord :flip)))


(deftest
 t29_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v28_l114)))


(def v31_l126 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t32_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l126)))


(def
 v34_l137
 (-> sales (sk/lay-lollipop :product :revenue) (sk/coord :flip)))


(deftest
 t35_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v34_l137)))
