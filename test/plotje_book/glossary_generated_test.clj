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
 v7_l126
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l130
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l135 (is ((fn [y0s] (every? pos? y0s)) v8_l130)))


(def v11_l149 (-> my-pose pj/draft kind/pprint))


(deftest
 t12_l151
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l149)))


(def
 v14_l181
 (merge
  (pj/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l183
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l181)))


(def
 v17_l194
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l200 (is ((fn [n] (= 3 n)) v17_l194)))


(def
 v20_l209
 (->
  {:x [1 2 3], :y [4 5 6]}
  (pj/lay-point :x :y {:nudge-x 0.5})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l214 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l209)))


(def v23_l226 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t24_l228 (is ((fn [m] (true? (:jitter m))) v23_l226)))


(def
 v26_l240
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t27_l244
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v26_l240)))


(def v29_l257 (def my-plan (pj/plan my-pose)))


(def v30_l259 (sort (keys my-plan)))


(deftest t31_l261 (is ((fn [ks] (every? keyword? ks)) v30_l259)))


(def v33_l270 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l272
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l270)))


(def v36_l282 (-> my-pose :layers first :layer-type))


(deftest t37_l284 (is ((fn [k] (= :point k)) v36_l282)))


(def v39_l292 (-> my-pose pj/plan (get-in [:panels 0 :layers 0])))


(deftest t40_l296 (is ((fn [m] (= :point (:mark m))) v39_l292)))


(def
 v42_l343
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l347
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l343)))


(def
 v45_l359
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t46_l361
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v45_l359)))


(def
 v48_l409
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)
  pj/plan
  :panels
  count))


(deftest t49_l414 (is ((fn [n] (= 3 n)) v48_l409)))


(def
 v51_l434
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})
  :layers
  (nth 1)
  :layer-type))


(deftest t52_l439 (is ((fn [m] (= :rule-h m)) v51_l434)))


(def v54_l448 (:legend my-plan))


(deftest
 t55_l450
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v54_l448)))


(def
 v57_l469
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  pj/svg-summary
  :points))


(deftest t58_l474 (is ((fn [n] (= 150 n)) v57_l469)))


(def v60_l487 (def my-membrane (pj/plan->membrane my-plan)))


(def v61_l489 (vector? my-membrane))


(deftest t62_l491 (is (true? v61_l489)))


(def v63_l493 (count my-membrane))


(deftest t64_l495 (is ((fn [n] (pos? n)) v63_l493)))


(def v66_l505 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v67_l507 (first my-plot))


(deftest t68_l509 (is ((fn [v] (= :svg v)) v67_l507)))


(def v70_l521 (count (c2d/find-palette #".*")))


(deftest t71_l523 (is ((fn [n] (<= 5000 n)) v70_l521)))


(def v73_l561 (count pj/plot-option-docs))


(deftest t74_l563 (is ((fn [n] (= 13 n)) v73_l561)))


(def v76_l581 (count pj/layer-option-docs))


(deftest t77_l583 (is ((fn [n] (pos? n)) v76_l581)))
