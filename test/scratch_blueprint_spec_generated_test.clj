(ns
 scratch-blueprint-spec-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l55
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l63
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t6_l68
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v5_l63)))


(def
 v8_l77
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (first (:entries bp))]))


(deftest
 t9_l80
 (is
  ((fn
    [[shared entry]]
    (and
     (= :species (:color shared))
     (nil? (:color entry))
     (= :sepal_length (:x entry))))
   v8_l77)))


(def
 v11_l90
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-lm))


(deftest
 t12_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v11_l90)))


(def
 v14_l103
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (first (:methods bp))]))


(deftest
 t15_l106
 (is
  ((fn
    [[shared method]]
    (and
     (empty? shared)
     (= :species (:color method))
     (= :point (:mark method))))
   v14_l103)))


(def
 v17_l113
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view :sepal_length :sepal_width)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t18_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v17_l113)))


(def
 v20_l126
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-lay-lm {:color nil})))


(deftest
 t21_l131
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v20_l126)))


(def
 v23_l142
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view :sepal_length :sepal_width)
  (sk/xkcd7-view :petal_length :petal_width)
  sk/xkcd7-lay-point))


(deftest
 t24_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v23_l142)))


(def
 v26_l159
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-view
   {:x :sepal_length,
    :y :sepal_width,
    :methods [{:mark :line, :stat :lm, :color nil}]})))


(deftest
 t27_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v26_l159)))


(def
 v29_l175
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-annotate (sk/rule-h 3.0))))


(deftest
 t30_l179
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v29_l175)))


(def
 v32_l192
 (->
  iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-loess))


(deftest
 t33_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v32_l192)))
