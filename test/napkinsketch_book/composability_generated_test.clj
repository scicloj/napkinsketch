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
 v12_l46
 (->
  iris
  (sk/view :petal_length :petal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t13_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v12_l46)))


(def
 v15_l59
 (->
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 1 11),
    :y
    (mapv
     (fn* [p1__89964#] (+ (* 2 p1__89964#) (- (rng/irandom r 5) 2)))
     (range 1 11))})
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Noisy Linear Trend"})))


(deftest
 t16_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 10 (:points s))
      (= 1 (:lines s))
      (some #{"Noisy Linear Trend"} (:texts s)))))
   v15_l59)))


(def
 v18_l75
 (def
  species-plot
  (fn
   [species-name]
   (->
    iris
    (tc/select-rows
     (fn* [p1__89965#] (= species-name (p1__89965# :species))))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:width 300, :height 250, :title species-name})))))


(def
 v19_l84
 (sk/arrange
  (mapv species-plot ["setosa" "versicolor" "virginica"])
  {:cols 3}))


(deftest t20_l87 (is ((fn [v] (= :div (first v))) v19_l84)))


(def
 v22_l91
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t23_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l91)))


(def
 v25_l107
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def v26_l109 (sk/pairs measurements))


(deftest t27_l111 (is ((fn [v] (= 6 (count v))) v26_l109)))


(def
 v28_l113
 (->
  iris
  (sk/view (sk/pairs measurements))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t29_l118
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 6 (:panels s)))) v28_l113)))


(def
 v31_l125
 (def
  quarterly-data
  {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4],
   :revenue [100 120 90 140 80 95 110 130],
   :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]}))


(def
 v32_l130
 (->
  quarterly-data
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/plot {:title "Quarterly Revenue Comparison"})))


(deftest
 t33_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Quarterly Revenue Comparison"} (:texts s)))))
   v32_l130)))


(def
 v35_l141
 (->
  quarterly-data
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/coord :flip)
  (sk/plot {:title "Revenue (Horizontal)"})))


(deftest
 t36_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Revenue (Horizontal)"} (:texts s)))))
   v35_l141)))


(def
 v38_l155
 (let
  [r
   (rng/rng :jdk 77)
   xs
   (range 0 10 0.5)
   ys
   (mapv
    (fn*
     [p1__89966#]
     (+ (* 3 p1__89966#) 5 (* 2 (- (rng/drandom r) 0.5))))
    xs)]
  (->
   {:x xs, :y ys}
   (sk/view [[:x :y]])
   (sk/lay (sk/point) (sk/lm))
   (sk/plot {:title "Simulated: y = 3x + 5 + noise"}))))


(deftest
 t39_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v38_l155)))


(def
 v41_l178
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t42_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 3 (:polygons s)))))
   v41_l178)))


(def
 v44_l190
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :jitter 3})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t45_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v44_l190)))


(def
 v47_l202
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  (sk/scale :x :log)
  (sk/labs {:title "Log-Scale with Gradient Color"})
  sk/plot))


(deftest
 t48_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Log-Scale with Gradient Color"} (:texts s)))))
   v47_l202)))


(def
 v50_l215
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:alpha 0.3}) (sk/boxplot))
  sk/plot))


(deftest
 t51_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v50_l215)))
