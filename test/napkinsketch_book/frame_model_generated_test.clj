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
 v15_l86
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t16_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v15_l86)))


(def
 v18_l97
 (kind/pprint
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}]})))


(deftest
 t19_l104
 (is
  ((fn
    [v]
    (and (= 2 (count (:layers v))) (= :species (:color (:mapping v)))))
   v18_l97)))


(def v21_l116 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t22_l119
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v21_l116)))


(def
 v24_l125
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length) kind/pprint))


(deftest t25_l129 (is ((fn [v] (empty? (:layers v))) v24_l125)))


(def
 v27_l144
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
 t28_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v27_l144)))


(def
 v30_l159
 (kind/pprint
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :layout {:direction :horizontal},
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width, :color :species},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(deftest
 t31_l168
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:frames v)))
     (= :horizontal (get-in v [:layout :direction]))))
   v30_l159)))
