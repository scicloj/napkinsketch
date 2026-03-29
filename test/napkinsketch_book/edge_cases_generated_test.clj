(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [tech.v3.datatype.datetime :as dt-dt]
  [tech.v3.datatype :as dtype]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l33 (-> with-missing (sk/lay-point :x :y)))


(deftest
 t5_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l33)))


(def v7_l44 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t8_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l44)))


(def v10_l56 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t11_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l56)))


(def
 v13_l68
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t14_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l68)))


(def v16_l80 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t17_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l80)))


(def v19_l91 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t20_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l91)))


(def
 v22_l102
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t23_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l102)))


(def
 v25_l111
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t26_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l111)))


(def
 v28_l120
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t29_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l120)))


(def
 v31_l131
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v32_l137 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t33_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l137)))


(def
 v35_l148
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__130635#] (keyword (str "cat-" p1__130635#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t36_l153
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l148)))


(def
 v38_l161
 (->
  data/iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))
