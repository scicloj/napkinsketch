(ns
 scratch-blueprint-stress-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l12
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l15
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l22
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4}))]
  [(:shared bp) (count (:entries bp))]))


(deftest
 t6_l27
 (is
  ((fn
    [[shared n]]
    (and (= :species (:color shared)) (= 0.4 (:alpha shared)) (= 2 n)))
   v5_l22)))


(def
 v8_l34
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4})
  sk/xkcd7-lay-point))


(deftest
 t9_l39
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v8_l34)))


(def
 v11_l47
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-view :petal_length :petal_width {:color :petal_length}))]
  (:color (:shared bp))))


(deftest t12_l52 (is ((fn [v] (= :petal_length v)) v11_l47)))


(def
 v14_l58
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (:color (first (:methods bp)))]))


(deftest
 t15_l62
 (is
  ((fn
    [[shared method-color]]
    (and (empty? shared) (= :species method-color)))
   v14_l58)))


(def
 v17_l68
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-lm)]
  (mapv :color (:methods bp))))


(deftest t18_l73 (is ((fn [colors] (= [:species nil] colors)) v17_l68)))


(def
 v20_l79
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-lay-lm {:color nil})
  (sk/xkcd7-facet :species)))


(deftest
 t21_l85
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v20_l79)))


(def
 v23_l96
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-distribution :sepal_length :sepal_width :petal_length)
  sk/xkcd7-lay-histogram))


(deftest
 t24_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v23_l96)))


(def
 v26_l109
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-view
   {:x :sepal_length,
    :y :sepal_width,
    :methods [{:mark :line, :stat :lm, :color nil}]})))


(deftest
 t27_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v26_l109)))


(def
 v29_l121
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-view
   {:x :sepal_length,
    :y :sepal_width,
    :methods [{:mark :line, :stat :lm}]})))


(deftest
 t30_l127
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v29_l121)))


(def
 v32_l135
 (let
  [bp (-> {:x [1 2 3], :y [4 5 6]} (sk/xkcd7-view))]
  [(count (:entries bp)) (:shared bp)]))


(deftest
 t33_l139
 (is ((fn [[n shared]] (and (= 1 n) (empty? shared))) v32_l135)))


(def
 v35_l145
 (->
  (sk/xkcd7-sketch
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :g [:a :a :b :b :a]}
   {:color :g})
  (sk/xkcd7-view)
  sk/xkcd7-lay-point))


(deftest
 t36_l150
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v35_l145)))


(def v38_l154 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v39_l156
 (->
  (sk/xkcd7-sketch iris {:color :species})
  (sk/xkcd7-view (sk/cross cols cols))
  sk/xkcd7-lay-point))


(deftest
 t40_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 9 (:panels s)) (= (* 9 150) (:points s)))))
   v39_l156)))


(def
 v42_l169
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-histogram :petal_length))]
  [(count (:entries bp)) (count (:methods bp))]))


(deftest
 t43_l174
 (is
  ((fn [[entries methods]] (and (= 2 entries) (= 2 methods)))
   v42_l169)))


(def
 v45_l182
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/xkcd7-lay-point :x :y)))


(deftest
 t46_l185
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v45_l182)))


(def
 v48_l189
 (-> {"x" [1 2 3 4 5], "y" [2 4 3 5 4]} (sk/xkcd7-lay-point "x" "y")))


(deftest
 t49_l192
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v48_l189)))


(def
 v51_l196
 (def
  recipe
  (->
   (sk/xkcd7-sketch)
   (sk/xkcd7-view :sepal_length :sepal_width)
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def v52_l201 (-> recipe (sk/xkcd7-with-data iris)))


(deftest
 t53_l203
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v52_l201)))


(def
 v55_l212
 (->
  iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  sk/xkcd7-lay-point
  (sk/xkcd7-annotate (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3}))))


(deftest
 t56_l217
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v55_l212)))


(def
 v58_l225
 (let
  [bp
   (->
    iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-lay-point)
    (sk/xkcd7-lay-lm {:color nil}))]
  [(:color (:shared bp))
   (:color (first (:entries bp)))
   (:color (second (:methods bp)))]))


(deftest
 t59_l233
 (is
  ((fn
    [[shared-c entry-c method-c]]
    (and (= :species shared-c) (nil? entry-c) (nil? method-c)))
   v58_l225)))


(def
 v61_l240
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-options {:title "My Plot", :width 800}))]
  [(:title (:opts bp)) (:width (:opts bp)) (:shared bp)]))


(deftest
 t62_l245
 (is
  ((fn
    [[title width shared]]
    (and (= "My Plot" title) (= 800 width) (empty? shared)))
   v61_l240)))


(def
 v64_l252
 (let
  [bp
   (->
    iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-scale :y :log)
    (sk/xkcd7-coord :flip))]
  [(-> bp :entries first :y-scale) (-> bp :entries first :coord)]))


(deftest
 t65_l259
 (is
  ((fn [[yscale coord]] (and (= {:type :log} yscale) (= :flip coord)))
   v64_l252)))
