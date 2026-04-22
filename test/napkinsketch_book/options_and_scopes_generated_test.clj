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
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__218807#] (dissoc p1__218807# :data))))
   kind/pprint)))


(def
 v5_l72
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t6_l75
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v5_l72)))


(def
 v8_l79
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary))


(deftest
 t9_l83
 (is
  ((fn
    [m]
    (= :species (get-in m [:views 0 :layers 0 :mapping :color])))
   v8_l79)))


(def
 v11_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)))


(deftest
 t12_l141
 (is ((fn [v] (some #{"Iris"} (:texts (sk/svg-summary v)))) v11_l136)))


(def
 v14_l145
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t15_l151
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v14_l145)))


(def v17_l213 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t18_l215
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l213)))


(def
 v20_l227
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v22_l237 demo)


(deftest
 t23_l239
 (is
  ((fn [v] (some #{"Iris measurements"} (:texts (sk/svg-summary v))))
   v22_l237)))


(def v25_l243 (sk-summary demo))


(deftest
 t26_l245
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v25_l243)))
