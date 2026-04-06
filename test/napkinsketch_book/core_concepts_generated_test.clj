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
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t7_l50
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l47)))


(def v9_l60 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


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
  (sk/lay-value-bar :city :temperature)))


(deftest
 t13_l75
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l69)))


(def v15_l81 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l84
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l81)))


(def
 v18_l88
 (->
  {:x [1 2 3 4], :y [4 5 6 7], :group ["a" "a" "b" "b"]}
  sk/lay-point))


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
  (sk/lay-line :x :y)))


(deftest
 t22_l110
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v21_l106)))


(def
 v24_l147
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t25_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v24_l147)))


(def v27_l165 (sk/method-lookup :histogram))


(deftest t28_l167 (is ((fn [m] (= :bar (:mark m))) v27_l165)))


(def v30_l171 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t31_l174
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v30_l171)))


(def v33_l190 (sk/method-lookup :lm))


(deftest t34_l192 (is ((fn [m] (= :lm (:stat m))) v33_l190)))


(def
 v36_l196
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t37_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l196)))


(def v39_l211 (sk/method-lookup :stacked-bar))


(deftest t40_l213 (is ((fn [m] (= :stack (:position m))) v39_l211)))


(def
 v42_l217
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t43_l222
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v42_l217)))


(def v45_l236 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t46_l239
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v45_l236)))


(def v48_l243 (-> data/iris (sk/view :sepal_length)))


(deftest
 t49_l246
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l243)))


(def
 v51_l261
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l261)))


(def
 v54_l273
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-loess))


(deftest
 t55_l278
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l273)))


(def
 v57_l290
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t58_l295
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v57_l290)))


(def
 v60_l301
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t61_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l301)))


(def
 v63_l329
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v65_l335 (-> scatter-base sk/lay-lm))


(deftest
 t66_l338
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v65_l335)))


(def v68_l345 (-> scatter-base sk/lay-loess))


(deftest
 t69_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l345)))


(def
 v71_l355
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/view :petal_length :petal_width)
  sk/lay-point))


(deftest
 t72_l360
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v71_l355)))


(def
 v74_l374
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t75_l377
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v74_l374)))


(def
 v77_l386
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t78_l389
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l386)))


(def
 v80_l395
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t81_l398
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l395)))


(def
 v83_l409
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t84_l414
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v83_l409)))


(def
 v86_l422
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t87_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v86_l422)))


(def
 v89_l442
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t90_l448
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v89_l442)))


(def v92_l463 (def cols [:sepal_length :sepal_width :petal_length]))


(def v93_l465 (sk/cross cols cols))


(deftest t94_l467 (is ((fn [v] (= 9 (count v))) v93_l465)))


(def
 v96_l472
 (-> data/iris (sk/view (sk/cross cols cols)) sk/lay-point))


(deftest
 t97_l476
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v96_l472)))


(def
 v99_l495
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t100_l499
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v99_l495)))


(def
 v102_l507
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t103_l513
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v102_l507)))
