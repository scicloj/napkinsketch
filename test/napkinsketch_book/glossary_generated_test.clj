(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v4_l32 my-sketch)


(deftest
 t5_l34
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l32)))


(def v7_l39 (sk/sketch? my-sketch))


(deftest t8_l41 (is (true? v7_l39)))


(def v9_l43 (count (:views my-sketch)))


(deftest t10_l45 (is ((fn [n] (= 1 n)) v9_l43)))


(def
 v12_l103
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v13_l107
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t14_l112 (is ((fn [y0s] (every? pos? y0s)) v13_l107)))


(def v16_l125 (sk/draft my-sketch))


(deftest
 t17_l127
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v16_l125)))


(def
 v19_l149
 (merge
  (sk/method-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t20_l151
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v19_l149)))


(def
 v22_l162
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t23_l168 (is ((fn [n] (= 3 n)) v22_l162)))


(def
 v25_l177
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t26_l182 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v25_l177)))


(def v28_l193 (merge (sk/method-lookup :point) {:jitter true}))


(deftest t29_l195 (is ((fn [m] (true? (:jitter m))) v28_l193)))


(def
 v31_l207
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t32_l210
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v31_l207)))


(def v34_l222 (def my-plan (sk/plan my-sketch)))


(def v35_l224 (sort (keys my-plan)))


(deftest t36_l226 (is ((fn [ks] (every? keyword? ks)) v35_l224)))


(def v38_l234 (sort (keys (first (:panels my-plan)))))


(deftest
 t39_l236
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v38_l234)))


(def v41_l244 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t42_l248 (is ((fn [m] (= :point (:mark m))) v41_l244)))


(def
 v44_l257
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t45_l261
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v44_l257)))


(def
 v47_l273
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t48_l275
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v47_l273)))


(def
 v50_l323
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t51_l328 (is ((fn [n] (= 3 n)) v50_l323)))


(def
 v53_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:intercept 3.0})
  :layers
  first
  :method))


(deftest t54_l351 (is ((fn [m] (= :rule-h m)) v53_l346)))


(def v56_l360 (:legend my-plan))


(deftest
 t57_l362
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v56_l360)))


(def
 v59_l381
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/svg-summary
  :points))


(deftest t60_l386 (is ((fn [n] (= 150 n)) v59_l381)))


(def v62_l399 (def my-membrane (sk/plan->membrane my-plan)))


(def v63_l401 (vector? my-membrane))


(deftest t64_l403 (is (true? v63_l401)))


(def v65_l405 (count my-membrane))


(deftest t66_l407 (is ((fn [n] (pos? n)) v65_l405)))


(def v68_l417 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v69_l419 (first my-figure))


(deftest t70_l421 (is ((fn [v] (= :svg v)) v69_l419)))


(def v72_l468 (count sk/plot-option-docs))


(deftest t73_l470 (is ((fn [n] (= 11 n)) v72_l468)))


(def v75_l485 (count sk/layer-option-docs))


(deftest t76_l487 (is ((fn [n] (pos? n)) v75_l485)))
