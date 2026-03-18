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
 (def
  tiny
  (tc/dataset
   {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]})))


(def
 v5_l32
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


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
  (tc/dataset
   {:treatment ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def v9_l45 (kind/doc #'sk/view))


(def
 v11_l49
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t12_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def
 v14_l57
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t15_l59
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l57)))


(def
 v17_l65
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t18_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l65)))


(def
 v20_l77
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t21_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l77)))


(def v22_l85 (kind/doc #'sk/lay))


(def
 v24_l89
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t25_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l89)))


(def v27_l101 (kind/doc #'sk/point))


(def
 v28_l103
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t29_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l103)))


(def v30_l108 (kind/doc #'sk/line))


(def
 v31_l110
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__94194#] (Math/sin (* p1__94194# 0.3)))
     (range 30))})))


(def v32_l113 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t33_l115
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l113)))


(def v34_l118 (kind/doc #'sk/histogram))


(def
 v35_l120
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t36_l122
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l120)))


(def v37_l125 (kind/doc #'sk/bar))


(def v38_l127 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t39_l129
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l127)))


(def v40_l132 (kind/doc #'sk/stacked-bar))


(def
 v41_l134
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l137
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t43_l139
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l137)))


(def v44_l142 (kind/doc #'sk/stacked-bar-fill))


(def
 v45_l144
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t46_l146
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l144)))


(def v47_l149 (kind/doc #'sk/value-bar))


(def
 v48_l151
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t49_l153
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l151)))


(def v50_l156 (kind/doc #'sk/lm))


(def
 v51_l158
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t52_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l158)))


(def v53_l167 (kind/doc #'sk/loess))


(def
 v54_l169
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__94195#]
       (+
        (Math/sin (* p1__94195# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v55_l174
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t56_l179
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l174)))


(def v57_l183 (kind/doc #'sk/density))


(def
 v58_l185
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t59_l190
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l185)))


(def v60_l193 (kind/doc #'sk/area))


(def v61_l195 (-> wave (sk/view [[:x :y]]) (sk/lay (sk/area)) sk/plot))


(deftest
 t62_l197
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l195)))


(def v63_l200 (kind/doc #'sk/stacked-area))


(def
 v64_l202
 (->
  (tc/dataset
   {:x (vec (concat (range 10) (range 10) (range 10))),
    :y
    (vec
     (concat
      [1 2 3 4 5 4 3 2 1 0]
      [2 2 2 3 3 3 2 2 2 2]
      [1 1 1 1 2 2 2 1 1 1])),
    :group
    (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))})
  (sk/view [[:x :y]])
  (sk/lay (sk/stacked-area {:color :group}))
  sk/plot))


(deftest
 t65_l211
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l202)))


(def v66_l214 (kind/doc #'sk/text))


(def
 v67_l216
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t68_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l216)))


(def v69_l224 (kind/doc #'sk/label))


(def
 v70_l226
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:size 5}) (sk/label {:text :name}))
  sk/plot))


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
   v70_l226)))


(def v72_l235 (kind/doc #'sk/boxplot))


(def
 v73_l237
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t74_l242
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l237)))


(def v75_l246 (kind/doc #'sk/violin))


(def
 v76_l248
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t77_l253
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l248)))


(def v78_l256 (kind/doc #'sk/errorbar))


(def
 v79_l258
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t80_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l258)))


(def v81_l268 (kind/doc #'sk/lollipop))


(def
 v82_l270
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t83_l275
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l270)))


(def v84_l279 (kind/doc #'sk/tile))


(def
 v85_l281
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t86_l286
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l281)))


(def v87_l289 (kind/doc #'sk/density2d))


(def
 v88_l291
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t89_l296
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l291)))


(def v90_l299 (kind/doc #'sk/contour))


(def
 v91_l301
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t92_l306
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l301)))


(def v93_l309 (kind/doc #'sk/ridgeline))


(def
 v94_l311
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t95_l316
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l311)))


(def v96_l319 (kind/doc #'sk/rug))


(def
 v97_l321
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rug {:side :both}))
  sk/plot))


