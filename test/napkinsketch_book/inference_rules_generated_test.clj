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


(def v6_l40 (sk/sketch scatter-views))


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
  {:animal ["cat" "dog" "bird" "fish"], :count [12 8 15 5]}))


(def
 v13_l83
 (def
  bar-views
  (-> animals (sk/view :animal :count) (sk/lay (sk/value-bar)))))


(def v14_l88 (sk/sketch bar-views))


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


(def v20_l119 (sk/sketch hist-views))


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


(def v26_l138 (sk/sketch count-views))


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
   {:x [1 2 3 4 5 6], :y [3 5 4 7 6 8], :g ["a" "a" "a" "b" "b" "b"]}
   (sk/view :x :y)
   (sk/lay (sk/point {:color :g})))))


(def v32_l163 (sk/sketch colored-views))


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


(def v38_l184 (sk/sketch fixed-color-views))


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
 (let
  [sk (sk/sketch scatter-views) p (first (:panels sk))]
  {:x-domain (:x-domain p),
   :data-range [1.0 5.0],
   :padding-each-side (* 0.05 (- 5.0 1.0))}))


(deftest
 t44_l213
 (is
  ((fn
    [m]
    (and (< (first (:x-domain m)) 1.0) (> (second (:x-domain m)) 5.0)))
   v43_l207)))


(def
 v46_l220
 (let
  [sk (sk/sketch bar-views) p (first (:panels sk))]
  {:y-domain (:y-domain p)}))


(deftest t47_l224 (is ((fn [m] (<= (first (:y-domain m)) 0)) v46_l220)))


(def
 v49_l230
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v50_l233
 (let
  [sk
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t51_l240
 (is
  ((fn
    [m]
    (and
     (= "sepal length" (:x-label m))
     (= "sepal width" (:y-label m))))
   v50_l233)))


(def
 v53_l246
 (let
  [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk), :y-label (:y-label sk)}))


(deftest
 t54_l250
 (is
  ((fn [m] (and (= "x" (:x-label m)) (nil? (:y-label m)))) v53_l246)))


(def
 v56_l255
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
 t57_l263
 (is ((fn [m] (= "Length (cm)" (:x-label m))) v56_l255)))


(def
 v59_l270
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
 t60_l283
 (is
  ((fn
    [m]
    (and
     (zero? (get-in m [:bare-layout :title-pad]))
     (pos? (get-in m [:full-layout :title-pad]))
     (zero? (get-in m [:bare-layout :legend-w]))
     (pos? (get-in m [:full-layout :legend-w]))
     (> (:full-total-width m) (:bare-total-width m))))
   v59_l270)))


(def
 v62_l298
 (def
  normal-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   sk/sketch)))


(def
 v63_l304
 (def
  flip-sk
  (->
   animals
   (sk/view :animal :count)
   (sk/lay (sk/value-bar))
   (sk/coord :flip)
   sk/sketch)))


(def
 v64_l311
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal
   {:x-categorical? (:categorical? (:x-ticks np)),
    :y-categorical? (:categorical? (:y-ticks np))},
   :flipped
   {:x-categorical? (:categorical? (:x-ticks fp)),
    :y-categorical? (:categorical? (:y-ticks fp))}}))


(deftest
 t65_l318
 (is
  ((fn
    [m]
    (and
     (get-in m [:normal :x-categorical?])
     (not (get-in m [:normal :y-categorical?]))
     (not (get-in m [:flipped :x-categorical?]))
     (get-in m [:flipped :y-categorical?])))
   v64_l311)))


(def
 v66_l323
 (->
  animals
  (sk/view :animal :count)
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t67_l329
 (is ((fn [v] (= 4 (:polygons (sk/svg-summary v)))) v66_l323)))


(def v69_l338 (:legend (sk/sketch colored-views)))


(deftest
 t70_l339
 (is
  ((fn [leg] (and (= :g (:title leg)) (= 2 (count (:entries leg)))))
   v69_l338)))


(def v72_l346 (:legend (sk/sketch scatter-views)))


(deftest t73_l348 (is (nil? v72_l346)))


(def v75_l352 (:legend (sk/sketch fixed-color-views)))


(deftest t76_l354 (is (nil? v75_l352)))


(def
 v78_l360
 (def
  multi-views
  (-> five-points (sk/view :x :y) (sk/lay (sk/point) (sk/lm)))))


(def v79_l365 (sk/sketch multi-views))


(deftest
 t80_l367
 (is
  ((fn [sk] (let [p (first (:panels sk))] (= 2 (count (:layers p)))))
   v79_l365)))


(def v81_l370 (sk/plot multi-views))


(deftest
 t82_l372
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v81_l370)))
