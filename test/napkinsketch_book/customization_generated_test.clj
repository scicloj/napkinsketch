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
 v13_l58
 (def
  exponential-data
  (tc/dataset
   {:x (range 1 50),
    :y
    (mapv
     (fn* [p1__103943#] (* 2 (Math/pow 1.1 p1__103943#)))
     (range 1 50))})))


(def
 v15_l64
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/plot {:title "Linear Scale"})))


(deftest
 t16_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v15_l64)))


(def
 v18_l75
 (->
  exponential-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  (sk/scale :y :log)
  (sk/plot {:title "Log Y Scale"})))


(deftest
 t19_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v18_l75)))


(def
 v21_l89
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/plot {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t22_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v21_l89)))


(def
 v24_l103
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species, :alpha 0.5, :size 5}))
  sk/plot))


(deftest
 t25_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v24_l103)))


(def
 v27_l114
 (-> iris (sk/view :species) (sk/lay (sk/bar {:alpha 0.4})) sk/plot))


(deftest
 t28_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v27_l114)))


(def
 v30_l126
 (def
  summary
  (tc/dataset {:category [:a :b :c :d], :value [42 28 35 19]})))


(def
 v31_l130
 (->
  summary
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  (sk/plot {:title "Pre-computed Values"})))


(deftest
 t32_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v31_l130)))


(def
 v34_l141
 (->
  summary
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  (sk/plot {:title "Horizontal Value Bars"})))


(deftest
 t35_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v34_l141)))


(def
 v37_l155
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t38_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v37_l155)))


(def
 v40_l166
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/stacked-bar {:color :species}))
  (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t41_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v40_l166)))


(def
 v43_l187
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :set2})))


(deftest
 t44_l192
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v43_l187)))


(def
 v46_l198
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:palette :dark2})))


(deftest
 t47_l203
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v46_l198)))


(def
 v49_l211
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t50_l217
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v49_l211)))


(def
 v52_l225
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:legend-position :bottom})))


(deftest
 t53_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v52_l225)))


(def
 v55_l238
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:tooltip true})))


(deftest t56_l243 (is ((fn [v] (= :div (first v))) v55_l238)))


(def
 v58_l249
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:brush true})))


(deftest t59_l254 (is ((fn [v] (= :div (first v))) v58_l249)))
