(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l26
 (->
  data/iris
  (sk/lay-boxplot :species :sepal_length)
  (sk/lay-point {:jitter true, :alpha 0.3})))


(deftest
 t4_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v3_l26)))


(def
 v6_l39
 (->
  data/iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
  sk/lay-density))


(deftest
 t7_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l39)))


(def
 v9_l51
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/lay-point {:alpha 0.6})
  sk/lay-lm))


(deftest
 t10_l56
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l51)))


(def
 v12_l64
 (->
  data/iris
  (sk/lay-violin :species :petal_width {:alpha 0.3})
  (sk/lay-point {:jitter true, :alpha 0.4})))


(deftest
 t13_l68
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v12_l64)))


(def
 v15_l77
 (def
  ts-dates
  (map
   (fn*
    [p1__79925#]
    (java.time.LocalDate/ofEpochDay (+ 18262 (* (long p1__79925#) 7))))
   (range 52))))


(def
 v16_l79
 (def
  ts-ds
  {:date ts-dates,
   :value
   (map
    (fn*
     [p1__79926#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__79926#) 0.12)))))
    (range 52))}))


(def
 v17_l83
 (->
  ts-ds
  (sk/lay-area :date :value {:alpha 0.2})
  sk/lay-line
  (sk/lay-point {:alpha 0.5})))


(deftest
 t18_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v17_l83)))


(def
 v20_l97
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t21_l101
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v20_l97)))


(def
 v23_l109
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t24_l113
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v23_l109)))


(def
 v26_l122
 (->
  data/iris
  (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t27_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v26_l122)))


(def
 v29_l133
 (-> data/penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t30_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v29_l133)))


(def
 v32_l146
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t33_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v32_l146)))


(def
 v35_l159
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v36_l165
 (->
  experiment
  (sk/lay-point :condition :mean {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t37_l169
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v36_l165)))


(def
 v39_l177
 (->
  experiment
  (sk/lay-lollipop :condition :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v39_l177)))


(def
 v42_l189
 (->
  data/iris
  (sk/lay-point :species :sepal_length {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t43_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v42_l189)))


(def
 v45_l201
 (->
  data/tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t46_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v45_l201)))


(def
 v48_l221
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/lay-point {:alpha 0.5})
  (sk/lay-lm {:se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t49_l227
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v48_l221)))


(def
 v51_l236
 (->
  data/tips
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t52_l240
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l236)))


(def
 v53_l242
 (->
  data/tips
  (sk/lay-stacked-bar :day {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t54_l246
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v53_l242)))


(def
 v56_l253
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v57_l257
 (->
  daily-temps
  (sk/lay-step :day :temp {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t58_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v57_l257)))


(def
 v60_l271
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t61_l275
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v60_l271)))


(def
 v63_l283
 (def
  top5
  (-> data/iris (tc/order-by :sepal_length :desc) (tc/head 5))))


(def
 v64_l285
 (->
  top5
  (sk/lay-point :sepal_length :sepal_width {:size 5})
  (sk/lay-label {:text :species, :nudge-y 0.15})))


(deftest
 t65_l289
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (some
       (fn* [p1__79927#] (= "virginica" p1__79927#))
       (:texts s)))))
   v64_l285)))


(def
 v67_l297
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E91E63", :versicolor "#4CAF50", :virginica "#2196F3"},
    :title "Custom Palette Map"})))


(deftest
 t68_l304
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v67_l297)))


(def
 v70_l313
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/coord :fixed)
  (sk/options {:title "Fixed Aspect Ratio"})))


(deftest
 t71_l320
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (= 3 (:lines s)))))
   v70_l313)))


(def
 v73_l329
 (->
  {:x (range 20),
   :y
   (map (fn* [p1__79928#] (Math/sin (/ p1__79928# 3.0))) (range 20)),
   :change (map (fn* [p1__79929#] (- p1__79929# 10)) (range 20))}
  (sk/lay-point :x :y {:color :change})
  (sk/options
   {:color-scale :diverging,
    :color-midpoint 0,
    :title "Diverging Color Scale"})))


(deftest
 t74_l337
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 20 (:points s)))))
   v73_l329)))


(def
 v76_l345
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  (sk/lay-loess {:se true})
  (sk/options {:title "LOESS with 95% CI"})))


