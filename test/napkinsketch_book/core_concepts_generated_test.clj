(ns
 napkinsketch-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (rdatasets/datasets-iris))


(deftest t4_l33 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l31)))


(def
 v6_l45
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t7_l48
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l45)))


(def v9_l57 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t10_l61
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l57)))


(def
 v12_l65
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t13_l71
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v12_l65)))


(def v15_l77 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l80
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l77)))


(def
 v18_l99
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t19_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v18_l99)))


(def
 v21_l111
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm)))


(deftest
 t22_l117
 (is
  ((fn [sk] (and (= 1 (count (:views sk))) (= 2 (count (:layers sk)))))
   v21_l111)))


(def
 v24_l129
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t25_l132
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v24_l129)))


(def
 v26_l134
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t27_l138
 (is
  ((fn
    [sk]
    (and
     (= 1 (count (:views sk)))
     (= 0 (count (:layers sk)))
     (= 1 (count (:layers (first (:views sk)))))))
   v26_l134)))


(def
 v29_l149
 (def
  two-panel-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v30_l155 two-panel-sketch)


(deftest
 t31_l157
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l155)))


(def v32_l159 (kind/pprint two-panel-sketch))


(deftest
 t33_l161
 (is
  ((fn [sk] (and (= 2 (count (:views sk))) (= 1 (count (:layers sk)))))
   v32_l159)))


(def
 v35_l187
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t36_l192
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v35_l187)))


(def
 v38_l202
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t39_l207
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v38_l202)))


(def
 v41_l212
 (def
  view-scoped
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width {:color :species})
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v42_l218 view-scoped)


(deftest
 t43_l220
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v42_l218)))


(def v44_l222 (kind/pprint view-scoped))


(deftest
 t45_l224
 (is
  ((fn
    [sk]
    (and
     (= {} (:mapping sk))
     (= :species (:color (:mapping (first (:views sk)))))
     (nil? (:color (:mapping (second (:views sk)))))))
   v44_l222)))


(def
 v47_l237
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t48_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v47_l237)))


(def
 v50_l253
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t51_l258
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v50_l253)))


(def
 v53_l288
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t54_l294
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 2 (:lines s)))))
   v53_l288)))


(def
 v56_l300
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t57_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 1 (:lines s)))))
   v56_l300)))


(def
 v59_l314
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t60_l319
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v59_l314)))


(def
 v62_l328
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1959981#] (= "setosa" (:species p1__1959981#))))))


(def
 v63_l332
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1959982#] (= "versicolor" (:species p1__1959982#))))))


(def
 v64_l336
 (->
  (sk/sketch (rdatasets/datasets-iris))
  (sk/view :sepal-length :sepal-width {:data setosa})
  (sk/view :sepal-length :sepal-width {:data versicolor})
  sk/lay-point))


(deftest
 t65_l341
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v64_l336)))


(def
 v67_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t68_l350
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v67_l346)))


(def
 v70_l358
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-lm {:data versicolor})))


(deftest
 t71_l363
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v70_l358)))


(def
 v73_l385
 (def
  targeted
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-width)
   (sk/lay-histogram :sepal-width)
   (sk/view :sepal-width)
   (sk/lay-density :sepal-width))))


(def v74_l392 targeted)


(deftest
 t75_l394
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v74_l392)))


(def v76_l396 (kind/pprint targeted))


(deftest
 t77_l398
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:views sk)))
     (= :histogram (:method (first (:layers (first (:views sk))))))
     (= :density (:method (first (:layers (second (:views sk))))))))
   v76_l396)))


(def
 v79_l421
 (def
  my-sketch
  (->
   (sk/sketch (rdatasets/datasets-iris) {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Iris"}))))


(def v80_l428 my-sketch)


(def v81_l430 (kind/pprint my-sketch))


(deftest
 t82_l432
 (is
  ((fn
    [sk]
    (and
     (tc/dataset? (:data sk))
     (= :species (:color (:mapping sk)))
     (= 1 (count (:views sk)))
     (= 2 (count (:layers sk)))
     (= "Iris" (:title (:opts sk)))))
   v81_l430)))


(deftest
 t83_l440
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v81_l430)))


(def v85_l453 (sk/method-lookup :histogram))


(deftest t86_l455 (is ((fn [m] (= :bar (:mark m))) v85_l453)))


(def
 v88_l459
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t89_l462
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v88_l459)))


(def v91_l466 (sk/method-lookup :lm))


(deftest t92_l468 (is ((fn [m] (= :lm (:stat m))) v91_l466)))


(def
 v94_l472
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t95_l477
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v94_l472)))


(def
 v97_l494
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t98_l497
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v97_l494)))


(def v100_l501 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t101_l504
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v100_l501)))


(def
 v103_l518
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v105_l524 (-> scatter-base sk/lay-lm))


(deftest
 t106_l526
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v105_l524)))


(def v108_l532 (-> scatter-base sk/lay-loess))


(deftest
 t109_l534
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l532)))


(def
 v111_l547
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t112_l550
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v111_l547)))


(def
 v114_l556
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t115_l559
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v114_l556)))


(def
 v117_l563
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t118_l566
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v117_l563)))


(def
 v120_l572
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t121_l575
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v120_l572)))


(def
 v123_l581
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t124_l586
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v123_l581)))


(def
 v126_l598
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t127_l603
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v126_l598)))


(def
 v129_l607
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.0 6.0 {:alpha 0.1}))))


(deftest
 t130_l612
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v129_l607)))


(def
 v132_l622
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t133_l626
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v132_l622)))


(def
 v135_l631
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t136_l637
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v135_l631)))


(def
 v138_l646
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t139_l652
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v138_l646)))


(def
 v141_l658
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t142_l661
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v141_l658)))


(def v144_l665 (def cols [:sepal-length :sepal-width :petal-length]))


(def
 v145_l667
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols))
  sk/lay-point))


(deftest
 t146_l671
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v145_l667)))
