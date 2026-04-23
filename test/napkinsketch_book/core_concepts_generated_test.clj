(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (rdatasets/datasets-iris))


(deftest t4_l33 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l31)))


(def
 v6_l45
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l48
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l45)))


(def v9_l57 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t10_l61
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l57)))


(def
 v12_l65
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t13_l71
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l65)))


(def v15_l77 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l80
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l77)))


(def
 v18_l99
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t19_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v18_l99)))


(def
 v21_l113
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t22_l116
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v21_l113)))


(def
 v24_l123
 (def
  two-panel
  (sk/arrange
   [(->
     (rdatasets/datasets-iris)
     (sk/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (sk/lay-point :petal-length :petal-width))])))


(def v25_l130 two-panel)


(deftest
 t26_l132
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v25_l130)))


(def
 v28_l156
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t29_l161
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v28_l156)))


(def
 v31_l170
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t32_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v31_l170)))


(def
 v34_l186
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t35_l191
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v34_l186)))


(def
 v37_l215
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__94055#] (= "setosa" (:species p1__94055#))))))


(def
 v38_l219
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__94056#] (= "versicolor" (:species p1__94056#))))))


(def
 v39_l223
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t40_l228
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v39_l223)))


(def
 v42_l237
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t43_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v42_l237)))


(def
 v45_l261
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t46_l265
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l261)))


(def
 v48_l275
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t49_l279
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v48_l275)))


(def
 v51_l286
 (sk/arrange
  [(-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-width))]))


(deftest
 t52_l290
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v51_l286)))


(def
 v54_l307
 (def
  my-frame
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}],
    :opts {:title "Iris"}})))


(def v55_l315 my-frame)


(deftest
 t56_l317
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Iris"} (:texts s)))))
   v55_l315)))


(def v58_l324 (kind/pprint my-frame))


(deftest
 t59_l326
 (is
  ((fn [fr] (= #{:mapping :opts :layers :data} (set (keys fr))))
   v58_l324)))


(def
 v61_l333
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris"})))


(deftest
 t62_l339
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v61_l333)))


(def v64_l352 (sk/layer-type-lookup :histogram))


(deftest t65_l354 (is ((fn [m] (= :bar (:mark m))) v64_l352)))


(def
 v67_l358
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t68_l361
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v67_l358)))


(def v70_l365 (sk/layer-type-lookup :smooth))


(deftest t71_l367 (is ((fn [m] (= :loess (:stat m))) v70_l365)))


(def
 v73_l371
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t74_l376
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v73_l371)))


(def
 v76_l421
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/lay-point))


(deftest
 t77_l424
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v76_l421)))


(def
 v79_l431
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t80_l434
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l431)))


(def v82_l438 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t83_l441
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v82_l438)))


(def
 v85_l466
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v87_l472 (-> scatter-base (sk/lay-smooth {:stat :linear-model})))


(deftest
 t88_l474
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v87_l472)))


(def v90_l480 (-> scatter-base sk/lay-smooth))


(deftest
 t91_l482
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v90_l480)))


(def
 v93_l495
 (def
  scatter-with-regression
  (->
   (sk/frame nil {:x :x, :y :y, :color :group})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Scatter with Regression"}))))


(def
 v95_l503
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t96_l508
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v95_l503)))


(def
 v98_l514
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t99_l519
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v98_l514)))


(def
 v101_l537
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t102_l540
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v101_l537)))


(def
 v104_l546
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t105_l549
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v104_l546)))


(def
 v107_l553
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t108_l556
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v107_l553)))


(def
 v110_l562
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t111_l565
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v110_l562)))


(def
 v113_l571
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:group :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t114_l576
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v113_l571)))


(def
 v116_l595
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t117_l600
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v116_l595)))


(def
 v119_l609
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t120_l614
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v119_l609)))


(def
 v122_l624
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t123_l628
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v122_l624)))


(def
 v125_l633
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t126_l639
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v125_l633)))


(def
 v128_l648
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t129_l654
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v128_l648)))


(def
 v131_l660
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t132_l663
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v131_l660)))


(def
 v134_l667
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width))]))


(deftest
 t135_l673
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v134_l667)))
