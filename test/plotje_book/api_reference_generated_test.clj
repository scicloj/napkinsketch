(ns
 plotje-book.api-reference-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
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


(def v7_l39 (kind/doc #'pj/frame))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t10_l47
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l43)))


(def
 v12_l53
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l53)))


(def v14_l62 (kind/doc #'pj/with-data))


(def
 v16_l68
 (def
  scatter-template
  (-> (pj/frame nil {:x :x, :y :y, :color :group}) pj/lay-point)))


(def v17_l72 (-> scatter-template (pj/with-data tiny)))


(deftest
 t18_l75
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v17_l72)))


(def
 v20_l80
 (->
  (rdatasets/datasets-iris)
  (pj/frame
   [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point {:color :species})))


(deftest
 t21_l85
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v20_l80)))


(def
 v23_l91
 (->
  (rdatasets/datasets-iris)
  (pj/frame {:x :sepal-length, :y :sepal-width})
  pj/lay-point))


(deftest
 t24_l95
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v23_l91)))


(def v25_l99 (kind/doc #'pj/cross))


(def v26_l101 (pj/cross [:a :b] [1 2 3]))


(deftest
 t27_l103
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v26_l101)))


(def
 v29_l107
 (->
  (rdatasets/datasets-iris)
  (pj/frame {:color :species})
  (pj/frame
   (pj/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))))


(deftest
 t30_l112
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v29_l107)))


(def
 v32_l118
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t33_l120
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v32_l118)))


(def v35_l126 (kind/doc #'pj/lay))


(def
 v37_l134
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay :point)))


(deftest
 t38_l138
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v37_l134)))


(def v39_l140 (kind/doc #'pj/lay-point))


(def
 v40_l142
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t41_l145
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v40_l142)))


(def v42_l148 (kind/doc #'pj/lay-line))


(def
 v43_l150
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__84163#] (Math/sin (* p1__84163# 0.3))) (range 30))}))


(def v44_l153 (-> wave (pj/lay-line :x :y)))


(deftest
 t45_l156
 (is ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:lines s)))) v44_l153)))


(def v46_l159 (kind/doc #'pj/lay-histogram))


(def
 v47_l161
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t48_l164
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v47_l161)))


(def v49_l167 (kind/doc #'pj/lay-bar))


(def v50_l169 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t51_l172
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v50_l169)))


(def
 v53_l177
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :stack, :color :species})))


(deftest
 t54_l180
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v53_l177)))


(def
 v56_l185
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :fill, :color :species})))


(deftest
 t57_l188
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v56_l185)))


(def v58_l191 (kind/doc #'pj/lay-value-bar))


(def v59_l193 (-> sales (pj/lay-value-bar :product :revenue)))


(deftest
 t60_l196
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v59_l193)))


(def
 v62_l201
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t63_l205
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v62_l201)))


(def v64_l209 (kind/doc #'pj/lay-smooth))


(def
 v65_l211
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__84164#]
      (+
       (Math/sin (* p1__84164# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (pj/lay-point :x :y)
  (pj/lay-smooth {:bandwidth 0.2})))


(deftest
 t66_l220
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v65_l211)))


(def v67_l224 (kind/doc #'pj/lay-density))


(def
 v68_l226
 (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-length)))


(deftest
 t69_l229
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v68_l226)))


(def v70_l232 (kind/doc #'pj/lay-area))


(def v71_l234 (-> wave (pj/lay-area :x :y)))


(deftest
 t72_l237
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v71_l234)))


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
  (pj/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t75_l249
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v74_l242)))


(def v76_l252 (kind/doc #'pj/lay-text))


(def
 v77_l254
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-text :x :y {:text :name})))


(deftest
 t78_l257
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v77_l254)))


(def v79_l260 (kind/doc #'pj/lay-label))


(def
 v80_l262
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-point :x :y {:size 5})
  (pj/lay-label {:text :name})))


(deftest
 t81_l266
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v80_l262)))


(def v82_l270 (kind/doc #'pj/lay-boxplot))


(def
 v83_l272
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t84_l275
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v83_l272)))


(def v85_l279 (kind/doc #'pj/lay-violin))


(def
 v86_l281
 (-> (rdatasets/reshape2-tips) (pj/lay-violin :day :total-bill)))


(deftest
 t87_l284
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v86_l281)))


(def v88_l287 (kind/doc #'pj/lay-errorbar))


(def
 v89_l289
 (->
  measurements
  (pj/lay-point :treatment :mean)
  (pj/lay-errorbar {:y-min :ci-lo, :y-max :ci-hi})))


(deftest
 t90_l293
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v89_l289)))


