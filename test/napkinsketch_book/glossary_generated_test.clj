(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l12
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l21
 (def
  views
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay (sk/point {:color :species})))))


(def
 v5_l26
 (kind/pprint
  (mapv
   (fn* [p1__78326#] (select-keys p1__78326# [:x :y :mark :color]))
   views)))


(deftest
 t6_l29
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v5_l26)))


(def v8_l38 (sk/point {:color :species, :alpha 0.5}))


(deftest
 t9_l40
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l38)))


(def
 v11_l69
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style])))


(deftest t12_l73 (is ((fn [m] (= :point (:mark m))) v11_l69)))


(def v14_l84 (def my-sketch (sk/sketch views)))


(def v15_l86 (sort (keys my-sketch)))


(deftest t16_l88 (is ((fn [ks] (every? keyword? ks)) v15_l86)))


(def v18_l96 (sort (keys (first (:panels my-sketch)))))


(deftest
 t19_l98
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v18_l96)))


(def
 v21_l107
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t22_l111
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v21_l107)))


(def v24_l153 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v25_l155 (vector? my-membrane))


(deftest t26_l157 (is ((fn [v] (true? v)) v25_l155)))


(def v27_l159 (count my-membrane))


(deftest t28_l161 (is ((fn [n] (pos? n)) v27_l159)))


(def v30_l171 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v31_l173 (first my-figure))


(deftest t32_l175 (is ((fn [v] (= :svg v)) v31_l173)))
