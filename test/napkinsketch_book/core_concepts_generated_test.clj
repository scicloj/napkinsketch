(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l32 data/iris)


(deftest t4_l34 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l32)))


(def
 v6_l46
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t7_l49
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l46)))


(def v9_l59 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t10_l63
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l59)))


(def
 v12_l68
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t13_l74
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l68)))


(def v15_l80 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l83
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l80)))


(def
 v18_l87
 (->
  {:x [1 2 3 4], :y [4 5 6 7], :group ["a" "a" "b" "b"]}
  sk/lay-point))


(deftest
 t19_l90
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l87)))


(def
 v21_l105
 (->
  (tc/dataset [[1 10] [2 20] [3 15] [4 25]] {:column-names [:x :y]})
  (sk/lay-line :x :y)))


(deftest
 t22_l109
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v21_l105)))


(def
 v24_l119
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v25_l121 (kind/pprint my-view))


(def
 v27_l130
 (kind/pprint
  (sk/view
   data/iris
   {:x :sepal_length, :y :sepal_width, :color :species})))


(def v29_l147 (sk/method-lookup :point))


(deftest
 t30_l149
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v29_l147)))


(def
 v32_l156
 (def view-with-method (sk/lay my-view (sk/method-lookup :point))))


(def v33_l159 (kind/pprint view-with-method))


(def v35_l173 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t36_l176
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v35_l173)))


(def
 v38_l183
 (sk/sketch? (sk/lay-point data/iris :sepal_length :sepal_width)))


(deftest t39_l185 (is ((fn [v] (true? v)) v38_l183)))


(def
 v41_l215
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t42_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v41_l215)))


(def v44_l233 (sk/method-lookup :histogram))


(deftest t45_l235 (is ((fn [m] (= :bar (:mark m))) v44_l233)))


(def v47_l239 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t48_l242
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v47_l239)))


(def v50_l258 (sk/method-lookup :lm))


(deftest t51_l260 (is ((fn [m] (= :lm (:stat m))) v50_l258)))


(def
 v53_l264
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t54_l268
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v53_l264)))


(def v56_l279 (sk/method-lookup :stacked-bar))


(deftest t57_l281 (is ((fn [m] (= :stack (:position m))) v56_l279)))


(def
 v59_l285
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t60_l290
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v59_l285)))


(def v62_l304 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t63_l307
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v62_l304)))


(def v65_l311 (-> data/iris (sk/view :sepal_length)))


(deftest
 t66_l314
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v65_l311)))


(def
 v68_l329
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t69_l334
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l329)))


(def
 v71_l341
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-loess))


(deftest
 t72_l346
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v71_l341)))


(def
 v74_l353
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t75_l357
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v74_l353)))


(def
 v77_l379
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v79_l385 (-> scatter-base sk/lay-lm))


(deftest
 t80_l388
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v79_l385)))


(def v82_l395 (-> scatter-base sk/lay-loess))


(deftest
 t83_l398
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v82_l395)))


(def
 v85_l406
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t86_l409
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v85_l406)))


(def
 v88_l423
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t89_l426
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v88_l423)))


(def
 v91_l435
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t92_l438
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v91_l435)))


(def
 v94_l444
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t95_l447
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v94_l444)))


(def
 v97_l457
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t98_l462
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v97_l457)))


(def
 v100_l470
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t101_l475
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v100_l470)))


(def
 v103_l490
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t104_l496
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v103_l490)))


(def v106_l511 (def cols [:sepal_length :sepal_width :petal_length]))


(def v107_l513 (sk/cross cols cols))


(deftest t108_l515 (is ((fn [v] (= 9 (count v))) v107_l513)))


(def v110_l520 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t111_l523
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v110_l520)))


(def
 v113_l542
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t114_l546
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v113_l542)))


(def
 v116_l554
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t117_l560
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v116_l554)))
