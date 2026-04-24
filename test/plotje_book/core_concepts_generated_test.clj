(ns
 plotje-book.core-concepts-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (rdatasets/datasets-iris))


(deftest t4_l33 (is ((fn [ds] (= 150 (count (tc/rows ds)))) v3_l31)))


(def
 v6_l45
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:color :species})))


(deftest
 t7_l49
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v6_l45)))


(def
 v9_l55
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
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


(def v12_l71 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-point :x :y)))


(deftest
 t13_l75
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v12_l71)))


(def
 v15_l79
 (->
  [{:city "Paris", :temperature 22}
   {:city "London", :temperature 18}
   {:city "Berlin", :temperature 20}
   {:city "Rome", :temperature 28}]
  (pj/lay-value-bar :city :temperature)))


(deftest
 t16_l85
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v15_l79)))


(def v18_l91 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t19_l94
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v18_l91)))


(def
 v21_l113
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t22_l118
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v21_l113)))


(def
 v24_l126
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
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
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t28_l143
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v27_l140)))


(def
 v30_l150
 (def
  two-panel
  (pj/arrange
   [(->
     (rdatasets/datasets-iris)
     (pj/lay-point :sepal-length :sepal-width))
    (->
     (rdatasets/datasets-iris)
     (pj/lay-point :petal-length :petal-width))])))


(def v31_l157 two-panel)


(deftest
 t32_l159
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v31_l157)))


(def
 v34_l183
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t35_l188
 (is ((fn [v] (= 3 (:lines (pj/svg-summary v)))) v34_l183)))


(def
 v37_l197
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t38_l202
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v37_l197)))


(def
 v40_l209
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})
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
  (pj/frame :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:color nil})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t44_l232
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v43_l227)))


(def
 v46_l240
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  (pj/lay-point {:color nil})
  (pj/lay-smooth {:stat :linear-model})
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
   (fn* [p1__80080#] (= "setosa" (:species p1__80080#))))))


(def
 v50_l274
 (def
  versicolor
  (tc/select-rows
   (rdatasets/datasets-iris)
   (fn* [p1__80081#] (= "versicolor" (:species p1__80081#))))))


(def
 v51_l278
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:data setosa})
  (pj/lay-smooth {:stat :linear-model, :data versicolor})))


(deftest
 t52_l283
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v51_l278)))


(def
 v54_l291
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/lay-point {:data setosa})
  (pj/lay-smooth {:stat :linear-model, :data versicolor})
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
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t58_l310
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v57_l306)))


(def
 v60_l330
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t61_l334
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v60_l330)))


(def
 v63_l345
 (->
  (rdatasets/datasets-iris)
  (pj/frame
   [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point)))


(deftest
 t64_l349
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v63_l345)))


(def
 v66_l353
 (->
  (rdatasets/datasets-iris)
  (pj/frame
   [[:sepal-length :sepal-width] [:petal-length :petal-width]])
  (pj/lay-point)
  kind/pprint))


(deftest
 t67_l358
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:frames v)))
     (= :sepal-length (get-in v [:frames 0 :mapping :x]))
     (= :sepal-width (get-in v [:frames 0 :mapping :y]))
     (= :petal-length (get-in v [:frames 1 :mapping :x]))
     (= :petal-width (get-in v [:frames 1 :mapping :y]))))
   v66_l353)))


(def
 v69_l368
 (pj/arrange
  [(-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-width))
   (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-width))]))


(deftest
 t70_l372
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v69_l368)))


(def
 v72_l389
 (def
  my-frame
  (pj/prepare-frame
   {:data (rdatasets/datasets-iris),
    :mapping {:x :sepal-length, :y :sepal-width, :color :species},
    :layers
    [{:layer-type :point}
     {:layer-type :smooth, :mapping {:stat :linear-model}}],
    :opts {:title "Iris"}})))


(def v73_l397 my-frame)


(deftest
 t74_l399
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Iris"} (:texts s)))))
   v73_l397)))


(def v76_l406 (kind/pprint my-frame))


(deftest
 t77_l408
 (is
  ((fn [fr] (= #{:mapping :opts :layers :data} (set (keys fr))))
   v76_l406)))


(def
 v79_l415
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris"})))


(deftest
 t80_l421
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v79_l415)))


(def
 v82_l427
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Iris"})
  kind/pprint))


(deftest
 t83_l434
 (is
  ((fn
    [v]
    (and
     (= 2 (count (:layers v)))
     (= :species (get-in v [:mapping :color]))
     (= "Iris" (get-in v [:opts :title]))))
   v82_l427)))


(def v85_l449 (pj/layer-type-lookup :histogram))


(deftest t86_l451 (is ((fn [m] (= :bar (:mark m))) v85_l449)))


(def
 v88_l455
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t89_l458
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v88_l455)))


(def v91_l462 (pj/layer-type-lookup :smooth))


(deftest t92_l464 (is ((fn [m] (= :loess (:stat m))) v91_l462)))


(def
 v94_l468
 (->
  {:day ["Mon" "Mon" "Tue" "Tue"],
   :count [30 20 45 15],
   :meal ["lunch" "dinner" "lunch" "dinner"]}
  (pj/lay-value-bar :day :count {:color :meal, :position :stack})))


(deftest
 t95_l473
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v94_l468)))


