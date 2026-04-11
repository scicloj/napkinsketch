(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l56
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l60 (def scatter-views (-> five-points (sk/lay-point :x :y))))


(def v6_l66 (sk/plan scatter-views))


(deftest
 t7_l68
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
   v6_l66)))


(def v9_l82 scatter-views)


(deftest
 t10_l84
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l82)))


(def v12_l112 (-> {:values [1 2 3 4 5 6]} sk/lay-histogram))


(deftest
 t13_l115
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v12_l112)))


(def v15_l119 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l122
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l119)))


(def
 v18_l126
 (-> {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]} sk/lay-point))


(deftest
 t19_l129
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l126)))


(def
 v21_l136
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t22_l139
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v21_l136)))


(def
 v24_l158
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v25_l162
 (def bar-views (-> animals (sk/lay-value-bar :animal :count))))


(def v26_l166 (sk/plan bar-views))


(deftest
 t27_l168
 (is
  ((fn
    [pl]
    (let
     [p (first (:panels pl))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v26_l166)))


(def v28_l172 bar-views)


(deftest
 t29_l174
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v28_l172)))


(def
 v31_l186
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
 t32_l195
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v31_l186)))


(def
 v34_l214
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v35_l220 (sk/plan colored-views))


(deftest
 t36_l222
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v35_l220)))


(def v37_l227 colored-views)


(deftest
 t38_l229
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v37_l227)))


(def
 v40_l240
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v41_l244 (sk/plan fixed-color-views))


(deftest
 t42_l246
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
   v41_l244)))


(def v43_l256 fixed-color-views)


(deftest
 t44_l258
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v43_l256)))


(def
 v46_l269
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t47_l272
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l269)))


(def
 v49_l299
 (let
  [pl (-> five-points (sk/lay-point :x :y {:color "red"}) sk/plan)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t50_l305
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v49_l299)))


(def
 v52_l335
 (let
  [pl
   (sk/plan colored-views)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t53_l341
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v52_l335)))


(def
 v55_l355
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
 t56_l365
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v55_l355)))


(def
 v58_l385
 (let
  [study
   {:subject [1 1 1 2 2 2 3 3 3],
    :day [1 2 3 1 2 3 1 2 3],
    :score [5 7 6 3 4 5 8 9 7]}
   pl
   (-> study (sk/lay-line :day :score {:color :subject}) sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl))}))


(deftest
 t59_l395
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v58_l385)))


(def
 v61_l400
 (let
  [study
   {:subject [1 1 1 2 2 2 3 3 3],
    :day [1 2 3 1 2 3 1 2 3],
    :score [5 7 6 3 4 5 8 9 7]}
   pl
   (->
    study
    (sk/lay-line
     :day
     :score
     {:color :subject, :color-type :categorical})
    sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend pl)))}))


(deftest
 t62_l411
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v61_l400)))


(def
 v64_l419
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (sk/lay-line :day :score {:color :subject, :color-type :categorical})
  sk/lay-point
  (sk/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t65_l427
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v64_l419)))


(def
 v67_l437
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v68_l442
 (let
  [pl
   (-> grouped-data (sk/lay-point :x :y {:group :g}) sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t69_l449
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v68_l442)))


(def v71_l464 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t72_l469
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v71_l464)))


(def
 v74_l475
 (-> grouped-data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm))


(deftest
 t75_l480
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v74_l475)))


(def v77_l507 (def hist-views (-> five-points (sk/view :x))))


(def v78_l511 (sk/plan hist-views))


(deftest
 t79_l513
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v78_l511)))


(def v80_l516 hist-views)


(deftest
 t81_l518
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v80_l516)))


(def v83_l526 (def count-views (-> animals (sk/view :animal))))


(def v84_l530 (sk/plan count-views))


(deftest
 t85_l532
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v84_l530)))


(def v86_l535 count-views)


(deftest
 t87_l537
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v86_l535)))


(def
 v89_l544
 (let
  [pl
   (->
    {:species ["a" "b" "c"], :val [10 20 15]}
    (sk/view :species :val)
    sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  (:mark layer)))


(deftest t90_l550 (is ((fn [m] (= :point m)) v89_l544)))


(def
 v92_l558
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t93_l564
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v92_l558)))


(def
 v95_l574
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest t96_l578 (is ((fn [m] (<= (first (:y-domain m)) 0)) v95_l574)))


(def
 v98_l582
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
 t99_l589
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v98_l582)))


(def
 v101_l612
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t102_l617
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v101_l612)))


(def
 v104_l626
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
 t105_l635
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v104_l626)))


(def
 v107_l644
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t108_l648
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v107_l644)))


(def
 v110_l655
 (let
  [pl
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t111_l661
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v110_l655)))


(def
 v113_l667
 (let
  [pl (-> five-points (sk/view :x) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t114_l671
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v113_l667)))


(def
 v116_l676
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"})
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t117_l683
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v116_l676)))


(def v119_l694 (:legend (sk/plan colored-views)))


(deftest
 t120_l696
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v119_l694)))


(def v122_l703 (:legend (sk/plan scatter-views)))


(deftest t123_l705 (is (nil? v122_l703)))


(def v125_l709 (:legend (sk/plan fixed-color-views)))


(deftest t126_l711 (is (nil? v125_l709)))


(def
 v128_l715
 (:legend
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val})
   sk/plan)))


(deftest
 t129_l719
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v128_l715)))


(def
 v131_l728
 (:size-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s})
   sk/plan)))


(deftest
 t132_l732
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v131_l728)))


(def v134_l738 (:size-legend (sk/plan scatter-views)))


(deftest t135_l740 (is (nil? v134_l738)))


(def
 v137_l749
 (:alpha-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a})
   sk/plan)))


(deftest
 t138_l753
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v137_l749)))


(def v140_l759 (:alpha-legend (sk/plan scatter-views)))


(deftest t141_l761 (is (nil? v140_l759)))


(def
 v143_l771
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
 t144_l783
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v143_l771)))


(def v146_l797 (let [pl (sk/plan scatter-views)] (:layout-type pl)))


(deftest t147_l800 (is ((fn [lt] (= :single lt)) v146_l797)))


(def
 v149_l808
 (def normal-pl (-> animals (sk/lay-value-bar :animal :count) sk/plan)))


(def
 v150_l813
 (def
  flip-pl
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/plan)))


(def
 v151_l819
 (let
  [np (first (:panels normal-pl)) fp (first (:panels flip-pl))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t152_l826
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v151_l819)))


(def
 v153_l831
 (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip)))


(deftest
 t154_l835
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v153_l831)))


(def
 v156_l842
 (let
  [pl (-> five-points (sk/lay-point :x :y) (sk/coord :flip) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t157_l849
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v156_l842)))


(def
 v159_l859
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v160_l865 (sk/plan multi-views))


(deftest
 t161_l867
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v160_l865)))


(def v162_l870 multi-views)


(deftest
 t163_l872
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v162_l870)))


(def
 v165_l886
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
