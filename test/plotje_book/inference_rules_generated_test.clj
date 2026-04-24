(ns
 plotje-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as sk]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure.test :refer [deftest is]]))


(def
 v3_l62
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def v4_l66 (def scatter-frame (-> five-points (sk/lay-point :x :y))))


(def v6_l72 scatter-frame)


(deftest
 t7_l74
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v6_l72)))


(def v9_l78 (sk/plan scatter-frame))


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
 (def bar-frame (-> animals (sk/lay-value-bar :animal :count))))


(def v26_l172 bar-frame)


(deftest
 t27_l174
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v26_l172)))


(def v29_l178 (sk/plan bar-frame))


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
  temporal-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-06-01T00:00:00.000-00:00"
     #inst "2024-12-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/lay-point :date :val))))


(def v33_l199 temporal-frame)


(def
 v34_l201
 (let
  [p (first (:panels (sk/plan temporal-frame)))]
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
 v37_l224
 (def
  hour-bar-frame
  (->
   {:hour [9 10 11 12], :count [5 8 12 7]}
   (sk/lay-value-bar :hour :count {:x-type :categorical}))))


(def v38_l228 hour-bar-frame)


(deftest
 t39_l230
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v38_l228)))


(def v40_l232 (:x-domain (first (:panels (sk/plan hour-bar-frame)))))


(deftest t41_l234 (is ((fn [d] (= ["9" "10" "11" "12"] d)) v40_l232)))


(def
 v43_l252
 (def
  colored-frame
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v44_l258 colored-frame)


(deftest
 t45_l260
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v44_l258)))


(def v47_l264 (sk/plan colored-frame))


(deftest
 t48_l266
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v47_l264)))


(def
 v50_l280
 (def
  fixed-color-frame
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v51_l284 fixed-color-frame)


(deftest
 t52_l286
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v51_l284)))


(def v54_l290 (sk/plan fixed-color-frame))


(deftest
 t55_l292
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
   v54_l290)))


(def
 v57_l311
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t58_l314
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v57_l311)))


(def
 v60_l341
 (def
  red-color-frame
  (-> five-points (sk/lay-point :x :y {:color "red"}))))


(def v61_l345 red-color-frame)


(def
 v62_l347
 (let
  [pl (sk/plan red-color-frame)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t63_l351
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v62_l347)))


(def v65_l381 colored-frame)


(def
 v66_l383
 (let
  [pl
   (sk/plan colored-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t67_l389
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v66_l383)))


(def
 v69_l403
 (def
  numeric-color-frame
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
   (sk/lay-point :x :y {:color :val}))))


(def v70_l409 numeric-color-frame)


(def
 v71_l411
 (let
  [pl
   (sk/plan numeric-color-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl)),
   :color-stops (count (:stops (:legend pl)))}))


(deftest
 t72_l417
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v71_l411)))


(def
 v74_l435
 (def
  study-data
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}))


(def
 v76_l442
 (def
  study-continuous-frame
  (-> study-data (sk/lay-line :day :score {:color :subject}))))


(def v77_l446 study-continuous-frame)


(def
 v78_l448
 (let
  [pl
   (sk/plan study-continuous-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend pl))}))


(deftest
 t79_l453
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v78_l448)))


(def
 v81_l458
 (def
  study-categorical-frame
  (->
   study-data
   (sk/lay-line
    :day
    :score
    {:color :subject, :color-type :categorical}))))


(def v82_l463 study-categorical-frame)


(def
 v83_l465
 (let
  [pl
   (sk/plan study-categorical-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :legend-entries (count (:entries (:legend pl)))}))


(deftest
 t84_l470
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v83_l465)))


(def
 v86_l478
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (sk/lay-line :day :score {:color :subject, :color-type :categorical})
  sk/lay-point
  (sk/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t87_l486
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v86_l478)))


(def
 v89_l496
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v90_l501
 (def
  explicit-group-frame
  (-> grouped-data (sk/lay-point :x :y {:group :g}))))


(def v91_l505 explicit-group-frame)


(def
 v92_l507
 (let
  [pl
   (sk/plan explicit-group-frame)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t93_l512
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v92_l507)))


(def
 v95_l527
 (->
  grouped-data
  (sk/frame :x :y)
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t96_l532
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v95_l527)))


