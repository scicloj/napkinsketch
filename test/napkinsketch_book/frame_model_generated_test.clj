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
  (sk/frame :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t7_l44
 (is
  ((fn [v] (and (seq (:data v)) (= :sepal-length (:x (:mapping v)))))
   v6_l40)))


(def
 v9_l69
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t10_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l69)))


(def
 v12_l92
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t13_l95
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l92)))


(def v15_l99 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t16_l102
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v15_l99)))


(def
 v18_l116
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris by Species"})))


(deftest
 t19_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v18_l116)))
