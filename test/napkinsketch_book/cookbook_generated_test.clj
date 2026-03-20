(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l18
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l28
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/boxplot) (sk/point {:jitter true, :alpha 0.3}))
  sk/plot))


(deftest
 t6_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v5_l28)))


(def
 v8_l43
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay (sk/histogram {:alpha 0.5}) (sk/density))
  sk/plot))


(deftest
 t9_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v8_l43)))


(def
 v11_l57
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :alpha 0.6})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t12_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v11_l57)))


(def
 v14_l71
 (->
  iris
  (sk/view [[:species :petal_width]])
  (sk/lay
   (sk/violin {:alpha 0.3})
   (sk/point {:jitter true, :alpha 0.4}))
  sk/plot))


(deftest
 t15_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v14_l71)))


(def
 v17_l86
 (def
  ts-dates
  (mapv
   (fn*
    [p1__119361#]
    (java.time.LocalDate/ofEpochDay
     (+ 18262 (* (long p1__119361#) 7))))
   (range 52))))


(def
 v18_l88
 (def
  ts-ds
  (tc/dataset
   {:date ts-dates,
    :value
    (mapv
     (fn*
      [p1__119362#]
      (+ 100.0 (* 30.0 (Math/sin (* (double p1__119362#) 0.12)))))
     (range 52))}
   {:key-fn keyword})))


(def
 v19_l93
 (->
  ts-ds
  (sk/view [[:date :value]])
  (sk/lay (sk/area {:alpha 0.2}) (sk/line) (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t20_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v19_l93)))


(def
 v22_l109
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/facet :species)
  sk/plot))


(deftest
 t23_l115
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v22_l109)))


(def
 v25_l123
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species})
   (sk/rule-h 3.0)
   (sk/band-v 5.5 6.5 {:alpha 0.3}))
  sk/plot))


(deftest
 t26_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v25_l123)))


(def
 v28_l139
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline {:color :species}))
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
  (sk/lay (sk/stacked-bar-fill {:color :species}))
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
  (sk/lay (sk/point {:color :species}) (sk/lm))
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
  (tc/dataset
   {:condition ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def
 v39_l191
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay
   (sk/point {:size 5})
   (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
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
  (sk/lay (sk/lollipop) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
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
  (sk/lay
   (sk/point {:alpha 0.3, :jitter 5})
   (sk/summary {:color :species}))
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
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
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
  (sk/lay
   (sk/point {:color :species, :alpha 0.5})
   (sk/lm {:color :species, :se true}))
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
  (sk/lay (sk/bar {:color :sex}))
  (sk/plot {:title "Dodged Bars (default)"})))


(deftest
 t55_l273
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l268)))


(def
 v56_l275
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :sex}))
  (sk/plot {:title "Stacked Bars"})))


(deftest
 t57_l280
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v56_l275)))


(def
 v59_l287
 (def
  daily-temps
  (tc/dataset
   {:day (range 1 15),
    :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]})))


(def
 v60_l291
 (->
  daily-temps
  (sk/view :day :temp)
  (sk/lay
   (sk/step {:color "#2196F3"})
   (sk/point {:color "#2196F3", :size 3}))
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
  (sk/view :sepal_length)
  (sk/lay
   (sk/histogram {:normalize :density, :alpha 0.4})
   (sk/density))
  sk/plot))


(deftest
 t64_l312
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (> (:polygons s) 1))) v63_l306)))


(def
 v66_l320
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay
   (sk/point {:color :species, :alpha 0.4})
   (sk/contour {:levels 5}))
  sk/plot))


(deftest
 t67_l326
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v66_l320)))


(def
 v69_l334
 (def top5 (-> iris (tc/order-by :sepal_length :desc) (tc/head 5))))


(def
 v70_l336
 (->
  top5
  (sk/view :sepal_length :sepal_width)
  (sk/lay
   (sk/point {:size 5})
   (sk/label {:text :species, :nudge-y 0.15}))
  sk/plot))


(deftest
 t71_l342
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__119363#] (= "virginica" p1__119363#))
       (:texts s)))))
   v70_l336)))


(def
 v73_l350
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t74_l358
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v73_l350)))


(def
 v76_l367
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/coord :fixed)
  (sk/plot {:title "Fixed Aspect Ratio"})))


(deftest
 t77_l373
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v76_l367)))


(def
 v79_l382
 (->
  (tc/dataset
   {:x (range 20),
    :y
    (map
     (fn* [p1__119364#] (Math/sin (/ p1__119364# 3.0)))
     (range 20)),
    :change (map (fn* [p1__119365#] (- p1__119365# 10)) (range 20))})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :change}))
  (sk/plot
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t80_l391
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v79_l382)))


(def
 v82_l399
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay
   (sk/point {:color :species})
   (sk/loess {:se true, :color :species}))
  (sk/plot {:title "LOESS with 95% CI"})))


(deftest
 t83_l405
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v82_l399)))


(def
 v85_l415
 (def
  iris-sepal
  (->
   iris
   (sk/view :sepal_length :sepal_width)
   (sk/lay (sk/point {:color :species}))
   (sk/plot {:title "Sepal", :width 300, :height 250}))))


(def
 v86_l421
 (def
  iris-petal
  (->
   iris
   (sk/view :petal_length :petal_width)
   (sk/lay (sk/point {:color :species}))
   (sk/plot {:title "Petal", :width 300, :height 250}))))


(def
 v87_l427
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t88_l430
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v87_l427)))


(def
 v90_l438
 (def
  top-cities
  (tc/dataset
   {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
    :population [37.4 32.9 29.2 22.4 21.7],
    :area [2194 1484 6341 1521 603]})))


(def
 v91_l443
 (->
  top-cities
  (sk/view :area :population)
  (sk/lay (sk/point) (sk/text {:text :city, :nudge-y 1.0}))
  (sk/plot {:title "Population vs Area"})))


(deftest
 t92_l448
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v91_l443)))


(def
 v94_l459
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l464
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l459)))


(def
 v97_l470
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l476
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l470)))


(def
 v100_l482
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm))
  (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l488
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l482)))


(def
 v103_l494
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/bar {:color :species}))
  (sk/plot {:title "Species by Island"})))


(deftest
 t104_l499
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l494)))


(def
 v106_l505
 (->
  penguins
  (sk/view [[:flipper_length_mm :body_mass_g]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l511
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l505)))


(def
 v109_l517
 (->
  penguins
  (sk/view :body_mass_g)
  (sk/lay (sk/histogram {:color :species}))
  (sk/plot {:title "Body Mass Distribution"})))


(deftest
 t110_l522
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l517)))


(def
 v112_l530
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  (sk/plot
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t113_l537
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l530)))


(def
 v115_l543
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :time}))
  (sk/plot {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l548
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l543)))


(def
 v118_l554
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :time}))
  (sk/plot {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l559
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l554)))


(def
 v121_l565
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :sex}))
  (sk/coord :flip)
  (sk/plot {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l571
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l565)))


(def
 v124_l577
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v126_l582
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay (sk/point {:color :origin}) (sk/lm {:color :origin}))
  (sk/plot {:title "Horsepower vs MPG by Origin"})))


(deftest
 t127_l588
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v126_l582)))


(def
 v129_l594
 (->
  mpg
  (sk/view [[:displacement :mpg]])
  (sk/lay (sk/point {:color :origin}))
  (sk/plot {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t130_l599
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v129_l594)))


(def
 v132_l605
 (->
  mpg
  (sk/view :origin)
  (sk/lay (sk/bar))
  (sk/plot {:title "Cars by Origin"})))


(deftest
 t133_l610
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v132_l605)))
