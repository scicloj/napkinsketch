(ns
 plotje-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l36
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l40 (def scatter-pose (-> five-points (pj/lay-point :x :y))))


(def v6_l46 scatter-pose)


(deftest
 t7_l48
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v6_l46)))


(def v9_l52 (pj/plan scatter-pose))


(deftest
 t10_l54
 (is
  ((fn
    [plan]
    (and
     (= :single (:layout-type plan))
     (= 1 (count (:panels plan)))
     (= "x" (:x-label plan))
     (= "y" (:y-label plan))
     (nil? (:legend plan))
     (zero? (get-in plan [:layout :legend-w]))
     (let
      [p
       (first (:panels plan))
       g
       (first (:groups (first (:layers p))))]
      (and
       (= :linear (get-in p [:x-scale :type]))
       (= 1 (count (:groups (first (:layers p)))))
       (=
        (scicloj.plotje.impl.defaults/hex->rgba
         (:default-color (scicloj.plotje.impl.defaults/config)))
        (:color g))))))
   v9_l52)))


(def v12_l122 (-> {:values [1 2 3 4 5 6]} pj/lay-histogram))


(deftest
 t13_l125
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v12_l122)))


(def v15_l129 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t16_l132
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v15_l129)))


(def
 v18_l136
 (-> {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]} pj/lay-point))


(deftest
 t19_l139
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l136)))


(def
 v21_l151
 (def
  two-col-pose
  (pj/pose {:x [1.0 2.0 3.0 4.0 5.0], :y [1.0 4.0 9.0 16.0 25.0]})))


(def v22_l155 two-col-pose)


(deftest
 t23_l157
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v22_l155)))


(def
 v25_l161
 (-> two-col-pose (select-keys [:mapping :layers]) kind/pprint))


(deftest
 t26_l163
 (is
  ((fn
    [pose]
    (and (= {:x :x, :y :y} (:mapping pose)) (empty? (:layers pose))))
   v25_l161)))


(def
 v28_l180
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t29_l183
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v28_l180)))


(def
 v31_l202
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v32_l206
 (def bar-pose (-> animals (pj/lay-value-bar :animal :count))))


(def v33_l210 bar-pose)


(deftest
 t34_l212
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v33_l210)))


(def v36_l216 (pj/plan bar-pose))


(deftest
 t37_l218
 (is
  ((fn
    [plan]
    (let
     [p (first (:panels plan))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v36_l216)))


(def
 v39_l232
 (def
  temporal-pose
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-06-01T00:00:00.000-00:00"
     #inst "2024-12-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/lay-point :date :val))))


(def v40_l237 temporal-pose)


(def
 v41_l239
 (let
  [p (first (:panels (pj/plan temporal-pose)))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t42_l244
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v41_l239)))


(def
 v44_l262
 (def
  hour-bar-pose
  (->
   {:hour [9 10 11 12], :count [5 8 12 7]}
   (pj/lay-value-bar :hour :count {:x-type :categorical}))))


(def v45_l266 hour-bar-pose)


(deftest
 t46_l268
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v45_l266)))


(def v47_l270 (:x-domain (first (:panels (pj/plan hour-bar-pose)))))


(deftest t48_l272 (is ((fn [d] (= ["9" "10" "11" "12"] d)) v47_l270)))


(def
 v50_l290
 (def
  colored-pose
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g}))))


(def v51_l296 colored-pose)


(deftest
 t52_l298
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v51_l296)))


(def v54_l302 (pj/plan colored-pose))


(deftest
 t55_l304
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend plan))
      (= 100 (get-in plan [:layout :legend-w])))))
   v54_l302)))


(def
 v57_l318
 (def
  fixed-color-pose
  (-> five-points (pj/lay-point :x :y {:color "#E74C3C"}))))


(def v58_l322 fixed-color-pose)


(deftest
 t59_l324
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v58_l322)))


(def v61_l328 (pj/plan fixed-color-pose))


