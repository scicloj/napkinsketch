(ns
 plotje-book.pose-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l46
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length :sepal-width)))


(deftest
 t4_l49
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v3_l46)))


(def
 v6_l54
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  kind/pprint))


(deftest
 t7_l58
 (is
  ((fn [v] (and (seq (:data v)) (= :sepal-length (:x (:mapping v)))))
   v6_l54)))


(def
 v9_l72
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})))


(deftest
 t10_l75
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v9_l72)))


(def
 v12_l79
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t13_l83
 (is ((fn [v] (= :species (:color (:mapping v)))) v12_l79)))


(def
 v15_l101
 (def
  multi-layer
  (pj/pose
   {:mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point} {:layer-type :smooth, :stat :linear-model}],
    :data (rdatasets/datasets-iris)})))


(def v16_l108 multi-layer)


(deftest
 t17_l110
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v16_l108)))


(def v19_l116 (kind/pprint multi-layer))


(deftest
 t20_l118
 (is
  ((fn
    [v]
    (and (= 2 (count (:layers v))) (= :species (:color (:mapping v)))))
   v19_l116)))


(def
 v22_l124
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t23_l129
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l124)))


(def
 v25_l137
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t26_l143
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))))
   v25_l137)))


(def v28_l151 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length)))


(deftest
 t29_l154
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v28_l151)))


(def
 v31_l160
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length) kind/pprint))


(deftest t32_l164 (is ((fn [v] (empty? (:layers v))) v31_l160)))


(def
 v34_l188
 (def
  two-panel
  (pj/pose
   {:layout {:direction :horizontal},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}],
    :data (rdatasets/datasets-iris)})))


(def v35_l197 two-panel)


(deftest
 t36_l199
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v35_l197)))


(def v38_l205 (kind/pprint two-panel))


(deftest
 t39_l207
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:poses v)))
     (= :horizontal (get-in v [:layout :direction]))))
   v38_l205)))


(def
 v41_l214
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
 t42_l222
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v41_l214)))


(def
 v44_l230
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
 t45_l239
 (is
  ((fn
    [v]
    (and
     (= :vertical (get-in v [:layout :direction]))
     (= 1 (count (:poses v)))
     (= 2 (count (:poses (first (:poses v)))))
     (= :horizontal (get-in v [:poses 0 :layout :direction]))))
   v44_l230)))
