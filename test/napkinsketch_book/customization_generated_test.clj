(ns
 napkinsketch-book.customization-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
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
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
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
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
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
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
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
    (fn* [p1__78856#] (* 2 (Math/pow 1.1 p1__78856#)))
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
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
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
  (rdatasets/datasets-iris)
  (sk/lay-point
   :sepal-length
   :sepal-width
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


(def
 v29_l119
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-line :x :y {:size 3})))


(deftest
 t30_l122
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v29_l119)))


(def
 v32_l126
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species {:alpha 0.4})))


(deftest
 t33_l129
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v32_l126)))


(def
 v35_l143
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-rule-v {:x-intercept 6.0})))


(deftest
 t36_l148
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v35_l143)))


(def v38_l155 (:band-opacity (sk/config)))


(deftest t39_l157 (is ((fn [v] (= 0.15 v)) v38_l155)))


(def
 v40_l159
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-band-v {:x-min 5.5, :x-max 6.5})
  (sk/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t41_l164
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v40_l159)))


(def
 v43_l187
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t44_l191
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v43_l187)))


(def
 v46_l195
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t47_l199
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v46_l195)))


(def v49_l211 (c2d/find-palette #"budapest"))


(deftest
 t50_l213
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v49_l211)))


(def v52_l217 (c2d/find-palette #"^:set"))


(deftest
 t53_l219
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v52_l217)))


(def v55_l223 (c2d/find-gradient #"viridis"))


(deftest
 t56_l225
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v55_l223)))


(def v58_l230 (c2d/palette :grand-budapest-1))


(deftest
 t59_l232
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v58_l230)))


(def
 v61_l244
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t62_l248
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v61_l244)))


(def
 v64_l254
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t65_l259
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v64_l254)))


(def
 v67_l267
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t68_l271
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v67_l267)))


(def
 v70_l279
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:tooltip true})))


(deftest t71_l283 (is ((fn [v] (= :div (first (sk/plot v)))) v70_l279)))


(def
 v73_l289
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:brush true})))


(deftest t74_l293 (is ((fn [v] (= :div (first (sk/plot v)))) v73_l289)))


(def
 v76_l299
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v77_l301
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross splom-cols splom-cols) {:color :species})
  sk/lay-point
  (sk/options {:brush true})))


(deftest t78_l306 (is ((fn [v] (= :div (first (sk/plot v)))) v77_l301)))
