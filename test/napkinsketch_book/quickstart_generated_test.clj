(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l35
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l38 iris)


(deftest
 t5_l40
 (is ((fn [v] (= 150 (count (tablecloth.api/rows v)))) v4_l38)))


(def v7_l51 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t8_l54
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l51)))


(def
 v10_l58
 (->
  [{:x 1, :y 2, :g "a"} {:x 3, :y 4, :g "a"} {:x 5, :y 6, :g "b"}]
  (sk/lay-point :x :y {:color :g})))


(deftest
 t11_l61
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v10_l58)))


(def
 v13_l65
 (->
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})
  (sk/lay-point :total_bill :tip)))


(deftest
 t14_l69
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v13_l65)))


(def
 v16_l78
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t17_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v16_l78)))


(def
 v19_l89
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t20_l93
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v19_l89)))


(def v22_l114 (-> iris (sk/view :sepal_length :sepal_width)))


(deftest
 t23_l117
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v22_l114)))


(def v25_l123 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t26_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v25_l123)))


(def
 v28_l134
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t29_l137
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v28_l134)))


(def
 v31_l146
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t32_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v31_l146)))


(def v34_l159 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t35_l162
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v34_l159)))


(def v37_l171 (-> iris (sk/lay-bar :species)))


(deftest
 t38_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v37_l171)))


(def v40_l182 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t41_l186
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v40_l182)))


(def
 v43_l193
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (sk/lay-line :x :y)))


(deftest
 t44_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v43_l193)))


(def
 v46_l205
 (->
  iris
  (sk/lay-point :petal_length :petal_width {:color :species})
  (sk/options
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t47_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v46_l205)))
