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
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t6_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v5_l27)))


(def
 v8_l39
 (->
  iris
  (sk/lay-point :petal_length :petal_width {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t9_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l39)))


(def
 v11_l51
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay-lm {:se true, :color :species})))


(deftest
 t12_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v11_l51)))


(def
 v14_l63
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :smoker})
  (sk/lay-lm {:color :smoker})))


(deftest
 t15_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v14_l63)))


(def
 v17_l75
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__90959#]
      (+
       (Math/sin (* p1__90959# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v18_l80 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t19_l84
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v18_l80)))


(def v21_l92 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t22_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v21_l92)))


(def
 v24_l103
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))})))


(def v25_l109 (-> grid-data (sk/lay-tile :x :y {:fill :value})))


(deftest
 t26_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l109)))


(def v28_l120 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t29_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v28_l120)))


(def
 v31_l131
 (->
  iris
  (sk/lay-density2d :sepal_length :sepal_width)
  (sk/lay-point {:alpha 0.5})))


(deftest
 t32_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v31_l131)))


(def v34_l143 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t35_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v34_l143)))


(def
 v37_l154
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:alpha 0.3})
  (sk/lay-contour {:levels 8})))


(deftest
 t38_l158
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v37_l154)))