(deftest
 t62_l330
 (is
  ((fn
    [plan]
    (and
     (nil? (:legend plan))
     (zero? (get-in plan [:layout :legend-w]))
     (let
      [layer
       (first (:layers (first (:panels plan))))
       c
       (:color (first (:groups layer)))]
      (and
       (= 1 (count (:groups layer)))
       (> (nth c 0) 0.85)
       (< (nth c 1) 0.35)
       (< (nth c 2) 0.3)
       (== 1.0 (nth c 3))))))
   v61_l328)))


(def
 v64_l349
 (-> five-points (pj/lay-point :x :y {:color "steelblue"})))


(deftest
 t65_l352
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v64_l349)))


(def
 v67_l379
 (def
  red-color-pose
  (-> five-points (pj/lay-point :x :y {:color "red"}))))


(def v68_l383 red-color-pose)


(def
 v69_l385
 (let
  [plan (pj/plan red-color-pose)]
  {:legend (:legend plan),
   :color
   (:color
    (first (:groups (first (:layers (first (:panels plan)))))))}))


(deftest
 t70_l389
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v69_l385)))


(def v72_l419 colored-pose)


(def
 v73_l421
 (let
  [plan
   (pj/plan colored-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend plan))}))


(deftest
 t74_l427
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v73_l421)))


(def
 v76_l441
 (def
  numeric-color-pose
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
   (pj/lay-point :x :y {:color :val}))))


(def v77_l447 numeric-color-pose)


(def
 v78_l449
 (let
  [plan
   (pj/plan numeric-color-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend plan)),
   :color-stops (count (:stops (:legend plan)))}))


(deftest
 t79_l455
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v78_l449)))


(def
 v81_l473
 (def
  study-data
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}))


(def
 v83_l480
 (def
  study-continuous-pose
  (-> study-data (pj/lay-line :day :score {:color :subject}))))


(def v84_l484 study-continuous-pose)


(def
 v85_l486
 (let
  [plan
   (pj/plan study-continuous-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend plan))}))


(deftest
 t86_l491
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v85_l486)))


(def
 v88_l496
 (def
  study-categorical-pose
  (->
   study-data
   (pj/lay-line
    :day
    :score
    {:color :subject, :color-type :categorical}))))


(def v89_l501 study-categorical-pose)


(def
 v90_l503
 (let
  [plan
   (pj/plan study-categorical-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend plan)))}))


(deftest
 t91_l508
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v90_l503)))


(def
 v93_l516
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (pj/lay-line :day :score {:color :subject, :color-type :categorical})
  pj/lay-point
  (pj/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t94_l524
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v93_l516)))


(def
 v96_l534
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v97_l539
 (def
  explicit-group-pose
  (-> grouped-data (pj/lay-point :x :y {:group :g}))))


(def v98_l543 explicit-group-pose)


(def
 v99_l545
 (let
  [plan
   (pj/plan explicit-group-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend plan))}))


(deftest
 t100_l550
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v99_l545)))


(def
 v102_l565
 (->
  grouped-data
  (pj/pose :x :y)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t103_l570
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v102_l565)))


(def
 v105_l576
 (->
  grouped-data
  (pj/pose :x :y {:color :g})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t106_l581
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v105_l576)))


(def v108_l625 (def hist-pose (-> five-points (pj/pose :x))))


(def v109_l629 hist-pose)


(deftest
 t110_l631
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v109_l629)))


(def v112_l635 (pj/plan hist-pose))


(deftest
 t113_l637
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :bar (:mark layer))))
   v112_l635)))


(def
 v115_l646
 (def
  temporal-hist-pose
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"
     #inst "2024-04-01T00:00:00.000-00:00"
     #inst "2024-05-01T00:00:00.000-00:00"]}
   (pj/pose :date))))


(def v116_l651 temporal-hist-pose)


(deftest
 t117_l653
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v116_l651)))


(def v119_l657 (pj/plan temporal-hist-pose))


(deftest
 t120_l659
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :bar (:mark layer))))
   v119_l657)))


