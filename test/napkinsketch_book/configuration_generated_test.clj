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


(def v12_l81 (-> (base-views) sk/sketch :width))


(deftest t13_l83 (is ((fn [v] (= 600 v)) v12_l81)))


(def v15_l87 (-> (base-views) (sk/plot {:width 900, :height 250})))


(deftest
 t16_l89
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v15_l87)))


(def
 v18_l97
 (->
  (base-views)
  (sk/sketch {:width 900, :height 250})
  (select-keys [:width :height])))


(deftest
 t19_l101
 (is ((fn [m] (and (= 900 (:width m)) (= 250 (:height m)))) v18_l97)))


(def v21_l108 (-> (base-views) (sk/plot {:theme {:bg "#FFFFFF"}})))


(deftest
 t22_l111
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(255,255,255)")))
   v21_l108)))


(def v24_l118 (-> (base-views) (sk/plot {:palette :dark2})))


(deftest
 t25_l121
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 150 (:points s)))) v24_l118)))


(def v27_l133 (sk/set-config! {:width 800}))


(def v29_l137 (:width (sk/config)))


(deftest t30_l139 (is ((fn [v] (= 800 v)) v29_l137)))


(def v32_l143 (-> (base-views) sk/sketch :width))


(deftest t33_l145 (is ((fn [v] (= 800 v)) v32_l143)))


(def v35_l149 (-> (base-views) (sk/plot)))


(deftest
 t36_l151
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 800))))
   v35_l149)))


(def v38_l159 (sk/set-config! nil))


(def v39_l161 (:width (sk/config)))


(deftest t40_l163 (is ((fn [v] (= 600 v)) v39_l161)))


(def
 v42_l174
 (sk/with-config {:width 1000, :height 300} (:width (sk/config))))


(deftest t43_l177 (is ((fn [v] (= 1000 v)) v42_l174)))


(def v45_l181 (:width (sk/config)))


(deftest t46_l183 (is ((fn [v] (= 600 v)) v45_l181)))


(def
 v48_l187
 (sk/with-config
  {:width 1000, :height 300}
  (-> (base-views) sk/sketch :width)))


(deftest t49_l190 (is ((fn [v] (= 1000 v)) v48_l187)))


(def
 v51_l194
 (sk/with-config
  {:theme {:bg "#1a1a2e", :grid "#16213e", :font-size 8}}
  (-> (base-views) (sk/plot {:title "Dark Theme via with-config"}))))


(deftest
 t52_l198
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(26,26,46)")
      (clojure.string/includes? s "Dark Theme via with-config"))))
   v51_l194)))


(def
 v54_l218
 (sk/set-config! {:width 800, :height 350, :point-radius 5.0}))


(def
 v56_l223
 (def
  precedence-result
  (sk/with-config
   {:width 1200, :height 500}
   (let
    [sketch (sk/sketch (base-views) {:width 900})]
    {:sketch-width (:width sketch), :sketch-height (:height sketch)}))))


(def v57_l230 precedence-result)


(deftest
 t58_l232
 (is
  ((fn [m] (and (= 900 (:sketch-width m)) (= 500 (:sketch-height m))))
   v57_l230)))


(def
 v60_l241
 (def
  precedence-point-radius
  (sk/with-config
   {:width 1200, :height 500}
   (:point-radius (sk/config)))))


(def v61_l245 precedence-point-radius)


(deftest t62_l247 (is ((fn [v] (= 5.0 v)) v61_l245)))


(def
 v64_l251
 (def
  precedence-plot
  (sk/with-config
   {:width 1200, :height 500}
   (-> (base-views) (sk/plot {:width 900})))))


(def v65_l255 precedence-plot)


(deftest
 t66_l257
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (> (:width s) 900) (< (:width s) 1100))))
   v65_l255)))


(def v68_l267 (sk/set-config! nil))


(def v70_l310 (-> (base-views) (sk/plot {:theme {:bg "#F5F5DC"}})))


(deftest
 t71_l313
 (is
  ((fn
    [v]
    (let
     [s (str v)]
     (and
      (clojure.string/includes? s "rgb(245,245,220)")
      (clojure.string/includes? s "rgb(255,255,255)"))))
   v70_l310)))


(def
 v73_l323
 (->
  (base-views)
  (sk/plot
   {:title "Full Dark Theme",
    :theme {:bg "#2d2d2d", :grid "#444444", :font-size 10}})))


(deftest
 t74_l327
 (is
  ((fn
    [v]
    (let [s (str v)] (clojure.string/includes? s "rgb(45,45,45)")))
   v73_l323)))


(def
 v76_l337
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


(deftest t77_l347 (is ((fn [v] (= :div (first v))) v76_l337)))


(def v79_l371 (-> (base-views) (sk/plot {:palette :tableau10})))


(deftest
 t80_l374
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v79_l371)))


(def
 v82_l379
 (-> (base-views) (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})))


(deftest
 t83_l382
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v82_l379)))


(def
 v85_l387
 (->
  (base-views)
  (sk/plot
   {:palette
    {"setosa" "#FF6B6B",
     "versicolor" "#4ECDC4",
     "virginica" "#45B7D1"}})))


(deftest
 t86_l392
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v85_l387)))


(def v88_l397 (sk/set-config! {:palette :pastel1}))


(def v89_l399 (-> (base-views) (sk/plot)))


(deftest
 t90_l401
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v89_l399)))


(def v91_l404 (sk/set-config! nil))


(def
 v93_l408
 (sk/with-config {:palette :accent} (-> (base-views) (sk/plot))))


(deftest
 t94_l411
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v93_l408)))


(def v96_l427 (sk/sketch (base-views)))


(deftest
 t97_l429
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch)))) v96_l427)))


(def v99_l436 (-> (base-views) (sk/plot)))


(deftest
 t100_l438
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v99_l436)))


(def
 v102_l447
 (def good-sketch (sk/sketch (base-views) {:validate false})))


(def v103_l449 (sk/valid-sketch? good-sketch))


(deftest t104_l451 (is ((fn [v] (true? v)) v103_l449)))


(def
 v106_l456
 (def bad-sketch (assoc good-sketch :width "not-a-number")))


(def v107_l458 (sk/valid-sketch? bad-sketch))


(deftest t108_l460 (is ((fn [v] (false? v)) v107_l458)))


(def
 v110_l465
 (->
  (sk/explain-sketch bad-sketch)
  :errors
  first
  (select-keys [:path :in :value])))


(deftest
 t111_l470
 (is
  ((fn [m] (and (= [:width] (:path m)) (= "not-a-number" (:value m))))
   v110_l465)))


(def
 v113_l479
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
 t114_l490
 (is
  ((fn
    [m]
    (and
     (:caught m)
     (= "Sketch does not conform to schema" (:message m))))
   v113_l479)))


(def v116_l499 (sk/sketch (base-views) {:validate false}))


(deftest
 t117_l501
 (is
  ((fn [sketch] (and (map? sketch) (= 600 (:width sketch))))
   v116_l499)))
