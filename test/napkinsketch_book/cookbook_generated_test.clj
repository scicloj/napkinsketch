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
  (sk/lay-point {:jitter true, :alpha 0.3})))


(deftest
 t6_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v5_l30)))


(def
 v8_l44
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay-histogram {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t9_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v8_l44)))


(def
 v11_l57
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species, :alpha 0.6})
  (sk/lay-lm {:color :species})))


(deftest
 t12_l62
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v11_l57)))


(def
 v14_l70
 (->
  iris
  (sk/view [[:species :petal_width]])
  (sk/lay-violin {:alpha 0.3})
  (sk/lay-point {:jitter true, :alpha 0.4})))


(deftest
 t15_l75
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v14_l70)))


(def
 v17_l84
 (def
  ts-dates
  (mapv
   (fn*
    [p1__86398#]
    (java.time.LocalDate/ofEpochDay (+ 18262 (* (long p1__86398#) 7))))
   (range 52))))


(def
 v18_l86
 (def
  ts-ds
  {:date ts-dates,
   :value
   (mapv
    (fn*
     [p1__86399#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__86399#) 0.12)))))
    (range 52))}))


(def
 v19_l90
 (->
  ts-ds
  (sk/view [[:date :value]])
  (sk/lay-area {:alpha 0.2})
  sk/lay-line
  (sk/lay-point {:alpha 0.5})))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v19_l90)))


(def
 v22_l105
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/facet :species)))


(deftest
 t23_l110
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v22_l105)))


(def
 v25_l118
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t26_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v25_l118)))


(def
 v28_l132
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay-ridgeline {:color :species})))


(deftest
 t29_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v28_l132)))


(def
 v31_l144
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v32_l147
 (->
  penguins
  (sk/view :island)
  (sk/lay-stacked-bar-fill {:color :species})))


(deftest
 t33_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v32_l147)))


(def
 v35_l161
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  sk/lay-lm))


(deftest
 t36_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v35_l161)))


(def
 v38_l175
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v39_l181
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay-point {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l181)))


(def
 v42_l194
 (->
  experiment
  (sk/view [[:condition :mean]])
  sk/lay-lollipop
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t43_l199
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l194)))


(def
 v45_l207
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay-point {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t46_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l207)))


(def
 v48_l220
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})
  (sk/options
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t49_l228
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v48_l220)))


(def
 v51_l240
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species, :alpha 0.5})
  (sk/lay-lm {:color :species, :se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t52_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v51_l240)))


(def
 v54_l255
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t55_l260
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l255)))


(def
 v56_l262
 (->
  tips
  (sk/view :day)
  (sk/lay-stacked-bar {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t57_l267
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v56_l262)))


(def
 v59_l274
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v60_l278
 (->
  daily-temps
  (sk/view :day :temp)
  (sk/lay-step {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t61_l284
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v60_l278)))


(def
 v63_l293
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t64_l298
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v63_l293)))


(def
 v66_l306
 (def top5 (-> iris (tc/order-by :sepal_length :desc) (tc/head 5))))


(def
 v67_l308
 (->
  top5
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t68_l313
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__86400#] (= "virginica" p1__86400#))
       (:texts s)))))
   v67_l308)))


(def
 v70_l321
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t71_l329
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v70_l321)))


(def
 v73_l338
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/coord :fixed)
  (sk/options {:title "Fixed Aspect Ratio"})))


(deftest
 t74_l345
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v73_l338)))


(def
 v76_l354
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__86401#] (Math/sin (/ p1__86401# 3.0))) (range 20)),
   :change (map (fn* [p1__86402#] (- p1__86402# 10)) (range 20))}
  (sk/view :x :y)
  (sk/lay-point {:color :change})
  (sk/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t77_l363
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v76_l354)))


(def
 v79_l371
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/lay-loess {:se true, :color :species})
  (sk/options {:title "LOESS with 95% CI"})))


(deftest
 t80_l377
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v79_l371)))


(def
 v82_l386
 (def
  iris-sepal
  (->
   iris
   (sk/view :sepal_length :sepal_width)
   (sk/lay-point {:color :species})
   (sk/options {:title "Sepal", :width 300, :height 250}))))


(def
 v83_l392
 (def
  iris-petal
  (->
   iris
   (sk/view :petal_length :petal_width)
   (sk/lay-point {:color :species})
   (sk/options {:title "Petal", :width 300, :height 250}))))


(def
 v84_l398
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t85_l401
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v84_l398)))


(def
 v87_l408
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v88_l413
 (->
  top-cities
  (sk/view :area :population)
  sk/lay-point
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/options {:title "Population vs Area"})))


(deftest
 t89_l419
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v88_l413)))


(def
 v91_l427
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (mapv
    (fn*
     [p1__86403#]
     (+ (* 3 p1__86403#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/view [[:x :y]])
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t92_l439
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v91_l427)))


(def
 v94_l450
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  (sk/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l455
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l450)))


(def
 v97_l461
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l467
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l461)))


(def
 v100_l473
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay-point {:color :species})
  sk/lay-lm
  (sk/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l479
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l473)))


(def
 v103_l485
 (->
  penguins
  (sk/view :island)
  (sk/lay-bar {:color :species})
  (sk/options {:title "Species by Island"})))


(deftest
 t104_l490
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l485)))


(def
 v106_l496
 (->
  penguins
  (sk/view [[:flipper_length_mm :body_mass_g]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})
  (sk/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l496)))


(def
 v109_l508
 (->
  penguins
  (sk/view :body_mass_g)
  (sk/lay-histogram {:color :species})
  (sk/options {:title "Body Mass Distribution"})))


(deftest
 t110_l513
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l508)))


(def
 v112_l521
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay-point {:color :smoker})
  (sk/lay-lm {:color :smoker})
  (sk/options
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t113_l528
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l521)))


(def
 v115_l534
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :time})
  (sk/options {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l539
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l534)))


(def
 v118_l545
 (->
  tips
  (sk/view :day)
  (sk/lay-stacked-bar {:color :time})
  (sk/options {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l545)))


(def
 v121_l556
 (->
  tips
  (sk/view :day)
  (sk/lay-bar {:color :sex})
  (sk/coord :flip)
  (sk/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l562
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l556)))


(def
 v124_l568
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v126_l573
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay-point {:color :origin})
  (sk/lay-lm {:color :origin})
  (sk/options {:title "Horsepower vs MPG by Origin"})))


(deftest
 t127_l579
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v126_l573)))


(def
 v129_l585
 (->
  mpg
  (sk/view [[:displacement :mpg]])
  (sk/lay-point {:color :origin})
  (sk/options {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t130_l590
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v129_l585)))


(def
 v132_l596
 (->
  mpg
  (sk/view :origin)
  sk/lay-bar
  (sk/options {:title "Cars by Origin"})))


(deftest
 t133_l601
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v132_l596)))
