(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l48
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l52 (def scatter-views (-> five-points (sk/lay-point :x :y))))


(def v6_l58 (sk/sketch scatter-views))


(deftest
 t7_l60
 (is
  ((fn
    [sk]
    (and
     (= :single (:layout-type sk))
     (= 1 (count (:panels sk)))
     (= "x" (:x-label sk))
     (= "y" (:y-label sk))
     (nil? (:legend sk))
     (zero? (get-in sk [:layout :legend-w]))
     (let
      [p (first (:panels sk)) g (first (:groups (first (:layers p))))]
      (and
       (= :linear (get-in p [:x-scale :type]))
       (= 1 (count (:groups (first (:layers p)))))
       (= [0.2 0.2 0.2 1.0] (:color g))))))
   v6_l58)))


(def v9_l74 scatter-views)


(deftest
 t10_l76
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l74)))


(def
 v12_l107
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v13_l111
 (def bar-views (-> animals (sk/lay-value-bar :animal :count))))


(def v14_l115 (sk/sketch bar-views))


(deftest
 t15_l117
 (is
  ((fn
    [sk]
    (let
     [p (first (:panels sk))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v14_l115)))


(def v16_l121 bar-views)


(deftest
 t17_l123
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v16_l121)))


(def
 v19_l135
 (let
  [sk
   (->
    {:date
     [#inst "2024-01-01T00:00:00.000-00:00"
      #inst "2024-06-01T00:00:00.000-00:00"
      #inst "2024-12-01T00:00:00.000-00:00"],
     :val [10 25 18]}
    (sk/lay-point :date :val)
    sk/sketch)
   p
   (first (:panels sk))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t20_l144
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v19_l135)))


(def
 v22_l163
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v23_l169 (sk/sketch colored-views))


(deftest
 t24_l171
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend sk))
      (= 100 (get-in sk [:layout :legend-w])))))
   v23_l169)))


(def v25_l176 colored-views)


(deftest
 t26_l178
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v25_l176)))


(def
 v28_l189
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v29_l193 (sk/sketch fixed-color-views))


(deftest
 t30_l195
 (is
  ((fn
    [sk]
    (and
     (nil? (:legend sk))
     (zero? (get-in sk [:layout :legend-w]))
     (let
      [layer
       (first (:layers (first (:panels sk))))
       c
       (:color (first (:groups layer)))]
      (and
       (= 1 (count (:groups layer)))
       (> (nth c 0) 0.85)
       (< (nth c 1) 0.35)
       (< (nth c 2) 0.3)
       (== 1.0 (nth c 3))))))
   v29_l193)))


(def v31_l205 fixed-color-views)


(deftest
 t32_l207
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v31_l205)))


(def
 v34_l218
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t35_l221
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v34_l218)))


(def
 v37_l248
 (let
  [sk (-> five-points (sk/lay-point :x :y {:color "red"}) sk/sketch)]
  {:legend (:legend sk),
   :color
   (:color (first (:groups (first (:layers (first (:panels sk)))))))}))


(deftest
 t38_l254
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v37_l248)))


(def
 v40_l284
 (let
  [sk
   (sk/sketch colored-views)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t41_l290
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v40_l284)))


(def
 v43_l304
 (let
  [sk
   (->
    {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
    (sk/lay-point :x :y {:color :val})
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend sk)),
   :color-stops (count (:stops (:legend sk)))}))


(deftest
 t44_l314
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v43_l304)))


(def
 v46_l327
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v47_l332
 (let
  [sk
   (-> grouped-data (sk/lay-point :x :y {:group :g}) sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t48_l339
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v47_l332)))


(def v50_l354 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t51_l359
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v50_l354)))


(def
 v53_l365
 (-> grouped-data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm))


(deftest
 t54_l370
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v53_l365)))


(def v56_l397 (def hist-views (-> five-points (sk/view :x))))


(def v57_l401 (sk/sketch hist-views))


(deftest
 t58_l403
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :bar (:mark layer))))
   v57_l401)))


(def v59_l406 hist-views)


(deftest
 t60_l408
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v59_l406)))


(def v62_l416 (def count-views (-> animals (sk/view :animal))))


(def v63_l420 (sk/sketch count-views))


(deftest
 t64_l422
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :rect (:mark layer))))
   v63_l420)))


(def v65_l425 count-views)


