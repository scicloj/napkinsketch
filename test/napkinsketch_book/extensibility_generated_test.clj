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
  [scicloj.napkinsketch.render.scene :as scene]
  [scicloj.napkinsketch.render.svg :as svg]
  [clojure.test :refer [deftest is]]))


(def
 v3_l51
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l75 (sk/histogram))


(deftest t6_l77 (is ((fn [m] (= :bin (:stat m))) v5_l75)))


(def v8_l81 (sk/bar))


(deftest t9_l83 (is ((fn [m] (= :count (:stat m))) v8_l81)))


(def v11_l87 (sk/point))


(deftest t12_l89 (is ((fn [m] (nil? (:stat m))) v11_l87)))


(def
 v14_l129
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
 t15_l135
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l129)))


(def
 v17_l197
 (def
  my-sketch
  (sk/sketch
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))))))


(def v18_l202 (first (sk/render-figure my-sketch :svg {})))


(deftest t19_l204 (is ((fn [v] (= :svg v)) v18_l202)))


(def v21_l208 (def my-figure (sk/render-figure my-sketch :svg {})))


(def v22_l210 (vector? my-figure))


(deftest t23_l212 (is ((fn [v] (true? v)) v22_l210)))


(def
 v25_l268
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t26_l274
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v25_l268)))


(def v28_l284 (def my-scene (scene/sketch->scene my-sketch)))


(def v29_l286 (vector? my-scene))


(deftest t30_l288 (is ((fn [v] (true? v)) v29_l286)))


(def
 v32_l294
 (let
  [svg-body
   (svg/scene->svg my-scene)
   svg
   (svg/wrap-svg
    (:total-width my-sketch)
    (:total-height my-sketch)
    svg-body)]
  (first svg)))


(deftest t33_l298 (is ((fn [v] (= :svg v)) v32_l294)))
