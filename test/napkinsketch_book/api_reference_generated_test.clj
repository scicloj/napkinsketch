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
 t12_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def
 v14_l60
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


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
  (sk/lay (sk/point {:color :species}))
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
  (sk/lay (sk/point))
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
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
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


(def v27_l107 (kind/doc #'sk/point))


(def
 v28_l109
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t29_l111
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l109)))


(def v30_l114 (kind/doc #'sk/line))


(def
 v31_l116
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__91899#] (Math/sin (* p1__91899# 0.3)))
     (range 30))})))


(def v32_l119 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t33_l121
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l119)))


(def v34_l124 (kind/doc #'sk/histogram))


(def
 v35_l126
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t36_l131
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l126)))


(def v37_l134 (kind/doc #'sk/bar))


(def v38_l136 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t39_l141
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l136)))


(def v40_l144 (kind/doc #'sk/stacked-bar))


(def
 v41_l146
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l149
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t43_l154
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l149)))


(def v44_l157 (kind/doc #'sk/stacked-bar-fill))


(def
 v45_l159
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t46_l164
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l159)))


(def v47_l167 (kind/doc #'sk/value-bar))


(def
 v48_l169
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t49_l171
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l169)))


(def v50_l174 (kind/doc #'sk/lm))


(def
 v51_l176
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t52_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l176)))


(def v53_l185 (kind/doc #'sk/loess))


(def
 v54_l187
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__91900#]
       (+
        (Math/sin (* p1__91900# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v55_l192
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t56_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l192)))


(def v57_l201 (kind/doc #'sk/density))


(def
 v58_l203
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t59_l208
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l203)))


(def v60_l211 (kind/doc #'sk/area))


(def v61_l213 (-> wave (sk/view [[:x :y]]) (sk/lay (sk/area)) sk/plot))


(deftest
 t62_l218
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l213)))


(def v63_l221 (kind/doc #'sk/stacked-area))


(def
 v64_l223
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
 t65_l232
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l223)))


(def v66_l235 (kind/doc #'sk/text))


(def
 v67_l237
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t68_l242
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l237)))


(def v69_l245 (kind/doc #'sk/label))


(def
 v70_l247
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:size 5}) (sk/label {:text :name}))
  sk/plot))


(deftest
 t71_l253
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l247)))


(def v72_l256 (kind/doc #'sk/boxplot))


(def
 v73_l258
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t74_l263
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l258)))


(def v75_l267 (kind/doc #'sk/violin))


(def
 v76_l269
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t77_l274
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l269)))


(def v78_l277 (kind/doc #'sk/errorbar))


(def
 v79_l279
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t80_l285
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l279)))


(def v81_l289 (kind/doc #'sk/lollipop))


(def
 v82_l291
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t83_l296
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l291)))


(def v84_l300 (kind/doc #'sk/tile))


(def
 v85_l302
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t86_l307
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l302)))


(def v87_l310 (kind/doc #'sk/density2d))


(def
 v88_l312
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t89_l317
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l312)))


(def v90_l320 (kind/doc #'sk/contour))


(def
 v91_l322
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t92_l327
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l322)))


(def v93_l330 (kind/doc #'sk/ridgeline))


(def
 v94_l332
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t95_l337
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l332)))


(def v96_l340 (kind/doc #'sk/rug))


(def
 v97_l342
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rug {:side :both}))
  sk/plot))


(deftest
 t98_l347
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l342)))


(def v99_l350 (kind/doc #'sk/step))


(def
 v100_l352
 (-> tiny (sk/view [[:x :y]]) (sk/lay (sk/step) (sk/point)) sk/plot))


(deftest
 t101_l357
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l352)))


(def v102_l361 (kind/doc #'sk/summary))


(def
 v103_l363
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary))
  sk/plot))


(deftest
 t104_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l363)))


(def v106_l374 (kind/doc #'sk/plot))


(def v108_l379 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t109_l381
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l379)))


(def v110_l384 (kind/doc #'sk/sketch))


(def
 v112_l388
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v113_l390 sk1)


(deftest
 t114_l392
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v113_l390)))


