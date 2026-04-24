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


(def v7_l39 (kind/doc #'sk/frame))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
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


(def
 v12_l53
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l53)))


(def v14_l62 (kind/doc #'sk/with-data))


(def
 v16_l68
 (def
  scatter-template
  (-> (sk/frame nil {:x :x, :y :y, :color :group}) sk/lay-point)))


(def v17_l72 (-> scatter-template (sk/with-data tiny)))


(deftest
 t18_l75
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v17_l72)))


(def
 v20_l80
 (->
  (rdatasets/datasets-iris)
  (sk/frame
   [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (sk/lay-point {:color :species})))


(deftest
 t21_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v20_l80)))


(def
 v23_l91
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:x :sepal-length, :y :sepal-width})
  sk/lay-point))


(deftest
 t24_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v23_l91)))


(def v25_l99 (kind/doc #'sk/cross))


(def v26_l101 (sk/cross [:a :b] [1 2 3]))


(deftest
 t27_l103
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v26_l101)))


(def
 v29_l107
 (->
  (rdatasets/datasets-iris)
  (sk/frame {:color :species})
  (sk/frame
   (sk/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))))


(deftest
 t30_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v29_l107)))


(def
 v32_l118
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t33_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v32_l118)))


(def v35_l126 (kind/doc #'sk/lay))


(def
 v37_l134
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay :point)))


(deftest
 t38_l138
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v37_l134)))


(def v39_l140 (kind/doc #'sk/lay-point))


(def
 v40_l142
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t41_l145
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v40_l142)))


(def v42_l148 (kind/doc #'sk/lay-line))


(def
 v43_l150
 (def
  wave
  {:x (range 30),
   :y
   (map
    (fn* [p1__181122#] (Math/sin (* p1__181122# 0.3)))
    (range 30))}))


(def v44_l153 (-> wave (sk/lay-line :x :y)))


(deftest
 t45_l156
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v44_l153)))


(def v46_l159 (kind/doc #'sk/lay-histogram))


(def
 v47_l161
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t48_l164
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v47_l161)))


(def v49_l167 (kind/doc #'sk/lay-bar))


(def v50_l169 (-> (rdatasets/datasets-iris) (sk/lay-bar :species)))


(deftest
 t51_l172
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v50_l169)))


(def
 v53_l177
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:position :stack, :color :species})))


(deftest
 t54_l180
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v53_l177)))


(def
 v56_l185
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:position :fill, :color :species})))


(deftest
 t57_l188
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v56_l185)))


(def v58_l191 (kind/doc #'sk/lay-value-bar))


(def v59_l193 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t60_l196
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v59_l193)))


(def
 v62_l201
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t63_l205
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v62_l201)))


(def v64_l209 (kind/doc #'sk/lay-smooth))


(def
 v65_l211
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__181123#]
      (+
       (Math/sin (* p1__181123# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (sk/lay-point :x :y)
  (sk/lay-smooth {:bandwidth 0.2})))


(deftest
 t66_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v65_l211)))


(def v67_l224 (kind/doc #'sk/lay-density))


(def
 v68_l226
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t69_l229
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v68_l226)))


(def v70_l232 (kind/doc #'sk/lay-area))


(def v71_l234 (-> wave (sk/lay-area :x :y)))


(deftest
 t72_l237
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v71_l234)))


(def
 v74_l242
 (->
  {:x (concat (range 10) (range 10) (range 10)),
   :y
   (concat
    [1 2 3 4 5 4 3 2 1 0]
    [2 2 2 3 3 3 2 2 2 2]
    [1 1 1 1 2 2 2 1 1 1]),
   :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
  (sk/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t75_l249
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v74_l242)))


(def v76_l252 (kind/doc #'sk/lay-text))


(def
 v77_l254
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t78_l257
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v77_l254)))


(def v79_l260 (kind/doc #'sk/lay-label))


(def
 v80_l262
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t81_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v80_l262)))


(def v82_l270 (kind/doc #'sk/lay-boxplot))


(def
 v83_l272
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t84_l275
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v83_l272)))


(def v85_l279 (kind/doc #'sk/lay-violin))


(def
 v86_l281
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t87_l284
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v86_l281)))


(def v88_l287 (kind/doc #'sk/lay-errorbar))


(def
 v89_l289
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:y-min :ci-lo, :y-max :ci-hi})))


(deftest
 t90_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v89_l289)))


