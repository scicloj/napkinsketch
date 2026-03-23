(ns
 napkinsketch-book.api-reference-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l28
 (def tiny {:x [1 2 3 4 5], :y [2 4 1 5 3], :group [:a :a :b :b :b]}))


(def
 v5_l32
 (def
  sales
  {:product [:widget :gadget :gizmo :doohickey],
   :revenue [120 340 210 95]}))


(def
 v6_l35
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v7_l38
 (def
  measurements
  {:treatment ["A" "B" "C" "D"],
   :mean [10.0 15.0 12.0 18.0],
   :ci_lo [8.0 12.0 9.5 15.5],
   :ci_hi [12.0 18.0 14.5 20.5]}))


(def v9_l45 (kind/doc #'sk/view))


(def v11_l49 (-> iris (sk/lay-point :sepal_length :sepal_width)))


(deftest
 t12_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v11_l49)))


(def v14_l58 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t15_l61
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v14_l58)))


(def
 v17_l67
 (->
  iris
  (sk/view [[:sepal_length :sepal_width] [:petal_length :petal_width]])
  (sk/lay-point {:color :species})))


(deftest
 t18_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (= 300 (:points s)))))
   v17_l67)))


(def
 v20_l78
 (-> (sk/view iris {:x :sepal_length, :y :sepal_width}) sk/lay-point))


(deftest
 t21_l81
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v20_l78)))


(def v22_l85 (kind/doc #'sk/lay))


(def
 v24_l89
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/lay-lm {:color :species})))


(deftest
 t25_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v24_l89)))


(def v27_l99 (kind/doc #'sk/lay-point))


(def
 v28_l101
 (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species})))


(deftest
 t29_l104
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v28_l101)))


(def v30_l107 (kind/doc #'sk/lay-line))


(def
 v31_l109
 (def
  wave
  {:x (range 30),
   :y
   (mapv
    (fn* [p1__115107#] (Math/sin (* p1__115107# 0.3)))
    (range 30))}))


(def v32_l112 (-> wave (sk/lay-line :x :y)))


(deftest
 t33_l115
 (is ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:lines s)))) v32_l112)))


(def v34_l118 (kind/doc #'sk/lay-histogram))


(def v35_l120 (-> iris (sk/lay-histogram :sepal_length)))


(deftest
 t36_l123
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v35_l120)))


(def v37_l126 (kind/doc #'sk/lay-bar))


(def v38_l128 (-> iris (sk/lay-bar :species)))


(deftest
 t39_l131
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v38_l128)))


(def v40_l134 (kind/doc #'sk/lay-stacked-bar))


(def
 v41_l136
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v42_l139
 (-> penguins (sk/lay-stacked-bar :island {:color :species})))


(deftest
 t43_l142
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l139)))


(def v44_l145 (kind/doc #'sk/lay-stacked-bar-fill))


(def
 v45_l147
 (-> penguins (sk/lay-stacked-bar-fill :island {:color :species})))


(deftest
 t46_l150
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l147)))


(def v47_l153 (kind/doc #'sk/lay-value-bar))


(def v48_l155 (-> sales (sk/lay-value-bar :product :revenue)))


(deftest
 t49_l158
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v48_l155)))


(def v50_l161 (kind/doc #'sk/lay-lm))


(def
 v51_l163
 (-> iris (sk/lay-point :sepal_length :sepal_width) sk/lay-lm))


(deftest
 t52_l167
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v51_l163)))


(def v53_l171 (kind/doc #'sk/lay-loess))


(def
 v54_l173
 (def
  noisy-wave
  (let
   [r (rng/rng :jdk 42)]
   {:x (range 50),
    :y
    (mapv
     (fn*
      [p1__115108#]
      (+
       (Math/sin (* p1__115108# 0.2))
       (* 0.3 (- (rng/drandom r) 0.5))))
     (range 50))})))


(def v55_l178 (-> noisy-wave (sk/lay-point :x :y) sk/lay-loess))


(deftest
 t56_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v55_l178)))


(def v57_l186 (kind/doc #'sk/lay-density))


(def v58_l188 (-> iris (sk/lay-density :sepal_length)))


(deftest
 t59_l191
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v58_l188)))


(def v60_l194 (kind/doc #'sk/lay-area))


(def v61_l196 (-> wave (sk/lay-area :x :y)))


(deftest
 t62_l199
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v61_l196)))


(def v63_l202 (kind/doc #'sk/lay-stacked-area))


(def
 v64_l204
 (->
  {:x (vec (concat (range 10) (range 10) (range 10))),
   :y
   (vec
    (concat
     [1 2 3 4 5 4 3 2 1 0]
     [2 2 2 3 3 3 2 2 2 2]
     [1 1 1 1 2 2 2 1 1 1])),
   :group
   (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))}
  (sk/lay-stacked-area :x :y {:color :group})))


(deftest
 t65_l211
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s)))) v64_l204)))


(def v66_l214 (kind/doc #'sk/lay-text))


(def
 v67_l216
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-text :x :y {:text :name})))


(deftest
 t68_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (every? (set (:texts s)) ["A" "B" "C" "D"])))
   v67_l216)))


