(ns
 napkinsketch-book.sketch-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l47 (def iris (rdatasets/datasets-iris)))


(def
 v5_l52
 (defn
  sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (->
   (select-keys sk [:mapping :views :layers :opts])
   (update
    :views
    (partial mapv (fn* [p1__81523#] (dissoc p1__81523# :data))))
   kind/pprint)))


(def v7_l73 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t8_l76
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v7_l73)))


(def
 v9_l78
 (-> iris (sk/lay-point :sepal-length :sepal-width) sk-summary))


(deftest
 t10_l82
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v9_l78)))


(def
 v12_l95
 (-> iris (sk/view :sepal-length :sepal-width) sk/lay-point sk/lay-lm))


(deftest
 t13_l100
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
      (= :lm (:stat (second d))))))
   v12_l95)))


(def
 v14_l114
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary))


(deftest
 t15_l120
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (= :lm (:method (second (:layers m))))
     (nil? (:layers (first (:views m))))))
   v14_l114)))


(def
 v17_l135
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t18_l139
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
   v17_l135)))


(def
 v19_l149
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t20_l154
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :lm (:method (first (:layers m))))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v19_l149)))


(def
 v22_l170
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t23_l175
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
   v22_l170)))


(def
 v24_l182
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm
  sk-summary))


(deftest
 t25_l188
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :lm (:method (first (:layers m))))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= 1 (count (:layers (second (:views m)))))))
   v24_l182)))


(def
 v27_l210
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t28_l215
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v27_l210)))


(def
 v29_l219
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk-summary))


(deftest
 t30_l225
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v29_l219)))


(def
 v32_l239
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t33_l244
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
   v32_l239)))


(def
 v34_l253
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary))


(deftest
 t35_l259
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))
     (= 1 (count (:layers (second (:views m)))))
     (= :lm (:method (first (:layers (second (:views m))))))))
   v34_l253)))


(def
 v37_l277
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t38_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v37_l277)))


(def
 v39_l286
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t40_l291
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v39_l286)))


(def v42_l307 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t43_l310
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v42_l307)))


(def v44_l312 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary))


(deftest
 t45_l316
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= 1 (count (:layers (first (:views m)))))))
   v44_l312)))


(def
 v47_l324
 (-> {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]} sk/lay-point))


(deftest
 t48_l327
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v47_l324)))


(def
 v49_l329
 (->
  {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]}
  sk/lay-point
  sk-summary))


(deftest
 t50_l333
 (is
  ((fn
    [m]
    (and
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= :c (get-in m [:views 0 :mapping :color]))))
   v49_l329)))


(def
 v52_l356
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t53_l361
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v52_l356)))


(def
 v54_l365
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t55_l371
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v54_l365)))


(def v57_l379 (def tiny {:x [1 2 3], :y [3 5 4]}))


(def
 v58_l381
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point))


(deftest
 t59_l386
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
   v58_l381)))


(def
 v60_l393
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point
  sk-summary))


(deftest
 t61_l399
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v60_l393)))


(def v63_l407 (def iris-small (tc/head iris 10)))


(def
 v64_l409
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  sk/lay-lm))


(deftest
 t65_l414
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
   v64_l409)))


(def
 v66_l421
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  sk/lay-lm
  sk-summary))


(deftest
 t67_l427
 (is
  ((fn [m] (and (= 1 (count (:views m))) (= 2 (count (:layers m)))))
   v66_l421)))


(def
 v69_l445
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t70_l450
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 300 (:points s))
      (every? (fn* [p1__81524#] (= :species (:color p1__81524#))) d))))
   v69_l445)))


(def
 v71_l456
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t72_l462
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v71_l456)))


(def
 v74_l475
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t75_l480
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
   v74_l475)))


(def
 v76_l487
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t77_l493
 (is
  ((fn
    [m]
    (and
     (= {} (:mapping m))
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:views 1 :mapping :color]))))
   v76_l487)))


(def
 v79_l506
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t80_l510
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
   v79_l506)))


(def
 v81_l517
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t82_l522
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= 1 (count (:layers m)))
     (= {} (:mapping (first (:layers m))))))
   v81_l517)))


(def
 v84_l538
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t85_l543
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
   v84_l538)))


(def
 v86_l550
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary))


