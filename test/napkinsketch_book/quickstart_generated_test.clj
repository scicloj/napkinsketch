(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l36 (-> iris (sk/xkcd7-lay-point :sepal_length :sepal_width)))


(deftest
 t5_l39
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l36)))


(def
 v7_l55
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t8_l58
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l55)))


(def v10_l63 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/xkcd7-lay-point))


(deftest
 t11_l66
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v10_l63)))


(def
 v13_l73
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/xkcd7-lay-point "x" "y")))


(deftest
 t14_l76
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v13_l73)))


(def
 v16_l82
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t17_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v16_l82)))


(def v19_l96 (-> iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t20_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v19_l96)))


(def v22_l106 (-> iris (sk/xkcd7-lay-bar :species)))


(deftest
 t23_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v22_l106)))


(def
 v25_l115
 (-> iris (sk/xkcd7-lay-bar :species) (sk/xkcd7-coord :flip)))


(deftest
 t26_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v25_l115)))


(def
 v28_l124
 (->
  {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]}
  (sk/xkcd7-lay-line :x :y)))


(deftest
 t29_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v28_l124)))


(def v31_l134 (-> iris (sk/xkcd7-lay-boxplot :species :sepal_width)))


(deftest
 t32_l137
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l134)))


(def v34_l148 (-> iris (sk/xkcd7-view :sepal_length :sepal_width)))


(deftest
 t35_l151
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v34_l148)))


(def v37_l155 (-> iris (sk/xkcd7-view :species)))


(deftest
 t38_l158
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v37_l155)))


(def v40_l162 (-> iris (sk/xkcd7-view :sepal_length)))


(deftest
 t41_l165
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v40_l162)))


(def
 v43_l176
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t44_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l176)))


(def
 v46_l189
 (->
  iris
  (sk/xkcd7-lay-point :petal_length :petal_width {:color :species})
  (sk/xkcd7-options
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t47_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v46_l189)))


(def
 v49_l205
 (sk/arrange
  [(sk/xkcd7-lay-point
    iris
    :sepal_length
    :sepal_width
    {:color :species})
   (sk/xkcd7-lay-histogram iris :sepal_length {:color :species})]
  {:cols 2}))


(deftest t50_l209 (is ((fn [v] (vector? v)) v49_l205)))
