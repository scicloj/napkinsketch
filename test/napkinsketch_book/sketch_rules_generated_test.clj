(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l55 (def iris (rdatasets/datasets-iris)))


(def
 v5_l60
 (defn
  sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__94398#] (dissoc p1__94398# :data))))
   kind/pprint)))


(def v7_l81 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t8_l84
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v7_l81)))


(def
 v9_l86
 (-> iris (sk/lay-point :sepal-length :sepal-width) sk-summary))


(deftest
 t10_l90
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:layer-type (first (:layers (first (:views m))))))))
   v9_l86)))


(def
 v12_l103
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t13_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 150 (:points s))
      (= 1 (:lines s))
      (= 2 (count d))
      (= :point (:mark (first d)))
      (= :sepal-length (:x (first d)))
      (= :sepal-width (:y (first d)))
      (= :identity (:stat (first d)))
      (= :line (:mark (second d)))
      (= :sepal-length (:x (second d)))
      (= :sepal-width (:y (second d)))
      (= :linear-model (:stat (second d))))))
   v12_l103)))


(def
 v14_l122
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t15_l128
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:layer-type (first (:layers m))))
     (= :smooth (:layer-type (second (:layers m))))
     (nil? (:layers (first (:views m))))))
   v14_l122)))


(def
 v17_l143
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t18_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 150 (:points s))
      (= 1 (:lines s))
      (= 2 (count d))
      (= :point (:mark (first d)))
      (= :species (:color (first d)))
      (= :line (:mark (second d)))
      (nil? (:color (second d))))))
   v17_l143)))


(def
 v19_l157
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t20_l162
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :smooth (:layer-type (first (:layers m))))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:layer-type (first (:layers (first (:views m))))))))
   v19_l157)))


(def
 v22_l178
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t23_l183
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 300 (:points s))
      (= 2 (:lines s))
      (= 4 (count d)))))
   v22_l178)))


(def
 v24_l190
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t25_l196
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :smooth (:layer-type (first (:layers m))))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= 1 (count (:layers (second (:views m)))))))
   v24_l190)))


(def
 v27_l218
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t28_l223
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v27_l218)))


(def
 v29_l227
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk-summary))


(deftest
 t30_l233
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v29_l227)))


(def
 v32_l247
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t33_l252
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 150 (:points s))
      (= 1 (:lines s))
      (= 2 (count d))
      (= :point (:mark (first d)))
      (= :line (:mark (second d))))))
   v32_l247)))


(def
 v34_l261
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})
  sk-summary))


(deftest
 t35_l267
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:layer-type (first (:layers (first (:views m))))))
     (= 1 (count (:layers (second (:views m)))))
     (= :smooth (:layer-type (first (:layers (second (:views m))))))))
   v34_l261)))


(def
 v37_l285
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t38_l289
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v37_l285)))


(def
 v39_l294
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t40_l299
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v39_l294)))


(def v42_l315 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t43_l318
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v42_l315)))


(def v44_l320 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary))


(deftest
 t45_l324
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= 1 (count (:layers (first (:views m)))))))
   v44_l320)))


(def
 v47_l332
 (-> {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]} sk/lay-point))


(deftest
 t48_l335
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v47_l332)))


(def
 v49_l337
 (->
  {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]}
  sk/lay-point
  sk-summary))


(deftest
 t50_l341
 (is
  ((fn
    [m]
    (and
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= :c (get-in m [:views 0 :mapping :color]))))
   v49_l337)))


(def
 v52_l364
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t53_l369
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v52_l364)))


(def
 v54_l373
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t55_l379
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v54_l373)))


(def v57_l387 (def tiny {:x [1 2 3], :y [3 5 4]}))


(def
 v58_l389
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point))


(deftest
 t59_l394
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 153 (:points s))
      (= 150 (tc/row-count (:data (first d))))
      (= 3 (tc/row-count (:data (second d)))))))
   v58_l389)))


(def
 v60_l401
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point
  sk-summary))


(deftest
 t61_l407
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v60_l401)))


(def v63_l415 (def iris-small (tc/head iris 10)))


(def
 v64_l417
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t65_l422
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 10 (:points s))
      (= 1 (:lines s))
      (= 10 (tc/row-count (:data (first d))))
      (= 150 (tc/row-count (:data (second d)))))))
   v64_l417)))


(def
 v66_l429
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t67_l435
 (is
  ((fn [m] (and (= 1 (count (:views m))) (= 2 (count (:layers m)))))
   v66_l429)))


(def
 v69_l453
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t70_l458
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 300 (:points s))
      (every? (fn* [p1__94399#] (= :species (:color p1__94399#))) d))))
   v69_l453)))


(def
 v71_l464
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t72_l470
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v71_l464)))


(def
 v74_l483
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t75_l488
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 300 (:points s))
      (= :species (:color (first d)))
      (nil? (:color (second d))))))
   v74_l483)))


(def
 v76_l495
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t77_l501
 (is
  ((fn
    [m]
    (and
     (= {} (:mapping m))
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:views 1 :mapping :color]))))
   v76_l495)))


(def
 v79_l514
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t80_l518
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 150 (:points s))
      (= 1 (:lines s))
      (= :species (:color (first d)))
      (nil? (:color (second d))))))
   v79_l514)))


(def
 v81_l525
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t82_l530
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= 1 (count (:layers m)))
     (nil? (:color (:mapping (first (:layers m)))))))
   v81_l525)))


(def
 v84_l546
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model, :color nil})))


