(ns
 plotje-book.api-reference-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l26
 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]}))


(def
 v4_l30
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def
 v5_l33
 (def
  measurements
  {:treatment ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci-lo [8.0 12.0 9.5 15.5],
   :ci-hi [12.0 18.0 14.5 20.5]}))


(def v7_l40 (kind/doc #'pj/pose))


(def
 v9_l44
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t10_l48
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l44)))


(def
 v12_l54
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t13_l59
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l54)))


(def v14_l63 (kind/doc #'pj/with-data))


(def
 v16_l69
 (def
  scatter-template
  (-> (pj/pose nil {:x :x, :y :y, :color :group}) pj/lay-point)))


(def v17_l73 (-> scatter-template (pj/with-data tiny)))


(deftest
 t18_l76
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v17_l73)))


(def
 v20_l81
 (->
  (rdatasets/datasets-iris)
  (pj/pose [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point {:color :species})))


(deftest
 t21_l86
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v20_l81)))


(def
 v23_l92
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:x :sepal-length, :y :sepal-width})
  pj/lay-point))


(deftest
 t24_l96
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v23_l92)))


(def v25_l100 (kind/doc #'pj/cross))


(def v26_l102 (pj/cross [:a :b] [1 2 3]))


(deftest
 t27_l104
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v26_l102)))


(def
 v29_l108
 (->
  (rdatasets/datasets-iris)
  (pj/pose
   (pj/cross [:sepal-length :petal-length] [:sepal-width :petal-width])
   {:color :species})))


(deftest
 t30_l113
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v29_l108)))


(def
 v32_l119
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t33_l121
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v32_l119)))


(def v35_l127 (kind/doc #'pj/lay))


(def
 v37_l135
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay :point)))


(deftest
 t38_l139
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v37_l135)))


(def v40_l150 (kind/doc #'pj/lay-point))


(def
 v41_l152
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t42_l155
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v41_l152)))


(def v43_l158 (kind/doc #'pj/lay-line))


(def
 v44_l160
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__85358#] (Math/sin (* p1__85358# 0.3))) (range 30))}))


(def v45_l163 (-> wave (pj/lay-line :x :y)))


(deftest
 t46_l166
 (is ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:lines s)))) v45_l163)))


(def v47_l169 (kind/doc #'pj/lay-histogram))


(def
 v48_l171
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t49_l174
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v48_l171)))


(def v50_l177 (kind/doc #'pj/lay-bar))


(def v51_l179 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t52_l182
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v51_l179)))


(def
 v54_l187
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :stack, :color :species})))


(deftest
 t55_l190
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v54_l187)))


(def
 v57_l195
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :fill, :color :species})))


(deftest
 t58_l198
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v57_l195)))


(def v59_l201 (kind/doc #'pj/lay-value-bar))


(def v60_l203 (-> sales (pj/lay-value-bar :product :revenue)))


(deftest
 t61_l206
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v60_l203)))


(def
 v63_l211
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t64_l215
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v63_l211)))


(def v65_l219 (kind/doc #'pj/lay-smooth))


(def
 v66_l221
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__85359#]
      (+
       (Math/sin (* p1__85359# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (pj/lay-point :x :y)
  (pj/lay-smooth {:bandwidth 0.2})))


(deftest
 t67_l230
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v66_l221)))


(def v68_l234 (kind/doc #'pj/lay-density))


(def
 v69_l236
 (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-length)))


(deftest
 t70_l239
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v69_l236)))


(def v71_l242 (kind/doc #'pj/lay-area))


(def v72_l244 (-> wave (pj/lay-area :x :y)))


(deftest
 t73_l247
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 1 (:polygons s)))) v72_l244)))


(def
 v75_l252
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
 t76_l259
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v75_l252)))


(def v77_l262 (kind/doc #'pj/lay-text))


(def
 v78_l264
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-text :x :y {:text :name})))


(deftest
 t79_l267
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v78_l264)))


(def v80_l270 (kind/doc #'pj/lay-label))


(def
 v81_l272
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (pj/lay-point :x :y {:size 5})
  (pj/lay-label {:text :name})))


(deftest
 t82_l276
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v81_l272)))


