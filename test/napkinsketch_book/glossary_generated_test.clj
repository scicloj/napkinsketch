(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l40
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species}))))


(def v4_l44 (kind/pprint my-sketch))


(deftest
 t5_l46
 (is ((fn [v] (and (sk/sketch? v) (= 1 (count (:views v))))) v4_l44)))


(def
 v7_l63
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v8_l68 (sk/sketch? my-sketch))


(deftest t9_l70 (is ((fn [v] (true? v)) v8_l68)))


(def v10_l72 (count (:views my-sketch)))


(deftest t11_l74 (is ((fn [n] (= 1 n)) v10_l72)))


(def
 v13_l119
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l123
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l128 (is ((fn [y0s] (every? pos? y0s)) v14_l123)))


(def
 v17_l147
 (merge
  (sk/method-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t18_l149
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v17_l147)))


(def
 v20_l160
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l166 (is ((fn [n] (= 3 n)) v20_l160)))


(def
 v23_l175
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l180 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l175)))


(def v26_l191 (merge (sk/method-lookup :point) {:jitter true}))


(deftest t27_l193 (is ((fn [m] (true? (:jitter m))) v26_l191)))


(def v29_l205 (def my-plan (sk/plan my-sketch)))


(def v30_l207 (sort (keys my-plan)))


(deftest t31_l209 (is ((fn [ks] (every? keyword? ks)) v30_l207)))


(def v33_l217 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l219
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l217)))


(def v36_l227 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l231 (is ((fn [m] (= :point (:mark m))) v36_l227)))


(def
 v39_l246
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t40_l251 (is ((fn [n] (= 3 n)) v39_l246)))


(def
 v42_l260
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l264
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l260)))


(def v45_l311 (:mark (sk/rule-h 5)))


(deftest t46_l313 (is ((fn [m] (= :rule-h m)) v45_l311)))


(def v48_l322 (:legend my-plan))


(deftest
 t49_l324
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l322)))


(def
 v51_l333
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t52_l339 (is ((fn [n] (= 1 n)) v51_l333)))


(def v54_l352 (def my-membrane (sk/plan->membrane my-plan)))


(def v55_l354 (vector? my-membrane))


(deftest t56_l356 (is ((fn [v] (true? v)) v55_l354)))


(def v57_l358 (count my-membrane))


(deftest t58_l360 (is ((fn [n] (pos? n)) v57_l358)))


(def v60_l370 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v61_l372 (first my-figure))


(deftest t62_l374 (is ((fn [v] (= :svg v)) v61_l372)))


(def v64_l421 (count sk/plot-option-docs))


(deftest t65_l423 (is ((fn [n] (= 6 n)) v64_l421)))


(def v67_l438 (count sk/layer-option-docs))


(deftest t68_l440 (is ((fn [n] (pos? n)) v67_l438)))
