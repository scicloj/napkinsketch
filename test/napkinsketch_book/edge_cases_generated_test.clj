(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (def
  with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]})))


(def
 v4_l25
 (-> with-missing (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t5_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l25)))


(def
 v7_l38
 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t8_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l38)))


(def
 v10_l52
 (->
  {:x [1 10], :y [5 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t11_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l52)))


(def
 v13_l65
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t14_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l65)))


(def
 v16_l78
 (->
  {:x [5 5 5 5 5], :y [1 2 3 4 5]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t17_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l78)))


(def
 v19_l91
 (->
  {:x [1 2 3 4 5], :y [3 3 3 3 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l91)))


(def
 v22_l104
 (->
  {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l104)))


(def
 v25_l115
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t26_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l115)))


(def
 v28_l126
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t29_l131
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l126)))


(def
 v31_l139
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
 v32_l145
 (->
  large-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:color :group}))
  sk/plot))


(deftest
 t33_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l145)))


(def
 v35_l158
 (->
  (let
   [r (rng/rng :jdk 99)]
   (tc/dataset
    {:category
     (mapv
      (fn* [p1__79996#] (keyword (str "cat-" p1__79996#)))
      (range 12)),
     :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))}))
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  sk/plot))


(deftest
 t36_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l158)))


(def
 v38_l173
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l176
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/view [[:sepal_length :sepal_ratio]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l176)))


(def
 v42_l190
 (->
  iris
  (tc/select-rows
   (fn* [p1__79997#] (= "setosa" (p1__79997# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Setosa Only"})))


(deftest
 t43_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l190)))
