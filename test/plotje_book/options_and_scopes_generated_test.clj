(ns
 plotje-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l41
 (defn
  pose-summary
  "Print pose structure without :data (for readability)."
  [pose]
  (-> (select-keys pose [:mapping :layers :opts]) kind/pprint)))


(def
 v5_l71
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})))


(deftest
 t6_l75
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v5_l71)))


(def
 v8_l79
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  pose-summary))


(deftest
 t9_l84
 (is
  ((fn [m] (= :species (get-in m [:layers 0 :mapping :color])))
   v8_l79)))


(def
 v11_l141
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})
  (pj/coord :flip)))


(deftest
 t12_l147
 (is ((fn [v] (some #{"Iris"} (:texts (pj/svg-summary v)))) v11_l141)))


(def
 v14_l151
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})
  (pj/coord :flip)
  pose-summary))


(deftest
 t15_l158
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v14_l151)))


(def v17_l220 (select-keys (pj/config) [:width :height :margin]))


(deftest
 t18_l222
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l220)))


(def
 v20_l234
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point {:color :species})
   (pj/options {:title "Iris measurements"})
   (pj/coord :flip))))


(def v22_l245 demo)


(deftest
 t23_l247
 (is
  ((fn [v] (some #{"Iris measurements"} (:texts (pj/svg-summary v))))
   v22_l245)))


(def v25_l251 (pose-summary demo))


(deftest
 t26_l253
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v25_l251)))
