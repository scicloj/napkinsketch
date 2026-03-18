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


(def v10_l44 tiny-sk)


(deftest
 t11_l46
 (is ((fn [m] (and (= 600 (:width m)) (= 400 (:height m)))) v10_l44)))


(def v13_l53 tiny-sk)


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


(def v35_l110 tiny-layer)


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
     (every? (fn* [p1__79606#] (= 50 (:n-points p1__79606#))) gs)))
   v53_l161)))


(def v56_l171 (:legend iris-sk))


(deftest
 t57_l173
 (is ((fn [leg] (= 3 (count (:entries leg)))) v56_l171)))


(def
 v59_l184
 (def
  cont-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :petal_length})])))


(def
 v61_l189
 (select-keys (:legend cont-sk) [:title :type :min :max :color-scale]))


(deftest
 t62_l191
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v61_l189)))


(def v64_l196 (count (:stops (:legend cont-sk))))


(deftest t65_l198 (is ((fn [n] (= 20 n)) v64_l196)))


(def v67_l205 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(deftest
 t68_l207
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v67_l205)))


(def
 v69_l211
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def
 v70_l213
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v71_l215 (:mark hist-layer))


(deftest t72_l217 (is ((fn [m] (= :bar m)) v71_l215)))


(def v74_l221 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t75_l224
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__79607#] (< (:lo p1__79607#) (:hi p1__79607#)))
      bars)
     (every? (fn* [p1__79608#] (pos? (:count p1__79608#))) bars)))
   v74_l221)))


(def
 v77_l236
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(deftest
 t78_l238
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v77_l236)))


(def
 v79_l242
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v80_l244
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v82_l248 bar-layer)


(deftest
 t83_l250
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v82_l248)))


(def
 v85_l256
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t86_l261 (is ((fn [gs] (= 3 (count gs))) v85_l256)))


(def
 v88_l270
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v89_l272
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v90_l274 (:position stacked-layer))


(deftest t91_l276 (is ((fn [p] (= :stack p)) v90_l274)))


(def
 v93_l285
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(deftest
 t94_l288
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v93_l285)))


(def
 v95_l292
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v97_l297 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t98_l298 (is ((fn [marks] (= [:point :line] marks)) v97_l297)))


(def v99_l299 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v101_l303 (first (:groups lm-layer)))


(deftest
 t102_l305
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v101_l303)))


(def
 v104_l317
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t105_l320
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v104_l317)))


(def
 v106_l323
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
 v107_l326
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t108_l333 (is ((fn [gs] (= 3 (count gs))) v107_l326)))


(def
 v110_l341
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__79609#] (Math/sin (* p1__79609# 0.3)))
     (range 30))})))


(def v111_l344 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t112_l346
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v111_l344)))


(def
 v113_l350
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v114_l352
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v115_l354
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t116_l358 (is ((fn [m] (= 30 (:n-points m))) v115_l354)))


(def
 v118_l367
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v119_l370
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t120_l372
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v119_l370)))


(def
 v121_l376
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v122_l378
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t123_l382 (is ((fn [m] (= 4 (count (:xs m)))) v122_l378)))


(def
 v125_l388
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v126_l391 (:coord (first (:panels flip-sk))))


(deftest t127_l393 (is ((fn [c] (= :flip c)) v126_l391)))


(def
 v129_l397
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t130_l401
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v129_l397)))


(def
 v132_l411
 (def
  opts-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Custom Title",
    :x-label "Length (cm)",
    :y-label "Width (cm)",
    :width 800,
    :height 300})))


(def v133_l418 opts-sk)


(deftest
 t134_l420
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v133_l418)))


(def v136_l426 (:layout opts-sk))


(deftest
 t137_l428
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v136_l426)))


(def
 v139_l439
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v140_l443
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def v141_l445 final-sk)


(deftest
 t142_l447
 (is ((fn [m] (= "Iris Petals" (:title m))) v141_l445)))


(def
 v144_l451
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t145_l456 (is ((fn [ls] (= 2 (count ls))) v144_l451)))


(def v147_l460 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t148_l462
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v147_l460)))


(def
 v150_l471
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v152_l480 (:grid faceted-sk))


(deftest
 t153_l482
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v152_l480)))


(def v155_l486 (count (:panels faceted-sk)))


(deftest t156_l488 (is ((fn [n] (= 3 n)) v155_l486)))


(def v158_l492 (:panels faceted-sk))


(deftest
 t159_l494
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v158_l492)))


(def v161_l499 (:panels faceted-sk))


(deftest t162_l501 (is ((fn [ps] (every? :x-domain ps)) v161_l499)))


(def v164_l508 faceted-sk)


(deftest
 t165_l510
 (is ((fn [m] (= :facet-grid (:layout-type m))) v164_l508)))


(def v167_l514 (sk/valid-sketch? faceted-sk))


(deftest t168_l516 (is (true? v167_l514)))


(def v170_l526 (sk/valid-sketch? tiny-sk))


(deftest t171_l528 (is (true? v170_l526)))


(def v172_l530 (sk/valid-sketch? iris-sk))


(deftest t173_l532 (is (true? v172_l530)))


(def v174_l534 (sk/valid-sketch? hist-sk))


(deftest t175_l536 (is (true? v174_l534)))


(def v176_l538 (sk/valid-sketch? bar-sk))


(deftest t177_l540 (is (true? v176_l538)))


(def v178_l542 (sk/valid-sketch? lm-sk))


(deftest t179_l544 (is (true? v178_l542)))


(def v180_l546 (sk/valid-sketch? final-sk))


(deftest t181_l548 (is (true? v180_l546)))


(def
 v183_l552
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t184_l554 (is (some? v183_l552)))


(def
 v186_l562
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t187_l566 (is (true? v186_l562)))