(def v69_l222 (kind/doc #'sk/lay-label))


(def
 v70_l224
 (->
  {:x [1 2 3 4], :y [4 7 5 8], :name ["A" "B" "C" "D"]}
  (sk/lay-point :x :y {:size 5})
  (sk/lay-label {:text :name})))


(deftest
 t71_l228
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 4 (:points s))
      (every? (set (:texts s)) ["A" "B" "C" "D"]))))
   v70_l224)))


(def v72_l231 (kind/doc #'sk/lay-boxplot))


(def v73_l233 (-> iris (sk/lay-boxplot :species :sepal_width)))


(deftest
 t74_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (pos? (:lines s)))))
   v73_l233)))


(def v75_l240 (kind/doc #'sk/lay-violin))


(def v76_l242 (-> tips (sk/lay-violin :day :total_bill)))


(deftest
 t77_l245
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 4 (:polygons s)))) v76_l242)))


(def v78_l248 (kind/doc #'sk/lay-errorbar))


(def
 v79_l250
 (->
  measurements
  (sk/lay-point :treatment :mean)
  (sk/lay-errorbar {:ymin :ci_lo, :ymax :ci_hi})))


(deftest
 t80_l254
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v79_l250)))


(def v81_l258 (kind/doc #'sk/lay-lollipop))


(def v82_l260 (-> sales (sk/lay-lollipop :product :revenue)))


(deftest
 t83_l263
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v82_l260)))


(def v84_l267 (kind/doc #'sk/lay-tile))


(def v85_l269 (-> iris (sk/lay-tile :sepal_length :sepal_width)))


(deftest
 t86_l272
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v85_l269)))


(def v87_l275 (kind/doc #'sk/lay-density2d))


(def v88_l277 (-> iris (sk/lay-density2d :sepal_length :sepal_width)))


(deftest
 t89_l280
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:tiles s)))) v88_l277)))


(def v90_l283 (kind/doc #'sk/lay-contour))


(def v91_l285 (-> iris (sk/lay-contour :sepal_length :sepal_width)))


(deftest
 t92_l288
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v91_l285)))


(def v93_l291 (kind/doc #'sk/lay-ridgeline))


(def v94_l293 (-> iris (sk/lay-ridgeline :species :sepal_length)))


(deftest
 t95_l296
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v94_l293)))


(def v96_l299 (kind/doc #'sk/lay-rug))


(def
 v97_l301
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay-rug {:side :both})))


(deftest
 t98_l305
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 300 (:lines s)))) v97_l301)))


(def v99_l308 (kind/doc #'sk/lay-step))


(def v100_l310 (-> tiny (sk/lay-step :x :y) sk/lay-point))


(deftest
 t101_l314
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v100_l310)))


(def v102_l318 (kind/doc #'sk/lay-summary))


(def v103_l320 (-> iris (sk/lay-summary :species :sepal_length)))


(deftest
 t104_l323
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 3 (:lines s)))))
   v103_l320)))


(def v106_l329 (kind/doc #'sk/plot))


(def v108_l334 (-> tiny (sk/lay-point :x :y)))


(deftest
 t109_l337
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:points s)))) v108_l334)))


(def v110_l340 (kind/doc #'sk/options))


(def
 v112_l344
 (->
  tiny
  (sk/lay-point :x :y)
  (sk/options {:width 400, :height 200, :title "Small Plot"})))


(deftest
 t113_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (< (:width s) 500) (some #{"Small Plot"} (:texts s)))))
   v112_l344)))


(def v114_l352 (kind/doc #'sk/plot-spec?))


(def v116_l356 (sk/plot-spec? (sk/lay-point tiny :x :y)))


(deftest t117_l358 (is (true? v116_l356)))


(def v118_l360 (kind/doc #'sk/views-of))


(def
 v120_l364
 (count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm)))))


(deftest t121_l366 (is ((fn [v] (= 2 v)) v120_l364)))


(def v122_l368 (kind/doc #'sk/sketch))


(def v124_l372 (def sk1 (-> tiny (sk/lay-point :x :y) sk/sketch)))


(def v125_l376 sk1)


(deftest
 t126_l378
 (is
  ((fn [m] (and (= 600 (:width m)) (= "x" (:x-label m)))) v125_l376)))


(def v128_l383 (kind/doc #'sk/views->sketch))


(def
 v129_l385
 (def sk2 (-> tiny (sk/lay-point :x :y) sk/views->sketch)))


(def v130_l389 (= (keys sk1) (keys sk2)))


(deftest t131_l391 (is (true? v130_l389)))


(def v132_l393 (kind/doc #'sk/sketch->membrane))


(def v133_l395 (def m1 (sk/sketch->membrane sk1)))


(def v134_l397 (vector? m1))


(deftest t135_l399 (is (true? v134_l397)))


(def v136_l401 (kind/doc #'sk/membrane->figure))


(def
 v137_l403
 (first
  (sk/membrane->figure
   m1
   :svg
   {:total-width (:total-width sk1),
    :total-height (:total-height sk1)})))


(deftest t138_l407 (is ((fn [v] (= :svg v)) v137_l403)))


(def v139_l409 (kind/doc #'sk/sketch->figure))


(def v140_l411 (first (sk/sketch->figure sk1 :svg {})))


(deftest t141_l413 (is ((fn [v] (= :svg v)) v140_l411)))


(def v143_l417 (kind/doc #'sk/coord))


(def v145_l421 (-> iris (sk/lay-bar :species) (sk/coord :flip)))


(deftest
 t146_l424
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:polygons s))))
   v145_l421)))


(def v148_l429 (-> iris (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t149_l432
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v148_l429)))


(def v150_l435 (kind/doc #'sk/scale))


(def
 v152_l439
 (-> iris (sk/lay-point :sepal_length :sepal_width) (sk/scale :x :log)))


(deftest
 t153_l442
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v152_l439)))


(def
 v155_l447
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/scale :x {:domain [3 9]})))


(deftest
 t156_l450
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v155_l447)))


(def v157_l453 (kind/doc #'sk/labs))


(def
 v158_l455
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/labs
   {:title "Iris Dimensions",
    :x "Sepal Length (cm)",
    :y "Sepal Width (cm)"})))


(deftest
 t159_l458
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (some #{"Iris Dimensions"} (:texts s))))
   v158_l455)))


(def v161_l463 (kind/doc #'sk/rule-v))


(def
 v162_l465
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-v 6.0))))


(deftest
 t163_l468
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v162_l465)))


