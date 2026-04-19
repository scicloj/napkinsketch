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
 t33_l203
 (is
  ((fn
    [m]
    (and
     (true? (:x-domain-numeric? m))
     (= 10 (:tick-count m))
     (= "Feb-01" (:first-tick-label m))))
   v32_l194)))


(def
 v35_l222
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


(def v36_l228 colored-views)


(deftest
 t37_l230
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v36_l228)))


(def v39_l234 (sk/plan colored-views))


(deftest
 t40_l236
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend pl))
      (= 100 (get-in pl [:layout :legend-w])))))
   v39_l234)))


(def
 v42_l250
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v43_l254 fixed-color-views)


(deftest
 t44_l256
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v43_l254)))


(def v46_l260 (sk/plan fixed-color-views))


(deftest
 t47_l262
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
   v46_l260)))


(def
 v49_l281
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t50_l284
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v49_l281)))


(def
 v52_l311
 (let
  [pl (-> five-points (sk/lay-point :x :y {:color "red"}) sk/plan)]
  {:legend (:legend pl),
   :color
   (:color (first (:groups (first (:layers (first (:panels pl)))))))}))


(deftest
 t53_l317
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v52_l311)))


(def
 v55_l347
 (let
  [pl
   (sk/plan colored-views)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t56_l353
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v55_l347)))


(def
 v58_l367
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
 t59_l377
 (is
  ((fn
    [m]
    (and
     (= 1 (:group-count m))
     (= :continuous (:legend-type m))
     (= 20 (:color-stops m))))
   v58_l367)))


(def
 v61_l397
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
 t62_l407
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v61_l397)))


(def
 v64_l412
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
 t65_l423
 (is
  ((fn [m] (and (= 3 (:group-count m)) (= 3 (:legend-entries m))))
   v64_l412)))


(def
 v67_l431
 (->
  {:subject [1 1 1 2 2 2 3 3 3],
   :day [1 2 3 1 2 3 1 2 3],
   :score [5 7 6 3 4 5 8 9 7]}
  (sk/lay-line :day :score {:color :subject, :color-type :categorical})
  sk/lay-point
  (sk/options {:title "Scores by Subject (categorical override)"})))


(deftest
 t68_l439
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v67_l431)))


(def
 v70_l449
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v71_l454
 (let
  [pl
   (-> grouped-data (sk/lay-point :x :y {:group :g}) sk/plan)
   layer
   (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend pl))}))


(deftest
 t72_l461
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v71_l454)))


(def v74_l476 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t75_l481
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v74_l476)))


(def
 v77_l487
 (-> grouped-data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm))


(deftest
 t78_l492
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v77_l487)))


(def v80_l536 (def hist-views (-> five-points (sk/view :x))))


(def v81_l540 hist-views)


(deftest
 t82_l542
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v81_l540)))


(def v84_l546 (sk/plan hist-views))


(deftest
 t85_l548
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :bar (:mark layer))))
   v84_l546)))


(def
 v87_l557
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


(def v88_l562 temporal-hist-sketch)


(deftest
 t89_l564
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v88_l562)))


(def v90_l566 (kind/pprint temporal-hist-sketch))


(deftest
 t91_l568
 (is
  ((fn
    [sk]
    (= :bar (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v90_l566)))


(def v93_l573 (def count-views (-> animals (sk/view :animal))))


(def v94_l577 count-views)


(deftest
 t95_l579
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v94_l577)))


(def v97_l583 (sk/plan count-views))


(deftest
 t98_l585
 (is
  ((fn
    [pl]
    (let
     [layer (first (:layers (first (:panels pl))))]
     (= :rect (:mark layer))))
   v97_l583)))


(def v100_l594 (def num-num-sketch (-> five-points (sk/view :x :y))))


(def v101_l597 num-num-sketch)


(deftest
 t102_l599
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v101_l597)))


(def v103_l601 (kind/pprint num-num-sketch))


