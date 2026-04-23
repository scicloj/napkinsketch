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
 v21_l111
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t22_l117
 (is
  ((fn [fr] (and (seq (:data fr)) (= 2 (count (:layers fr)))))
   v21_l111)))


(def
 v24_l128
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t25_l131
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v24_l128)))


(def
 v26_l133
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  kind/pprint))


(deftest t27_l137 (is ((fn [fr] (seq (:data fr))) v26_l133)))


(def
 v29_l141
 (def
  two-panel
  (sk/arrange
   [(->
     (rdatasets/datasets-iris)
     (sk/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (sk/lay-point :petal-length :petal-width))])))


(def v30_l148 two-panel)


(deftest
 t31_l150
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l148)))


(def
 v33_l174
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:x :sepal-length, :y :sepal-width, :color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t34_l179
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v33_l174)))


(def
 v36_l188
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t37_l192
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v36_l188)))


(def
 v39_l204
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:x :sepal-length, :y :sepal-width, :color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t40_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v39_l204)))


(def
 v42_l233
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__145626#] (= "setosa" (:species p1__145626#))))))


(def
 v43_l237
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__145627#] (= "versicolor" (:species p1__145627#))))))


(def
 v44_l241
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t45_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v44_l241)))


(def
 v47_l255
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t48_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v47_l255)))


(def
 v50_l279
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t51_l283
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v50_l279)))


(def
 v53_l293
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t54_l297
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v53_l293)))


(def
 v56_l304
 (sk/arrange
  [(-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-width))]))


(deftest
 t57_l308
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v56_l304)))


(def
 v59_l322
 (def
  my-frame
  (->
   (rdatasets/datasets-iris)
   (sk/frame {:x :sepal-length, :y :sepal-width, :color :species})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Iris"}))))


(def v60_l329 my-frame)


(def v61_l331 (kind/pprint my-frame))


(deftest
 t62_l333
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v61_l331)))


(def v64_l346 (sk/layer-type-lookup :histogram))


(deftest t65_l348 (is ((fn [m] (= :bar (:mark m))) v64_l346)))


(def
 v67_l352
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t68_l355
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v67_l352)))


(def v70_l359 (sk/layer-type-lookup :smooth))


(deftest t71_l361 (is ((fn [m] (= :loess (:stat m))) v70_l359)))


(def
 v73_l365
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t74_l370
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v73_l365)))


(def
 v76_l415
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/lay-point))


(deftest
 t77_l418
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v76_l415)))


(def
 v79_l425
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t80_l428
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l425)))


(def v82_l432 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t83_l435
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v82_l432)))


(def
 v85_l460
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v87_l466 (-> scatter-base (sk/lay-smooth {:stat :linear-model})))


(deftest
 t88_l468
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v87_l466)))


(def v90_l474 (-> scatter-base sk/lay-smooth))


(deftest
 t91_l476
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v90_l474)))


(def
 v93_l489
 (def
  scatter-with-regression
  (->
   (sk/frame nil {:x :x, :y :y, :color :group})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Scatter with Regression"}))))


(def
 v95_l497
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t96_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v95_l497)))


(def
 v98_l508
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t99_l513
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v98_l508)))


(def
 v101_l531
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t102_l534
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v101_l531)))


(def
 v104_l540
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t105_l543
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v104_l540)))


(def
 v107_l547
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t108_l550
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v107_l547)))


(def
 v110_l556
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t111_l559
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v110_l556)))


(def
 v113_l565
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:x :sepal-length, :y :sepal-width, :group :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t114_l570
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v113_l565)))


(def
 v116_l589
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t117_l594
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v116_l589)))


(def
 v119_l603
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t120_l608
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v119_l603)))


(def
 v122_l618
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t123_l622
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v122_l618)))


(def
 v125_l627
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t126_l633
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v125_l627)))


(def
 v128_l642
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t129_l648
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v128_l642)))


(def
 v131_l654
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t132_l657
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v131_l654)))


(def
 v134_l661
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width))]))


(deftest
 t135_l667
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v134_l661)))
