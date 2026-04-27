(ns
 plotje-book.troubleshooting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (tc/column-names (rdatasets/datasets-iris)))


(deftest t4_l35 (is ((fn [v] (some #{:sepal-length} v)) v3_l33)))


(def
 v6_l51
 (-> (rdatasets/datasets-iris) (pj/pose :species :sepal-width)))


(deftest
 t7_l54
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v6_l51)))


(def
 v9_l58
 (-> (rdatasets/datasets-iris) (pj/lay-point :species :sepal-width)))


(deftest
 t10_l61
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v9_l58)))


(def
 v12_l102
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (pj/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t13_l105
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v12_l102)))


(def
 v15_l128
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.1})
  (pj/scale :y :log)))


(deftest
 t16_l132
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v15_l128)))


(def
 v18_l150
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t19_l153
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v18_l150)))


(def
 v21_l165
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/lay-bar :species)
   (pj/scale :x :log)
   pj/plan)
  (catch Exception e (.getMessage e))))


(deftest
 t22_l172
 (is
  ((fn [msg] (and (string? msg) (re-find #"[Ll]og scale" msg)))
   v21_l165)))


(def
 v24_l188
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t25_l193
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v24_l188)))


(def
 v27_l213
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t28_l217
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v27_l213)))


(def
 v30_l233
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t31_l237
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v30_l233)))


(def
 v33_l255
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-text
   {:data
    (->
     (tc/dataset {:sepal-length [6.5], :species ["mean"]})
     (tc/add-column :yy (constantly 3.5))),
    :x :sepal-length,
    :y :yy,
    :text :species})))


(deftest
 t34_l262
 (is ((fn [v] (some #{"mean"} (:texts (pj/svg-summary v)))) v33_l255)))


(def
 v36_l279
 (def template (-> (pj/pose nil {:x :x, :y :y}) pj/lay-point)))


(def v37_l283 (-> template (pj/with-data {:x [1 2 3], :y [4 5 6]})))


(deftest
 t38_l286
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v37_l283)))


(def
 v40_l303
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (tc/dataset)
  (tc/order-by [:value] :asc)
  (pj/lay-value-bar :category :value)
  (pj/coord :flip)))


(deftest
 t41_l311
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v40_l303)))


(def
 v43_l333
 (->
  {:x (concat (range 5) (range 5)),
   :y [1 2 3 4 5 2 2 2 3 3],
   :group (concat (repeat 5 "A") (repeat 5 "B"))}
  (pj/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t44_l338
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v43_l333)))
