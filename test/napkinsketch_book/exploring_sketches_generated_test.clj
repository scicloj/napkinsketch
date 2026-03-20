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


(def v10_l45 (kind/pprint tiny-sk))


(deftest
 t11_l47
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
   v10_l45)))


(def v13_l66 (def tiny-panel (first (:panels tiny-sk))))


(def v14_l68 (keys tiny-panel))


(deftest
 t15_l70
 (is
  ((fn [ks] (every? (set ks) [:x-domain :y-domain :layers])) v14_l68)))


(def v17_l74 (:x-domain tiny-panel))


(deftest
 t18_l76
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v17_l74)))


(def v19_l78 (:y-domain tiny-panel))


(deftest
 t20_l80
 (is ((fn [d] (and (<= (first d) 1) (>= (second d) 5))) v19_l78)))


(def v22_l84 (:x-scale tiny-panel))


(deftest t23_l86 (is ((fn [s] (= :linear (:type s))) v22_l84)))


(def v25_l90 (:x-ticks tiny-panel))


(deftest
 t26_l92
 (is
  ((fn
    [t]
    (and
     (vector? (:values t))
     (vector? (:labels t))
     (= (count (:values t)) (count (:labels t)))))
   v25_l90)))


(def v28_l104 (def tiny-layer (first (:layers tiny-panel))))


(def v29_l106 tiny-layer)


(deftest t30_l108 (is ((fn [m] (= :point (:mark m))) v29_l106)))


(def v32_l113 (count (:groups tiny-layer)))


(deftest t33_l115 (is ((fn [n] (= 1 n)) v32_l113)))


(def v35_l120 (first (:groups tiny-layer)))


(deftest
 t36_l122
 (is
  ((fn
    [g]
    (and
     (= 4 (count (:color g)))
     (= [1 2 3 4 5] (mapv int (:xs g)))
     (= [2 4 1 5 3] (mapv int (:ys g)))))
   v35_l120)))


(def
 v38_l136
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l139
 (sk/plot
  [(sk/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]))


(deftest
 t40_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l139)))


