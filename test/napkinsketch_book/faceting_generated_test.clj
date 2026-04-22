(ns
 napkinsketch-book.faceting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v3_l21)))


(def
 v6_l37
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species :row)))


(deftest
 t7_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v6_l37)))


(def
 v9_l49
 (->
  (rdatasets/reshape2-tips)
  (sk/lay-point :total-bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t10_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v9_l49)))


(def
 v12_l61
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram :sepal-length {:color :species})
  (sk/facet :species)))


(deftest
 t13_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v12_l61)))


(def
 v15_l73
 (->
  (rdatasets/reshape2-tips)
  (sk/view :total-bill :tip {:color :sex})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/facet-grid :smoker :sex)))


(deftest
 t16_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v15_l73)))


(def
 v18_l91
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :shared})))


(deftest
 t19_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v18_l91)))


(def
 v21_l102
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :free-y})))


(deftest
 t22_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v21_l102)))


(def
 v24_l117
 (def
  faceted-pl
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species})
   (sk/facet :species)
   sk/plan)))


(def v25_l123 (:grid faceted-pl))


(deftest
 t26_l125
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v25_l123)))


(def v27_l127 (count (:panels faceted-pl)))


(deftest t28_l129 (is ((fn [n] (= 3 n)) v27_l127)))


(def v30_l133 (:panels faceted-pl))


(deftest t31_l135 (is ((fn [ps] (= 3 (count ps))) v30_l133)))


(def
 v33_l142
 (def cols [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v34_l144
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols) {:color :species})))


(deftest
 t35_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v34_l144)))


(def
 v37_l160
 (sk/lay-histogram
  (rdatasets/datasets-iris)
  [:sepal-length :sepal-width :petal-length]
  {:color :species}))


(deftest
 t38_l162
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v37_l160)))


(def
 v40_l168
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :species {:color :species})
  (sk/facet :island)))


(deftest
 t41_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 5 (:polygons s)))))
   v40_l168)))


(def
 v43_l180
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/facet :species)
  (sk/options
   {:title "Iris by Species",
    :x-label "Sepal Length (cm)",
    :y-label "Sepal Width (cm)"})))


(deftest
 t44_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v43_l180)))
