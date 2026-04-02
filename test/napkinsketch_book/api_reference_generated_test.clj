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
  (sk/view data/iris {:x :sepal_length, :y :sepal_width})
  sk/lay-point))


(deftest
 t19_l75
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v18_l72)))


(def v20_l79 (kind/doc #'sk/lay))


(def
 v22_l83
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t23_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l83)))


(def v25_l94 (kind/doc #'sk/lay-point))


(def
 v26_l96
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t27_l99
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v26_l96)))


(def v28_l102 (kind/doc #'sk/lay-line))


(def
 v29_l104
 (def
  wave
  {:x (range 30),
   :y
   (map
    (fn* [p1__121899#] (Math/sin (* p1__121899# 0.3)))
    (range 30))}))


(def v30_l107 (-> wave (sk/lay-line :x :y)))


(deftest
 t31_l110
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v30_l107)))


(def v32_l113 (kind/doc #'sk/lay-histogram))


(def v33_l115 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t34_l118
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l115)))


(def v35_l121 (kind/doc #'sk/lay-bar))


(def v36_l123 (-> data/iris (sk/lay-bar :species)))


(deftest
 t37_l126
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v36_l123)))


(def v38_l129 (kind/doc #'sk/lay-stacked-bar))


(def
 v39_l131
 (-> data/penguins (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t40_l134
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v39_l131)))


(def v41_l137 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v42_l139
 (-> data/penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t43_l142
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l139)))


(def v44_l145 (kind/doc #'sk/lay-value-bar))


(def v45_l147 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t46_l150
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v45_l147)))


(def v47_l153 (kind/doc #'sk/lay-lm))


(def
 v48_l155
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t49_l159
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l155)))


(def v50_l163 (kind/doc #'sk/lay-loess))


(def
 v51_l165
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__121900#]
      (+
       (Math/sin (* p1__121900# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v52_l170 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t53_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v52_l170)))


(def v54_l178 (kind/doc #'sk/lay-density))


(def v55_l180 (-> data/iris (sk/lay-density :sepal_length)))


(deftest
 t56_l183
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v55_l180)))


(def v57_l186 (kind/doc #'sk/lay-area))


(def v58_l188 (-> wave (sk/lay-area :x :y)))


(deftest
 t59_l191
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l188)))


(def v60_l194 (kind/doc #'sk/lay-stacked-area))


(def
 v61_l196
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
 t62_l203
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v61_l196)))


(def v63_l206 (kind/doc #'sk/lay-text))


(def
 v64_l208
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t65_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v64_l208)))


(def v66_l214 (kind/doc #'sk/lay-label))


(def
 v67_l216
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t68_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v67_l216)))


(def v69_l223 (kind/doc #'sk/lay-boxplot))


(def v70_l225 (-> data/iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t71_l228
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v70_l225)))


(def v72_l232 (kind/doc #'sk/lay-violin))


(def v73_l234 (-> data/tips (sk/lay-violin :day :total_bill)))


(deftest
 t74_l237
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v73_l234)))


(def v75_l240 (kind/doc #'sk/lay-errorbar))


(def
 v76_l242
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t77_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v76_l242)))


(def v78_l250 (kind/doc #'sk/lay-lollipop))


(def v79_l252 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t80_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v79_l252)))


(def v81_l259 (kind/doc #'sk/lay-tile))


(def v82_l261 (-> data/iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t83_l264
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v82_l261)))


(def v84_l267 (kind/doc #'sk/lay-density2d))


(def
 v85_l269
 (-> data/iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t86_l272
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v85_l269)))


(def v87_l275 (kind/doc #'sk/lay-contour))


(def
 v88_l277
 (-> data/iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t89_l280
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v88_l277)))


(def v90_l283 (kind/doc #'sk/lay-ridgeline))


(def v91_l285 (-> data/iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t92_l288
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v91_l285)))


(def v93_l291 (kind/doc #'sk/lay-rug))


(def
 v94_l293
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-rug {:side :both})))


(deftest
 t95_l297
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v94_l293)))


(def v96_l300 (kind/doc #'sk/lay-step))


(def v97_l302 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t98_l306
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v97_l302)))


(def v99_l310 (kind/doc #'sk/lay-summary))


(def v100_l312 (-> data/iris (sk/lay-summary :species :sepal_length)))


(deftest
 t101_l315
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v100_l312)))


(def v103_l321 (kind/doc #'sk/plot))


(def v105_l326 (-> tiny (sk/lay-point :x :y)))


(deftest
 t106_l329
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v105_l326)))


(def v107_l332 (kind/doc #'sk/options))


(def
 v109_l336
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t110_l340
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v109_l336)))


(def v111_l344 (kind/doc #'sk/sketch?))


(def v113_l348 (sk/sketch? (sk/lay-point tiny :x :y)))


(deftest t114_l350 (is (true? v113_l348)))


(def v115_l352 (kind/doc #'sk/views-of))


(def
 v117_l356
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t118_l358 (is ((fn [v] (= 2 v)) v117_l356)))


(def v119_l360 (kind/doc #'sk/plan))


(def v121_l364 (def sk1 (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v122_l368 sk1)


(deftest
 t123_l370
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v122_l368)))


(def v125_l375 (kind/doc #'sk/views->plan))


(def v126_l377 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->plan)))


(def v127_l381 (= (keys sk1) (keys sk2)))


(deftest t128_l383 (is (true? v127_l381)))


(def v129_l385 (kind/doc #'sk/plan->membrane))


(def v130_l387 (def m1 (sk/plan->membrane sk1)))


(def v131_l389 (vector? m1))


(deftest t132_l391 (is (true? v131_l389)))


(def v133_l393 (kind/doc #'sk/membrane->figure))


(def
 v134_l395
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t135_l399 (is ((fn [v] (= :svg v)) v134_l395)))


(def v136_l401 (kind/doc #'sk/plan->figure))


(def v137_l403 (first (sk/plan->figure sk1 :svg {})))


(deftest t138_l405 (is ((fn [v] (= :svg v)) v137_l403)))


(def v140_l409 (kind/doc #'sk/coord))


(def v142_l413 (-> data/iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t143_l416
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v142_l413)))


(def v145_l421 (-> data/iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t146_l424
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v145_l421)))


(def v147_l427 (kind/doc #'sk/scale))


(def
 v149_l431
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x :log)))


(deftest
 t150_l434
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v149_l431)))


(def
 v152_l439
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t153_l442
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l439)))


(def v155_l446 (kind/doc #'sk/rule-v))


(def
 v156_l448
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t157_l451
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v156_l448)))


(def v158_l455 (kind/doc #'sk/rule-h))


(def
 v159_l457
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t160_l460
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v159_l457)))


(def v161_l464 (kind/doc #'sk/band-v))


(def
 v162_l466
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t163_l469
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v162_l466)))


(def v164_l472 (kind/doc #'sk/band-h))


(def
 v165_l474
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t166_l477
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v165_l474)))


(def v168_l482 (kind/doc #'sk/cross))


(def v169_l484 (sk/cross [:a :b] [1 2 3]))


(deftest
 t170_l486
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v169_l484)))


(def
 v171_l488
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t172_l493
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v171_l488)))


(def v173_l497 (kind/doc #'sk/distribution))


(def
 v174_l499
 (->
  (sk/distribution data/iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t175_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v174_l499)))


(def v177_l508 (kind/doc #'sk/facet))


(def
 v178_l510
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t179_l514
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v178_l510)))


(def v180_l518 (kind/doc #'sk/facet-grid))


(def
 v181_l520
 (->
  data/tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t182_l524
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v181_l520)))


(def v184_l530 (kind/doc #'sk/svg-summary))


(def
 v185_l532
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t186_l535
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v185_l532)))


(def v187_l538 (kind/doc #'sk/valid-plan?))


(def v188_l540 (sk/valid-plan? sk1))


(deftest t189_l542 (is (true? v188_l540)))


(def v190_l544 (kind/doc #'sk/explain-plan))


(def v191_l546 (sk/explain-plan sk1))


(deftest t192_l548 (is (nil? v191_l546)))


(def v194_l552 (kind/doc #'sk/config))


(def v195_l554 (sk/config))


(deftest t196_l556 (is ((fn [m] (map? m)) v195_l554)))


(def v197_l558 (kind/doc #'sk/set-config!))


(def v198_l560 (kind/doc #'sk/with-config))


(def
 v199_l562
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t200_l565 (is ((fn [p] (= :pastel1 p)) v199_l562)))


(def v202_l571 (kind/doc #'sk/config-key-docs))


(def v203_l573 (count sk/config-key-docs))


(deftest t204_l575 (is ((fn [n] (= 36 n)) v203_l573)))


(def v205_l577 (kind/doc #'sk/plot-option-docs))


(def v206_l579 (count sk/plot-option-docs))


(deftest t207_l581 (is ((fn [n] (= 6 n)) v206_l579)))


(def v208_l583 (kind/doc #'sk/layer-option-docs))


(def v209_l585 (count sk/layer-option-docs))


(deftest t210_l587 (is ((fn [n] (= 20 n)) v209_l585)))


(def v212_l591 (kind/doc #'sk/method-lookup))


(def v213_l593 (sk/method-lookup :lm))


(deftest
 t214_l595
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v213_l593)))


(def v215_l598 (kind/doc #'sk/method-registered))


(def v216_l600 (count (sk/method-registered)))


(deftest t217_l602 (is ((fn [n] (= 25 n)) v216_l600)))


(def v219_l608 (kind/doc #'sk/stat-doc))


(def v220_l610 (sk/stat-doc :lm))


(deftest t221_l612 (is ((fn [s] (string? s)) v220_l610)))


(def v222_l614 (kind/doc #'sk/mark-doc))


(def v223_l616 (sk/mark-doc :point))


(deftest t224_l618 (is ((fn [s] (string? s)) v223_l616)))


(def v225_l620 (kind/doc #'sk/position-doc))


(def v226_l622 (sk/position-doc :dodge))


(deftest t227_l624 (is ((fn [s] (string? s)) v226_l622)))


(def v228_l626 (kind/doc #'sk/scale-doc))


(def v229_l628 (sk/scale-doc :linear))


(deftest t230_l630 (is ((fn [s] (string? s)) v229_l628)))


(def v231_l632 (kind/doc #'sk/coord-doc))


(def v232_l634 (sk/coord-doc :cartesian))


(deftest t233_l636 (is ((fn [s] (string? s)) v232_l634)))


(def v234_l638 (kind/doc #'sk/membrane-mark-doc))


(def v235_l640 (sk/membrane-mark-doc :point))


(deftest t236_l642 (is ((fn [s] (string? s)) v235_l640)))


(def v238_l646 (kind/doc #'sk/arrange))


(def
 v239_l648
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


(deftest t240_l654 (is ((fn [v] (= :div (first v))) v239_l648)))


(def v242_l657 (kind/doc #'sk/save))


(def
 v244_l661
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


(deftest t245_l667 (is (true? v244_l661)))
