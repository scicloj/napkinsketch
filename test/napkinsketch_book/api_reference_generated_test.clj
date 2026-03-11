(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l18
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l21
 (def
  tiny
  (tc/dataset
   {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]})))


(def
 v5_l25
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v6_l28
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v7_l31
 (def
  measurements
  (tc/dataset
   {:treatment ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def v9_l38 (kind/doc #'sk/view))


(def
 v11_l42
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t12_l44
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v11_l42)))


(def
 v14_l48
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t15_l50
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v14_l48)))


(def
 v17_l54
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t18_l60
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v17_l54)))


(def
 v20_l64
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t21_l68
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v20_l64)))


(def v22_l70 (kind/doc #'sk/lay))


(def
 v24_l74
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t25_l76
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v24_l74)))


(def
 v27_l80
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t28_l86
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v27_l80)))


(def v30_l90 (kind/doc #'sk/point))


(def v32_l94 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t33_l96
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v32_l94)))


(def
 v35_l100
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color :group})]))


(deftest
 t36_l102
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v35_l100)))


(def
 v38_l106
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color "#E74C3C"})]))


(deftest
 t39_l108
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l106)))


(def
 v41_l112
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :size :petal_length,
     :color :species})]))


(deftest
 t42_l115
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v41_l112)))


(def
 v44_l119
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :size 6})]))


(deftest
 t45_l121
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v44_l119)))


(def
 v47_l125
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :alpha 0.3})]))


(deftest
 t48_l127
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v47_l125)))


(def
 v50_l131
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha 0.5,
     :size 5})]))


(deftest
 t51_l134
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v50_l131)))


(def
 v53_l138
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha :petal_length})]))


(deftest
 t54_l141
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v53_l138)))


(def v55_l143 (kind/doc #'sk/line))


(def
 v57_l147
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__74116#] (Math/sin (* p1__74116# 0.3)))
     (range 30))})))


(def v58_l150 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t59_l152
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v58_l150)))


(def
 v61_l156
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__74117#] (Math/sin (* p1__74117# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__74118#] (Math/cos (* p1__74118# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v62_l161
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t63_l163
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v62_l161)))


(def v65_l167 (sk/plot [(sk/line {:data wave, :x :x, :y :y, :size 4})]))


(deftest
 t66_l169
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v65_l167)))


(def v67_l171 (kind/doc #'sk/histogram))


(def
 v69_l175
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t70_l177
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v69_l175)))


(def
 v72_l181
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t73_l183
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v72_l181)))


(def v74_l185 (kind/doc #'sk/bar))


(def v76_l189 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t77_l191
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v76_l189)))


(def
 v79_l195
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t80_l197
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v79_l195)))


(def
 v82_l201
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t83_l203
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v82_l201)))


(def v84_l205 (kind/doc #'sk/stacked-bar))


(def
 v86_l209
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v87_l212
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t88_l214
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v87_l212)))


(def v89_l216 (kind/doc #'sk/value-bar))


(def
 v91_l220
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t92_l222
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v91_l220)))


(def v93_l224 (kind/doc #'sk/lm))


(def
 v95_l228
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t96_l233
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v95_l228)))


(def
 v98_l237
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t99_l243
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v98_l237)))


(def v100_l245 (kind/doc #'sk/loess))


(def
 v102_l249
 (def
  noisy-wave
  (tc/dataset
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__74119#]
      (+ (Math/sin (* p1__74119# 0.2)) (* 0.3 (- (rand) 0.5))))
     (range 50))})))


(def
 v103_l253
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t104_l258
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v103_l253)))


(def v105_l260 (kind/doc #'sk/density))


(def
 v107_l264
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t108_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v107_l264)))


(def
 v110_l275
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t111_l280
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v110_l275)))


(def
 v113_l286
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t114_l291
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v113_l286)))


(def v115_l295 (kind/doc #'sk/area))


(def
 v117_l299
 (->
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__74120#] (Math/sin (* p1__74120# 0.3)))
     (range 30))})
  (sk/view [[:x :y]])
  (sk/lay (sk/area))
  sk/plot))


(deftest
 t118_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v117_l299)))


(def v119_l309 (kind/doc #'sk/text))


(def
 v121_l313
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t122_l318
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v121_l313)))


(def
 v124_l324
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/text {:text :name}))
  sk/plot))


(deftest
 t125_l329
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:points s)))))
   v124_l324)))


(def v126_l333 (kind/doc #'sk/boxplot))


(def
 v128_l337
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t129_l342
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v128_l337)))


(def
 v131_l349
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t132_l354
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v131_l349)))


