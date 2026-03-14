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
     (fn* [p1__76394#] (Math/sin (* p1__76394# 0.3)))
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
       (fn* [p1__76395#] (Math/sin (* p1__76395# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__76396#] (Math/cos (* p1__76396# 0.3)))
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


(def v89_l263 (kind/doc #'sk/value-bar))


(def
 v91_l267
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t92_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v91_l267)))


(def v93_l273 (kind/doc #'sk/lm))


(def
 v95_l277
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t96_l282
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v95_l277)))


(def
 v98_l288
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t99_l294
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v98_l288)))


(def v100_l298 (kind/doc #'sk/loess))


(def
 v102_l302
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (range 50),
     :y
     (mapv
      (fn*
       [p1__76397#]
       (+
        (Math/sin (* p1__76397# 0.2))
        (* 0.3 (- (rng/drandom r) 0.5))))
      (range 50))}))))


(def
 v103_l307
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t104_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v103_l307)))


(def v105_l316 (kind/doc #'sk/density))


(def
 v107_l320
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t108_l325
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v107_l320)))


(def
 v110_l331
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t111_l336
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v110_l331)))


(def
 v113_l342
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t114_l347
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v113_l342)))


(def v115_l351 (kind/doc #'sk/area))


(def
 v117_l355
 (->
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__76398#] (Math/sin (* p1__76398# 0.3)))
     (range 30))})
  (sk/view [[:x :y]])
  (sk/lay (sk/area))
  sk/plot))


(deftest
 t118_l361
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v117_l355)))


(def v119_l365 (kind/doc #'sk/stacked-area))


(def
 v121_l369
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
 t122_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v121_l369)))


(def v123_l382 (kind/doc #'sk/text))


(def
 v125_l386
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t126_l391
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v125_l386)))


(def
 v128_l397
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/text {:text :name}))
  sk/plot))


(deftest
 t129_l402
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:points s)))))
   v128_l397)))


(def v130_l406 (kind/doc #'sk/boxplot))


(def
 v132_l410
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t133_l415
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v132_l410)))


(def
 v135_l422
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t136_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v135_l422)))


(def v137_l432 (kind/doc #'sk/violin))


(def
 v139_l436
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t140_l441
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v139_l436)))


(def
 v142_l447
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t143_l452
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v142_l447)))


(def v144_l456 (kind/doc #'sk/errorbar))


(def
 v146_l460
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t147_l466
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v146_l460)))


(def v148_l470 (kind/doc #'sk/lollipop))


(def
 v150_l474
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t151_l479
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v150_l474)))


(def
 v153_l485
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  (sk/coord :flip)
  sk/plot))


(deftest
 t154_l491
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v153_l485)))


(def v155_l496 (kind/doc #'sk/tile))


(def
 v157_l500
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t158_l505
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v157_l500)))


(def
 v160_l511
 (def
  grid-data
  (tc/dataset
   {:x (for [i (range 5) j (range 5)] i),
    :y (for [i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rand-int 100))))})))


(def
 v161_l516
 (sk/plot [(sk/tile {:data grid-data, :x :x, :y :y, :fill :value})]))


(deftest
 t162_l518
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v161_l516)))


(def v163_l522 (kind/doc #'sk/density2d))


(def
 v165_l526
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  sk/plot))


(deftest
 t166_l531
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v165_l526)))


(def
 v168_l537
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/density2d))
  (sk/lay (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t169_l543
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:tiles s)))))
   v168_l537)))


(def v170_l547 (kind/doc #'sk/ridgeline))


(def
 v172_l551
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t173_l556
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v172_l551)))


(def v174_l560 (kind/doc #'sk/rug))


(def
 v176_l564
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/rug {:color :species}))
  sk/plot))


(deftest
 t177_l569
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 150 (:lines s)))))
   v176_l564)))


(def
 v179_l575
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rug {:side :both}))
  sk/plot))


(deftest
 t180_l580
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v179_l575)))


(def v181_l585 (kind/doc #'sk/step))


(def
 v183_l589
 (-> tiny (sk/view [[:x :y]]) (sk/lay (sk/step) (sk/point)) sk/plot))


(deftest
 t184_l594
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v183_l589)))


(def
 v186_l600
 (->
  tiny
  (sk/view [[:x :y]])
  (sk/lay (sk/step {:color :group}) (sk/point {:color :group}))
  sk/plot))


(deftest
 t187_l605
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 2 (:lines s)))))
   v186_l600)))


(def v188_l609 (kind/doc #'sk/summary))


(def
 v190_l613
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary))
  sk/plot))


(deftest
 t191_l618
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v190_l613)))


(def
 v193_l624
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/summary {:color :species}))
  sk/plot))


(deftest
 t194_l629
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v193_l624)))


(def v196_l635 (kind/doc #'sk/plot))


(def v198_l639 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t199_l641
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v198_l639)))


(def
 v201_l647
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t202_l654
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v201_l647)))


