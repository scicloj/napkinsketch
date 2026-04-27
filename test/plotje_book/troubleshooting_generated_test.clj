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
 v6_l53
 (let
  [string-keyed
   (->
    (rdatasets/datasets-iris)
    (assoc
     "species-str"
     (mapv str ((rdatasets/datasets-iris) :species))))]
  (->
   string-keyed
   (pj/pose {:color "species-str"})
   (pj/lay-point :sepal-length :sepal-width))))


(deftest
 t7_l61
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< 1 (count (:colors s))))))
   v6_l53)))


(def
 v9_l82
 (-> (rdatasets/datasets-iris) (pj/pose :species :sepal-width)))


(deftest
 t10_l85
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v9_l82)))


(def
 v12_l89
 (-> (rdatasets/datasets-iris) (pj/lay-point :species :sepal-width)))


(deftest
 t13_l92
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v12_l89)))


(def
 v15_l133
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (pj/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t16_l136
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v15_l133)))


(def
 v18_l159
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.1})
  (pj/scale :y :log)))


(deftest
 t19_l163
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v18_l159)))


(def
 v21_l181
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t22_l184
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v21_l181)))


(def
 v24_l196
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/lay-bar :species)
   (pj/scale :x :log)
   pj/plan)
  (catch Exception e (.getMessage e))))


(deftest
 t25_l203
 (is
  ((fn [msg] (and (string? msg) (re-find #"[Ll]og scale" msg)))
   v24_l196)))


(def
 v27_l219
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t28_l224
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v27_l219)))


(def
 v30_l244
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t31_l248
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v30_l244)))


(def
 v33_l264
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t34_l268
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v33_l264)))


(def
 v36_l286
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
 t37_l293
 (is ((fn [v] (some #{"mean"} (:texts (pj/svg-summary v)))) v36_l286)))


(def
 v39_l310
 (def template (-> (pj/pose nil {:x :x, :y :y}) pj/lay-point)))


(def v40_l314 (-> template (pj/with-data {:x [1 2 3], :y [4 5 6]})))


(deftest
 t41_l317
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v40_l314)))


(def
 v43_l334
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (tc/dataset)
  (tc/order-by [:value] :asc)
  (pj/lay-value-bar :category :value)
  (pj/coord :flip)))


(deftest
 t44_l342
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v43_l334)))


(def
 v46_l364
 (->
  {:x (concat (range 5) (range 5)),
   :y [1 2 3 4 5 2 2 2 3 3],
   :group (concat (repeat 5 "A") (repeat 5 "B"))}
  (pj/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t47_l369
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v46_l364)))


(def
 v49_l388
 (->
  {:cat ["A" "A" "B" "B" "C" "C"],
   :y [10 20 30 40 50 60],
   :group ["a" "b" "a" "b" "a" "b"]}
  (pj/lay-value-bar :cat :y {:color :group, :position :dodge})))


(deftest
 t50_l393
 (is ((fn [v] (= 6 (:polygons (pj/svg-summary v)))) v49_l388)))


(def
 v52_l411
 (-> (rdatasets/datasets-chickwts) (pj/pose :feed) pj/lay-bar))


(deftest
 t53_l415
 (is
  ((fn
    [v]
    (pos?
     (count
      (filter
       #{"soybean"
         "meatmeal"
         "sunflower"
         "horsebean"
         "casein"
         "linseed"}
       (:texts (pj/svg-summary v))))))
   v52_l411)))
