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
    (sk/lay-point :sepal_length :sepal_width {:color :species})))))


(def v6_l31 (sk/config))


(deftest
 t7_l33
 (is
  ((fn
    [cfg]
    (and
     (map? cfg)
     (= 600 (:width cfg))
     (= 400 (:height cfg))
     (= 25 (:margin cfg))
     (map? (:theme cfg))))
   v6_l31)))


(def v9_l70 (-> (base-views)))


(deftest
 t10_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v9_l70)))


(def v12_l80 (-> (base-views) sk/sketch :width))


(deftest t13_l84 (is ((fn [v] (= 600 v)) v12_l80)))


(def v15_l88 (-> (base-views) (sk/options {:width 900, :height 250})))


(deftest
 t16_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l88)))


(def
 v18_l99
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t19_l103
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v18_l99)))


(def
 v21_l110
 (-> (base-views) (sk/options {:theme {:bg "#FFFFFF"}}) sk/plot))


(deftest
 t22_l114
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v21_l110)))


(def v24_l121 (-> (base-views) (sk/options {:palette :dark2})))


(deftest
 t25_l124
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v24_l121)))


(def v27_l136 (sk/set-config! {:width 800}))


(def v29_l140 (:width (sk/config)))


(deftest t30_l142 (is ((fn [v] (= 800 v)) v29_l140)))


(def v32_l146 (-> (base-views) sk/sketch :width))


(deftest t33_l150 (is ((fn [v] (= 800 v)) v32_l146)))


(def v35_l154 (-> (base-views) sk/plot))


(deftest
 t36_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v35_l154)))


(def v38_l164 (sk/set-config! nil))


(def v39_l166 (:width (sk/config)))


(deftest t40_l168 (is ((fn [v] (= 600 v)) v39_l166)))


(def
 v42_l179
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t43_l182 (is ((fn [v] (= 1000 v)) v42_l179)))


(def v45_l186 (:width (sk/config)))


(deftest t46_l188 (is ((fn [v] (= 600 v)) v45_l186)))


(def
 v48_l192
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t49_l197 (is ((fn [v] (= 1000 v)) v48_l192)))


(def
 v51_l201
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (->
   (base-views)
   (sk/options {:title "Dark Theme via with-config"})
   sk/plot)))


(deftest
 t52_l206
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v51_l201)))


(def
 v54_l226
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v56_l231
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v57_l238 precedence-result)


(deftest
 t58_l240
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v57_l238)))


(def
 v60_l249
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v61_l253 precedence-point-radius)


(deftest t62_l255 (is ((fn [v] (= 5.0 v)) v61_l253)))


(def
 v64_l259
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/options {:width 900})))))


(def v65_l264 precedence-plot)


(deftest
 t66_l266
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v65_l264)))


(def v68_l276 (sk/set-config! nil))


(def
 v70_l319
 (-> (base-views) (sk/options {:theme {:bg "#F5F5DC"}}) sk/plot))


(deftest
 t71_l323
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v70_l319)))


(def
 v73_l333
 (->
  (base-views)
  (sk/options
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})
  sk/plot))


(deftest
 t74_l338
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v73_l333)))


(def
 v76_l348
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


(deftest t77_l358 (is ((fn [v] (= :div (first v))) v76_l348)))


(def v79_l382 (-> (base-views) (sk/options {:palette :tableau10})))


(deftest
 t80_l385
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l382)))


(def
 v82_l390
 (->
  (base-views)
  (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t83_l393
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v82_l390)))


(def
 v85_l398
 (->
  (base-views)
  (sk/options
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t86_l403
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l398)))


(def v88_l408 (sk/set-config! {:palette :pastel1}))


(def v89_l410 (-> (base-views)))


(deftest
 t90_l412
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v89_l410)))


(def v91_l415 (sk/set-config! nil))


(def v93_l419 (sk/with-config {:palette :accent} (-> (base-views))))


(deftest
 t94_l422
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v93_l419)))


(def
 v96_l433
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t97_l436
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v96_l433)))


(def
 v99_l440
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/options {:color-scale :inferno})))


(deftest
 t100_l444
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v99_l440)))


(def
 v102_l448
 (sk/with-config
  {:color-scale :plasma}
  (->
   {:x (range 50), :y (range 50), :c (range 50)}
   (sk/lay-point :x :y {:color :c}))))


(deftest
 t103_l452
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v102_l448)))


(def
 v105_l458
 (->
  {:x (range 50), :y (range 50), :c (range 50)}
  (sk/lay-point :x :y {:color :c})
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t106_l464
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v105_l458)))


(def v108_l480 (sk/sketch (base-views)))


(deftest
 t109_l482
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v108_l480)))


(def v111_l489 (-> (base-views)))


(deftest
 t112_l491
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v111_l489)))


(def
 v114_l500
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v115_l502 (sk/valid-sketch? good-sketch))


(deftest t116_l504 (is ((fn [v] (true? v)) v115_l502)))


(def
 v118_l509
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v119_l511 (sk/valid-sketch? bad-sketch))


(deftest t120_l513 (is ((fn [v] (false? v)) v119_l511)))


(def
 v122_l518
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t123_l523
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v122_l518)))


(def
 v125_l532
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
 t126_l543
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v125_l532)))


(def v128_l552 (sk/sketch (base-views) {:validate false}))


(deftest
 t129_l554
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v128_l552)))
