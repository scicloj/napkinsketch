(ns
 napkinsketch-book.relationships-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v2_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l20
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l27
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm))


(deftest
 t6_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v5_l27)))


(def
 v8_l40
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t9_l45
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l40)))


(def
 v11_l53
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:se true, :color :species})))


(deftest
 t12_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v11_l53)))


(def
 v14_l66
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})))


(deftest
 t15_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v14_l66)))


(def
 v17_l79
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__84541#]
      (+
       (Math/sin (* p1__84541# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v18_l84
 (-> noisy-wave (sk/view [[:x :y]]) sk/lay-point sk/lay-loess))


(deftest
 t19_l89
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v18_l84)))


(def v21_l97 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t22_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v21_l97)))


(def
 v24_l108
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))})))


(def v25_l114 (-> grid-data (sk/lay-tile :x :y {:fill :value})))


(deftest
 t26_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l114)))


(def v28_l125 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t29_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v28_l125)))


(def
 v31_l136
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-density2d
  (sk/lay-point {:alpha 0.5})))


(deftest
 t32_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v31_l136)))


(def v34_l149 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t35_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v34_l149)))


(def
 v37_l160
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:alpha 0.3})
  (sk/lay-contour {:levels 8})))


(deftest
 t38_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v37_l160)))