(def v83_l280 (kind/doc #'pj/lay-boxplot))


(def
 v84_l282
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t85_l285
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v84_l282)))


(def v86_l289 (kind/doc #'pj/lay-violin))


(def
 v87_l291
 (-> (rdatasets/reshape2-tips) (pj/lay-violin :day :total-bill)))


(deftest
 t88_l294
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 4 (:polygons s)))) v87_l291)))


(def v89_l297 (kind/doc #'pj/lay-errorbar))


(def
 v90_l299
 (->
  measurements
  (pj/lay-point :treatment :mean)
  (pj/lay-errorbar {:y-min :ci-lo, :y-max :ci-hi})))


(deftest
 t91_l303
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v90_l299)))


(def v92_l307 (kind/doc #'pj/lay-lollipop))


(def v93_l309 (-> sales (pj/lay-lollipop :product :revenue)))


(deftest
 t94_l312
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v93_l309)))


(def v95_l316 (kind/doc #'pj/lay-tile))


(def
 v96_l318
 (->
  (rdatasets/datasets-iris)
  (pj/lay-tile :sepal-length :sepal-width)))


(deftest
 t97_l321
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v96_l318)))


(def v98_l324 (kind/doc #'pj/lay-density-2d))


(def
 v99_l326
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t100_l329
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:visible-tiles s))))
   v99_l326)))


(def v101_l332 (kind/doc #'pj/lay-contour))


(def
 v102_l334
 (->
  (rdatasets/datasets-iris)
  (pj/lay-contour :sepal-length :sepal-width)))


(deftest
 t103_l337
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:lines s)))) v102_l334)))


(def v104_l340 (kind/doc #'pj/lay-ridgeline))


(def
 v105_l342
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length)))


(deftest
 t106_l345
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v105_l342)))


(def v107_l348 (kind/doc #'pj/lay-rug))


(def
 v108_l350
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rug {:side :both})))


(deftest
 t109_l354
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 300 (:lines s)))) v108_l350)))


(def v110_l357 (kind/doc #'pj/lay-step))


(def v111_l359 (-> tiny (pj/lay-step :x :y) pj/lay-point))


(deftest
 t112_l363
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v111_l359)))


(def v113_l367 (kind/doc #'pj/lay-summary))


(def
 v114_l369
 (-> (rdatasets/datasets-iris) (pj/lay-summary :species :sepal-length)))


(deftest
 t115_l372
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v114_l369)))


(def v116_l376 (kind/doc #'pj/lay-interval-h))


(def
 v117_l378
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
 t118_l383
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v117_l378)))


(def v120_l411 (kind/doc #'pj/lay-rule-v))


(def
 v121_l413
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t122_l417
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v121_l413)))


(def
 v124_l424
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
 t125_l430
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v124_l424)))


(def v126_l434 (kind/doc #'pj/lay-rule-h))


(def
 v127_l436
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t128_l440
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v127_l436)))


(def v129_l444 (kind/doc #'pj/lay-band-v))


(def
 v130_l446
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t131_l450
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v130_l446)))


(def v132_l453 (kind/doc #'pj/lay-band-h))


(def
 v133_l455
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t134_l459
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v133_l455)))


(def v136_l464 (kind/doc #'pj/coord))


(def
 v138_l468
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t139_l471
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v138_l468)))


(def
 v141_l476
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :polar)))


(deftest
 t142_l479
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v141_l476)))


(def v143_l482 (kind/doc #'pj/scale))


(def
 v145_l486
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x :log)))


(deftest
 t146_l489
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v145_l486)))


(def
 v148_l494
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x {:domain [3 9]})))


(deftest
 t149_l497
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v148_l494)))


(def
 v151_l503
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t152_l507
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v151_l503)))


(def v154_l511 (kind/doc #'pj/facet))


(def
 v155_l513
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t156_l517
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l513)))


(def v157_l521 (kind/doc #'pj/facet-grid))


(def
 v158_l523
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t159_l527
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v158_l523)))


