(ns
 napkinsketch-book.faceting-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l18
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l21
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l24
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v7_l32
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})))


(deftest
 t8_l37
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v7_l32)))


(def
 v10_l49
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay-point {:color :species})))


(deftest
 t11_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v10_l49)))


(def
 v13_l62
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay-point {:color :sex})))


(deftest
 t14_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v13_l62)))


(def
 v16_l75
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay-histogram {:color :species})))


(deftest
 t17_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v16_l75)))


(def
 v19_l88
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay-point {:color :sex})
  (sk/lay-lm {:color :sex})))


(deftest
 t20_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v19_l88)))


(def
 v22_l106
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})
  (sk/options {:scales :shared})))


(deftest
 t23_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v22_l106)))


(def
 v25_l118
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})
  (sk/options {:scales :free-y})))


(deftest
 t26_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v25_l118)))


(def
 v28_l134
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay-point {:color :species})
   sk/sketch)))


(def v29_l141 (:grid faceted-sk))


(deftest
 t30_l143
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v29_l141)))


(def v31_l145 (count (:panels faceted-sk)))


(deftest t32_l147 (is ((fn [n] (= 3 n)) v31_l145)))


(def v34_l151 (:panels faceted-sk))


(deftest t35_l153 (is ((fn [ps] (= 3 (count ps))) v34_l151)))


(def
 v37_l160
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v38_l162
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay-point {:color :species})))


(deftest
 t39_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 16 (:panels s)) (= 2400 (:points s)))))
   v38_l162)))


(def
 v41_l179
 (->
  (sk/distribution iris :sepal_length :sepal_width :petal_length)
  (sk/lay-histogram {:color :species})))


(deftest
 t42_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v41_l179)))


(def
 v44_l188
 (->
  penguins
  (sk/view :species)
  (sk/facet :island)
  (sk/lay-bar {:color :species})))


(deftest
 t45_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 9 (:polygons s)))))
   v44_l188)))


(def
 v47_l201
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay-point {:color :species})
  (sk/labs
   {:title "Iris by Species",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t48_l209
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
   v47_l201)))
