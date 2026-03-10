(ns
 napkinsketch-book.composability-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l16
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l19
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def v6_l26 (def views (sk/view iris [[:sepal_length :sepal_width]])))


(def
 v7_l28
 (kind/pprint
  (mapv (fn* [p1__457804#] (dissoc p1__457804# :data)) views)))


(def v9_l35 (def layered (sk/lay views (sk/point {:color :species}))))


(def
 v10_l37
 (kind/pprint
  (mapv (fn* [p1__457805#] (dissoc p1__457805# :data)) layered)))


(def
 v12_l44
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
 v13_l51
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (mark-for-type :scatter) (mark-for-type :trend))
  sk/plot))


(deftest
 t14_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v13_l51)))


(def
 v16_l65
 (->
  iris
  (sk/view :petal_length :petal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t17_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l65)))


(def
 v19_l78
 (->
  {:x (range 1 11),
   :y
   (mapv
    (fn* [p1__457806#] (+ (* 2 p1__457806#) (- (rand-int 5) 2)))
    (range 1 11))}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Noisy Linear Trend"})))


(deftest
 t20_l84
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 10 (:points s))
      (= 1 (:lines s))
      (some #{"Noisy Linear Trend"} (:texts s)))))
   v19_l78)))


(def
 v22_l93
 (def
  species-plot
  (fn
   [species-name]
   (->
    iris
    (tc/select-rows
     (fn* [p1__457807#] (= species-name (p1__457807# :species))))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:width 300, :height 250, :title species-name})))))


(def v23_l102 (species-plot "setosa"))


(deftest
 t24_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 50 (:points s))
      (= 1 (:lines s))
      (some #{"setosa"} (:texts s)))))
   v23_l102)))


(def v25_l109 (species-plot "versicolor"))


(deftest
 t26_l111
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (some #{"versicolor"} (:texts s)))))
   v25_l109)))


(def v27_l115 (species-plot "virginica"))


(deftest
 t28_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (some #{"virginica"} (:texts s)))))
   v27_l115)))


(def
 v30_l123
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t31_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v30_l123)))


(def
 v33_l138
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v35_l142
 (->
  iris
  (sk/view [[:sepal_length :petal_length]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length vs Petal Length"})))


(deftest
 t36_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Sepal Length vs Petal Length"} (:texts s)))))
   v35_l142)))


(def
 v37_l151
 (->
  iris
  (sk/view [[:sepal_width :petal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Width vs Petal Width"})))


(deftest
 t38_l156
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v37_l151)))


(def
 v40_l163
 (def
  quarterly-data
  (fn
   []
   (tc/dataset
    {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4],
     :revenue [100 120 90 140 80 95 110 130],
     :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]}))))


(def
 v41_l169
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/plot {:title "Quarterly Revenue Comparison"})))


(deftest
 t42_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Quarterly Revenue Comparison"} (:texts s)))))
   v41_l169)))


(def
 v44_l180
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/coord :flip)
  (sk/plot {:title "Revenue (Horizontal)"})))


(deftest
 t45_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 8 (:polygons s))
      (some #{"Revenue (Horizontal)"} (:texts s)))))
   v44_l180)))


(def
 v47_l194
 (def
  simulated
  (fn
   []
   (let
    [xs
     (range 0 10 0.5)
     ys
     (mapv
      (fn* [p1__457808#] (+ (* 3 p1__457808#) 5 (* 2 (- (rand) 0.5))))
      xs)]
    (tc/dataset {:x xs, :y ys})))))


(def
 v48_l200
 (->
  (simulated)
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Simulated: y = 3x + 5 + noise"})))


(deftest
 t49_l205
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 20 (:points s))
      (= 1 (:lines s))
      (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))
   v48_l200)))


(def
 v51_l217
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/density {:color :species}))
  sk/plot))


(deftest
 t52_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 3 (:polygons s)))))
   v51_l217)))


(def
 v54_l229
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :jitter 3})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t55_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v54_l229)))


(def
 v57_l241
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  (sk/scale :x :log)
  (sk/labs {:title "Log-Scale with Gradient Color"})
  sk/plot))


(deftest
 t58_l248
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Log-Scale with Gradient Color"} (:texts s)))))
   v57_l241)))


(def
 v60_l254
 (->
  tips
  (sk/view [[:day :total_bill]])
  (sk/lay (sk/violin {:alpha 0.3}) (sk/boxplot))
  sk/plot))


(deftest
 t61_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v60_l254)))
