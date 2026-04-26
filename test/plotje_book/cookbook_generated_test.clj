(ns
 plotje-book.cookbook-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (->
  (rdatasets/datasets-iris)
  (pj/lay-boxplot :species :sepal-length)
  (pj/lay-point {:jitter true, :alpha 0.3})))


(deftest
 t4_l32
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v3_l28)))


(def
 v6_l41
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram :sepal-length {:normalize :density, :alpha 0.5})
  pj/lay-density))


(deftest
 t7_l45
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l41)))


(def
 v9_l53
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:alpha 0.6})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t10_l58
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l53)))


(def
 v12_l66
 (->
  (rdatasets/datasets-iris)
  (pj/lay-violin :species :petal-width {:alpha 0.3})
  (pj/lay-point {:jitter true, :alpha 0.4})))


(deftest
 t13_l70
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
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
     [p1__80670#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__80670#) 0.12)))))
    (range 52))}))


(def
 v17_l85
 (->
  ts-ds
  (pj/lay-area :date :value {:alpha 0.2})
  pj/lay-line
  (pj/lay-point {:alpha 0.5})))


(deftest
 t18_l90
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v17_l85)))


(def
 v20_l99
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t21_l103
 (is ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:panels s)))) v20_l99)))


(def
 v23_l111
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5, :alpha 0.3})))


(deftest
 t24_l116
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v23_l111)))


(def
 v26_l125
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length {:color :species})))


(deftest
 t27_l128
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v26_l125)))


(def
 v29_l136
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:position :fill, :color :species})))


(deftest
 t30_l139
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v29_l136)))


(def
 v32_l149
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-smooth {:stat :linear-model, :color nil})))


(deftest
 t33_l153
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v32_l149)))


(def
 v35_l163
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:alpha 0.3})
  (pj/lay-point
   {:data {:sepal-length [5.0 6.5], :sepal-width [3.5 3.0]},
    :x :sepal-length,
    :y :sepal-width,
    :color "red",
    :size 6})))


(deftest
 t36_l170
 (is ((fn [v] (= 152 (:points (pj/svg-summary v)))) v35_l163)))


(def
 v38_l177
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v39_l183
 (->
  experiment
  (pj/lay-point :condition :mean {:size 5})
  (pj/lay-errorbar {:y-min :ci_lo, :y-max :ci_hi})))


(deftest
 t40_l187
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l183)))


(def
 v42_l195
 (->
  experiment
  (pj/lay-lollipop :condition :mean)
  (pj/lay-errorbar {:y-min :ci_lo, :y-max :ci_hi})))


(deftest
 t43_l199
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l195)))


(def
 v45_l207
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :species :sepal-length {:alpha 0.3, :jitter 5})
  (pj/lay-summary {:color :species})))


(deftest
 t46_l211
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l207)))


(def
 v48_l219
 (->
  (rdatasets/reshape2-tips)
  (pj/pose :total-bill :tip {:color :smoker})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t49_l227
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v48_l219)))


(def
 v51_l239
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:alpha 0.5})
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})
  (pj/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t52_l245
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v51_l239)))


(def
 v54_l254
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :sex})
  (pj/options {:title "Dodged Bars (default)"})))


(deftest
 t55_l258
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v54_l254)))


(def
 v56_l260
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:position :stack, :color :sex})
  (pj/options {:title "Stacked Bars"})))


(deftest
 t57_l264
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v56_l260)))


(def
 v59_l271
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v60_l275
 (->
  daily-temps
  (pj/lay-step :day :temp {:color "#2196F3"})
  (pj/lay-point {:color "#2196F3", :size 3})
  (pj/options {:title "Daily Temperature (Step)"})))


(deftest
 t61_l280
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (pos? (:lines s))
      (pos? (:points s))
      (contains? (:colors s) "rgb(33,150,243)")
      (contains? (:sizes s) 3.0))))
   v60_l275)))


(def
 v63_l291
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.4})
  (pj/lay-contour {:levels 5})))


(deftest
 t64_l295
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v63_l291)))


(def
 v66_l303
 (def
  top5
  (->
   (rdatasets/datasets-iris)
   (tc/order-by :sepal-length :desc)
   (tc/head 5))))


(def
 v67_l305
 (->
  top5
  (pj/lay-point :sepal-length :sepal-width {:size 5})
  (pj/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t68_l309
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__80671#] (= "virginica" p1__80671#))
       (:texts s)))))
   v67_l305)))