(def v116_l397 (kind/doc #'sk/views->sketch))


(def
 v117_l399
 (def sk2 (sk/views->sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v118_l401 (= (keys sk1) (keys sk2)))


(deftest t119_l403 (is (true? v118_l401)))


(def v120_l405 (kind/doc #'sk/sketch->membrane))


(def v121_l407 (def m1 (sk/sketch->membrane sk1)))


(def v122_l409 (vector? m1))


(deftest t123_l411 (is (true? v122_l409)))


(def v124_l413 (kind/doc #'sk/membrane->figure))


(def
 v125_l415
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t126_l419 (is ((fn [v] (= :svg v)) v125_l415)))


(def v127_l421 (kind/doc #'sk/sketch->figure))


(def v128_l423 (first (sk/sketch->figure sk1 :svg {})))


(deftest t129_l425 (is ((fn [v] (= :svg v)) v128_l423)))


(def v131_l429 (kind/doc #'sk/coord))


(def
 v133_l433
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t134_l439
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v133_l433)))


(def
 v136_l444
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t137_l450
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v136_l444)))


(def v138_l453 (kind/doc #'sk/scale))


(def
 v140_l457
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t141_l463
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v140_l457)))


(def
 v143_l468
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t144_l474
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v143_l468)))


(def v145_l477 (kind/doc #'sk/labs))


(def
 v146_l479
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
 t147_l485
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v146_l479)))


(def v149_l490 (kind/doc #'sk/rule-v))


(def
 v150_l492
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t151_l497
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v150_l492)))


(def v152_l501 (kind/doc #'sk/rule-h))


(def
 v153_l503
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t154_l508
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v153_l503)))


(def v155_l512 (kind/doc #'sk/band-v))


(def
 v156_l514
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t157_l519
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v156_l514)))


(def v158_l522 (kind/doc #'sk/band-h))


(def
 v159_l524
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t160_l529
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v159_l524)))


(def v162_l534 (kind/doc #'sk/cross))


(def v163_l536 (sk/cross [:a :b] [1 2 3]))


(deftest
 t164_l538
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v163_l536)))


(def
 v165_l540
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t166_l546
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v165_l540)))


(def v167_l550 (kind/doc #'sk/pairs))


(def v168_l552 (sk/pairs [:a :b :c]))


(deftest
 t169_l554
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v168_l552)))


(def
 v170_l556
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t171_l561
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v170_l556)))


(def v172_l565 (kind/doc #'sk/distribution))


(def
 v173_l567
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t174_l571
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v173_l567)))


(def v176_l577 (kind/doc #'sk/facet))


(def
 v177_l579
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t178_l585
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v177_l579)))


(def v179_l589 (kind/doc #'sk/facet-grid))


(def
 v180_l591
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t181_l597
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v180_l591)))


(def v183_l603 (kind/doc #'sk/svg-summary))


(def
 v184_l605
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t185_l611
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v184_l605)))


(def v186_l614 (kind/doc #'sk/valid-sketch?))


(def v187_l616 (sk/valid-sketch? sk1))


(deftest t188_l618 (is (true? v187_l616)))


(def v189_l620 (kind/doc #'sk/explain-sketch))


(def v190_l622 (sk/explain-sketch sk1))


(deftest t191_l624 (is (nil? v190_l622)))


(def v193_l628 (kind/doc #'sk/config))


(def v194_l630 (sk/config))


(deftest t195_l632 (is ((fn [m] (map? m)) v194_l630)))


(def v196_l634 (kind/doc #'sk/set-config!))


(def v197_l636 (kind/doc #'sk/with-config))


(def
 v198_l638
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t199_l641 (is ((fn [p] (= :pastel1 p)) v198_l638)))


(def v201_l645 (kind/doc #'sk/arrange))


(def
 v202_l647
 (sk/arrange
  [(->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 250, :height 200}))
   (->
    iris
    (sk/view :petal_length :petal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 250, :height 200}))]
  {:cols 2}))


(deftest t203_l657 (is ((fn [v] (= :div (first v))) v202_l647)))
