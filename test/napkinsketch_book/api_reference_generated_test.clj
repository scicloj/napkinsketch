(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
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


(def v9_l43 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l43)))


(def v12_l52 (-> data/iris (sk/lay-histogram :sepal_length)))


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
  data/iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
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
  data/iris
  (sk/view {:x :sepal_length, :y :sepal_width})
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
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
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


(def v25_l95 (kind/doc #'sk/lay-point))


(def
 v26_l97
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t27_l100
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v26_l97)))


(def v28_l103 (kind/doc #'sk/lay-line))


(def
 v29_l105
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__80509#] (Math/sin (* p1__80509# 0.3))) (range 30))}))


(def v30_l108 (-> wave (sk/lay-line :x :y)))


(deftest
 t31_l111
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v30_l108)))


(def v32_l114 (kind/doc #'sk/lay-histogram))


(def v33_l116 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t34_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l116)))


(def v35_l122 (kind/doc #'sk/lay-bar))


(def v36_l124 (-> data/iris (sk/lay-bar :species)))


(deftest
 t37_l127
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v36_l124)))


(def v38_l130 (kind/doc #'sk/lay-stacked-bar))


(def
 v39_l132
 (-> data/penguins (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t40_l135
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v39_l132)))


(def v41_l138 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v42_l140
 (-> data/penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t43_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l140)))


(def v44_l146 (kind/doc #'sk/lay-value-bar))


(def v45_l148 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t46_l151
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v45_l148)))


(def v47_l154 (kind/doc #'sk/lay-lm))


(def
 v48_l156
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t49_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l156)))


(def v50_l164 (kind/doc #'sk/lay-loess))


(def
 v51_l166
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__80510#]
      (+
       (Math/sin (* p1__80510# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v52_l171 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t53_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v52_l171)))


(def v54_l179 (kind/doc #'sk/lay-density))


(def v55_l181 (-> data/iris (sk/lay-density :sepal_length)))


(deftest
 t56_l184
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v55_l181)))


(def v57_l187 (kind/doc #'sk/lay-area))


(def v58_l189 (-> wave (sk/lay-area :x :y)))


(deftest
 t59_l192
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l189)))


(def v60_l195 (kind/doc #'sk/lay-stacked-area))


(def
 v61_l197
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
 t62_l204
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v61_l197)))


(def v63_l207 (kind/doc #'sk/lay-text))


(def
 v64_l209
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t65_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v64_l209)))


(def v66_l215 (kind/doc #'sk/lay-label))


(def
 v67_l217
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t68_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v67_l217)))


(def v69_l224 (kind/doc #'sk/lay-boxplot))


(def v70_l226 (-> data/iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t71_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v70_l226)))


(def v72_l233 (kind/doc #'sk/lay-violin))


(def v73_l235 (-> data/tips (sk/lay-violin :day :total_bill)))


(deftest
 t74_l238
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v73_l235)))


(def v75_l241 (kind/doc #'sk/lay-errorbar))


(def
 v76_l243
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t77_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v76_l243)))


(def v78_l251 (kind/doc #'sk/lay-lollipop))


(def v79_l253 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t80_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v79_l253)))


(def v81_l260 (kind/doc #'sk/lay-tile))


(def v82_l262 (-> data/iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t83_l265
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v82_l262)))


(def v84_l268 (kind/doc #'sk/lay-density2d))


(def
 v85_l270
 (-> data/iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t86_l273
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v85_l270)))


(def v87_l276 (kind/doc #'sk/lay-contour))


(def
 v88_l278
 (-> data/iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t89_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v88_l278)))


(def v90_l284 (kind/doc #'sk/lay-ridgeline))


(def v91_l286 (-> data/iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t92_l289
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v91_l286)))


(def v93_l292 (kind/doc #'sk/lay-rug))


(def
 v94_l294
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-rug {:side :both})))


(deftest
 t95_l298
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v94_l294)))


(def v96_l301 (kind/doc #'sk/lay-step))


(def v97_l303 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t98_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v97_l303)))


(def v99_l311 (kind/doc #'sk/lay-summary))


(def v100_l313 (-> data/iris (sk/lay-summary :species :sepal_length)))


(deftest
 t101_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v100_l313)))


(def v103_l322 (kind/doc #'sk/plot))


(def v105_l327 (-> tiny (sk/lay-point :x :y)))


(deftest
 t106_l330
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v105_l327)))


(def v107_l333 (kind/doc #'sk/options))


(def
 v109_l337
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t110_l341
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v109_l337)))


(def v111_l345 (kind/doc #'sk/sketch?))


(def v113_l349 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t114_l351 (is (true? v113_l349)))


(def v115_l353 (kind/doc #'sk/plan))


(def
 v117_l358
 (let
  [sk (-> tiny (sk/lay-point :x :y) sk/lay-lm)]
  [(count (:entries sk)) (count (:methods sk))]))


(deftest
 t118_l361
 (is
  ((fn [[entries globals]] (and (= 1 entries) (= 1 globals)))
   v117_l358)))


(def v119_l363 (kind/doc #'sk/plan))


(def v121_l367 (def plan1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v122_l371 plan1)


(deftest
 t123_l373
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v122_l371)))


(def v125_l378 (kind/doc #'sk/plan->membrane))


(def v126_l380 (def m1 (sk/plan->membrane plan1)))


(def v127_l382 (vector? m1))


(deftest t128_l384 (is (true? v127_l382)))


(def v129_l386 (kind/doc #'sk/membrane->figure))


(def
 v130_l388
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width plan1),
    :total-height (:total-height plan1)})))


(deftest t131_l392 (is ((fn [v] (= :svg v)) v130_l388)))


(def v132_l394 (kind/doc #'sk/plan->figure))


(def v133_l396 (first (sk/plan->figure plan1 :svg {})))


(deftest t134_l398 (is ((fn [v] (= :svg v)) v133_l396)))


(def v136_l402 (kind/doc #'sk/coord))


(def v138_l406 (-> data/iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t139_l409
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v138_l406)))


(def v141_l414 (-> data/iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t142_l417
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v141_l414)))


(def v143_l420 (kind/doc #'sk/scale))


(def
 v145_l424
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x :log)))


(deftest
 t146_l427
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v145_l424)))


(def
 v148_l432
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t149_l435
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v148_l432)))


(def v151_l439 (kind/doc #'sk/rule-v))


(def
 v152_l441
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/annotate (sk/rule-v 6.0))))


(deftest
 t153_l444
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v152_l441)))


(def v154_l448 (kind/doc #'sk/rule-h))


(def
 v155_l450
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t156_l453
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v155_l450)))


(def v157_l457 (kind/doc #'sk/band-v))


(def
 v158_l459
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/annotate (sk/band-v 5.5 6.5))))


(deftest
 t159_l462
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v158_l459)))


(def v160_l465 (kind/doc #'sk/band-h))


(def
 v161_l467
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/annotate (sk/band-h 2.5 3.5))))


(deftest
 t162_l470
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v161_l467)))


(def v164_l475 (kind/doc #'sk/cross))


(def v165_l477 (sk/cross [:a :b] [1 2 3]))


(deftest
 t166_l479
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v165_l477)))


(def
 v167_l481
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t168_l486
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v167_l481)))


(def
 v170_l492
 (sk/lay-histogram data/iris [:sepal_length :sepal_width]))


(deftest
 t171_l494
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v170_l492)))


(def v173_l500 (kind/doc #'sk/facet))


(def
 v174_l502
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t175_l506
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v174_l502)))


(def v176_l510 (kind/doc #'sk/facet-grid))


(def
 v177_l512
 (->
  data/tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t178_l516
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v177_l512)))


(def v180_l522 (kind/doc #'sk/svg-summary))


(def
 v181_l524
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t182_l527
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v181_l524)))


(def v183_l530 (kind/doc #'sk/valid-plan?))


(def v184_l532 (sk/valid-plan? plan1))


(deftest t185_l534 (is (true? v184_l532)))


(def v186_l536 (kind/doc #'sk/explain-plan))


(def v187_l538 (sk/explain-plan plan1))


(deftest t188_l540 (is (nil? v187_l538)))


(def v190_l544 (kind/doc #'sk/config))


(def v191_l546 (sk/config))


(deftest t192_l548 (is ((fn [m] (map? m)) v191_l546)))


(def v193_l550 (kind/doc #'sk/set-config!))


(def v194_l552 (kind/doc #'sk/with-config))


(def
 v195_l554
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t196_l557 (is ((fn [p] (= :pastel1 p)) v195_l554)))


(def v198_l563 (kind/doc #'sk/config-key-docs))


(def v199_l565 (count sk/config-key-docs))


(deftest t200_l567 (is ((fn [n] (= 36 n)) v199_l565)))


(def v201_l569 (kind/doc #'sk/plot-option-docs))


(def v202_l571 (count sk/plot-option-docs))


(deftest t203_l573 (is ((fn [n] (= 6 n)) v202_l571)))


(def v204_l575 (kind/doc #'sk/layer-option-docs))


(def v205_l577 (count sk/layer-option-docs))


(deftest t206_l579 (is ((fn [n] (= 20 n)) v205_l577)))


(def v208_l583 (kind/doc #'sk/method-lookup))


(def v209_l585 (sk/method-lookup :lm))


(deftest
 t210_l587
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v209_l585)))


(def v211_l590 (kind/doc #'sk/registered-methods))


(def v212_l592 (count (sk/registered-methods)))


(deftest t213_l594 (is ((fn [n] (= 25 n)) v212_l592)))


(def v215_l600 (kind/doc #'sk/stat-doc))


(def v216_l602 (sk/stat-doc :lm))


(deftest t217_l604 (is ((fn [s] (string? s)) v216_l602)))


(def v218_l606 (kind/doc #'sk/mark-doc))


(def v219_l608 (sk/mark-doc :point))


(deftest t220_l610 (is ((fn [s] (string? s)) v219_l608)))


(def v221_l612 (kind/doc #'sk/position-doc))


(def v222_l614 (sk/position-doc :dodge))


(deftest t223_l616 (is ((fn [s] (string? s)) v222_l614)))


(def v224_l618 (kind/doc #'sk/scale-doc))


(def v225_l620 (sk/scale-doc :linear))


(deftest t226_l622 (is ((fn [s] (string? s)) v225_l620)))


(def v227_l624 (kind/doc #'sk/coord-doc))


(def v228_l626 (sk/coord-doc :cartesian))


(deftest t229_l628 (is ((fn [s] (string? s)) v228_l626)))


(def v230_l630 (kind/doc #'sk/membrane-mark-doc))


(def v231_l632 (sk/membrane-mark-doc :point))


(deftest t232_l634 (is ((fn [s] (string? s)) v231_l632)))


(def v234_l638 (kind/doc #'sk/arrange))


(def
 v235_l640
 (sk/arrange
  [(->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:width 250, :height 200}))
   (->
    data/iris
    (sk/lay-point :petal_length :petal_width {:color :species})
    (sk/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t236_l646 (is ((fn [v] (= :div (first v))) v235_l640)))


(def v238_l649 (kind/doc #'sk/save))


(def
 v240_l653
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save
   (->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))
   path
   {:title "Iris Export"})
  (.contains (slurp path) "<svg")))


(deftest t241_l659 (is (true? v240_l653)))
