(ns
 plotje-book.pose-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l44
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length :sepal-width)))


(deftest
 t4_l47
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v3_l44)))


(def
 v6_l52
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  kind/pprint))


(deftest
 t7_l56
 (is
  ((fn [v] (and (seq (:data v)) (= :sepal-length (:x (:mapping v)))))
   v6_l52)))


(def
 v9_l70
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})))


(deftest
 t10_l73
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v9_l70)))


(def
 v12_l77
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t13_l81
 (is ((fn [v] (= :species (:color (:mapping v)))) v12_l77)))


(def
 v15_l99
 (def
  multi-layer
  (pj/pose
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :stat :linear-model}]})))


(def v16_l106 multi-layer)


(deftest
 t17_l108
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v16_l106)))


(def v19_l114 (kind/pprint multi-layer))


(deftest
 t20_l116
 (is
  ((fn
    [v]
    (and (= 2 (count (:layers v))) (= :species (:color (:mapping v)))))
   v19_l114)))


(def
 v22_l122
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t23_l127
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l122)))


(def
 v25_l135
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t26_l141
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))))
   v25_l135)))


(def v28_l149 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length)))


(deftest
 t29_l152
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v28_l149)))


(def
 v31_l158
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length) kind/pprint))


(deftest t32_l162 (is ((fn [v] (empty? (:layers v))) v31_l158)))


(def
 v34_l183
 (def
  two-panel
  (pj/pose
   {:data (rdatasets/datasets-iris),
    :layout {:direction :horizontal},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v35_l192 two-panel)


(deftest
 t36_l194
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v35_l192)))


(def v38_l200 (kind/pprint two-panel))


(deftest
 t39_l202
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:poses v)))
     (= :horizontal (get-in v [:layout :direction]))))
   v38_l200)))


(def
 v41_l209
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    pj/lay-point)
   (->
    (rdatasets/datasets-iris)
    (pj/pose :petal-length :petal-width {:color :species})
    pj/lay-point)]))


(deftest
 t42_l217
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v41_l209)))


(def
 v44_l225
 (->
  (pj/arrange
   [(->
     (rdatasets/datasets-iris)
     (pj/pose :sepal-length :sepal-width {:color :species})
     pj/lay-point)
    (->
     (rdatasets/datasets-iris)
     (pj/pose :petal-length :petal-width {:color :species})
     pj/lay-point)])
  kind/pprint))


(deftest
 t45_l234
 (is
  ((fn
    [v]
    (and
     (= :vertical (get-in v [:layout :direction]))
     (= 1 (count (:poses v)))
     (= 2 (count (:poses (first (:poses v)))))
     (= :horizontal (get-in v [:poses 0 :layout :direction]))))
   v44_l225)))
