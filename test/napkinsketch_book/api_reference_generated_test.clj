(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]}))


(def
 v4_l29
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def
 v5_l32
 (def
  measurements
  {:treatment ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def v7_l39 (kind/doc #'sk/xkcd7-view))


(def
 v9_l43
 (-> data/iris (sk/xkcd7-lay-point :sepal_length :sepal_width)))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v9_l43)))


(def v12_l52 (-> data/iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t13_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v12_l52)))


(def
 v15_l61
 (->
  data/iris
  (sk/xkcd7-view
   [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/xkcd7-lay-point {:color :species})))


(deftest
 t16_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v15_l61)))


(def
 v18_l72
 (->
  data/iris
  (sk/xkcd7-view {:x :sepal_length, :y :sepal_width})
  sk/xkcd7-lay-point))


(deftest
 t19_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v18_l72)))


(def v20_l80 (kind/doc #'sk/xkcd7-annotate))


(def
 v22_l84
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t23_l89
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l84)))


(def v25_l95 (kind/doc #'sk/xkcd7-lay-point))


(def
 v26_l97
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t27_l100
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v26_l97)))


(def v28_l103 (kind/doc #'sk/xkcd7-lay-line))


(def
 v29_l105
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__69080#] (Math/sin (* p1__69080# 0.3))) (range 30))}))


(def v30_l108 (-> wave (sk/xkcd7-lay-line :x :y)))


(deftest
 t31_l111
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v30_l108)))


(def v32_l114 (kind/doc #'sk/xkcd7-lay-histogram))


(def v33_l116 (-> data/iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t34_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l116)))


(def v35_l122 (kind/doc #'sk/xkcd7-lay-bar))


(def v36_l124 (-> data/iris (sk/xkcd7-lay-bar :species)))


(deftest
 t37_l127
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v36_l124)))


(def v38_l130 (kind/doc #'sk/xkcd7-lay-stacked-bar))


(def
 v39_l132
 (->
  data/penguins
  (sk/xkcd7-lay-stacked-bar :island {:color :species})))


(deftest
 t40_l135
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v39_l132)))


(def v41_l138 (kind/doc #'sk/xkcd7-lay-stacked-bar-fill))


(def
 v42_l140
 (->
  data/penguins
  (sk/xkcd7-lay-stacked-bar-fill :island {:color :species})))


(deftest
 t43_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l140)))


(def v44_l146 (kind/doc #'sk/xkcd7-lay-value-bar))


(def v45_l148 (-> sales (sk/xkcd7-lay-value-bar :product :revenue)))


(deftest
 t46_l151
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v45_l148)))


(def v47_l154 (kind/doc #'sk/xkcd7-lay-lm))


(def
 v48_l156
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  sk/xkcd7-lay-lm))


(deftest
 t49_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l156)))


(def v50_l164 (kind/doc #'sk/xkcd7-lay-loess))


(def
 v51_l166
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (map
     (fn*
      [p1__69081#]
      (+
       (Math/sin (* p1__69081# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def
 v52_l171
 (-> noisy-wave (sk/xkcd7-lay-point :x :y) sk/xkcd7-lay-loess))


(deftest
 t53_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v52_l171)))


(def v54_l179 (kind/doc #'sk/xkcd7-lay-density))


(def v55_l181 (-> data/iris (sk/xkcd7-lay-density :sepal_length)))


(deftest
 t56_l184
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v55_l181)))


(def v57_l187 (kind/doc #'sk/xkcd7-lay-area))


(def v58_l189 (-> wave (sk/xkcd7-lay-area :x :y)))


(deftest
 t59_l192
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l189)))


(def v60_l195 (kind/doc #'sk/xkcd7-lay-stacked-area))


(def
 v61_l197
 (->
  {:x (concat (range 10) (range 10) (range 10)),
   :y
   (concat
    [1 2 3 4 5 4 3 2 1 0]
    [2 2 2 3 3 3 2 2 2 2]
    [1 1 1 1 2 2 2 1 1 1]),
   :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
  (sk/xkcd7-lay-stacked-area :x :y {:color :group})))


(deftest
 t62_l204
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v61_l197)))


(def v63_l207 (kind/doc #'sk/xkcd7-lay-text))


(def
 v64_l209
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/xkcd7-lay-text :x :y {:text :name})))


(deftest
 t65_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v64_l209)))


(def v66_l215 (kind/doc #'sk/xkcd7-lay-label))


(def
 v67_l217
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/xkcd7-lay-point :x :y {:size 5})
  (sk/xkcd7-lay-label {:text :name})))


(deftest
 t68_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v67_l217)))


(def v69_l224 (kind/doc #'sk/xkcd7-lay-boxplot))


(def
 v70_l226
 (-> data/iris (sk/xkcd7-lay-boxplot :species :sepal_width)))


(deftest
 t71_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v70_l226)))


(def v72_l233 (kind/doc #'sk/xkcd7-lay-violin))


(def v73_l235 (-> data/tips (sk/xkcd7-lay-violin :day :total_bill)))


(deftest
 t74_l238
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v73_l235)))


(def v75_l241 (kind/doc #'sk/xkcd7-lay-errorbar))


(def
 v76_l243
 (->
  measurements
  (sk/xkcd7-lay-point :treatment :mean)
  (sk/xkcd7-lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t77_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v76_l243)))


(def v78_l251 (kind/doc #'sk/xkcd7-lay-lollipop))


(def v79_l253 (-> sales (sk/xkcd7-lay-lollipop :product :revenue)))


(deftest
 t80_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v79_l253)))


(def v81_l260 (kind/doc #'sk/xkcd7-lay-tile))


(def
 v82_l262
 (-> data/iris (sk/xkcd7-lay-tile :sepal_length :sepal_width)))


(deftest
 t83_l265
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v82_l262)))


(def v84_l268 (kind/doc #'sk/xkcd7-lay-density2d))


(def
 v85_l270
 (-> data/iris (sk/xkcd7-lay-density2d :sepal_length :sepal_width)))


(deftest
 t86_l273
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v85_l270)))


(def v87_l276 (kind/doc #'sk/xkcd7-lay-contour))


(def
 v88_l278
 (-> data/iris (sk/xkcd7-lay-contour :sepal_length :sepal_width)))


(deftest
 t89_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v88_l278)))


(def v90_l284 (kind/doc #'sk/xkcd7-lay-ridgeline))


(def
 v91_l286
 (-> data/iris (sk/xkcd7-lay-ridgeline :species :sepal_length)))


(deftest
 t92_l289
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v91_l286)))


(def v93_l292 (kind/doc #'sk/xkcd7-lay-rug))


(def
 v94_l294
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-lay-rug {:side :both})))


(deftest
 t95_l298
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v94_l294)))


(def v96_l301 (kind/doc #'sk/xkcd7-lay-step))


(def v97_l303 (-> tiny (sk/xkcd7-lay-step :x :y) sk/xkcd7-lay-point))


(deftest
 t98_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v97_l303)))


(def v99_l311 (kind/doc #'sk/xkcd7-lay-summary))


(def
 v100_l313
 (-> data/iris (sk/xkcd7-lay-summary :species :sepal_length)))


(deftest
 t101_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v100_l313)))


(def v103_l322 (kind/doc #'sk/xkcd7-plot))


(def v105_l327 (-> tiny (sk/xkcd7-lay-point :x :y)))


(deftest
 t106_l330
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v105_l327)))


(def v107_l333 (kind/doc #'sk/xkcd7-options))


(def
 v109_l337
 (->
  tiny
  (sk/xkcd7-lay-point :x :y)
  (sk/xkcd7-options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t110_l341
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v109_l337)))


(def v111_l345 (kind/doc #'sk/sketch?))


(def v113_l349 (sk/sketch? (sk/xkcd7-lay-point tiny :x :y)))


(deftest t114_l351 (is (true? v113_l349)))


(def v115_l353 (kind/doc #'sk/xkcd7-plan))


(def
 v117_l357
 (count
  (:entries (-> tiny (sk/xkcd7-lay-point :x :y) (sk/xkcd7-lay-lm)))))


(deftest t118_l359 (is ((fn [v] (= 2 v)) v117_l357)))


(def v119_l361 (kind/doc #'sk/xkcd7-plan))


(def
 v121_l365
 (def plan1 (-> tiny (sk/xkcd7-lay-point :x :y) sk/xkcd7-plan)))


(def v122_l369 plan1)


(deftest
 t123_l371
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v122_l369)))


(def v125_l376 (kind/doc #'sk/views->plan))


(def
 v126_l378
 (def plan2 (-> tiny (sk/xkcd7-lay-point :x :y) sk/views->plan)))
