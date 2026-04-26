(ns
 plotje-book.composition-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v2_l25 (def iris (rdatasets/datasets-iris)))


(def
 v4_l34
 (pj/arrange
  [(->
    iris
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (pj/lay-point :petal-length :petal-width {:color :species}))]))


(deftest
 t5_l38
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v4_l34)))


(def
 v7_l45
 (pj/arrange
  [(->
    iris
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (pj/lay-point :petal-length :petal-width {:color :species}))]
  {:cols 1}))


(deftest
 t8_l50
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v7_l45)))


(def
 v10_l68
 (def
  weighted
  (pj/prepare-pose
   {:data iris,
    :layout {:direction :horizontal, :weights [2 1]},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v12_l83 weighted)


(deftest
 t13_l85
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v12_l83)))


(def v15_l93 (kind/pprint weighted))


(deftest
 t16_l95
 (is
  ((fn
    [fr]
    (and
     (= [2 1] (get-in fr [:layout :weights]))
     (= 2 (count (:poses fr)))))
   v15_l93)))


(def
 v18_l112
 (def
  shared-x
  (pj/prepare-pose
   {:data iris,
    :share-scales #{:x},
    :layout {:direction :horizontal, :weights [1 1]},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :sepal-length, :y :petal-length},
      :layers [{:layer-type :point}]}]})))


(def v19_l122 shared-x)


(deftest
 t20_l124
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v19_l122)))


(def
 v22_l138
 (def
  marginal
  (pj/prepare-pose
   {:data iris,
    :share-scales #{:x},
    :layout {:direction :vertical, :weights [1 3]},
    :poses
    [{:mapping {:x :sepal-length}, :layers [{:layer-type :density}]}
     {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v23_l148 marginal)


(deftest
 t24_l150
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v23_l148)))


(def
 v26_l171
 (def
  dashboard
  (pj/prepare-pose
   {:data iris,
    :layout {:direction :vertical, :weights [1 1]},
    :poses
    [{:layout {:direction :horizontal, :weights [1 1]},
      :poses
      [{:mapping {:x :sepal-length},
        :layers [{:layer-type :histogram}]}
       {:mapping {:x :species, :y :sepal-width, :color :species},
        :layers [{:layer-type :boxplot}]}]}
     {:layout {:direction :horizontal, :weights [1 1]},
      :poses
      [{:mapping {:x :petal-length, :y :petal-width, :color :species},
        :layers [{:layer-type :point}]}
       {:mapping {:x :petal-length, :color :species},
        :layers [{:layer-type :density}]}]}]})))


(def v27_l186 dashboard)


(deftest
 t28_l188
 (is ((fn [v] (= 4 (:panels (pj/svg-summary v)))) v27_l186)))
