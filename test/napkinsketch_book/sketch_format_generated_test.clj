(ns
 napkinsketch-book.sketch-format-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as ns]
  [scicloj.napkinsketch.impl.sketch-schema :as ss]
  [clojure.pprint :as pp]
  [clojure.test :refer [deftest is]]))


(def
 v3_l39
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v5_l44
 (def
  scatter-sketch
  (ns/sketch
   [(ns/point
     {:data iris,
      :x :sepal_length,
      :y :sepal_width,
      :color :species})])))


(def
 v7_l51
 (kind/table
  {:column-names ["Key" "Description"],
   :row-maps
   (mapv
    (fn [[k desc]] {"Key" (str k), "Description" desc})
    [[:width "Plot area width in pixels"]
     [:height "Plot area height in pixels"]
     [:margin "Margin inside the plot area"]
     [:total-width "Total width including labels and legend"]
     [:total-height "Total height including title and labels"]
     [:title "Plot title (optional)"]
     [:x-label "X-axis label"]
     [:y-label "Y-axis label"]
     [:config "Merged configuration map"]
     [:legend "Legend info (optional)"]
     [:panels "Vector of panel maps"]
     [:layout "Layout padding/offsets"]])}))


(def v9_l70 (keys scatter-sketch))


(deftest
 t10_l72
 (is
  (fn
   v9_l70
   [ks]
   (every? (set ks) [:width :height :panels :legend :layout]))))


(def
 v12_l76
 (select-keys
  scatter-sketch
  [:width :height :margin :total-width :total-height]))


(deftest
 t13_l78
 (is (fn v12_l76 [m] (and (= 600 (:width m)) (= 400 (:height m))))))


(def v15_l85 (def panel (first (:panels scatter-sketch))))


(def v16_l87 (keys panel))


(deftest
 t17_l89
 (is
  (fn
   v16_l87
   [ks]
   (every?
    (set ks)
    [:x-domain
     :y-domain
     :x-scale
     :y-scale
     :coord
     :x-ticks
     :y-ticks
     :layers]))))


(def v19_l96 (:x-domain panel))


(deftest
 t20_l98
 (is
  (fn v19_l96 [d] (and (number? (first d)) (< (first d) (second d))))))


(def v21_l100 (:y-domain panel))


(deftest
 t22_l102
 (is
  (fn v21_l100 [d] (and (number? (first d)) (< (first d) (second d))))))


(def v24_l106 (:x-scale panel))


(deftest t25_l108 (is (fn v24_l106 [s] (= :linear (:type s)))))


(def v27_l112 (:coord panel))


(deftest t28_l114 (is (fn v27_l112 [c] (= :cartesian c))))


(def v30_l120 (:x-ticks panel))


(deftest
 t31_l122
 (is
  (fn
   v30_l120
   [t]
   (and (seq (:values t)) (seq (:labels t)) (not (:categorical? t))))))


(def v33_l128 (count (:layers panel)))


(deftest t34_l130 (is (fn v33_l128 [n] (= 1 n))))


(def v35_l132 (def point-layer (first (:layers panel))))


(def v37_l136 (select-keys point-layer [:mark :style]))


(deftest
 t38_l138
 (is
  (fn
   v37_l136
   [m]
   (and
    (= :point (:mark m))
    (number? (:opacity (:style m)))
    (number? (:radius (:style m)))))))


(def v40_l144 (count (:groups point-layer)))


(deftest t41_l146 (is (fn v40_l144 [n] (= 3 n))))


(def
 v43_l150
 (let
  [g (first (:groups point-layer))]
  {:color (:color g),
   :n-points (count (:xs g)),
   :x-range [(reduce min (:xs g)) (reduce max (:xs g))],
   :y-range [(reduce min (:ys g)) (reduce max (:ys g))]}))


(deftest
 t44_l156
 (is
  (fn
   v43_l150
   [m]
   (and (= 4 (count (:color m))) (pos? (:n-points m))))))


(def v46_l163 (:legend scatter-sketch))


(deftest
 t47_l165
 (is
  (fn
   v46_l163
   [leg]
   (and (= :species (:title leg)) (= 3 (count (:entries leg)))))))


(def
 v49_l172
 (def
  hist-sketch
  (ns/sketch [(ns/histogram {:data iris, :x :sepal_length})])))


(def
 v50_l175
 (def hist-layer (first (:layers (first (:panels hist-sketch))))))


(def v51_l177 (:mark hist-layer))


(deftest t52_l179 (is (fn v51_l177 [m] (= :bar m))))


(def
 v54_l183
 (let
  [g (first (:groups hist-layer))]
  {:color (:color g),
   :n-bins (count (:bars g)),
   :first-bin (first (:bars g))}))