(deftest
 t104_l603
 (is
  ((fn
    [sk]
    (=
     :point
     (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v103_l601)))


(def
 v106_l609
 (def
  ts-line-sketch
  (->
   {:date
    [#inst "2024-01-01T00:00:00.000-00:00"
     #inst "2024-02-01T00:00:00.000-00:00"
     #inst "2024-03-01T00:00:00.000-00:00"],
    :val [10 25 18]}
   (sk/view :date :val))))


(def v107_l614 ts-line-sketch)


(deftest
 t108_l616
 (is ((fn [v] (= 1 (:lines (sk/svg-summary v)))) v107_l614)))


(def v109_l618 (kind/pprint ts-line-sketch))


(deftest
 t110_l620
 (is
  ((fn
    [sk]
    (= :line (:mark (first (:layers (first (:panels (sk/plan sk))))))))
   v109_l618)))


(def
 v112_l626
 (def
  boxplot-sketch
  (->
   {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"],
    :val [8 10 12 18 20 22 14 15 17]}
   (sk/view :species :val))))


(def v113_l631 boxplot-sketch)


(deftest
 t114_l633
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v113_l631)))


(def v115_l635 (kind/pprint boxplot-sketch))


(deftest
 t116_l637
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels (sk/plan sk)))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v115_l635)))


(def
 v118_l645
 (def
  horizontal-boxplot-sketch
  (->
   {:val [8 10 12 18 20 22 14 15 17],
    :species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]}
   (sk/view :val :species))))


(def v119_l650 horizontal-boxplot-sketch)


(deftest
 t120_l652
 (is ((fn [v] (pos? (:lines (sk/svg-summary v)))) v119_l650)))


(def v121_l654 (kind/pprint horizontal-boxplot-sketch))


(deftest
 t122_l656
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels (sk/plan sk)))))]
     (and (= :boxplot (:mark layer)) (= 3 (count (:boxes layer))))))
   v121_l654)))


(def
 v124_l667
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t125_l673
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v124_l667)))


(def
 v127_l683
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  {:y-domain (:y-domain p)}))


(deftest
 t128_l687
 (is ((fn [m] (<= (first (:y-domain m)) 0)) v127_l683)))


(def
 v130_l691
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
 t131_l698
 (is ((fn [d] (and (== 0.0 (first d)) (== 1.0 (second d)))) v130_l691)))


(def
 v133_l721
 (let
  [pl (sk/plan scatter-views) p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p)),
   :x-tick-labels (:labels (:x-ticks p))}))


(deftest
 t134_l726
 (is
  ((fn
    [m]
    (and
     (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0] (:x-tick-values m))
     (=
      ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
      (:x-tick-labels m))))
   v133_l721)))


