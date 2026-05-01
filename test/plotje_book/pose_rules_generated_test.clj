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
    (partial mapv (fn* [p1__102781#] (dissoc p1__102781# :data))))
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
 (=
  (->
   iris
   pj/pose
   (pj/pose {:color :species})
   (pj/pose :sepal-length :sepal-width))
  (pj/pose iris :sepal-length :sepal-width {:color :species})))


(deftest t23_l145 (is (true? v22_l139)))


(def
 v25_l156
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t26_l160
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
   v25_l156)))


(def
 v28_l172
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/pose :petal-length :petal-width)))


(def
 v29_l176
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/pose :petal-length :petal-width)
  pose-summary))


(deftest
 t30_l181
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
   v29_l176)))


(def
 v32_l193
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/options {:title "Iris"})
  (pj/pose :petal-length :petal-width)))


(def
 v34_l201
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/options {:title "Iris"})
  (pj/pose :petal-length :petal-width)
  pose-summary))


(deftest
 t35_l207
 (is
  ((fn
    [pose]
    (and
     (= "Iris" (get-in pose [:opts :title]))
     (not (contains? (first (:poses pose)) :opts))))
   v34_l201)))


(def
 v37_l220
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})))


(deftest
 t38_l224
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:poses pose)))
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))))
   v37_l220)))


(def
 v40_l237
 (=
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/pose {:color :species})
   (pj/pose :petal-length :petal-width))
  (->
   iris
   (pj/pose :sepal-length :sepal-width {:color :species})
   (pj/pose :petal-length :petal-width))))


(deftest t41_l245 (is (true? v40_l237)))


(def
 v43_l256
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/pose :petal-length :petal-width)))


(deftest
 t44_l261
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (= 2 (count (:poses pose)))
     (= [] (:layers (first (:poses pose))))
     (= [] (:layers (second (:poses pose))))))
   v43_l256)))


(def
 v46_l274
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t47_l279
 (is
  ((fn
    [pose]
    (and
     (or (not (contains? pose :layers)) (= [] (:layers pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= [] (:layers (second (:poses pose))))))
   v46_l274)))


(def
 v49_l295
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose :sepal-length :petal-length)))


(deftest
 t50_l300
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
   v49_l295)))


(def
 v52_l311
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})))


(def
 v53_l316
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})
  pose-summary))


(deftest
 t54_l322
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= {:color :species} (:mapping pose))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses pose))))))
   v53_l316)))


(def
 v56_l337
 (def leaf-pose (-> iris (pj/pose :sepal-length :sepal-width))))


(def v57_l339 leaf-pose)


(def v58_l341 (pose-summary leaf-pose))


(def v60_l345 (= leaf-pose (pj/pose leaf-pose)))


(deftest t61_l347 (is (true? v60_l345)))


(def
 v63_l351
 (def
  composite-pose
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/pose :petal-length :petal-width))))


(def v64_l356 composite-pose)


(def v65_l358 (pose-summary composite-pose))


(def v67_l362 (= composite-pose (pj/pose composite-pose)))


(deftest t68_l364 (is (true? v67_l362)))


(def
 v70_l373
 (pj/arrange
  [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
   (-> iris (pj/pose :petal-length :petal-width) pj/lay-point)]))


(deftest
 t71_l377
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= :vertical (get-in pose [:layout :direction]))
     (= 1 (count (:poses pose)))
     (= 2 (count (:poses (first (:poses pose)))))))
   v70_l373)))


