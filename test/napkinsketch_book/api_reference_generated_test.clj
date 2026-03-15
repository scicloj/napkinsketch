(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l19
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l22
 (def
  tiny
  (tc/dataset
   {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]})))


(def
 v5_l26
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v6_l29
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v7_l32
 (def
  measurements
  (tc/dataset
   {:treatment ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def v9_l39 (kind/doc #'sk/view))


(def
 v11_l43
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t12_l45
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l43)))


(def
 v14_l51
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t15_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l51)))


(def
 v17_l59
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t18_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l59)))


(def
 v20_l71
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t21_l75
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l71)))


(def v22_l79 (kind/doc #'sk/lay))


(def
 v24_l83
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t25_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v24_l83)))


(def
 v27_l91
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t28_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v27_l91)))


(def v30_l103 (kind/doc #'sk/point))


(def v32_l107 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t33_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v32_l107)))


(def
 v35_l115
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color :group})]))


(deftest
 t36_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v35_l115)))


(def
 v38_l123
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color "#E74C3C"})]))


(deftest
 t39_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v38_l123)))


(def
 v41_l131
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :size :petal_length,
     :color :species})]))


(deftest
 t42_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v41_l131)))


(def
 v44_l140
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :size 6})]))


(deftest
 t45_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v44_l140)))


(def
 v47_l148
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :alpha 0.3})]))


(deftest
 t48_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v47_l148)))


(def
 v50_l156
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha 0.5,
     :size 5})]))


(deftest
 t51_l159
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v50_l156)))


(def
 v53_l165
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha :petal_length})]))


(deftest
 t54_l168
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v53_l165)))


(def v55_l172 (kind/doc #'sk/line))


(def
 v57_l176
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__97534#] (Math/sin (* p1__97534# 0.3)))
     (range 30))})))


(def v58_l179 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t59_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v58_l179)))


(def
 v61_l187
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__97535#] (Math/sin (* p1__97535# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__97536#] (Math/cos (* p1__97536# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v62_l192
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t63_l194
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v62_l192)))


(def v65_l200 (sk/plot [(sk/line {:data wave, :x :x, :y :y, :size 4})]))


(deftest
 t66_l202
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v65_l200)))


(def v67_l206 (kind/doc #'sk/histogram))


(def
 v69_l210
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t70_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v69_l210)))


(def
 v72_l218
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t73_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v72_l218)))


(def v74_l224 (kind/doc #'sk/bar))


(def v76_l228 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t77_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v76_l228)))


(def
 v79_l236
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t80_l238
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v79_l236)))


(def
 v82_l244
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t83_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v82_l244)))


(def v84_l250 (kind/doc #'sk/stacked-bar))


(def
 v86_l254
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v87_l257
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t88_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v87_l257)))


(def v89_l264 (kind/doc #'sk/stacked-bar-fill))


(def
 v91_l268
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t92_l270
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v91_l268)))


(def v93_l274 (kind/doc #'sk/value-bar))


(def
 v95_l278
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t96_l280
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v95_l278)))


(def v97_l284 (kind/doc #'sk/lm))


(def
 v99_l288
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t100_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v99_l288)))


(def
 v102_l299
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t103_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v102_l299)))


(def v104_l309 (kind/doc #'sk/loess))


(def
 v106_l313
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__97537#]
       (+
        (Math/sin (* p1__97537# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v107_l318
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t108_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v107_l318)))


(def v109_l327 (kind/doc #'sk/density))


(def
 v111_l331
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t112_l336
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v111_l331)))


(def
 v114_l342
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t115_l347
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v114_l342)))


(def
 v117_l353
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t118_l358
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v117_l353)))


(def v119_l362 (kind/doc #'sk/area))


(def
 v121_l366
 (->
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__97538#] (Math/sin (* p1__97538# 0.3)))
     (range 30))})
  (sk/view [[:x :y]])
  (sk/lay (sk/area))
  sk/plot))


(deftest
 t122_l372
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v121_l366)))


(def v123_l376 (kind/doc #'sk/stacked-area))


(def
 v125_l380
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
 t126_l389
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v125_l380)))


(def v127_l393 (kind/doc #'sk/text))


(def
 v129_l397
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t130_l402
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v129_l397)))


(def
 v132_l408
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/text {:text :name}))
  sk/plot))


(deftest
 t133_l413
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:points s)))))
   v132_l408)))


