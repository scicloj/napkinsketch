(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
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
 (is ((fn [v] (and (sk/sketch? v) (= 1 (count (:entries v))))) v4_l44)))


(def
 v7_l61
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v8_l66 (sk/sketch? my-sketch))


(deftest t9_l68 (is ((fn [v] (true? v)) v8_l66)))


(def v10_l70 (count (:entries my-sketch)))


(deftest t11_l72 (is ((fn [n] (= 1 n)) v10_l70)))


(def
 v13_l117
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l121
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l126 (is ((fn [y0s] (every? pos? y0s)) v14_l121)))


(def
 v17_l145
 (merge
  (method/lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t18_l147
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v17_l145)))


(def
 v20_l158
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l164 (is ((fn [n] (= 3 n)) v20_l158)))


(def
 v23_l173
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l178 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l173)))


(def v26_l189 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l191 (is ((fn [m] (true? (:jitter m))) v26_l189)))


(def v29_l203 (def my-plan (sk/plan my-sketch)))


(def v30_l205 (sort (keys my-plan)))


(deftest t31_l207 (is ((fn [ks] (every? keyword? ks)) v30_l205)))


(def v33_l215 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l217
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l215)))


(def v36_l225 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l229 (is ((fn [m] (= :point (:mark m))) v36_l225)))


(def
 v39_l244
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t40_l249 (is ((fn [n] (= 3 n)) v39_l244)))


(def
 v42_l258
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l262
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l258)))


(def v45_l309 (:mark (sk/rule-h 5)))


(deftest t46_l311 (is ((fn [m] (= :rule-h m)) v45_l309)))


(def v48_l320 (:legend my-plan))


(deftest
 t49_l322
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l320)))


(def
 v51_l331
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


(deftest t52_l337 (is ((fn [n] (= 1 n)) v51_l331)))


(def v54_l350 (def my-membrane (sk/plan->membrane my-plan)))


(def v55_l352 (vector? my-membrane))


(deftest t56_l354 (is ((fn [v] (true? v)) v55_l352)))


(def v57_l356 (count my-membrane))


(deftest t58_l358 (is ((fn [n] (pos? n)) v57_l356)))


(def v60_l368 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v61_l370 (first my-figure))


(deftest t62_l372 (is ((fn [v] (= :svg v)) v61_l370)))


(def v64_l419 (count sk/plot-option-docs))


(deftest t65_l421 (is ((fn [n] (= 6 n)) v64_l419)))


(def v67_l436 (count sk/layer-option-docs))


(deftest t68_l438 (is ((fn [n] (pos? n)) v67_l436)))
