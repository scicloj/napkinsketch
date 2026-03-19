(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (kind/mermaid
  "\ngraph TD\n  INPUT[\"User Input<br/>(data + columns + mark)\"] --> CT[\"Column Types<br/>numerical / categorical\"]\n  CT --> MARK[\"Default Mark<br/>point / bar / rect\"]\n  CT --> STAT[\"Default Stat<br/>identity / bin / count\"]\n  CT --> SCALE[\"Scale Type<br/>linear / categorical\"]\n  INPUT --> COLOR[\"Color Resolution<br/>column → groups + palette<br/>string → fixed RGBA<br/>nil → default gray\"]\n  INPUT --> GROUP[\"Grouping<br/>from color column\"]\n  STAT --> DOMAIN[\"Domain<br/>from data extent + padding\"]\n  SCALE --> TICKS[\"Tick Values + Labels<br/>from domain + pixel range\"]\n  DOMAIN --> LABEL[\"Axis Labels<br/>from column names\"]\n  DOMAIN --> LAYOUT[\"Layout<br/>padding from presence of title/labels/legend\"]\n  style INPUT fill:#e8f5e9\n  style CT fill:#fff3e0\n  style MARK fill:#fff3e0\n  style STAT fill:#fff3e0\n  style SCALE fill:#fff3e0\n  style COLOR fill:#fff3e0\n  style GROUP fill:#fff3e0\n  style DOMAIN fill:#e3f2fd\n  style TICKS fill:#e3f2fd\n  style LABEL fill:#e3f2fd\n  style LAYOUT fill:#e3f2fd\n"))


(def
 v4_l59
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v6_l76
 (def
  scatter-sk
  (sk/sketch
   (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point))))))


(def
 v7_l82
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain-kind
   (if (number? (first (:x-domain p))) :numerical :categorical),
   :y-domain-kind
   (if (number? (first (:y-domain p))) :numerical :categorical),
   :x-scale-type (get-in p [:x-scale :type]),
   :y-scale-type (get-in p [:y-scale :type])}))


(deftest
 t8_l88
 (is
  ((fn
    [m]
    (and
     (= :numerical (:x-domain-kind m))
     (= :numerical (:y-domain-kind m))
     (= :linear (:x-scale-type m))))
   v7_l82)))


(def
 v10_l94
 (def
  bar-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v12_l102
 (let
  [p (first (:panels bar-sk))]
  {:x-domain (:x-domain p),
   :x-ticks-categorical? (:categorical? (:x-ticks p))}))


(deftest
 t13_l106
 (is
  ((fn
    [m]
    (and (every? string? (:x-domain m)) (:x-ticks-categorical? m)))
   v12_l102)))


(def
 v15_l129
 (def hist-sk (sk/sketch (-> iris (sk/view :sepal_length)))))


(def
 v16_l133
 (let
  [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)}))


(deftest t17_l136 (is ((fn [m] (= :bar (:mark m))) v16_l133)))


(def v19_l140 (def count-sk (sk/sketch (-> iris (sk/view :species)))))


(def
 v20_l144
 (let
  [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)}))


(deftest t21_l147 (is ((fn [m] (= :rect (:mark m))) v20_l144)))


(def
 v23_l159
 (def
  colored-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))))))


(def
 v24_l165
 (let
  [layer (first (:layers (first (:panels colored-sk))))]
  (mapv
   (fn [g] {:color (:color g), :n (count (:xs g))})
   (:groups layer))))


(deftest
 t25_l169
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every?
      (fn* [p1__101640#] (= 4 (count (:color p1__101640#))))
      gs)))
   v24_l165)))


(def
 v27_l178
 (def
  fixed-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color "#E74C3C"}))))))


(def
 v28_l184
 (let
  [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g)))


(deftest
 t29_l187
 (is ((fn [c] (and (= 4 (count c)) (> (first c) 0.8))) v28_l184)))


(def v31_l192 (:legend fixed-sk))


(deftest t32_l194 (is (nil? v31_l192)))


(def
 v34_l200
 (let
  [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g)))


(deftest t35_l203 (is ((fn [c] (= 4 (count c))) v34_l200)))


(def
 v37_l210
 (def
  grp-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))))))


(def
 v39_l219
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels grp-sk)))))


(deftest
 t40_l224
 (is
  ((fn
    [ls]
    (and
     (= 2 (count ls))
     (every? (fn* [p1__101641#] (= 3 (:n-groups p1__101641#))) ls)))
   v39_l219)))


(def
 v42_l239
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p),
   :actual-min
   (reduce min (map :sepal_length (tc/rows iris :as-maps))),
   :actual-max
   (reduce max (map :sepal_length (tc/rows iris :as-maps)))}))


(deftest
 t43_l244
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (:actual-min m))
     (> (second (:x-domain m)) (:actual-max m))))
   v42_l239)))