(deftest
 t85_l551
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 150 (:points s))
      (= 1 (:lines s))
      (= :species (:color (first d)))
      (nil? (:color (second d))))))
   v84_l546)))


(def
 v86_l558
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model, :color nil})
  sk-summary))


(deftest
 t87_l564
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 2 (count (:layers m)))
     (contains? (:mapping (second (:layers m))) :color)
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v86_l558)))


(def
 v89_l573
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t90_l578
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v89_l573)))


(def
 v91_l582
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  (sk/lay-smooth {:stat :linear-model})
  sk-summary))


(deftest
 t92_l588
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 2 (count (:layers m)))
     (contains? (:mapping (first (:layers m))) :color)
     (nil? (get-in m [:layers 0 :mapping :color]))
     (nil? (:color (:mapping (second (:layers m)))))))
   v91_l582)))


(def
 v94_l614
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:title "Iris Scatter"})))


(deftest
 t95_l618
 (is
  ((fn [v] (let [p (sk/plan v)] (= "Iris Scatter" (:title p))))
   v94_l614)))


(def
 v96_l621
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris Scatter"})
  sk-summary))


(deftest
 t97_l626
 (is
  ((fn
    [m]
    (and
     (= "Iris Scatter" (get-in m [:opts :title]))
     (= {} (:mapping m))))
   v96_l621)))


(def
 v99_l634
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t100_l640
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) p (sk/plan v) panel (first (:panels p))]
     (and
      (= 150 (:points s))
      (= :flip (:coord panel))
      (= :log (:type (:y-scale panel))))))
   v99_l634)))


(def
 v101_l647
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)
  sk-summary))


(deftest
 t102_l654
 (is
  ((fn
    [m]
    (and
     (= {:type :log} (get-in m [:opts :x-scale]))
     (= :flip (get-in m [:opts :coord]))))
   v101_l647)))


(def
 v104_l671
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t105_l675
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v104_l671)))


(def
 v106_l679
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})
  sk-summary))


(deftest
 t107_l684
 (is
  ((fn
    [m]
    (let
     [layers
      (:layers m)
      rule
      (some
       (fn*
        [p1__94400#]
        (when (= :rule-h (:layer-type p1__94400#)) p1__94400#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v106_l679)))


(def
 v109_l695
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t110_l699
 (is ((fn [v] (= 3 (count (:panels (sk/plan v))))) v109_l695)))


(def
 v111_l701
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t112_l706
 (is ((fn [m] (= :species (get-in m [:opts :facet-col]))) v111_l701)))


(def
 v113_l709
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)))


(deftest
 t114_l713
 (is ((fn [v] (= 9 (count (:panels (sk/plan v))))) v113_l709)))


(def
 v115_l715
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)
  sk-summary))


(deftest
 t116_l720
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:opts :facet-col]))
     (= :species (get-in m [:opts :facet-row]))))
   v115_l715)))


(def
 v118_l756
 (def
  assembly-sketch
  (->
   iris
   (sk/lay-point :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   (sk/lay-smooth {:stat :linear-model}))))


(def v119_l762 assembly-sketch)


(deftest
 t120_l764
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 150 (:points s))
      (= 2 (:lines s))
      (= 3 (count d))
      (= [:point :line :line] (mapv :mark d)))))
   v119_l762)))


(def v121_l772 (sk-summary assembly-sketch))


(deftest
 t122_l774
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (nil? (:layers (second (:views m))))))
   v121_l772)))


(def
 v124_l786
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t125_l789
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (first (sk/draft v))]
     (and
      (= 150 (:points s))
      (= :sepal-length (:x d))
      (= :sepal-width (:y d))
      (= :species (:color d))
      (= :point (:mark d)))))
   v124_l786)))


(def
 v126_l797
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)
  sk-summary))


(deftest
 t127_l801
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))))
   v126_l797)))


(def
 v129_l821
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t130_l825
 (is
  ((fn [v] (let [p (sk/plan v)] (= 2 (count (:panels p))))) v129_l821)))


(def
 v131_l828
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t132_l833
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v131_l828)))


(def
 v134_l842
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t135_l846
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 1 (count (:panels p)))
      (= 2 (count (:layers (first (:panels p))))))))
   v134_l842)))


(def
 v136_l850
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})
  sk-summary))


(deftest
 t137_l855
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 2 (count (:layers (first (:views m)))))))
   v136_l850)))


(def
 v139_l867
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t140_l871
 (is
  ((fn [v] (let [p (sk/plan v)] (= 3 (count (:panels p))))) v139_l867)))


(def
 v141_l874
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t142_l879
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :species (get-in m [:opts :facet-col]))))
   v141_l874)))


(def
 v144_l891
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t145_l895
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and (= 2 (count (:panels p))) (= 1 (get-in p [:grid :cols])))))
   v144_l891)))


(def
 v146_l899
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)
  sk-summary))


(deftest
 t147_l904
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :sepal-length (get-in m [:views 1 :mapping :x]))))
   v146_l899)))


(def
 v149_l917
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v150_l919
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t151_l922
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 9 (count (:panels p)))
      (= 3 (get-in p [:grid :rows]))
      (= 3 (get-in p [:grid :cols])))))
   v150_l919)))


(def
 v152_l927
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))
  sk-summary))


(deftest
 t153_l931
 (is
  ((fn
    [m]
    (and (= 9 (count (:views m))) (= :species (:color (:mapping m)))))
   v152_l927)))
