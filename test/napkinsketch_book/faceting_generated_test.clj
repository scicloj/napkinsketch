(ns
 napkinsketch-book.faceting-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l23
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t4_l27
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v3_l23)))


(def
 v6_l39
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species :col)))


(deftest
 t7_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v6_l39)))


(def
 v9_l51
 (->
  data/tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t10_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v9_l51)))


(def
 v12_l63
 (->
  data/iris
  (sk/lay-histogram :sepal_length {:color :species})
  (sk/facet :species)))


(deftest
 t13_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v12_l63)))


(def
 v15_l75
 (->
  data/tips
  (sk/view :total_bill :tip {:color :sex})
  sk/lay-point
  sk/lay-lm
  (sk/facet-grid :smoker :sex)))


(deftest
 t16_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v15_l75)))


(def
 v18_l93
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :shared})))


(deftest
 t19_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v18_l93)))


(def
 v21_l104
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/options {:scales :free-y})))


(deftest
 t22_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v21_l104)))


(def
 v24_l119
 (def
  faceted-sk
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/facet :species)
   sk/sketch)))


(def v25_l125 (:grid faceted-sk))


(deftest
 t26_l127
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v25_l125)))


(def v27_l129 (count (:panels faceted-sk)))


(deftest t28_l131 (is ((fn [n] (= 3 n)) v27_l129)))


(def v30_l135 (:panels faceted-sk))


(deftest t31_l137 (is ((fn [ps] (= 3 (count ps))) v30_l135)))


(def
 v33_l144
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v34_l146
 (->
  data/iris
  (sk/view (sk/cross cols cols))
  (sk/lay-point {:color :species})))


(deftest
 t35_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 16 (:panels s)) (= 2400 (:points s)))))
   v34_l146)))


(def
 v37_l163
 (->
  (sk/distribution data/iris :sepal_length :sepal_width :petal_length)
  (sk/lay-histogram {:color :species})))


(deftest
 t38_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v37_l163)))


(def
 v40_l172
 (->
  data/penguins
  (sk/lay-bar :species {:color :species})
  (sk/facet :island)))


(deftest
 t41_l176
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 9 (:polygons s)))))
   v40_l172)))


(def
 v43_l184
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)
  (sk/labs
   {:title "Iris by Species",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t44_l191
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
   v43_l184)))
