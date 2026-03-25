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


(def
 v8_l64
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v9_l68
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t10_l73 (is ((fn [y0s] (every? pos? y0s)) v9_l68)))


(def
 v12_l91
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t13_l93
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v12_l91)))


(def
 v15_l104
 (->
  iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t16_l110 (is ((fn [n] (= 3 n)) v15_l104)))


(def
 v18_l119
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t19_l124 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v18_l119)))


(def v21_l135 (merge (method/lookup :point) {:jitter true}))


(deftest t22_l137 (is ((fn [m] (true? (:jitter m))) v21_l135)))


(def v24_l149 (def my-sketch (sk/sketch views)))


(def v25_l151 (sort (keys my-sketch)))


(deftest t26_l153 (is ((fn [ks] (every? keyword? ks)) v25_l151)))


(def v28_l161 (sort (keys (first (:panels my-sketch)))))


(deftest
 t29_l163
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v28_l161)))


(def v31_l171 (-> views sk/sketch (get-in [:panels 0 :layers 0])))


(deftest t32_l175 (is ((fn [m] (= :point (:mark m))) v31_l171)))


(def
 v34_l186
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t35_l191 (is ((fn [n] (= 3 n)) v34_l186)))


(def
 v37_l200
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t38_l204
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v37_l200)))


(def v40_l250 (:mark (sk/rule-h 5)))


(deftest t41_l252 (is ((fn [m] (= :rule-h m)) v40_l250)))


(def v43_l261 (:legend my-sketch))


(deftest
 t44_l263
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v43_l261)))


(def
 v46_l272
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


(deftest t47_l278 (is ((fn [n] (= 1 n)) v46_l272)))


(def v49_l290 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v50_l292 (vector? my-membrane))


(deftest t51_l294 (is ((fn [v] (true? v)) v50_l292)))


(def v52_l296 (count my-membrane))


(deftest t53_l298 (is ((fn [n] (pos? n)) v52_l296)))


(def v55_l308 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v56_l310 (first my-figure))


(deftest t57_l312 (is ((fn [v] (= :svg v)) v56_l310)))
