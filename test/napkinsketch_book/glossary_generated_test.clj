(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (def
  my-frame
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris"}))))


(def v4_l33 my-frame)


(deftest
 t5_l35
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v4_l33)))


(def
 v7_l102
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l106
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l111 (is ((fn [y0s] (every? pos? y0s)) v8_l106)))


(def v11_l125 (-> my-frame sk/draft kind/pprint))


(deftest
 t12_l127
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l125)))


(def
 v14_l149
 (merge
  (sk/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l151
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l149)))


(def
 v17_l162
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l168 (is ((fn [n] (= 3 n)) v17_l162)))


(def
 v20_l177
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l182 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l177)))


(def v23_l194 (merge (sk/layer-type-lookup :point) {:jitter true}))


(deftest t24_l196 (is ((fn [m] (true? (:jitter m))) v23_l194)))


(def
 v26_l209
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t27_l213
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v26_l209)))


(def v29_l226 (def my-plan (sk/plan my-frame)))


(def v30_l228 (sort (keys my-plan)))


(deftest t31_l230 (is ((fn [ks] (every? keyword? ks)) v30_l228)))


(def v33_l239 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l241
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l239)))


(def v36_l249 (-> my-frame sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l253 (is ((fn [m] (= :point (:mark m))) v36_l249)))


(def
 v39_l262
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t40_l266
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v39_l262)))


(def
 v42_l278
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t43_l280
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v42_l278)))


(def
 v45_l328
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t46_l333 (is ((fn [n] (= 3 n)) v45_l328)))


(def
 v48_l353
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})
  :layers
  first
  :layer-type))


(deftest t49_l358 (is ((fn [m] (= :rule-h m)) v48_l353)))


(def v51_l367 (:legend my-plan))


(deftest
 t52_l369
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l367)))


(def
 v54_l388
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/svg-summary
  :points))


(deftest t55_l393 (is ((fn [n] (= 150 n)) v54_l388)))


(def v57_l406 (def my-membrane (sk/plan->membrane my-plan)))


(def v58_l408 (vector? my-membrane))


(deftest t59_l410 (is (true? v58_l408)))


(def v60_l412 (count my-membrane))


(deftest t61_l414 (is ((fn [n] (pos? n)) v60_l412)))


(def v63_l424 (def my-plot (sk/plan->plot my-plan :svg {})))


(def v64_l426 (first my-plot))


(deftest t65_l428 (is ((fn [v] (= :svg v)) v64_l426)))


(def v67_l440 (count (c2d/find-palette #".*")))


(deftest t68_l442 (is ((fn [n] (< 1000 n)) v67_l440)))


(def v70_l480 (count sk/plot-option-docs))


(deftest t71_l482 (is ((fn [n] (= 11 n)) v70_l480)))


(def v73_l500 (count sk/layer-option-docs))


(deftest t74_l502 (is ((fn [n] (pos? n)) v73_l500)))
