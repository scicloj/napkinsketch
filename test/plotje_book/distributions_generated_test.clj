(ns
 plotje-book.distributions-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l19
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t4_l22
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v3_l19)))


(def
 v6_l31
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram :sepal-length {:color :species})))


(deftest
 t7_l34
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v6_l31)))


(def
 v9_l43
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :petal-width)))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v9_l43)))


(def
 v12_l53
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-histogram :total-bill)
  (pj/options
   {:title "Distribution of Total Bill", :x-label "Amount ($)"})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (pos? (:polygons s))
      (some
       (fn* [p1__84331#] (= "Distribution of Total Bill" p1__84331#))
       (:texts s)))))
   v12_l53)))


(def
 v15_l70
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram :sepal-length {:normalize :density, :alpha 0.5})
  pj/lay-density))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v15_l70)))


(def
 v18_l83
 (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-length)))


(deftest
 t19_l86
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v18_l83)))


(def
 v21_l95
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:color :species})))


(deftest
 t22_l98
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l95)))


(def
 v24_l107
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:bandwidth 0.3})))


(deftest
 t25_l110
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l107)))


(def
 v27_l121
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length)
  pj/lay-rug))


(deftest
 t28_l125
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)) (= 150 (:lines s)))))
   v27_l121)))


(def
 v30_l135
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t31_l138
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v30_l135)))


(def
 v33_l148
 (let
  [plan
   (->
    (rdatasets/datasets-iris)
    (pj/lay-boxplot :species :sepal-width)
    pj/plan)
   box-layer
   (first
    (filter
     (fn* [p1__84332#] (= :boxplot (:mark p1__84332#)))
     (:layers (first (:panels plan)))))]
  (mapv
   (fn
    [{:keys [q1 q3 whisker-lo whisker-hi outliers]}]
    (let
     [iqr
      (- q3 q1)
      lo-fence
      (- q1 (* 1.5 iqr))
      hi-fence
      (+ q3 (* 1.5 iqr))]
     {:whisker-lo-in-fence (>= whisker-lo lo-fence),
      :whisker-hi-in-fence (<= whisker-hi hi-fence),
      :outliers-outside-fence
      (every? (fn [o] (or (< o lo-fence) (> o hi-fence))) outliers)}))
   (:boxes box-layer))))


(deftest
 t34_l164
 (is
  ((fn
    [results]
    (and
     (= 3 (count results))
     (every?
      (fn
       [r]
       (and
        (:whisker-lo-in-fence r)
        (:whisker-hi-in-fence r)
        (:outliers-outside-fence r)))
      results)))
   v33_l148)))


(def
 v36_l176
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-boxplot :day :total-bill {:color :smoker})))


(deftest
 t37_l179
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v36_l176)))


(def
 v39_l187
 (let
  [plan
   (->
    (rdatasets/reshape2-tips)
    (pj/lay-boxplot :day :total-bill {:color :smoker})
    pj/plan)
   panel
   (first (:panels plan))
   box-layer
   (first
    (filter
     (fn* [p1__84333#] (= :boxplot (:mark p1__84333#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t40_l195 (is ((fn [v] (= 2 v)) v39_l187)))


(def
 v42_l202
 (->
  (rdatasets/datasets-iris)
  (pj/lay-boxplot :species :sepal-width)
  (pj/coord :flip)))


(deftest
 t43_l206
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v42_l202)))


(def
 v45_l217
 (-> (rdatasets/reshape2-tips) (pj/lay-violin :day :total-bill)))


(deftest
 t46_l220
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v45_l217)))


(def
 v48_l229
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-violin :day :total-bill {:color :smoker})))


(deftest
 t49_l232
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v48_l229)))


(def
 v51_l239
 (let
  [plan
   (->
    (rdatasets/reshape2-tips)
    (pj/lay-violin :day :total-bill {:color :smoker})
    pj/plan)
   panel
   (first (:panels plan))
   viol-layer
   (first
    (filter
     (fn* [p1__84334#] (= :violin (:mark p1__84334#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t52_l247 (is ((fn [v] (= 2 v)) v51_l239)))


(def
 v54_l252
 (->
  (rdatasets/datasets-iris)
  (pj/lay-violin :species :petal-length)
  (pj/coord :flip)))


(deftest
 t55_l256
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v54_l252)))


(def
 v57_l266
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length)))


(deftest
 t58_l269
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v57_l266)))


(def
 v60_l278
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length {:color :species})))


(deftest
 t61_l281
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v60_l278)))


(def
 v63_l292
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]))


(deftest
 t64_l294
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v63_l292)))


(def
 v66_l301
 (pj/lay-density
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]
  {:color :species}))


(deftest
 t67_l303
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v66_l301)))
