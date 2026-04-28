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


(def v7_l39 (kind/doc #'pj/pose))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
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
  (pj/pose :sepal-length :sepal-width {:color :species})
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
  (-> (pj/pose nil {:x :x, :y :y, :color :group}) pj/lay-point)))


(def v17_l72 (-> scatter-template (pj/with-data tiny)))


(deftest
 t18_l75
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v17_l72)))


(def
 v20_l80
 (->
  (rdatasets/datasets-iris)
  (pj/pose [[:sepal-length :sepal-width] [:petal-length :petal-width]])
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
  (pj/pose {:x :sepal-length, :y :sepal-width})
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
  (pj/pose
   (pj/cross [:sepal-length :petal-length] [:sepal-width :petal-width])
   {:color :species})))


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
  (pj/pose :sepal-length :sepal-width)
  (pj/lay :point)))


(deftest
 t38_l138
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v37_l134)))


(def v40_l149 (kind/doc #'pj/lay-point))


(def
 v41_l151
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t42_l154
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v41_l151)))


(def v43_l157 (kind/doc #'pj/lay-line))


(def
 v44_l159
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__85545#] (Math/sin (* p1__85545# 0.3))) (range 30))}))


(def v45_l162 (-> wave (pj/lay-line :x :y)))


(deftest
 t46_l165
 (is ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:lines s)))) v45_l162)))


(def v47_l168 (kind/doc #'pj/lay-histogram))


(def
 v48_l170
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t49_l173
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v48_l170)))


(def v50_l176 (kind/doc #'pj/lay-bar))


(def v51_l178 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t52_l181
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v51_l178)))


(def
 v54_l186
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :stack, :color :species})))


(deftest
 t55_l189
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v54_l186)))


(def
 v57_l194
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :fill, :color :species})))


(deftest
 t58_l197
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v57_l194)))


(def v59_l200 (kind/doc #'pj/lay-value-bar))


(def v60_l202 (-> sales (pj/lay-value-bar :product :revenue)))


(deftest
 t61_l205
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v60_l202)))


(def
 v63_l210
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t64_l214
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v63_l210)))


(def v65_l218 (kind/doc #'pj/lay-smooth))


(def
 v66_l220
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__85546#]
      (+
       (Math/sin (* p1__85546# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (pj/lay-point :x :y)
  (pj/lay-smooth {:bandwidth 0.2})))


(deftest
 t67_l229
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v66_l220)))


(def v68_l233 (kind/doc #'pj/lay-density))


(def
 v69_l235
 (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-length)))


(deftest
 t70_l238
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v69_l235)))


(def v71_l241 (kind/doc #'pj/lay-area))


(def v72_l243 (-> wave (pj/lay-area :x :y)))


(deftest
 t73_l246
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v72_l243)))


(def
 v75_l251
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
 t76_l258
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v75_l251)))


(def v77_l261 (kind/doc #'pj/lay-text))


(def
 v78_l263
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-text :x :y {:text :name})))


(deftest
 t79_l266
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v78_l263)))


(def v80_l269 (kind/doc #'pj/lay-label))


(def
 v81_l271
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-point :x :y {:size 5})
  (pj/lay-label {:text :name})))


(deftest
 t82_l275
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v81_l271)))


(def v83_l279 (kind/doc #'pj/lay-boxplot))


(def
 v84_l281
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t85_l284
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v84_l281)))


(def v86_l288 (kind/doc #'pj/lay-violin))


(def
 v87_l290
 (-> (rdatasets/reshape2-tips) (pj/lay-violin :day :total-bill)))


(deftest
 t88_l293
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v87_l290)))


(def v89_l296 (kind/doc #'pj/lay-errorbar))


(def
 v90_l298
 (->
  measurements
  (pj/lay-point :treatment :mean)
  (pj/lay-errorbar {:y-min :ci-lo, :y-max :ci-hi})))


(deftest
 t91_l302
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v90_l298)))


(def v92_l306 (kind/doc #'pj/lay-lollipop))


(def v93_l308 (-> sales (pj/lay-lollipop :product :revenue)))


(deftest
 t94_l311
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v93_l308)))


(def v95_l315 (kind/doc #'pj/lay-tile))


(def
 v96_l317
 (->
  (rdatasets/datasets-iris)
  (pj/lay-tile :sepal-length :sepal-width)))


(deftest
 t97_l320
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v96_l317)))


