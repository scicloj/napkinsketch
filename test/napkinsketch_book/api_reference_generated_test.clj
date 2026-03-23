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
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-point))


(deftest
 t12_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def v14_l59 (-> iris (sk/view :sepal_length) sk/lay-histogram))


(deftest
 t15_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l59)))


(def
 v17_l69
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t18_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l69)))


(def
 v20_l80
 (-> (sk/view iris {:x :sepal_length, :y :sepal_width}) sk/lay-point))


(deftest
 t21_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l80)))


(def v22_l87 (kind/doc #'sk/lay))


(def
 v24_l91
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t25_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l91)))


(def v27_l102 (kind/doc #'sk/lay-point))


(def
 v28_l104
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t29_l107
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l104)))


(def v30_l110 (kind/doc #'sk/lay-line))


(def
 v31_l112
 (def
  wave
  {:x (range 30),
   :y
   (mapv (fn* [p1__90208#] (Math/sin (* p1__90208# 0.3))) (range 30))}))


(def v32_l115 (-> wave (sk/lay-line :x :y)))


(deftest
 t33_l118
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l115)))


(def v34_l121 (kind/doc #'sk/lay-histogram))


(def v35_l123 (-> iris (sk/view :sepal_length) sk/lay-histogram))


(deftest
 t36_l127
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l123)))


(def v37_l130 (kind/doc #'sk/lay-bar))


(def v38_l132 (-> iris (sk/view :species) sk/lay-bar))


(deftest
 t39_l136
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l132)))


(def v40_l139 (kind/doc #'sk/lay-stacked-bar))


(def
 v41_l141
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l144
 (-> penguins (sk/view :island) (sk/lay-stacked-bar {:color :species})))


(deftest
 t43_l148
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l144)))


(def v44_l151 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v45_l153
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})))


(deftest
 t46_l157
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l153)))


(def v47_l160 (kind/doc #'sk/lay-value-bar))


(def v48_l162 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t49_l165
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l162)))


(def v50_l168 (kind/doc #'sk/lay-lm))


(def
 v51_l170
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l170)))


(def v53_l179 (kind/doc #'sk/lay-loess))


(def
 v54_l181
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__90209#]
      (+
       (Math/sin (* p1__90209# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v55_l186
 (-> noisy-wave (sk/view [[:x :y]]) sk/lay-point sk/lay-loess))


(deftest
 t56_l191
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l186)))


(def v57_l195 (kind/doc #'sk/lay-density))


(def v58_l197 (-> iris (sk/view [[:sepal_length]]) sk/lay-density))


(deftest
 t59_l201
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l197)))


(def v60_l204 (kind/doc #'sk/lay-area))


(def v61_l206 (-> wave (sk/view [[:x :y]]) sk/lay-area))


(deftest
 t62_l210
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l206)))


(def v63_l213 (kind/doc #'sk/lay-stacked-area))


(def
 v64_l215
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
  (sk/lay-stacked-area {:color :group})))


(deftest
 t65_l223
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l215)))


(def v66_l226 (kind/doc #'sk/lay-text))


(def
 v67_l228
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/view [[:x :y]])
  (sk/lay-text {:text :name})))


(deftest
 t68_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l228)))


(def v69_l235 (kind/doc #'sk/lay-label))


(def
 v70_l237
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/view [[:x :y]])
  (sk/lay-point {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t71_l242
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l237)))


(def v72_l245 (kind/doc #'sk/lay-boxplot))


(def
 v73_l247
 (-> iris (sk/view [[:species :sepal_width]]) sk/lay-boxplot))


(deftest
 t74_l251
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l247)))


(def v75_l255 (kind/doc #'sk/lay-violin))


(def v76_l257 (-> tips (sk/view [[:day :total_bill]]) sk/lay-violin))


(deftest
 t77_l261
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l257)))


(def v78_l264 (kind/doc #'sk/lay-errorbar))


(def
 v79_l266
 (->
  measurements
  (sk/view [[:treatment :mean]])
  sk/lay-point
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t80_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l266)))


(def v81_l275 (kind/doc #'sk/lay-lollipop))


(def
 v82_l277
 (-> sales (sk/view [[:product :revenue]]) sk/lay-lollipop))


(deftest
 t83_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l277)))


(def v84_l285 (kind/doc #'sk/lay-tile))


(def
 v85_l287
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-tile))


(deftest
 t86_l291
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l287)))


(def v87_l294 (kind/doc #'sk/lay-density2d))


(def
 v88_l296
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-density2d))


(deftest
 t89_l300
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l296)))


(def v90_l303 (kind/doc #'sk/lay-contour))


(def
 v91_l305
 (-> iris (sk/view [[:sepal_length :sepal_width]]) sk/lay-contour))


(deftest
 t92_l309
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l305)))


(def v93_l312 (kind/doc #'sk/lay-ridgeline))


(def
 v94_l314
 (-> iris (sk/view [[:species :sepal_length]]) sk/lay-ridgeline))


(deftest
 t95_l318
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l314)))


(def v96_l321 (kind/doc #'sk/lay-rug))


(def
 v97_l323
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay-rug {:side :both})))


(deftest
 t98_l328
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l323)))


(def v99_l331 (kind/doc #'sk/lay-step))


(def v100_l333 (-> tiny (sk/view [[:x :y]]) sk/lay-step sk/lay-point))


(deftest
 t101_l338
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l333)))


