(ns
 napkinsketch-book.sketch-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l32
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l35
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l32)))


(def
 v6_l45
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  kind/pprint))


(deftest
 t7_l49
 (is
  ((fn
    [v]
    (and
     (:data v)
     (vector? (:views v))
     (empty? (:layers v))
     (seq (:layers (first (:views v))))))
   v6_l45)))


(def
 v9_l75
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t10_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l75)))


(def
 v12_l98
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t13_l101
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l98)))


(def v15_l105 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t16_l108
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v15_l105)))


(def
 v18_l123
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris by Species"})))


(deftest
 t19_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v18_l123)))
