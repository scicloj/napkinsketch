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
       (fn* [p1__82627#] (= "Distribution of Total Bill" p1__82627#))
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
 v18_l82
 (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-length)))


(deftest
 t19_l85
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v18_l82)))


(def
 v21_l94
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:color :species})))


(deftest
 t22_l97
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v21_l94)))


(def
 v24_l106
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:bandwidth 0.3})))


(deftest
 t25_l109
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l106)))


(def
 v27_l118
 (-> (rdatasets/datasets-iris) (pj/lay-boxplot :species :sepal-width)))


(deftest
 t28_l121
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v27_l118)))


(def
 v30_l131
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-boxplot :day :total-bill {:color :smoker})))


(deftest
 t31_l134
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)) (pos? (:lines s)))))
   v30_l131)))


(def
 v33_l142
 (let
  [pl
   (->
    (rdatasets/reshape2-tips)
    (pj/lay-boxplot :day :total-bill {:color :smoker})
    pj/plan)
   panel
   (first (:panels pl))
   box-layer
   (first
    (filter
     (fn* [p1__82628#] (= :boxplot (:mark p1__82628#)))
     (:layers panel)))
   cats
   (:color-categories box-layer)]
  (count cats)))


(deftest t34_l150 (is ((fn [v] (= 2 v)) v33_l142)))


(def
 v36_l157
 (->
  (rdatasets/datasets-iris)
  (pj/lay-boxplot :species :sepal-width)
  (pj/coord :flip)))


(deftest
 t37_l161
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)) (pos? (:lines s)))))
   v36_l157)))


(def
 v39_l172
 (-> (rdatasets/reshape2-tips) (pj/lay-violin :day :total-bill)))


(deftest
 t40_l175
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v39_l172)))


(def
 v42_l184
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-violin :day :total-bill {:color :smoker})))


(deftest
 t43_l187
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v42_l184)))


(def
 v45_l194
 (let
  [pl
   (->
    (rdatasets/reshape2-tips)
    (pj/lay-violin :day :total-bill {:color :smoker})
    pj/plan)
   panel
   (first (:panels pl))
   viol-layer
   (first
    (filter
     (fn* [p1__82629#] (= :violin (:mark p1__82629#)))
     (:layers panel)))
   cats
   (:color-categories viol-layer)]
  (count cats)))


(deftest t46_l202 (is ((fn [v] (= 2 v)) v45_l194)))


(def
 v48_l207
 (->
  (rdatasets/datasets-iris)
  (pj/lay-violin :species :petal-length)
  (pj/coord :flip)))


(deftest
 t49_l211
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v48_l207)))


(def
 v51_l221
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length)))


(deftest
 t52_l224
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v51_l221)))


(def
 v54_l233
 (->
  (rdatasets/datasets-iris)
  (pj/lay-ridgeline :species :sepal-length {:color :species})))


(deftest
 t55_l236
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v54_l233)))


(def
 v57_l247
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]))


(deftest
 t58_l249
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v57_l247)))


(def
 v60_l256
 (pj/lay-density
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]
  {:color :species}))


(deftest
 t61_l258
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v60_l256)))
