(ns
 napkinsketch-book.exploring-sketches-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.pprint :as pp]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (def tiny (tc/dataset {:x [1 2 3 4 5], :y [2 4 1 5 3]})))


(def v5_l29 (sk/plot [(sk/point {:data tiny, :x :x, :y :y})]))


(deftest
 t6_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l29)))


(def
 v8_l38
 (def tiny-sk (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v10_l44
 (select-keys
  tiny-sk
  [:width :height :margin :total-width :total-height]))


(deftest
 t11_l46
 (is ((fn [m] (and (= 600 (:width m)) (= 400 (:height m)))) v10_l44)))


(def v13_l53 (select-keys tiny-sk [:title :x-label :y-label]))


(deftest
 t14_l55
 (is
  ((fn
    [m]
    (and (nil? (:title m)) (= "x" (:x-label m)) (= "y" (:y-label m))))
   v13_l53)))


(def v16_l61 (:legend tiny-sk))


(deftest t17_l63 (is (nil? v16_l61)))


(def v19_l70 (def tiny-panel (first (:panels tiny-sk))))


(def v20_l72 (keys tiny-panel))


(deftest
 t21_l74
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v20_l72)))


(def v23_l78 (:x-domain tiny-panel))


(deftest
 t24_l80
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v23_l78)))


(def v25_l82 (:y-domain tiny-panel))


(deftest
 t26_l84
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v25_l82)))


(def v28_l88 (:x-scale tiny-panel))


(deftest t29_l90 (is ((fn [s] (= :linear (:type s))) v28_l88)))


(def v31_l94 (:x-ticks tiny-panel))


(deftest
 t32_l96
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v31_l94)))


(def v34_l108 (def tiny-layer (first (:layers tiny-panel))))


(def v35_l110 (select-keys tiny-layer [:mark :style]))


(deftest t36_l112 (is ((fn [m] (= :point (:mark m))) v35_l110)))


(def v38_l117 (count (:groups tiny-layer)))


(deftest t39_l119 (is ((fn [n] (= 1 n)) v38_l117)))


(def v41_l124 (first (:groups tiny-layer)))


(deftest
 t42_l126
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v41_l124)))


(def
 v44_l140
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v45_l143
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t46_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v45_l143)))


(def
 v47_l149
 (def
  iris-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v49_l153
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v50_l155 (count (:groups iris-layer)))


(deftest t51_l157 (is ((fn [n] (= 3 n)) v50_l155)))


(def
 v53_l161
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t54_l166
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__79466#] (= 50 (:n-points p1__79466#))) gs)))
   v53_l161)))


(def v56_l171 (:legend iris-sk))


(deftest
 t57_l173
 (is ((fn [leg] (= 3 (count (:entries leg)))) v56_l171)))


(def v59_l183 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(deftest
 t60_l185
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v59_l183)))


(def
 v61_l189
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def
 v62_l191
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v63_l193 (:mark hist-layer))


(deftest t64_l195 (is ((fn [m] (= :bar m)) v63_l193)))


(def v66_l199 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t67_l202
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__79467#] (< (:lo p1__79467#) (:hi p1__79467#)))
      bars)
     (every? (fn* [p1__79468#] (pos? (:count p1__79468#))) bars)))
   v66_l199)))


(def
 v69_l214
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(deftest
 t70_l216
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v69_l214)))


(def
 v71_l220
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v72_l222
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v74_l226 (select-keys bar-layer [:mark :position :categories]))


(deftest
 t75_l228
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v74_l226)))


(def
 v77_l234
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t78_l239 (is ((fn [gs] (= 3 (count gs))) v77_l234)))


(def
 v80_l248
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v81_l250
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v82_l252 (:position stacked-layer))


(deftest t83_l254 (is ((fn [p] (= :stack p)) v82_l252)))


(def
 v85_l263
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(deftest
 t86_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v85_l263)))


