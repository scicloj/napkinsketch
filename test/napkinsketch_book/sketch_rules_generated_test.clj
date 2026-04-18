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
    (partial mapv (fn* [p1__2900181#] (dissoc p1__2900181# :data))))
   kind/pprint)))


(def
 v7_l73
 (-> iris (sk/lay-point :sepal-length :sepal-width) sk-summary))


(deftest
 t8_l77
 (is
  ((fn
    [m]
    (and
     (= 0 (count (:layers m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v7_l73)))


(def v9_l83 (-> iris (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t10_l86
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v9_l83)))


(def
 v12_l95
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk/lay-lm
  sk-summary))


(deftest
 t13_l101
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:layers m)))
     (= :point (:method (first (:layers m))))
     (= :lm (:method (second (:layers m))))
     (nil? (:layers (first (:views m))))))
   v12_l95)))


(def
 v14_l107
 (-> iris (sk/view :sepal-length :sepal-width) sk/lay-point sk/lay-lm))


(deftest
 t15_l112
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
   v14_l107)))


(def
 v17_l135
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t18_l140
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :lm (:method (first (:layers m))))
     (= 1 (count (:layers (first (:views m)))))
     (= :point (:method (first (:layers (first (:views m))))))))
   v17_l135)))


(def
 v19_l146
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t20_l150
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
   v19_l146)))


(def
 v22_l170
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm
  sk-summary))


(deftest
 t23_l176
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= :lm (:method (first (:layers m))))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (= 1 (count (:layers (second (:views m)))))))
   v22_l170)))


(def
 v24_l183
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)
  sk/lay-lm))


(deftest
 t25_l188
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
   v24_l183)))


(def
 v27_l210
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  sk-summary))


(deftest
 t28_l216
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v27_l210)))


(def
 v29_l220
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  sk/lay-point))


(deftest
 t30_l225
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v29_l220)))


(def
 v32_l239
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary))


(deftest
 t33_l245
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
   v32_l239)))


(def
 v34_l253
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/view :sepal-length :sepal-width)
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t35_l258
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
   v34_l253)))


(def
 v37_l277
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t38_l282
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= 0 (count (:layers m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :petal-length (get-in m [:views 1 :mapping :x]))))
   v37_l277)))


(def
 v39_l288
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t40_l292
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 150 (:points s)) (pos? (:polygons s)))))
   v39_l288)))


(def v42_l307 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point sk-summary))


(deftest
 t43_l311
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= 1 (count (:layers (first (:views m)))))))
   v42_l307)))


(def v44_l317 (-> {:x [1 2 3], :y [4 5 6]} sk/lay-point))


(deftest
 t45_l320
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v44_l317)))


(def
 v47_l324
 (->
  {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]}
  sk/lay-point
  sk-summary))


(deftest
 t48_l328
 (is
  ((fn
    [m]
    (and
     (= :x (get-in m [:views 0 :mapping :x]))
     (= :y (get-in m [:views 0 :mapping :y]))
     (= :c (get-in m [:views 0 :mapping :color]))))
   v47_l324)))


(def
 v49_l333
 (-> {:x [1 2 3], :y [4 5 6], :c ["a" "b" "a"]} sk/lay-point))


(deftest
 t50_l336
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v49_l333)))


(def
 v52_l356
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t53_l362
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v52_l356)))


(def
 v54_l366
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t55_l371
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v54_l366)))


(def v57_l379 (def tiny {:x [1 2 3], :y [3 5 4]}))


(def
 v58_l381
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point
  sk-summary))


(deftest
 t59_l387
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 1 (count (:layers m)))))
   v58_l381)))


(def
 v60_l391
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/view :x :y {:data tiny})
  sk/lay-point))


(deftest
 t61_l396
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
   v60_l391)))


(def v63_l407 (def iris-small (tc/head iris 10)))


(def
 v64_l409
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  sk/lay-lm
  sk-summary))