(deftest
 t66_l427
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v65_l425)))


(def
 v68_l434
 (let
  [sk
   (->
    {:species ["a" "b" "c"], :val [10 20 15]}
    (sk/view :species :val)
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  (:mark layer)))


(deftest t69_l440 (is ((fn [m] (= :point m)) v68_l434)))


(def
 v71_l448
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t72_l454
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v71_l448)))


(def
 v74_l464
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  {:y-domain (:y-domain p)}))


(deftest t75_l468 (is ((fn [m] (<= (first (:y-domain m)) 0)) v74_l464)))


(def
 v77_l472
 (let
  [fill-sk
   (->
    {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
    (sk/lay-stacked-bar-fill :x {:color :g})
    sk/sketch)
   p
   (first (:panels fill-sk))]
  (:y-domain p)))


(deftest
 t78_l479
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v77_l472)))


(def
 v80_l502
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t81_l507
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v80_l502)))


(def
 v83_l516
 (let
  [sk
   (->
    {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
    (sk/lay-point :x :y)
    (sk/scale :x :log)
    sk/sketch)
   p
   (first (:panels sk))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t84_l525
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v83_l516)))


(def
 v86_l534
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  (:values (:x-ticks p))))


(deftest
 t87_l538
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v86_l534)))


(def
 v89_l545
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v90_l548
 (let
  [sk (-> iris (sk/lay-point :sepal_length :sepal_width) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t91_l554
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v90_l548)))


(def
 v93_l560
 (let
  [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t94_l564
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v93_l560)))


(def
 v96_l569
 (let
  [sk
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/labs {:x "Length (cm)", :y "Width (cm)"})
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t97_l576
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v96_l569)))


(def v99_l587 (:legend (sk/sketch colored-views)))


(deftest
 t100_l589
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v99_l587)))


(def v102_l596 (:legend (sk/sketch scatter-views)))


(deftest t103_l598 (is (nil? v102_l596)))


(def v105_l602 (:legend (sk/sketch fixed-color-views)))


(deftest t106_l604 (is (nil? v105_l602)))


(def
 v108_l608
 (:legend
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val})
   sk/sketch)))


(deftest
 t109_l612
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v108_l608)))


(def
 v111_l621
 (:size-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s})
   sk/sketch)))


(deftest
 t112_l625
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v111_l621)))


(def v114_l631 (:size-legend (sk/sketch scatter-views)))


(deftest t115_l633 (is (nil? v114_l631)))


(def
 v117_l641
 (:alpha-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a})
   sk/sketch)))


(deftest
 t118_l645
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (= 5 (count (:entries leg)))))
   v117_l641)))


(def v120_l651 (:alpha-legend (sk/sketch scatter-views)))


(deftest t121_l653 (is (nil? v120_l651)))


(def
 v123_l663
 (let
  [bare
   (sk/sketch scatter-views)
   full
   (->
    {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
    (sk/lay-point :x :y {:color :g})
    (sk/labs {:title "My Plot"})
    sk/sketch)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t124_l675
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v123_l663)))


(def v126_l689 (let [sk (sk/sketch scatter-views)] (:layout-type sk)))


(deftest t127_l692 (is ((fn [lt] (= :single lt)) v126_l689)))


(def
 v129_l700
 (def
  normal-sk
  (-> animals (sk/lay-value-bar :animal :count) sk/sketch)))


(def
 v130_l705
 (def
  flip-sk
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/sketch)))


(def
 v131_l711
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t132_l718
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v131_l711)))


(def
 v133_l723
 (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip)))


(deftest
 t134_l727
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v133_l723)))


(def
 v136_l734
 (let
  [sk (-> five-points (sk/lay-point :x :y) (sk/coord :flip) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t137_l741
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v136_l734)))


(def
 v139_l751
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v140_l757 (sk/sketch multi-views))


(deftest
 t141_l759
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v140_l757)))


(def v142_l762 multi-views)


(deftest
 t143_l764
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v142_l762)))


(def
 v145_l778
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> SKETCH[\"Sketch\"]\n  TK --> SKETCH\n  LBL --> SKETCH\n  LEG --> SKETCH\n  SLEG --> SKETCH\n  ALEG --> SKETCH\n  LAYOUT --> SKETCH\n  STATS --> SKETCH\n\n  style VIEWS fill:#e8f5e9\n  style SKETCH fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
