(ns
 plotje-book.pose-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (def iris (rdatasets/datasets-iris)))


(def
 v5_l32
 (defn
  strip-data
  [fr]
  (cond->
   (dissoc fr :data)
   (:layers fr)
   (update
    :layers
    (partial mapv (fn* [p1__69781#] (dissoc p1__69781# :data))))
   (:poses fr)
   (update :poses (partial mapv strip-data)))))


(def
 v6_l37
 (defn
  fr-summary
  "Print pose structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr))))


(def v8_l79 (-> iris (pj/pose :sepal-length :sepal-width)))


(deftest
 t9_l82
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v8_l79)))


(def v10_l84 (-> iris (pj/pose :sepal-length :sepal-width) fr-summary))


(deftest
 t11_l88
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= [] (:layers fr))
     (not (contains? fr :poses))))
   v10_l84)))


(def v13_l98 (-> iris (pj/pose {:color :species}) fr-summary))


(deftest
 t14_l102
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (not (contains? fr :poses))))
   v13_l98)))


(def v16_l114 (-> iris pj/pose (pj/pose :sepal-length :sepal-width)))


(deftest
 t17_l118
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (not (contains? fr :poses))))
   v16_l114)))


(def
 v19_l125
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t20_l129
 (is
  ((fn
    [fr]
    (=
     {:x :sepal-length, :y :sepal-width, :color :species}
     (:mapping fr)))
   v19_l125)))


(def
 v22_l137
 (->
  iris
  pj/pose
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t23_l142
 (is
  ((fn
    [fr]
    (= fr (pj/pose iris :sepal-length :sepal-width {:color :species})))
   v22_l137)))


(def
 v25_l155
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t26_l159
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:poses fr))))))
   v25_l155)))


(def
 v28_l171
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/pose :petal-length :petal-width)
  fr-summary))


(deftest
 t29_l176
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:poses fr))))))
   v28_l171)))


(def
 v31_l188
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/options {:title "Iris"})
  (pj/pose :petal-length :petal-width)))


(deftest
 t32_l193
 (is
  ((fn
    [fr]
    (and
     (= "Iris" (get-in fr [:opts :title]))
     (not (contains? (first (:poses fr)) :opts))))
   v31_l188)))


(def
 v34_l206
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})))


(deftest
 t35_l210
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:poses fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))))
   v34_l206)))


(def
 v37_l223
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})
  (pj/pose :petal-length :petal-width)))


(deftest
 t38_l228
 (is
  ((fn
    [fr]
    (=
     fr
     (->
      iris
      (pj/pose :sepal-length :sepal-width {:color :species})
      (pj/pose :petal-length :petal-width))))
   v37_l223)))


(def
 v40_l245
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/pose :petal-length :petal-width)))


(deftest
 t41_l250
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (= 2 (count (:poses fr)))
     (= [] (:layers (first (:poses fr))))
     (= [] (:layers (second (:poses fr))))))
   v40_l245)))


(def
 v43_l263
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)))


(deftest
 t44_l268
 (is
  ((fn
    [fr]
    (and
     (or (not (contains? fr :layers)) (= [] (:layers fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= [] (:layers (second (:poses fr))))))
   v43_l263)))


(def
 v46_l284
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose :sepal-length :petal-length)))


(deftest
 t47_l289
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:poses fr)))
     (=
      [{:x :sepal-length, :y :sepal-width}
       {:x :petal-length, :y :petal-width}
       {:x :sepal-length, :y :petal-length}]
      (mapv :mapping (:poses fr)))))
   v46_l284)))


(def
 v49_l300
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})
  fr-summary))


(deftest
 t50_l306
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))))
   v49_l300)))


(def
 v52_l319
 (let
  [fr (-> iris (pj/pose :sepal-length :sepal-width))]
  (= fr (pj/pose fr))))


(deftest t53_l322 (is (true? v52_l319)))


(def
 v54_l324
 (let
  [fr
   (->
    iris
    (pj/pose :sepal-length :sepal-width)
    (pj/pose :petal-length :petal-width))]
  (= fr (pj/pose fr))))


(deftest t55_l329 (is (true? v54_l324)))


