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
     (every? (fn* [p1__78677#] (= 50 (:n-points p1__78677#))) gs)))
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
      (fn* [p1__78678#] (< (:lo p1__78678#) (:hi p1__78678#)))
      bars)
     (every? (fn* [p1__78679#] (pos? (:count p1__78679#))) bars)))
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


(deftest t90_l273 (is ((fn [marks] (= [:point :line] marks)) v89_l271)))


(def v92_l277 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v93_l279 (:stat-origin lm-layer))


(deftest t94_l281 (is ((fn [s] (= :lm s)) v93_l279)))


(def
 v96_l285
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest
 t97_l288
 (is
  ((fn [m] (and (< (:x1 m) (:x2 m)) (every? number? (vals m))))
   v96_l285)))


(def
 v99_l299
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t100_l303
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v99_l299)))


(def
 v101_l306
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
 v102_l309
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t103_l316 (is ((fn [gs] (= 3 (count gs))) v102_l309)))


(def
 v105_l324
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__78680#] (Math/sin (* p1__78680# 0.3)))
     (range 30))})))


(def v106_l327 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t107_l329
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v106_l327)))


(def
 v108_l333
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v109_l335
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v110_l337
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t111_l341 (is ((fn [m] (= 30 (:n-points m))) v110_l337)))


(def
 v113_l350
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v114_l353
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t115_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v114_l353)))


(def
 v116_l359
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v117_l361
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t118_l365 (is ((fn [m] (= 4 (count (:xs m)))) v117_l361)))


(def
 v120_l371
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v121_l374 (:coord (first (:panels flip-sk))))


(deftest t122_l376 (is ((fn [c] (= :flip c)) v121_l374)))


(def
 v124_l380
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t125_l384
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v124_l380)))


(def
 v127_l394
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
 v128_l401
 (select-keys opts-sk [:title :x-label :y-label :width :height]))


(deftest
 t129_l403
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v128_l401)))


(def v131_l409 (:layout opts-sk))


(deftest
 t132_l411
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v131_l409)))


(def
 v134_l422
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v135_l426
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def
 v136_l428
 (select-keys final-sk [:title :x-label :y-label :width :height]))


(deftest
 t137_l430
 (is ((fn [m] (= "Iris Petals" (:title m))) v136_l428)))


(def
 v139_l434
 (mapv
  (fn
   [l]
   {:mark (:mark l),
    :n-groups (count (:groups l)),
    :stat-origin (:stat-origin l)})
  (:layers (first (:panels final-sk)))))


(deftest t140_l440 (is ((fn [ls] (= 2 (count ls))) v139_l434)))


(def v142_l444 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t143_l446
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v142_l444)))


(def
 v145_l455
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v147_l464 (:grid faceted-sk))


(deftest
 t148_l466
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v147_l464)))


(def v150_l470 (count (:panels faceted-sk)))


(deftest t151_l472 (is ((fn [n] (= 3 n)) v150_l470)))


(def
 v153_l476
 (mapv
  (fn* [p1__78681#] (select-keys p1__78681# [:row :col :col-label]))
  (:panels faceted-sk)))


(deftest
 t154_l479
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v153_l476)))


(def
 v156_l484
 (mapv
  (fn*
   [p1__78682#]
   (select-keys p1__78682# [:col-label :x-domain :y-domain]))
  (:panels faceted-sk)))


(deftest t157_l487 (is ((fn [ps] (every? :x-domain ps)) v156_l484)))


(def
 v159_l494
 (select-keys faceted-sk [:panel-width :panel-height :layout-type]))


(deftest
 t160_l496
 (is ((fn [m] (= :facet-grid (:layout-type m))) v159_l494)))


(def v162_l500 (sk/valid-sketch? faceted-sk))


(deftest t163_l502 (is (true? v162_l500)))


(def v165_l509 (sk/valid-sketch? tiny-sk))


(deftest t166_l511 (is (true? v165_l509)))


(def v167_l513 (sk/valid-sketch? iris-sk))


(deftest t168_l515 (is (true? v167_l513)))


(def v169_l517 (sk/valid-sketch? hist-sk))


(deftest t170_l519 (is (true? v169_l517)))


(def v171_l521 (sk/valid-sketch? bar-sk))


(deftest t172_l523 (is (true? v171_l521)))


(def v173_l525 (sk/valid-sketch? lm-sk))


(deftest t174_l527 (is (true? v173_l525)))


(def v175_l529 (sk/valid-sketch? final-sk))


(deftest t176_l531 (is (true? v175_l529)))


(def
 v178_l535
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t179_l537 (is (some? v178_l535)))


(def
 v181_l545
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t182_l549 (is (true? v181_l545)))
