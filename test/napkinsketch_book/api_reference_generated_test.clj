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


(def v7_l30 (kind/doc #'sk/view))


(def
 v9_l34
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t10_l36
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v9_l34)))


(def
 v12_l40
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t13_l42
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v12_l40)))


(def
 v15_l46
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t16_l52
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v15_l46)))


(def
 v18_l56
 (->
  (sk/view iris {:x :sepal_length, :y :sepal_width})
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t19_l60
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v18_l56)))


(def v20_l62 (kind/doc #'sk/lay))


(def
 v22_l66
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l68
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v22_l66)))


(def
 v25_l72
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t26_l78
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v25_l72)))


(def v28_l82 (kind/doc #'sk/point))


(def v30_l86 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t31_l88
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v30_l86)))


(def
 v33_l92
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color :group})]))


(deftest
 t34_l94
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v33_l92)))


(def
 v36_l98
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :color "#E74C3C"})]))


(deftest
 t37_l100
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v36_l98)))


(def
 v39_l104
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :size :petal_length,
     :color :species})]))


(deftest
 t40_l107
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v39_l104)))


(def
 v42_l111
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :size 6})]))


(deftest
 t43_l113
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v42_l111)))


(def
 v45_l117
 (sk/plot [(sk/point {:data tiny, :x :x, :y :y, :alpha 0.3})]))


(deftest
 t46_l119
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v45_l117)))


(def
 v48_l123
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha 0.5,
     :size 5})]))


(deftest
 t49_l126
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v48_l123)))


(def v50_l128 (kind/doc #'sk/line))


(def
 v52_l132
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__281093#] (Math/sin (* p1__281093# 0.3)))
     (range 30))})))


(def v53_l135 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t54_l137
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v53_l135)))


(def
 v56_l141
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__281094#] (Math/sin (* p1__281094# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__281095#] (Math/cos (* p1__281095# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v57_l146
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t58_l148
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v57_l146)))


(def v60_l152 (sk/plot [(sk/line {:data wave, :x :x, :y :y, :size 4})]))


(deftest
 t61_l154
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v60_l152)))


(def v62_l156 (kind/doc #'sk/histogram))


(def
 v64_l160
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t65_l162
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v64_l160)))


(def
 v67_l166
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t68_l168
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v67_l166)))


(def v69_l170 (kind/doc #'sk/bar))


(def v71_l174 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t72_l176
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v71_l174)))


(def
 v74_l180
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t75_l182
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v74_l180)))


(def
 v77_l186
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t78_l188
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v77_l186)))


(def v79_l190 (kind/doc #'sk/stacked-bar))


(def
 v81_l194
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v82_l197
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t83_l199
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v82_l197)))


(def v84_l201 (kind/doc #'sk/value-bar))


(def
 v86_l205
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t87_l207
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v86_l205)))


(def v88_l209 (kind/doc #'sk/lm))


(def
 v90_l213
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t91_l218
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v90_l213)))


(def
 v93_l222
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t94_l228
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v93_l222)))


(def v96_l232 (kind/doc #'sk/plot))


(def v98_l236 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t99_l238
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v98_l236)))


(def
 v101_l242
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t102_l249
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v101_l242)))


(def
 v104_l253
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t105_l256
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v104_l253)))


(def v106_l258 (kind/doc #'sk/sketch))


(def
 v108_l263
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v109_l265
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t110_l267
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v109_l265)))


(def
 v112_l272
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t113_l278
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v112_l272)))


(def v115_l286 (kind/doc #'sk/coord))


(def
 v117_l290
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t118_l296
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v117_l290)))


(def v119_l298 (kind/doc #'sk/scale))


(def
 v121_l302
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t122_l308
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v121_l302)))


(def
 v124_l312
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t125_l318
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v124_l312)))


(def
 v127_l322
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t128_l328
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v127_l322)))


(def v130_l332 (kind/doc #'sk/cross))


(def v132_l336 (sk/cross [:a :b] [1 2 3]))


(deftest
 t133_l338
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v132_l336)))


(def
 v135_l342
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t136_l348
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v135_l342)))
