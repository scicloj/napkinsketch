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


(def v6_l72 scatter-views)


(deftest
 t7_l74
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l72)))


(def v9_l78 (sk/plan scatter-views))


(deftest
 t10_l80
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
   v9_l78)))


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


(def v26_l172 bar-views)


(deftest
 t27_l174
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v26_l172)))


(def v29_l178 (sk/plan bar-views))


(deftest
 t30_l180
 (is
  ((fn
    [pl]
    (let
     [p (first (:panels pl))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v29_l178)))


(def
 v32_l194
 (def
  temporal-sketch
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-06-01T00:00:00.000-00:00"
     #inst "2024-12-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/lay-point :date :val))))


(def v33_l199 temporal-sketch)


(def
 v34_l201
 (let
  [p (first (:panels (sk/plan temporal-sketch)))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t35_l206
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v34_l201)))


(def
 v37_l225
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v38_l231 colored-views)


(deftest
 t39_l233
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v38_l231)))


(def v41_l237 (sk/plan colored-views))


(deftest
 t42_l239
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v41_l237)))


(def
 v44_l253
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v45_l257 fixed-color-views)


(deftest
 t46_l259
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v45_l257)))


(def v48_l263 (sk/plan fixed-color-views))


(deftest
 t49_l265
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
   v48_l263)))


(def
 v51_l284
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t52_l287
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v51_l284)))


(def
 v54_l314
 (def
  red-color-sketch
  (-> five-points (sk/lay-point :x :y {:color "red"}))))


(def v55_l318 red-color-sketch)


(def
 v56_l320
 (let
  [pl (sk/plan red-color-sketch)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t57_l324
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v56_l320)))


(def v59_l354 colored-views)


(def
 v60_l356
 (let
  [pl
   (sk/plan colored-views)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t61_l362
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v60_l356)))


(def
 v63_l376
 (def
  numeric-color-sketch
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
   (sk/lay-point :x :y {:color :val}))))


(def v64_l382 numeric-color-sketch)


(def
 v65_l384
 (let
  [pl
   (sk/plan numeric-color-sketch)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl)),
   :color-stops (count (:stops (:legend pl)))}))


(deftest
 t66_l390
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v65_l384)))


(def
 v68_l408
 (def
  study-data
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}))


(def
 v70_l415
 (def
  study-continuous-sketch
  (-> study-data (sk/lay-line :day :score {:color :subject}))))


(def v71_l419 study-continuous-sketch)


(def
 v72_l421
 (let
  [pl
   (sk/plan study-continuous-sketch)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl))}))


(deftest
 t73_l426
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v72_l421)))


(def
 v75_l431
 (def
  study-categorical-sketch
  (->
   study-data
   (sk/lay-line
    :day
    :score
    {:color :subject, :color-type :categorical}))))


(def v76_l436 study-categorical-sketch)


(def
 v77_l438
 (let
  [pl
   (sk/plan study-categorical-sketch)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend pl)))}))


(deftest
 t78_l443
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v77_l438)))


(def
 v80_l451
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (sk/lay-line :day :score {:color :subject, :color-type :categorical})
  sk/lay-point
  (sk/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t81_l459
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v80_l451)))


(def
 v83_l469
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v84_l474
 (def
  explicit-group-sketch
  (-> grouped-data (sk/lay-point :x :y {:group :g}))))


(def v85_l478 explicit-group-sketch)


(def
 v86_l480
 (let
  [pl
   (sk/plan explicit-group-sketch)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t87_l485
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v86_l480)))


(def v89_l500 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t90_l505
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v89_l500)))


(def
 v92_l511
 (-> grouped-data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm))


(deftest
 t93_l516
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v92_l511)))


(def v95_l560 (def hist-views (-> five-points (sk/view :x))))


(def v96_l564 hist-views)


(deftest
 t97_l566
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v96_l564)))


(def v99_l570 (sk/plan hist-views))


(deftest
 t100_l572
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v99_l570)))


(def
 v102_l581
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


(def v103_l586 temporal-hist-sketch)


(deftest
 t104_l588
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v103_l586)))


(def v106_l592 (sk/plan temporal-hist-sketch))


(deftest
 t107_l594
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v106_l592)))


(def v109_l599 (def count-views (-> animals (sk/view :animal))))


(def v110_l603 count-views)


(deftest
 t111_l605
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v110_l603)))


(def v113_l609 (sk/plan count-views))


(deftest
 t114_l611
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v113_l609)))


(def v116_l620 (def num-num-sketch (-> five-points (sk/view :x :y))))


(def v117_l623 num-num-sketch)


(deftest
 t118_l625
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v117_l623)))


(def v120_l629 (sk/plan num-num-sketch))


(deftest
 t121_l631
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :point (:mark layer))))
   v120_l629)))


(def
 v123_l637
 (def
  ts-line-sketch
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/view :date :val))))


(def v124_l642 ts-line-sketch)


(deftest
 t125_l644
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v124_l642)))


(def v127_l648 (sk/plan ts-line-sketch))


(deftest
 t128_l650
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :line (:mark layer))))
   v127_l648)))


(def
 v130_l656
 (def
  boxplot-sketch
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (sk/view :species :val))))


(def v131_l661 boxplot-sketch)


(deftest
 t132_l663
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v131_l661)))


(def v134_l667 (sk/plan boxplot-sketch))


(deftest
 t135_l669
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v134_l667)))


(def
 v137_l676
 (def
  horizontal-boxplot-sketch
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (sk/view :val :species))))


