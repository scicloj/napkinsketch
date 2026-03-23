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
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/view :x :y) sk/lay-point))


(deftest
 t8_l53
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l48)))


(def v10_l63 (def my-view (sk/view iris :sepal_length :sepal_width)))


(def v11_l65 (kind/pprint my-view))


(def v13_l84 (method/point))


(deftest
 t14_l86
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l84)))


(def v16_l93 (def view-with-method (sk/lay my-view (method/point))))


(def v17_l96 (kind/pprint view-with-method))


(def
 v19_l110
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point))


(deftest
 t20_l114
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v19_l110)))


(def v22_l129 (method/histogram))


(deftest t23_l131 (is ((fn [m] (= :bar (:mark m))) v22_l129)))


(def v25_l142 (-> iris (sk/view :sepal_length) sk/lay-histogram))


(deftest
 t26_l146
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l142)))


(def v28_l159 (method/stacked-bar))


(deftest t29_l161 (is ((fn [m] (= :stack (:position m))) v28_l159)))


(def v31_l173 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t32_l176
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v31_l173)))


(def v34_l180 (-> iris (sk/view :sepal_length)))


(deftest
 t35_l183
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v34_l180)))


(def
 v37_l198
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t38_l203
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l198)))


(def
 v40_l213
 (def
  scatter-base
  (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point)))


(def v42_l220 (-> scatter-base sk/lay-lm))


(deftest
 t43_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v42_l220)))


(def v45_l230 (-> scatter-base sk/lay-loess))


(deftest
 t46_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l230)))


(def
 v48_l247
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})))


(deftest
 t49_l251
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v48_l247)))


(def
 v51_l260
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :petal_length})))


(deftest
 t52_l264
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v51_l260)))


(def
 v54_l270
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color "steelblue"})))


(deftest
 t55_l274
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l270)))


(def
 v57_l284
 (-> iris (sk/view :sepal_length :sepal_width) sk/lay-point sk/lay-lm))


(deftest
 t58_l289
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l284)))


(def
 v60_l297
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t61_l302
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v60_l297)))


(def
 v63_l317
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t64_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v63_l317)))


(def v66_l338 (def cols [:sepal_length :sepal_width :petal_length]))


(def v67_l340 (sk/cross cols cols))


(deftest t68_l342 (is ((fn [v] (= 9 (count v))) v67_l340)))


(def v70_l347 (-> iris (sk/view (sk/cross cols cols))))


(deftest
 t71_l350
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v70_l347)))


(def
 v73_l369
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/coord :flip)))


(deftest
 t74_l374
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l369)))


(def
 v76_l382
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/view :population :area)
  sk/lay-point
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t77_l389
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v76_l382)))
