(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (kind/mermaid
  "\ngraph TD\n  INPUT[\"User Input<br/>(data + columns + mark)\"] --> CT[\"Column Types<br/>numerical / categorical\"]\n  CT --> MARK[\"Default Mark<br/>point / bar / rect\"]\n  CT --> STAT[\"Default Stat<br/>identity / bin / count\"]\n  CT --> SCALE[\"Scale Type<br/>linear / categorical\"]\n  INPUT --> COLOR[\"Color Resolution<br/>column → groups + palette<br/>string → fixed RGBA<br/>nil → default gray\"]\n  INPUT --> GROUP[\"Grouping<br/>from color column\"]\n  STAT --> DOMAIN[\"Domain<br/>from data extent + padding\"]\n  SCALE --> TICKS[\"Tick Values + Labels<br/>from domain + pixel range\"]\n  DOMAIN --> LABEL[\"Axis Labels<br/>from column names\"]\n  DOMAIN --> LAYOUT[\"Layout<br/>padding from presence of title/labels/legend\"]\n  style INPUT fill:#e8f5e9\n  style CT fill:#fff3e0\n  style MARK fill:#fff3e0\n  style STAT fill:#fff3e0\n  style SCALE fill:#fff3e0\n  style COLOR fill:#fff3e0\n  style GROUP fill:#fff3e0\n  style DOMAIN fill:#e3f2fd\n  style TICKS fill:#e3f2fd\n  style LABEL fill:#e3f2fd\n  style LAYOUT fill:#e3f2fd\n"))


(def
 v4_l55
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v6_l71
 (def
  scatter-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})])))


(def
 v7_l74
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain-kind
   (if (number? (first (:x-domain p))) :numerical :categorical),
   :y-domain-kind
   (if (number? (first (:y-domain p))) :numerical :categorical),
   :x-scale-type (get-in p [:x-scale :type]),
   :y-scale-type (get-in p [:y-scale :type])}))


(deftest
 t8_l80
 (is
  ((fn
    [m]
    (and
     (= :numerical (:x-domain-kind m))
     (= :numerical (:y-domain-kind m))
     (= :linear (:x-scale-type m))))
   v7_l74)))


(def
 v10_l86
 (def bar-sk (sk/sketch [(sk/bar {:data iris, :x :species})])))


(def
 v12_l90
 (let
  [p (first (:panels bar-sk))]
  {:x-domain (:x-domain p),
   :x-ticks-categorical? (:categorical? (:x-ticks p))}))


(deftest
 t13_l94
 (is
  ((fn
    [m]
    (and (every? string? (:x-domain m)) (:x-ticks-categorical? m)))
   v12_l90)))


(def
 v15_l118
 (def
  hist-sk
  (sk/sketch
   (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram))))))


(def
 v16_l121
 (let
  [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)}))


(deftest t17_l124 (is ((fn [m] (= :bar (:mark m))) v16_l121)))


(def
 v19_l128
 (def
  count-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v20_l131
 (let
  [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)}))


(deftest t21_l134 (is ((fn [m] (= :rect (:mark m))) v20_l131)))


(def
 v23_l146
 (def
  colored-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v24_l149
 (let
  [layer (first (:layers (first (:panels colored-sk))))]
  (mapv
   (fn [g] {:color (:color g), :n (count (:xs g))})
   (:groups layer))))


(deftest
 t25_l153
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__77305#] (= 4 (count (:color p1__77305#)))) gs)))
   v24_l149)))


(def
 v27_l162
 (def
  fixed-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color "#E74C3C"})])))


(def
 v28_l166
 (let
  [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g)))


(deftest
 t29_l169
 (is ((fn [c] (and (= 4 (count c)) (> (first c) 0.8))) v28_l166)))


(def v31_l174 (:legend fixed-sk))


(deftest t32_l176 (is (nil? v31_l174)))


(def
 v34_l182
 (let
  [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g)))


(deftest t35_l185 (is ((fn [c] (= 4 (count c))) v34_l182)))


(def
 v37_l192
 (def
  grp-sk
  (sk/sketch
   [(sk/point
     {:data iris, :x :sepal_length, :y :sepal_width, :color :species})
    (sk/lm
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v39_l198
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels grp-sk)))))


(deftest
 t40_l203
 (is
  ((fn
    [ls]
    (and
     (= 2 (count ls))
     (every? (fn* [p1__77306#] (= 3 (:n-groups p1__77306#))) ls)))
   v39_l198)))


(def
 v42_l218
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p),
   :actual-min
   (reduce min (map :sepal_length (tc/rows iris :as-maps))),
   :actual-max
   (reduce max (map :sepal_length (tc/rows iris :as-maps)))}))


(deftest
 t43_l223
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (:actual-min m))
     (> (second (:x-domain m)) (:actual-max m))))
   v42_l218)))