(def
 v70_l317
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t71_l324
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v70_l317)))


(def
 v73_l333
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/coord :fixed)
  (pj/options {:title "Fixed Aspect Ratio"})))


(deftest
 t74_l340
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v73_l333)))


(def
 v76_l349
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__80672#] (Math/sin (/ p1__80672# 3.0))) (range 20)),
   :change (map (fn* [p1__80673#] (- p1__80673# 10)) (range 20))}
  (pj/lay-point :x :y {:color :change})
  (pj/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t77_l357
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v76_l349)))


(def
 v79_l365
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:confidence-band true})
  (pj/options {:title "LOESS with 95% CI"})))


(deftest
 t80_l371
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v79_l365)))


(def
 v82_l380
 (def
  iris-sepal
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/options {:title "Sepal", :width 300, :height 250}))))


(def
 v83_l385
 (def
  iris-petal
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :petal-length :petal-width {:color :species})
   (pj/options {:title "Petal", :width 300, :height 250}))))


(def
 v84_l390
 (pj/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t85_l393
 (is
  ((fn [v] (and (pj/pose? v) (= "Iris Dashboard" (-> v :opts :title))))
   v84_l390)))


(def
 v87_l400
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v88_l405
 (->
  top-cities
  (pj/lay-point :area :population)
  (pj/lay-text {:text :city, :nudge-y 1.0})
  (pj/options {:title "Population vs Area"})))


(deftest
 t89_l410
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v88_l405)))


(def
 v91_l418
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (map
    (fn*
     [p1__80674#]
     (+ (* 3 p1__80674#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (pj/lay-point :x :y)
   (pj/lay-smooth {:stat :linear-model})
   (pj/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t92_l429
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v91_l418)))


(def
 v94_l440
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (pj/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l444
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l440)))


(def
 v97_l450
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/pose :bill-length-mm :bill-depth-mm {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l456
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l450)))


(def
 v100_l463
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (pj/lay-smooth {:stat :linear-model, :color nil})
  (pj/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l468
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l463)))


(def
 v103_l474
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :island {:color :species})
  (pj/options {:title "Species by Island"})))


(deftest
 t104_l478
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l474)))


(def
 v106_l484
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/pose :flipper-length-mm :body-mass-g {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l490
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l484)))


(def
 v109_l496
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-histogram :body-mass-g {:color :species})
  (pj/options {:title "Body Mass Distribution"})))


(deftest
 t110_l500
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l496)))


(def
 v112_l508
 (->
  (rdatasets/reshape2-tips)
  (pj/pose :total-bill :tip {:color :smoker})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t113_l515
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l508)))


(def
 v115_l521
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :time})
  (pj/options {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l525
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l521)))


(def
 v118_l531
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:position :stack, :color :time})
  (pj/options {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l535
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l531)))


(def
 v121_l541
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-bar :day {:color :sex})
  (pj/coord :flip)
  (pj/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l546
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l541)))


(def
 v124_l554
 (->
  (rdatasets/ggplot2-mpg)
  (pj/pose :displ :hwy {:color :class})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Displacement vs Highway MPG by Class"})))


(deftest
 t125_l560
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 234 (:points s)) (pos? (:lines s)))))
   v124_l554)))


(def
 v127_l566
 (->
  (rdatasets/ggplot2-mpg)
  (pj/lay-point :displ :cty {:color :drv})
  (pj/options {:title "Engine Displacement vs City Fuel Efficiency"})))


(deftest
 t128_l570
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 234 (:points s)))))
   v127_l566)))


(def
 v130_l576
 (->
  (rdatasets/ggplot2-mpg)
  (pj/lay-bar :drv)
  (pj/options {:title "Cars by Drive Type"})))


(deftest
 t131_l580
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v130_l576)))


(def
 v133_l600
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (pj/lay-point :carat :price {:color :cut})
  (pj/options {:title "Diamonds (500 rows, SVG)"})))


(deftest
 t134_l605
 (is ((fn [v] (= 500 (:points (pj/svg-summary v)))) v133_l600)))


(def
 v136_l612
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:color :cut, :alpha 0.3})
  (pj/options
   {:title "Diamonds (53,940 rows, BufferedImage)", :format :bufimg})))


(deftest t137_l617 (is ((fn [v] (some? v)) v136_l612)))
