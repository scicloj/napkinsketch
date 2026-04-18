(ns
 napkinsketch-book.configuration-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l31
 (defn
  base-plot
  []
  (->
   (rdatasets/datasets-iris)
   (sk/lay-point :sepal-length :sepal-width {:color :species}))))


(def v5_l40 (sk/config))


(deftest
 t6_l42
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 10 (:margin cfg))
     (map? (:theme cfg))))
   v5_l40)))


(def
 v8_l54
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
 v9_l59
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


(deftest t10_l72 (is ((fn [t] (= 36 (count (:row-maps t)))) v9_l59)))


(def
 v12_l82
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


(deftest t13_l92 (is ((fn [t] (= 11 (count (:row-maps t)))) v12_l82)))


(def v15_l102 (select-keys (sk/config) [:width :height]))


(deftest
 t16_l104
 (is ((fn [m] (= {:width 600, :height 400} m)) v15_l102)))


(def v17_l106 (-> (base-plot) (sk/options {:width 900, :height 250})))


(deftest
 t18_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v17_l106)))


(def v20_l118 (-> (base-plot) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t21_l121
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v20_l118)))


(def v23_l127 (-> (base-plot) (sk/options {:palette :dark2})))


(deftest
 t24_l130
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v23_l127)))


(def v26_l142 (sk/set-config! {:width 800}))


(def v27_l144 (select-keys (sk/config) [:width :height]))


(deftest
 t28_l146
 (is ((fn [m] (= {:width 800, :height 400} m)) v27_l144)))


(def v29_l148 (-> (base-plot)))


(def v31_l152 (sk/set-config! nil))


(def v32_l154 (select-keys (sk/config) [:width :height]))


(deftest
 t33_l156
 (is ((fn [m] (= {:width 600, :height 400} m)) v32_l154)))


(def
 v35_l167
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-plot) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t36_l171
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v35_l167)))


(def
 v38_l178
 (sk/with-config
  {:theme {:bg "#F5F5DC"}}
  (-> (base-plot) (sk/options {:title "Partial Theme Override"}))))


(deftest
 t39_l182
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v38_l178)))


(def v41_l188 (select-keys (sk/config) [:width :height]))


(deftest
 t42_l190
 (is ((fn [m] (= {:width 600, :height 400} m)) v41_l188)))


(def v44_l206 (:point-radius (sk/config)))


(deftest t45_l208 (is ((fn [v] (= 3.0 v)) v44_l206)))


(def
 v47_l212
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v49_l217
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [plan (sk/plan (base-plot) {:width 900})]
    {:plan-width (:width plan), :plan-height (:height plan)}))))


(def v50_l224 precedence-result)


(deftest
 t51_l226
 (is
  ((fn [m] (and (= 900 (:plan-width m)) (= 500 (:plan-height m))))
   v50_l224)))


(def
 v53_l234
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v54_l238 precedence-point-radius)


(deftest t55_l240 (is ((fn [v] (= 5.0 v)) v54_l238)))


(def
 v57_l244
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-plot) (sk/options {:width 900})))))


(def v58_l249 precedence-plot)


(deftest
 t59_l251
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 900.0 (double (:width s))))))
   v58_l249)))


(def v61_l261 (sk/set-config! nil))


(def v62_l263 (select-keys (sk/config) [:width :height :point-radius]))


(deftest
 t63_l265
 (is
  ((fn [m] (= {:width 600, :height 400, :point-radius 3.0} m))
   v62_l263)))


(def v65_l303 (count (:theme (sk/config))))


(deftest t66_l305 (is ((fn [n] (= 3 n)) v65_l303)))


(def
 v68_l314
 (-> (base-plot) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t69_l318
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(245,245,245)"))))
   v68_l314)))


(def
 v71_l328
 (->
  (base-plot)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t72_l333
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v71_l328)))


(def
 v74_l343
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


(deftest t75_l353 (is ((fn [v] (= :div (first v))) v74_l343)))


(def v77_l377 (-> (base-plot) (sk/options {:palette :tableau10})))


(deftest
 t78_l380
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l377)))


(def
 v80_l385
 (->
  (base-plot)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t81_l388
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l385)))


(def
 v83_l393
 (->
  (base-plot)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t84_l398
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v83_l393)))


(def v86_l403 (sk/set-config! {:palette :pastel1}))


(def v87_l405 (-> (base-plot)))


(deftest
 t88_l407
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v87_l405)))


(def v89_l410 (sk/set-config! nil))


(def v91_l414 (sk/with-config {:palette :accent} (-> (base-plot))))


(deftest
 t92_l417
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v91_l414)))


(def
 v94_l428
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t95_l431
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v94_l428)))


(def
 v97_l435
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t98_l439
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v97_l435)))


(def
 v100_l443
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t101_l447
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v100_l443)))


(def
 v103_l453
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/plan {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t104_l459
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v103_l453)))


(def v106_l475 (sk/plan (base-plot)))


(deftest
 t107_l477
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v106_l475)))


(def v109_l484 (-> (base-plot)))


(deftest
 t110_l486
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v109_l484)))


(def v112_l495 (def good-plan (sk/plan (base-plot) {:validate false})))


(def v113_l497 (sk/valid-plan? good-plan))


(deftest t114_l499 (is ((fn [v] (true? v)) v113_l497)))


(def v116_l504 (def bad-plan (assoc good-plan :width "not-a-number")))


(def v117_l506 (sk/valid-plan? bad-plan))


(deftest t118_l508 (is ((fn [v] (false? v)) v117_l506)))


(def
 v120_l513
 (->
  (sk/explain-plan bad-plan)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t121_l518
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v120_l513)))


(def
 v123_l527
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
 t124_l538
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Plan does not conform to schema" (:message m))))
   v123_l527)))


(def v126_l547 (sk/plan (base-plot) {:validate false}))


(deftest
 t127_l549
 (is ((fn [plan] (and (map? plan) (= 600 (:width plan)))) v126_l547)))
