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
  (sk/prepare-frame
   {:data iris,
    :layout {:direction :horizontal, :weights [2 1]},
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v12_l85 weighted)


(deftest
 t13_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v12_l85)))


(def
 v15_l104
 (def
  shared-x
  (sk/prepare-frame
   {:data iris,
    :share-scales #{:x},
    :layout {:direction :horizontal, :weights [1 1]},
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :sepal-length, :y :petal-length},
      :layers [{:layer-type :point}]}]})))


(def v16_l114 shared-x)


(deftest
 t17_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v16_l114)))


(def
 v19_l130
 (def
  marginal
  (sk/prepare-frame
   {:data iris,
    :share-scales #{:x},
    :layout {:direction :vertical, :weights [1 3]},
    :frames
    [{:mapping {:x :sepal-length}, :layers [{:layer-type :density}]}
     {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v20_l140 marginal)


(deftest
 t21_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v20_l140)))


(def
 v23_l163
 (def
  dashboard
  (sk/prepare-frame
   {:data iris,
    :layout {:direction :vertical, :weights [1 1]},
    :frames
    [{:layout {:direction :horizontal, :weights [1 1]},
      :frames
      [{:mapping {:x :sepal-length},
        :layers [{:layer-type :histogram}]}
       {:mapping {:x :species, :y :sepal-width, :color :species},
        :layers [{:layer-type :boxplot}]}]}
     {:layout {:direction :horizontal, :weights [1 1]},
      :frames
      [{:mapping {:x :petal-length, :y :petal-width, :color :species},
        :layers [{:layer-type :point}]}
       {:mapping {:x :petal-length, :color :species},
        :layers [{:layer-type :density}]}]}]})))


(def v24_l178 dashboard)


(deftest
 t25_l180
 (is ((fn [v] (= 4 (:panels (sk/svg-summary v)))) v24_l178)))
