(ns
 plotje-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v3_l34 (rdatasets/datasets-iris))


(deftest t4_l36 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l34)))


(def
 v6_l48
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})))


(deftest
 t7_l52
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v6_l48)))


(def
 v9_l58
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  kind/pprint))


(deftest
 t10_l63
 (is
  ((fn
    [v]
    (and
     (= :sepal-length (get-in v [:mapping :x]))
     (= 1 (count (:layers v)))
     (= :species (get-in v [:layers 0 :mapping :color]))))
   v9_l58)))


(def v12_l74 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-point :x :y)))


(deftest
 t13_l78
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v12_l74)))


(def
 v15_l82
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (pj/lay-value-bar :city :temperature)))


(deftest
 t16_l88
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v15_l82)))


(def v18_l94 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t19_l97
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v18_l94)))


(def
 v21_l114
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t22_l119
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v21_l114)))


(def
 v24_l128
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t25_l131
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v24_l128)))


(def
 v27_l138
 (def
  two-panel
  (pj/arrange
   [(->
     (rdatasets/datasets-iris)
     (pj/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (pj/lay-point :petal-length :petal-width))])))


(def v28_l145 two-panel)


(deftest
 t29_l147
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v28_l145)))


(def
 v31_l171
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t32_l176
 (is ((fn [v] (= 3 (:lines (pj/svg-summary v)))) v31_l171)))


(def
 v34_l185
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t35_l190
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v34_l185)))


(def
 v37_l197
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t38_l203
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:layers 0 :mapping :color]))
     (not (contains? (or (get-in v [:layers 1 :mapping]) {}) :color))))
   v37_l197)))


(def
 v40_l215
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:color nil})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t41_l220
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v40_l215)))


(def
 v43_l228
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:color nil})
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t44_l234
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:mapping :color]))
     (contains? (get (first (:layers v)) :mapping) :color)
     (nil? (get-in (first (:layers v)) [:mapping :color]))))
   v43_l228)))


(def
 v46_l262
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__490770#] (= "setosa" (:species p1__490770#))))))


(def
 v47_l266
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__490771#] (= "versicolor" (:species p1__490771#))))))


(def
 v48_l270
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:data setosa})
  (pj/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t49_l275
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v48_l270)))


(def
 v51_l283
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:data setosa})
  (pj/lay-smooth {:stat :linear-model, :data versicolor})
  kind/pprint))


(deftest
 t52_l289
 (is
  ((fn
    [v]
    (and
     (some? (:data v))
     (contains? (first (:layers v)) :data)
     (contains? (second (:layers v)) :data)))
   v51_l283)))


(def
 v54_l298
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t55_l302
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v54_l298)))


(def
 v57_l322
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t58_l326
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v57_l322)))


(def
 v60_l337
 (->
  (rdatasets/datasets-iris)
  (pj/pose [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point)))


(deftest
 t61_l341
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v60_l337)))


(def
 v63_l345
 (->
  (rdatasets/datasets-iris)
  (pj/pose [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point)
  kind/pprint))


(deftest
 t64_l350
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:poses v)))
     (= :sepal-length (get-in v [:poses 0 :mapping :x]))
     (= :sepal-width (get-in v [:poses 0 :mapping :y]))
     (= :petal-length (get-in v [:poses 1 :mapping :x]))
     (= :petal-width (get-in v [:poses 1 :mapping :y]))))
   v63_l345)))


(def
 v66_l360
 (pj/arrange
  [(-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-width))]))


(deftest
 t67_l364
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v66_l360)))


(def v69_l397 (pj/layer-type-lookup :histogram))


(deftest t70_l399 (is ((fn [m] (= :bar (:mark m))) v69_l397)))


(def
 v72_l403
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t73_l406
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v72_l403)))


(def v75_l410 (pj/layer-type-lookup :smooth))


(deftest t76_l412 (is ((fn [m] (= :loess (:stat m))) v75_l410)))