(def v164_l472 (kind/doc #'sk/rule-h))


(def
 v165_l474
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/rule-h 3.0))))


(deftest
 t166_l477
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (pos? (:lines s)))))
   v165_l474)))


(def v167_l481 (kind/doc #'sk/band-v))


(def
 v168_l483
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-v 5.5 6.5))))


(deftest
 t169_l486
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v168_l483)))


(def v170_l489 (kind/doc #'sk/band-h))


(def
 v171_l491
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width)
  (sk/lay (sk/band-h 2.5 3.5))))


(deftest
 t172_l494
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s))))
   v171_l491)))


(def v174_l499 (kind/doc #'sk/cross))


(def v175_l501 (sk/cross [:a :b] [1 2 3]))


(deftest
 t176_l503
 (is
  ((fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))
   v175_l501)))


(def
 v177_l505
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :petal_length]
    [:sepal_width :petal_width]))
  (sk/lay-point {:color :species})))


(deftest
 t178_l510
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 600 (:points s)))))
   v177_l505)))


(def v179_l514 (kind/doc #'sk/distribution))


(def
 v180_l516
 (->
  (sk/distribution iris :sepal_length :sepal_width)
  sk/lay-histogram))


(deftest
 t181_l519
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:panels s)) (pos? (:polygons s)))))
   v180_l516)))


(def v183_l525 (kind/doc #'sk/facet))


(def
 v184_l527
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  (sk/facet :species)))


(deftest
 t185_l531
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v184_l527)))


(def v186_l535 (kind/doc #'sk/facet-grid))


(def
 v187_l537
 (->
  tips
  (sk/lay-point :total_bill :tip {:color :sex})
  (sk/facet-grid :smoker :sex)))


(deftest
 t188_l541
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v187_l537)))


(def v190_l547 (kind/doc #'sk/svg-summary))


(def
 v191_l549
 (->
  iris
  (sk/lay-point :sepal_length :sepal_width {:color :species})
  sk/svg-summary))


(deftest
 t192_l552
 (is ((fn [m] (and (= 1 (:panels m)) (= 150 (:points m)))) v191_l549)))


(def v193_l555 (kind/doc #'sk/valid-sketch?))


(def v194_l557 (sk/valid-sketch? sk1))


(deftest t195_l559 (is (true? v194_l557)))


(def v196_l561 (kind/doc #'sk/explain-sketch))


(def v197_l563 (sk/explain-sketch sk1))


(deftest t198_l565 (is (nil? v197_l563)))


(def v200_l569 (kind/doc #'sk/config))


(def v201_l571 (sk/config))


(deftest t202_l573 (is ((fn [m] (map? m)) v201_l571)))


(def v203_l575 (kind/doc #'sk/set-config!))


(def v204_l577 (kind/doc #'sk/with-config))


(def
 v205_l579
 (sk/with-config {:palette :pastel1} (:palette (sk/config))))


(deftest t206_l582 (is ((fn [p] (= :pastel1 p)) v205_l579)))


(def v208_l586 (kind/doc #'sk/arrange))


(def
 v209_l588
 (sk/arrange
  [(->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:width 250, :height 200}))
   (->
    iris
    (sk/lay-point :petal_length :petal_width {:color :species})
    (sk/options {:width 250, :height 200}))]
  {:cols 2}))


(deftest t210_l594 (is ((fn [v] (= :div (first v))) v209_l588)))


(def v212_l597 (kind/doc #'sk/save))


(def
 v214_l601
 (let
  [path
   (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save
   (->
    iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))
   path
   {:title "Iris Export"})
  (.contains (slurp path) "<svg")))


(deftest t215_l607 (is (true? v214_l601)))
