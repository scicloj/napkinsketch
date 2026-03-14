(ns
 napkinsketch-book.layers-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l18
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v6_l25
 (->
  {:x [1 2 3 4 5 6 7], :y [2 4 3 6 5 8 7]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/line))
  sk/plot))


(deftest
 t7_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 7 (:points s)) (= 1 (:lines s)))))
   v6_l25)))


(def
 v9_l39
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t10_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v9_l39)))


(def
 v12_l52
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  sk/plot))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"setosa"} (:texts s)))))
   v12_l52)))


(def
 v15_l67
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm))
  sk/plot))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v15_l67)))


(def
 v18_l81
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Sepal: Length vs Width"})))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Sepal: Length vs Width"} (:texts s)))))
   v18_l81)))


(def
 v20_l92
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Petal: Length vs Width"})))


(deftest
 t21_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v20_l92)))


(def
 v23_l106
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  (sk/plot
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t24_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s))
      (some #{"Total Bill ($)"} (:texts s)))))
   v23_l106)))


(def
 v26_l124
 (def
  growth
  (tc/dataset
   {:day [1 2 3 4 5 1 2 3 4 5],
    :value [10 15 13 18 22 8 12 11 16 19],
    :group [:a :a :a :a :a :b :b :b :b :b]})))


(def
 v27_l129
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay (sk/line {:color :group}) (sk/point {:color :group}))
  (sk/plot {:title "Growth Over Time"})))


(deftest
 t28_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 10 (:points s))
      (= 2 (:lines s))
      (some #{"Growth Over Time"} (:texts s)))))
   v27_l129)))


(def
 v30_l145
 (def
  experiment
  (tc/dataset
   {:condition ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def
 v31_l151
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay
   (sk/point {:size 5})
   (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t32_l157
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v31_l151)))


(def
 v34_l165
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t35_l170
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v34_l165)))


(def
 v37_l178
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay (sk/lollipop) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t38_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v37_l178)))


(def
 v40_l193
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay (sk/step {:color :group}) (sk/point {:color :group}))
  (sk/plot {:title "Step Growth"})))


(deftest
 t41_l199
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v40_l193)))


(def
 v43_l207
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay
   (sk/point {:alpha 0.3, :jitter 5})
   (sk/summary {:color :species}))
  sk/plot))


(deftest
 t44_l213
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v43_l207)))
