(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.render.scene :as scene]
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
   (fn* [p1__386859#] (select-keys p1__386859# [:x :y :mark :color]))
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
 v11_l63
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style])))


(deftest t12_l67 (is ((fn [m] (= :point (:mark m))) v11_l63)))


(def v14_l78 (def my-sketch (sk/sketch views)))


(def v15_l80 (sort (keys my-sketch)))


(deftest t16_l82 (is ((fn [ks] (every? keyword? ks)) v15_l80)))


(def v18_l90 (sort (keys (first (:panels my-sketch)))))


(deftest
 t19_l92
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v18_l90)))


(def
 v21_l101
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t22_l105
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v21_l101)))


(def v24_l142 (def my-scene (scene/sketch->scene my-sketch)))


(def v25_l144 (vector? my-scene))


(deftest t26_l146 (is ((fn [v] (true? v)) v25_l144)))


(def v27_l148 (count my-scene))


(deftest t28_l150 (is ((fn [n] (pos? n)) v27_l148)))


(def v30_l160 (def my-figure (sk/render-figure my-sketch :svg {})))


(def v31_l162 (first my-figure))


(deftest t32_l164 (is ((fn [v] (= :svg v)) v31_l162)))