(deftest
 t77_l351
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)) (= 3 (:polygons s)))))
   v76_l345)))


(def
 v79_l360
 (def
  iris-sepal
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/options {:title "Sepal", :width 300, :height 250}))))


(def
 v80_l365
 (def
  iris-petal
  (->
   data/iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/options {:title "Petal", :width 300, :height 250}))))


(def
 v81_l370
 (sk/arrange
  [iris-sepal iris-petal]
  {:title "Iris Dashboard", :cols 2}))


(deftest
 t82_l373
 (is
  ((fn
    [v]
    (and (= :div (first v)) (= :kind/hiccup (:kindly/kind (meta v)))))
   v81_l370)))


(def
 v84_l380
 (def
  top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"],
   :population [37.4 32.9 29.2 22.4 21.7],
   :area [2194 1484 6341 1521 603]}))


(def
 v85_l385
 (->
  top-cities
  (sk/lay-point :area :population)
  (sk/lay-text {:text :city, :nudge-y 1.0})
  (sk/options {:title "Population vs Area"})))


(deftest
 t86_l390
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 5 (:points s))
      (every? (set (:texts s)) ["Tokyo" "Delhi"]))))
   v85_l385)))


(def
 v88_l398
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (map
    (fn*
     [p1__79930#]
     (+ (* 3 p1__79930#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/lay-point :x :y)
   sk/lay-lm
   (sk/options {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t89_l409
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v88_l398)))


(def
 v91_l420
 (->
  data/penguins
  (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
  (sk/options {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t92_l424
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v91_l420)))


(def
 v94_l430
 (->
  data/penguins
  (sk/view :bill_length_mm :bill_depth_mm {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Bill Length vs Depth with Regression"})))


(deftest
 t95_l436
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v94_l430)))


(def
 v97_l443
 (->
  data/penguins
  (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
  sk/lay-lm
  (sk/options
   {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t98_l448
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v97_l443)))


(def
 v100_l454
 (->
  data/penguins
  (sk/lay-bar :island {:color :species})
  (sk/options {:title "Species by Island"})))


(deftest
 t101_l458
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v100_l454)))


(def
 v103_l464
 (->
  data/penguins
  (sk/view :flipper_length_mm :body_mass_g {:color :species})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Flipper Length vs Body Mass"})))


(deftest
 t104_l470
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v103_l464)))


(def
 v106_l476
 (->
  data/penguins
  (sk/lay-histogram :body_mass_g {:color :species})
  (sk/options {:title "Body Mass Distribution"})))


(deftest
 t107_l480
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v106_l476)))


(def
 v109_l488
 (->
  data/tips
  (sk/view :total_bill :tip {:color :smoker})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t110_l495
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v109_l488)))


(def
 v112_l501
 (->
  data/tips
  (sk/lay-bar :day {:color :time})
  (sk/options {:title "Visits by Day and Meal Time"})))


(deftest
 t113_l505
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v112_l501)))


(def
 v115_l511
 (->
  data/tips
  (sk/lay-stacked-bar :day {:color :time})
  (sk/options {:title "Visits by Day (Stacked)"})))


(deftest
 t116_l515
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v115_l511)))


(def
 v118_l521
 (->
  data/tips
  (sk/lay-bar :day {:color :sex})
  (sk/coord :flip)
  (sk/options {:title "Day by Gender (Horizontal)"})))


(deftest
 t119_l526
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v118_l521)))


(def
 v121_l534
 (->
  data/mpg
  (sk/view :horsepower :mpg {:color :origin})
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Horsepower vs MPG by Origin"})))


(deftest
 t122_l540
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v121_l534)))


(def
 v124_l546
 (->
  data/mpg
  (sk/lay-point :displacement :mpg {:color :origin})
  (sk/options {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t125_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v124_l546)))


(def
 v127_l556
 (->
  data/mpg
  (sk/lay-bar :origin)
  (sk/options {:title "Cars by Origin"})))


(deftest
 t128_l560
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v127_l556)))
