(ns
 plotje-book.configuration-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (defn
  base-plot
  []
  (->
   (rdatasets/datasets-iris)
   (pj/lay-point :sepal-length :sepal-width {:color :species}))))


(def v5_l38 (pj/config))


(deftest
 t6_l40
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 10 (:margin cfg))
     (map? (:theme cfg))))
   v5_l38)))


(def
 v8_l52
 (def
  category-order
  ["Layout"
   "Theme"
   "Typography"
   "Points"
   "Bars & Lines"
   "Annotations"
   "Ticks"
   "Statistics"
   "Labels"
   "Behavior"
   "Color"
   "Interaction"
   "Output"]))


(def
 v9_l57
 (kind/table
  {:column-names ["Key" "Default" "Category" "Description"],
   :row-maps
   (let
    [cfg (pj/config)]
    (->>
     pj/config-key-docs
     (sort-by
      (fn [[k [cat]]] [(.indexOf category-order cat) (name k)]))
     (mapv
      (fn
       [[k [cat desc]]]
       {"Key" (kind/code (pr-str k)),
        "Default" (kind/code (pr-str (get cfg k))),
        "Category" cat,
        "Description" desc}))))}))


(deftest t10_l70 (is ((fn [t] (= 37 (count (:row-maps t)))) v9_l57)))


(def
 v12_l80
 (kind/table
  {:column-names ["Key" "Category" "Description"],
   :row-maps
   (->>
    pj/plot-option-docs
    (sort-by (fn [[k [cat]]] [cat (name k)]))
    (mapv
     (fn
      [[k [cat desc]]]
      {"Key" (kind/code (pr-str k)),
       "Category" cat,
       "Description" desc})))}))


(deftest t13_l90 (is ((fn [t] (= 11 (count (:row-maps t)))) v12_l80)))


(def v15_l100 (select-keys (pj/config) [:width :height]))


(deftest
 t16_l102
 (is ((fn [m] (= {:width 600, :height 400} m)) v15_l100)))


(def v17_l104 (-> (base-plot) (pj/options {:width 900, :height 250})))


(deftest
 t18_l107
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v17_l104)))


(def v20_l116 (-> (base-plot) (pj/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t21_l119
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v20_l116)))


(def v23_l125 (-> (base-plot) (pj/options {:palette :dark2})))


(deftest
 t24_l128
 (is
  ((fn [v] (let [s (pj/svg-summary v)] (= 150 (:points s)))) v23_l125)))


(def v26_l140 (pj/set-config! {:width 800}))


(def v27_l142 (select-keys (pj/config) [:width :height]))


(deftest
 t28_l144
 (is ((fn [m] (= {:width 800, :height 400} m)) v27_l142)))


(def v29_l146 (-> (base-plot)))


(def v31_l150 (pj/set-config! nil))


(def v32_l152 (select-keys (pj/config) [:width :height]))


(deftest
 t33_l154
 (is ((fn [m] (= {:width 600, :height 400} m)) v32_l152)))


(def
 v35_l165
 (pj/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-plot) (pj/options {:title "Dark Theme via with-config"}))))


(deftest
 t36_l169
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v35_l165)))


(def
 v38_l176
 (pj/with-config
  {:theme {:bg "#F5F5DC"}}
  (-> (base-plot) (pj/options {:title "Partial Theme Override"}))))


(deftest
 t39_l180
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v38_l176)))


(def v41_l186 (select-keys (pj/config) [:width :height]))


(deftest
 t42_l188
 (is ((fn [m] (= {:width 600, :height 400} m)) v41_l186)))


(def v44_l204 (:point-radius (pj/config)))


(deftest t45_l206 (is ((fn [v] (= 3.0 v)) v44_l204)))


(def
 v47_l210
 (pj/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v49_l215
 (def
  precedence-result
  (pj/with-config
   {:width 1200, :height 500}
   (let
    [plan (pj/plan (base-plot) {:width 900})]
    {:plan-width (:width plan), :plan-height (:height plan)}))))


(def v50_l222 precedence-result)


(deftest
 t51_l224
 (is
  ((fn [m] (and (= 900 (:plan-width m)) (= 500 (:plan-height m))))
   v50_l222)))


(def
 v53_l232
 (def
  precedence-point-radius
  (pj/with-config
   {:width 1200, :height 500}
   (:point-radius (pj/config)))))


(def v54_l236 precedence-point-radius)


(deftest t55_l238 (is ((fn [v] (= 5.0 v)) v54_l236)))


(def
 v57_l242
 (def
  precedence-plot
  (pj/with-config
   {:width 1200, :height 500}
   (-> (base-plot) (pj/options {:width 900})))))


(def v58_l247 precedence-plot)


(deftest
 t59_l249
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 150 (:points s)) (= 900.0 (double (:width s))))))
   v58_l247)))