(def v102_l342 (kind/doc #'sk/lay-summary))


(def
 v103_l344
 (-> iris (sk/view [[:species :sepal_length]]) sk/lay-summary))


(deftest
 t104_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l344)))


(def v106_l354 (kind/doc #'sk/plot))


(def v108_l359 (-> tiny (sk/lay-point :x :y)))


(deftest
 t109_l362
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l359)))


(def v110_l365 (kind/doc #'sk/options))


(def
 v112_l369
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t113_l373
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v112_l369)))


(def v114_l377 (kind/doc #'sk/plot-spec?))


(def v116_l381 (sk/plot-spec? (sk/lay-point tiny :x :y)))


(deftest t117_l383 (is (true? v116_l381)))


(def v118_l385 (kind/doc #'sk/views-of))


(def
 v120_l389
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t121_l391 (is ((fn [v] (= 2 v)) v120_l389)))


(def v122_l393 (kind/doc #'sk/sketch))


(def v124_l397 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v125_l401 sk1)


(deftest
 t126_l403
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v125_l401)))


(def v128_l408 (kind/doc #'sk/views->sketch))


(def
 v129_l410
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v130_l414 (= (keys sk1) (keys sk2)))


(deftest t131_l416 (is (true? v130_l414)))


(def v132_l418 (kind/doc #'sk/sketch->membrane))


(def v133_l420 (def m1 (sk/sketch->membrane sk1)))


(def v134_l422 (vector? m1))


(deftest t135_l424 (is (true? v134_l422)))


(def v136_l426 (kind/doc #'sk/membrane->figure))


(def
 v137_l428
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t138_l432 (is ((fn [v] (= :svg v)) v137_l428)))


(def v139_l434 (kind/doc #'sk/sketch->figure))


(def v140_l436 (first (sk/sketch->figure sk1 :svg {})))


(deftest t141_l438 (is ((fn [v] (= :svg v)) v140_l436)))


(def v143_l442 (kind/doc #'sk/coord))


(def v145_l446 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip)))


(deftest
 t146_l451
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v145_l446)))


(def
 v148_l456
 (-> iris (sk/view :species) sk/lay-bar (sk/coord :polar)))


(deftest
 t149_l461
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v148_l456)))


(def v150_l464 (kind/doc #'sk/scale))


(def
 v152_l468
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/scale :x :log)))


(deftest
 t153_l473
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l468)))


(def
 v155_l478
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/scale :x {:domain [3 9]})))


(deftest
 t156_l483
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v155_l478)))


(def v157_l486 (kind/doc #'sk/labs))


(def
 v158_l488
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t159_l493
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v158_l488)))


(def v161_l498 (kind/doc #'sk/rule-v))


(def
 v162_l500
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t163_l505
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v162_l500)))


(def v164_l509 (kind/doc #'sk/rule-h))


(def
 v165_l511
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t166_l516
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v165_l511)))


(def v167_l520 (kind/doc #'sk/band-v))


(def
 v168_l522
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t169_l527
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v168_l522)))


(def v170_l530 (kind/doc #'sk/band-h))


(def
 v171_l532
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t172_l537
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v171_l532)))


(def v174_l542 (kind/doc #'sk/cross))


(def v175_l544 (sk/cross [:a :b] [1 2 3]))


(deftest
 t176_l546
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v175_l544)))


(def
 v177_l548
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t178_l553
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v177_l548)))


(def v179_l557 (kind/doc #'sk/distribution))


(def
 v180_l559
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t181_l562
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v180_l559)))


(def v183_l568 (kind/doc #'sk/facet))


(def
 v184_l570
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})))


(deftest
 t185_l575
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v184_l570)))


(def v186_l579 (kind/doc #'sk/facet-grid))


(def
 v187_l581
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay-point {:color :sex})))


(deftest
 t188_l586
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v187_l581)))


(def v190_l592 (kind/doc #'sk/svg-summary))


(def
 v191_l594
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/svg-summary))


(deftest
 t192_l599
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l594)))


(def v193_l602 (kind/doc #'sk/valid-sketch?))


(def v194_l604 (sk/valid-sketch? sk1))


(deftest t195_l606 (is (true? v194_l604)))


(def v196_l608 (kind/doc #'sk/explain-sketch))


(def v197_l610 (sk/explain-sketch sk1))


(deftest t198_l612 (is (nil? v197_l610)))


(def v200_l616 (kind/doc #'sk/config))


(def v201_l618 (sk/config))


(deftest t202_l620 (is ((fn [m] (map? m)) v201_l618)))


(def v203_l622 (kind/doc #'sk/set-config!))


(def v204_l624 (kind/doc #'sk/with-config))


(def
 v205_l626
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t206_l629 (is ((fn [p] (= :pastel1 p)) v205_l626)))


(def v208_l633 (kind/doc #'sk/arrange))


(def
 v209_l635
 (sk/arrange
  [(->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:color :species})
    (sk/options {:width 250, :height 200}))
   (->
    iris
    (sk/view :petal_length :petal_width)
    (sk/lay-point {:color :species})
    (sk/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t210_l645 (is ((fn [v] (= :div (first v))) v209_l635)))


(def v212_l648 (kind/doc #'sk/save))


(def
 v214_l652
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


(deftest t215_l658 (is (true? v214_l652)))
