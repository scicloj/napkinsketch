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
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:width 800, :height 250})))


(deftest
 t5_l28
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v4_l24)))


(def
 v7_l34
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:width 300, :height 500})))


(deftest
 t8_l38
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v7_l34)))


(def
 v10_l46
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t11_l52
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
 v13_l59
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})))


(deftest
 t14_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v13_l59)))


(def
 v16_l69
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t17_l75
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
 v19_l84
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (mapv
    (fn* [p1__75694#] (* 2 (Math/pow 1.1 p1__75694#)))
    (range 1 50))}))


(def
 v21_l90
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/options {:title "Linear Scale"})))


(deftest
 t22_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v21_l90)))


(def
 v24_l100
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/scale :y :log)
  (sk/options {:title "Log Y Scale"})))


(deftest
 t25_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v24_l100)))


(def
 v27_l111
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t28_l116
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v27_l111)))


(def
 v30_l124
 (->
  iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t31_l127
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v30_l124)))


(def v33_l133 (-> iris (sk/lay-bar :species {:alpha 0.4})))


(deftest
 t34_l136
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v33_l133)))


(def
 v36_l145
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/rule-v 6.0))))


(deftest
 t37_l149
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v36_l145)))


(def v39_l156 (:band-opacity (sk/config)))


(deftest t40_l158 (is ((fn [v] (= 0.15 v)) v39_l156)))


(def
 v41_l160
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))))


(deftest
 t42_l164
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v41_l160)))


(def
 v44_l171
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t45_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v44_l171)))


(def
 v47_l181
 (->
  iris
  (sk/lay-stacked-bar :species {:color :species})
  (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t48_l185
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v47_l181)))


(def
 v50_l191
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t51_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v50_l191)))


(def
 v53_l219
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :set2})))


(deftest
 t54_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v53_l219)))


(def
 v56_l229
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t57_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v56_l229)))


(def v59_l247 (c2d/find-palette #"budapest"))


(deftest
 t60_l249
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v59_l247)))


(def v62_l253 (c2d/find-palette #"^:set"))


(deftest
 t63_l255
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v62_l253)))


(def v65_l259 (c2d/find-gradient #"viridis"))


(deftest
 t66_l261
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v65_l259)))


(def v68_l266 (c2d/palette :grand-budapest-1))


(deftest
 t69_l268
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v68_l266)))


(def
 v71_l280
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t72_l284
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v71_l280)))


(def
 v74_l290
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t75_l295
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v74_l290)))


(def
 v77_l303
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t78_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v77_l303)))


(def
 v80_l315
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:tooltip true})
  sk/plot))


(deftest t81_l320 (is ((fn [v] (= :div (first v))) v80_l315)))


(def
 v83_l326
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:brush true})
  sk/plot))


(deftest t84_l331 (is ((fn [v] (= :div (first v))) v83_l326)))
