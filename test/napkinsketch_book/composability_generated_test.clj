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


(def v5_l23 (def views (sk/view iris [[:sepal_length :sepal_width]])))


(def
 v6_l25
 (kind/pprint
  (mapv (fn* [p1__80667#] (dissoc p1__80667# :data)) views)))


(def v8_l32 (def layered (sk/lay views (sk/point {:color :species}))))


(def
 v9_l34
 (kind/pprint
  (mapv (fn* [p1__80668#] (dissoc p1__80668# :data)) layered)))


(def
 v11_l41
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
 v12_l48
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (mark-for-type :scatter) (mark-for-type :trend))
  sk/plot))


(deftest
 t13_l54
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v12_l48)))


(def
 v15_l63
 (->
  iris
  (sk/view :petal_length :petal_width)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t16_l68
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v15_l63)))


(def
 v18_l77
 (->
  {:x (range 1 11),
   :y
   (mapv
    (fn* [p1__80669#] (+ (* 2 p1__80669#) (- (rand-int 5) 2)))
    (range 1 11))}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Noisy Linear Trend"})))


(deftest
 t19_l83
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v18_l77)))


(def
 v21_l92
 (def
  species-plot
  (fn
   [species-name]
   (->
    iris
    (tc/select-rows
     (fn* [p1__80670#] (= species-name (p1__80670# :species))))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:width 300, :height 250, :title species-name})))))


(def v22_l101 (species-plot "setosa"))


(deftest
 t23_l103
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and (map? attrs) (= 300 (:width attrs))))))
   v22_l101)))


(def v24_l109 (species-plot "versicolor"))


(deftest
 t25_l111
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v24_l109)))


(def v26_l116 (species-plot "virginica"))


(deftest
 t27_l118
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v26_l116)))


(def
 v29_l127
 (def
  measurements
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v31_l131
 (->
  iris
  (sk/view [[:sepal_length :petal_length]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length vs Petal Length"})))


(deftest
 t32_l136
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v31_l131)))


(def
 v33_l141
 (->
  iris
  (sk/view [[:sepal_width :petal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Width vs Petal Width"})))


(deftest
 t34_l146
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v33_l141)))


(def
 v36_l155
 (def
  quarterly-data
  (fn
   []
   (tc/dataset
    {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4],
     :revenue [100 120 90 140 80 95 110 130],
     :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]}))))


(def
 v37_l161
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/plot {:title "Quarterly Revenue Comparison"})))


(deftest
 t38_l166
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v37_l161)))


(def
 v40_l173
 (->
  (quarterly-data)
  (sk/view [[:quarter :revenue]])
  (sk/lay (sk/value-bar {:color :year}))
  (sk/coord :flip)
  (sk/plot {:title "Revenue (Horizontal)"})))


(deftest
 t41_l179
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v40_l173)))


(def
 v43_l188
 (def
  simulated
  (fn
   []
   (let
    [xs
     (range 0 10 0.5)
     ys
     (mapv
      (fn* [p1__80671#] (+ (* 3 p1__80671#) 5 (* 2 (- (rand) 0.5))))
      xs)]
    (tc/dataset {:x xs, :y ys})))))


(def
 v44_l194
 (->
  (simulated)
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Simulated: y = 3x + 5 + noise"})))


(deftest
 t45_l199
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and
       (map? attrs)
       (number? (:width attrs))
       (number? (:height attrs))))
     (let [body (nth v 2)] (and (vector? body) (= :g (first body))))))
   v44_l194)))
