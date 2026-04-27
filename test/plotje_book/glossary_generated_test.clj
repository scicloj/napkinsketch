(ns
 plotje-book.glossary-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (def
  my-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/options {:title "Iris"}))))


(def v4_l33 my-pose)


(deftest
 t5_l35
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v4_l33)))


(def
 v7_l114
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l118
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l123 (is ((fn [y0s] (every? pos? y0s)) v8_l118)))


(def v11_l137 (-> my-pose pj/draft kind/pprint))


(deftest
 t12_l139
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l137)))


(def
 v14_l169
 (merge
  (pj/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l171
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l169)))


(def
 v17_l182
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l188 (is ((fn [n] (= 3 n)) v17_l182)))


(def
 v20_l197
 (->
  {:x [1 2 3], :y [4 5 6]}
  (pj/lay-point :x :y {:nudge-x 0.5})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l202 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l197)))


(def v23_l214 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t24_l216 (is ((fn [m] (true? (:jitter m))) v23_l214)))


(def
 v26_l228
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t27_l232
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v26_l228)))


(def v29_l245 (def my-plan (pj/plan my-pose)))


(def v30_l247 (sort (keys my-plan)))


(deftest t31_l249 (is ((fn [ks] (every? keyword? ks)) v30_l247)))


(def v33_l258 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l260
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l258)))


(def v36_l270 (-> my-pose :layers first :layer-type))


(deftest t37_l272 (is ((fn [k] (= :point k)) v36_l270)))


(def v39_l280 (-> my-pose pj/plan (get-in [:panels 0 :layers 0])))


(deftest t40_l284 (is ((fn [m] (= :point (:mark m))) v39_l280)))


(def
 v42_l331
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l335
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l331)))


(def
 v45_l347
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t46_l349
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v45_l347)))


(def
 v48_l397
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)
  pj/plan
  :panels
  count))


(deftest t49_l402 (is ((fn [n] (= 3 n)) v48_l397)))


(def
 v51_l422
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})
  :layers
  (nth 1)
  :layer-type))


(deftest t52_l427 (is ((fn [m] (= :rule-h m)) v51_l422)))


(def v54_l436 (:legend my-plan))


(deftest
 t55_l438
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v54_l436)))


(def
 v57_l457
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  pj/svg-summary
  :points))


(deftest t58_l462 (is ((fn [n] (= 150 n)) v57_l457)))


(def v60_l475 (def my-membrane (pj/plan->membrane my-plan)))


(def v61_l477 (vector? my-membrane))


(deftest t62_l479 (is (true? v61_l477)))


(def v63_l481 (count my-membrane))


(deftest t64_l483 (is ((fn [n] (pos? n)) v63_l481)))


(def v66_l493 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v67_l495 (first my-plot))


(deftest t68_l497 (is ((fn [v] (= :svg v)) v67_l495)))


(def v70_l509 (count (c2d/find-palette #".*")))


(deftest t71_l511 (is ((fn [n] (<= 5000 n)) v70_l509)))


(def v73_l549 (count pj/plot-option-docs))


(deftest t74_l551 (is ((fn [n] (= 13 n)) v73_l549)))


(def v76_l569 (count pj/layer-option-docs))


(deftest t77_l571 (is ((fn [n] (pos? n)) v76_l569)))
