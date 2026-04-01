(ns
 napkinsketch-book.troubleshooting-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (tc/column-names data/iris))


(deftest t4_l35 (is ((fn [v] (some #{:sepal_length} v)) v3_l33)))


(def v6_l51 (-> data/iris (sk/view :species :sepal_width)))


(deftest
 t7_l54
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l51)))


(def v9_l58 (-> data/iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t10_l61
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v9_l58)))


(def
 v12_l115
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:tooltip true})))


(deftest
 t13_l119
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l115)))
