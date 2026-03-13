(ns
 napkinsketch-book.inference-rules-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l26
 (kind/mermaid
  "\ngraph TD\n  INPUT[\"User Input<br/>(data + columns + mark)\"] --> CT[\"Column Types<br/>numerical / categorical\"]\n  CT --> MARK[\"Default Mark<br/>point / bar / rect\"]\n  CT --> STAT[\"Default Stat<br/>identity / bin / count\"]\n  CT --> SCALE[\"Scale Type<br/>linear / categorical\"]\n  INPUT --> COLOR[\"Color Resolution<br/>column → groups + palette<br/>string → fixed RGBA<br/>nil → default gray\"]\n  INPUT --> GROUP[\"Grouping<br/>from color column\"]\n  STAT --> DOMAIN[\"Domain<br/>from data extent + padding\"]\n  SCALE --> TICKS[\"Tick Values + Labels<br/>from domain + pixel range\"]\n  DOMAIN --> LABEL[\"Axis Labels<br/>from column names\"]\n  DOMAIN --> LAYOUT[\"Layout<br/>padding from presence of title/labels/legend\"]\n  style INPUT fill:#e8f5e9\n  style CT fill:#fff3e0\n  style MARK fill:#fff3e0\n  style STAT fill:#fff3e0\n  style SCALE fill:#fff3e0\n  style COLOR fill:#fff3e0\n  style GROUP fill:#fff3e0\n  style DOMAIN fill:#e3f2fd\n  style TICKS fill:#e3f2fd\n  style LABEL fill:#e3f2fd\n  style LAYOUT fill:#e3f2fd\n"))


(def
 v4_l51
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v6_l67
 (def
  scatter-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})])))


(def
 v7_l70
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain-kind
   (if (number? (first (:x-domain p))) :numerical :categorical),
   :y-domain-kind
   (if (number? (first (:y-domain p))) :numerical :categorical),
   :x-scale-type (get-in p [:x-scale :type]),
   :y-scale-type (get-in p [:y-scale :type])}))


(deftest
 t8_l76
 (is
  ((fn
    [m]
    (and
     (= :numerical (:x-domain-kind m))
     (= :numerical (:y-domain-kind m))
     (= :linear (:x-scale-type m))))
   v7_l70)))


(def
 v10_l82
 (def bar-sk (sk/sketch [(sk/bar {:data iris, :x :species})])))


(def
 v12_l86
 (let
  [p (first (:panels bar-sk))]
  {:x-domain (:x-domain p),
   :x-ticks-categorical? (:categorical? (:x-ticks p))}))


(deftest
 t13_l90
 (is
  ((fn
    [m]
    (and (every? string? (:x-domain m)) (:x-ticks-categorical? m)))
   v12_l86)))


(def
 v15_l115
 (def
  hist-sk
  (sk/sketch
   (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram))))))


(def
 v16_l118
 (let
  [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)}))


(deftest t17_l121 (is ((fn [m] (= :bar (:mark m))) v16_l118)))


(def
 v19_l125
 (def
  count-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v20_l128
 (let
  [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)}))


(deftest t21_l131 (is ((fn [m] (= :rect (:mark m))) v20_l128)))


(def
 v23_l143
 (def
  colored-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v24_l146
 (let
  [layer (first (:layers (first (:panels colored-sk))))]
  (mapv
   (fn [g] {:color (:color g), :n (count (:xs g))})
   (:groups layer))))


(deftest
 t25_l150
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__78470#] (= 4 (count (:color p1__78470#)))) gs)))
   v24_l146)))


(def
 v27_l159
 (def
  fixed-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color "#E74C3C"})])))


(def
 v28_l163
 (let
  [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g)))


(deftest
 t29_l166
 (is ((fn [c] (and (= 4 (count c)) (> (first c) 0.8))) v28_l163)))


(def v31_l171 (:legend fixed-sk))


(deftest t32_l173 (is (nil? v31_l171)))


(def
 v34_l179
 (let
  [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g)))


(deftest t35_l182 (is ((fn [c] (= 4 (count c))) v34_l179)))


(def
 v37_l189
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
 v39_l195
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels grp-sk)))))


(deftest
 t40_l200
 (is
  ((fn
    [ls]
    (and
     (= 2 (count ls))
     (every? (fn* [p1__78471#] (= 3 (:n-groups p1__78471#))) ls)))
   v39_l195)))


(def
 v42_l215
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p),
   :actual-min
   (reduce min (map :sepal_length (tc/rows iris :as-maps))),
   :actual-max
   (reduce max (map :sepal_length (tc/rows iris :as-maps)))}))


(deftest
 t43_l220
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (:actual-min m))
     (> (second (:x-domain m)) (:actual-max m))))
   v42_l215)))


