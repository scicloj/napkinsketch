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


(def v4_l25 (-> with-missing (sk/lay-point :x :y)))


(deftest
 t5_l28
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l25)))


(def v7_l36 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t8_l39
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l36)))


(def v10_l48 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t11_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l48)))


(def
 v13_l60
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t14_l64
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l60)))


(def v16_l72 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t17_l75
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l72)))


(def v19_l83 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t20_l86
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l83)))


(def
 v22_l94
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t23_l97
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l94)))


(def
 v25_l103
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t26_l106
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l103)))


(def
 v28_l112
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t29_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l112)))


(def
 v31_l123
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v32_l129 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t33_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l129)))


(def
 v35_l140
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (mapv
     (fn* [p1__91194#] (keyword (str "cat-" p1__91194#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t36_l145
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l140)))


(def
 v38_l153
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l156
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l156)))


(def
 v42_l169
 (->
  iris
  (tc/select-rows
   (fn* [p1__91195#] (= "setosa" (p1__91195# :species))))
  (sk/lay-point :sepal_length :sepal_width)
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t43_l175
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l169)))


(def
 v45_l185
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t46_l189
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l185)))


(def
 v48_l196
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t49_l200
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l196)))


(def
 v51_l207
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t52_l211
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l207)))


(def
 v54_l217
 (->
  iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t55_l220
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l217)))


(def
 v57_l227
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/lay-point :x :y)
  (sk/lay-lm {:se true})))


(deftest
 t58_l231
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v57_l227)))


(def
 v60_l239
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t61_l244
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v60_l239)))


(def
 v63_l250
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t64_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v63_l250)))


(def
 v66_l262
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t67_l267
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v66_l262)))


(def
 v69_l276
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t70_l279
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v69_l276)))


(def
 v72_l283
 (->
  {:x (range 20),
   :y (map (fn* [p1__91196#] (- p1__91196# 10)) (range 20)),
   :val (map (fn* [p1__91197#] (- p1__91197# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t73_l289
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v72_l283)))


(def
 v75_l295
 (->
  {:date
   [(java.time.LocalDate/of 2025 1 1)
    (java.time.LocalDate/of 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t76_l300
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v75_l295)))


(def
 v78_l307
 (->
  {:time
   (mapv
    (fn*
     [p1__91198#]
     (java.time.LocalDateTime/of
      2025
      3
      15
      (+ 8 (int (/ p1__91198# 4)))
      (* 15 (mod (int p1__91198#) 4))
      0))
    (range 24)),
   :value
   (mapv
    (fn* [p1__91199#] (+ 18.0 (* 4.0 (Math/sin (* p1__91199# 0.3)))))
    (range 24))}
  (sk/lay-line :time :value)
  sk/lay-point))


(deftest
 t79_l316
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v78_l307)))


(def
 v81_l326
 (->
  {:time
   (mapv
    (fn*
     [p1__91200#]
     (java.time.Instant/ofEpochSecond
      (+ 1750003200 (* p1__91200# 3600))))
    (range 12)),
   :temp
   (mapv
    (fn* [p1__91201#] (+ 20.0 (* 5.0 (Math/sin (* p1__91201# 0.5)))))
    (range 12))}
  (sk/lay-line :time :temp)
  sk/lay-point))


(deftest
 t82_l333
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__91202#] (re-find #":\d\d" p1__91202#))
       (:texts s)))))
   v81_l326)))


(def
 v84_l342
 (->
  {:date
   (mapv
    (fn*
     [p1__91203#]
     (java.time.LocalDate/ofEpochDay
      (+ 18262 (* (long p1__91203#) 120))))
    (range 20)),
   :value
   (mapv
    (fn* [p1__91204#] (+ 100 (* 50 (Math/sin (* p1__91204# 0.4)))))
    (range 20))}
  (sk/lay-line :date :value)
  sk/lay-point))


(deftest
 t85_l348
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v84_l342)))


(def
 v87_l356
 (->
  {:cat (map (fn* [p1__91205#] (str "cat-" p1__91205#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t88_l361
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v87_l356)))


(def
 v90_l365
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t91_l369
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v90_l365)))


(def
 v93_l378
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t94_l382
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
       (fn* [p1__91206#] (re-find #"sepal|petal" p1__91206#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v93_l378)))


(def
 v96_l393
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t97_l400
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v96_l393)))


(def
 v99_l405
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t100_l412
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v99_l405)))


(def
 v102_l417
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t103_l425
 (is ((fn [m] (re-find #"not supported with polar" m)) v102_l417)))


(def
 v105_l429
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t106_l437
 (is ((fn [m] (re-find #"must contain :boxes" m)) v105_l429)))
