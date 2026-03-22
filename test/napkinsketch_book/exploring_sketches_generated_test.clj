(ns
 napkinsketch-book.exploring-sketches-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.pprint :as pp]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3]}))


(def v5_l29 (-> tiny (sk/lay-point :x :y) sk/plot))


(deftest
 t6_l33
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l29)))


(def v8_l40 (def tiny-sk (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v10_l49 tiny-sk)


(deftest
 t11_l51
 (is
  ((fn
    [m]
    (and
     (= 600 (:width m))
     (= 400 (:height m))
     (nil? (:title m))
     (= "x" (:x-label m))
     (= "y" (:y-label m))
     (nil? (:legend m))))
   v10_l49)))


(def v13_l70 (def tiny-panel (first (:panels tiny-sk))))


(def v14_l72 (keys tiny-panel))


(deftest
 t15_l74
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v14_l72)))


(def v17_l78 (:x-domain tiny-panel))


(deftest
 t18_l80
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v17_l78)))


(def v19_l82 (:y-domain tiny-panel))


(deftest
 t20_l84
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v19_l82)))


(def v22_l88 (:x-scale tiny-panel))


(deftest t23_l90 (is ((fn [s] (= :linear (:type s))) v22_l88)))


(def v25_l94 (:x-ticks tiny-panel))


(deftest
 t26_l96
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v25_l94)))


(def v28_l108 (def tiny-layer (first (:layers tiny-panel))))


(def v29_l110 tiny-layer)


(deftest t30_l112 (is ((fn [m] (= :point (:mark m))) v29_l110)))


(def v32_l117 (count (:groups tiny-layer)))


(deftest t33_l119 (is ((fn [n] (= 1 n)) v32_l117)))


(def v35_l124 (first (:groups tiny-layer)))


(deftest
 t36_l126
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v35_l124)))


(def
 v38_l140
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l143
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/plot))


(deftest
 t40_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l143)))


(def
 v41_l151
 (def
  iris-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   sk/sketch)))


(def v43_l157 iris-sk)


(deftest
 t44_l159
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v43_l157)))


(def
 v46_l164
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v47_l166 (count (:groups iris-layer)))


(deftest t48_l168 (is ((fn [n] (= 3 n)) v47_l166)))


(def
 v50_l172
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t51_l177
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__76381#] (= 50 (:n-points p1__76381#))) gs)))
   v50_l172)))


(def v53_l182 (:legend iris-sk))


(deftest
 t54_l184
 (is ((fn [leg] (= 3 (count (:entries leg)))) v53_l182)))


(def
 v56_l194
 (def
  cont-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width {:color :petal_length})
   sk/sketch)))


(def v57_l198 (:legend cont-sk))


(deftest t58_l200 (is ((fn [m] (= :continuous (:type m))) v57_l198)))


(def
 v60_l204
 (select-keys (:legend cont-sk) [:title :type :min :max :color-scale]))


(deftest
 t61_l206
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v60_l204)))


(def v63_l211 (count (:stops (:legend cont-sk))))


(deftest t64_l213 (is ((fn [n] (= 20 n)) v63_l211)))


(def v66_l220 (-> iris (sk/lay-histogram :sepal_length) sk/plot))


(deftest
 t67_l224
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v66_l220)))


(def
 v68_l228
 (def hist-sk (-> iris (sk/lay-histogram :sepal_length) sk/sketch)))


(def v69_l232 hist-sk)


(deftest t70_l234 (is ((fn [m] (= 1 (count (:panels m)))) v69_l232)))


(def
 v71_l236
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v72_l238 (:mark hist-layer))


(deftest t73_l240 (is ((fn [m] (= :bar m)) v72_l238)))


(def v75_l244 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t76_l247
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__76382#] (< (:lo p1__76382#) (:hi p1__76382#)))
      bars)
     (every? (fn* [p1__76383#] (pos? (:count p1__76383#))) bars)))
   v75_l244)))


(def v78_l259 (-> iris (sk/lay-bar :species {:color :species}) sk/plot))


(deftest
 t79_l263
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v78_l259)))


(def
 v80_l267
 (def
  bar-sk
  (-> iris (sk/lay-bar :species {:color :species}) sk/sketch)))


(def
 v81_l271
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v83_l275 bar-layer)


(deftest
 t84_l277
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v83_l275)))


(def
 v86_l283
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t87_l288 (is ((fn [gs] (= 3 (count gs))) v86_l283)))


(def
 v89_l297
 (def
  stacked-sk
  (-> iris (sk/lay-stacked-bar :species {:color :species}) sk/sketch)))


(def
 v90_l301
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v91_l303 (:position stacked-layer))


(deftest t92_l305 (is ((fn [p] (= :stack p)) v91_l303)))


(def
 v94_l314
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm sk/plot))


(deftest
 t95_l319
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v94_l314)))


(def
 v96_l323
 (def
  lm-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width)
   sk/lay-lm
   sk/sketch)))


(def v98_l330 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t99_l331 (is ((fn [marks] (= [:point :line] marks)) v98_l330)))


(def
 v100_l332
 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v102_l336 (first (:groups lm-layer)))


