(ns
 napkinsketch-book.exploring-sketches-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.pprint :as pp]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3]}))


(def v5_l29 (-> tiny (sk/lay-point :x :y)))


(deftest
 t6_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l29)))


(def v8_l39 (def tiny-sk (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v10_l48 tiny-sk)


(deftest
 t11_l50
 (is
  ((fn
    [m]
    (and
     (= 600 (:width m))
     (= 400 (:height m))
     (nil? (:title m))
     (= "x" (:x-label m))
     (= "y" (:y-label m))
     (nil? (:legend m))))
   v10_l48)))


(def v13_l69 (def tiny-panel (first (:panels tiny-sk))))


(def v14_l71 (keys tiny-panel))


(deftest
 t15_l73
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v14_l71)))


(def v17_l77 (:x-domain tiny-panel))


(deftest
 t18_l79
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v17_l77)))


(def v19_l81 (:y-domain tiny-panel))


(deftest
 t20_l83
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v19_l81)))


(def v22_l87 (:x-scale tiny-panel))


(deftest t23_l89 (is ((fn [s] (= :linear (:type s))) v22_l87)))


(def v25_l93 (:x-ticks tiny-panel))


(deftest
 t26_l95
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v25_l93)))


(def v28_l107 (def tiny-layer (first (:layers tiny-panel))))


(def v29_l109 tiny-layer)


(deftest t30_l111 (is ((fn [m] (= :point (:mark m))) v29_l109)))


(def v32_l116 (count (:groups tiny-layer)))


(deftest t33_l118 (is ((fn [n] (= 1 n)) v32_l116)))


(def v35_l123 (first (:groups tiny-layer)))


(deftest
 t36_l125
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v35_l123)))


(def
 v38_l139
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l142
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t40_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l142)))


(def
 v41_l149
 (def
  iris-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   sk/sketch)))


(def v43_l155 iris-sk)


(deftest
 t44_l157
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v43_l155)))


(def
 v46_l162
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v47_l164 (count (:groups iris-layer)))


(deftest t48_l166 (is ((fn [n] (= 3 n)) v47_l164)))


(def
 v50_l170
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t51_l175
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__86130#] (= 50 (:n-points p1__86130#))) gs)))
   v50_l170)))


(def v53_l180 (:legend iris-sk))


(deftest
 t54_l182
 (is ((fn [leg] (= 3 (count (:entries leg)))) v53_l180)))


(def
 v56_l192
 (def
  cont-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :petal_length})
   sk/sketch)))


(def v57_l196 (:legend cont-sk))


(deftest t58_l198 (is ((fn [m] (= :continuous (:type m))) v57_l196)))


(def
 v60_l202
 (select-keys (:legend cont-sk) [:title :type :min :max :color-scale]))


(deftest
 t61_l204
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v60_l202)))


(def v63_l209 (count (:stops (:legend cont-sk))))


(deftest t64_l211 (is ((fn [n] (= 20 n)) v63_l209)))


(def v66_l218 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t67_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v66_l218)))


(def
 v68_l225
 (def hist-sk (-> iris (sk/lay-histogram :sepal_length) sk/sketch)))


(def v69_l229 hist-sk)


(deftest t70_l231 (is ((fn [m] (= 1 (count (:panels m)))) v69_l229)))


(def
 v71_l233
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v72_l235 (:mark hist-layer))


(deftest t73_l237 (is ((fn [m] (= :bar m)) v72_l235)))


(def v75_l241 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t76_l244
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__86131#] (< (:lo p1__86131#) (:hi p1__86131#)))
      bars)
     (every? (fn* [p1__86132#] (pos? (:count p1__86132#))) bars)))
   v75_l241)))


(def v78_l256 (-> iris (sk/lay-bar :species {:color :species})))


(deftest
 t79_l259
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v78_l256)))


(def
 v80_l263
 (def
  bar-sk
  (-> iris (sk/lay-bar :species {:color :species}) sk/sketch)))


(def
 v81_l267
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v83_l271 bar-layer)


(deftest
 t84_l273
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v83_l271)))


(def
 v86_l279
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t87_l284 (is ((fn [gs] (= 3 (count gs))) v86_l279)))


(def
 v89_l293
 (def
  stacked-sk
  (-> iris (sk/lay-stacked-bar :species {:color :species}) sk/sketch)))


(def
 v90_l297
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v91_l299 (:position stacked-layer))


(deftest t92_l301 (is ((fn [p] (= :stack p)) v91_l299)))


(def
 v94_l310
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t95_l314
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v94_l310)))


(def
 v96_l318
 (def
  lm-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width)
   sk/lay-lm
   sk/sketch)))


(def v98_l325 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t99_l326 (is ((fn [marks] (= [:point :line] marks)) v98_l325)))


(def
 v100_l327
 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v102_l331 (first (:groups lm-layer)))