(def
 v73_l389
 (pj/arrange
  [(pj/pose iris :sepal-length :sepal-width)
   (pj/pose iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t74_l395
 (is
  ((fn
    [pose]
    (and
     (= "Arranged" (get-in pose [:opts :title]))
     (= #{:y} (get-in pose [:opts :share-scales]))))
   v73_l389)))


(def
 v76_l411
 (->
  iris
  (pj/pose
   (pj/cross [:sepal-length :sepal-width] [:petal-length :petal-width])
   {:color :species})))


(deftest
 t77_l416
 (is
  ((fn
    [pose]
    (and
     (= {:color :species} (:mapping pose))
     (= 2 (count (:poses pose)))
     (every?
      (fn* [p1__102782#] (= 2 (count (:poses p1__102782#))))
      (:poses pose))))
   v76_l411)))


(def
 v79_l425
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


(deftest t80_l435 (is (true? v79_l425)))


(def
 v82_l459
 (-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point))


(deftest
 t83_l463
 (is
  ((fn
    [pose]
    (and
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))
     (empty? (or (:mapping (first (:layers pose))) {}))))
   v82_l459)))


(def
 v85_l472
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point))


(def
 v86_l477
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point
  pose-summary))


(deftest
 t87_l483
 (is
  ((fn
    [pose]
    (and
     (contains? pose :poses)
     (= 1 (count (:layers pose)))
     (= :point (:layer-type (first (:layers pose))))))
   v86_l477)))


(def
 v89_l495
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


(deftest t90_l505 (is ((fn [counts] (= [0 1] counts)) v89_l495)))


(def
 v92_l519
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t93_l524
 (is
  ((fn
    [pose]
    (and
     (= 2 (count (:poses pose)))
     (= 1 (count (:layers (first (:poses pose)))))
     (= 0 (count (:layers (second (:poses pose)))))
     (= :point (:layer-type (first (:layers (first (:poses pose))))))))
   v92_l519)))


(def
 v95_l535
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t96_l539
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v95_l535)))


(def
 v98_l554
 (try
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point :petal-length :petal-width))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t99_l561
 (is
  ((fn
    [msg]
    (and
     (string? msg)
     (re-find #"conflict with the pose's existing position" msg)))
   v98_l554)))


(def
 v101_l575
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :petal-length)))


(deftest
 t102_l580
 (is
  ((fn
    [pose]
    (and
     (= 3 (count (:poses pose)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:poses pose) 2)))
     (= 1 (count (:layers (nth (:poses pose) 2))))))
   v101_l575)))


(def v104_l596 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v105_l600 (-> tiny (pj/lay-point :a :b)))


(deftest
 t106_l603
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v105_l600)))


(def v108_l607 (-> tiny (pj/pose :a :b) pj/lay-point pose-summary))


(deftest
 t109_l612
 (is
  ((fn
    [pose]
    (and
     (= {:x :a, :y :b} (:mapping pose))
     (= 1 (count (:layers pose)))
     (not (contains? pose :poses))))
   v108_l607)))


(def
 v111_l637
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  pj/lay-point))


(deftest
 t112_l640
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v111_l637)))


(def
 v114_l645
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} pj/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t115_l651
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v114_l645)))


(def
 v117_l661
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t118_l665
 (is
  ((fn
    [pose]
    (and (not (contains? pose :poses)) (= 1 (count (:layers pose)))))
   v117_l661)))


(def v120_l673 (-> iris (pj/pose "sepal-length" "sepal-width")))


(def
 v121_l676
 (-> iris (pj/pose "sepal-length" "sepal-width") pose-summary))


(deftest
 t122_l680
 (is
  ((fn
    [pose]
    (= {:x "sepal-length", :y "sepal-width"} (:mapping pose)))
   v121_l676)))


(def
 v124_l701
 (def
  s1-composite
  (pj/pose
   {:mapping {:color :species},
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}],
    :data iris})))


(def v125_l710 s1-composite)


(deftest
 t126_l712
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
   v125_l710)))


(def
 v128_l724
 (def
  s1-siblings
  (pj/pose
   {:poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}],
    :data iris})))


(def v129_l732 s1-siblings)


(deftest
 t130_l734
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
   v129_l732)))


(def
 v132_l752
 (def
  s2-tree
  (pj/pose
   {:poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :a, :y :b},
      :layers [{:layer-type :point}],
      :data (tc/dataset {:a [1 2 3], :b [3 5 4]})}],
    :data iris})))


(def v133_l761 s2-tree)


(deftest
 t134_l763
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
   v133_l761)))


(def
 v136_l780
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t137_l785
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v136_l780)))


(def
 v139_l799
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t140_l804
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v139_l799)))


(def
 v142_l825
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})))


(deftest
 t143_l830
 (is ((fn [pose] (= "Iris" (get-in pose [:opts :title]))) v142_l825)))


(def
 v145_l835
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "One"})
  (pj/options {:title "Two", :subtitle "Sub"})))


