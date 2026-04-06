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
 v24_l126
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm
  kind/pprint))


(deftest
 t25_l132
 (is
  ((fn
    [sk]
    (and
     (= :species (get-in sk [:shared :color]))
     (= 1 (count (:entries sk)))
     (= 2 (count (:methods sk)))))
   v24_l126)))


(def
 v27_l148
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/view :petal_length :petal_width)
  sk/lay-point
  kind/pprint))


(deftest
 t28_l154
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:entries sk)))
     (= :sepal_length (:x (first (:entries sk))))
     (= :petal_length (:x (second (:entries sk))))))
   v27_l148)))


(def
 v30_l163
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-histogram :petal_length)
  kind/pprint))


(deftest
 t31_l168
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:entries sk)))
     (= 0 (count (:methods sk)))
     (= 1 (count (:methods (first (:entries sk)))))))
   v30_l163)))


(def
 v33_l180
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t34_l185
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v33_l180)))


(def
 v36_l194
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t37_l198
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l194)))


(def
 v39_l259
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t40_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v39_l259)))


(def v42_l277 (sk/method-lookup :histogram))


(deftest t43_l279 (is ((fn [m] (= :bar (:mark m))) v42_l277)))


(def v45_l283 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t46_l286
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l283)))


(def v48_l302 (sk/method-lookup :lm))


(deftest t49_l304 (is ((fn [m] (= :lm (:stat m))) v48_l302)))


(def
 v51_l308
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t52_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l308)))


(def v54_l323 (sk/method-lookup :stacked-bar))


(deftest t55_l325 (is ((fn [m] (= :stack (:position m))) v54_l323)))


(def
 v57_l329
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t58_l334
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v57_l329)))


(def v60_l348 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t61_l351
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v60_l348)))


(def v63_l355 (-> data/iris (sk/view :sepal_length)))


(deftest
 t64_l358
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v63_l355)))


(def
 v66_l373
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t67_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v66_l373)))


(def
 v69_l385
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-loess))


(deftest
 t70_l390
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v69_l385)))


(def
 v72_l402
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t73_l407
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v72_l402)))


(def
 v75_l413
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t76_l417
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v75_l413)))


(def
 v78_l441
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v80_l447 (-> scatter-base sk/lay-lm))


(deftest
 t81_l450
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v80_l447)))


(def v83_l457 (-> scatter-base sk/lay-loess))


(deftest
 t84_l460
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v83_l457)))


(def
 v86_l467
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/view :petal_length :petal_width)
  sk/lay-point))


(deftest
 t87_l472
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v86_l467)))


(def
 v89_l486
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t90_l489
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v89_l486)))


(def
 v92_l498
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t93_l501
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v92_l498)))


(def
 v95_l507
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t96_l510
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v95_l507)))


(def
 v98_l521
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t99_l526
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v98_l521)))


(def
 v101_l534
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t102_l539
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v101_l534)))


(def
 v104_l554
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t105_l560
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v104_l554)))


(def v107_l575 (def cols [:sepal_length :sepal_width :petal_length]))


(def v108_l577 (sk/cross cols cols))


(deftest t109_l579 (is ((fn [v] (= 9 (count v))) v108_l577)))


(def
 v111_l584
 (-> data/iris (sk/view (sk/cross cols cols)) sk/lay-point))


(deftest
 t112_l588
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v111_l584)))


(def
 v114_l607
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t115_l611
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v114_l607)))


(def
 v117_l619
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t118_l625
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v117_l619)))
