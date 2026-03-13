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
  (sk/lay (sk/line {:color :species}))
  (sk/coord :polar)
  sk/plot))


(deftest
 t22_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:lines s)))))
   v21_l103)))


(def
 v24_l118
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  (sk/coord :polar)
  sk/plot))


(deftest
 t25_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v24_l118)))


(def
 v27_l134
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/area {:color :species}))
  (sk/coord :polar)
  sk/plot))


(deftest
 t28_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v27_l134)))


(def
 v30_l149
 (->
  iris
  (sk/view [:species :sepal_length])
  (sk/lay (sk/boxplot))
  (sk/coord :polar)
  sk/plot))


(deftest
 t31_l155
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l149)))


(def
 v33_l165
 (->
  iris
  (sk/view [:species :sepal_length])
  (sk/lay (sk/violin))
  (sk/coord :polar)
  sk/plot))


(deftest
 t34_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v33_l165)))


(def
 v36_l179
 (def
  summary-ds
  (tc/dataset
   {:species ["setosa" "versicolor" "virginica"],
    :mean [5.0 5.9 6.6],
    :lo [4.8 5.6 6.3],
    :hi [5.2 6.2 6.9]})))


(def
 v37_l184
 (->
  summary-ds
  (sk/view [:species :mean])
  (sk/lay (sk/errorbar {:ymin :lo, :ymax :hi}))
  (sk/coord :polar)
  sk/plot))


(deftest
 t38_l190
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v37_l184)))


(def
 v40_l198
 (->
  wind
  (sk/view [:direction :speed])
  (sk/lay (sk/lollipop))
  (sk/coord :polar)
  sk/plot))


(deftest
 t41_l204
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:points s)) (= 8 (:lines s)))))
   v40_l198)))


(def
 v43_l214
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/coord :polar)
  (sk/labs {:title "Iris in Polar Space"})
  sk/plot))


(deftest
 t44_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v43_l214)))
