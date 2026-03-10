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
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v7_l29)))


(def
 v10_l45
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t11_l51
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v10_l45)))


(def
 v13_l57
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t14_l63
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v13_l57)))


(def
 v16_l69
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t17_l75
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v16_l69)))


(def
 v19_l81
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}) (sk/lm {:color :sex}))
  sk/plot))


(deftest
 t20_l88
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v19_l81)))


(def
 v22_l97
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :shared})))


(deftest
 t23_l103
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v22_l97)))


(def
 v25_l107
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t26_l113
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v25_l107)))


(def
 v28_l121
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v29_l128 (:grid faceted-sk))


(deftest
 t30_l130
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v29_l128)))


(def v31_l132 (count (:panels faceted-sk)))


(deftest t32_l134 (is ((fn [n] (= 3 n)) v31_l132)))


(def
 v34_l138
 (mapv
  (fn* [p1__77322#] (select-keys p1__77322# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest t35_l141 (is ((fn [ps] (= 3 (count ps))) v34_l138)))


(def
 v37_l148
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v38_l150
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t39_l155
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v38_l150)))


(def v41_l167 (sk/pairs [:a :b :c :d]))


(deftest
 t42_l169
 (is
  ((fn [v] (= [[:a :b] [:a :c] [:a :d] [:b :c] [:b :d] [:c :d]] v))
   v41_l167)))


(def
 v43_l171
 (->
  iris
  (sk/view (sk/pairs cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t44_l176
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v43_l171)))


(def
 v46_l182
 (->
  (sk/distribution iris :sepal_length :sepal_width :petal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t47_l186
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v46_l182)))


(def
 v49_l190
 (->
  penguins
  (sk/view :species)
  (sk/facet :island)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t50_l196
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v49_l190)))


(def
 v52_l202
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
 t53_l211
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v52_l202)))
