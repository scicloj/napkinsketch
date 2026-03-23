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
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t8_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v7_l32)))


(def
 v10_l48
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species :col)))


(deftest
 t11_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v10_l48)))


(def
 v13_l60
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t14_l64
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v13_l60)))


(def
 v16_l72
 (->
  iris
  (sk/lay-histogram :sepal_length {:color :species})
  (sk/facet :species)))


(deftest
 t17_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v16_l72)))


(def
 v19_l84
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/lay-lm {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t20_l89
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v19_l84)))


(def
 v22_l101
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :shared})))


(deftest
 t23_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v22_l101)))


(def
 v25_l112
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :free-y})))


(deftest
 t26_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v25_l112)))


(def
 v28_l127
 (def
  faceted-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/facet :species)
   sk/sketch)))


(def v29_l133 (:grid faceted-sk))


(deftest
 t30_l135
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v29_l133)))


(def v31_l137 (count (:panels faceted-sk)))


(deftest t32_l139 (is ((fn [n] (= 3 n)) v31_l137)))


(def v34_l143 (:panels faceted-sk))


(deftest t35_l145 (is ((fn [ps] (= 3 (count ps))) v34_l143)))


(def
 v37_l152
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v38_l154
 (->
  iris
  (sk/view (sk/cross cols cols))
  (sk/lay-point {:color :species})))


(deftest
 t39_l158
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 16 (:panels s)) (= 2400 (:points s)))))
   v38_l154)))


(def
 v41_l171
 (->
  (sk/distribution iris :sepal_length :sepal_width :petal_length)
  (sk/lay-histogram {:color :species})))


(deftest
 t42_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v41_l171)))


(def
 v44_l180
 (->
  penguins
  (sk/lay-bar :species {:color :species})
  (sk/facet :island)))


(deftest
 t45_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 9 (:polygons s)))))
   v44_l180)))


(def
 v47_l192
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/labs
   {:title "Iris by Species",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t48_l199
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
   v47_l192)))
