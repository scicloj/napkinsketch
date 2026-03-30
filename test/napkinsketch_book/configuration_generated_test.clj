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


(def v15_l89 (-> (base-views)))


(deftest
 t16_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v15_l89)))


(def v18_l99 (-> (base-views) sk/sketch :width))


(deftest t19_l103 (is ((fn [v] (= 600 v)) v18_l99)))


(def v21_l107 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t22_l110
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v21_l107)))


(def
 v24_l118
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t25_l122
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v24_l118)))


(def
 v27_l129
 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}}) sk/plot))


(deftest
 t28_l133
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v27_l129)))


(def v30_l140 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t31_l143
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v30_l140)))


(def v33_l155 (sk/set-config! {:width 800}))


(def v35_l159 (:width (sk/config)))


(deftest t36_l161 (is ((fn [v] (= 800 v)) v35_l159)))


(def v38_l165 (-> (base-views) sk/sketch :width))


(deftest t39_l169 (is ((fn [v] (= 800 v)) v38_l165)))


(def v41_l173 (-> (base-views) sk/plot))


(deftest
 t42_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v41_l173)))


(def v44_l183 (sk/set-config! nil))


(def v45_l185 (:width (sk/config)))


(deftest t46_l187 (is ((fn [v] (= 600 v)) v45_l185)))


(def
 v48_l198
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t49_l201 (is ((fn [v] (= 1000 v)) v48_l198)))


(def v51_l205 (:width (sk/config)))


(deftest t52_l207 (is ((fn [v] (= 600 v)) v51_l205)))


(def
 v54_l211
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t55_l216 (is ((fn [v] (= 1000 v)) v54_l211)))


(def
 v57_l220
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (->
   (base-views)
   (sk/options {:title "Dark Theme via with-config"})
   sk/plot)))


(deftest
 t58_l225
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v57_l220)))


(def
 v60_l245
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v62_l250
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v63_l257 precedence-result)


(deftest
 t64_l259
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v63_l257)))


(def
 v66_l268
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v67_l272 precedence-point-radius)


(deftest t68_l274 (is ((fn [v] (= 5.0 v)) v67_l272)))


(def
 v70_l278
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v71_l283 precedence-plot)


(deftest
 t72_l285
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v71_l283)))


(def v74_l295 (sk/set-config! nil))


(def
 v76_l338
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t77_l342
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v76_l338)))


(def
 v79_l352
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t80_l357
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v79_l352)))


(def
 v82_l367
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


(deftest t83_l377 (is ((fn [v] (= :div (first v))) v82_l367)))


(def v85_l401 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t86_l404
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l401)))


(def
 v88_l409
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t89_l412
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v88_l409)))


(def
 v91_l417
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t92_l422
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v91_l417)))


(def v94_l427 (sk/set-config! {:palette :pastel1}))


(def v95_l429 (-> (base-views)))


(deftest
 t96_l431
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v95_l429)))


(def v97_l434 (sk/set-config! nil))


(def v99_l438 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t100_l441
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v99_l438)))


(def
 v102_l452
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t103_l455
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v102_l452)))


(def
 v105_l459
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t106_l463
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v105_l459)))


(def
 v108_l467
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t109_l471
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v108_l467)))


(def
 v111_l477
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t112_l483
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v111_l477)))


(def v114_l499 (sk/sketch (base-views)))


(deftest
 t115_l501
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v114_l499)))


(def v117_l508 (-> (base-views)))


(deftest
 t118_l510
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v117_l508)))


(def
 v120_l519
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v121_l521 (sk/valid-sketch? good-sketch))


(deftest t122_l523 (is ((fn [v] (true? v)) v121_l521)))


(def
 v124_l528
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v125_l530 (sk/valid-sketch? bad-sketch))


(deftest t126_l532 (is ((fn [v] (false? v)) v125_l530)))


(def
 v128_l537
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t129_l542
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v128_l537)))


(def
 v131_l551
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
 t132_l562
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v131_l551)))


(def v134_l571 (sk/sketch (base-views) {:validate false}))


(deftest
 t135_l573
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v134_l571)))
