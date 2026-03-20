(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l28
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def
 v4_l32
 (def
  scatter-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point)))))


(def v6_l39 (sk/sketch scatter-views))


(deftest
 t7_l41
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
   v6_l39)))


(def v9_l55 (sk/plot scatter-views))


(deftest
 t10_l57
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l55)))


(def
 v12_l85
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v13_l89
 (def
  bar-views
  (-> animals (sk/view :animal :count) (sk/lay (sk/value-bar)))))


(def v14_l94 (sk/sketch bar-views))


(deftest
 t15_l96
 (is
  ((fn
    [sk]
    (let
     [p (first (:panels sk))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v14_l94)))


(def v16_l100 (sk/plot bar-views))


(deftest
 t17_l102
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v16_l100)))


(def
 v19_l111
 (let
  [sk
   (->
    {:date
     [(java.time.LocalDate/of 2024 1 1)
      (java.time.LocalDate/of 2024 6 1)
      (java.time.LocalDate/of 2024 12 1)],
     :val [10 25 18]}
    (sk/view :date :val)
    (sk/lay (sk/point))
    sk/sketch)
   p
   (first (:panels sk))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t20_l122
 (is
  ((fn
    [m]
    (and (true? (:x-domain-numeric? m)) (not-empty (:tick-labels m))))
   v19_l111)))


(def v22_l143 (def hist-views (-> five-points (sk/view :x))))


(def v23_l147 (sk/sketch hist-views))


(deftest
 t24_l149
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :bar (:mark layer))))
   v23_l147)))


(def v25_l152 (sk/plot hist-views))


(deftest
 t26_l154
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l152)))


(def v28_l162 (def count-views (-> animals (sk/view :animal))))


(def v29_l166 (sk/sketch count-views))


(deftest
 t30_l168
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :rect (:mark layer))))
   v29_l166)))


(def v31_l171 (sk/plot count-views))


(deftest
 t32_l173
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v31_l171)))


(def
 v34_l179
 (let
  [sk
   (->
    {:species ["a" "b" "c"], :val [10 20 15]}
    (sk/view :species :val)
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  (:mark layer)))


(deftest t35_l185 (is ((fn [m] (= :point m)) v34_l179)))


(def
 v37_l194
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/view :x :y)
   (sk/lay (sk/point {:color :g})))))


(def v38_l201 (sk/sketch colored-views))


(deftest
 t39_l203
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (and
      (= 2 (count (:groups layer)))
      (some? (:legend sk))
      (= 100 (get-in sk [:layout :legend-w])))))
   v38_l201)))


(def v40_l208 (sk/plot colored-views))


(deftest
 t41_l210
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v40_l208)))


(def
 v43_l221
 (def
  fixed-color-views
  (->
   five-points
   (sk/view :x :y)
   (sk/lay (sk/point {:color "#E74C3C"})))))


(def v44_l226 (sk/sketch fixed-color-views))


(deftest
 t45_l228
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
   v44_l226)))


(def v46_l238 (sk/plot fixed-color-views))


(deftest
 t47_l240
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l238)))


(def
 v49_l251
 (->
  five-points
  (sk/view :x :y)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t50_l256
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v49_l251)))


(def
 v52_l283
 (let
  [sk
   (->
    five-points
    (sk/view :x :y)
    (sk/lay (sk/point {:color "red"}))
    sk/sketch)]
  {:legend (:legend sk),
   :color
   (:color (first (:groups (first (:layers (first (:panels sk)))))))}))


(deftest
 t53_l290
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v52_l283)))


(def
 v55_l317
 (let
  [sk
   (sk/sketch colored-views)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t56_l323
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v55_l317)))


(def
 v58_l336
 (let
  [sk
   (->
    {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
    (sk/view :x :y)
    (sk/lay (sk/point {:color :val}))
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend sk))}))


(deftest
 t59_l346
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v58_l336)))


