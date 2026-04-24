(ns
 plotje-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l62
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l66 (def scatter-frame (-> five-points (pj/lay-point :x :y))))


(def v6_l72 scatter-frame)


(deftest
 t7_l74
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v6_l72)))


(def v9_l78 (pj/plan scatter-frame))


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
        (scicloj.plotje.impl.defaults/hex->rgba
         (:default-color (scicloj.plotje.impl.defaults/config)))
        (:color g))))))
   v9_l78)))


(def v12_l123 (-> {:values [1 2 3 4 5 6]} pj/lay-histogram))


(deftest
 t13_l126
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v12_l123)))


(def v15_l130 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} pj/lay-point))


(deftest
 t16_l133
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v15_l130)))


(def
 v18_l137
 (-> {:x [1 2 3 4], :y [4 5 6 7], :g ["a" "a" "b" "b"]} pj/lay-point))


(deftest
 t19_l140
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 4 (:points s)) (some #{"a"} (:texts s)))))
   v18_l137)))


(def
 v21_l152
 (def
  two-col-frame
  (pj/frame {:x [1.0 2.0 3.0 4.0 5.0], :y [1.0 4.0 9.0 16.0 25.0]})))


(def v22_l156 two-col-frame)


(deftest
 t23_l158
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v22_l156)))


(def
 v25_l162
 (-> two-col-frame (select-keys [:mapping :layers]) kind/pprint))


(deftest
 t26_l164
 (is
  ((fn
    [fr]
    (and (= {:x :x, :y :y} (:mapping fr)) (empty? (:layers fr))))
   v25_l162)))


(def
 v28_l181
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :petal-length :petal-width {:color :species})))


(deftest
 t29_l184
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v28_l181)))


(def
 v31_l203
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v32_l207
 (def bar-frame (-> animals (pj/lay-value-bar :animal :count))))


(def v33_l211 bar-frame)


(deftest
 t34_l213
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v33_l211)))


(def v36_l217 (pj/plan bar-frame))


(deftest
 t37_l219
 (is
  ((fn
    [pl]
    (let
     [p (first (:panels pl))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v36_l217)))


(def
 v39_l233
 (def
  temporal-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-06-01T00:00:00.000-00:00"
     #inst "2024-12-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/lay-point :date :val))))


(def v40_l238 temporal-frame)


(def
 v41_l240
 (let
  [p (first (:panels (pj/plan temporal-frame)))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-count (count (:values (:x-ticks p))),
   :first-tick-label (first (:labels (:x-ticks p)))}))


(deftest
 t42_l245
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v41_l240)))


(def
 v44_l263
 (def
  hour-bar-frame
  (->
   {:hour [9 10 11 12], :count [5 8 12 7]}
   (pj/lay-value-bar :hour :count {:x-type :categorical}))))


(def v45_l267 hour-bar-frame)


(deftest
 t46_l269
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v45_l267)))


(def v47_l271 (:x-domain (first (:panels (pj/plan hour-bar-frame)))))


(deftest t48_l273 (is ((fn [d] (= ["9" "10" "11" "12"] d)) v47_l271)))


(def
 v50_l291
 (def
  colored-frame
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g}))))


(def v51_l297 colored-frame)


(deftest
 t52_l299
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v51_l297)))


(def v54_l303 (pj/plan colored-frame))


(deftest
 t55_l305
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v54_l303)))


(def
 v57_l319
 (def
  fixed-color-frame
  (-> five-points (pj/lay-point :x :y {:color "#E74C3C"}))))


(def v58_l323 fixed-color-frame)


(deftest
 t59_l325
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v58_l323)))


(def v61_l329 (pj/plan fixed-color-frame))


(deftest
 t62_l331
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
   v61_l329)))


(def
 v64_l350
 (-> five-points (pj/lay-point :x :y {:color "steelblue"})))


(deftest
 t65_l353
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v64_l350)))


(def
 v67_l380
 (def
  red-color-frame
  (-> five-points (pj/lay-point :x :y {:color "red"}))))


(def v68_l384 red-color-frame)


(def
 v69_l386
 (let
  [pl (pj/plan red-color-frame)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t70_l390
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v69_l386)))


(def v72_l420 colored-frame)


(def
 v73_l422
 (let
  [pl
   (pj/plan colored-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t74_l428
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v73_l422)))


(def
 v76_l442
 (def
  numeric-color-frame
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
   (pj/lay-point :x :y {:color :val}))))


(def v77_l448 numeric-color-frame)


(def
 v78_l450
 (let
  [pl
   (pj/plan numeric-color-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl)),
   :color-stops (count (:stops (:legend pl)))}))


(deftest
 t79_l456
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v78_l450)))


(def
 v81_l474
 (def
  study-data
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}))


(def
 v83_l481
 (def
  study-continuous-frame
  (-> study-data (pj/lay-line :day :score {:color :subject}))))