(deftest
 t65_l415
 (is
  ((fn [m] (and (= 1 (count (:views m))) (= 2 (count (:layers m)))))
   v64_l409)))


(def
 v66_l419
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  (sk/lay-point {:data iris-small})
  sk/lay-lm))


(deftest
 t67_l424
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
   v66_l419)))


(def
 v69_l445
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t70_l451
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers m)))))
   v69_l445)))


(def
 v71_l456
 (->
  (sk/sketch iris {:color :species})
  (sk/view :sepal-length :sepal-width)
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t72_l461
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) d (sk/draft v)]
     (and
      (= 2 (:panels s))
      (= 300 (:points s))
      (every?
       (fn* [p1__2900182#] (= :species (:color p1__2900182#)))
       d))))
   v71_l456)))


(def
 v74_l475
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point
  sk-summary))


(deftest
 t75_l481
 (is
  ((fn
    [m]
    (and
     (= {} (:mapping m))
     (= :species (get-in m [:views 0 :mapping :color]))
     (nil? (get-in m [:views 1 :mapping :color]))))
   v74_l475)))


(def
 v76_l486
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  (sk/view :petal-length :petal-width)
  sk/lay-point))


(deftest
 t77_l491
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
   v76_l486)))


(def
 v79_l506
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm
  sk-summary))


(deftest
 t80_l511
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     (= 1 (count (:layers m)))
     (= {} (:mapping (first (:layers m))))))
   v79_l506)))


(def
 v81_l516
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  sk/lay-lm))


(deftest
 t82_l520
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
   v81_l516)))


(def
 v84_l538
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})
  sk-summary))


(deftest
 t85_l544
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:views 0 :mapping :color]))
     (= 2 (count (:layers m)))
     (contains? (:mapping (second (:layers m))) :color)
     (nil? (get-in m [:layers 1 :mapping :color]))))
   v84_l538)))


(def
 v86_l550
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-lm {:color nil})))


(deftest
 t87_l555
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
  (sk/lay-point :sepal-length :sepal-width)
  (sk/options {:title "Iris Scatter"})
  sk-summary))


(deftest
 t93_l598
 (is
  ((fn
    [m]
    (and
     (= "Iris Scatter" (get-in m [:opts :title]))
     (= {} (:mapping m))))
   v92_l593)))


(def
 v94_l602
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/options {:title "Iris Scatter"})))


(deftest
 t95_l606
 (is
  ((fn [v] (let [p (sk/plan v)] (= "Iris Scatter" (:title p))))
   v94_l602)))


(def
 v97_l613
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)
  sk-summary))


(deftest
 t98_l620
 (is
  ((fn
    [m]
    (and
     (= {:type :log} (get-in m [:opts :x-scale]))
     (= :flip (get-in m [:opts :coord]))))
   v97_l613)))


(def
 v99_l624
 (->
  iris
  (sk/view :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t100_l630
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v) p (sk/plan v) panel (first (:panels p))]
     (and
      (= 150 (:points s))
      (= :flip (:coord panel))
      (= :log (:type (:y-scale panel))))))
   v99_l624)))


(def
 v102_l650
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))
  sk-summary))


(deftest
 t103_l655
 (is
  ((fn
    [m]
    (and
     (= 1 (count (get-in m [:opts :annotations])))
     (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))
   v102_l650)))


(def
 v104_l659
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/annotate (sk/rule-h 3.0))))


(deftest
 t105_l663
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v104_l659)))


(def
 v107_l672
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t108_l677
 (is ((fn [m] (= :species (get-in m [:opts :facet-col]))) v107_l672)))


(def
 v109_l680
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t110_l684
 (is ((fn [v] (= 3 (count (:panels (sk/plan v))))) v109_l680)))


(def
 v111_l686
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)
  sk-summary))