(def
 v41_l145
 (def
  iris-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def v43_l149 (kind/pprint iris-sk))


(deftest
 t44_l151
 (is
  ((fn
    [m]
    (and
     (= 3 (count (:entries (:legend m))))
     (= 1 (count (:panels m)))))
   v43_l149)))


(def
 v46_l156
 (def iris-layer (first (:layers (first (:panels iris-sk))))))


(def v47_l158 (count (:groups iris-layer)))


(deftest t48_l160 (is ((fn [n] (= 3 n)) v47_l158)))


(def
 v50_l164
 (mapv
  (fn [g] {:color (:color g), :n-points (count (:xs g))})
  (:groups iris-layer)))


(deftest
 t51_l169
 (is
  ((fn
    [gs]
    (and
     (= 3 (count gs))
     (every? (fn* [p1__130371#] (= 50 (:n-points p1__130371#))) gs)))
   v50_l164)))


(def v53_l174 (:legend iris-sk))


(deftest
 t54_l176
 (is ((fn [leg] (= 3 (count (:entries leg)))) v53_l174)))


(def
 v56_l187
 (def
  cont-sk
  (sk/sketch
   [(sk/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :petal_length})])))


(def v57_l190 (kind/pprint (:legend cont-sk)))


(deftest t58_l192 (is ((fn [m] (= :continuous (:type m))) v57_l190)))


(def
 v60_l196
 (select-keys (:legend cont-sk) [:title :type :min :max :color-scale]))


(deftest
 t61_l198
 (is
  ((fn
    [m]
    (and (= :continuous (:type m)) (not (contains? m :gradient-fn))))
   v60_l196)))


(def v63_l203 (count (:stops (:legend cont-sk))))


(deftest t64_l205 (is ((fn [n] (= 20 n)) v63_l203)))


(def v66_l212 (sk/plot [(sk/histogram {:data iris, :x :sepal_length})]))


(deftest
 t67_l214
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v66_l212)))


(def
 v68_l218
 (def
  hist-sk
  (sk/sketch [(sk/histogram {:data iris, :x :sepal_length})])))


(def v69_l220 (kind/pprint hist-sk))


(deftest t70_l222 (is ((fn [m] (= 1 (count (:panels m)))) v69_l220)))


(def
 v71_l224
 (def hist-layer (first (:layers (first (:panels hist-sk))))))


(def v72_l226 (:mark hist-layer))


(deftest t73_l228 (is ((fn [m] (= :bar m)) v72_l226)))


(def v75_l232 (let [g (first (:groups hist-layer))] (:bars g)))


(deftest
 t76_l235
 (is
  ((fn
    [bars]
    (and
     (> (count bars) 3)
     (every?
      (fn* [p1__130372#] (< (:lo p1__130372#) (:hi p1__130372#)))
      bars)
     (every? (fn* [p1__130373#] (pos? (:count p1__130373#))) bars)))
   v75_l232)))


(def
 v78_l247
 (sk/plot [(sk/bar {:data iris, :x :species, :color :species})]))


(deftest
 t79_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v78_l247)))


(def
 v80_l253
 (def
  bar-sk
  (sk/sketch [(sk/bar {:data iris, :x :species, :color :species})])))


(def
 v81_l255
 (def bar-layer (first (:layers (first (:panels bar-sk))))))


(def v83_l259 (kind/pprint bar-layer))


(deftest
 t84_l261
 (is
  ((fn
    [m]
    (and
     (= :rect (:mark m))
     (= :dodge (:position m))
     (= 3 (count (:categories m)))))
   v83_l259)))


(def
 v86_l267
 (mapv
  (fn [g] {:label (:label g), :counts (:counts g)})
  (:groups bar-layer)))


(deftest t87_l272 (is ((fn [gs] (= 3 (count gs))) v86_l267)))


(def
 v89_l281
 (def
  stacked-sk
  (sk/sketch
   [(sk/stacked-bar {:data iris, :x :species, :color :species})])))


(def
 v90_l283
 (def stacked-layer (first (:layers (first (:panels stacked-sk))))))


(def v91_l285 (:position stacked-layer))


(deftest t92_l287 (is ((fn [p] (= :stack p)) v91_l285)))


(def
 v94_l296
 (sk/plot
  [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
   (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})]))


(deftest
 t95_l299
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v94_l296)))


(def
 v96_l303
 (def
  lm-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})
    (sk/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def v98_l308 (mapv :mark (:layers (first (:panels lm-sk)))))


(deftest t99_l309 (is ((fn [marks] (= [:point :line] marks)) v98_l308)))


(def
 v100_l310
 (def lm-layer (second (:layers (first (:panels lm-sk))))))


(def v102_l314 (first (:groups lm-layer)))


(deftest
 t103_l316
 (is
  ((fn
    [m]
    (and (< (:x1 m) (:x2 m)) (number? (:x1 m)) (number? (:y2 m))))
   v102_l314)))


(def
 v105_l328
 (sk/plot
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(deftest
 t106_l331
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v105_l328)))


(def
 v107_l334
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
 v108_l337
 (let
  [line-layer (second (:layers (first (:panels grp-sk))))]
  (mapv
   (fn
    [g]
    {:color (:color g),
     :x1 (some-> (:x1 g) (Math/round) int),
     :x2 (some-> (:x2 g) (Math/round) int)})
   (:groups line-layer))))


(deftest t109_l344 (is ((fn [gs] (= 3 (count gs))) v108_l337)))


(def
 v111_l352
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__130374#] (Math/sin (* p1__130374# 0.3)))
     (range 30))})))


(def v112_l355 (sk/plot [(sk/line {:data wave, :x :x, :y :y})]))


(deftest
 t113_l357
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v112_l355)))


(def
 v114_l361
 (def wave-sk (sk/sketch [(sk/line {:data wave, :x :x, :y :y})])))


(def
 v115_l363
 (def
  wave-group
  (first (:groups (first (:layers (first (:panels wave-sk))))))))


(def
 v116_l365
 {:n-points (count (:xs wave-group)),
  :first-x (first (:xs wave-group)),
  :last-x (last (:xs wave-group))})


(deftest t117_l369 (is ((fn [m] (= 30 (:n-points m))) v116_l365)))


(def
 v119_l378
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def
 v120_l381
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t121_l383
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v120_l381)))


(def
 v122_l387
 (def
  sales-sk
  (sk/sketch [(sk/value-bar {:data sales, :x :product, :y :revenue})])))


(def
 v123_l389
 (let
  [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g), :ys (:ys g)}))


(deftest t124_l393 (is ((fn [m] (= 4 (count (:xs m)))) v123_l389)))


(def
 v126_l399
 (def
  flip-sk
  (sk/sketch
   [(-> (sk/bar {:data iris, :x :species}) (assoc :coord :flip))])))


(def v127_l402 (:coord (first (:panels flip-sk))))


(deftest t128_l404 (is ((fn [c] (= :flip c)) v127_l402)))


