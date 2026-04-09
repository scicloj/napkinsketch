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
 v18_l97
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t19_l102
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v18_l97)))


(def
 v21_l109
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm)))


(deftest
 t22_l115
 (is
  ((fn [sk] (and (= 1 (count (:views sk))) (= 2 (count (:layers sk)))))
   v21_l109)))


(def
 v24_l126
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t25_l130
 (is
  ((fn
    [sk]
    (and
     (= 1 (count (:views sk)))
     (= 0 (count (:layers sk)))
     (= 1 (count (:layers (first (:views sk)))))))
   v24_l126)))


(def
 v27_l141
 (def
  two-panel-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v28_l147 (kind/pprint two-panel-sketch))


(deftest
 t29_l149
 (is
  ((fn [sk] (and (= 2 (count (:views sk))) (= 1 (count (:layers sk)))))
   v28_l147)))


(def v30_l152 two-panel-sketch)


(deftest
 t31_l154
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l152)))


(def
 v33_l187
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t34_l192
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v33_l187)))


(def
 v36_l202
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t37_l207
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v36_l202)))


(def
 v39_l212
 (def
  view-scoped
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width {:color :species})
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v40_l218 (kind/pprint view-scoped))


(deftest
 t41_l220
 (is
  ((fn
    [sk]
    (and
     (= {} (:mapping sk))
     (= :species (:color (:mapping (first (:views sk)))))
     (nil? (:color (:mapping (second (:views sk)))))))
   v40_l218)))


(def v42_l226 view-scoped)


(deftest
 t43_l228
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v42_l226)))


(def
 v45_l237
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t46_l241
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l237)))


(def
 v48_l253
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t49_l258
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v48_l253)))


(def
 v51_l293
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l299
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 2 (:lines s)))))
   v51_l293)))


(def
 v54_l305
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t55_l310
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 1 (:lines s)))))
   v54_l305)))


(def
 v57_l319
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t58_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v57_l319)))


(def
 v60_l333
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1927345#] (= "setosa" (:species p1__1927345#))))))


(def
 v61_l337
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1927346#] (= "versicolor" (:species p1__1927346#))))))


(def
 v62_l341
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:data setosa})
  (sk/view :sepal-length :sepal-width {:data versicolor})
  sk/lay-point))


(deftest
 t63_l346
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v62_l341)))


(def
 v65_l351
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t66_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v65_l351)))


(def
 v68_l363
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-lm {:data versicolor})))


(deftest
 t69_l368
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v68_l363)))


(def
 v71_l389
 (def
  targeted
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-width)
   (sk/lay-histogram :sepal-width)
   (sk/view :sepal-width)
   (sk/lay-density :sepal-width))))


(def v72_l396 (kind/pprint targeted))


(deftest
 t73_l398
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:views sk)))
     (= :histogram (:method (first (:layers (first (:views sk))))))
     (= :density (:method (first (:layers (second (:views sk))))))))
   v72_l396)))


(def v74_l404 targeted)


(deftest
 t75_l406
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v74_l404)))


(def
 v77_l425
 (def
  my-sketch
  (->
   (sk/sketch (rdatasets/datasets-iris) {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Iris"}))))


(def v78_l432 (kind/pprint my-sketch))


(deftest
 t79_l434
 (is
  ((fn
    [sk]
    (and
     (tc/dataset? (:data sk))
     (= :species (:color (:mapping sk)))
     (= 1 (count (:views sk)))
     (= 2 (count (:layers sk)))
     (= "Iris" (:title (:opts sk)))))
   v78_l432)))


(def v80_l442 my-sketch)


(deftest
 t81_l444
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l442)))


(def v83_l457 (sk/method-lookup :histogram))


(deftest t84_l459 (is ((fn [m] (= :bar (:mark m))) v83_l457)))


(def
 v86_l463
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t87_l466
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v86_l463)))


(def v89_l470 (sk/method-lookup :lm))


(deftest t90_l472 (is ((fn [m] (= :lm (:stat m))) v89_l470)))


(def
 v92_l476
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t93_l481
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v92_l476)))


(def
 v95_l498
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t96_l501
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v95_l498)))


(def v98_l505 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t99_l508
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v98_l505)))


(def
 v101_l522
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v103_l528 (-> scatter-base sk/lay-lm))


(deftest
 t104_l530
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v103_l528)))


(def v106_l536 (-> scatter-base sk/lay-loess))


(deftest
 t107_l538
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v106_l536)))


(def
 v109_l551
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t110_l554
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v109_l551)))


(def
 v112_l560
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t113_l563
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v112_l560)))


(def
 v115_l567
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t116_l570
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v115_l567)))


(def
 v118_l576
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t119_l579
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v118_l576)))


(def
 v121_l583
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t122_l588
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v121_l583)))


(def
 v124_l600
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t125_l605
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v124_l600)))


(def
 v127_l609
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.0 6.0 {:alpha 0.1}))))


(deftest
 t128_l614
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v127_l609)))


(def
 v130_l624
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t131_l628
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v130_l624)))


(def
 v133_l633
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t134_l639
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v133_l633)))


(def
 v136_l648
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t137_l654
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v136_l648)))


(def
 v139_l660
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t140_l663
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v139_l660)))


(def v142_l667 (def cols [:sepal-length :sepal-width :petal-length]))


(def
 v143_l669
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols))
  sk/lay-point))


(deftest
 t144_l673
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v143_l669)))
