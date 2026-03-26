(ns
 napkinsketch-book.configuration-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l23
 (def
  base-views
  (fn
   []
   (->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})))))


(def v5_l32 (sk/config))


(deftest
 t6_l34
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 25 (:margin cfg))
     (map? (:theme cfg))))
   v5_l32)))


(def v8_l71 (-> (base-views)))


(deftest
 t9_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v8_l71)))


(def v11_l81 (-> (base-views) sk/sketch :width))


(deftest t12_l85 (is ((fn [v] (= 600 v)) v11_l81)))


(def v14_l89 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t15_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v14_l89)))


(def
 v17_l100
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t18_l104
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v17_l100)))


(def
 v20_l111
 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}}) sk/plot))


(deftest
 t21_l115
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v20_l111)))


(def v23_l122 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t24_l125
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v23_l122)))


(def v26_l137 (sk/set-config! {:width 800}))


(def v28_l141 (:width (sk/config)))


(deftest t29_l143 (is ((fn [v] (= 800 v)) v28_l141)))


(def v31_l147 (-> (base-views) sk/sketch :width))


(deftest t32_l151 (is ((fn [v] (= 800 v)) v31_l147)))


(def v34_l155 (-> (base-views) sk/plot))


(deftest
 t35_l157
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v34_l155)))


(def v37_l165 (sk/set-config! nil))


(def v38_l167 (:width (sk/config)))


(deftest t39_l169 (is ((fn [v] (= 600 v)) v38_l167)))


(def
 v41_l180
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t42_l183 (is ((fn [v] (= 1000 v)) v41_l180)))


(def v44_l187 (:width (sk/config)))


(deftest t45_l189 (is ((fn [v] (= 600 v)) v44_l187)))


(def
 v47_l193
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t48_l198 (is ((fn [v] (= 1000 v)) v47_l193)))


(def
 v50_l202
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (->
   (base-views)
   (sk/options {:title "Dark Theme via with-config"})
   sk/plot)))


(deftest
 t51_l207
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v50_l202)))


(def
 v53_l227
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v55_l232
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v56_l239 precedence-result)


(deftest
 t57_l241
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v56_l239)))


(def
 v59_l250
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v60_l254 precedence-point-radius)


(deftest t61_l256 (is ((fn [v] (= 5.0 v)) v60_l254)))


(def
 v63_l260
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v64_l265 precedence-plot)


(deftest
 t65_l267
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v64_l265)))


(def v67_l277 (sk/set-config! nil))


(def
 v69_l320
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t70_l324
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v69_l320)))


(def
 v72_l334
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t73_l339
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v72_l334)))


(def
 v75_l349
 (sk/arrange
  [(->
    (base-views)
    (sk/options
     {:title "Light",
      :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 8},
      :width 350,
      :height 250}))
   (->
    (base-views)
    (sk/options
     {:title "Dark",
      :theme {:bg "#2d2d2d", :grid "#444444", :font-size 8},
      :width 350,
      :height 250}))]))


(deftest t76_l359 (is ((fn [v] (= :div (first v))) v75_l349)))


(def v78_l383 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t79_l386
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v78_l383)))


(def
 v81_l391
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t82_l394
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v81_l391)))


(def
 v84_l399
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t85_l404
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v84_l399)))


(def v87_l409 (sk/set-config! {:palette :pastel1}))


(def v88_l411 (-> (base-views)))


(deftest
 t89_l413
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v88_l411)))


(def v90_l416 (sk/set-config! nil))


(def v92_l420 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t93_l423
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v92_l420)))


(def
 v95_l434
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t96_l437
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v95_l434)))


(def
 v98_l441
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t99_l445
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v98_l441)))


(def
 v101_l449
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t102_l453
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v101_l449)))


(def
 v104_l459
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t105_l465
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v104_l459)))


(def v107_l481 (sk/sketch (base-views)))


(deftest
 t108_l483
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v107_l481)))


(def v110_l490 (-> (base-views)))


(deftest
 t111_l492
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v110_l490)))


(def
 v113_l501
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v114_l503 (sk/valid-sketch? good-sketch))


(deftest t115_l505 (is ((fn [v] (true? v)) v114_l503)))


(def
 v117_l510
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v118_l512 (sk/valid-sketch? bad-sketch))


(deftest t119_l514 (is ((fn [v] (false? v)) v118_l512)))


(def
 v121_l519
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t122_l524
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v121_l519)))


(def
 v124_l533
 (try
  (let
   [sketch
    (sk/sketch (base-views) {:validate false})
    bad
    (assoc sketch :width "not-a-number")]
   (when-let
    [explanation (sk/explain-sketch bad)]
    (throw
     (ex-info
      "Sketch does not conform to schema"
      {:explanation explanation})))
   :no-error)
  (catch Exception e {:caught true, :message (.getMessage e)})))


(deftest
 t125_l544
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v124_l533)))


(def v127_l553 (sk/sketch (base-views) {:validate false}))


(deftest
 t128_l555
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v127_l553)))
