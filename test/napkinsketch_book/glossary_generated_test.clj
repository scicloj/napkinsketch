(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v2_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l27
 (def
  views
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay-point {:color :species}))))


(def v5_l32 (kind/pprint views))


(deftest
 t6_l34
 (is
  ((fn [v] (and (sk/plot-spec? v) (= 1 (count (sk/views-of v)))))
   v5_l32)))


(def v8_l42 (method/point {:color :species, :alpha 0.5}))


(deftest
 t9_l44
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l42)))


(def v11_l59 (method/histogram))


(deftest
 t12_l61
 (is ((fn [m] (and (= :bar (:mark m)) (= :bin (:stat m)))) v11_l59)))


(def v13_l64 (method/point))


(deftest
 t14_l66
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l64)))


(def
 v16_l86
 (method/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t17_l88
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v16_l86)))


(def
 v19_l99
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-line {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t20_l106 (is ((fn [n] (= 3 n)) v19_l99)))


(def
 v22_l144
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v23_l148
 (->
  tips
  (sk/view :day :count)
  (sk/lay-value-bar {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t24_l154 (is ((fn [y0s] (every? pos? y0s)) v23_l148)))


(def
 v26_l163
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/view :x :y)
  (sk/lay-point {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t27_l169 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v26_l163)))


(def v29_l180 (method/point {:jitter true}))


(deftest t30_l182 (is ((fn [m] (true? (:jitter m))) v29_l180)))


(def
 v32_l190
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  layer))


(deftest t33_l194 (is ((fn [m] (= :point (:mark m))) v32_l190)))


(def v35_l206 (def my-sketch (sk/sketch views)))


(def v36_l208 (sort (keys my-sketch)))


(deftest t37_l210 (is ((fn [ks] (every? keyword? ks)) v36_l208)))


(def v39_l218 (sort (keys (first (:panels my-sketch)))))


(deftest
 t40_l220
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v39_l218)))


(def
 v42_l231
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t43_l237 (is ((fn [n] (= 3 n)) v42_l231)))


(def
 v45_l246
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t46_l250
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v45_l246)))


(def v48_l296 (:mark (sk/rule-h 5)))


(deftest t49_l298 (is ((fn [m] (= :rule-h m)) v48_l296)))


(def v51_l307 (:legend my-sketch))


(deftest
 t52_l309
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l307)))


(def
 v54_l318
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:color :species})
  (sk/options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t55_l325 (is ((fn [n] (= 1 n)) v54_l318)))


(def v57_l337 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v58_l339 (vector? my-membrane))


(deftest t59_l341 (is ((fn [v] (true? v)) v58_l339)))


(def v60_l343 (count my-membrane))


(deftest t61_l345 (is ((fn [n] (pos? n)) v60_l343)))


(def v63_l355 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v64_l357 (first my-figure))


(deftest t65_l359 (is ((fn [v] (= :svg v)) v64_l357)))
