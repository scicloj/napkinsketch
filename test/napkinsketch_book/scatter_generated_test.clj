(ns
 napkinsketch-book.scatter-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l19
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l22
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v3_l19)))


(def
 v6_l31
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l34
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v6_l31)))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v9_l43)))


(def
 v12_l55
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "#E74C3C"})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v12_l55)))


(def
 v15_l66
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :day})
  (sk/options
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 244 (:points s))
      (>= (:width s) 700)
      (some #{"Tips by Day"} (:texts s)))))
   v15_l66)))


(def
 v18_l84
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :day, :size :size})))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v18_l84)))


(def
 v21_l93
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point
   :total-bill
   :tip
   {:color :day, :size :size, :alpha 0.6})))


(deftest
 t22_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v21_l93)))


(def
 v24_l105
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :species :sepal-width {:jitter true})))


(deftest
 t25_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v24_l105)))


(def
 v27_l114
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :species :sepal-width {:jitter 10, :alpha 0.5})))


(deftest
 t28_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l114)))


(def
 v30_l126
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t31_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v30_l126)))


(def
 v33_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point
   :sepal-length
   :sepal-width
   {:color :petal-length, :size :petal-width, :alpha 0.7})))


(deftest
 t34_l139
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v33_l136)))


(def
 v36_l150
 (def cols [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v37_l152
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols) {:color :species})))


(deftest
 t38_l155
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v37_l152)))
