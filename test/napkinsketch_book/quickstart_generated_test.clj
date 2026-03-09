(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l30 (tc/head iris))


(def
 v6_l39
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t7_l44
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v6_l39)))


(def
 v9_l51
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t10_l54
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v9_l51)))


(def
 v12_l69
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t13_l74
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
   v12_l69)))


(def
 v15_l85
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t16_l90
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
   v15_l85)))


(def
 v18_l101
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t19_l107
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
   v18_l101)))


(def
 v21_l118
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t22_l123
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
   v21_l118)))


(def v24_l134 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t25_l139
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
   v24_l134)))


(def
 v27_l150
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t28_l156
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
   v27_l150)))


(def
 v30_l167
 (->
  {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/line))
  sk/plot))


(deftest
 t31_l173
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
   v30_l167)))


(def
 v33_l184
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t34_l192
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and (map? attrs) (>= (:width attrs) 500)))))
   v33_l184)))