(def
 v98_l538
 (->
  grouped-data
  (sk/frame :x :y {:color :g})
  sk/lay-point
  (sk/lay-smooth {:stat :linear-model})))


(deftest
 t99_l543
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v98_l538)))


(def v101_l587 (def hist-frame (-> five-points (sk/frame :x))))


(def v102_l591 hist-frame)


(deftest
 t103_l593
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v102_l591)))


(def v105_l597 (sk/plan hist-frame))


(deftest
 t106_l599
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v105_l597)))


(def
 v108_l608
 (def
  temporal-hist-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"
     #inst "2024-04-01T00:00:00.000-00:00"
     #inst "2024-05-01T00:00:00.000-00:00"]}
   (sk/frame :date))))


(def v109_l613 temporal-hist-frame)


(deftest
 t110_l615
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v109_l613)))


(def v112_l619 (sk/plan temporal-hist-frame))


(deftest
 t113_l621
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v112_l619)))


(def v115_l626 (def count-frame (-> animals (sk/frame :animal))))


(def v116_l630 count-frame)


(deftest
 t117_l632
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v116_l630)))


(def v119_l636 (sk/plan count-frame))


(deftest
 t120_l638
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v119_l636)))


(def v122_l647 (def num-num-frame (-> five-points (sk/frame :x :y))))


(def v123_l650 num-num-frame)


(deftest
 t124_l652
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v123_l650)))


(def v126_l656 (sk/plan num-num-frame))


(deftest
 t127_l658
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :point (:mark layer))))
   v126_l656)))


(def
 v129_l664
 (def
  ts-line-frame
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/frame :date :val))))


(def v130_l669 ts-line-frame)


(deftest
 t131_l671
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v130_l669)))


(def v133_l675 (sk/plan ts-line-frame))


(deftest
 t134_l677
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :line (:mark layer))))
   v133_l675)))


(def
 v136_l683
 (def
  boxplot-frame
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (sk/frame :species :val))))


(def v137_l688 boxplot-frame)


(deftest
 t138_l690
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v137_l688)))


(def v140_l694 (sk/plan boxplot-frame))


(deftest
 t141_l696
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v140_l694)))


(def
 v143_l703
 (def
  horizontal-boxplot-frame
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (sk/frame :val :species))))


(def v144_l708 horizontal-boxplot-frame)


(deftest
 t145_l710
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v144_l708)))


(def v147_l714 (sk/plan horizontal-boxplot-frame))


(deftest
 t148_l716
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v147_l714)))


(def v150_l726 scatter-frame)


(def
 v151_l728
 (let
  [pl (sk/plan scatter-frame) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t152_l734
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v151_l728)))


(def v154_l744 bar-frame)


(def
 v155_l746
 (let
  [pl (sk/plan bar-frame) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest
 t156_l750
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v155_l746)))


(def
 v158_l754
 (def
  fill-frame
  (->
   {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
   (sk/lay-bar :x {:position :fill, :color :g}))))


(def v159_l759 fill-frame)


(def v160_l761 (:y-domain (first (:panels (sk/plan fill-frame)))))


(deftest
 t161_l763
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v160_l761)))


(def v163_l786 scatter-frame)


(def
 v164_l788
 (let
  [pl (sk/plan scatter-frame) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t165_l793
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v164_l788)))


(def
 v167_l802
 (def
  log-scale-frame
  (->
   {:x [0.1 1.0 10.0 100.0 1000.0], :y [5 10 15 20 25]}
   (sk/lay-point :x :y)
   (sk/scale :x :log))))


(def v168_l808 log-scale-frame)


(def
 v169_l810
 (let
  [pl (sk/plan log-scale-frame) p (first (:panels pl))]
  {:tick-values (:values (:x-ticks p)),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t170_l815
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v169_l810)))


(def v172_l824 bar-frame)


(def
 v173_l826
 (let
  [pl (sk/plan bar-frame) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t174_l830
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v173_l826)))


(def
 v176_l837
 (def
  iris-label-frame
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width))))


(def v177_l841 iris-label-frame)


