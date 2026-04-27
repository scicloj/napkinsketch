(ns
 plotje-book.quickstart-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l32
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l35
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v3_l32)))


(def v6_l49 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-point :x :y)))


(deftest
 t7_l52
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v6_l49)))


(def v9_l57 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t10_l60
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v9_l57)))


(def
 v12_l67
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (pj/lay-point "x" "y")))


(deftest
 t13_l70
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v12_l67)))


(def
 v15_l76
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t16_l79
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"setosa"} (:texts s))
      (some #{"sepal length"} (:texts s)))))
   v15_l76)))


(def
 v18_l90
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t19_l93
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)) (zero? (:points s)))))
   v18_l90)))


(def v21_l100 (-> (rdatasets/datasets-iris) (pj/lay-bar :species)))


(deftest
 t22_l103
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l100)))


(def
 v24_l109
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species) (pj/coord :flip)))


(deftest
 t25_l113
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v24_l109)))


(def
 v27_l118
 (-> {:x [1 2 3 4 5 6 7 8], :y [3 5 4 7 6 8 7 9]} (pj/lay-line :x :y)))


(deftest
 t28_l122
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:lines s)) (zero? (:points s)))))
   v27_l118)))


(def
 v30_l128
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t31_l131
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:lines s)))))
   v30_l128)))


(def
 v33_l144
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length :sepal-width)))


(deftest
 t34_l147
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v33_l144)))


(def v36_l151 (-> (rdatasets/datasets-iris) (pj/pose :species)))


(deftest
 t37_l154
 (is ((fn [v] (= 3 (:polygons (pj/svg-summary v)))) v36_l151)))


(def v39_l158 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length)))


(deftest
 t40_l161
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v39_l158)))


(def
 v42_l172
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t43_l177
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v42_l172)))


(def
 v45_l185
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
 t46_l192
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Petals"} (:texts s))
      (some #{"Petal Length (cm)"} (:texts s)))))
   v45_l185)))


(def
 v48_l201
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


(deftest t49_l205 (is ((fn [v] (pj/pose? v)) v48_l201)))


(def
 v51_l211
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/save "/tmp/iris-scatter.svg")))


(deftest
 t52_l215
 (is ((fn [p] (and (string? p) (.endsWith p ".svg"))) v51_l211)))
