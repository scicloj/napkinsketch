(ns
 plotje-book.customization-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 800, :height 250})))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v3_l21)))


(def
 v6_l31
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 300, :height 500})))


(deftest
 t7_l35
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v6_l31)))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t10_l49
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v9_l43)))


(def
 v12_l55
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t13_l61
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v12_l55)))


(def
 v15_l69
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:color-label "Species (override)"})))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Species (override)"} (:texts s)))))
   v15_l69)))


(def
 v18_l81
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__71758#] (* 2 (Math/pow 1.1 p1__71758#)))
    (range 1 50))}))


(def
 v20_l87
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/options {:title "Linear Scale"})))


(deftest
 t21_l91
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v20_l87)))


(def
 v23_l97
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/scale :y :log)
  (pj/options {:title "Log Y Scale"})))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v23_l97)))


(def
 v26_l108
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :domain [0 6]})
  (pj/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t27_l113
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v26_l108)))


(def
 v29_l120
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :breaks [2.0 3.0 4.0]})))


(deftest
 t30_l124
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (every? (set (:texts s)) ["2" "3" "4"]))))
   v29_l120)))


(def
 v32_l132
 (->
  {:size ["medium" "small" "large"], :count [12 30 7]}
  (pj/lay-value-bar :size :count)
  (pj/scale
   :x
   {:type :categorical, :domain ["large" "medium" "small"]})))


(deftest
 t33_l137
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      labels
      (filter #{"small" "medium" "large"} (:texts s))]
     (= ["large" "medium" "small"] (vec labels))))
   v32_l132)))


(def
 v35_l145
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t36_l148
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (contains? (:alphas s) 0.5)
      (contains? (:sizes s) 5.0))))
   v35_l145)))


(def
 v38_l156
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-line :x :y {:size 3})))


(deftest
 t39_l159
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v38_l156)))


(def
 v41_l163
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species {:alpha 0.4})))


(deftest
 t42_l166
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (contains? (:alphas s) 0.4))))
   v41_l163)))


(def
 v44_l181
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t45_l186
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v44_l181)))


(def v47_l193 (:band-opacity (pj/config)))


(deftest t48_l195 (is ((fn [v] (= 0.15 v)) v47_l193)))


(def
 v49_l197
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})
  (pj/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t50_l202
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v49_l197)))


(def
 v52_l225
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t53_l229
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v52_l225)))


(def
 v55_l233
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :dark2})))


(deftest
 t56_l237
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v55_l233)))


(def v58_l249 (c2d/find-palette #"budapest"))


(deftest
 t59_l251
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v58_l249)))


(def v61_l255 (c2d/find-palette #"^:set"))


(deftest
 t62_l257
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v61_l255)))


(def v64_l261 (c2d/find-gradient #"viridis"))


(deftest
 t65_l263
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v64_l261)))


(def v67_l268 (c2d/palette :grand-budapest-1))


(deftest
 t68_l270
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v67_l268)))


(def
 v70_l282
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :khroma/okabeito})))


(deftest
 t71_l286
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v70_l282)))


(def
 v73_l292
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t74_l297
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v73_l292)))


(def
 v76_l305
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :bottom})))


(deftest
 t77_l309
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v76_l305)))


(def
 v79_l315
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :top})))


(deftest
 t80_l319
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v79_l315)))


(def
 v82_l325
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :none})
  pj/plan
  (get-in [:layout :legend-w])))


(deftest t83_l331 (is (zero? v82_l325)))


(def
 v85_l337
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest t86_l341 (is ((fn [v] (= :div (first (pj/plot v)))) v85_l337)))


(def
 v88_l347
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:brush true})))


(deftest t89_l351 (is ((fn [v] (= :div (first (pj/plot v)))) v88_l347)))


(def
 v91_l357
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v92_l359
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  pj/lay-point
  (pj/pose (pj/cross splom-cols splom-cols))
  (pj/options {:brush true})))


(deftest t93_l365 (is ((fn [v] (= :div (first (pj/plot v)))) v92_l359)))
