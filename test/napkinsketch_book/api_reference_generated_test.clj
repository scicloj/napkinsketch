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


(def
 v51_l130
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :color :species,
     :alpha :petal_length})]))


(deftest
 t52_l133
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v51_l130)))


(def v53_l135 (kind/doc #'sk/line))


(def
 v55_l139
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__72898#] (Math/sin (* p1__72898# 0.3)))
     (range 30))})))


(def v56_l142 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t57_l144
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v56_l142)))


(def
 v59_l148
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__72899#] (Math/sin (* p1__72899# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__72900#] (Math/cos (* p1__72900# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v60_l153
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t61_l155
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v60_l153)))


(def v63_l159 (sk/plot [(sk/line {:data wave, :x :x, :y :y, :size 4})]))


(deftest
 t64_l161
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v63_l159)))


(def v65_l163 (kind/doc #'sk/histogram))


(def
 v67_l167
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t68_l169
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v67_l167)))


(def
 v70_l173
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t71_l175
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v70_l173)))


(def v72_l177 (kind/doc #'sk/bar))


(def v74_l181 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t75_l183
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v74_l181)))


(def
 v77_l187
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t78_l189
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v77_l187)))


(def
 v80_l193
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t81_l195
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v80_l193)))


(def v82_l197 (kind/doc #'sk/stacked-bar))


(def
 v84_l201
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v85_l204
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t86_l206
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v85_l204)))


(def v87_l208 (kind/doc #'sk/value-bar))


(def
 v89_l212
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t90_l214
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v89_l212)))


(def v91_l216 (kind/doc #'sk/lm))


(def
 v93_l220
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t94_l225
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v93_l220)))


(def
 v96_l229
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t97_l235
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v96_l229)))


(def v99_l239 (kind/doc #'sk/plot))


(def v101_l243 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t102_l245
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v101_l243)))


(def
 v104_l249
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t105_l256
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v104_l249)))


(def
 v107_l260
 (sk/plot
  [(sk/point
    {:data iris,
     :x :sepal_length,
     :y :sepal_width,
     :alpha 0.5,
     :size 4})]))


(deftest
 t108_l263
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v107_l260)))


(def v109_l265 (kind/doc #'sk/sketch))


(def
 v111_l270
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v112_l272
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t113_l274
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v112_l272)))


(def
 v115_l279
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t116_l285
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v115_l279)))


(def v118_l293 (kind/doc #'sk/coord))


(def
 v120_l297
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t121_l303
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v120_l297)))


(def v122_l305 (kind/doc #'sk/scale))


(def
 v124_l309
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t125_l315
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v124_l309)))


(def
 v127_l319
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t128_l325
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v127_l319)))


(def
 v130_l329
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t131_l335
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v130_l329)))


(def v133_l339 (kind/doc #'sk/cross))


(def v135_l343 (sk/cross [:a :b] [1 2 3]))


(deftest
 t136_l345
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v135_l343)))


(def
 v138_l349
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t139_l355
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v138_l349)))
