(ns
 napkinsketch-book.xkcd7-composability-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (-> data/iris (sk/xkcd7-lay-point :sepal_length :sepal_width)))


(deftest
 t4_l36
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v3_l33)))


(def
 v6_l46
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t7_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v6_l46)))


(def
 v9_l59
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t10_l64
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v9_l59)))


(def v12_l80 (-> data/iris (sk/xkcd7-view :sepal_length :sepal_width)))


(deftest
 t13_l83
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v12_l80)))


(def v15_l87 (-> data/iris (sk/xkcd7-view :sepal_length)))


(deftest
 t16_l90
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v15_l87)))


(def
 v18_l100
 (sk/xkcd7-lay-histogram
  data/iris
  [:sepal_length :sepal_width :petal_length]))


(deftest
 t19_l102
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v18_l100)))


(def
 v21_l106
 (->
  data/iris
  (sk/xkcd7-view
   [[:sepal_length :sepal_width] [:petal_length :petal_width]])))


(deftest
 t22_l110
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v21_l106)))


(def v24_l115 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v25_l117
 (-> data/iris (sk/xkcd7-view (sk/cross cols cols) {:color :species})))


(deftest
 t26_l120
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v25_l117)))


(def
 v28_l131
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm
  (sk/xkcd7-options {:title "Iris by Species"})))


(deftest
 t29_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v28_l131)))


(def
 v31_l147
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-annotate
   (sk/rule-h 3.0)
   (sk/band-v 5.5 7.0 {:alpha 0.15}))))


(deftest
 t32_l152
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v31_l147)))


(def
 v34_l161
 (def
  my-bp
  (->
   data/iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def v36_l170 (:shared my-bp))


(deftest t37_l172 (is ((fn [v] (= :species (:color v))) v36_l170)))


(def v39_l176 (:entries my-bp))


(deftest
 t40_l178
 (is
  ((fn [v] (and (= 1 (count v)) (= :sepal_length (:x (first v)))))
   v39_l176)))


(def v42_l184 (mapv :mark (:methods my-bp)))


(deftest t43_l186 (is ((fn [v] (= [:point :line] v)) v42_l184)))
