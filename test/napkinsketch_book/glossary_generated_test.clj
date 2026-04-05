(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]])
 (:import [scicloj.napkinsketch.impl.blueprint Blueprint]))


(def
 v3_l27
 (def
  my-blueprint
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))))


(def v4_l31 (kind/pprint my-blueprint))


(deftest
 t5_l33
 (is
  ((fn [v] (and (instance? Blueprint v) (= 1 (count (:entries v)))))
   v4_l31)))


(def
 v7_l46
 (def
  my-sketch
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-options {:title "Iris"}))))


(def v8_l51 (instance? Blueprint my-sketch))


(deftest t9_l53 (is ((fn [v] (true? v)) v8_l51)))


(def v10_l55 (count (:entries my-sketch)))


(deftest t11_l57 (is ((fn [n] (= 1 n)) v10_l55)))


(def
 v13_l91
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l95
 (->
  tips
  (sk/xkcd7-lay-value-bar :day :count {:color :meal, :position :stack})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l100 (is ((fn [y0s] (every? pos? y0s)) v14_l95)))


(def
 v17_l119
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t18_l121
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v17_l119)))


(def
 v20_l132
 (->
  data/iris
  (sk/xkcd7-lay-line :sepal_length :sepal_width {:group :species})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l138 (is ((fn [n] (= 3 n)) v20_l132)))


(def
 v23_l147
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/xkcd7-lay-point :x :y {:nudge-x 0.5})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l152 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l147)))


(def v26_l163 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l165 (is ((fn [m] (true? (:jitter m))) v26_l163)))


(def v29_l177 (def my-plan (sk/xkcd7-plan my-blueprint)))


(def v30_l179 (sort (keys my-plan)))


(deftest t31_l181 (is ((fn [ks] (every? keyword? ks)) v30_l179)))


(def v33_l189 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l191
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l189)))


(def
 v36_l199
 (-> my-blueprint sk/xkcd7-plan (get-in [:panels 0 :layers 0])))


(deftest t37_l203 (is ((fn [m] (= :point (:mark m))) v36_l199)))


(def
 v39_l218
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-facet :species)
  sk/xkcd7-plan
  :panels
  count))


(deftest t40_l223 (is ((fn [n] (= 3 n)) v39_l218)))


(def
 v42_l232
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l236
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l232)))


(def v45_l282 (:mark (sk/rule-h 5)))


(deftest t46_l284 (is ((fn [m] (= :rule-h m)) v45_l282)))


(def v48_l293 (:legend my-plan))


(deftest
 t49_l295
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l293)))


(def
 v51_l304
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t52_l310 (is ((fn [n] (= 1 n)) v51_l304)))


(def v54_l323 (def my-membrane (sk/plan->membrane my-plan)))


(def v55_l325 (vector? my-membrane))


(deftest t56_l327 (is ((fn [v] (true? v)) v55_l325)))


(def v57_l329 (count my-membrane))


(deftest t58_l331 (is ((fn [n] (pos? n)) v57_l329)))


(def v60_l341 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v61_l343 (first my-figure))


(deftest t62_l345 (is ((fn [v] (= :svg v)) v61_l343)))


(def v64_l392 (count sk/plot-option-docs))


(deftest t65_l394 (is ((fn [n] (= 6 n)) v64_l392)))


(def v67_l409 (count sk/layer-option-docs))


(deftest t68_l411 (is ((fn [n] (= 20 n)) v67_l409)))
