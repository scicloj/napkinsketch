(ns
 napkinsketch-book.relationships-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/lay-lm))


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
  (rdatasets/datasets-iris)
  (sk/view :petal-length :petal-width {:color :species})
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
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
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
  (rdatasets/reshape2-tips)
  (sk/view :total-bill :tip {:color :smoker})
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
      [p1__1961663#]
      (+
       (Math/sin (* p1__1961663# 0.2))
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


(def
 v19_l89
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t20_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
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
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v23_l106)))


(def
 v26_l117
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)))


(deftest
 t27_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v26_l117)))


(def
 v29_l128
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)
  (sk/lay-point {:alpha 0.5})))


(deftest
 t30_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:visible-tiles s)))))
   v29_l128)))


(def
 v32_l140
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


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
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:alpha 0.3})
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
