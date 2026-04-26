(ns
 plotje-book.pose-rules-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l28 (def iris (rdatasets/datasets-iris)))


(def
 v5_l34
 (defn
  strip-data
  [fr]
  (cond->
   (dissoc fr :data)
   (:layers fr)
   (update
    :layers
    (partial mapv (fn* [p1__84119#] (dissoc p1__84119# :data))))
   (:poses fr)
   (update :poses (partial mapv strip-data)))))


(def
 v6_l39
 (defn
  fr-summary
  "Print pose structure without :data (for readability)."
  [fr]
  (kind/pprint (strip-data fr))))


(def v8_l80 (-> iris (pj/pose :sepal-length :sepal-width)))


(deftest
 t9_l83
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v8_l80)))


(def v10_l85 (-> iris (pj/pose :sepal-length :sepal-width) fr-summary))


(deftest
 t11_l89
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (= [] (:layers fr))
     (not (contains? fr :poses))))
   v10_l85)))


(def v13_l99 (-> iris (pj/pose {:color :species}) fr-summary))


(deftest
 t14_l103
 (is
  ((fn
    [fr]
    (and
     (= {:color :species} (:mapping fr))
     (not (contains? fr :poses))))
   v13_l99)))


(def v16_l115 (-> iris pj/pose (pj/pose :sepal-length :sepal-width)))


(deftest
 t17_l119
 (is
  ((fn
    [fr]
    (and
     (= {:x :sepal-length, :y :sepal-width} (:mapping fr))
     (not (contains? fr :poses))))
   v16_l115)))


(def
 v19_l126
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t20_l130
 (is
  ((fn
    [fr]
    (=
     {:x :sepal-length, :y :sepal-width, :color :species}
     (:mapping fr)))
   v19_l126)))


(def
 v22_l138
 (->
  iris
  pj/pose
  (pj/pose {:color :species})
  (pj/pose :sepal-length :sepal-width)))


(deftest
 t23_l143
 (is
  ((fn
    [fr]
    (= fr (pj/pose iris :sepal-length :sepal-width {:color :species})))
   v22_l138)))


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
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))
     (=
      {:x :petal-length, :y :petal-width}
      (:mapping (second (:poses fr))))))
   v25_l156)))


(def
 v28_l172
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  (pj/pose :petal-length :petal-width)
  fr-summary))


(deftest
 t29_l177
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
   v28_l172)))


(def
 v31_l189
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/options {:title "Iris"})
  (pj/pose :petal-length :petal-width)))


(deftest
 t32_l194
 (is
  ((fn
    [fr]
    (and
     (= "Iris" (get-in fr [:opts :title]))
     (not (contains? (first (:poses fr)) :opts))))
   v31_l189)))


(def
 v34_l207
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})))


(deftest
 t35_l211
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:poses fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))))
   v34_l207)))


(def
 v37_l224
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose {:color :species})
  (pj/pose :petal-length :petal-width)))


(deftest
 t38_l229
 (is
  ((fn
    [fr]
    (=
     fr
     (->
      iris
      (pj/pose :sepal-length :sepal-width {:color :species})
      (pj/pose :petal-length :petal-width))))
   v37_l224)))


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
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (= 2 (count (:poses fr)))
     (= [] (:layers (first (:poses fr))))
     (= [] (:layers (second (:poses fr))))))
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
    [fr]
    (and
     (or (not (contains? fr :layers)) (= [] (:layers fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= [] (:layers (second (:poses fr))))))
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
    [fr]
    (and
     (= 3 (count (:poses fr)))
     (=
      [{:x :sepal-length, :y :sepal-width}
       {:x :petal-length, :y :petal-width}
       {:x :sepal-length, :y :petal-length}]
      (mapv :mapping (:poses fr)))))
   v46_l285)))


(def
 v49_l301
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/pose {:color :species})
  fr-summary))


(deftest
 t50_l307
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= {:color :species} (:mapping fr))
     (=
      {:x :sepal-length, :y :sepal-width}
      (:mapping (first (:poses fr))))))
   v49_l301)))


(def
 v52_l320
 (let
  [fr (-> iris (pj/pose :sepal-length :sepal-width))]
  (= fr (pj/pose fr))))


(deftest t53_l323 (is (true? v52_l320)))