(def
 v61_l358
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v62_l363
 (let
  [sk
   (->
    grouped-data
    (sk/view :x :y)
    (sk/lay (sk/point {:group :g}))
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t63_l371
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v62_l363)))


(def
 v65_l386
 (-> grouped-data (sk/view :x :y) (sk/lay (sk/point) (sk/lm)) sk/plot))


(deftest
 t66_l391
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v65_l386)))


(def
 v68_l397
 (->
  grouped-data
  (sk/view :x :y)
  (sk/lay (sk/point {:color :g}) (sk/lm {:color :g}))
  sk/plot))


(deftest
 t69_l403
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v68_l397)))


(def
 v71_l416
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t72_l422
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v71_l416)))


(def
 v74_l430
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  {:y-domain (:y-domain p)}))


(deftest t75_l434 (is ((fn [m] (<= (first (:y-domain m)) 0)) v74_l430)))


(def
 v77_l438
 (let
  [fill-sk
   (->
    {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
    (sk/view :x)
    (sk/lay (sk/stacked-bar-fill {:color :g}))
    sk/sketch)
   p
   (first (:panels fill-sk))]
  (:y-domain p)))


(deftest
 t78_l446
 (is ((fn [d] (and (= 0.0 (first d)) (= 1.0 (second d)))) v77_l438)))


(def
 v80_l455
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v81_l458
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t82_l465
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v81_l458)))


(def
 v84_l471
 (let
  [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t85_l475
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v84_l471)))


(def
 v87_l480
 (let
  [sk
   (->
    five-points
    (sk/view :x :y)
    (sk/lay (sk/point))
    (sk/labs {:x "Length (cm)", :y "Width (cm)"})
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t88_l488
 (is ((fn [m] (= "Length (cm)" (:x-label m))) v87_l480)))


(def
 v90_l495
 (let
  [bare
   (sk/sketch scatter-views)
   full
   (->
    {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
    (sk/view :x :y)
    (sk/lay (sk/point {:color :g}))
    (sk/labs {:title "My Plot"})
    sk/sketch)]
  {:bare-layout (:layout bare),
   :bare-total-width (:total-width bare),
   :full-layout (:layout full),
   :full-total-width (:total-width full)}))


(deftest
 t91_l508
 (is
  ((fn
    [m]
    (and
     (zero? (get-in m [:bare-layout :title-pad]))
     (pos? (get-in m [:full-layout :title-pad]))
     (zero? (get-in m [:bare-layout :legend-w]))
     (= 100 (get-in m [:full-layout :legend-w]))
     (> (:full-total-width m) (:bare-total-width m))))
   v90_l495)))


(def
 v93_l523
 (def
  normal-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   sk/sketch)))


(def
 v94_l529
 (def
  flip-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   (sk/coord :flip)
   sk/sketch)))


(def
 v95_l536
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t96_l543
 (is
  ((fn
    [m]
    (and
     (get-in m [:normal :x-categorical?])
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (get-in m [:flipped :y-categorical?])))
   v95_l536)))


(def
 v97_l548
 (->
  animals
  (sk/view :animal :count)
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t98_l554
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v97_l548)))


(def
 v100_l561
 (let
  [sk
   (->
    five-points
    (sk/view :x :y)
    (sk/lay (sk/point))
    (sk/coord :flip)
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t101_l569
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v100_l561)))


(def v103_l580 (:legend (sk/sketch colored-views)))


(deftest
 t104_l581
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v103_l580)))


(def v106_l588 (:legend (sk/sketch scatter-views)))


(deftest t107_l590 (is (nil? v106_l588)))


(def v109_l594 (:legend (sk/sketch fixed-color-views)))


(deftest t110_l596 (is (nil? v109_l594)))


(def
 v112_l602
 (def
  multi-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point) (sk/lm)))))


(def v113_l607 (sk/sketch multi-views))


(deftest
 t114_l609
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v113_l607)))


(def v115_l612 (sk/plot multi-views))


(deftest
 t116_l614
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v115_l612)))