(def v84_l485 study-continuous-frame)


(def
 v85_l487
 (let
  [pl
   (pj/plan study-continuous-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl))}))


(deftest
 t86_l492
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v85_l487)))


(def
 v88_l497
 (def
  study-categorical-frame
  (->
   study-data
   (pj/lay-line
    :day
    :score
    {:color :subject, :color-type :categorical}))))


(def v89_l502 study-categorical-frame)


(def
 v90_l504
 (let
  [pl
   (pj/plan study-categorical-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend pl)))}))


(deftest
 t91_l509
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v90_l504)))


(def
 v93_l517
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (pj/lay-line :day :score {:color :subject, :color-type :categorical})
  pj/lay-point
  (pj/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t94_l525
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v93_l517)))


(def
 v96_l535
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v97_l540
 (def
  explicit-group-frame
  (-> grouped-data (pj/lay-point :x :y {:group :g}))))


(def v98_l544 explicit-group-frame)


(def
 v99_l546
 (let
  [pl
   (pj/plan explicit-group-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t100_l551
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v99_l546)))


(def
 v102_l566
 (->
  grouped-data
  (pj/frame :x :y)
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t103_l571
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v102_l566)))


(def
 v105_l577
 (->
  grouped-data
  (pj/frame :x :y {:color :g})
  pj/lay-point
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t106_l582
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v105_l577)))


(def v108_l626 (def hist-frame (-> five-points (pj/frame :x))))


(def v109_l630 hist-frame)


(deftest
 t110_l632
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v109_l630)))


(def v112_l636 (pj/plan hist-frame))


(deftest
 t113_l638
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v112_l636)))


(def
 v115_l647
 (def
  temporal-hist-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"
     #inst "2024-04-01T00:00:00.000-00:00"
     #inst "2024-05-01T00:00:00.000-00:00"]}
   (pj/frame :date))))


(def v116_l652 temporal-hist-frame)


(deftest
 t117_l654
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v116_l652)))


(def v119_l658 (pj/plan temporal-hist-frame))


(deftest
 t120_l660
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v119_l658)))


(def v122_l665 (def count-frame (-> animals (pj/frame :animal))))


(def v123_l669 count-frame)


(deftest
 t124_l671
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v123_l669)))


(def v126_l675 (pj/plan count-frame))


(deftest
 t127_l677
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v126_l675)))


(def v129_l686 (def num-num-frame (-> five-points (pj/frame :x :y))))


(def v130_l689 num-num-frame)


(deftest
 t131_l691
 (is ((fn [v] (= 5 (:points (pj/svg-summary v)))) v130_l689)))


(def v133_l695 (pj/plan num-num-frame))


(deftest
 t134_l697
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :point (:mark layer))))
   v133_l695)))


(def
 v136_l703
 (def
  ts-line-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (pj/frame :date :val))))


(def v137_l708 ts-line-frame)


(deftest
 t138_l710
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v137_l708)))


(def v140_l714 (pj/plan ts-line-frame))


(deftest
 t141_l716
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :line (:mark layer))))
   v140_l714)))


(def
 v143_l722
 (def
  boxplot-frame
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (pj/frame :species :val))))


(def v144_l727 boxplot-frame)


(deftest
 t145_l729
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v144_l727)))


(def v147_l733 (pj/plan boxplot-frame))


(deftest
 t148_l735
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v147_l733)))


(def
 v150_l742
 (def
  horizontal-boxplot-frame
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (pj/frame :val :species))))


(def v151_l747 horizontal-boxplot-frame)


(deftest
 t152_l749
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v151_l747)))


(def v154_l753 (pj/plan horizontal-boxplot-frame))


(deftest
 t155_l755
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v154_l753)))


(def v157_l765 scatter-frame)


(def
 v158_l767
 (let
  [pl (pj/plan scatter-frame) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t159_l773
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v158_l767)))


(def v161_l783 bar-frame)


(def
 v162_l785
 (let
  [pl (pj/plan bar-frame) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest
 t163_l789
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v162_l785)))


(def
 v165_l793
 (def
  fill-frame
  (->
   {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
   (pj/lay-bar :x {:position :fill, :color :g}))))


(def v166_l798 fill-frame)


(def v167_l800 (:y-domain (first (:panels (pj/plan fill-frame)))))


(deftest
 t168_l802
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v167_l800)))


(def v170_l825 scatter-frame)


(def
 v171_l827
 (let
  [pl (pj/plan scatter-frame) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t172_l832
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v171_l827)))


