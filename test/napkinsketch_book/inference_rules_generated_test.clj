(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def five-points {:x [1.0 2.0 3.0 4.0 5.0], :y [2.1 4.3 3.0 5.2 4.8]}))


(def
 v4_l33
 (def
  scatter-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point)))))


(def v6_l40 (kind/pprint (sk/sketch scatter-views)))


(deftest
 t7_l42
 (is
  ((fn
    [sk]
    (and
     (= :single (:layout-type sk))
     (= 1 (count (:panels sk)))
     (= "x" (:x-label sk))
     (= "y" (:y-label sk))))
   v6_l40)))


(def v9_l49 (sk/plot scatter-views))


(deftest
 t10_l51
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v9_l49)))


(def
 v12_l79
 (def
  animals
  (tc/dataset
   {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]})))


(def
 v13_l83
 (def
  bar-views
  (-> animals (sk/view :animal :count) (sk/lay (sk/value-bar)))))


(def v14_l88 (kind/pprint (sk/sketch bar-views)))


(deftest
 t15_l90
 (is
  ((fn
    [sk]
    (let
     [p (first (:panels sk))]
     (and
      (= ["cat" "dog" "bird" "fish"] (:x-domain p))
      (true? (:categorical? (:x-ticks p))))))
   v14_l88)))


(def v16_l94 (sk/plot bar-views))


(deftest
 t17_l96
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v16_l94)))


(def v19_l115 (def hist-views (-> five-points (sk/view :x))))


(def v20_l119 (kind/pprint (sk/sketch hist-views)))


(deftest
 t21_l121
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :bar (:mark layer))))
   v20_l119)))


(def v22_l124 (sk/plot hist-views))


(deftest
 t23_l126
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v22_l124)))


(def v25_l134 (def count-views (-> animals (sk/view :animal))))


(def v26_l138 (kind/pprint (sk/sketch count-views)))


(deftest
 t27_l140
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (= :rect (:mark layer))))
   v26_l138)))


(def v28_l143 (sk/plot count-views))


(deftest
 t29_l145
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v28_l143)))


(def
 v31_l156
 (def
  colored-views
  (->
   (tc/dataset
    {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]})
   (sk/view :x :y)
   (sk/lay (sk/point {:color :g})))))


(def v32_l163 (kind/pprint (sk/sketch colored-views)))


(deftest
 t33_l165
 (is
  ((fn
    [sk]
    (let
     [layer (first (:layers (first (:panels sk))))]
     (and (= 2 (count (:groups layer))) (some? (:legend sk)))))
   v32_l163)))


(def v34_l169 (sk/plot colored-views))


(deftest
 t35_l171
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v34_l169)))


(def
 v37_l179
 (def
  fixed-color-views
  (->
   five-points
   (sk/view :x :y)
   (sk/lay (sk/point {:color "#E74C3C"})))))


(def v38_l184 (kind/pprint (sk/sketch fixed-color-views)))


(deftest
 t39_l186
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
   v38_l184)))


(def v40_l190 (sk/plot fixed-color-views))


(deftest
 t41_l192
 (is ((fn [v] (= 5 (:points (sk/svg-summary v)))) v40_l190)))


(def
 v43_l207
 (kind/pprint
  (let
   [sk (sk/sketch scatter-views) p (first (:panels sk))]
   {:x-domain (:x-domain p),
    :data-range [1.0 5.0],
    :padding-each-side (* 0.05 (- 5.0 1.0))})))


(deftest
 t44_l214
 (is
  ((fn
    [m]
    (and (< (first (:x-domain m)) 1.0) (> (second (:x-domain m)) 5.0)))
   v43_l207)))


(def
 v46_l221
 (kind/pprint
  (let
   [sk (sk/sketch bar-views) p (first (:panels sk))]
   {:y-domain (:y-domain p)})))


(deftest t47_l226 (is ((fn [m] (<= (first (:y-domain m)) 0)) v46_l221)))


