(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [tech.v3.datatype.datetime :as dt-dt]
  [tech.v3.datatype :as dtype]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l33 (-> with-missing (sk/lay-point :x :y)))


(deftest
 t5_l36
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l33)))


(def
 v7_l45
 (def
  with-infinity
  {:x [1 2 3 4 5],
   :y
   [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]}))


(def v8_l49 (-> with-infinity (sk/lay-point :x :y)))


(deftest
 t9_l52
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 3 (:points s))
      (not (clojure.string/includes? (str v) "NaN")))))
   v8_l49)))


(def v11_l60 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t12_l63
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v11_l60)))


(def v14_l72 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t15_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v14_l72)))


(def
 v17_l84
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t18_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v17_l84)))


(def v20_l96 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t21_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v20_l96)))


(def
 v23_l107
 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t24_l110
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v23_l107)))


(def
 v26_l118
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t27_l121
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v26_l118)))


(def
 v29_l127
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t30_l130
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v29_l127)))


(def
 v32_l136
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t33_l139
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v32_l136)))


(def
 v35_l147
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v36_l153 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t37_l156
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v36_l153)))


(def
 v39_l164
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__82445#] (keyword (str "cat-" p1__82445#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t40_l169
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v39_l164)))


(def
 v42_l177
 (->
  data/iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t43_l182
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v42_l177)))


(def
 v45_l190
 (->
  data/iris
  (tc/select-rows
   (fn* [p1__82446#] (= "setosa" (p1__82446# :species))))
  (sk/lay-point :sepal_length :sepal_width)
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t46_l196
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v45_l190)))


(def
 v48_l206
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t49_l210
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l206)))


(def
 v51_l217
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t52_l221
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l217)))


(def
 v54_l228
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t55_l232
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l228)))


(def
 v57_l238
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t58_l241
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v57_l238)))


(def
 v60_l248
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/lay-point :x :y)
  (sk/lay-lm {:se true})))


(deftest
 t61_l252
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v60_l248)))


(def
 v63_l260
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t64_l265
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v63_l260)))


(def
 v66_l271
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t67_l277
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v66_l271)))


(def
 v69_l283
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t70_l288
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v69_l283)))


(def
 v72_l295
 (->
  {:x [0 -1 1 10 100], :y [1 2 3 4 5]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t73_l299
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v72_l295)))


(def
 v75_l308
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t76_l311
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v75_l308)))


(def
 v78_l315
 (->
  {:x (range 20),
   :y (map (fn* [p1__82447#] (- p1__82447# 10)) (range 20)),
   :val (map (fn* [p1__82448#] (- p1__82448# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t79_l321
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v78_l315)))


(def
 v81_l327
 (->
  {:date [(jt/local-date 2025 1 1) (jt/local-date 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t82_l332
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v81_l327)))


(def
 v84_l339
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
    (map (fn* [p1__82449#] (* (long p1__82449#) 15)) (range 24))
    :minutes),
   :value
   (map
    (fn* [p1__82450#] (+ 18.0 (* 4.0 (Math/sin (* p1__82450# 0.3)))))
    (range 24))}
  (sk/lay-line :time :value)
  sk/lay-point))


(deftest
 t85_l346
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v84_l339)))


(def
 v87_l356
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/instant 1750003200000) 12)
    (range 12)
    :hours),
   :temp
   (map
    (fn* [p1__82451#] (+ 20.0 (* 5.0 (Math/sin (* p1__82451# 0.5)))))
    (range 12))}
  (sk/lay-line :time :temp)
  sk/lay-point))


(deftest
 t88_l363
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__82452#] (re-find #":\d\d" p1__82452#))
       (:texts s)))))
   v87_l356)))


(def
 v90_l372
 (->
  {:date
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date 2020 1 1) 20)
    (map (fn* [p1__82453#] (* (long p1__82453#) 120)) (range 20))
    :days),
   :value
   (map
    (fn* [p1__82454#] (+ 100 (* 50 (Math/sin (* p1__82454# 0.4)))))
    (range 20))}
  (sk/lay-line :date :value)
  sk/lay-point))


(deftest
 t91_l379
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v90_l372)))


(def
 v93_l387
 (->
  {:cat (map (fn* [p1__82455#] (str "cat-" p1__82455#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-value-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t94_l392
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v93_l387)))


(def
 v96_l396
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t97_l400
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v96_l396)))


(def
 v99_l409
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t100_l413
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
       (fn* [p1__82456#] (re-find #"sepal|petal" p1__82456#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v99_l409)))


(def
 v102_l424
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t103_l431
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v102_l424)))


(def
 v105_l436
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t106_l443
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v105_l436)))


(def
 v108_l448
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t109_l456
 (is ((fn [m] (re-find #"not supported with polar" m)) v108_l448)))


(def
 v111_l460
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t112_l468
 (is ((fn [m] (re-find #"must contain :boxes" m)) v111_l460)))


(def
 v114_l475
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-histogram :x :y))
  (catch Exception e (ex-message e))))


(deftest
 t115_l481
 (is ((fn [m] (re-find #"uses only the x column" m)) v114_l475)))
