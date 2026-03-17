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
 t10_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (< (:width s) 800))))
   v9_l71)))


(def v12_l81 (-> (base-views) (sk/plot {:width 900, :height 250})))


(deftest
 t13_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v12_l81)))


(def v15_l93 (-> (base-views) (sk/plot {:theme {:bg "#FFFFFF"}})))


(deftest
 t16_l96
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v15_l93)))


(def v18_l103 (-> (base-views) (sk/plot {:palette :dark2})))


(deftest
 t19_l106
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v18_l103)))


(def v21_l118 (sk/set-config! {:width 800}))


(def v22_l120 (:width (sk/config)))


(deftest t23_l122 (is ((fn [v] (= 800 v)) v22_l120)))


(def v25_l126 (-> (base-views) (sk/sketch)))


(deftest t26_l128 (is ((fn [sketch] (= 800 (:width sketch))) v25_l126)))


(def v28_l134 (sk/set-config! nil))


(def v29_l136 (:width (sk/config)))


(deftest t30_l138 (is ((fn [v] (= 600 v)) v29_l136)))


(def
 v32_l147
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t33_l150 (is ((fn [v] (= 1000 v)) v32_l147)))


(def v35_l154 (:width (sk/config)))


(deftest t36_l156 (is ((fn [v] (= 600 v)) v35_l154)))


(def
 v38_l160
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/plot {:title "Dark Theme via with-config"}))))


(deftest
 t39_l164
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v38_l160)))


(def v41_l183 (sk/set-config! {:width 800, :height 350}))


(def
 v43_l187
 (def
  precedence-result
  (sk/with-config
   {:width 1200}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v44_l194 precedence-result)


(deftest
 t45_l196
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 350 (:sketch-height m))))
   v44_l194)))


(def v47_l204 (sk/set-config! nil))


(def v49_l246 (-> (base-views) (sk/plot {:theme {:bg "#F5F5DC"}})))


(deftest
 t50_l249
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v49_l246)))


(def
 v52_l259
 (->
  (base-views)
  (sk/plot
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t53_l263
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v52_l259)))


(def
 v55_l273
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


(deftest t56_l283 (is ((fn [v] (= :div (first v))) v55_l273)))


(def v58_l301 (-> (base-views) (sk/plot {:palette :tableau10})))


(deftest
 t59_l304
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v58_l301)))


(def
 v61_l309
 (-> (base-views) (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t62_l312
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v61_l309)))


(def
 v64_l317
 (->
  (base-views)
  (sk/plot
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t65_l322
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v64_l317)))


(def v67_l327 (sk/set-config! {:palette :pastel1}))


(def v68_l329 (-> (base-views) (sk/plot)))


(deftest
 t69_l331
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v68_l329)))


(def v70_l334 (sk/set-config! nil))


(def
 v72_l338
 (sk/with-config {:palette :accent} (-> (base-views) (sk/plot))))


(deftest
 t73_l341
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v72_l338)))


(def v75_l352 (sk/sketch (base-views)))


(deftest
 t76_l354
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v75_l352)))


(def v78_l361 (sk/sketch (base-views) {:validate false}))


(deftest
 t79_l363
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v78_l361)))