(def v61_l259 (pj/set-config! nil))


(def v62_l261 (select-keys (pj/config) [:width :height :point-radius]))


(deftest
 t63_l263
 (is
  ((fn [m] (= {:width 600, :height 400, :point-radius 3.0} m))
   v62_l261)))


(def v65_l301 (count (:theme (pj/config))))


(deftest t66_l303 (is ((fn [n] (= 3 n)) v65_l301)))


(def
 v68_l312
 (-> (base-plot) (pj/options {:theme {:bg "#F5F5DC"}}) pj/plot))


(deftest
 t69_l316
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(245,245,245)"))))
   v68_l312)))


(def
 v71_l326
 (->
  (base-plot)
  (pj/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  pj/plot))


(deftest
 t72_l331
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v71_l326)))


(def
 v74_l341
 (pj/arrange
  [(->
    (base-plot)
    (pj/options
     {:title "Light",
      :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 8},
      :width 350,
      :height 250}))
   (->
    (base-plot)
    (pj/options
     {:title "Dark",
      :theme {:bg "#2d2d2d", :grid "#444444", :font-size 8},
      :width 350,
      :height 250}))]))


(deftest
 t75_l351
 (is
  ((fn
    [v]
    (and (pj/pose? v) (= 2 (count (:poses (first (:poses v)))))))
   v74_l341)))


(def v77_l376 (-> (base-plot) (pj/options {:palette :tableau10})))


(deftest
 t78_l379
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v77_l376)))


(def
 v80_l384
 (->
  (base-plot)
  (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t81_l387
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v80_l384)))


(def
 v83_l392
 (->
  (base-plot)
  (pj/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t84_l397
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v83_l392)))


(def v86_l402 (pj/set-config! {:palette :pastel1}))


(def v87_l404 (-> (base-plot)))


(deftest
 t88_l406
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v87_l404)))


(def v89_l409 (pj/set-config! nil))


(def v91_l413 (pj/with-config {:palette :accent} (-> (base-plot))))


(deftest
 t92_l416
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v91_l413)))


(def
 v94_l427
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})))


(deftest
 t95_l430
 (is ((fn [v] (= 50 (:points (pj/svg-summary v)))) v94_l427)))


(def
 v97_l434
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})
  (pj/options {:color-scale :inferno})))


(deftest
 t98_l438
 (is ((fn [v] (= 50 (:points (pj/svg-summary v)))) v97_l434)))


(def
 v100_l442
 (pj/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (pj/lay-point :x :y {:color :c}))))


(deftest
 t101_l446
 (is ((fn [v] (= 50 (:points (pj/svg-summary v)))) v100_l442)))


(def
 v103_l452
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (pj/lay-point :x :y {:color :c})
  (pj/plan {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t104_l458
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v103_l452)))


(def v106_l474 (pj/plan (base-plot)))


(deftest
 t107_l476
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v106_l474)))


(def v109_l483 (-> (base-plot)))


(deftest
 t110_l485
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v109_l483)))


(def v112_l494 (def good-plan (pj/plan (base-plot) {:validate false})))


(def v113_l496 (pj/valid-plan? good-plan))


(deftest t114_l498 (is ((fn [v] (true? v)) v113_l496)))


(def v116_l503 (def bad-plan (assoc good-plan :width "not-a-number")))


(def v117_l505 (pj/valid-plan? bad-plan))


(deftest t118_l507 (is ((fn [v] (false? v)) v117_l505)))


(def
 v120_l512
 (->
  (pj/explain-plan bad-plan)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t121_l517
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v120_l512)))


(def
 v123_l526
 (try
  (let
   [plan
    (pj/plan (base-plot) {:validate false})
    bad
    (assoc plan :width "not-a-number")]
   (when-let
    [explanation (pj/explain-plan bad)]
    (throw
     (ex-info
      "Plan does not conform to schema"
      {:explanation explanation})))
   :no-error)
  (catch Exception e {:caught true, :message (.getMessage e)})))


(deftest
 t124_l537
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Plan does not conform to schema" (:message m))))
   v123_l526)))


(def v126_l546 (pj/plan (base-plot) {:validate false}))


(deftest
 t127_l548
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v126_l546)))