(def v161_l533 (kind/doc #'pj/arrange))


(def
 v162_l535
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


(deftest t163_l543 (is ((fn [v] (pj/pose? v)) v162_l535)))


(def v165_l547 (kind/doc #'pj/plot))


(def v167_l552 (-> tiny (pj/lay-point :x :y)))


(deftest
 t168_l555
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:points s)))) v167_l552)))


(def v169_l558 (kind/doc #'pj/options))


(def
 v171_l562
 (->
  tiny
  (pj/lay-point :x :y)
  (pj/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t172_l566
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v171_l562)))


(def v174_l572 (kind/doc #'pj/pose?))


(def v176_l576 (pj/pose? (-> tiny (pj/pose :x :y) pj/lay-point)))


(deftest t177_l578 (is (true? v176_l576)))


(def v178_l580 (kind/doc #'pj/plan?))


(def v180_l584 (pj/plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t181_l586 (is (true? v180_l584)))


(def v182_l588 (kind/doc #'pj/leaf-plan?))


(def v184_l593 (pj/leaf-plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t185_l595 (is (true? v184_l593)))


(def v186_l597 (kind/doc #'pj/composite-plan?))


(def
 v188_l602
 (pj/composite-plan?
  (pj/plan
   (pj/arrange [(pj/lay-point tiny :x :y) (pj/lay-point tiny :x :y)]))))


(deftest t189_l606 (is (true? v188_l602)))


(def v190_l608 (kind/doc #'pj/draft?))


(def v192_l613 (pj/draft? (pj/draft (pj/lay-point tiny :x :y))))


(deftest t193_l615 (is (true? v192_l613)))


(def v194_l617 (kind/doc #'pj/composite-draft?))


(def
 v196_l622
 (pj/composite-draft?
  (pj/draft
   (pj/arrange [(pj/lay-point tiny :x :y) (pj/lay-point tiny :x :y)]))))


(deftest t197_l626 (is (true? v196_l622)))


(def v198_l628 (kind/doc #'pj/plan-layer?))


(def
 v200_l632
 (pj/plan-layer?
  (first
   (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y))))))))


(deftest t201_l634 (is (true? v200_l632)))


(def v202_l636 (kind/doc #'pj/layer-type?))


(def v204_l640 (pj/layer-type? (pj/layer-type-lookup :point)))


(deftest t205_l642 (is (true? v204_l640)))


(def v207_l646 (kind/doc #'pj/draft))


(def
 v209_l652
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  pj/draft
  kind/pprint))


(deftest
 t210_l658
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v209_l652)))


(def v211_l662 (kind/doc #'pj/plan))


(def v213_l666 (def plan1 (-> tiny (pj/lay-point :x :y) pj/plan)))


(def v214_l670 plan1)


(deftest
 t215_l672
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v214_l670)))


(def v216_l675 (kind/doc #'pj/svg-summary))


(def
 v217_l677
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  pj/svg-summary))


(deftest
 t218_l680
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v217_l677)))


(def v219_l683 (kind/doc #'pj/valid-plan?))


(def v220_l685 (pj/valid-plan? plan1))


(deftest t221_l687 (is (true? v220_l685)))


(def v222_l689 (kind/doc #'pj/explain-plan))


(def v223_l691 (pj/explain-plan plan1))


(deftest t224_l693 (is (nil? v223_l691)))


(def v226_l697 (kind/doc #'pj/plan->membrane))


(def v227_l699 (def m1 (pj/plan->membrane plan1)))


(def v228_l701 (vector? m1))


(deftest t229_l703 (is (true? v228_l701)))


(def v230_l705 (kind/doc #'pj/membrane->plot))


(def
 v231_l707
 (first
  (pj/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t232_l711 (is ((fn [v] (= :svg v)) v231_l707)))


(def v233_l713 (kind/doc #'pj/plan->plot))


(def v234_l715 (first (pj/plan->plot plan1 :svg {})))


(deftest t235_l717 (is ((fn [v] (= :svg v)) v234_l715)))


(def v237_l724 (kind/doc #'pj/draft->plan))


(def v238_l726 (def draft1 (pj/draft (pj/lay-point tiny :x :y))))


(def v239_l728 (pj/plan? (pj/draft->plan draft1)))


(deftest t240_l730 (is (true? v239_l728)))


(def v241_l732 (kind/doc #'pj/draft->membrane))


(def v242_l734 (vector? (pj/draft->membrane draft1)))


(deftest t243_l736 (is (true? v242_l734)))


(def v244_l738 (kind/doc #'pj/draft->plot))


(def v245_l740 (first (pj/draft->plot draft1 :svg {})))


(deftest t246_l742 (is ((fn [v] (= :svg v)) v245_l740)))


(def v248_l746 (kind/doc #'pj/config))


(def v249_l748 (pj/config))


(deftest t250_l750 (is ((fn [m] (map? m)) v249_l748)))


(def v251_l752 (kind/doc #'pj/set-config!))


(def v252_l754 (kind/doc #'pj/with-config))


(def
 v253_l756
 (pj/with-config {:palette :pastel1} (:palette (pj/config))))


(deftest t254_l759 (is ((fn [p] (= :pastel1 p)) v253_l756)))


(def v256_l765 (kind/doc #'pj/config-key-docs))


(def v257_l767 (count pj/config-key-docs))


(deftest t258_l769 (is ((fn [n] (= 37 n)) v257_l767)))


(def v259_l771 (kind/doc #'pj/plot-option-docs))


(def v260_l773 (count pj/plot-option-docs))


(deftest t261_l775 (is ((fn [n] (= 14 n)) v260_l773)))


(def v262_l777 (kind/doc #'pj/layer-option-docs))


(def v263_l779 (count pj/layer-option-docs))


(deftest t264_l781 (is ((fn [n] (pos? n)) v263_l779)))


(def v266_l785 (kind/doc #'pj/layer-type-lookup))


(def v267_l787 (pj/layer-type-lookup :smooth))


(deftest
 t268_l789
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v267_l787)))


(def v269_l792 (kind/doc #'pj/registered-layer-types))


(def v270_l794 (count (pj/registered-layer-types)))


(deftest t271_l796 (is ((fn [n] (= 26 n)) v270_l794)))


(def v272_l798 (first (pj/registered-layer-types)))


(deftest
 t273_l800
 (is
  ((fn [[k m]] (and (keyword? k) (some? (:mark m)) (some? (:stat m))))
   v272_l798)))


(def v275_l808 (kind/doc #'pj/stat-doc))


(def v276_l810 (pj/stat-doc :linear-model))


(deftest t277_l812 (is ((fn [s] (string? s)) v276_l810)))


(def v278_l814 (kind/doc #'pj/mark-doc))


(def v279_l816 (pj/mark-doc :point))


(deftest t280_l818 (is ((fn [s] (string? s)) v279_l816)))


(def v281_l820 (kind/doc #'pj/position-doc))


(def v282_l822 (pj/position-doc :dodge))


(deftest t283_l824 (is ((fn [s] (string? s)) v282_l822)))


(def v284_l826 (kind/doc #'pj/scale-doc))


(def v285_l828 (pj/scale-doc :linear))


(deftest t286_l830 (is ((fn [s] (string? s)) v285_l828)))


(def v287_l832 (kind/doc #'pj/coord-doc))


(def v288_l834 (pj/coord-doc :cartesian))


(deftest t289_l836 (is ((fn [s] (string? s)) v288_l834)))


(def v290_l838 (kind/doc #'pj/membrane-mark-doc))


(def v291_l840 (pj/membrane-mark-doc :point))


(deftest t292_l842 (is ((fn [s] (string? s)) v291_l840)))


(def v294_l846 (kind/doc #'pj/save))


(def
 v296_l850
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t297_l856 (is (true? v296_l850)))


(def
 v299_l861
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
    (mapv (fn* [p1__85360#] (bit-and p1__85360# 255)) (vec bs))))))


(deftest
 t300_l870
 (is ((fn [bs] (= [137 80 78 71 13 10 26 10] bs)) v299_l861)))


(def
 v302_l875
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
    (mapv (fn* [p1__85361#] (bit-and p1__85361# 255)) (vec bs))))))


(deftest t303_l884 (is ((fn [bs] (= [137 80 78 71] bs)) v302_l875)))
