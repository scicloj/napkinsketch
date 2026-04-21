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
   (fn* [p1__79038#] (= "setosa" (:species p1__79038#))))))


(def
 v63_l325
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__79039#] (= "versicolor" (:species p1__79039#))))))


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
 v97_l518
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/view))


(deftest
 t98_l521
 (is
  ((fn
    [v]
    (and
     (= 4 (:points (sk/svg-summary v)))
     (= {:x :height, :y :weight} (get-in v [:views 0 :mapping]))))
   v97_l518)))


(def
 v100_l532
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t101_l535
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v100_l532)))


(def v103_l539 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t104_l542
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v103_l539)))


(def
 v106_l567
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v108_l573 (-> scatter-base sk/lay-lm))


(deftest
 t109_l575
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l573)))


(def v111_l581 (-> scatter-base sk/lay-loess))


(deftest
 t112_l583
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v111_l581)))


(def
 v114_l596
 (def
  scatter-with-regression
  (->
   (sk/sketch)
   (sk/view :x :y {:color :group})
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Scatter with Regression"}))))


(def
 v116_l605
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t117_l610
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v116_l605)))


(def
 v119_l616
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t120_l621
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v119_l616)))


(def
 v122_l639
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t123_l642
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v122_l639)))


(def
 v125_l648
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t126_l651
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v125_l648)))


(def
 v128_l655
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t129_l658
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v128_l655)))


(def
 v131_l664
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t132_l667
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v131_l664)))


(def
 v134_l673
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t135_l678
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v134_l673)))


(def
 v137_l697
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t138_l702
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v137_l697)))


(def
 v140_l711
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t141_l716
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v140_l711)))


(def
 v143_l726
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t144_l730
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v143_l726)))


(def
 v146_l735
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t147_l741
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v146_l735)))


(def
 v149_l750
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t150_l756
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v149_l750)))


(def
 v152_l762
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t153_l765
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v152_l762)))


(def
 v155_l770
 (->
  (rdatasets/datasets-iris)
  (sk/view [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  sk/lay-point))


(deftest
 t156_l775
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v155_l770)))


(def v158_l781 (def cols [:sepal-length :sepal-width :petal-length]))


(def v159_l783 (sk/cross cols cols))


(def
 v161_l787
 (-> (rdatasets/datasets-iris) (sk/view (sk/cross cols cols))))


(deftest
 t162_l790
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v161_l787)))
