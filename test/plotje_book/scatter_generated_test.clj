(ns
 plotje-book.scatter-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l19
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t4_l22
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v3_l19)))


(def
 v6_l31
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l34
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v6_l31)))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t10_l46
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (zero? (:lines s)))))
   v9_l43)))


(def
 v12_l55
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color "#E74C3C"})))


(deftest
 t13_l58
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (contains? (:colors s) "rgb(231,76,60)"))))
   v12_l55)))


(def
 v15_l67
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :day})
  (pj/options
   {:width 700,
    :height 300,
    :title "Tips by Day",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 244 (:points s))
      (>= (:width s) 700)
      (some #{"Tips by Day"} (:texts s)))))
   v15_l67)))


(def
 v18_l85
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :day, :size :size})))


(deftest
 t19_l88
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v18_l85)))


(def
 v21_l94
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point
   :total-bill
   :tip
   {:color :day, :size :size, :alpha 0.6})))


(deftest
 t22_l97
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v21_l94)))


(def
 v24_l106
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :species :sepal-width {:jitter true})))


(deftest
 t25_l109
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v24_l106)))


(def
 v27_l115
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :species :sepal-width {:jitter 10, :alpha 0.5})))


(deftest
 t28_l118
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l115)))


(def
 v30_l127
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t31_l130
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v30_l127)))


(def
 v33_l137
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :petal-length, :size :petal-width, :alpha 0.7})))


(deftest
 t34_l140
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v33_l137)))


(def
 v36_l150
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:shape :species})))


(deftest
 t37_l153
 (is
  ((fn
    [v]
    (let
     [layer
      (-> v pj/plan :panels first :layers first)
      shape-values
      (set (mapcat :shapes (:groups layer)))]
     (= 3 (count shape-values))))
   v36_l150)))


(def
 v39_l166
 (def cols [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v40_l168
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  (pj/pose (pj/cross cols cols))))


(deftest
 t41_l172
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v40_l168)))


(deftest
 t42_l177
 (is
  ((fn
    [v]
    (->>
     (:sub-plots (pj/plan v))
     (every?
      (fn
       [{:keys [path plan]}]
       (let
        [[r c] path mark (-> plan :panels first :layers first :mark)]
        (= mark (if (= r c) :bar :point)))))))
   v40_l168)))
