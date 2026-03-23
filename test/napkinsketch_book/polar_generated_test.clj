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
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/coord :polar)))


(deftest
 t7_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v6_l30)))


(def v9_l46 (-> iris (sk/view :species) sk/lay-bar (sk/coord :polar)))


(deftest
 t10_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v9_l46)))


(def
 v12_l60
 (->
  wind
  (sk/view [:direction :speed])
  sk/lay-value-bar
  (sk/coord :polar)))


(deftest
 t13_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v12_l60)))


(def
 v15_l73
 (->
  iris
  (sk/view :species)
  (sk/lay-stacked-bar {:color :species})
  (sk/coord :polar)))


(deftest
 t16_l78
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l73)))


(def
 v18_l87
 (-> iris (sk/view :sepal_length) sk/lay-histogram (sk/coord :polar)))


(deftest
 t19_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l87)))


(def
 v21_l101
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})))


(deftest
 t22_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v21_l101)))
