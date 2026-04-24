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


(def v14_l62 (kind/doc #'sk/sketch))


(def
 v16_l68
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t17_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v16_l68)))


(def v18_l76 (kind/doc #'sk/with-data))


(def
 v20_l82
 (def
  scatter-template
  (-> (sk/frame nil {:x :x, :y :y, :color :group}) sk/lay-point)))


(def v21_l86 (-> scatter-template (sk/with-data tiny)))


(deftest
 t22_l89
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v21_l86)))


(def v23_l91 (kind/doc #'sk/view))


(def
 v25_l95
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t26_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v25_l95)))


(def
 v28_l104
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t29_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v28_l104)))


(def
 v31_l113
 (->
  (rdatasets/datasets-iris)
  (sk/view [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (sk/lay-point {:color :species})))


(deftest
 t32_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v31_l113)))


(def
 v34_l124
 (->
  (rdatasets/datasets-iris)
  (sk/view {:x :sepal-length, :y :sepal-width})
  sk/lay-point))


(deftest
 t35_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v34_l124)))


(def v36_l132 (kind/doc #'sk/cross))


(def v37_l134 (sk/cross [:a :b] [1 2 3]))


(deftest
 t38_l136
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v37_l134)))


(def
 v39_l138
 (->
  (rdatasets/datasets-iris)
  (sk/view
   (sk/cross
    [:sepal-length :petal-length]
    [:sepal-width :petal-width]))
  (sk/lay-point {:color :species})))


(deftest
 t40_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v39_l138)))


(def
 v42_l149
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width]))


(deftest
 t43_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v42_l149)))


(def v45_l157 (kind/doc #'sk/lay))


(def
 v47_l165
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay :point)))


(deftest
 t48_l169
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v47_l165)))


(def v49_l171 (kind/doc #'sk/lay-point))


(def
 v50_l173
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t51_l176
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v50_l173)))


(def v52_l179 (kind/doc #'sk/lay-line))


(def
 v53_l181
 (def
  wave
  {:x (range 30),
   :y
   (map
    (fn* [p1__140617#] (Math/sin (* p1__140617# 0.3)))
    (range 30))}))


(def v54_l184 (-> wave (sk/lay-line :x :y)))


(deftest
 t55_l187
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v54_l184)))


(def v56_l190 (kind/doc #'sk/lay-histogram))


(def
 v57_l192
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t58_l195
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v57_l192)))


(def v59_l198 (kind/doc #'sk/lay-bar))


(def v60_l200 (-> (rdatasets/datasets-iris) (sk/lay-bar :species)))


(deftest
 t61_l203
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v60_l200)))


(def
 v63_l208
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:position :stack, :color :species})))


(deftest
 t64_l211
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v63_l208)))


(def
 v66_l216
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:position :fill, :color :species})))


(deftest
 t67_l219
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v66_l216)))


(def v68_l222 (kind/doc #'sk/lay-value-bar))


(def v69_l224 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t70_l227
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v69_l224)))


(def
 v72_l232
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t73_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v72_l232)))


(def v74_l240 (kind/doc #'sk/lay-smooth))


(def
 v75_l242
 (->
  (let
   [r (rng/rng :jdk 42) xs (vec (range 50))]
   {:x xs,
    :y
    (mapv
     (fn*
      [p1__140618#]
      (+
       (Math/sin (* p1__140618# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     xs)})
  (sk/lay-point :x :y)
  (sk/lay-smooth {:bandwidth 0.2})))


(deftest
 t76_l251
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v75_l242)))


(def v77_l255 (kind/doc #'sk/lay-density))


(def
 v78_l257
 (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-length)))


(deftest
 t79_l260
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v78_l257)))


(def v80_l263 (kind/doc #'sk/lay-area))


(def v81_l265 (-> wave (sk/lay-area :x :y)))


(deftest
 t82_l268
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v81_l265)))


(def
 v84_l273
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
 t85_l280
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v84_l273)))


(def v86_l283 (kind/doc #'sk/lay-text))


(def
 v87_l285
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t88_l288
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v87_l285)))


(def v89_l291 (kind/doc #'sk/lay-label))


(def
 v90_l293
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t91_l297
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v90_l293)))


(def v92_l301 (kind/doc #'sk/lay-boxplot))


(def
 v93_l303
 (-> (rdatasets/datasets-iris) (sk/lay-boxplot :species :sepal-width)))


(deftest
 t94_l306
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v93_l303)))


(def v95_l310 (kind/doc #'sk/lay-violin))


(def
 v96_l312
 (-> (rdatasets/reshape2-tips) (sk/lay-violin :day :total-bill)))


(deftest
 t97_l315
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v96_l312)))


(def v98_l318 (kind/doc #'sk/lay-errorbar))


(def
 v99_l320
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:y-min :ci-lo, :y-max :ci-hi})))


(deftest
 t100_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v99_l320)))


(def v101_l328 (kind/doc #'sk/lay-lollipop))


(def v102_l330 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t103_l333
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v102_l330)))


(def v104_l337 (kind/doc #'sk/lay-tile))


(def
 v105_l339
 (->
  (rdatasets/datasets-iris)
  (sk/lay-tile :sepal-length :sepal-width)))


(deftest
 t106_l342
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v105_l339)))


(def v107_l345 (kind/doc #'sk/lay-density-2d))


(def
 v108_l347
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density-2d :sepal-length :sepal-width)))


(deftest
 t109_l350
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v108_l347)))


