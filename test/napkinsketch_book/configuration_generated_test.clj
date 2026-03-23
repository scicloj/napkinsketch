(ns
 napkinsketch-book.configuration-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l19
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v4_l22
 (def
  base-views
  (fn
   []
   (->
    iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})))))


(def v6_l32 (sk/config))


(deftest
 t7_l34
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 25 (:margin cfg))
     (map? (:theme cfg))))
   v6_l32)))


(def v9_l71 (-> (base-views)))


(deftest
 t10_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v9_l71)))


(def v12_l81 (-> (base-views) sk/sketch :width))


(deftest t13_l85 (is ((fn [v] (= 600 v)) v12_l81)))


(def v15_l89 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t16_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l89)))


(def
 v18_l100
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t19_l104
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v18_l100)))


(def
 v21_l111
 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}}) sk/plot))


(deftest
 t22_l115
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v21_l111)))


(def v24_l122 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t25_l125
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v24_l122)))


(def v27_l137 (sk/set-config! {:width 800}))


(def v29_l141 (:width (sk/config)))


(deftest t30_l143 (is ((fn [v] (= 800 v)) v29_l141)))


(def v32_l147 (-> (base-views) sk/sketch :width))


(deftest t33_l151 (is ((fn [v] (= 800 v)) v32_l147)))


(def v35_l155 (-> (base-views) sk/plot))


(deftest
 t36_l157
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v35_l155)))


(def v38_l165 (sk/set-config! nil))


(def v39_l167 (:width (sk/config)))


(deftest t40_l169 (is ((fn [v] (= 600 v)) v39_l167)))


(def
 v42_l180
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t43_l183 (is ((fn [v] (= 1000 v)) v42_l180)))


(def v45_l187 (:width (sk/config)))


(deftest t46_l189 (is ((fn [v] (= 600 v)) v45_l187)))


(def
 v48_l193
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t49_l198 (is ((fn [v] (= 1000 v)) v48_l193)))


(def
 v51_l202
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (->
   (base-views)
   (sk/options {:title "Dark Theme via with-config"})
   sk/plot)))


(deftest
 t52_l207
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v51_l202)))


(def
 v54_l227
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v56_l232
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v57_l239 precedence-result)


(deftest
 t58_l241
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v57_l239)))


(def
 v60_l250
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v61_l254 precedence-point-radius)


(deftest t62_l256 (is ((fn [v] (= 5.0 v)) v61_l254)))


(def
 v64_l260
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v65_l265 precedence-plot)


(deftest
 t66_l267
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v65_l265)))


(def v68_l277 (sk/set-config! nil))


(def
 v70_l320
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t71_l324
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v70_l320)))


(def
 v73_l334
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t74_l339
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v73_l334)))


(def
 v76_l349
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


(deftest t77_l359 (is ((fn [v] (= :div (first v))) v76_l349)))


(def v79_l383 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t80_l386
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l383)))


(def
 v82_l391
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t83_l394
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v82_l391)))


(def
 v85_l399
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t86_l404
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l399)))


(def v88_l409 (sk/set-config! {:palette :pastel1}))


(def v89_l411 (-> (base-views)))


(deftest
 t90_l413
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v89_l411)))


(def v91_l416 (sk/set-config! nil))


(def v93_l420 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t94_l423
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v93_l420)))


(def
 v96_l434
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/view :x :y)
  (sk/lay-point {:color :c})))


(deftest
 t97_l438
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v96_l434)))


(def
 v99_l442
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/view :x :y)
  (sk/lay-point {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t100_l447
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v99_l442)))


(def
 v102_l451
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/view :x :y)
   (sk/lay-point {:color :c}))))


(deftest
 t103_l456
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v102_l451)))


(def
 v105_l462
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/view :x :y)
  (sk/lay-point {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t106_l469
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v105_l462)))


(def v108_l485 (sk/sketch (base-views)))


(deftest
 t109_l487
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v108_l485)))


(def v111_l494 (-> (base-views)))


(deftest
 t112_l496
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v111_l494)))


(def
 v114_l505
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v115_l507 (sk/valid-sketch? good-sketch))


(deftest t116_l509 (is ((fn [v] (true? v)) v115_l507)))


(def
 v118_l514
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v119_l516 (sk/valid-sketch? bad-sketch))


(deftest t120_l518 (is ((fn [v] (false? v)) v119_l516)))


(def
 v122_l523
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t123_l528
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v122_l523)))


(def
 v125_l537
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
 t126_l548
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v125_l537)))


(def v128_l557 (sk/sketch (base-views) {:validate false}))


(deftest
 t129_l559
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v128_l557)))
