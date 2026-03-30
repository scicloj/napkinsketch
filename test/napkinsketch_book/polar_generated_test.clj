(ns
 napkinsketch-book.polar-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l16
 (def
  wind
  {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
   :speed [12 8 15 10 7 13 9 11]}))


(def
 v4_l25
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)))


(deftest
 t5_l29
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v4_l25)))


(def v7_l40 (-> data/iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t8_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v7_l40)))


(def
 v10_l53
 (-> wind (sk/lay-value-bar :direction :speed) (sk/coord :polar)))


(deftest
 t11_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v10_l53)))


(def
 v13_l65
 (->
  data/penguins
  (sk/lay-stacked-bar :island {:color :species})
  (sk/coord :polar)))


(deftest
 t14_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l65)))


(def
 v16_l78
 (-> data/iris (sk/lay-histogram :sepal_length) (sk/coord :polar)))


(deftest
 t17_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l78)))


(def
 v19_l91
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v19_l91)))
