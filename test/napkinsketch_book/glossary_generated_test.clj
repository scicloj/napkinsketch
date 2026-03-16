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


(def
 v5_l29
 (kind/pprint
  (mapv
   (fn* [p1__77388#] (select-keys p1__77388# [:x :y :mark :color]))
   views)))


(deftest
 t6_l32
 (is ((fn [v] (and (vector? v) (= 1 (count v)))) v5_l29)))


(def v8_l42 (sk/point {:color :species, :alpha 0.5}))


(deftest
 t9_l44
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l42)))


(def
 v11_l64
 (sk/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t12_l66
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v11_l64)))


(def
 v14_l77
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/line {:group :species}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t15_l83 (is ((fn [n] (= 3 n)) v14_l77)))


(def
 v17_l121
 (def
  tips
  (tc/dataset
   {:day ["Mon" "Mon" "Tue" "Tue"],
    :count [30 20 45 15],
    :meal ["lunch" "dinner" "lunch" "dinner"]})))


(def
 v18_l125
 (->
  tips
  (sk/view :day :count)
  (sk/lay (sk/value-bar {:color :meal, :position :stack}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t19_l130 (is ((fn [y0s] (every? pos? y0s)) v18_l125)))


(def
 v21_l139
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/view :x :y)
  (sk/lay (sk/point {:nudge-x 0.5}))
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t22_l145 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v21_l139)))


(def v24_l156 (sk/point {:jitter true}))


(deftest t25_l158 (is ((fn [m] (true? (:jitter m))) v24_l156)))


(def
 v27_l166
 (let
  [s (sk/sketch views) layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style])))


(deftest t28_l170 (is ((fn [m] (= :point (:mark m))) v27_l166)))


(def v30_l181 (def my-sketch (sk/sketch views)))


(def v31_l183 (sort (keys my-sketch)))


(deftest t32_l185 (is ((fn [ks] (every? keyword? ks)) v31_l183)))


(def v34_l193 (sort (keys (first (:panels my-sketch)))))


(deftest
 t35_l195
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v34_l193)))


(def
 v37_l206
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point))
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t38_l211 (is ((fn [n] (= 3 n)) v37_l206)))


(def
 v40_l220
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t41_l224
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v40_l220)))


(def v43_l269 (:mark (sk/rule-h 5)))


(deftest t44_l271 (is ((fn [m] (= :rule-h m)) v43_l269)))


(def v46_l280 (:legend my-sketch))


(deftest
 t47_l282
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v46_l280)))


(def
 v49_l291
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


(deftest t50_l297 (is ((fn [n] (= 1 n)) v49_l291)))


(def v52_l309 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v53_l311 (vector? my-membrane))


(deftest t54_l313 (is ((fn [v] (true? v)) v53_l311)))


(def v55_l315 (count my-membrane))


(deftest t56_l317 (is ((fn [n] (pos? n)) v55_l315)))


(def v58_l327 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v59_l329 (first my-figure))


(deftest t60_l331 (is ((fn [v] (= :svg v)) v59_l329)))
