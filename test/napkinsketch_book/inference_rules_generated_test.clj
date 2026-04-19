(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l62
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l66 (def scatter-views (-> five-points (sk/lay-point :x :y))))


(def v6_l72 (sk/plan scatter-views))


(deftest
 t7_l74
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
   v6_l72)))


(def v9_l88 scatter-views)


(deftest
 t10_l90
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l88)))


(def v12_l118 (-> {:values [1 2 3 4 5 6]} sk/lay-histogram))


(deftest
 t13_l121
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v12_l118)))


(def v15_l125 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} sk/lay-point))


(deftest
 t16_l128
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v15_l125)))


(def
 v18_l132
 (-> {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]} sk/lay-point))


(deftest
 t19_l135
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l132)))


(def
 v21_l142
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t22_l145
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v21_l142)))


(def
 v24_l164
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v25_l168
 (def bar-views (-> animals (sk/lay-value-bar :animal :count))))


(def v26_l172 (sk/plan bar-views))


(deftest
 t27_l174
 (is
  ((fn
    [pl]
    (let
     [p (first (:panels pl))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v26_l172)))


(def v28_l178 bar-views)


(deftest
 t29_l180
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v28_l178)))


(def
 v31_l192
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
 t32_l201
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v31_l192)))


(def
 v34_l220
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v35_l226 (sk/plan colored-views))


(deftest
 t36_l228
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v35_l226)))


(def v37_l233 colored-views)


(deftest
 t38_l235
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v37_l233)))


(def
 v40_l246
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v41_l250 (sk/plan fixed-color-views))


(deftest
 t42_l252
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
   v41_l250)))


(def v43_l262 fixed-color-views)


(deftest
 t44_l264
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v43_l262)))


(def
 v46_l275
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t47_l278
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l275)))


(def
 v49_l305
 (let
  [pl (-> five-points (sk/lay-point :x :y {:color "red"}) sk/plan)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t50_l311
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v49_l305)))


(def
 v52_l341
 (let
  [pl
   (sk/plan colored-views)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t53_l347
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v52_l341)))


(def
 v55_l361
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
 t56_l371
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v55_l361)))


(def
 v58_l391
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
 t59_l401
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v58_l391)))


(def
 v61_l406
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
 t62_l417
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v61_l406)))


(def
 v64_l425
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (sk/lay-line :day :score {:color :subject, :color-type :categorical})
  sk/lay-point
  (sk/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t65_l433
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v64_l425)))


(def
 v67_l443
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v68_l448
 (let
  [pl
   (-> grouped-data (sk/lay-point :x :y {:group :g}) sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t69_l455
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v68_l448)))


(def v71_l470 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t72_l475
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v71_l470)))


(def
 v74_l481
 (-> grouped-data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm))


(deftest
 t75_l486
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v74_l481)))


(def v77_l530 (def hist-views (-> five-points (sk/view :x))))


(def v78_l534 (sk/plan hist-views))


(deftest
 t79_l536
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v78_l534)))


(def v80_l539 hist-views)


(deftest
 t81_l541
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v80_l539)))


(def
 v83_l549
 (def
  temporal-hist-sketch
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"
     #inst "2024-04-01T00:00:00.000-00:00"
     #inst "2024-05-01T00:00:00.000-00:00"]}
   (sk/view :date))))


(def v84_l554 temporal-hist-sketch)


(deftest
 t85_l556
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v84_l554)))


(def v86_l558 (kind/pprint temporal-hist-sketch))


(deftest
 t87_l560
 (is
  ((fn
    [sk]
    (= :bar (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v86_l558)))


(def v89_l565 (def count-views (-> animals (sk/view :animal))))


(def v90_l569 (sk/plan count-views))


(deftest
 t91_l571
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v90_l569)))


(def v92_l574 count-views)


(deftest
 t93_l576
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v92_l574)))


(def v95_l584 (def num-num-sketch (-> five-points (sk/view :x :y))))


(def v96_l587 num-num-sketch)


(deftest
 t97_l589
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v96_l587)))


