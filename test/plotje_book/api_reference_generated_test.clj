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
   (map (fn* [p1__86060#] (Math/sin (* p1__86060# 0.3))) (range 30))}))


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
      [p1__86061#]
      (+
       (Math/sin (* p1__86061# 0.2))
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


(def v116_l375 (kind/doc #'pj/lay-interval-h))


(def
 v117_l377
 (->
  {:start
   [#inst "2024-01-01T00:00:00.000-00:00"
    #inst "2024-03-01T00:00:00.000-00:00"
    #inst "2024-05-01T00:00:00.000-00:00"],
   :end
   [#inst "2024-04-01T00:00:00.000-00:00"
    #inst "2024-06-01T00:00:00.000-00:00"
    #inst "2024-08-01T00:00:00.000-00:00"],
   :task ["Design" "Build" "Test"]}
  (pj/lay-interval-h :start :task {:x-end :end})))


(deftest
 t118_l382
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v117_l377)))


(def v120_l400 (kind/doc #'pj/lay-rule-v))


(def
 v121_l402
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t122_l406
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v121_l402)))


(def
 v124_l413
 (->
  {:date
   [#inst "2024-01-01T00:00:00.000-00:00"
    #inst "2024-04-01T00:00:00.000-00:00"
    #inst "2024-08-01T00:00:00.000-00:00"],
   :value [3 5 9]}
  (pj/lay-line :date :value)
  (pj/lay-rule-v
   {:x-intercept (java.time.LocalDate/parse "2024-06-01"),
    :color "#c0392b"})))


(deftest
 t125_l419
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v124_l413)))


(def v126_l423 (kind/doc #'pj/lay-rule-h))


(def
 v127_l425
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t128_l429
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v127_l425)))


(def v129_l433 (kind/doc #'pj/lay-band-v))


(def
 v130_l435
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t131_l439
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v130_l435)))


(def v132_l442 (kind/doc #'pj/lay-band-h))


(def
 v133_l444
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t134_l448
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v133_l444)))


(def v136_l453 (kind/doc #'pj/coord))


(def
 v138_l457
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t139_l460
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v138_l457)))


(def
 v141_l465
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :polar)))


(deftest
 t142_l468
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v141_l465)))


(def v143_l471 (kind/doc #'pj/scale))


(def
 v145_l475
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x :log)))


(deftest
 t146_l478
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v145_l475)))


(def
 v148_l483
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x {:domain [3 9]})))


(deftest
 t149_l486
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v148_l483)))


(def
 v151_l492
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t152_l496
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v151_l492)))


(def v154_l500 (kind/doc #'pj/facet))


(def
 v155_l502
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t156_l506
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l502)))


(def v157_l510 (kind/doc #'pj/facet-grid))


(def
 v158_l512
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t159_l516
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v158_l512)))


(def v161_l522 (kind/doc #'pj/arrange))


(def
 v162_l524
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


(deftest t163_l532 (is ((fn [v] (pj/pose? v)) v162_l524)))


(def v165_l536 (kind/doc #'pj/plot))


(def v167_l541 (-> tiny (pj/lay-point :x :y)))


(deftest
 t168_l544
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:points s)))) v167_l541)))


