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
     (= "y" (:y-label sk))))
   v6_l39)))


(def v9_l48 (sk/plot scatter-views))


(deftest
 t10_l50
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l48)))


(def
 v12_l78
 (def
  animals
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v13_l82
 (def
  bar-views
  (-> animals (sk/view :animal :count) (sk/lay (sk/value-bar)))))


(def v14_l87 (sk/sketch bar-views))


(deftest
 t15_l89
 (is
  ((fn
    [sk]
    (let
     [p (first (:panels sk))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v14_l87)))


(def v16_l93 (sk/plot bar-views))


(deftest
 t17_l95
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v16_l93)))


(def v19_l114 (def hist-views (-> five-points (sk/view :x))))


(def v20_l118 (sk/sketch hist-views))


(deftest
 t21_l120
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :bar (:mark layer))))
   v20_l118)))


(def v22_l123 (sk/plot hist-views))


(deftest
 t23_l125
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v22_l123)))


(def v25_l133 (def count-views (-> animals (sk/view :animal))))


(def v26_l137 (sk/sketch count-views))


(deftest
 t27_l139
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :rect (:mark layer))))
   v26_l137)))


(def v28_l142 (sk/plot count-views))


(deftest
 t29_l144
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v28_l142)))


(def
 v31_l155
 (def
  colored-views
  (->
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/view :x :y)
   (sk/lay (sk/point {:color :g})))))


(def v32_l162 (sk/sketch colored-views))


(deftest
 t33_l164
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (and (= 2 (count (:groups layer))) (some? (:legend sk)))))
   v32_l162)))


(def v34_l168 (sk/plot colored-views))


(deftest
 t35_l170
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v34_l168)))


(def
 v37_l181
 (def
  fixed-color-views
  (->
   five-points
   (sk/view :x :y)
   (sk/lay (sk/point {:color "#E74C3C"})))))


(def v38_l186 (sk/sketch fixed-color-views))


(deftest
 t39_l188
 (is
  ((fn
    [sk]
    (and
     (nil? (:legend sk))
     (let
      [c
       (:color
        (first (:groups (first (:layers (first (:panels sk)))))))]
      (> (first c) 0.8))))
   v38_l186)))


(def v40_l192 (sk/plot fixed-color-views))


(deftest
 t41_l194
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v40_l192)))


(def
 v43_l205
 (->
  five-points
  (sk/view :x :y)
  (sk/lay (sk/point {:color "steelblue"}))
  sk/plot))


(deftest
 t44_l210
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v43_l205)))


(def
 v46_l237
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
 t47_l244
 (is
  ((fn [m] (and (nil? (:legend m)) (> (first (:color m)) 0.9)))
   v46_l237)))


(def
 v49_l271
 (let
  [sk
   (sk/sketch colored-views)
   layer
   (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer)),
   :group-labels (mapv :label (:groups layer)),
   :has-legend? (some? (:legend sk))}))


(deftest
 t50_l277
 (is
  ((fn
    [m]
    (and
     (= 2 (:group-count m))
     (= ["a" "b"] (:group-labels m))
     (true? (:has-legend? m))))
   v49_l271)))


(def
 v52_l290
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
 t53_l300
 (is
  ((fn
    [m]
    (and (= 1 (:group-count m)) (= :continuous (:legend-type m))))
   v52_l290)))


(def
 v55_l312
 (def
  grouped-data
  {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}))


(def
 v56_l317
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
 t57_l325
 (is
  ((fn [m] (and (= 2 (:group-count m)) (false? (:has-legend? m))))
   v56_l317)))


(def
 v59_l340
 (-> grouped-data (sk/view :x :y) (sk/lay (sk/point) (sk/lm)) sk/plot))


(deftest
 t60_l345
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v59_l340)))


(def
 v62_l351
 (->
  grouped-data
  (sk/view :x :y)
  (sk/lay (sk/point {:color :g}) (sk/lm {:color :g}))
  sk/plot))


(deftest
 t63_l357
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 2 (:lines s)))))
   v62_l351)))


(def
 v65_l370
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t66_l376
 (is
  ((fn
    [m]
    (and (< (first (:x-domain m)) 1.0) (> (second (:x-domain m)) 5.0)))
   v65_l370)))


(def
 v68_l383
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  {:y-domain (:y-domain p)}))


(deftest t69_l387 (is ((fn [m] (<= (first (:y-domain m)) 0)) v68_l383)))


(def
 v71_l393
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v72_l396
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t73_l403
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v72_l396)))


(def
 v75_l409
 (let
  [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t76_l413
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v75_l409)))


(def
 v78_l418
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
 t79_l426
 (is ((fn [m] (= "Length (cm)" (:x-label m))) v78_l418)))


(def
 v81_l433
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
 t82_l446
 (is
  ((fn
    [m]
    (and
     (zero? (get-in m [:bare-layout :title-pad]))
     (pos? (get-in m [:full-layout :title-pad]))
     (zero? (get-in m [:bare-layout :legend-w]))
     (pos? (get-in m [:full-layout :legend-w]))
     (> (:full-total-width m) (:bare-total-width m))))
   v81_l433)))


(def
 v84_l461
 (def
  normal-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   sk/sketch)))


(def
 v85_l467
 (def
  flip-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   (sk/coord :flip)
   sk/sketch)))


(def
 v86_l474
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t87_l481
 (is
  ((fn
    [m]
    (and
     (get-in m [:normal :x-categorical?])
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (get-in m [:flipped :y-categorical?])))
   v86_l474)))


(def
 v88_l486
 (->
  animals
  (sk/view :animal :count)
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t89_l492
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v88_l486)))


(def v91_l501 (:legend (sk/sketch colored-views)))


(deftest
 t92_l502
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v91_l501)))


(def v94_l509 (:legend (sk/sketch scatter-views)))


(deftest t95_l511 (is (nil? v94_l509)))


(def v97_l515 (:legend (sk/sketch fixed-color-views)))


(deftest t98_l517 (is (nil? v97_l515)))


(def
 v100_l523
 (def
  multi-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point) (sk/lm)))))


(def v101_l528 (sk/sketch multi-views))


(deftest
 t102_l530
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v101_l528)))


(def v103_l533 (sk/plot multi-views))


(deftest
 t104_l535
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v103_l533)))
