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


(def v8_l41 (sk/point {:color :species, :alpha 0.5}))


(deftest
 t9_l43
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l41)))


(def
 v11_l63
 (sk/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t12_l65
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v11_l63)))


(def
 v14_l76
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/line {:group :species}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t15_l82 (is ((fn [n] (= 3 n)) v14_l76)))


(def
 v17_l120
 (def
  tips
  (tc/dataset
   {:day ["Mon" "Mon" "Tue" "Tue"],
    :count [30 20 45 15],
    :meal ["lunch" "dinner" "lunch" "dinner"]})))


(def
 v18_l124
 (->
  tips
  (sk/view :day :count)
  (sk/lay (sk/value-bar {:color :meal, :position :stack}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t19_l129 (is ((fn [y0s] (every? pos? y0s)) v18_l124)))


(def
 v21_l138
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/view :x :y)
  (sk/lay (sk/point {:nudge-x 0.5}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t22_l144 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v21_l138)))


(def v24_l155 (sk/point {:jitter true}))


(deftest t25_l157 (is ((fn [m] (true? (:jitter m))) v24_l155)))


(def
 v27_l165
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  layer))


(deftest t28_l169 (is ((fn [m] (= :point (:mark m))) v27_l165)))


(def v30_l180 (def my-sketch (sk/sketch views)))


(def v31_l182 (sort (keys my-sketch)))


(deftest t32_l184 (is ((fn [ks] (every? keyword? ks)) v31_l182)))


(def v34_l192 (sort (keys (first (:panels my-sketch)))))


(deftest
 t35_l194
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v34_l192)))


(def
 v37_l205
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t38_l210 (is ((fn [n] (= 3 n)) v37_l205)))


(def
 v40_l219
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t41_l223
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v40_l219)))


(def v43_l268 (:mark (sk/rule-h 5)))


(deftest t44_l270 (is ((fn [m] (= :rule-h m)) v43_l268)))


(def v46_l279 (:legend my-sketch))


(deftest
 t47_l281
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v46_l279)))


(def
 v49_l290
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


(deftest t50_l296 (is ((fn [n] (= 1 n)) v49_l290)))


(def v52_l308 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v53_l310 (vector? my-membrane))


(deftest t54_l312 (is ((fn [v] (true? v)) v53_l310)))


(def v55_l314 (count my-membrane))


(deftest t56_l316 (is ((fn [n] (pos? n)) v55_l314)))


(def v58_l326 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v59_l328 (first my-figure))


(deftest t60_l330 (is ((fn [v] (= :svg v)) v59_l328)))