(def
 v97_l518
 (-> {:height [170 180 165 175], :weight [70 80 65 75]} pj/lay-point))


(deftest
 t98_l521
 (is ((fn [v] (= 4 (:points (pj/svg-summary v)))) v97_l518)))


(def
 v100_l528
 (-> (rdatasets/datasets-iris) (pj/frame :sepal-length :sepal-width)))


(deftest
 t101_l531
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v100_l528)))


(def v103_l535 (-> (rdatasets/datasets-iris) (pj/frame :sepal-length)))


(deftest
 t104_l538
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v103_l535)))


(def
 v106_l563
 (def
  scatter-base
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width))))


(def v108_l569 (-> scatter-base (pj/lay-smooth {:stat :linear-model})))


(deftest
 t109_l571
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l569)))


(def v111_l577 (-> scatter-base pj/lay-smooth))


(deftest
 t112_l579
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v111_l577)))


(def
 v114_l592
 (def
  scatter-with-regression
  (->
   (pj/frame nil {:x :x, :y :y, :color :group})
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model})
   (pj/options {:title "Scatter with Regression"}))))


(def v116_l601 (kind/pprint scatter-with-regression))


(deftest
 t117_l603
 (is
  ((fn
    [v]
    (and
     (nil? (:data v))
     (= 2 (count (:layers v)))
     (= "Scatter with Regression" (get-in v [:opts :title]))))
   v116_l601)))


(def
 v119_l609
 (->
  scatter-with-regression
  (pj/with-data
   {:x [1 2 3 4 5 6],
    :y [2 4 3 5 6 8],
    :group ["a" "a" "a" "b" "b" "b"]})))


(deftest
 t120_l614
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v119_l609)))


(def
 v122_l620
 (->
  scatter-with-regression
  (pj/with-data
   {:x [10 20 30 40 50 60],
    :y [15 18 22 20 25 28],
    :group ["x" "x" "x" "y" "y" "y"]})))


(deftest
 t123_l625
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v122_l620)))


(def
 v125_l643
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})))


(deftest
 t126_l646
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (some #{"setosa"} (:texts s)))))
   v125_l643)))


(def
 v128_l652
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t129_l655
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v128_l652)))


(def
 v131_l659
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color "steelblue"})))


(deftest
 t132_l662
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v131_l659)))


(def
 v134_l668
 (->
  (rdatasets/datasets-iris)
  (pj/lay-density :sepal-length {:color :species})))


(deftest
 t135_l671
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v134_l668)))


(def
 v137_l677
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width {:group :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t138_l682
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v137_l677)))


(def
 v140_l701
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Measurements", :width 500, :palette :dark2})))


(deftest
 t141_l706
 (is
  ((fn [v] (some #{"Iris Measurements"} (:texts (pj/svg-summary v))))
   v140_l701)))


(def
 v143_l715
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})))


(deftest
 t144_l720
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v143_l715)))


(def
 v146_l726
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-band-v {:x-min 5.0, :x-max 6.0, :alpha 0.1})
  kind/pprint))


(deftest
 t147_l732
 (is
  ((fn
    [v]
    (and
     (= :point (get-in v [:layers 0 :layer-type]))
     (= :rule-h (get-in v [:layers 1 :layer-type]))
     (= 3.0 (get-in v [:layers 1 :mapping :y-intercept]))
     (= :band-v (get-in v [:layers 2 :layer-type]))
     (= 5.0 (get-in v [:layers 2 :mapping :x-min]))))
   v146_l726)))


(def
 v149_l746
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/coord :flip)))


(deftest
 t150_l750
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v149_l746)))


(def
 v152_l755
 (->
  {:population [1000 5000 50000 200000 1000000 5000000],
   :area [2 8 30 120 500 2100]}
  (pj/lay-point :population :area)
  (pj/scale :x :log)
  (pj/scale :y :log)))


(deftest
 t153_l761
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v152_l755)))


(def
 v155_l770
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/facet :species)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t156_l776
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v155_l770)))


(def
 v158_l783
 (->
  (rdatasets/datasets-iris)
  (pj/frame :sepal-length :sepal-width)
  (pj/facet :species)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})
  kind/pprint))


(deftest
 t159_l790
 (is ((fn [v] (= :species (get-in v [:opts :facet-col]))) v158_l783)))


(def
 v161_l794
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram [:sepal-length :sepal-width :petal-length])))


(deftest
 t162_l797
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v161_l794)))


(def
 v164_l803
 (->
  (rdatasets/datasets-iris)
  (pj/lay-histogram [:sepal-length :sepal-width :petal-length])
  kind/pprint))


(deftest
 t165_l807
 (is
  ((fn
    [v]
    (and
     (= 3 (count (:frames v)))
     (= :sepal-length (get-in v [:frames 0 :mapping :x]))
     (= :sepal-width (get-in v [:frames 1 :mapping :x]))
     (= :petal-length (get-in v [:frames 2 :mapping :x]))))
   v164_l803)))


(def
 v167_l814
 (pj/arrange
  [(->
    (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width))
   (->
    (rdatasets/datasets-iris)
    (pj/lay-point :petal-length :petal-width))]))


(deftest
 t168_l820
 (is ((fn [v] (= 2 (:panels (pj/svg-summary v)))) v167_l814)))
