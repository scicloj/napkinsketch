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
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


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
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


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
   {:category
    (mapv
     (fn* [p1__168778#] (keyword (str "cat-" p1__168778#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
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
   (fn* [p1__168779#] (= "setosa" (p1__168779# :species))))
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
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/view :category :count)
  (sk/lay (sk/value-bar {:position :stack}))
  sk/plot))


(deftest
 t46_l212
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l206)))


(def
 v48_l219
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/view :x)
  (sk/lay (sk/bar {:color :g}))
  sk/plot))


(deftest
 t49_l225
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l219)))


(def
 v51_l232
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
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
  {:x [1 2 3], :y [2 4 5]}
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
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/view :x :y)
  (sk/lay (sk/stacked-area))
  sk/plot))


(deftest
 t61_l276
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v60_l269)))


(def
 v63_l282
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/view :x :y)
  (sk/lay (sk/point))
  (sk/scale :x :log)
  (sk/scale :y :log)
  sk/plot))


(deftest
 t64_l290
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v63_l282)))


(def
 v66_l296
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/view :x :y)
  (sk/lay (sk/point))
  (sk/scale :x :log)
  sk/plot))


(deftest
 t67_l303
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v66_l296)))


(def
 v69_l312
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/view :x :y)
  (sk/lay (sk/point {:color :c}))
  sk/plot))


(deftest
 t70_l317
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v69_l312)))


(def
 v72_l321
 (->
  {:x (range 20),
   :y (map (fn* [p1__168780#] (- p1__168780# 10)) (range 20)),
   :val (map (fn* [p1__168781#] (- p1__168781# 10.0)) (range 20))}
  (sk/view :x :y)
  (sk/lay (sk/point {:color :val}))
  (sk/plot {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t73_l328
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v72_l321)))


(def
 v75_l334
 (->
  {:date
   [(java.time.LocalDate/of 2025 1 1)
    (java.time.LocalDate/of 2025 1 2)],
   :val [10 20]}
  (sk/view :date :val)
  (sk/lay (sk/point))
  sk/plot))


(deftest
 t76_l341
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v75_l334)))


(def
 v78_l348
 (->
  {:time
   (mapv
    (fn*
     [p1__168782#]
     (java.time.LocalDateTime/of
      2025
      3
      15
      (+ 8 (int (/ p1__168782# 4)))
      (* 15 (mod (int p1__168782#) 4))
      0))
    (range 24)),
   :value
   (mapv
    (fn* [p1__168783#] (+ 18.0 (* 4.0 (Math/sin (* p1__168783# 0.3)))))
    (range 24))}
  (sk/view :time :value)
  (sk/lay (sk/line) (sk/point))
  sk/plot))


(deftest
 t79_l358
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v78_l348)))


(def
 v81_l368
 (->
  {:time
   (mapv
    (fn*
     [p1__168784#]
     (java.time.Instant/ofEpochSecond
      (+ 1750003200 (* p1__168784# 3600))))
    (range 12)),
   :temp
   (mapv
    (fn* [p1__168785#] (+ 20.0 (* 5.0 (Math/sin (* p1__168785# 0.5)))))
    (range 12))}
  (sk/view :time :temp)
  (sk/lay (sk/line) (sk/point))
  sk/plot))


(deftest
 t82_l376
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__168786#] (re-find #":\d\d" p1__168786#))
       (:texts s)))))
   v81_l368)))


(def
 v84_l385
 (->
  {:date
   (mapv
    (fn*
     [p1__168787#]
     (java.time.LocalDate/ofEpochDay
      (+ 18262 (* (long p1__168787#) 120))))
    (range 20)),
   :value
   (mapv
    (fn* [p1__168788#] (+ 100 (* 50 (Math/sin (* p1__168788# 0.4)))))
    (range 20))}
  (sk/view :date :value)
  (sk/lay (sk/line) (sk/point))
  sk/plot))


(deftest
 t85_l392
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v84_l385)))


(def
 v87_l400
 (->
  {:cat (map (fn* [p1__168789#] (str "cat-" p1__168789#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/view :cat :val)
  (sk/lay (sk/bar))
  (sk/coord :polar)
  sk/plot))


(deftest
 t88_l407
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v87_l400)))


(def
 v90_l411
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/view :x :y)
  (sk/lay (sk/point))
  (sk/coord :fixed)
  sk/plot))


(deftest
 t91_l417
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v90_l411)))


(def
 v93_l426
 (->
  iris
  (sk/view
   (sk/pairs [:sepal_length :sepal_width :petal_length :petal_width]))
  (sk/lay (sk/point {:color :species}))
  sk/plot))


(deftest
 t94_l431
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
       (fn* [p1__168790#] (re-find #"sepal|petal" p1__168790#))
       texts)]
     (and (= 6 (:panels s)) (= 6 (count strip-labels)))))
   v93_l426)))


(def
 v96_l442
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :nonexistent :y)
   (sk/lay (sk/point))
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t97_l450
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v96_l442)))


(def
 v99_l455
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :x :y)
   (sk/lay (sk/point {:color :bogus}))
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t100_l463
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v99_l455)))


(def
 v102_l468
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :x :y)
   (sk/lay (sk/line))
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t103_l477
 (is ((fn [m] (re-find #"not supported with polar" m)) v102_l468)))


(def
 v105_l481
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t106_l489
 (is ((fn [m] (re-find #"must contain :boxes" m)) v105_l481)))
