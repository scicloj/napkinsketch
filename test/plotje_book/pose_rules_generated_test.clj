(ns
 plotje-book.pose-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (def iris (rdatasets/datasets-iris)))


(def
 v5_l35
 (defn
  strip-data
  [pose]
  (cond->
   (dissoc pose :data)
   (:layers pose)
   (update
    :layers
    (partial mapv (fn* [p1__87875#] (dissoc p1__87875# :data))))
   (:poses pose)
   (update :poses (partial mapv strip-data)))))


(def
 v6_l40
 (defn
  pose-summary
  "Print pose structure without :data (for readability)."
  [pose]
  (kind/pprint (strip-data pose))))


(def v8_l81 (-> iris (pj/pose :sepal-length :sepal-width)))


(deftest
 t9_l84
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v8_l81)))


(def
 v10_l86
 (-> iris (pj/pose :sepal-length :sepal-width) pose-summary))


(deftest
 t11_l90
 (is
  ((fn
    [pose]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping pose))
     (= [] (:layers pose))
     (not (contains? pose :poses))))
   v10_l86)))


(def v13_l100 (-> iris (pj/pose {:color :species}) pose-summary))


(deftest
 t14_l104
 (is
  ((fn
    [pose]
    (and
     (= {:color :species} (:mapping pose))
     (not (contains? pose :poses))))
   v13_l100)))


(def v16_l116 (-> iris pj/pose (pj/pose :sepal-length :sepal-width)))


(deftest
 t17_l120
 (is
  ((fn
    [pose]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping pose))
     (not (contains? pose :poses))))
   v16_l116)))


(def
 v19_l127
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t20_l131
 (is
  ((fn
    [pose]
    (=
     {:x :sepal-length, :y :sepal-width, :color :species}
     (:mapping pose)))
   v19_l127)))


(def
 v22_l139
 (->
  iris
  pj/pose
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t23_l144
 (is
  ((fn
    [pose]
    (=
     pose
     (pj/pose iris :sepal-length :sepal-width {:color :species})))
   v22_l139)))


(def
 v25_l157
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t26_l161
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:poses pose))))))
   v25_l157)))


(def
 v28_l173
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/pose :petal-length :petal-width)
  pose-summary))


(deftest
 t29_l178
 (is
  ((fn
    [pose]
    (and
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:poses pose))))))
   v28_l173)))


(def
 v31_l190
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/options {:title "Iris"})
  (pj/pose :petal-length :petal-width)))


(deftest
 t32_l195
 (is
  ((fn
    [pose]
    (and
     (= "Iris" (get-in pose [:opts :title]))
     (not (contains? (first (:poses pose)) :opts))))
   v31_l190)))


(def
 v34_l208
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})))


(deftest
 t35_l212
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:poses pose)))
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))))
   v34_l208)))


(def
 v37_l225
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})
  (pj/pose :petal-length :petal-width)))


(deftest
 t38_l230
 (is
  ((fn
    [pose]
    (=
     pose
     (->
      iris
      (pj/pose :sepal-length :sepal-width {:color :species})
      (pj/pose :petal-length :petal-width))))
   v37_l225)))


(def
 v40_l246
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/pose :petal-length :petal-width)))


(deftest
 t41_l251
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (= 2 (count (:poses pose)))
     (= [] (:layers (first (:poses pose))))
     (= [] (:layers (second (:poses pose))))))
   v40_l246)))


(def
 v43_l264
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t44_l269
 (is
  ((fn
    [pose]
    (and
     (or (not (contains? pose :layers)) (= [] (:layers pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= [] (:layers (second (:poses pose))))))
   v43_l264)))


(def
 v46_l285
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose :sepal-length :petal-length)))


(deftest
 t47_l290
 (is
  ((fn
    [pose]
    (and
     (= 3 (count (:poses pose)))
     (=
      [{:x :sepal-length, :y :sepal-width}
       {:x :petal-length, :y :petal-width}
       {:x :sepal-length, :y :petal-length}]
      (mapv :mapping (:poses pose)))))
   v46_l285)))


(def
 v49_l301
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})
  pose-summary))


(deftest
 t50_l307
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))))
   v49_l301)))


(def
 v52_l320
 (let
  [pose (-> iris (pj/pose :sepal-length :sepal-width))]
  (= pose (pj/pose pose))))


(deftest t53_l323 (is (true? v52_l320)))


(def
 v54_l325
 (let
  [pose
   (->
    iris
    (pj/pose :sepal-length :sepal-width)
    (pj/pose :petal-length :petal-width))]
  (= pose (pj/pose pose))))


(deftest t55_l330 (is (true? v54_l325)))


(def
 v57_l339
 (pj/arrange
  [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
   (-> iris (pj/pose :petal-length :petal-width) pj/lay-point)]))


(deftest
 t58_l343
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= :vertical (get-in pose [:layout :direction]))
     (= 1 (count (:poses pose)))
     (= 2 (count (:poses (first (:poses pose)))))))
   v57_l339)))