(def v45_l231 (let [p (first (:panels bar-sk))] (:x-domain p)))


(deftest t46_l234 (is ((fn [d] (= 3 (count d))) v45_l231)))


(def v48_l238 (let [p (first (:panels bar-sk))] (first (:y-domain p))))


(deftest t49_l241 (is ((fn [v] (<= v 0)) v48_l238)))


(def
 v51_l247
 (let
  [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t52_l251
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (second (:x-domain m)))
     (< (first (:y-domain m)) (second (:y-domain m)))))
   v51_l247)))


(def
 v54_l262
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v55_l265
 (let [p (first (:panels stacked-sk))] {:y-max (second (:y-domain p))}))


(deftest t56_l268 (is ((fn [m] (>= (:y-max m) 50)) v55_l265)))


(def v58_l281 (:x-label scatter-sk))


(deftest t59_l283 (is ((fn [l] (= "sepal length" l)) v58_l281)))


(def v60_l285 (:y-label scatter-sk))


(deftest t61_l287 (is ((fn [l] (= "sepal width" l)) v60_l285)))


(def v63_l294 (:y-label hist-sk))


(deftest t64_l296 (is (nil? v63_l294)))


(def
 v66_l300
 (def
  custom-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:x-label "Length (cm)", :y-label "Width (cm)"})))


(def v67_l303 (:x-label custom-sk))


(deftest t68_l305 (is ((fn [l] (= "Length (cm)" l)) v67_l303)))


(def
 v70_l317
 (let
  [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p))),
   :x-categorical? (:categorical? (:x-ticks p)),
   :first-x-tick (first (:values (:x-ticks p))),
   :first-x-label (first (:labels (:x-ticks p)))}))


(deftest
 t71_l323
 (is
  ((fn
    [m]
    (and
     (> (:n-x-ticks m) 2)
     (not (:x-categorical? m))
     (number? (:first-x-tick m))
     (string? (:first-x-label m))))
   v70_l317)))


(def
 v73_l330
 (let
  [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p)),
   :labels (:labels (:x-ticks p)),
   :categorical? (:categorical? (:x-ticks p))}))


(deftest
 t74_l335
 (is
  ((fn
    [m]
    (and
     (:categorical? m)
     (= (count (:values m)) (count (:labels m)))))
   v73_l330)))


(def v76_l353 (:layout scatter-sk))


(deftest t77_l355 (is ((fn [lay] (zero? (:title-pad lay))) v76_l353)))


(def
 v79_l359
 (def
  titled-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Plot"})))


(def v80_l362 (:layout titled-sk))


(deftest t81_l364 (is ((fn [lay] (pos? (:title-pad lay))) v80_l362)))


(def v83_l368 (:layout colored-sk))


(deftest t84_l370 (is ((fn [lay] (pos? (:legend-w lay))) v83_l368)))


(def v85_l372 (:layout scatter-sk))


(deftest t86_l374 (is ((fn [lay] (zero? (:legend-w lay))) v85_l372)))


(def v88_l378 scatter-sk)


(deftest
 t89_l380
 (is
  ((fn
    [m]
    (and
     (>= (:total-width m) (:width m))
     (>= (:total-height m) (:height m))))
   v88_l378)))


(def
 v91_l388
 (def normal-sk (sk/sketch [(sk/bar {:data iris, :x :species})])))


(def
 v92_l389
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def
 v93_l392
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np)),
   :normal-y-categorical? (:categorical? (:y-ticks np)),
   :flipped-x-categorical? (:categorical? (:x-ticks fp)),
   :flipped-y-categorical? (:categorical? (:y-ticks fp))}))


(deftest
 t94_l399
 (is
  ((fn
    [m]
    (and
     (:normal-x-categorical? m)
     (not (:normal-y-categorical? m))
     (not (:flipped-x-categorical? m))
     (:flipped-y-categorical? m)))
   v93_l392)))


(def v96_l417 (:legend colored-sk))


(deftest
 t97_l419
 (is
  ((fn
    [leg]
    (and (= :species (:title leg)) (= 3 (count (:entries leg)))))
   v96_l417)))


(def v98_l422 (:legend fixed-sk))


(deftest t99_l424 (is (nil? v98_l422)))


(def v100_l426 (:legend scatter-sk))


(deftest t101_l428 (is (nil? v100_l426)))
