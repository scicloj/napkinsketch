(ns
 napkinsketch-book.relationships-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v2_l13
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l16
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l23
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t6_l28
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v5_l23)))


(def
 v8_l36
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t9_l42
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l36)))


(def
 v11_l50
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  sk/plot))


(deftest
 t12_l56
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v11_l50)))


(def
 v14_l64
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__103561#]
       (+
        (Math/sin (* p1__103561# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v15_l69
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v15_l69)))


(def
 v18_l82
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v18_l82)))


(def
 v21_l95
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   (tc/dataset
    {:x (for [i (range 5) _j (range 5)] i),
     :y (for [_i (range 5) j (range 5)] j),
     :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))}))))


(def
 v22_l101
 (sk/plot [(sk/tile {:data grid-data, :x :x, :y :y, :fill :value})]))


(deftest
 t23_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v22_l101)))


(def
 v25_l111
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t26_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l111)))


(def
 v28_l124
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  (sk/lay (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t29_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v28_l124)))


(def
 v31_l138
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t32_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l138)))


(def
 v34_l151
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:alpha 0.3}) (sk/contour {:levels 8}))
  sk/plot))


(deftest
 t35_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v34_l151)))
