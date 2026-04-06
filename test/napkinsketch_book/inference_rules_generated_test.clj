(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [napkinsketch-book.datasets :as data]
  [clojure.test :refer [deftest is]]))


(def
 v3_l52
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def
 v4_l56
 (def scatter-views (-> five-points (sk/lay-point :x :y))))


(def v6_l62 (sk/plan scatter-views))


(deftest
 t7_l64
 (is
  ((fn
    [pl]
    (and
     (= :single (:layout-type pl))
     (= 1 (count (:panels pl)))
     (= "x" (:x-label pl))
     (= "y" (:y-label pl))
     (nil? (:legend pl))
     (zero? (get-in pl [:layout :legend-w]))
     (let
      [p (first (:panels pl)) g (first (:groups (first (:layers p))))]
      (and
       (= :linear (get-in p [:x-scale :type]))
       (= 1 (count (:groups (first (:layers p)))))
       (=
        (scicloj.napkinsketch.impl.defaults/hex->rgba
         (:default-color (scicloj.napkinsketch.impl.defaults/config)))
        (:color g))))))
   v6_l62)))


(def v9_l78 scatter-views)


(deftest
 t10_l80
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l78)))


(def v12_l108 (-> {:values [1 2 3 4 5 6]} sk/lay-histogram))


(deftest
 t13_l111
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v12_l108)))


(def v15_l115 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l118
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l115)))


(def
 v18_l122
 (->
  {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]}
  sk/lay-point))


(deftest
 t19_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l122)))


(def
 v21_l132
 (->
  data/iris
  (sk/lay-point :petal_length :petal_width {:color :species})))


(deftest
 t22_l135
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v21_l132)))


(def
 v24_l154
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v25_l158
 (def bar-views (-> animals (sk/lay-value-bar :animal :count))))


(def v26_l162 (sk/plan bar-views))


(deftest
 t27_l164
 (is
  ((fn
    [pl]
    (let
     [p (first (:panels pl))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v26_l162)))


(def v28_l168 bar-views)


(deftest
 t29_l170
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v28_l168)))


(def
 v31_l182
 (let
  [pl
   (->
    {:date
     [#inst "2024-01-01T00:00:00.000-00:00"
      #inst "2024-06-01T00:00:00.000-00:00"
      #inst "2024-12-01T00:00:00.000-00:00"],
     :val [10 25 18]}
    (sk/lay-point :date :val)
    sk/plan)
   p
   (first (:panels pl))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t32_l191
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v31_l182)))


(def
 v34_l210
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v35_l216 (sk/plan colored-views))


(deftest
 t36_l218
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v35_l216)))


(def v37_l223 colored-views)


(deftest
 t38_l225
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v37_l223)))


(def
 v40_l236
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v41_l240 (sk/plan fixed-color-views))


(deftest
 t42_l242
 (is
  ((fn
    [pl]
    (and
     (nil? (:legend pl))
     (zero? (get-in pl [:layout :legend-w]))
     (let
      [layer
       (first (:layers (first (:panels pl))))
       c
       (:color (first (:groups layer)))]
      (and
       (= 1 (count (:groups layer)))
       (> (nth c 0) 0.85)
       (< (nth c 1) 0.35)
       (< (nth c 2) 0.3)
       (== 1.0 (nth c 3))))))
   v41_l240)))


(def v43_l252 fixed-color-views)


(deftest
 t44_l254
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v43_l252)))


(def
 v46_l265
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t47_l268
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l265)))


(def
 v49_l295
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y {:color "red"})
    sk/plan)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t50_l301
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v49_l295)))


(def
 v52_l331
 (let
  [pl
   (sk/plan colored-views)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t53_l337
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v52_l331)))


(def
 v55_l351
 (let
  [pl
   (->
    {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
    (sk/lay-point :x :y {:color :val})
    sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl)),
   :color-stops (count (:stops (:legend pl)))}))


(deftest
 t56_l361
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v55_l351)))


(def
 v58_l374
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v59_l379
 (let
  [pl
   (->
    grouped-data
    (sk/lay-point :x :y {:group :g})
    sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t60_l386
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v59_l379)))


(def
 v62_l401
 (->
  grouped-data
  (sk/view :x :y)
  sk/lay-point
  sk/lay-lm))


(deftest
 t63_l406
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v62_l401)))


(def
 v65_l412
 (->
  grouped-data
  (sk/view :x :y {:color :g})
  sk/lay-point
  sk/lay-lm))


(deftest
 t66_l417
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v65_l412)))


