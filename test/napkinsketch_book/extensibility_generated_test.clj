(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.impl.scale :as scale]
  [scicloj.napkinsketch.impl.coord :as coord]
  [scicloj.napkinsketch.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def
 v3_l59
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l86 (sk/histogram))


(deftest t6_l88 (is ((fn [m] (= :bin (:stat m))) v5_l86)))


(def v8_l92 (sk/bar))


(deftest t9_l94 (is ((fn [m] (= :count (:stat m))) v8_l92)))


(def v11_l98 (sk/point))


(deftest t12_l100 (is ((fn [m] (nil? (:stat m))) v11_l98)))


(def
 v14_l145
 (let
  [s
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/sketch)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t15_l152
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l145)))


(def
 v17_l216
 (def
  my-sketch
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v18_l222 (first (sk/sketch->figure my-sketch :svg {})))


(deftest t19_l224 (is ((fn [v] (= :svg v)) v18_l222)))


(def v21_l228 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v22_l230 (vector? my-figure))


(deftest t23_l232 (is ((fn [v] (true? v)) v22_l230)))


(def v25_l273 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v26_l275 (vector? my-membrane))


(deftest t27_l277 (is ((fn [v] (true? v)) v26_l275)))


(def
 v28_l279
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-sketch),
    :total-height (:total-height my-sketch)})))


(deftest t29_l283 (is ((fn [v] (= :svg v)) v28_l279)))


(def
 v31_l337
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t32_l343
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v31_l337)))
