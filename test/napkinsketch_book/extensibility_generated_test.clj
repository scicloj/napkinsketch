(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.impl.scale :as scale]
  [scicloj.napkinsketch.impl.coord :as coord]
  [scicloj.napkinsketch.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def
 v3_l61
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l89 (method/histogram))


(deftest t6_l91 (is ((fn [m] (= :bin (:stat m))) v5_l89)))


(def v8_l95 (method/bar))


(deftest t9_l97 (is ((fn [m] (= :count (:stat m))) v8_l95)))


(def v11_l101 (method/point))


(deftest t12_l103 (is ((fn [m] (= :identity (:stat m))) v11_l101)))


(def
 v14_l148
 (let
  [s
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    sk/sketch)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t15_l155
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l148)))


(def
 v17_l219
 (def
  my-sketch
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay-point {:color :species})
   sk/sketch)))


(def v18_l225 (first (sk/sketch->figure my-sketch :svg {})))


(deftest t19_l227 (is ((fn [v] (= :svg v)) v18_l225)))


(def v21_l231 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v22_l233 (vector? my-figure))


(deftest t23_l235 (is ((fn [v] (true? v)) v22_l233)))


(def v25_l276 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v26_l278 (vector? my-membrane))


(deftest t27_l280 (is ((fn [v] (true? v)) v26_l278)))


(def
 v28_l282
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-sketch),
    :total-height (:total-height my-sketch)})))


(deftest t29_l286 (is ((fn [v] (= :svg v)) v28_l282)))


(def v31_l340 (-> iris (sk/view :species) sk/lay-bar (sk/coord :flip)))


(deftest
 t32_l345
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v31_l340)))
