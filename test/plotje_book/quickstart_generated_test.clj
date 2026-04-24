(ns
 plotje-book.quickstart-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l32 (def iris (rdatasets/datasets-iris)))


(def v4_l34 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t5_l37
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l34)))


(def v7_l51 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t8_l54
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v7_l51)))


(def v10_l59 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t11_l62
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v10_l59)))


(def
 v13_l69
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/lay-point "x" "y")))


(deftest
 t14_l72
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v13_l69)))


(def
 v16_l78
 (-> iris (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t17_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v16_l78)))


(def v19_l92 (-> iris (sk/lay-histogram :sepal-length)))


(deftest
 t20_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v19_l92)))


(def v22_l102 (-> iris (sk/lay-bar :species)))


(deftest
 t23_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v22_l102)))


(def v25_l111 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t26_l115
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v25_l111)))


(def
 v28_l120
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (sk/lay-line :x :y)))


(deftest
 t29_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v28_l120)))


(def v31_l130 (-> iris (sk/lay-boxplot :species :sepal-width)))


(deftest
 t32_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v31_l130)))


(def v34_l146 (-> iris (sk/frame :sepal-length :sepal-width)))


(deftest
 t35_l149
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v34_l146)))


(def v37_l153 (-> iris (sk/frame :species)))


(deftest
 t38_l156
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v37_l153)))


(def v40_l160 (-> iris (sk/frame :sepal-length)))


(deftest
 t41_l163
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v40_l160)))


(def
 v43_l174
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t44_l179
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l174)))


(def
 v46_l187
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
 t47_l194
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v46_l187)))


(def
 v49_l203
 (sk/arrange
  [(sk/lay-point iris :sepal-length :sepal-width {:color :species})
   (sk/lay-histogram iris :sepal-length {:color :species})]
  {:cols 2}))


(deftest t50_l207 (is ((fn [v] (sk/frame? v)) v49_l203)))


(def
 v52_l213
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/save "/tmp/iris-scatter.svg")))


(deftest
 t53_l217
 (is ((fn [p] (and (string? p) (.endsWith p ".svg"))) v52_l213)))
