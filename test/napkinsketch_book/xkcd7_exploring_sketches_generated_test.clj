(ns
 napkinsketch-book.xkcd7-exploring-sketches-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.method :as method]
  [clojure.test :refer [deftest is]]))


(def v3_l25 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3]}))


(def v5_l30 (-> tiny (sk/xkcd7-lay-point :x :y)))


(deftest
 t6_l33
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l30)))


(def
 v8_l40
 (def tiny-pl (-> tiny (sk/xkcd7-lay-point :x :y) sk/xkcd7-plan)))


(def v10_l49 tiny-pl)


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


(def v13_l70 (def tiny-panel (first (:panels tiny-pl))))


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
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t39_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l140)))


(def
 v40_l147
 (def
  iris-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   sk/xkcd7-plan)))


(def v42_l153 iris-pl)


(deftest
 t43_l155
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v42_l153)))


(def
 v45_l160
 (def iris-layer (first (:layers (first (:panels iris-pl))))))


(def v46_l162 (count (:groups iris-layer)))


(deftest t47_l164 (is ((fn [n] (= 3 n)) v46_l162)))


(def
 v49_l168
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t50_l173
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__335062#] (= 50 (:n-points p1__335062#))) gs)))
   v49_l168)))


(def v52_l178 (:legend iris-pl))


(deftest
 t53_l180
 (is ((fn [leg] (= 3 (count (:entries leg)))) v52_l178)))


(def
 v55_l190
 (def
  cont-pl
  (->
   data/iris
   (sk/xkcd7-lay-point
    :sepal_length
    :sepal_width
    {:color :petal_length})
   sk/xkcd7-plan)))


(def v56_l194 (:legend cont-pl))


(deftest t57_l196 (is ((fn [m] (= :continuous (:type m))) v56_l194)))


(def
 v59_l200
 (select-keys (:legend cont-pl) [:title :type :min :max :color-scale]))


(deftest
 t60_l202
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v59_l200)))


(def v62_l207 (count (:stops (:legend cont-pl))))


(deftest t63_l209 (is ((fn [n] (= 20 n)) v62_l207)))


(def v65_l216 (-> data/iris (sk/xkcd7-lay-histogram :sepal_length)))


(deftest
 t66_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v65_l216)))


(def
 v67_l223
 (def
  hist-pl
  (-> data/iris (sk/xkcd7-lay-histogram :sepal_length) sk/xkcd7-plan)))


(def v68_l227 hist-pl)


(deftest t69_l229 (is ((fn [m] (= 1 (count (:panels m)))) v68_l227)))


(def
 v70_l231
 (def hist-layer (first (:layers (first (:panels hist-pl))))))


(def v71_l233 (:mark hist-layer))


(deftest t72_l235 (is ((fn [m] (= :bar m)) v71_l233)))


(def v74_l239 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t75_l242
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__335063#] (< (:lo p1__335063#) (:hi p1__335063#)))
      bars)
     (every? (fn* [p1__335064#] (pos? (:count p1__335064#))) bars)))
   v74_l239)))


(def
 v77_l254
 (-> data/penguins (sk/xkcd7-lay-bar :island {:color :species})))


(deftest
 t78_l257
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v77_l254)))


(def
 v79_l261
 (def
  bar-pl
  (->
   data/penguins
   (sk/xkcd7-lay-bar :island {:color :species})
   sk/xkcd7-plan)))


(def
 v80_l265
 (def bar-layer (first (:layers (first (:panels bar-pl))))))


(def v82_l269 bar-layer)


(deftest
 t83_l271
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v82_l269)))


(def
 v85_l277
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t86_l282 (is ((fn [gs] (= 3 (count gs))) v85_l277)))


(def
 v88_l291
 (def
  stacked-pl
  (->
   data/penguins
   (sk/xkcd7-lay-stacked-bar :island {:color :species})
   sk/xkcd7-plan)))


(def
 v89_l295
 (def stacked-layer (first (:layers (first (:panels stacked-pl))))))


(def v90_l297 (:position stacked-layer))


(deftest t91_l299 (is ((fn [p] (= :stack p)) v90_l297)))


(def
 v93_l308
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width)
  sk/xkcd7-lay-lm))


(deftest
 t94_l312
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v93_l308)))


(def
 v95_l316
 (def
  lm-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width)
   sk/xkcd7-lay-lm
   sk/xkcd7-plan)))


(def v97_l323 (mapv :mark (:layers (first (:panels lm-pl)))))


(deftest t98_l324 (is ((fn [marks] (= [:point :line] marks)) v97_l323)))


(def v99_l325 (def lm-layer (second (:layers (first (:panels lm-pl))))))


(def v101_l329 (first (:groups lm-layer)))


(deftest
 t102_l331
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v101_l329)))


(def
 v104_l343
 (->
  data/iris
  (sk/xkcd7-view :petal_length :petal_width {:color :species})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm))


(deftest
 t105_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v104_l343)))


