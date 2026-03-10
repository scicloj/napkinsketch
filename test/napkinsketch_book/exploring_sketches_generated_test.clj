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
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v5_l25)))


(def
 v8_l32
 (def tiny-sk (sk/sketch [(sk/point {:data tiny, :x :x, :y :y})])))


(def
 v10_l38
 (select-keys
  tiny-sk
  [:width :height :margin :total-width :total-height]))


(deftest
 t11_l40
 (is ((fn [m] (and (= 600 (:width m)) (= 400 (:height m)))) v10_l38)))


(def v13_l47 (select-keys tiny-sk [:title :x-label :y-label]))


(deftest
 t14_l49
 (is
  ((fn
    [m]
    (and (nil? (:title m)) (= "x" (:x-label m)) (= "y" (:y-label m))))
   v13_l47)))


(def v16_l55 (:legend tiny-sk))


(deftest t17_l57 (is (nil? v16_l55)))


(def v19_l64 (def tiny-panel (first (:panels tiny-sk))))


(def v20_l66 (keys tiny-panel))


(deftest
 t21_l68
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v20_l66)))


(def v23_l72 (:x-domain tiny-panel))


(deftest
 t24_l74
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v23_l72)))


(def v25_l76 (:y-domain tiny-panel))


(deftest
 t26_l78
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v25_l76)))


(def v28_l82 (:x-scale tiny-panel))


(deftest t29_l84 (is ((fn [s] (= :linear (:type s))) v28_l82)))


(def v31_l88 (:x-ticks tiny-panel))


(deftest
 t32_l90
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v31_l88)))


(def v34_l102 (def tiny-layer (first (:layers tiny-panel))))


(def v35_l104 (select-keys tiny-layer [:mark :style]))


(deftest t36_l106 (is ((fn [m] (= :point (:mark m))) v35_l104)))


(def v38_l111 (count (:groups tiny-layer)))


(deftest t39_l113 (is ((fn [n] (= 1 n)) v38_l111)))


(def v41_l118 (first (:groups tiny-layer)))


(deftest
 t42_l120
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v41_l118)))


(def
 v44_l134
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v45_l137
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t46_l139
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v45_l137)))


(def
 v47_l141
 (def
  iris-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v49_l145
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v50_l147 (count (:groups iris-layer)))


(deftest t51_l149 (is ((fn [n] (= 3 n)) v50_l147)))


(def
 v53_l153
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t54_l158
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__79304#] (= 50 (:n-points p1__79304#))) gs)))
   v53_l153)))


(def v56_l163 (:legend iris-sk))


(deftest
 t57_l165
 (is ((fn [leg] (= 3 (count (:entries leg)))) v56_l163)))


(def v59_l175 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(deftest
 t60_l177
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v59_l175)))


(def
 v61_l179
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def
 v62_l181
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v63_l183 (:mark hist-layer))


(deftest t64_l185 (is ((fn [m] (= :bar m)) v63_l183)))


(def v66_l189 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t67_l192
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__79305#] (< (:lo p1__79305#) (:hi p1__79305#)))
      bars)
     (every? (fn* [p1__79306#] (pos? (:count p1__79306#))) bars)))
   v66_l189)))


(def
 v69_l204
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(deftest
 t70_l206
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v69_l204)))


(def
 v71_l208
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v72_l210
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v74_l214 (select-keys bar-layer [:mark :position :categories]))


(deftest
 t75_l216
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v74_l214)))


(def
 v77_l222
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t78_l227 (is ((fn [gs] (= 3 (count gs))) v77_l222)))


(def
 v80_l236
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v81_l238
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v82_l240 (:position stacked-layer))


(deftest t83_l242 (is ((fn [p] (= :stack p)) v82_l240)))


(def
 v85_l251
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(deftest
 t86_l254
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v85_l251)))


(def
 v87_l256
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v89_l261 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t90_l263 (is ((fn [marks] (= [:point :line] marks)) v89_l261)))


(def v92_l267 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v93_l269 (:stat-origin lm-layer))


(deftest t94_l271 (is ((fn [s] (= :lm s)) v93_l269)))


