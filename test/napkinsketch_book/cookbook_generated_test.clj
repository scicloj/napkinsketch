(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (->
  data/iris
  (sk/lay-boxplot :species :sepal_length)
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
  data/iris
  (sk/lay-histogram :sepal_length {:normalize :density, :alpha 0.5})
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
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
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
  data/iris
  (sk/lay-violin :species :petal_width {:alpha 0.3})
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
     [p1__130286#]
     (+ 100.0 (* 30.0 (Math/sin (* (double p1__130286#) 0.12)))))
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
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t21_l103
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v20_l99)))


(def
 v23_l111
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t24_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v23_l111)))


(def
 v26_l124
 (->
  data/iris
  (sk/lay-ridgeline :species :sepal_length {:color :species})))


(deftest
 t27_l127
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v26_l124)))


(def
 v29_l135
 (-> data/penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t30_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v29_l135)))


(def
 v32_l148
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/lay-lm))


(deftest
 t33_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v32_l148)))


(def
 v35_l161
 (def
  experiment
  {:condition ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def
 v36_l167
 (->
  experiment
  (sk/lay-point :condition :mean {:size 5})
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t37_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v36_l167)))


(def
 v39_l179
 (->
  experiment
  (sk/lay-lollipop :condition :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t40_l183
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v39_l179)))


(def
 v42_l191
 (->
  data/iris
  (sk/lay-point :species :sepal_length {:alpha 0.3, :jitter 5})
  (sk/lay-summary {:color :species})))


(deftest
 t43_l195
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v42_l191)))


(def
 v45_l203
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
 t46_l211
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v45_l203)))


(def
 v48_l223
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  (sk/lay-point {:alpha 0.5})
  (sk/lay-lm {:se true})
  (sk/options {:title "Sepal Regression with Confidence Bands"})))


(deftest
 t49_l229
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (pos? (:lines s)))))
   v48_l223)))


(def
 v51_l238
 (->
  data/tips
  (sk/lay-bar :day {:color :sex})
  (sk/options {:title "Dodged Bars (default)"})))


(deftest
 t52_l242
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l238)))


(def
 v53_l244
 (->
  data/tips
  (sk/lay-stacked-bar :day {:color :sex})
  (sk/options {:title "Stacked Bars"})))


(deftest
 t54_l248
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v53_l244)))


(def
 v56_l255
 (def
  daily-temps
  {:day (range 1 15),
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))


(def
 v57_l259
 (->
  daily-temps
  (sk/lay-step :day :temp {:color "#2196F3"})
  (sk/lay-point {:color "#2196F3", :size 3})
  (sk/options {:title "Daily Temperature (Step)"})))


(deftest
 t58_l264
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v57_l259)))


(def
 v60_l273
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.4})
  (sk/lay-contour {:levels 5})))


(deftest
 t61_l277
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v60_l273)))


(def
 v63_l285
 (def
  top5
  (-> data/iris (tc/order-by :sepal_length :desc) (tc/head 5))))
