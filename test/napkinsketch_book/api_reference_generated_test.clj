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


(def v8_l33 (kind/doc #'sk/view))


(def
 v10_l37
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t11_l39
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v10_l37)))


(def
 v13_l43
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t14_l45
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v13_l43)))


(def
 v16_l49
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t17_l55
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v16_l49)))


(def
 v19_l59
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l63
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v19_l59)))


(def v21_l65 (kind/doc #'sk/lay))


(def
 v23_l69
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t24_l71
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v23_l69)))


(def
 v26_l75
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t27_l81
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v26_l75)))


(def v29_l85 (kind/doc #'sk/point))


(def v31_l89 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t32_l91
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v31_l89)))


(def
 v34_l95
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color :group})]))


(deftest
 t35_l97
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v34_l95)))


(def
 v37_l101
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color "#E74C3C"})]))


(deftest
 t38_l103
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v37_l101)))


(def
 v40_l107
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :size :petal_length,
     :color :species})]))


(deftest
 t41_l110
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v40_l107)))


(def
 v43_l114
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :size 6})]))


(deftest
 t44_l116
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v43_l114)))


(def
 v46_l120
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :alpha 0.3})]))


(deftest
 t47_l122
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v46_l120)))


(def
 v49_l126
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha 0.5,
     :size 5})]))


(deftest
 t50_l129
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v49_l126)))


(def
 v52_l133
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha :petal_length})]))


(deftest
 t53_l136
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v52_l133)))


(def v54_l138 (kind/doc #'sk/line))


(def
 v56_l142
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__86901#] (Math/sin (* p1__86901# 0.3)))
     (range 30))})))


(def v57_l145 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t58_l147
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v57_l145)))


(def
 v60_l151
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__86902#] (Math/sin (* p1__86902# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__86903#] (Math/cos (* p1__86903# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v61_l156
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t62_l158
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v61_l156)))


(def v64_l162 (sk/plot [(sk/line {:data wave, :x :x, :y :y, :size 4})]))


(deftest
 t65_l164
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v64_l162)))


(def v66_l166 (kind/doc #'sk/histogram))


(def
 v68_l170
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t69_l172
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v68_l170)))


(def
 v71_l176
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t72_l178
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v71_l176)))


(def v73_l180 (kind/doc #'sk/bar))


(def v75_l184 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t76_l186
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v75_l184)))


(def
 v78_l190
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t79_l192
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v78_l190)))


(def
 v81_l196
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t82_l198
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v81_l196)))


(def v83_l200 (kind/doc #'sk/stacked-bar))


(def
 v85_l204
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v86_l207
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t87_l209
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v86_l207)))


(def v88_l211 (kind/doc #'sk/value-bar))


(def
 v90_l215
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t91_l217
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v90_l215)))


(def v92_l219 (kind/doc #'sk/lm))


(def
 v94_l223
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t95_l228
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v94_l223)))


(def
 v97_l232
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t98_l238
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v97_l232)))


(def v99_l240 (kind/doc #'sk/loess))


(def
 v101_l244
 (def
  noisy-wave
  (tc/dataset
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__86904#]
      (+ (Math/sin (* p1__86904# 0.2)) (* 0.3 (- (rand) 0.5))))
     (range 50))})))


(def
 v102_l248
 (->
  noisy-wave
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/loess))
  sk/plot))


(deftest
 t103_l253
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v102_l248)))


(def v104_l255 (kind/doc #'sk/density))


(def
 v106_l259
 (-> iris (sk/view [[:sepal_length]]) (sk/lay (sk/density)) sk/plot))


(deftest
 t107_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v106_l259)))


(def
 v109_l270
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t110_l275
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v109_l270)))


(def
 v112_l281
 (->
  iris
  (sk/view [[:sepal_length]])
  (sk/lay (sk/density {:bandwidth 0.3}))
  sk/plot))


(deftest
 t113_l286
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v112_l281)))