(def
 v106_l351
 (def
  grp-pl
  (->
   data/iris
   (sk/xkcd7-view :petal_length :petal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm
   sk/xkcd7-plan)))


(def
 v107_l357
 (let
  [line-layer (second (:layers (first (:panels grp-pl))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t108_l364 (is ((fn [gs] (= 3 (count gs))) v107_l357)))


(def
 v110_l372
 (def
  wave
  {:x (range 30),
   :y
   (map
    (fn* [p1__335065#] (Math/sin (* p1__335065# 0.3)))
    (range 30))}))


(def v111_l375 (-> wave (sk/xkcd7-lay-line :x :y)))


(deftest
 t112_l378
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v111_l375)))


(def
 v113_l382
 (def wave-pl (-> wave (sk/xkcd7-lay-line :x :y) sk/xkcd7-plan)))


(def
 v114_l386
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-pl))))))))


(def
 v115_l388
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t116_l392 (is ((fn [m] (= 30 (:n-points m))) v115_l388)))


(def
 v118_l401
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def v119_l404 (-> sales (sk/xkcd7-lay-value-bar :product :revenue)))


(deftest
 t120_l407
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v119_l404)))


(def
 v121_l411
 (def
  sales-pl
  (-> sales (sk/xkcd7-lay-value-bar :product :revenue) sk/xkcd7-plan)))


(def
 v122_l415
 (let
  [g (first (:groups (first (:layers (first (:panels sales-pl))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t123_l419 (is ((fn [m] (= 4 (count (:xs m)))) v122_l415)))


(def
 v125_l425
 (def
  flip-pl
  (->
   data/iris
   (sk/xkcd7-lay-bar :species)
   (sk/xkcd7-coord :flip)
   sk/xkcd7-plan)))


(def v126_l430 (:coord (first (:panels flip-pl))))


(deftest t127_l432 (is ((fn [c] (= :flip c)) v126_l430)))


(def
 v129_l436
 (let
  [p (first (:panels flip-pl))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t130_l440
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v129_l436)))


(def
 v132_l450
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


(def v133_l458 opts-pl)


(deftest
 t134_l460
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v133_l458)))


(def v136_l466 (:layout opts-pl))


(deftest
 t137_l468
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v136_l466)))


(def
 v139_l479
 (def
  final-bp
  (->
   data/iris
   (sk/xkcd7-view :petal_length :petal_width {:color :species})
   sk/xkcd7-lay-point
   sk/xkcd7-lay-lm)))


(def
 v140_l485
 (def final-pl (sk/xkcd7-plan final-bp {:title "Iris Petals"})))


(def v141_l487 final-pl)


(deftest
 t142_l489
 (is ((fn [m] (= "Iris Petals" (:title m))) v141_l487)))


(def
 v144_l493
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-pl)))))


(deftest t145_l498 (is ((fn [ls] (= 2 (count ls))) v144_l493)))


(def v147_l502 (-> final-bp (sk/xkcd7-options {:title "Iris Petals"})))


(deftest
 t148_l504
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v147_l502)))


(def
 v150_l513
 (def
  faceted-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-facet :species)
   sk/xkcd7-plan)))


(def v152_l521 (:grid faceted-pl))


(deftest
 t153_l523
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v152_l521)))


(def v155_l527 (count (:panels faceted-pl)))


(deftest t156_l529 (is ((fn [n] (= 3 n)) v155_l527)))


(def v158_l533 (:panels faceted-pl))


(deftest
 t159_l535
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v158_l533)))


(def v161_l540 (:panels faceted-pl))


(deftest t162_l542 (is ((fn [ps] (every? :x-domain ps)) v161_l540)))


(def
 v164_l549
 (select-keys
  faceted-pl
  [:layout-type :grid :total-width :total-height]))


(deftest
 t165_l551
 (is ((fn [m] (= :facet-grid (:layout-type m))) v164_l549)))


(def v167_l555 (sk/valid-plan? faceted-pl))


(deftest t168_l557 (is (true? v167_l555)))


(def v170_l567 (sk/valid-plan? tiny-pl))


(deftest t171_l569 (is (true? v170_l567)))


(def v172_l571 (sk/valid-plan? iris-pl))


(deftest t173_l573 (is (true? v172_l571)))


(def v174_l575 (sk/valid-plan? hist-pl))


(deftest t175_l577 (is (true? v174_l575)))


(def v176_l579 (sk/valid-plan? bar-pl))


(deftest t177_l581 (is (true? v176_l579)))


(def v178_l583 (sk/valid-plan? lm-pl))


(deftest t179_l585 (is (true? v178_l583)))


(def v180_l587 (sk/valid-plan? final-pl))


(deftest t181_l589 (is (true? v180_l587)))


(def v183_l593 (sk/explain-plan (assoc tiny-pl :width "not-a-number")))


(deftest t184_l595 (is (some? v183_l593)))


(def
 v186_l604
 (type
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t187_l606
 (is ((fn [t] (not= clojure.lang.PersistentVector t)) v186_l604)))


(def
 v189_l610
 (vec
  (:xs (first (:groups (first (:layers (first (:panels tiny-pl)))))))))


(deftest
 t190_l612
 (is ((fn [v] (and (vector? v) (number? (first v)))) v189_l610)))
