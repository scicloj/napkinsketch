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


(def v12_l107 (-> {:values [1 2 3 4 5 6]} pj/lay-histogram))


(deftest
 t13_l110
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v12_l107)))


(def v15_l114 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t16_l117
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v15_l114)))


(def
 v18_l121
 (-> {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]} pj/lay-point))


(deftest
 t19_l124
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l121)))


(def
 v21_l136
 (def
  two-col-pose
  (pj/pose {:x [1.0 2.0 3.0 4.0 5.0], :y [1.0 4.0 9.0 16.0 25.0]})))


(def v22_l140 two-col-pose)


(deftest
 t23_l142
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v22_l140)))


(def
 v25_l146
 (-> two-col-pose (select-keys [:mapping :layers]) kind/pprint))


(deftest
 t26_l148
 (is
  ((fn
    [pose]
    (and (= {:x :x, :y :y} (:mapping pose)) (empty? (:layers pose))))
   v25_l146)))


(def
 v28_l165
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t29_l168
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v28_l165)))


(def
 v31_l188
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v32_l192
 (def bar-pose (-> animals (pj/lay-value-bar :animal :count))))


(def v33_l196 bar-pose)


(deftest
 t34_l198
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v33_l196)))


(def v36_l202 (pj/plan bar-pose))


(deftest
 t37_l204
 (is
  ((fn
    [plan]
    (let
     [p (first (:panels plan))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v36_l202)))


(def
 v39_l218
 (def
  temporal-pose
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-06-01T00:00:00.000-00:00"
     #inst "2024-12-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/lay-point :date :val))))


(def v40_l223 temporal-pose)


(def
 v41_l225
 (let
  [p (first (:panels (pj/plan temporal-pose)))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t42_l230
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v41_l225)))


(def
 v44_l248
 (def
  hour-bar-pose
  (->
   {:hour [9 10 11 12], :count [5 8 12 7]}
   (pj/lay-value-bar :hour :count {:x-type :categorical}))))


(def v45_l252 hour-bar-pose)


(deftest
 t46_l254
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v45_l252)))


(def v47_l256 (:x-domain (first (:panels (pj/plan hour-bar-pose)))))


(deftest t48_l258 (is ((fn [d] (= ["9" "10" "11" "12"] d)) v47_l256)))


(def
 v50_l276
 (def
  colored-pose
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g}))))


(def v51_l282 colored-pose)


(deftest
 t52_l284
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v51_l282)))


(def v54_l288 (pj/plan colored-pose))


(deftest
 t55_l290
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend plan))
      (= 100 (get-in plan [:layout :legend-w])))))
   v54_l288)))


(def
 v57_l304
 (def
  fixed-color-pose
  (-> five-points (pj/lay-point :x :y {:color "#E74C3C"}))))


(def v58_l308 fixed-color-pose)


(deftest
 t59_l310
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v58_l308)))


(def v61_l314 (pj/plan fixed-color-pose))


(deftest
 t62_l316
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
       (= [(/ 231.0 255.0) (/ 76.0 255.0) (/ 60.0 255.0) 1.0] c)))))
   v61_l314)))


(def
 v64_l336
 (-> five-points (pj/lay-point :x :y {:color "steelblue"})))


(deftest
 t65_l339
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v64_l336)))


(def
 v67_l366
 (def
  red-color-pose
  (-> five-points (pj/lay-point :x :y {:color "red"}))))


(def v68_l370 red-color-pose)


(def
 v69_l372
 (let
  [plan (pj/plan red-color-pose)]
  {:legend (:legend plan),
   :color
   (:color
    (first (:groups (first (:layers (first (:panels plan)))))))}))


(deftest
 t70_l376
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v69_l372)))


(def v72_l406 colored-pose)


(def
 v73_l408
 (let
  [plan
   (pj/plan colored-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend plan))}))


(deftest
 t74_l414
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v73_l408)))


(def
 v76_l428
 (def
  numeric-color-pose
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
   (pj/lay-point :x :y {:color :val}))))


(def v77_l434 numeric-color-pose)


(def
 v78_l436
 (let
  [plan
   (pj/plan numeric-color-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend plan)),
   :color-stops (count (:stops (:legend plan)))}))


(deftest
 t79_l442
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v78_l436)))


(def
 v81_l460
 (def
  study-data
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}))


(def
 v83_l467
 (def
  study-continuous-pose
  (-> study-data (pj/lay-line :day :score {:color :subject}))))


(def v84_l471 study-continuous-pose)


(def
 v85_l473
 (let
  [plan
   (pj/plan study-continuous-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend plan))}))


(deftest
 t86_l478
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v85_l473)))


(def
 v88_l483
 (def
  study-categorical-pose
  (->
   study-data
   (pj/lay-line
    :day
    :score
    {:color :subject, :color-type :categorical}))))


