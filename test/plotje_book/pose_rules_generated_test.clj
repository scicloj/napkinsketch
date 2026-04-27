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
    (partial mapv (fn* [p1__77138#] (dissoc p1__77138# :data))))
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
 v40_l247
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/pose :petal-length :petal-width)))


(deftest
 t41_l252
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (= 2 (count (:poses pose)))
     (= [] (:layers (first (:poses pose))))
     (= [] (:layers (second (:poses pose))))))
   v40_l247)))


(def
 v43_l265
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t44_l270
 (is
  ((fn
    [pose]
    (and
     (or (not (contains? pose :layers)) (= [] (:layers pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= [] (:layers (second (:poses pose))))))
   v43_l265)))


(def
 v46_l286
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose :sepal-length :petal-length)))


(deftest
 t47_l291
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
   v46_l286)))


(def
 v49_l302
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})
  pose-summary))


(deftest
 t50_l308
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))))
   v49_l302)))


(def
 v52_l321
 (let
  [pose (-> iris (pj/pose :sepal-length :sepal-width))]
  (= pose (pj/pose pose))))


(deftest t53_l324 (is (true? v52_l321)))


(def
 v54_l326
 (let
  [pose
   (->
    iris
    (pj/pose :sepal-length :sepal-width)
    (pj/pose :petal-length :petal-width))]
  (= pose (pj/pose pose))))


(deftest t55_l331 (is (true? v54_l326)))


(def
 v57_l340
 (pj/arrange
  [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
   (-> iris (pj/pose :petal-length :petal-width) pj/lay-point)]))


(deftest
 t58_l344
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= :vertical (get-in pose [:layout :direction]))
     (= 1 (count (:poses pose)))
     (= 2 (count (:poses (first (:poses pose)))))))
   v57_l340)))


(def
 v60_l356
 (pj/arrange
  [(pj/pose iris :sepal-length :sepal-width)
   (pj/pose iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t61_l362
 (is
  ((fn
    [pose]
    (and
     (= "Arranged" (get-in pose [:opts :title]))
     (= #{:y} (get-in pose [:opts :share-scales]))))
   v60_l356)))


(def
 v63_l390
 (-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point))


(deftest
 t64_l394
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (empty? (or (:mapping (first (:layers pose))) {}))))
   v63_l390)))


(def
 v66_l403
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point
  pose-summary))


(deftest
 t67_l409
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))))
   v66_l403)))


(def
 v69_l421
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


(deftest t70_l431 (is ((fn [counts] (= [0 1] counts)) v69_l421)))


(def
 v72_l442
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t73_l447
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= 0 (count (:layers (second (:poses pose)))))
     (= :point (:layer-type (first (:layers (first (:poses pose))))))))
   v72_l442)))


(def
 v75_l458
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t76_l462
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v75_l458)))


(def
 v78_l477
 (try
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point :petal-length :petal-width))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t79_l484
 (is
  ((fn
    [msg]
    (and
     (string? msg)
     (re-find #"conflict with the pose's existing position" msg)))
   v78_l477)))


(def
 v81_l498
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :petal-length)))


(deftest
 t82_l503
 (is
  ((fn
    [pose]
    (and
     (= 3 (count (:poses pose)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:poses pose) 2)))
     (= 1 (count (:layers (nth (:poses pose) 2))))))
   v81_l498)))


(def v84_l519 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v85_l523 (-> tiny (pj/lay-point :a :b)))


(deftest
 t86_l526
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v85_l523)))


(def v88_l530 (-> tiny (pj/pose :a :b) pj/lay-point pose-summary))


(deftest
 t89_l535
 (is
  ((fn
    [pose]
    (and
     (= {:x :a, :y :b} (:mapping pose))
     (= 1 (count (:layers pose)))
     (not (contains? pose :poses))))
   v88_l530)))


(def
 v91_l560
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  pj/lay-point))


(deftest
 t92_l563
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v91_l560)))


(def
 v94_l568
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} pj/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t95_l574
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v94_l568)))


(def
 v97_l584
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t98_l588
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v97_l584)))


(def
 v100_l596
 (-> iris (pj/pose "sepal-length" "sepal-width") pose-summary))


(deftest
 t101_l600
 (is
  ((fn
    [pose]
    (= {:x "sepal-length", :y "sepal-width"} (:mapping pose)))
   v100_l596)))


