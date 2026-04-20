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


(def v25_l95 (kind/doc #'sk/lay))


(def
 v27_l103
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay :point)))


(deftest
 t28_l107
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v27_l103)))


(def v29_l109 (kind/doc #'sk/lay-point))


(def
 v30_l111
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t31_l114
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v30_l111)))


(def v32_l117 (kind/doc #'sk/lay-line))


(def
 v33_l119
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__82429#] (Math/sin (* p1__82429# 0.3))) (range 30))}))


(def v34_l122 (-> wave (sk/lay-line :x :y)))


(deftest
 t35_l125
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v34_l122)))


(def v36_l128 (kind/doc #'sk/lay-histogram))


(def
 v37_l130
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t38_l133
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v37_l130)))


(def v39_l136 (kind/doc #'sk/lay-bar))


(def v40_l138 (-> (rdatasets/datasets-iris) (sk/lay-bar :species)))


(deftest
 t41_l141
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v40_l138)))


(def v42_l144 (kind/doc #'sk/lay-stacked-bar))


(def
 v43_l146
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t44_l149
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v43_l146)))


(def v45_l152 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v46_l154
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t47_l157
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v46_l154)))


(def v48_l160 (kind/doc #'sk/lay-value-bar))


(def v49_l162 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t50_l165
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v49_l162)))


(def v51_l168 (kind/doc #'sk/lay-lm))


(def
 v52_l170
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/lay-lm))


(deftest
 t53_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l170)))


