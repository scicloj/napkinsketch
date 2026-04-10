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


(def v4_l44 my-sketch)


(deftest
 t5_l46
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l44)))


(def v6_l48 (kind/pprint my-sketch))


(deftest
 t7_l50
 (is ((fn [v] (and (sk/sketch? v) (= 1 (count (:views v))))) v6_l48)))


(def
 v9_l67
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v10_l72 (sk/sketch? my-sketch))


(deftest t11_l74 (is ((fn [v] (true? v)) v10_l72)))


(def v12_l76 (count (:views my-sketch)))


(deftest t13_l78 (is ((fn [n] (= 1 n)) v12_l76)))


(def
 v15_l123
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v16_l127
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t17_l132 (is ((fn [y0s] (every? pos? y0s)) v16_l127)))


(def
 v19_l151
 (merge
  (sk/method-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t20_l153
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v19_l151)))


(def
 v22_l164
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t23_l170 (is ((fn [n] (= 3 n)) v22_l164)))


(def
 v25_l179
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t26_l184 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v25_l179)))


(def v28_l195 (merge (sk/method-lookup :point) {:jitter true}))


(deftest t29_l197 (is ((fn [m] (true? (:jitter m))) v28_l195)))


(def v31_l209 (def my-plan (sk/plan my-sketch)))


(def v32_l211 (sort (keys my-plan)))


(deftest t33_l213 (is ((fn [ks] (every? keyword? ks)) v32_l211)))


(def v35_l221 (sort (keys (first (:panels my-plan)))))


(deftest
 t36_l223
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v35_l221)))


(def v38_l231 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t39_l235 (is ((fn [m] (= :point (:mark m))) v38_l231)))


(def
 v41_l250
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t42_l255 (is ((fn [n] (= 3 n)) v41_l250)))


(def
 v44_l264
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t45_l268
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v44_l264)))


(def v47_l315 (:mark (sk/rule-h 5)))


(deftest t48_l317 (is ((fn [m] (= :rule-h m)) v47_l315)))


(def v50_l326 (:legend my-plan))


(deftest
 t51_l328
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v50_l326)))


(def
 v53_l337
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


(deftest t54_l343 (is ((fn [n] (= 1 n)) v53_l337)))


(def v56_l356 (def my-membrane (sk/plan->membrane my-plan)))


(def v57_l358 (vector? my-membrane))


(deftest t58_l360 (is ((fn [v] (true? v)) v57_l358)))


(def v59_l362 (count my-membrane))


(deftest t60_l364 (is ((fn [n] (pos? n)) v59_l362)))


(def v62_l374 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v63_l376 (first my-figure))


(deftest t64_l378 (is ((fn [v] (= :svg v)) v63_l376)))


(def v66_l425 (count sk/plot-option-docs))


(deftest t67_l427 (is ((fn [n] (= 6 n)) v66_l425)))


(def v69_l442 (count sk/layer-option-docs))


(deftest t70_l444 (is ((fn [n] (pos? n)) v69_l442)))
