(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v3_l24
 (def
  views
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v4_l28 (kind/pprint views))


(deftest
 t5_l30
 (is
  ((fn [v] (and (sk/plot-spec? v) (= 1 (count (sk/views-of v)))))
   v4_l28)))


(def
 v7_l64
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l68
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l73 (is ((fn [y0s] (every? pos? y0s)) v8_l68)))


(def
 v11_l92
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t12_l94
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v11_l92)))


(def
 v14_l105
 (->
  data/iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t15_l111 (is ((fn [n] (= 3 n)) v14_l105)))


(def
 v17_l120
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t18_l125 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v17_l120)))


(def v20_l136 (merge (method/lookup :point) {:jitter true}))


(deftest t21_l138 (is ((fn [m] (true? (:jitter m))) v20_l136)))


(def v23_l150 (def my-sketch (sk/sketch views)))


(def v24_l152 (sort (keys my-sketch)))


(deftest t25_l154 (is ((fn [ks] (every? keyword? ks)) v24_l152)))


(def v27_l162 (sort (keys (first (:panels my-sketch)))))


(deftest
 t28_l164
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v27_l162)))


(def v30_l172 (-> views sk/sketch (get-in [:panels 0 :layers 0])))


(deftest t31_l176 (is ((fn [m] (= :point (:mark m))) v30_l172)))


(def
 v33_l187
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t34_l192 (is ((fn [n] (= 3 n)) v33_l187)))


(def
 v36_l201
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t37_l205
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v36_l201)))


(def v39_l251 (:mark (sk/rule-h 5)))


(deftest t40_l253 (is ((fn [m] (= :rule-h m)) v39_l251)))


(def v42_l262 (:legend my-sketch))


(deftest
 t43_l264
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v42_l262)))


(def
 v45_l273
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t46_l279 (is ((fn [n] (= 1 n)) v45_l273)))


(def v48_l291 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v49_l293 (vector? my-membrane))


(deftest t50_l295 (is ((fn [v] (true? v)) v49_l293)))


(def v51_l297 (count my-membrane))


(deftest t52_l299 (is ((fn [n] (pos? n)) v51_l297)))


(def v54_l309 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v55_l311 (first my-figure))


(deftest t56_l313 (is ((fn [v] (= :svg v)) v55_l311)))
