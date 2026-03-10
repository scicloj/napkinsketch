(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l16
 (def
  with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]})))


(def
 v4_l20
 (-> with-missing (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t5_l25
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and
       (map? attrs)
       (number? (:width attrs))
       (number? (:height attrs))))
     (let [body (nth v 2)] (and (vector? body) (= :g (first body))))))
   v4_l20)))


(def
 v7_l36
 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t8_l41
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v7_l36)))


(def
 v10_l51
 (->
  {:x [1 10], :y [5 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t11_l56
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v10_l51)))


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
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v13_l65)))


(def
 v16_l79
 (->
  {:x [5 5 5 5 5], :y [1 2 3 4 5]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t17_l84
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v16_l79)))


(def
 v19_l93
 (->
  {:x [1 2 3 4 5], :y [3 3 3 3 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l98
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v19_l93)))


(def
 v22_l107
 (->
  {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l112
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v22_l107)))


(def
 v25_l119
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t26_l124
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v25_l119)))


(def
 v28_l131
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t29_l136
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v28_l131)))


(def
 v31_l145
 (def
  large-data
  (fn
   []
   (tc/dataset
    {:x (repeatedly 1000 (fn* [] (rand))),
     :y (repeatedly 1000 (fn* [] (rand))),
     :group (repeatedly 1000 (fn* [] (rand-nth [:a :b :c])))}))))


(def
 v32_l151
 (->
  (large-data)
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:color :group}))
  sk/plot))


(deftest
 t33_l156
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and
       (map? attrs)
       (number? (:width attrs))
       (number? (:height attrs))))
     (let [body (nth v 2)] (and (vector? body) (= :g (first body))))))
   v32_l151)))


(def
 v35_l167
 (->
  (tc/dataset
   {:category
    (mapv
     (fn* [p1__72745#] (keyword (str "cat-" p1__72745#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rand-int 90))))})
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  sk/plot))


(deftest
 t36_l173
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v35_l167)))


(def
 v38_l182
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l185
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/view [[:sepal_length :sepal_ratio]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l191
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v39_l185)))


(def
 v42_l200
 (->
  iris
  (tc/select-rows
   (fn* [p1__72746#] (= "setosa" (p1__72746# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Setosa Only"})))


(deftest
 t43_l206
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and
       (map? attrs)
       (number? (:width attrs))
       (number? (:height attrs))))
     (let [body (nth v 2)] (and (vector? body) (= :g (first body))))))
   v42_l200)))
