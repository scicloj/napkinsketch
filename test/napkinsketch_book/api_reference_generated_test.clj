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
   (map
    (fn* [p1__199311#] (Math/sin (* p1__199311# 0.3)))
    (range 30))}))


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
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__199312#]
      (+
       (Math/sin (* p1__199312# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (sk/lay-point :x :y)
  (sk/lay-loess {:bandwidth 0.2})))


(deftest
 t69_l226
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v68_l217)))


(def v70_l230 (kind/doc #'sk/lay-density))


(def
 v71_l232
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t72_l235
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v71_l232)))


(def v73_l238 (kind/doc #'sk/lay-area))


(def v74_l240 (-> wave (sk/lay-area :x :y)))


(deftest
 t75_l243
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v74_l240)))


(def v76_l246 (kind/doc #'sk/lay-stacked-area))


(def
 v77_l248
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
 t78_l255
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v77_l248)))


(def v79_l258 (kind/doc #'sk/lay-text))


(def
 v80_l260
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t81_l263
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v80_l260)))


(def v82_l266 (kind/doc #'sk/lay-label))


(def
 v83_l268
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t84_l272
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v83_l268)))


(def v85_l276 (kind/doc #'sk/lay-boxplot))


(def
 v86_l278
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t87_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v86_l278)))


(def v88_l285 (kind/doc #'sk/lay-violin))


(def
 v89_l287
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t90_l290
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v89_l287)))


(def v91_l293 (kind/doc #'sk/lay-errorbar))


(def
 v92_l295
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci-lo, :ymax :ci-hi})))


(deftest
 t93_l299
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v92_l295)))


(def v94_l303 (kind/doc #'sk/lay-lollipop))


(def v95_l305 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t96_l308
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v95_l305)))


(def v97_l312 (kind/doc #'sk/lay-tile))


(def
 v98_l314
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t99_l317
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v98_l314)))


(def v100_l320 (kind/doc #'sk/lay-density2d))


(def
 v101_l322
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)))


(deftest
 t102_l325
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v101_l322)))


(def v103_l328 (kind/doc #'sk/lay-contour))


(def
 v104_l330
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t105_l333
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v104_l330)))


(def v106_l336 (kind/doc #'sk/lay-ridgeline))


(def
 v107_l338
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t108_l341
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v107_l338)))


(def v109_l344 (kind/doc #'sk/lay-rug))


(def
 v110_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t111_l350
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v110_l346)))


(def v112_l353 (kind/doc #'sk/lay-step))


(def v113_l355 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t114_l359
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v113_l355)))


(def v115_l363 (kind/doc #'sk/lay-summary))


(def
 v116_l365
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t117_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v116_l365)))


(def v119_l382 (kind/doc #'sk/lay-rule-v))


(def
 v120_l384
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-v {:x-intercept 6.0})))


(deftest
 t121_l388
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v120_l384)))


(def v122_l392 (kind/doc #'sk/lay-rule-h))


(def
 v123_l394
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t124_l398
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v123_l394)))


(def v125_l402 (kind/doc #'sk/lay-band-v))


(def
 v126_l404
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t127_l408
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v126_l404)))


(def v128_l411 (kind/doc #'sk/lay-band-h))


(def
 v129_l413
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t130_l417
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v129_l413)))


(def v132_l422 (kind/doc #'sk/coord))


(def
 v134_l426
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t135_l429
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v134_l426)))


(def
 v137_l434
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t138_l437
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v137_l434)))


(def v139_l440 (kind/doc #'sk/scale))


(def
 v141_l444
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t142_l447
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v141_l444)))


(def
 v144_l452
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t145_l455
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v144_l452)))


(def v147_l460 (kind/doc #'sk/facet))


(def
 v148_l462
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t149_l466
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v148_l462)))


(def v150_l470 (kind/doc #'sk/facet-grid))


(def
 v151_l472
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t152_l476
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v151_l472)))


