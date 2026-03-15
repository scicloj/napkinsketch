(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l24
 (def
  tiny
  (tc/dataset
   {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]})))


(def
 v5_l28
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v6_l31
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v7_l34
 (def
  measurements
  (tc/dataset
   {:treatment ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def v9_l41 (kind/doc #'sk/view))


(def
 v11_l45
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t12_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l45)))


(def
 v14_l53
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t15_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l53)))


(def
 v17_l61
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t18_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l61)))


(def
 v20_l73
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t21_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l73)))


(def v22_l81 (kind/doc #'sk/lay))


(def
 v24_l85
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t25_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l85)))


(def v27_l97 (kind/doc #'sk/point))


(def
 v28_l99
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t29_l101
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l99)))


(def v30_l104 (kind/doc #'sk/line))


(def
 v31_l106
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__75171#] (Math/sin (* p1__75171# 0.3)))
     (range 30))})))


(def v32_l109 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t33_l111
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l109)))


(def v34_l114 (kind/doc #'sk/histogram))


(def
 v35_l116
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t36_l118
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l116)))


(def v37_l121 (kind/doc #'sk/bar))


(def v38_l123 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t39_l125
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l123)))


(def v40_l128 (kind/doc #'sk/stacked-bar))


(def
 v41_l130
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l133
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t43_l135
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l133)))


(def v44_l138 (kind/doc #'sk/stacked-bar-fill))


(def
 v45_l140
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t46_l142
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l140)))


(def v47_l145 (kind/doc #'sk/value-bar))


(def
 v48_l147
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t49_l149
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l147)))


(def v50_l152 (kind/doc #'sk/lm))


(def
 v51_l154
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t52_l159
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l154)))


(def v53_l163 (kind/doc #'sk/loess))


(def
 v54_l165
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__75172#]
       (+
        (Math/sin (* p1__75172# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v55_l170
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t56_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l170)))


(def v57_l179 (kind/doc #'sk/density))


(def
 v58_l181
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t59_l186
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l181)))


(def v60_l189 (kind/doc #'sk/area))


(def v61_l191 (-> wave (sk/view [[:x :y]]) (sk/lay (sk/area)) sk/plot))


(deftest
 t62_l193
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l191)))


(def v63_l196 (kind/doc #'sk/stacked-area))


(def
 v64_l198
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
 t65_l207
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l198)))


(def v66_l210 (kind/doc #'sk/text))


(def
 v67_l212
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t68_l217
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l212)))


(def v69_l220 (kind/doc #'sk/boxplot))


(def
 v70_l222
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t71_l227
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v70_l222)))


(def v72_l231 (kind/doc #'sk/violin))


(def
 v73_l233
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t74_l238
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v73_l233)))


(def v75_l241 (kind/doc #'sk/errorbar))


(def
 v76_l243
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t77_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v76_l243)))


(def v78_l253 (kind/doc #'sk/lollipop))


(def
 v79_l255
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t80_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v79_l255)))


(def v81_l264 (kind/doc #'sk/tile))


(def
 v82_l266
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t83_l271
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v82_l266)))


(def v84_l274 (kind/doc #'sk/density2d))


(def
 v85_l276
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t86_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l276)))


(def v87_l284 (kind/doc #'sk/contour))


(def
 v88_l286
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t89_l291
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v88_l286)))


(def v90_l294 (kind/doc #'sk/ridgeline))


(def
 v91_l296
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t92_l301
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v91_l296)))


(def v93_l304 (kind/doc #'sk/rug))


(def
 v94_l306
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rug {:side :both}))
  sk/plot))


(deftest
 t95_l311
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v94_l306)))


(def v96_l314 (kind/doc #'sk/step))


(def
 v97_l316
 (-> tiny (sk/view [[:x :y]]) (sk/lay (sk/step) (sk/point)) sk/plot))


(deftest
 t98_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v97_l316)))


(def v99_l325 (kind/doc #'sk/summary))


(def
 v100_l327
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary))
  sk/plot))


(deftest
 t101_l332
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v100_l327)))


(def v103_l338 (kind/doc #'sk/plot))


(def v105_l343 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t106_l345
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v105_l343)))


(def v107_l348 (kind/doc #'sk/sketch))


(def
 v109_l352
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v110_l354
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t111_l356
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v110_l354)))


(def v113_l361 (kind/doc #'sk/views->sketch))


(def
 v114_l363
 (def sk2 (sk/views->sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v115_l365 (= (keys sk1) (keys sk2)))


(deftest t116_l367 (is (true? v115_l365)))


(def v117_l369 (kind/doc #'sk/sketch->membrane))


(def v118_l371 (def m1 (sk/sketch->membrane sk1)))


(def v119_l373 (vector? m1))


(deftest t120_l375 (is (true? v119_l373)))


(def v121_l377 (kind/doc #'sk/membrane->figure))


(def
 v122_l379
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t123_l383 (is ((fn [v] (= :svg v)) v122_l379)))


(def v124_l385 (kind/doc #'sk/sketch->figure))


(def v125_l387 (first (sk/sketch->figure sk1 :svg {})))


(deftest t126_l389 (is ((fn [v] (= :svg v)) v125_l387)))


(def v128_l393 (kind/doc #'sk/coord))


(def
 v130_l397
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t131_l403
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v130_l397)))


(def
 v133_l408
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t134_l414
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v133_l408)))


(def v135_l417 (kind/doc #'sk/scale))


(def
 v137_l421
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t138_l427
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v137_l421)))


(def
 v140_l432
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t141_l438
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v140_l432)))


(def v142_l441 (kind/doc #'sk/labs))


(def
 v143_l443
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
 t144_l449
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v143_l443)))


(def v146_l454 (kind/doc #'sk/rule-v))


(def
 v147_l456
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t148_l461
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v147_l456)))


(def v149_l465 (kind/doc #'sk/rule-h))


(def
 v150_l467
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t151_l472
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v150_l467)))


(def v152_l476 (kind/doc #'sk/band-v))


(def
 v153_l478
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t154_l483
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v153_l478)))


(def v155_l486 (kind/doc #'sk/band-h))


(def
 v156_l488
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t157_l493
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v156_l488)))


(def v159_l498 (kind/doc #'sk/cross))


(def v160_l500 (sk/cross [:a :b] [1 2 3]))


(deftest
 t161_l502
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v160_l500)))


(def
 v162_l504
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t163_l510
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v162_l504)))


(def v164_l514 (kind/doc #'sk/pairs))


(def v165_l516 (sk/pairs [:a :b :c]))


(deftest
 t166_l518
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v165_l516)))


(def
 v167_l520
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t168_l525
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v167_l520)))


(def v169_l529 (kind/doc #'sk/distribution))


(def
 v170_l531
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t171_l535
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v170_l531)))


(def v173_l541 (kind/doc #'sk/facet))


(def
 v174_l543
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t175_l549
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v174_l543)))


(def v176_l553 (kind/doc #'sk/facet-grid))


(def
 v177_l555
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t178_l561
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v177_l555)))


(def v180_l567 (kind/doc #'sk/svg-summary))


(def
 v181_l569
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t182_l575
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v181_l569)))


(def v183_l578 (kind/doc #'sk/valid-sketch?))


(def v184_l580 (sk/valid-sketch? sk1))


(deftest t185_l582 (is (true? v184_l580)))


(def v186_l584 (kind/doc #'sk/explain-sketch))


(def v187_l586 (sk/explain-sketch sk1))


(deftest t188_l588 (is (nil? v187_l586)))
