(ns
 plotje-book.composition-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l32
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width {:color :species}))]))


(deftest
 t4_l36
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v3_l32)))


(def
 v6_l43
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species}))
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width {:color :species}))]
  {:cols 1}))


(deftest
 t7_l48
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v6_l43)))


(def
 v9_l66
 (def
  weighted
  (pj/pose
   {:data (rdatasets/datasets-iris),
    :layout {:direction :horizontal, :weights [2 1]},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v11_l78 weighted)


(deftest
 t12_l80
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v11_l78)))


(def v14_l88 (kind/pprint weighted))


(deftest
 t15_l90
 (is
  ((fn
    [pose]
    (and
     (= [2 1] (get-in pose [:layout :weights]))
     (= 2 (count (:poses pose)))))
   v14_l88)))


(def
 v17_l107
 (def
  shared-x
  (pj/pose
   {:data (rdatasets/datasets-iris),
    :share-scales #{:x},
    :layout {:direction :horizontal, :weights [1 1]},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :sepal-length, :y :petal-length},
      :layers [{:layer-type :point}]}]})))


(def v18_l117 shared-x)


(deftest
 t19_l119
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v18_l117)))


(def
 v21_l133
 (def
  marginal
  (pj/pose
   {:data (rdatasets/datasets-iris),
    :share-scales #{:x},
    :layout {:direction :vertical, :weights [1 3]},
    :poses
    [{:mapping {:x :sepal-length}, :layers [{:layer-type :density}]}
     {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v22_l143 marginal)


(deftest
 t23_l145
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v22_l143)))


(def
 v25_l166
 (def
  dashboard
  (pj/pose
   {:data (rdatasets/datasets-iris),
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


(def v26_l181 dashboard)


(deftest
 t27_l183
 (is ((fn [v] (= 4 (:panels (pj/svg-summary v)))) v26_l181)))
