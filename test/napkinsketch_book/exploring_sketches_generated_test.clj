(ns
 napkinsketch-book.exploring-sketches-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3]}))


(def v5_l29 (-> tiny (sk/lay-point :x :y)))


(deftest
 t6_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l29)))


(def v8_l39 (def tiny-pl (-> tiny (sk/lay-point :x :y) sk/plan)))


(def v10_l48 tiny-pl)


(deftest
 t11_l50
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
   v10_l48)))


(def v13_l69 (def tiny-panel (first (:panels tiny-pl))))


(def v14_l71 (keys tiny-panel))


(deftest
 t15_l73
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v14_l71)))


(def v17_l77 (:x-domain tiny-panel))


(deftest
 t18_l79
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v17_l77)))


(def v19_l81 (:y-domain tiny-panel))


(deftest
 t20_l83
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v19_l81)))


(def v22_l87 (:x-scale tiny-panel))


(deftest t23_l89 (is ((fn [s] (= :linear (:type s))) v22_l87)))


(def v25_l93 (:x-ticks tiny-panel))


(deftest
 t26_l95
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v25_l93)))


(def v28_l107 (def tiny-layer (first (:layers tiny-panel))))


(def v29_l109 tiny-layer)


(deftest t30_l111 (is ((fn [m] (= :point (:mark m))) v29_l109)))


(def v32_l116 (count (:groups tiny-layer)))


(deftest t33_l118 (is ((fn [n] (= 1 n)) v32_l116)))


(def v35_l123 (first (:groups tiny-layer)))


(deftest
 t36_l125
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v35_l123)))


(def
 v38_l139
 (->
  data/iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t39_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l139)))


(def
 v40_l146
 (def
  iris-pl
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   sk/plan)))


(def v42_l152 iris-pl)


(deftest
 t43_l154
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v42_l152)))


(def
 v45_l159
 (def iris-layer (first (:layers (first (:panels iris-pl))))))


(def v46_l161 (count (:groups iris-layer)))


(deftest t47_l163 (is ((fn [n] (= 3 n)) v46_l161)))


(def
 v49_l167
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t50_l172
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__90572#] (= 50 (:n-points p1__90572#))) gs)))
   v49_l167)))


(def v52_l177 (:legend iris-pl))


(deftest
 t53_l179
 (is ((fn [leg] (= 3 (count (:entries leg)))) v52_l177)))


(def
 v55_l189
 (def
  cont-pl
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :petal_length})
   sk/plan)))


(def v56_l193 (:legend cont-pl))


(deftest t57_l195 (is ((fn [m] (= :continuous (:type m))) v56_l193)))


(def
 v59_l199
 (select-keys (:legend cont-pl) [:title :type :min :max :color-scale]))


(deftest
 t60_l201
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v59_l199)))


(def v62_l206 (count (:stops (:legend cont-pl))))


(deftest t63_l208 (is ((fn [n] (= 20 n)) v62_l206)))


(def v65_l215 (-> data/iris (sk/lay-histogram :sepal_length)))


(deftest
 t66_l218
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v65_l215)))


(def
 v67_l222
 (def hist-pl (-> data/iris (sk/lay-histogram :sepal_length) sk/plan)))


(def v68_l226 hist-pl)


(deftest t69_l228 (is ((fn [m] (= 1 (count (:panels m)))) v68_l226)))


(def
 v70_l230
 (def hist-layer (first (:layers (first (:panels hist-pl))))))


(def v71_l232 (:mark hist-layer))


(deftest t72_l234 (is ((fn [m] (= :bar m)) v71_l232)))


(def v74_l238 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t75_l241
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__90573#] (< (:lo p1__90573#) (:hi p1__90573#)))
      bars)
     (every? (fn* [p1__90574#] (pos? (:count p1__90574#))) bars)))
   v74_l238)))


(def v77_l253 (-> data/penguins (sk/lay-bar :island {:color :species})))


(deftest
 t78_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v77_l253)))


(def
 v79_l260
 (def
  bar-pl
  (-> data/penguins (sk/lay-bar :island {:color :species}) sk/plan)))


(def
 v80_l264
 (def bar-layer (first (:layers (first (:panels bar-pl))))))


(def v82_l268 bar-layer)


(deftest
 t83_l270
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v82_l268)))


(def
 v85_l276
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t86_l281 (is ((fn [gs] (= 3 (count gs))) v85_l276)))


(def
 v88_l290
 (def
  stacked-pl
  (->
   data/penguins
   (sk/lay-stacked-bar :island {:color :species})
   sk/plan)))


(def
 v89_l294
 (def stacked-layer (first (:layers (first (:panels stacked-pl))))))


(def v90_l296 (:position stacked-layer))


(deftest t91_l298 (is ((fn [p] (= :stack p)) v90_l296)))


(def
 v93_l307
 (-> data/iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t94_l311
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v93_l307)))


(def
 v95_l315
 (def
  lm-pl
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width)
   sk/lay-lm
   sk/plan)))


(def v97_l322 (mapv :mark (:layers (first (:panels lm-pl)))))


(deftest t98_l323 (is ((fn [marks] (= [:point :line] marks)) v97_l322)))


(def v99_l324 (def lm-layer (second (:layers (first (:panels lm-pl))))))


