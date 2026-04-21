(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def
  my-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v4_l34 my-sketch)


(deftest
 t5_l36
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l34)))


(def v7_l41 (sk/sketch? my-sketch))


(deftest t8_l43 (is (true? v7_l41)))


(def v9_l45 (count (:views my-sketch)))


(deftest t10_l47 (is ((fn [n] (= 1 n)) v9_l45)))


(def
 v12_l105
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v13_l109
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t14_l114 (is ((fn [y0s] (every? pos? y0s)) v13_l109)))


(def v16_l127 (-> my-sketch sk/draft kind/pprint))


(deftest
 t17_l129
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v16_l127)))


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


(def
 v31_l209
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t32_l212
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v31_l209)))


(def v34_l224 (def my-plan (sk/plan my-sketch)))


(def v35_l226 (sort (keys my-plan)))


(deftest t36_l228 (is ((fn [ks] (every? keyword? ks)) v35_l226)))


(def v38_l236 (sort (keys (first (:panels my-plan)))))


(deftest
 t39_l238
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v38_l236)))


(def v41_l246 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t42_l250 (is ((fn [m] (= :point (:mark m))) v41_l246)))


(def
 v44_l259
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t45_l263
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v44_l259)))


(def
 v47_l275
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t48_l277
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v47_l275)))


(def
 v50_l325
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t51_l330 (is ((fn [n] (= 3 n)) v50_l325)))


(def
 v53_l349
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})
  :layers
  first
  :method))


(deftest t54_l354 (is ((fn [m] (= :rule-h m)) v53_l349)))


(def v56_l363 (:legend my-plan))


(deftest
 t57_l365
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v56_l363)))


(def
 v59_l384
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/svg-summary
  :points))


(deftest t60_l389 (is ((fn [n] (= 150 n)) v59_l384)))


(def v62_l402 (def my-membrane (sk/plan->membrane my-plan)))


(def v63_l404 (vector? my-membrane))


(deftest t64_l406 (is (true? v63_l404)))


(def v65_l408 (count my-membrane))


(deftest t66_l410 (is ((fn [n] (pos? n)) v65_l408)))


(def v68_l420 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v69_l422 (first my-figure))


(deftest t70_l424 (is ((fn [v] (= :svg v)) v69_l422)))


(def v72_l436 (count (c2d/find-palette #".*")))


(deftest t73_l438 (is ((fn [n] (< 1000 n)) v72_l436)))


(def v75_l476 (count sk/plot-option-docs))


(deftest t76_l478 (is ((fn [n] (= 11 n)) v75_l476)))


(def v78_l493 (count sk/layer-option-docs))


(deftest t79_l495 (is ((fn [n] (pos? n)) v78_l493)))
