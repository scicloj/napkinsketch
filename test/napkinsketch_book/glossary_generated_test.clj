(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v3_l24
 (def
  views
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v4_l28 (kind/pprint views))


(deftest
 t5_l30
 (is
  ((fn [v] (and (sk/plot-spec? v) (= 1 (count (sk/views-of v)))))
   v4_l28)))


(def
 v7_l43
 (def
  spec
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/options {:title "Iris"}))))


(def v8_l48 (sk/plot-spec? spec))


(deftest t9_l50 (is ((fn [v] (true? v)) v8_l48)))


(def v10_l52 (count (sk/views-of spec)))


(deftest t11_l54 (is ((fn [n] (= 1 n)) v10_l52)))


(def
 v13_l88
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l92
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l97 (is ((fn [y0s] (every? pos? y0s)) v14_l92)))


(def
 v17_l116
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t18_l118
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v17_l116)))


(def
 v20_l129
 (->
  data/iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l135 (is ((fn [n] (= 3 n)) v20_l129)))


(def
 v23_l144
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/sketch
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l149 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l144)))


(def v26_l160 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l162 (is ((fn [m] (true? (:jitter m))) v26_l160)))


(def v29_l174 (def my-sketch (sk/sketch views)))


(def v30_l176 (sort (keys my-sketch)))


(deftest t31_l178 (is ((fn [ks] (every? keyword? ks)) v30_l176)))


(def v33_l186 (sort (keys (first (:panels my-sketch)))))


(deftest
 t34_l188
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l186)))


(def v36_l196 (-> views sk/sketch (get-in [:panels 0 :layers 0])))


(deftest t37_l200 (is ((fn [m] (= :point (:mark m))) v36_l196)))


(def
 v39_l215
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/sketch
  :panels
  count))


(deftest t40_l220 (is ((fn [n] (= 3 n)) v39_l215)))


(def
 v42_l229
 (let
  [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l233
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l229)))


(def v45_l279 (:mark (sk/rule-h 5)))


(deftest t46_l281 (is ((fn [m] (= :rule-h m)) v45_l279)))


(def v48_l290 (:legend my-sketch))


(deftest
 t49_l292
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l290)))


(def
 v51_l301
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:theme
    {:background "#2d2d2d",
     :grid "#444444",
     :text "#cccccc",
     :tick "#999999"}})
  sk/svg-summary
  :panels))


(deftest t52_l307 (is ((fn [n] (= 1 n)) v51_l301)))


(def v54_l320 (def my-membrane (sk/sketch->membrane my-sketch)))


(def v55_l322 (vector? my-membrane))


(deftest t56_l324 (is ((fn [v] (true? v)) v55_l322)))


(def v57_l326 (count my-membrane))


(deftest t58_l328 (is ((fn [n] (pos? n)) v57_l326)))


(def v60_l338 (def my-figure (sk/sketch->figure my-sketch :svg {})))


(def v61_l340 (first my-figure))


(deftest t62_l342 (is ((fn [v] (= :svg v)) v61_l340)))


(def v64_l389 (count sk/plot-option-docs))


(deftest t65_l391 (is ((fn [n] (= 6 n)) v64_l389)))


(def v67_l406 (count sk/layer-option-docs))


(deftest t68_l408 (is ((fn [n] (= 17 n)) v67_l406)))
