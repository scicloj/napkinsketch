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
 v12_l55
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t13_l61
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v12_l55)))


(def
 v15_l70
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__99963#] (* 2 (Math/pow 1.1 p1__99963#)))
    (range 1 50))}))


(def
 v17_l76
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/options {:title "Linear Scale"})))


(deftest
 t18_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v17_l76)))


(def
 v20_l86
 (->
  exponential-data
  (sk/lay-point :x :y)
  (sk/scale :y :log)
  (sk/options {:title "Log Y Scale"})))


(deftest
 t21_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v20_l86)))


(def
 v23_l97
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/scale :y {:type :linear, :domain [0 6]})
  (sk/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v23_l97)))


(def
 v26_l110
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t27_l113
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l110)))


(def v29_l119 (-> data/iris (sk/lay-bar :species {:alpha 0.4})))


(deftest
 t30_l122
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v29_l119)))


(def
 v32_l131
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/rule-h 3.0) (sk/rule-v 6.0))))


(deftest
 t33_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v32_l131)))


(def v35_l142 (:band-opacity (sk/config)))


(deftest t36_l144 (is ((fn [v] (= 0.15 v)) v35_l142)))


(def
 v37_l146
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))))


(deftest
 t38_l150
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v37_l146)))


(def
 v40_l157
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t41_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v40_l157)))


(def
 v43_l167
 (->
  data/penguins
  (sk/lay-stacked-bar :island {:color :species})
  (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t44_l171
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v43_l167)))


(def
 v46_l177
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t47_l183
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v46_l177)))


(def
 v49_l205
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :set2})))


(deftest
 t50_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v49_l205)))


(def
 v52_l215
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t53_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v52_l215)))


(def v55_l233 (c2d/find-palette #"budapest"))


(deftest
 t56_l235
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v55_l233)))


(def v58_l239 (c2d/find-palette #"^:set"))


(deftest
 t59_l241
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v58_l239)))


(def v61_l245 (c2d/find-gradient #"viridis"))


(deftest
 t62_l247
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v61_l245)))


(def v64_l252 (c2d/palette :grand-budapest-1))


(deftest
 t65_l254
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v64_l252)))


(def
 v67_l266
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t68_l270
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v67_l266)))


(def
 v70_l276
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t71_l281
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v70_l276)))


(def
 v73_l289
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t74_l293
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v73_l289)))


(def
 v76_l301
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:tooltip true})))


(deftest t77_l305 (is ((fn [v] (= :div (first (sk/plot v)))) v76_l301)))


(def
 v79_l311
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/options {:brush true})))


(deftest t80_l315 (is ((fn [v] (= :div (first (sk/plot v)))) v79_l311)))


(def
 v82_l321
 (def
  splom-cols
  [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v83_l323
 (->
  data/iris
  (sk/view (sk/cross splom-cols splom-cols) {:color :species})
  sk/lay-point
  (sk/options {:brush true})))


(deftest t84_l328 (is ((fn [v] (= :div (first (sk/plot v)))) v83_l323)))
