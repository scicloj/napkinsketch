(ns
 napkinsketch-book.configuration-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (defn
  base-plot
  []
  (->
   data/iris
   (sk/lay-point :sepal_length :sepal_width {:color :species}))))


(def v5_l39 (sk/config))


(deftest
 t6_l41
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 30 (:margin cfg))
     (map? (:theme cfg))))
   v5_l39)))


(def
 v8_l53
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
 v9_l58
 (kind/table
  {:column-names ["Key" "Default" "Category" "Description"],
   :row-maps
   (let
    [cfg (sk/config)]
    (->>
     sk/config-key-docs
     (sort-by
      (fn [[k [cat]]] [(.indexOf category-order cat) (name k)]))
     (mapv
      (fn
       [[k [cat desc]]]
       {"Key" (kind/code (pr-str k)),
        "Default" (kind/code (pr-str (get cfg k))),
        "Category" cat,
        "Description" desc}))))}))


(deftest t10_l71 (is ((fn [t] (= 36 (count (:row-maps t)))) v9_l58)))


(def
 v12_l78
 (kind/table
  {:column-names ["Key" "Category" "Description"],
   :row-maps
   (->>
    sk/plot-option-docs
    (sort-by (fn [[k [cat]]] [cat (name k)]))
    (mapv
     (fn
      [[k [cat desc]]]
      {"Key" (kind/code (pr-str k)),
       "Category" cat,
       "Description" desc})))}))


(deftest t13_l88 (is ((fn [t] (= 6 (count (:row-maps t)))) v12_l78)))


(def v15_l98 (select-keys (sk/config) [:width :height]))


(deftest
 t16_l100
 (is ((fn [m] (= {:width 600, :height 400} m)) v15_l98)))


(def v17_l102 (-> (base-plot) (sk/options {:width 900, :height 250})))


(deftest
 t18_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v17_l102)))


(def v20_l114 (-> (base-plot) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t21_l117
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v20_l114)))


(def v23_l123 (-> (base-plot) (sk/options {:palette :dark2})))


(deftest
 t24_l126
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v23_l123)))


(def v26_l138 (sk/set-config! {:width 800}))


(def v27_l140 (select-keys (sk/config) [:width :height]))


(deftest
 t28_l142
 (is ((fn [m] (= {:width 800, :height 400} m)) v27_l140)))


(def v29_l144 (-> (base-plot)))


(def v31_l148 (sk/set-config! nil))


(def v32_l150 (select-keys (sk/config) [:width :height]))


(deftest
 t33_l152
 (is ((fn [m] (= {:width 600, :height 400} m)) v32_l150)))


(def
 v35_l163
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-plot) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t36_l167
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v35_l163)))


(def
 v38_l174
 (sk/with-config
  {:theme {:bg "#F5F5DC"}}
  (-> (base-plot) (sk/options {:title "Partial Theme Override"}))))


(deftest
 t39_l178
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v38_l174)))


(def v41_l184 (select-keys (sk/config) [:width :height]))


(deftest
 t42_l186
 (is ((fn [m] (= {:width 600, :height 400} m)) v41_l184)))


(def v44_l202 (:point-radius (sk/config)))


(deftest t45_l204 (is ((fn [v] (= 3.0 v)) v44_l202)))


(def
 v47_l208
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v49_l213
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [plan (sk/plan (base-plot) {:width 900})]
    {:plan-width (:width plan), :plan-height (:height plan)}))))


(def v50_l220 precedence-result)


(deftest
 t51_l222
 (is
  ((fn [m] (and (= 900 (:plan-width m)) (= 500 (:plan-height m))))
   v50_l220)))


(def
 v53_l230
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v54_l234 precedence-point-radius)


(deftest t55_l236 (is ((fn [v] (= 5.0 v)) v54_l234)))


(def
 v57_l240
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-plot) (sk/options {:width 900})))))


(def v58_l245 precedence-plot)


(deftest
 t59_l247
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v58_l245)))


(def v61_l257 (sk/set-config! nil))


(def v62_l259 (select-keys (sk/config) [:width :height :point-radius]))


(deftest
 t63_l261
 (is
  ((fn [m] (= {:width 600, :height 400, :point-radius 3.0} m))
   v62_l259)))


(def v65_l299 (count (:theme (sk/config))))


(deftest t66_l301 (is ((fn [n] (= 3 n)) v65_l299)))


(def
 v68_l310
 (-> (base-plot) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t69_l314
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(245,245,245)"))))
   v68_l310)))


(def
 v71_l324
 (->
  (base-plot)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t72_l329
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v71_l324)))


(def
 v74_l339
 (sk/arrange
  [(->
    (base-plot)
    (sk/options
     {:title "Light",
      :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 8},
      :width 350,
      :height 250}))
   (->
    (base-plot)
    (sk/options
     {:title "Dark",
      :theme {:bg "#2d2d2d", :grid "#444444", :font-size 8},
      :width 350,
      :height 250}))]))


(deftest t75_l349 (is ((fn [v] (= :div (first v))) v74_l339)))


(def v77_l373 (-> (base-plot) (sk/options {:palette :tableau10})))


(deftest
 t78_l376
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l373)))


(def
 v80_l381
 (->
  (base-plot)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t81_l384
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l381)))


(def
 v83_l389
 (->
  (base-plot)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t84_l394
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v83_l389)))


(def v86_l399 (sk/set-config! {:palette :pastel1}))


(def v87_l401 (-> (base-plot)))


(deftest
 t88_l403
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v87_l401)))


(def v89_l406 (sk/set-config! nil))


(def v91_l410 (sk/with-config {:palette :accent} (-> (base-plot))))


(deftest
 t92_l413
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v91_l410)))


(def
 v94_l424
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t95_l427
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v94_l424)))


(def
 v97_l431
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t98_l435
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v97_l431)))


(def
 v100_l439
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t101_l443
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v100_l439)))


(def
 v103_l449
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/plan {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t104_l455
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v103_l449)))


(def v106_l471 (sk/plan (base-plot)))


(deftest
 t107_l473
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v106_l471)))


(def v109_l480 (-> (base-plot)))


(deftest
 t110_l482
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v109_l480)))


(def v112_l491 (def good-plan (sk/plan (base-plot) {:validate false})))


(def v113_l493 (sk/valid-plan? good-plan))


(deftest t114_l495 (is ((fn [v] (true? v)) v113_l493)))


(def v116_l500 (def bad-plan (assoc good-plan :width "not-a-number")))


(def v117_l502 (sk/valid-plan? bad-plan))


(deftest t118_l504 (is ((fn [v] (false? v)) v117_l502)))


(def
 v120_l509
 (->
  (sk/explain-plan bad-plan)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t121_l514
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v120_l509)))


(def
 v123_l523
 (try
  (let
   [plan
    (sk/plan (base-plot) {:validate false})
    bad
    (assoc plan :width "not-a-number")]
   (when-let
    [explanation (sk/explain-plan bad)]
    (throw
     (ex-info
      "Plan does not conform to schema"
      {:explanation explanation})))
   :no-error)
  (catch Exception e {:caught true, :message (.getMessage e)})))


(deftest
 t124_l534
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Plan does not conform to schema" (:message m))))
   v123_l523)))


(def v126_l543 (sk/plan (base-plot) {:validate false}))


(deftest
 t127_l545
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v126_l543)))
