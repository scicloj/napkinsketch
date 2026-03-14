(ns
 napkinsketch-book.composability-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l20
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def v6_l27 (def views (sk/view iris [[:sepal_length :sepal_width]])))


(def
 v7_l29
 (kind/pprint
  (mapv (fn* [p1__74511#] (dissoc p1__74511# :data)) views)))


(def v9_l36 (def layered (sk/lay views (sk/point {:color :species}))))


(def
 v10_l38
 (kind/pprint
  (mapv (fn* [p1__74512#] (dissoc p1__74512# :data)) layered)))


(def
 v12_l45
 (def
  mark-for-type
  (fn
   [col-type]
   (case
    col-type
    :scatter
    (sk/point)
    :trend
    (sk/lm)
    :dist
    (sk/histogram)))))


(def
 v13_l52
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (mark-for-type :scatter) (mark-for-type :trend))
  sk/plot))


(deftest
 t14_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v13_l52)))


(def
 v16_l66
 (->
  iris
  (sk/view :petal_length :petal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t17_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l66)))


(def
 v19_l79
 (->
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 1 11),
    :y
    (mapv
     (fn* [p1__74513#] (+ (* 2 p1__74513#) (- (rng/irandom r 5) 2)))
     (range 1 11))})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Noisy Linear Trend"})))


(deftest
 t20_l86
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 10 (:points s))
      (= 1 (:lines s))
      (some #{"Noisy Linear Trend"} (:texts s)))))
   v19_l79)))


(def
 v22_l95
 (def
  species-plot
  (fn
   [species-name]
   (->
    iris
    (tc/select-rows
     (fn* [p1__74514#] (= species-name (p1__74514# :species))))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:width 300, :height 250, :title species-name})))))


(def v23_l104 (species-plot "setosa"))


(deftest
 t24_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 50 (:points s))
      (= 1 (:lines s))
      (some #{"setosa"} (:texts s)))))
   v23_l104)))


(def v25_l111 (species-plot "versicolor"))


(deftest
 t26_l113
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (some #{"versicolor"} (:texts s)))))
   v25_l111)))


(def v27_l117 (species-plot "virginica"))


(deftest
 t28_l119
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (some #{"virginica"} (:texts s)))))
   v27_l117)))


(def
 v30_l125
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t31_l131
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v30_l125)))


(def
 v33_l140
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v35_l144
 (->
  iris
  (sk/view [[:sepal_length :petal_length]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length vs Petal Length"})))


(deftest
 t36_l149
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Sepal Length vs Petal Length"} (:texts s)))))
   v35_l144)))


(def
 v37_l153
 (->
  iris
  (sk/view [[:sepal_width :petal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Width vs Petal Width"})))


(deftest
 t38_l158
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v37_l153)))


(def
 v40_l165
 (def
  quarterly-data
  (fn
   []
   (tc/dataset
    {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4],
     :revenue [100 120 90 140 80 95 110 130],
     :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]}))))


(def
 v41_l171
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/plot {:title "Quarterly Revenue Comparison"})))


(deftest
 t42_l176
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Quarterly Revenue Comparison"} (:texts s)))))
   v41_l171)))


(def
 v44_l182
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/coord :flip)
  (sk/plot {:title "Revenue (Horizontal)"})))


(deftest
 t45_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Revenue (Horizontal)"} (:texts s)))))
   v44_l182)))


(def
 v47_l196
 (def
  simulated
  (let
   [r
    (rng/rng :jdk 77)
    xs
    (range 0 10 0.5)
    ys
    (mapv
     (fn*
      [p1__74515#]
      (+ (* 3 p1__74515#) 5 (* 2 (- (rng/drandom r) 0.5))))
     xs)]
   (tc/dataset {:x xs, :y ys}))))


(def
 v48_l202
 (->
  simulated
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Simulated: y = 3x + 5 + noise"})))


(deftest
 t49_l207
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v48_l202)))


(def
 v51_l219
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t52_l225
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 3 (:polygons s)))))
   v51_l219)))


(def
 v54_l231
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :jitter 3})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t55_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v54_l231)))


(def
 v57_l243
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  (sk/scale :x :log)
  (sk/labs {:title "Log-Scale with Gradient Color"})
  sk/plot))


(deftest
 t58_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Log-Scale with Gradient Color"} (:texts s)))))
   v57_l243)))


(def
 v60_l256
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:alpha 0.3}) (sk/boxplot))
  sk/plot))


(deftest
 t61_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v60_l256)))