(def v91_l297 (kind/doc #'pj/lay-lollipop))


(def v92_l299 (-> sales (pj/lay-lollipop :product :revenue)))


(deftest
 t93_l302
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v92_l299)))


(def v94_l306 (kind/doc #'pj/lay-tile))


(def
 v95_l308
 (->
  (rdatasets/datasets-iris)
  (pj/lay-tile :sepal-length :sepal-width)))


(deftest
 t96_l311
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v95_l308)))


(def v97_l314 (kind/doc #'pj/lay-density-2d))


(def
 v98_l316
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t99_l319
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v98_l316)))


(def v100_l322 (kind/doc #'pj/lay-contour))


(def
 v101_l324
 (->
  (rdatasets/datasets-iris)
  (pj/lay-contour :sepal-length :sepal-width)))


(deftest
 t102_l327
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:lines s)))) v101_l324)))


(def v103_l330 (kind/doc #'pj/lay-ridgeline))


(def
 v104_l332
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length)))


(deftest
 t105_l335
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v104_l332)))


(def v106_l338 (kind/doc #'pj/lay-rug))


(def
 v107_l340
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rug {:side :both})))


(deftest
 t108_l344
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 300 (:lines s)))) v107_l340)))


(def v109_l347 (kind/doc #'pj/lay-step))


(def v110_l349 (-> tiny (pj/lay-step :x :y) pj/lay-point))


(deftest
 t111_l353
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v110_l349)))


(def v112_l357 (kind/doc #'pj/lay-summary))


(def
 v113_l359
 (-> (rdatasets/datasets-iris) (pj/lay-summary :species :sepal-length)))


(deftest
 t114_l362
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v113_l359)))


(def v116_l376 (kind/doc #'pj/lay-rule-v))


(def
 v117_l378
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t118_l382
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v117_l378)))


(def v119_l386 (kind/doc #'pj/lay-rule-h))


(def
 v120_l388
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t121_l392
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v120_l388)))


(def v122_l396 (kind/doc #'pj/lay-band-v))


(def
 v123_l398
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t124_l402
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v123_l398)))


(def v125_l405 (kind/doc #'pj/lay-band-h))


(def
 v126_l407
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t127_l411
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v126_l407)))


(def v129_l416 (kind/doc #'pj/coord))


(def
 v131_l420
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t132_l423
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v131_l420)))


(def
 v134_l428
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :polar)))


(deftest
 t135_l431
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v134_l428)))


(def v136_l434 (kind/doc #'pj/scale))


(def
 v138_l438
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x :log)))


(deftest
 t139_l441
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v138_l438)))


(def
 v141_l446
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x {:domain [3 9]})))


(deftest
 t142_l449
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v141_l446)))


(def v144_l454 (kind/doc #'pj/facet))


(def
 v145_l456
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t146_l460
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v145_l456)))


(def v147_l464 (kind/doc #'pj/facet-grid))


(def
 v148_l466
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t149_l470
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v148_l466)))


(def v151_l476 (kind/doc #'pj/arrange))


(def
 v152_l478
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:width 250, :height 200}))
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width {:color :species})
    (pj/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t153_l486 (is ((fn [v] (pj/frame? v)) v152_l478)))


(def v155_l490 (kind/doc #'pj/plot))


(def v157_l495 (-> tiny (pj/lay-point :x :y)))


(deftest
 t158_l498
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:points s)))) v157_l495)))


