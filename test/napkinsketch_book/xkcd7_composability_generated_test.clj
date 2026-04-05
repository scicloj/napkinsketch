(ns
 napkinsketch-book.xkcd7-composability-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l34
 (def
  scatter
  (-> data/iris (sk/xkcd7-lay-point :sepal_length :sepal_width))))


(def v5_l42 scatter)


(deftest
 t6_l44
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v5_l42)))


(def v8_l58 (keys scatter))


(deftest
 t9_l60
 (is
  ((fn [v] (every? (set v) [:data :shared :entries :methods :opts]))
   v8_l58)))


(def v11_l65 (:entries scatter))


(deftest
 t12_l67
 (is
  ((fn
    [v]
    (and
     (= 1 (count v))
     (= :sepal_length (:x (first v)))
     (= :sepal_width (:y (first v)))))
   v11_l65)))


(def v14_l74 (:methods (first (:entries scatter))))


(deftest
 t15_l76
 (is
  ((fn [v] (and (= 1 (count v)) (= :point (:mark (first v)))))
   v14_l74)))


(def
 v17_l84
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t18_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v17_l84)))


(def
 v20_l98
 (def
  scatter-with-regression
  (->
   data/iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def v22_l107 (:entries scatter-with-regression))


(deftest t23_l109 (is ((fn [v] (= 1 (count v))) v22_l107)))


(def v24_l111 (:methods scatter-with-regression))


(deftest t25_l113 (is ((fn [v] (= 2 (count v))) v24_l111)))


(def v27_l118 scatter-with-regression)


(deftest
 t28_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v27_l118)))


(def
 v30_l139
 (def
  layered
  (->
   data/iris
   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-lay-point :sepal_length :sepal_width)
   (sk/xkcd7-lay-lm :sepal_length :sepal_width))))


(def v32_l148 (count (:methods (first (:entries layered)))))


(deftest t33_l150 (is ((fn [v] (= 2 v)) v32_l148)))


(def v35_l154 (:methods layered))


(deftest t36_l156 (is ((fn [v] (= 0 (count v))) v35_l154)))


(def v38_l170 (-> data/iris (sk/xkcd7-view :sepal_length :sepal_width)))


(deftest
 t39_l173
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v38_l170)))


(def v41_l177 (-> data/iris (sk/xkcd7-view :sepal_length)))


(deftest
 t42_l180
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v41_l177)))


(def
 v44_l187
 (def
  two-panels
  (->
   data/iris
   (sk/xkcd7-view
    [[:sepal_length :sepal_width] [:petal_length :petal_width]]))))


(def v46_l194 (:entries two-panels))


(deftest t47_l196 (is ((fn [v] (= 2 (count v))) v46_l194)))


(def v48_l198 two-panels)


(deftest
 t49_l200
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v48_l198)))


(def
 v51_l205
 (->
  data/iris
  (sk/xkcd7-view [:sepal_length :sepal_width :petal_length])))


(deftest
 t52_l208
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v51_l205)))


(def
 v54_l213
 (->
  data/iris
  (sk/xkcd7-lay-histogram [:sepal_length :sepal_width :petal_length])))


(deftest
 t55_l216
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v54_l213)))


(def v57_l225 (def cols [:sepal_length :sepal_width :petal_length]))


(def
 v58_l227
 (-> data/iris (sk/xkcd7-view (sk/cross cols cols) {:color :species})))


(deftest
 t59_l230
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v58_l227)))


(def
 v61_l243
 (->
  data/iris
  (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm
  (sk/xkcd7-options {:title "Iris by Species"})))


(deftest
 t62_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s)))))
   v61_l243)))


(def
 v64_l260
 (->
  data/penguins
  (sk/xkcd7-view :bill_length_mm :bill_depth_mm {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm
  (sk/xkcd7-options {:title "Palmer Penguins"})))


(deftest
 t65_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Palmer Penguins"} (:texts s))))
   v64_l260)))
