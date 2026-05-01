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
   (map (fn* [p1__87025#] (Math/sin (* p1__87025# 0.3))) (range 30))}))


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
      [p1__87026#]
      (+
       (Math/sin (* p1__87026# 0.2))
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


(def v120_l401 (kind/doc #'pj/lay-rule-v))


(def
 v121_l403
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t122_l407
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v121_l403)))


(def
 v124_l414
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
 t125_l420
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v124_l414)))


(def v126_l424 (kind/doc #'pj/lay-rule-h))


(def
 v127_l426
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t128_l430
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v127_l426)))


(def v129_l434 (kind/doc #'pj/lay-band-v))


(def
 v130_l436
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t131_l440
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v130_l436)))


(def v132_l443 (kind/doc #'pj/lay-band-h))


(def
 v133_l445
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t134_l449
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v133_l445)))


(def v136_l454 (kind/doc #'pj/coord))


(def
 v138_l458
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t139_l461
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s))))
   v138_l458)))


(def
 v141_l466
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :polar)))


(deftest
 t142_l469
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (pos? (:polygons s))))
   v141_l466)))


(def v143_l472 (kind/doc #'pj/scale))


(def
 v145_l476
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x :log)))


(deftest
 t146_l479
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v145_l476)))


(def
 v148_l484
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :x {:domain [3 9]})))


(deftest
 t149_l487
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v148_l484)))


(def
 v151_l493
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t152_l497
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v151_l493)))


(def v154_l501 (kind/doc #'pj/facet))


(def
 v155_l503
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t156_l507
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l503)))


(def v157_l511 (kind/doc #'pj/facet-grid))


(def
 v158_l513
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t159_l517
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v158_l513)))


(def v161_l523 (kind/doc #'pj/arrange))


(def
 v162_l525
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


(deftest t163_l533 (is ((fn [v] (pj/pose? v)) v162_l525)))


(def v165_l537 (kind/doc #'pj/plot))


(def v167_l542 (-> tiny (pj/lay-point :x :y)))


(deftest
 t168_l545
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 5 (:points s)))) v167_l542)))


