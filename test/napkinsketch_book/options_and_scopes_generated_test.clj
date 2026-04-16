(ns
 napkinsketch-book.options-and-scopes-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (def iris (rdatasets/datasets-iris)))


(def
 v5_l34
 (defn
  sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__2887938#] (dissoc p1__2887938# :data))))
   kind/pprint)))


(def
 v7_l83
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/scale :x :log)
  sk-summary))


(deftest
 t8_l89
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= {:type :log} (get-in m [:opts :x-scale]))))
   v7_l83)))


(def
 v10_l151
 (def
  demo
  (->
   iris
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/scale :x :log))))


(def v12_l159 demo)


(def v14_l163 (sk-summary demo))


(deftest
 t15_l165
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= {:type :log} (get-in m [:opts :x-scale]))))
   v14_l163)))


(def v17_l185 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t18_l187
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l185)))
