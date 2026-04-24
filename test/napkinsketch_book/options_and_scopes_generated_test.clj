(ns
 napkinsketch-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l41
 (defn
  sk-summary
  "Print frame structure without :data (for readability)."
  [fr]
  (-> (select-keys fr [:mapping :layers :opts]) kind/pprint)))


(def
 v5_l71
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})))


(deftest
 t6_l75
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v5_l71)))


(def
 v8_l79
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  sk-summary))


(deftest
 t9_l84
 (is
  ((fn [m] (= :species (get-in m [:layers 0 :mapping :color])))
   v8_l79)))


(def
 v11_l138
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "Iris"})
  (sk/coord :flip)))


(deftest
 t12_l144
 (is ((fn [v] (some #{"Iris"} (:texts (sk/svg-summary v)))) v11_l138)))


(def
 v14_l148
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t15_l155
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v14_l148)))


(def v17_l217 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t18_l219
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l217)))


(def
 v20_l231
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/frame :sepal-length :sepal-width)
   (sk/lay-point {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v22_l242 demo)


(deftest
 t23_l244
 (is
  ((fn [v] (some #{"Iris measurements"} (:texts (sk/svg-summary v))))
   v22_l242)))


(def v25_l248 (sk-summary demo))


(deftest
 t26_l250
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v25_l248)))
