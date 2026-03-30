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


(def v7_l44 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t8_l47
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v7_l44)))


(def v10_l56 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t11_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v10_l56)))


(def
 v13_l68
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t14_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v13_l68)))


(def v16_l80 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t17_l83
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v16_l80)))


(def v19_l91 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t20_l94
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v19_l91)))


(def
 v22_l102
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t23_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v22_l102)))


(def
 v25_l111
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t26_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v25_l111)))


(def
 v28_l120
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t29_l123
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v28_l120)))


(def
 v31_l131
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v32_l137 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t33_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v32_l137)))


(def
 v35_l148
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__77762#] (keyword (str "cat-" p1__77762#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t36_l153
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v35_l148)))


(def
 v38_l161
 (->
  data/iris
  (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
  (sk/lay-point :sepal_length :sepal_ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t39_l166
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v38_l161)))


(def
 v41_l174
 (->
  data/iris
  (tc/select-rows
   (fn* [p1__77763#] (= "setosa" (p1__77763# :species))))
  (sk/lay-point :sepal_length :sepal_width)
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t42_l180
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v41_l174)))


(def
 v44_l190
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t45_l194
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v44_l190)))


(def
 v47_l201
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t48_l205
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v47_l201)))


(def
 v50_l212
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t51_l216
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v50_l212)))


(def
 v53_l222
 (->
  data/iris
  (sk/lay-point
   :sepal_length
   :sepal_width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t54_l225
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v53_l222)))


(def
 v56_l232
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/lay-point :x :y)
  (sk/lay-lm {:se true})))


(deftest
 t57_l236
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v56_l232)))


(def
 v59_l244
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t60_l249
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v59_l244)))


(def
 v62_l255
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t63_l261
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v62_l255)))


(def
 v65_l267
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t66_l272
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v65_l267)))


(def
 v68_l281
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t69_l284
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v68_l281)))


(def
 v71_l288
 (->
  {:x (range 20),
   :y (map (fn* [p1__77764#] (- p1__77764# 10)) (range 20)),
   :val (map (fn* [p1__77765#] (- p1__77765# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t72_l294
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v71_l288)))


(def
 v74_l300
 (->
  {:date [(jt/local-date 2025 1 1) (jt/local-date 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t75_l305
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v74_l300)))


(def
 v77_l312
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
    (map (fn* [p1__77766#] (* (long p1__77766#) 15)) (range 24))
    :minutes),
   :value
   (map
    (fn* [p1__77767#] (+ 18.0 (* 4.0 (Math/sin (* p1__77767# 0.3)))))
    (range 24))}
  (sk/lay-line :time :value)
  sk/lay-point))


(deftest
 t78_l319
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v77_l312)))


(def
 v80_l329
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/instant 1750003200000) 12)
    (range 12)
    :hours),
   :temp
   (map
    (fn* [p1__77768#] (+ 20.0 (* 5.0 (Math/sin (* p1__77768# 0.5)))))
    (range 12))}
  (sk/lay-line :time :temp)
  sk/lay-point))


(deftest
 t81_l336
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__77769#] (re-find #":\d\d" p1__77769#))
       (:texts s)))))
   v80_l329)))


(def
 v83_l345
 (->
  {:date
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date 2020 1 1) 20)
    (map (fn* [p1__77770#] (* (long p1__77770#) 120)) (range 20))
    :days),
   :value
   (map
    (fn* [p1__77771#] (+ 100 (* 50 (Math/sin (* p1__77771# 0.4)))))
    (range 20))}
  (sk/lay-line :date :value)
  sk/lay-point))


(deftest
 t84_l352
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v83_l345)))


(def
 v86_l360
 (->
  {:cat (map (fn* [p1__77772#] (str "cat-" p1__77772#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t87_l365
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v86_l360)))


(def
 v89_l369
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t90_l373
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v89_l369)))


(def
 v92_l382
 (->
  data/iris
  (sk/view
   (sk/cross
    [:sepal_length :sepal_width :petal_length]
    [:sepal_length :sepal_width :petal_length]))
  (sk/lay-point {:color :species})))


(deftest
 t93_l386
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
       (fn* [p1__77773#] (re-find #"sepal|petal" p1__77773#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v92_l382)))


(def
 v95_l397
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t96_l404
 (is
  ((fn
    [m]
    (and
     (re-find #"not found in dataset" m)
     (re-find #":nonexistent" m)))
   v95_l397)))


(def
 v98_l409
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t99_l416
 (is
  ((fn
    [m]
    (and (re-find #"not found in dataset" m) (re-find #":bogus" m)))
   v98_l409)))


(def
 v101_l421
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t102_l429
 (is ((fn [m] (re-find #"not supported with polar" m)) v101_l421)))


(def
 v104_l433
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t105_l441
 (is ((fn [m] (re-find #"must contain :boxes" m)) v104_l433)))
