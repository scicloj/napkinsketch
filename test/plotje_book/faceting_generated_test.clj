(ns
 plotje-book.faceting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l20
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)))


(deftest
 t4_l24
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      panels
      (:panels
       (pj/plan
        (->
         (rdatasets/datasets-iris)
         (pj/lay-point :sepal-length :sepal-width {:color :species})
         (pj/facet :species))))
      x-doms
      (mapv :x-domain panels)
      y-doms
      (mapv :y-domain panels)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (apply = x-doms)
      (apply = y-doms))))
   v3_l20)))


(def
 v6_l48
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species :row)))


(deftest
 t7_l52
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v6_l48)))


(def
 v9_l60
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :sex})
  (pj/facet-grid :smoker :sex)))


(deftest
 t10_l64
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v9_l60)))


(def
 v12_l72
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram :sepal-length {:color :species})
  (pj/facet :species)))


(deftest
 t13_l76
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v12_l72)))


(def
 v15_l84
 (->
  (rdatasets/reshape2-tips)
  (pj/pose :total-bill :tip {:color :sex})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/facet-grid :smoker :sex)))


(deftest
 t16_l90
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v15_l84)))


(def
 v18_l102
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)
  (pj/options {:scales :shared})))


(deftest
 t19_l107
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      doms
      (mapv
       :x-domain
       (:panels
        (pj/plan
         (->
          (rdatasets/datasets-iris)
          (pj/lay-point :sepal-length :sepal-width)
          (pj/facet :species)))))]
     (and (= 3 (:panels s)) (= 150 (:points s)) (apply = doms))))
   v18_l102)))


(def
 v21_l121
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)
  (pj/options {:scales :free-y})))


(deftest
 t22_l126
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      doms
      (mapv
       :y-domain
       (:panels
        (pj/plan
         (->
          (rdatasets/datasets-iris)
          (pj/lay-point :sepal-length :sepal-width)
          (pj/facet :species)
          (pj/options {:scales :free-y})))))]
     (and (= 3 (:panels s)) (= 3 (count (distinct doms))))))
   v21_l121)))


(def
 v24_l151
 (pj/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]
  {:color :species}))


(deftest
 t25_l153
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v24_l151)))


(def
 v27_l159
 (->
  (rdatasets/palmerpenguins-penguins)
  (pj/lay-bar :species {:color :species})
  (pj/facet :island)))


(deftest
 t28_l163
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 5 (:polygons s)))))
   v27_l159)))


(def
 v30_l171
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/facet :species)
  (pj/options
   {:title "Iris by Species",
    :x-label "Sepal Length (cm)",
    :y-label "Sepal Width (cm)"})))


(deftest
 t31_l177
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
   v30_l171)))
