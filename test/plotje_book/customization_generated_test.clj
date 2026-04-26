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
 v15_l70
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__75307#] (* 2 (Math/pow 1.1 p1__75307#)))
    (range 1 50))}))


(def
 v17_l76
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/options {:title "Linear Scale"})))


(deftest
 t18_l80
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v17_l76)))


(def
 v20_l86
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/scale :y :log)
  (pj/options {:title "Log Y Scale"})))


(deftest
 t21_l91
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v20_l86)))


(def
 v23_l97
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :domain [0 6]})
  (pj/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t24_l102
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v23_l97)))


(def
 v26_l109
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :breaks [2.0 3.0 4.0]})))


(deftest
 t27_l113
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"2"} (:texts s))
      (some #{"4"} (:texts s)))))
   v26_l109)))


(def
 v29_l122
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t30_l125
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v29_l122)))


(def
 v32_l131
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-line :x :y {:size 3})))


(deftest
 t33_l134
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v32_l131)))


(def
 v35_l138
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species {:alpha 0.4})))


(deftest
 t36_l141
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 3 (:polygons s)))) v35_l138)))


(def
 v38_l155
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t39_l160
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v38_l155)))


(def v41_l167 (:band-opacity (pj/config)))


(deftest t42_l169 (is ((fn [v] (= 0.15 v)) v41_l167)))


(def
 v43_l171
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})
  (pj/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t44_l176
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v43_l171)))


(def
 v46_l199
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t47_l203
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v46_l199)))


(def
 v49_l207
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :dark2})))


(deftest
 t50_l211
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v49_l207)))


(def v52_l223 (c2d/find-palette #"budapest"))


(deftest
 t53_l225
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v52_l223)))


(def v55_l229 (c2d/find-palette #"^:set"))


(deftest
 t56_l231
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v55_l229)))


(def v58_l235 (c2d/find-gradient #"viridis"))


(deftest
 t59_l237
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v58_l235)))


(def v61_l242 (c2d/palette :grand-budapest-1))


(deftest
 t62_l244
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v61_l242)))


(def
 v64_l256
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :khroma/okabeito})))


(deftest
 t65_l260
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v64_l256)))


(def
 v67_l266
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t68_l271
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v67_l266)))


(def
 v70_l279
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :bottom})))


(deftest
 t71_l283
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v70_l279)))


(def
 v73_l291
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest t74_l295 (is ((fn [v] (= :div (first (pj/plot v)))) v73_l291)))


(def
 v76_l301
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:brush true})))


(deftest t77_l305 (is ((fn [v] (= :div (first (pj/plot v)))) v76_l301)))


(def
 v79_l311
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v80_l313
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  pj/lay-point
  (pj/pose (pj/cross splom-cols splom-cols))
  (pj/options {:brush true})))


(deftest t81_l319 (is ((fn [v] (= :div (first (pj/plot v)))) v80_l313)))
