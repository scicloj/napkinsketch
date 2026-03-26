(ns
 napkinsketch-book.polar-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l18
 (def
  wind
  {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
   :speed [12 8 15 10 7 13 9 11]}))


(def
 v5_l27
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)))


(deftest
 t6_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v5_l27)))


(def v8_l42 (-> data/iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t9_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v8_l42)))


(def
 v11_l55
 (-> wind (sk/lay-value-bar :direction :speed) (sk/coord :polar)))


(deftest
 t12_l59
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v11_l55)))


(def
 v14_l67
 (->
  data/iris
  (sk/lay-stacked-bar :species {:color :species})
  (sk/coord :polar)))


(deftest
 t15_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l67)))


(def
 v17_l80
 (-> data/iris (sk/lay-histogram :sepal_length) (sk/coord :polar)))


(deftest
 t18_l84
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v17_l80)))


(def
 v20_l93
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})))


(deftest
 t21_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v20_l93)))
