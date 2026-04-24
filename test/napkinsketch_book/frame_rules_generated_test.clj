(ns
 napkinsketch-book.frame-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
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
    (partial mapv (fn* [p1__177286#] (dissoc p1__177286# :data))))
   (:frames fr)
   (update :frames (partial mapv strip-data)))))


(def
 v6_l37
 (defn
  fr-summary
  "Print frame structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr))))


(def v8_l79 (-> iris (sk/frame :sepal-length :sepal-width)))


(deftest
 t9_l82
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v8_l79)))


(def v10_l84 (-> iris (sk/frame :sepal-length :sepal-width) fr-summary))


(deftest
 t11_l88
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= [] (:layers fr))
     (not (contains? fr :frames))))
   v10_l84)))


(def v13_l98 (-> iris (sk/frame {:color :species}) fr-summary))


(deftest
 t14_l102
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (not (contains? fr :frames))))
   v13_l98)))


(def v16_l114 (-> iris sk/frame (sk/frame :sepal-length :sepal-width)))


(deftest
 t17_l118
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (not (contains? fr :frames))))
   v16_l114)))


(def
 v19_l125
 (->
  iris
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


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
  sk/frame
  (sk/frame {:color :species})
  (sk/frame :sepal-length :sepal-width)))


(deftest
 t23_l142
 (is
  ((fn
    [fr]
    (=
     fr
     (sk/frame iris :sepal-length :sepal-width {:color :species})))
   v22_l137)))


(def
 v25_l155
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t26_l159
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
   v25_l155)))


(def
 v28_l171
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  (sk/frame :petal-length :petal-width)
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
      (:mapping (first (:frames fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:frames fr))))))
   v28_l171)))


(def
 v31_l188
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/options {:title "Iris"})
  (sk/frame :petal-length :petal-width)))


(deftest
 t32_l193
 (is
  ((fn
    [fr]
    (and
     (= "Iris" (get-in fr [:opts :title]))
     (not (contains? (first (:frames fr)) :opts))))
   v31_l188)))


(def
 v34_l206
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})))


(deftest
 t35_l210
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v34_l206)))


(def
 v37_l223
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame {:color :species})
  (sk/frame :petal-length :petal-width)))


(deftest
 t38_l228
 (is
  ((fn
    [fr]
    (=
     fr
     (->
      iris
      (sk/frame :sepal-length :sepal-width {:color :species})
      (sk/frame :petal-length :petal-width))))
   v37_l223)))


(def
 v40_l245
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/frame :petal-length :petal-width)))


(deftest
 t41_l250
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (= 2 (count (:frames fr)))
     (= [] (:layers (first (:frames fr))))
     (= [] (:layers (second (:frames fr))))))
   v40_l245)))


(def
 v43_l263
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)))


(deftest
 t44_l268
 (is
  ((fn
    [fr]
    (and
     (or (not (contains? fr :layers)) (= [] (:layers fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= [] (:layers (second (:frames fr))))))
   v43_l263)))


(def
 v46_l284
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame :sepal-length :petal-length)))


(deftest
 t47_l289
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
   v46_l284)))


(def
 v49_l300
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/frame {:color :species})
  fr-summary))


(deftest
 t50_l306
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:frames fr))))))
   v49_l300)))


(def
 v52_l319
 (let
  [fr (-> iris (sk/frame :sepal-length :sepal-width))]
  (= fr (sk/frame fr))))


(deftest t53_l322 (is (true? v52_l319)))


(def
 v54_l324
 (let
  [fr
   (->
    iris
    (sk/frame :sepal-length :sepal-width)
    (sk/frame :petal-length :petal-width))]
  (= fr (sk/frame fr))))


(deftest t55_l329 (is (true? v54_l324)))


(def
 v57_l338
 (sk/arrange
  [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
   (-> iris (sk/frame :petal-length :petal-width) sk/lay-point)]))