(deftest
 t87_l556
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 2 (count (:layers m)))
     (contains? (:mapping (second (:layers m))) :color)
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v86_l550)))


(def
 v89_l565
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:color nil})
  sk/lay-lm))


(deftest
 t90_l570
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v89_l565)))


(def
 v92_l593
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:title "Iris Scatter"})))


(deftest
 t93_l597
 (is
  ((fn [v] (let [p (sk/plan v)] (= "Iris Scatter" (:title p))))
   v92_l593)))


(def
 v94_l600
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris Scatter"})
  sk-summary))


(deftest
 t95_l605
 (is
  ((fn
    [m]
    (and
     (= "Iris Scatter" (get-in m [:opts :title]))
     (= {} (:mapping m))))
   v94_l600)))


(def
 v97_l613
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t98_l619
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) p (sk/plan v) panel (first (:panels p))]
     (and
      (= 150 (:points s))
      (= :flip (:coord panel))
      (= :log (:type (:y-scale panel))))))
   v97_l613)))


(def
 v99_l626
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)
  sk-summary))


(deftest
 t100_l633
 (is
  ((fn
    [m]
    (and
     (= {:type :log} (get-in m [:opts :x-scale]))
     (= :flip (get-in m [:opts :coord]))))
   v99_l626)))


(def
 v102_l650
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t103_l654
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v102_l650)))


(def
 v104_l658
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary))


(deftest
 t105_l663
 (is
  ((fn
    [m]
    (and
     (= 1 (count (get-in m [:opts :annotations])))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v104_l658)))


(def
 v107_l672
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t108_l676
 (is ((fn [v] (= 3 (count (:panels (sk/plan v))))) v107_l672)))


(def
 v109_l678
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t110_l683
 (is ((fn [m] (= :species (get-in m [:opts :facet-col]))) v109_l678)))


(def
 v111_l686
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)))


(deftest
 t112_l690
 (is ((fn [v] (= 9 (count (:panels (sk/plan v))))) v111_l686)))


(def
 v113_l692
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)
  sk-summary))


(deftest
 t114_l697
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:opts :facet-col]))
     (= :species (get-in m [:opts :facet-row]))))
   v113_l692)))


(def
 v116_l733
 (def
  assembly-sketch
  (->
   iris
   (sk/lay-point :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-lm)))


(def v117_l739 assembly-sketch)


(deftest
 t118_l741
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
   v117_l739)))


(def v119_l749 (sk-summary assembly-sketch))


(deftest
 t120_l751
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (nil? (:layers (second (:views m))))))
   v119_l749)))


(def
 v122_l763
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t123_l766
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
   v122_l763)))


(def
 v124_l774
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)
  sk-summary))


(deftest
 t125_l778
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))))
   v124_l774)))


(def
 v127_l798
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t128_l802
 (is
  ((fn [v] (let [p (sk/plan v)] (= 2 (count (:panels p))))) v127_l798)))


(def
 v129_l805
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t130_l810
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v129_l805)))


(def
 v132_l819
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t133_l823
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 1 (count (:panels p)))
      (= 2 (count (:layers (first (:panels p))))))))
   v132_l819)))


(def
 v134_l827
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary))


(deftest
 t135_l832
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 2 (count (:layers (first (:views m)))))))
   v134_l827)))


(def
 v137_l844
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t138_l848
 (is
  ((fn [v] (let [p (sk/plan v)] (= 3 (count (:panels p))))) v137_l844)))


(def
 v139_l851
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t140_l856
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :species (get-in m [:opts :facet-col]))))
   v139_l851)))


(def
 v142_l868
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t143_l872
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and (= 2 (count (:panels p))) (= 1 (get-in p [:grid :cols])))))
   v142_l868)))


(def
 v144_l876
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)
  sk-summary))


(deftest
 t145_l881
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :sepal-length (get-in m [:views 1 :mapping :x]))))
   v144_l876)))


(def
 v147_l894
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v148_l896
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t149_l899
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 9 (count (:panels p)))
      (= 3 (get-in p [:grid :rows]))
      (= 3 (get-in p [:grid :cols])))))
   v148_l896)))


(def
 v150_l904
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))
  sk-summary))


(deftest
 t151_l908
 (is
  ((fn
    [m]
    (and (= 9 (count (:views m))) (= :species (:color (:mapping m)))))
   v150_l904)))
