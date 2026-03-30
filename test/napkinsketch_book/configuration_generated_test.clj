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


(def
 v8_l46
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
 v9_l51
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


(deftest t10_l64 (is ((fn [t] (= 36 (count (:row-maps t)))) v9_l51)))


(def
 v12_l71
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


(deftest t13_l81 (is ((fn [t] (= 6 (count (:row-maps t)))) v12_l71)))


(def v15_l91 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t16_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l91)))


(def v18_l103 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t19_l106
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l103)))


(def v21_l112 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t22_l115
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v21_l112)))


(def v24_l127 (sk/set-config! {:width 800}))


(def v25_l129 (:width (sk/config)))


(deftest t26_l131 (is ((fn [v] (= 800 v)) v25_l129)))


(def v27_l133 (-> (base-views)))


(def v29_l137 (sk/set-config! nil))


(def v30_l139 (:width (sk/config)))


(deftest t31_l141 (is ((fn [v] (= 600 v)) v30_l139)))


(def
 v33_l152
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t34_l156
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v33_l152)))


(def v36_l162 (:width (sk/config)))


(deftest t37_l164 (is ((fn [v] (= 600 v)) v36_l162)))


(def
 v39_l180
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v41_l185
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v42_l192 precedence-result)


(deftest
 t43_l194
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v42_l192)))


(def
 v45_l203
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v46_l207 precedence-point-radius)


(deftest t47_l209 (is ((fn [v] (= 5.0 v)) v46_l207)))


(def
 v49_l213
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v50_l218 precedence-plot)


(deftest
 t51_l220
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v50_l218)))


(def v53_l230 (sk/set-config! nil))


(def
 v55_l273
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t56_l277
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v55_l273)))


(def
 v58_l287
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t59_l292
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v58_l287)))


(def
 v61_l302
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


(deftest t62_l312 (is ((fn [v] (= :div (first v))) v61_l302)))


(def v64_l336 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t65_l339
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v64_l336)))


(def
 v67_l344
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t68_l347
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v67_l344)))


(def
 v70_l352
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t71_l357
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v70_l352)))


(def v73_l362 (sk/set-config! {:palette :pastel1}))


(def v74_l364 (-> (base-views)))


(deftest
 t75_l366
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v74_l364)))


(def v76_l369 (sk/set-config! nil))


(def v78_l373 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t79_l376
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v78_l373)))


(def
 v81_l387
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t82_l390
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v81_l387)))


(def
 v84_l394
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t85_l398
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v84_l394)))


(def
 v87_l402
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t88_l406
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v87_l402)))


(def
 v90_l412
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t91_l418
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v90_l412)))


(def v93_l434 (sk/sketch (base-views)))


(deftest
 t94_l436
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v93_l434)))


(def v96_l443 (-> (base-views)))


(deftest
 t97_l445
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v96_l443)))


(def
 v99_l454
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v100_l456 (sk/valid-sketch? good-sketch))


(deftest t101_l458 (is ((fn [v] (true? v)) v100_l456)))


(def
 v103_l463
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v104_l465 (sk/valid-sketch? bad-sketch))


(deftest t105_l467 (is ((fn [v] (false? v)) v104_l465)))


(def
 v107_l472
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t108_l477
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v107_l472)))


(def
 v110_l486
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
 t111_l497
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v110_l486)))


(def v113_l506 (sk/sketch (base-views) {:validate false}))


(deftest
 t114_l508
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v113_l506)))
