(ns
 napkinsketch-book.polar-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l18
 (def
  wind
  (tc/dataset
   {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
    :speed [12 8 15 10 7 13 9 11]})))


(def
 v6_l27
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/coord :polar)
  sk/plot))


(deftest
 t7_l33
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v6_l27)))


(def
 v9_l44
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t10_l50
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v9_l44)))


(def
 v12_l59
 (->
  wind
  (sk/view [:direction :speed])
  (sk/lay (sk/value-bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t13_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v12_l59)))


(def
 v15_l73
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/stacked-bar {:color :species}))
  (sk/coord :polar)
  sk/plot))


(deftest
 t16_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l73)))


(def
 v18_l88
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram))
  (sk/coord :polar)
  sk/plot))


(deftest
 t19_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v18_l88)))


(def
 v21_l103
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})
  sk/plot))


(deftest
 t22_l110
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v21_l103)))
