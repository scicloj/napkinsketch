(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l29 data/iris)


(deftest
 t4_l31
 (is ((fn [ds] (= 150 (count (tablecloth.api/rows ds)))) v3_l29)))


(def v6_l44 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t7_l48
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l44)))


(def
 v9_l58
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v10_l60 (kind/pprint my-view))


(def
 v12_l69
 (kind/pprint
  (sk/view
   data/iris
   {:x :sepal_length, :y :sepal_width, :color :species})))


(def v14_l86 (sk/method-lookup :point))


(deftest
 t15_l88
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v14_l86)))


(def
 v17_l95
 (def view-with-method (sk/lay my-view (sk/method-lookup :point))))


(def v18_l98 (kind/pprint view-with-method))


(def v20_l112 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t21_l115
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v20_l112)))


(def
 v23_l122
 (sk/plot-spec? (sk/lay-point data/iris :sepal_length :sepal_width)))


(deftest t24_l124 (is ((fn [v] (true? v)) v23_l122)))


(def
 v26_l154
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t27_l159
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v26_l154)))


(def v29_l174 (sk/method-lookup :histogram))


(deftest t30_l176 (is ((fn [m] (= :bar (:mark m))) v29_l174)))


(def v32_l180 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t33_l183
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v32_l180)))


(def v35_l199 (sk/method-lookup :lm))


(deftest t36_l201 (is ((fn [m] (= :lm (:stat m))) v35_l199)))


(def
 v38_l205
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t39_l209
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v38_l205)))


(def v41_l220 (sk/method-lookup :stacked-bar))


(deftest t42_l222 (is ((fn [m] (= :stack (:position m))) v41_l220)))


(def
 v44_l226
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t45_l231
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v44_l226)))


(def v47_l240 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t48_l243
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v47_l240)))


(def v50_l247 (-> data/iris (sk/view :sepal_length)))


(deftest
 t51_l250
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v50_l247)))


(def
 v53_l265
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t54_l270
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v53_l265)))


(def
 v56_l277
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t57_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v56_l277)))


(def
 v59_l301
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v61_l307 (-> scatter-base sk/lay-lm))


(deftest
 t62_l310
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v61_l307)))


(def v64_l317 (-> scatter-base sk/lay-loess))


(deftest
 t65_l320
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v64_l317)))


(def
 v67_l328
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t68_l331
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v67_l328)))


(def
 v70_l345
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t71_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v70_l345)))


(def
 v73_l357
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t74_l360
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v73_l357)))


(def
 v76_l366
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t77_l369
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v76_l366)))


(def
 v79_l379
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t80_l384
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v79_l379)))


(def
 v82_l392
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t83_l397
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v82_l392)))


(def
 v85_l412
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t86_l418
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v85_l412)))


(def v88_l433 (def cols [:sepal_length :sepal_width :petal_length]))


(def v89_l435 (sk/cross cols cols))


(deftest t90_l437 (is ((fn [v] (= 9 (count v))) v89_l435)))


(def v92_l442 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t93_l445
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v92_l442)))


(def
 v95_l464
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t96_l468
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v95_l464)))


(def
 v98_l476
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t99_l482
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v98_l476)))
