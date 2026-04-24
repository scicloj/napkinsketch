(ns
 plotje-book.relationships-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v3_l21)))


(def
 v6_l33
 (->
  (rdatasets/datasets-iris)
  (pj/frame :petal-length :petal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t7_l38
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v6_l33)))


(def
 v9_l46
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})))


(deftest
 t10_l51
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v9_l46)))


(def
 v12_l59
 (->
  (rdatasets/reshape2-tips)
  (pj/frame :total-bill :tip {:color :smoker})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t13_l64
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v12_l59)))


(def
 v15_l72
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__82088#]
      (+
       (Math/sin (* p1__82088# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (pj/lay-point :x :y)
  (pj/lay-smooth {:bandwidth 0.2})))


(deftest
 t16_l81
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v15_l72)))


(def
 v18_l89
 (->
  (rdatasets/datasets-iris)
  (pj/lay-tile :sepal-length :sepal-width)))


(deftest
 t19_l92
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v18_l89)))


(def
 v21_l100
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (repeatedly 25 (fn* [] (rng/irandom r 100)))})))


(def v22_l106 (-> grid-data (pj/lay-tile :x :y {:fill :value})))


(deftest
 t23_l109
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v22_l106)))


(def
 v25_l117
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t26_l120
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v25_l117)))


(def
 v28_l128
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)
  (pj/lay-point {:alpha 0.5})))


(deftest
 t29_l132
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:visible-tiles s)))))
   v28_l128)))


(def
 v31_l140
 (->
  (rdatasets/datasets-iris)
  (pj/lay-contour :sepal-length :sepal-width)))


(deftest
 t32_l143
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l140)))


(def
 v34_l151
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:alpha 0.3})
  (pj/lay-contour {:levels 8})))


(deftest
 t35_l155
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v34_l151)))