(def v98_l323 (kind/doc #'pj/lay-density-2d))


(def
 v99_l325
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t100_l328
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v99_l325)))


(def v101_l331 (kind/doc #'pj/lay-contour))


(def
 v102_l333
 (->
  (rdatasets/datasets-iris)
  (pj/lay-contour :sepal-length :sepal-width)))


(deftest
 t103_l336
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:lines s)))) v102_l333)))


(def v104_l339 (kind/doc #'pj/lay-ridgeline))


(def
 v105_l341
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length)))


(deftest
 t106_l344
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v105_l341)))


(def v107_l347 (kind/doc #'pj/lay-rug))


(def
 v108_l349
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rug {:side :both})))


(deftest
 t109_l353
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 300 (:lines s)))) v108_l349)))


(def v110_l356 (kind/doc #'pj/lay-step))


(def v111_l358 (-> tiny (pj/lay-step :x :y) pj/lay-point))


(deftest
 t112_l362
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v111_l358)))


(def v113_l366 (kind/doc #'pj/lay-summary))


(def
 v114_l368
 (-> (rdatasets/datasets-iris) (pj/lay-summary :species :sepal-length)))


(deftest
 t115_l371
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v114_l368)))


(def v117_l385 (kind/doc #'pj/lay-rule-v))


(def
 v118_l387
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t119_l391
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v118_l387)))


(def v120_l395 (kind/doc #'pj/lay-rule-h))


(def
 v121_l397
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t122_l401
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v121_l397)))


(def v123_l405 (kind/doc #'pj/lay-band-v))


(def
 v124_l407
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t125_l411
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v124_l407)))


(def v126_l414 (kind/doc #'pj/lay-band-h))


(def
 v127_l416
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t128_l420
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v127_l416)))


(def v130_l425 (kind/doc #'pj/coord))


(def
 v132_l429
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t133_l432
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v132_l429)))


(def
 v135_l437
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :polar)))


(deftest
 t136_l440
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v135_l437)))


(def v137_l443 (kind/doc #'pj/scale))


(def
 v139_l447
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x :log)))


(deftest
 t140_l450
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v139_l447)))


(def
 v142_l455
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x {:domain [3 9]})))


(deftest
 t143_l458
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v142_l455)))


(def
 v145_l464
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t146_l468
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v145_l464)))


(def v148_l472 (kind/doc #'pj/facet))


(def
 v149_l474
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t150_l478
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v149_l474)))


(def v151_l482 (kind/doc #'pj/facet-grid))


(def
 v152_l484
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t153_l488
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v152_l484)))


(def v155_l494 (kind/doc #'pj/arrange))


(def
 v156_l496
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


(deftest t157_l504 (is ((fn [v] (pj/pose? v)) v156_l496)))


(def v159_l508 (kind/doc #'pj/plot))


(def v161_l513 (-> tiny (pj/lay-point :x :y)))


(deftest
 t162_l516
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:points s)))) v161_l513)))