(def v54_l178 (kind/doc #'sk/lay-loess))


(def
 v55_l180
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__82430#]
      (+
       (Math/sin (* p1__82430# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v56_l185 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t57_l189
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v56_l185)))


(def v58_l193 (kind/doc #'sk/lay-density))


(def
 v59_l195
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t60_l198
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v59_l195)))


(def v61_l201 (kind/doc #'sk/lay-area))


(def v62_l203 (-> wave (sk/lay-area :x :y)))


(deftest
 t63_l206
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v62_l203)))


(def v64_l209 (kind/doc #'sk/lay-stacked-area))


(def
 v65_l211
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
 t66_l218
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v65_l211)))


(def v67_l221 (kind/doc #'sk/lay-text))


(def
 v68_l223
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t69_l226
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v68_l223)))


(def v70_l229 (kind/doc #'sk/lay-label))


(def
 v71_l231
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t72_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v71_l231)))


(def v73_l238 (kind/doc #'sk/lay-boxplot))


(def
 v74_l240
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t75_l243
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v74_l240)))


(def v76_l247 (kind/doc #'sk/lay-violin))


(def
 v77_l249
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t78_l252
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v77_l249)))


(def v79_l255 (kind/doc #'sk/lay-errorbar))


(def
 v80_l257
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t81_l261
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v80_l257)))


(def v82_l265 (kind/doc #'sk/lay-lollipop))


(def v83_l267 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t84_l270
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v83_l267)))


(def v85_l274 (kind/doc #'sk/lay-tile))


(def
 v86_l276
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t87_l279
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v86_l276)))


(def v88_l282 (kind/doc #'sk/lay-density2d))


(def
 v89_l284
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density2d :sepal-length :sepal-width)))


(deftest
 t90_l287
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v89_l284)))


(def v91_l290 (kind/doc #'sk/lay-contour))


(def
 v92_l292
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t93_l295
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v92_l292)))


(def v94_l298 (kind/doc #'sk/lay-ridgeline))


(def
 v95_l300
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t96_l303
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v95_l300)))


(def v97_l306 (kind/doc #'sk/lay-rug))


(def
 v98_l308
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t99_l312
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v98_l308)))


(def v100_l315 (kind/doc #'sk/lay-step))


(def v101_l317 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t102_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v101_l317)))


(def v103_l325 (kind/doc #'sk/lay-summary))


(def
 v104_l327
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t105_l330
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v104_l327)))


(def v107_l336 (kind/doc #'sk/plot))


(def v109_l341 (-> tiny (sk/lay-point :x :y)))


(deftest
 t110_l344
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v109_l341)))


(def v111_l347 (kind/doc #'sk/options))


(def
 v113_l351
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t114_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v113_l351)))


(def v115_l359 (kind/doc #'sk/sketch?))


(def v117_l363 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t118_l365 (is (true? v117_l363)))


(def v119_l367 (kind/doc #'sk/plan?))


(def v121_l371 (sk/plan? (sk/plan (sk/lay-point tiny :x :y))))


(deftest t122_l373 (is (true? v121_l371)))


(def v123_l375 (kind/doc #'sk/layer?))


(def
 v125_l379
 (sk/layer?
  (first
   (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y))))))))


(deftest t126_l381 (is (true? v125_l379)))


(def v127_l383 (kind/doc #'sk/method?))


(def v129_l387 (sk/method? (sk/method-lookup :point)))


(deftest t130_l389 (is (true? v129_l387)))


(def v131_l391 (kind/doc #'sk/sketch))


(def
 v133_l395
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t134_l399
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v133_l395)))


(def v135_l403 (kind/doc #'sk/with-data))


(def
 v137_l408
 (def
  scatter-template
  (-> (sk/sketch) (sk/view :x :y {:color :group}) sk/lay-point)))


(def
 v138_l413
 (->
  scatter-template
  (sk/with-data
   {:x [1 2 3 4], :y [2 4 3 5], :group ["a" "a" "b" "b"]})))


(deftest
 t139_l416
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v138_l413)))


(def v140_l418 (kind/doc #'sk/draft))


(def
 v142_l424
 (sk/draft
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t143_l427
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v142_l424)))


(def v144_l431 (kind/doc #'sk/plan))


(def
 v146_l436
 (let
  [sk (-> tiny (sk/lay-point :x :y) sk/lay-lm)]
  [(count (:views sk)) (count (:layers sk))]))


(deftest
 t147_l439
 (is
  ((fn [[views globals]] (and (= 1 views) (= 1 globals))) v146_l436)))


(def v148_l441 (kind/doc #'sk/plan))


(def v150_l445 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v151_l449 plan1)


(deftest
 t152_l451
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v151_l449)))


(def v154_l456 (kind/doc #'sk/plan->membrane))


(def v155_l458 (def m1 (sk/plan->membrane plan1)))


(def v156_l460 (vector? m1))


(deftest t157_l462 (is (true? v156_l460)))


(def v158_l464 (kind/doc #'sk/membrane->figure))


(def
 v159_l466
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t160_l470 (is ((fn [v] (= :svg v)) v159_l466)))


(def v161_l472 (kind/doc #'sk/plan->figure))


(def v162_l474 (first (sk/plan->figure plan1 :svg {})))


(deftest t163_l476 (is ((fn [v] (= :svg v)) v162_l474)))


(def v165_l480 (kind/doc #'sk/coord))


(def
 v167_l484
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t168_l487
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v167_l484)))


(def
 v170_l492
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t171_l495
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v170_l492)))


(def v172_l498 (kind/doc #'sk/scale))


(def
 v174_l502
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t175_l505
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v174_l502)))


(def
 v177_l510
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t178_l513
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v177_l510)))


(def v180_l523 (kind/doc #'sk/rule-v))


(def
 v181_l525
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-v 6.0))))


(deftest
 t182_l528
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v181_l525)))


(def v183_l532 (kind/doc #'sk/rule-h))


(def
 v184_l534
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t185_l537
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v184_l534)))


(def v186_l541 (kind/doc #'sk/band-v))


(def
 v187_l543
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-v 5.5 6.5))))


(deftest
 t188_l546
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v187_l543)))


(def v189_l549 (kind/doc #'sk/band-h))


(def
 v190_l551
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/annotate (sk/band-h 2.5 3.5))))


(deftest
 t191_l554
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v190_l551)))


(def v193_l559 (kind/doc #'sk/cross))


(def v194_l561 (sk/cross [:a :b] [1 2 3]))


(deftest
 t195_l563
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v194_l561)))


(def
 v196_l565
 (->
  (rdatasets/datasets-iris)
  (sk/view
   (sk/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))
  (sk/lay-point {:color :species})))


(deftest
 t197_l570
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v196_l565)))