(def
 v103_l621
 (def
  s1-composite
  (pj/prepare-pose
   {:data iris,
    :mapping {:color :species},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v104_l630 s1-composite)


(deftest
 t105_l632
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
   v104_l630)))


(def
 v107_l644
 (def
  s1-siblings
  (pj/prepare-pose
   {:data iris,
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v108_l652 s1-siblings)


(deftest
 t109_l654
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
   v108_l652)))


(def
 v111_l672
 (def
  s2-tree
  (pj/prepare-pose
   {:data iris,
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :a, :y :b},
      :data (tc/dataset {:a [1 2 3], :b [3 5 4]}),
      :layers [{:layer-type :point}]}]})))


(def v112_l681 s2-tree)


(deftest
 t113_l683
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
   v112_l681)))


(def
 v115_l700
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t116_l705
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v115_l700)))


(def
 v118_l719
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t119_l724
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v118_l719)))


(def
 v121_l745
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})))


(deftest
 t122_l750
 (is ((fn [pose] (= "Iris" (get-in pose [:opts :title]))) v121_l745)))


(def
 v124_l755
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "One"})
  (pj/options {:title "Two", :subtitle "Sub"})))


(deftest
 t125_l761
 (is
  ((fn
    [pose]
    (and
     (= "Two" (get-in pose [:opts :title]))
     (= "Sub" (get-in pose [:opts :subtitle]))))
   v124_l755)))


(def
 v127_l773
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/scale :x :log)
  (pj/coord :flip)))


(deftest
 t128_l779
 (is
  ((fn
    [pose]
    (and
     (= {:type :log} (get-in pose [:opts :x-scale]))
     (= :flip (get-in pose [:opts :coord]))))
   v127_l773)))


(def
 v130_l791
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t131_l796
 (is
  ((fn [pose] (= :species (get-in pose [:opts :facet-col])))
   v130_l791)))


(def
 v133_l801
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet-grid :species :species)))


(deftest
 t134_l806
 (is
  ((fn
    [pose]
    (and
     (= :species (get-in pose [:opts :facet-col]))
     (= :species (get-in pose [:opts :facet-row]))))
   v133_l801)))


(def
 v136_l820
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t137_l825
 (is
  ((fn
    [pose]
    (let
     [layers
      (:layers pose)
      rule
      (some
       (fn*
        [p1__77139#]
        (when (= :rule-h (:layer-type p1__77139#)) p1__77139#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v136_l820)))


(def
 v139_l835
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t140_l840
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
   v139_l835)))


(def
 v142_l861
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t143_l868
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
   v142_l861)))


(def
 v145_l885
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  pj/draft))


(deftest
 t146_l890
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
   v145_l885)))


(def
 v148_l914
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  pj/plan))


(deftest
 t149_l920
 (is
  ((fn [plan] (and (:composite? plan) (= 2 (count (:sub-plots plan)))))
   v148_l914)))


(def
 v151_l932
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t152_l937
 (is
  ((fn
    [pose]
    (let
     [plan (pj/plan pose) panel (first (:panels plan))]
     (and (= 1 (count (:panels plan))) (= 2 (count (:layers panel))))))
   v151_l932)))


(def
 v154_l950
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t155_l955
 (is ((fn [pose] (= 3 (count (:panels (pj/plan pose))))) v154_l950)))


(def
 v157_l973
 (def
  l4-shared
  (pj/arrange
   [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
    (-> iris (pj/pose :sepal-length :petal-width) pj/lay-point)]
   {:share-scales #{:x}})))


(def v158_l979 l4-shared)


(deftest
 t159_l981
 (is
  ((fn
    [pose]
    (let
     [sub-plots
      (:sub-plots (pj/plan pose))
      domains
      (mapv
       (fn*
        [p1__77140#]
        (get-in p1__77140# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v158_l979)))


(def
 v161_l1012
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose
   (pj/cross
    [:sepal-length :sepal-width]
    [:petal-length :petal-width]))))


(deftest
 t162_l1017
 (is
  ((fn
    [pose]
    (and
     (= :vertical (get-in pose [:layout :direction]))
     (= #{:y :x} (get-in pose [:opts :share-scales]))
     (= 2 (count (:poses pose)))
     (every?
      (fn* [p1__77141#] (= 2 (count (:poses p1__77141#))))
      (:poses pose))
     (= {:color :species} (:mapping pose))))
   v161_l1012)))


(def v164_l1037 (pj/cross [:a :b] [:c :d]))


(deftest
 t165_l1039
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v164_l1037)))
