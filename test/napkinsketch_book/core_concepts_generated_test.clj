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


(def v6_l52 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t7_l56
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l52)))


(def
 v9_l61
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t10_l67
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v9_l61)))


(def
 v12_l73
 (->
  (tc/dataset
   {:product ["Apples" "Bananas" "Cherries"], :sales [120 85 200]})
  (sk/lay-value-bar :product :sales)))


(deftest
 t13_l77
 (is ((fn [v] (= 3 (:polygons (sk/svg-summary v)))) v12_l73)))


(def
 v15_l87
 (->
  (tc/dataset [[1 10] [2 20] [3 15] [4 25]] {:column-names [:x :y]})
  (sk/lay-line :x :y)))


(deftest
 t16_l91
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v15_l87)))


(def
 v18_l101
 (def my-view (sk/view data/iris :sepal_length :sepal_width)))


(def v19_l103 (kind/pprint my-view))


(def
 v21_l112
 (kind/pprint
  (sk/view
   data/iris
   {:x :sepal_length, :y :sepal_width, :color :species})))


(def v23_l129 (sk/method-lookup :point))


(deftest
 t24_l131
 (is
  ((fn [m] (and (= :point (:mark m)) (= :identity (:stat m))))
   v23_l129)))


(def
 v26_l138
 (def view-with-method (sk/lay my-view (sk/method-lookup :point))))


(def v27_l141 (kind/pprint view-with-method))


(def v29_l155 (-> data/iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t30_l158
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v29_l155)))


(def
 v32_l165
 (sk/plot-spec? (sk/lay-point data/iris :sepal_length :sepal_width)))


(deftest t33_l167 (is ((fn [v] (true? v)) v32_l165)))


(def
 v35_l197
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:color :species, :alpha 0.5})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t36_l202
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s)))))
   v35_l197)))


(def v38_l215 (sk/method-lookup :histogram))


(deftest t39_l217 (is ((fn [m] (= :bar (:mark m))) v38_l215)))


(def v41_l221 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t42_l224
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v41_l221)))


(def v44_l240 (sk/method-lookup :lm))


(deftest t45_l242 (is ((fn [m] (= :lm (:stat m))) v44_l240)))


(def
 v47_l246
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t48_l250
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v47_l246)))


(def v50_l261 (sk/method-lookup :stacked-bar))


(deftest t51_l263 (is ((fn [m] (= :stack (:position m))) v50_l261)))


(def
 v53_l267
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t54_l272
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v53_l267)))


(def v56_l281 (-> data/iris (sk/view :sepal_length :sepal_width)))


(deftest
 t57_l284
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v56_l281)))


(def v59_l288 (-> data/iris (sk/view :sepal_length)))


(deftest
 t60_l291
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v59_l288)))


(def
 v62_l306
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t63_l311
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v62_l306)))


(def
 v65_l318
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-loess))


(deftest
 t66_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v65_l318)))


(def
 v68_l330
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t69_l334
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v68_l330)))


(def
 v71_l355
 (def
  scatter-base
  (-> data/iris (sk/lay-point :sepal_length :sepal_width))))


(def v73_l361 (-> scatter-base sk/lay-lm))


(deftest
 t74_l364
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v73_l361)))


(def v76_l371 (-> scatter-base sk/lay-loess))


(deftest
 t77_l374
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v76_l371)))


(def
 v79_l382
 (-> scatter-base (sk/lay-point :petal_length :petal_width)))


(deftest
 t80_l385
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v79_l382)))


(def
 v82_l399
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t83_l402
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v82_l399)))


(def
 v85_l411
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :petal_length})))


(deftest
 t86_l414
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l411)))


(def
 v88_l420
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color "steelblue"})))


(deftest
 t89_l423
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v88_l420)))


(def
 v91_l433
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t92_l438
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v91_l433)))


(def
 v94_l446
 (->
  data/iris
  (sk/view :sepal_length :sepal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t95_l451
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v94_l446)))


(def
 v97_l466
 (->
  data/iris
  (sk/view :sepal_length :sepal_width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t98_l472
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)) (= 3 (:lines s)))))
   v97_l466)))


(def v100_l487 (def cols [:sepal_length :sepal_width :petal_length]))


(def v101_l489 (sk/cross cols cols))


(deftest t102_l491 (is ((fn [v] (= 9 (count v))) v101_l489)))


(def v104_l496 (-> data/iris (sk/view (sk/cross cols cols))))


(deftest
 t105_l499
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v104_l496)))


(def
 v107_l518
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/coord :flip)))


(deftest
 t108_l522
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v107_l518)))


(def
 v110_l530
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t111_l536
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v110_l530)))