(def
 v54_l325
 (let
  [fr
   (->
    iris
    (pj/pose :sepal-length :sepal-width)
    (pj/pose :petal-length :petal-width))]
  (= fr (pj/pose fr))))


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
    [fr]
    (and
     (contains? fr :poses)
     (= :vertical (get-in fr [:layout :direction]))
     (= 1 (count (:poses fr)))
     (= 2 (count (:poses (first (:poses fr)))))))
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
    [fr]
    (and
     (= "Arranged" (get-in fr [:opts :title]))
     (= #{:y} (get-in fr [:opts :share-scales]))))
   v60_l355)))


(def
 v63_l389
 (-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point))


(deftest
 t64_l393
 (is
  ((fn
    [fr]
    (and
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))
     (empty? (or (:mapping (first (:layers fr))) {}))))
   v63_l389)))


(def
 v66_l402
 (->
  (pj/arrange
   [(pj/pose iris :sepal-length :sepal-width)
    (pj/pose iris :petal-length :petal-width)])
  pj/lay-point
  fr-summary))


(deftest
 t67_l408
 (is
  ((fn
    [fr]
    (and
     (contains? fr :poses)
     (= 1 (count (:layers fr)))
     (= :point (:layer-type (first (:layers fr))))))
   v66_l402)))


(def
 v69_l420
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


(deftest t70_l430 (is ((fn [counts] (= [0 1] counts)) v69_l420)))


(def
 v72_l441
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :sepal-width)))


(deftest
 t73_l446
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= 0 (count (:layers (second (:poses fr)))))
     (= :point (:layer-type (first (:layers (first (:poses fr))))))))
   v72_l441)))


(def
 v75_l457
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t76_l461
 (is
  ((fn
    [fr]
    (and (not (contains? fr :poses)) (= 1 (count (:layers fr)))))
   v75_l457)))


(def
 v78_l476
 (try
  (->
   iris
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point :petal-length :petal-width))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t79_l483
 (is
  ((fn
    [msg]
    (and
     (string? msg)
     (re-find #"conflict with the pose's existing position" msg)))
   v78_l476)))


(def
 v81_l497
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-point :sepal-length :petal-length)))


(deftest
 t82_l502
 (is
  ((fn
    [fr]
    (and
     (= 3 (count (:poses fr)))
     (=
      {:x :sepal-length, :y :petal-length}
      (:mapping (nth (:poses fr) 2)))
     (= 1 (count (:layers (nth (:poses fr) 2))))))
   v81_l497)))


(def v84_l518 (def tiny {:a [1 2 3 4 5], :b [2 4 3 5 4]}))


(def v85_l522 (-> tiny (pj/lay-point :a :b)))


(deftest
 t86_l525
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v85_l522)))


(def v88_l529 (-> tiny (pj/pose :a :b) pj/lay-point fr-summary))


(deftest
 t89_l534
 (is
  ((fn
    [fr]
    (and
     (= {:x :a, :y :b} (:mapping fr))
     (= 1 (count (:layers fr)))
     (not (contains? fr :poses))))
   v88_l529)))


(def
 v91_l559
 (->
  {:height [1 2 3], :weight [4 5 6], :species ["a" "b" "a"]}
  pj/lay-point))


(deftest
 t92_l562
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v91_l559)))


(def
 v94_l567
 (try
  (-> {:a [1 2], :b [3 4], :c [5 6], :d [7 8]} pj/lay-point)
  (catch Exception e (ex-message e))))


(deftest
 t95_l573
 (is ((fn [msg] (re-find #"Cannot auto-infer columns" msg)) v94_l567)))


(def
 v97_l583
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point "sepal-length" "sepal-width")))


(deftest
 t98_l587
 (is
  ((fn
    [fr]
    (and (not (contains? fr :poses)) (= 1 (count (:layers fr)))))
   v97_l583)))


(def
 v100_l595
 (-> iris (pj/pose "sepal-length" "sepal-width") fr-summary))


(deftest
 t101_l599
 (is
  ((fn [fr] (= {:x "sepal-length", :y "sepal-width"} (:mapping fr)))
   v100_l595)))


(def
 v103_l620
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


(def v104_l629 s1-composite)


(deftest
 t105_l631
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
   v104_l629)))


(def
 v107_l643
 (def
  s1-siblings
  (pj/prepare-pose
   {:data iris,
    :poses
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
      (:sub-plots (pj/plan fr))
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
  (pj/prepare-pose
   {:data iris,
    :poses
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
   v112_l680)))


(def
 v115_l699
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:color nil, :stat :linear-model})))


(deftest
 t116_l704
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v115_l699)))


