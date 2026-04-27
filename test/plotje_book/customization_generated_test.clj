(ns
 plotje-book.customization-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [clojure2d.color :as c2d]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 800, :height 250})))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 800))))
   v3_l21)))


(def
 v6_l31
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:width 300, :height 500})))


(deftest
 t7_l35
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (>= (:width s) 300))))
   v6_l31)))


(def
 v9_l43
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Sepal Measurements",
    :x-label "Length (cm)",
    :y-label "Width (cm)"})))


(deftest
 t10_l49
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Sepal Measurements"} (:texts s)))))
   v9_l43)))


(def
 v12_l55
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "Iris Measurements",
    :subtitle "Sepal dimensions across three species",
    :caption "Source: Fisher's Iris dataset (1936)"})))


(deftest
 t13_l61
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Iris Measurements"} (:texts s))
      (some (fn [t] (.contains t "Sepal dimensions")) (:texts s)))))
   v12_l55)))


(def
 v15_l69
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:color-label "Species (override)"})))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Species (override)"} (:texts s)))))
   v15_l69)))


(def
 v18_l79
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:size :petal-length})
  (pj/options {:size-label "Petal length (override)"})))


(deftest
 t19_l83
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Petal length (override)"} (:texts s)))))
   v18_l79)))


(def
 v21_l89
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:alpha :petal-length})
  (pj/options {:alpha-label "Petal length (override)"})))


(deftest
 t22_l93
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (some #{"Petal length (override)"} (:texts s)))))
   v21_l89)))


(def
 v24_l101
 (def
  exponential-data
  {:x (range 1 50),
   :y
   (map
    (fn* [p1__85039#] (* 2 (Math/pow 1.1 p1__85039#)))
    (range 1 50))}))


(def
 v26_l107
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/options {:title "Linear Scale"})))


(deftest
 t27_l111
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v26_l107)))


(def
 v29_l117
 (->
  exponential-data
  (pj/lay-point :x :y)
  (pj/scale :y :log)
  (pj/options {:title "Log Y Scale"})))


(deftest
 t30_l122
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 49 (:points s)))))
   v29_l117)))


(def
 v32_l128
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :domain [0 6]})
  (pj/options {:title "Fixed Y Domain [0, 6]"})))


(deftest
 t33_l133
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v32_l128)))


(def
 v35_l140
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/scale :y {:type :linear, :breaks [2.0 3.0 4.0]})))


(deftest
 t36_l144
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 150 (:points s))
      (every? (set (:texts s)) ["2" "3" "4"]))))
   v35_l140)))


(def
 v38_l152
 (->
  {:size ["medium" "small" "large"], :count [12 30 7]}
  (pj/lay-value-bar :size :count)
  (pj/scale
   :x
   {:type :categorical, :domain ["large" "medium" "small"]})))


(deftest
 t39_l157
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      labels
      (filter #{"small" "medium" "large"} (:texts s))]
     (= ["large" "medium" "small"] (vec labels))))
   v38_l152)))


(def
 v41_l174
 (->
  {:user [:a :b :c], :n [10 100 1000]}
  (pj/lay-point :user :n {:size :n, :x-type :categorical})
  (pj/scale :size :log)))


(deftest
 t42_l178
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v41_l174)))


(def
 v44_l187
 (->
  (for
   [r (range 5) c (range 5)]
   {:r r, :c c, :v (Math/pow 10.0 (/ (+ r c) 2.0))})
  (pj/lay-tile :r :c {:fill :v})
  (pj/scale :fill :log)))


(deftest
 t45_l192
 (is ((fn [v] (>= (:visible-tiles (pj/svg-summary v)) 25)) v44_l187)))


(def
 v47_l206
 (->
  {:hour [9 10 11 12], :count [5 8 12 7]}
  (pj/lay-value-bar :hour :count {:x-type :categorical})))


(deftest
 t48_l209
 (is ((fn [v] (= 4 (:polygons (pj/svg-summary v)))) v47_l206)))


(def
 v50_l222
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:color :species, :alpha 0.5, :size 5})))


(deftest
 t51_l225
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
   v50_l222)))


(def
 v53_l233
 (-> {:x [1 2 3 4 5], :y [2 4 3 5 4]} (pj/lay-line :x :y {:size 3})))


(deftest
 t54_l236
 (is ((fn [v] (= 1 (:lines (pj/svg-summary v)))) v53_l233)))


(def
 v56_l240
 (-> (rdatasets/datasets-iris) (pj/lay-bar :species {:alpha 0.4})))


(deftest
 t57_l243
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:polygons s)) (contains? (:alphas s) 0.4))))
   v56_l240)))


(def
 v59_l258
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-rule-h {:y-intercept 3.0})
  (pj/lay-rule-v {:x-intercept 6.0})))


(deftest
 t60_l263
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 2 (:lines s)))))
   v59_l258)))


(def v62_l270 (:band-opacity (pj/config)))


(deftest t63_l272 (is ((fn [v] (= 0.15 v)) v62_l270)))


(def
 v64_l274
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/lay-band-v {:x-min 5.5, :x-max 6.5})
  (pj/lay-band-h {:y-min 3.0, :y-max 3.5, :alpha 0.3})))


(deftest
 t65_l279
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v64_l274)))


(def
 v67_l302
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t68_l306
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v67_l302)))


(def
 v70_l310
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :dark2})))


(deftest
 t71_l314
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v70_l310)))


(def v73_l326 (c2d/find-palette #"budapest"))


(deftest
 t74_l328
 (is
  ((fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))
   v73_l326)))


(def v76_l332 (c2d/find-palette #"^:set"))


(deftest
 t77_l334
 (is ((fn [v] (and (sequential? v) (some #{:set1} v))) v76_l332)))


(def v79_l338 (c2d/find-gradient #"viridis"))


(deftest
 t80_l340
 (is
  ((fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))
   v79_l338)))


(def v82_l345 (c2d/palette :grand-budapest-1))


(deftest
 t83_l347
 (is ((fn [v] (and (sequential? v) (pos? (count v)))) v82_l345)))


(def
 v85_l359
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:palette :khroma/okabeito})))


(deftest
 t86_l363
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v85_l359)))


(def
 v88_l369
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options
   {:title "White Theme",
    :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 10}})))


(deftest
 t89_l374
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v88_l369)))


(def
 v91_l382
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :bottom})))


(deftest
 t92_l386
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 700))))
   v91_l382)))


(def
 v94_l392
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :top})))


(deftest
 t95_l396
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v94_l392)))


(def
 v97_l402
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:legend-position :none})
  pj/plan
  (get-in [:layout :legend-w])))


(deftest t98_l408 (is (zero? v97_l402)))


(def
 v100_l414
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t101_l418
 (is ((fn [v] (= :div (first (pj/plot v)))) v100_l414)))


(def
 v103_l424
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:brush true})))


(deftest
 t104_l428
 (is ((fn [v] (= :div (first (pj/plot v)))) v103_l424)))


(def
 v106_l434
 (def
  splom-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v107_l436
 (->
  (rdatasets/datasets-iris)
  (pj/pose (pj/cross splom-cols splom-cols) {:color :species})
  (pj/options {:brush true})))


(deftest
 t108_l440
 (is ((fn [v] (= :div (first (pj/plot v)))) v107_l436)))