(deftest
 t98_l326
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l321)))


(def v99_l329 (kind/doc #'sk/step))


(def
 v100_l331
 (-> tiny (sk/view [[:x :y]]) (sk/lay (sk/step) (sk/point)) sk/plot))


(deftest
 t101_l336
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l331)))


(def v102_l340 (kind/doc #'sk/summary))


(def
 v103_l342
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary))
  sk/plot))


(deftest
 t104_l347
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l342)))


(def v106_l353 (kind/doc #'sk/plot))


(def v108_l358 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t109_l360
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l358)))


(def v110_l363 (kind/doc #'sk/sketch))


(def
 v112_l367
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v113_l369 sk1)


(deftest
 t114_l371
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v113_l369)))


(def v116_l376 (kind/doc #'sk/views->sketch))


(def
 v117_l378
 (def sk2 (sk/views->sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v118_l380 (= (keys sk1) (keys sk2)))


(deftest t119_l382 (is (true? v118_l380)))


(def v120_l384 (kind/doc #'sk/sketch->membrane))


(def v121_l386 (def m1 (sk/sketch->membrane sk1)))


(def v122_l388 (vector? m1))


(deftest t123_l390 (is (true? v122_l388)))


(def v124_l392 (kind/doc #'sk/membrane->figure))


(def
 v125_l394
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t126_l398 (is ((fn [v] (= :svg v)) v125_l394)))


(def v127_l400 (kind/doc #'sk/sketch->figure))


(def v128_l402 (first (sk/sketch->figure sk1 :svg {})))


(deftest t129_l404 (is ((fn [v] (= :svg v)) v128_l402)))


(def v131_l408 (kind/doc #'sk/coord))


(def
 v133_l412
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t134_l418
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v133_l412)))


(def
 v136_l423
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t137_l429
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v136_l423)))


(def v138_l432 (kind/doc #'sk/scale))


(def
 v140_l436
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t141_l442
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v140_l436)))


(def
 v143_l447
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t144_l453
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v143_l447)))


(def v145_l456 (kind/doc #'sk/labs))


(def
 v146_l458
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})
  sk/plot))


(deftest
 t147_l464
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v146_l458)))


(def v149_l469 (kind/doc #'sk/rule-v))


(def
 v150_l471
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t151_l476
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v150_l471)))


(def v152_l480 (kind/doc #'sk/rule-h))


(def
 v153_l482
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t154_l487
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v153_l482)))


(def v155_l491 (kind/doc #'sk/band-v))


(def
 v156_l493
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t157_l498
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v156_l493)))


(def v158_l501 (kind/doc #'sk/band-h))


(def
 v159_l503
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t160_l508
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v159_l503)))


(def v162_l513 (kind/doc #'sk/cross))


(def v163_l515 (sk/cross [:a :b] [1 2 3]))


(deftest
 t164_l517
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v163_l515)))


(def
 v165_l519
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t166_l525
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v165_l519)))


(def v167_l529 (kind/doc #'sk/pairs))


(def v168_l531 (sk/pairs [:a :b :c]))


(deftest
 t169_l533
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v168_l531)))


(def
 v170_l535
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t171_l540
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v170_l535)))


(def v172_l544 (kind/doc #'sk/distribution))


(def
 v173_l546
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t174_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v173_l546)))


(def v176_l556 (kind/doc #'sk/facet))


(def
 v177_l558
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t178_l564
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v177_l558)))


(def v179_l568 (kind/doc #'sk/facet-grid))


(def
 v180_l570
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t181_l576
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v180_l570)))


(def v183_l582 (kind/doc #'sk/svg-summary))


(def
 v184_l584
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t185_l590
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v184_l584)))


(def v186_l593 (kind/doc #'sk/valid-sketch?))


(def v187_l595 (sk/valid-sketch? sk1))


(deftest t188_l597 (is (true? v187_l595)))


(def v189_l599 (kind/doc #'sk/explain-sketch))


(def v190_l601 (sk/explain-sketch sk1))


(deftest t191_l603 (is (nil? v190_l601)))
