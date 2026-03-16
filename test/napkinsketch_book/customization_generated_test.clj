(ns
 napkinsketch-book.customization-generated-test
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
 v4_l19
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 800, :height 250})))


(deftest
 t5_l24
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v4_l19)))


(def
 v7_l30
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 300, :height 500})))


(deftest
 t8_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v7_l30)))


(def
 v10_l43
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t11_l50
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v10_l43)))


(def
 v13_l57
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})
  sk/plot))


(deftest
 t14_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v13_l57)))


(def
 v16_l69
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t17_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v16_l69)))


(def
 v19_l85
 (def
  exponential-data
  (tc/dataset
   {:x (range 1 50),
    :y
    (mapv
     (fn* [p1__79214#] (* 2 (Math/pow 1.1 p1__79214#)))
     (range 1 50))})))


(def
 v21_l91
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/plot {:title "Linear Scale"})))


(deftest
 t22_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v21_l91)))


(def
 v24_l102
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/scale :y :log)
  (sk/plot {:title "Log Y Scale"})))


(deftest
 t25_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v24_l102)))


(def
 v27_l114
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/plot {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t28_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l114)))


(def
 v30_l128
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species, :alpha 0.5, :size 5}))
  sk/plot))


(deftest
 t31_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v30_l128)))


(def
 v33_l139
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t34_l144
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v33_l139)))


(def
 v36_l153
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/rule-h 3.0) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t37_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v36_l153)))


(def
 v39_l167
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species})
   (sk/band-v 5.5 6.5)
   (sk/band-h 3.0 3.5 {:alpha 0.3}))
  sk/plot))


(deftest
 t40_l174
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v39_l167)))


(def
 v42_l181
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t43_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v42_l181)))


(def
 v45_l192
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/stacked-bar {:color :species}))
  (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t46_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v45_l192)))


(def
 v48_l203
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t49_l210
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v48_l203)))


(def
 v51_l225
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :set2})))


(deftest
 t52_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v51_l225)))


(def
 v54_l236
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :dark2})))


(deftest
 t55_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v54_l236)))


(def
 v57_l249
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t58_l255
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v57_l249)))


(def
 v60_l263
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:legend-position :bottom})))


(deftest
 t61_l268
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v60_l263)))


(def
 v63_l276
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:tooltip true})))


(deftest t64_l281 (is ((fn [v] (= :div (first v))) v63_l276)))


(def
 v66_l287
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:brush true})))


(deftest t67_l292 (is ((fn [v] (= :div (first v))) v66_l287)))
