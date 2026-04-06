(ns
 napkinsketch-book.xkcd7-exploring-sketches-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3]}))


(def v5_l38 (-> tiny (sk/xkcd7-lay-point :x :y)))


(deftest
 t6_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l38)))


(def
 v8_l48
 (def tiny-pl (-> tiny (sk/xkcd7-lay-point :x :y) sk/xkcd7-plan)))


(def v10_l57 tiny-pl)


(deftest
 t11_l59
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
   v10_l57)))


(def v13_l78 (def tiny-panel (first (:panels tiny-pl))))


(def v14_l80 (keys tiny-panel))


(deftest
 t15_l82
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v14_l80)))


(def v17_l86 (:x-domain tiny-panel))


(deftest
 t18_l88
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v17_l86)))


(def v19_l90 (:y-domain tiny-panel))


(deftest
 t20_l92
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v19_l90)))


(def v22_l96 (:x-scale tiny-panel))


(deftest t23_l98 (is ((fn [s] (= :linear (:type s))) v22_l96)))


(def v25_l102 (:x-ticks tiny-panel))


(deftest
 t26_l104
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v25_l102)))


(def v28_l116 (def tiny-layer (first (:layers tiny-panel))))


(def v29_l118 tiny-layer)


(deftest t30_l120 (is ((fn [m] (= :point (:mark m))) v29_l118)))


(def v32_l125 (count (:groups tiny-layer)))


(deftest t33_l127 (is ((fn [n] (= 1 n)) v32_l125)))


(def v35_l132 (first (:groups tiny-layer)))


(deftest
 t36_l134
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v35_l132)))


(def
 v38_l148
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t39_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l148)))


(def
 v40_l155
 (def
  iris-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   sk/xkcd7-plan)))


(def v42_l161 iris-pl)


(deftest
 t43_l163
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v42_l161)))


(def
 v45_l168
 (def iris-layer (first (:layers (first (:panels iris-pl))))))


(def v46_l170 (count (:groups iris-layer)))


(deftest t47_l172 (is ((fn [n] (= 3 n)) v46_l170)))


(def
 v49_l176
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t50_l181
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__336205#] (= 50 (:n-points p1__336205#))) gs)))
   v49_l176)))


(def v52_l186 (:legend iris-pl))


(deftest
 t53_l188
 (is ((fn [leg] (= 3 (count (:entries leg)))) v52_l186)))


(def
 v55_l198
 (def
  cont-pl
  (->
   data/iris
   (sk/xkcd7-lay-point
    :sepal_length
    :sepal_width
    {:color :petal_length})
   sk/xkcd7-plan)))


(def v56_l202 (:legend cont-pl))


(deftest t57_l204 (is ((fn [m] (= :continuous (:type m))) v56_l202)))


(def
 v59_l208
 (select-keys (:legend cont-pl) [:title :type :min :max :color-scale]))


(deftest
 t60_l210
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v59_l208)))


(def v62_l215 (count (:stops (:legend cont-pl))))


(deftest t63_l217 (is ((fn [n] (= 20 n)) v62_l215)))


(def v65_l224 (-> data/iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t66_l227
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v65_l224)))


(def
 v67_l231
 (def
  hist-pl
  (-> data/iris (sk/xkcd7-lay-histogram :sepal_length) sk/xkcd7-plan)))


(def v68_l235 hist-pl)


(deftest t69_l237 (is ((fn [m] (= 1 (count (:panels m)))) v68_l235)))


(def
 v70_l239
 (def hist-layer (first (:layers (first (:panels hist-pl))))))


(def v71_l241 (:mark hist-layer))


(deftest t72_l243 (is ((fn [m] (= :bar m)) v71_l241)))


(def v74_l247 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t75_l250
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__336206#] (< (:lo p1__336206#) (:hi p1__336206#)))
      bars)
     (every? (fn* [p1__336207#] (pos? (:count p1__336207#))) bars)))
   v74_l247)))


(def
 v77_l262
 (-> data/penguins (sk/xkcd7-lay-bar :island {:color :species})))


(deftest
 t78_l265
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v77_l262)))


(def
 v79_l269
 (def
  bar-pl
  (->
   data/penguins
   (sk/xkcd7-lay-bar :island {:color :species})
   sk/xkcd7-plan)))


(def
 v80_l273
 (def bar-layer (first (:layers (first (:panels bar-pl))))))


(def v82_l277 bar-layer)


(deftest
 t83_l279
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v82_l277)))


(def
 v85_l285
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t86_l290 (is ((fn [gs] (= 3 (count gs))) v85_l285)))


(def
 v88_l299
 (def
  stacked-pl
  (->
   data/penguins
   (sk/xkcd7-lay-stacked-bar :island {:color :species})
   sk/xkcd7-plan)))


(def
 v89_l303
 (def stacked-layer (first (:layers (first (:panels stacked-pl))))))


(def v90_l305 (:position stacked-layer))


(deftest t91_l307 (is ((fn [p] (= :stack p)) v90_l305)))


(def
 v93_l316
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  sk/xkcd7-lay-lm))


(deftest
 t94_l320
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v93_l316)))


(def
 v95_l324
 (def
  lm-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width)
   sk/xkcd7-lay-lm
   sk/xkcd7-plan)))


(def v97_l331 (mapv :mark (:layers (first (:panels lm-pl)))))


(deftest t98_l332 (is ((fn [marks] (= [:point :line] marks)) v97_l331)))


(def v99_l333 (def lm-layer (second (:layers (first (:panels lm-pl))))))


