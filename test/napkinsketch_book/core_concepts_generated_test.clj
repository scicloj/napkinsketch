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


(def
 v9_l53
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  kind/pprint))


(deftest
 t10_l57
 (is
  ((fn
    [v]
    (and
     (= 1 (count (:views v)))
     (= :species (get-in v [:views 0 :layers 0 :mapping :color]))))
   v9_l53)))


(def v12_l67 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t13_l71
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v12_l67)))


(def
 v15_l75
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t16_l81
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v15_l75)))


(def v18_l87 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t19_l90
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v18_l87)))


(def
 v21_l109
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t22_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v21_l109)))


(def
 v24_l122
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t25_l128
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :sepal-length (get-in v [:mapping :x]))))
   v24_l122)))


(def
 v27_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t28_l139
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v27_l136)))


(def
 v30_l146
 (def
  two-panel
  (sk/arrange
   [(->
     (rdatasets/datasets-iris)
     (sk/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (sk/lay-point :petal-length :petal-width))])))


(def v31_l153 two-panel)


(deftest
 t32_l155
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v31_l153)))


(def
 v34_l179
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t35_l184
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v34_l179)))


(def
 v37_l193
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t38_l197
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l193)))


(def
 v40_l204
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t41_l209
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:views 0 :layers 0 :mapping :color]))
     (not (contains? (:mapping (first (:layers v))) :color))))
   v40_l204)))


(def
 v43_l220
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t44_l225
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l220)))


(def
 v46_l233
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t47_l239
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:mapping :color]))
     (contains? (get (first (:layers v)) :mapping) :color)
     (nil? (get-in (first (:layers v)) [:mapping :color]))))
   v46_l233)))


(def
 v49_l263
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__124743#] (= "setosa" (:species p1__124743#))))))


(def
 v50_l267
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__124744#] (= "versicolor" (:species p1__124744#))))))


(def
 v51_l271
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t52_l276
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v51_l271)))


(def
 v54_l284
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})
  kind/pprint))


(deftest
 t55_l290
 (is
  ((fn
    [v]
    (and
     (some? (:data v))
     (contains? (first (:layers v)) :data)
     (contains? (second (:layers v)) :data)))
   v54_l284)))


(def
 v57_l299
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t58_l303
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v57_l299)))


(def
 v60_l323
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t61_l327
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l323)))


(def
 v63_l337
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t64_l341
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v63_l337)))


(def
 v66_l346
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  kind/pprint))


(deftest
 t67_l351
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:views v)))
     (= :sepal-length (get-in v [:views 0 :mapping :x]))
     (= :petal-length (get-in v [:views 1 :mapping :x]))))
   v66_l346)))


(def
 v69_l360
 (sk/arrange
  [(-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-width))]))


(deftest
 t70_l364
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v69_l360)))


(def
 v72_l381
 (def
  my-frame
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}],
    :opts {:title "Iris"}})))


(def v73_l389 my-frame)


(deftest
 t74_l391
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Iris"} (:texts s)))))
   v73_l389)))


(def v76_l398 (kind/pprint my-frame))


(deftest
 t77_l400
 (is
  ((fn [fr] (= #{:mapping :opts :layers :data} (set (keys fr))))
   v76_l398)))


(def
 v79_l407
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris"})))


(deftest
 t80_l413
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l407)))


(def
 v82_l419
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris"})
  kind/pprint))


(deftest
 t83_l426
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))
     (= "Iris" (get-in v [:opts :title]))))
   v82_l419)))


(def v85_l441 (sk/layer-type-lookup :histogram))


(deftest t86_l443 (is ((fn [m] (= :bar (:mark m))) v85_l441)))


(def
 v88_l447
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t89_l450
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v88_l447)))


(def v91_l454 (sk/layer-type-lookup :smooth))


(deftest t92_l456 (is ((fn [m] (= :loess (:stat m))) v91_l454)))


(def
 v94_l460
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t95_l465
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v94_l460)))


(def
 v97_l510
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/lay-point))


(deftest
 t98_l513
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v97_l510)))


(def
 v100_l520
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t101_l523
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v100_l520)))


(def v103_l527 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t104_l530
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v103_l527)))


(def
 v106_l555
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v108_l561 (-> scatter-base (sk/lay-smooth {:stat :linear-model})))


(deftest
 t109_l563
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l561)))


(def v111_l569 (-> scatter-base sk/lay-smooth))


(deftest
 t112_l571
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v111_l569)))


(def
 v114_l584
 (def
  scatter-with-regression
  (->
   (sk/frame nil {:x :x, :y :y, :color :group})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Scatter with Regression"}))))


(def v116_l593 (kind/pprint scatter-with-regression))


(deftest
 t117_l595
 (is
  ((fn
    [v]
    (and
     (nil? (:data v))
     (= 2 (count (:layers v)))
     (= "Scatter with Regression" (get-in v [:opts :title]))))
   v116_l593)))


(def
 v119_l601
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t120_l606
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v119_l601)))


(def
 v122_l612
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t123_l617
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v122_l612)))


(def
 v125_l635
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t126_l638
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v125_l635)))


(def
 v128_l644
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t129_l647
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v128_l644)))


(def
 v131_l651
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t132_l654
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v131_l651)))


(def
 v134_l660
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t135_l663
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v134_l660)))


(def
 v137_l669
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:group :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t138_l674
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v137_l669)))


(def
 v140_l693
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t141_l698
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v140_l693)))


(def
 v143_l707
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t144_l712
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v143_l707)))


(def
 v146_l718
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  kind/pprint))


(deftest
 t147_l724
 (is
  ((fn
    [v]
    (and
     (= :rule-h (get-in v [:layers 0 :layer-type]))
     (= 3.0 (get-in v [:layers 0 :mapping :y-intercept]))
     (= :band-v (get-in v [:layers 1 :layer-type]))
     (= 5.0 (get-in v [:layers 1 :mapping :x-min]))))
   v146_l718)))


(def
 v149_l737
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t150_l741
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v149_l737)))


(def
 v152_l746
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t153_l752
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v152_l746)))


(def
 v155_l761
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t156_l767
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l761)))


(def
 v158_l774
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t159_l781
 (is ((fn [v] (= :species (get-in v [:opts :facet-col]))) v158_l774)))


(def
 v161_l785
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t162_l788
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v161_l785)))


(def
 v164_l793
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])
  kind/pprint))


(deftest
 t165_l797
 (is
  ((fn
    [v]
    (and
     (= 3 (count (:views v)))
     (= :sepal-length (get-in v [:views 0 :mapping :x]))
     (= :sepal-width (get-in v [:views 1 :mapping :x]))
     (= :petal-length (get-in v [:views 2 :mapping :x]))))
   v164_l793)))


(def
 v167_l804
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width))]))


(deftest
 t168_l810
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v167_l804)))
