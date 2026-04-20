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
   :ci-lo [8.0 12.0 9.5 15.5],
   :ci-hi [12.0 18.0 14.5 20.5]}))


(def v7_l39 (kind/doc #'sk/sketch))


(def
 v9_l43
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t10_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l43)))


(def v11_l51 (kind/doc #'sk/with-data))


(def
 v13_l56
 (def
  scatter-template
  (-> (sk/sketch) (sk/view :x :y {:color :group}) sk/lay-point)))


(def v14_l61 (-> scatter-template (sk/with-data tiny)))


(deftest
 t15_l64
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v14_l61)))


(def v16_l66 (kind/doc #'sk/view))


(def
 v18_l70
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t19_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v18_l70)))


(def
 v21_l79
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t22_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v21_l79)))


(def
 v24_l88
 (->
  (rdatasets/datasets-iris)
  (sk/view [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (sk/lay-point {:color :species})))


(deftest
 t25_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v24_l88)))


(def
 v27_l99
 (->
  (rdatasets/datasets-iris)
  (sk/view {:x :sepal-length, :y :sepal-width})
  sk/lay-point))


(deftest
 t28_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l99)))


(def v29_l107 (kind/doc #'sk/cross))


(def v30_l109 (sk/cross [:a :b] [1 2 3]))


(deftest
 t31_l111
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v30_l109)))


(def
 v32_l113
 (->
  (rdatasets/datasets-iris)
  (sk/view
   (sk/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))
  (sk/lay-point {:color :species})))


(deftest
 t33_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v32_l113)))


(def
 v35_l124
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t36_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v35_l124)))


(def v38_l132 (kind/doc #'sk/lay))


(def
 v40_l140
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay :point)))


(deftest
 t41_l144
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v40_l140)))


(def v42_l146 (kind/doc #'sk/lay-point))


(def
 v43_l148
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t44_l151
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v43_l148)))


(def v45_l154 (kind/doc #'sk/lay-line))


(def
 v46_l156
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__83115#] (Math/sin (* p1__83115# 0.3))) (range 30))}))


(def v47_l159 (-> wave (sk/lay-line :x :y)))


(deftest
 t48_l162
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v47_l159)))


(def v49_l165 (kind/doc #'sk/lay-histogram))


(def
 v50_l167
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t51_l170
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v50_l167)))


(def v52_l173 (kind/doc #'sk/lay-bar))


(def v53_l175 (-> (rdatasets/datasets-iris) (sk/lay-bar :species)))


(deftest
 t54_l178
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v53_l175)))


(def v55_l181 (kind/doc #'sk/lay-stacked-bar))


(def
 v56_l183
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t57_l186
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v56_l183)))


(def v58_l189 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v59_l191
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t60_l194
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v59_l191)))


(def v61_l197 (kind/doc #'sk/lay-value-bar))


(def v62_l199 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t63_l202
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v62_l199)))


(def v64_l205 (kind/doc #'sk/lay-lm))


(def
 v65_l207
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/lay-lm))


(deftest
 t66_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v65_l207)))


(def v67_l215 (kind/doc #'sk/lay-loess))


(def
 v68_l217
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__83116#]
      (+
       (Math/sin (* p1__83116# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v69_l222 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t70_l226
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v69_l222)))


(def v71_l230 (kind/doc #'sk/lay-density))


(def
 v72_l232
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t73_l235
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v72_l232)))


(def v74_l238 (kind/doc #'sk/lay-area))


(def v75_l240 (-> wave (sk/lay-area :x :y)))


(deftest
 t76_l243
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v75_l240)))


(def v77_l246 (kind/doc #'sk/lay-stacked-area))


(def
 v78_l248
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
 t79_l255
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v78_l248)))


(def v80_l258 (kind/doc #'sk/lay-text))


(def
 v81_l260
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t82_l263
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v81_l260)))


(def v83_l266 (kind/doc #'sk/lay-label))


(def
 v84_l268
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t85_l272
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v84_l268)))


(def v86_l276 (kind/doc #'sk/lay-boxplot))


(def
 v87_l278
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t88_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v87_l278)))


(def v89_l285 (kind/doc #'sk/lay-violin))


(def
 v90_l287
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t91_l290
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v90_l287)))


(def v92_l293 (kind/doc #'sk/lay-errorbar))


(def
 v93_l295
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci-lo, :ymax :ci-hi})))


(deftest
 t94_l299
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v93_l295)))


