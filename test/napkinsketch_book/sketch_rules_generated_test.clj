(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l46 (def iris (rdatasets/datasets-iris)))


(def
 v5_l51
 (defn
  sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__80719#] (dissoc p1__80719# :data))))
   kind/pprint)))


(def
 v7_l72
 (-> iris (sk/lay-point :sepal-length :sepal-width) sk-summary))


(deftest
 t8_l76
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v7_l72)))


(def v9_l82 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t10_l85
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v9_l82)))


(def
 v12_l94
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary))


(deftest
 t13_l100
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (= :lm (:method (second (:layers m))))
     (nil? (:layers (first (:views m))))))
   v12_l94)))


(def
 v14_l106
 (-> iris (sk/view :sepal-length :sepal-width) sk/lay-point sk/lay-lm))


(deftest
 t15_l111
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v14_l106)))


(def
 v17_l124
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t18_l129
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :lm (:method (first (:layers m))))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v17_l124)))


(def
 v19_l135
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t20_l139
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v19_l135)))


(def
 v22_l151
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/lay-point :sepal-length :sepal-width {:color :species})
     sk/lay-lm))]
  {:count (count d),
   :first-mark (:mark (first d)),
   :first-color (:color (first d)),
   :second-mark (:mark (second d)),
   :second-color (:color (second d))}))


(deftest
 t23_l160
 (is
  ((fn
    [m]
    (and
     (= 2 (:count m))
     (= :point (:first-mark m))
     (= :species (:first-color m))
     (= :line (:second-mark m))
     (nil? (:second-color m))))
   v22_l151)))


(def
 v25_l173
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t26_l178
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)) (= 2 (:lines s)))))
   v25_l173)))


(def
 v28_l188
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/lay-point :sepal-length :sepal-width)
     (sk/lay-point :petal-length :petal-width)
     sk/lay-lm))]
  (count d)))


(deftest t29_l194 (is ((fn [n] (= 4 n)) v28_l188)))


(def
 v31_l207
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk-summary))


(deftest
 t32_l213
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v31_l207)))


(def
 v33_l217
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t34_l222
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v33_l217)))


(def
 v36_l236
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary))


(deftest
 t37_l241
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 2 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))
     (= :lm (:method (second (:layers (first (:views m))))))))
   v36_l236)))


(def
 v38_l248
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t39_l252
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)) (= 1 (:lines s)))))
   v38_l248)))


(def
 v41_l266
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t42_l271
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v41_l266)))


(def
 v43_l277
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t44_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v43_l277)))


(def v46_l296 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary))


(deftest
 t47_l300
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= 1 (count (:layers (first (:views m)))))))
   v46_l296)))


(def v48_l306 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t49_l309
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v48_l306)))


(def
 v51_l313
 (->
  {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]}
  sk/lay-point
  sk-summary))


(deftest
 t52_l317
 (is
  ((fn
    [m]
    (and
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= :c (get-in m [:views 0 :mapping :color]))))
   v51_l313)))


(def
 v54_l334
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t55_l339
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v54_l334)))


(def
 v57_l347
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/view :sepal-length :sepal-width)
     (sk/view :petal-length :petal-width)
     sk/lay-point))]
  (mapv (fn* [p1__80720#] (tc/row-count (:data p1__80720#))) d)))


(deftest t58_l353 (is ((fn [v] (= [150 150] v)) v57_l347)))


(def
 v60_l366
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t61_l372
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v60_l366)))


(def
 v62_l377
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t63_l382
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v62_l377)))


(def
 v65_l391
 (let
  [d
   (sk/draft
    (->
     (sk/sketch iris {:color :species})
     (sk/view :sepal-length :sepal-width)
     (sk/view :petal-length :petal-width)
     sk/lay-point))]
  (mapv :color d)))


(deftest
 t66_l397
 (is
  ((fn [v] (every? (fn* [p1__80721#] (= :species p1__80721#)) v))
   v65_l391)))


(def
 v68_l404
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t69_l410
 (is
  ((fn
    [m]
    (and
     (= {} (:mapping m))
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:views 1 :mapping :color]))))
   v68_l404)))


(def
 v70_l415
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t71_l420
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v70_l415)))


(def
 v73_l430
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/view :sepal-length :sepal-width {:color :species})
     (sk/view :petal-length :petal-width)
     sk/lay-point))]
  (mapv :color d)))


(deftest t74_l436 (is ((fn [v] (= [:species nil] v)) v73_l430)))


(def
 v76_l443
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t77_l448
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= 1 (count (:layers m)))
     (= {} (:mapping (first (:layers m))))))
   v76_l443)))


(def
 v78_l453
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t79_l457
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v78_l453)))