(def
 v57_l338
 (pj/arrange
  [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
   (-> iris (pj/pose :petal-length :petal-width) pj/lay-point)]))


(deftest
 t58_l342
 (is
  ((fn
    [fr]
    (and
     (contains? fr :poses)
     (= :vertical (get-in fr [:layout :direction]))
     (= 1 (count (:poses fr)))
     (= 2 (count (:poses (first (:poses fr)))))))
   v57_l338)))


(def
 v60_l354
 (pj/arrange
  [(pj/pose iris :sepal-length :sepal-width)
   (pj/pose iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t61_l360
 (is
  ((fn
    [fr]
    (and
     (= "Arranged" (get-in fr [:opts :title]))
     (= #{:y} (:share-scales fr))))
   v60_l354)))


(def
 v63_l388
 (-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point))


(deftest
 t64_l392
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (empty? (or (:mapping (first (:layers fr))) {}))))
   v63_l388)))


(def
 v66_l401
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point
  fr-summary))


(deftest
 t67_l407
 (is
  ((fn
    [fr]
    (and
     (contains? fr :poses)
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))))
   v66_l401)))


(def
 v69_l419
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


(deftest t70_l429 (is ((fn [counts] (= [0 1] counts)) v69_l419)))


(def
 v72_l440
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t73_l445
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= 0 (count (:layers (second (:poses fr)))))
     (= :point (:layer-type (first (:layers (first (:poses fr))))))))
   v72_l440)))


(def
 v75_l456
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t76_l460
 (is
  ((fn
    [fr]
    (and (not (contains? fr :poses)) (= 1 (count (:layers fr)))))
   v75_l456)))


(def
 v78_l474
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point :petal-length :petal-width)))


(deftest
 t79_l478
 (is
  ((fn
    [fr]
    (and
     (not (contains? fr :poses))
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= 1 (count (:layers fr)))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (first (:layers fr))))))
   v78_l474)))


(def
 v81_l495
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :petal-length)))


(deftest
 t82_l500
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:poses fr)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:poses fr) 2)))
     (= 1 (count (:layers (nth (:poses fr) 2))))))
   v81_l495)))


(def v84_l516 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v85_l520 (-> tiny (pj/lay-point :a :b)))


(deftest
 t86_l523
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v85_l520)))


(def v88_l527 (-> tiny (pj/pose :a :b) pj/lay-point fr-summary))


(deftest
 t89_l532
 (is
  ((fn
    [fr]
    (and
     (= {:x :a, :y :b} (:mapping fr))
     (= 1 (count (:layers fr)))
     (not (contains? fr :poses))))
   v88_l527)))


(def
 v91_l557
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  pj/lay-point))


(deftest
 t92_l560
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v91_l557)))


(def
 v94_l565
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} pj/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t95_l571
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v94_l565)))


(def
 v97_l581
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t98_l585
 (is
  ((fn
    [fr]
    (and (not (contains? fr :poses)) (= 1 (count (:layers fr)))))
   v97_l581)))


(def
 v100_l593
 (-> iris (pj/pose "sepal-length" "sepal-width") fr-summary))


(deftest
 t101_l597
 (is
  ((fn [fr] (= {:x "sepal-length", :y "sepal-width"} (:mapping fr)))
   v100_l593)))


(def
 v103_l618
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


(def v104_l627 s1-composite)


(deftest
 t105_l629
 (is
  ((fn
    [fr]
    (let
     [pl
      (pj/plan fr)
      panels
      (mapv (comp :panels :plan) (:sub-plots pl))]
     (every?
      (fn [pp] (= 3 (count (:groups (first (:layers (first pp)))))))
      panels)))
   v104_l627)))


(def
 v107_l641
 (def
  s1-siblings
  (pj/prepare-pose
   {:data iris,
    :poses
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v108_l649 s1-siblings)


(deftest
 t109_l651
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (pj/plan fr))
      panel-groups
      (mapv
       (fn
        [sp]
        (count
         (:groups (first (:layers (first (-> sp :plan :panels)))))))
       sub-plots)]
     (= [1 3] panel-groups)))
   v108_l649)))