(def
 v204_l660
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t205_l663
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v204_l660)))


(def
 v207_l668
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:tooltip true})))


(deftest t208_l673 (is ((fn [v] (= :div (first v))) v207_l668)))


(def v209_l674 (kind/doc #'sk/sketch))


(def
 v211_l679
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v212_l681
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t213_l683
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v212_l681)))


(def
 v215_l688
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t216_l694
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v215_l688)))


(def v218_l702 (kind/doc #'sk/views->sketch))


(def
 v220_l706
 (def sk2 (sk/views->sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def v221_l708 (= (keys sk1) (keys sk2)))


(deftest t222_l710 (is (true? v221_l708)))


(def v223_l712 (kind/doc #'sk/sketch->membrane))


(def v225_l716 (def m1 (sk/sketch->membrane sk1)))


(def v226_l718 (vector? m1))


(deftest t227_l720 (is (true? v226_l718)))


(def v228_l722 (kind/doc #'sk/membrane->figure))


(def
 v230_l726
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t231_l730 (is ((fn [v] (= :svg v)) v230_l726)))


(def v232_l732 (kind/doc #'sk/sketch->figure))


(def v234_l736 (first (sk/sketch->figure sk1 :svg {})))


(deftest t235_l738 (is ((fn [v] (= :svg v)) v234_l736)))


(def v237_l743 (kind/doc #'sk/coord))


(def
 v239_l747
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t240_l753
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
   v239_l747)))


(def v241_l770 (kind/doc #'sk/scale))


(def
 v243_l774
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t244_l780
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v243_l774)))


(def
 v246_l786
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t247_l792
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v246_l786)))


(def
 v249_l798
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t250_l804
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v249_l798)))


(def v251_l808 (kind/doc #'sk/labs))


(def
 v253_l812
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
 t254_l818
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (some #{"Iris Dimensions"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v253_l812)))


(def v256_l824 (kind/doc #'sk/rule-v))


(def
 v258_l828
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t259_l834
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v258_l828)))


(def v260_l838 (kind/doc #'sk/rule-h))


(def
 v262_l842
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t263_l848
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v262_l842)))


(def v264_l852 (kind/doc #'sk/band-v))


(def
 v266_l856
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t267_l862
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:polygons s)))))
   v266_l856)))


(def v268_l866 (kind/doc #'sk/band-h))


(def
 v270_l870
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t271_l876
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:polygons s)))))
   v270_l870)))


(def v273_l883 (kind/doc #'sk/cross))


(def v275_l887 (sk/cross [:a :b] [1 2 3]))


(deftest
 t276_l889
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v275_l887)))


(def
 v278_l893
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t279_l899
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v278_l893)))


(def v280_l903 (kind/doc #'sk/pairs))


(def v282_l907 (sk/pairs [:a :b :c]))


(deftest
 t283_l909
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v282_l907)))


(def
 v285_l913
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t286_l918
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v285_l913)))


(def v287_l922 (kind/doc #'sk/distribution))


(def
 v289_l926
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t290_l930
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v289_l926)))


(def v292_l936 (kind/doc #'sk/facet))


(def
 v294_l940
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t295_l946
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v294_l940)))


(def
 v297_l952
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t298_l958
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v297_l952)))


(def v299_l962 (kind/doc #'sk/facet-grid))


(def
 v301_l966
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t302_l972
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v301_l966)))


(def
 v304_l978
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t305_l984
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v304_l978)))


(def v307_l990 (kind/doc #'sk/svg-summary))


(def
 v309_l994
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t310_l1000
 (is
  ((fn
    [m]
    (and
     (= 1 (:panels m))
     (= 150 (:points m))
     (zero? (:lines m))
     (zero? (:polygons m))))
   v309_l994)))


(def
 v312_l1007
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:panels :points])))


(deftest
 t313_l1015
 (is ((fn [m] (and (= 3 (:panels m)) (= 150 (:points m)))) v312_l1007)))


(def
 v315_l1020
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:points :lines])))


(deftest
 t316_l1028
 (is ((fn [m] (and (= 150 (:points m)) (= 3 (:lines m)))) v315_l1020)))


(def
 v318_l1034
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Iris Scatter"})
  sk/plot
  sk/svg-summary
  :texts))


(deftest
 t319_l1042
 (is
  ((fn
    [ts]
    (and
     (some #{"Iris Scatter"} ts)
     (some #{"sepal length"} ts)
     (some #{"setosa"} ts)))
   v318_l1034)))


(def v320_l1046 (kind/doc #'sk/valid-sketch?))


(def v321_l1048 (sk/valid-sketch? sk1))


(deftest t322_l1050 (is (true? v321_l1048)))


(def v323_l1052 (kind/doc #'sk/explain-sketch))


(def v325_l1056 (sk/explain-sketch sk1))


(deftest t326_l1058 (is (nil? v325_l1056)))