(def v101_l328 (first (:groups lm-layer)))


(deftest
 t102_l330
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v101_l328)))


(def
 v104_l342
 (->
  data/iris
  (sk/view :petal_length :petal_width {:color :species})
  sk/lay-point
  sk/lay-lm))


(deftest
 t105_l347
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v104_l342)))


(def
 v106_l350
 (def
  grp-pl
  (->
   data/iris
   (sk/view :petal_length :petal_width {:color :species})
   sk/lay-point
   sk/lay-lm
   sk/plan)))


(def
 v107_l356
 (let
  [line-layer (second (:layers (first (:panels grp-pl))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t108_l363 (is ((fn [gs] (= 3 (count gs))) v107_l356)))


(def
 v110_l371
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__90575#] (Math/sin (* p1__90575# 0.3))) (range 30))}))


(def v111_l374 (-> wave (sk/lay-line :x :y)))


(deftest
 t112_l377
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v111_l374)))


(def v113_l381 (def wave-pl (-> wave (sk/lay-line :x :y) sk/plan)))


(def
 v114_l385
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-pl))))))))


(def
 v115_l387
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t116_l391 (is ((fn [m] (= 30 (:n-points m))) v115_l387)))


(def
 v118_l400
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v119_l403 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t120_l406
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v119_l403)))


(def
 v121_l410
 (def sales-pl (-> sales (sk/lay-value-bar :product :revenue) sk/plan)))


(def
 v122_l414
 (let
  [g (first (:groups (first (:layers (first (:panels sales-pl))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t123_l418 (is ((fn [m] (= 4 (count (:xs m)))) v122_l414)))


(def
 v125_l424
 (def
  flip-pl
  (-> data/iris (sk/lay-bar :species) (sk/coord :flip) sk/plan)))


(def v126_l429 (:coord (first (:panels flip-pl))))


(deftest t127_l431 (is ((fn [c] (= :flip c)) v126_l429)))


(def
 v129_l435
 (let
  [p (first (:panels flip-pl))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t130_l439
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v129_l435)))


(def
 v132_l449
 (def
  opts-pl
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width)
   (sk/plan
    {:title "My Custom Title",
     :x-label "Length (cm)",
     :y-label "Width (cm)",
     :width 800,
     :height 300}))))


(def v133_l457 opts-pl)


(deftest
 t134_l459
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v133_l457)))


(def v136_l465 (:layout opts-pl))


(deftest
 t137_l467
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v136_l465)))


(def
 v139_l478
 (def
  final-views
  (->
   data/iris
   (sk/view :petal_length :petal_width {:color :species})
   sk/lay-point
   sk/lay-lm)))


(def
 v140_l484
 (def final-pl (sk/plan final-views {:title "Iris Petals"})))


(def v141_l486 final-pl)


(deftest
 t142_l488
 (is ((fn [m] (= "Iris Petals" (:title m))) v141_l486)))


(def
 v144_l492
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-pl)))))


(deftest t145_l497 (is ((fn [ls] (= 2 (count ls))) v144_l492)))


(def v147_l501 (-> final-views (sk/options {:title "Iris Petals"})))


(deftest
 t148_l503
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v147_l501)))


(def
 v150_l512
 (def
  faceted-pl
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species})
   (sk/facet :species)
   sk/plan)))


(def v152_l520 (:grid faceted-pl))


(deftest
 t153_l522
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v152_l520)))


(def v155_l526 (count (:panels faceted-pl)))


(deftest t156_l528 (is ((fn [n] (= 3 n)) v155_l526)))


(def v158_l532 (:panels faceted-pl))


(deftest
 t159_l534
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v158_l532)))


(def v161_l539 (:panels faceted-pl))


(deftest t162_l541 (is ((fn [ps] (every? :x-domain ps)) v161_l539)))


(def
 v164_l548
 (select-keys
  faceted-pl
  [:layout-type :grid :total-width :total-height]))


(deftest
 t165_l550
 (is ((fn [m] (= :facet-grid (:layout-type m))) v164_l548)))


(def v167_l554 (sk/valid-plan? faceted-pl))


(deftest t168_l556 (is (true? v167_l554)))


(def v170_l566 (sk/valid-plan? tiny-pl))


(deftest t171_l568 (is (true? v170_l566)))


(def v172_l570 (sk/valid-plan? iris-pl))


(deftest t173_l572 (is (true? v172_l570)))


(def v174_l574 (sk/valid-plan? hist-pl))


(deftest t175_l576 (is (true? v174_l574)))


(def v176_l578 (sk/valid-plan? bar-pl))


(deftest t177_l580 (is (true? v176_l578)))


(def v178_l582 (sk/valid-plan? lm-pl))


(deftest t179_l584 (is (true? v178_l582)))


(def v180_l586 (sk/valid-plan? final-pl))


(deftest t181_l588 (is (true? v180_l586)))


(def v183_l592 (sk/explain-plan (assoc tiny-pl :width "not-a-number")))


(deftest t184_l594 (is (some? v183_l592)))


(def
 v186_l603
 (type
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t187_l605
 (is ((fn [t] (not= clojure.lang.PersistentVector t)) v186_l603)))


(def
 v189_l609
 (vec
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t190_l611
 (is ((fn [v] (and (vector? v) (number? (first v)))) v189_l609)))