(def v95_l303 (kind/doc #'sk/lay-lollipop))


(def v96_l305 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t97_l308
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v96_l305)))


(def v98_l312 (kind/doc #'sk/lay-tile))


(def
 v99_l314
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t100_l317
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v99_l314)))


(def v101_l320 (kind/doc #'sk/lay-density2d))


(def
 v102_l322
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)))


(deftest
 t103_l325
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v102_l322)))


(def v104_l328 (kind/doc #'sk/lay-contour))


(def
 v105_l330
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t106_l333
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v105_l330)))


(def v107_l336 (kind/doc #'sk/lay-ridgeline))


(def
 v108_l338
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t109_l341
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v108_l338)))


(def v110_l344 (kind/doc #'sk/lay-rug))


(def
 v111_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t112_l350
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v111_l346)))


(def v113_l353 (kind/doc #'sk/lay-step))


(def v114_l355 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t115_l359
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v114_l355)))


(def v116_l363 (kind/doc #'sk/lay-summary))


(def
 v117_l365
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t118_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v117_l365)))


(def v120_l380 (kind/doc #'sk/annotate))


(def
 v122_l386
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-v 6.0))))


(deftest
 t123_l390
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v122_l386)))


(def v124_l394 (kind/doc #'sk/rule-v))


(def
 v125_l396
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-v 6.0))))


(deftest
 t126_l399
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v125_l396)))


(def v127_l403 (kind/doc #'sk/rule-h))


(def
 v128_l405
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t129_l408
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v128_l405)))


(def v130_l412 (kind/doc #'sk/band-v))


(def
 v131_l414
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-v 5.5 6.5))))


(deftest
 t132_l417
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v131_l414)))


(def v133_l420 (kind/doc #'sk/band-h))


(def
 v134_l422
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-h 2.5 3.5))))


(deftest
 t135_l425
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v134_l422)))


(def v137_l430 (kind/doc #'sk/coord))


(def
 v139_l434
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t140_l437
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v139_l434)))


(def
 v142_l442
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t143_l445
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v142_l442)))


(def v144_l448 (kind/doc #'sk/scale))


(def
 v146_l452
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t147_l455
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v146_l452)))


(def
 v149_l460
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t150_l463
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v149_l460)))


(def v152_l468 (kind/doc #'sk/facet))


(def
 v153_l470
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t154_l474
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v153_l470)))


(def v155_l478 (kind/doc #'sk/facet-grid))


(def
 v156_l480
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t157_l484
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v156_l480)))