(def v101_l337 (first (:groups lm-layer)))


(deftest
 t102_l339
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v101_l337)))


(def
 v104_l351
 (->
  data/iris
  (sk/xkcd7-view :petal_length :petal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t105_l356
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v104_l351)))


(def
 v106_l359
 (def
  grp-pl
  (->
   data/iris
   (sk/xkcd7-view :petal_length :petal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm
   sk/xkcd7-plan)))


(def
 v107_l365
 (let
  [line-layer (second (:layers (first (:panels grp-pl))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t108_l372 (is ((fn [gs] (= 3 (count gs))) v107_l365)))


(def
 v110_l380
 (def
  wave
  {:x (range 30),
   :y
   (map
    (fn* [p1__336208#] (Math/sin (* p1__336208# 0.3)))
    (range 30))}))


(def v111_l383 (-> wave (sk/xkcd7-lay-line :x :y)))


(deftest
 t112_l386
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v111_l383)))


(def
 v113_l390
 (def wave-pl (-> wave (sk/xkcd7-lay-line :x :y) sk/xkcd7-plan)))


(def
 v114_l394
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-pl))))))))


(def
 v115_l396
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t116_l400 (is ((fn [m] (= 30 (:n-points m))) v115_l396)))


(def
 v118_l409
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v119_l412 (-> sales (sk/xkcd7-lay-value-bar :product :revenue)))


(deftest
 t120_l415
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v119_l412)))


(def
 v121_l419
 (def
  sales-pl
  (-> sales (sk/xkcd7-lay-value-bar :product :revenue) sk/xkcd7-plan)))


(def
 v122_l423
 (let
  [g (first (:groups (first (:layers (first (:panels sales-pl))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t123_l427 (is ((fn [m] (= 4 (count (:xs m)))) v122_l423)))


(def
 v125_l433
 (def
  flip-pl
  (->
   data/iris
   (sk/xkcd7-lay-bar :species)
   (sk/xkcd7-coord :flip)
   sk/xkcd7-plan)))


(def v126_l438 (:coord (first (:panels flip-pl))))


(deftest t127_l440 (is ((fn [c] (= :flip c)) v126_l438)))


(def
 v129_l444
 (let
  [p (first (:panels flip-pl))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t130_l448
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v129_l444)))


(def
 v132_l458
 (def
  opts-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width)
   (sk/xkcd7-plan
    {:title "My Custom Title",
     :x-label "Length (cm)",
     :y-label "Width (cm)",
     :width 800,
     :height 300}))))


(def v133_l466 opts-pl)


(deftest
 t134_l468
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v133_l466)))


(def v136_l474 (:layout opts-pl))


(deftest
 t137_l476
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v136_l474)))


(def
 v139_l487
 (def
  final-xkcd7-sk
  (->
   data/iris
   (sk/xkcd7-view :petal_length :petal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def
 v140_l493
 (def final-pl (sk/xkcd7-plan final-xkcd7-sk {:title "Iris Petals"})))


(def v141_l495 final-pl)


(deftest
 t142_l497
 (is ((fn [m] (= "Iris Petals" (:title m))) v141_l495)))


(def
 v144_l501
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-pl)))))


(deftest t145_l506 (is ((fn [ls] (= 2 (count ls))) v144_l501)))


(def v147_l510 (-> final-xkcd7-sk (sk/xkcd7-options {:title "Iris Petals"})))


(deftest
 t148_l512
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v147_l510)))


(def
 v150_l521
 (def
  faceted-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-facet :species)
   sk/xkcd7-plan)))


(def v152_l529 (:grid faceted-pl))


(deftest
 t153_l531
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v152_l529)))


(def v155_l535 (count (:panels faceted-pl)))


(deftest t156_l537 (is ((fn [n] (= 3 n)) v155_l535)))


(def v158_l541 (:panels faceted-pl))


(deftest
 t159_l543
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v158_l541)))


(def v161_l548 (:panels faceted-pl))


(deftest t162_l550 (is ((fn [ps] (every? :x-domain ps)) v161_l548)))


(def
 v164_l557
 (select-keys
  faceted-pl
  [:layout-type :grid :total-width :total-height]))


(deftest
 t165_l559
 (is ((fn [m] (= :facet-grid (:layout-type m))) v164_l557)))


(def v167_l563 (sk/valid-plan? faceted-pl))


(deftest t168_l565 (is (true? v167_l563)))


(def v170_l575 (sk/valid-plan? tiny-pl))


(deftest t171_l577 (is (true? v170_l575)))


(def v172_l579 (sk/valid-plan? iris-pl))


(deftest t173_l581 (is (true? v172_l579)))


(def v174_l583 (sk/valid-plan? hist-pl))


(deftest t175_l585 (is (true? v174_l583)))


(def v176_l587 (sk/valid-plan? bar-pl))


(deftest t177_l589 (is (true? v176_l587)))


(def v178_l591 (sk/valid-plan? lm-pl))


(deftest t179_l593 (is (true? v178_l591)))


(def v180_l595 (sk/valid-plan? final-pl))


(deftest t181_l597 (is (true? v180_l595)))


(def v183_l601 (sk/explain-plan (assoc tiny-pl :width "not-a-number")))


(deftest t184_l603 (is (some? v183_l601)))


(def
 v186_l612
 (type
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t187_l614
 (is ((fn [t] (not= clojure.lang.PersistentVector t)) v186_l612)))


(def
 v189_l618
 (vec
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t190_l620
 (is ((fn [v] (and (vector? v) (number? (first v)))) v189_l618)))
