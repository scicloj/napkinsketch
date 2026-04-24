(ns
 plotje-book.ranking-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v2_l14
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v4_l21 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t5_l24
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v4_l21)))


(def
 v7_l33
 (-> (rdatasets/reshape2-tips) (pj/lay-bar :day {:color :smoker})))


(deftest
 t8_l36
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l33)))


(def
 v10_l45
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:position :stack, :color :smoker})))


(deftest
 t11_l48
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l45)))


(def
 v13_l57
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :fill, :color :species})))


(deftest
 t14_l60
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l57)))


(def
 v16_l68
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t17_l72
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l68)))


(def
 v19_l87
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :time})
  (pj/coord :flip)))


(deftest
 t20_l91
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l87)))


(def v22_l100 (-> sales (pj/lay-value-bar :product :revenue)))


(deftest
 t23_l103
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v22_l100)))


(def
 v25_l111
 (-> sales (pj/lay-value-bar :product :revenue) (pj/coord :flip)))


(deftest
 t26_l115
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l111)))


(def v28_l123 (-> sales (pj/lay-lollipop :product :revenue)))


(deftest
 t29_l126
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v28_l123)))


(def
 v31_l134
 (-> sales (pj/lay-lollipop :product :revenue) (pj/coord :flip)))


(deftest
 t32_l138
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l134)))