(deftest
 t55_l188
 (is
  (fn
   v54_l183
   [m]
   (and
    (pos? (:n-bins m))
    (contains? (:first-bin m) :lo)
    (contains? (:first-bin m) :hi)
    (contains? (:first-bin m) :count)))))


(def
 v57_l197
 (def bar-sketch (ns/sketch [(ns/bar {:data iris, :x :species})])))


(def
 v58_l200
 (def bar-layer (first (:layers (first (:panels bar-sketch))))))


(def v59_l202 (select-keys bar-layer [:mark :position]))


(deftest
 t60_l204
 (is
  (fn v59_l202 [m] (and (= :rect (:mark m)) (= :dodge (:position m))))))


(def v61_l206 (:categories bar-layer))


(deftest t62_l208 (is (fn v61_l206 [cats] (= 3 (count cats)))))


(def
 v64_l212
 (let
  [g (first (:groups bar-layer))]
  {:label (:label g), :counts (:counts g)}))


(deftest
 t65_l216
 (is
  (fn
   v64_l212
   [m]
   (and
    (string? (:label m))
    (every?
     (fn* [p1__73277#] (contains? p1__73277# :category))
     (:counts m))))))


(def
 v67_l223
 (def
  lm-sketch
  (ns/sketch
   [(ns/point {:data iris, :x :sepal_length, :y :sepal_width})
    (ns/lm {:data iris, :x :sepal_length, :y :sepal_width})])))


(def
 v68_l227
 (def lm-layer (second (:layers (first (:panels lm-sketch))))))


(def v69_l229 (:mark lm-layer))


(deftest t70_l231 (is (fn v69_l229 [m] (= :line m))))


(def v71_l233 (:stat-origin lm-layer))


(deftest t72_l235 (is (fn v71_l233 [s] (= :lm s))))


(def
 v74_l239
 (let [g (first (:groups lm-layer))] (select-keys g [:x1 :y1 :x2 :y2])))


(deftest t75_l242 (is (fn v74_l239 [m] (every? number? (vals m)))))


(def
 v77_l248
 (def
  line-data
  (tc/dataset
   {:x (range 20),
    :y
    (map
     (fn* [p1__73278#] (Math/sin (* p1__73278# 0.3)))
     (range 20))})))


(def
 v78_l250
 (def
  line-sketch
  (ns/sketch [(ns/line {:data line-data, :x :x, :y :y})])))


(def
 v79_l253
 (def line-layer (first (:layers (first (:panels line-sketch))))))


(def
 v80_l255
 (let
  [g (first (:groups line-layer))]
  {:n-points (count (:xs g)),
   :has-xs? (some? (:xs g)),
   :has-ys? (some? (:ys g))}))


(deftest
 t81_l260
 (is
  (fn
   v80_l255
   [m]
   (and (:has-xs? m) (:has-ys? m) (= 20 (:n-points m))))))


(def
 v83_l266
 (def
  vbar-data
  (tc/dataset {:category [:a :b :c :d], :value [10 25 15 30]})))


(def
 v84_l268
 (def
  vbar-sketch
  (ns/sketch
   [(ns/value-bar {:data vbar-data, :x :category, :y :value})])))


(def
 v85_l271
 (def vbar-layer (first (:layers (first (:panels vbar-sketch))))))


(def v86_l273 (:mark vbar-layer))


(deftest t87_l275 (is (fn v86_l273 [m] (= :rect m))))


(def
 v88_l277
 (let [g (first (:groups vbar-layer))] {:xs (:xs g), :ys (:ys g)}))


(deftest
 t89_l280
 (is
  (fn v88_l277 [m] (and (= 4 (count (:xs m))) (= 4 (count (:ys m)))))))


(def v91_l286 (ss/valid? scatter-sketch))


(deftest t92_l288 (is (true? v91_l286)))


(def v93_l290 (ss/valid? hist-sketch))


(deftest t94_l292 (is (true? v93_l290)))


(def v95_l294 (ss/valid? bar-sketch))


(deftest t96_l296 (is (true? v95_l294)))


(def v97_l298 (ss/valid? lm-sketch))


(deftest t98_l300 (is (true? v97_l298)))


(def v99_l302 (ss/valid? line-sketch))


(deftest t100_l304 (is (true? v99_l302)))


(def v101_l306 (ss/valid? vbar-sketch))


(deftest t102_l308 (is (true? v101_l306)))


(def
 v104_l314
 (let
  [s (pr-str scatter-sketch) back (read-string s)]
  (= scatter-sketch back)))


(deftest t105_l318 (is (true? v104_l314)))


(def
 v107_l326
 (ns/sketch
  [(ns/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Petals"}))


(def
 v109_l330
 (ns/plot
  [(ns/point
    {:data iris, :x :sepal_length, :y :sepal_width, :color :species})]
  {:title "Iris Petals"}))
