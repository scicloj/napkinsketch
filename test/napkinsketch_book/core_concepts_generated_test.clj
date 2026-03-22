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


(def v13_l83 (sk/point))


(deftest
 t14_l85
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l83)))


(def v16_l92 (def view-with-method (sk/lay my-view (sk/point))))


(def v17_l95 (kind/pprint view-with-method))


(def
 v19_l103
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l108
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l103)))


(def v22_l123 (sk/histogram))


(deftest t23_l125 (is ((fn [m] (= :bar (:mark m))) v22_l123)))


(def
 v25_l136
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t26_l141
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l136)))


(def v28_l154 (sk/stacked-bar))


(deftest t29_l156 (is ((fn [m] (= :stack (:position m))) v28_l154)))


(def v31_l168 (-> iris (sk/view :sepal_length :sepal_width) sk/plot))


(deftest
 t32_l172
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v31_l168)))


(def v34_l176 (-> iris (sk/view :sepal_length) sk/plot))


(deftest
 t35_l180
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v34_l176)))


(def
 v37_l195
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t38_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l195)))


(def
 v40_l210
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point)))))


(def v42_l217 (-> scatter-base (sk/lay (sk/lm)) sk/plot))


(deftest
 t43_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l217)))


(def v45_l228 (-> scatter-base (sk/lay (sk/loess)) sk/plot))


(deftest
 t46_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l228)))


(def
 v48_l246
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t49_l251
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v48_l246)))


(def
 v51_l260
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t52_l265
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v51_l260)))


(def
 v54_l271
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t55_l276
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l271)))


(def
 v57_l286
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t58_l291
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l286)))


(def
 v60_l299
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t61_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v60_l299)))


(def
 v63_l320
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t64_l326
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v63_l320)))


(def v66_l341 (def cols [:sepal_length :sepal_width :petal_length]))


(def v67_l343 (sk/cross cols cols))


(deftest t68_l345 (is ((fn [v] (= 9 (count v))) v67_l343)))


(def v70_l350 (-> iris (sk/view (sk/cross cols cols)) sk/plot))


(deftest
 t71_l354
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v70_l350)))


(def
 v73_l373
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/coord :flip)
  sk/plot))


(deftest
 t74_l379
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l373)))


(def
 v76_l387
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/view :population :area)
  (sk/lay (sk/point))
  (sk/scale :x :log)
  (sk/scale :y :log)
  sk/plot))


(deftest
 t77_l395
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v76_l387)))
