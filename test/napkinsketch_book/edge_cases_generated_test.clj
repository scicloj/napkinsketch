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


(def
 v10_l48
 (-> {:x [1 10], :y [5 50]} (sk/view [[:x :y]]) sk/lay-point sk/lay-lm))


(deftest
 t11_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l48)))


(def
 v13_l61
 (->
  {:x [1 5 10], :y [5 25 50]}
  (sk/view [[:x :y]])
  sk/lay-point
  sk/lay-lm))


(deftest
 t14_l66
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l61)))


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
    (mapv
     (fn* [p1__86658#] (keyword (str "cat-" p1__86658#)))
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
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v39_l158
 (->
  iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t40_l163
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v39_l158)))


(def
 v42_l171
 (->
  iris
  (tc/select-rows
   (fn* [p1__86659#] (= "setosa" (p1__86659# :species))))
  (sk/view [[:sepal_length :sepal_width]])
  sk/lay-point
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t43_l178
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v42_l171)))


(def
 v45_l188
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t46_l192
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v45_l188)))


(def
 v48_l199
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t49_l203
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l199)))


(def
 v51_l210
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t52_l214
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l210)))


(def
 v54_l220
 (->
  iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t55_l223
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v54_l220)))


(def
 v57_l230
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/view :x :y)
  sk/lay-point
  (sk/lay-lm {:se true})))


(deftest
 t58_l235
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v57_l230)))


(def
 v60_l243
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t61_l248
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v60_l243)))


(def
 v63_l254
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t64_l260
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v63_l254)))


(def
 v66_l266
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t67_l271
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v66_l266)))


(def
 v69_l280
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t70_l283
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v69_l280)))


(def
 v72_l287
 (->
  {:x (range 20),
   :y (map (fn* [p1__86660#] (- p1__86660# 10)) (range 20)),
   :val (map (fn* [p1__86661#] (- p1__86661# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t73_l293
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v72_l287)))


(def
 v75_l299
 (->
  {:date
   [(java.time.LocalDate/of 2025 1 1)
    (java.time.LocalDate/of 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t76_l304
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v75_l299)))


(def
 v78_l311
 (->
  {:time
   (mapv
    (fn*
     [p1__86662#]
     (java.time.LocalDateTime/of
      2025
      3
      15
      (+ 8 (int (/ p1__86662# 4)))
      (* 15 (mod (int p1__86662#) 4))
      0))
    (range 24)),
   :value
   (mapv
    (fn* [p1__86663#] (+ 18.0 (* 4.0 (Math/sin (* p1__86663# 0.3)))))
    (range 24))}
  (sk/view :time :value)
  sk/lay-line
  sk/lay-point))


(deftest
 t79_l321
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v78_l311)))


(def
 v81_l331
 (->
  {:time
   (mapv
    (fn*
     [p1__86664#]
     (java.time.Instant/ofEpochSecond
      (+ 1750003200 (* p1__86664# 3600))))
    (range 12)),
   :temp
   (mapv
    (fn* [p1__86665#] (+ 20.0 (* 5.0 (Math/sin (* p1__86665# 0.5)))))
    (range 12))}
  (sk/view :time :temp)
  sk/lay-line
  sk/lay-point))


(deftest
 t82_l339
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__86666#] (re-find #":\d\d" p1__86666#))
       (:texts s)))))
   v81_l331)))


(def
 v84_l348
 (->
  {:date
   (mapv
    (fn*
     [p1__86667#]
     (java.time.LocalDate/ofEpochDay
      (+ 18262 (* (long p1__86667#) 120))))
    (range 20)),
   :value
   (mapv
    (fn* [p1__86668#] (+ 100 (* 50 (Math/sin (* p1__86668# 0.4)))))
    (range 20))}
  (sk/view :date :value)
  sk/lay-line
  sk/lay-point))


(deftest
 t85_l355
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v84_l348)))


(def
 v87_l363
 (->
  {:cat (map (fn* [p1__86669#] (str "cat-" p1__86669#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t88_l368
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v87_l363)))


(def
 v90_l372
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t91_l376
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v90_l372)))


(def
 v93_l385
 (->
  iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t94_l389
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
       (fn* [p1__86670#] (re-find #"sepal|petal" p1__86670#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v93_l385)))


(def
 v96_l400
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t97_l407
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v96_l400)))


(def
 v99_l412
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t100_l419
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v99_l412)))


(def
 v102_l424
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t103_l432
 (is ((fn [m] (re-find #"not supported with polar" m)) v102_l424)))


(def
 v105_l436
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t106_l444
 (is ((fn [m] (re-find #"must contain :boxes" m)) v105_l436)))
