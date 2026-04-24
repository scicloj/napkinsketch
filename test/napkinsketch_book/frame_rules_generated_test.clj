(ns
 napkinsketch-book.frame-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (def iris (rdatasets/datasets-iris)))


(def
 v5_l35
 (defn
  strip-data
  [fr]
  (cond->
   (dissoc fr :data)
   (:layers fr)
   (update
    :layers
    (partial mapv (fn* [p1__167005#] (dissoc p1__167005# :data))))
   (:frames fr)
   (update :frames (partial mapv strip-data)))))


(def
 v6_l40
 (defn
  fr-summary
  "Print frame structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr))))


(def v8_l82 (-> iris (sk/frame :sepal-length :sepal-width)))


(deftest
 t9_l85
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v8_l82)))


(def v10_l87 (-> iris (sk/frame :sepal-length :sepal-width) fr-summary))


(deftest
 t11_l91
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= [] (:layers fr))
     (not (contains? fr :frames))))
   v10_l87)))


(def v13_l101 (-> iris (sk/frame {:color :species}) fr-summary))


(deftest
 t14_l105
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (not (contains? fr :frames))))
   v13_l101)))


(def v16_l117 (-> iris sk/frame (sk/frame :sepal-length :sepal-width)))


(deftest
 t17_l121
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (not (contains? fr :frames))))
   v16_l117)))


(def
 v19_l128
 (->
  iris
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


(deftest
 t20_l132
 (is
  ((fn
    [fr]
    (=
     {:x :sepal-length, :y :sepal-width, :color :species}
     (:mapping fr)))
   v19_l128)))


(def
 v22_l140
 (->
  iris
  sk/frame
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


(deftest
 t23_l145
 (is
  ((fn
    [fr]
    (=
     fr
     (sk/frame iris :sepal-length :sepal-width {:color :species})))
   v22_l140)))


(def
 v25_l158
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t26_l162
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:frames fr))))))
   v25_l158)))


(def
 v28_l174
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/frame :petal-length :petal-width)
  fr-summary))


(deftest
 t29_l179
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:frames fr))))))
   v28_l174)))


(def
 v31_l191
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/frame :petal-length :petal-width)))


(deftest
 t32_l196
 (is
  ((fn
    [fr]
    (and
     (= "Iris" (get-in fr [:opts :title]))
     (not (contains? (first (:frames fr)) :opts))))
   v31_l191)))


(def
 v34_l209
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})))


(deftest
 t35_l213
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v34_l209)))


(def
 v37_l226
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})
  (sk/frame :petal-length :petal-width)))


(deftest
 t38_l231
 (is
  ((fn
    [fr]
    (=
     fr
     (->
      iris
      (sk/frame :sepal-length :sepal-width {:color :species})
      (sk/frame :petal-length :petal-width))))
   v37_l226)))


(def
 v40_l248
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/frame :petal-length :petal-width)))


(deftest
 t41_l253
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (= 2 (count (:frames fr)))
     (= [] (:layers (first (:frames fr))))
     (= [] (:layers (second (:frames fr))))))
   v40_l248)))


(def
 v43_l266
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t44_l271
 (is
  ((fn
    [fr]
    (and
     (or (not (contains? fr :layers)) (= [] (:layers fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= [] (:layers (second (:frames fr))))))
   v43_l266)))


(def
 v46_l287
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame :sepal-length :petal-length)))


(deftest
 t47_l292
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:frames fr)))
     (=
      [{:x :sepal-length, :y :sepal-width}
       {:x :petal-length, :y :petal-width}
       {:x :sepal-length, :y :petal-length}]
      (mapv :mapping (:frames fr)))))
   v46_l287)))


(def
 v49_l303
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame {:color :species})
  fr-summary))


(deftest
 t50_l309
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v49_l303)))


(def
 v52_l322
 (let
  [fr (-> iris (sk/frame :sepal-length :sepal-width))]
  (= fr (sk/frame fr))))


(deftest t53_l325 (is (true? v52_l322)))


(def
 v54_l327
 (let
  [fr
   (->
    iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width))]
  (= fr (sk/frame fr))))


(deftest t55_l332 (is (true? v54_l327)))


(def
 v57_l341
 (sk/arrange
  [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
   (-> iris (sk/frame :petal-length :petal-width) sk/lay-point)]))


(deftest
 t58_l345
 (is
  ((fn
    [fr]
    (and
     (contains? fr :frames)
     (= :vertical (get-in fr [:layout :direction]))
     (= 1 (count (:frames fr)))
     (= 2 (count (:frames (first (:frames fr)))))))
   v57_l341)))


