(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]}))


(def
 v4_l29
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def
 v5_l32
 (def
  measurements
  {:treatment ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def v7_l39 (kind/doc #'sk/view))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l43)))


(def
 v12_l52
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t13_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l52)))


(def
 v15_l61
 (->
  (rdatasets/datasets-iris)
  (sk/view [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (sk/lay-point {:color :species})))


(deftest
 t16_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v15_l61)))


(def
 v18_l72
 (->
  (rdatasets/datasets-iris)
  (sk/view {:x :sepal-length, :y :sepal-width})
  sk/lay-point))


(deftest
 t19_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v18_l72)))


(def v20_l80 (kind/doc #'sk/annotate))


(def
 v22_l84
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t23_l89
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l84)))


(def v25_l95 (kind/doc #'sk/lay-point))


(def
 v26_l97
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t27_l100
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v26_l97)))


(def v28_l103 (kind/doc #'sk/lay-line))


(def
 v29_l105
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__84022#] (Math/sin (* p1__84022# 0.3))) (range 30))}))


(def v30_l108 (-> wave (sk/lay-line :x :y)))


(deftest
 t31_l111
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v30_l108)))


(def v32_l114 (kind/doc #'sk/lay-histogram))


(def
 v33_l116
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t34_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l116)))


(def v35_l122 (kind/doc #'sk/lay-bar))


(def v36_l124 (-> (rdatasets/datasets-iris) (sk/lay-bar :species)))


(deftest
 t37_l127
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v36_l124)))


(def v38_l130 (kind/doc #'sk/lay-stacked-bar))


(def
 v39_l132
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t40_l135
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v39_l132)))


(def v41_l138 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v42_l140
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t43_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l140)))


(def v44_l146 (kind/doc #'sk/lay-value-bar))


(def v45_l148 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t46_l151
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v45_l148)))


(def v47_l154 (kind/doc #'sk/lay-lm))


(def
 v48_l156
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/lay-lm))


(deftest
 t49_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l156)))


