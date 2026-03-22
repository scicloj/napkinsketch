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
  (sk/plot {:width 800, :height 250})))


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
  (sk/plot {:width 300, :height 500})))


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
  (sk/plot
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
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})
  sk/plot))


(deftest
 t14_l68
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v13_l62)))


(def
 v16_l74
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t17_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v16_l74)))


(def
 v19_l90
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (mapv
    (fn* [p1__75595#] (* 2 (Math/pow 1.1 p1__75595#)))
    (range 1 50))}))


(def
 v21_l96
 (->
  exponential-data
  (sk/view [[:x :y]])
  sk/lay-point
  (sk/plot {:title "Linear Scale"})))


(deftest
 t22_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v21_l96)))


(def
 v24_l107
 (->
  exponential-data
  (sk/view [[:x :y]])
  sk/lay-point
  (sk/scale :y :log)
  (sk/plot {:title "Log Y Scale"})))


(deftest
 t25_l113
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v24_l107)))


(def
 v27_l119
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/plot {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t28_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l119)))


(def
 v30_l133
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species, :alpha 0.5, :size 5})
  sk/plot))


(deftest
 t31_l138
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v30_l133)))


(def
 v33_l144
 (-> iris (sk/view :species) (sk/lay-bar {:alpha 0.4}) sk/plot))


(deftest
 t34_l149
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v33_l144)))


(def
 v36_l158
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/rule-v 6.0))
  sk/plot))


(deftest
 t37_l164
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v36_l158)))


(def v39_l171 (:band-opacity (sk/config)))


(deftest t40_l173 (is ((fn [v] (= 0.15 v)) v39_l171)))


(def
 v41_l175
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/lay (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))
  sk/plot))


(deftest
 t42_l181
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v41_l175)))


(def
 v44_l188
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t45_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v44_l188)))


(def
 v47_l199
 (->
  iris
  (sk/view :species)
  (sk/lay-stacked-bar {:color :species})
  (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t48_l204
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v47_l199)))


(def
 v50_l210
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t51_l217
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v50_l210)))


(def
 v53_l239
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:palette :set2})))


(deftest
 t54_l244
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v53_l239)))


(def
 v56_l250
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:palette :dark2})))


(deftest
 t57_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v56_l250)))


(def v59_l269 (c2d/find-palette #"budapest"))


(deftest
 t60_l271
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v59_l269)))


(def v62_l275 (c2d/find-palette #"^:set"))


(deftest
 t63_l277
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v62_l275)))


(def v65_l281 (c2d/find-gradient #"viridis"))


(deftest
 t66_l283
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v65_l281)))


(def v68_l288 (c2d/palette :grand-budapest-1))


(deftest
 t69_l290
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v68_l288)))


(def
 v71_l302
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:palette :khroma/okabeito})))


(deftest
 t72_l307
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v71_l302)))


(def
 v74_l313
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t75_l319
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v74_l313)))


(def
 v77_l327
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:legend-position :bottom})))


(deftest
 t78_l332
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v77_l327)))


(def
 v80_l340
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:tooltip true})))


(deftest t81_l345 (is ((fn [v] (= :div (first v))) v80_l340)))


(def
 v83_l351
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay-point {:color :species})
  (sk/plot {:brush true})))


(deftest t84_l356 (is ((fn [v] (= :div (first v))) v83_l351)))
