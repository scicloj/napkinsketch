(ns
 napkinsketch-book.ranking-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l14
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v4_l21 (-> data/iris (sk/lay-bar :species)))


(deftest
 t5_l24
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v4_l21)))


(def v7_l33 (-> data/tips (sk/lay-bar :day {:color :smoker})))


(deftest
 t8_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l33)))


(def
 v10_l45
 (-> data/tips (sk/lay-stacked-bar :day {:color :smoker})))


(deftest
 t11_l48
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l45)))


(def
 v13_l57
 (->
  data/penguins
  (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t14_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l57)))


(def
 v16_l68
 (-> data/iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t17_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l68)))


(def
 v19_l81
 (->
  data/tips
  (sk/lay-bar :day {:color :time})
  (sk/coord :flip)))


(deftest
 t20_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l81)))


(def v22_l94 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t23_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v22_l94)))


(def
 v25_l105
 (->
  sales
  (sk/lay-value-bar :product :revenue)
  (sk/coord :flip)))


(deftest
 t26_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l105)))


(def v28_l117 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t29_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v28_l117)))


(def
 v31_l128
 (->
  sales
  (sk/lay-lollipop :product :revenue)
  (sk/coord :flip)))


(deftest
 t32_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l128)))
