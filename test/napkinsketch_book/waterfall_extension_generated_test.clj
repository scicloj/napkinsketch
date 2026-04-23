(ns
 napkinsketch-book.waterfall-extension-generated-test
 (:require
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [scicloj.napkinsketch.impl.stat :as stat]
  [scicloj.napkinsketch.impl.extract :as extract]
  [scicloj.napkinsketch.render.mark :as mark]
  [scicloj.napkinsketch.layer-type :as layer-type]
  [membrane.ui :as ui]
  [tablecloth.api :as tc]
  [clojure.test :refer [deftest is]]))


(def
 v3_l38
 (def
  pnl-data
  {:category
   ["Revenue" "COGS" "Gross Profit" "OpEx" "Tax" "Net Income"],
   :amount [500 -300 200 -120 -30 50]}))


(def
 v5_l52
 (defmethod
  stat/compute-stat
  :waterfall
  [{:keys [data x y x-type], :as view}]
  (let
   [clean
    (tc/drop-missing data [x y])
    categories
    (vec (distinct (clean x)))
    values
    (vec (clean y))
    ends
    (vec (reductions + values))
    starts
    (vec (cons 0 (butlast ends)))
    bars
    (mapv
     (fn
      [cat s e v]
      {:category cat,
       :start (double s),
       :end (double e),
       :value (double v)})
     categories
     starts
     ends
     values)
    y-min
    (min 0.0 (apply min (concat starts ends)))
    y-max
    (apply max (concat starts ends))]
   {:waterfall-bars bars,
    :categories categories,
    :x-domain categories,
    :y-domain [y-min y-max]})))


(def
 v7_l75
 (stat/compute-stat
  {:stat :waterfall,
   :data (tc/dataset pnl-data),
   :x :category,
   :y :amount,
   :x-type :categorical}))


(deftest
 t8_l77
 (is
  ((fn
    [m]
    (and
     (= 6 (count (:waterfall-bars m)))
     (= 500.0 (:end (first (:waterfall-bars m))))
     (=
      ["Revenue" "COGS" "Gross Profit" "OpEx" "Tax" "Net Income"]
      (:categories m))))
   v7_l75)))


(def
 v10_l90
 (defmethod
  extract/extract-layer
  :waterfall
  [view stat all-colors cfg]
  (let
   [bars
    (:waterfall-bars stat)
    green
    [0.2 0.7 0.3 1.0]
    red
    [0.85 0.25 0.25 1.0]]
   {:mark :waterfall,
    :style {:opacity 0.85},
    :categories (:categories stat),
    :bars
    (mapv
     (fn
      [{:keys [category start end value]}]
      {:category category,
       :start start,
       :end end,
       :color (if (>= value 0) green red)})
     bars)})))


(def
 v12_l116
 (defmethod
  mark/layer->membrane
  :waterfall
  [layer ctx]
  (let
   [{:keys [bars style]}
    layer
    {:keys [sx sy coord-fn]}
    ctx
    {:keys [opacity]}
    style
    sample-info
    (sx (-> bars first :category) true)
    bw
    (- (:rend sample-info) (:rstart sample-info))
    w
    (* 0.8 bw)]
   (vec
    (for
     [{:keys [category start end color]}
      bars
      :let
      [[cr cg cb ca]
       color
       band
       (sx category true)
       mid-x
       (:point band)
       py-start
       (double (sy start))
       py-end
       (double (sy end))
       top
       (min py-start py-end)
       bot
       (max py-start py-end)
       x0
       (- mid-x (/ w 2.0))
       x1
       (+ mid-x (/ w 2.0))]]
     (ui/with-color
      [cr cg cb (or opacity ca)]
      (ui/with-style
       :membrane.ui/style-fill
       (ui/path [x0 top] [x1 top] [x1 bot] [x0 bot]))))))))


(def
 v14_l145
 (layer-type/register!
  :waterfall
  {:mark :waterfall,
   :stat :waterfall,
   :doc "Waterfall -- running total with increase/decrease bars."}))


(def
 v16_l157
 (->
  pnl-data
  (sk/frame :category :amount)
  (sk/lay (layer-type/lookup :waterfall))
  (sk/options
   {:title "Profit & Loss Waterfall", :width 500, :height 350})
  sk/plot))


(deftest
 t17_l164
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 6 (:polygons s)))))
   v16_l157)))


(def
 v19_l176
 (defn
  lay-waterfall
  ([sk] (sk/lay sk (layer-type/lookup :waterfall)))
  ([data x y]
   (-> data (sk/frame x y) (sk/lay (layer-type/lookup :waterfall))))
  ([data x y opts]
   (->
    data
    (sk/frame x y)
    (sk/lay (merge (layer-type/lookup :waterfall) opts))))))


(def
 v21_l183
 (->
  pnl-data
  (lay-waterfall :category :amount)
  (sk/options {:title "Quarterly Cash Flow", :width 500})
  sk/plot))


(deftest
 t22_l188
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 6 (:polygons s)))) v21_l183)))


(def
 v24_l196
 (defmethod
  stat/compute-stat
  [:waterfall :doc]
  [_]
  "Compute running totals for waterfall bars"))


(def
 v25_l199
 (defmethod
  extract/extract-layer
  [:waterfall :doc]
  [_ _ _ _]
  "Colored bars (green/red) with start/end positions"))


(def
 v26_l202
 (defmethod
  mark/layer->membrane
  [:waterfall :doc]
  [_ _]
  "Filled rectangles positioned by running total"))


(def v27_l205 (sk/stat-doc :waterfall))


(deftest
 t28_l207
 (is
  ((fn [v] (= "Compute running totals for waterfall bars" v))
   v27_l205)))


(def v30_l213 (remove-method stat/compute-stat :waterfall))


(def v31_l214 (remove-method stat/compute-stat [:waterfall :doc]))


(def v32_l215 (remove-method extract/extract-layer :waterfall))


(def v33_l216 (remove-method extract/extract-layer [:waterfall :doc]))


(def v34_l217 (remove-method mark/layer->membrane :waterfall))


(def v35_l218 (remove-method mark/layer->membrane [:waterfall :doc]))


(def
 v36_l219
 (swap!
  @(resolve 'scicloj.napkinsketch.layer-type/registry*)
  dissoc
  :waterfall))


(def v38_l223 (nil? (layer-type/lookup :waterfall)))


(deftest t39_l225 (is ((fn [v] (true? v)) v38_l223)))