(def v163_l519 (kind/doc #'pj/options))


(def
 v165_l523
 (->
  tiny
  (pj/lay-point :x :y)
  (pj/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t166_l527
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v165_l523)))


(def v168_l533 (kind/doc #'pj/pose?))


(def v170_l537 (pj/pose? (-> tiny (pj/pose :x :y) pj/lay-point)))


(deftest t171_l539 (is (true? v170_l537)))


(def v172_l541 (kind/doc #'pj/plan?))


(def v174_l545 (pj/plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t175_l547 (is (true? v174_l545)))


(def v176_l549 (kind/doc #'pj/layer?))


(def
 v178_l553
 (pj/layer?
  (first
   (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y))))))))


(deftest t179_l555 (is (true? v178_l553)))


(def v180_l557 (kind/doc #'pj/layer-type?))


(def v182_l561 (pj/layer-type? (pj/layer-type-lookup :point)))


(deftest t183_l563 (is (true? v182_l561)))


(def v185_l567 (kind/doc #'pj/draft))


(def
 v187_l573
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  pj/draft
  kind/pprint))


(deftest
 t188_l579
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v187_l573)))


(def v189_l583 (kind/doc #'pj/plan))


(def v191_l587 (def plan1 (-> tiny (pj/lay-point :x :y) pj/plan)))


(def v192_l591 plan1)


(deftest
 t193_l593
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v192_l591)))


(def v194_l596 (kind/doc #'pj/svg-summary))


(def
 v195_l598
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  pj/svg-summary))


(deftest
 t196_l601
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v195_l598)))


(def v197_l604 (kind/doc #'pj/valid-plan?))


(def v198_l606 (pj/valid-plan? plan1))


(deftest t199_l608 (is (true? v198_l606)))


(def v200_l610 (kind/doc #'pj/explain-plan))


(def v201_l612 (pj/explain-plan plan1))


(deftest t202_l614 (is (nil? v201_l612)))


(def v204_l618 (kind/doc #'pj/plan->membrane))


(def v205_l620 (def m1 (pj/plan->membrane plan1)))


(def v206_l622 (vector? m1))


(deftest t207_l624 (is (true? v206_l622)))


(def v208_l626 (kind/doc #'pj/membrane->plot))


(def
 v209_l628
 (first
  (pj/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t210_l632 (is ((fn [v] (= :svg v)) v209_l628)))


(def v211_l634 (kind/doc #'pj/plan->plot))


(def v212_l636 (first (pj/plan->plot plan1 :svg {})))


(deftest t213_l638 (is ((fn [v] (= :svg v)) v212_l636)))


(def v215_l642 (kind/doc #'pj/config))


(def v216_l644 (pj/config))


(deftest t217_l646 (is ((fn [m] (map? m)) v216_l644)))


(def v218_l648 (kind/doc #'pj/set-config!))


(def v219_l650 (kind/doc #'pj/with-config))


(def
 v220_l652
 (pj/with-config {:palette :pastel1} (:palette (pj/config))))


(deftest t221_l655 (is ((fn [p] (= :pastel1 p)) v220_l652)))


(def v223_l661 (kind/doc #'pj/config-key-docs))


(def v224_l663 (count pj/config-key-docs))


(deftest t225_l665 (is ((fn [n] (= 37 n)) v224_l663)))


(def v226_l667 (kind/doc #'pj/plot-option-docs))


(def v227_l669 (count pj/plot-option-docs))


(deftest t228_l671 (is ((fn [n] (= 13 n)) v227_l669)))


(def v229_l673 (kind/doc #'pj/layer-option-docs))


(def v230_l675 (count pj/layer-option-docs))


(deftest t231_l677 (is ((fn [n] (pos? n)) v230_l675)))


(def v233_l681 (kind/doc #'pj/layer-type-lookup))


(def v234_l683 (pj/layer-type-lookup :smooth))


(deftest
 t235_l685
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v234_l683)))


(def v236_l688 (kind/doc #'pj/registered-layer-types))


(def v237_l690 (count (pj/registered-layer-types)))


(deftest t238_l692 (is ((fn [n] (= 26 n)) v237_l690)))


(def v239_l694 (first (pj/registered-layer-types)))


(deftest
 t240_l696
 (is
  ((fn [[k m]] (and (keyword? k) (some? (:mark m)) (some? (:stat m))))
   v239_l694)))


(def v242_l704 (kind/doc #'pj/stat-doc))


(def v243_l706 (pj/stat-doc :linear-model))


(deftest t244_l708 (is ((fn [s] (string? s)) v243_l706)))


(def v245_l710 (kind/doc #'pj/mark-doc))


(def v246_l712 (pj/mark-doc :point))


(deftest t247_l714 (is ((fn [s] (string? s)) v246_l712)))


(def v248_l716 (kind/doc #'pj/position-doc))


(def v249_l718 (pj/position-doc :dodge))


(deftest t250_l720 (is ((fn [s] (string? s)) v249_l718)))


(def v251_l722 (kind/doc #'pj/scale-doc))


(def v252_l724 (pj/scale-doc :linear))


(deftest t253_l726 (is ((fn [s] (string? s)) v252_l724)))


(def v254_l728 (kind/doc #'pj/coord-doc))


(def v255_l730 (pj/coord-doc :cartesian))


(deftest t256_l732 (is ((fn [s] (string? s)) v255_l730)))


(def v257_l734 (kind/doc #'pj/membrane-mark-doc))


(def v258_l736 (pj/membrane-mark-doc :point))


(deftest t259_l738 (is ((fn [s] (string? s)) v258_l736)))


(def v261_l742 (kind/doc #'pj/save))


(def
 v263_l746
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t264_l752 (is (true? v263_l746)))


(def v265_l754 (kind/doc #'pj/save-png))


(def
 v267_l759
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save-png path))
  (.exists (java.io.File. path))))


(deftest t268_l765 (is (true? v267_l759)))