(def v159_l501 (kind/doc #'pj/options))


(def
 v161_l505
 (->
  tiny
  (pj/lay-point :x :y)
  (pj/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t162_l509
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v161_l505)))


(def v164_l515 (kind/doc #'pj/frame?))


(def v166_l519 (pj/frame? (-> tiny (pj/frame :x :y) pj/lay-point)))


(deftest t167_l521 (is (true? v166_l519)))


(def v168_l523 (kind/doc #'pj/plan?))


(def v170_l527 (pj/plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t171_l529 (is (true? v170_l527)))


(def v172_l531 (kind/doc #'pj/layer?))


(def
 v174_l535
 (pj/layer?
  (first
   (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y))))))))


(deftest t175_l537 (is (true? v174_l535)))


(def v176_l539 (kind/doc #'pj/layer-type?))


(def v178_l543 (pj/layer-type? (pj/layer-type-lookup :point)))


(deftest t179_l545 (is (true? v178_l543)))


(def v181_l549 (kind/doc #'pj/draft))


(def
 v183_l555
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point
  pj/draft
  kind/pprint))


(deftest
 t184_l561
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v183_l555)))


(def v185_l565 (kind/doc #'pj/plan))


(def v187_l569 (def plan1 (-> tiny (pj/lay-point :x :y) pj/plan)))


(def v188_l573 plan1)


(deftest
 t189_l575
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v188_l573)))


(def v190_l578 (kind/doc #'pj/svg-summary))


(def
 v191_l580
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  pj/svg-summary))


(deftest
 t192_l583
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l580)))


(def v193_l586 (kind/doc #'pj/valid-plan?))


(def v194_l588 (pj/valid-plan? plan1))


(deftest t195_l590 (is (true? v194_l588)))


(def v196_l592 (kind/doc #'pj/explain-plan))


(def v197_l594 (pj/explain-plan plan1))


(deftest t198_l596 (is (nil? v197_l594)))


(def v200_l600 (kind/doc #'pj/plan->membrane))


(def v201_l602 (def m1 (pj/plan->membrane plan1)))


(def v202_l604 (vector? m1))


(deftest t203_l606 (is (true? v202_l604)))


(def v204_l608 (kind/doc #'pj/membrane->plot))


(def
 v205_l610
 (first
  (pj/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t206_l614 (is ((fn [v] (= :svg v)) v205_l610)))


(def v207_l616 (kind/doc #'pj/plan->plot))


(def v208_l618 (first (pj/plan->plot plan1 :svg {})))


(deftest t209_l620 (is ((fn [v] (= :svg v)) v208_l618)))


(def v211_l624 (kind/doc #'pj/config))


(def v212_l626 (pj/config))


(deftest t213_l628 (is ((fn [m] (map? m)) v212_l626)))


(def v214_l630 (kind/doc #'pj/set-config!))


(def v215_l632 (kind/doc #'pj/with-config))


(def
 v216_l634
 (pj/with-config {:palette :pastel1} (:palette (pj/config))))


(deftest t217_l637 (is ((fn [p] (= :pastel1 p)) v216_l634)))


(def v219_l643 (kind/doc #'pj/config-key-docs))


(def v220_l645 (count pj/config-key-docs))


(deftest t221_l647 (is ((fn [n] (= 36 n)) v220_l645)))


(def v222_l649 (kind/doc #'pj/plot-option-docs))


(def v223_l651 (count pj/plot-option-docs))


(deftest t224_l653 (is ((fn [n] (= 11 n)) v223_l651)))


(def v225_l655 (kind/doc #'pj/layer-option-docs))


(def v226_l657 (count pj/layer-option-docs))


(deftest t227_l659 (is ((fn [n] (pos? n)) v226_l657)))


(def v229_l663 (kind/doc #'pj/layer-type-lookup))


(def v230_l665 (pj/layer-type-lookup :smooth))


(deftest
 t231_l667
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v230_l665)))


(def v232_l670 (kind/doc #'pj/registered-layer-types))


(def v233_l672 (count (pj/registered-layer-types)))


(deftest t234_l674 (is ((fn [n] (= 25 n)) v233_l672)))


(def v236_l680 (kind/doc #'pj/stat-doc))


(def v237_l682 (pj/stat-doc :linear-model))


(deftest t238_l684 (is ((fn [s] (string? s)) v237_l682)))


(def v239_l686 (kind/doc #'pj/mark-doc))


(def v240_l688 (pj/mark-doc :point))


(deftest t241_l690 (is ((fn [s] (string? s)) v240_l688)))


(def v242_l692 (kind/doc #'pj/position-doc))


(def v243_l694 (pj/position-doc :dodge))


(deftest t244_l696 (is ((fn [s] (string? s)) v243_l694)))


(def v245_l698 (kind/doc #'pj/scale-doc))


(def v246_l700 (pj/scale-doc :linear))


(deftest t247_l702 (is ((fn [s] (string? s)) v246_l700)))


(def v248_l704 (kind/doc #'pj/coord-doc))


(def v249_l706 (pj/coord-doc :cartesian))


(deftest t250_l708 (is ((fn [s] (string? s)) v249_l706)))


(def v251_l710 (kind/doc #'pj/membrane-mark-doc))


(def v252_l712 (pj/membrane-mark-doc :point))


(deftest t253_l714 (is ((fn [s] (string? s)) v252_l712)))


(def v255_l718 (kind/doc #'pj/save))


(def
 v257_l722
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t258_l728 (is (true? v257_l722)))


(def v259_l730 (kind/doc #'pj/save-png))


(def
 v261_l735
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save-png path))
  (.exists (java.io.File. path))))


(deftest t262_l741 (is (true? v261_l735)))