(def
 v130_l408
 (let
  [p (first (:panels flip-sk))]
  {:x-domain-type
   (if (number? (first (:x-domain p))) :numeric :categorical),
   :y-domain-type
   (if (number? (first (:y-domain p))) :numeric :categorical)}))


(deftest
 t131_l412
 (is
  ((fn
    [m]
    (and
     (= :numeric (:x-domain-type m))
     (= :categorical (:y-domain-type m))))
   v130_l408)))


(def
 v133_l422
 (def
  opts-sk
  (sk/sketch
   [(sk/point {:data iris, :x :sepal_length, :y :sepal_width})]
   {:title "My Custom Title",
    :x-label "Length (cm)",
    :y-label "Width (cm)",
    :width 800,
    :height 300})))


(def v134_l429 (kind/pprint opts-sk))


(deftest
 t135_l431
 (is
  ((fn
    [m]
    (and
     (= "My Custom Title" (:title m))
     (= 800 (:width m))
     (= 300 (:height m))))
   v134_l429)))


(def v137_l437 (:layout opts-sk))


(deftest
 t138_l439
 (is
  ((fn
    [lay]
    (and
     (pos? (:title-pad lay))
     (pos? (:x-label-pad lay))
     (pos? (:y-label-pad lay))))
   v137_l437)))


(def
 v140_l450
 (def
  final-views
  [(sk/point
    {:data iris, :x :petal_length, :y :petal_width, :color :species})
   (sk/lm
    {:data iris, :x :petal_length, :y :petal_width, :color :species})]))


(def
 v141_l454
 (def final-sk (sk/sketch final-views {:title "Iris Petals"})))


(def v142_l456 (kind/pprint final-sk))


(deftest
 t143_l458
 (is ((fn [m] (= "Iris Petals" (:title m))) v142_l456)))


(def
 v145_l462
 (mapv
  (fn [l] {:mark (:mark l), :n-groups (count (:groups l))})
  (:layers (first (:panels final-sk)))))


(deftest t146_l467 (is ((fn [ls] (= 2 (count ls))) v145_l462)))


(def v148_l471 (sk/plot final-views {:title "Iris Petals"}))


(deftest
 t149_l473
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v148_l471)))


(def
 v151_l482
 (def
  faceted-sk
  (->
   iris
   (sk/view [[:sepal_length :sepal_width]])
   (sk/facet :species)
   (sk/lay (sk/point {:color :species}))
   sk/sketch)))


(def v153_l491 (:grid faceted-sk))


(deftest
 t154_l493
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v153_l491)))


(def v156_l497 (count (:panels faceted-sk)))


(deftest t157_l499 (is ((fn [n] (= 3 n)) v156_l497)))


(def v159_l503 (:panels faceted-sk))


(deftest
 t160_l505
 (is
  ((fn [ps] (and (= 3 (count ps)) (every? :col-label ps))) v159_l503)))


(def v162_l510 (:panels faceted-sk))


(deftest t163_l512 (is ((fn [ps] (every? :x-domain ps)) v162_l510)))


(def
 v165_l519
 (kind/pprint
  (select-keys
   faceted-sk
   [:layout-type :grid :total-width :total-height])))


(deftest
 t166_l521
 (is ((fn [m] (= :facet-grid (:layout-type m))) v165_l519)))


(def v168_l525 (sk/valid-sketch? faceted-sk))


(deftest t169_l527 (is (true? v168_l525)))


(def v171_l537 (sk/valid-sketch? tiny-sk))


(deftest t172_l539 (is (true? v171_l537)))


(def v173_l541 (sk/valid-sketch? iris-sk))


(deftest t174_l543 (is (true? v173_l541)))


(def v175_l545 (sk/valid-sketch? hist-sk))


(deftest t176_l547 (is (true? v175_l545)))


(def v177_l549 (sk/valid-sketch? bar-sk))


(deftest t178_l551 (is (true? v177_l549)))


(def v179_l553 (sk/valid-sketch? lm-sk))


(deftest t180_l555 (is (true? v179_l553)))


(def v181_l557 (sk/valid-sketch? final-sk))


(deftest t182_l559 (is (true? v181_l557)))


(def
 v184_l563
 (sk/explain-sketch (assoc tiny-sk :width "not-a-number")))


(deftest t185_l565 (is (some? v184_l563)))


(def
 v187_l573
 (let [s (pr-str tiny-sk) back (read-string s)] (= tiny-sk back)))


(deftest t188_l577 (is (true? v187_l573)))