(def v114_l290 (kind/doc #'sk/area))


(def
 v116_l294
 (->
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__86905#] (Math/sin (* p1__86905# 0.3)))
     (range 30))})
  (sk/view [[:x :y]])
  (sk/lay (sk/area))
  sk/plot))


(deftest
 t117_l300
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v116_l294)))


(def v118_l304 (kind/doc #'sk/text))


(def
 v120_l308
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/text {:text :name}))
  sk/plot))


(deftest
 t121_l313
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v120_l308)))


(def
 v123_l319
 (->
  (tc/dataset {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/text {:text :name}))
  sk/plot))


(deftest
 t124_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:points s)))))
   v123_l319)))


(def v125_l328 (kind/doc #'sk/boxplot))


(def
 v127_l332
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/boxplot))
  sk/plot))


(deftest
 t128_l337
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v127_l332)))


(def
 v130_l344
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/boxplot {:color :smoker}))
  sk/plot))


(deftest
 t131_l349
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v130_l344)))


(def v133_l358 (kind/doc #'sk/plot))


(def v135_l362 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t136_l364
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v135_l362)))


(def
 v138_l368
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t139_l375
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v138_l368)))


(def
 v141_l379
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t142_l382
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v141_l379)))


(def v143_l384 (kind/doc #'sk/sketch))


(def
 v145_l389
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v146_l391
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t147_l393
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v146_l391)))


(def
 v149_l398
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t150_l404
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v149_l398)))


(def v152_l412 (kind/doc #'sk/coord))


(def
 v154_l416
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t155_l422
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v154_l416)))


(def v156_l424 (kind/doc #'sk/scale))


(def
 v158_l428
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t159_l434
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v158_l428)))


(def
 v161_l438
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t162_l444
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v161_l438)))


(def
 v164_l448
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t165_l454
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v164_l448)))


(def v167_l458 (kind/doc #'sk/cross))


(def v169_l462 (sk/cross [:a :b] [1 2 3]))


(deftest
 t170_l464
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v169_l462)))


(def
 v172_l468
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t173_l474
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v172_l468)))


(def v174_l476 (kind/doc #'sk/pairs))


(def v176_l480 (sk/pairs [:a :b :c]))


(deftest
 t177_l482
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v176_l480)))


(def
 v179_l486
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t180_l491
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v179_l486)))


(def v181_l493 (kind/doc #'sk/distribution))


(def
 v183_l497
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t184_l501
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v183_l497)))


(def v186_l505 (kind/doc #'sk/facet))


(def
 v188_l509
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t189_l515
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v188_l509)))


(def
 v191_l519
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t192_l525
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v191_l519)))


(def v193_l527 (kind/doc #'sk/facet-grid))


(def
 v195_l531
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t196_l537
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v195_l531)))


(def
 v198_l541
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t199_l547
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v198_l541)))


(def v201_l551 (kind/doc #'sk/svg-summary))


(def
 v203_l555
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary))


(deftest
 t204_l561
 (is
  ((fn
    [m]
    (and
     (= 1 (:panels m))
     (= 150 (:points m))
     (zero? (:lines m))
     (zero? (:polygons m))))
   v203_l555)))


(def
 v206_l568
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:panels :points])))


(deftest
 t207_l576
 (is ((fn [m] (and (= 3 (:panels m)) (= 150 (:points m)))) v206_l568)))


(def
 v209_l581
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot
  sk/svg-summary
  (select-keys [:points :lines])))


(deftest
 t210_l589
 (is ((fn [m] (and (= 150 (:points m)) (= 3 (:lines m)))) v209_l581)))


(def
 v212_l595
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Iris Scatter"})
  sk/plot
  sk/svg-summary
  :texts))


(deftest
 t213_l603
 (is
  ((fn
    [ts]
    (and
     (some #{"Iris Scatter"} ts)
     (some #{"sepal length"} ts)
     (some #{"setosa"} ts)))
   v212_l595)))