(def
 v174_l841
 (def
  log-scale-frame
  (->
   {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
   (pj/lay-point :x :y)
   (pj/scale :x :log))))


(def v175_l847 log-scale-frame)


(def
 v176_l849
 (let
  [pl (pj/plan log-scale-frame) p (first (:panels pl))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t177_l854
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v176_l849)))


(def v179_l863 bar-frame)


(def
 v180_l865
 (let
  [pl (pj/plan bar-frame) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t181_l869
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v180_l865)))


(def
 v183_l876
 (def
  iris-label-frame
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width))))


(def v184_l880 iris-label-frame)


(def
 v185_l882
 (let
  [pl (pj/plan iris-label-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t186_l886
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v185_l882)))


(def v188_l892 (def x-only-frame (-> five-points (pj/frame :x))))


(def v189_l895 x-only-frame)


(def
 v190_l897
 (let
  [pl (pj/plan x-only-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t191_l901
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v190_l897)))


(def
 v193_l906
 (def
  explicit-label-frame
  (->
   five-points
   (pj/lay-point :x :y)
   (pj/options {:x-label "Length (cm)", :y-label "Width (cm)"}))))


(def v194_l911 explicit-label-frame)


(def
 v195_l913
 (let
  [pl (pj/plan explicit-label-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t196_l917
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v195_l913)))


(def v198_l928 colored-frame)


(def v199_l930 (:legend (pj/plan colored-frame)))


(deftest
 t200_l932
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v199_l930)))


(def v202_l939 scatter-frame)


(def v203_l941 (:legend (pj/plan scatter-frame)))


(deftest t204_l943 (is (nil? v203_l941)))


(def v206_l947 fixed-color-frame)


(def v207_l949 (:legend (pj/plan fixed-color-frame)))


(deftest t208_l951 (is (nil? v207_l949)))


(def
 v210_l955
 (def
  continuous-color-frame
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (pj/lay-point :x :y {:color :val}))))


(def v211_l959 continuous-color-frame)


(def v212_l961 (:legend (pj/plan continuous-color-frame)))


(deftest
 t213_l963
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v212_l961)))


(def
 v215_l972
 (def
  size-legend-frame
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (pj/lay-point :x :y {:size :s}))))


(def v216_l976 size-legend-frame)


(def v217_l978 (:size-legend (pj/plan size-legend-frame)))


(deftest
 t218_l980
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v217_l978)))


(def v220_l986 scatter-frame)


(def v221_l988 (:size-legend (pj/plan scatter-frame)))


(deftest t222_l990 (is (nil? v221_l988)))


(def
 v224_l999
 (def
  alpha-legend-frame
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (pj/lay-point :x :y {:alpha :a}))))


(def v225_l1003 alpha-legend-frame)


(def v226_l1005 (:alpha-legend (pj/plan alpha-legend-frame)))


(deftest
 t227_l1007
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v226_l1005)))


(def v229_l1013 scatter-frame)


(def v230_l1015 (:alpha-legend (pj/plan scatter-frame)))


(deftest t231_l1017 (is (nil? v230_l1015)))


(def v233_l1027 scatter-frame)


(def
 v234_l1029
 (def
  full-layout-frame
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (pj/lay-point :x :y {:color :g})
   (pj/options {:title "My Plot"}))))


(def v235_l1036 full-layout-frame)


(def
 v236_l1038
 (let
  [bare (pj/plan scatter-frame) full (pj/plan full-layout-frame)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t237_l1045
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v236_l1038)))


(def v239_l1059 scatter-frame)


(def v240_l1061 (:layout-type (pj/plan scatter-frame)))


(deftest t241_l1063 (is ((fn [lt] (= :single lt)) v240_l1061)))


(def
 v243_l1071
 (def normal-frame (-> animals (pj/lay-value-bar :animal :count))))


(def v244_l1075 normal-frame)


(def
 v245_l1077
 (def
  flip-frame
  (-> animals (pj/lay-value-bar :animal :count) (pj/coord :flip))))


(def v246_l1082 flip-frame)


(deftest
 t247_l1084
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v246_l1082)))


(def
 v248_l1086
 (let
  [np
   (first (:panels (pj/plan normal-frame)))
   fp
   (first (:panels (pj/plan flip-frame)))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t249_l1093
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v248_l1086)))


(def
 v251_l1103
 (def
  flipped-labels-frame
  (-> five-points (pj/lay-point :x :y) (pj/coord :flip))))


(def v252_l1108 flipped-labels-frame)


(def
 v253_l1110
 (let
  [pl (pj/plan flipped-labels-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t254_l1114
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m))))
   v253_l1110)))


(def
 v256_l1128
 (def
  multi-frame
  (->
   five-points
   (pj/frame :x :y)
   pj/lay-point
   (pj/lay-smooth {:stat :linear-model}))))


(def v257_l1134 multi-frame)


(deftest
 t258_l1136
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v257_l1134)))


(def v260_l1142 (pj/plan multi-frame))


(deftest
 t261_l1144
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v260_l1142)))


(def
 v263_l1157
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Layer type<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
