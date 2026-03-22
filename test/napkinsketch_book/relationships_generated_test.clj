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
  sk/lay-lm
  sk/plot))


(deftest
 t6_l33
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v5_l27)))


(def
 v8_l41
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t9_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l41)))


(def
 v11_l55
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:se true, :color :species})
  sk/plot))


(deftest
 t12_l61
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v11_l55)))


(def
 v14_l69
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})
  sk/plot))


(deftest
 t15_l75
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v14_l69)))


(def
 v17_l83
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__74815#]
      (+
       (Math/sin (* p1__74815# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v18_l88
 (-> noisy-wave (sk/view [[:x :y]]) sk/lay-point sk/lay-loess sk/plot))


(deftest
 t19_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v18_l88)))


(def
 v21_l102
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-tile sk/plot))


(deftest
 t22_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v21_l102)))


(def
 v24_l115
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))})))


(def v25_l121 (-> grid-data (sk/lay-tile :x :y {:fill :value}) sk/plot))


(deftest
 t26_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l121)))


(def
 v28_l133
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-density2d
  sk/plot))


(deftest
 t29_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v28_l133)))


(def
 v31_l146
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-density2d
  (sk/lay-point {:alpha 0.5})
  sk/plot))


(deftest
 t32_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v31_l146)))


(def
 v34_l160
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-contour
  sk/plot))


(deftest
 t35_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v34_l160)))


(def
 v37_l173
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:alpha 0.3})
  (sk/lay-contour {:levels 8})
  sk/plot))


(deftest
 t38_l179
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v37_l173)))
