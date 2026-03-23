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
 v4_l28
 (def
  views
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v5_l32 (kind/pprint views))


(deftest
 t6_l34
 (is
  ((fn [v] (and (sk/plot-spec? v) (= 1 (count (sk/views-of v)))))
   v5_l32)))


(def v8_l53 (method/histogram))


(deftest
 t9_l55
 (is ((fn [m] (and (= :bar (:mark m)) (= :bin (:stat m)))) v8_l53)))


(def v10_l58 (method/point))


(deftest
 t11_l60
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v10_l58)))


(def v13_l68 (method/point {:color :species, :alpha 0.5}))


(deftest
 t14_l70
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m))))
   v13_l68)))


(def
 v16_l110
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v17_l114
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t18_l119 (is ((fn [y0s] (every? pos? y0s)) v17_l114)))


(def
 v20_l138
 (method/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t21_l140
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v20_l138)))


(def
 v23_l151
 (->
  iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t24_l157 (is ((fn [n] (= 3 n)) v23_l151)))


(def
 v26_l166
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t27_l171 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v26_l166)))


(def v29_l182 (method/point {:jitter true}))


(deftest t30_l184 (is ((fn [m] (true? (:jitter m))) v29_l182)))


(def v32_l196 (def my-sketch (sk/sketch views)))


(def v33_l198 (sort (keys my-sketch)))


(deftest t34_l200 (is ((fn [ks] (every? keyword? ks)) v33_l198)))


(def v36_l208 (sort (keys (first (:panels my-sketch)))))


(deftest
 t37_l210
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v36_l208)))


(def v39_l218 (-> views sk/sketch (get-in [:panels 0 :layers 0])))


(deftest t40_l222 (is ((fn [m] (= :point (:mark m))) v39_l218)))


(def
 v42_l233
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t43_l238 (is ((fn [n] (= 3 n)) v42_l233)))


(def
 v45_l247
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t46_l251
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v45_l247)))


(def v48_l297 (:mark (sk/rule-h 5)))


(deftest t49_l299 (is ((fn [m] (= :rule-h m)) v48_l297)))


(def v51_l308 (:legend my-sketch))


(deftest
 t52_l310
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l308)))


(def
 v54_l319
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t55_l325 (is ((fn [n] (= 1 n)) v54_l319)))


(def v57_l337 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v58_l339 (vector? my-membrane))


(deftest t59_l341 (is ((fn [v] (true? v)) v58_l339)))


(def v60_l343 (count my-membrane))


(deftest t61_l345 (is ((fn [n] (pos? n)) v60_l343)))


(def v63_l355 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v64_l357 (first my-figure))


(deftest t65_l359 (is ((fn [v] (= :svg v)) v64_l357)))
