(ns
 plotje-book.quickstart-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l33
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v3_l30)))


(def v6_l47 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-point :x :y)))


(deftest
 t7_l50
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v6_l47)))


(def v9_l55 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t10_l58
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l55)))


(def
 v12_l69
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (pj/lay-point "x" "y")))


(deftest
 t13_l72
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v12_l69)))


(def
 v15_l78
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t16_l81
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v15_l78)))


(def
 v18_l92
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t19_l95
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v18_l92)))


(def v21_l102 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t22_l105
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l102)))


(def
 v24_l111
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t25_l115
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v24_l111)))


(def
 v27_l120
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (pj/lay-line :x :y)))


(deftest
 t28_l124
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v27_l120)))


(def
 v30_l130
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t31_l133
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v30_l130)))


(def
 v33_l146
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length :sepal-width)))


(deftest
 t34_l149
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v33_l146)))


(def v36_l153 (-> (rdatasets/datasets-iris) (pj/pose :species)))


(deftest
 t37_l156
 (is ((fn [v] (= 3 (:polygons (pj/svg-summary v)))) v36_l153)))


(def v39_l160 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length)))


(deftest
 t40_l163
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v39_l160)))


(def
 v42_l174
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t43_l179
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v42_l174)))


(def
 v45_l187
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :petal-length :petal-width {:color :species})
  (pj/options
   {:width 500,
    :height 350,
    :title "Iris Petals",
    :x-label "Petal Length (cm)",
    :y-label "Petal Width (cm)"})))


(deftest
 t46_l194
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v45_l187)))


(def
 v48_l203
 (pj/arrange
  [(pj/lay-point
    (rdatasets/datasets-iris)
    :sepal-length
    :sepal-width
    {:color :species})
   (pj/lay-histogram
    (rdatasets/datasets-iris)
    :sepal-length
    {:color :species})]
  {:cols 2}))


(deftest t49_l207 (is ((fn [v] (pj/pose? v)) v48_l203)))


(def
 v51_l220
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  pj/plot))


(deftest
 t52_l224
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v51_l220)))


(def
 v54_l232
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  pj/plot
  kind/pprint))


(deftest
 t55_l237
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v54_l232)))


(def
 v57_l243
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/save "/tmp/iris-scatter.svg")))


(deftest
 t58_l247
 (is ((fn [p] (and (string? p) (.endsWith p ".svg"))) v57_l243)))
