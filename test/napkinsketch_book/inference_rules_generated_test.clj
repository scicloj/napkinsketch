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
  scatter-views
  (-> iris (sk/view :sepal_length :sepal_width) (sk/lay (sk/point)))))


(def v7_l81 (sk/plot scatter-views))


(def v8_l83 (def scatter-sk (sk/sketch scatter-views)))


(def v10_l87 (-> scatter-sk :panels first :x-scale))


(deftest t11_l89 (is ((fn [s] (= :linear (:type s))) v10_l87)))


(def v12_l91 (-> scatter-sk :panels first :x-domain))


(deftest t13_l93 (is ((fn [d] (every? number? d)) v12_l91)))


(def
 v15_l97
 (def bar-views (-> iris (sk/view :species) (sk/lay (sk/bar)))))


(def v16_l102 (sk/plot bar-views))


(def v17_l104 (def bar-sk (sk/sketch bar-views)))


(def v19_l108 (-> bar-sk :panels first :x-domain))


(deftest t20_l110 (is ((fn [d] (every? string? d)) v19_l108)))


(def v21_l112 (-> bar-sk :panels first :x-ticks :categorical?))


(deftest t22_l114 (is ((fn [v] (true? v)) v21_l112)))


(def
 v24_l136
 (def hist-sk (sk/sketch (-> iris (sk/view :sepal_length)))))


(def
 v25_l140
 (let
  [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)}))


(deftest t26_l143 (is ((fn [m] (= :bar (:mark m))) v25_l140)))


(def v28_l147 (def count-sk (sk/sketch (-> iris (sk/view :species)))))


(def
 v29_l151
 (let
  [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)}))


(deftest t30_l154 (is ((fn [m] (= :rect (:mark m))) v29_l151)))


(def
 v32_l166
 (def
  colored-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))))))


(def
 v33_l172
 (let
  [layer (first (:layers (first (:panels colored-sk))))]
  (mapv
   (fn [g] {:color (:color g), :n (count (:xs g))})
   (:groups layer))))


(deftest
 t34_l176
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every?
      (fn* [p1__114045#] (= 4 (count (:color p1__114045#))))
      gs)))
   v33_l172)))


(def
 v36_l185
 (def
  fixed-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color "#E74C3C"}))))))


(def
 v37_l191
 (let
  [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g)))


(deftest
 t38_l194
 (is ((fn [c] (and (= 4 (count c)) (> (first c) 0.8))) v37_l191)))


(def v40_l199 (:legend fixed-sk))


(deftest t41_l201 (is (nil? v40_l199)))


(def
 v43_l207
 (let
  [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g)))


(deftest t44_l210 (is ((fn [c] (= 4 (count c))) v43_l207)))


(def
 v46_l217
 (def
  grp-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))))))


(def
 v48_l226
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels grp-sk)))))


(deftest
 t49_l231
 (is
  ((fn
    [ls]
    (and
     (= 2 (count ls))
     (every? (fn* [p1__114046#] (= 3 (:n-groups p1__114046#))) ls)))
   v48_l226)))


(def
 v51_l246
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p),
   :actual-min
   (reduce min (map :sepal_length (tc/rows iris :as-maps))),
   :actual-max
   (reduce max (map :sepal_length (tc/rows iris :as-maps)))}))


(deftest
 t52_l251
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (:actual-min m))
     (> (second (:x-domain m)) (:actual-max m))))
   v51_l246)))


(def v54_l259 (let [p (first (:panels bar-sk))] (:x-domain p)))


(deftest t55_l262 (is ((fn [d] (= 3 (count d))) v54_l259)))


(def v57_l266 (let [p (first (:panels bar-sk))] (first (:y-domain p))))


(deftest t58_l269 (is ((fn [v] (<= v 0)) v57_l266)))


(def
 v60_l275
 (let
  [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t61_l279
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (second (:x-domain m)))
     (< (first (:y-domain m)) (second (:y-domain m)))))
   v60_l275)))


