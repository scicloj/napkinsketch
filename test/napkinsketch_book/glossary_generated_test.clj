(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v3_l42
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species}))))


(def v4_l46 (kind/pprint my-sketch))


(deftest
 t5_l48
 (is ((fn [v] (and (sk/sketch? v) (= 1 (count (:views v))))) v4_l46)))


(def
 v7_l65
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v8_l70 (sk/sketch? my-sketch))


(deftest t9_l72 (is ((fn [v] (true? v)) v8_l70)))


(def v10_l74 (count (:views my-sketch)))


(deftest t11_l76 (is ((fn [n] (= 1 n)) v10_l74)))


(def
 v13_l121
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l125
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l130 (is ((fn [y0s] (every? pos? y0s)) v14_l125)))


(def
 v17_l149
 (merge
  (method/lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t18_l151
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v17_l149)))


(def
 v20_l162
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l168 (is ((fn [n] (= 3 n)) v20_l162)))


(def
 v23_l177
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l182 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l177)))


(def v26_l193 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l195 (is ((fn [m] (true? (:jitter m))) v26_l193)))


(def v29_l207 (def my-plan (sk/plan my-sketch)))


(def v30_l209 (sort (keys my-plan)))


(deftest t31_l211 (is ((fn [ks] (every? keyword? ks)) v30_l209)))


(def v33_l219 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l221
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l219)))


(def v36_l229 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l233 (is ((fn [m] (= :point (:mark m))) v36_l229)))


(def
 v39_l248
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t40_l253 (is ((fn [n] (= 3 n)) v39_l248)))


(def
 v42_l262
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l266
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l262)))


(def v45_l313 (:mark (sk/rule-h 5)))


(deftest t46_l315 (is ((fn [m] (= :rule-h m)) v45_l313)))


(def v48_l324 (:legend my-plan))


(deftest
 t49_l326
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l324)))


(def
 v51_l335
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


(deftest t52_l341 (is ((fn [n] (= 1 n)) v51_l335)))


(def v54_l354 (def my-membrane (sk/plan->membrane my-plan)))


(def v55_l356 (vector? my-membrane))


(deftest t56_l358 (is ((fn [v] (true? v)) v55_l356)))


(def v57_l360 (count my-membrane))


(deftest t58_l362 (is ((fn [n] (pos? n)) v57_l360)))


(def v60_l372 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v61_l374 (first my-figure))


(deftest t62_l376 (is ((fn [v] (= :svg v)) v61_l374)))


(def v64_l423 (count sk/plot-option-docs))


(deftest t65_l425 (is ((fn [n] (= 6 n)) v64_l423)))


(def v67_l440 (count sk/layer-option-docs))


(deftest t68_l442 (is ((fn [n] (pos? n)) v67_l440)))
