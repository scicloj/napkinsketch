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
 v24_l141
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t25_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v24_l141)))


(def v27_l159 (sk/method-lookup :histogram))


(deftest t28_l161 (is ((fn [m] (= :bar (:mark m))) v27_l159)))


(def v30_l165 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t31_l168
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v30_l165)))


(def v33_l184 (sk/method-lookup :lm))


(deftest t34_l186 (is ((fn [m] (= :lm (:stat m))) v33_l184)))


(def
 v36_l190
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t37_l194
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l190)))


(def v39_l205 (sk/method-lookup :stacked-bar))


(deftest t40_l207 (is ((fn [m] (= :stack (:position m))) v39_l205)))


(def
 v42_l211
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t43_l216
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v42_l211)))


(def v45_l230 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t46_l233
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v45_l230)))


(def v48_l237 (-> data/iris (sk/view :sepal_length)))


(deftest
 t49_l240
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l237)))


(def
 v51_l255
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l255)))


(def
 v54_l267
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-loess))


(deftest
 t55_l272
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v54_l267)))


(def
 v57_l279
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t58_l283
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l279)))


(def
 v60_l305
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v62_l311 (-> scatter-base sk/lay-lm))


(deftest
 t63_l314
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v62_l311)))


(def v65_l321 (-> scatter-base sk/lay-loess))


(deftest
 t66_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v65_l321)))


(def
 v68_l332
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t69_l335
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v68_l332)))


(def
 v71_l349
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t72_l352
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v71_l349)))


(def
 v74_l361
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t75_l364
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v74_l361)))


(def
 v77_l370
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t78_l373
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l370)))


(def
 v80_l383
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t81_l388
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v80_l383)))


(def
 v83_l396
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t84_l401
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v83_l396)))


(def
 v86_l416
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t87_l422
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v86_l416)))


(def v89_l437 (def cols [:sepal_length :sepal_width :petal_length]))


(def v90_l439 (sk/cross cols cols))


(deftest t91_l441 (is ((fn [v] (= 9 (count v))) v90_l439)))


(def v93_l446 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t94_l449
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v93_l446)))


(def
 v96_l468
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t97_l472
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v96_l468)))


(def
 v99_l480
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t100_l486
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v99_l480)))
