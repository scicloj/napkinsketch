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


(def v4_l25 (-> with-missing (sk/view [[:x :y]]) sk/lay-point))


(deftest
 t5_l29
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l25)))


(def v7_l37 (-> {:x [3], :y [7]} (sk/view [[:x :y]]) sk/lay-point))


(deftest
 t8_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l37)))


(def
 v10_l50
 (-> {:x [1 10], :y [5 50]} (sk/view [[:x :y]]) sk/lay-point sk/lay-lm))


(deftest
 t11_l55
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l50)))


(def
 v13_l63
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/view [[:x :y]])
  sk/lay-point
  sk/lay-lm))


(deftest
 t14_l68
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l63)))


(def
 v16_l76
 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/view [[:x :y]]) sk/lay-point))


(deftest
 t17_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l76)))


(def
 v19_l88
 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/view [[:x :y]]) sk/lay-point))


(deftest
 t20_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l88)))


(def
 v22_l100
 (->
  {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]}
  (sk/view [[:x :y]])
  sk/lay-point))


(deftest
 t23_l104
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l100)))


(def
 v25_l110
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/view [[:x :y]])
  sk/lay-point))


(deftest
 t26_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l110)))


(def
 v28_l120
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/view [[:x :y]])
  sk/lay-point))


(deftest
 t29_l124
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l120)))


(def
 v31_l132
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def
 v32_l138
 (-> large-data (sk/view [[:x :y]]) (sk/lay-point {:color :group})))


(deftest
 t33_l142
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l138)))


(def
 v35_l150
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (mapv
     (fn* [p1__88515#] (keyword (str "cat-" p1__88515#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/view [[:category :value]])
  sk/lay-value-bar))


(deftest
 t36_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l150)))


(def
 v38_l164
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l167
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/view [[:sepal_length :sepal_ratio]])
  (sk/lay-point {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l173
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l167)))


(def
 v42_l181
 (->
  iris
  (tc/select-rows
   (fn* [p1__88516#] (= "setosa" (p1__88516# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t43_l188
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l181)))


(def
 v45_l198
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/view :category :count)
  (sk/lay-value-bar {:position :stack})))


(deftest
 t46_l203
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l198)))


(def
 v48_l210
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/view :x)
  (sk/lay-bar {:color :g})))


(deftest
 t49_l215
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l210)))


(def
 v51_l222
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/view :x)
  (sk/lay-stacked-bar-fill {:color :g})))


(deftest
 t52_l227
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l222)))


(def
 v54_l233
 (->
  iris
  (sk/view :sepal_length :sepal_width)
  (sk/lay-point {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t55_l237
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l233)))


(def
 v57_l244
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/view :x :y)
  sk/lay-point
  (sk/lay-lm {:se true})))


(deftest
 t58_l249
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v57_l244)))


(def
 v60_l257
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/view :x :y)
  sk/lay-stacked-area))


(deftest
 t61_l263
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v60_l257)))


(def
 v63_l269
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/view :x :y)
  sk/lay-point
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t64_l276
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v63_l269)))


(def
 v66_l282
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/view :x :y)
  sk/lay-point
  (sk/scale :x :log)))


(deftest
 t67_l288
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v66_l282)))


(def
 v69_l297
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/view :x :y)
  (sk/lay-point {:color :c})))


(deftest
 t70_l301
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v69_l297)))


(def
 v72_l305
 (->
  {:x (range 20),
   :y (map (fn* [p1__88517#] (- p1__88517# 10)) (range 20)),
   :val (map (fn* [p1__88518#] (- p1__88518# 10.0)) (range 20))}
  (sk/view :x :y)
  (sk/lay-point {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t73_l312
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v72_l305)))


(def
 v75_l318
 (->
  {:date
   [(java.time.LocalDate/of 2025 1 1)
    (java.time.LocalDate/of 2025 1 2)],
   :val [10 20]}
  (sk/view :date :val)
  sk/lay-point))


(deftest
 t76_l324
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v75_l318)))


(def
 v78_l331
 (->
  {:time
   (mapv
    (fn*
     [p1__88519#]
     (java.time.LocalDateTime/of
      2025
      3
      15
      (+ 8 (int (/ p1__88519# 4)))
      (* 15 (mod (int p1__88519#) 4))
      0))
    (range 24)),
   :value
   (mapv
    (fn* [p1__88520#] (+ 18.0 (* 4.0 (Math/sin (* p1__88520# 0.3)))))
    (range 24))}
  (sk/view :time :value)
  sk/lay-line
  sk/lay-point))


(deftest
 t79_l341
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v78_l331)))


(def
 v81_l351
 (->
  {:time
   (mapv
    (fn*
     [p1__88521#]
     (java.time.Instant/ofEpochSecond
      (+ 1750003200 (* p1__88521# 3600))))
    (range 12)),
   :temp
   (mapv
    (fn* [p1__88522#] (+ 20.0 (* 5.0 (Math/sin (* p1__88522# 0.5)))))
    (range 12))}
  (sk/view :time :temp)
  sk/lay-line
  sk/lay-point))


(deftest
 t82_l359
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__88523#] (re-find #":\d\d" p1__88523#))
       (:texts s)))))
   v81_l351)))


(def
 v84_l368
 (->
  {:date
   (mapv
    (fn*
     [p1__88524#]
     (java.time.LocalDate/ofEpochDay
      (+ 18262 (* (long p1__88524#) 120))))
    (range 20)),
   :value
   (mapv
    (fn* [p1__88525#] (+ 100 (* 50 (Math/sin (* p1__88525# 0.4)))))
    (range 20))}
  (sk/view :date :value)
  sk/lay-line
  sk/lay-point))


(deftest
 t85_l375
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v84_l368)))


(def
 v87_l383
 (->
  {:cat (map (fn* [p1__88526#] (str "cat-" p1__88526#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/view :cat :val)
  sk/lay-bar
  (sk/coord :polar)))


(deftest
 t88_l389
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v87_l383)))


(def
 v90_l393
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/view :x :y)
  sk/lay-point
  (sk/coord :fixed)))


(deftest
 t91_l398
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v90_l393)))


(def
 v93_l407
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t94_l411
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
       (fn* [p1__88527#] (re-find #"sepal|petal" p1__88527#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v93_l407)))


(def
 v96_l422
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :nonexistent :y)
   sk/lay-point
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t97_l430
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v96_l422)))


(def
 v99_l435
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :x :y)
   (sk/lay-point {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t100_l443
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v99_l435)))


(def
 v102_l448
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/view :x :y)
   sk/lay-line
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t103_l457
 (is ((fn [m] (re-find #"not supported with polar" m)) v102_l448)))


(def
 v105_l461
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t106_l469
 (is ((fn [m] (re-find #"must contain :boxes" m)) v105_l461)))