(def
 v199_l576
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t200_l578
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v199_l576)))


(def v202_l584 (kind/doc #'sk/facet))


(def
 v203_l586
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t204_l590
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v203_l586)))


(def v205_l594 (kind/doc #'sk/facet-grid))


(def
 v206_l596
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t207_l600
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v206_l596)))


(def v209_l606 (kind/doc #'sk/svg-summary))


(def
 v210_l608
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t211_l611
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v210_l608)))


(def v212_l614 (kind/doc #'sk/valid-plan?))


(def v213_l616 (sk/valid-plan? plan1))


(deftest t214_l618 (is (true? v213_l616)))


(def v215_l620 (kind/doc #'sk/explain-plan))


(def v216_l622 (sk/explain-plan plan1))


(deftest t217_l624 (is (nil? v216_l622)))


(def v219_l628 (kind/doc #'sk/config))


(def v220_l630 (sk/config))


(deftest t221_l632 (is ((fn [m] (map? m)) v220_l630)))


(def v222_l634 (kind/doc #'sk/set-config!))


(def v223_l636 (kind/doc #'sk/with-config))


(def
 v224_l638
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t225_l641 (is ((fn [p] (= :pastel1 p)) v224_l638)))


(def v227_l647 (kind/doc #'sk/config-key-docs))


(def v228_l649 (count sk/config-key-docs))


(deftest t229_l651 (is ((fn [n] (= 36 n)) v228_l649)))


(def v230_l653 (kind/doc #'sk/plot-option-docs))


(def v231_l655 (count sk/plot-option-docs))


(deftest t232_l657 (is ((fn [n] (= 11 n)) v231_l655)))


(def v233_l659 (kind/doc #'sk/layer-option-docs))


(def v234_l661 (count sk/layer-option-docs))


(deftest t235_l663 (is ((fn [n] (pos? n)) v234_l661)))


(def v237_l667 (kind/doc #'sk/method-lookup))


(def v238_l669 (sk/method-lookup :lm))


(deftest
 t239_l671
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v238_l669)))


(def v240_l674 (kind/doc #'sk/registered-methods))


(def v241_l676 (count (sk/registered-methods)))


(deftest t242_l678 (is ((fn [n] (= 25 n)) v241_l676)))


(def v244_l684 (kind/doc #'sk/stat-doc))


(def v245_l686 (sk/stat-doc :lm))


(deftest t246_l688 (is ((fn [s] (string? s)) v245_l686)))


(def v247_l690 (kind/doc #'sk/mark-doc))


(def v248_l692 (sk/mark-doc :point))


(deftest t249_l694 (is ((fn [s] (string? s)) v248_l692)))


(def v250_l696 (kind/doc #'sk/position-doc))


(def v251_l698 (sk/position-doc :dodge))


(deftest t252_l700 (is ((fn [s] (string? s)) v251_l698)))


(def v253_l702 (kind/doc #'sk/scale-doc))


(def v254_l704 (sk/scale-doc :linear))


(deftest t255_l706 (is ((fn [s] (string? s)) v254_l704)))


(def v256_l708 (kind/doc #'sk/coord-doc))


(def v257_l710 (sk/coord-doc :cartesian))


(deftest t258_l712 (is ((fn [s] (string? s)) v257_l710)))


(def v259_l714 (kind/doc #'sk/membrane-mark-doc))


(def v260_l716 (sk/membrane-mark-doc :point))


(deftest t261_l718 (is ((fn [s] (string? s)) v260_l716)))


(def v263_l722 (kind/doc #'sk/arrange))


(def
 v264_l724
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


(deftest t265_l730 (is ((fn [v] (= :div (first v))) v264_l724)))


(def v267_l733 (kind/doc #'sk/save))


(def
 v269_l737
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


(deftest t270_l743 (is (true? v269_l737)))


(def v271_l745 (kind/doc #'sk/save-png))


(def
 v273_l750
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (sk/save-png
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   path)
  (.exists (java.io.File. path))))


(deftest t274_l755 (is (true? v273_l750)))
