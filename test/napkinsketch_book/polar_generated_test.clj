(ns
 napkinsketch-book.polar-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l17
 (def
  wind
  {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
   :speed [12 8 15 10 7 13 9 11]}))


(def
 v4_l26
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :polar)))


(deftest
 t5_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v4_l26)))


(def
 v7_l41
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t8_l45
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v7_l41)))


(def
 v10_l54
 (-> wind (sk/lay-value-bar :direction :speed) (sk/coord :polar)))


(deftest
 t11_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v10_l54)))


(def
 v13_l66
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar :island {:color :species})
  (sk/coord :polar)))


(deftest
 t14_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l66)))


(def
 v16_l79
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram :sepal-length)
  (sk/coord :polar)))


(deftest
 t17_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l79)))


(def
 v19_l92
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :polar)
  (sk/options {:title "Iris in Polar Space"})))


(deftest
 t20_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v19_l92)))
