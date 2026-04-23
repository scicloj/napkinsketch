(ns
 napkinsketch-book.frame-model-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t4_l33
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l30)))


(def
 v6_l38
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  kind/pprint))


(deftest
 t7_l42
 (is
  ((fn [v] (and (seq (:data v)) (= :sepal-length (:x (:mapping v)))))
   v6_l38)))


(def
 v9_l56
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})))


(deftest
 t10_l59
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v9_l56)))


(def
 v12_l63
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t13_l67
 (is ((fn [v] (= :species (:color (:mapping v)))) v12_l63)))


(def
 v15_l81
 (def
  multi-layer
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}]})))


(def v16_l88 multi-layer)


(deftest
 t17_l90
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v16_l88)))


(def v19_l96 (kind/pprint multi-layer))


(deftest
 t20_l98
 (is
  ((fn
    [v]
    (and (= 2 (count (:layers v))) (= :species (:color (:mapping v)))))
   v19_l96)))


(def
 v22_l109
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t23_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v22_l109)))


(def
 v25_l126
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t26_l132
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))))
   v25_l126)))


(def v28_l140 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t29_l143
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v28_l140)))


(def
 v31_l149
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length) kind/pprint))


(deftest t32_l153 (is ((fn [v] (empty? (:layers v))) v31_l149)))


(def
 v34_l169
 (def
  two-panel
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :layout {:direction :horizontal},
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v35_l178 two-panel)


(deftest
 t36_l180
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v35_l178)))


(def v38_l186 (kind/pprint two-panel))


(deftest
 t39_l188
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:frames v)))
     (= :horizontal (get-in v [:layout :direction]))))
   v38_l186)))


(def
 v41_l195
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width {:color :species})
    sk/lay-point)
   (->
    (rdatasets/datasets-iris)
    (sk/frame :petal-length :petal-width {:color :species})
    sk/lay-point)]))


(deftest
 t42_l203
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v41_l195)))


(def
 v44_l211
 (->
  (sk/arrange
   [(->
     (rdatasets/datasets-iris)
     (sk/frame :sepal-length :sepal-width {:color :species})
     sk/lay-point)
    (->
     (rdatasets/datasets-iris)
     (sk/frame :petal-length :petal-width {:color :species})
     sk/lay-point)])
  kind/pprint))


(deftest
 t45_l220
 (is
  ((fn
    [v]
    (and
     (= :vertical (get-in v [:layout :direction]))
     (= 1 (count (:frames v)))
     (= 2 (count (:frames (first (:frames v)))))
     (= :horizontal (get-in v [:frames 0 :layout :direction]))))
   v44_l211)))