(def v134_l417 (kind/doc #'sk/boxplot))


(def
 v136_l421
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t137_l426
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v136_l421)))


(def
 v139_l433
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t140_l438
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v139_l433)))


(def v141_l443 (kind/doc #'sk/violin))


(def
 v143_l447
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t144_l452
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v143_l447)))


(def
 v146_l458
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t147_l463
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v146_l458)))


(def v148_l467 (kind/doc #'sk/errorbar))


(def
 v150_l471
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t151_l477
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v150_l471)))


(def v152_l481 (kind/doc #'sk/lollipop))


(def
 v154_l485
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t155_l490
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v154_l485)))


(def
 v157_l496
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  (sk/coord :flip)
  sk/plot))


(deftest
 t158_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v157_l496)))


(def v159_l507 (kind/doc #'sk/tile))


(def
 v161_l511
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t162_l516
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v161_l511)))


(def
 v164_l522
 (def
  grid-data
  (tc/dataset
   {:x (for [i (range 5) j (range 5)] i),
    :y (for [i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rand-int 100))))})))


(def
 v165_l527
 (sk/plot [(sk/tile {:data grid-data, :x :x, :y :y, :fill :value})]))


(deftest
 t166_l529
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v165_l527)))


(def v167_l533 (kind/doc #'sk/density2d))


(def
 v169_l537
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t170_l542
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v169_l537)))


(def
 v172_l548
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  (sk/lay (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t173_l554
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v172_l548)))


(def v174_l559 (kind/doc #'sk/contour))


(def
 v176_l563
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/contour))
  sk/plot))


(deftest
 t177_l568
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v176_l563)))


(def
 v179_l574
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:alpha 0.3}) (sk/contour {:levels 8}))
  sk/plot))


(deftest
 t180_l579
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v179_l574)))


(def v181_l582 (kind/doc #'sk/ridgeline))


(def
 v183_l586
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t184_l591
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v183_l586)))


(def v185_l595 (kind/doc #'sk/rug))


(def
 v187_l599
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/rug {:color :species}))
  sk/plot))


(deftest
 t188_l604
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 150 (:lines s)))))
   v187_l599)))


(def
 v190_l610
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rug {:side :both}))
  sk/plot))


(deftest
 t191_l615
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v190_l610)))


(def v192_l620 (kind/doc #'sk/step))


(def
 v194_l624
 (-> tiny (sk/view [[:x :y]]) (sk/lay (sk/step) (sk/point)) sk/plot))


(deftest
 t195_l629
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v194_l624)))


(def
 v197_l635
 (->
  tiny
  (sk/view [[:x :y]])
  (sk/lay (sk/step {:color :group}) (sk/point {:color :group}))
  sk/plot))


(deftest
 t198_l640
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 2 (:lines s)))))
   v197_l635)))


(def v199_l644 (kind/doc #'sk/summary))


(def
 v201_l648
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary))
  sk/plot))


(deftest
 t202_l653
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v201_l648)))


(def
 v204_l659
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary {:color :species}))
  sk/plot))


(deftest
 t205_l664
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v204_l659)))


(def v207_l670 (kind/doc #'sk/plot))


(def v209_l674 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t210_l676
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v209_l674)))


(def
 v212_l682
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t213_l689
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v212_l682)))


(def
 v215_l695
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t216_l698
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v215_l695)))


(def
 v218_l703
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:tooltip true})))


(deftest t219_l708 (is ((fn [v] (= :div (first v))) v218_l703)))


(def
 v221_l712
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:brush true})))


(deftest t222_l717 (is ((fn [v] (= :div (first v))) v221_l712)))


(def
 v224_l721
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t225_l727
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v224_l721)))


(def
 v227_l732
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:legend-position :bottom})))


(deftest
 t228_l737
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v227_l732)))


(def v229_l741 (kind/doc #'sk/sketch))


(def
 v231_l746
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v232_l748
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t233_l750
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v232_l748)))


(def
 v235_l755
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t236_l761
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v235_l755)))


