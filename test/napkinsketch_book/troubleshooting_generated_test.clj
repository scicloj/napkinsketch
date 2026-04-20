(ns
 napkinsketch-book.troubleshooting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (tc/column-names (rdatasets/datasets-iris)))


(deftest t4_l35 (is ((fn [v] (some #{:sepal-length} v)) v3_l33)))


(def
 v6_l51
 (-> (rdatasets/datasets-iris) (sk/view :species :sepal-width)))


(deftest
 t7_l54
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v6_l51)))


(def
 v9_l58
 (-> (rdatasets/datasets-iris) (sk/lay-point :species :sepal-width)))


(deftest
 t10_l61
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v9_l58)))


(def
 v12_l102
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (sk/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t13_l105
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l102)))


(def
 v15_l128
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:alpha 0.1})
  (sk/scale :y :log)))


(deftest
 t16_l132
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v15_l128)))


(def
 v18_l189
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:tooltip true})))


(deftest
 t19_l193
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l189)))