(deftest
 t146_l841
 (is
  ((fn
    [pose]
    (and
     (= "Two" (get-in pose [:opts :title]))
     (= "Sub" (get-in pose [:opts :subtitle]))))
   v145_l835)))


(def
 v148_l857
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/scale :x :log)
  (pj/coord :flip)))


(deftest
 t149_l863
 (is
  ((fn
    [pose]
    (and
     (= {:type :log} (get-in pose [:opts :x-scale]))
     (= :flip (get-in pose [:opts :coord]))))
   v148_l857)))


(def
 v151_l870
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:size :petal-length})
  pj/lay-point
  (pj/scale :size :log)))


(deftest
 t152_l875
 (is
  ((fn [pose] (= {:type :log} (get-in pose [:opts :size-scale])))
   v151_l870)))


(def
 v154_l886
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t155_l891
 (is
  ((fn [pose] (= :species (get-in pose [:opts :facet-col])))
   v154_l886)))


(def
 v157_l896
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet-grid :species :species)))


(deftest
 t158_l901
 (is
  ((fn
    [pose]
    (and
     (= :species (get-in pose [:opts :facet-col]))
     (= :species (get-in pose [:opts :facet-row]))))
   v157_l896)))


(def
 v160_l915
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t161_l920
 (is
  ((fn
    [pose]
    (let
     [layers
      (:layers pose)
      rule
      (some
       (fn*
        [p1__102783#]
        (when (= :rule-h (:layer-type p1__102783#)) p1__102783#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v160_l915)))


(def
 v163_l930
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t164_l935
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
   v163_l930)))


(def
 v166_l956
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t167_l963
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
   v166_l956)))


(def
 v169_l980
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point))


(deftest
 t170_l984
 (is
  ((fn
    [_]
    (let
     [drafts
      (->
       iris
       (pj/pose :sepal-length :sepal-width {:color :species})
       pj/lay-point
       pj/draft)]
     (and
      (= 1 (count drafts))
      (let
       [d (first drafts)]
       (and
        (= :sepal-length (:x d))
        (= :sepal-width (:y d))
        (= :species (:color d))
        (= :point (:mark d))
        (= 150 (tc/row-count (:data d))))))))
   v169_l980)))


(def
 v172_l1012
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point))


(deftest
 t173_l1017
 (is
  ((fn
    [pose]
    (let
     [plan (pj/plan pose)]
     (and (:composite? plan) (= 2 (count (:sub-plots plan))))))
   v172_l1012)))


(def
 v175_l1030
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t176_l1035
 (is
  ((fn
    [pose]
    (let
     [plan (pj/plan pose) panel (first (:panels plan))]
     (and (= 1 (count (:panels plan))) (= 2 (count (:layers panel))))))
   v175_l1030)))


(def
 v178_l1048
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t179_l1053
 (is ((fn [pose] (= 3 (count (:panels (pj/plan pose))))) v178_l1048)))


(def
 v181_l1071
 (def
  l4-shared
  (pj/arrange
   [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
    (-> iris (pj/pose :sepal-length :petal-width) pj/lay-point)]
   {:share-scales #{:x}})))


(def v182_l1077 l4-shared)


(deftest
 t183_l1079
 (is
  ((fn
    [pose]
    (let
     [sub-plots
      (:sub-plots (pj/plan pose))
      domains
      (mapv
       (fn*
        [p1__102784#]
        (get-in p1__102784# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v182_l1077)))


(def
 v185_l1110
 (->
  iris
  (pj/pose
   (pj/cross [:sepal-length :sepal-width] [:petal-length :petal-width])
   {:color :species})))


(deftest
 t186_l1115
 (is
  ((fn
    [pose]
    (and
     (= :vertical (get-in pose [:layout :direction]))
     (= #{:y :x} (get-in pose [:opts :share-scales]))
     (= 2 (count (:poses pose)))
     (every?
      (fn* [p1__102785#] (= 2 (count (:poses p1__102785#))))
      (:poses pose))
     (= {:color :species} (:mapping pose))))
   v185_l1110)))


(def v188_l1135 (pj/cross [:a :b] [:c :d]))


(deftest
 t189_l1137
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v188_l1135)))
