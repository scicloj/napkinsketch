(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 data/iris)


(deftest t4_l35 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l33)))


(def
 v6_l47
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t7_l50
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l47)))


(def
 v9_l60
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t10_l64
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l60)))


(def
 v12_l69
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/xkcd7-lay-value-bar :city :temperature)))


(deftest
 t13_l75
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l69)))


(def v15_l81 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/xkcd7-lay-point))


(deftest
 t16_l84
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l81)))


(def
 v18_l88
 (->
  {:x [1 2 3 4], :y [4 5 6 7], :group ["a" "a" "b" "b"]}
  sk/xkcd7-lay-point))


(deftest
 t19_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l88)))


(def
 v21_l106
 (->
  (tc/dataset [[1 10] [2 20] [3 15] [4 25]] {:column-names [:x :y]})
  (sk/xkcd7-lay-line :x :y)))


(deftest
 t22_l110
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v21_l106)))


(def
 v24_l148
 (->
  data/iris
  (sk/xkcd7-lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/xkcd7-options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t25_l153
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v24_l148)))


(def v27_l166 (sk/method-lookup :histogram))


(deftest t28_l168 (is ((fn [m] (= :bar (:mark m))) v27_l166)))


(def v30_l172 (-> data/iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t31_l175
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v30_l172)))


(def v33_l191 (sk/method-lookup :lm))


(deftest t34_l193 (is ((fn [m] (= :lm (:stat m))) v33_l191)))


(def
 v36_l197
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  sk/xkcd7-lay-lm))


(deftest
 t37_l201
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l197)))


(def v39_l212 (sk/method-lookup :stacked-bar))


(deftest t40_l214 (is ((fn [m] (= :stack (:position m))) v39_l212)))


(def
 v42_l218
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/xkcd7-lay-value-bar
   :day
   :count
   {:color :meal, :position :stack})))


(deftest
 t43_l223
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v42_l218)))


(def v45_l237 (-> data/iris (sk/xkcd7-view :sepal_length :sepal_width)))


(deftest
 t46_l240
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v45_l237)))


(def v48_l244 (-> data/iris (sk/xkcd7-view :sepal_length)))


(deftest
 t49_l247
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l244)))


(def
 v51_l262
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t52_l267
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l262)))


(def
 v54_l274
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-loess))


(deftest
 t55_l279
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l274)))


(def
 v57_l291
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t58_l296
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v57_l291)))


(def
 v60_l302
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-lm))


(deftest
 t61_l306
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l302)))


(def
 v63_l330
 (def
  scatter-base
  (-> data/iris (sk/xkcd7-lay-point :sepal_length :sepal_width))))


(def v65_l336 (-> scatter-base sk/xkcd7-lay-lm))


(deftest
 t66_l339
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v65_l336)))


(def v68_l346 (-> scatter-base sk/xkcd7-lay-loess))


(deftest
 t69_l349
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l346)))


(def
 v71_l356
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  (sk/xkcd7-view :petal_length :petal_width)
  sk/xkcd7-lay-point))


(deftest
 t72_l361
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v71_l356)))


(def
 v74_l375
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t75_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v74_l375)))


(def
 v77_l387
 (->
  data/iris
  (sk/xkcd7-lay-point
   :sepal_length
   :sepal_width
   {:color :petal_length})))


(deftest
 t78_l390
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l387)))


(def
 v80_l396
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t81_l399
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l396)))


(def
 v83_l410
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t84_l415
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v83_l410)))


(def
 v86_l423
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t87_l428
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v86_l423)))


(def
 v89_l443
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width)
  (sk/xkcd7-facet :species)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t90_l449
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v89_l443)))


(def v92_l464 (def cols [:sepal_length :sepal_width :petal_length]))


(def v93_l466 (sk/cross cols cols))


(deftest t94_l468 (is ((fn [v] (= 9 (count v))) v93_l466)))


(def
 v96_l473
 (-> data/iris (sk/xkcd7-view (sk/cross cols cols)) sk/xkcd7-lay-point))


(deftest
 t97_l477
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v96_l473)))


(def
 v99_l496
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-coord :flip)))


(deftest
 t100_l500
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v99_l496)))


(def
 v102_l508
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/xkcd7-lay-point :population :area)
  (sk/xkcd7-scale :x :log)
  (sk/xkcd7-scale :y :log)))


(deftest
 t103_l514
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v102_l508)))
