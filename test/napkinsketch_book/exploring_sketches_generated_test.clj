(ns
 napkinsketch-book.exploring-sketches-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.pprint :as pp]
  [clojure.test :refer [deftest is]]))


(def v3_l20 (def tiny (tc/dataset {:x [1 2 3 4 5], :y [2 4 1 5 3]})))


(def v5_l25 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(def
 v7_l30
 (def tiny-sk (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v9_l36
 (select-keys
  tiny-sk
  [:width :height :margin :total-width :total-height]))


(deftest
 t10_l38
 (is (fn v9_l36 [m] (and (= 600 (:width m)) (= 400 (:height m))))))


(def v12_l45 (select-keys tiny-sk [:title :x-label :y-label]))


(deftest
 t13_l47
 (is
  (fn
   v12_l45
   [m]
   (and (nil? (:title m)) (= "x" (:x-label m)) (= "y" (:y-label m))))))


(def v15_l53 (:legend tiny-sk))


(deftest t16_l55 (is (nil? v15_l53)))


(def v18_l62 (def tiny-panel (first (:panels tiny-sk))))


(def v19_l64 (keys tiny-panel))


(deftest
 t20_l66
 (is (fn v19_l64 [ks] (every? (set ks) [:x-domain :y-domain :layers]))))


(def v22_l70 (:x-domain tiny-panel))


(deftest
 t23_l72
 (is (fn v22_l70 [d] (and (<= (first d) 1) (>= (second d) 5)))))


(def v24_l74 (:y-domain tiny-panel))


(deftest
 t25_l76
 (is (fn v24_l74 [d] (and (<= (first d) 1) (>= (second d) 5)))))


(def v27_l80 (:x-scale tiny-panel))


(deftest t28_l82 (is (fn v27_l80 [s] (= :linear (:type s)))))


(def v30_l86 (:x-ticks tiny-panel))


(deftest
 t31_l88
 (is
  (fn
   v30_l86
   [t]
   (and
    (vector? (:values t))
    (vector? (:labels t))
    (= (count (:values t)) (count (:labels t)))))))


(def v33_l100 (def tiny-layer (first (:layers tiny-panel))))


(def v34_l102 (select-keys tiny-layer [:mark :style]))


(deftest t35_l104 (is (fn v34_l102 [m] (= :point (:mark m)))))


(def v37_l109 (count (:groups tiny-layer)))


(deftest t38_l111 (is (fn v37_l109 [n] (= 1 n))))


(def v40_l116 (first (:groups tiny-layer)))


(deftest
 t41_l118
 (is
  (fn
   v40_l116
   [g]
   (and
    (= 4 (count (:color g)))
    (= [1 2 3 4 5] (mapv int (:xs g)))
    (= [2 4 1 5 3] (mapv int (:ys g)))))))


(def
 v43_l132
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v44_l135
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(def
 v45_l137
 (def
  iris-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v47_l141
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v48_l143 (count (:groups iris-layer)))


(deftest t49_l145 (is (fn v48_l143 [n] (= 3 n))))


(def
 v51_l149
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t52_l154
 (is
  (fn
   v51_l149
   [gs]
   (and
    (= 3 (count gs))
    (every? (fn* [p1__77973#] (= 50 (:n-points p1__77973#))) gs)))))


(def v54_l159 (:legend iris-sk))


(deftest t55_l161 (is (fn v54_l159 [leg] (= 3 (count (:entries leg))))))


(def v57_l171 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(def
 v58_l173
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def
 v59_l175
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v60_l177 (:mark hist-layer))


(deftest t61_l179 (is (fn v60_l177 [m] (= :bar m))))


(def v63_l183 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t64_l186
 (is
  (fn
   v63_l183
   [bars]
   (and
    (> (count bars) 3)
    (every?
     (fn* [p1__77974#] (< (:lo p1__77974#) (:hi p1__77974#)))
     bars)
    (every? (fn* [p1__77975#] (pos? (:count p1__77975#))) bars)))))


(def
 v66_l198
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(def
 v67_l200
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v68_l202
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v70_l206 (select-keys bar-layer [:mark :position :categories]))


(deftest
 t71_l208
 (is
  (fn
   v70_l206
   [m]
   (and
    (= :rect (:mark m))
    (= :dodge (:position m))
    (= 3 (count (:categories m)))))))


(def
 v73_l214
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t74_l219 (is (fn v73_l214 [gs] (= 3 (count gs)))))


(def
 v76_l228
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v77_l230
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v78_l232 (:position stacked-layer))


(deftest t79_l234 (is (fn v78_l232 [p] (= :stack p))))


(def
 v81_l243
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(def
 v82_l246
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v84_l251 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t85_l253 (is (fn v84_l251 [marks] (= [:point :line] marks))))


(def v87_l257 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v88_l259 (:stat-origin lm-layer))


(deftest t89_l261 (is (fn v88_l259 [s] (= :lm s))))


(def
 v91_l265
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest
 t92_l268
 (is
  (fn
   v91_l265
   [m]
   (and (< (:x1 m) (:x2 m)) (every? number? (vals m))))))


(def
 v94_l279
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v95_l282
 (def
  grp-sk
  (sk/sketch
   [(sk/point
     {:data iris, :x :petal_length, :y :petal_width, :color :species})
    (sk/lm
     {:data iris,
      :x :petal_length,
      :y :petal_width,
      :color :species})])))


(def
 v96_l285
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t97_l292 (is (fn v96_l285 [gs] (= 3 (count gs)))))


(def
 v99_l300
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__77976#] (Math/sin (* p1__77976# 0.3)))
     (range 30))})))


(def v100_l303 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(def
 v101_l305
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v102_l307
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v103_l309
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t104_l313 (is (fn v103_l309 [m] (= 30 (:n-points m)))))


(def
 v106_l322
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v107_l325
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(def
 v108_l327
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v109_l329
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t110_l333 (is (fn v109_l329 [m] (= 4 (count (:xs m))))))


(def
 v112_l339
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v113_l342 (:coord (first (:panels flip-sk))))


(deftest t114_l344 (is (fn v113_l342 [c] (= :flip c))))


(def
 v116_l348
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t117_l352
 (is
  (fn
   v116_l348
   [m]
   (and
    (= :numeric (:x-domain-type m))
    (= :categorical (:y-domain-type m))))))


(def
 v119_l362
 (def
  opts-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Custom Title",
    :x-label "Length (cm)",
    :y-label "Width (cm)",
    :width 800,
    :height 300})))


(def
 v120_l369
 (select-keys opts-sk [:title :x-label :y-label :width :height]))


(deftest
 t121_l371
 (is
  (fn
   v120_l369
   [m]
   (and
    (= "My Custom Title" (:title m))
    (= 800 (:width m))
    (= 300 (:height m))))))


(def v123_l377 (:layout opts-sk))


(deftest
 t124_l379
 (is
  (fn
   v123_l377
   [lay]
   (and
    (pos? (:title-pad lay))
    (pos? (:x-label-pad lay))
    (pos? (:y-label-pad lay))))))


(def
 v126_l390
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v127_l394
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def
 v128_l396
 (select-keys final-sk [:title :x-label :y-label :width :height]))


(deftest t129_l398 (is (fn v128_l396 [m] (= "Iris Petals" (:title m)))))


(def
 v131_l402
 (mapv
  (fn
   [l]
   {:mark (:mark l),
    :n-groups (count (:groups l)),
    :stat-origin (:stat-origin l)})
  (:layers (first (:panels final-sk)))))


(deftest t132_l408 (is (fn v131_l402 [ls] (= 2 (count ls)))))


(def v134_l412 (sk/plot final-views {:title "Iris Petals"}))


(def v136_l419 (sk/valid-sketch? tiny-sk))


(deftest t137_l421 (is (true? v136_l419)))


(def v138_l423 (sk/valid-sketch? iris-sk))


(deftest t139_l425 (is (true? v138_l423)))


(def v140_l427 (sk/valid-sketch? hist-sk))


(deftest t141_l429 (is (true? v140_l427)))


(def v142_l431 (sk/valid-sketch? bar-sk))


(deftest t143_l433 (is (true? v142_l431)))


(def v144_l435 (sk/valid-sketch? lm-sk))


(deftest t145_l437 (is (true? v144_l435)))


(def v146_l439 (sk/valid-sketch? final-sk))


(deftest t147_l441 (is (true? v146_l439)))


(def
 v149_l445
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t150_l447 (is (some? v149_l445)))


(def
 v152_l455
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t153_l459 (is (true? v152_l455)))
