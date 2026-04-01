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
 v10_l65
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/lay-point "x" "y")))


(deftest
 t11_l68
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v10_l65)))


(def
 v13_l74
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t14_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v13_l74)))


(def v16_l88 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t17_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v16_l88)))


(def v19_l98 (-> iris (sk/lay-bar :species)))


(deftest
 t20_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v19_l98)))


(def v22_l107 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t23_l111
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v22_l107)))


(def
 v25_l116
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (sk/lay-line :x :y)))


(deftest
 t26_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v25_l116)))


(def v28_l126 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t29_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v28_l126)))


(def v31_l140 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t32_l143
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v31_l140)))


(def v34_l147 (-> iris (sk/view :species)))


(deftest
 t35_l150
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v34_l147)))


(def v37_l154 (-> iris (sk/view :sepal_length)))


(deftest
 t38_l157
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v37_l154)))


(def
 v40_l167
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t41_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v40_l167)))


(def
 v43_l180
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
 t44_l187
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v43_l180)))


(def
 v46_l196
 (sk/arrange
  [(sk/lay-point iris :sepal_length :sepal_width {:color :species})
   (sk/lay-histogram iris :sepal_length {:color :species})]
  {:cols 2}))


(deftest t47_l200 (is ((fn [v] (vector? v)) v46_l196)))
