(ns
 napkinsketch-book.scatter-gallery-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l14
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l17
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l20
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v7_l27
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t8_l32
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
   v7_l27)))


(def
 v10_l43
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t11_l48
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v10_l43)))


(def
 v13_l57
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t14_l62
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v13_l57)))


(def
 v16_l71
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t17_l77
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
   v16_l71)))


(def
 v19_l88
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}))
  sk/plot))


(deftest
 t20_l93
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v19_l88)))


(def
 v22_l102
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  sk/plot))


(deftest
 t23_l108
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v22_l102)))


(def
 v25_l117
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay (sk/point {:color :origin}))
  sk/plot))


(deftest
 t26_l122
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v25_l117)))


(def
 v28_l131
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color "#E74C3C"}))
  sk/plot))


(deftest
 t29_l136
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v28_l131)))


(def
 v31_l145
 (->
  {:x [1 5 9], :y [2 8 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t32_l150
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v31_l145)))


(def
 v34_l159
 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t35_l164
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v34_l159)))


(def
 v37_l173
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day}))
  (sk/plot
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t38_l181
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (let
      [attrs (second v)]
      (and (map? attrs) (>= (:width attrs) 700)))))
   v37_l173)))


(def
 v40_l192
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day, :size :size}))
  sk/plot))


(deftest
 t41_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v40_l192)))


(def
 v43_l203
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :day, :size :size, :alpha 0.6}))
  sk/plot))


(deftest
 t44_l208
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v43_l203)))


(def
 v46_l217
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/point {:jitter true}))
  sk/plot))


(deftest
 t47_l222
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v46_l217)))


(def
 v49_l228
 (->
  iris
  (sk/view [[:species :sepal_width]])
  (sk/lay (sk/point {:jitter 10, :alpha 0.5}))
  sk/plot))


(deftest
 t50_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v49_l228)))


(def
 v52_l242
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :petal_length}))
  sk/plot))


(deftest
 t53_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v52_l242)))


(def
 v55_l254
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :petal_length, :size :petal_width, :alpha 0.7}))
  sk/plot))


(deftest
 t56_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v55_l254)))