(def v91_l297 (kind/doc #'sk/lay-lollipop))


(def v92_l299 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t93_l302
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v92_l299)))


(def v94_l306 (kind/doc #'sk/lay-tile))


(def
 v95_l308
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t96_l311
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v95_l308)))


(def v97_l314 (kind/doc #'sk/lay-density-2d))


(def
 v98_l316
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t99_l319
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v98_l316)))


(def v100_l322 (kind/doc #'sk/lay-contour))


(def
 v101_l324
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t102_l327
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v101_l324)))


(def v103_l330 (kind/doc #'sk/lay-ridgeline))


(def
 v104_l332
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t105_l335
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v104_l332)))


(def v106_l338 (kind/doc #'sk/lay-rug))


(def
 v107_l340
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t108_l344
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v107_l340)))


(def v109_l347 (kind/doc #'sk/lay-step))


(def v110_l349 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t111_l353
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v110_l349)))


(def v112_l357 (kind/doc #'sk/lay-summary))


(def
 v113_l359
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t114_l362
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v113_l359)))


(def v116_l376 (kind/doc #'sk/lay-rule-v))


(def
 v117_l378
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-v {:x-intercept 6.0})))


(deftest
 t118_l382
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v117_l378)))


(def v119_l386 (kind/doc #'sk/lay-rule-h))


(def
 v120_l388
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t121_l392
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v120_l388)))


(def v122_l396 (kind/doc #'sk/lay-band-v))


(def
 v123_l398
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t124_l402
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v123_l398)))


(def v125_l405 (kind/doc #'sk/lay-band-h))


(def
 v126_l407
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t127_l411
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v126_l407)))


(def v129_l416 (kind/doc #'sk/coord))


(def
 v131_l420
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t132_l423
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v131_l420)))


(def
 v134_l428
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t135_l431
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v134_l428)))


(def v136_l434 (kind/doc #'sk/scale))


(def
 v138_l438
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t139_l441
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v138_l438)))


(def
 v141_l446
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t142_l449
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v141_l446)))


(def v144_l454 (kind/doc #'sk/facet))


(def
 v145_l456
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t146_l460
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v145_l456)))


(def v147_l464 (kind/doc #'sk/facet-grid))


(def
 v148_l466
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t149_l470
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v148_l466)))