(def
 v78_l416
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t79_l421
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v78_l416)))


(def
 v81_l454
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} pj/lay-point))


(deftest
 t82_l457
 (is ((fn [v] (= 4 (:points (pj/svg-summary v)))) v81_l454)))


(def
 v84_l464
 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length :sepal-width)))


(deftest
 t85_l467
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v84_l464)))


(def v87_l471 (-> (rdatasets/datasets-iris) (pj/pose :sepal-length)))


(deftest
 t88_l474
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v87_l471)))


(def
 v90_l490
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width))))


(def v92_l496 (-> scatter-base (pj/lay-smooth {:stat :linear-model})))


(deftest
 t93_l498
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v92_l496)))


(def v95_l504 (-> scatter-base pj/lay-smooth))


(deftest
 t96_l506
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v95_l504)))


(def
 v98_l519
 (def
  scatter-with-regression
  (->
   (pj/pose nil {:x :x, :y :y, :color :group})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model})
   (pj/options {:title "Scatter with Regression"}))))


(def v100_l528 (kind/pprint scatter-with-regression))


(deftest
 t101_l530
 (is
  ((fn
    [v]
    (and
     (nil? (:data v))
     (= 2 (count (:layers v)))
     (= "Scatter with Regression" (get-in v [:opts :title]))))
   v100_l528)))


(def
 v103_l536
 (->
  scatter-with-regression
  (pj/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t104_l541
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v103_l536)))


(def
 v106_l547
 (->
  scatter-with-regression
  (pj/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t107_l552
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v106_l547)))


(def
 v109_l570
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t110_l573
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v109_l570)))


(def
 v112_l579
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t113_l582
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v112_l579)))


(def
 v115_l586
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t116_l589
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v115_l586)))


(def
 v118_l595
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:color :species})))


(deftest
 t119_l598
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v118_l595)))


(def
 v121_l604
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width {:group :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t122_l609
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v121_l604)))


(def
 v124_l628
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t125_l633
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (pj/svg-summary v))))
   v124_l628)))


(def
 v127_l642
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t128_l647
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v127_l642)))


(def
 v130_l653
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  kind/pprint))


(deftest
 t131_l659
 (is
  ((fn
    [v]
    (and
     (= :point (get-in v [:layers 0 :layer-type]))
     (= :rule-h (get-in v [:layers 1 :layer-type]))
     (= 3.0 (get-in v [:layers 1 :mapping :y-intercept]))
     (= :band-v (get-in v [:layers 2 :layer-type]))
     (= 5.0 (get-in v [:layers 2 :mapping :x-min]))))
   v130_l653)))


(def
 v133_l673
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/coord :flip)))


(deftest
 t134_l677
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v133_l673)))


(def
 v136_l682
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (pj/lay-point :population :area)
  (pj/scale :x :log)
  (pj/scale :y :log)))


(deftest
 t137_l688
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v136_l682)))


(def
 v139_l697
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/facet :species)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t140_l703
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v139_l697)))


(def
 v142_l710
 (->
  (rdatasets/datasets-iris)
  (pj/pose :sepal-length :sepal-width)
  (pj/facet :species)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t143_l717
 (is ((fn [v] (= :species (get-in v [:opts :facet-col]))) v142_l710)))


(def
 v145_l721
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t146_l724
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v145_l721)))


(def
 v148_l730
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram [:sepal-length :sepal-width :petal-length])
  kind/pprint))


(deftest
 t149_l734
 (is
  ((fn
    [v]
    (and
     (= 3 (count (:poses v)))
     (= :sepal-length (get-in v [:poses 0 :mapping :x]))
     (= :sepal-width (get-in v [:poses 1 :mapping :x]))
     (= :petal-length (get-in v [:poses 2 :mapping :x]))))
   v148_l730)))


(def
 v151_l741
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width))]))


(deftest
 t152_l747
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v151_l741)))