(def v68_l444 (def hist-views (-> five-points (sk/view :x))))


(def v69_l448 (sk/plan hist-views))


(deftest
 t70_l450
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v69_l448)))


(def v71_l453 hist-views)


(deftest
 t72_l455
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v71_l453)))


(def v74_l463 (def count-views (-> animals (sk/view :animal))))


(def v75_l467 (sk/plan count-views))


(deftest
 t76_l469
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v75_l467)))


(def v77_l472 count-views)


(deftest
 t78_l474
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v77_l472)))


(def
 v80_l481
 (let
  [pl
   (->
    {:species ["a" "b" "c"], :val [10 20 15]}
    (sk/view :species :val)
    sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  (:mark layer)))


(deftest t81_l487 (is ((fn [m] (= :point m)) v80_l481)))


(def
 v83_l495
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t84_l501
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v83_l495)))


(def
 v86_l511
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest t87_l515 (is ((fn [m] (<= (first (:y-domain m)) 0)) v86_l511)))


(def
 v89_l519
 (let
  [fill-pl
   (->
    {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
    (sk/lay-stacked-bar-fill :x {:color :g})
    sk/plan)
   p
   (first (:panels fill-pl))]
  (:y-domain p)))


(deftest
 t90_l526
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v89_l519)))


(def
 v92_l549
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t93_l554
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v92_l549)))


(def
 v95_l563
 (let
  [pl
   (->
    {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
    (sk/lay-point :x :y)
    (sk/scale :x :log)
    sk/plan)
   p
   (first (:panels pl))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t96_l572
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v95_l563)))


(def
 v98_l581
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t99_l585
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v98_l581)))


(def v101_l592 (def iris data/iris))


(def
 v102_l594
 (let
  [pl
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width)
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t103_l600
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v102_l594)))


(def
 v105_l606
 (let
  [pl (-> five-points (sk/view :x) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t106_l610
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v105_l606)))


(def
 v108_l615
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"})
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t109_l622
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v108_l615)))


(def v111_l633 (:legend (sk/plan colored-views)))


(deftest
 t112_l635
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v111_l633)))


(def v114_l642 (:legend (sk/plan scatter-views)))


(deftest t115_l644 (is (nil? v114_l642)))


(def v117_l648 (:legend (sk/plan fixed-color-views)))


(deftest t118_l650 (is (nil? v117_l648)))


(def
 v120_l654
 (:legend
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val})
   sk/plan)))


(deftest
 t121_l658
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v120_l654)))


(def
 v123_l667
 (:size-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s})
   sk/plan)))


(deftest
 t124_l671
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v123_l667)))


(def v126_l677 (:size-legend (sk/plan scatter-views)))


(deftest t127_l679 (is (nil? v126_l677)))


(def
 v129_l687
 (:alpha-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a})
   sk/plan)))


(deftest
 t130_l691
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (= 5 (count (:entries leg)))))
   v129_l687)))


(def v132_l697 (:alpha-legend (sk/plan scatter-views)))


(deftest t133_l699 (is (nil? v132_l697)))


(def
 v135_l709
 (let
  [bare
   (sk/plan scatter-views)
   full
   (->
    {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
    (sk/lay-point :x :y {:color :g})
    (sk/options {:title "My Plot"})
    sk/plan)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t136_l721
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v135_l709)))


(def
 v138_l735
 (let [pl (sk/plan scatter-views)] (:layout-type pl)))


(deftest t139_l738 (is ((fn [lt] (= :single lt)) v138_l735)))


(def
 v141_l746
 (def
  normal-pl
  (-> animals (sk/lay-value-bar :animal :count) sk/plan)))


(def
 v142_l751
 (def
  flip-pl
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/plan)))


(def
 v143_l757
 (let
  [np (first (:panels normal-pl)) fp (first (:panels flip-pl))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t144_l764
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v143_l757)))


(def
 v145_l769
 (->
  animals
  (sk/lay-value-bar :animal :count)
  (sk/coord :flip)))


(deftest
 t146_l773
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v145_l769)))


(def
 v148_l780
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/coord :flip)
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t149_l787
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v148_l780)))


(def
 v151_l797
 (def
  multi-views
  (->
   five-points
   (sk/view :x :y)
   sk/lay-point
   sk/lay-lm)))


(def v152_l803 (sk/plan multi-views))


(deftest
 t153_l805
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v152_l803)))


(def v154_l808 multi-views)


(deftest
 t155_l810
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v154_l808)))


(def
 v157_l824
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
