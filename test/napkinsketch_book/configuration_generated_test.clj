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
    (sk/lay (sk/point {:color :species}))))))


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


(def v9_l71 (-> (base-views) (sk/plot)))


(deftest
 t10_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v9_l71)))


(def v12_l82 (-> (base-views) sk/sketch :width))


(deftest t13_l86 (is ((fn [v] (= 600 v)) v12_l82)))


(def v15_l90 (-> (base-views) (sk/plot {:width 900, :height 250})))


(deftest
 t16_l93
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l90)))


(def
 v18_l101
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t19_l105
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v18_l101)))


(def v21_l112 (-> (base-views) (sk/plot {:theme {:bg "#FFFFFF"}})))


(deftest
 t22_l115
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v21_l112)))


(def v24_l122 (-> (base-views) (sk/plot {:palette :dark2})))


(deftest
 t25_l125
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v24_l122)))


(def v27_l137 (sk/set-config! {:width 800}))


(def v29_l141 (:width (sk/config)))


(deftest t30_l143 (is ((fn [v] (= 800 v)) v29_l141)))


(def v32_l147 (-> (base-views) sk/sketch :width))


(deftest t33_l151 (is ((fn [v] (= 800 v)) v32_l147)))


(def v35_l155 (-> (base-views) (sk/plot)))


(deftest
 t36_l158
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v35_l155)))


(def v38_l166 (sk/set-config! nil))


(def v39_l168 (:width (sk/config)))


(deftest t40_l170 (is ((fn [v] (= 600 v)) v39_l168)))


(def
 v42_l181
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t43_l184 (is ((fn [v] (= 1000 v)) v42_l181)))


(def v45_l188 (:width (sk/config)))


(deftest t46_l190 (is ((fn [v] (= 600 v)) v45_l188)))


(def
 v48_l194
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t49_l199 (is ((fn [v] (= 1000 v)) v48_l194)))


(def
 v51_l203
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/plot {:title "Dark Theme via with-config"}))))


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
   v51_l203)))


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
   (-> (base-views) (sk/plot {:width 900})))))


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


(def v70_l320 (-> (base-views) (sk/plot {:theme {:bg "#F5F5DC"}})))


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
   v70_l320)))


(def
 v73_l333
 (->
  (base-views)
  (sk/plot
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t74_l337
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v73_l333)))


(def
 v76_l347
 (sk/arrange
  [(->
    (base-views)
    (sk/plot
     {:title "Light",
      :theme {:bg "#FFFFFF", :grid "#EEEEEE", :font-size 8},
      :width 350,
      :height 250}))
   (->
    (base-views)
    (sk/plot
     {:title "Dark",
      :theme {:bg "#2d2d2d", :grid "#444444", :font-size 8},
      :width 350,
      :height 250}))]))


(deftest t77_l357 (is ((fn [v] (= :div (first v))) v76_l347)))


(def v79_l381 (-> (base-views) (sk/plot {:palette :tableau10})))


(deftest
 t80_l384
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l381)))


(def
 v82_l389
 (-> (base-views) (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t83_l392
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v82_l389)))


(def
 v85_l397
 (->
  (base-views)
  (sk/plot
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t86_l402
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l397)))


(def v88_l407 (sk/set-config! {:palette :pastel1}))


(def v89_l409 (-> (base-views) (sk/plot)))


(deftest
 t90_l412
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v89_l409)))


(def v91_l415 (sk/set-config! nil))


(def
 v93_l419
 (sk/with-config {:palette :accent} (-> (base-views) (sk/plot))))


(deftest
 t94_l423
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v93_l419)))


(def
 v96_l435
 (->
  (tc/dataset {:x (range 50), :y (range 50), :c (range 50)})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :c}))
  (sk/plot)))


(deftest
 t97_l440
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v96_l435)))


(def
 v99_l444
 (->
  (tc/dataset {:x (range 50), :y (range 50), :c (range 50)})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :c}))
  (sk/plot {:color-scale :inferno})))


(deftest
 t100_l449
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v99_l444)))


(def
 v102_l453
 (sk/with-config
  {:color-scale :plasma}
  (->
   (tc/dataset {:x (range 50), :y (range 50), :c (range 50)})
   (sk/view :x :y)
   (sk/lay (sk/point {:color :c}))
   (sk/plot))))


(deftest
 t103_l459
 (is ((fn [v] (= 50 (:points (sk/svg-summary v)))) v102_l453)))


(def
 v105_l465
 (->
  (tc/dataset {:x (range 50), :y (range 50), :c (range 50)})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :c}))
  (sk/sketch {:color-scale :inferno})
  :legend
  (select-keys [:color-scale :type])))


(deftest
 t106_l472
 (is
  ((fn
    [m]
    (and (= :inferno (:color-scale m)) (= :continuous (:type m))))
   v105_l465)))


(def v108_l488 (sk/sketch (base-views)))


(deftest
 t109_l490
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v108_l488)))


(def v111_l497 (-> (base-views) (sk/plot)))


(deftest
 t112_l500
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v111_l497)))


(def
 v114_l509
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v115_l511 (sk/valid-sketch? good-sketch))


(deftest t116_l513 (is ((fn [v] (true? v)) v115_l511)))


(def
 v118_l518
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v119_l520 (sk/valid-sketch? bad-sketch))


(deftest t120_l522 (is ((fn [v] (false? v)) v119_l520)))


(def
 v122_l527
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t123_l532
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v122_l527)))


(def
 v125_l541
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
 t126_l552
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v125_l541)))


(def v128_l561 (sk/sketch (base-views) {:validate false}))


(deftest
 t129_l563
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v128_l561)))