(def
 v118_l718
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t119_l723
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v118_l718)))


(def
 v121_l744
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "Iris"})))


(deftest
 t122_l749
 (is ((fn [fr] (= "Iris" (get-in fr [:opts :title]))) v121_l744)))


(def
 v124_l754
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/options {:title "One"})
  (pj/options {:title "Two", :subtitle "Sub"})))


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
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/scale :x :log)
  (pj/coord :flip)))


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
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t131_l795
 (is ((fn [fr] (= :species (get-in fr [:opts :facet-col]))) v130_l790)))


(def
 v133_l800
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet-grid :species :species)))


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
  (pj/pose :sepal-length :sepal-width)
  (pj/lay-point {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})))


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
        [p1__84120#]
        (when (= :rule-h (:layer-type p1__84120#)) p1__84120#))
       layers)]
     (and (some? rule) (= 3.0 (get-in rule [:mapping :y-intercept])))))
   v136_l819)))


(def
 v139_l834
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  (pj/lay-rule-h :sepal-length :sepal-width {:y-intercept 3.0})))


(deftest
 t140_l839
 (is
  ((fn
    [fr]
    (and
     (= 2 (count (:poses fr)))
     (= 1 (count (:layers (first (:poses fr)))))
     (= 0 (count (:layers (second (:poses fr)))))
     (= :rule-h (:layer-type (first (:layers (first (:poses fr))))))))
   v139_l834)))


(def
 v142_l860
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model})))


(deftest
 t143_l867
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
   v142_l860)))


(def
 v145_l884
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  pj/draft))


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
  (pj/pose :sepal-length :sepal-width)
  (pj/pose :petal-length :petal-width)
  pj/lay-point
  pj/plan))


(deftest
 t149_l919
 (is
  ((fn [pl] (and (:composite? pl) (= 2 (count (:sub-plots pl)))))
   v148_l913)))


(def
 v151_l931
 (->
  iris
  (pj/pose :sepal-length :sepal-width {:color :species})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t152_l936
 (is
  ((fn
    [fr]
    (let
     [pl (pj/plan fr) panel (first (:panels pl))]
     (and (= 1 (count (:panels pl))) (= 2 (count (:layers panel))))))
   v151_l931)))


(def
 v154_l949
 (->
  iris
  (pj/pose :sepal-length :sepal-width)
  pj/lay-point
  (pj/facet :species)))


(deftest
 t155_l954
 (is ((fn [fr] (= 3 (count (:panels (pj/plan fr))))) v154_l949)))


(def
 v157_l972
 (def
  l4-shared
  (pj/arrange
   [(-> iris (pj/pose :sepal-length :sepal-width) pj/lay-point)
    (-> iris (pj/pose :sepal-length :petal-width) pj/lay-point)]
   {:share-scales #{:x}})))


(def v158_l978 l4-shared)


(deftest
 t159_l980
 (is
  ((fn
    [fr]
    (let
     [sub-plots
      (:sub-plots (pj/plan fr))
      domains
      (mapv
       (fn*
        [p1__84121#]
        (get-in p1__84121# [:plan :panels 0 :x-scale :domain]))
       sub-plots)]
     (and (= 2 (count domains)) (= (first domains) (second domains)))))
   v158_l978)))


(def
 v161_l1011
 (->
  iris
  (pj/pose {:color :species})
  (pj/pose
   (pj/cross
    [:sepal-length :sepal-width]
    [:petal-length :petal-width]))))


(deftest
 t162_l1016
 (is
  ((fn
    [fr]
    (and
     (= :vertical (get-in fr [:layout :direction]))
     (= #{:y :x} (get-in fr [:opts :share-scales]))
     (= 2 (count (:poses fr)))
     (every?
      (fn* [p1__84122#] (= 2 (count (:poses p1__84122#))))
      (:poses fr))
     (= {:color :species} (:mapping fr))))
   v161_l1011)))


(def v164_l1035 (pj/cross [:a :b] [:c :d]))


(deftest
 t165_l1037
 (is
  ((fn [pairs] (= [[:a :c] [:a :d] [:b :c] [:b :d]] pairs))
   v164_l1035)))
