(ns
 napkinsketch-book.customization-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:width 800, :height 250})))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v3_l21)))


(def
 v6_l31
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:width 300, :height 500})))


(deftest
 t7_l35
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v6_l31)))


(def
 v9_l43
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t10_l49
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v9_l43)))


(def
 v12_l56
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs {:title "Pipeline Labels", :x "Length", :y "Width"})))


(deftest
 t13_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"Pipeline Labels"} (:texts s)))))
   v12_l56)))


(def
 v15_l66
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t16_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v15_l66)))


(def
 v18_l81
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__75471#] (* 2 (Math/pow 1.1 p1__75471#)))
    (range 1 50))}))


(def
 v20_l87
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/options {:title "Linear Scale"})))


(deftest
 t21_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v20_l87)))


(def
 v23_l97
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/scale :y :log)
  (sk/options {:title "Log Y Scale"})))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v23_l97)))


(def
 v26_l108
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t27_l113
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l108)))


(def
 v29_l121
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t30_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l121)))


(def v32_l130 (-> data/iris (sk/lay-bar :species {:alpha 0.4})))


(deftest
 t33_l133
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v32_l130)))


(def
 v35_l142
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/rule-v 6.0))))


(deftest
 t36_l146
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v35_l142)))


(def v38_l153 (:band-opacity (sk/config)))


(deftest t39_l155 (is ((fn [v] (= 0.15 v)) v38_l153)))


(def
 v40_l157
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))))


(deftest
 t41_l161
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v40_l157)))


(def
 v43_l168
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t44_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v43_l168)))


(def
 v46_l178
 (->
  data/penguins
  (sk/lay-stacked-bar :island {:color :species})
  (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t47_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v46_l178)))


(def
 v49_l188
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t50_l194
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v49_l188)))


(def
 v52_l216
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :set2})))


(deftest
 t53_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v52_l216)))


(def
 v55_l226
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t56_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v55_l226)))


(def v58_l244 (c2d/find-palette #"budapest"))


(deftest
 t59_l246
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v58_l244)))


(def v61_l250 (c2d/find-palette #"^:set"))


(deftest
 t62_l252
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v61_l250)))


(def v64_l256 (c2d/find-gradient #"viridis"))


(deftest
 t65_l258
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v64_l256)))


(def v67_l263 (c2d/palette :grand-budapest-1))


(deftest
 t68_l265
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v67_l263)))


(def
 v70_l277
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t71_l281
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v70_l277)))


(def
 v73_l287
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t74_l292
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v73_l287)))


(def
 v76_l300
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t77_l304
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v76_l300)))


(def
 v79_l312
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:tooltip true})
  sk/plot))


(deftest t80_l317 (is ((fn [v] (= :div (first v))) v79_l312)))


(def
 v82_l323
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:brush true})
  sk/plot))


(deftest t83_l328 (is ((fn [v] (= :div (first v))) v82_l323)))
