(ns
 napkinsketch-book.customization-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v2_l17
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l24
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:width 800, :height 250})))


(deftest
 t5_l29
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v4_l24)))


(def
 v7_l35
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:width 300, :height 500})))


(deftest
 t8_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v7_l35)))


(def
 v10_l48
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t11_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v10_l48)))


(def
 v13_l62
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})))


(deftest
 t14_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v13_l62)))


(def
 v16_l73
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t17_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v16_l73)))


(def
 v19_l89
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (mapv
    (fn* [p1__89470#] (* 2 (Math/pow 1.1 p1__89470#)))
    (range 1 50))}))


(def
 v21_l95
 (->
  exponential-data
  (sk/view [[:x :y]])
  sk/lay-point
  (sk/options {:title "Linear Scale"})))


(deftest
 t22_l100
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v21_l95)))


(def
 v24_l106
 (->
  exponential-data
  (sk/view [[:x :y]])
  sk/lay-point
  (sk/scale :y :log)
  (sk/options {:title "Log Y Scale"})))


(deftest
 t25_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v24_l106)))


(def
 v27_l118
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t28_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l118)))


(def
 v30_l132
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species, :alpha 0.5, :size 5})))


(deftest
 t31_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v30_l132)))


(def v33_l142 (-> iris (sk/view :species) (sk/lay-bar {:alpha 0.4})))


(deftest
 t34_l146
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v33_l142)))


(def
 v36_l155
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/rule-v 6.0))))


(deftest
 t37_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v36_l155)))


(def v39_l167 (:band-opacity (sk/config)))


(deftest t40_l169 (is ((fn [v] (= 0.15 v)) v39_l167)))


(def
 v41_l171
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))))


(deftest
 t42_l176
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v41_l171)))


(def
 v44_l183
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t45_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v44_l183)))


(def
 v47_l194
 (->
  iris
  (sk/view :species)
  (sk/lay-stacked-bar {:color :species})
  (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t48_l199
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v47_l194)))


(def
 v50_l205
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t51_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v50_l205)))


(def
 v53_l234
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:palette :set2})))


(deftest
 t54_l239
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v53_l234)))


(def
 v56_l245
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t57_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v56_l245)))


(def v59_l264 (c2d/find-palette #"budapest"))


(deftest
 t60_l266
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v59_l264)))


(def v62_l270 (c2d/find-palette #"^:set"))


(deftest
 t63_l272
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v62_l270)))


(def v65_l276 (c2d/find-gradient #"viridis"))


(deftest
 t66_l278
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v65_l276)))


(def v68_l283 (c2d/palette :grand-budapest-1))


(deftest
 t69_l285
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v68_l283)))


(def
 v71_l297
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t72_l302
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v71_l297)))


(def
 v74_l308
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t75_l314
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v74_l308)))


(def
 v77_l322
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t78_l327
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v77_l322)))


(def
 v80_l335
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:tooltip true})
  sk/plot))


(deftest t81_l341 (is ((fn [v] (= :div (first v))) v80_l335)))


(def
 v83_l347
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/options {:brush true})
  sk/plot))


(deftest t84_l353 (is ((fn [v] (= :div (first v))) v83_l347)))
