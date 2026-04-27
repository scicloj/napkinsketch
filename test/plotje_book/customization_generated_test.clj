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
    (fn* [p1__13517#] (* 2 (Math/pow 1.1 p1__13517#)))
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
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t33_l135
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
   v32_l132)))


(def
 v35_l143
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-line :x :y {:size 3})))


(deftest
 t36_l146
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v35_l143)))


(def
 v38_l150
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species {:alpha 0.4})))


(deftest
 t39_l153
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (contains? (:alphas s) 0.4))))
   v38_l150)))


(def
 v41_l168
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t42_l173
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v41_l168)))


(def v44_l180 (:band-opacity (pj/config)))


(deftest t45_l182 (is ((fn [v] (= 0.15 v)) v44_l180)))


(def
 v46_l184
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})
  (pj/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t47_l189
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v46_l184)))


(def
 v49_l212
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t50_l216
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v49_l212)))


(def
 v52_l220
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :dark2})))


(deftest
 t53_l224
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v52_l220)))


(def v55_l236 (c2d/find-palette #"budapest"))


(deftest
 t56_l238
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v55_l236)))


(def v58_l242 (c2d/find-palette #"^:set"))


(deftest
 t59_l244
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v58_l242)))


(def v61_l248 (c2d/find-gradient #"viridis"))


(deftest
 t62_l250
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v61_l248)))


(def v64_l255 (c2d/palette :grand-budapest-1))


(deftest
 t65_l257
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v64_l255)))


(def
 v67_l269
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :khroma/okabeito})))


(deftest
 t68_l273
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v67_l269)))


(def
 v70_l279
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t71_l284
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v70_l279)))


(def
 v73_l292
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :bottom})))


(deftest
 t74_l296
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v73_l292)))


(def
 v76_l302
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :top})))


(deftest
 t77_l306
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v76_l302)))


(def
 v79_l312
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :none})
  pj/plan
  (get-in [:layout :legend-w])))


(deftest t80_l318 (is (zero? v79_l312)))


(def
 v82_l324
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest t83_l328 (is ((fn [v] (= :div (first (pj/plot v)))) v82_l324)))


(def
 v85_l334
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:brush true})))


(deftest t86_l338 (is ((fn [v] (= :div (first (pj/plot v)))) v85_l334)))


(def
 v88_l344
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v89_l346
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  pj/lay-point
  (pj/pose (pj/cross splom-cols splom-cols))
  (pj/options {:brush true})))


(deftest t90_l352 (is ((fn [v] (= :div (first (pj/plot v)))) v89_l346)))
