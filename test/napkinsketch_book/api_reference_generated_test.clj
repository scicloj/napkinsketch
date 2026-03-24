(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l28
 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]}))


(def
 v5_l32
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def
 v6_l35
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v7_l38
 (def
  measurements
  {:treatment ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def v9_l45 (kind/doc #'sk/view))


(def v11_l49 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t12_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def v14_l58 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t15_l61
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l58)))


(def
 v17_l67
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t18_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l67)))


(def
 v20_l78
 (-> (sk/view iris {:x :sepal_length, :y :sepal_width}) sk/lay-point))


(deftest
 t21_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l78)))


(def v22_l85 (kind/doc #'sk/lay))


(def
 v24_l89
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t25_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l89)))


(def v27_l100 (kind/doc #'sk/lay-point))


(def
 v28_l102
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t29_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l102)))


(def v30_l108 (kind/doc #'sk/lay-line))


(def
 v31_l110
 (def
  wave
  {:x (range 30),
   :y
   (mapv (fn* [p1__76031#] (Math/sin (* p1__76031# 0.3))) (range 30))}))


(def v32_l113 (-> wave (sk/lay-line :x :y)))


(deftest
 t33_l116
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l113)))


(def v34_l119 (kind/doc #'sk/lay-histogram))


(def v35_l121 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t36_l124
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l121)))


(def v37_l127 (kind/doc #'sk/lay-bar))


(def v38_l129 (-> iris (sk/lay-bar :species)))


(deftest
 t39_l132
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l129)))


(def v40_l135 (kind/doc #'sk/lay-stacked-bar))


(def
 v41_l137
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l140
 (-> penguins (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t43_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l140)))


(def v44_l146 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v45_l148
 (-> penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t46_l151
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l148)))


(def v47_l154 (kind/doc #'sk/lay-value-bar))


(def v48_l156 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t49_l159
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l156)))


(def v50_l162 (kind/doc #'sk/lay-lm))


(def
 v51_l164
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t52_l168
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l164)))


(def v53_l172 (kind/doc #'sk/lay-loess))


(def
 v54_l174
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__76032#]
      (+
       (Math/sin (* p1__76032# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v55_l179 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t56_l183
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l179)))


(def v57_l187 (kind/doc #'sk/lay-density))


(def v58_l189 (-> iris (sk/lay-density :sepal_length)))


(deftest
 t59_l192
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l189)))


(def v60_l195 (kind/doc #'sk/lay-area))


(def v61_l197 (-> wave (sk/lay-area :x :y)))


(deftest
 t62_l200
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l197)))


(def v63_l203 (kind/doc #'sk/lay-stacked-area))


(def
 v64_l205
 (->
  {:x (vec (concat (range 10) (range 10) (range 10))),
   :y
   (vec
    (concat
     [1 2 3 4 5 4 3 2 1 0]
     [2 2 2 3 3 3 2 2 2 2]
     [1 1 1 1 2 2 2 1 1 1])),
   :group
   (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))}
  (sk/lay-stacked-area :x :y {:color :group})))


(deftest
 t65_l212
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l205)))


(def v66_l215 (kind/doc #'sk/lay-text))


(def
 v67_l217
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t68_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l217)))


(def v69_l223 (kind/doc #'sk/lay-label))


(def
 v70_l225
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t71_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l225)))


(def v72_l232 (kind/doc #'sk/lay-boxplot))


(def v73_l234 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t74_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l234)))


(def v75_l241 (kind/doc #'sk/lay-violin))


(def v76_l243 (-> tips (sk/lay-violin :day :total_bill)))


(deftest
 t77_l246
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l243)))


(def v78_l249 (kind/doc #'sk/lay-errorbar))


(def
 v79_l251
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t80_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l251)))


(def v81_l259 (kind/doc #'sk/lay-lollipop))


(def v82_l261 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t83_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l261)))


(def v84_l268 (kind/doc #'sk/lay-tile))


(def v85_l270 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t86_l273
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l270)))


(def v87_l276 (kind/doc #'sk/lay-density2d))


(def v88_l278 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t89_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l278)))


(def v90_l284 (kind/doc #'sk/lay-contour))


(def v91_l286 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t92_l289
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l286)))


(def v93_l292 (kind/doc #'sk/lay-ridgeline))


(def v94_l294 (-> iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t95_l297
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l294)))


(def v96_l300 (kind/doc #'sk/lay-rug))


(def
 v97_l302
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-rug {:side :both})))


(deftest
 t98_l306
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l302)))


(def v99_l309 (kind/doc #'sk/lay-step))


(def v100_l311 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t101_l315
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l311)))


(def v102_l319 (kind/doc #'sk/lay-summary))


(def v103_l321 (-> iris (sk/lay-summary :species :sepal_length)))


(deftest
 t104_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l321)))


(def v106_l330 (kind/doc #'sk/plot))


(def v108_l335 (-> tiny (sk/lay-point :x :y)))


(deftest
 t109_l338
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l335)))


(def v110_l341 (kind/doc #'sk/options))


(def
 v112_l345
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t113_l349
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v112_l345)))


(def v114_l353 (kind/doc #'sk/plot-spec?))


(def v116_l357 (sk/plot-spec? (sk/lay-point tiny :x :y)))


(deftest t117_l359 (is (true? v116_l357)))


(def v118_l361 (kind/doc #'sk/views-of))


(def
 v120_l365
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t121_l367 (is ((fn [v] (= 2 v)) v120_l365)))


(def v122_l369 (kind/doc #'sk/sketch))


(def v124_l373 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v125_l377 sk1)


(deftest
 t126_l379
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v125_l377)))


(def v128_l384 (kind/doc #'sk/views->sketch))


(def
 v129_l386
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v130_l390 (= (keys sk1) (keys sk2)))


(deftest t131_l392 (is (true? v130_l390)))


(def v132_l394 (kind/doc #'sk/sketch->membrane))


(def v133_l396 (def m1 (sk/sketch->membrane sk1)))


(def v134_l398 (vector? m1))


(deftest t135_l400 (is (true? v134_l398)))


(def v136_l402 (kind/doc #'sk/membrane->figure))


(def
 v137_l404
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t138_l408 (is ((fn [v] (= :svg v)) v137_l404)))


(def v139_l410 (kind/doc #'sk/sketch->figure))


(def v140_l412 (first (sk/sketch->figure sk1 :svg {})))


(deftest t141_l414 (is ((fn [v] (= :svg v)) v140_l412)))


(def v143_l418 (kind/doc #'sk/coord))


(def v145_l422 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t146_l425
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v145_l422)))


(def v148_l430 (-> iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t149_l433
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v148_l430)))


(def v150_l436 (kind/doc #'sk/scale))


(def
 v152_l440
 (-> iris (sk/lay-point :sepal_length :sepal_width) (sk/scale :x :log)))


(deftest
 t153_l443
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l440)))


(def
 v155_l448
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t156_l451
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v155_l448)))


(def v157_l454 (kind/doc #'sk/labs))


(def
 v158_l456
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t159_l459
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v158_l456)))


(def v161_l464 (kind/doc #'sk/rule-v))


(def
 v162_l466
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t163_l469
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v162_l466)))


(def v164_l473 (kind/doc #'sk/rule-h))


(def
 v165_l475
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t166_l478
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v165_l475)))


(def v167_l482 (kind/doc #'sk/band-v))


(def
 v168_l484
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t169_l487
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v168_l484)))


(def v170_l490 (kind/doc #'sk/band-h))


(def
 v171_l492
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t172_l495
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v171_l492)))


(def v174_l500 (kind/doc #'sk/cross))


(def v175_l502 (sk/cross [:a :b] [1 2 3]))


(deftest
 t176_l504
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v175_l502)))


(def
 v177_l506
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t178_l511
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v177_l506)))


(def v179_l515 (kind/doc #'sk/distribution))


(def
 v180_l517
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t181_l520
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v180_l517)))


(def v183_l526 (kind/doc #'sk/facet))


(def
 v184_l528
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t185_l532
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v184_l528)))


(def v186_l536 (kind/doc #'sk/facet-grid))


(def
 v187_l538
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t188_l542
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v187_l538)))


(def v190_l548 (kind/doc #'sk/svg-summary))


(def
 v191_l550
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t192_l553
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l550)))


(def v193_l556 (kind/doc #'sk/valid-sketch?))


(def v194_l558 (sk/valid-sketch? sk1))


(deftest t195_l560 (is (true? v194_l558)))


(def v196_l562 (kind/doc #'sk/explain-sketch))


(def v197_l564 (sk/explain-sketch sk1))


(deftest t198_l566 (is (nil? v197_l564)))


(def v200_l570 (kind/doc #'sk/config))


(def v201_l572 (sk/config))


(deftest t202_l574 (is ((fn [m] (map? m)) v201_l572)))


(def v203_l576 (kind/doc #'sk/set-config!))


(def v204_l578 (kind/doc #'sk/with-config))


(def
 v205_l580
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t206_l583 (is ((fn [p] (= :pastel1 p)) v205_l580)))


(def v208_l587 (kind/doc #'sk/arrange))


(def
 v209_l589
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:width 250, :height 200}))
   (->
    iris
    (sk/lay-point :petal_length :petal_width {:color :species})
    (sk/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t210_l595 (is ((fn [v] (= :div (first v))) v209_l589)))


(def v212_l598 (kind/doc #'sk/save))


(def
 v214_l602
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))
   path
   {:title "Iris Export"})
  (.contains (slurp path) "<svg")))


(deftest t215_l608 (is (true? v214_l602)))
