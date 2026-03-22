(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l35
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l38 iris)


(deftest
 t5_l40
 (is ((fn [v] (= 150 (count (tablecloth.api/rows v)))) v4_l38)))


(def
 v7_l51
 (->
  {:x [1 2 3 4 5], :y [2 4 3 5 4]}
  (sk/view :x :y)
  sk/lay-point
  sk/plot))


(deftest
 t8_l56
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l51)))


(def
 v10_l60
 (->
  [{:x 1, :y 2, :g "a"} {:x 3, :y 4, :g "a"} {:x 5, :y 6, :g "b"}]
  (sk/view :x :y)
  (sk/lay-point {:color :g})
  sk/plot))


(deftest
 t11_l65
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v10_l60)))


(def
 v13_l69
 (->
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})
  (sk/view :total_bill :tip)
  sk/lay-point
  sk/plot))


(deftest
 t14_l75
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v13_l69)))


(def
 v16_l84
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/plot))


(deftest
 t17_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l84)))


(def
 v19_l96
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t20_l101
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v19_l96)))


(def v22_l121 (-> iris (sk/view :sepal_length :sepal_width) sk/plot))


(deftest
 t23_l125
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v22_l121)))


(def
 v25_l131
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/plot))


(deftest
 t26_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v25_l131)))


(def
 v28_l144
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t29_l149
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v28_l144)))


(def
 v31_l158
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t32_l164
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v31_l158)))


(def
 v34_l172
 (-> iris (sk/view :sepal_length) sk/lay-histogram sk/plot))


(deftest
 t35_l177
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v34_l172)))


(def v37_l186 (-> iris (sk/view :species) sk/lay-bar sk/plot))


(deftest
 t38_l191
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v37_l186)))


(def
 v40_l199
 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip) sk/plot))


(deftest
 t41_l205
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v40_l199)))


(def
 v43_l212
 (->
  {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]}
  (sk/view [[:x :y]])
  sk/lay-line
  sk/plot))


(deftest
 t44_l218
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v43_l212)))


(def
 v46_l226
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay-point {:color :species})
  (sk/plot
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t47_l234
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v46_l226)))
