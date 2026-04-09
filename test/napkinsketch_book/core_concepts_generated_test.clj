(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v3_l27 (rdatasets/datasets-iris))


(deftest t4_l29 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l27)))


(def
 v6_l41
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l44
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l41)))


(def v9_l53 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t10_l57
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l53)))


(def
 v12_l61
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t13_l67
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l61)))


(def v15_l73 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l76
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l73)))


(def
 v18_l93
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t19_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v18_l93)))


(def
 v21_l105
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm)))


(deftest
 t22_l111
 (is
  ((fn [sk] (and (= 1 (count (:views sk))) (= 2 (count (:layers sk)))))
   v21_l105)))


(def
 v24_l122
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t25_l126
 (is
  ((fn
    [sk]
    (and
     (= 1 (count (:views sk)))
     (= 0 (count (:layers sk)))
     (= 1 (count (:layers (first (:views sk)))))))
   v24_l122)))


(def
 v27_l137
 (def
  two-panel-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v28_l143 (kind/pprint two-panel-sketch))


(deftest
 t29_l145
 (is
  ((fn [sk] (and (= 2 (count (:views sk))) (= 1 (count (:layers sk)))))
   v28_l143)))


(def v30_l148 two-panel-sketch)


(deftest
 t31_l150
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l148)))


(def
 v33_l183
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t34_l188
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v33_l183)))


(def
 v36_l198
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t37_l203
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v36_l198)))


(def
 v39_l208
 (def
  view-scoped
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width {:color :species})
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v40_l214 (kind/pprint view-scoped))


(deftest
 t41_l216
 (is
  ((fn
    [sk]
    (and
     (= {} (:mapping sk))
     (= :species (:color (:mapping (first (:views sk)))))
     (nil? (:color (:mapping (second (:views sk)))))))
   v40_l214)))


(def v42_l222 view-scoped)


(deftest
 t43_l224
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v42_l222)))


(def
 v45_l233
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t46_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l233)))


(def
 v48_l249
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t49_l254
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v48_l249)))


(def
 v51_l286
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l292
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 2 (:lines s)))))
   v51_l286)))


(def
 v54_l298
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t55_l303
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 1 (:lines s)))))
   v54_l298)))


(def
 v57_l325
 (def
  targeted
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-width)
   (sk/lay-histogram :sepal-width)
   (sk/view :sepal-width)
   (sk/lay-density :sepal-width))))


(def v58_l332 (kind/pprint targeted))


(deftest
 t59_l334
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:views sk)))
     (= :histogram (:method (first (:layers (first (:views sk))))))
     (= :density (:method (first (:layers (second (:views sk))))))))
   v58_l332)))


(def v60_l340 targeted)


(deftest
 t61_l342
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v60_l340)))


(def
 v63_l361
 (def
  my-sketch
  (->
   (sk/sketch (rdatasets/datasets-iris) {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Iris"}))))


(def v64_l368 (kind/pprint my-sketch))


(deftest
 t65_l370
 (is
  ((fn
    [sk]
    (and
     (tc/dataset? (:data sk))
     (= :species (:color (:mapping sk)))
     (= 1 (count (:views sk)))
     (= 2 (count (:layers sk)))
     (= "Iris" (:title (:opts sk)))))
   v64_l368)))


(def v66_l378 my-sketch)


(deftest
 t67_l380
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v66_l378)))


(def v69_l393 (sk/method-lookup :histogram))


(deftest t70_l395 (is ((fn [m] (= :bar (:mark m))) v69_l393)))


(def
 v72_l399
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t73_l402
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v72_l399)))


(def v75_l406 (sk/method-lookup :lm))


(deftest t76_l408 (is ((fn [m] (= :lm (:stat m))) v75_l406)))


(def
 v78_l412
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t79_l417
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v78_l412)))


(def
 v81_l434
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t82_l437
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v81_l434)))


(def v84_l441 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t85_l444
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v84_l441)))


(def
 v87_l458
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v89_l464 (-> scatter-base sk/lay-lm))


(deftest
 t90_l466
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v89_l464)))


(def v92_l472 (-> scatter-base sk/lay-loess))


(deftest
 t93_l474
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v92_l472)))


(def
 v95_l487
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t96_l490
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v95_l487)))


(def
 v98_l496
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t99_l499
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v98_l496)))


(def
 v101_l503
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t102_l506
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v101_l503)))


(def
 v104_l512
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t105_l515
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v104_l512)))


(def
 v107_l519
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t108_l524
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v107_l519)))


(def
 v110_l536
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t111_l541
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v110_l536)))


(def
 v113_l545
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.0 6.0 {:alpha 0.1}))))


(deftest
 t114_l550
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v113_l545)))


(def
 v116_l560
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t117_l564
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v116_l560)))


(def
 v119_l569
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t120_l575
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v119_l569)))


(def
 v122_l584
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t123_l590
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v122_l584)))


(def
 v125_l596
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t126_l599
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v125_l596)))


(def v128_l603 (def cols [:sepal-length :sepal-width :petal-length]))


(def
 v129_l605
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols))
  sk/lay-point))


(deftest
 t130_l609
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v129_l605)))
