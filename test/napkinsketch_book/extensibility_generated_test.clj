(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.impl.mark :as mark]
  [scicloj.napkinsketch.impl.scale :as scale]
  [scicloj.napkinsketch.impl.coord :as coord]
  [scicloj.napkinsketch.impl.render :as render]
  [scicloj.napkinsketch.render.svg :as svg]
  [clojure.test :refer [deftest is]]))


(def
 v3_l50
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l74 (sk/histogram))


(deftest t6_l76 (is ((fn [m] (= :bin (:stat m))) v5_l74)))


(def v8_l80 (sk/bar))


(deftest t9_l82 (is ((fn [m] (= :count (:stat m))) v8_l80)))


(def v11_l86 (sk/point))


(deftest t12_l88 (is ((fn [m] (nil? (:stat m))) v11_l86)))


(def
 v14_l128
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
 t15_l134
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l128)))


(def
 v17_l196
 (def
  my-sketch
  (sk/sketch
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))))))


(def v18_l201 (first (sk/render-figure my-sketch :svg {})))


(deftest t19_l203 (is ((fn [v] (= :svg v)) v18_l201)))


(def v21_l207 (def my-figure (sk/render-figure my-sketch :svg {})))


(def v22_l209 (vector? my-figure))


(deftest t23_l211 (is ((fn [v] (true? v)) v22_l209)))


(def
 v25_l267
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t26_l273
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v25_l267)))


(def v28_l281 (def my-scene (svg/sketch->scene my-sketch)))


(def v29_l283 (vector? my-scene))


(deftest t30_l285 (is ((fn [v] (true? v)) v29_l283)))


(def
 v32_l291
 (let
  [svg-body
   (svg/scene->svg my-scene)
   svg
   (svg/wrap-svg
    (:total-width my-sketch)
    (:total-height my-sketch)
    svg-body)]
  (first svg)))


(deftest t33_l295 (is ((fn [v] (= :svg v)) v32_l291)))
