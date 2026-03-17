(ns
 napkinsketch-book.customization-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l15
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l22
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 800, :height 250})))


(deftest
 t5_l27
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v4_l22)))


(def
 v7_l33
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 300, :height 500})))


(deftest
 t8_l38
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v7_l33)))


(def
 v10_l46
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t11_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v10_l46)))


(def
 v13_l60
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})
  sk/plot))


(deftest
 t14_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v13_l60)))


(def
 v16_l72
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t17_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v16_l72)))


(def
 v19_l88
 (def
  exponential-data
  (tc/dataset
   {:x (range 1 50),
    :y
    (mapv
     (fn* [p1__78260#] (* 2 (Math/pow 1.1 p1__78260#)))
     (range 1 50))})))


(def
 v21_l94
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/plot {:title "Linear Scale"})))


(deftest
 t22_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v21_l94)))


(def
 v24_l105
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/scale :y :log)
  (sk/plot {:title "Log Y Scale"})))


(deftest
 t25_l111
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v24_l105)))


(def
 v27_l117
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/plot {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t28_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l117)))


(def
 v30_l131
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species, :alpha 0.5, :size 5}))
  sk/plot))


(deftest
 t31_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v30_l131)))


(def
 v33_l142
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t34_l147
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v33_l142)))


(def
 v36_l156
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/rule-h 3.0) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t37_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v36_l156)))


(def
 v39_l170
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species})
   (sk/band-v 5.5 6.5)
   (sk/band-h 3.0 3.5 {:alpha 0.3}))
  sk/plot))


(deftest
 t40_l177
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v39_l170)))


(def
 v42_l184
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t43_l189
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v42_l184)))


(def
 v45_l195
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/stacked-bar {:color :species}))
  (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t46_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v45_l195)))


(def
 v48_l206
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t49_l213
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v48_l206)))


(def
 v51_l228
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :set2})))


(deftest
 t52_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v51_l228)))


(def
 v54_l239
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :dark2})))


(deftest
 t55_l244
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v54_l239)))


(def
 v57_l252
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t58_l258
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v57_l252)))


(def
 v60_l266
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:legend-position :bottom})))


(deftest
 t61_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v60_l266)))


(def
 v63_l279
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:tooltip true})))


(deftest t64_l284 (is ((fn [v] (= :div (first v))) v63_l279)))


(def
 v66_l290
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:brush true})))


(deftest t67_l295 (is ((fn [v] (= :div (first v))) v66_l290)))
