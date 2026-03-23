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


(def v4_l32 (def scatter-views (-> five-points (sk/lay-point :x :y))))


(def v6_l38 (sk/sketch scatter-views))


(deftest
 t7_l40
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
   v6_l38)))


(def v9_l54 scatter-views)


(deftest
 t10_l56
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l54)))


(def
 v12_l84
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v13_l88
 (def bar-views (-> animals (sk/lay-value-bar :animal :count))))


(def v14_l92 (sk/sketch bar-views))


(deftest
 t15_l94
 (is
  ((fn
    [sk]
    (let
     [p (first (:panels sk))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v14_l92)))


(def v16_l98 bar-views)


(deftest
 t17_l100
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v16_l98)))


(def
 v19_l109
 (let
  [sk
   (->
    {:date
     [(java.time.LocalDate/of 2024 1 1)
      (java.time.LocalDate/of 2024 6 1)
      (java.time.LocalDate/of 2024 12 1)],
     :val [10 25 18]}
    (sk/lay-point :date :val)
    sk/sketch)
   p
   (first (:panels sk))]
  {:x-domain-numeric? (number? (first (:x-domain p))),
   :tick-labels (:labels (:x-ticks p))}))


(deftest
 t20_l118
 (is
  ((fn
    [m]
    (and (true? (:x-domain-numeric? m)) (not-empty (:tick-labels m))))
   v19_l109)))


(def v22_l144 (def hist-views (-> five-points (sk/view :x))))


(def v23_l148 (sk/sketch hist-views))


(deftest
 t24_l150
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :bar (:mark layer))))
   v23_l148)))


(def v25_l153 hist-views)


(deftest
 t26_l155
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v25_l153)))


(def v28_l163 (def count-views (-> animals (sk/view :animal))))


(def v29_l167 (sk/sketch count-views))


(deftest
 t30_l169
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :rect (:mark layer))))
   v29_l167)))


(def v31_l172 count-views)


(deftest
 t32_l174
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v31_l172)))


(def
 v34_l180
 (let
  [sk
   (->
    {:species ["a" "b" "c"], :val [10 20 15]}
    (sk/view :species :val)
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  (:mark layer)))


(deftest t35_l186 (is ((fn [m] (= :point m)) v34_l180)))


(def
 v37_l195
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/lay-point :x :y {:color :g}))))


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


(def v40_l208 colored-views)


(deftest
 t41_l210
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v40_l208)))


(def
 v43_l221
 (def
  fixed-color-views
  (-> five-points (sk/lay-point :x :y {:color "#E74C3C"}))))


(def v44_l225 (sk/sketch fixed-color-views))


(deftest
 t45_l227
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
   v44_l225)))


(def v46_l237 fixed-color-views)


(deftest
 t47_l239
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v46_l237)))


(def
 v49_l250
 (-> five-points (sk/lay-point :x :y {:color "steelblue"})))


(deftest
 t50_l253
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v49_l250)))


(def
 v52_l280
 (let
  [sk (-> five-points (sk/lay-point :x :y {:color "red"}) sk/sketch)]
  {:legend (:legend sk),
   :color
   (:color (first (:groups (first (:layers (first (:panels sk)))))))}))


(deftest
 t53_l285
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v52_l280)))


(def
 v55_l312
 (let
  [sk
   (sk/sketch colored-views)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t56_l318
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v55_l312)))


(def
 v58_l331
 (let
  [sk
   (->
    {:x [1 2 3 4 5], :y [2 4 3 5 4], :val [10 20 30 40 50]}
    (sk/lay-point :x :y {:color :val})
    sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :legend-type (:type (:legend sk))}))


(deftest
 t59_l339
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v58_l331)))


(def
 v61_l351
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v62_l356
 (let
  [sk
   (-> grouped-data (sk/lay-point :x :y {:group :g}) sk/sketch)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t63_l362
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v62_l356)))


(def v65_l377 (-> grouped-data (sk/view :x :y) sk/lay-point sk/lay-lm))


(deftest
 t66_l382
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v65_l377)))


