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
 (kind/pprint
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(deftest
 t25_l133
 (is
  ((fn
    [sk]
    (and
     (= 1 (count (:views sk)))
     (= 0 (count (:layers sk)))
     (= 1 (count (:layers (first (:views sk)))))))
   v24_l129)))


(def
 v27_l144
 (def
  two-panel-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v28_l150 (kind/pprint two-panel-sketch))


(deftest
 t29_l152
 (is
  ((fn [sk] (and (= 2 (count (:views sk))) (= 1 (count (:layers sk)))))
   v28_l150)))


(def v30_l155 two-panel-sketch)


(deftest
 t31_l157
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v30_l155)))


(def
 v33_l182
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t34_l187
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v33_l182)))


(def
 v36_l197
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t37_l202
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v36_l197)))


(def
 v39_l207
 (def
  view-scoped
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-length :sepal-width {:color :species})
   (sk/view :petal-length :petal-width)
   sk/lay-point)))


(def v40_l213 (kind/pprint view-scoped))


(deftest
 t41_l215
 (is
  ((fn
    [sk]
    (and
     (= {} (:mapping sk))
     (= :species (:color (:mapping (first (:views sk)))))
     (nil? (:color (:mapping (second (:views sk)))))))
   v40_l213)))


(def v42_l221 view-scoped)


(deftest
 t43_l223
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v42_l221)))


(def
 v45_l232
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t46_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v45_l232)))


(def
 v48_l248
 (->
  (sk/sketch (rdatasets/datasets-iris) {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t49_l253
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v48_l248)))


(def
 v51_l288
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk/lay-lm))


(deftest
 t52_l294
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 2 (:lines s)))))
   v51_l288)))


(def
 v54_l300
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t55_l305
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 1 (:lines s)))))
   v54_l300)))


(def
 v57_l314
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t58_l319
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v57_l314)))


(def
 v60_l328
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1927826#] (= "setosa" (:species p1__1927826#))))))


(def
 v61_l332
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__1927827#] (= "versicolor" (:species p1__1927827#))))))


(def
 v62_l336
 (->
  (sk/sketch (rdatasets/datasets-iris))
  (sk/view :sepal-length :sepal-width {:data setosa})
  (sk/view :sepal-length :sepal-width {:data versicolor})
  sk/lay-point))


(deftest
 t63_l341
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v62_l336)))


(def
 v65_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t66_l350
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v65_l346)))


(def
 v68_l358
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-lm {:data versicolor})))


(deftest
 t69_l363
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v68_l358)))


(def
 v71_l384
 (def
  targeted
  (->
   (rdatasets/datasets-iris)
   (sk/view :sepal-width)
   (sk/lay-histogram :sepal-width)
   (sk/view :sepal-width)
   (sk/lay-density :sepal-width))))


(def v72_l391 (kind/pprint targeted))


(deftest
 t73_l393
 (is
  ((fn
    [sk]
    (and
     (= 2 (count (:views sk)))
     (= :histogram (:method (first (:layers (first (:views sk))))))
     (= :density (:method (first (:layers (second (:views sk))))))))
   v72_l391)))


(def v74_l399 targeted)


(deftest
 t75_l401
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v74_l399)))


(def
 v77_l420
 (def
  my-sketch
  (->
   (sk/sketch (rdatasets/datasets-iris) {:color :species})
   (sk/view :sepal-length :sepal-width)
   sk/lay-point
   sk/lay-lm
   (sk/options {:title "Iris"}))))


(def v78_l427 (kind/pprint my-sketch))


(deftest
 t79_l429
 (is
  ((fn
    [sk]
    (and
     (tc/dataset? (:data sk))
     (= :species (:color (:mapping sk)))
     (= 1 (count (:views sk)))
     (= 2 (count (:layers sk)))
     (= "Iris" (:title (:opts sk)))))
   v78_l427)))


(def v80_l437 my-sketch)


(deftest
 t81_l439
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l437)))


(def v83_l452 (sk/method-lookup :histogram))


(deftest t84_l454 (is ((fn [m] (= :bar (:mark m))) v83_l452)))


(def
 v86_l458
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t87_l461
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v86_l458)))


(def v89_l465 (sk/method-lookup :lm))


(deftest t90_l467 (is ((fn [m] (= :lm (:stat m))) v89_l465)))


(def
 v92_l471
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t93_l476
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v92_l471)))


(def
 v95_l493
 (-> (rdatasets/datasets-iris) (sk/view :sepal-length :sepal-width)))


(deftest
 t96_l496
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v95_l493)))


(def v98_l500 (-> (rdatasets/datasets-iris) (sk/view :sepal-length)))


(deftest
 t99_l503
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v98_l500)))


(def
 v101_l517
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v103_l523 (-> scatter-base sk/lay-lm))


(deftest
 t104_l525
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v103_l523)))


(def v106_l531 (-> scatter-base sk/lay-loess))


(deftest
 t107_l533
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v106_l531)))


(def
 v109_l546
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t110_l549
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v109_l546)))


(def
 v112_l555
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t113_l558
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v112_l555)))


(def
 v115_l562
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t116_l565
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v115_l562)))


(def
 v118_l571
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t119_l574
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v118_l571)))


(def
 v121_l580
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width {:group :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t122_l585
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v121_l580)))


(def
 v124_l597
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t125_l602
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v124_l597)))


(def
 v127_l606
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.0 6.0 {:alpha 0.1}))))


(deftest
 t128_l611
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v127_l606)))


(def
 v130_l621
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t131_l625
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v130_l621)))


(def
 v133_l630
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t134_l636
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v133_l630)))


(def
 v136_l645
 (->
  (rdatasets/datasets-iris)
  (sk/view :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  sk/lay-lm))


(deftest
 t137_l651
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v136_l645)))


(def
 v139_l657
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t140_l660
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v139_l657)))


(def v142_l664 (def cols [:sepal-length :sepal-width :petal-length]))


(def
 v143_l666
 (->
  (rdatasets/datasets-iris)
  (sk/view (sk/cross cols cols))
  sk/lay-point))


(deftest
 t144_l670
 (is ((fn [v] (= 9 (:panels (sk/svg-summary v)))) v143_l666)))