(def
 v81_l466
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/lay-point :sepal-length :sepal-width {:color :species})
     sk/lay-lm))]
  (mapv :color d)))


(deftest t82_l471 (is ((fn [v] (= [:species nil] v)) v81_l466)))


(def
 v84_l481
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary))


(deftest
 t85_l487
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 2 (count (:layers m)))
     (contains? (:mapping (second (:layers m))) :color)
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v84_l481)))


(def
 v86_l493
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t87_l498
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v86_l493)))


(def
 v89_l505
 (let
  [d
   (sk/draft
    (->
     iris
     (sk/view :sepal-length :sepal-width {:color :species})
     sk/lay-point
     (sk/lay-lm {:color nil})))]
  {:point-color (:color (first d)), :lm-color (:color (second d))}))


(deftest
 t90_l512
 (is
  ((fn [m] (and (= :species (:point-color m)) (nil? (:lm-color m))))
   v89_l505)))


(def
 v92_l519
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t93_l524
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v92_l519)))


(def
 v95_l544
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris Scatter"})
  sk-summary))


(deftest
 t96_l549
 (is
  ((fn
    [m]
    (and
     (= "Iris Scatter" (get-in m [:opts :title]))
     (= {} (:mapping m))))
   v95_l544)))


(def
 v97_l553
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:title "Iris Scatter"})))


(deftest
 t98_l557
 (is
  ((fn [v] (let [p (sk/plan v)] (= "Iris Scatter" (:title p))))
   v97_l553)))


(def
 v100_l564
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)
  sk-summary))


(deftest
 t101_l571
 (is
  ((fn
    [m]
    (and
     (= {:type :log} (get-in m [:opts :x-scale]))
     (= :flip (get-in m [:opts :coord]))))
   v100_l564)))


(def
 v103_l578
 (let
  [plan
   (sk/plan
    (->
     iris
     (sk/view :sepal-length :sepal-width)
     sk/lay-point
     (sk/scale :x :log)
     (sk/coord :flip)))
   panel
   (first (:panels plan))]
  {:coord (:coord panel), :y-scale-type (:type (:y-scale panel))}))


(deftest
 t104_l587
 (is
  ((fn [m] (and (= :flip (:coord m)) (= :log (:y-scale-type m))))
   v103_l578)))


(def
 v106_l596
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary))


(deftest
 t107_l601
 (is
  ((fn
    [m]
    (and
     (= 1 (count (get-in m [:opts :annotations])))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v106_l596)))


(def
 v108_l605
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t109_l609
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v108_l605)))


(def
 v111_l618
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t112_l623
 (is ((fn [m] (= :species (get-in m [:opts :facet-col]))) v111_l618)))


(def
 v113_l626
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)
  sk-summary))


(deftest
 t114_l631
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:opts :facet-col]))
     (= :species (get-in m [:opts :facet-row]))))
   v113_l626)))


(def
 v116_l663
 (def
  assembly-sketch
  (->
   iris
   (sk/lay-point :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-lm)))


(def v117_l669 (count (sk/draft assembly-sketch)))


(deftest t118_l671 (is ((fn [n] (= 3 n)) v117_l669)))


(def v120_l676 (mapv :mark (sk/draft assembly-sketch)))


(deftest t121_l678 (is ((fn [v] (= [:point :line :line] v)) v120_l676)))


(def
 v123_l686
 (let
  [sk
   (->
    (sk/sketch iris {:color :species})
    (sk/lay-point :sepal-length :sepal-width))
   d
   (first (sk/draft sk))]
  (select-keys d [:x :y :color :mark])))


(deftest
 t124_l691
 (is
  ((fn
    [m]
    (and
     (= :sepal-length (:x m))
     (= :sepal-width (:y m))
     (= :species (:color m))
     (= :point (:mark m))))
   v123_l686)))


(def
 v126_l712
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t127_l716
 (is
  ((fn [v] (let [p (sk/plan v)] (= 2 (count (:panels p))))) v126_l712)))


(def
 v129_l724
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t130_l728
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 1 (count (:panels p)))
      (= 2 (count (:layers (first (:panels p))))))))
   v129_l724)))


(def
 v132_l740
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t133_l744
 (is
  ((fn [v] (let [p (sk/plan v)] (= 3 (count (:panels p))))) v132_l740)))


(def
 v135_l755
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t136_l759
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and (= 2 (count (:panels p))) (= 1 (get-in p [:grid :cols])))))
   v135_l755)))


(def
 v138_l771
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v139_l773
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t140_l776
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 9 (count (:panels p)))
      (= 3 (get-in p [:grid :rows]))
      (= 3 (get-in p [:grid :cols])))))
   v139_l773)))