(def
 v63_l290
 (def
  stacked-sk
  (sk/sketch
   (->
    iris
    (sk/view :species)
    (sk/lay (sk/stacked-bar {:color :species}))))))


(def
 v64_l296
 (let [p (first (:panels stacked-sk))] {:y-max (second (:y-domain p))}))


(deftest t65_l299 (is ((fn [m] (>= (:y-max m) 50)) v64_l296)))


(def v67_l312 (:x-label scatter-sk))


(deftest t68_l314 (is ((fn [l] (= "sepal length" l)) v67_l312)))


(def v69_l316 (:y-label scatter-sk))


(deftest t70_l318 (is ((fn [l] (= "sepal width" l)) v69_l316)))


(def v72_l325 (:y-label hist-sk))


(deftest t73_l327 (is (nil? v72_l325)))


(def
 v75_l331
 (def
  custom-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    (sk/labs {:x "Length (cm)", :y "Width (cm)"})))))


(def v76_l338 (:x-label custom-sk))


(deftest t77_l340 (is ((fn [l] (= "Length (cm)" l)) v76_l338)))


(def
 v79_l352
 (let
  [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p))),
   :x-categorical? (:categorical? (:x-ticks p)),
   :first-x-tick (first (:values (:x-ticks p))),
   :first-x-label (first (:labels (:x-ticks p)))}))


(deftest
 t80_l358
 (is
  ((fn
    [m]
    (and
     (> (:n-x-ticks m) 2)
     (not (:x-categorical? m))
     (number? (:first-x-tick m))
     (string? (:first-x-label m))))
   v79_l352)))


(def
 v82_l365
 (let
  [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p)),
   :labels (:labels (:x-ticks p)),
   :categorical? (:categorical? (:x-ticks p))}))


(deftest
 t83_l370
 (is
  ((fn
    [m]
    (and
     (:categorical? m)
     (= (count (:values m)) (count (:labels m)))))
   v82_l365)))


(def v85_l388 (:layout scatter-sk))


(deftest t86_l390 (is ((fn [lay] (zero? (:title-pad lay))) v85_l388)))


(def
 v88_l394
 (def
  titled-sk
  (sk/sketch
   (->
    iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    (sk/labs {:title "My Plot"})))))


(def v89_l401 (:layout titled-sk))


(deftest t90_l403 (is ((fn [lay] (pos? (:title-pad lay))) v89_l401)))


(def v92_l407 (:layout colored-sk))


(deftest t93_l409 (is ((fn [lay] (pos? (:legend-w lay))) v92_l407)))


(def v94_l411 (:layout scatter-sk))


(deftest t95_l413 (is ((fn [lay] (zero? (:legend-w lay))) v94_l411)))


(def v97_l417 scatter-sk)


(deftest
 t98_l419
 (is
  ((fn
    [m]
    (and
     (>= (:total-width m) (:width m))
     (>= (:total-height m) (:height m))))
   v97_l417)))


(def
 v100_l427
 (def
  normal-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v101_l432
 (def
  flip-sk
  (sk/sketch
   (-> iris (sk/view :species) (sk/lay (sk/bar)) (sk/coord :flip)))))


(def
 v102_l439
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np)),
   :normal-y-categorical? (:categorical? (:y-ticks np)),
   :flipped-x-categorical? (:categorical? (:x-ticks fp)),
   :flipped-y-categorical? (:categorical? (:y-ticks fp))}))


(deftest
 t103_l446
 (is
  ((fn
    [m]
    (and
     (:normal-x-categorical? m)
     (not (:normal-y-categorical? m))
     (not (:flipped-x-categorical? m))
     (:flipped-y-categorical? m)))
   v102_l439)))


(def v105_l464 (:legend colored-sk))


(deftest
 t106_l466
 (is
  ((fn
    [leg]
    (and (= :species (:title leg)) (= 3 (count (:entries leg)))))
   v105_l464)))


(def v107_l469 (:legend fixed-sk))


(deftest t108_l471 (is (nil? v107_l469)))


(def v109_l473 (:legend scatter-sk))


(deftest t110_l475 (is (nil? v109_l473)))
