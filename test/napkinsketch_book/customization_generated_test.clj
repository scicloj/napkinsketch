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
    (fn* [p1__90342#] (* 2 (Math/pow 1.1 p1__90342#)))
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
 v35_l144
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/rule-v 6.0))))


(deftest
 t36_l148
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v35_l144)))


(def v38_l155 (:band-opacity (sk/config)))


(deftest t39_l157 (is ((fn [v] (= 0.15 v)) v38_l155)))


(def
 v40_l159
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3}))))


(deftest
 t41_l163
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v40_l159)))


(def
 v43_l170
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t44_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v43_l170)))


(def
 v46_l180
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-stacked-bar :island {:color :species})
  (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]})))


(deftest
 t47_l184
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v46_l180)))


(def
 v49_l190
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:palette
    {:setosa "#E74C3C", :versicolor "#3498DB", :virginica "#2ECC71"}})))


(deftest
 t50_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v49_l190)))


(def
 v52_l219
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette :set2})))


(deftest
 t53_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v52_l219)))


(def
 v55_l229
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette :dark2})))


(deftest
 t56_l233
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v55_l229)))


(def v58_l247 (c2d/find-palette #"budapest"))


(deftest
 t59_l249
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v58_l247)))


(def v61_l253 (c2d/find-palette #"^:set"))


(deftest
 t62_l255
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v61_l253)))


(def v64_l259 (c2d/find-gradient #"viridis"))


(deftest
 t65_l261
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v64_l259)))


(def v67_l266 (c2d/palette :grand-budapest-1))


(deftest
 t68_l268
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v67_l266)))


(def
 v70_l280
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:palette :khroma/okabeito})))


(deftest
 t71_l284
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v70_l280)))


(def
 v73_l290
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t74_l295
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v73_l290)))


(def
 v76_l303
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:legend-position :bottom})))


(deftest
 t77_l307
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v76_l303)))


(def
 v79_l315
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:tooltip true})))


(deftest t80_l319 (is ((fn [v] (= :div (first (sk/plot v)))) v79_l315)))


(def
 v82_l325
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:brush true})))


(deftest t83_l329 (is ((fn [v] (= :div (first (sk/plot v)))) v82_l325)))


(def
 v85_l335
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v86_l337
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross splom-cols splom-cols) {:color :species})
  sk/lay-point
  (sk/options {:brush true})))


(deftest t87_l342 (is ((fn [v] (= :div (first (sk/plot v)))) v86_l337)))
