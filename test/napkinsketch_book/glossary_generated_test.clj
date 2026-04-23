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
 v7_l98
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l102
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l107 (is ((fn [y0s] (every? pos? y0s)) v8_l102)))


(def v11_l121 (-> my-frame sk/draft kind/pprint))


(deftest
 t12_l123
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l121)))


(def
 v14_l145
 (merge
  (sk/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l147
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l145)))


(def
 v17_l158
 (->
  (rdatasets/datasets-iris)
  (sk/lay-line :sepal-length :sepal-width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l164 (is ((fn [n] (= 3 n)) v17_l158)))


(def
 v20_l173
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l178 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l173)))


(def v23_l190 (merge (sk/layer-type-lookup :point) {:jitter true}))


(deftest t24_l192 (is ((fn [m] (true? (:jitter m))) v23_l190)))


(def
 v26_l205
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t27_l209
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v26_l205)))


(def v29_l222 (def my-plan (sk/plan my-frame)))


(def v30_l224 (sort (keys my-plan)))


(deftest t31_l226 (is ((fn [ks] (every? keyword? ks)) v30_l224)))


(def v33_l235 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l237
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l235)))


(def v36_l245 (-> my-frame sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l249 (is ((fn [m] (= :point (:mark m))) v36_l245)))


(def
 v39_l258
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t40_l262
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v39_l258)))


(def
 v42_l274
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t43_l276
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v42_l274)))


(def
 v45_l324
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t46_l329 (is ((fn [n] (= 3 n)) v45_l324)))


(def
 v48_l348
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-rule-h {:y-intercept 3.0})
  :layers
  first
  :layer-type))


(deftest t49_l353 (is ((fn [m] (= :rule-h m)) v48_l348)))


(def v51_l362 (:legend my-plan))


(deftest
 t52_l364
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v51_l362)))


(def
 v54_l383
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/svg-summary
  :points))


(deftest t55_l388 (is ((fn [n] (= 150 n)) v54_l383)))


(def v57_l401 (def my-membrane (sk/plan->membrane my-plan)))


(def v58_l403 (vector? my-membrane))


(deftest t59_l405 (is (true? v58_l403)))


(def v60_l407 (count my-membrane))


(deftest t61_l409 (is ((fn [n] (pos? n)) v60_l407)))


(def v63_l419 (def my-plot (sk/plan->plot my-plan :svg {})))


(def v64_l421 (first my-plot))


(deftest t65_l423 (is ((fn [v] (= :svg v)) v64_l421)))


(def v67_l435 (count (c2d/find-palette #".*")))


(deftest t68_l437 (is ((fn [n] (< 1000 n)) v67_l435)))


(def v70_l475 (count sk/plot-option-docs))


(deftest t71_l477 (is ((fn [n] (= 11 n)) v70_l475)))


(def v73_l495 (count sk/layer-option-docs))


(deftest t74_l497 (is ((fn [n] (pos? n)) v73_l495)))