(def
 v178_l843
 (let
  [pl (sk/plan iris-label-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t179_l847
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v178_l843)))


(def v181_l853 (def x-only-frame (-> five-points (sk/frame :x))))


(def v182_l856 x-only-frame)


(def
 v183_l858
 (let
  [pl (sk/plan x-only-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t184_l862
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v183_l858)))


(def
 v186_l867
 (def
  explicit-label-frame
  (->
   five-points
   (sk/lay-point :x :y)
   (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"}))))


(def v187_l872 explicit-label-frame)


(def
 v188_l874
 (let
  [pl (sk/plan explicit-label-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t189_l878
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v188_l874)))


(def v191_l889 colored-frame)


(def v192_l891 (:legend (sk/plan colored-frame)))


(deftest
 t193_l893
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v192_l891)))


(def v195_l900 scatter-frame)


(def v196_l902 (:legend (sk/plan scatter-frame)))


(deftest t197_l904 (is (nil? v196_l902)))


(def v199_l908 fixed-color-frame)


(def v200_l910 (:legend (sk/plan fixed-color-frame)))


(deftest t201_l912 (is (nil? v200_l910)))


(def
 v203_l916
 (def
  continuous-color-frame
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val}))))


(def v204_l920 continuous-color-frame)


(def v205_l922 (:legend (sk/plan continuous-color-frame)))


(deftest
 t206_l924
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v205_l922)))


(def
 v208_l933
 (def
  size-legend-frame
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s}))))


(def v209_l937 size-legend-frame)


(def v210_l939 (:size-legend (sk/plan size-legend-frame)))


(deftest
 t211_l941
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v210_l939)))


(def v213_l947 scatter-frame)


(def v214_l949 (:size-legend (sk/plan scatter-frame)))


(deftest t215_l951 (is (nil? v214_l949)))


(def
 v217_l960
 (def
  alpha-legend-frame
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a}))))


(def v218_l964 alpha-legend-frame)


(def v219_l966 (:alpha-legend (sk/plan alpha-legend-frame)))


(deftest
 t220_l968
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v219_l966)))


(def v222_l974 scatter-frame)


(def v223_l976 (:alpha-legend (sk/plan scatter-frame)))


(deftest t224_l978 (is (nil? v223_l976)))


(def v226_l988 scatter-frame)


(def
 v227_l990
 (def
  full-layout-frame
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g})
   (sk/options {:title "My Plot"}))))


(def v228_l997 full-layout-frame)


(def
 v229_l999
 (let
  [bare (sk/plan scatter-frame) full (sk/plan full-layout-frame)]
  {:bare-title-pad (get-in bare [:layout :title-pad]),
   :full-title-pad (get-in full [:layout :title-pad]),
   :bare-legend-w (get-in bare [:layout :legend-w]),
   :full-legend-w (get-in full [:layout :legend-w])}))


(deftest
 t230_l1006
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v229_l999)))


(def v232_l1020 scatter-frame)


(def v233_l1022 (:layout-type (sk/plan scatter-frame)))


(deftest t234_l1024 (is ((fn [lt] (= :single lt)) v233_l1022)))


(def
 v236_l1032
 (def normal-frame (-> animals (sk/lay-value-bar :animal :count))))


(def v237_l1036 normal-frame)


(def
 v238_l1038
 (def
  flip-frame
  (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip))))


(def v239_l1043 flip-frame)


(deftest
 t240_l1045
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v239_l1043)))


(def
 v241_l1047
 (let
  [np
   (first (:panels (sk/plan normal-frame)))
   fp
   (first (:panels (sk/plan flip-frame)))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t242_l1054
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v241_l1047)))


(def
 v244_l1064
 (def
  flipped-labels-frame
  (-> five-points (sk/lay-point :x :y) (sk/coord :flip))))


(def v245_l1069 flipped-labels-frame)


(def
 v246_l1071
 (let
  [pl (sk/plan flipped-labels-frame)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t247_l1075
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m))))
   v246_l1071)))


(def
 v249_l1089
 (def
  multi-frame
  (->
   five-points
   (sk/frame :x :y)
   sk/lay-point
   (sk/lay-smooth {:stat :linear-model}))))


(def v250_l1095 multi-frame)


(deftest
 t251_l1097
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v250_l1095)))


(def v253_l1103 (sk/plan multi-frame))


(deftest
 t254_l1105
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v253_l1103)))


(def
 v256_l1118
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Layer type<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
