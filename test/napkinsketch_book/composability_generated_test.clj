(ns
 napkinsketch-book.composability-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t4_l36
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l33)))


(def
 v6_l44
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) kind/pprint))


(deftest
 t7_l48
 (is
  ((fn
    [v]
    (and (:data v) (vector? (:entries v)) (vector? (:methods v))))
   v6_l44)))


(def
 v9_l59
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t10_l62
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v9_l59)))


(def
 v12_l72
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t13_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l72)))


(def v15_l93 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t16_l96
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v15_l93)))


(def v18_l100 (-> data/iris (sk/view :sepal_length)))


(deftest
 t19_l103
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v18_l100)))


(def
 v21_l113
 (sk/lay-histogram
  data/iris
  [:sepal_length :sepal_width :petal_length]))


(deftest
 t22_l115
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v21_l113)))


(def
 v24_l119
 (->
  data/iris
  (sk/view
   [[:sepal_length :sepal_width] [:petal_length :petal_width]])))


(deftest
 t25_l123
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v24_l119)))


(def v27_l128 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v28_l130
 (-> data/iris (sk/view (sk/cross cols cols) {:color :species})))


(deftest
 t29_l133
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v28_l130)))


(def
 v31_l144
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris by Species"})))


(deftest
 t32_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v31_l144)))


(def
 v34_l160
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.5 7.0 {:alpha 0.15}))))


(deftest
 t35_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v34_l160)))


(def
 v37_l174
 (def
  my-sk
  (->
   data/iris
   (sk/view :sepal_length :sepal_width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def v39_l183 (:shared my-sk))


(deftest t40_l185 (is ((fn [v] (= :species (:color v))) v39_l183)))


(def v42_l189 (:entries my-sk))


(deftest
 t43_l191
 (is
  ((fn [v] (and (= 1 (count v)) (= :sepal_length (:x (first v)))))
   v42_l189)))


(def v45_l197 (mapv :mark (:methods my-sk)))


(deftest t46_l199 (is ((fn [v] (= [:point :line] v)) v45_l197)))
