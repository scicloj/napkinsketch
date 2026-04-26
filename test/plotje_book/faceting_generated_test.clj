(ns
 plotje-book.faceting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v3_l21)))


(def
 v6_l37
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species :row)))


(deftest
 t7_l41
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v6_l37)))


(def
 v9_l49
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t10_l53
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v9_l49)))


(def
 v12_l61
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram :sepal-length {:color :species})
  (pj/facet :species)))


(deftest
 t13_l65
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v12_l61)))


(def
 v15_l73
 (->
  (rdatasets/reshape2-tips)
  (pj/pose :total-bill :tip {:color :sex})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/facet-grid :smoker :sex)))


(deftest
 t16_l79
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v15_l73)))


(def
 v18_l91
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)
  (pj/options {:scales :shared})))


(deftest
 t19_l96
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v18_l91)))


(def
 v21_l102
 (->>
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width)
   (pj/facet :species)
   pj/plan
   :panels)
  (mapv :x-domain)))


(deftest t22_l109 (is ((fn [doms] (apply = doms)) v21_l102)))


(def
 v24_l113
 (->>
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width)
   (pj/facet :species)
   (pj/options {:scales :free-y})
   pj/plan
   :panels)
  (mapv :y-domain)))


(deftest
 t25_l121
 (is ((fn [doms] (= 3 (count (distinct doms)))) v24_l113)))


(def
 v27_l130
 (def
  faceted-pl
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species})
   (pj/facet :species)
   pj/plan)))


(def v28_l136 (:grid faceted-pl))


(deftest
 t29_l138
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v28_l136)))


(def v30_l140 (count (:panels faceted-pl)))


(deftest t31_l142 (is ((fn [n] (= 3 n)) v30_l140)))


(def v33_l146 (:panels faceted-pl))


(deftest t34_l148 (is ((fn [ps] (= 3 (count ps))) v33_l146)))


(def
 v36_l155
 (def cols [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v37_l157
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  (pj/pose (pj/cross cols cols))))


(deftest
 t38_l161
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v37_l157)))


(def
 v40_l175
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]
  {:color :species}))


(deftest
 t41_l177
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v40_l175)))


(def
 v43_l183
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :species {:color :species})
  (pj/facet :island)))


(deftest
 t44_l187
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 5 (:polygons s)))))
   v43_l183)))


(def
 v46_l195
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)
  (pj/options
   {:title "Iris by Species",
    :x-label "Sepal Length (cm)",
    :y-label "Sepal Width (cm)"})))


(deftest
 t47_l201
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v46_l195)))