(def
 v68_l388
 (->
  grouped-data
  (sk/view :x :y)
  (sk/lay-point {:color :g})
  (sk/lay-lm {:color :g})))


(deftest
 t69_l393
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v68_l388)))


(def
 v71_l406
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t72_l412
 (is
  ((fn
    [m]
    (and
     (== 0.8 (first (:x-domain m)))
     (== 5.2 (second (:x-domain m)))
     (== 0.2 (:padding-each-side m))))
   v71_l406)))


(def
 v74_l420
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  {:y-domain (:y-domain p)}))


(deftest t75_l424 (is ((fn [m] (<= (first (:y-domain m)) 0)) v74_l420)))


(def
 v77_l428
 (let
  [fill-sk
   (->
    {:x ["a" "a" "b" "b"], :g ["m" "n" "m" "n"]}
    (sk/lay-stacked-bar-fill :x {:color :g})
    sk/sketch)
   p
   (first (:panels fill-sk))]
  (:y-domain p)))


(deftest
 t78_l434
 (is ((fn [d] (and (= 0.0 (first d)) (= 1.0 (second d)))) v77_l428)))


(def
 v80_l443
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v81_l446
 (let
  [sk (-> iris (sk/lay-point :sepal_length :sepal_width) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t82_l451
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v81_l446)))


(def
 v84_l457
 (let
  [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t85_l461
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v84_l457)))


(def
 v87_l466
 (let
  [sk
   (->
    five-points
    (sk/lay-point :x :y)
    (sk/labs {:x "Length (cm)", :y "Width (cm)"})
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t88_l472
 (is ((fn [m] (= "Length (cm)" (:x-label m))) v87_l466)))


(def
 v90_l479
 (let
  [bare
   (sk/sketch scatter-views)
   full
   (->
    {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
    (sk/lay-point :x :y {:color :g})
    (sk/labs {:title "My Plot"})
    sk/sketch)]
  {:bare-layout (:layout bare),
   :bare-total-width (:total-width bare),
   :full-layout (:layout full),
   :full-total-width (:total-width full)}))


(deftest
 t91_l490
 (is
  ((fn
    [m]
    (and
     (zero? (get-in m [:bare-layout :title-pad]))
     (pos? (get-in m [:full-layout :title-pad]))
     (zero? (get-in m [:bare-layout :legend-w]))
     (= 100 (get-in m [:full-layout :legend-w]))
     (> (:full-total-width m) (:bare-total-width m))))
   v90_l479)))


(def
 v93_l505
 (def
  normal-sk
  (-> animals (sk/lay-value-bar :animal :count) sk/sketch)))


(def
 v94_l509
 (def
  flip-sk
  (->
   animals
   (sk/lay-value-bar :animal :count)
   (sk/coord :flip)
   sk/sketch)))


(def
 v95_l514
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t96_l521
 (is
  ((fn
    [m]
    (and
     (get-in m [:normal :x-categorical?])
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (get-in m [:flipped :y-categorical?])))
   v95_l514)))


(def
 v97_l526
 (-> animals (sk/lay-value-bar :animal :count) (sk/coord :flip)))


(deftest
 t98_l529
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v97_l526)))


(def
 v100_l536
 (let
  [sk (-> five-points (sk/lay-point :x :y) (sk/coord :flip) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t101_l542
 (is
  ((fn [m] (and (= "y" (:x-label m)) (= "x" (:y-label m)))) v100_l536)))


(def v103_l553 (:legend (sk/sketch colored-views)))


(deftest
 t104_l554
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v103_l553)))


(def v106_l561 (:legend (sk/sketch scatter-views)))


(deftest t107_l563 (is (nil? v106_l561)))


(def v109_l567 (:legend (sk/sketch fixed-color-views)))


(deftest t110_l569 (is (nil? v109_l567)))


(def
 v112_l575
 (def
  multi-views
  (-> five-points (sk/view :x :y) sk/lay-point sk/lay-lm)))


(def v113_l581 (sk/sketch multi-views))


(deftest
 t114_l583
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v113_l581)))


(def v115_l586 multi-views)


(deftest
 t116_l588
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v115_l586)))