(def v238_l769 (kind/doc #'sk/views->sketch))


(def
 v240_l773
 (def sk2 (sk/views->sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v241_l775 (= (keys sk1) (keys sk2)))


(deftest t242_l777 (is (true? v241_l775)))


(def v243_l779 (kind/doc #'sk/sketch->membrane))


(def v245_l783 (def m1 (sk/sketch->membrane sk1)))


(def v246_l785 (vector? m1))


(deftest t247_l787 (is (true? v246_l785)))


(def v248_l789 (kind/doc #'sk/membrane->figure))


(def
 v250_l793
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t251_l797 (is ((fn [v] (= :svg v)) v250_l793)))


(def v252_l799 (kind/doc #'sk/sketch->figure))


(def v254_l803 (first (sk/sketch->figure sk1 :svg {})))


(deftest t255_l805 (is ((fn [v] (= :svg v)) v254_l803)))


(def v257_l810 (kind/doc #'sk/coord))


(def
 v259_l814
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t260_l820
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (->
       iris
       (sk/view :species)
       (sk/lay (sk/bar))
       (sk/coord :polar)
       sk/plot)
      (kind/test-last
       [(fn
         [v]
         (let
          [s (sk/svg-summary v)]
          (and (= 1 (:panels s)) (pos? (:polygons s)))))])
      (= 3 (:polygons s)))))
   v259_l814)))


(def v261_l837 (kind/doc #'sk/scale))


(def
 v263_l841
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t264_l847
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v263_l841)))


(def
 v266_l853
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t267_l859
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v266_l853)))


(def
 v269_l865
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t270_l871
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v269_l865)))


(def v271_l875 (kind/doc #'sk/labs))


(def
 v273_l879
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
 t274_l885
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (some #{"Iris Dimensions"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v273_l879)))


(def v276_l891 (kind/doc #'sk/rule-v))


(def
 v278_l895
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t279_l901
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v278_l895)))


(def v280_l905 (kind/doc #'sk/rule-h))


(def
 v282_l909
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t283_l915
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v282_l909)))


(def v284_l919 (kind/doc #'sk/band-v))


(def
 v286_l923
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t287_l929
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:panels s)))))
   v286_l923)))


(def v288_l933 (kind/doc #'sk/band-h))


(def
 v290_l937
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t291_l943
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:panels s)))))
   v290_l937)))


(def v293_l950 (kind/doc #'sk/cross))


(def v295_l954 (sk/cross [:a :b] [1 2 3]))


(deftest
 t296_l956
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v295_l954)))


(def
 v298_l960
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t299_l966
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v298_l960)))


(def v300_l970 (kind/doc #'sk/pairs))


(def v302_l974 (sk/pairs [:a :b :c]))


(deftest
 t303_l976
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v302_l974)))


(def
 v305_l980
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t306_l985
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v305_l980)))


(def v307_l989 (kind/doc #'sk/distribution))


(def
 v309_l993
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t310_l997
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v309_l993)))


(def v312_l1003 (kind/doc #'sk/facet))


(def
 v314_l1007
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t315_l1013
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v314_l1007)))


(def
 v317_l1019
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t318_l1025
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v317_l1019)))


(def v319_l1029 (kind/doc #'sk/facet-grid))


(def
 v321_l1033
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t322_l1039
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v321_l1033)))


(def
 v324_l1045
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t325_l1051
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v324_l1045)))


(def v327_l1057 (kind/doc #'sk/svg-summary))


(def
 v329_l1061
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t330_l1067
 (is
  ((fn
    [m]
    (and
     (= 1 (:panels m))
     (= 150 (:points m))
     (zero? (:lines m))
     (zero? (:polygons m))))
   v329_l1061)))


(def
 v332_l1074
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:panels :points])))


(deftest
 t333_l1082
 (is ((fn [m] (and (= 3 (:panels m)) (= 150 (:points m)))) v332_l1074)))


(def
 v335_l1087
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:points :lines])))


(deftest
 t336_l1095
 (is ((fn [m] (and (= 150 (:points m)) (= 3 (:lines m)))) v335_l1087)))


(def
 v338_l1101
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Iris Scatter"})
  sk/plot
  sk/svg-summary
  :texts))


(deftest
 t339_l1109
 (is
  ((fn
    [ts]
    (and
     (some #{"Iris Scatter"} ts)
     (some #{"sepal length"} ts)
     (some #{"setosa"} ts)))
   v338_l1101)))


(def v340_l1113 (kind/doc #'sk/valid-sketch?))


(def v341_l1115 (sk/valid-sketch? sk1))


(deftest t342_l1117 (is (true? v341_l1115)))


(def v343_l1119 (kind/doc #'sk/explain-sketch))


(def v345_l1123 (sk/explain-sketch sk1))


(deftest t346_l1125 (is (nil? v345_l1123)))