(def v169_l548 (kind/doc #'pj/options))


(def
 v171_l552
 (->
  tiny
  (pj/lay-point :x :y)
  (pj/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t172_l556
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v171_l552)))


(def v174_l562 (kind/doc #'pj/pose?))


(def v176_l566 (pj/pose? (-> tiny (pj/pose :x :y) pj/lay-point)))


(deftest t177_l568 (is (true? v176_l566)))


(def v178_l570 (kind/doc #'pj/plan?))


(def v180_l574 (pj/plan? (pj/plan (pj/lay-point tiny :x :y))))


(deftest t181_l576 (is (true? v180_l574)))


(def v182_l578 (kind/doc #'pj/plan-layer?))


(def
 v184_l582
 (pj/plan-layer?
  (first
   (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y))))))))


(deftest t185_l584 (is (true? v184_l582)))


(def v186_l586 (kind/doc #'pj/layer-type?))


(def v188_l590 (pj/layer-type? (pj/layer-type-lookup :point)))


(deftest t189_l592 (is (true? v188_l590)))


(def v191_l596 (kind/doc #'pj/draft))


(def
 v193_l602
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  pj/draft
  kind/pprint))


(deftest
 t194_l608
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v193_l602)))


(def v195_l612 (kind/doc #'pj/plan))


(def v197_l616 (def plan1 (-> tiny (pj/lay-point :x :y) pj/plan)))


(def v198_l620 plan1)


(deftest
 t199_l622
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v198_l620)))


(def v200_l625 (kind/doc #'pj/svg-summary))


(def
 v201_l627
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  pj/svg-summary))


(deftest
 t202_l630
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v201_l627)))


(def v203_l633 (kind/doc #'pj/valid-plan?))


(def v204_l635 (pj/valid-plan? plan1))


(deftest t205_l637 (is (true? v204_l635)))


(def v206_l639 (kind/doc #'pj/explain-plan))


(def v207_l641 (pj/explain-plan plan1))


(deftest t208_l643 (is (nil? v207_l641)))


(def v210_l647 (kind/doc #'pj/plan->membrane))


(def v211_l649 (def m1 (pj/plan->membrane plan1)))


(def v212_l651 (vector? m1))


(deftest t213_l653 (is (true? v212_l651)))


(def v214_l655 (kind/doc #'pj/membrane->plot))


(def
 v215_l657
 (first
  (pj/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t216_l661 (is ((fn [v] (= :svg v)) v215_l657)))


(def v217_l663 (kind/doc #'pj/plan->plot))


(def v218_l665 (first (pj/plan->plot plan1 :svg {})))


(deftest t219_l667 (is ((fn [v] (= :svg v)) v218_l665)))


(def v221_l671 (kind/doc #'pj/config))


(def v222_l673 (pj/config))


(deftest t223_l675 (is ((fn [m] (map? m)) v222_l673)))


(def v224_l677 (kind/doc #'pj/set-config!))


(def v225_l679 (kind/doc #'pj/with-config))


(def
 v226_l681
 (pj/with-config {:palette :pastel1} (:palette (pj/config))))


(deftest t227_l684 (is ((fn [p] (= :pastel1 p)) v226_l681)))


(def v229_l690 (kind/doc #'pj/config-key-docs))


(def v230_l692 (count pj/config-key-docs))


(deftest t231_l694 (is ((fn [n] (= 37 n)) v230_l692)))


(def v232_l696 (kind/doc #'pj/plot-option-docs))


(def v233_l698 (count pj/plot-option-docs))


(deftest t234_l700 (is ((fn [n] (= 14 n)) v233_l698)))


(def v235_l702 (kind/doc #'pj/layer-option-docs))


(def v236_l704 (count pj/layer-option-docs))


(deftest t237_l706 (is ((fn [n] (pos? n)) v236_l704)))


(def v239_l710 (kind/doc #'pj/layer-type-lookup))


(def v240_l712 (pj/layer-type-lookup :smooth))


(deftest
 t241_l714
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v240_l712)))


(def v242_l717 (kind/doc #'pj/registered-layer-types))


(def v243_l719 (count (pj/registered-layer-types)))


(deftest t244_l721 (is ((fn [n] (= 26 n)) v243_l719)))


(def v245_l723 (first (pj/registered-layer-types)))


(deftest
 t246_l725
 (is
  ((fn [[k m]] (and (keyword? k) (some? (:mark m)) (some? (:stat m))))
   v245_l723)))


(def v248_l733 (kind/doc #'pj/stat-doc))


(def v249_l735 (pj/stat-doc :linear-model))


(deftest t250_l737 (is ((fn [s] (string? s)) v249_l735)))


(def v251_l739 (kind/doc #'pj/mark-doc))


(def v252_l741 (pj/mark-doc :point))


(deftest t253_l743 (is ((fn [s] (string? s)) v252_l741)))


(def v254_l745 (kind/doc #'pj/position-doc))


(def v255_l747 (pj/position-doc :dodge))


(deftest t256_l749 (is ((fn [s] (string? s)) v255_l747)))


(def v257_l751 (kind/doc #'pj/scale-doc))


(def v258_l753 (pj/scale-doc :linear))


(deftest t259_l755 (is ((fn [s] (string? s)) v258_l753)))


(def v260_l757 (kind/doc #'pj/coord-doc))


(def v261_l759 (pj/coord-doc :cartesian))


(deftest t262_l761 (is ((fn [s] (string? s)) v261_l759)))


(def v263_l763 (kind/doc #'pj/membrane-mark-doc))


(def v264_l765 (pj/membrane-mark-doc :point))


(deftest t265_l767 (is ((fn [s] (string? s)) v264_l765)))


(def v267_l771 (kind/doc #'pj/save))


(def
 v269_l775
 (let
  [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t270_l781 (is (true? v269_l775)))


(def
 v272_l786
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
    (mapv (fn* [p1__87027#] (bit-and p1__87027# 255)) (vec bs))))))


(deftest
 t273_l795
 (is ((fn [bs] (= [137 80 78 71 13 10 26 10] bs)) v272_l786)))


(def
 v275_l800
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
    (mapv (fn* [p1__87028#] (bit-and p1__87028# 255)) (vec bs))))))


(deftest t276_l809 (is ((fn [bs] (= [137 80 78 71] bs)) v275_l800)))
