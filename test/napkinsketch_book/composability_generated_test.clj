(ns
 napkinsketch-book.composability-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l24
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def v6_l31 (def views (sk/view iris [[:sepal_length :sepal_width]])))


(def v7_l33 (kind/pprint views))


(def v9_l38 (def layered (sk/lay views (sk/point {:color :species}))))


(def v10_l40 (kind/pprint layered))


(def
 v12_l47
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t13_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l47)))


(def
 v14_l57
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/lay (sk/lm {:color :species}))
  sk/plot))


(deftest
 t15_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v14_l57)))


(def
 v17_l70
 (def
  scatter-base
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay (sk/point {:color :species})))))


(def
 v19_l77
 (-> scatter-base (sk/lay (sk/lm {:color :species})) sk/plot))


(deftest
 t20_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v19_l77)))


(def
 v22_l87
 (-> scatter-base (sk/lay (sk/loess {:color :species})) sk/plot))


(deftest
 t23_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l87)))


(def
 v25_l99
 (->
  iris
  (sk/view :petal_length :petal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t26_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v25_l99)))


(def
 v28_l112
 (->
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 1 11),
    :y
    (mapv
     (fn* [p1__150225#] (+ (* 2 p1__150225#) (- (rng/irandom r 5) 2)))
     (range 1 11))})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Noisy Linear Trend"})))


(deftest
 t29_l119
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 10 (:points s))
      (= 1 (:lines s))
      (some #{"Noisy Linear Trend"} (:texts s)))))
   v28_l112)))


(def
 v31_l128
 (def
  species-plot
  (fn
   [species-name]
   (->
    iris
    (tc/select-rows
     (fn* [p1__150226#] (= species-name (p1__150226# :species))))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:width 300, :height 250, :title species-name})))))


(def
 v32_l137
 (sk/arrange
  (mapv species-plot ["setosa" "versicolor" "virginica"])
  {:cols 3}))


(deftest t33_l140 (is ((fn [v] (= :div (first v))) v32_l137)))


(def
 v35_l144
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t36_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v35_l144)))


(def
 v38_l160
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def v39_l162 (sk/pairs measurements))


(deftest t40_l164 (is ((fn [v] (= 6 (count v))) v39_l162)))


(def
 v41_l166
 (->
  iris
  (sk/view (sk/pairs measurements))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t42_l171
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 6 (:panels s)))) v41_l166)))


(def
 v44_l178
 (def
  quarterly-data
  {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4],
   :revenue [100 120 90 140 80 95 110 130],
   :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]}))


(def
 v45_l183
 (->
  quarterly-data
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/plot {:title "Quarterly Revenue Comparison"})))


(deftest
 t46_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Quarterly Revenue Comparison"} (:texts s)))))
   v45_l183)))


(def
 v48_l194
 (->
  quarterly-data
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/coord :flip)
  (sk/plot {:title "Revenue (Horizontal)"})))


(deftest
 t49_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Revenue (Horizontal)"} (:texts s)))))
   v48_l194)))


(def
 v51_l208
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (mapv
    (fn*
     [p1__150227#]
     (+ (* 3 p1__150227#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/view [[:x :y]])
   (sk/lay (sk/point) (sk/lm))
   (sk/plot {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t52_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v51_l208)))


(def
 v54_l231
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t55_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 3 (:polygons s)))))
   v54_l231)))


(def
 v57_l243
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :jitter 3})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t58_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v57_l243)))


(def
 v60_l255
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  (sk/scale :x :log)
  (sk/labs {:title "Log-Scale with Gradient Color"})
  sk/plot))


(deftest
 t61_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Log-Scale with Gradient Color"} (:texts s)))))
   v60_l255)))


(def
 v63_l268
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:alpha 0.3}) (sk/boxplot))
  sk/plot))


(deftest
 t64_l274
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v63_l268)))
