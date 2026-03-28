(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [clojure.test :refer [deftest is]]))


(def
 v3_l23
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l27 (-> with-missing (sk/lay-point :x :y)))


(deftest
 t5_l30
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l27)))


(def v7_l38 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t8_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l38)))


(def v10_l50 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t11_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l50)))


(def
 v13_l62
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t14_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l62)))


(def v16_l74 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t17_l77
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l74)))


(def v19_l85 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t20_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l85)))


(def
 v22_l96
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t23_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l96)))


(def
 v25_l105
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t26_l108
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l105)))


(def
 v28_l114
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t29_l117
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l114)))


(def
 v31_l125
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v32_l131 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t33_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l131)))


(def
 v35_l142
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__81107#] (keyword (str "cat-" p1__81107#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t36_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l142)))


(def
 v38_l155
 (->
  data/iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t39_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l155)))


(def
 v41_l168
 (->
  data/iris
  (tc/select-rows
   (fn* [p1__81108#] (= "setosa" (p1__81108# :species))))
  (sk/lay-point :sepal_length :sepal_width)
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t42_l174
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v41_l168)))


(def
 v44_l184
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t45_l188
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v44_l184)))


(def
 v47_l195
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t48_l199
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v47_l195)))


(def
 v50_l206
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t51_l210
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v50_l206)))


(def
 v53_l216
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t54_l219
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v53_l216)))


(def
 v56_l226
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/lay-point :x :y)
  (sk/lay-lm {:se true})))


(deftest
 t57_l230
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v56_l226)))


(def
 v59_l238
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t60_l243
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v59_l238)))


(def
 v62_l249
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t63_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v62_l249)))


(def
 v65_l261
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t66_l266
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v65_l261)))


(def
 v68_l275
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t69_l278
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v68_l275)))


(def
 v71_l282
 (->
  {:x (range 20),
   :y (map (fn* [p1__81109#] (- p1__81109# 10)) (range 20)),
   :val (map (fn* [p1__81110#] (- p1__81110# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t72_l288
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v71_l282)))


(def
 v74_l294
 (->
  {:date
   [(java.time.LocalDate/of 2025 1 1)
    (java.time.LocalDate/of 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t75_l299
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v74_l294)))


(def
 v77_l306
 (->
  {:time
   (map
    (fn*
     [p1__81111#]
     (java.time.LocalDateTime/of
      2025
      3
      15
      (+ 8 (int (/ p1__81111# 4)))
      (* 15 (mod (int p1__81111#) 4))
      0))
    (range 24)),
   :value
   (map
    (fn* [p1__81112#] (+ 18.0 (* 4.0 (Math/sin (* p1__81112# 0.3)))))
    (range 24))}
  (sk/lay-line :time :value)
  sk/lay-point))


(deftest
 t78_l315
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v77_l306)))


(def
 v80_l325
 (->
  {:time
   (map
    (fn*
     [p1__81113#]
     (java.time.Instant/ofEpochSecond
      (+ 1750003200 (* p1__81113# 3600))))
    (range 12)),
   :temp
   (map
    (fn* [p1__81114#] (+ 20.0 (* 5.0 (Math/sin (* p1__81114# 0.5)))))
    (range 12))}
  (sk/lay-line :time :temp)
  sk/lay-point))


(deftest
 t81_l332
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__81115#] (re-find #":\d\d" p1__81115#))
       (:texts s)))))
   v80_l325)))


(def
 v83_l341
 (->
  {:date
   (map
    (fn*
     [p1__81116#]
     (java.time.LocalDate/ofEpochDay
      (+ 18262 (* (long p1__81116#) 120))))
    (range 20)),
   :value
   (map
    (fn* [p1__81117#] (+ 100 (* 50 (Math/sin (* p1__81117# 0.4)))))
    (range 20))}
  (sk/lay-line :date :value)
  sk/lay-point))


(deftest
 t84_l347
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v83_l341)))


(def
 v86_l355
 (->
  {:cat (map (fn* [p1__81118#] (str "cat-" p1__81118#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t87_l360
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v86_l355)))


(def
 v89_l364
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t90_l368
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v89_l364)))


(def
 v92_l377
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t93_l381
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
       (fn* [p1__81119#] (re-find #"sepal|petal" p1__81119#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v92_l377)))


(def
 v95_l392
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t96_l399
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v95_l392)))


(def
 v98_l404
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t99_l411
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v98_l404)))


(def
 v101_l416
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t102_l424
 (is ((fn [m] (re-find #"not supported with polar" m)) v101_l416)))


(def
 v104_l428
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t105_l436
 (is ((fn [m] (re-find #"must contain :boxes" m)) v104_l428)))
