(ns
 plotje-book.composition-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v2_l27 (def iris (rdatasets/datasets-iris)))


(def
 v4_l36
 (pj/arrange
  [(->
    iris
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (pj/lay-point :petal-length :petal-width {:color :species}))]))


(deftest
 t5_l40
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v4_l36)))


(def
 v7_l47
 (pj/arrange
  [(->
    iris
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    iris
    (pj/lay-point :petal-length :petal-width {:color :species}))]
  {:cols 1}))


(deftest
 t8_l52
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v7_l47)))


(def
 v10_l70
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


(def v12_l85 weighted)


(deftest
 t13_l87
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v12_l85)))


(def v15_l95 (kind/pprint weighted))


(deftest
 t16_l97
 (is
  ((fn
    [fr]
    (and
     (= [2 1] (get-in fr [:layout :weights]))
     (= 2 (count (:poses fr)))))
   v15_l95)))


(def
 v18_l114
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


(def v19_l124 shared-x)


(deftest
 t20_l126
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v19_l124)))


(def
 v22_l140
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


(def v23_l150 marginal)


(deftest
 t24_l152
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v23_l150)))


(def
 v26_l173
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


(def v27_l188 dashboard)


(deftest
 t28_l190
 (is ((fn [v] (= 4 (:panels (pj/svg-summary v)))) v27_l188)))