(def
 v87_l270
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v89_l275 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t90_l276 (is ((fn [marks] (= [:point :line] marks)) v89_l275)))


(def v91_l277 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def
 v93_l281
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest
 t94_l284
 (is
  ((fn [m] (and (< (:x1 m) (:x2 m)) (every? number? (vals m))))
   v93_l281)))


(def
 v96_l295
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t97_l298
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v96_l295)))


(def
 v98_l301
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
 v99_l304
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t100_l311 (is ((fn [gs] (= 3 (count gs))) v99_l304)))


(def
 v102_l319
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__79469#] (Math/sin (* p1__79469# 0.3)))
     (range 30))})))


(def v103_l322 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t104_l324
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v103_l322)))


(def
 v105_l328
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v106_l330
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v107_l332
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t108_l336 (is ((fn [m] (= 30 (:n-points m))) v107_l332)))


(def
 v110_l345
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v111_l348
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t112_l350
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v111_l348)))


(def
 v113_l354
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v114_l356
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t115_l360 (is ((fn [m] (= 4 (count (:xs m)))) v114_l356)))


(def
 v117_l366
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v118_l369 (:coord (first (:panels flip-sk))))


(deftest t119_l371 (is ((fn [c] (= :flip c)) v118_l369)))


(def
 v121_l375
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t122_l379
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v121_l375)))


(def
 v124_l389
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
 v125_l396
 (select-keys opts-sk [:title :x-label :y-label :width :height]))


(deftest
 t126_l398
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v125_l396)))


(def v128_l404 (:layout opts-sk))


(deftest
 t129_l406
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v128_l404)))


(def
 v131_l417
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v132_l421
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def
 v133_l423
 (select-keys final-sk [:title :x-label :y-label :width :height]))


(deftest
 t134_l425
 (is ((fn [m] (= "Iris Petals" (:title m))) v133_l423)))


(def
 v136_l429
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t137_l434 (is ((fn [ls] (= 2 (count ls))) v136_l429)))


(def v139_l438 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t140_l440
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v139_l438)))


(def
 v142_l449
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v144_l458 (:grid faceted-sk))


(deftest
 t145_l460
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v144_l458)))


(def v147_l464 (count (:panels faceted-sk)))


(deftest t148_l466 (is ((fn [n] (= 3 n)) v147_l464)))


(def
 v150_l470
 (mapv
  (fn* [p1__79470#] (select-keys p1__79470# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest
 t151_l473
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v150_l470)))


(def
 v153_l478
 (mapv
  (fn*
   [p1__79471#]
   (select-keys p1__79471# [:col-label :x-domain :y-domain]))
  (:panels faceted-sk)))


(deftest t154_l481 (is ((fn [ps] (every? :x-domain ps)) v153_l478)))


(def
 v156_l488
 (select-keys faceted-sk [:panel-width :panel-height :layout-type]))


(deftest
 t157_l490
 (is ((fn [m] (= :facet-grid (:layout-type m))) v156_l488)))


(def v159_l494 (sk/valid-sketch? faceted-sk))


(deftest t160_l496 (is (true? v159_l494)))


(def v162_l503 (sk/valid-sketch? tiny-sk))


(deftest t163_l505 (is (true? v162_l503)))


(def v164_l507 (sk/valid-sketch? iris-sk))


(deftest t165_l509 (is (true? v164_l507)))


(def v166_l511 (sk/valid-sketch? hist-sk))


(deftest t167_l513 (is (true? v166_l511)))


(def v168_l515 (sk/valid-sketch? bar-sk))


(deftest t169_l517 (is (true? v168_l515)))


(def v170_l519 (sk/valid-sketch? lm-sk))


(deftest t171_l521 (is (true? v170_l519)))


(def v172_l523 (sk/valid-sketch? final-sk))


(deftest t173_l525 (is (true? v172_l523)))


(def
 v175_l529
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t176_l531 (is (some? v175_l529)))


(def
 v178_l539
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t179_l543 (is (true? v178_l539)))
