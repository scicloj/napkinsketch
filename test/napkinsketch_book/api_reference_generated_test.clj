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
   (map (fn* [p1__75946#] (Math/sin (* p1__75946# 0.3))) (range 30))}))


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
      [p1__75947#]
      (+
       (Math/sin (* p1__75947# 0.2))
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


(def v111_l344 (kind/doc #'sk/plot-spec?))


(def v113_l348 (sk/plot-spec? (sk/lay-point tiny :x :y)))


(deftest t114_l350 (is (true? v113_l348)))


(def v115_l352 (kind/doc #'sk/views-of))


(def
 v117_l356
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t118_l358 (is ((fn [v] (= 2 v)) v117_l356)))


(def v119_l360 (kind/doc #'sk/sketch))


(def v121_l364 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v122_l368 sk1)


(deftest
 t123_l370
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v122_l368)))


(def v125_l375 (kind/doc #'sk/views->sketch))


(def
 v126_l377
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v127_l381 (= (keys sk1) (keys sk2)))


(deftest t128_l383 (is (true? v127_l381)))


(def v129_l385 (kind/doc #'sk/sketch->membrane))


(def v130_l387 (def m1 (sk/sketch->membrane sk1)))


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


(def v136_l401 (kind/doc #'sk/sketch->figure))


(def v137_l403 (first (sk/sketch->figure sk1 :svg {})))


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


(def v154_l445 (kind/doc #'sk/labs))


(def
 v155_l447
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t156_l450
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v155_l447)))


(def v158_l455 (kind/doc #'sk/rule-v))


(def
 v159_l457
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t160_l460
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v159_l457)))


(def v161_l464 (kind/doc #'sk/rule-h))


(def
 v162_l466
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t163_l469
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v162_l466)))


(def v164_l473 (kind/doc #'sk/band-v))


(def
 v165_l475
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t166_l478
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v165_l475)))


(def v167_l481 (kind/doc #'sk/band-h))


(def
 v168_l483
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t169_l486
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v168_l483)))


(def v171_l491 (kind/doc #'sk/cross))


(def v172_l493 (sk/cross [:a :b] [1 2 3]))


(deftest
 t173_l495
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v172_l493)))


(def
 v174_l497
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t175_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v174_l497)))


(def v176_l506 (kind/doc #'sk/distribution))


(def
 v177_l508
 (->
  (sk/distribution data/iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t178_l511
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v177_l508)))


(def v180_l517 (kind/doc #'sk/facet))


(def
 v181_l519
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t182_l523
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v181_l519)))


(def v183_l527 (kind/doc #'sk/facet-grid))


(def
 v184_l529
 (->
  data/tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t185_l533
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v184_l529)))


(def v187_l539 (kind/doc #'sk/svg-summary))


(def
 v188_l541
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t189_l544
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v188_l541)))


(def v190_l547 (kind/doc #'sk/valid-sketch?))


(def v191_l549 (sk/valid-sketch? sk1))


(deftest t192_l551 (is (true? v191_l549)))


(def v193_l553 (kind/doc #'sk/explain-sketch))


(def v194_l555 (sk/explain-sketch sk1))


(deftest t195_l557 (is (nil? v194_l555)))


(def v197_l561 (kind/doc #'sk/config))


(def v198_l563 (sk/config))


(deftest t199_l565 (is ((fn [m] (map? m)) v198_l563)))


(def v200_l567 (kind/doc #'sk/set-config!))


(def v201_l569 (kind/doc #'sk/with-config))


(def
 v202_l571
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t203_l574 (is ((fn [p] (= :pastel1 p)) v202_l571)))


(def v205_l580 (kind/doc #'sk/config-key-docs))


(def v206_l582 (count sk/config-key-docs))


(deftest t207_l584 (is ((fn [n] (= 36 n)) v206_l582)))


(def v208_l586 (kind/doc #'sk/plot-option-docs))


(def v209_l588 (count sk/plot-option-docs))


(deftest t210_l590 (is ((fn [n] (= 6 n)) v209_l588)))


(def v211_l592 (kind/doc #'sk/layer-option-docs))


(def v212_l594 (count sk/layer-option-docs))


(deftest t213_l596 (is ((fn [n] (= 17 n)) v212_l594)))


(def v215_l600 (kind/doc #'sk/method-lookup))


(def v216_l602 (sk/method-lookup :lm))


(deftest
 t217_l604
 (is ((fn [m] (and (= :line (:mark m)) (= :lm (:stat m)))) v216_l602)))


(def v218_l607 (kind/doc #'sk/method-registered))


(def v219_l609 (count (sk/method-registered)))


(deftest t220_l611 (is ((fn [n] (= 25 n)) v219_l609)))


(def v222_l617 (kind/doc #'sk/stat-doc))


(def v223_l619 (sk/stat-doc :lm))


(deftest t224_l621 (is ((fn [s] (string? s)) v223_l619)))


(def v225_l623 (kind/doc #'sk/mark-doc))


(def v226_l625 (sk/mark-doc :point))


(deftest t227_l627 (is ((fn [s] (string? s)) v226_l625)))


(def v228_l629 (kind/doc #'sk/position-doc))


(def v229_l631 (sk/position-doc :dodge))


(deftest t230_l633 (is ((fn [s] (string? s)) v229_l631)))


(def v231_l635 (kind/doc #'sk/scale-doc))


(def v232_l637 (sk/scale-doc :linear))


(deftest t233_l639 (is ((fn [s] (string? s)) v232_l637)))


(def v234_l641 (kind/doc #'sk/coord-doc))


(def v235_l643 (sk/coord-doc :cartesian))


(deftest t236_l645 (is ((fn [s] (string? s)) v235_l643)))


(def v237_l647 (kind/doc #'sk/membrane-mark-doc))


(def v238_l649 (sk/membrane-mark-doc :point))


(deftest t239_l651 (is ((fn [s] (string? s)) v238_l649)))


(def v241_l655 (kind/doc #'sk/arrange))


(def
 v242_l657
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


(deftest t243_l663 (is ((fn [v] (= :div (first v))) v242_l657)))


(def v245_l666 (kind/doc #'sk/save))


(def
 v247_l670
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


(deftest t248_l676 (is (true? v247_l670)))
