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
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})))


(deftest
 t7_l49
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v6_l45)))


(def
 v9_l55
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  kind/pprint))


(deftest
 t10_l60
 (is
  ((fn
    [v]
    (and
     (= :sepal-length (get-in v [:mapping :x]))
     (= 1 (count (:layers v)))
     (= :species (get-in v [:layers 0 :mapping :color]))))
   v9_l55)))


(def v12_l71 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (sk/lay-point :x :y)))


(deftest
 t13_l75
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v12_l71)))


(def
 v15_l79
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (sk/lay-value-bar :city :temperature)))


(deftest
 t16_l85
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v15_l79)))


(def v18_l91 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t19_l94
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v18_l91)))


(def
 v21_l113
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t22_l118
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v21_l113)))


(def
 v24_l126
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t25_l132
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :sepal-length (get-in v [:mapping :x]))))
   v24_l126)))


(def
 v27_l140
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t28_l143
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v27_l140)))


(def
 v30_l150
 (def
  two-panel
  (sk/arrange
   [(->
     (rdatasets/datasets-iris)
     (sk/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (sk/lay-point :petal-length :petal-width))])))


(def v31_l157 two-panel)


(deftest
 t32_l159
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v31_l157)))


(def
 v34_l183
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t35_l188
 (is ((fn [v] (= 3 (:lines (sk/svg-summary v)))) v34_l183)))


(def
 v37_l197
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t38_l202
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l197)))


(def
 v40_l209
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t41_l215
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:layers 0 :mapping :color]))
     (not (contains? (or (get-in v [:layers 1 :mapping]) {}) :color))))
   v40_l209)))


(def
 v43_l227
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t44_l232
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l227)))


(def
 v46_l240
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t47_l246
 (is
  ((fn
    [v]
    (and
     (= :species (get-in v [:mapping :color]))
     (contains? (get (first (:layers v)) :mapping) :color)
     (nil? (get-in (first (:layers v)) [:mapping :color]))))
   v46_l240)))


(def
 v49_l270
 (def
  setosa
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__139735#] (= "setosa" (:species p1__139735#))))))


(def
 v50_l274
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__139736#] (= "versicolor" (:species p1__139736#))))))


(def
 v51_l278
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t52_l283
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v51_l278)))


(def
 v54_l291
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:data setosa})
  (sk/lay-smooth {:stat :linear-model, :data versicolor})
  kind/pprint))


(deftest
 t55_l297
 (is
  ((fn
    [v]
    (and
     (some? (:data v))
     (contains? (first (:layers v)) :data)
     (contains? (second (:layers v)) :data)))
   v54_l291)))


(def
 v57_l306
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t58_l310
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v57_l306)))


(def
 v60_l330
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t61_l334
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l330)))


(def
 v63_l344
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t64_l348
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v63_l344)))


(def
 v66_l353
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  kind/pprint))


(deftest
 t67_l358
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:views v)))
     (= :sepal-length (get-in v [:views 0 :mapping :x]))
     (= :petal-length (get-in v [:views 1 :mapping :x]))))
   v66_l353)))


(def
 v69_l367
 (sk/arrange
  [(-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-width))]))


(deftest
 t70_l371
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v69_l367)))


(def
 v72_l388
 (def
  my-frame
  (sk/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}],
    :opts {:title "Iris"}})))


(def v73_l396 my-frame)


(deftest
 t74_l398
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Iris"} (:texts s)))))
   v73_l396)))


(def v76_l405 (kind/pprint my-frame))


(deftest
 t77_l407
 (is
  ((fn [fr] (= #{:mapping :opts :layers :data} (set (keys fr))))
   v76_l405)))


(def
 v79_l414
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris"})))


(deftest
 t80_l420
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l414)))


(def
 v82_l426
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  (sk/options {:title "Iris"})
  kind/pprint))


(deftest
 t83_l433
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))
     (= "Iris" (get-in v [:opts :title]))))
   v82_l426)))


(def v85_l448 (sk/layer-type-lookup :histogram))


(deftest t86_l450 (is ((fn [m] (= :bar (:mark m))) v85_l448)))


(def
 v88_l454
 (-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-length)))


