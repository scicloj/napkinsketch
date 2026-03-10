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
     (fn* [p1__78212#] (Math/sin (* p1__78212# 0.3)))
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
       (fn* [p1__78213#] (Math/sin (* p1__78213# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__78214#] (Math/cos (* p1__78214# 0.3)))
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


(def v100_l242 (kind/doc #'sk/plot))


(def v102_l246 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t103_l248
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v102_l246)))


(def
 v105_l252
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t106_l259
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v105_l252)))


(def
 v108_l263
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t109_l266
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v108_l263)))


(def v110_l268 (kind/doc #'sk/sketch))


(def
 v112_l273
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v113_l275
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t114_l277
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v113_l275)))


(def
 v116_l282
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t117_l288
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v116_l282)))


(def v119_l296 (kind/doc #'sk/coord))


(def
 v121_l300
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t122_l306
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v121_l300)))


(def v123_l308 (kind/doc #'sk/scale))


(def
 v125_l312
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t126_l318
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v125_l312)))


(def
 v128_l322
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t129_l328
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v128_l322)))


(def
 v131_l332
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t132_l338
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v131_l332)))


(def v134_l342 (kind/doc #'sk/cross))


(def v136_l346 (sk/cross [:a :b] [1 2 3]))


(deftest
 t137_l348
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v136_l346)))


(def
 v139_l352
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t140_l358
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v139_l352)))


(def v141_l360 (kind/doc #'sk/pairs))


(def v143_l364 (sk/pairs [:a :b :c]))


(deftest
 t144_l366
 (is ((fn [v] (= [[:a :b] [:a :c] [:b :c]] v)) v143_l364)))


(def
 v146_l370
 (->
  iris
  (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t147_l375
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v146_l370)))


(def v148_l377 (kind/doc #'sk/distribution))


(def
 v150_l381
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  (sk/lay (sk/histogram))
  sk/plot))


(deftest
 t151_l385
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v150_l381)))


(def v153_l389 (kind/doc #'sk/facet))


(def
 v155_l393
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t156_l399
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v155_l393)))


(def
 v158_l403
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t159_l409
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v158_l403)))


(def v160_l411 (kind/doc #'sk/facet-grid))


(def
 v162_l415
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t163_l421
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v162_l415)))


(def
 v165_l425
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t166_l431
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v165_l425)))
