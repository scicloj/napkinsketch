(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (->
  (rdatasets/datasets-iris)
  (sk/lay-boxplot :species :sepal-length)
  (sk/lay-point {:jitter true, :alpha 0.3})))


(deftest
 t4_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v3_l28)))


(def
 v6_l41
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram :sepal-length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t7_l45
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l41)))


(def
 v9_l53
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:alpha 0.6})
  sk/lay-lm))


(deftest
 t10_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l53)))


(def
 v12_l66
 (->
  (rdatasets/datasets-iris)
  (sk/lay-violin :species :petal-width {:alpha 0.3})
  (sk/lay-point {:jitter true, :alpha 0.4})))


(deftest
 t13_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v12_l66)))


(def
 v15_l79
 (def
  ts-dates
  (take 52 (jt/iterate jt/plus (jt/local-date 2020 1 6) (jt/weeks 1)))))


(def
 v16_l81
 (def
  ts-ds
  {:date ts-dates,
   :value
   (map
    (fn*
     [p1__79384#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__79384#) 0.12)))))
    (range 52))}))


(def
 v17_l85
 (->
  ts-ds
  (sk/lay-area :date :value {:alpha 0.2})
  sk/lay-line
  (sk/lay-point {:alpha 0.5})))


(deftest
 t18_l90
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v17_l85)))


(def
 v20_l99
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t21_l103
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v20_l99)))


(def
 v23_l111
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.5, :x-max 6.5, :alpha 0.3})))


(deftest
 t24_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v23_l111)))


(def
 v26_l125
 (->
  (rdatasets/datasets-iris)
  (sk/lay-ridgeline :species :sepal-length {:color :species})))


(deftest
 t27_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v26_l125)))


(def
 v29_l136
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t30_l139
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v29_l136)))


(def
 v32_l149
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm {:color nil})))


(deftest
 t33_l153
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v32_l149)))


(def
 v35_l162
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v36_l168
 (->
  experiment
  (sk/lay-point :condition :mean {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t37_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v36_l168)))


(def
 v39_l180
 (->
  experiment
  (sk/lay-lollipop :condition :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v39_l180)))


(def
 v42_l192
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :species :sepal-length {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t43_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v42_l192)))


(def
 v45_l204
 (->
  (rdatasets/reshape2-tips)
  (sk/view :total-bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t46_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v45_l204)))


(def
 v48_l224
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:alpha 0.5})
  (sk/lay-lm {:se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t49_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v48_l224)))


(def
 v51_l239
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t52_l243
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l239)))


(def
 v53_l245
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-stacked-bar :day {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t54_l249
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v53_l245)))


(def
 v56_l256
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v57_l260
 (->
  daily-temps
  (sk/lay-step :day :temp {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t58_l265
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v57_l260)))


(def
 v60_l274
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t61_l278
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v60_l274)))


(def
 v63_l286
 (def
  top5
  (->
   (rdatasets/datasets-iris)
   (tc/order-by :sepal-length :desc)
   (tc/head 5))))


(def
 v64_l288
 (->
  top5
  (sk/lay-point :sepal-length :sepal-width {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t65_l292
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__79385#] (= "virginica" p1__79385#))
       (:texts s)))))
   v64_l288)))


(def
 v67_l300
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t68_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v67_l300)))


(def
 v70_l316
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/coord :fixed)
  (sk/options {:title "Fixed Aspect Ratio"})))


(deftest
 t71_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v70_l316)))


(def
 v73_l332
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__79386#] (Math/sin (/ p1__79386# 3.0))) (range 20)),
   :change (map (fn* [p1__79387#] (- p1__79387# 10)) (range 20))}
  (sk/lay-point :x :y {:color :change})
  (sk/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t74_l340
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v73_l332)))


(def
 v76_l348
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-loess {:se true})
  (sk/options {:title "LOESS with 95% CI"})))


(deftest
 t77_l354
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v76_l348)))


(def
 v79_l363
 (def
  iris-sepal
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Sepal", :width 300, :height 250}))))


(def
 v80_l368
 (def
  iris-petal
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :petal-length :petal-width {:color :species})
   (sk/options {:title "Petal", :width 300, :height 250}))))


(def
 v81_l373
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t82_l376
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v81_l373)))


(def
 v84_l383
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v85_l388
 (->
  top-cities
  (sk/lay-point :area :population)
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/options {:title "Population vs Area"})))


(deftest
 t86_l393
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v85_l388)))


(def
 v88_l401
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (map
    (fn*
     [p1__79388#]
     (+ (* 3 p1__79388#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/lay-point :x :y)
   sk/lay-lm
   (sk/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t89_l412
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v88_l401)))


(def
 v91_l423
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (sk/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t92_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v91_l423)))


(def
 v94_l433
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/view :bill-length-mm :bill-depth-mm {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t95_l439
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v94_l433)))


(def
 v97_l446
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (sk/lay-lm {:color nil})
  (sk/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t98_l451
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v97_l446)))


(def
 v100_l457
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:color :species})
  (sk/options {:title "Species by Island"})))


(deftest
 t101_l461
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v100_l457)))


(def
 v103_l467
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/view :flipper-length-mm :body-mass-g {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t104_l473
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v103_l467)))


(def
 v106_l479
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-histogram :body-mass-g {:color :species})
  (sk/options {:title "Body Mass Distribution"})))


(deftest
 t107_l483
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v106_l479)))


(def
 v109_l491
 (->
  (rdatasets/reshape2-tips)
  (sk/view :total-bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t110_l498
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v109_l491)))


(def
 v112_l504
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :time})
  (sk/options {:title "Visits by Day and Meal Time"})))


(deftest
 t113_l508
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v112_l504)))


(def
 v115_l514
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-stacked-bar :day {:color :time})
  (sk/options {:title "Visits by Day (Stacked)"})))


(deftest
 t116_l518
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l514)))


(def
 v118_l524
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/coord :flip)
  (sk/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t119_l529
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l524)))


(def
 v121_l537
 (->
  (rdatasets/ggplot2-mpg)
  (sk/view :displ :hwy {:color :class})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Displacement vs Highway MPG by Class"})))


(deftest
 t122_l543
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 234 (:points s)) (pos? (:lines s)))))
   v121_l537)))


(def
 v124_l549
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-point :displ :cty {:color :drv})
  (sk/options {:title "Engine Displacement vs City Fuel Efficiency"})))


(deftest
 t125_l553
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 234 (:points s)))))
   v124_l549)))


(def
 v127_l559
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-bar :drv)
  (sk/options {:title "Cars by Drive Type"})))


(deftest
 t128_l563
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v127_l559)))


(def
 v130_l583
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (sk/lay-point :carat :price {:color :cut})
  (sk/options {:title "Diamonds (500 rows, SVG)"})))


(deftest
 t131_l588
 (is ((fn [v] (= 500 (:points (sk/svg-summary v)))) v130_l583)))


(def
 v133_l595
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:color :cut, :alpha 0.3})
  (sk/options
   {:title "Diamonds (53,940 rows, BufferedImage)", :format :bufimg})))


(deftest t134_l600 (is ((fn [v] (some? v)) v133_l595)))
