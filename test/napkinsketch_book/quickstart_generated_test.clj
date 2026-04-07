(ns
 napkinsketch-book.quickstart-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (def iris (rdatasets/datasets-iris)))


(def v4_l35 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t5_l38
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l35)))


(def v7_l52 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t8_l55
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l52)))


(def v10_l60 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t11_l63
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v10_l60)))


(def
 v13_l70
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/lay-point "x" "y")))


(deftest
 t14_l73
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v13_l70)))


(def
 v16_l79
 (-> iris (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t17_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v16_l79)))


(def v19_l93 (-> iris (sk/lay-histogram :sepal-length)))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v19_l93)))


(def v22_l103 (-> iris (sk/lay-bar :species)))


(deftest
 t23_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v22_l103)))


(def v25_l112 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t26_l116
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v25_l112)))


(def
 v28_l121
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (sk/lay-line :x :y)))


(deftest
 t29_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v28_l121)))


(def v31_l131 (-> iris (sk/lay-boxplot :species :sepal-width)))


(deftest
 t32_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l131)))


(def v34_l145 (-> iris (sk/view :sepal-length :sepal-width)))


(deftest
 t35_l148
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v34_l145)))


(def v37_l152 (-> iris (sk/view :species)))


(deftest
 t38_l155
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v37_l152)))


(def v40_l159 (-> iris (sk/view :sepal-length)))


(deftest
 t41_l162
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v40_l159)))


(def
 v43_l173
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t44_l178
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l173)))


(def
 v46_l186
 (->
  iris
  (sk/lay-point :petal-length :petal-width {:color :species})
  (sk/options
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t47_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v46_l186)))


(def
 v49_l202
 (sk/arrange
  [(sk/lay-point iris :sepal-length :sepal-width {:color :species})
   (sk/lay-histogram iris :sepal-length {:color :species})]
  {:cols 2}))


(deftest t50_l206 (is ((fn [v] (vector? v)) v49_l202)))