(def
 v60_l355
 (pj/arrange
  [(pj/pose iris :sepal-length :sepal-width)
   (pj/pose iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t61_l361
 (is
  ((fn
    [pose]
    (and
     (= "Arranged" (get-in pose [:opts :title]))
     (= #{:y} (get-in pose [:opts :share-scales]))))
   v60_l355)))


(def
 v63_l377
 (->
  iris
  (pj/pose
   (pj/cross [:sepal-length :sepal-width] [:petal-length :petal-width])
   {:color :species})))


(deftest
 t64_l382
 (is
  ((fn
    [pose]
    (and
     (= {:color :species} (:mapping pose))
     (= 2 (count (:poses pose)))
     (every?
      (fn* [p1__87876#] (= 2 (count (:poses p1__87876#))))
      (:poses pose))))
   v63_l377)))


(def
 v66_l391
 (let
  [a
   (->
    iris
    (pj/pose {:color :species})
    (pj/pose
     (pj/cross
      [:sepal-length :sepal-width]
      [:petal-length :petal-width])))
   b
   (->
    iris
    (pj/pose
     (pj/cross
      [:sepal-length :sepal-width]
      [:petal-length :petal-width])
     {:color :species}))]
  (= a b)))


(deftest t67_l401 (is (true? v66_l391)))


(def
 v69_l425
 (-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point))


(deftest
 t70_l429
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (empty? (or (:mapping (first (:layers pose))) {}))))
   v69_l425)))


(def
 v72_l438
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point
  pose-summary))


(deftest
 t73_l444
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))))
   v72_l438)))


(def
 v75_l456
 (let
  [before
   (pj/arrange
    [(pj/pose iris :sepal-length :sepal-width)
     (pj/pose iris :petal-length :petal-width)])
   after
   (->
    (pj/arrange
     [(pj/pose iris :sepal-length :sepal-width)
      (pj/pose iris :petal-length :petal-width)])
    pj/lay-point)]
  [(count (or (:layers before) [])) (count (or (:layers after) []))]))


(deftest t76_l466 (is ((fn [counts] (= [0 1] counts)) v75_l456)))


(def
 v78_l477
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t79_l482
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= 0 (count (:layers (second (:poses pose)))))
     (= :point (:layer-type (first (:layers (first (:poses pose))))))))
   v78_l477)))


(def
 v81_l493
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t82_l497
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v81_l493)))


(def
 v84_l512
 (try
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point :petal-length :petal-width))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t85_l519
 (is
  ((fn
    [msg]
    (and
     (string? msg)
     (re-find #"conflict with the pose's existing position" msg)))
   v84_l512)))


(def
 v87_l533
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :petal-length)))


(deftest
 t88_l538
 (is
  ((fn
    [pose]
    (and
     (= 3 (count (:poses pose)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:poses pose) 2)))
     (= 1 (count (:layers (nth (:poses pose) 2))))))
   v87_l533)))


(def v90_l554 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v91_l558 (-> tiny (pj/lay-point :a :b)))


(deftest
 t92_l561
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v91_l558)))


(def v94_l565 (-> tiny (pj/pose :a :b) pj/lay-point pose-summary))


(deftest
 t95_l570
 (is
  ((fn
    [pose]
    (and
     (= {:x :a, :y :b} (:mapping pose))
     (= 1 (count (:layers pose)))
     (not (contains? pose :poses))))
   v94_l565)))


(def
 v97_l595
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  pj/lay-point))


(deftest
 t98_l598
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v97_l595)))


(def
 v100_l603
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} pj/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t101_l609
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v100_l603)))


(def
 v103_l619
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t104_l623
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v103_l619)))


(def
 v106_l631
 (-> iris (pj/pose "sepal-length" "sepal-width") pose-summary))


(deftest
 t107_l635
 (is
  ((fn
    [pose]
    (= {:x "sepal-length", :y "sepal-width"} (:mapping pose)))
   v106_l631)))


(def
 v109_l656
 (def
  s1-composite
  (pj/pose
   {:data iris,
    :mapping {:color :species},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v110_l665 s1-composite)


(deftest
 t111_l667
 (is
  ((fn
    [pose]
    (let
     [plan
      (pj/plan pose)
      panels
      (mapv (comp :panels :plan) (:sub-plots plan))]
     (every?
      (fn [pp] (= 3 (count (:groups (first (:layers (first pp)))))))
      panels)))
   v110_l665)))


(def
 v113_l679
 (def
  s1-siblings
  (pj/pose
   {:data iris,
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v114_l687 s1-siblings)


(deftest
 t115_l689
 (is
  ((fn
    [pose]
    (let
     [sub-plots
      (:sub-plots (pj/plan pose))
      panel-groups
      (mapv
       (fn
        [sp]
        (count
         (:groups (first (:layers (first (-> sp :plan :panels)))))))
       sub-plots)]
     (= [1 3] panel-groups)))
   v114_l687)))


(def
 v117_l707
 (def
  s2-tree
  (pj/pose
   {:data iris,
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :a, :y :b},
      :data (tc/dataset {:a [1 2 3], :b [3 5 4]}),
      :layers [{:layer-type :point}]}]})))


(def v118_l716 s2-tree)


(deftest
 t119_l718
 (is
  ((fn
    [pose]
    (let
     [sub-plots
      (:sub-plots (pj/plan pose))
      counts
      (mapv
       (fn
        [sp]
        (->
         sp
         :plan
         :panels
         first
         :layers
         first
         :groups
         first
         :xs
         count))
       sub-plots)]
     (= [150 3] counts)))
   v118_l716)))


(def
 v121_l735
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t122_l740
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v121_l735)))


(def
 v124_l754
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t125_l759
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v124_l754)))