(def v50_l164 (kind/doc #'sk/lay-loess))


(def
 v51_l166
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__84023#]
      (+
       (Math/sin (* p1__84023# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v52_l171 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t53_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v52_l171)))


(def v54_l179 (kind/doc #'sk/lay-density))


(def
 v55_l181
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t56_l184
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v55_l181)))


(def v57_l187 (kind/doc #'sk/lay-area))


(def v58_l189 (-> wave (sk/lay-area :x :y)))


(deftest
 t59_l192
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l189)))


(def v60_l195 (kind/doc #'sk/lay-stacked-area))


(def
 v61_l197
 (->
  {:x (concat (range 10) (range 10) (range 10)),
   :y
   (concat
    [1 2 3 4 5 4 3 2 1 0]
    [2 2 2 3 3 3 2 2 2 2]
    [1 1 1 1 2 2 2 1 1 1]),
   :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
  (sk/lay-stacked-area :x :y {:color :group})))


(deftest
 t62_l204
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v61_l197)))


(def v63_l207 (kind/doc #'sk/lay-text))


(def
 v64_l209
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t65_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v64_l209)))


(def v66_l215 (kind/doc #'sk/lay-label))


(def
 v67_l217
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t68_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v67_l217)))


(def v69_l224 (kind/doc #'sk/lay-boxplot))


(def
 v70_l226
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t71_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v70_l226)))


(def v72_l233 (kind/doc #'sk/lay-violin))


(def
 v73_l235
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t74_l238
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v73_l235)))


(def v75_l241 (kind/doc #'sk/lay-errorbar))


(def
 v76_l243
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t77_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v76_l243)))


(def v78_l251 (kind/doc #'sk/lay-lollipop))


(def v79_l253 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t80_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v79_l253)))


(def v81_l260 (kind/doc #'sk/lay-tile))


(def
 v82_l262
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t83_l265
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v82_l262)))


(def v84_l268 (kind/doc #'sk/lay-density2d))


(def
 v85_l270
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)))


(deftest
 t86_l273
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v85_l270)))


(def v87_l276 (kind/doc #'sk/lay-contour))


(def
 v88_l278
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t89_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v88_l278)))


(def v90_l284 (kind/doc #'sk/lay-ridgeline))


(def
 v91_l286
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t92_l289
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v91_l286)))


(def v93_l292 (kind/doc #'sk/lay-rug))


(def
 v94_l294
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t95_l298
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v94_l294)))


(def v96_l301 (kind/doc #'sk/lay-step))


(def v97_l303 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t98_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v97_l303)))


(def v99_l311 (kind/doc #'sk/lay-summary))


(def
 v100_l313
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t101_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v100_l313)))


(def v103_l322 (kind/doc #'sk/plot))


(def v105_l327 (-> tiny (sk/lay-point :x :y)))


(deftest
 t106_l330
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v105_l327)))


(def v107_l333 (kind/doc #'sk/options))


(def
 v109_l337
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t110_l341
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v109_l337)))


(def v111_l345 (kind/doc #'sk/sketch?))


(def v113_l349 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t114_l351 (is (true? v113_l349)))


(def v115_l353 (kind/doc #'sk/sketch))


(def
 v117_l357
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t118_l361
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v117_l357)))


(def v119_l365 (kind/doc #'sk/plan))


(def
 v121_l370
 (let
  [sk (-> tiny (sk/lay-point :x :y) sk/lay-lm)]
  [(count (:views sk)) (count (:layers sk))]))


(deftest
 t122_l373
 (is
  ((fn [[views globals]] (and (= 1 views) (= 1 globals))) v121_l370)))


(def v123_l375 (kind/doc #'sk/plan))


(def v125_l379 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v126_l383 plan1)


(deftest
 t127_l385
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v126_l383)))


(def v129_l390 (kind/doc #'sk/plan->membrane))


(def v130_l392 (def m1 (sk/plan->membrane plan1)))


(def v131_l394 (vector? m1))


(deftest t132_l396 (is (true? v131_l394)))


(def v133_l398 (kind/doc #'sk/membrane->figure))


(def
 v134_l400
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t135_l404 (is ((fn [v] (= :svg v)) v134_l400)))


(def v136_l406 (kind/doc #'sk/plan->figure))


(def v137_l408 (first (sk/plan->figure plan1 :svg {})))


(deftest t138_l410 (is ((fn [v] (= :svg v)) v137_l408)))


(def v140_l414 (kind/doc #'sk/coord))


(def
 v142_l418
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t143_l421
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v142_l418)))


(def
 v145_l426
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t146_l429
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v145_l426)))


(def v147_l432 (kind/doc #'sk/scale))


(def
 v149_l436
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t150_l439
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v149_l436)))


(def
 v152_l444
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t153_l447
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l444)))


(def v155_l451 (kind/doc #'sk/rule-v))


(def
 v156_l453
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-v 6.0))))


(deftest
 t157_l456
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v156_l453)))


(def v158_l460 (kind/doc #'sk/rule-h))


(def
 v159_l462
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t160_l465
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v159_l462)))


(def v161_l469 (kind/doc #'sk/band-v))


(def
 v162_l471
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-v 5.5 6.5))))


(deftest
 t163_l474
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v162_l471)))


(def v164_l477 (kind/doc #'sk/band-h))


(def
 v165_l479
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-h 2.5 3.5))))


(deftest
 t166_l482
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v165_l479)))


(def v168_l487 (kind/doc #'sk/cross))


(def v169_l489 (sk/cross [:a :b] [1 2 3]))


(deftest
 t170_l491
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v169_l489)))


(def
 v171_l493
 (->
  (rdatasets/datasets-iris)
  (sk/view
   (sk/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))
  (sk/lay-point {:color :species})))


(deftest
 t172_l498
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v171_l493)))


