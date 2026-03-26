(ns
 napkinsketch-book.extensibility-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.impl.sketch :as sketch]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.render.svg :as svg]
  [scicloj.napkinsketch.impl.render :as render]
  [clojure.test :refer [deftest is]]))


(def v3_l85 (method/lookup :histogram))


(deftest t4_l87 (is ((fn [m] (= :bin (:stat m))) v3_l85)))


(def v6_l91 (method/lookup :bar))


(deftest t7_l93 (is ((fn [m] (= :count (:stat m))) v6_l91)))


(def v9_l97 (method/lookup :point))


(deftest t10_l99 (is ((fn [m] (= :identity (:stat m))) v9_l97)))


(def
 v12_l146
 (let
  [s
   (->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/sketch)
   layer
   (first (:layers (first (:panels s))))]
  layer))


(deftest
 t13_l152
 (is
  ((fn
    [m]
    (and (= :point (:mark m)) (number? (get-in m [:style :opacity]))))
   v12_l146)))


(def
 v15_l218
 (def
  my-sketch
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   sk/sketch)))


(def v16_l223 (first (sk/sketch->figure my-sketch :svg {})))


(deftest t17_l225 (is ((fn [v] (= :svg v)) v16_l223)))


(def v19_l229 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v20_l231 (vector? my-figure))


(deftest t21_l233 (is ((fn [v] (true? v)) v20_l231)))


(def v23_l274 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v24_l276 (vector? my-membrane))


(deftest t25_l278 (is ((fn [v] (true? v)) v24_l276)))


(def
 v26_l280
 (first
  (sk/membrane->figure
   my-membrane
   :svg
   {:total-width (:total-width my-sketch),
    :total-height (:total-height my-sketch)})))


(deftest t27_l284 (is ((fn [v] (= :svg v)) v26_l280)))


(def v29_l338 (-> data/iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t30_l342
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v29_l338)))
