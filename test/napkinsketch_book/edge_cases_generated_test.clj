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
 v3_l33
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l37 (-> with-missing (sk/xkcd7-lay-point :x :y)))


(deftest
 t5_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l37)))


(def
 v7_l49
 (def
  with-infinity
  {:x [1 2 3 4 5],
   :y
   [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]}))


(def v8_l53 (-> with-infinity (sk/xkcd7-lay-point :x :y)))


(deftest
 t9_l56
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 3 (:points s))
      (not (clojure.string/includes? (str v) "NaN")))))
   v8_l53)))


(def v11_l64 (-> {:x [3], :y [7]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t12_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v11_l64)))


(def
 v14_l76
 (-> {:x [1 10], :y [5 50]} (sk/xkcd7-lay-point :x :y) sk/xkcd7-lay-lm))


(deftest
 t15_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v14_l76)))


(def
 v17_l88
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/xkcd7-lay-point :x :y)
  sk/xkcd7-lay-lm))


(deftest
 t18_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v17_l88)))


(def
 v20_l100
 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t21_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v20_l100)))


(def
 v23_l111
 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t24_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v23_l111)))


(def
 v26_l122
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t27_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v26_l122)))


(def
 v29_l131
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/xkcd7-lay-point :x :y)))


(deftest
 t30_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v29_l131)))


(def
 v32_l140
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/xkcd7-lay-point :x :y)))


(deftest
 t33_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v32_l140)))


(def
 v35_l151
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def
 v36_l157
 (-> large-data (sk/xkcd7-lay-point :x :y {:color :group})))


(deftest
 t37_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v36_l157)))


(def
 v39_l168
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__397420#] (keyword (str "cat-" p1__397420#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/xkcd7-lay-value-bar :category :value)))


(deftest
 t40_l173
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v39_l168)))


(def
 v42_l181
 (->
  data/iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/xkcd7-lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/xkcd7-options {:title "Sepal Length/Width Ratio"})))