(def
 v136_l735
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
 t137_l744
 (is
  ((fn
    [m]
    (and
     (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
     (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))
   v136_l735)))


(def
 v139_l753
 (let
  [pl (sk/plan bar-views) p (first (:panels pl))]
  (:values (:x-ticks p))))


(deftest
 t140_l757
 (is ((fn [v] (= ["cat" "dog" "bird" "fish"] v)) v139_l753)))


(def
 v142_l764
 (let
  [pl
   (->
    (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t143_l770
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v142_l764)))


(def
 v145_l776
 (let
  [pl (-> five-points (sk/view :x) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t146_l780
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v145_l776)))


(def
 v148_l785
 (let
  [pl
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/options {:x-label "Length (cm)", :y-label "Width (cm)"})
    sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t149_l792
 (is
  ((fn
    [m]
    (and (= "Length (cm)" (:x-label m)) (= "Width (cm)" (:y-label m))))
   v148_l785)))


(def v151_l803 (:legend (sk/plan colored-views)))


(deftest
 t152_l805
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v151_l803)))


(def v154_l812 (:legend (sk/plan scatter-views)))


(deftest t155_l814 (is (nil? v154_l812)))


(def v157_l818 (:legend (sk/plan fixed-color-views)))


(deftest t158_l820 (is (nil? v157_l818)))


(def
 v160_l824
 (:legend
  (->
   {:x [1 2 3], :y [4 5 6], :val [10 20 30]}
   (sk/lay-point :x :y {:color :val})
   sk/plan)))


(deftest
 t161_l828
 (is
  ((fn
    [leg]
    (and (= :continuous (:type leg)) (= 20 (count (:stops leg)))))
   v160_l824)))


(def
 v163_l837
 (:size-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :s [10 20 30 40 50]}
   (sk/lay-point :x :y {:size :s})
   sk/plan)))


(deftest
 t164_l841
 (is
  ((fn
    [leg]
    (and
     (= :size (:type leg))
     (= :s (:title leg))
     (= 5 (count (:entries leg)))))
   v163_l837)))


(def v166_l847 (:size-legend (sk/plan scatter-views)))


(deftest t167_l849 (is (nil? v166_l847)))


(def
 v169_l858
 (:alpha-legend
  (->
   {:x [1 2 3 4 5], :y [1 2 3 4 5], :a [0.1 0.3 0.5 0.7 0.9]}
   (sk/lay-point :x :y {:alpha :a})
   sk/plan)))


(deftest
 t170_l862
 (is
  ((fn
    [leg]
    (and
     (= :alpha (:type leg))
     (= :a (:title leg))
     (pos? (count (:entries leg)))))
   v169_l858)))


(def v172_l868 (:alpha-legend (sk/plan scatter-views)))


(deftest t173_l870 (is (nil? v172_l868)))


(def
 v175_l880
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
 t176_l892
 (is
  ((fn
    [m]
    (and
     (zero? (:bare-title-pad m))
     (pos? (:full-title-pad m))
     (zero? (:bare-legend-w m))
     (= 100 (:full-legend-w m))))
   v175_l880)))


(def v178_l906 (let [pl (sk/plan scatter-views)] (:layout-type pl)))


(deftest t179_l909 (is ((fn [lt] (= :single lt)) v178_l906)))


(def
 v181_l917
 (def normal-pl (-> animals (sk/lay-value-bar :animal :count) sk/plan)))


(def
 v182_l922
 (def
  flip-pl
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/plan)))


(def
 v183_l928
 (let
  [np (first (:panels normal-pl)) fp (first (:panels flip-pl))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t184_l935
 (is
  ((fn
    [m]
    (and
     (true? (get-in m [:normal :x-categorical?]))
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (true? (get-in m [:flipped :y-categorical?]))))
   v183_l928)))


(def
 v185_l940
 (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip)))


(deftest
 t186_l944
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v185_l940)))


(def
 v188_l951
 (let
  [pl (-> five-points (sk/lay-point :x :y) (sk/coord :flip) sk/plan)]
  {:x-label (:x-label pl), :y-label (:y-label pl)}))


(deftest
 t189_l958
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v188_l951)))


(def
 v191_l972
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v192_l978 multi-views)


(deftest
 t193_l980
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v192_l978)))


(def v195_l986 (sk/plan multi-views))


(deftest
 t196_l988
 (is
  ((fn [pl] (let [p (first (:panels pl))] (= 2 (count (:layers p)))))
   v195_l986)))


(def
 v198_l1001
 (kind/mermaid
  "\ngraph TD\n  VIEWS[\"views + options\"]\n  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]\n  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]\n  CT --> GR[\"Grouping<br/>(infer-grouping)\"]\n  AE --> GR\n  CT --> ME[\"Method<br/>(infer-method)\"]\n  GR --> STATS[\"Statistics<br/>(compute-stat)\"]\n  ME --> STATS\n\n  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]\n  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]\n\n  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]\n  AE --> LEG[\"Color Legend<br/>(build-legend)\"]\n  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]\n  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]\n\n  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]\n  LBL --> LAYOUT\n  LEG --> LAYOUT\n  SLEG --> LAYOUT\n  ALEG --> LAYOUT\n\n  DOM --> PLAN[\"Plan\"]\n  TK --> PLAN\n  LBL --> PLAN\n  LEG --> PLAN\n  SLEG --> PLAN\n  ALEG --> PLAN\n  LAYOUT --> PLAN\n  STATS --> PLAN\n\n  style VIEWS fill:#e8f5e9\n  style PLAN fill:#fff3e0\n  style STATS fill:#e3f2fd\n  style DOM fill:#e3f2fd\n"))
