(ns
 napkinsketch-book.config-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l13
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l20
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 800, :height 250})))


(deftest
 t6_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v5_l20)))


(def
 v8_l31
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:width 300, :height 500})))


(deftest
 t9_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v8_l31)))


(def
 v11_l44
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t12_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v11_l44)))


(def
 v14_l59
 (def
  exponential-data
  (tc/dataset
   {:x (range 1 50),
    :y
    (mapv
     (fn* [p1__96919#] (* 2 (Math/pow 1.1 p1__96919#)))
     (range 1 50))})))


(def
 v16_l65
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/plot {:title "Linear Scale"})))


(deftest
 t17_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v16_l65)))


(def
 v19_l76
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/scale :y :log)
  (sk/plot {:title "Log Y Scale"})))


(deftest
 t20_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v19_l76)))


(def
 v22_l90
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/plot {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t23_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v22_l90)))


(def
 v25_l105
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species, :alpha 0.5, :size 5}))
  sk/plot))


(deftest
 t26_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v25_l105)))


(def
 v28_l116
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t29_l121
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v28_l116)))


(def
 v31_l127
 (def
  summary
  (tc/dataset {:category [:a :b :c :d], :value [42 28 35 19]})))


(def
 v32_l131
 (->
  summary
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  (sk/plot {:title "Pre-computed Values"})))


(deftest
 t33_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v32_l131)))


(def
 v35_l142
 (->
  summary
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  (sk/plot {:title "Horizontal Value Bars"})))


(deftest
 t36_l148
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v35_l142)))


(def
 v38_l157
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t39_l162
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l157)))


(def
 v41_l168
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/stacked-bar {:color :species}))
  (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t42_l173
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v41_l168)))


(def
 v44_l190
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :set2})))


(deftest
 t45_l195
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v44_l190)))


(def
 v47_l201
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :dark2})))


(deftest
 t48_l206
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v47_l201)))
