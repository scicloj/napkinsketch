(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (def
  with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]})))


(def
 v4_l25
 (-> with-missing (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t5_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l25)))


(def
 v7_l38
 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) (sk/lay (sk/point)) sk/plot))


(deftest
 t8_l43
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l38)))


(def
 v10_l52
 (->
  {:x [1 10], :y [5 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t11_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l52)))


(def
 v13_l65
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point) (sk/lm))
  sk/plot))


(deftest
 t14_l70
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l65)))


(def
 v16_l78
 (->
  {:x [5 5 5 5 5], :y [1 2 3 4 5]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t17_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l78)))


(def
 v19_l91
 (->
  {:x [1 2 3 4 5], :y [3 3 3 3 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t20_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l91)))


(def
 v22_l104
 (->
  {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t23_l109
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l104)))


(def
 v25_l115
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t26_l120
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l115)))


(def
 v28_l126
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/view [[:x :y]])
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t29_l131
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l126)))


(def
 v31_l139
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   (tc/dataset
    {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
     :y (repeatedly 1000 (fn* [] (rng/drandom r))),
     :group
     (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))}))))


(def
 v32_l145
 (->
  large-data
  (sk/view [[:x :y]])
  (sk/lay (sk/point {:color :group}))
  sk/plot))


(deftest
 t33_l150
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l145)))


(def
 v35_l158
 (->
  (let
   [r (rng/rng :jdk 99)]
   (tc/dataset
    {:category
     (mapv
      (fn* [p1__102278#] (keyword (str "cat-" p1__102278#)))
      (range 12)),
     :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))}))
  (sk/view [[:category :value]])
  (sk/lay (sk/value-bar))
  sk/plot))


(deftest
 t36_l165
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l158)))


(def
 v38_l173
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l176
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/view [[:sepal_length :sepal_ratio]])
  (sk/lay (sk/point {:color :species}))
  (sk/plot {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l176)))


(def
 v42_l190
 (->
  iris
  (tc/select-rows
   (fn* [p1__102279#] (= "setosa" (p1__102279# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  (sk/lay (sk/point) (sk/lm))
  (sk/plot {:title "Setosa Only"})))


(deftest
 t43_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l190)))


(def
 v45_l206
 (->
  (tc/dataset {:category ["a" "b" "c"], :count [10 20 15]})
  (sk/view :category :count)
  (sk/lay (sk/value-bar {:position :stack}))
  sk/plot))


(deftest
 t46_l212
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l206)))


(def
 v48_l219
 (->
  (tc/dataset {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]})
  (sk/view :x)
  (sk/lay (sk/bar {:color :g}))
  sk/plot))


(deftest
 t49_l225
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l219)))


(def
 v51_l232
 (->
  (tc/dataset
   {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]})
  (sk/view :x)
  (sk/lay (sk/stacked-bar-fill {:color :g}))
  sk/plot))


(deftest
 t52_l238
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l232)))


(def
 v54_l244
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay (sk/point {:nudge-x 0.1, :nudge-y -0.05}))
  sk/plot))


(deftest
 t55_l249
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l244)))


(def
 v57_l256
 (->
  (tc/dataset {:x [1 2 3], :y [2 4 5]})
  (sk/view :x :y)
  (sk/lay (sk/point) (sk/lm {:se true}))
  sk/plot))


(deftest
 t58_l261
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v57_l256)))


(def
 v60_l269
 (->
  (let
   [r (rng/rng :jdk 55)]
   (tc/dataset
    {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))}))
  (sk/view :x :y)
  (sk/lay (sk/stacked-area))
  sk/plot))


(deftest
 t61_l276
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v60_l269)))


(def
 v63_l286
 (->
  (tc/dataset {:x [1 2 3], :y [4 5 6], :c [5 5 5]})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :c}))
  sk/plot))


(deftest
 t64_l291
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v63_l286)))


(def
 v66_l295
 (->
  (tc/dataset
   {:x (range 20),
    :y (map (fn* [p1__102280#] (- p1__102280# 10)) (range 20)),
    :val (map (fn* [p1__102281#] (- p1__102281# 10.0)) (range 20))})
  (sk/view :x :y)
  (sk/lay (sk/point {:color :val}))
  (sk/plot {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t67_l302
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v66_l295)))


(def
 v69_l308
 (->
  (tc/dataset {:date ["2025-01-01" "2025-01-02"], :val [10 20]})
  (sk/view :date :val)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t70_l314
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v69_l308)))


(def
 v72_l320
 (->
  (tc/dataset
   {:cat (map (fn* [p1__102282#] (str "cat-" p1__102282#)) (range 12)),
    :val (repeatedly 12 (fn* [] (rand-int 100)))})
  (sk/view :cat :val)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t73_l327
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v72_l320)))


(def
 v75_l331
 (->
  (tc/dataset {:x (range 100), :y (range 0 10 0.1)})
  (sk/view :x :y)
  (sk/lay (sk/point))
  (sk/coord :fixed)
  sk/plot))


(deftest
 t76_l337
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v75_l331)))


(def
 v78_l346
 (->
  iris
  (sk/view
   (sk/pairs [:sepal_length :sepal_width :petal_length :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t79_l351
 (is
  ((fn
    [v]
    (let
     [s
      (sk/svg-summary v)
      texts
      (:texts s)
      strip-labels
      (filter
       (fn* [p1__102283#] (re-find #"sepal|petal" p1__102283#))
       texts)]
     (and (= 6 (:panels s)) (= 6 (count strip-labels)))))
   v78_l346)))
