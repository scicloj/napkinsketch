(ns
 plotje-book.customization-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l22
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 800, :height 250})))


(deftest
 t4_l26
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v3_l22)))


(def
 v6_l32
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 300, :height 500})))


(deftest
 t7_l36
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v6_l32)))


(def
 v9_l44
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t10_l50
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v9_l44)))


(def
 v12_l56
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t13_l62
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v12_l56)))


(def
 v15_l70
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:color-label "Species (override)"})))


(deftest
 t16_l74
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Species (override)"} (:texts s)))))
   v15_l70)))


(def
 v18_l80
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:size :petal-length})
  (pj/options {:size-label "Petal length (override)"})))


(deftest
 t19_l84
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Petal length (override)"} (:texts s)))))
   v18_l80)))


(def
 v21_l90
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:alpha :petal-length})
  (pj/options {:alpha-label "Petal length (override)"})))


(deftest
 t22_l94
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Petal length (override)"} (:texts s)))))
   v21_l90)))


(def
 v24_l109
 (->
  {:x [1 2 3 1 2 3], :y [1 1 1 2 2 2], :z [10 20 30 40 50 60]}
  (pj/lay-tile :x :y {:fill :z})
  (pj/options {:fill-label "Score"})))


(deftest
 t25_l113
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (some #{"Score"} (:texts s)) (pos? (:visible-tiles s)))))
   v24_l109)))


(def
 v27_l121
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__86391#] (* 2 (Math/pow 1.1 p1__86391#)))
    (range 1 50))}))


(def
 v29_l127
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/options {:title "Linear Scale"})))


(deftest
 t30_l131
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v29_l127)))


(def
 v32_l137
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/scale :y :log)
  (pj/options {:title "Log Y Scale"})))


(deftest
 t33_l142
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v32_l137)))


(def
 v35_l148
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :domain [0 6]})
  (pj/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t36_l153
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v35_l148)))


(def
 v38_l160
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :breaks [2.0 3.0 4.0]})))


(deftest
 t39_l164
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (every? (set (:texts s)) ["2" "3" "4"]))))
   v38_l160)))


(def
 v41_l172
 (->
  {:size ["medium" "small" "large"], :count [12 30 7]}
  (pj/lay-value-bar :size :count)
  (pj/scale
   :x
   {:type :categorical, :domain ["large" "medium" "small"]})))


(deftest
 t42_l177
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      labels
      (filter #{"small" "medium" "large"} (:texts s))]
     (= ["large" "medium" "small"] (vec labels))))
   v41_l172)))


(def
 v44_l194
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t45_l198
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v44_l194)))


(def
 v47_l207
 (->
  (for
   [r (range 5) c (range 5)]
   {:r r, :c c, :v (Math/pow 10.0 (/ (+ r c) 2.0))})
  (pj/lay-tile :r :c {:fill :v})
  (pj/scale :fill :log)))


(deftest
 t48_l212
 (is ((fn [v] (>= (:visible-tiles (pj/svg-summary v)) 25)) v47_l207)))


(def
 v50_l226
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (pj/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t51_l229
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v50_l226)))


(def
 v53_l242
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t54_l245
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (contains? (:alphas s) 0.5)
      (contains? (:sizes s) 5.0))))
   v53_l242)))


(def
 v56_l253
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-line :x :y {:size 3})))


(deftest
 t57_l256
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v56_l253)))


(def
 v59_l260
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species {:alpha 0.4})))


(deftest
 t60_l263
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (contains? (:alphas s) 0.4))))
   v59_l260)))


(def
 v62_l281
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color "#E74C3C"})))


(deftest
 t63_l284
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (contains? (:colors s) "rgb(231,76,60)"))))
   v62_l281)))


(def
 v65_l294
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :petal-length})))


(deftest
 t66_l297
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 150 (:points s))
      (some #{"petal length"} (:texts s)))))
   v65_l294)))


(def
 v68_l307
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point :total-bill :tip {:color :day, :size :size})))


(deftest
 t69_l310
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v68_l307)))


(def
 v71_l316
 (->
  (rdatasets/reshape2-tips)
  (pj/lay-point
   :total-bill
   :tip
   {:color :day, :size :size, :alpha 0.6})))


(deftest
 t72_l319
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)))))
   v71_l316)))


(def
 v74_l325
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :petal-length, :size :petal-width, :alpha 0.7})))


(deftest
 t75_l329
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (some #{"petal length"} (:texts s)))))
   v74_l325)))


(def
 v77_l339
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:shape :species})))


(deftest
 t78_l342
 (is
  ((fn
    [v]
    (let
     [layer
      (-> v pj/plan :panels first :layers first)
      shape-values
      (set (mapcat :shapes (:groups layer)))]
     (= 3 (count shape-values))))
   v77_l339)))


(def
 v80_l359
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t81_l364
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v80_l359)))


(def
 v83_l373
 (->
  {:date
   [#inst "2024-01-01T00:00:00.000-00:00"
    #inst "2024-04-01T00:00:00.000-00:00"
    #inst "2024-08-01T00:00:00.000-00:00"],
   :value [3 5 9]}
  (pj/lay-line :date :value)
  (pj/lay-rule-v
   {:x-intercept (java.time.LocalDate/parse "2024-06-01"),
    :color "#c0392b"})))


(deftest
 t84_l379
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v83_l373)))


(def v86_l386 (:band-opacity (pj/config)))


(deftest t87_l388 (is ((fn [v] (= 0.15 v)) v86_l386)))


(def
 v88_l390
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})
  (pj/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t89_l395
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v88_l390)))


(def
 v91_l418
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t92_l422
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v91_l418)))


(def
 v94_l426
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :dark2})))


(deftest
 t95_l430
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v94_l426)))


(def v97_l442 (c2d/find-palette #"budapest"))


(deftest
 t98_l444
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v97_l442)))


(def v100_l448 (c2d/find-palette #"^:set"))


(deftest
 t101_l450
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v100_l448)))


(def v103_l454 (c2d/find-gradient #"viridis"))


(deftest
 t104_l456
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v103_l454)))


(def v106_l461 (c2d/palette :grand-budapest-1))


(deftest
 t107_l463
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v106_l461)))


(def
 v109_l475
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :khroma/okabeito})))


(deftest
 t110_l479
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v109_l475)))


(def
 v112_l485
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t113_l490
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s))))
   v112_l485)))


(def
 v115_l498
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :bottom})))


(deftest
 t116_l502
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v115_l498)))


(def
 v118_l508
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :top})))


(deftest
 t119_l512
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v118_l508)))


(def
 v121_l518
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :none})))


(deftest
 t122_l522
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      plan
      (pj/plan
       (->
        (rdatasets/datasets-iris)
        (pj/lay-point :sepal-length :sepal-width {:color :species})
        (pj/options {:legend-position :none})))]
     (and
      (= 150 (:points s))
      (zero? (get-in plan [:layout :legend-w])))))
   v121_l518)))


(def
 v124_l535
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t125_l539
 (is ((fn [v] (= :div (first (pj/plot v)))) v124_l535)))


(def
 v127_l545
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:brush true})))


(deftest
 t128_l549
 (is ((fn [v] (= :div (first (pj/plot v)))) v127_l545)))


(def
 v130_l555
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v131_l557
 (->
  (rdatasets/datasets-iris)
  (pj/pose (pj/cross splom-cols splom-cols) {:color :species})
  (pj/options {:brush true})))


(deftest
 t132_l561
 (is ((fn [v] (= :div (first (pj/plot v)))) v131_l557)))
