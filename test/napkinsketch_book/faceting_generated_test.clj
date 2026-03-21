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
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t8_l38
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v7_l32)))


(def
 v10_l50
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species :col)
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t11_l56
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v10_l50)))


(def
 v13_l64
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}))
  sk/plot))


(deftest
 t14_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v13_l64)))


(def
 v16_l78
 (->
  iris
  (sk/view :sepal_length)
  (sk/facet :species)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t17_l84
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v16_l78)))


(def
 v19_l92
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/facet-grid :smoker :sex)
  (sk/lay (sk/point {:color :sex}) (sk/lm {:color :sex}))
  sk/plot))


(deftest
 t20_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v19_l92)))


(def
 v22_l111
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :shared})))


(deftest
 t23_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v22_l111)))


(def
 v25_l123
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/facet :species)
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:scales :free-y})))


(deftest
 t26_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v25_l123)))


(def
 v28_l139
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v29_l146 (:grid faceted-sk))


(deftest
 t30_l148
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v29_l146)))


(def v31_l150 (count (:panels faceted-sk)))


(deftest t32_l152 (is ((fn [n] (= 3 n)) v31_l150)))


(def v34_l156 (:panels faceted-sk))


(deftest t35_l158 (is ((fn [ps] (= 3 (count ps))) v34_l156)))


(def
 v37_l165
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v38_l167
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t39_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 16 (:panels s)) (= 1800 (:points s)))))
   v38_l167)))


(def
 v41_l187
 (->
  (sk/distribution iris :sepal_length :sepal_width :petal_length)
  (sk/lay (sk/histogram {:color :species}))
  sk/plot))


(deftest
 t42_l191
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v41_l187)))


(def
 v44_l197
 (->
  penguins
  (sk/view :species)
  (sk/facet :island)
  (sk/lay (sk/bar {:color :species}))
  sk/plot))


(deftest
 t45_l203
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 9 (:polygons s)))))
   v44_l197)))


(def
 v47_l211
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
 t48_l220
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
   v47_l211)))