(def v89_l488 study-categorical-pose)


(def
 v90_l490
 (let
  [plan
   (pj/plan study-categorical-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend plan)))}))


(deftest
 t91_l495
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v90_l490)))


(def
 v93_l503
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (pj/lay-line :day :score {:color :subject, :color-type :categorical})
  pj/lay-point
  (pj/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t94_l511
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v93_l503)))


(def
 v96_l521
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v97_l526
 (def
  explicit-group-pose
  (-> grouped-data (pj/lay-point :x :y {:group :g}))))


(def v98_l530 explicit-group-pose)


(def
 v99_l532
 (let
  [plan
   (pj/plan explicit-group-pose)
   layer
   (first (:layers (first (:panels plan))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend plan))}))


(deftest
 t100_l537
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v99_l532)))


(def
 v102_l552
 (->
  grouped-data
  (pj/pose :x :y)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t103_l557
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v102_l552)))


(def
 v105_l563
 (->
  grouped-data
  (pj/pose :x :y {:color :g})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t106_l568
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v105_l563)))


(def v108_l612 (def hist-pose (-> five-points (pj/pose :x))))


(def v109_l616 hist-pose)


(deftest
 t110_l618
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v109_l616)))


(def v112_l622 (pj/plan hist-pose))


(deftest
 t113_l624
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :bar (:mark layer))))
   v112_l622)))


(def
 v115_l633
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


(def v116_l638 temporal-hist-pose)


(deftest
 t117_l640
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v116_l638)))


(def v119_l644 (pj/plan temporal-hist-pose))


(deftest
 t120_l646
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :bar (:mark layer))))
   v119_l644)))


(def v122_l651 (def count-pose (-> animals (pj/pose :animal))))


(def v123_l655 count-pose)


(deftest
 t124_l657
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v123_l655)))


(def v126_l661 (pj/plan count-pose))


(deftest
 t127_l663
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :rect (:mark layer))))
   v126_l661)))


(def v129_l672 (def num-num-pose (-> five-points (pj/pose :x :y))))


(def v130_l675 num-num-pose)


(deftest
 t131_l677
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v130_l675)))


(def v133_l681 (pj/plan num-num-pose))


(deftest
 t134_l683
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :point (:mark layer))))
   v133_l681)))


(def
 v136_l689
 (def
  ts-line-pose
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/pose :date :val))))


(def v137_l694 ts-line-pose)


(deftest
 t138_l696
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v137_l694)))


(def v140_l700 (pj/plan ts-line-pose))


(deftest
 t141_l702
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (= :line (:mark layer))))
   v140_l700)))


(def
 v143_l708
 (def
  boxplot-pose
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (pj/pose :species :val))))


(def v144_l713 boxplot-pose)


(deftest
 t145_l715
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v144_l713)))


(def v147_l719 (pj/plan boxplot-pose))


(deftest
 t148_l721
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v147_l719)))


(def
 v150_l728
 (def
  horizontal-boxplot-pose
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (pj/pose :val :species))))


(def v151_l733 horizontal-boxplot-pose)


(deftest
 t152_l735
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v151_l733)))


(def v154_l739 (pj/plan horizontal-boxplot-pose))


(deftest
 t155_l741
 (is
  ((fn
    [plan]
    (let
     [layer (first (:layers (first (:panels plan))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v154_l739)))


(def v157_l751 scatter-pose)


(def
 v158_l753
 (let
  [plan (pj/plan scatter-pose) p (first (:panels plan))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t159_l759
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v158_l753)))


(def v161_l769 bar-pose)


(def
 v162_l771
 (let
  [plan (pj/plan bar-pose) p (first (:panels plan))]
  {:y-domain (:y-domain p)}))


(deftest
 t163_l775
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v162_l771)))


(def
 v165_l779
 (def
  fill-pose
  (->
   {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
   (pj/lay-bar :x {:position :fill, :color :g}))))


(def v166_l784 fill-pose)


(def v167_l786 (:y-domain (first (:panels (pj/plan fill-pose)))))


(deftest
 t168_l788
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v167_l786)))


(def v170_l811 scatter-pose)


(def
 v171_l813
 (let
  [plan (pj/plan scatter-pose) p (first (:panels plan))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t172_l818
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v171_l813)))


(def
 v174_l827
 (def
  log-scale-pose
  (->
   {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
   (pj/lay-point :x :y)
   (pj/scale :x :log))))


(def v175_l833 log-scale-pose)


(def
 v176_l835
 (let
  [plan (pj/plan log-scale-pose) p (first (:panels plan))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t177_l840
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v176_l835)))


(def v179_l849 bar-pose)


(def
 v180_l851
 (let
  [plan (pj/plan bar-pose) p (first (:panels plan))]
  (:values (:x-ticks p))))


(deftest
 t181_l855
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v180_l851)))


(def
 v183_l862
 (def
  iris-label-pose
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width))))


