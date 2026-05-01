(ns
 plotje-book.troubleshooting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (tc/column-names (rdatasets/datasets-iris)))


(deftest t4_l31 (is ((fn [v] (some #{:sepal-length} v)) v3_l29)))


(def
 v6_l37
 (try
  (->
   (tc/dataset {"sepal_length" [5.0 6.0], "sepal_width" [3.0 3.5]})
   (pj/pose :sepal_length :sepal_width)
   pj/lay-point
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t7_l43
 (is ((fn [msg] (re-find #"Column :sepal_\w+.*not found" msg)) v6_l37)))


(def
 v9_l54
 (try
  (->
   (tc/dataset {"sepal length" [5.0 6.0], "sepal width" [3.0 3.5]})
   (pj/pose :sepal-length :sepal-width)
   pj/lay-point
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t10_l60
 (is ((fn [msg] (re-find #"Column :sepal-\w+.*not found" msg)) v9_l54)))


(def
 v12_l84
 (-> (rdatasets/datasets-iris) (pj/pose :species :sepal-width)))


(deftest
 t13_l87
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v12_l84)))


(def
 v15_l91
 (-> (rdatasets/datasets-iris) (pj/lay-point :species :sepal-width)))


(deftest
 t16_l94
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v15_l91)))


(def
 v18_l106
 (def
  subject-scores
  {:day [1 2 3 4 1 2 3 4 1 2 3 4],
   :score [3 5 4 6 6 7 5 8 8 9 7 10],
   :subject [1 1 1 1 2 2 2 2 3 3 3 3]}))


(def
 v20_l115
 (-> subject-scores (pj/lay-line :day :score {:color :subject})))


(deftest
 t21_l118
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v20_l115)))


(def
 v23_l123
 (->
  subject-scores
  (pj/lay-line
   :day
   :score
   {:color :subject, :color-type :categorical})))


(deftest
 t24_l126
 (is ((fn [v] (= 3 (:lines (pj/svg-summary v)))) v23_l123)))


(def
 v26_l145
 (try
  (->
   {:hour [9 10 11 12], :count [5 8 12 7]}
   (pj/lay-value-bar :hour :count)
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t27_l151
 (is
  ((fn [msg] (re-find #"requires a categorical column for :x" msg))
   v26_l145)))


(def
 v29_l158
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (pj/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t30_l161
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v29_l158)))


(def
 v32_l184
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.1})
  (pj/scale :y :log)))


(deftest
 t33_l188
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v32_l184)))


(def
 v35_l206
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t36_l209
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v35_l206)))


(def
 v38_l221
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/lay-bar :species)
   (pj/scale :x :log)
   pj/plot)
  (catch Exception e (.getMessage e))))


(deftest
 t39_l228
 (is
  ((fn [msg] (and (string? msg) (re-find #"[Ll]og scale" msg)))
   v38_l221)))


(def
 v41_l243
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t42_l248
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v41_l243)))


(def
 v44_l268
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t45_l272
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v44_l268)))


(def
 v47_l288
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t48_l292
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v47_l288)))


(def
 v50_l310
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
 t51_l317
 (is ((fn [v] (some #{"mean"} (:texts (pj/svg-summary v)))) v50_l310)))


(def
 v53_l334
 (def template (-> (pj/pose nil {:x :x, :y :y}) pj/lay-point)))


(def v54_l338 (-> template (pj/with-data {:x [1 2 3], :y [4 5 6]})))


(deftest
 t55_l341
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v54_l338)))


(def
 v57_l358
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (tc/dataset)
  (tc/order-by [:value] :asc)
  (pj/lay-value-bar :category :value)
  (pj/coord :flip)))


(deftest
 t58_l366
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v57_l358)))


(def
 v60_l388
 (->
  {:x (concat (range 5) (range 5)),
   :y [1 2 3 4 5 2 2 2 3 3],
   :group (concat (repeat 5 "A") (repeat 5 "B"))}
  (pj/lay-area :x :y {:position :stack, :color :group})))


(deftest
 t61_l393
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v60_l388)))


(def
 v63_l412
 (->
  {:cat ["A" "A" "B" "B" "C" "C"],
   :y [10 20 30 40 50 60],
   :group ["a" "b" "a" "b" "a" "b"]}
  (pj/lay-value-bar :cat :y {:color :group, :position :dodge})))


(deftest
 t64_l417
 (is ((fn [v] (= 6 (:polygons (pj/svg-summary v)))) v63_l412)))


(def
 v66_l433
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t67_l438
 (is
  ((fn
    [v]
    (zero?
     (count
      (filter
       #{"soybean"
         "meatmeal"
         "sunflower"
         "horsebean"
         "casein"
         "linseed"}
       (:texts (pj/svg-summary v))))))
   v66_l433)))


(def
 v69_l447
 (-> (rdatasets/datasets-chickwts) (pj/pose :feed) pj/lay-bar))


(deftest
 t70_l451
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
   v69_l447)))