(def
 v174_l504
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t175_l506
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v174_l504)))


(def v177_l512 (kind/doc #'sk/facet))


(def
 v178_l514
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t179_l518
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v178_l514)))


(def v180_l522 (kind/doc #'sk/facet-grid))


(def
 v181_l524
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t182_l528
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v181_l524)))


(def v184_l534 (kind/doc #'sk/svg-summary))


(def
 v185_l536
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t186_l539
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v185_l536)))


(def v187_l542 (kind/doc #'sk/valid-plan?))


(def v188_l544 (sk/valid-plan? plan1))


(deftest t189_l546 (is (true? v188_l544)))


(def v190_l548 (kind/doc #'sk/explain-plan))


(def v191_l550 (sk/explain-plan plan1))


(deftest t192_l552 (is (nil? v191_l550)))


(def v194_l556 (kind/doc #'sk/config))


(def v195_l558 (sk/config))


(deftest t196_l560 (is ((fn [m] (map? m)) v195_l558)))


(def v197_l562 (kind/doc #'sk/set-config!))


(def v198_l564 (kind/doc #'sk/with-config))


(def
 v199_l566
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t200_l569 (is ((fn [p] (= :pastel1 p)) v199_l566)))


(def v202_l575 (kind/doc #'sk/config-key-docs))


(def v203_l577 (count sk/config-key-docs))


(deftest t204_l579 (is ((fn [n] (= 36 n)) v203_l577)))


(def v205_l581 (kind/doc #'sk/plot-option-docs))


(def v206_l583 (count sk/plot-option-docs))


(deftest t207_l585 (is ((fn [n] (= 11 n)) v206_l583)))


(def v208_l587 (kind/doc #'sk/layer-option-docs))


(def v209_l589 (count sk/layer-option-docs))


(deftest t210_l591 (is ((fn [n] (pos? n)) v209_l589)))


(def v212_l595 (kind/doc #'sk/method-lookup))


(def v213_l597 (sk/method-lookup :lm))


(deftest
 t214_l599
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v213_l597)))


(def v215_l602 (kind/doc #'sk/registered-methods))


(def v216_l604 (count (sk/registered-methods)))


(deftest t217_l606 (is ((fn [n] (= 25 n)) v216_l604)))


(def v219_l612 (kind/doc #'sk/stat-doc))


(def v220_l614 (sk/stat-doc :lm))


(deftest t221_l616 (is ((fn [s] (string? s)) v220_l614)))


(def v222_l618 (kind/doc #'sk/mark-doc))


(def v223_l620 (sk/mark-doc :point))


(deftest t224_l622 (is ((fn [s] (string? s)) v223_l620)))


(def v225_l624 (kind/doc #'sk/position-doc))


(def v226_l626 (sk/position-doc :dodge))


(deftest t227_l628 (is ((fn [s] (string? s)) v226_l626)))


(def v228_l630 (kind/doc #'sk/scale-doc))


(def v229_l632 (sk/scale-doc :linear))


(deftest t230_l634 (is ((fn [s] (string? s)) v229_l632)))


(def v231_l636 (kind/doc #'sk/coord-doc))


(def v232_l638 (sk/coord-doc :cartesian))


(deftest t233_l640 (is ((fn [s] (string? s)) v232_l638)))


(def v234_l642 (kind/doc #'sk/membrane-mark-doc))


(def v235_l644 (sk/membrane-mark-doc :point))


(deftest t236_l646 (is ((fn [s] (string? s)) v235_l644)))


(def v238_l650 (kind/doc #'sk/arrange))


(def
 v239_l652
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:width 250, :height 200}))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width {:color :species})
    (sk/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t240_l658 (is ((fn [v] (= :div (first v))) v239_l652)))


(def v242_l661 (kind/doc #'sk/save))


(def
 v244_l665
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   path
   {:title "Iris Export"})
  (.contains (slurp path) "<svg")))


(deftest t245_l671 (is (true? v244_l665)))
