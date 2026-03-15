(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l17
 (def
  with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]})))


(def
 v4_l21
 (-> with-missing (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t5_l26
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l21)))


(def
 v7_l34
 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t8_l39
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l34)))


(def
 v10_l48
 (->
  {:x [1 10], :y [5 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t11_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l48)))


(def
 v13_l61
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t14_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l61)))


(def
 v16_l74
 (->
  {:x [5 5 5 5 5], :y [1 2 3 4 5]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t17_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l74)))


(def
 v19_l87
 (->
  {:x [1 2 3 4 5], :y [3 3 3 3 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l87)))


(def
 v22_l100
 (->
  {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l100)))


(def
 v25_l111
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t26_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l111)))


(def
 v28_l122
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t29_l127
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l122)))


(def
 v31_l135
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
     :y (repeatedly 1000 (fn* [] (rng/drandom r))),
     :group
     (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))}))))


(def
 v32_l141
 (->
  large-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:color :group}))
  sk/plot))


(deftest
 t33_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l141)))


(def
 v35_l154
 (->
  (let
   [r (rng/rng :jdk 99)]
   (tc/dataset
    {:category
     (mapv
      (fn* [p1__81888#] (keyword (str "cat-" p1__81888#)))
      (range 12)),
     :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))}))
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  sk/plot))


(deftest
 t36_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l154)))


(def
 v38_l169
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l172
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/view [[:sepal_length :sepal_ratio]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l178
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l172)))


(def
 v42_l186
 (->
  iris
  (tc/select-rows
   (fn* [p1__81889#] (= "setosa" (p1__81889# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Setosa Only"})))


(deftest
 t43_l192
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l186)))
