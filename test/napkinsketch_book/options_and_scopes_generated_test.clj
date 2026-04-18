(ns
 napkinsketch-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l42
 (defn
  sk-summary
  "Print sketch structure without :data and :kindly/f (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__2896044#] (dissoc p1__2896044# :data))))
   kind/pprint)))


(def
 v5_l72
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary))


(deftest
 t6_l76
 (is
  ((fn
    [m]
    (= :species (get-in m [:views 0 :layers 0 :mapping :color])))
   v5_l72)))


(def
 v8_l125
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t9_l131
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v8_l125)))


(def v11_l184 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t12_l186
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v11_l184)))


(def
 v14_l198
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v16_l208 demo)


(def v18_l212 (sk-summary demo))


(deftest
 t19_l214
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v18_l212)))
