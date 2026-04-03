(ns
 napkinsketch-book.composability-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (def scatter (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v5_l42 (sk/sketch? scatter))


(deftest t6_l44 (is ((fn [v] (true? v)) v5_l42)))


(def v7_l46 (kind/pprint (sk/views-of scatter)))


(deftest
 t8_l48
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v7_l46)))


(def v10_l54 scatter)


(deftest
 t11_l56
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v10_l54)))


(def
 v13_l63
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t14_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v13_l63)))


(def
 v16_l76
 (def
  scatter-with-regression
  (->
   data/iris
   (sk/view :sepal_length :sepal_width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def v18_l85 (kind/pprint (sk/views-of scatter-with-regression)))


(deftest t19_l87 (is ((fn [v] (= 2 (count v))) v18_l85)))


(def v21_l92 scatter-with-regression)


(deftest
 t22_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v21_l92)))


(def v24_l110 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t25_l113
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v24_l110)))


(def v27_l117 (-> data/iris (sk/view :sepal_length)))


(deftest
 t28_l120
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v27_l117)))


(def
 v30_l127
 (def
  two-panels
  (->
   data/iris
   (sk/view
    [[:sepal_length :sepal_width] [:petal_length :petal_width]]))))


(def v32_l134 (kind/pprint (sk/views-of two-panels)))


(deftest t33_l136 (is ((fn [v] (= 2 (count v))) v32_l134)))


(def v34_l138 two-panels)


(deftest
 t35_l140
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v34_l138)))


(def v37_l149 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v38_l151
 (-> data/iris (sk/view (sk/cross cols cols) {:color :species})))


(deftest
 t39_l154
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v38_l151)))


(def
 v41_l167
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris by Species"})))


(deftest
 t42_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v41_l167)))