(def v122_l664 (def count-pose (-> animals (pj/pose :animal))))


(def v123_l668 count-pose)


(deftest
 t124_l670
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v123_l668)))


(def v126_l674 (pj/plan count-pose))


(deftest
 t127_l676
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :rect (:mark layer))))
   v126_l674)))


(def v129_l685 (def num-num-pose (-> five-points (pj/pose :x :y))))


(def v130_l688 num-num-pose)


(deftest
 t131_l690
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v130_l688)))


(def v133_l694 (pj/plan num-num-pose))


(deftest
 t134_l696
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :point (:mark layer))))
   v133_l694)))


(def
 v136_l702
 (def
  ts-line-pose
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/pose :date :val))))


(def v137_l707 ts-line-pose)


(deftest
 t138_l709
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v137_l707)))


(def v140_l713 (pj/plan ts-line-pose))


(deftest
 t141_l715
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :line (:mark layer))))
   v140_l713)))


(def
 v143_l721
 (def
  boxplot-pose
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (pj/pose :species :val))))


(def v144_l726 boxplot-pose)


(deftest
 t145_l728
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v144_l726)))


(def v147_l732 (pj/plan boxplot-pose))


(deftest
 t148_l734
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v147_l732)))


(def
 v150_l741
 (def
  horizontal-boxplot-pose
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (pj/pose :val :species))))


(def v151_l746 horizontal-boxplot-pose)


(deftest
 t152_l748
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v151_l746)))


(def v154_l752 (pj/plan horizontal-boxplot-pose))


(deftest
 t155_l754
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v154_l752)))


(def v157_l764 scatter-pose)


(def
 v158_l766
 (let
  [plan (pj/plan scatter-pose) p (first (:panels plan))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t159_l772
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v158_l766)))


(def v161_l782 bar-pose)


(def
 v162_l784
 (let
  [plan (pj/plan bar-pose) p (first (:panels plan))]
  {:y-domain (:y-domain p)}))


(deftest
 t163_l788
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v162_l784)))


(def
 v165_l792
 (def
  fill-pose
  (->
   {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
   (pj/lay-bar :x {:position :fill, :color :g}))))


(def v166_l797 fill-pose)


(def v167_l799 (:y-domain (first (:panels (pj/plan fill-pose)))))


(deftest
 t168_l801
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v167_l799)))


(def v170_l824 scatter-pose)


(def
 v171_l826
 (let
  [plan (pj/plan scatter-pose) p (first (:panels plan))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t172_l831
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v171_l826)))


(def
 v174_l840
 (def
  log-scale-pose
  (->
   {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
   (pj/lay-point :x :y)
   (pj/scale :x :log))))


(def v175_l846 log-scale-pose)


(def
 v176_l848
 (let
  [plan (pj/plan log-scale-pose) p (first (:panels plan))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t177_l853
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v176_l848)))


(def v179_l862 bar-pose)


(def
 v180_l864
 (let
  [plan (pj/plan bar-pose) p (first (:panels plan))]
  (:values (:x-ticks p))))


(deftest
 t181_l868
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v180_l864)))


(def
 v183_l875
 (def
  iris-label-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width))))


(def v184_l879 iris-label-pose)


(def
 v185_l881
 (let
  [plan (pj/plan iris-label-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t186_l885
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v185_l881)))


(def v188_l891 (def x-only-pose (-> five-points (pj/pose :x))))


(def v189_l894 x-only-pose)


(def
 v190_l896
 (let
  [plan (pj/plan x-only-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t191_l900
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v190_l896)))


(def
 v193_l905
 (def
  explicit-label-pose
  (->
   five-points
   (pj/lay-point :x :y)
   (pj/options {:x-label "Length (cm)", :y-label "Width (cm)"}))))


(def v194_l910 explicit-label-pose)


(def
 v195_l912
 (let
  [plan (pj/plan explicit-label-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t196_l916
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v195_l912)))


(def v198_l927 colored-pose)


(def v199_l929 (:legend (pj/plan colored-pose)))


(deftest
 t200_l931
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v199_l929)))


(def v202_l938 scatter-pose)


(def v203_l940 (:legend (pj/plan scatter-pose)))


(deftest t204_l942 (is (nil? v203_l940)))


(def v206_l946 fixed-color-pose)


(def v207_l948 (:legend (pj/plan fixed-color-pose)))


(deftest t208_l950 (is (nil? v207_l948)))


(def
 v210_l954
 (def
  continuous-color-pose
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (pj/lay-point :x :y {:color :val}))))


