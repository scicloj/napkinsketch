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


(def v13_l83 (method/point))


(deftest
 t14_l85
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l83)))


(def v16_l92 (def view-with-method (sk/lay my-view (method/point))))


(def v17_l95 (kind/pprint view-with-method))


(def v19_l109 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t20_l112
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l109)))


(def v22_l127 (method/histogram))


(deftest t23_l129 (is ((fn [m] (= :bar (:mark m))) v22_l127)))


(def v25_l140 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t26_l143
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l140)))


(def v28_l156 (method/stacked-bar))


(deftest t29_l158 (is ((fn [m] (= :stack (:position m))) v28_l156)))


(def v31_l170 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t32_l173
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v31_l170)))


(def v34_l177 (-> iris (sk/view :sepal_length)))


(deftest
 t35_l180
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v34_l177)))


(def
 v37_l195
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


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
 v40_l207
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t41_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v40_l207)))


(def
 v43_l221
 (def scatter-base (-> iris (sk/lay-point :sepal_length :sepal_width))))


(def v45_l227 (-> scatter-base sk/lay-lm))


(deftest
 t46_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l227)))


(def v48_l237 (-> scatter-base sk/lay-loess))


(deftest
 t49_l240
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l237)))


(def
 v51_l254
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t52_l257
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v51_l254)))


(def
 v54_l266
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t55_l269
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l266)))


(def
 v57_l275
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t58_l278
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v57_l275)))


(def
 v60_l288
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t61_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l288)))


(def
 v63_l301
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t64_l306
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v63_l301)))


(def
 v66_l321
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t67_l327
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v66_l321)))


(def v69_l342 (def cols [:sepal_length :sepal_width :petal_length]))


(def v70_l344 (sk/cross cols cols))


(deftest t71_l346 (is ((fn [v] (= 9 (count v))) v70_l344)))


(def v73_l351 (-> iris (sk/view (sk/cross cols cols))))


(deftest
 t74_l354
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v73_l351)))


(def
 v76_l373
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t77_l377
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v76_l373)))


(def
 v79_l385
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t80_l391
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v79_l385)))