(def v98_l591 (kind/pprint num-num-sketch))


(deftest
 t99_l593
 (is
  ((fn
    [sk]
    (=
     :point
     (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v98_l591)))


(def
 v101_l599
 (def
  ts-line-sketch
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/view :date :val))))


(def v102_l604 ts-line-sketch)


(deftest
 t103_l606
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v102_l604)))


(def v104_l608 (kind/pprint ts-line-sketch))


(deftest
 t105_l610
 (is
  ((fn
    [sk]
    (= :line (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v104_l608)))


(def
 v107_l616
 (def
  boxplot-sketch
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (sk/view :species :val))))


(def v108_l621 boxplot-sketch)


(deftest
 t109_l623
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v108_l621)))


(def v110_l625 (kind/pprint boxplot-sketch))


(deftest
 t111_l627
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels (sk/plan sk)))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v110_l625)))


(def
 v113_l635
 (def
  horizontal-boxplot-sketch
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (sk/view :val :species))))


(def v114_l640 horizontal-boxplot-sketch)


(deftest
 t115_l642
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v114_l640)))


(def v116_l644 (kind/pprint horizontal-boxplot-sketch))


(deftest
 t117_l646
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels (sk/plan sk)))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v116_l644)))


(def
 v119_l657
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t120_l663
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v119_l657)))


(def
 v122_l673
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest
 t123_l677
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v122_l673)))


(def
 v125_l681
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
 t126_l688
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v125_l681)))


(def
 v128_l711
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t129_l716
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v128_l711)))


(def
 v131_l725
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
 t132_l734
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v131_l725)))


(def
 v134_l743
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t135_l747
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v134_l743)))


(def
 v137_l754
 (let
  [pl
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t138_l760
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v137_l754)))


(def
 v140_l766
 (let
  [pl (-> five-points (sk/view :x) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t141_l770
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v140_l766)))


(def
 v143_l775
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"})
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t144_l782
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v143_l775)))


(def v146_l793 (:legend (sk/plan colored-views)))


(deftest
 t147_l795
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v146_l793)))


(def v149_l802 (:legend (sk/plan scatter-views)))


(deftest t150_l804 (is (nil? v149_l802)))


(def v152_l808 (:legend (sk/plan fixed-color-views)))


(deftest t153_l810 (is (nil? v152_l808)))


(def
 v155_l814
 (:legend
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val})
   sk/plan)))


(deftest
 t156_l818
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v155_l814)))


(def
 v158_l827
 (:size-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s})
   sk/plan)))


(deftest
 t159_l831
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v158_l827)))


(def v161_l837 (:size-legend (sk/plan scatter-views)))


(deftest t162_l839 (is (nil? v161_l837)))


(def
 v164_l848
 (:alpha-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a})
   sk/plan)))


(deftest
 t165_l852
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v164_l848)))


(def v167_l858 (:alpha-legend (sk/plan scatter-views)))


(deftest t168_l860 (is (nil? v167_l858)))


(def
 v170_l870
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
 t171_l882
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v170_l870)))


(def v173_l896 (let [pl (sk/plan scatter-views)] (:layout-type pl)))


(deftest t174_l899 (is ((fn [lt] (= :single lt)) v173_l896)))


(def
 v176_l907
 (def normal-pl (-> animals (sk/lay-value-bar :animal :count) sk/plan)))


(def
 v177_l912
 (def
  flip-pl
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/plan)))


(def
 v178_l918
 (let
  [np (first (:panels normal-pl)) fp (first (:panels flip-pl))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t179_l925
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v178_l918)))


(def
 v180_l930
 (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip)))


(deftest
 t181_l934
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v180_l930)))


(def
 v183_l941
 (let
  [pl (-> five-points (sk/lay-point :x :y) (sk/coord :flip) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t184_l948
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v183_l941)))


(def
 v186_l962
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v187_l968 (sk/plan multi-views))


(deftest
 t188_l970
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v187_l968)))


(def v189_l973 multi-views)


(deftest
 t190_l975
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v189_l973)))


(def
 v192_l989
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