(def v159_l490 (kind/doc #'sk/arrange))


(def
 v160_l492
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


(deftest t161_l500 (is ((fn [v] (= :div (first v))) v160_l492)))


(def v163_l504 (kind/doc #'sk/plot))


(def v165_l509 (-> tiny (sk/lay-point :x :y)))


(deftest
 t166_l512
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v165_l509)))


(def v167_l515 (kind/doc #'sk/options))


(def
 v169_l519
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t170_l523
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v169_l519)))


(def v172_l529 (kind/doc #'sk/sketch?))


(def v174_l533 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t175_l535 (is (true? v174_l533)))


(def v176_l537 (kind/doc #'sk/plan?))


(def v178_l541 (sk/plan? (sk/plan (sk/lay-point tiny :x :y))))


(deftest t179_l543 (is (true? v178_l541)))


(def v180_l545 (kind/doc #'sk/layer?))


(def
 v182_l549
 (sk/layer?
  (first
   (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y))))))))


(deftest t183_l551 (is (true? v182_l549)))


(def v184_l553 (kind/doc #'sk/method?))


(def v186_l557 (sk/method? (sk/method-lookup :point)))


(deftest t187_l559 (is (true? v186_l557)))


(def v189_l563 (kind/doc #'sk/draft))


(def
 v191_l569
 (sk/draft
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t192_l572
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v191_l569)))


(def v193_l576 (kind/doc #'sk/plan))


(def v195_l580 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v196_l584 plan1)


(deftest
 t197_l586
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v196_l584)))


(def v198_l589 (kind/doc #'sk/svg-summary))


(def
 v199_l591
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t200_l594
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v199_l591)))


(def v201_l597 (kind/doc #'sk/valid-plan?))


(def v202_l599 (sk/valid-plan? plan1))


(deftest t203_l601 (is (true? v202_l599)))


(def v204_l603 (kind/doc #'sk/explain-plan))


(def v205_l605 (sk/explain-plan plan1))


(deftest t206_l607 (is (nil? v205_l605)))


(def v208_l611 (kind/doc #'sk/plan->membrane))


(def v209_l613 (def m1 (sk/plan->membrane plan1)))


(def v210_l615 (vector? m1))


(deftest t211_l617 (is (true? v210_l615)))


(def v212_l619 (kind/doc #'sk/membrane->figure))


(def
 v213_l621
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t214_l625 (is ((fn [v] (= :svg v)) v213_l621)))


(def v215_l627 (kind/doc #'sk/plan->figure))


(def v216_l629 (first (sk/plan->figure plan1 :svg {})))


(deftest t217_l631 (is ((fn [v] (= :svg v)) v216_l629)))


(def v219_l635 (kind/doc #'sk/config))


(def v220_l637 (sk/config))


(deftest t221_l639 (is ((fn [m] (map? m)) v220_l637)))


(def v222_l641 (kind/doc #'sk/set-config!))


(def v223_l643 (kind/doc #'sk/with-config))


(def
 v224_l645
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t225_l648 (is ((fn [p] (= :pastel1 p)) v224_l645)))


(def v227_l654 (kind/doc #'sk/config-key-docs))


(def v228_l656 (count sk/config-key-docs))


(deftest t229_l658 (is ((fn [n] (= 36 n)) v228_l656)))


(def v230_l660 (kind/doc #'sk/plot-option-docs))


(def v231_l662 (count sk/plot-option-docs))


(deftest t232_l664 (is ((fn [n] (= 11 n)) v231_l662)))


(def v233_l666 (kind/doc #'sk/layer-option-docs))


(def v234_l668 (count sk/layer-option-docs))


(deftest t235_l670 (is ((fn [n] (pos? n)) v234_l668)))


(def v237_l674 (kind/doc #'sk/method-lookup))


(def v238_l676 (sk/method-lookup :lm))


(deftest
 t239_l678
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v238_l676)))


(def v240_l681 (kind/doc #'sk/registered-methods))


(def v241_l683 (count (sk/registered-methods)))


(deftest t242_l685 (is ((fn [n] (= 25 n)) v241_l683)))


(def v244_l691 (kind/doc #'sk/stat-doc))


(def v245_l693 (sk/stat-doc :lm))


(deftest t246_l695 (is ((fn [s] (string? s)) v245_l693)))


(def v247_l697 (kind/doc #'sk/mark-doc))


(def v248_l699 (sk/mark-doc :point))


(deftest t249_l701 (is ((fn [s] (string? s)) v248_l699)))


(def v250_l703 (kind/doc #'sk/position-doc))


(def v251_l705 (sk/position-doc :dodge))


(deftest t252_l707 (is ((fn [s] (string? s)) v251_l705)))


(def v253_l709 (kind/doc #'sk/scale-doc))


(def v254_l711 (sk/scale-doc :linear))


(deftest t255_l713 (is ((fn [s] (string? s)) v254_l711)))


(def v256_l715 (kind/doc #'sk/coord-doc))


(def v257_l717 (sk/coord-doc :cartesian))


(deftest t258_l719 (is ((fn [s] (string? s)) v257_l717)))


(def v259_l721 (kind/doc #'sk/membrane-mark-doc))


(def v260_l723 (sk/membrane-mark-doc :point))


(deftest t261_l725 (is ((fn [s] (string? s)) v260_l723)))


(def v263_l729 (kind/doc #'sk/save))


(def
 v265_l733
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


(deftest t266_l740 (is (true? v265_l733)))


(def v267_l742 (kind/doc #'sk/save-png))


(def
 v269_l747
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (sk/save-png
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   path)
  (.exists (java.io.File. path))))


(deftest t270_l753 (is (true? v269_l747)))