(def
 v60_l357
 (sk/arrange
  [(sk/frame iris :sepal-length :sepal-width)
   (sk/frame iris :petal-length :petal-width)]
  {:title "Arranged", :share-scales #{:y}}))


(deftest
 t61_l363
 (is
  ((fn
    [fr]
    (and
     (= "Arranged" (get-in fr [:opts :title]))
     (= #{:y} (:share-scales fr))))
   v60_l357)))


(def
 v63_l391
 (-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point))


(deftest
 t64_l395
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (empty? (or (:mapping (first (:layers fr))) {}))))
   v63_l391)))


(def
 v66_l404
 (->
  (sk/arrange
   [(sk/frame iris :sepal-length :sepal-width)
    (sk/frame iris :petal-length :petal-width)])
  sk/lay-point
  fr-summary))


(deftest
 t67_l410
 (is
  ((fn
    [fr]
    (and
     (contains? fr :frames)
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))))
   v66_l404)))


(def
 v69_l422
 (let
  [before
   (sk/arrange
    [(sk/frame iris :sepal-length :sepal-width)
     (sk/frame iris :petal-length :petal-width)])
   after
   (->
    (sk/arrange
     [(sk/frame iris :sepal-length :sepal-width)
      (sk/frame iris :petal-length :petal-width)])
    sk/lay-point)]
  [(count (or (:layers before) [])) (count (or (:layers after) []))]))


(deftest t70_l432 (is ((fn [counts] (= [0 1] counts)) v69_l422)))


(def
 v72_l443
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t73_l448
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= 0 (count (:layers (second (:frames fr)))))
     (= :point (:layer-type (first (:layers (first (:frames fr))))))))
   v72_l443)))


(def
 v75_l459
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point "sepal-length" "sepal-width")))


(deftest
 t76_l463
 (is
  ((fn
    [fr]
    (and (not (contains? fr :frames)) (= 1 (count (:layers fr)))))
   v75_l459)))


(def
 v78_l477
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t79_l481
 (is
  ((fn
    [fr]
    (and
     (not (contains? fr :frames))
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= 1 (count (:layers fr)))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (first (:layers fr))))))
   v78_l477)))


(def
 v81_l498
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-point :sepal-length :petal-length)))


(deftest
 t82_l503
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:frames fr)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:frames fr) 2)))
     (= 1 (count (:layers (nth (:frames fr) 2))))))
   v81_l498)))


(def v84_l521 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v85_l525 (-> tiny (sk/lay-point :a :b)))


(deftest
 t86_l528
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v85_l525)))


(def v88_l532 (-> tiny (sk/frame :a :b) sk/lay-point fr-summary))


(deftest
 t89_l537
 (is
  ((fn
    [fr]
    (and
     (= {:x :a, :y :b} (:mapping fr))
     (= 1 (count (:layers fr)))
     (not (contains? fr :frames))))
   v88_l532)))


(def
 v91_l562
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  sk/lay-point))


(deftest
 t92_l565
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v91_l562)))


(def
 v94_l570
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} sk/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t95_l576
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v94_l570)))


(def
 v97_l586
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point "sepal-length" "sepal-width")))


(deftest
 t98_l590
 (is
  ((fn
    [fr]
    (and (not (contains? fr :frames)) (= 1 (count (:layers fr)))))
   v97_l586)))


(def
 v100_l598
 (-> iris (sk/frame "sepal-length" "sepal-width") fr-summary))


(deftest
 t101_l602
 (is
  ((fn [fr] (= {:x "sepal-length", :y "sepal-width"} (:mapping fr)))
   v100_l598)))


(def
 v103_l623
 (def
  s1-composite
  (sk/prepare-frame
   {:data iris,
    :mapping {:color :species},
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width},
      :layers [{:layer-type :point}]}]})))


(def v104_l632 s1-composite)


(deftest
 t105_l634
 (is
  ((fn
    [fr]
    (let
     [pl
      (sk/plan fr)
      panels
      (mapv (comp :panels :plan) (:sub-plots pl))]
     (every?
      (fn [pp] (= 3 (count (:groups (first (:layers (first pp)))))))
      panels)))
   v104_l632)))


(def
 v107_l646
 (def
  s1-siblings
  (sk/prepare-frame
   {:data iris,
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v108_l654 s1-siblings)


(deftest
 t109_l656
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (sk/plan fr))
      panel-groups
      (mapv
       (fn
        [sp]
        (count
         (:groups (first (:layers (first (-> sp :plan :panels)))))))
       sub-plots)]
     (= [1 3] panel-groups)))
   v108_l654)))