(deftest
 t89_l457
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v88_l454)))


(def v91_l461 (sk/layer-type-lookup :smooth))


(deftest t92_l463 (is ((fn [m] (= :loess (:stat m))) v91_l461)))


(def
 v94_l467
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (sk/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t95_l472
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v94_l467)))


(def
 v97_l517
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} sk/lay-point))


(deftest
 t98_l520
 (is ((fn [v] (= 4 (:points (sk/svg-summary v)))) v97_l517)))


(def
 v100_l527
 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length :sepal-width)))


(deftest
 t101_l530
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v100_l527)))


(def v103_l534 (-> (rdatasets/datasets-iris) (sk/frame :sepal-length)))


(deftest
 t104_l537
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v103_l534)))


(def
 v106_l562
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v108_l568 (-> scatter-base (sk/lay-smooth {:stat :linear-model})))


(deftest
 t109_l570
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l568)))


(def v111_l576 (-> scatter-base sk/lay-smooth))


(deftest
 t112_l578
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v111_l576)))


(def
 v114_l591
 (def
  scatter-with-regression
  (->
   (sk/frame nil {:x :x, :y :y, :color :group})
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model})
   (sk/options {:title "Scatter with Regression"}))))


(def v116_l600 (kind/pprint scatter-with-regression))


(deftest
 t117_l602
 (is
  ((fn
    [v]
    (and
     (nil? (:data v))
     (= 2 (count (:layers v)))
     (= "Scatter with Regression" (get-in v [:opts :title]))))
   v116_l600)))


(def
 v119_l608
 (->
  scatter-with-regression
  (sk/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t120_l613
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v119_l608)))


(def
 v122_l619
 (->
  scatter-with-regression
  (sk/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t123_l624
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v122_l619)))


(def
 v125_l642
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t126_l645
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v125_l642)))


(def
 v128_l651
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t129_l654
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v128_l651)))


(def
 v131_l658
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t132_l661
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v131_l658)))


(def
 v134_l667
 (->
  (rdatasets/datasets-iris)
  (sk/lay-density :sepal-length {:color :species})))


(deftest
 t135_l670
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v134_l667)))


(def
 v137_l676
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width {:group :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t138_l681
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v137_l676)))


(def
 v140_l700
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t141_l705
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))
   v140_l700)))


(def
 v143_l714
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t144_l719
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v143_l714)))


(def
 v146_l725
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  (sk/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  kind/pprint))


(deftest
 t147_l731
 (is
  ((fn
    [v]
    (and
     (= :rule-h (get-in v [:layers 0 :layer-type]))
     (= 3.0 (get-in v [:layers 0 :mapping :y-intercept]))
     (= :band-v (get-in v [:layers 1 :layer-type]))
     (= 5.0 (get-in v [:layers 1 :mapping :x-min]))))
   v146_l725)))


(def
 v149_l744
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :flip)))


(deftest
 t150_l748
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v149_l744)))


(def
 v152_l753
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (sk/lay-point :population :area)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t153_l759
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v152_l753)))


(def
 v155_l768
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t156_l774
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l768)))


(def
 v158_l781
 (->
  (rdatasets/datasets-iris)
  (sk/frame :sepal-length :sepal-width)
  (sk/facet :species)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t159_l788
 (is ((fn [v] (= :species (get-in v [:opts :facet-col]))) v158_l781)))


(def
 v161_l792
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t162_l795
 (is ((fn [v] (= 3 (:panels (sk/svg-summary v)))) v161_l792)))


(def
 v164_l800
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram [:sepal-length :sepal-width :petal-length])
  kind/pprint))


(deftest
 t165_l804
 (is
  ((fn
    [v]
    (and
     (= 3 (count (:views v)))
     (= :sepal-length (get-in v [:views 0 :mapping :x]))
     (= :sepal-width (get-in v [:views 1 :mapping :x]))
     (= :petal-length (get-in v [:views 2 :mapping :x]))))
   v164_l800)))


(def
 v167_l811
 (sk/arrange
  [(->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width))]))


(deftest
 t168_l817
 (is ((fn [v] (= 2 (:panels (sk/svg-summary v)))) v167_l811)))
