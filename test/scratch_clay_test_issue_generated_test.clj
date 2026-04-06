(ns
 scratch-clay-test-issue-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l11
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l16 nil)


(deftest t5_l18 (is ((fn [v] (= 42 v)) v4_l16)))


(def v7_l22 (kind/pprint {:a 1, :b 2}))


(deftest t8_l24 (is ((fn [m] (= 1 (:a m))) v7_l22)))


(def v10_l28 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t11_l31
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v10_l28)))


(def
 v13_l35
 (let
  [sk (-> iris (sk/lay-point :sepal_length :sepal_width))]
  (kind/pprint
   {:shared (:shared sk),
    :entries
    (mapv
     (fn* [p1__326051#] (dissoc p1__326051# :data))
     (:entries sk)),
    :methods (:methods sk)})))


(deftest t14_l40 (is ((fn [m] (= 1 (count (:entries m)))) v13_l35)))


(def v16_l44 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t17_l47
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v16_l44)))
