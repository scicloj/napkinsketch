(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l31 data/iris)


(deftest
 t4_l33
 (is ((fn [ds] (= 150 (count (tablecloth.api/rows ds)))) v3_l31)))


(def v6_l46 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t7_l50
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l46)))


(def
 v9_l60
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v10_l62 (kind/pprint my-view))


(def v12_l83 (method/lookup :point))


(deftest
 t13_l85
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v12_l83)))


(def
 v15_l92
 (def view-with-method (sk/lay my-view (method/lookup :point))))


(def v16_l95 (kind/pprint view-with-method))


(def v18_l109 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t19_l112
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l109)))


(def
 v21_l121
 (sk/plot-spec? (sk/lay-point data/iris :sepal_length :sepal_width)))


(deftest t22_l123 (is ((fn [v] (true? v)) v21_l121)))


(def v24_l157 (method/lookup :histogram))


(deftest t25_l159 (is ((fn [m] (= :bar (:mark m))) v24_l157)))


(def v27_l163 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t28_l166
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v27_l163)))


(def v30_l182 (method/lookup :lm))


(deftest t31_l184 (is ((fn [m] (= :lm (:stat m))) v30_l182)))


(def
 v33_l188
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t34_l192
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v33_l188)))


(def v36_l203 (method/lookup :stacked-bar))


(deftest t37_l205 (is ((fn [m] (= :stack (:position m))) v36_l203)))


(def
 v39_l209
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t40_l214
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v39_l209)))


(def v42_l223 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t43_l226
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v42_l223)))


(def v45_l230 (-> data/iris (sk/view :sepal_length)))


(deftest
 t46_l233
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l230)))


(def
 v48_l248
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t49_l253
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l248)))


(def
 v51_l260
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t52_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l260)))


(def
 v54_l274
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v56_l280 (-> scatter-base sk/lay-lm))


(deftest
 t57_l283
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v56_l280)))


(def v59_l290 (-> scatter-base sk/lay-loess))


(deftest
 t60_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v59_l290)))


(def
 v62_l301
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t63_l304
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v62_l301)))


(def
 v65_l318
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t66_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v65_l318)))


(def
 v68_l330
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t69_l333
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v68_l330)))


(def
 v71_l339
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t72_l342
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v71_l339)))


(def
 v74_l352
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t75_l357
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v74_l352)))


(def
 v77_l365
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t78_l370
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v77_l365)))


(def
 v80_l385
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t81_l391
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v80_l385)))


(def v83_l406 (def cols [:sepal_length :sepal_width :petal_length]))


(def v84_l408 (sk/cross cols cols))


(deftest t85_l410 (is ((fn [v] (= 9 (count v))) v84_l408)))


(def v87_l415 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t88_l418
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v87_l415)))


(def
 v90_l437
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t91_l441
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v90_l437)))


(def
 v93_l449
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t94_l455
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v93_l449)))
