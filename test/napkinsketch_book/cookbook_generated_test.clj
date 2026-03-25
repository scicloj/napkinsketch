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
  (sk/lay-boxplot :species :sepal_length)
  (sk/lay-point {:jitter true, :alpha 0.3})))


(deftest
 t6_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v5_l30)))


(def
 v8_l43
 (->
  iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t9_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v8_l43)))


(def
 v11_l55
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/lay-point {:alpha 0.6})
  sk/lay-lm))


(deftest
 t12_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v11_l55)))


(def
 v14_l68
 (->
  iris
  (sk/lay-violin :species :petal_width {:alpha 0.3})
  (sk/lay-point {:jitter true, :alpha 0.4})))


(deftest
 t15_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v14_l68)))


(def
 v17_l81
 (def
  ts-dates
  (mapv
   (fn*
    [p1__74421#]
    (java.time.LocalDate/ofEpochDay (+ 18262 (* (long p1__74421#) 7))))
   (range 52))))


(def
 v18_l83
 (def
  ts-ds
  {:date ts-dates,
   :value
   (mapv
    (fn*
     [p1__74422#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__74422#) 0.12)))))
    (range 52))}))


(def
 v19_l87
 (->
  ts-ds
  (sk/lay-area :date :value {:alpha 0.2})
  sk/lay-line
  (sk/lay-point {:alpha 0.5})))


(deftest
 t20_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v19_l87)))


(def
 v22_l101
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t23_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v22_l101)))


(def
 v25_l113
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t26_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v25_l113)))


(def
 v28_l126
 (-> iris (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t29_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v28_l126)))


(def
 v31_l137
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v32_l140
 (-> penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t33_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v32_l140)))


(def
 v35_l153
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t36_l157
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v35_l153)))


(def
 v38_l166
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v39_l172
 (->
  experiment
  (sk/lay-point :condition :mean {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l176
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l172)))


(def
 v42_l184
 (->
  experiment
  (sk/lay-lollipop :condition :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t43_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l184)))


(def
 v45_l196
 (->
  iris
  (sk/lay-point :species :sepal_length {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t46_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l196)))


(def
 v48_l208
 (->
  tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t49_l216
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v48_l208)))


(def
 v51_l228
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/lay-point {:alpha 0.5})
  (sk/lay-lm {:se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t52_l234
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v51_l228)))


(def
 v54_l243
 (->
  tips
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t55_l247
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l243)))


(def
 v56_l249
 (->
  tips
  (sk/lay-stacked-bar :day {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t57_l253
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v56_l249)))


(def
 v59_l260
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v60_l264
 (->
  daily-temps
  (sk/lay-step :day :temp {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t61_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v60_l264)))


(def
 v63_l278
 (->
  iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t64_l282
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v63_l278)))


(def
 v66_l290
 (def top5 (-> iris (tc/order-by :sepal_length :desc) (tc/head 5))))


(def
 v67_l292
 (->
  top5
  (sk/lay-point :sepal_length :sepal_width {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t68_l296
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__74423#] (= "virginica" p1__74423#))
       (:texts s)))))
   v67_l292)))


(def
 v70_l304
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t71_l311
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v70_l304)))


(def
 v73_l320
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/coord :fixed)
  (sk/options {:title "Fixed Aspect Ratio"})))


(deftest
 t74_l327
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v73_l320)))


(def
 v76_l336
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__74424#] (Math/sin (/ p1__74424# 3.0))) (range 20)),
   :change (map (fn* [p1__74425#] (- p1__74425# 10)) (range 20))}
  (sk/lay-point :x :y {:color :change})
  (sk/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t77_l344
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v76_l336)))


(def
 v79_l352
 (->
  iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-loess {:se true})
  (sk/options {:title "LOESS with 95% CI"})))


(deftest
 t80_l358
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v79_l352)))


(def
 v82_l367
 (def
  iris-sepal
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/options {:title "Sepal", :width 300, :height 250}))))


(def
 v83_l372
 (def
  iris-petal
  (->
   iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/options {:title "Petal", :width 300, :height 250}))))


(def
 v84_l377
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t85_l380
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v84_l377)))


(def
 v87_l387
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v88_l392
 (->
  top-cities
  (sk/lay-point :area :population)
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/options {:title "Population vs Area"})))


(deftest
 t89_l397
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v88_l392)))


(def
 v91_l405
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (mapv
    (fn*
     [p1__74426#]
     (+ (* 3 p1__74426#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/lay-point :x :y)
   sk/lay-lm
   (sk/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t92_l416
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v91_l405)))


(def
 v94_l427
 (->
  penguins
  (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
  (sk/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t95_l431
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v94_l427)))


(def
 v97_l437
 (->
  penguins
  (sk/view :bill_length_mm :bill_depth_mm {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t98_l443
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v97_l437)))


(def
 v100_l450
 (->
  penguins
  (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
  sk/lay-lm
  (sk/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t101_l455
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v100_l450)))


(def
 v103_l461
 (->
  penguins
  (sk/lay-bar :island {:color :species})
  (sk/options {:title "Species by Island"})))


(deftest
 t104_l465
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v103_l461)))


(def
 v106_l471
 (->
  penguins
  (sk/view :flipper_length_mm :body_mass_g {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t107_l477
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v106_l471)))


(def
 v109_l483
 (->
  penguins
  (sk/lay-histogram :body_mass_g {:color :species})
  (sk/options {:title "Body Mass Distribution"})))


(deftest
 t110_l487
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v109_l483)))


(def
 v112_l495
 (->
  tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t113_l502
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v112_l495)))


(def
 v115_l508
 (->
  tips
  (sk/lay-bar :day {:color :time})
  (sk/options {:title "Visits by Day and Meal Time"})))


(deftest
 t116_l512
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l508)))


(def
 v118_l518
 (->
  tips
  (sk/lay-stacked-bar :day {:color :time})
  (sk/options {:title "Visits by Day (Stacked)"})))


(deftest
 t119_l522
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l518)))


(def
 v121_l528
 (->
  tips
  (sk/lay-bar :day {:color :sex})
  (sk/coord :flip)
  (sk/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t122_l533
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v121_l528)))


(def
 v124_l539
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v126_l544
 (->
  mpg
  (sk/view :horsepower :mpg {:color :origin})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Horsepower vs MPG by Origin"})))


(deftest
 t127_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v126_l544)))


(def
 v129_l556
 (->
  mpg
  (sk/lay-point :displacement :mpg {:color :origin})
  (sk/options {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t130_l560
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v129_l556)))


(def
 v132_l566
 (-> mpg (sk/lay-bar :origin) (sk/options {:title "Cars by Origin"})))


(deftest
 t133_l570
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v132_l566)))