(deftest
 t103_l338
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v102_l336)))


(def
 v105_l350
 (->
  iris
  (sk/lay-point :petal_length :petal_width {:color :species})
  (sk/lay-lm {:color :species})
  sk/plot))


(deftest
 t106_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v105_l350)))


(def
 v107_l358
 (def
  grp-sk
  (->
   iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/lay-lm {:color :species})
   sk/sketch)))


(def
 v108_l363
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t109_l370 (is ((fn [gs] (= 3 (count gs))) v108_l363)))


(def
 v111_l378
 (def
  wave
  {:x (range 30),
   :y
   (mapv (fn* [p1__76384#] (Math/sin (* p1__76384# 0.3))) (range 30))}))


(def v112_l381 (-> wave (sk/lay-line :x :y) sk/plot))


(deftest
 t113_l385
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v112_l381)))


(def v114_l389 (def wave-sk (-> wave (sk/lay-line :x :y) sk/sketch)))


(def
 v115_l393
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v116_l395
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t117_l399 (is ((fn [m] (= 30 (:n-points m))) v116_l395)))


(def
 v119_l408
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v120_l411 (-> sales (sk/lay-value-bar :product :revenue) sk/plot))


(deftest
 t121_l415
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v120_l411)))


(def
 v122_l419
 (def
  sales-sk
  (-> sales (sk/lay-value-bar :product :revenue) sk/sketch)))


(def
 v123_l423
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t124_l427 (is ((fn [m] (= 4 (count (:xs m)))) v123_l423)))


(def
 v126_l433
 (def
  flip-sk
  (-> iris (sk/lay-bar :species) (sk/coord :flip) sk/sketch)))


(def v127_l438 (:coord (first (:panels flip-sk))))


(deftest t128_l440 (is ((fn [c] (= :flip c)) v127_l438)))


(def
 v130_l444
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t131_l448
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v130_l444)))


(def
 v133_l458
 (def
  opts-sk
  (->
   iris
   (sk/lay-point :sepal_length :sepal_width)
   (sk/sketch
    {:title "My Custom Title",
     :x-label "Length (cm)",
     :y-label "Width (cm)",
     :width 800,
     :height 300}))))


(def v134_l466 opts-sk)


(deftest
 t135_l468
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v134_l466)))


(def v137_l474 (:layout opts-sk))


(deftest
 t138_l476
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v137_l474)))


(def
 v140_l487
 (def
  final-views
  (->
   iris
   (sk/lay-point :petal_length :petal_width {:color :species})
   (sk/lay-lm {:color :species}))))


(def
 v141_l492
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def v142_l494 final-sk)


(deftest
 t143_l496
 (is ((fn [m] (= "Iris Petals" (:title m))) v142_l494)))


(def
 v145_l500
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t146_l505 (is ((fn [ls] (= 2 (count ls))) v145_l500)))


(def v148_l509 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t149_l511
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v148_l509)))


(def
 v151_l520
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay-point {:color :species})
   sk/sketch)))


(def v153_l529 (:grid faceted-sk))


(deftest
 t154_l531
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v153_l529)))


(def v156_l535 (count (:panels faceted-sk)))


(deftest t157_l537 (is ((fn [n] (= 3 n)) v156_l535)))


(def v159_l541 (:panels faceted-sk))


(deftest
 t160_l543
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v159_l541)))


(def v162_l548 (:panels faceted-sk))


(deftest t163_l550 (is ((fn [ps] (every? :x-domain ps)) v162_l548)))


(def
 v165_l557
 (select-keys
  faceted-sk
  [:layout-type :grid :total-width :total-height]))


(deftest
 t166_l559
 (is ((fn [m] (= :facet-grid (:layout-type m))) v165_l557)))


(def v168_l563 (sk/valid-sketch? faceted-sk))


(deftest t169_l565 (is (true? v168_l563)))


(def v171_l575 (sk/valid-sketch? tiny-sk))


(deftest t172_l577 (is (true? v171_l575)))


(def v173_l579 (sk/valid-sketch? iris-sk))


(deftest t174_l581 (is (true? v173_l579)))


(def v175_l583 (sk/valid-sketch? hist-sk))


(deftest t176_l585 (is (true? v175_l583)))


(def v177_l587 (sk/valid-sketch? bar-sk))


(deftest t178_l589 (is (true? v177_l587)))


(def v179_l591 (sk/valid-sketch? lm-sk))


(deftest t180_l593 (is (true? v179_l591)))


(def v181_l595 (sk/valid-sketch? final-sk))


(deftest t182_l597 (is (true? v181_l595)))


(def
 v184_l601
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t185_l603 (is (some? v184_l601)))


(def
 v187_l612
 (type
  (:xs (first (:groups (first (:layers (first (:panels tiny-sk)))))))))


(deftest
 t188_l614
 (is ((fn [t] (not= clojure.lang.PersistentVector t)) v187_l612)))


(def
 v190_l618
 (vec
  (:xs (first (:groups (first (:layers (first (:panels tiny-sk)))))))))


(deftest
 t191_l620
 (is ((fn [v] (and (vector? v) (number? (first v)))) v190_l618)))
