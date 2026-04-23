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
   (fn* [p1__174292#] (= "setosa" (:species p1__174292#))))))


(def
 v38_l219
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__174293#] (= "versicolor" (:species p1__174293#))))))


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
 v54_l304
 (def
  my-frame
  (->
   (rdatasets/datasets-iris)
   (sk/frame :sepal-length :sepal-width {:color :species})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Iris"}))))


(def v55_l311 my-frame)


(deftest
 t56_l313
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v55_l311)))


(def v58_l326 (sk/layer-type-lookup :histogram))


(deftest t59_l328 (is ((fn [m] (= :bar (:mark m))) v58_l326)))


(def
 v61_l332
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t62_l335
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v61_l332)))


(def v64_l339 (sk/layer-type-lookup :smooth))


(deftest t65_l341 (is ((fn [m] (= :loess (:stat m))) v64_l339)))


(def
 v67_l345
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t68_l350
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v67_l345)))


(def
 v70_l395
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/lay-point))


(deftest
 t71_l398
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v70_l395)))


(def
 v73_l405
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t74_l408
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l405)))


(def v76_l412 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t77_l415
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v76_l412)))


(def
 v79_l440
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v81_l446 (-> scatter-base (sk/lay-smooth {:stat :linear-model})))


(deftest
 t82_l448
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v81_l446)))


(def v84_l454 (-> scatter-base sk/lay-smooth))


(deftest
 t85_l456
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v84_l454)))


(def
 v87_l469
 (def
  scatter-with-regression
  (->
   (sk/frame nil {:x :x, :y :y, :color :group})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Scatter with Regression"}))))


(def
 v89_l477
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t90_l482
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v89_l477)))


(def
 v92_l488
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t93_l493
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v92_l488)))


(def
 v95_l511
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t96_l514
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v95_l511)))


(def
 v98_l520
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t99_l523
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v98_l520)))


(def
 v101_l527
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t102_l530
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v101_l527)))


(def
 v104_l536
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t105_l539
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v104_l536)))


(def
 v107_l545
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:group :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t108_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v107_l545)))


(def
 v110_l569
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t111_l574
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v110_l569)))


(def
 v113_l583
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t114_l588
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v113_l583)))


(def
 v116_l598
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t117_l602
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v116_l598)))


(def
 v119_l607
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t120_l613
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v119_l607)))


(def
 v122_l622
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t123_l628
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v122_l622)))


(def
 v125_l634
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t126_l637
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v125_l634)))


(def
 v128_l641
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width))]))


(deftest
 t129_l647
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v128_l641)))