(def
 v49_l232
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v50_l235
 (kind/pprint
  (let
   [sk
    (->
     iris
     (sk/view :sepal_length :sepal_width)
     (sk/lay (sk/point))
     sk/sketch)]
   {:x-label (:x-label sk), :y-label (:y-label sk)})))


(deftest
 t51_l243
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v50_l235)))


(def
 v53_l249
 (kind/pprint
  (let
   [sk (sk/sketch (-> five-points (sk/view :x)))]
   {:x-label (:x-label sk), :y-label (:y-label sk)})))


(deftest
 t54_l254
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v53_l249)))


(def
 v56_l259
 (kind/pprint
  (let
   [sk
    (->
     five-points
     (sk/view :x :y)
     (sk/lay (sk/point))
     (sk/labs {:x "Length (cm)", :y "Width (cm)"})
     sk/sketch)]
   {:x-label (:x-label sk), :y-label (:y-label sk)})))


(deftest
 t57_l268
 (is ((fn [m] (= "Length (cm)" (:x-label m))) v56_l259)))


(def
 v59_l275
 (kind/pprint
  (let
   [bare
    (sk/sketch scatter-views)
    full
    (->
     (tc/dataset
      {:x [1 2 3 4 5 6],
       :y [3 5 4 7 6 8],
       :g ["a" "a" "a" "b" "b" "b"]})
     (sk/view :x :y)
     (sk/lay (sk/point {:color :g}))
     (sk/labs {:title "My Plot"})
     sk/sketch)]
   {:bare-layout (:layout bare),
    :bare-total-width (:total-width bare),
    :full-layout (:layout full),
    :full-total-width (:total-width full)})))


(deftest
 t60_l289
 (is
  ((fn
    [m]
    (and
     (zero? (get-in m [:bare-layout :title-pad]))
     (pos? (get-in m [:full-layout :title-pad]))
     (zero? (get-in m [:bare-layout :legend-w]))
     (pos? (get-in m [:full-layout :legend-w]))
     (> (:full-total-width m) (:bare-total-width m))))
   v59_l275)))


(def
 v62_l304
 (def
  normal-sk
  (sk/sketch
   (-> animals (sk/view :animal :count) (sk/lay (sk/value-bar))))))


(def
 v63_l310
 (def
  flip-sk
  (sk/sketch
   (->
    animals
    (sk/view :animal :count)
    (sk/lay (sk/value-bar))
    (sk/coord :flip)))))


(def
 v64_l317
 (kind/pprint
  (let
   [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
   {:normal
    {:x-categorical? (:categorical? (:x-ticks np)),
     :y-categorical? (:categorical? (:y-ticks np))},
    :flipped
    {:x-categorical? (:categorical? (:x-ticks fp)),
     :y-categorical? (:categorical? (:y-ticks fp))}})))


(deftest
 t65_l325
 (is
  ((fn
    [m]
    (and
     (get-in m [:normal :x-categorical?])
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (get-in m [:flipped :y-categorical?])))
   v64_l317)))


(def
 v66_l330
 (sk/plot
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   (sk/coord :flip))))


(deftest
 t67_l335
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v66_l330)))


(def v69_l344 (kind/pprint (:legend (sk/sketch colored-views))))


(deftest
 t70_l346
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v69_l344)))


(def v72_l353 (kind/pprint (:legend (sk/sketch scatter-views))))


(def v73_l355 (:legend (sk/sketch scatter-views)))


(deftest t74_l357 (is (nil? v73_l355)))


(def v76_l361 (kind/pprint (:legend (sk/sketch fixed-color-views))))


(def v77_l363 (:legend (sk/sketch fixed-color-views)))


(deftest t78_l365 (is (nil? v77_l363)))


(def
 v80_l371
 (def
  multi-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point) (sk/lm)))))


(def v81_l376 (kind/pprint (sk/sketch multi-views)))


(deftest
 t82_l378
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v81_l376)))


(def v83_l381 (sk/plot multi-views))


(deftest
 t84_l383
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v83_l381)))