(def v45_l228 (let [p (first (:panels bar-sk))] (:x-domain p)))


(deftest t46_l231 (is ((fn [d] (= 3 (count d))) v45_l228)))


(def v48_l235 (let [p (first (:panels bar-sk))] (first (:y-domain p))))


(deftest t49_l238 (is ((fn [v] (<= v 0)) v48_l235)))


(def
 v51_l244
 (let
  [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t52_l248
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (second (:x-domain m)))
     (< (first (:y-domain m)) (second (:y-domain m)))))
   v51_l244)))


(def
 v54_l259
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v55_l262
 (let [p (first (:panels stacked-sk))] {:y-max (second (:y-domain p))}))


(deftest t56_l265 (is ((fn [m] (>= (:y-max m) 50)) v55_l262)))


(def v58_l278 (:x-label scatter-sk))


(deftest t59_l280 (is ((fn [l] (= "sepal length" l)) v58_l278)))


(def v60_l282 (:y-label scatter-sk))


(deftest t61_l284 (is ((fn [l] (= "sepal width" l)) v60_l282)))


(def v63_l291 (:y-label hist-sk))


(deftest t64_l293 (is (nil? v63_l291)))


(def
 v66_l297
 (def
  custom-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:x-label "Length (cm)", :y-label "Width (cm)"})))


(def v67_l300 (:x-label custom-sk))


(deftest t68_l302 (is ((fn [l] (= "Length (cm)" l)) v67_l300)))


(def
 v70_l314
 (let
  [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p))),
   :x-categorical? (:categorical? (:x-ticks p)),
   :first-x-tick (first (:values (:x-ticks p))),
   :first-x-label (first (:labels (:x-ticks p)))}))


(deftest
 t71_l320
 (is
  ((fn
    [m]
    (and
     (> (:n-x-ticks m) 2)
     (not (:x-categorical? m))
     (number? (:first-x-tick m))
     (string? (:first-x-label m))))
   v70_l314)))


(def
 v73_l327
 (let
  [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p)),
   :labels (:labels (:x-ticks p)),
   :categorical? (:categorical? (:x-ticks p))}))


(deftest
 t74_l332
 (is
  ((fn
    [m]
    (and
     (:categorical? m)
     (= (count (:values m)) (count (:labels m)))))
   v73_l327)))


(def v76_l350 (:layout scatter-sk))


(deftest t77_l352 (is ((fn [lay] (zero? (:title-pad lay))) v76_l350)))


(def
 v79_l356
 (def
  titled-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Plot"})))


(def v80_l359 (:layout titled-sk))


(deftest t81_l361 (is ((fn [lay] (pos? (:title-pad lay))) v80_l359)))


(def v83_l365 (:layout colored-sk))


(deftest t84_l367 (is ((fn [lay] (pos? (:legend-w lay))) v83_l365)))


(def v85_l369 (:layout scatter-sk))


(deftest t86_l371 (is ((fn [lay] (zero? (:legend-w lay))) v85_l369)))


(def
 v88_l375
 (select-keys scatter-sk [:width :height :total-width :total-height]))


(deftest
 t89_l377
 (is
  ((fn
    [m]
    (and
     (>= (:total-width m) (:width m))
     (>= (:total-height m) (:height m))))
   v88_l375)))


(def
 v91_l385
 (def normal-sk (sk/sketch [(sk/bar {:data iris, :x :species})])))


(def
 v92_l386
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def
 v93_l389
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np)),
   :normal-y-categorical? (:categorical? (:y-ticks np)),
   :flipped-x-categorical? (:categorical? (:x-ticks fp)),
   :flipped-y-categorical? (:categorical? (:y-ticks fp))}))


(deftest
 t94_l396
 (is
  ((fn
    [m]
    (and
     (:normal-x-categorical? m)
     (not (:normal-y-categorical? m))
     (not (:flipped-x-categorical? m))
     (:flipped-y-categorical? m)))
   v93_l389)))


(def v96_l414 (:legend colored-sk))


(deftest
 t97_l416
 (is
  ((fn
    [leg]
    (and (= :species (:title leg)) (= 3 (count (:entries leg)))))
   v96_l414)))


(def v98_l419 (:legend fixed-sk))


(deftest t99_l421 (is (nil? v98_l419)))


(def v100_l423 (:legend scatter-sk))


(deftest t101_l425 (is (nil? v100_l423)))
