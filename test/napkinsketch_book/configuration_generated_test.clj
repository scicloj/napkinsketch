(ns
 napkinsketch-book.configuration-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (def
  base-views
  (fn
   []
   (->
    data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})))))


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
     (= 25 (:margin cfg))
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


(def v17_l102 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t18_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v17_l102)))


(def v20_l114 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t21_l117
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v20_l114)))


(def v23_l123 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t24_l126
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v23_l123)))


(def v26_l138 (sk/set-config! {:width 800}))


(def v27_l140 (select-keys (sk/config) [:width :height]))


(deftest
 t28_l142
 (is ((fn [m] (= {:width 800, :height 400} m)) v27_l140)))


(def v29_l144 (-> (base-views)))


(def v31_l148 (sk/set-config! nil))


(def v32_l150 (select-keys (sk/config) [:width :height]))


(deftest
 t33_l152
 (is ((fn [m] (= {:width 600, :height 400} m)) v32_l150)))


(def
 v35_l163
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t36_l167
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v35_l163)))


(def v38_l173 (select-keys (sk/config) [:width :height]))


(deftest
 t39_l175
 (is ((fn [m] (= {:width 600, :height 400} m)) v38_l173)))


(def v41_l191 (:point-radius (sk/config)))


(deftest t42_l193 (is ((fn [v] (= 2.5 v)) v41_l191)))


(def
 v44_l197
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v46_l202
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v47_l209 precedence-result)


(deftest
 t48_l211
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v47_l209)))


(def
 v50_l220
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v51_l224 precedence-point-radius)


(deftest t52_l226 (is ((fn [v] (= 5.0 v)) v51_l224)))


(def
 v54_l230
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v55_l235 precedence-plot)


(deftest
 t56_l237
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v55_l235)))


(def v58_l247 (sk/set-config! nil))


(def v59_l249 (select-keys (sk/config) [:width :height :point-radius]))


(deftest
 t60_l251
 (is
  ((fn [m] (= {:width 600, :height 400, :point-radius 2.5} m))
   v59_l249)))


(def v62_l289 (count (:theme (sk/config))))


(deftest t63_l291 (is ((fn [n] (= 3 n)) v62_l289)))


(def
 v65_l298
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t66_l302
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v65_l298)))


(def
 v68_l312
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t69_l317
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v68_l312)))


(def
 v71_l327
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


(deftest t72_l337 (is ((fn [v] (= :div (first v))) v71_l327)))


(def v74_l361 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t75_l364
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v74_l361)))


(def
 v77_l369
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t78_l372
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v77_l369)))


(def
 v80_l377
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t81_l382
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v80_l377)))


(def v83_l387 (sk/set-config! {:palette :pastel1}))


(def v84_l389 (-> (base-views)))


(deftest
 t85_l391
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v84_l389)))


(def v86_l394 (sk/set-config! nil))


(def v88_l398 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t89_l401
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v88_l398)))


(def
 v91_l412
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t92_l415
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v91_l412)))


(def
 v94_l419
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t95_l423
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v94_l419)))


(def
 v97_l427
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t98_l431
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v97_l427)))


(def
 v100_l437
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t101_l443
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v100_l437)))


(def v103_l459 (sk/sketch (base-views)))


(deftest
 t104_l461
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v103_l459)))


(def v106_l468 (-> (base-views)))


(deftest
 t107_l470
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v106_l468)))


(def
 v109_l479
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v110_l481 (sk/valid-sketch? good-sketch))


(deftest t111_l483 (is ((fn [v] (true? v)) v110_l481)))


(def
 v113_l488
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v114_l490 (sk/valid-sketch? bad-sketch))


(deftest t115_l492 (is ((fn [v] (false? v)) v114_l490)))


(def
 v117_l497
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t118_l502
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v117_l497)))


(def
 v120_l511
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
 t121_l522
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v120_l511)))


(def v123_l531 (sk/sketch (base-views) {:validate false}))


(deftest
 t124_l533
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v123_l531)))
