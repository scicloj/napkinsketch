(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l28 data/iris)


(deftest
 t4_l30
 (is ((fn [ds] (= 150 (count (tablecloth.api/rows ds)))) v3_l28)))


(def v6_l43 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t7_l47
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l43)))


(def
 v9_l57
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v10_l59 (kind/pprint my-view))


(def v12_l80 (method/lookup :point))


(deftest
 t13_l82
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v12_l80)))


(def
 v15_l89
 (def view-with-method (sk/lay my-view (method/lookup :point))))


(def v16_l92 (kind/pprint view-with-method))


(def v18_l106 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t19_l109
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l106)))


(def
 v21_l118
 (sk/plot-spec? (sk/lay-point data/iris :sepal_length :sepal_width)))


(deftest t22_l120 (is ((fn [v] (true? v)) v21_l118)))


(def v24_l154 (method/lookup :histogram))


(deftest t25_l156 (is ((fn [m] (= :bar (:mark m))) v24_l154)))


(def v27_l160 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t28_l163
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v27_l160)))


(def v30_l179 (method/lookup :lm))


(deftest t31_l181 (is ((fn [m] (= :lm (:stat m))) v30_l179)))


(def
 v33_l185
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t34_l189
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v33_l185)))


(def v36_l200 (method/lookup :stacked-bar))


(deftest t37_l202 (is ((fn [m] (= :stack (:position m))) v36_l200)))


(def
 v39_l206
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t40_l211
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v39_l206)))


(def v42_l220 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t43_l223
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v42_l220)))


(def v45_l227 (-> data/iris (sk/view :sepal_length)))


(deftest
 t46_l230
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l227)))


(def
 v48_l245
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t49_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l245)))


(def
 v51_l257
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t52_l261
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l257)))


(def
 v54_l271
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v56_l277 (-> scatter-base sk/lay-lm))


(deftest
 t57_l280
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v56_l277)))


(def v59_l287 (-> scatter-base sk/lay-loess))


(deftest
 t60_l290
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v59_l287)))


(def
 v62_l298
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t63_l301
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v62_l298)))


(def
 v65_l315
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t66_l318
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v65_l315)))


(def
 v68_l327
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t69_l330
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v68_l327)))


(def
 v71_l336
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t72_l339
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v71_l336)))


(def
 v74_l349
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t75_l354
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v74_l349)))


(def
 v77_l362
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t78_l367
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v77_l362)))


(def
 v80_l382
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t81_l388
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v80_l382)))


(def v83_l403 (def cols [:sepal_length :sepal_width :petal_length]))


(def v84_l405 (sk/cross cols cols))


(deftest t85_l407 (is ((fn [v] (= 9 (count v))) v84_l405)))


(def v87_l412 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t88_l415
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v87_l412)))


(def
 v90_l434
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t91_l438
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v90_l434)))


(def
 v93_l446
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t94_l452
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v93_l446)))
