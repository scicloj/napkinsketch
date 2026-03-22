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


(def
 v11_l49
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/plot))


(deftest
 t12_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def v14_l60 (-> iris (sk/view :sepal_length) sk/lay-histogram sk/plot))


(deftest
 t15_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l60)))


(def
 v17_l71
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t18_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l71)))


(def
 v20_l83
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  sk/lay-point
  sk/plot))


(deftest
 t21_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l83)))


(def v22_l91 (kind/doc #'sk/lay))


(def
 v24_l95
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t25_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l95)))


(def v27_l107 (kind/doc #'sk/lay-point))


(def
 v28_l109
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/plot))


(deftest
 t29_l113
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l109)))


(def v30_l116 (kind/doc #'sk/lay-line))


(def
 v31_l118
 (def
  wave
  {:x (range 30),
   :y
   (mapv (fn* [p1__94491#] (Math/sin (* p1__94491# 0.3))) (range 30))}))


(def v32_l121 (-> wave (sk/lay-line :x :y) sk/plot))


(deftest
 t33_l125
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l121)))


(def v34_l128 (kind/doc #'sk/lay-histogram))


(def
 v35_l130
 (-> iris (sk/view :sepal_length) sk/lay-histogram sk/plot))


(deftest
 t36_l135
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l130)))


(def v37_l138 (kind/doc #'sk/lay-bar))


(def v38_l140 (-> iris (sk/view :species) sk/lay-bar sk/plot))


(deftest
 t39_l145
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l140)))


(def v40_l148 (kind/doc #'sk/lay-stacked-bar))


(def
 v41_l150
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l153
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar {:color :species})
  sk/plot))


(deftest
 t43_l158
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l153)))


(def v44_l161 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v45_l163
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})
  sk/plot))


(deftest
 t46_l168
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l163)))


(def v47_l171 (kind/doc #'sk/lay-value-bar))


(def v48_l173 (-> sales (sk/lay-value-bar :product :revenue) sk/plot))


(deftest
 t49_l177
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l173)))


(def v50_l180 (kind/doc #'sk/lay-lm))


(def
 v51_l182
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm
  sk/plot))


(deftest
 t52_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l182)))


(def v53_l192 (kind/doc #'sk/lay-loess))


(def
 v54_l194
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__94492#]
      (+
       (Math/sin (* p1__94492# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v55_l199
 (-> noisy-wave (sk/view [[:x :y]]) sk/lay-point sk/lay-loess sk/plot))


(deftest
 t56_l205
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l199)))


(def v57_l209 (kind/doc #'sk/lay-density))


(def
 v58_l211
 (-> iris (sk/view [[:sepal_length]]) sk/lay-density sk/plot))


(deftest
 t59_l216
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l211)))


(def v60_l219 (kind/doc #'sk/lay-area))


(def v61_l221 (-> wave (sk/view [[:x :y]]) sk/lay-area sk/plot))


(deftest
 t62_l226
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l221)))


(def v63_l229 (kind/doc #'sk/lay-stacked-area))


(def
 v64_l231
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
  (sk/view [[:x :y]])
  (sk/lay-stacked-area {:color :group})
  sk/plot))


(deftest
 t65_l240
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l231)))


(def v66_l243 (kind/doc #'sk/lay-text))


(def
 v67_l245
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/view [[:x :y]])
  (sk/lay-text {:text :name})
  sk/plot))


(deftest
 t68_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l245)))


(def v69_l253 (kind/doc #'sk/lay-label))


(def
 v70_l255
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/view [[:x :y]])
  (sk/lay-point {:size 5})
  (sk/lay-label {:text :name})
  sk/plot))


(deftest
 t71_l261
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l255)))


(def v72_l264 (kind/doc #'sk/lay-boxplot))


(def
 v73_l266
 (-> iris (sk/view [[:species :sepal_width]]) sk/lay-boxplot sk/plot))


(deftest
 t74_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l266)))


(def v75_l275 (kind/doc #'sk/lay-violin))


(def
 v76_l277
 (-> tips (sk/view [[:day :total_bill]]) sk/lay-violin sk/plot))


(deftest
 t77_l282
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l277)))


(def v78_l285 (kind/doc #'sk/lay-errorbar))


(def
 v79_l287
 (->
  measurements
  (sk/view [[:treatment :mean]])
  sk/lay-point
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})
  sk/plot))


(deftest
 t80_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l287)))


(def v81_l297 (kind/doc #'sk/lay-lollipop))


(def
 v82_l299
 (-> sales (sk/view [[:product :revenue]]) sk/lay-lollipop sk/plot))


(deftest
 t83_l304
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l299)))


(def v84_l308 (kind/doc #'sk/lay-tile))


(def
 v85_l310
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-tile sk/plot))


(deftest
 t86_l315
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l310)))


(def v87_l318 (kind/doc #'sk/lay-density2d))


(def
 v88_l320
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-density2d
  sk/plot))


(deftest
 t89_l325
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l320)))


(def v90_l328 (kind/doc #'sk/lay-contour))


(def
 v91_l330
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-contour
  sk/plot))


(deftest
 t92_l335
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l330)))


(def v93_l338 (kind/doc #'sk/lay-ridgeline))


(def
 v94_l340
 (->
  iris
  (sk/view [[:species :sepal_length]])
  sk/lay-ridgeline
  sk/plot))


(deftest
 t95_l345
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l340)))


(def v96_l348 (kind/doc #'sk/lay-rug))


(def
 v97_l350
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay-rug {:side :both})
  sk/plot))


