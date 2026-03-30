(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l36 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t5_l39
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l36)))


(def v7_l55 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t8_l58
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l55)))


(def
 v10_l66
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t11_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v10_l66)))


(def v13_l78 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t14_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v13_l78)))


(def v16_l90 (-> iris (sk/lay-bar :species)))


(deftest
 t17_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v16_l90)))


(def v19_l101 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t20_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v19_l101)))


(def
 v22_l112
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (sk/lay-line :x :y)))


(deftest
 t23_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v22_l112)))


(def v25_l125 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t26_l128
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v25_l125)))


(def v28_l132 (-> iris (sk/view :species)))


(deftest
 t29_l135
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v28_l132)))


(def v31_l139 (-> iris (sk/view :sepal_length)))


(deftest
 t32_l142
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v31_l139)))


(def
 v34_l151
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t35_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v34_l151)))


(def
 v37_l164
 (->
  iris
  (sk/lay-point :petal_length :petal_width {:color :species})
  (sk/options
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t38_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v37_l164)))
