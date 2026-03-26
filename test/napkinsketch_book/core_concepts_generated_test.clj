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


(def v7_l48 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t8_l52
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l48)))


(def v10_l62 (def my-view (sk/view iris :sepal_length :sepal_width)))


(def v11_l64 (kind/pprint my-view))


(def v13_l85 (method/lookup :point))


(deftest
 t14_l87
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l85)))


(def
 v16_l94
 (def view-with-method (sk/lay my-view (method/lookup :point))))


(def v17_l97 (kind/pprint view-with-method))


(def v19_l111 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t20_l114
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l111)))


(def v22_l128 (method/lookup :histogram))


(deftest t23_l130 (is ((fn [m] (= :bar (:mark m))) v22_l128)))


(def v25_l134 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t26_l137
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l134)))


(def v28_l153 (method/lookup :lm))


(deftest t29_l155 (is ((fn [m] (= :lm (:stat m))) v28_l153)))


(def
 v31_l159
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t32_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v31_l159)))


(def v34_l174 (method/lookup :stacked-bar))


(deftest t35_l176 (is ((fn [m] (= :stack (:position m))) v34_l174)))


(def
 v37_l180
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t38_l185
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v37_l180)))


(def v40_l194 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t41_l197
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v40_l194)))


(def v43_l201 (-> iris (sk/view :sepal_length)))


(deftest
 t44_l204
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v43_l201)))


(def
 v46_l219
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t47_l224
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v46_l219)))


(def
 v49_l231
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t50_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v49_l231)))


(def
 v52_l245
 (def scatter-base (-> iris (sk/lay-point :sepal_length :sepal_width))))


(def v54_l251 (-> scatter-base sk/lay-lm))


(deftest
 t55_l254
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l251)))


(def v57_l261 (-> scatter-base sk/lay-loess))


(deftest
 t58_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l261)))


(def
 v60_l278
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t61_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v60_l278)))


(def
 v63_l290
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t64_l293
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v63_l290)))


(def
 v66_l299
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t67_l302
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v66_l299)))


(def
 v69_l312
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t70_l317
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v69_l312)))


(def
 v72_l325
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t73_l330
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v72_l325)))


(def
 v75_l345
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t76_l351
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v75_l345)))


(def v78_l366 (def cols [:sepal_length :sepal_width :petal_length]))


(def v79_l368 (sk/cross cols cols))


(deftest t80_l370 (is ((fn [v] (= 9 (count v))) v79_l368)))


(def v82_l375 (-> iris (sk/view (sk/cross cols cols))))


(deftest
 t83_l378
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v82_l375)))


(def
 v85_l397
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t86_l401
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l397)))


(def
 v88_l409
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t89_l415
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v88_l409)))