(def
 v111_l669
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


(def v112_l678 s2-tree)


(deftest
 t113_l680
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (pj/plan fr))
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
   v112_l678)))


(def
 v115_l697
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t116_l702
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v115_l697)))


(def
 v118_l716
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t119_l721
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v118_l716)))


(def
 v121_l742
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})))


(deftest
 t122_l747
 (is ((fn [fr] (= "Iris" (get-in fr [:opts :title]))) v121_l742)))


(def
 v124_l752
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "One"})
  (pj/options {:title "Two", :subtitle "Sub"})))


(deftest
 t125_l758
 (is
  ((fn
    [fr]
    (and
     (= "Two" (get-in fr [:opts :title]))
     (= "Sub" (get-in fr [:opts :subtitle]))))
   v124_l752)))


(def
 v127_l770
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/scale :x :log)
  (pj/coord :flip)))


(deftest
 t128_l776
 (is
  ((fn
    [fr]
    (and
     (= {:type :log} (get-in fr [:opts :x-scale]))
     (= :flip (get-in fr [:opts :coord]))))
   v127_l770)))


(def
 v130_l788
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t131_l793
 (is ((fn [fr] (= :species (get-in fr [:opts :facet-col]))) v130_l788)))


(def
 v133_l798
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet-grid :species :species)))


(deftest
 t134_l803
 (is
  ((fn
    [fr]
    (and
     (= :species (get-in fr [:opts :facet-col]))
     (= :species (get-in fr [:opts :facet-row]))))
   v133_l798)))


(def
 v136_l817
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})))


(deftest
 t137_l822
 (is
  ((fn
    [fr]
    (let
     [layers
      (:layers fr)
      rule
      (some
       (fn*
        [p1__69782#]
        (when (= :rule-h (:layer-type p1__69782#)) p1__69782#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v136_l817)))


(def
 v139_l832
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t140_l837
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= 0 (count (:layers (second (:poses fr)))))
     (= :rule-h (:layer-type (first (:layers (first (:poses fr))))))))
   v139_l832)))


(def
 v142_l858
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t143_l865
 (is
  ((fn
    [fr]
    (let
     [pl
      (pj/plan fr)
      panel-layer-counts
      (mapv
       (fn [sp] (count (:layers (first (-> sp :plan :panels)))))
       (:sub-plots pl))]
     (= [2 1] panel-layer-counts)))
   v142_l858)))


(def
 v145_l882
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  pj/draft))


(deftest
 t146_l887
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
   v145_l882)))


(def
 v148_l911
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  pj/plan))


(deftest
 t149_l917
 (is
  ((fn [pl] (and (:composite? pl) (= 2 (count (:sub-plots pl)))))
   v148_l911)))


(def
 v151_l929
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t152_l934
 (is
  ((fn
    [fr]
    (let
     [pl (pj/plan fr) panel (first (:panels pl))]
     (and (= 1 (count (:panels pl))) (= 2 (count (:layers panel))))))
   v151_l929)))


(def
 v154_l947
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t155_l952
 (is ((fn [fr] (= 3 (count (:panels (pj/plan fr))))) v154_l947)))


(def
 v157_l970
 (def
  l4-shared
  (pj/arrange
   [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
    (-> iris (pj/pose :sepal-length :petal-width) pj/lay-point)]
   {:share-scales #{:x}})))


(def v158_l976 l4-shared)


(deftest
 t159_l978
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (pj/plan fr))
      domains
      (mapv
       (fn*
        [p1__69783#]
        (get-in p1__69783# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v158_l976)))


(def
 v161_l1009
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose
   (pj/cross
    [:sepal-length :sepal-width]
    [:petal-length :petal-width]))))


(deftest
 t162_l1014
 (is
  ((fn
    [fr]
    (and
     (= :vertical (get-in fr [:layout :direction]))
     (= #{:y :x} (:share-scales fr))
     (= 2 (count (:poses fr)))
     (every?
      (fn* [p1__69784#] (= 2 (count (:poses p1__69784#))))
      (:poses fr))
     (= {:color :species} (:mapping fr))))
   v161_l1009)))


(def v164_l1033 (pj/cross [:a :b] [:c :d]))


(deftest
 t165_l1035
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v164_l1033)))
