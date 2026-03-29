(ns
 napkinsketch-book.relationships-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v3_l21)))


(def
 v6_l33
 (->
  data/iris
  (sk/view :petal_length :petal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t7_l38
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v6_l33)))


(def
 v9_l46
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-lm {:se true})))


(deftest
 t10_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v9_l46)))


(def
 v12_l59
 (->
  data/tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm))


(deftest
 t13_l64
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v12_l59)))


(def
 v15_l72
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__79724#]
      (+
       (Math/sin (* p1__79724# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v16_l77 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t17_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v16_l77)))


(def v19_l89 (-> data/iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t20_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v19_l89)))


(def
 v22_l100
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (repeatedly 25 (fn* [] (rng/irandom r 100)))})))


(def v23_l106 (-> grid-data (sk/lay-tile :x :y {:fill :value})))


(deftest
 t24_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v23_l106)))


(def
 v26_l117
 (-> data/iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t27_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v26_l117)))


(def
 v29_l128
 (->
  data/iris
  (sk/lay-density2d :sepal_length :sepal_width)
  (sk/lay-point {:alpha 0.5})))


(deftest
 t30_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v29_l128)))


(def
 v32_l140
 (-> data/iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t33_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v32_l140)))


(def
 v35_l151
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:alpha 0.3})
  (sk/lay-contour {:levels 8})))


(deftest
 t36_l155
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v35_l151)))
