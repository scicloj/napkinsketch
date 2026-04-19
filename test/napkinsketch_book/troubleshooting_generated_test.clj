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
 v12_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:tooltip true})))


(deftest
 t13_l140
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l136)))
