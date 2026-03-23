(ns
 napkinsketch-book.polar-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l18
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l21
 (def
  wind
  {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
   :speed [12 8 15 10 7 13 9 11]}))


(def
 v6_l30
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)))


(deftest
 t7_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v6_l30)))


(def v9_l45 (-> iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t10_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v9_l45)))


(def
 v12_l58
 (-> wind (sk/lay-value-bar :direction :speed) (sk/coord :polar)))


(deftest
 t13_l62
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v12_l58)))


(def
 v15_l70
 (->
  iris
  (sk/lay-stacked-bar :species {:color :species})
  (sk/coord :polar)))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l70)))


(def
 v18_l83
 (-> iris (sk/lay-histogram :sepal_length) (sk/coord :polar)))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l83)))


(def
 v21_l96
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})))


(deftest
 t22_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v21_l96)))
