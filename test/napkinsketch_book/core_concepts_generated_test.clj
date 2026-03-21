(ns
 napkinsketch-book.core-concepts-generated-test
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


(def v4_l31 iris)


(deftest t5_l33 (is ((fn [ds] (= 150 (tc/row-count ds))) v4_l31)))


(def
 v7_l46
 (->
  {:x [1 2 3 4 5], :y [2 4 3 5 4]}
  (sk/view :x :y)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t8_l52
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l46)))


(def v10_l62 (def my-view (sk/view iris :sepal_length :sepal_width)))


(def v11_l64 (kind/pprint my-view))


(def v13_l76 (sk/point))


(deftest t14_l78 (is ((fn [m] (= :point (:mark m))) v13_l76)))


(def v16_l84 (def view-with-mark (sk/lay my-view (sk/point))))


(def v17_l87 (kind/pprint view-with-mark))


(def
 v19_l95
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l100
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l95)))


(def
 v22_l115
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t23_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v22_l115)))


(def
 v25_l130
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point)))))


(def v27_l137 (-> scatter-base (sk/lay (sk/lm)) sk/plot))


(deftest
 t28_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v27_l137)))


(def v30_l148 (-> scatter-base (sk/lay (sk/loess)) sk/plot))


(deftest
 t31_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v30_l148)))


(def
 v33_l166
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t34_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v33_l166)))


(def
 v36_l180
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t37_l185
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v36_l180)))


(def
 v39_l191
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t40_l196
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v39_l191)))


(def
 v42_l206
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t43_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l206)))


(def
 v45_l219
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t46_l225
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v45_l219)))


(def
 v48_l240
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t49_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v48_l240)))


(def v51_l261 (def cols [:sepal_length :sepal_width :petal_length]))


(def v52_l263 (sk/cross cols cols))


(deftest t53_l265 (is ((fn [v] (= 9 (count v))) v52_l263)))


(def
 v55_l270
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t56_l275
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v55_l270)))


(def
 v58_l289
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/coord :flip)
  sk/plot))


(deftest
 t59_l295
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v58_l289)))


(def
 v61_l303
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/view :population :area)
  (sk/lay (sk/point))
  (sk/scale :x :log)
  (sk/scale :y :log)
  sk/plot))


(deftest
 t62_l311
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v61_l303)))
