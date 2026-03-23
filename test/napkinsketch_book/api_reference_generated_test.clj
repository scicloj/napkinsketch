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
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


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
   (mapv (fn* [p1__85632#] (Math/sin (* p1__85632# 0.3))) (range 30))}))


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
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l169
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l164)))


(def v53_l173 (kind/doc #'sk/lay-loess))


(def
 v54_l175
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__85633#]
      (+
       (Math/sin (* p1__85633# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v55_l180
 (-> noisy-wave (sk/view [[:x :y]]) sk/lay-point sk/lay-loess))


(deftest
 t56_l185
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l180)))


(def v57_l189 (kind/doc #'sk/lay-density))


(def v58_l191 (-> iris (sk/lay-density :sepal_length)))


(deftest
 t59_l194
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l191)))


(def v60_l197 (kind/doc #'sk/lay-area))


(def v61_l199 (-> wave (sk/lay-area :x :y)))


(deftest
 t62_l202
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l199)))


(def v63_l205 (kind/doc #'sk/lay-stacked-area))


(def
 v64_l207
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
 t65_l214
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l207)))


(def v66_l217 (kind/doc #'sk/lay-text))


(def
 v67_l219
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t68_l222
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l219)))


(def v69_l225 (kind/doc #'sk/lay-label))


(def
 v70_l227
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/view [[:x :y]])
  (sk/lay-point {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t71_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l227)))


(def v72_l235 (kind/doc #'sk/lay-boxplot))


(def v73_l237 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t74_l240
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l237)))


(def v75_l244 (kind/doc #'sk/lay-violin))


(def v76_l246 (-> tips (sk/lay-violin :day :total_bill)))


(deftest
 t77_l249
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l246)))


(def v78_l252 (kind/doc #'sk/lay-errorbar))


(def
 v79_l254
 (->
  measurements
  (sk/view [[:treatment :mean]])
  sk/lay-point
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t80_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l254)))


(def v81_l263 (kind/doc #'sk/lay-lollipop))


(def v82_l265 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t83_l268
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l265)))


(def v84_l272 (kind/doc #'sk/lay-tile))


(def v85_l274 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t86_l277
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l274)))


(def v87_l280 (kind/doc #'sk/lay-density2d))


(def v88_l282 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t89_l285
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l282)))


(def v90_l288 (kind/doc #'sk/lay-contour))


(def v91_l290 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t92_l293
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l290)))


(def v93_l296 (kind/doc #'sk/lay-ridgeline))


(def v94_l298 (-> iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t95_l301
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l298)))


(def v96_l304 (kind/doc #'sk/lay-rug))


(def
 v97_l306
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay-rug {:side :both})))


(deftest
 t98_l311
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l306)))


(def v99_l314 (kind/doc #'sk/lay-step))


(def v100_l316 (-> tiny (sk/view [[:x :y]]) sk/lay-step sk/lay-point))


(deftest
 t101_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l316)))


(def v102_l325 (kind/doc #'sk/lay-summary))


(def v103_l327 (-> iris (sk/lay-summary :species :sepal_length)))


(deftest
 t104_l330
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l327)))


(def v106_l336 (kind/doc #'sk/plot))


(def v108_l341 (-> tiny (sk/lay-point :x :y)))


(deftest
 t109_l344
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l341)))


(def v110_l347 (kind/doc #'sk/options))


(def
 v112_l351
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t113_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v112_l351)))


(def v114_l359 (kind/doc #'sk/plot-spec?))


(def v116_l363 (sk/plot-spec? (sk/lay-point tiny :x :y)))


(deftest t117_l365 (is (true? v116_l363)))


(def v118_l367 (kind/doc #'sk/views-of))


(def
 v120_l371
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t121_l373 (is ((fn [v] (= 2 v)) v120_l371)))


(def v122_l375 (kind/doc #'sk/sketch))


(def v124_l379 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v125_l383 sk1)


(deftest
 t126_l385
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v125_l383)))


(def v128_l390 (kind/doc #'sk/views->sketch))


(def
 v129_l392
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v130_l396 (= (keys sk1) (keys sk2)))


(deftest t131_l398 (is (true? v130_l396)))


(def v132_l400 (kind/doc #'sk/sketch->membrane))


(def v133_l402 (def m1 (sk/sketch->membrane sk1)))


(def v134_l404 (vector? m1))


(deftest t135_l406 (is (true? v134_l404)))


(def v136_l408 (kind/doc #'sk/membrane->figure))


(def
 v137_l410
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t138_l414 (is ((fn [v] (= :svg v)) v137_l410)))


(def v139_l416 (kind/doc #'sk/sketch->figure))


(def v140_l418 (first (sk/sketch->figure sk1 :svg {})))


(deftest t141_l420 (is ((fn [v] (= :svg v)) v140_l418)))


(def v143_l424 (kind/doc #'sk/coord))


(def v145_l428 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t146_l431
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v145_l428)))


(def v148_l436 (-> iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t149_l439
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v148_l436)))


(def v150_l442 (kind/doc #'sk/scale))


(def
 v152_l446
 (-> iris (sk/lay-point :sepal_length :sepal_width) (sk/scale :x :log)))


(deftest
 t153_l449
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l446)))


(def
 v155_l454
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t156_l457
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v155_l454)))


(def v157_l460 (kind/doc #'sk/labs))


(def
 v158_l462
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t159_l465
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v158_l462)))


(def v161_l470 (kind/doc #'sk/rule-v))


(def
 v162_l472
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t163_l475
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v162_l472)))


(def v164_l479 (kind/doc #'sk/rule-h))


(def
 v165_l481
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t166_l484
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v165_l481)))


(def v167_l488 (kind/doc #'sk/band-v))


(def
 v168_l490
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t169_l493
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v168_l490)))


(def v170_l496 (kind/doc #'sk/band-h))


(def
 v171_l498
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t172_l501
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v171_l498)))


(def v174_l506 (kind/doc #'sk/cross))


(def v175_l508 (sk/cross [:a :b] [1 2 3]))


(deftest
 t176_l510
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v175_l508)))


(def
 v177_l512
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t178_l517
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v177_l512)))


(def v179_l521 (kind/doc #'sk/distribution))


(def
 v180_l523
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t181_l526
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v180_l523)))


(def v183_l532 (kind/doc #'sk/facet))


(def
 v184_l534
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})))


(deftest
 t185_l539
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v184_l534)))


(def v186_l543 (kind/doc #'sk/facet-grid))


(def
 v187_l545
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay-point {:color :sex})))


(deftest
 t188_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v187_l545)))


(def v190_l556 (kind/doc #'sk/svg-summary))


(def
 v191_l558
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t192_l561
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l558)))


(def v193_l564 (kind/doc #'sk/valid-sketch?))


(def v194_l566 (sk/valid-sketch? sk1))


(deftest t195_l568 (is (true? v194_l566)))


(def v196_l570 (kind/doc #'sk/explain-sketch))


(def v197_l572 (sk/explain-sketch sk1))


(deftest t198_l574 (is (nil? v197_l572)))


(def v200_l578 (kind/doc #'sk/config))


(def v201_l580 (sk/config))


(deftest t202_l582 (is ((fn [m] (map? m)) v201_l580)))


(def v203_l584 (kind/doc #'sk/set-config!))


(def v204_l586 (kind/doc #'sk/with-config))


(def
 v205_l588
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t206_l591 (is ((fn [p] (= :pastel1 p)) v205_l588)))


(def v208_l595 (kind/doc #'sk/arrange))


(def
 v209_l597
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


(deftest t210_l603 (is ((fn [v] (= :div (first v))) v209_l597)))


(def v212_l606 (kind/doc #'sk/save))


(def
 v214_l610
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


(deftest t215_l616 (is (true? v214_l610)))