(def v133_l359 (kind/doc #'sk/violin))


(def
 v135_l363
 (-> tips (sk/view [[:day :total_bill]]) (sk/lay (sk/violin)) sk/plot))


(deftest
 t136_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v135_l363)))


(def
 v138_l374
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:color :smoker}))
  sk/plot))


(deftest
 t139_l379
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v138_l374)))


(def v140_l383 (kind/doc #'sk/errorbar))


(def
 v142_l387
 (->
  measurements
  (sk/view [[:treatment :mean]])
  (sk/lay (sk/point) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t143_l393
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v142_l387)))


(def v144_l397 (kind/doc #'sk/lollipop))


(def
 v146_l401
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t147_l406
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v146_l401)))


(def
 v149_l412
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  (sk/coord :flip)
  sk/plot))


(deftest
 t150_l418
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v149_l412)))


(def v152_l425 (kind/doc #'sk/plot))


(def v154_l429 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t155_l431
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v154_l429)))


(def
 v157_l435
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t158_l442
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v157_l435)))


(def
 v160_l446
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t161_l449
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v160_l446)))


(def v162_l451 (kind/doc #'sk/sketch))


(def
 v164_l456
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v165_l458
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t166_l460
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v165_l458)))


(def
 v168_l465
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t169_l471
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v168_l465)))


(def v171_l479 (kind/doc #'sk/coord))


(def
 v173_l483
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t174_l489
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v173_l483)))


(def v175_l491 (kind/doc #'sk/scale))


(def
 v177_l495
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t178_l501
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v177_l495)))


(def
 v180_l505
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t181_l511
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v180_l505)))


(def
 v183_l515
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t184_l521
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v183_l515)))


(def v185_l523 (kind/doc #'sk/labs))


(def
 v187_l527
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
 t188_l533
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (some #{"Iris Dimensions"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v187_l527)))


(def v190_l539 (kind/doc #'sk/rule-v))


(def
 v192_l543
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t193_l549
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v192_l543)))


(def v194_l553 (kind/doc #'sk/rule-h))


(def
 v196_l557
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/rule-h 3.0))
  sk/plot))


(deftest
 t197_l563
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v196_l557)))


(def v198_l565 (kind/doc #'sk/band-v))


(def
 v200_l569
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t201_l575
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:polygons s)))))
   v200_l569)))


(def v202_l579 (kind/doc #'sk/band-h))


(def
 v204_l583
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/band-h 2.5 3.5))
  sk/plot))


(deftest
 t205_l589
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v204_l583)))


(def v207_l594 (kind/doc #'sk/cross))


(def v209_l598 (sk/cross [:a :b] [1 2 3]))


(deftest
 t210_l600
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v209_l598)))


(def
 v212_l604
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t213_l610
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v212_l604)))


(def v214_l612 (kind/doc #'sk/pairs))


(def v216_l616 (sk/pairs [:a :b :c]))


(deftest
 t217_l618
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v216_l616)))


(def
 v219_l622
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t220_l627
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v219_l622)))


(def v221_l629 (kind/doc #'sk/distribution))


(def
 v223_l633
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t224_l637
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v223_l633)))


(def v226_l641 (kind/doc #'sk/facet))


(def
 v228_l645
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t229_l651
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v228_l645)))


(def
 v231_l655
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t232_l661
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v231_l655)))


(def v233_l663 (kind/doc #'sk/facet-grid))


(def
 v235_l667
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t236_l673
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v235_l667)))


(def
 v238_l677
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t239_l683
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v238_l677)))


(def v241_l687 (kind/doc #'sk/svg-summary))


(def
 v243_l691
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t244_l697
 (is
  ((fn
    [m]
    (and
     (= 1 (:panels m))
     (= 150 (:points m))
     (zero? (:lines m))
     (zero? (:polygons m))))
   v243_l691)))


(def
 v246_l704
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:panels :points])))


(deftest
 t247_l712
 (is ((fn [m] (and (= 3 (:panels m)) (= 150 (:points m)))) v246_l704)))


(def
 v249_l717
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:points :lines])))


(deftest
 t250_l725
 (is ((fn [m] (and (= 150 (:points m)) (= 3 (:lines m)))) v249_l717)))


(def
 v252_l731
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Iris Scatter"})
  sk/plot
  sk/svg-summary
  :texts))


(deftest
 t253_l739
 (is
  ((fn
    [ts]
    (and
     (some #{"Iris Scatter"} ts)
     (some #{"sepal length"} ts)
     (some #{"setosa"} ts)))
   v252_l731)))