(def v211_l958 continuous-color-pose)


(def v212_l960 (:legend (pj/plan continuous-color-pose)))


(deftest
 t213_l962
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v212_l960)))


(def
 v215_l971
 (def
  size-legend-pose
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (pj/lay-point :x :y {:size :s}))))


(def v216_l975 size-legend-pose)


(def v217_l977 (:size-legend (pj/plan size-legend-pose)))


(deftest
 t218_l979
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v217_l977)))


(def v220_l985 scatter-pose)


(def v221_l987 (:size-legend (pj/plan scatter-pose)))


(deftest t222_l989 (is (nil? v221_l987)))


(def
 v224_l998
 (def
  alpha-legend-pose
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (pj/lay-point :x :y {:alpha :a}))))


(def v225_l1002 alpha-legend-pose)


(def v226_l1004 (:alpha-legend (pj/plan alpha-legend-pose)))


(deftest
 t227_l1006
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v226_l1004)))


(def v229_l1012 scatter-pose)


(def v230_l1014 (:alpha-legend (pj/plan scatter-pose)))


(deftest t231_l1016 (is (nil? v230_l1014)))


(def v233_l1026 scatter-pose)


(def
 v234_l1028
 (def
  full-layout-pose
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g})
   (pj/options {:title "My Plot"}))))


(def v235_l1035 full-layout-pose)


(def
 v236_l1037
 (let
  [bare (pj/plan scatter-pose) full (pj/plan full-layout-pose)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t237_l1044
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v236_l1037)))


(def v239_l1058 scatter-pose)


(def v240_l1060 (:layout-type (pj/plan scatter-pose)))


(deftest t241_l1062 (is ((fn [lt] (= :single lt)) v240_l1060)))


(def
 v243_l1070
 (def normal-pose (-> animals (pj/lay-value-bar :animal :count))))


(def v244_l1074 normal-pose)


(def
 v245_l1076
 (def
  flip-pose
  (-> animals (pj/lay-value-bar :animal :count) (pj/coord :flip))))


(def v246_l1081 flip-pose)


(deftest
 t247_l1083
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v246_l1081)))


(def
 v248_l1085
 (let
  [np
   (first (:panels (pj/plan normal-pose)))
   fp
   (first (:panels (pj/plan flip-pose)))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t249_l1092
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v248_l1085)))


(def
 v251_l1102
 (def
  flipped-labels-pose
  (-> five-points (pj/lay-point :x :y) (pj/coord :flip))))


(def v252_l1107 flipped-labels-pose)


(def
 v253_l1109
 (let
  [plan (pj/plan flipped-labels-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t254_l1113
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m))))
   v253_l1109)))


(def
 v256_l1127
 (def
  multi-pose
  (->
   five-points
   (pj/pose :x :y)
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v257_l1133 multi-pose)


(deftest
 t258_l1135
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v257_l1133)))


(def v260_l1141 (pj/plan multi-pose))


(deftest
 t261_l1143
 (is
  ((fn
    [plan]
    (let [p (first (:panels plan))] (= 2 (count (:layers p)))))
   v260_l1141)))


(def
 v263_l1156
 (kind/mermaid
  "\ngraph TD\n  POSE[\"pose + options\"]\n  POSE --> CT[\"Column Types<br/>(infer-column-types)\"]\n  POSE --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Layer type<br/>(infer-layer-type)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  POSE --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style POSE fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
