(ns
 napkinsketch-book.glossary-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
 (def
  my-sketch
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v4_l29 (kind/pprint my-sketch))


(deftest
 t5_l31
 (is ((fn [v] (and (sk/sketch? v) (= 1 (count (:entries v))))) v4_l29)))


(def
 v7_l44
 (def
  my-sketch
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/options {:title "Iris"}))))


(def v8_l49 (sk/sketch? my-sketch))


(deftest t9_l51 (is ((fn [v] (true? v)) v8_l49)))


(def v10_l53 (count (:entries my-sketch)))


(deftest t11_l55 (is ((fn [n] (= 1 n)) v10_l53)))


(def
 v13_l89
 (def
  tips
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}))


(def
 v14_l93
 (->
  tips
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 1 :y0s])))


(deftest t15_l98 (is ((fn [y0s] (every? pos? y0s)) v14_l93)))


(def
 v17_l117
 (merge
  (method/lookup :point)
  {:color :species, :size :petal_length, :alpha 0.7}))


(deftest
 t18_l119
 (is
  ((fn
    [m]
    (and
     (= :species (:color m))
     (= :petal_length (:size m))
     (= 0.7 (:alpha m))))
   v17_l117)))


(def
 v20_l130
 (->
  data/iris
  (sk/lay-line :sepal_length :sepal_width {:group :species})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups])
  count))


(deftest t21_l136 (is ((fn [n] (= 3 n)) v20_l130)))


(def
 v23_l145
 (->
  {:x [1 2 3], :y [4 5 6]}
  (sk/lay-point :x :y {:nudge-x 0.5})
  sk/plan
  (get-in [:panels 0 :layers 0 :groups 0 :xs])))


(deftest t24_l150 (is ((fn [xs] (= [1.5 2.5 3.5] xs)) v23_l145)))


(def v26_l161 (merge (method/lookup :point) {:jitter true}))


(deftest t27_l163 (is ((fn [m] (true? (:jitter m))) v26_l161)))


(def v29_l175 (def my-plan (sk/plan my-sketch)))


(def v30_l177 (sort (keys my-plan)))


(deftest t31_l179 (is ((fn [ks] (every? keyword? ks)) v30_l177)))


(def v33_l187 (sort (keys (first (:panels my-plan)))))


(deftest
 t34_l189
 (is ((fn [ks] (some #{:y-domain :x-domain :layers} ks)) v33_l187)))


(def v36_l197 (-> my-sketch sk/plan (get-in [:panels 0 :layers 0])))


(deftest t37_l201 (is ((fn [m] (= :point (:mark m))) v36_l197)))


(def
 v39_l216
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/facet :species)
  sk/plan
  :panels
  count))


(deftest t40_l221 (is ((fn [n] (= 3 n)) v39_l216)))


(def
 v42_l230
 (let
  [p (first (:panels my-plan))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t43_l234
 (is
  ((fn
    [m]
    (and (= 2 (count (:x-domain m))) (number? (first (:x-domain m)))))
   v42_l230)))


(def v45_l280 (:mark (sk/rule-h 5)))


(deftest t46_l282 (is ((fn [m] (= :rule-h m)) v45_l280)))


(def v48_l291 (:legend my-plan))


(deftest
 t49_l293
 (is ((fn [leg] (and (map? leg) (contains? leg :entries))) v48_l291)))


(def
 v51_l302
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
