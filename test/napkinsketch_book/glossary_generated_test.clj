(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l24
 (def
  views
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/lay (sk/point {:color :species})))))


(def v5_l29 (kind/pprint views))


(deftest
 t6_l31
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v5_l29)))


(def v8_l39 (sk/point {:color :species, :alpha 0.5}))


(deftest
 t9_l41
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l39)))


(def v11_l55 (sk/histogram))


(deftest
 t12_l57
 (is ((fn [m] (and (= :bar (:mark m)) (= :bin (:stat m)))) v11_l55)))


(def v13_l60 (sk/point))


(deftest
 t14_l62
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l60)))


(def
 v16_l82
 (sk/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t17_l84
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v16_l82)))


(def
 v19_l95
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/line {:group :species}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t20_l102 (is ((fn [n] (= 3 n)) v19_l95)))


(def
 v22_l140
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v23_l144
 (->
  tips
  (sk/view :day :count)
  (sk/lay (sk/value-bar {:color :meal, :position :stack}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t24_l150 (is ((fn [y0s] (every? pos? y0s)) v23_l144)))


(def
 v26_l159
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/view :x :y)
  (sk/lay (sk/point {:nudge-x 0.5}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t27_l165 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v26_l159)))


(def v29_l176 (sk/point {:jitter true}))


(deftest t30_l178 (is ((fn [m] (true? (:jitter m))) v29_l176)))


(def
 v32_l186
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  layer))


(deftest t33_l190 (is ((fn [m] (= :point (:mark m))) v32_l186)))


(def v35_l202 (def my-sketch (sk/sketch views)))


(def v36_l204 (sort (keys my-sketch)))


(deftest t37_l206 (is ((fn [ks] (every? keyword? ks)) v36_l204)))


(def v39_l214 (sort (keys (first (:panels my-sketch)))))


(deftest
 t40_l216
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v39_l214)))


(def
 v42_l227
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t43_l233 (is ((fn [n] (= 3 n)) v42_l227)))


(def
 v45_l242
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t46_l246
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v45_l242)))


(def v48_l292 (:mark (sk/rule-h 5)))


(deftest t49_l294 (is ((fn [m] (= :rule-h m)) v48_l292)))


(def v51_l303 (:legend my-sketch))


(deftest
 t52_l305
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l303)))


(def
 v54_l314
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t55_l321 (is ((fn [n] (= 1 n)) v54_l314)))


(def v57_l333 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v58_l335 (vector? my-membrane))


(deftest t59_l337 (is ((fn [v] (true? v)) v58_l335)))


(def v60_l339 (count my-membrane))


(deftest t61_l341 (is ((fn [n] (pos? n)) v60_l339)))


(def v63_l351 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v64_l353 (first my-figure))


(deftest t65_l355 (is ((fn [v] (= :svg v)) v64_l353)))