(def
 v111_l674
 (def
  s2-tree
  (sk/prepare-frame
   {:data iris,
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :a, :y :b},
      :data (tc/dataset {:a [1 2 3], :b [3 5 4]}),
      :layers [{:layer-type :point}]}]})))


(def v112_l683 s2-tree)


(deftest
 t113_l685
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (sk/plan fr))
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
   v112_l683)))


(def
 v115_l702
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t116_l707
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v115_l702)))


(def
 v118_l721
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t119_l726
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v118_l721)))


(def
 v121_l747
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "Iris"})))


(deftest
 t122_l752
 (is ((fn [fr] (= "Iris" (get-in fr [:opts :title]))) v121_l747)))


(def
 v124_l757
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "One"})
  (sk/options {:title "Two", :subtitle "Sub"})))


(deftest
 t125_l763
 (is
  ((fn
    [fr]
    (and
     (= "Two" (get-in fr [:opts :title]))
     (= "Sub" (get-in fr [:opts :subtitle]))))
   v124_l757)))


(def
 v127_l775
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t128_l781
 (is
  ((fn
    [fr]
    (and
     (= {:type :log} (get-in fr [:opts :x-scale]))
     (= :flip (get-in fr [:opts :coord]))))
   v127_l775)))


(def
 v130_l793
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet :species)))


(deftest
 t131_l798
 (is ((fn [fr] (= :species (get-in fr [:opts :facet-col]))) v130_l793)))


(def
 v133_l803
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet-grid :species :species)))


(deftest
 t134_l808
 (is
  ((fn
    [fr]
    (and
     (= :species (get-in fr [:opts :facet-col]))
     (= :species (get-in fr [:opts :facet-row]))))
   v133_l803)))


(def
 v136_l822
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t137_l827
 (is
  ((fn
    [fr]
    (let
     [layers
      (:layers fr)
      rule
      (some
       (fn*
        [p1__167006#]
        (when (= :rule-h (:layer-type p1__167006#)) p1__167006#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v136_l822)))


(def
 v139_l837
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t140_l842
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= 0 (count (:layers (second (:frames fr)))))
     (= :rule-h (:layer-type (first (:layers (first (:frames fr))))))))
   v139_l837)))


(def
 v142_l863
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  sk/lay-point
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t143_l870
 (is
  ((fn
    [fr]
    (let
     [pl
      (sk/plan fr)
      panel-layer-counts
      (mapv
       (fn [sp] (count (:layers (first (-> sp :plan :panels)))))
       (:sub-plots pl))]
     (= [2 1] panel-layer-counts)))
   v142_l863)))


(def
 v145_l887
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/draft))


(deftest
 t146_l892
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
   v145_l887)))


(def
 v148_l916
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  sk/lay-point
  sk/plan))


(deftest
 t149_l922
 (is
  ((fn [pl] (and (:composite? pl) (= 2 (count (:sub-plots pl)))))
   v148_l916)))


(def
 v151_l934
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t152_l939
 (is
  ((fn
    [fr]
    (let
     [pl (sk/plan fr) panel (first (:panels pl))]
     (and (= 1 (count (:panels pl))) (= 2 (count (:layers panel))))))
   v151_l934)))


(def
 v154_l952
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet :species)))


(deftest
 t155_l957
 (is ((fn [fr] (= 3 (count (:panels (sk/plan fr))))) v154_l952)))


(def
 v157_l975
 (def
  l4-shared
  (sk/arrange
   [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
    (-> iris (sk/frame :sepal-length :petal-width) sk/lay-point)]
   {:share-scales #{:x}})))


(def v158_l981 l4-shared)


(deftest
 t159_l983
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (sk/plan fr))
      domains
      (mapv
       (fn*
        [p1__167007#]
        (get-in p1__167007# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v158_l981)))


(def
 v161_l1005
 (->
  iris
  (sk/frame {:color :species})
  sk/lay-point
  (sk/frame
   (sk/cross
    [:sepal-length :sepal-width]
    [:petal-length :petal-width]))))


(deftest
 t162_l1011
 (is
  ((fn
    [fr]
    (and
     (= :vertical (get-in fr [:layout :direction]))
     (= #{:y :x} (:share-scales fr))
     (= 2 (count (:frames fr)))
     (every?
      (fn* [p1__167008#] (= 2 (count (:frames p1__167008#))))
      (:frames fr))
     (= {:color :species} (:mapping fr))
     (= 1 (count (:layers fr)))))
   v161_l1005)))


(def v164_l1032 (sk/cross [:a :b] [:c :d]))


(deftest
 t165_l1034
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v164_l1032)))
