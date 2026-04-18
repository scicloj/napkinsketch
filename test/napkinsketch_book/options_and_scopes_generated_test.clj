(ns
 napkinsketch-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l39
 (defn
  sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__2895657#] (dissoc p1__2895657# :data))))
   kind/pprint)))


(def
 v5_l69
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk-summary))


(deftest
 t6_l73
 (is
  ((fn
    [m]
    (= :species (get-in m [:views 0 :layers 0 :mapping :color])))
   v5_l69)))


(def
 v8_l122
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t9_l128
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v8_l122)))


(def v11_l178 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t12_l180
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v11_l178)))


(def
 v14_l192
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v16_l202 demo)


(def v18_l206 (sk-summary demo))


(deftest
 t19_l208
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v18_l206)))