(def v154_l482 (kind/doc #'sk/arrange))


(def
 v155_l484
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


(deftest t156_l492 (is ((fn [v] (= :div (first v))) v155_l484)))


(def v158_l496 (kind/doc #'sk/plot))


(def v160_l501 (-> tiny (sk/lay-point :x :y)))


(deftest
 t161_l504
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v160_l501)))


(def v162_l507 (kind/doc #'sk/options))


(def
 v164_l511
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t165_l515
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v164_l511)))


(def v167_l521 (kind/doc #'sk/sketch?))


(def v169_l525 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t170_l527 (is (true? v169_l525)))


(def v171_l529 (kind/doc #'sk/plan?))


(def v173_l533 (sk/plan? (sk/plan (sk/lay-point tiny :x :y))))


(deftest t174_l535 (is (true? v173_l533)))


(def v175_l537 (kind/doc #'sk/layer?))


(def
 v177_l541
 (sk/layer?
  (first
   (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y))))))))


(deftest t178_l543 (is (true? v177_l541)))


(def v179_l545 (kind/doc #'sk/layer-type?))


(def v181_l549 (sk/layer-type? (sk/layer-type-lookup :point)))


(deftest t182_l551 (is (true? v181_l549)))


(def v184_l555 (kind/doc #'sk/draft))


(def
 v186_l561
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/draft
  kind/pprint))


(deftest
 t187_l566
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v186_l561)))


(def v188_l570 (kind/doc #'sk/plan))


(def v190_l574 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v191_l578 plan1)


(deftest
 t192_l580
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v191_l578)))


(def v193_l583 (kind/doc #'sk/svg-summary))


(def
 v194_l585
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t195_l588
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v194_l585)))


(def v196_l591 (kind/doc #'sk/valid-plan?))


(def v197_l593 (sk/valid-plan? plan1))


(deftest t198_l595 (is (true? v197_l593)))


(def v199_l597 (kind/doc #'sk/explain-plan))


(def v200_l599 (sk/explain-plan plan1))


(deftest t201_l601 (is (nil? v200_l599)))


(def v203_l605 (kind/doc #'sk/plan->membrane))


(def v204_l607 (def m1 (sk/plan->membrane plan1)))


(def v205_l609 (vector? m1))


(deftest t206_l611 (is (true? v205_l609)))


(def v207_l613 (kind/doc #'sk/membrane->plot))


(def
 v208_l615
 (first
  (sk/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t209_l619 (is ((fn [v] (= :svg v)) v208_l615)))


(def v210_l621 (kind/doc #'sk/plan->plot))


(def v211_l623 (first (sk/plan->plot plan1 :svg {})))


(deftest t212_l625 (is ((fn [v] (= :svg v)) v211_l623)))


(def v214_l629 (kind/doc #'sk/config))


(def v215_l631 (sk/config))


(deftest t216_l633 (is ((fn [m] (map? m)) v215_l631)))


(def v217_l635 (kind/doc #'sk/set-config!))


(def v218_l637 (kind/doc #'sk/with-config))


(def
 v219_l639
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t220_l642 (is ((fn [p] (= :pastel1 p)) v219_l639)))


(def v222_l648 (kind/doc #'sk/config-key-docs))


(def v223_l650 (count sk/config-key-docs))


(deftest t224_l652 (is ((fn [n] (= 36 n)) v223_l650)))


(def v225_l654 (kind/doc #'sk/plot-option-docs))


(def v226_l656 (count sk/plot-option-docs))


(deftest t227_l658 (is ((fn [n] (= 11 n)) v226_l656)))


(def v228_l660 (kind/doc #'sk/layer-option-docs))


(def v229_l662 (count sk/layer-option-docs))


(deftest t230_l664 (is ((fn [n] (pos? n)) v229_l662)))


(def v232_l668 (kind/doc #'sk/layer-type-lookup))


(def v233_l670 (sk/layer-type-lookup :lm))


(deftest
 t234_l672
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v233_l670)))


(def v235_l675 (kind/doc #'sk/registered-layer-types))


(def v236_l677 (count (sk/registered-layer-types)))


(deftest t237_l679 (is ((fn [n] (= 29 n)) v236_l677)))


(def v239_l685 (kind/doc #'sk/stat-doc))


(def v240_l687 (sk/stat-doc :lm))


(deftest t241_l689 (is ((fn [s] (string? s)) v240_l687)))


(def v242_l691 (kind/doc #'sk/mark-doc))


(def v243_l693 (sk/mark-doc :point))


(deftest t244_l695 (is ((fn [s] (string? s)) v243_l693)))


(def v245_l697 (kind/doc #'sk/position-doc))


(def v246_l699 (sk/position-doc :dodge))


(deftest t247_l701 (is ((fn [s] (string? s)) v246_l699)))


(def v248_l703 (kind/doc #'sk/scale-doc))


(def v249_l705 (sk/scale-doc :linear))


(deftest t250_l707 (is ((fn [s] (string? s)) v249_l705)))


(def v251_l709 (kind/doc #'sk/coord-doc))


(def v252_l711 (sk/coord-doc :cartesian))


(deftest t253_l713 (is ((fn [s] (string? s)) v252_l711)))


(def v254_l715 (kind/doc #'sk/membrane-mark-doc))


(def v255_l717 (sk/membrane-mark-doc :point))


(deftest t256_l719 (is ((fn [s] (string? s)) v255_l717)))


(def v258_l723 (kind/doc #'sk/save))


(def
 v260_l727
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t261_l733 (is (true? v260_l727)))


(def v262_l735 (kind/doc #'sk/save-png))


(def
 v264_l740
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save-png path))
  (.exists (java.io.File. path))))


(deftest t265_l746 (is (true? v264_l740)))
