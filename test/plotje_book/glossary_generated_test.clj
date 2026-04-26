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
 v7_l117
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v8_l121
 (->
  tips
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t9_l126 (is ((fn [y0s] (every? pos? y0s)) v8_l121)))


(def v11_l140 (-> my-pose pj/draft kind/pprint))


(deftest
 t12_l142
 (is
  ((fn
    [d]
    (and (vector? d) (= 1 (count d)) (= :point (:mark (first d)))))
   v11_l140)))


(def
 v14_l164
 (merge
  (pj/layer-type-lookup :point)
  {:color :species, :size :petal-length, :alpha 0.7}))


(deftest
 t15_l166
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal-length (:size m))
     (= 0.7 (:alpha m))))
   v14_l164)))


(def
 v17_l177
 (->
  (rdatasets/datasets-iris)
  (pj/lay-line :sepal-length :sepal-width {:group :species})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t18_l183 (is ((fn [n] (= 3 n)) v17_l177)))


(def
 v20_l192
 (->
  {:x [1 2 3], :y [4 5 6]}
  (pj/lay-point :x :y {:nudge-x 0.5})
  pj/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t21_l197 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v20_l192)))


(def v23_l209 (merge (pj/layer-type-lookup :point) {:jitter true}))


(deftest t24_l211 (is ((fn [m] (true? (:jitter m))) v23_l209)))


(def
 v26_l223
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point))


(deftest
 t27_l227
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v26_l223)))


(def v29_l240 (def my-plan (pj/plan my-pose)))


(def v30_l242 (sort (keys my-plan)))


(deftest t31_l244 (is ((fn [ks] (every? keyword? ks)) v30_l242)))


(def v33_l253 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l255
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l253)))


(def v36_l265 (-> my-pose :layers first :layer-type))


(deftest t37_l267 (is ((fn [k] (= :point k)) v36_l265)))


(def v39_l275 (-> my-pose pj/plan (get-in [:panels 0 :layers 0])))


(deftest t40_l279 (is ((fn [m] (= :point (:mark m))) v39_l275)))


(def
 v42_l326
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l330
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l326)))


(def
 v45_l342
 (-> my-plan :panels first :x-ticks (select-keys [:values :labels])))


(deftest
 t46_l344
 (is
  ((fn
    [m]
    (and
     (vector? (:values m))
     (vector? (:labels m))
     (= (count (:values m)) (count (:labels m)))))
   v45_l342)))


(def
 v48_l392
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)
  pj/plan
  :panels
  count))


(deftest t49_l397 (is ((fn [n] (= 3 n)) v48_l392)))


(def
 v51_l417
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-rule-h {:y-intercept 3.0})
  :layers
  (nth 1)
  :layer-type))


(deftest t52_l422 (is ((fn [m] (= :rule-h m)) v51_l417)))


(def v54_l431 (:legend my-plan))


(deftest
 t55_l433
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v54_l431)))


(def
 v57_l452
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  pj/svg-summary
  :points))


(deftest t58_l457 (is ((fn [n] (= 150 n)) v57_l452)))


(def v60_l470 (def my-membrane (pj/plan->membrane my-plan)))


(def v61_l472 (vector? my-membrane))


(deftest t62_l474 (is (true? v61_l472)))


(def v63_l476 (count my-membrane))


(deftest t64_l478 (is ((fn [n] (pos? n)) v63_l476)))


(def v66_l488 (def my-plot (pj/plan->plot my-plan :svg {})))


(def v67_l490 (first my-plot))


(deftest t68_l492 (is ((fn [v] (= :svg v)) v67_l490)))


(def v70_l504 (count (c2d/find-palette #".*")))


(deftest t71_l506 (is ((fn [n] (< 1000 n)) v70_l504)))


(def v73_l544 (count pj/plot-option-docs))


(deftest t74_l546 (is ((fn [n] (= 13 n)) v73_l544)))


(def v76_l564 (count pj/layer-option-docs))


(deftest t77_l566 (is ((fn [n] (pos? n)) v76_l564)))
