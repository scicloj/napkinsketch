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
     (fn* [p1__77749#] (Math/sin (* p1__77749# 0.3)))
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
       (fn* [p1__77750#] (Math/sin (* p1__77750# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__77751#] (Math/cos (* p1__77751# 0.3)))
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
       [p1__77752#]
       (+
        (Math/sin (* p1__77752# 0.2))
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
     (fn* [p1__77753#] (Math/sin (* p1__77753# 0.3)))
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


(def v119_l365 (kind/doc #'sk/text))


(def
 v121_l369
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t122_l374
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v121_l369)))


(def
 v124_l380
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/text {:text :name}))
  sk/plot))


(deftest
 t125_l385
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:points s)))))
   v124_l380)))


(def v126_l389 (kind/doc #'sk/boxplot))


(def
 v128_l393
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t129_l398
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v128_l393)))


(def
 v131_l405
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t132_l410
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v131_l405)))


(def v133_l415 (kind/doc #'sk/violin))


(def
 v135_l419
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t136_l424
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v135_l419)))


(def
 v138_l430
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t139_l435
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v138_l430)))


(def v140_l439 (kind/doc #'sk/errorbar))


(def
 v142_l443
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t143_l449
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v142_l443)))


(def v144_l453 (kind/doc #'sk/lollipop))


(def
 v146_l457
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t147_l462
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v146_l457)))


(def
 v149_l468
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  (sk/coord :flip)
  sk/plot))


(deftest
 t150_l474
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v149_l468)))


(def v151_l479 (kind/doc #'sk/tile))


(def
 v153_l483
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/tile))
  sk/plot))


(deftest
 t154_l488
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v153_l483)))


(def
 v156_l494
 (def
  grid-data
  (tc/dataset
   {:x (for [i (range 5) j (range 5)] i),
    :y (for [i (range 5) j (range 5)] j),
    :value (vec (repeatedly 25 (fn* [] (rand-int 100))))})))


(def
 v157_l499
 (sk/plot [(sk/tile {:data grid-data, :x :x, :y :y, :fill :value})]))


(deftest
 t158_l501
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:tiles s)))))
   v157_l499)))


(def v159_l505 (kind/doc #'sk/ridgeline))


(def
 v161_l509
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline))
  sk/plot))


(deftest
 t162_l514
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v161_l509)))


(def v164_l521 (kind/doc #'sk/plot))


(def v166_l525 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t167_l527
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v166_l525)))


(def
 v169_l533
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t170_l540
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v169_l533)))


(def
 v172_l546
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t173_l549
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v172_l546)))


(def v174_l551 (kind/doc #'sk/sketch))


(def
 v176_l556
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v177_l558
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t178_l560
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v177_l558)))


(def
 v180_l565
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t181_l571
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v180_l565)))


(def v183_l579 (kind/doc #'sk/coord))


(def
 v185_l583
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t186_l589
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
   v185_l583)))


(def v187_l606 (kind/doc #'sk/scale))


(def
 v189_l610
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t190_l616
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v189_l610)))


(def
 v192_l622
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t193_l628
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v192_l622)))


(def
 v195_l634
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t196_l640
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v195_l634)))


(def v197_l644 (kind/doc #'sk/labs))


(def
 v199_l648
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
 t200_l654
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (some #{"Iris Dimensions"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v199_l648)))


(def v202_l660 (kind/doc #'sk/rule-v))


(def
 v204_l664
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t205_l670
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v204_l664)))


(def v206_l674 (kind/doc #'sk/rule-h))


(def
 v208_l678
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t209_l684
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v208_l678)))


(def v210_l688 (kind/doc #'sk/band-v))


(def
 v212_l692
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t213_l698
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:polygons s)))))
   v212_l692)))


(def v214_l702 (kind/doc #'sk/band-h))


(def
 v216_l706
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t217_l712
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:polygons s)))))
   v216_l706)))


(def v219_l719 (kind/doc #'sk/cross))


(def v221_l723 (sk/cross [:a :b] [1 2 3]))


(deftest
 t222_l725
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v221_l723)))


(def
 v224_l729
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t225_l735
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v224_l729)))


(def v226_l739 (kind/doc #'sk/pairs))


(def v228_l743 (sk/pairs [:a :b :c]))


(deftest
 t229_l745
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v228_l743)))


(def
 v231_l749
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t232_l754
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 450 (:points s)))))
   v231_l749)))


(def v233_l758 (kind/doc #'sk/distribution))


(def
 v235_l762
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t236_l766
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v235_l762)))


(def v238_l772 (kind/doc #'sk/facet))


(def
 v240_l776
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t241_l782
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v240_l776)))


(def
 v243_l788
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t244_l794
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v243_l788)))


(def v245_l798 (kind/doc #'sk/facet-grid))


(def
 v247_l802
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t248_l808
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v247_l802)))


(def
 v250_l814
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t251_l820
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v250_l814)))


(def v253_l826 (kind/doc #'sk/svg-summary))


(def
 v255_l830
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t256_l836
 (is
  ((fn
    [m]
    (and
     (= 1 (:panels m))
     (= 150 (:points m))
     (zero? (:lines m))
     (zero? (:polygons m))))
   v255_l830)))


(def
 v258_l843
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:panels :points])))


(deftest
 t259_l851
 (is ((fn [m] (and (= 3 (:panels m)) (= 150 (:points m)))) v258_l843)))


(def
 v261_l856
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:points :lines])))


(deftest
 t262_l864
 (is ((fn [m] (and (= 150 (:points m)) (= 3 (:lines m)))) v261_l856)))


(def
 v264_l870
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Iris Scatter"})
  sk/plot
  sk/svg-summary
  :texts))


(deftest
 t265_l878
 (is
  ((fn
    [ts]
    (and
     (some #{"Iris Scatter"} ts)
     (some #{"sepal length"} ts)
     (some #{"setosa"} ts)))
   v264_l870)))
