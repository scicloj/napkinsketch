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
  (pj/pose :petal-length :petal-width {:color :species})
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
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})))


(deftest
 t10_l51
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      base
      (->
       (rdatasets/datasets-iris)
       (pj/pose :sepal-length :sepal-width {:color :species})
       pj/lay-point)
      default-band
      (->
       base
       (pj/lay-smooth {:stat :linear-model, :confidence-band true})
       pj/plan
       :panels
       first
       :layers
       last
       :ribbons)
      explicit-95
      (->
       base
       (pj/lay-smooth
        {:stat :linear-model, :confidence-band true, :level 0.95})
       pj/plan
       :panels
       first
       :layers
       last
       :ribbons)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (= 3 (:polygons s))
      (= default-band explicit-95))))
   v9_l46)))


(def
 v12_l78
 (->
  (rdatasets/reshape2-tips)
  (pj/pose :total-bill :tip {:color :smoker})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t13_l83
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v12_l78)))


(def
 v15_l91
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__211674#]
      (+
       (Math/sin (* p1__211674# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (pj/lay-point :x :y)
  (pj/lay-smooth {:bandwidth 0.2})))


(deftest
 t16_l100
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v15_l91)))


(def
 v18_l108
 (->
  (rdatasets/datasets-iris)
  (pj/lay-tile :sepal-length :sepal-width)))


(deftest
 t19_l111
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v18_l108)))


(def
 v21_l119
 (def
  grid-data
  (let
   [r (rng/rng :jdk 99)]
   {:x (for [i (range 5) _j (range 5)] i),
    :y (for [_i (range 5) j (range 5)] j),
    :value (repeatedly 25 (fn* [] (rng/irandom r 100)))})))


(def v22_l125 (-> grid-data (pj/lay-tile :x :y {:fill :value})))


(deftest
 t23_l128
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v22_l125)))


(def
 v25_l136
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t26_l139
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:visible-tiles s)))))
   v25_l136)))


(def
 v28_l147
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)
  (pj/lay-point {:alpha 0.5})))


(deftest
 t29_l151
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:visible-tiles s)))))
   v28_l147)))


(def
 v31_l159
 (->
  (rdatasets/datasets-iris)
  (pj/lay-contour :sepal-length :sepal-width)))


(deftest
 t32_l162
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l159)))


(def
 v34_l170
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:alpha 0.3})
  (pj/lay-contour {:levels 8})))


(deftest
 t35_l174
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v34_l170)))
