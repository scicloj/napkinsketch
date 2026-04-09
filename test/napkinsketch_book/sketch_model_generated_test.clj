(ns
 napkinsketch-book.sketch-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l33
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l30)))


(def
 v6_l43
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  kind/pprint))


(deftest
 t7_l47
 (is
  ((fn
    [v]
    (and
     (:data v)
     (vector? (:views v))
     (empty? (:layers v))
     (seq (:layers (first (:views v))))))
   v6_l43)))


(def
 v9_l72
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t10_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l72)))


(def
 v12_l95
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t13_l98
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l95)))


(def v15_l102 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t16_l105
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v15_l102)))


(def
 v18_l118
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Iris by Species"})))


(deftest
 t19_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v18_l118)))
