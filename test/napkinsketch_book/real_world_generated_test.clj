(ns
 napkinsketch-book.real-world-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l14
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v5_l19
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t6_l24
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
   v5_l19)))


(def
 v8_l33
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Bill Length vs Depth with Regression"})))


(deftest
 t9_l39
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v8_l33)))


(def
 v11_l46
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm))
  (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t12_l52
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v11_l46)))


(def
 v14_l59
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/bar {:color :species}))
  (sk/plot {:title "Species by Island"})))


(deftest
 t15_l64
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v14_l59)))


(def
 v17_l71
 (->
  penguins
  (sk/view [[:flipper_length_mm :body_mass_g]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Flipper Length vs Body Mass"})))


(deftest
 t18_l77
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v17_l71)))


(def
 v20_l84
 (->
  penguins
  (sk/view :body_mass_g)
  (sk/lay (sk/histogram {:color :species}))
  (sk/plot {:title "Body Mass Distribution"})))


(deftest
 t21_l89
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v20_l84)))


(def
 v23_l96
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v25_l101
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  (sk/plot
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t26_l108
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
   v25_l101)))


(def
 v28_l117
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :time}))
  (sk/plot {:title "Visits by Day and Meal Time"})))


(deftest
 t29_l122
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v28_l117)))


(def
 v31_l129
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :time}))
  (sk/plot {:title "Visits by Day (Stacked)"})))


(deftest
 t32_l134
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v31_l129)))


(def
 v34_l141
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :sex}))
  (sk/coord :flip)
  (sk/plot {:title "Day by Gender (Horizontal)"})))


(deftest
 t35_l147
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v34_l141)))


(def
 v37_l154
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v39_l159
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay (sk/point {:color :origin}) (sk/lm {:color :origin}))
  (sk/plot {:title "Horsepower vs MPG by Origin"})))


(deftest
 t40_l165
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v39_l159)))


(def
 v42_l172
 (->
  mpg
  (sk/view [[:displacement :mpg]])
  (sk/lay (sk/point {:color :origin}))
  (sk/plot {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t43_l177
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v42_l172)))


(def
 v45_l184
 (->
  mpg
  (sk/view :origin)
  (sk/lay (sk/bar))
  (sk/plot {:title "Cars by Origin"})))


(deftest
 t46_l189
 (is
  ((fn
    [v]
    (and
     (vector? v)
     (= :svg (first v))
     (map? (second v))
     (vector? (nth v 2))))
   v45_l184)))