(deftest
 t58_l342
 (is
  ((fn
    [fr]
    (and
     (contains? fr :frames)
     (= :vertical (get-in fr [:layout :direction]))
     (= 1 (count (:frames fr)))
     (= 2 (count (:frames (first (:frames fr)))))))
   v57_l338)))


(def
 v60_l354
 (sk/arrange
  [(sk/frame iris :sepal-length :sepal-width)
   (sk/frame iris :petal-length :petal-width)]
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
 (-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point))


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
  (sk/arrange
   [(sk/frame iris :sepal-length :sepal-width)
    (sk/frame iris :petal-length :petal-width)])
  sk/lay-point
  fr-summary))


(deftest
 t67_l407
 (is
  ((fn
    [fr]
    (and
     (contains? fr :frames)
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))))
   v66_l401)))


(def
 v69_l419
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


(deftest t70_l429 (is ((fn [counts] (= [0 1] counts)) v69_l419)))


(def
 v72_l440
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-point :sepal-length :sepal-width)))


(deftest
 t73_l445
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= 0 (count (:layers (second (:frames fr)))))
     (= :point (:layer-type (first (:layers (first (:frames fr))))))))
   v72_l440)))


(def
 v75_l456
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point "sepal-length" "sepal-width")))


(deftest
 t76_l460
 (is
  ((fn
    [fr]
    (and (not (contains? fr :frames)) (= 1 (count (:layers fr)))))
   v75_l456)))


(def
 v78_l474
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point :petal-length :petal-width)))


(deftest
 t79_l478
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
   v78_l474)))


(def
 v81_l495
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-point :sepal-length :petal-length)))


(deftest
 t82_l500
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:frames fr)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:frames fr) 2)))
     (= 1 (count (:layers (nth (:frames fr) 2))))))
   v81_l495)))


(def v84_l518 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v85_l522 (-> tiny (sk/lay-point :a :b)))


(deftest
 t86_l525
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v85_l522)))


(def v88_l529 (-> tiny (sk/frame :a :b) sk/lay-point fr-summary))


(deftest
 t89_l534
 (is
  ((fn
    [fr]
    (and
     (= {:x :a, :y :b} (:mapping fr))
     (= 1 (count (:layers fr)))
     (not (contains? fr :frames))))
   v88_l529)))


(def
 v91_l559
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  sk/lay-point))


(deftest
 t92_l562
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v91_l559)))


(def
 v94_l567
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} sk/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t95_l573
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v94_l567)))


(def
 v97_l583
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point "sepal-length" "sepal-width")))


(deftest
 t98_l587
 (is
  ((fn
    [fr]
    (and (not (contains? fr :frames)) (= 1 (count (:layers fr)))))
   v97_l583)))


(def
 v100_l595
 (-> iris (sk/frame "sepal-length" "sepal-width") fr-summary))


(deftest
 t101_l599
 (is
  ((fn [fr] (= {:x "sepal-length", :y "sepal-width"} (:mapping fr)))
   v100_l595)))


(def
 v103_l620
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


(def v104_l629 s1-composite)


(deftest
 t105_l631
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
   v104_l629)))


(def
 v107_l643
 (def
  s1-siblings
  (sk/prepare-frame
   {:data iris,
    :frames
    [{:mapping {:x :sepal-length, :y :sepal-width},
      :layers [{:layer-type :point}]}
     {:mapping {:x :petal-length, :y :petal-width, :color :species},
      :layers [{:layer-type :point}]}]})))


(def v108_l651 s1-siblings)


(deftest
 t109_l653
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
   v108_l651)))


(def
 v111_l671
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


(def v112_l680 s2-tree)


(deftest
 t113_l682
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
   v112_l680)))


(def
 v115_l699
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t116_l704
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v115_l699)))


(def
 v118_l718
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t119_l723
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v118_l718)))


(def
 v121_l744
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "Iris"})))


(deftest
 t122_l749
 (is ((fn [fr] (= "Iris" (get-in fr [:opts :title]))) v121_l744)))


(def
 v124_l754
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/options {:title "One"})
  (sk/options {:title "Two", :subtitle "Sub"})))


