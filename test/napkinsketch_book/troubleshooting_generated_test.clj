(ns
 napkinsketch-book.troubleshooting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (tc/column-names (rdatasets/datasets-iris)))


(deftest t4_l35 (is ((fn [v] (some #{:sepal-length} v)) v3_l33)))


(def
 v6_l51
 (-> (rdatasets/datasets-iris) (sk/frame :species :sepal-width)))


(deftest
 t7_l54
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v6_l51)))


(def
 v9_l58
 (-> (rdatasets/datasets-iris) (sk/lay-point :species :sepal-width)))


(deftest
 t10_l61
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v9_l58)))


(def
 v12_l102
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (sk/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t13_l105
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l102)))


(def
 v15_l128
 (->
  (rdatasets/ggplot2-diamonds)
  (sk/lay-point :carat :price {:alpha 0.1})
  (sk/scale :y :log)))


(deftest
 t16_l132
 (is ((fn [v] (pos? (:points (sk/svg-summary v)))) v15_l128)))


(def
 v18_l150
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t19_l153
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v18_l150)))


(def
 v21_l165
 (try
  (->
   (rdatasets/datasets-iris)
   (sk/lay-bar :species)
   (sk/scale :x :log)
   sk/plan)
  (catch Exception e (.getMessage e))))


(deftest
 t22_l172
 (is
  ((fn [msg] (and (string? msg) (re-find #"[Ll]og scale" msg)))
   v21_l165)))


(def
 v24_l185
 (->
  (rdatasets/datasets-chickwts)
  (sk/frame :feed)
  sk/lay-bar
  (sk/coord :polar)))


(deftest
 t25_l190
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v24_l185)))


(def
 v27_l208
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:tooltip true})))


(deftest
 t28_l212
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v27_l208)))


(def
 v30_l228
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t31_l232
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v30_l228)))


(def
 v33_l250
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-text
   {:data
    (->
     (tc/dataset {:sepal-length [6.5], :species ["mean"]})
     (tc/add-column :yy (constantly 3.5))),
    :x :sepal-length,
    :y :yy,
    :text :species})))


(deftest
 t34_l257
 (is ((fn [v] (some #{"mean"} (:texts (sk/svg-summary v)))) v33_l250)))


(def
 v36_l278
 (def template (-> (sk/frame nil {:x :x, :y :y}) sk/lay-point)))


(def v37_l282 (-> template (sk/with-data {:x [1 2 3], :y [4 5 6]})))


(deftest
 t38_l285
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v37_l282)))


(def
 v40_l302
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (tc/dataset)
  (tc/order-by [:value] :asc)
  (sk/lay-value-bar :category :value)
  (sk/coord :flip)))


(deftest
 t41_l310
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v40_l302)))


(def
 v43_l332
 (->
  {:x (concat (range 5) (range 5)),
   :y [1 2 3 4 5 2 2 2 3 3],
   :group (concat (repeat 5 "A") (repeat 5 "B"))}
  (sk/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t44_l337
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v43_l332)))
