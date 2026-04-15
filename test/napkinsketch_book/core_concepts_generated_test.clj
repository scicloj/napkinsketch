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
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


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
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm)))


(deftest
 t22_l117
 (is
  ((fn [sk] (and (= 1 (count (:views sk))) (= 2 (count (:layers sk)))))
   v21_l111)))


(def
 v24_l129
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t25_l132
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v24_l129)))


(def
 v26_l134
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t27_l138
 (is
  ((fn
    [sk]
    (and
     (= 1 (count (:views sk)))
     (= 0 (count (:layers sk)))
     (= 1 (count (:layers (first (:views sk)))))))
   v26_l134)))


(def
 v29_l149
 (def
  two-panel-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v30_l155 two-panel-sketch)


(deftest
 t31_l157
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l155)))


(def v32_l159 (kind/pprint two-panel-sketch))


(deftest
 t33_l161
 (is
  ((fn [sk] (and (= 2 (count (:views sk))) (= 1 (count (:layers sk)))))
   v32_l159)))


(def
 v35_l187
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t36_l192
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v35_l187)))


(def
 v38_l202
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t39_l207
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v38_l202)))


(def
 v41_l212
 (def
  view-scoped
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width {:color :species})
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v42_l218 view-scoped)


(deftest
 t43_l220
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v42_l218)))


(def v44_l222 (kind/pprint view-scoped))


(deftest
 t45_l224
 (is
  ((fn
    [sk]
    (and
     (= {} (:mapping sk))
     (= :species (:color (:mapping (first (:views sk)))))
     (nil? (:color (:mapping (second (:views sk)))))))
   v44_l222)))


(def
 v47_l237
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t48_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v47_l237)))


(def
 v50_l253
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t51_l258
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v50_l253)))


(def
 v53_l281
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t54_l287
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 2 (:lines s)))))
   v53_l281)))


(def
 v56_l293
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t57_l298
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 1 (:lines s)))))
   v56_l293)))


(def
 v59_l307
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t60_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v59_l307)))


(def
 v62_l321
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__80394#] (= "setosa" (:species p1__80394#))))))


(def
 v63_l325
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__80395#] (= "versicolor" (:species p1__80395#))))))


(def
 v64_l329
 (->
  (sk/sketch (rdatasets/datasets-iris))
  (sk/view :sepal-length :sepal-width {:data setosa})
  (sk/view :sepal-length :sepal-width {:data versicolor})
  sk/lay-point))


(deftest
 t65_l334
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v64_l329)))


(def
 v67_l339
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t68_l343
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v67_l339)))


(def
 v70_l351
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-lm {:data versicolor})))


(deftest
 t71_l356
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v70_l351)))


(def
 v73_l378
 (def
  targeted
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-width)
   (sk/lay-histogram :sepal-width)
   (sk/view :sepal-width)
   (sk/lay-density :sepal-width))))


(def v74_l385 targeted)


(deftest
 t75_l387
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v74_l385)))


(def v76_l389 (kind/pprint targeted))


(deftest
 t77_l391
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:views sk)))
     (= :histogram (:method (first (:layers (first (:views sk))))))
     (= :density (:method (first (:layers (second (:views sk))))))))
   v76_l389)))


(def
 v79_l414
 (def
  my-sketch
  (->
   (sk/sketch (rdatasets/datasets-iris) {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Iris"}))))


(def v80_l421 my-sketch)


(def v81_l423 (kind/pprint my-sketch))


(deftest
 t82_l425
 (is
  ((fn
    [sk]
    (and
     (tc/dataset? (:data sk))
     (= :species (:color (:mapping sk)))
     (= 1 (count (:views sk)))
     (= 2 (count (:layers sk)))
     (= "Iris" (:title (:opts sk)))))
   v81_l423)))


(deftest
 t83_l433
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v81_l423)))


(def v85_l446 (sk/method-lookup :histogram))


(deftest t86_l448 (is ((fn [m] (= :bar (:mark m))) v85_l446)))


(def
 v88_l452
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t89_l455
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v88_l452)))


(def v91_l459 (sk/method-lookup :lm))


(deftest t92_l461 (is ((fn [m] (= :lm (:stat m))) v91_l459)))


(def
 v94_l465
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t95_l470
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v94_l465)))


(def
 v97_l487
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t98_l490
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v97_l487)))


(def v100_l494 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t101_l497
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v100_l494)))


(def
 v103_l511
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v105_l517 (-> scatter-base sk/lay-lm))


(deftest
 t106_l519
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v105_l517)))


(def v108_l525 (-> scatter-base sk/lay-loess))


(deftest
 t109_l527
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l525)))


(def
 v111_l540
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t112_l543
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v111_l540)))


(def
 v114_l549
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t115_l552
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v114_l549)))


(def
 v117_l556
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t118_l559
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v117_l556)))


(def
 v120_l565
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t121_l568
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v120_l565)))


(def
 v123_l574
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t124_l579
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v123_l574)))


(def
 v126_l591
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t127_l596
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v126_l591)))


(def
 v129_l600
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.0 6.0 {:alpha 0.1}))))


(deftest
 t130_l605
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v129_l600)))


(def
 v132_l615
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t133_l619
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v132_l615)))


(def
 v135_l624
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t136_l630
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v135_l624)))


(def
 v138_l639
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t139_l645
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v138_l639)))


(def
 v141_l651
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t142_l654
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v141_l651)))


(def
 v144_l659
 (->
  (rdatasets/datasets-iris)
  (sk/view [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  sk/lay-point))


(deftest
 t145_l664
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v144_l659)))


(def v147_l670 (def cols [:sepal-length :sepal-width :petal-length]))


(def v148_l672 (sk/cross cols cols))


(def
 v150_l676
 (-> (rdatasets/datasets-iris) (sk/view (sk/cross cols cols))))


(deftest
 t151_l679
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v150_l676)))
