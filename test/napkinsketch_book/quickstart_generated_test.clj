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


(deftest
 t5_l32
 (is ((fn [v] (= 5 (count (tablecloth.api/rows v)))) v4_l30)))


(def
 v7_l43
 (->
  {:x [1 2 3 4 5], :y [2 4 3 5 4]}
  (sk/view :x :y)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t8_l48
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l43)))


(def
 v10_l52
 (->
  [{:x 1, :y 2, :g "a"} {:x 3, :y 4, :g "a"} {:x 5, :y 6, :g "b"}]
  (sk/view :x :y)
  (sk/lay (sk/point {:color :g}))
  sk/plot))


(deftest
 t11_l57
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v10_l52)))


(def
 v13_l62
 (->
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})
  (sk/view :total_bill :tip)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t14_l68
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v13_l62)))


(def
 v16_l77
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t17_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l77)))


(def
 v19_l88
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t20_l91
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v19_l88)))


(def
 v22_l104
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v22_l104)))


(def
 v25_l117
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t26_l122
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v25_l117)))


(def
 v28_l131
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t29_l137
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v28_l131)))


(def
 v31_l145
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t32_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v31_l145)))


(def v34_l159 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t35_l164
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v34_l159)))


(def
 v37_l172
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t38_l178
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v37_l172)))


(def
 v40_l185
 (->
  {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/line))
  sk/plot))


(deftest
 t41_l191
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v40_l185)))


(def
 v43_l199
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
 t44_l207
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v43_l199)))
