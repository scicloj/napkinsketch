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


(def v7_l46 {:x [1 2 3 4 5], :y [2 4 3 5 4]})


(def v9_l57 (def my-view (sk/view iris :sepal_length :sepal_width)))


(def v10_l59 (kind/pprint my-view))


(def v12_l71 (sk/point))


(deftest t13_l73 (is ((fn [m] (= :point (:mark m))) v12_l71)))


(def v15_l79 (def view-with-mark (sk/lay my-view (sk/point))))


(def v16_l82 (kind/pprint view-with-mark))


(def
 v18_l90
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t19_l95
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l90)))


(def
 v21_l110
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t22_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v21_l110)))


(def
 v24_l125
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point)))))


(def v26_l132 (-> scatter-base (sk/lay (sk/lm)) sk/plot))


(deftest
 t27_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v26_l132)))


(def v29_l143 (-> scatter-base (sk/lay (sk/loess)) sk/plot))


(deftest
 t30_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v29_l143)))


(def
 v32_l161
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t33_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v32_l161)))


(def
 v35_l175
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t36_l180
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v35_l175)))


(def
 v38_l186
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t39_l191
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v38_l186)))


(def
 v41_l202
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t42_l208
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v41_l202)))


(def
 v44_l224
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t45_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v44_l224)))


(def
 v47_l245
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def v48_l247 (sk/pairs measurements))


(deftest t49_l249 (is ((fn [v] (= 6 (count v))) v48_l247)))


(def
 v51_l254
 (->
  iris
  (sk/view (sk/pairs measurements))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t52_l259
 (is ((fn [v] (= 6 (:panels (sk/svg-summary v)))) v51_l254)))


(def
 v54_l271
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t55_l277
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v54_l271)))


(def
 v57_l283
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t58_l289
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v57_l283)))
