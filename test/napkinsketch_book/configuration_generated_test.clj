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


(def v15_l98 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t16_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l98)))


(def v18_l110 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t19_l113
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l110)))


(def v21_l119 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t22_l122
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v21_l119)))


(def v24_l134 (sk/set-config! {:width 800}))


(def v25_l136 (:width (sk/config)))


(deftest t26_l138 (is ((fn [v] (= 800 v)) v25_l136)))


(def v27_l140 (-> (base-views)))


(def v29_l144 (sk/set-config! nil))


(def v30_l146 (:width (sk/config)))


(deftest t31_l148 (is ((fn [v] (= 600 v)) v30_l146)))


(def
 v33_l159
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t34_l163
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v33_l159)))


(def v36_l169 (:width (sk/config)))


(deftest t37_l171 (is ((fn [v] (= 600 v)) v36_l169)))


(def
 v39_l187
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v41_l192
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v42_l199 precedence-result)


(deftest
 t43_l201
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v42_l199)))


(def
 v45_l210
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v46_l214 precedence-point-radius)


(deftest t47_l216 (is ((fn [v] (= 5.0 v)) v46_l214)))


(def
 v49_l220
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v50_l225 precedence-plot)


(deftest
 t51_l227
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v50_l225)))


(def v53_l237 (sk/set-config! nil))


(def
 v55_l280
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t56_l284
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v55_l280)))


(def
 v58_l294
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t59_l299
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v58_l294)))


(def
 v61_l309
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


(deftest t62_l319 (is ((fn [v] (= :div (first v))) v61_l309)))


(def v64_l343 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t65_l346
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v64_l343)))


(def
 v67_l351
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t68_l354
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v67_l351)))


(def
 v70_l359
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t71_l364
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v70_l359)))


(def v73_l369 (sk/set-config! {:palette :pastel1}))


(def v74_l371 (-> (base-views)))


(deftest
 t75_l373
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v74_l371)))


(def v76_l376 (sk/set-config! nil))


(def v78_l380 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t79_l383
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v78_l380)))


(def
 v81_l394
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t82_l397
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v81_l394)))


(def
 v84_l401
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t85_l405
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v84_l401)))


(def
 v87_l409
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t88_l413
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v87_l409)))


(def
 v90_l419
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t91_l425
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v90_l419)))


(def v93_l441 (sk/sketch (base-views)))


(deftest
 t94_l443
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v93_l441)))


(def v96_l450 (-> (base-views)))


(deftest
 t97_l452
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v96_l450)))


(def
 v99_l461
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v100_l463 (sk/valid-sketch? good-sketch))


(deftest t101_l465 (is ((fn [v] (true? v)) v100_l463)))


(def
 v103_l470
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v104_l472 (sk/valid-sketch? bad-sketch))


(deftest t105_l474 (is ((fn [v] (false? v)) v104_l472)))


(def
 v107_l479
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t108_l484
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v107_l479)))


(def
 v110_l493
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
 t111_l504
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v110_l493)))


(def v113_l513 (sk/sketch (base-views) {:validate false}))


(deftest
 t114_l515
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v113_l513)))
