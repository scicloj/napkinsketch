(ns
 napkinsketch-book.cookbook-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l12
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l15
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v5_l25
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/boxplot) (sk/point {:jitter true, :alpha 0.3}))
  sk/plot))


(deftest
 t6_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:points s)) (= 3 (:polygons s)))))
   v5_l25)))


(def
 v8_l40
 (->
  iris
  (sk/view [[:sepal_length :sepal_length]])
  (sk/lay (sk/histogram {:alpha 0.5}) (sk/density))
  sk/plot))


(deftest
 t9_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v8_l40)))


(def
 v11_l54
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species, :alpha 0.6})
   (sk/lm {:color :species}))
  sk/plot))


(deftest
 t12_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v11_l54)))


(def
 v14_l68
 (->
  iris
  (sk/view [[:species :petal_width]])
  (sk/lay
   (sk/violin {:alpha 0.3})
   (sk/point {:jitter true, :alpha 0.4}))
  sk/plot))


(deftest
 t15_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:polygons s)))))
   v14_l68)))


(def
 v17_l83
 (def
  ts-dates
  (mapv
   (fn*
    [p1__104082#]
    (java.time.LocalDate/ofEpochDay
     (+ 18262 (* (long p1__104082#) 7))))
   (range 52))))


(def
 v18_l85
 (def
  ts-ds
  (tc/dataset
   {:date ts-dates,
    :value
    (mapv
     (fn*
      [p1__104083#]
      (+ 100.0 (* 30.0 (Math/sin (* (double p1__104083#) 0.12)))))
     (range 52))}
   {:key-fn keyword})))


(def
 v19_l90
 (->
  ts-ds
  (sk/view [[:date :value]])
  (sk/lay (sk/area {:alpha 0.2}) (sk/line) (sk/point {:alpha 0.5}))
  sk/plot))


(deftest
 t20_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 52 (:points s)) (= 1 (:lines s)) (= 1 (:polygons s)))))
   v19_l90)))


(def
 v22_l106
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}))
  (sk/facet :species)
  sk/plot))


(deftest
 t23_l112
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v22_l106)))


(def
 v25_l119
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay
   (sk/point {:color :species})
   (sk/rule-h 3.0)
   (sk/band-v 5.5 6.5))
  sk/plot))


(deftest
 t26_l126
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v25_l119)))


(def
 v28_l135
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay (sk/ridgeline {:color :species}))
  sk/plot))


(deftest
 t29_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:polygons s)) (= 3 (:lines s)))))
   v28_l135)))


(def
 v31_l148
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v32_l151
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t33_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v32_l151)))


(def
 v35_l166
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm))
  sk/plot))


(deftest
 t36_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 1 (:lines s)))))
   v35_l166)))


(def
 v38_l181
 (def
  experiment
  (tc/dataset
   {:condition ["A" "B" "C" "D"],
    :mean [10.0 15.0 12.0 18.0],
    :ci_lo [8.0 12.0 9.5 15.5],
    :ci_hi [12.0 18.0 14.5 20.5]})))


(def
 v39_l187
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay
   (sk/point {:size 5})
   (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t40_l193
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 12 (:lines s)))))
   v39_l187)))


(def
 v42_l201
 (->
  experiment
  (sk/view [[:condition :mean]])
  (sk/lay (sk/lollipop) (sk/errorbar {:ymin :ci_lo, :ymax :ci_hi}))
  sk/plot))


(deftest
 t43_l207
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 16 (:lines s)))))
   v42_l201)))


(def
 v45_l215
 (->
  iris
  (sk/view [[:species :sepal_length]])
  (sk/lay
   (sk/point {:alpha 0.3, :jitter 5})
   (sk/summary {:color :species}))
  sk/plot))


(deftest
 t46_l221
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 153 (:points s)) (= 3 (:lines s)))))
   v45_l215)))


