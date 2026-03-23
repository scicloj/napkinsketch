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
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v5_l31 (kind/pprint views))


(deftest
 t6_l33
 (is
  ((fn [v] (and (sk/plot-spec? v) (= 1 (count (sk/views-of v)))))
   v5_l31)))


(def v8_l41 (method/point {:color :species, :alpha 0.5}))


(deftest
 t9_l43
 (is
  ((fn [m] (and (= :point (:mark m)) (= :species (:color m)))) v8_l41)))


(def v11_l58 (method/histogram))


(deftest
 t12_l60
 (is ((fn [m] (and (= :bar (:mark m)) (= :bin (:stat m)))) v11_l58)))


(def v13_l63 (method/point))


(deftest
 t14_l65
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v13_l63)))


(def
 v16_l85
 (method/point {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t17_l87
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v16_l85)))


(def
 v19_l98
 (->
  iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t20_l104 (is ((fn [n] (= 3 n)) v19_l98)))


(def
 v22_l142
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v23_l146
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t24_l151 (is ((fn [y0s] (every? pos? y0s)) v23_l146)))


(def
 v26_l160
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t27_l165 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v26_l160)))


(def v29_l176 (method/point {:jitter true}))


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
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t43_l232 (is ((fn [n] (= 3 n)) v42_l227)))


(def
 v45_l241
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t46_l245
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v45_l241)))


(def v48_l291 (:mark (sk/rule-h 5)))


(deftest t49_l293 (is ((fn [m] (= :rule-h m)) v48_l291)))


(def v51_l302 (:legend my-sketch))


(deftest
 t52_l304
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l302)))


(def
 v54_l313
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


(deftest t55_l319 (is ((fn [n] (= 1 n)) v54_l313)))


(def v57_l331 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v58_l333 (vector? my-membrane))


(deftest t59_l335 (is ((fn [v] (true? v)) v58_l333)))


(def v60_l337 (count my-membrane))


(deftest t61_l339 (is ((fn [n] (pos? n)) v60_l337)))


(def v63_l349 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v64_l351 (first my-figure))


(deftest t65_l353 (is ((fn [v] (= :svg v)) v64_l351)))