(def v45_l252 (let [p (first (:panels bar-sk))] (:x-domain p)))


(deftest t46_l255 (is ((fn [d] (= 3 (count d))) v45_l252)))


(def v48_l259 (let [p (first (:panels bar-sk))] (first (:y-domain p))))


(deftest t49_l262 (is ((fn [v] (<= v 0)) v48_l259)))


(def
 v51_l268
 (let
  [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t52_l272
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (second (:x-domain m)))
     (< (first (:y-domain m)) (second (:y-domain m)))))
   v51_l268)))


(def
 v54_l283
 (def
  stacked-sk
  (sk/sketch
   (->
    iris
    (sk/view :species)
    (sk/lay (sk/stacked-bar {:color :species}))))))


(def
 v55_l289
 (let [p (first (:panels stacked-sk))] {:y-max (second (:y-domain p))}))


(deftest t56_l292 (is ((fn [m] (>= (:y-max m) 50)) v55_l289)))


(def v58_l305 (:x-label scatter-sk))


(deftest t59_l307 (is ((fn [l] (= "sepal length" l)) v58_l305)))


(def v60_l309 (:y-label scatter-sk))


(deftest t61_l311 (is ((fn [l] (= "sepal width" l)) v60_l309)))


(def v63_l318 (:y-label hist-sk))


(deftest t64_l320 (is (nil? v63_l318)))


(def
 v66_l324
 (def
  custom-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    (sk/labs {:x "Length (cm)", :y "Width (cm)"})))))


(def v67_l331 (:x-label custom-sk))


(deftest t68_l333 (is ((fn [l] (= "Length (cm)" l)) v67_l331)))


(def
 v70_l345
 (let
  [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p))),
   :x-categorical? (:categorical? (:x-ticks p)),
   :first-x-tick (first (:values (:x-ticks p))),
   :first-x-label (first (:labels (:x-ticks p)))}))


(deftest
 t71_l351
 (is
  ((fn
    [m]
    (and
     (> (:n-x-ticks m) 2)
     (not (:x-categorical? m))
     (number? (:first-x-tick m))
     (string? (:first-x-label m))))
   v70_l345)))


(def
 v73_l358
 (let
  [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p)),
   :labels (:labels (:x-ticks p)),
   :categorical? (:categorical? (:x-ticks p))}))


(deftest
 t74_l363
 (is
  ((fn
    [m]
    (and
     (:categorical? m)
     (= (count (:values m)) (count (:labels m)))))
   v73_l358)))


(def v76_l381 (:layout scatter-sk))


(deftest t77_l383 (is ((fn [lay] (zero? (:title-pad lay))) v76_l381)))


(def
 v79_l387
 (def
  titled-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    (sk/labs {:title "My Plot"})))))


(def v80_l394 (:layout titled-sk))


(deftest t81_l396 (is ((fn [lay] (pos? (:title-pad lay))) v80_l394)))


(def v83_l400 (:layout colored-sk))


(deftest t84_l402 (is ((fn [lay] (pos? (:legend-w lay))) v83_l400)))


(def v85_l404 (:layout scatter-sk))


(deftest t86_l406 (is ((fn [lay] (zero? (:legend-w lay))) v85_l404)))


(def v88_l410 scatter-sk)


(deftest
 t89_l412
 (is
  ((fn
    [m]
    (and
     (>= (:total-width m) (:width m))
     (>= (:total-height m) (:height m))))
   v88_l410)))


(def
 v91_l420
 (def
  normal-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v92_l425
 (def
  flip-sk
  (sk/sketch
   (-> iris (sk/view :species) (sk/lay (sk/bar)) (sk/coord :flip)))))


(def
 v93_l432
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np)),
   :normal-y-categorical? (:categorical? (:y-ticks np)),
   :flipped-x-categorical? (:categorical? (:x-ticks fp)),
   :flipped-y-categorical? (:categorical? (:y-ticks fp))}))


(deftest
 t94_l439
 (is
  ((fn
    [m]
    (and
     (:normal-x-categorical? m)
     (not (:normal-y-categorical? m))
     (not (:flipped-x-categorical? m))
     (:flipped-y-categorical? m)))
   v93_l432)))


(def v96_l457 (:legend colored-sk))


(deftest
 t97_l459
 (is
  ((fn
    [leg]
    (and (= :species (:title leg)) (= 3 (count (:entries leg)))))
   v96_l457)))


(def v98_l462 (:legend fixed-sk))


(deftest t99_l464 (is (nil? v98_l462)))


(def v100_l466 (:legend scatter-sk))


(deftest t101_l468 (is (nil? v100_l466)))
