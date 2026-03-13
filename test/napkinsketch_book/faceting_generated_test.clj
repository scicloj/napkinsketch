(ns
 napkinsketch-book.faceting-generated-test
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
 v5_l21
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v7_l29
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t8_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v7_l29)))


(def
 v10_l47
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t11_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v10_l47)))


(def
 v13_l61
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t14_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v13_l61)))


(def
 v16_l75
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t17_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v16_l75)))


(def
 v19_l89
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}) (sk/lm {:color :sex}))
  sk/plot))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v19_l89)))


(def
 v22_l108
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :shared})))


(deftest
 t23_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v22_l108)))


(def
 v25_l120
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t26_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v25_l120)))


(def
 v28_l136
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v29_l143 (:grid faceted-sk))


(deftest
 t30_l145
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v29_l143)))


(def v31_l147 (count (:panels faceted-sk)))


(deftest t32_l149 (is ((fn [n] (= 3 n)) v31_l147)))


(def
 v34_l153
 (mapv
  (fn* [p1__303088#] (select-keys p1__303088# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest t35_l156 (is ((fn [ps] (= 3 (count ps))) v34_l153)))


(def
 v37_l163
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v38_l165
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t39_l170
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 16 (:panels s)) (= 1800 (:points s)))))
   v38_l165)))


(def v41_l184 (sk/pairs [:a :b :c :d]))


(deftest
 t42_l186
 (is
  ((fn [v] (= [[:a :b] [:a :c] [:a :d] [:b :c] [:b :d] [:c :d]] v))
   v41_l184)))


(def
 v43_l188
 (->
  iris
  (sk/view (sk/pairs cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t44_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:panels s)) (= 900 (:points s)))))
   v43_l188)))


(def
 v46_l201
 (->
  (sk/distribution iris :sepal_length :sepal_width :petal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t47_l205
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v46_l201)))


(def
 v49_l211
 (->
  penguins
  (sk/view :species)
  (sk/facet :island)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t50_l217
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 9 (:polygons s)))))
   v49_l211)))


(def
 v52_l225
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/labs
   {:title "Iris by Species",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})
  sk/plot))


(deftest
 t53_l234
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v52_l225)))