(def
 v48_l229
 (->
  iris
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Sepal: Length vs Width"})))


(deftest
 t49_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 150 (:points s))
      (= 3 (:lines s))
      (some #{"Sepal: Length vs Width"} (:texts s)))))
   v48_l229)))


(def
 v50_l240
 (->
  iris
  (sk/view [[:petal_length :petal_width]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Petal: Length vs Width"})))


(deftest
 t51_l246
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 150 (:points s)) (= 3 (:lines s)))))
   v50_l240)))


(def
 v53_l254
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  (sk/plot
   {:title "Tipping Behavior",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t54_l262
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (pos? (:points s))
      (= 2 (:lines s))
      (some #{"Tipping Behavior"} (:texts s)))))
   v53_l254)))


(def
 v56_l273
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Palmer Penguins: Bill Dimensions"})))


(deftest
 t57_l278
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 342 (:points s)))))
   v56_l273)))


(def
 v59_l284
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Bill Length vs Depth with Regression"})))


(deftest
 t60_l290
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v59_l284)))


(def
 v62_l296
 (->
  penguins
  (sk/view [[:bill_length_mm :bill_depth_mm]])
  (sk/lay (sk/point {:color :species}) (sk/lm))
  (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"})))


(deftest
 t63_l302
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 1 (:lines s)))))
   v62_l296)))


(def
 v65_l308
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/bar {:color :species}))
  (sk/plot {:title "Species by Island"})))


(deftest
 t66_l313
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v65_l308)))


(def
 v68_l319
 (->
  penguins
  (sk/view [[:flipper_length_mm :body_mass_g]])
  (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
  (sk/plot {:title "Flipper Length vs Body Mass"})))


(deftest
 t69_l325
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 342 (:points s)) (= 3 (:lines s)))))
   v68_l319)))


(def
 v71_l331
 (->
  penguins
  (sk/view :body_mass_g)
  (sk/lay (sk/histogram {:color :species}))
  (sk/plot {:title "Body Mass Distribution"})))


(deftest
 t72_l336
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v71_l331)))


(def
 v74_l344
 (->
  tips
  (sk/view [[:total_bill :tip]])
  (sk/lay (sk/point {:color :smoker}) (sk/lm {:color :smoker}))
  (sk/plot
   {:title "Tipping: Smokers vs Non-Smokers",
    :x-label "Total Bill ($)",
    :y-label "Tip ($)"})))


(deftest
 t75_l351
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 244 (:points s)) (= 2 (:lines s)))))
   v74_l344)))


(def
 v77_l357
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :time}))
  (sk/plot {:title "Visits by Day and Meal Time"})))


(deftest
 t78_l362
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v77_l357)))


(def
 v80_l368
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :time}))
  (sk/plot {:title "Visits by Day (Stacked)"})))


(deftest
 t81_l373
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v80_l368)))


(def
 v83_l379
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :sex}))
  (sk/coord :flip)
  (sk/plot {:title "Day by Gender (Horizontal)"})))


(deftest
 t84_l385
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v83_l379)))


(def
 v86_l391
 (def
  mpg
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
   {:key-fn keyword})))


(def
 v88_l396
 (->
  mpg
  (sk/view [[:horsepower :mpg]])
  (sk/lay (sk/point {:color :origin}) (sk/lm {:color :origin}))
  (sk/plot {:title "Horsepower vs MPG by Origin"})))


(deftest
 t89_l402
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 392 (:points s)) (= 3 (:lines s)))))
   v88_l396)))


(def
 v91_l408
 (->
  mpg
  (sk/view [[:displacement :mpg]])
  (sk/lay (sk/point {:color :origin}))
  (sk/plot {:title "Engine Displacement vs Fuel Efficiency"})))


(deftest
 t92_l413
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 398 (:points s)))))
   v91_l408)))


(def
 v94_l419
 (->
  mpg
  (sk/view :origin)
  (sk/lay (sk/bar))
  (sk/plot {:title "Cars by Origin"})))


(deftest
 t95_l424
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v94_l419)))