(deftest
 t112_l691
 (is
  ((fn
    [m]
    (and
     (= :species (get-in m [:opts :facet-col]))
     (= :species (get-in m [:opts :facet-row]))))
   v111_l686)))


(def
 v113_l695
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet-grid :species :species)))


(deftest
 t114_l699
 (is ((fn [v] (= 9 (count (:panels (sk/plan v))))) v113_l695)))


(def
 v116_l733
 (def
  assembly-sketch
  (->
   iris
   (sk/lay-point :sepal-length :sepal-width)
   (sk/view :petal-length :petal-width)
   sk/lay-lm)))


(def v117_l739 (sk-summary assembly-sketch))


(deftest
 t118_l741
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:layers m)))
     (= 2 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))
     (nil? (:layers (second (:views m))))))
   v117_l739)))


(def v119_l747 assembly-sketch)


(deftest
 t120_l749
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
   v119_l747)))


(def
 v122_l763
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)
  sk-summary))


(deftest
 t123_l767
 (is
  ((fn
    [m]
    (and
     (= :species (:color (:mapping m)))
     (= 1 (count (:views m)))
     (= 1 (count (:layers (first (:views m)))))))
   v122_l763)))


(def
 v124_l772
 (->
  (sk/sketch iris {:color :species})
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t125_l775
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
   v124_l772)))


(def
 v127_l798
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)
  sk-summary))


(deftest
 t128_l803
 (is
  ((fn [m] (and (= 2 (count (:views m))) (= 0 (count (:layers m)))))
   v127_l798)))


(def
 v129_l807
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :petal-length)))


(deftest
 t130_l811
 (is
  ((fn [v] (let [p (sk/plan v)] (= 2 (count (:panels p))))) v129_l807)))


(def
 v132_l819
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)
  sk-summary))


(deftest
 t133_l824
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= 2 (count (:layers (first (:views m)))))))
   v132_l819)))


(def
 v134_l828
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/lay-lm :sepal-length :sepal-width)))


(deftest
 t135_l832
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 1 (count (:panels p)))
      (= 2 (count (:layers (first (:panels p))))))))
   v134_l828)))


(def
 v137_l844
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)
  sk-summary))


(deftest
 t138_l849
 (is
  ((fn
    [m]
    (and
     (= 1 (count (:views m)))
     (= :species (get-in m [:opts :facet-col]))))
   v137_l844)))


(def
 v139_l853
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/facet :species)))


(deftest
 t140_l857
 (is
  ((fn [v] (let [p (sk/plan v)] (= 3 (count (:panels p))))) v139_l853)))


(def
 v142_l868
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)
  sk-summary))


(deftest
 t143_l873
 (is
  ((fn
    [m]
    (and
     (= 2 (count (:views m)))
     (= :sepal-length (get-in m [:views 0 :mapping :x]))
     (= :sepal-length (get-in m [:views 1 :mapping :x]))))
   v142_l868)))


(def
 v144_l878
 (->
  iris
  (sk/lay-point :sepal-length :sepal-width)
  (sk/lay-histogram :sepal-length)))


(deftest
 t145_l882
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and (= 2 (count (:panels p))) (= 1 (get-in p [:grid :cols])))))
   v144_l878)))


(def
 v147_l894
 (def splom-cols [:sepal-length :sepal-width :petal-length]))


(def
 v148_l896
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))
  sk-summary))


(deftest
 t149_l900
 (is
  ((fn
    [m]
    (and (= 9 (count (:views m))) (= :species (:color (:mapping m)))))
   v148_l896)))


(def
 v150_l904
 (->
  (sk/sketch iris {:color :species})
  (sk/view (sk/cross splom-cols splom-cols))))


(deftest
 t151_l907
 (is
  ((fn
    [v]
    (let
     [p (sk/plan v)]
     (and
      (= 9 (count (:panels p)))
      (= 3 (get-in p [:grid :rows]))
      (= 3 (get-in p [:grid :cols])))))
   v150_l904)))
