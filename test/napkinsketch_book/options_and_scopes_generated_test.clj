(ns
 napkinsketch-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l43
 (defn
  sk-summary
  "Print frame structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__166438#] (dissoc p1__166438# :data))))
   kind/pprint)))


(def
 v5_l74
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t6_l77
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v5_l74)))


(def
 v8_l81
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary))


(deftest
 t9_l85
 (is
  ((fn
    [m]
    (= :species (get-in m [:views 0 :layers 0 :mapping :color])))
   v8_l81)))


(def
 v11_l139
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)))


(deftest
 t12_l144
 (is ((fn [v] (some #{"Iris"} (:texts (sk/svg-summary v)))) v11_l139)))


(def
 v14_l148
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t15_l154
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v14_l148)))


(def v17_l216 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t18_l218
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l216)))


(def
 v20_l230
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v22_l240 demo)


(deftest
 t23_l242
 (is
  ((fn [v] (some #{"Iris measurements"} (:texts (sk/svg-summary v))))
   v22_l240)))


(def v25_l246 (sk-summary demo))


(deftest
 t26_l248
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v25_l246)))
