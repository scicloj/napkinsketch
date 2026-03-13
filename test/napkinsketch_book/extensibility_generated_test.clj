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
  [scicloj.napkinsketch.render.membrane :as membrane]
  [scicloj.napkinsketch.render.svg :as svg]
  [clojure.test :refer [deftest is]]))


(def
 v3_l51
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def v5_l76 (sk/histogram))


(deftest t6_l78 (is ((fn [m] (= :bin (:stat m))) v5_l76)))


(def v8_l82 (sk/bar))


(deftest t9_l84 (is ((fn [m] (= :count (:stat m))) v8_l82)))


(def v11_l88 (sk/point))


(deftest t12_l90 (is ((fn [m] (nil? (:stat m))) v11_l88)))


(def
 v14_l131
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
 t15_l137
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v14_l131)))


(def
 v17_l200
 (def
  my-sketch
  (sk/sketch
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))))))


(def v18_l205 (first (sk/render-figure my-sketch :svg {})))


(deftest t19_l207 (is ((fn [v] (= :svg v)) v18_l205)))


(def v21_l211 (def my-figure (sk/render-figure my-sketch :svg {})))


(def v22_l213 (vector? my-figure))


(deftest t23_l215 (is ((fn [v] (true? v)) v22_l213)))


(def
 v25_l272
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t26_l278
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v25_l272)))


(def v28_l288 (def my-membrane (membrane/sketch->membrane my-sketch)))


(def v29_l290 (vector? my-membrane))


(deftest t30_l292 (is ((fn [v] (true? v)) v29_l290)))


(def
 v32_l298
 (let
  [svg-body
   (svg/membrane->svg my-membrane)
   svg
   (svg/wrap-svg
    (:total-width my-sketch)
    (:total-height my-sketch)
    svg-body)]
  (first svg)))


(deftest t33_l302 (is ((fn [v] (= :svg v)) v32_l298)))