(def v184_l866 iris-label-pose)


(def
 v185_l868
 (let
  [plan (pj/plan iris-label-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t186_l872
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v185_l868)))


(def v188_l878 (def x-only-pose (-> five-points (pj/pose :x))))


(def v189_l881 x-only-pose)


(def
 v190_l883
 (let
  [plan (pj/plan x-only-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t191_l887
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v190_l883)))


(def
 v193_l892
 (def
  explicit-label-pose
  (->
   five-points
   (pj/lay-point :x :y)
   (pj/options {:x-label "Length (cm)", :y-label "Width (cm)"}))))


(def v194_l897 explicit-label-pose)


(def
 v195_l899
 (let
  [plan (pj/plan explicit-label-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t196_l903
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v195_l899)))


(def v198_l914 colored-pose)


(def v199_l916 (:legend (pj/plan colored-pose)))


(deftest
 t200_l918
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v199_l916)))


(def v202_l925 scatter-pose)


(def v203_l927 (:legend (pj/plan scatter-pose)))


(deftest t204_l929 (is (nil? v203_l927)))


(def v206_l933 fixed-color-pose)


(def v207_l935 (:legend (pj/plan fixed-color-pose)))


(deftest t208_l937 (is (nil? v207_l935)))


(def
 v210_l941
 (def
  continuous-color-pose
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (pj/lay-point :x :y {:color :val}))))


(def v211_l945 continuous-color-pose)


(def v212_l947 (:legend (pj/plan continuous-color-pose)))


(deftest
 t213_l949
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v212_l947)))


(def
 v215_l958
 (def
  size-legend-pose
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (pj/lay-point :x :y {:size :s}))))


(def v216_l962 size-legend-pose)


(def v217_l964 (:size-legend (pj/plan size-legend-pose)))


(deftest
 t218_l966
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v217_l964)))


(def v220_l972 scatter-pose)


(def v221_l974 (:size-legend (pj/plan scatter-pose)))


(deftest t222_l976 (is (nil? v221_l974)))


(def
 v224_l985
 (def
  alpha-legend-pose
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (pj/lay-point :x :y {:alpha :a}))))


(def v225_l989 alpha-legend-pose)


(def v226_l991 (:alpha-legend (pj/plan alpha-legend-pose)))


(deftest
 t227_l993
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (= 4 (count (:entries leg)))))
   v226_l991)))


(def v229_l999 scatter-pose)


(def v230_l1001 (:alpha-legend (pj/plan scatter-pose)))


(deftest t231_l1003 (is (nil? v230_l1001)))


(def v233_l1013 scatter-pose)


(def
 v234_l1015
 (def
  full-layout-pose
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g})
   (pj/options {:title "My Plot"}))))


(def v235_l1022 full-layout-pose)


(def
 v236_l1024
 (let
  [bare (pj/plan scatter-pose) full (pj/plan full-layout-pose)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t237_l1031
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v236_l1024)))


(def v239_l1045 scatter-pose)


(def v240_l1047 (:layout-type (pj/plan scatter-pose)))


(deftest t241_l1049 (is ((fn [lt] (= :single lt)) v240_l1047)))


(def
 v243_l1057
 (def normal-pose (-> animals (pj/lay-value-bar :animal :count))))


(def v244_l1061 normal-pose)


(def
 v245_l1063
 (def
  flip-pose
  (-> animals (pj/lay-value-bar :animal :count) (pj/coord :flip))))


(def v246_l1068 flip-pose)


(deftest
 t247_l1070
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v246_l1068)))


(def
 v248_l1072
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
 t249_l1079
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v248_l1072)))


(def
 v251_l1089
 (def
  flipped-labels-pose
  (-> five-points (pj/lay-point :x :y) (pj/coord :flip))))


(def v252_l1094 flipped-labels-pose)


(def
 v253_l1096
 (let
  [plan (pj/plan flipped-labels-pose)]
  {:x-label (:x-label plan), :y-label (:y-label plan)}))


(deftest
 t254_l1100
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m))))
   v253_l1096)))


(def
 v256_l1114
 (def
  multi-pose
  (->
   five-points
   (pj/pose :x :y)
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v257_l1120 multi-pose)


(deftest
 t258_l1122
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v257_l1120)))


(def v260_l1128 (pj/plan multi-pose))


(deftest
 t261_l1130
 (is
  ((fn
    [plan]
    (let [p (first (:panels plan))] (= 2 (count (:layers p)))))
   v260_l1128)))


(def
 v263_l1143
 (kind/mermaid
  "\ngraph TD\n  POSE[\"pose + options\"]\n  POSE --> CT[\"Column Types<br/>(infer-column-types)\"]\n  POSE --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Layer type<br/>(infer-layer-type)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  POSE --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style POSE fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
