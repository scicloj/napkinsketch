(ns
 napkinsketch-book.scatter-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l19 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


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
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


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
  data/iris
  (sk/lay-point :petal_length :petal_width {:color :species})))


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
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "#E74C3C"})))


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
  data/tips
  (sk/lay-point :total_bill :tip {:color :day})
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
  data/tips
  (sk/lay-point :total_bill :tip {:color :day, :size :size})))


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
  data/tips
  (sk/lay-point
   :total_bill
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
 (-> data/iris (sk/lay-point :species :sepal_width {:jitter true})))


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
  data/iris
  (sk/lay-point :species :sepal_width {:jitter 10, :alpha 0.5})))


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
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


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
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :petal_length, :size :petal_width, :alpha 0.7})))


(deftest
 t34_l139
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v33_l136)))