(def v110_l353 (kind/doc #'sk/lay-contour))


(def
 v111_l355
 (->
  (rdatasets/datasets-iris)
  (sk/lay-contour :sepal-length :sepal-width)))


(deftest
 t112_l358
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v111_l355)))


(def v113_l361 (kind/doc #'sk/lay-ridgeline))


(def
 v114_l363
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length)))


(deftest
 t115_l366
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v114_l363)))


(def v116_l369 (kind/doc #'sk/lay-rug))


(def
 v117_l371
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rug {:side :both})))


(deftest
 t118_l375
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v117_l371)))


(def v119_l378 (kind/doc #'sk/lay-step))


(def v120_l380 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t121_l384
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v120_l380)))


(def v122_l388 (kind/doc #'sk/lay-summary))


(def
 v123_l390
 (-> (rdatasets/datasets-iris) (sk/lay-summary :species :sepal-length)))


(deftest
 t124_l393
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v123_l390)))


(def v126_l407 (kind/doc #'sk/lay-rule-v))


(def
 v127_l409
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-v {:x-intercept 6.0})))


(deftest
 t128_l413
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v127_l409)))


(def v129_l417 (kind/doc #'sk/lay-rule-h))


(def
 v130_l419
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t131_l423
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v130_l419)))


(def v132_l427 (kind/doc #'sk/lay-band-v))


(def
 v133_l429
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-v {:x-min 5.5, :x-max 6.5})))


(deftest
 t134_l433
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v133_l429)))


(def v135_l436 (kind/doc #'sk/lay-band-h))


(def
 v136_l438
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-band-h {:y-min 2.5, :y-max 3.5})))


(deftest
 t137_l442
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v136_l438)))


(def v139_l447 (kind/doc #'sk/coord))


(def
 v141_l451
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t142_l454
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v141_l451)))


(def
 v144_l459
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t145_l462
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v144_l459)))


(def v146_l465 (kind/doc #'sk/scale))


(def
 v148_l469
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x :log)))


(deftest
 t149_l472
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v148_l469)))


(def
 v151_l477
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t152_l480
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v151_l477)))


(def v154_l485 (kind/doc #'sk/facet))


(def
 v155_l487
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t156_l491
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l487)))


(def v157_l495 (kind/doc #'sk/facet-grid))


(def
 v158_l497
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t159_l501
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v158_l497)))


