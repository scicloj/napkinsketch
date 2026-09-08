(ns
 plotje-book.troubleshooting-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [tablecloth.api :as tc]
  [scicloj.plotje.api :as pj]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (tc/column-names (rdatasets/datasets-iris)))


(deftest t4_l31 (is ((fn [v] (some #{:sepal-length} v)) v3_l29)))


(def
 v6_l37
 (try
  (->
   (tc/dataset {"sepal_length" [5.0 6.0], "sepal_width" [3.0 3.5]})
   (pj/pose :sepal_length :sepal_width)
   pj/lay-point
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t7_l43
 (is ((fn [msg] (re-find #"Column :sepal_\w+.*not found" msg)) v6_l37)))


(def
 v9_l54
 (try
  (->
   (tc/dataset {"sepal length" [5.0 6.0], "sepal width" [3.0 3.5]})
   (pj/pose :sepal-length :sepal-width)
   pj/lay-point
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t10_l60
 (is ((fn [msg] (re-find #"Column :sepal-\w+.*not found" msg)) v9_l54)))


(def
 v12_l84
 (-> (rdatasets/datasets-iris) (pj/pose :species :sepal-width)))


(deftest
 t13_l87
 (is ((fn [v] (pos? (:lines (pj/svg-summary v)))) v12_l84)))


(def
 v15_l91
 (-> (rdatasets/datasets-iris) (pj/lay-point :species :sepal-width)))


(deftest
 t16_l94
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v15_l91)))


(def
 v18_l106
 (def
  subject-scores
  {:day [1 2 3 4 1 2 3 4 1 2 3 4],
   :score [3 5 4 6 6 7 5 8 8 9 7 10],
   :subject [1 1 1 1 2 2 2 2 3 3 3 3]}))


(def
 v20_l117
 (-> subject-scores (pj/lay-line :day :score {:color :subject})))


(deftest
 t21_l120
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:lines s)) (contains? (:colors s) "rgb(51,51,51)"))))
   v20_l117)))


(def
 v23_l130
 (->
  subject-scores
  (pj/lay-line
   :day
   :score
   {:color :subject, :color-type :categorical})))


(deftest
 t24_l133
 (is ((fn [v] (= 3 (:lines (pj/svg-summary v)))) v23_l130)))


(def
 v26_l154
 (try
  (->
   {:hour [9 9 10 10 11 11], :value [1 2 3 4 5 6]}
   (pj/lay-boxplot :hour :value)
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t27_l160
 (is
  ((fn [msg] (re-find #"requires a categorical column" msg)) v26_l154)))


(def
 v29_l167
 (->
  {:hour [9 9 10 10 11 11], :value [1 2 3 4 5 6]}
  (pj/lay-boxplot :hour :value {:x-type :categorical})))


(deftest
 t30_l170
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v29_l167)))


(def
 v32_l189
 (->
  {:species ["setosa" "versicolor" "virginica"], :pct [33.3 33.3 33.3]}
  (pj/lay-bar :species :pct)
  (pj/lay-text :species :pct {:text :pct, :nudge-x 0.3})))


(deftest
 t33_l193
 (is
  ((fn
    [fr]
    (=
     [nil 0.3]
     (->> fr pj/plan :panels first :layers (mapv :nudge-x))))
   v32_l189)))


(def
 v35_l204
 (->
  {:species ["setosa" "versicolor" "virginica"], :pct [33.3 33.3 33.3]}
  (pj/lay-bar :species :pct)
  (pj/lay-text
   :species
   :pct
   {:text :pct, :align-x :center, :offset-y -6})))


(deftest
 t36_l208
 (is
  ((fn
    [fr]
    (=
     [nil -6]
     (->> fr pj/plan :panels first :layers (mapv :offset-y))))
   v35_l204)))


(def
 v38_l218
 (->
  {:species ["setosa" "versicolor" "virginica"], :pct [33.3 33.3 33.3]}
  (pj/lay-bar :species :pct {:color "#a6cee3"})
  (pj/lay-text :species :pct {:text :pct, :align-x :right})
  (pj/coord :flip)))


(deftest
 t39_l223
 (is
  ((fn
    [fr]
    (=
     :right
     (->>
      fr
      pj/plan
      :panels
      first
      :layers
      (filter (fn* [p1__72523#] (= :text (:mark p1__72523#))))
      first
      :style
      :align-x)))
   v38_l218)))


(def
 v41_l248
 (with-out-str
  (->
   (rdatasets/ggplot2-diamonds)
   (pj/lay-point :carat :price {:scale-y :log})
   pj/plan)))


(deftest
 t42_l253
 (is
  ((fn [out] (re-find #"does not recognize option.*:scale-y" out))
   v41_l248)))


(def
 v44_l258
 (->
  (rdatasets/ggplot2-diamonds)
  (pj/lay-point :carat :price {:alpha 0.1})
  (pj/scale :y :log)))


(deftest
 t45_l262
 (is ((fn [v] (pos? (:points (pj/svg-summary v)))) v44_l258)))


(def
 v47_l281
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/lay-histogram :sepal-length :sepal-width)
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t48_l287
 (is ((fn [msg] (re-find #"uses only the x column" msg)) v47_l281)))


(def
 v50_l292
 (-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-length)))


(deftest
 t51_l295
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v50_l292)))


(def
 v53_l304
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/lay-bar :species)
   (pj/scale :x :log)
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t54_l311
 (is ((fn [msg] (re-find #"[Ll]og scale" msg)) v53_l304)))


(def
 v56_l328
 (try
  (->
   {:x [1 2 3 4 5], :y [2 4 3 5 4]}
   (pj/lay-line :x :y)
   (pj/coord :polar)
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t57_l335
 (is
  ((fn [msg] (re-find #"not supported with polar coordinates" msg))
   v56_l328)))


(def
 v59_l341
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t60_l346
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v59_l341)))


(def
 v62_l366
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width {:color :species})
  (pj/options {:tooltip true})))


(deftest
 t63_l370
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v62_l366)))


(def
 v65_l383
 (try
  (->
   (rdatasets/datasets-iris)
   (pj/pose :sepal-length :sepal-width)
   (pj/lay-point {:facet-col :species})
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t66_l390
 (is ((fn [msg] (re-find #"Faceting is plot-level" msg)) v65_l383)))


(def
 v68_l396
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/facet :species)))


(deftest
 t69_l400
 (is ((fn [v] (= 3 (:panels (pj/svg-summary v)))) v68_l396)))


(def
 v71_l413
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-text {:x 6.5, :y 3.5, :text "mean"})))


(deftest
 t72_l417
 (is ((fn [v] (some #{"mean"} (:texts (pj/svg-summary v)))) v71_l413)))


(def
 v74_l425
 (->
  {:team ["North" "South" "East" "West" "Central"],
   :spend [12 19 15 24 31],
   :revenue [30 45 38 62 74]}
  (pj/lay-point :spend :revenue)
  (pj/lay-text {:x 33, :y :revenue, :text :team})))


(deftest
 t75_l431
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 5 (:points s))
      (every?
       (set (:texts s))
       ["North" "South" "East" "West" "Central"]))))
   v74_l425)))


(def v77_l460 (-> (tc/dataset [[1 2] [3 4] [5 7]]) (pj/lay-point 0 1)))


(deftest
 t78_l463
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v77_l460)))


(def
 v80_l472
 (->
  (tc/dataset [[1 2] [3 4] [5 7]])
  (tc/rename-columns [:x :y])
  (pj/lay-point :x :y)))


(deftest
 t81_l476
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v80_l472)))


(def
 v83_l492
 (->
  {:cohort [:a :b :c], :growth [12 19 15], :tax [3 5 4]}
  (pj/lay-bar :growth :cohort {:color "#377eb8"})
  (pj/lay-bar :tax :cohort {:color "#e6550d"})))


(deftest
 t84_l496
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 2 (:panels s))
      (=
       #{"rgb(55,126,184)" "rgb(230,85,13)"}
       (disj (:colors s) "none")))))
   v83_l492)))


(def
 v86_l508
 (->
  {:cohort [:a :b :c], :growth [12 19 15], :tax [3 5 4]}
  pj/overlay
  (pj/lay-bar :growth :cohort {:color "#377eb8"})
  (pj/lay-bar :tax :cohort {:bar-width 0.4, :color "#e6550d"})))


(deftest
 t87_l513
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 6 (:polygons s))
      (=
       #{"rgb(55,126,184)" "rgb(230,85,13)"}
       (disj (:colors s) "none")))))
   v86_l508)))


(def
 v89_l543
 (def
  template
  (-> (pj/pose nil {:x :x, :y :y, :color :group}) pj/lay-point)))


(def
 v90_l547
 (try
  (-> template (pj/with-data {:x [1 2 3], :y [4 5 6]}))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t91_l552
 (is
  ((fn [msg] (re-find #"\[:group\] not present in the dataset" msg))
   v90_l547)))


(def
 v93_l559
 (->
  (pj/pose nil {:x :x, :y :y})
  pj/lay-point
  (pj/with-data {:x [1 2 3], :y [4 5 6]})))


(deftest
 t94_l563
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v93_l559)))


(def
 v96_l579
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (pj/lay-bar :category :value)
  (pj/coord :flip)))


(deftest
 t97_l585
 (is ((fn [v] (= 3 (:polygons (pj/svg-summary v)))) v96_l579)))


(def
 v99_l591
 (->
  [{:category "A", :value 100}
   {:category "B", :value 50}
   {:category "C", :value 25}]
  (tc/dataset)
  (tc/order-by [:value] :asc)
  (pj/lay-bar :category :value)
  (pj/coord :flip)))


(deftest
 t100_l599
 (is ((fn [v] (= 3 (:polygons (pj/svg-summary v)))) v99_l591)))


(def
 v102_l617
 (->
  {:x [1 2 3 4 5 6], :y [1 1 1 1 1 1], :n [1 4 9 16 25 36]}
  (pj/lay-point :x :y {:size :n})))


(deftest
 t103_l620
 (is
  ((fn
    [fr]
    (let
     [radii
      (fn* [p1__72524#] (sort (:sizes (pj/svg-summary p1__72524#))))
      now
      (radii fr)
      before
      (radii (-> fr (pj/scale :size {:by :linear})))]
     (and
      (= (first now) (first before))
      (= (last now) (last before))
      (every?
       (fn [[a b]] (> a b))
       (map vector (butlast (rest now)) (butlast (rest before)))))))
   v102_l617)))


(def
 v105_l636
 (->
  {:x [1 2 3 4 5 6], :y [1 1 1 1 1 1], :n [1 4 9 16 25 36]}
  (pj/lay-point :x :y {:size :n})
  (pj/scale :size {:by :linear})))


(deftest
 t106_l640
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v105_l636)))


(def
 v108_l659
 (->
  {:x [1 2 3], :y [2 4 3], :r [1 2 3]}
  (pj/pose :x :y)
  (pj/lay-line {:size 2})
  (pj/lay-point {:size :r})))


(deftest
 t109_l664
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (pos? (:lines s)))))
   v108_l659)))


(def
 v111_l681
 (def
  points-data
  {:x [1 1 2 2 3 3],
   :y [10 15 20 25 30 35],
   :group ["A" "B" "A" "B" "A" "B"]}))


(def
 v112_l684
 (defn
  point-xs
  [pose]
  (->
   pose
   pj/plan
   :panels
   first
   :layers
   first
   :groups
   (->> (mapcat :xs) sort vec))))


(def
 v113_l688
 (=
  (point-xs (-> points-data (pj/lay-point :x :y {:color :group})))
  (point-xs
   (->
    points-data
    (pj/lay-point :x :y {:color :group, :position :dodge})))))


(deftest t114_l691 (is ((fn [v] (true? v)) v113_l688)))


(def
 v116_l700
 (->
  {:cat ["A" "A" "B" "B" "C" "C"],
   :y [10 20 30 40 50 60],
   :group ["a" "b" "a" "b" "a" "b"]}
  (pj/lay-bar :cat :y {:color :group, :position :dodge})))


(deftest
 t117_l705
 (is ((fn [v] (= 6 (:polygons (pj/svg-summary v)))) v116_l700)))


(def
 v119_l721
 (->
  (rdatasets/datasets-chickwts)
  (pj/pose :feed)
  pj/lay-bar
  (pj/coord :polar)))


(deftest
 t120_l726
 (is
  ((fn
    [v]
    (zero?
     (count
      (filter
       #{"soybean"
         "meatmeal"
         "sunflower"
         "horsebean"
         "casein"
         "linseed"}
       (:texts (pj/svg-summary v))))))
   v119_l721)))


(def
 v122_l735
 (-> (rdatasets/datasets-chickwts) (pj/pose :feed) pj/lay-bar))


(deftest
 t123_l739
 (is
  ((fn
    [v]
    (pos?
     (count
      (filter
       #{"soybean"
         "meatmeal"
         "sunflower"
         "horsebean"
         "casein"
         "linseed"}
       (:texts (pj/svg-summary v))))))
   v122_l735)))


(def
 v125_l754
 (try
  (->
   {:x ["a" "b" "c"], :y ["a" "b" "c"], :v [1 2 3]}
   (pj/lay-tile :x :y {:fill :v})
   pj/plan)
  (catch Throwable t (.getMessage t))))


(deftest
 t126_l760
 (is
  ((fn [msg] (re-find #"String cannot be cast to.*Number" msg))
   v125_l754)))


(def
 v128_l767
 (->
  (for
   [day (range 1 8) hour (range 0 24)]
   {:day day,
    :hour hour,
    :v (+ (* 0.3 (Math/sin (* 0.5 hour))) (* 0.2 (mod day 3)))})
  (pj/lay-tile :day :hour {:fill :v})
  (pj/scale
   :x
   {:type :linear,
    :breaks [1 2 3 4 5 6 7],
    :tick-labels ["Mon" "Tue" "Wed" "Thu" "Fri" "Sat" "Sun"]})))


(deftest
 t129_l775
 (is
  ((fn
    [v]
    (let
     [texts (set (:texts (pj/svg-summary v)))]
     (every? texts ["Mon" "Sun"])))
   v128_l767)))


(def
 v131_l794
 (try
  (->
   {:group [], :measurement []}
   (pj/lay-boxplot :group :measurement)
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t132_l800
 (is
  ((fn
    [msg]
    (re-find #"requires a categorical column.*has no rows" msg))
   v131_l794)))


(def
 v134_l807
 (try
  (->
   {:group [nil nil], :measurement [nil nil]}
   (pj/lay-boxplot :group :measurement)
   pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t135_l813
 (is ((fn [msg] (re-find #"has no values" msg)) v134_l807)))


(def
 v137_l832
 (try
  (-> {:x [1 2], :y [1 2]} (pj/lay-text :x :y {:text :nope}) pj/plot)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t138_l838
 (is ((fn [msg] (re-find #"not a label either" msg)) v137_l832)))


(def
 v140_l855
 (try
  (->
   {:height [1 2 3], :weight [1 2 3]}
   (pj/lay-point :height :weight)
   (pj/scale :y {:domain [0]})
   pj/plan)
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t141_l862
 (is
  ((fn [msg] (re-find #"not a pair of two finite numbers" msg))
   v140_l855)))
