(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l34
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l37 iris)


(deftest
 t5_l39
 (is ((fn [v] (= 150 (count (tablecloth.api/rows v)))) v4_l37)))


(def
 v7_l50
 (->
  {:x [1 2 3 4 5], :y [2 4 3 5 4]}
  (sk/view :x :y)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t8_l55
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l50)))


(def
 v10_l59
 (->
  [{:x 1, :y 2, :g "a"} {:x 3, :y 4, :g "a"} {:x 5, :y 6, :g "b"}]
  (sk/view :x :y)
  (sk/lay (sk/point {:color :g}))
  sk/plot))


(deftest
 t11_l64
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v10_l59)))


(def
 v13_l68
 (->
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})
  (sk/view :total_bill :tip)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t14_l74
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v13_l68)))


(def
 v16_l91
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t17_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l91)))


(def
 v19_l102
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t20_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v19_l102)))


(def
 v22_l118
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v22_l118)))


(def
 v25_l131
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t26_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v25_l131)))


(def
 v28_l145
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t29_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v28_l145)))


(def
 v31_l159
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t32_l164
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v31_l159)))


(def v34_l173 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t35_l178
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v34_l173)))


(def
 v37_l186
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t38_l192
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v37_l186)))


(def
 v40_l199
 (->
  {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/line))
  sk/plot))


(deftest
 t41_l205
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v40_l199)))


(def
 v43_l213
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
 t44_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v43_l213)))
