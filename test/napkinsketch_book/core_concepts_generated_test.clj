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


(def v13_l77 (sk/point))


(deftest t14_l79 (is ((fn [m] (= :point (:mark m))) v13_l77)))


(def v16_l85 (def view-with-mark (sk/lay my-view (sk/point))))


(def v17_l88 (kind/pprint view-with-mark))


(def
 v19_l96
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l101
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l96)))


(def v22_l113 (-> iris (sk/view :sepal_length :sepal_width) sk/plot))


(deftest
 t23_l117
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v22_l113)))


(def v25_l121 (-> iris (sk/view :sepal_length) sk/plot))


(deftest
 t26_l125
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l121)))


(def
 v28_l140
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t29_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v28_l140)))


(def
 v31_l155
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point)))))


(def v33_l162 (-> scatter-base (sk/lay (sk/lm)) sk/plot))


(deftest
 t34_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v33_l162)))


(def v36_l173 (-> scatter-base (sk/lay (sk/loess)) sk/plot))


(deftest
 t37_l177
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l173)))


(def
 v39_l191
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t40_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v39_l191)))


(def
 v42_l205
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t43_l210
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v42_l205)))


(def
 v45_l216
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t46_l221
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v45_l216)))


(def
 v48_l231
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t49_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l231)))


(def
 v51_l244
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t52_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v51_l244)))


(def
 v54_l265
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t55_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v54_l265)))


(def v57_l286 (def cols [:sepal_length :sepal_width :petal_length]))


(def v58_l288 (sk/cross cols cols))


(deftest t59_l290 (is ((fn [v] (= 9 (count v))) v58_l288)))


(def v61_l295 (-> iris (sk/view (sk/cross cols cols)) sk/plot))


(deftest
 t62_l299
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v61_l295)))


(def
 v64_l318
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/coord :flip)
  sk/plot))


(deftest
 t65_l324
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v64_l318)))


(def
 v67_l332
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/view :population :area)
  (sk/lay (sk/point))
  (sk/scale :x :log)
  (sk/scale :y :log)
  sk/plot))


(deftest
 t68_l340
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v67_l332)))
