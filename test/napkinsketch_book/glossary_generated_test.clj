(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.render.membrane :as membrane]
  [clojure.test :refer [deftest is]]))


(def
 v2_l13
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l22
 (def
  views
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay (sk/point {:color :species})))))


(def
 v5_l27
 (kind/pprint
  (mapv
   (fn* [p1__78398#] (select-keys p1__78398# [:x :y :mark :color]))
   views)))


(deftest
 t6_l30
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v5_l27)))


(def v8_l39 (sk/point {:color :species, :alpha 0.5}))


(deftest
 t9_l41
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l39)))


(def
 v11_l68
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style])))


(deftest t12_l72 (is ((fn [m] (= :point (:mark m))) v11_l68)))


(def v14_l83 (def my-sketch (sk/sketch views)))


(def v15_l85 (sort (keys my-sketch)))


(deftest t16_l87 (is ((fn [ks] (every? keyword? ks)) v15_l85)))


(def v18_l95 (sort (keys (first (:panels my-sketch)))))


(deftest
 t19_l97
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v18_l95)))


(def
 v21_l106
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t22_l110
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v21_l106)))


(def v24_l152 (def my-membrane (membrane/sketch->membrane my-sketch)))


(def v25_l154 (vector? my-membrane))


(deftest t26_l156 (is ((fn [v] (true? v)) v25_l154)))


(def v27_l158 (count my-membrane))


(deftest t28_l160 (is ((fn [n] (pos? n)) v27_l158)))


(def v30_l170 (def my-figure (sk/render-figure my-sketch :svg {})))


(def v31_l172 (first my-figure))


(deftest t32_l174 (is ((fn [v] (= :svg v)) v31_l172)))
