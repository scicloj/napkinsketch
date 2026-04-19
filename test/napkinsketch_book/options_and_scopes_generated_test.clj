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
    (partial mapv (fn* [p1__82404#] (dissoc p1__82404# :data))))
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
 v11_l138
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)))


(deftest
 t12_l143
 (is ((fn [v] (some #{"Iris"} (:texts (sk/svg-summary v)))) v11_l138)))


(def
 v14_l147
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/coord :flip)
  sk-summary))


(deftest
 t15_l153
 (is
  ((fn
    [m]
    (and
     (= "Iris" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v14_l147)))


(def v17_l215 (select-keys (sk/config) [:width :height :margin]))


(deftest
 t18_l217
 (is
  ((fn
    [m]
    (and
     (number? (:width m))
     (number? (:height m))
     (number? (:margin m))))
   v17_l215)))


(def
 v20_l229
 (def
  demo
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/options {:title "Iris measurements"})
   (sk/coord :flip))))


(def v22_l239 demo)


(def v24_l243 (sk-summary demo))


(deftest
 t25_l245
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))
   v24_l243)))
