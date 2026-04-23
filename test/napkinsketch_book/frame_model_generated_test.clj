(ns
 napkinsketch-book.frame-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l30
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l27)))


(def
 v6_l40
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l43
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l40)))


(def
 v9_l49
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t10_l53
 (is
  ((fn [v] (and (seq (:data v)) (= :sepal-length (:x (:mapping v)))))
   v9_l49)))


(def
 v12_l78
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t13_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v12_l78)))


(def
 v15_l101
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t16_l104
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v15_l101)))


(def v18_l108 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t19_l111
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v18_l108)))


(def
 v21_l125
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris by Species"})))


(deftest
 t22_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v21_l125)))
