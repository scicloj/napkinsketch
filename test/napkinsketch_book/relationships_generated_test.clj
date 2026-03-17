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
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


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
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t9_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v8_l40)))


(def
 v11_l54
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species})
   (sk/lm {:se true, :color :species}))
  sk/plot))


(deftest
 t12_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v11_l54)))


(def
 v14_l68
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  sk/plot))


(deftest
 t15_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v14_l68)))


(def
 v17_l82
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__84434#]
       (+
        (Math/sin (* p1__84434# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v18_l87
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t19_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v18_l87)))


(def
 v21_l100
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t22_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v21_l100)))


(def
 v24_l113
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   (tc/dataset
    {:x (for [i (range 5) _j (range 5)] i),
     :y (for [_i (range 5) j (range 5)] j),
     :value (vec (repeatedly 25 (fn* [] (rng/irandom r 100))))}))))


(def
 v25_l119
 (sk/plot [(sk/tile {:data grid-data, :x :x, :y :y, :fill :value})]))


(deftest
 t26_l121
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v25_l119)))


(def
 v28_l129
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t29_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v28_l129)))


(def
 v31_l142
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  (sk/lay (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t32_l148
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v31_l142)))


(def
 v34_l156
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t35_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v34_l156)))


(def
 v37_l169
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:alpha 0.3}) (sk/contour {:levels 8}))
  sk/plot))


(deftest
 t38_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v37_l169)))
