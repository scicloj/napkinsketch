(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v2_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l20
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l30
 (->
  iris
  (sk/view [[:species :sepal_length]])
  sk/lay-boxplot
  (sk/lay-point {:jitter true, :alpha 0.3})
  sk/plot))


(deftest
 t6_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v5_l30)))


(def
 v8_l45
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay-histogram {:normalize :density, :alpha 0.5})
  sk/lay-density
  sk/plot))


(deftest
 t9_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v8_l45)))


(def
 v11_l59
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species, :alpha 0.6})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t12_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v11_l59)))


(def
 v14_l73
 (->
  iris
  (sk/view [[:species :petal_width]])
  (sk/lay-violin {:alpha 0.3})
  (sk/lay-point {:jitter true, :alpha 0.4})
  sk/plot))


(deftest
 t15_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v14_l73)))


(def
 v17_l88
 (def
  ts-dates
  (mapv
   (fn*
    [p1__75016#]
    (java.time.LocalDate/ofEpochDay (+ 18262 (* (long p1__75016#) 7))))
   (range 52))))


(def
 v18_l90
 (def
  ts-ds
  {:date ts-dates,
   :value
   (mapv
    (fn*
     [p1__75017#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__75017#) 0.12)))))
    (range 52))}))


(def
 v19_l94
 (->
  ts-ds
  (sk/view [[:date :value]])
  (sk/lay-area {:alpha 0.2})
  sk/lay-line
  (sk/lay-point {:alpha 0.5})
  sk/plot))


(deftest
 t20_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v19_l94)))


(def
 v22_l110
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/facet :species)
  sk/plot))


(deftest
 t23_l116
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v22_l110)))


(def
 v25_l124
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))
  sk/plot))


(deftest
 t26_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v25_l124)))


(def
 v28_l139
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay-ridgeline {:color :species})
  sk/plot))


(deftest
 t29_l144
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v28_l139)))


(def
 v31_l152
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v32_l155
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})
  sk/plot))


(deftest
 t33_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v32_l155)))


(def
 v35_l170
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/lay-lm
  sk/plot))


(deftest
 t36_l176
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v35_l170)))


(def
 v38_l185
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v39_l191
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay-point {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})
  sk/plot))


(deftest
 t40_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l191)))


(def
 v42_l205
 (->
  experiment
  (sk/view [[:condition :mean]])
  sk/lay-lollipop
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})
  sk/plot))


(deftest
 t43_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l205)))


(def
 v45_l219
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay-point {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})
  sk/plot))


(deftest
 t46_l225
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l219)))


(def
 v48_l233
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})
  (sk/plot
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t49_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v48_l233)))


(def
 v51_l253
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species, :alpha 0.5})
  (sk/lay-lm {:color :species, :se true})
  (sk/plot {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t52_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v51_l253)))


(def
 v54_l268
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :sex})
  (sk/plot {:title "Dodged Bars (default)"})))


(deftest
 t55_l273
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l268)))


(def
 v56_l275
 (->
  tips
  (sk/view :day)
  (sk/lay-stacked-bar {:color :sex})
  (sk/plot {:title "Stacked Bars"})))


(deftest
 t57_l280
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v56_l275)))


(def
 v59_l287
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v60_l291
 (->
  daily-temps
  (sk/view :day :temp)
  (sk/lay-step {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/plot {:title "Daily Temperature (Step)"})))


(deftest
 t61_l297
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v60_l291)))


(def
 v63_l306
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})
  sk/plot))


(deftest
 t64_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v63_l306)))


(def
 v66_l320
 (def top5 (-> iris (tc/order-by :sepal_length :desc) (tc/head 5))))


(def
 v67_l322
 (->
  top5
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})
  sk/plot))


(deftest
 t68_l328
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__75018#] (= "virginica" p1__75018#))
       (:texts s)))))
   v67_l322)))


(def
 v70_l336
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/plot
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t71_l344
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v70_l336)))


(def
 v73_l353
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/coord :fixed)
  (sk/plot {:title "Fixed Aspect Ratio"})))


(deftest
 t74_l360
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v73_l353)))


(def
 v76_l369
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__75019#] (Math/sin (/ p1__75019# 3.0))) (range 20)),
   :change (map (fn* [p1__75020#] (- p1__75020# 10)) (range 20))}
  (sk/view :x :y)
  (sk/lay-point {:color :change})
  (sk/plot
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t77_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v76_l369)))


(def
 v79_l386
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-loess {:se true, :color :species})
  (sk/plot {:title "LOESS with 95% CI"})))


(deftest
 t80_l392
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v79_l386)))


(def
 v82_l401
 (def
  iris-sepal
  (->
   iris
   (sk/view :sepal_length :sepal_width)
   (sk/lay-point {:color :species})
   (sk/plot {:title "Sepal", :width 300, :height 250}))))


(def
 v83_l407
 (def
  iris-petal
  (->
   iris
   (sk/view :petal_length :petal_width)
   (sk/lay-point {:color :species})
   (sk/plot {:title "Petal", :width 300, :height 250}))))


(def
 v84_l413
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t85_l416
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v84_l413)))


(def
 v87_l423
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v88_l428
 (->
  top-cities
  (sk/view :area :population)
  sk/lay-point
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/plot {:title "Population vs Area"})))


(deftest
 t89_l434
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v88_l428)))


(def
 v91_l442
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (mapv
    (fn*
     [p1__75021#]
     (+ (* 3 p1__75021#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/view [[:x :y]])
   sk/lay-point
   sk/lay-lm
   (sk/plot {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t92_l454
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v91_l442)))


(def
 v94_l465
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  (sk/plot {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l470
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l465)))


(def
 v97_l476
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/plot {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l482
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l476)))


(def
 v100_l488
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  sk/lay-lm
  (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l494
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l488)))


(def
 v103_l500
 (->
  penguins
  (sk/view :island)
  (sk/lay-bar {:color :species})
  (sk/plot {:title "Species by Island"})))


(deftest
 t104_l505
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l500)))


(def
 v106_l511
 (->
  penguins
  (sk/view [[:flipper_length_mm :body_mass_g]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/plot {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l517
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l511)))


(def
 v109_l523
 (->
  penguins
  (sk/view :body_mass_g)
  (sk/lay-histogram {:color :species})
  (sk/plot {:title "Body Mass Distribution"})))


(deftest
 t110_l528
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l523)))


(def
 v112_l536
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})
  (sk/plot
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t113_l543
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l536)))


(def
 v115_l549
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :time})
  (sk/plot {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l554
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l549)))


(def
 v118_l560
 (->
  tips
  (sk/view :day)
  (sk/lay-stacked-bar {:color :time})
  (sk/plot {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l565
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l560)))


(def
 v121_l571
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :sex})
  (sk/coord :flip)
  (sk/plot {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l577
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l571)))


(def
 v124_l583
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v126_l588
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay-point {:color :origin})
  (sk/lay-lm {:color :origin})
  (sk/plot {:title "Horsepower vs MPG by Origin"})))


(deftest
 t127_l594
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v126_l588)))


(def
 v129_l600
 (->
  mpg
  (sk/view [[:displacement :mpg]])
  (sk/lay-point {:color :origin})
  (sk/plot {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t130_l605
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v129_l600)))


(def
 v132_l611
 (->
  mpg
  (sk/view :origin)
  sk/lay-bar
  (sk/plot {:title "Cars by Origin"})))


(deftest
 t133_l616
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v132_l611)))
