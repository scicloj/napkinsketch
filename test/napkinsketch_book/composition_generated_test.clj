(ns
 napkinsketch-book.composition-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v2_l26 (def iris (rdatasets/datasets-iris)))


(def
 v4_l35
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (sk/lay-point :petal-length :petal-width {:color :species}))]))


(deftest
 t5_l39
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v4_l35)))


(def
 v7_l45
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (sk/lay-point :petal-length :petal-width {:color :species}))]
  {:direction :vertical}))


(deftest
 t8_l50
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v7_l45)))


(def
 v10_l56
 (sk/arrange
  [(-> iris (sk/lay-point :sepal-length :sepal-width))
   (-> iris (sk/lay-point :petal-length :petal-width))]
  {:weights [2 1]}))


(deftest
 t11_l61
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v10_l56)))


(def
 v13_l70
 (def
  bare-composite
  {:data iris,
   :layout {:direction :horizontal, :weights [1 1]},
   :frames
   [{:mapping {:x :sepal-length, :y :sepal-width},
     :layers [{:layer-type :point}]}
    {:mapping {:x :petal-length, :y :petal-width},
     :layers [{:layer-type :point}]}]}))


(def v14_l78 bare-composite)


(deftest
 t15_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v14_l78)))


(def
 v17_l97
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


(def v18_l106 shared-x)


(deftest
 t19_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v18_l106)))


(def
 v21_l122
 (def
  marginal
  {:data iris,
   :share-scales #{:x},
   :layout {:direction :vertical, :weights [1 3]},
   :frames
   [{:mapping {:x :sepal-length}, :layers [{:layer-type :density}]}
    {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
     :layers [{:layer-type :point}]}]}))


(def v22_l131 marginal)


(deftest
 t23_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v22_l131)))


(def
 v25_l154
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


(def v26_l168 dashboard)


(deftest
 t27_l170
 (is ((fn [v] (= 4 (:panels (sk/svg-summary v)))) v26_l168)))
