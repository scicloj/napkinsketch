(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.impl.scale :as scale]
  [scicloj.napkinsketch.impl.coord :as coord]
  [scicloj.napkinsketch.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def
 v3_l56
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l83 (sk/histogram))


(deftest t6_l85 (is ((fn [m] (= :bin (:stat m))) v5_l83)))


(def v8_l89 (sk/bar))


(deftest t9_l91 (is ((fn [m] (= :count (:stat m))) v8_l89)))


(def v11_l95 (sk/point))


(deftest t12_l97 (is ((fn [m] (nil? (:stat m))) v11_l95)))


(def
 v14_l142
 (let
  [s
   (sk/sketch
    (->
     iris
     (sk/view [[:sepal_length :sepal_width]])
     (sk/lay (sk/point {:color :species}))))
   layer
   (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style])))


(deftest
 t15_l148
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l142)))


(def
 v17_l213
 (def
  my-sketch
  (sk/sketch
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))))))


(def v18_l218 (first (sk/sketch->figure my-sketch :svg {})))


(deftest t19_l220 (is ((fn [v] (= :svg v)) v18_l218)))


(def v21_l224 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v22_l226 (vector? my-figure))


(deftest t23_l228 (is ((fn [v] (true? v)) v22_l226)))


(def v25_l269 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v26_l271 (vector? my-membrane))


(deftest t27_l273 (is ((fn [v] (true? v)) v26_l271)))


(def
 v28_l275
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-sketch),
    :total-height (:total-height my-sketch)})))


(deftest t29_l279 (is ((fn [v] (= :svg v)) v28_l275)))


(def
 v31_l333
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t32_l339
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v31_l333)))
