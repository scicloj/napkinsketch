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
 v40_l210
 (def scatter-base (-> iris (sk/lay-point :sepal_length :sepal_width))))


(def v42_l216 (-> scatter-base sk/lay-lm))


(deftest
 t43_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l216)))


(def v45_l226 (-> scatter-base sk/lay-loess))


(deftest
 t46_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l226)))


(def
 v48_l243
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t49_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v48_l243)))


(def
 v51_l255
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t52_l258
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v51_l255)))


(def
 v54_l264
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t55_l267
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l264)))


(def
 v57_l277
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t58_l282
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l277)))


(def
 v60_l290
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t61_l295
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v60_l290)))


(def
 v63_l310
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t64_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v63_l310)))


(def v66_l331 (def cols [:sepal_length :sepal_width :petal_length]))


(def v67_l333 (sk/cross cols cols))


(deftest t68_l335 (is ((fn [v] (= 9 (count v))) v67_l333)))


(def v70_l340 (-> iris (sk/view (sk/cross cols cols))))


(deftest
 t71_l343
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v70_l340)))


(def
 v73_l362
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t74_l366
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l362)))


(def
 v76_l374
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t77_l380
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v76_l374)))
