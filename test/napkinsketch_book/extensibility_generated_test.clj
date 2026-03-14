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


(def v5_l82 (sk/histogram))


(deftest t6_l84 (is ((fn [m] (= :bin (:stat m))) v5_l82)))


(def v8_l88 (sk/bar))


(deftest t9_l90 (is ((fn [m] (= :count (:stat m))) v8_l88)))


(def v11_l94 (sk/point))


(deftest t12_l96 (is ((fn [m] (nil? (:stat m))) v11_l94)))


(def
 v14_l141
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
 t15_l147
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l141)))


(def
 v17_l212
 (def
  my-sketch
  (sk/sketch
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))))))


(def v18_l217 (first (sk/sketch->figure my-sketch :svg {})))


(deftest t19_l219 (is ((fn [v] (= :svg v)) v18_l217)))


(def v21_l223 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v22_l225 (vector? my-figure))


(deftest t23_l227 (is ((fn [v] (true? v)) v22_l225)))


(def v25_l268 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v26_l270 (vector? my-membrane))


(deftest t27_l272 (is ((fn [v] (true? v)) v26_l270)))


(def
 v28_l274
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-sketch),
    :total-height (:total-height my-sketch)})))


(deftest t29_l278 (is ((fn [v] (= :svg v)) v28_l274)))


(def
 v31_l332
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t32_l338
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v31_l332)))
