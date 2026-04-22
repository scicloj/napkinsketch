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
     [p1__123244#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__123244#) 0.12)))))
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
 v35_l163
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:alpha 0.3})
  (sk/lay-point
   {:data {:sepal-length [5.0 6.5], :sepal-width [3.5 3.0]},
    :x :sepal-length,
    :y :sepal-width,
    :color "red",
    :size 6})))


(deftest
 t36_l170
 (is ((fn [v] (= 152 (:points (sk/svg-summary v)))) v35_l163)))


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
  (sk/lay-point :condition :mean {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l187
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l183)))


(def
 v42_l195
 (->
  experiment
  (sk/lay-lollipop :condition :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t43_l199
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l195)))


(def
 v45_l207
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :species :sepal-length {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t46_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l207)))


(def
 v48_l219
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
 t49_l227
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v48_l219)))


(def
 v51_l239
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:alpha 0.5})
  (sk/lay-lm {:se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t52_l245
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v51_l239)))


(def
 v54_l254
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t55_l258
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l254)))


(def
 v56_l260
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-stacked-bar :day {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t57_l264
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v56_l260)))


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
  (sk/lay-step :day :temp {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t61_l280
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v60_l275)))


(def
 v63_l289
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t64_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v63_l289)))


(def
 v66_l301
 (def
  top5
  (->
   (rdatasets/datasets-iris)
   (tc/order-by :sepal-length :desc)
   (tc/head 5))))


(def
 v67_l303
 (->
  top5
  (sk/lay-point :sepal-length :sepal-width {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t68_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__123245#] (= "virginica" p1__123245#))
       (:texts s)))))
   v67_l303)))


(def
 v70_l315
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t71_l322
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v70_l315)))


(def
 v73_l331
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/coord :fixed)
  (sk/options {:title "Fixed Aspect Ratio"})))


(deftest
 t74_l338
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v73_l331)))


(def
 v76_l347
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__123246#] (Math/sin (/ p1__123246# 3.0))) (range 20)),
   :change (map (fn* [p1__123247#] (- p1__123247# 10)) (range 20))}
  (sk/lay-point :x :y {:color :change})
  (sk/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t77_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v76_l347)))


(def
 v79_l363
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-loess {:se true})
  (sk/options {:title "LOESS with 95% CI"})))


(deftest
 t80_l369
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v79_l363)))


(def
 v82_l378
 (def
  iris-sepal
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Sepal", :width 300, :height 250}))))


(def
 v83_l383
 (def
  iris-petal
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :petal-length :petal-width {:color :species})
   (sk/options {:title "Petal", :width 300, :height 250}))))


(def
 v84_l388
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t85_l391
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v84_l388)))


(def
 v87_l398
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v88_l403
 (->
  top-cities
  (sk/lay-point :area :population)
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/options {:title "Population vs Area"})))


(deftest
 t89_l408
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v88_l403)))


(def
 v91_l416
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (map
    (fn*
     [p1__123248#]
     (+ (* 3 p1__123248#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/lay-point :x :y)
   sk/lay-lm
   (sk/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t92_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v91_l416)))


(def
 v94_l438
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (sk/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l442
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l438)))


(def
 v97_l448
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/view :bill-length-mm :bill-depth-mm {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l454
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l448)))


(def
 v100_l461
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
  (sk/lay-lm {:color nil})
  (sk/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l466
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l461)))


(def
 v103_l472
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:color :species})
  (sk/options {:title "Species by Island"})))


(deftest
 t104_l476
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l472)))


(def
 v106_l482
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/view :flipper-length-mm :body-mass-g {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l488
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l482)))


(def
 v109_l494
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-histogram :body-mass-g {:color :species})
  (sk/options {:title "Body Mass Distribution"})))


(deftest
 t110_l498
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l494)))


(def
 v112_l506
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
 t113_l513
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l506)))


(def
 v115_l519
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :time})
  (sk/options {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l523
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l519)))


(def
 v118_l529
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-stacked-bar :day {:color :time})
  (sk/options {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l533
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l529)))


(def
 v121_l539
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-bar :day {:color :sex})
  (sk/coord :flip)
  (sk/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l544
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l539)))


(def
 v124_l552
 (->
  (rdatasets/ggplot2-mpg)
  (sk/view :displ :hwy {:color :class})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Displacement vs Highway MPG by Class"})))


(deftest
 t125_l558
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 234 (:points s)) (pos? (:lines s)))))
   v124_l552)))


(def
 v127_l564
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-point :displ :cty {:color :drv})
  (sk/options {:title "Engine Displacement vs City Fuel Efficiency"})))


(deftest
 t128_l568
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 234 (:points s)))))
   v127_l564)))


(def
 v130_l574
 (->
  (rdatasets/ggplot2-mpg)
  (sk/lay-bar :drv)
  (sk/options {:title "Cars by Drive Type"})))


(deftest
 t131_l578
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v130_l574)))


(def
 v133_l598
 (->
  (rdatasets/ggplot2-diamonds)
  (tc/head 500)
  (sk/lay-point :carat :price {:color :cut})
  (sk/options {:title "Diamonds (500 rows, SVG)"})))


(deftest
 t134_l603
 (is ((fn [v] (= 500 (:points (sk/svg-summary v)))) v133_l598)))


(def
 v136_l610
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:color :cut, :alpha 0.3})
  (sk/options
   {:title "Diamonds (53,940 rows, BufferedImage)", :format :bufimg})))


(deftest t137_l615 (is ((fn [v] (some? v)) v136_l610)))