(def v138_l681 horizontal-boxplot-sketch)


(deftest
 t139_l683
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v138_l681)))


(def v141_l687 (sk/plan horizontal-boxplot-sketch))


(deftest
 t142_l689
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v141_l687)))


(def v144_l699 scatter-views)


(def
 v145_l701
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t146_l707
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v145_l701)))


(def v148_l717 bar-views)


(def
 v149_l719
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest
 t150_l723
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v149_l719)))


(def
 v152_l727
 (def
  fill-sketch
  (->
   {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
   (sk/lay-stacked-bar-fill :x {:color :g}))))


(def v153_l732 fill-sketch)


(def v154_l734 (:y-domain (first (:panels (sk/plan fill-sketch)))))


(deftest
 t155_l736
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v154_l734)))


(def v157_l759 scatter-views)


(def
 v158_l761
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t159_l766
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v158_l761)))


(def
 v161_l775
 (def
  log-scale-sketch
  (->
   {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
   (sk/lay-point :x :y)
   (sk/scale :x :log))))


(def v162_l781 log-scale-sketch)


(def
 v163_l783
 (let
  [pl (sk/plan log-scale-sketch) p (first (:panels pl))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t164_l788
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v163_l783)))


(def v166_l797 bar-views)


(def
 v167_l799
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t168_l803
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v167_l799)))


(def
 v170_l810
 (def
  iris-label-sketch
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v171_l814 iris-label-sketch)


(def
 v172_l816
 (let
  [pl (sk/plan iris-label-sketch)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t173_l820
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v172_l816)))


(def v175_l826 (def x-only-sketch (-> five-points (sk/view :x))))


(def v176_l829 x-only-sketch)


(def
 v177_l831
 (let
  [pl (sk/plan x-only-sketch)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t178_l835
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v177_l831)))


(def
 v180_l840
 (def
  explicit-label-sketch
  (->
   five-points
   (sk/lay-point :x :y)
   (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"}))))


(def v181_l845 explicit-label-sketch)


(def
 v182_l847
 (let
  [pl (sk/plan explicit-label-sketch)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t183_l851
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v182_l847)))


(def v185_l862 colored-views)


(def v186_l864 (:legend (sk/plan colored-views)))


(deftest
 t187_l866
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v186_l864)))


(def v189_l873 scatter-views)


(def v190_l875 (:legend (sk/plan scatter-views)))


(deftest t191_l877 (is (nil? v190_l875)))


(def v193_l881 fixed-color-views)


(def v194_l883 (:legend (sk/plan fixed-color-views)))


(deftest t195_l885 (is (nil? v194_l883)))


(def
 v197_l889
 (def
  continuous-color-sketch
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val}))))


(def v198_l893 continuous-color-sketch)


(def v199_l895 (:legend (sk/plan continuous-color-sketch)))


(deftest
 t200_l897
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v199_l895)))


(def
 v202_l906
 (def
  size-legend-sketch
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s}))))


(def v203_l910 size-legend-sketch)


(def v204_l912 (:size-legend (sk/plan size-legend-sketch)))


(deftest
 t205_l914
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v204_l912)))


(def v207_l920 scatter-views)


(def v208_l922 (:size-legend (sk/plan scatter-views)))


(deftest t209_l924 (is (nil? v208_l922)))


(def
 v211_l933
 (def
  alpha-legend-sketch
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a}))))


(def v212_l937 alpha-legend-sketch)


(def v213_l939 (:alpha-legend (sk/plan alpha-legend-sketch)))


(deftest
 t214_l941
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v213_l939)))


(def v216_l947 scatter-views)


(def v217_l949 (:alpha-legend (sk/plan scatter-views)))


(deftest t218_l951 (is (nil? v217_l949)))


(def v220_l961 scatter-views)


(def
 v221_l963
 (def
  full-layout-sketch
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g})
   (sk/options {:title "My Plot"}))))


(def v222_l970 full-layout-sketch)


(def
 v223_l972
 (let
  [bare (sk/plan scatter-views) full (sk/plan full-layout-sketch)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t224_l979
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v223_l972)))


(def v226_l993 scatter-views)


(def v227_l995 (:layout-type (sk/plan scatter-views)))


(deftest t228_l997 (is ((fn [lt] (= :single lt)) v227_l995)))


(def
 v230_l1005
 (def normal-sketch (-> animals (sk/lay-value-bar :animal :count))))


(def v231_l1009 normal-sketch)


(def
 v232_l1011
 (def
  flip-sketch
  (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip))))


(def v233_l1016 flip-sketch)


(deftest
 t234_l1018
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v233_l1016)))


(def
 v235_l1020
 (let
  [np
   (first (:panels (sk/plan normal-sketch)))
   fp
   (first (:panels (sk/plan flip-sketch)))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t236_l1027
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v235_l1020)))


(def
 v238_l1037
 (def
  flipped-labels-sketch
  (-> five-points (sk/lay-point :x :y) (sk/coord :flip))))


(def v239_l1042 flipped-labels-sketch)


(def
 v240_l1044
 (let
  [pl (sk/plan flipped-labels-sketch)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t241_l1048
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m))))
   v240_l1044)))


(def
 v243_l1062
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v244_l1068 multi-views)


(deftest
 t245_l1070
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v244_l1068)))


(def v247_l1076 (sk/plan multi-views))


(deftest
 t248_l1078
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v247_l1076)))


(def
 v250_l1091
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