(def v161_l507 (kind/doc #'sk/arrange))


(def
 v162_l509
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


(deftest t163_l517 (is ((fn [v] (sk/frame? v)) v162_l509)))


(def v165_l521 (kind/doc #'sk/plot))


(def v167_l526 (-> tiny (sk/lay-point :x :y)))


(deftest
 t168_l529
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v167_l526)))


(def v169_l532 (kind/doc #'sk/options))


(def
 v171_l536
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t172_l540
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v171_l536)))


(def v174_l546 (kind/doc #'sk/sketch?))


(def v176_l550 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t177_l552 (is (true? v176_l550)))


(def v178_l554 (kind/doc #'sk/plan?))


(def v180_l558 (sk/plan? (sk/plan (sk/lay-point tiny :x :y))))


(deftest t181_l560 (is (true? v180_l558)))


(def v182_l562 (kind/doc #'sk/layer?))


(def
 v184_l566
 (sk/layer?
  (first
   (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y))))))))


(deftest t185_l568 (is (true? v184_l566)))


(def v186_l570 (kind/doc #'sk/layer-type?))


(def v188_l574 (sk/layer-type? (sk/layer-type-lookup :point)))


(deftest t189_l576 (is (true? v188_l574)))


(def v191_l580 (kind/doc #'sk/draft))


(def
 v193_l586
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  sk/draft
  kind/pprint))


(deftest
 t194_l591
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v193_l586)))


(def v195_l595 (kind/doc #'sk/plan))


(def v197_l599 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v198_l603 plan1)


(deftest
 t199_l605
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v198_l603)))


(def v200_l608 (kind/doc #'sk/svg-summary))


(def
 v201_l610
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/svg-summary))


(deftest
 t202_l613
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v201_l610)))


(def v203_l616 (kind/doc #'sk/valid-plan?))


(def v204_l618 (sk/valid-plan? plan1))


(deftest t205_l620 (is (true? v204_l618)))


(def v206_l622 (kind/doc #'sk/explain-plan))


(def v207_l624 (sk/explain-plan plan1))


(deftest t208_l626 (is (nil? v207_l624)))


(def v210_l630 (kind/doc #'sk/plan->membrane))


(def v211_l632 (def m1 (sk/plan->membrane plan1)))


(def v212_l634 (vector? m1))


(deftest t213_l636 (is (true? v212_l634)))


(def v214_l638 (kind/doc #'sk/membrane->plot))


(def
 v215_l640
 (first
  (sk/membrane->plot
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t216_l644 (is ((fn [v] (= :svg v)) v215_l640)))


(def v217_l646 (kind/doc #'sk/plan->plot))


(def v218_l648 (first (sk/plan->plot plan1 :svg {})))


(deftest t219_l650 (is ((fn [v] (= :svg v)) v218_l648)))


(def v221_l654 (kind/doc #'sk/config))


(def v222_l656 (sk/config))


(deftest t223_l658 (is ((fn [m] (map? m)) v222_l656)))


(def v224_l660 (kind/doc #'sk/set-config!))


(def v225_l662 (kind/doc #'sk/with-config))


(def
 v226_l664
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t227_l667 (is ((fn [p] (= :pastel1 p)) v226_l664)))


(def v229_l673 (kind/doc #'sk/config-key-docs))


(def v230_l675 (count sk/config-key-docs))


(deftest t231_l677 (is ((fn [n] (= 36 n)) v230_l675)))


(def v232_l679 (kind/doc #'sk/plot-option-docs))


(def v233_l681 (count sk/plot-option-docs))


(deftest t234_l683 (is ((fn [n] (= 11 n)) v233_l681)))


(def v235_l685 (kind/doc #'sk/layer-option-docs))


(def v236_l687 (count sk/layer-option-docs))


(deftest t237_l689 (is ((fn [n] (pos? n)) v236_l687)))


(def v239_l693 (kind/doc #'sk/layer-type-lookup))


(def v240_l695 (sk/layer-type-lookup :smooth))


(deftest
 t241_l697
 (is
  ((fn [m] (and (= :line (:mark m)) (= :loess (:stat m)))) v240_l695)))


(def v242_l700 (kind/doc #'sk/registered-layer-types))


(def v243_l702 (count (sk/registered-layer-types)))


(deftest t244_l704 (is ((fn [n] (= 25 n)) v243_l702)))


(def v246_l710 (kind/doc #'sk/stat-doc))


(def v247_l712 (sk/stat-doc :linear-model))


(deftest t248_l714 (is ((fn [s] (string? s)) v247_l712)))


(def v249_l716 (kind/doc #'sk/mark-doc))


(def v250_l718 (sk/mark-doc :point))


(deftest t251_l720 (is ((fn [s] (string? s)) v250_l718)))


(def v252_l722 (kind/doc #'sk/position-doc))


(def v253_l724 (sk/position-doc :dodge))


(deftest t254_l726 (is ((fn [s] (string? s)) v253_l724)))


(def v255_l728 (kind/doc #'sk/scale-doc))


(def v256_l730 (sk/scale-doc :linear))


(deftest t257_l732 (is ((fn [s] (string? s)) v256_l730)))


(def v258_l734 (kind/doc #'sk/coord-doc))


(def v259_l736 (sk/coord-doc :cartesian))


(deftest t260_l738 (is ((fn [s] (string? s)) v259_l736)))


(def v261_l740 (kind/doc #'sk/membrane-mark-doc))


(def v262_l742 (sk/membrane-mark-doc :point))


(deftest t263_l744 (is ((fn [s] (string? s)) v262_l742)))


(def v265_l748 (kind/doc #'sk/save))


(def
 v267_l752
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg")))


(deftest t268_l758 (is (true? v267_l752)))


(def v269_l760 (kind/doc #'sk/save-png))


(def
 v271_l765
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/save-png path))
  (.exists (java.io.File. path))))


(deftest t272_l771 (is (true? v271_l765)))
