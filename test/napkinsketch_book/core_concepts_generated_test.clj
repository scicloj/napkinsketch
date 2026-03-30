(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l28 data/iris)


(deftest
 t4_l30
 (is ((fn [ds] (= 150 (count (tablecloth.api/rows ds)))) v3_l28)))


(def v6_l43 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t7_l47
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l43)))


(def
 v9_l57
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v10_l59 (kind/pprint my-view))


(def v12_l80 (method/lookup :point))


(deftest
 t13_l82
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v12_l80)))


(def
 v15_l89
 (def view-with-method (sk/lay my-view (method/lookup :point))))


(def v16_l92 (kind/pprint view-with-method))


(def v18_l106 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t19_l109
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l106)))


(def v21_l123 (method/lookup :histogram))


(deftest t22_l125 (is ((fn [m] (= :bar (:mark m))) v21_l123)))


(def v24_l129 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t25_l132
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v24_l129)))


(def v27_l148 (method/lookup :lm))


(deftest t28_l150 (is ((fn [m] (= :lm (:stat m))) v27_l148)))


(def
 v30_l154
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t31_l158
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v30_l154)))


(def v33_l169 (method/lookup :stacked-bar))


(deftest t34_l171 (is ((fn [m] (= :stack (:position m))) v33_l169)))


(def
 v36_l175
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t37_l180
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v36_l175)))


(def v39_l189 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t40_l192
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v39_l189)))


(def v42_l196 (-> data/iris (sk/view :sepal_length)))


(deftest
 t43_l199
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v42_l196)))


(def
 v45_l214
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t46_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l214)))


(def
 v48_l226
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t49_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v48_l226)))


(def
 v51_l240
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v53_l246 (-> scatter-base sk/lay-lm))


(deftest
 t54_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v53_l246)))


(def v56_l256 (-> scatter-base sk/lay-loess))


(deftest
 t57_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v56_l256)))


(def
 v59_l273
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t60_l276
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v59_l273)))


(def
 v62_l285
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t63_l288
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v62_l285)))


(def
 v65_l294
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t66_l297
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v65_l294)))


(def
 v68_l307
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t69_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l307)))


(def
 v71_l320
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t72_l325
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v71_l320)))


(def
 v74_l340
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t75_l346
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v74_l340)))


(def v77_l361 (def cols [:sepal_length :sepal_width :petal_length]))


(def v78_l363 (sk/cross cols cols))


(deftest t79_l365 (is ((fn [v] (= 9 (count v))) v78_l363)))


(def v81_l370 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t82_l373
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v81_l370)))


(def
 v84_l392
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t85_l396
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v84_l392)))


(def
 v87_l404
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t88_l410
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v87_l404)))