(deftest
 t103_l333
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v102_l331)))


(def
 v105_l345
 (->
  iris
  (sk/lay-point :petal_length :petal_width {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t106_l349
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v105_l345)))


(def
 v107_l352
 (def
  grp-sk
  (->
   iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/lay-lm {:color :species})
   sk/sketch)))


(def
 v108_l357
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t109_l364 (is ((fn [gs] (= 3 (count gs))) v108_l357)))


(def
 v111_l372
 (def
  wave
  {:x (range 30),
   :y
   (mapv (fn* [p1__86133#] (Math/sin (* p1__86133# 0.3))) (range 30))}))


(def v112_l375 (-> wave (sk/lay-line :x :y)))


(deftest
 t113_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v112_l375)))


(def v114_l382 (def wave-sk (-> wave (sk/lay-line :x :y) sk/sketch)))


(def
 v115_l386
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v116_l388
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t117_l392 (is ((fn [m] (= 30 (:n-points m))) v116_l388)))


(def
 v119_l401
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v120_l404 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t121_l407
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v120_l404)))


(def
 v122_l411
 (def
  sales-sk
  (-> sales (sk/lay-value-bar :product :revenue) sk/sketch)))


(def
 v123_l415
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t124_l419 (is ((fn [m] (= 4 (count (:xs m)))) v123_l415)))


(def
 v126_l425
 (def
  flip-sk
  (-> iris (sk/lay-bar :species) (sk/coord :flip) sk/sketch)))


(def v127_l430 (:coord (first (:panels flip-sk))))


(deftest t128_l432 (is ((fn [c] (= :flip c)) v127_l430)))


(def
 v130_l436
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t131_l440
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v130_l436)))


(def
 v133_l450
 (def
  opts-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width)
   (sk/sketch
    {:title "My Custom Title",
     :x-label "Length (cm)",
     :y-label "Width (cm)",
     :width 800,
     :height 300}))))


(def v134_l458 opts-sk)


(deftest
 t135_l460
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v134_l458)))


(def v137_l466 (:layout opts-sk))


(deftest
 t138_l468
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v137_l466)))


(def
 v140_l479
 (def
  final-views
  (->
   iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/lay-lm {:color :species}))))


(def
 v141_l484
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def v142_l486 final-sk)


(deftest
 t143_l488
 (is ((fn [m] (= "Iris Petals" (:title m))) v142_l486)))


(def
 v145_l492
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t146_l497 (is ((fn [ls] (= 2 (count ls))) v145_l492)))


(def v148_l501 (-> final-views (sk/options {:title "Iris Petals"})))


(deftest
 t149_l503
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v148_l501)))


(def
 v151_l512
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay-point {:color :species})
   sk/sketch)))


(def v153_l521 (:grid faceted-sk))


(deftest
 t154_l523
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v153_l521)))


(def v156_l527 (count (:panels faceted-sk)))


(deftest t157_l529 (is ((fn [n] (= 3 n)) v156_l527)))


(def v159_l533 (:panels faceted-sk))


(deftest
 t160_l535
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v159_l533)))


(def v162_l540 (:panels faceted-sk))


(deftest t163_l542 (is ((fn [ps] (every? :x-domain ps)) v162_l540)))


(def
 v165_l549
 (select-keys
  faceted-sk
  [:layout-type :grid :total-width :total-height]))


(deftest
 t166_l551
 (is ((fn [m] (= :facet-grid (:layout-type m))) v165_l549)))


(def v168_l555 (sk/valid-sketch? faceted-sk))


(deftest t169_l557 (is (true? v168_l555)))


(def v171_l567 (sk/valid-sketch? tiny-sk))


(deftest t172_l569 (is (true? v171_l567)))


(def v173_l571 (sk/valid-sketch? iris-sk))


(deftest t174_l573 (is (true? v173_l571)))


(def v175_l575 (sk/valid-sketch? hist-sk))


(deftest t176_l577 (is (true? v175_l575)))


(def v177_l579 (sk/valid-sketch? bar-sk))


(deftest t178_l581 (is (true? v177_l579)))


(def v179_l583 (sk/valid-sketch? lm-sk))


(deftest t180_l585 (is (true? v179_l583)))


(def v181_l587 (sk/valid-sketch? final-sk))


(deftest t182_l589 (is (true? v181_l587)))


(def
 v184_l593
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t185_l595 (is (some? v184_l593)))


(def
 v187_l604
 (type
  (:xs (first (:groups (first (:layers (first (:panels tiny-sk)))))))))


(deftest
 t188_l606
 (is ((fn [t] (not= clojure.lang.PersistentVector t)) v187_l604)))


(def
 v190_l610
 (vec
  (:xs (first (:groups (first (:layers (first (:panels tiny-sk)))))))))


(deftest
 t191_l612
 (is ((fn [v] (and (vector? v) (number? (first v)))) v190_l610)))
