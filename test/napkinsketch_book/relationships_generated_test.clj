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
  (sk/view :petal_length :petal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t9_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l39)))


(def
 v11_l52
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-lm {:se true})))


(deftest
 t12_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v11_l52)))


(def
 v14_l65
 (->
  tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm))


(deftest
 t15_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v14_l65)))


(def
 v17_l78
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__74220#]
      (+
       (Math/sin (* p1__74220# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v18_l83 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v18_l83)))


(def v21_l95 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t22_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v21_l95)))


(def
 v24_l106
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))})))


(def v25_l112 (-> grid-data (sk/lay-tile :x :y {:fill :value})))


(deftest
 t26_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l112)))


(def v28_l123 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t29_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v28_l123)))


(def
 v31_l134
 (->
  iris
  (sk/lay-density2d :sepal_length :sepal_width)
  (sk/lay-point {:alpha 0.5})))


(deftest
 t32_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v31_l134)))


(def v34_l146 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t35_l149
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v34_l146)))


(def
 v37_l157
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:alpha 0.3})
  (sk/lay-contour {:levels 8})))


(deftest
 t38_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v37_l157)))
