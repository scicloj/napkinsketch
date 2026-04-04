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
 v3_l26
 (def
  views
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))))


(def v4_l30 (kind/pprint views))


(deftest
 t5_l32
 (is
  ((fn [v] (and (instance? Blueprint v) (= 1 (count (:entries v)))))
   v4_l30)))


(def
 v7_l45
 (def
  my-sketch
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-options {:title "Iris"}))))


(def v8_l50 (instance? Blueprint my-sketch))


(deftest t9_l52 (is ((fn [v] (true? v)) v8_l50)))


(def v10_l54 (count (:entries my-sketch)))


(deftest t11_l56 (is ((fn [n] (= 1 n)) v10_l54)))


(def
 v13_l90
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l94
 (->
  tips
  (sk/xkcd7-lay-value-bar :day :count {:color :meal, :position :stack})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l99 (is ((fn [y0s] (every? pos? y0s)) v14_l94)))


(def
 v17_l118
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t18_l120
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v17_l118)))


(def
 v20_l131
 (->
  data/iris
  (sk/xkcd7-lay-line :sepal_length :sepal_width {:group :species})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l137 (is ((fn [n] (= 3 n)) v20_l131)))


(def
 v23_l146
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/xkcd7-lay-point :x :y {:nudge-x 0.5})
  sk/xkcd7-plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l151 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l146)))


(def v26_l162 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l164 (is ((fn [m] (true? (:jitter m))) v26_l162)))


(def v29_l176 (def my-plan (sk/xkcd7-plan views)))


(def v30_l178 (sort (keys my-plan)))


(deftest t31_l180 (is ((fn [ks] (every? keyword? ks)) v30_l178)))


(def v33_l188 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l190
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l188)))


(def v36_l198 (-> views sk/xkcd7-plan (get-in [:panels 0 :layers 0])))


(deftest t37_l202 (is ((fn [m] (= :point (:mark m))) v36_l198)))


(def
 v39_l217
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  (sk/xkcd7-facet :species)
  sk/xkcd7-plan
  :panels
  count))


(deftest t40_l222 (is ((fn [n] (= 3 n)) v39_l217)))


(def
 v42_l231
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l235
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l231)))


(def v45_l281 (:mark (sk/rule-h 5)))


(deftest t46_l283 (is ((fn [m] (= :rule-h m)) v45_l281)))


(def v48_l292 (:legend my-plan))


(deftest
 t49_l294
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l292)))


(def
 v51_l303
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


(deftest t52_l309 (is ((fn [n] (= 1 n)) v51_l303)))


(def v54_l322 (def my-membrane (sk/plan->membrane my-plan)))


(def v55_l324 (vector? my-membrane))


(deftest t56_l326 (is ((fn [v] (true? v)) v55_l324)))


(def v57_l328 (count my-membrane))


(deftest t58_l330 (is ((fn [n] (pos? n)) v57_l328)))


(def v60_l340 (def my-figure (sk/plan->figure my-plan :svg {})))


(def v61_l342 (first my-figure))


(deftest t62_l344 (is ((fn [v] (= :svg v)) v61_l342)))


(def v64_l391 (count sk/plot-option-docs))


(deftest t65_l393 (is ((fn [n] (= 6 n)) v64_l391)))


(def v67_l408 (count sk/layer-option-docs))


(deftest t68_l410 (is ((fn [n] (= 20 n)) v67_l408)))
