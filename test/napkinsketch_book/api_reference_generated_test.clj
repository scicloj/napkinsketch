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


(def v41_l109 (kind/doc #'sk/line))


(def
 v43_l113
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__81093#] (Math/sin (* p1__81093# 0.3)))
     (range 30))})))


(def v44_l116 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t45_l118
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v44_l116)))


(def
 v47_l122
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__81094#] (Math/sin (* p1__81094# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__81095#] (Math/cos (* p1__81095# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v48_l127
 (sk/plot [(sk/line {:data waves, :x :x, :y :y, :color :fn})]))


(deftest
 t49_l129
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v48_l127)))


(def v50_l131 (kind/doc #'sk/histogram))


(def
 v52_l135
 (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot))


(deftest
 t53_l137
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v52_l135)))


(def
 v55_l141
 (->
  iris
  (sk/view :sepal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t56_l143
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v55_l141)))


(def v57_l145 (kind/doc #'sk/bar))


(def v59_l149 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t60_l151
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v59_l149)))


(def
 v62_l155
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t63_l157
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v62_l155)))


(def v64_l159 (kind/doc #'sk/stacked-bar))


(def
 v66_l163
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v67_l166
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar {:color :species}))
  sk/plot))


(deftest
 t68_l168
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v67_l166)))


(def v69_l170 (kind/doc #'sk/value-bar))


(def
 v71_l174
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t72_l176
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v71_l174)))


(def v73_l178 (kind/doc #'sk/lm))


(def
 v75_l182
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t76_l187
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v75_l182)))


(def
 v78_l191
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t79_l197
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v78_l191)))


(def v81_l201 (kind/doc #'sk/plot))


(def v83_l205 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t84_l207
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v83_l205)))


(def
 v86_l211
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Scatter",
   :x-label "Sepal Length (cm)",
   :y-label "Sepal Width (cm)",
   :width 800,
   :height 300}))


(deftest
 t87_l218
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v86_l211)))


(def
 v89_l222
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
  {:config {:point-radius 4, :point-opacity 0.5}}))


(deftest
 t90_l225
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v89_l222)))


(def v91_l227 (kind/doc #'sk/sketch))


(def
 v93_l232
 (def sk1 (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v94_l234
 (select-keys sk1 [:width :height :x-label :y-label :title]))


(deftest
 t95_l236
 (is ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v94_l234)))


(def
 v97_l241
 (let
  [panel (first (:panels sk1))]
  {:x-domain (:x-domain panel),
   :y-domain (:y-domain panel),
   :n-layers (count (:layers panel)),
   :mark (:mark (first (:layers panel)))}))


(deftest
 t98_l247
 (is
  ((fn [m] (and (= 1 (:n-layers m)) (= :point (:mark m)))) v97_l241)))


(def v100_l255 (kind/doc #'sk/coord))


(def
 v102_l259
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t103_l265
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v102_l259)))


(def v104_l267 (kind/doc #'sk/scale))


(def
 v106_l271
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t107_l277
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v106_l271)))


(def
 v109_l281
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:domain [3 9]})
  sk/plot))


(deftest
 t110_l287
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v109_l281)))


(def
 v112_l291
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  (sk/scale :x {:label "Length (cm)"})
  sk/plot))


(deftest
 t113_l297
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v112_l291)))


(def v115_l301 (kind/doc #'sk/cross))


(def v117_l305 (sk/cross [:a :b] [1 2 3]))


(deftest
 t118_l307
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v117_l305)))


(def
 v120_l311
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t121_l317
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v120_l311)))