(def v151_l476 (kind/doc #'sk/arrange))


(def
 v152_l478
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


(deftest t153_l486 (is ((fn [v] (sk/frame? v)) v152_l478)))


(def v155_l490 (kind/doc #'sk/plot))


(def v157_l495 (-> tiny (sk/lay-point :x :y)))


(deftest
 t158_l498
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v157_l495)))


(def v159_l501 (kind/doc #'sk/options))


(def
 v161_l505
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t162_l509
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v161_l505)))


(def v164_l515 (kind/doc #'sk/frame?))


(def v166_l519 (sk/frame? (-> tiny (sk/frame :x :y) sk/lay-point)))


(deftest t167_l521 (is (true? v166_l519)))


(def v168_l523 (kind/doc #'sk/plan?))


(def v170_l527 (sk/plan? (sk/plan (sk/lay-point tiny :x :y))))


(deftest t171_l529 (is (true? v170_l527)))


(def v172_l531 (kind/doc #'sk/layer?))


(def
 v174_l535
 (sk/layer?
  (first
   (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y))))))))


(deftest t175_l537 (is (true? v174_l535)))


(def v176_l539 (kind/doc #'sk/layer-type?))


(def v178_l543 (sk/layer-type? (sk/layer-type-lookup :point)))


(deftest t179_l545 (is (true? v178_l543)))


(def v181_l549 (kind/doc #'sk/draft))


(def
 v183_l555
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  sk/draft
  kind/pprint))


(deftest
 t184_l561
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v183_l555)))


(def v185_l565 (kind/doc #'sk/plan))


(def v187_l569 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v188_l573 plan1)


(deftest
 t189_l575
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v188_l573)))


(def v190_l578 (kind/doc #'sk/svg-summary))


(def
 v191_l580
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t192_l583
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l580)))


(def v193_l586 (kind/doc #'sk/valid-plan?))


(def v194_l588 (sk/valid-plan? plan1))


(deftest t195_l590 (is (true? v194_l588)))


(def v196_l592 (kind/doc #'sk/explain-plan))


(def v197_l594 (sk/explain-plan plan1))


(deftest t198_l596 (is (nil? v197_l594)))


(def v200_l600 (kind/doc #'sk/plan->membrane))


(def v201_l602 (def m1 (sk/plan->membrane plan1)))


(def v202_l604 (vector? m1))


(deftest t203_l606 (is (true? v202_l604)))


(def v204_l608 (kind/doc #'sk/membrane->plot))


(def
 v205_l610
 (first
  (sk/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t206_l614 (is ((fn [v] (= :svg v)) v205_l610)))


(def v207_l616 (kind/doc #'sk/plan->plot))


(def v208_l618 (first (sk/plan->plot plan1 :svg {})))


(deftest t209_l620 (is ((fn [v] (= :svg v)) v208_l618)))


(def v211_l624 (kind/doc #'sk/config))


(def v212_l626 (sk/config))


(deftest t213_l628 (is ((fn [m] (map? m)) v212_l626)))


(def v214_l630 (kind/doc #'sk/set-config!))


(def v215_l632 (kind/doc #'sk/with-config))


(def
 v216_l634
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t217_l637 (is ((fn [p] (= :pastel1 p)) v216_l634)))


(def v219_l643 (kind/doc #'sk/config-key-docs))


(def v220_l645 (count sk/config-key-docs))


(deftest t221_l647 (is ((fn [n] (= 36 n)) v220_l645)))


(def v222_l649 (kind/doc #'sk/plot-option-docs))


(def v223_l651 (count sk/plot-option-docs))


(deftest t224_l653 (is ((fn [n] (= 11 n)) v223_l651)))


(def v225_l655 (kind/doc #'sk/layer-option-docs))


(def v226_l657 (count sk/layer-option-docs))


(deftest t227_l659 (is ((fn [n] (pos? n)) v226_l657)))


(def v229_l663 (kind/doc #'sk/layer-type-lookup))


(def v230_l665 (sk/layer-type-lookup :smooth))


(deftest
 t231_l667
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v230_l665)))


(def v232_l670 (kind/doc #'sk/registered-layer-types))


(def v233_l672 (count (sk/registered-layer-types)))


(deftest t234_l674 (is ((fn [n] (= 25 n)) v233_l672)))


(def v236_l680 (kind/doc #'sk/stat-doc))


(def v237_l682 (sk/stat-doc :linear-model))


(deftest t238_l684 (is ((fn [s] (string? s)) v237_l682)))


(def v239_l686 (kind/doc #'sk/mark-doc))


(def v240_l688 (sk/mark-doc :point))


(deftest t241_l690 (is ((fn [s] (string? s)) v240_l688)))


(def v242_l692 (kind/doc #'sk/position-doc))


(def v243_l694 (sk/position-doc :dodge))


(deftest t244_l696 (is ((fn [s] (string? s)) v243_l694)))


(def v245_l698 (kind/doc #'sk/scale-doc))


(def v246_l700 (sk/scale-doc :linear))


(deftest t247_l702 (is ((fn [s] (string? s)) v246_l700)))


(def v248_l704 (kind/doc #'sk/coord-doc))


(def v249_l706 (sk/coord-doc :cartesian))


(deftest t250_l708 (is ((fn [s] (string? s)) v249_l706)))


(def v251_l710 (kind/doc #'sk/membrane-mark-doc))


(def v252_l712 (sk/membrane-mark-doc :point))


(deftest t253_l714 (is ((fn [s] (string? s)) v252_l712)))


(def v255_l718 (kind/doc #'sk/save))


(def
 v257_l722
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t258_l728 (is (true? v257_l722)))


(def v259_l730 (kind/doc #'sk/save-png))


(def
 v261_l735
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save-png path))
  (.exists (java.io.File. path))))


(deftest t262_l741 (is (true? v261_l735)))
