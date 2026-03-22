(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l33 iris)


(deftest t5_l35 (is ((fn [ds] (= 150 (tc/row-count ds))) v4_l33)))


(def
 v7_l48
 (->
  {:x [1 2 3 4 5], :y [2 4 3 5 4]}
  (sk/view :x :y)
  sk/lay-point
  sk/plot))


(deftest
 t8_l54
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l48)))


(def v10_l64 (def my-view (sk/view iris :sepal_length :sepal_width)))


(def v11_l66 (kind/pprint my-view))


(def v13_l85 (method/point))


(deftest
 t14_l87
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l85)))


(def v16_l94 (def view-with-method (sk/lay my-view (method/point))))


(def v17_l97 (kind/pprint view-with-method))


(def
 v19_l105
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/plot))


(deftest
 t20_l110
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l105)))


(def v22_l125 (method/histogram))


(deftest t23_l127 (is ((fn [m] (= :bar (:mark m))) v22_l125)))


(def
 v25_l138
 (-> iris (sk/view :sepal_length) sk/lay-histogram sk/plot))


(deftest
 t26_l143
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l138)))


(def v28_l156 (method/stacked-bar))


(deftest t29_l158 (is ((fn [m] (= :stack (:position m))) v28_l156)))


(def v31_l170 (-> iris (sk/view :sepal_length :sepal_width) sk/plot))


(deftest
 t32_l174
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v31_l170)))


(def v34_l178 (-> iris (sk/view :sepal_length) sk/plot))


(deftest
 t35_l182
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v34_l178)))


(def
 v37_l197
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm
  sk/plot))


(deftest
 t38_l203
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l197)))


(def
 v40_l213
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point)))


(def v42_l220 (-> scatter-base sk/lay-lm sk/plot))


(deftest
 t43_l224
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l220)))


(def v45_l231 (-> scatter-base sk/lay-loess sk/plot))


(deftest
 t46_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l231)))


(def
 v48_l249
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t49_l254
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v48_l249)))


(def
 v51_l263
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :petal_length})
  sk/plot))


(deftest
 t52_l268
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v51_l263)))


(def
 v54_l274
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color "steelblue"})
  sk/plot))


(deftest
 t55_l279
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l274)))


(def
 v57_l289
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm
  sk/plot))


(deftest
 t58_l295
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l289)))


(def
 v60_l303
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t61_l309
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v60_l303)))


(def
 v63_l324
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  sk/plot))


(deftest
 t64_l331
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v63_l324)))


(def v66_l346 (def cols [:sepal_length :sepal_width :petal_length]))


(def v67_l348 (sk/cross cols cols))


(deftest t68_l350 (is ((fn [v] (= 9 (count v))) v67_l348)))


(def v70_l355 (-> iris (sk/view (sk/cross cols cols)) sk/plot))


(deftest
 t71_l359
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v70_l355)))


(def
 v73_l378
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/coord :flip)
  sk/plot))


(deftest
 t74_l384
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l378)))


(def
 v76_l392
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/view :population :area)
  sk/lay-point
  (sk/scale :x :log)
  (sk/scale :y :log)
  sk/plot))


(deftest
 t77_l400
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v76_l392)))