(def
 v96_l275
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest
 t97_l278
 (is
  ((fn [m] (and (< (:x1 m) (:x2 m)) (every? number? (vals m))))
   v96_l275)))


(def
 v99_l289
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t100_l293
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v99_l289)))


(def
 v101_l294
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
 v102_l297
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t103_l304 (is ((fn [gs] (= 3 (count gs))) v102_l297)))


(def
 v105_l312
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__79307#] (Math/sin (* p1__79307# 0.3)))
     (range 30))})))


(def v106_l315 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t107_l317
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v106_l315)))


(def
 v108_l319
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v109_l321
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v110_l323
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t111_l327 (is ((fn [m] (= 30 (:n-points m))) v110_l323)))


(def
 v113_l336
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v114_l339
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t115_l341
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v114_l339)))


(def
 v116_l343
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v117_l345
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t118_l349 (is ((fn [m] (= 4 (count (:xs m)))) v117_l345)))


(def
 v120_l355
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v121_l358 (:coord (first (:panels flip-sk))))


(deftest t122_l360 (is ((fn [c] (= :flip c)) v121_l358)))


(def
 v124_l364
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t125_l368
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v124_l364)))


(def
 v127_l378
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
 v128_l385
 (select-keys opts-sk [:title :x-label :y-label :width :height]))


(deftest
 t129_l387
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v128_l385)))


(def v131_l393 (:layout opts-sk))


(deftest
 t132_l395
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v131_l393)))


(def
 v134_l406
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v135_l410
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def
 v136_l412
 (select-keys final-sk [:title :x-label :y-label :width :height]))


(deftest
 t137_l414
 (is ((fn [m] (= "Iris Petals" (:title m))) v136_l412)))


(def
 v139_l418
 (mapv
  (fn
   [l]
   {:mark (:mark l),
    :n-groups (count (:groups l)),
    :stat-origin (:stat-origin l)})
  (:layers (first (:panels final-sk)))))


(deftest t140_l424 (is ((fn [ls] (= 2 (count ls))) v139_l418)))


(def v142_l428 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t143_l430
 (is ((fn [v] (and (vector? v) (= :svg (first v)))) v142_l428)))


(def
 v145_l437
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v147_l446 (:grid faceted-sk))


(deftest
 t148_l448
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v147_l446)))


(def v150_l452 (count (:panels faceted-sk)))


(deftest t151_l454 (is ((fn [n] (= 3 n)) v150_l452)))


(def
 v153_l458
 (mapv
  (fn* [p1__79308#] (select-keys p1__79308# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest
 t154_l461
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v153_l458)))


(def
 v156_l466
 (mapv
  (fn*
   [p1__79309#]
   (select-keys p1__79309# [:col-label :x-domain :y-domain]))
  (:panels faceted-sk)))


(deftest t157_l469 (is ((fn [ps] (every? :x-domain ps)) v156_l466)))


(def
 v159_l476
 (select-keys faceted-sk [:panel-width :panel-height :layout-type]))


(deftest
 t160_l478
 (is ((fn [m] (= :facet-grid (:layout-type m))) v159_l476)))


(def v162_l482 (sk/valid-sketch? faceted-sk))


(deftest t163_l484 (is (true? v162_l482)))


(def v165_l491 (sk/valid-sketch? tiny-sk))


(deftest t166_l493 (is (true? v165_l491)))


(def v167_l495 (sk/valid-sketch? iris-sk))


(deftest t168_l497 (is (true? v167_l495)))


(def v169_l499 (sk/valid-sketch? hist-sk))


(deftest t170_l501 (is (true? v169_l499)))


(def v171_l503 (sk/valid-sketch? bar-sk))


(deftest t172_l505 (is (true? v171_l503)))


(def v173_l507 (sk/valid-sketch? lm-sk))


(deftest t174_l509 (is (true? v173_l507)))


(def v175_l511 (sk/valid-sketch? final-sk))


(deftest t176_l513 (is (true? v175_l511)))


(def
 v178_l517
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t179_l519 (is (some? v178_l517)))


(def
 v181_l527
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t182_l531 (is (true? v181_l527)))
