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
 v11_l84
 (let
  [p (first (:panels bar-sk))]
  {:x-domain (:x-domain p), :x-scale-type (get-in p [:x-scale :type])}))


(deftest
 t12_l88
 (is ((fn [m] (= :categorical (:x-scale-type m))) v11_l84)))


(def
 v14_l111
 (def
  hist-sk
  (sk/sketch
   (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram))))))


(def
 v15_l114
 (let
  [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)}))


(deftest t16_l117 (is ((fn [m] (= :bar (:mark m))) v15_l114)))


(def
 v18_l121
 (def
  count-sk
  (sk/sketch (-> iris (sk/view :species) (sk/lay (sk/bar))))))


(def
 v19_l124
 (let
  [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)}))


(deftest t20_l127 (is ((fn [m] (= :rect (:mark m))) v19_l124)))


(def
 v22_l139
 (def
  colored-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v23_l142
 (let
  [layer (first (:layers (first (:panels colored-sk))))]
  (mapv
   (fn [g] {:color (:color g), :n (count (:xs g))})
   (:groups layer))))


(deftest
 t24_l146
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__81358#] (= 4 (count (:color p1__81358#)))) gs)))
   v23_l142)))


(def
 v26_l155
 (def
  fixed-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color "#E74C3C"})])))


(def
 v27_l159
 (let
  [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g)))


(deftest
 t28_l162
 (is ((fn [c] (and (= 4 (count c)) (> (first c) 0.8))) v27_l159)))


(def v30_l167 (:legend fixed-sk))


(deftest t31_l169 (is (nil? v30_l167)))


(def
 v33_l175
 (let
  [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g)))


(deftest t34_l178 (is ((fn [c] (= 4 (count c))) v33_l175)))


(def
 v36_l185
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
 v38_l191
 (mapv
  (fn [layer] {:mark (:mark layer), :n-groups (count (:groups layer))})
  (:layers (first (:panels grp-sk)))))


(deftest
 t39_l196
 (is
  ((fn
    [ls]
    (and
     (= 2 (count ls))
     (every? (fn* [p1__81359#] (= 3 (:n-groups p1__81359#))) ls)))
   v38_l191)))


(def
 v41_l211
 (let
  [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p),
   :actual-min
   (reduce min (map :sepal_length (tc/rows iris :as-maps))),
   :actual-max
   (reduce max (map :sepal_length (tc/rows iris :as-maps)))}))


(deftest
 t42_l216
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (:actual-min m))
     (> (second (:x-domain m)) (:actual-max m))))
   v41_l211)))


(def v44_l224 (let [p (first (:panels bar-sk))] (:x-domain p)))


(deftest t45_l227 (is ((fn [d] (= 3 (count d))) v44_l224)))


(def v47_l231 (let [p (first (:panels bar-sk))] (first (:y-domain p))))


(deftest t48_l234 (is (zero? v47_l231)))


(def
 v50_l240
 (let
  [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p), :y-domain (:y-domain p)}))


(deftest
 t51_l244
 (is
  ((fn
    [m]
    (and
     (< (first (:x-domain m)) (second (:x-domain m)))
     (< (first (:y-domain m)) (second (:y-domain m)))))
   v50_l240)))


(def
 v53_l255
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v54_l258
 (let [p (first (:panels stacked-sk))] {:y-max (second (:y-domain p))}))


(deftest t55_l261 (is ((fn [m] (>= (:y-max m) 50)) v54_l258)))


(def v57_l274 (:x-label scatter-sk))


(deftest t58_l276 (is ((fn [l] (= "sepal length" l)) v57_l274)))


(def v59_l278 (:y-label scatter-sk))


(deftest t60_l280 (is ((fn [l] (= "sepal width" l)) v59_l278)))


(def v62_l287 (:y-label hist-sk))


(deftest t63_l289 (is (nil? v62_l287)))


(def
 v65_l293
 (def
  custom-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:x-label "Length (cm)", :y-label "Width (cm)"})))


(def v66_l296 (:x-label custom-sk))


(deftest t67_l298 (is ((fn [l] (= "Length (cm)" l)) v66_l296)))


(def
 v69_l310
 (let
  [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p))),
   :x-categorical? (:categorical? (:x-ticks p)),
   :first-x-tick (first (:values (:x-ticks p))),
   :first-x-label (first (:labels (:x-ticks p)))}))


(deftest
 t70_l316
 (is
  ((fn
    [m]
    (and
     (> (:n-x-ticks m) 2)
     (not (:x-categorical? m))
     (number? (:first-x-tick m))
     (string? (:first-x-label m))))
   v69_l310)))


(def
 v72_l323
 (let
  [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p)),
   :labels (:labels (:x-ticks p)),
   :categorical? (:categorical? (:x-ticks p))}))


(deftest
 t73_l328
 (is
  ((fn
    [m]
    (and
     (:categorical? m)
     (= (count (:values m)) (count (:labels m)))))
   v72_l323)))


(def v75_l346 (:layout scatter-sk))


(deftest t76_l348 (is ((fn [lay] (zero? (:title-pad lay))) v75_l346)))


(def
 v78_l352
 (def
  titled-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Plot"})))


(def v79_l355 (:layout titled-sk))


(deftest t80_l357 (is ((fn [lay] (pos? (:title-pad lay))) v79_l355)))


(def v82_l361 (:layout colored-sk))


(deftest t83_l363 (is ((fn [lay] (pos? (:legend-w lay))) v82_l361)))


(def v84_l365 (:layout scatter-sk))


(deftest t85_l367 (is ((fn [lay] (zero? (:legend-w lay))) v84_l365)))


(def
 v87_l371
 (select-keys scatter-sk [:width :height :total-width :total-height]))


(deftest
 t88_l373
 (is
  ((fn
    [m]
    (and
     (>= (:total-width m) (:width m))
     (>= (:total-height m) (:height m))))
   v87_l371)))


(def
 v90_l381
 (def normal-sk (sk/sketch [(sk/bar {:data iris, :x :species})])))


(def
 v91_l382
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def
 v92_l385
 (let
  [np (first (:panels normal-sk)) fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np)),
   :normal-y-categorical? (:categorical? (:y-ticks np)),
   :flipped-x-categorical? (:categorical? (:x-ticks fp)),
   :flipped-y-categorical? (:categorical? (:y-ticks fp))}))


(deftest
 t93_l392
 (is
  ((fn
    [m]
    (and
     (:normal-x-categorical? m)
     (not (:normal-y-categorical? m))
     (not (:flipped-x-categorical? m))
     (:flipped-y-categorical? m)))
   v92_l385)))


(def v95_l410 (:legend colored-sk))


(deftest
 t96_l412
 (is
  ((fn
    [leg]
    (and (= :species (:title leg)) (= 3 (count (:entries leg)))))
   v95_l410)))


(def v97_l415 (:legend fixed-sk))


(deftest t98_l417 (is (nil? v97_l415)))


(def v99_l419 (:legend scatter-sk))


(deftest t100_l421 (is (nil? v99_l419)))
