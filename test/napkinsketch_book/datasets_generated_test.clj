(ns
 napkinsketch-book.datasets-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v4_l25 iris)


(deftest t5_l27 (is ((fn [ds] (= 150 (tc/row-count ds))) v4_l25)))


(def
 v7_l34
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def v8_l38 tips)


(deftest t9_l40 (is ((fn [ds] (= 244 (tc/row-count ds))) v8_l38)))


(def
 v11_l47
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def v12_l51 penguins)


(deftest t13_l53 (is ((fn [ds] (= 344 (tc/row-count ds))) v12_l51)))


(def
 v15_l60
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def v16_l64 mpg)


(deftest t17_l66 (is ((fn [ds] (= 398 (tc/row-count ds))) v16_l64)))
