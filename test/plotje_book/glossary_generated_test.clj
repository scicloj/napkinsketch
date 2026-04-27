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
 v7_l116
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l120
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l125 (is ((fn [y0s] (every? pos? y0s)) v8_l120)))


(def v11_l139 (-> my-pose pj/draft kind/pprint))


(deftest
 t12_l141
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l139)))


(def
 v14_l163
 (merge
  (pj/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l165
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l163)))


(def
 v17_l176
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l182 (is ((fn [n] (= 3 n)) v17_l176)))


(def
 v20_l191
 (->
  {:x [1 2 3], :y [4 5 6]}
  (pj/lay-point :x :y {:nudge-x 0.5})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l196 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l191)))


(def v23_l208 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t24_l210 (is ((fn [m] (true? (:jitter m))) v23_l208)))


(def
 v26_l222
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t27_l226
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v26_l222)))


(def v29_l239 (def my-plan (pj/plan my-pose)))


(def v30_l241 (sort (keys my-plan)))


(deftest t31_l243 (is ((fn [ks] (every? keyword? ks)) v30_l241)))


(def v33_l252 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l254
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l252)))


(def v36_l264 (-> my-pose :layers first :layer-type))


(deftest t37_l266 (is ((fn [k] (= :point k)) v36_l264)))


(def v39_l274 (-> my-pose pj/plan (get-in [:panels 0 :layers 0])))


(deftest t40_l278 (is ((fn [m] (= :point (:mark m))) v39_l274)))


(def
 v42_l325
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l329
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l325)))


(def
 v45_l341
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t46_l343
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v45_l341)))


(def
 v48_l391
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)
  pj/plan
  :panels
  count))


(deftest t49_l396 (is ((fn [n] (= 3 n)) v48_l391)))


(def
 v51_l416
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})
  :layers
  (nth 1)
  :layer-type))


(deftest t52_l421 (is ((fn [m] (= :rule-h m)) v51_l416)))


(def v54_l430 (:legend my-plan))


(deftest
 t55_l432
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v54_l430)))


(def
 v57_l451
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  pj/svg-summary
  :points))


(deftest t58_l456 (is ((fn [n] (= 150 n)) v57_l451)))


(def v60_l469 (def my-membrane (pj/plan->membrane my-plan)))


(def v61_l471 (vector? my-membrane))


(deftest t62_l473 (is (true? v61_l471)))


(def v63_l475 (count my-membrane))


(deftest t64_l477 (is ((fn [n] (pos? n)) v63_l475)))


(def v66_l487 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v67_l489 (first my-plot))


(deftest t68_l491 (is ((fn [v] (= :svg v)) v67_l489)))


(def v70_l503 (count (c2d/find-palette #".*")))


(deftest t71_l505 (is ((fn [n] (<= 5000 n)) v70_l503)))


(def v73_l543 (count pj/plot-option-docs))


(deftest t74_l545 (is ((fn [n] (= 13 n)) v73_l543)))


(def v76_l563 (count pj/layer-option-docs))


(deftest t77_l565 (is ((fn [n] (pos? n)) v76_l563)))
