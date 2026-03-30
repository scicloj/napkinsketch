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
   "Behavior"]))


(def
 v9_l50
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


(deftest t10_l63 (is ((fn [t] (= 29 (count (:row-maps t)))) v9_l50)))


(def
 v12_l70
 (kind/table
  {:column-names ["Key" "Category" "Description"],
   :row-maps
   (->>
    sk/per-call-key-docs
    (sort-by (fn [[k [cat]]] [cat (name k)]))
    (mapv
     (fn
      [[k [cat desc]]]
      {"Key" (kind/code (pr-str k)),
       "Category" cat,
       "Description" desc})))}))


(deftest t13_l80 (is ((fn [t] (= 13 (count (:row-maps t)))) v12_l70)))


(def v15_l90 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t16_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l90)))


(def v18_l102 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}})))


(deftest
 t19_l105
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v18_l102)))


(def v21_l111 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t22_l114
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v21_l111)))


(def v24_l126 (sk/set-config! {:width 800}))


(def v25_l128 (:width (sk/config)))


(deftest t26_l130 (is ((fn [v] (= 800 v)) v25_l128)))


(def v27_l132 (-> (base-views)))


(def v29_l136 (sk/set-config! nil))


(def v30_l138 (:width (sk/config)))


(deftest t31_l140 (is ((fn [v] (= 600 v)) v30_l138)))


(def
 v33_l151
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/options {:title "Dark Theme via with-config"}))))


(deftest
 t34_l155
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v33_l151)))


(def v36_l161 (:width (sk/config)))


(deftest t37_l163 (is ((fn [v] (= 600 v)) v36_l161)))


(def
 v39_l179
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v41_l184
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v42_l191 precedence-result)


(deftest
 t43_l193
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v42_l191)))


(def
 v45_l202
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v46_l206 precedence-point-radius)


(deftest t47_l208 (is ((fn [v] (= 5.0 v)) v46_l206)))


(def
 v49_l212
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v50_l217 precedence-plot)


(deftest
 t51_l219
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v50_l217)))


(def v53_l229 (sk/set-config! nil))


(def
 v55_l272
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t56_l276
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v55_l272)))


(def
 v58_l286
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t59_l291
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v58_l286)))


(def
 v61_l301
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


(deftest t62_l311 (is ((fn [v] (= :div (first v))) v61_l301)))


(def v64_l335 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t65_l338
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v64_l335)))


(def
 v67_l343
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t68_l346
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v67_l343)))


(def
 v70_l351
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t71_l356
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v70_l351)))


(def v73_l361 (sk/set-config! {:palette :pastel1}))


(def v74_l363 (-> (base-views)))


(deftest
 t75_l365
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v74_l363)))


(def v76_l368 (sk/set-config! nil))


(def v78_l372 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t79_l375
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v78_l372)))


(def
 v81_l386
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t82_l389
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v81_l386)))


(def
 v84_l393
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t85_l397
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v84_l393)))


(def
 v87_l401
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t88_l405
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v87_l401)))


(def
 v90_l411
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t91_l417
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v90_l411)))


(def v93_l433 (sk/sketch (base-views)))


(deftest
 t94_l435
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v93_l433)))


(def v96_l442 (-> (base-views)))


(deftest
 t97_l444
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v96_l442)))


(def
 v99_l453
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v100_l455 (sk/valid-sketch? good-sketch))


(deftest t101_l457 (is ((fn [v] (true? v)) v100_l455)))


(def
 v103_l462
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v104_l464 (sk/valid-sketch? bad-sketch))


(deftest t105_l466 (is ((fn [v] (false? v)) v104_l464)))


(def
 v107_l471
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t108_l476
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v107_l471)))


(def
 v110_l485
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
 t111_l496
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v110_l485)))


(def v113_l505 (sk/sketch (base-views) {:validate false}))


(deftest
 t114_l507
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v113_l505)))
