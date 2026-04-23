(ns
 napkinsketch-book.composition-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v2_l27 (def iris (rdatasets/datasets-iris)))


(def
 v4_l36
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (sk/lay-point :petal-length :petal-width {:color :species}))]))


(deftest
 t5_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v4_l36)))


(def
 v7_l47
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (sk/lay-point :petal-length :petal-width {:color :species}))]
  {:cols 1}))


(deftest
 t8_l52
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v7_l47)))


(def
 v10_l70
 (def
  weighted
  {:data iris,
   :layout {:direction :horizontal, :weights [2 1]},
   :frames
   [{:mapping {:x :sepal-length, :y :sepal-width},
     :layers [{:layer-type :point}]}
    {:mapping {:x :petal-length, :y :petal-width},
     :layers [{:layer-type :point}]}]}))


(def v11_l78 weighted)


(deftest
 t12_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v11_l78)))


(def
 v14_l97
 (def
  shared-x
  {:data iris,
   :share-scales #{:x},
   :layout {:direction :horizontal, :weights [1 1]},
   :frames
   [{:mapping {:x :sepal-length, :y :sepal-width},
     :layers [{:layer-type :point}]}
    {:mapping {:x :sepal-length, :y :petal-length},
     :layers [{:layer-type :point}]}]}))


(def v15_l106 shared-x)


(deftest
 t16_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v15_l106)))


(def
 v18_l122
 (def
  marginal
  {:data iris,
   :share-scales #{:x},
   :layout {:direction :vertical, :weights [1 3]},
   :frames
   [{:mapping {:x :sepal-length}, :layers [{:layer-type :density}]}
    {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
     :layers [{:layer-type :point}]}]}))


(def v19_l131 marginal)


(deftest
 t20_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v19_l131)))


(def
 v22_l154
 (def
  dashboard
  {:data iris,
   :layout {:direction :vertical, :weights [1 1]},
   :frames
   [{:layout {:direction :horizontal, :weights [1 1]},
     :frames
     [{:mapping {:x :sepal-length}, :layers [{:layer-type :histogram}]}
      {:mapping {:x :species, :y :sepal-width, :color :species},
       :layers [{:layer-type :boxplot}]}]}
    {:layout {:direction :horizontal, :weights [1 1]},
     :frames
     [{:mapping {:x :petal-length, :y :petal-width, :color :species},
       :layers [{:layer-type :point}]}
      {:mapping {:x :petal-length, :color :species},
       :layers [{:layer-type :density}]}]}]}))


(def v23_l168 dashboard)


(deftest
 t24_l170
 (is ((fn [v] (= 4 (:panels (sk/svg-summary v)))) v23_l168)))