(def v169_l547 (kind/doc #'pj/options))


(def
 v171_l551
 (->
  tiny
  (pj/lay-point :x :y)
  (pj/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t172_l555
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v171_l551)))


(def v174_l561 (kind/doc #'pj/pose?))


(def v176_l565 (pj/pose? (-> tiny (pj/pose :x :y) pj/lay-point)))


(deftest t177_l567 (is (true? v176_l565)))


(def v178_l569 (kind/doc #'pj/plan?))


(def v180_l573 (pj/plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t181_l575 (is (true? v180_l573)))


(def v182_l577 (kind/doc #'pj/plan-layer?))


(def
 v184_l581
 (pj/plan-layer?
  (first
   (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y))))))))


(deftest t185_l583 (is (true? v184_l581)))


(def v186_l585 (kind/doc #'pj/layer-type?))


(def v188_l589 (pj/layer-type? (pj/layer-type-lookup :point)))


(deftest t189_l591 (is (true? v188_l589)))


(def v191_l595 (kind/doc #'pj/draft))


(def
 v193_l601
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  pj/draft
  kind/pprint))


(deftest
 t194_l607
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v193_l601)))


(def v195_l611 (kind/doc #'pj/plan))


(def v197_l615 (def plan1 (-> tiny (pj/lay-point :x :y) pj/plan)))


(def v198_l619 plan1)


(deftest
 t199_l621
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v198_l619)))


(def v200_l624 (kind/doc #'pj/svg-summary))


(def
 v201_l626
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  pj/svg-summary))


(deftest
 t202_l629
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v201_l626)))


(def v203_l632 (kind/doc #'pj/valid-plan?))


(def v204_l634 (pj/valid-plan? plan1))


(deftest t205_l636 (is (true? v204_l634)))


(def v206_l638 (kind/doc #'pj/explain-plan))


(def v207_l640 (pj/explain-plan plan1))


(deftest t208_l642 (is (nil? v207_l640)))


(def v210_l646 (kind/doc #'pj/plan->membrane))


(def v211_l648 (def m1 (pj/plan->membrane plan1)))


(def v212_l650 (vector? m1))


(deftest t213_l652 (is (true? v212_l650)))


(def v214_l654 (kind/doc #'pj/membrane->plot))


(def
 v215_l656
 (first
  (pj/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t216_l660 (is ((fn [v] (= :svg v)) v215_l656)))


(def v217_l662 (kind/doc #'pj/plan->plot))


(def v218_l664 (first (pj/plan->plot plan1 :svg {})))


(deftest t219_l666 (is ((fn [v] (= :svg v)) v218_l664)))


(def v221_l670 (kind/doc #'pj/config))


(def v222_l672 (pj/config))


(deftest t223_l674 (is ((fn [m] (map? m)) v222_l672)))


(def v224_l676 (kind/doc #'pj/set-config!))


(def v225_l678 (kind/doc #'pj/with-config))


(def
 v226_l680
 (pj/with-config {:palette :pastel1} (:palette (pj/config))))


(deftest t227_l683 (is ((fn [p] (= :pastel1 p)) v226_l680)))


(def v229_l689 (kind/doc #'pj/config-key-docs))


(def v230_l691 (count pj/config-key-docs))


(deftest t231_l693 (is ((fn [n] (= 37 n)) v230_l691)))


(def v232_l695 (kind/doc #'pj/plot-option-docs))


(def v233_l697 (count pj/plot-option-docs))


(deftest t234_l699 (is ((fn [n] (= 14 n)) v233_l697)))


(def v235_l701 (kind/doc #'pj/layer-option-docs))


(def v236_l703 (count pj/layer-option-docs))


(deftest t237_l705 (is ((fn [n] (pos? n)) v236_l703)))


(def v239_l709 (kind/doc #'pj/layer-type-lookup))


(def v240_l711 (pj/layer-type-lookup :smooth))


(deftest
 t241_l713
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v240_l711)))


(def v242_l716 (kind/doc #'pj/registered-layer-types))


(def v243_l718 (count (pj/registered-layer-types)))


(deftest t244_l720 (is ((fn [n] (= 26 n)) v243_l718)))


(def v245_l722 (first (pj/registered-layer-types)))


(deftest
 t246_l724
 (is
  ((fn [[k m]] (and (keyword? k) (some? (:mark m)) (some? (:stat m))))
   v245_l722)))


(def v248_l732 (kind/doc #'pj/stat-doc))


(def v249_l734 (pj/stat-doc :linear-model))


(deftest t250_l736 (is ((fn [s] (string? s)) v249_l734)))


(def v251_l738 (kind/doc #'pj/mark-doc))


(def v252_l740 (pj/mark-doc :point))


(deftest t253_l742 (is ((fn [s] (string? s)) v252_l740)))


(def v254_l744 (kind/doc #'pj/position-doc))


(def v255_l746 (pj/position-doc :dodge))


(deftest t256_l748 (is ((fn [s] (string? s)) v255_l746)))


(def v257_l750 (kind/doc #'pj/scale-doc))


(def v258_l752 (pj/scale-doc :linear))


(deftest t259_l754 (is ((fn [s] (string? s)) v258_l752)))


(def v260_l756 (kind/doc #'pj/coord-doc))


(def v261_l758 (pj/coord-doc :cartesian))


(deftest t262_l760 (is ((fn [s] (string? s)) v261_l758)))


(def v263_l762 (kind/doc #'pj/membrane-mark-doc))


(def v264_l764 (pj/membrane-mark-doc :point))


(deftest t265_l766 (is ((fn [s] (string? s)) v264_l764)))


(def v267_l770 (kind/doc #'pj/save))


(def
 v269_l774
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t270_l780 (is (true? v269_l774)))


(def
 v272_l785
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path))
  (with-open
   [in (java.io.FileInputStream. path)]
   (let
    [bs (byte-array 8)]
    (.read in bs)
    (mapv (fn* [p1__86062#] (bit-and p1__86062# 255)) (vec bs))))))


(deftest
 t273_l794
 (is ((fn [bs] (= [137 80 78 71 13 10 26 10] bs)) v272_l785)))


(def
 v275_l799
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".out"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:format :png}))
  (with-open
   [in (java.io.FileInputStream. path)]
   (let
    [bs (byte-array 4)]
    (.read in bs)
    (mapv (fn* [p1__86063#] (bit-and p1__86063# 255)) (vec bs))))))


(deftest t276_l808 (is ((fn [bs] (= [137 80 78 71] bs)) v275_l799)))
