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


(deftest
 t6_l27
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v5_l25)))


(def
 v8_l34
 (def tiny-sk (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v10_l40
 (select-keys
  tiny-sk
  [:width :height :margin :total-width :total-height]))


(deftest
 t11_l42
 (is ((fn [m] (and (= 600 (:width m)) (= 400 (:height m)))) v10_l40)))


(def v13_l49 (select-keys tiny-sk [:title :x-label :y-label]))


(deftest
 t14_l51
 (is
  ((fn
    [m]
    (and (nil? (:title m)) (= "x" (:x-label m)) (= "y" (:y-label m))))
   v13_l49)))


(def v16_l57 (:legend tiny-sk))


(deftest t17_l59 (is (nil? v16_l57)))


(def v19_l66 (def tiny-panel (first (:panels tiny-sk))))


(def v20_l68 (keys tiny-panel))


(deftest
 t21_l70
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v20_l68)))


(def v23_l74 (:x-domain tiny-panel))


(deftest
 t24_l76
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v23_l74)))


(def v25_l78 (:y-domain tiny-panel))


(deftest
 t26_l80
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v25_l78)))


(def v28_l84 (:x-scale tiny-panel))


(deftest t29_l86 (is ((fn [s] (= :linear (:type s))) v28_l84)))


(def v31_l90 (:x-ticks tiny-panel))


(deftest
 t32_l92
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v31_l90)))


(def v34_l104 (def tiny-layer (first (:layers tiny-panel))))


(def v35_l106 (select-keys tiny-layer [:mark :style]))


(deftest t36_l108 (is ((fn [m] (= :point (:mark m))) v35_l106)))


(def v38_l113 (count (:groups tiny-layer)))


(deftest t39_l115 (is ((fn [n] (= 1 n)) v38_l113)))


(def v41_l120 (first (:groups tiny-layer)))


(deftest
 t42_l122
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v41_l120)))


(def
 v44_l136
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v45_l139
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t46_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v45_l139)))


(def
 v47_l145
 (def
  iris-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v49_l149
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v50_l151 (count (:groups iris-layer)))


(deftest t51_l153 (is ((fn [n] (= 3 n)) v50_l151)))


(def
 v53_l157
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t54_l162
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__304528#] (= 50 (:n-points p1__304528#))) gs)))
   v53_l157)))


(def v56_l167 (:legend iris-sk))


(deftest
 t57_l169
 (is ((fn [leg] (= 3 (count (:entries leg)))) v56_l167)))


(def v59_l179 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(deftest
 t60_l181
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v59_l179)))


(def
 v61_l185
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def
 v62_l187
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v63_l189 (:mark hist-layer))


(deftest t64_l191 (is ((fn [m] (= :bar m)) v63_l189)))


(def v66_l195 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t67_l198
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__304529#] (< (:lo p1__304529#) (:hi p1__304529#)))
      bars)
     (every? (fn* [p1__304530#] (pos? (:count p1__304530#))) bars)))
   v66_l195)))


(def
 v69_l210
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(deftest
 t70_l212
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v69_l210)))


(def
 v71_l216
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v72_l218
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v74_l222 (select-keys bar-layer [:mark :position :categories]))


(deftest
 t75_l224
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v74_l222)))


(def
 v77_l230
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t78_l235 (is ((fn [gs] (= 3 (count gs))) v77_l230)))


(def
 v80_l244
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v81_l246
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v82_l248 (:position stacked-layer))


(deftest t83_l250 (is ((fn [p] (= :stack p)) v82_l248)))


(def
 v85_l259
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(deftest
 t86_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v85_l259)))


(def
 v87_l266
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v89_l271 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t90_l272 (is ((fn [marks] (= [:point :line] marks)) v89_l271)))


(def v91_l273 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def
 v93_l278
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest
 t94_l281
 (is
  ((fn [m] (and (< (:x1 m) (:x2 m)) (every? number? (vals m))))
   v93_l278)))


(def
 v96_l292
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t97_l296
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v96_l292)))


(def
 v98_l299
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
 v99_l302
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t100_l309 (is ((fn [gs] (= 3 (count gs))) v99_l302)))


(def
 v102_l317
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__304531#] (Math/sin (* p1__304531# 0.3)))
     (range 30))})))


(def v103_l320 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t104_l322
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v103_l320)))


(def
 v105_l326
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v106_l328
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v107_l330
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t108_l334 (is ((fn [m] (= 30 (:n-points m))) v107_l330)))


(def
 v110_l343
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v111_l346
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t112_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v111_l346)))


(def
 v113_l352
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v114_l354
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t115_l358 (is ((fn [m] (= 4 (count (:xs m)))) v114_l354)))


(def
 v117_l364
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v118_l367 (:coord (first (:panels flip-sk))))


(deftest t119_l369 (is ((fn [c] (= :flip c)) v118_l367)))


(def
 v121_l373
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t122_l377
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v121_l373)))


(def
 v124_l387
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
 v125_l394
 (select-keys opts-sk [:title :x-label :y-label :width :height]))


(deftest
 t126_l396
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v125_l394)))


(def v128_l402 (:layout opts-sk))


(deftest
 t129_l404
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v128_l402)))


(def
 v131_l415
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v132_l419
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def
 v133_l421
 (select-keys final-sk [:title :x-label :y-label :width :height]))


(deftest
 t134_l423
 (is ((fn [m] (= "Iris Petals" (:title m))) v133_l421)))


(def
 v136_l427
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t137_l432 (is ((fn [ls] (= 2 (count ls))) v136_l427)))


(def v139_l436 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t140_l438
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v139_l436)))


(def
 v142_l447
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v144_l456 (:grid faceted-sk))


(deftest
 t145_l458
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v144_l456)))


(def v147_l462 (count (:panels faceted-sk)))


(deftest t148_l464 (is ((fn [n] (= 3 n)) v147_l462)))


(def
 v150_l468
 (mapv
  (fn* [p1__304532#] (select-keys p1__304532# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest
 t151_l471
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v150_l468)))


(def
 v153_l476
 (mapv
  (fn*
   [p1__304533#]
   (select-keys p1__304533# [:col-label :x-domain :y-domain]))
  (:panels faceted-sk)))


(deftest t154_l479 (is ((fn [ps] (every? :x-domain ps)) v153_l476)))


(def
 v156_l486
 (select-keys faceted-sk [:panel-width :panel-height :layout-type]))


(deftest
 t157_l488
 (is ((fn [m] (= :facet-grid (:layout-type m))) v156_l486)))


(def v159_l492 (sk/valid-sketch? faceted-sk))


(deftest t160_l494 (is (true? v159_l492)))


(def v162_l501 (sk/valid-sketch? tiny-sk))


(deftest t163_l503 (is (true? v162_l501)))


(def v164_l505 (sk/valid-sketch? iris-sk))


(deftest t165_l507 (is (true? v164_l505)))


(def v166_l509 (sk/valid-sketch? hist-sk))


(deftest t167_l511 (is (true? v166_l509)))


(def v168_l513 (sk/valid-sketch? bar-sk))


(deftest t169_l515 (is (true? v168_l513)))


(def v170_l517 (sk/valid-sketch? lm-sk))


(deftest t171_l519 (is (true? v170_l517)))


(def v172_l521 (sk/valid-sketch? final-sk))


(deftest t173_l523 (is (true? v172_l521)))


(def
 v175_l527
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t176_l529 (is (some? v175_l527)))


(def
 v178_l537
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t179_l541 (is (true? v178_l537)))
