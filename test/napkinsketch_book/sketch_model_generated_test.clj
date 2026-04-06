(ns
 napkinsketch-book.sketch-model-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t4_l33
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l30)))


(def
 v6_l43
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) kind/pprint))


(deftest
 t7_l47
 (is
  ((fn
    [v]
    (and (:data v) (vector? (:entries v)) (vector? (:methods v))))
   v6_l43)))


(def
 v9_l69
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t10_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l69)))


(def v12_l92 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t13_l95
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l92)))


(def v15_l99 (-> data/iris (sk/view :sepal_length)))


(deftest
 t16_l102
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v15_l99)))


(def
 v18_l115
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris by Species"})))


(deftest
 t19_l122
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v18_l115)))