(deftest
 t125_l760
 (is
  ((fn
    [fr]
    (and
     (= "Two" (get-in fr [:opts :title]))
     (= "Sub" (get-in fr [:opts :subtitle]))))
   v124_l754)))


(def
 v127_l772
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t128_l778
 (is
  ((fn
    [fr]
    (and
     (= {:type :log} (get-in fr [:opts :x-scale]))
     (= :flip (get-in fr [:opts :coord]))))
   v127_l772)))


(def
 v130_l790
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet :species)))


(deftest
 t131_l795
 (is ((fn [fr] (= :species (get-in fr [:opts :facet-col]))) v130_l790)))


(def
 v133_l800
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet-grid :species :species)))


(deftest
 t134_l805
 (is
  ((fn
    [fr]
    (and
     (= :species (get-in fr [:opts :facet-col]))
     (= :species (get-in fr [:opts :facet-row]))))
   v133_l800)))


(def
 v136_l819
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/lay-point {:color :species})
  (sk/lay-rule-h {:y-intercept 3.0})))


(deftest
 t137_l824
 (is
  ((fn
    [fr]
    (let
     [layers
      (:layers fr)
      rule
      (some
       (fn*
        [p1__177287#]
        (when (= :rule-h (:layer-type p1__177287#)) p1__177287#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v136_l819)))


(def
 v139_l834
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  (sk/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t140_l839
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:frames fr)))
     (= 1 (count (:layers (first (:frames fr)))))
     (= 0 (count (:layers (second (:frames fr)))))
     (= :rule-h (:layer-type (first (:layers (first (:frames fr))))))))
   v139_l834)))


(def
 v142_l860
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  sk/lay-point
  (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t143_l867
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
   v142_l860)))


(def
 v145_l884
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/draft))


(deftest
 t146_l889
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
   v145_l884)))


(def
 v148_l913
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  (sk/frame :petal-length :petal-width)
  sk/lay-point
  sk/plan))


(deftest
 t149_l919
 (is
  ((fn [pl] (and (:composite? pl) (= 2 (count (:sub-plots pl)))))
   v148_l913)))


(def
 v151_l931
 (->
  iris
  (sk/frame :sepal-length :sepal-width {:color :species})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t152_l936
 (is
  ((fn
    [fr]
    (let
     [pl (sk/plan fr) panel (first (:panels pl))]
     (and (= 1 (count (:panels pl))) (= 2 (count (:layers panel))))))
   v151_l931)))


(def
 v154_l949
 (->
  iris
  (sk/frame :sepal-length :sepal-width)
  sk/lay-point
  (sk/facet :species)))


(deftest
 t155_l954
 (is ((fn [fr] (= 3 (count (:panels (sk/plan fr))))) v154_l949)))


(def
 v157_l972
 (def
  l4-shared
  (sk/arrange
   [(-> iris (sk/frame :sepal-length :sepal-width) sk/lay-point)
    (-> iris (sk/frame :sepal-length :petal-width) sk/lay-point)]
   {:share-scales #{:x}})))


(def v158_l978 l4-shared)


(deftest
 t159_l980
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (sk/plan fr))
      domains
      (mapv
       (fn*
        [p1__177288#]
        (get-in p1__177288# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v158_l978)))


(def
 v161_l1011
 (->
  iris
  (sk/frame {:color :species})
  (sk/frame
   (sk/cross
    [:sepal-length :sepal-width]
    [:petal-length :petal-width]))))


(deftest
 t162_l1016
 (is
  ((fn
    [fr]
    (and
     (= :vertical (get-in fr [:layout :direction]))
     (= #{:y :x} (:share-scales fr))
     (= 2 (count (:frames fr)))
     (every?
      (fn* [p1__177289#] (= 2 (count (:frames p1__177289#))))
      (:frames fr))
     (= {:color :species} (:mapping fr))))
   v161_l1011)))


(def v164_l1035 (sk/cross [:a :b] [:c :d]))


(deftest
 t165_l1037
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v164_l1035)))