(deftest
 t98_l356
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l350)))


(def v99_l359 (kind/doc #'sk/lay-step))


(def
 v100_l361
 (-> tiny (sk/view [[:x :y]]) sk/lay-step sk/lay-point sk/plot))


(deftest
 t101_l367
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l361)))


(def v102_l371 (kind/doc #'sk/lay-summary))


(def
 v103_l373
 (-> iris (sk/view [[:species :sepal_length]]) sk/lay-summary sk/plot))


(deftest
 t104_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l373)))


(def v106_l384 (kind/doc #'sk/plot))


(def v108_l389 (-> tiny (sk/lay-point :x :y) sk/plot))


(deftest
 t109_l393
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l389)))


(def v110_l396 (kind/doc #'sk/sketch))


(def v112_l400 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v113_l404 sk1)


(deftest
 t114_l406
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v113_l404)))


(def v116_l411 (kind/doc #'sk/views->sketch))


(def
 v117_l413
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v118_l417 (= (keys sk1) (keys sk2)))


(deftest t119_l419 (is (true? v118_l417)))


(def v120_l421 (kind/doc #'sk/sketch->membrane))


(def v121_l423 (def m1 (sk/sketch->membrane sk1)))


(def v122_l425 (vector? m1))


(deftest t123_l427 (is (true? v122_l425)))


(def v124_l429 (kind/doc #'sk/membrane->figure))


(def
 v125_l431
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t126_l435 (is ((fn [v] (= :svg v)) v125_l431)))


(def v127_l437 (kind/doc #'sk/sketch->figure))


(def v128_l439 (first (sk/sketch->figure sk1 :svg {})))


(deftest t129_l441 (is ((fn [v] (= :svg v)) v128_l439)))


(def v131_l445 (kind/doc #'sk/coord))


(def
 v133_l449
 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip) sk/plot))


(deftest
 t134_l455
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v133_l449)))


(def
 v136_l460
 (-> iris (sk/view :species) sk/lay-bar (sk/coord :polar) sk/plot))


(deftest
 t137_l466
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v136_l460)))


(def v138_l469 (kind/doc #'sk/scale))


(def
 v140_l473
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/scale :x :log)
  sk/plot))


(deftest
 t141_l479
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v140_l473)))


(def
 v143_l484
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t144_l490
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v143_l484)))


(def v145_l493 (kind/doc #'sk/labs))


(def
 v146_l495
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})
  sk/plot))


(deftest
 t147_l501
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v146_l495)))


(def v149_l506 (kind/doc #'sk/rule-v))


(def
 v150_l508
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/rule-v 6.0))
  sk/plot))


(deftest
 t151_l514
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v150_l508)))


(def v152_l518 (kind/doc #'sk/rule-h))


(def
 v153_l520
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/rule-h 3.0))
  sk/plot))


(deftest
 t154_l526
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v153_l520)))


(def v155_l530 (kind/doc #'sk/band-v))


(def
 v156_l532
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t157_l538
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v156_l532)))


(def v158_l541 (kind/doc #'sk/band-h))


(def
 v159_l543
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t160_l549
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v159_l543)))


(def v162_l554 (kind/doc #'sk/cross))


(def v163_l556 (sk/cross [:a :b] [1 2 3]))


(deftest
 t164_l558
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v163_l556)))


(def
 v165_l560
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t166_l566
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v165_l560)))


(def v167_l570 (kind/doc #'sk/distribution))


(def
 v168_l572
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  sk/lay-histogram
  sk/plot))


(deftest
 t169_l576
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v168_l572)))


(def v171_l582 (kind/doc #'sk/facet))


(def
 v172_l584
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})
  sk/plot))


(deftest
 t173_l590
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v172_l584)))


(def v174_l594 (kind/doc #'sk/facet-grid))


(def
 v175_l596
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay-point {:color :sex})
  sk/plot))


(deftest
 t176_l602
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v175_l596)))


(def v178_l608 (kind/doc #'sk/svg-summary))


(def
 v179_l610
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/plot
  sk/svg-summary))


(deftest
 t180_l616
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v179_l610)))


(def v181_l619 (kind/doc #'sk/valid-sketch?))


(def v182_l621 (sk/valid-sketch? sk1))


(deftest t183_l623 (is (true? v182_l621)))


(def v184_l625 (kind/doc #'sk/explain-sketch))


(def v185_l627 (sk/explain-sketch sk1))


(deftest t186_l629 (is (nil? v185_l627)))


(def v188_l633 (kind/doc #'sk/config))


(def v189_l635 (sk/config))


(deftest t190_l637 (is ((fn [m] (map? m)) v189_l635)))


(def v191_l639 (kind/doc #'sk/set-config!))


(def v192_l641 (kind/doc #'sk/with-config))


(def
 v193_l643
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t194_l646 (is ((fn [p] (= :pastel1 p)) v193_l643)))


(def v196_l650 (kind/doc #'sk/arrange))


(def
 v197_l652
 (sk/arrange
  [(->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:color :species})
    (sk/plot {:width 250, :height 200}))
   (->
    iris
    (sk/view :petal_length :petal_width)
    (sk/lay-point {:color :species})
    (sk/plot {:width 250, :height 200}))]
  {:cols 2}))


(deftest t198_l662 (is ((fn [v] (= :div (first v))) v197_l652)))


(def v200_l665 (kind/doc #'sk/save))


(def
 v202_l669
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


(deftest t203_l675 (is (true? v202_l669)))