(def
 v127_l780
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})))


(deftest
 t128_l785
 (is ((fn [pose] (= "Iris" (get-in pose [:opts :title]))) v127_l780)))


(def
 v130_l790
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "One"})
  (pj/options {:title "Two", :subtitle "Sub"})))


(deftest
 t131_l796
 (is
  ((fn
    [pose]
    (and
     (= "Two" (get-in pose [:opts :title]))
     (= "Sub" (get-in pose [:opts :subtitle]))))
   v130_l790)))


(def
 v133_l812
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/scale :x :log)
  (pj/coord :flip)))


(deftest
 t134_l818
 (is
  ((fn
    [pose]
    (and
     (= {:type :log} (get-in pose [:opts :x-scale]))
     (= :flip (get-in pose [:opts :coord]))))
   v133_l812)))


(def
 v136_l825
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:size :petal-length})
  pj/lay-point
  (pj/scale :size :log)))


(deftest
 t137_l830
 (is
  ((fn [pose] (= {:type :log} (get-in pose [:opts :size-scale])))
   v136_l825)))


(def
 v139_l841
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t140_l846
 (is
  ((fn [pose] (= :species (get-in pose [:opts :facet-col])))
   v139_l841)))


(def
 v142_l851
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet-grid :species :species)))


(deftest
 t143_l856
 (is
  ((fn
    [pose]
    (and
     (= :species (get-in pose [:opts :facet-col]))
     (= :species (get-in pose [:opts :facet-row]))))
   v142_l851)))


(def
 v145_l870
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t146_l875
 (is
  ((fn
    [pose]
    (let
     [layers
      (:layers pose)
      rule
      (some
       (fn*
        [p1__87877#]
        (when (= :rule-h (:layer-type p1__87877#)) p1__87877#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v145_l870)))


(def
 v148_l885
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t149_l890
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= 0 (count (:layers (second (:poses pose)))))
     (=
      :rule-h
      (:layer-type (first (:layers (first (:poses pose))))))))
   v148_l885)))


(def
 v151_l911
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t152_l918
 (is
  ((fn
    [pose]
    (let
     [plan
      (pj/plan pose)
      panel-layer-counts
      (mapv
       (fn [sp] (count (:layers (first (-> sp :plan :panels)))))
       (:sub-plots plan))]
     (= [2 1] panel-layer-counts)))
   v151_l911)))


(def
 v154_l935
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  pj/draft))


(deftest
 t155_l940
 (is
  ((fn
    [drafts]
    (and
     (= 1 (count drafts))
     (let
      [d (first drafts)]
      (and
       (= :sepal-length (:x d))
       (= :sepal-width (:y d))
       (= :species (:color d))
       (= :point (:mark d))
       (= 150 (tc/row-count (:data d)))))))
   v154_l935)))


(def
 v157_l964
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  pj/plan))


(deftest
 t158_l970
 (is
  ((fn [plan] (and (:composite? plan) (= 2 (count (:sub-plots plan)))))
   v157_l964)))


(def
 v160_l982
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t161_l987
 (is
  ((fn
    [pose]
    (let
     [plan (pj/plan pose) panel (first (:panels plan))]
     (and (= 1 (count (:panels plan))) (= 2 (count (:layers panel))))))
   v160_l982)))


(def
 v163_l1000
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t164_l1005
 (is ((fn [pose] (= 3 (count (:panels (pj/plan pose))))) v163_l1000)))


(def
 v166_l1023
 (def
  l4-shared
  (pj/arrange
   [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
    (-> iris (pj/pose :sepal-length :petal-width) pj/lay-point)]
   {:share-scales #{:x}})))


(def v167_l1029 l4-shared)


(deftest
 t168_l1031
 (is
  ((fn
    [pose]
    (let
     [sub-plots
      (:sub-plots (pj/plan pose))
      domains
      (mapv
       (fn*
        [p1__87878#]
        (get-in p1__87878# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v167_l1029)))


(def
 v170_l1062
 (->
  iris
  (pj/pose
   (pj/cross [:sepal-length :sepal-width] [:petal-length :petal-width])
   {:color :species})))


(deftest
 t171_l1067
 (is
  ((fn
    [pose]
    (and
     (= :vertical (get-in pose [:layout :direction]))
     (= #{:y :x} (get-in pose [:opts :share-scales]))
     (= 2 (count (:poses pose)))
     (every?
      (fn* [p1__87879#] (= 2 (count (:poses p1__87879#))))
      (:poses pose))
     (= {:color :species} (:mapping pose))))
   v170_l1062)))


(def v173_l1087 (pj/cross [:a :b] [:c :d]))


(deftest
 t174_l1089
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v173_l1087)))
