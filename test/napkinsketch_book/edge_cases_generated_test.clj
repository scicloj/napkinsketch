(ns
 napkinsketch-book.edge-cases-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [tech.v3.datatype.datetime :as dt-dt]
  [tech.v3.datatype :as dtype]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l37 (-> with-missing (sk/lay-point :x :y)))


(deftest
 t5_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l37)))


(def
 v7_l49
 (def
  with-infinity
  {:x [1 2 3 4 5],
   :y
   [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]}))


(def v8_l53 (-> with-infinity (sk/lay-point :x :y)))


(deftest
 t9_l56
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 3 (:points s))
      (not (clojure.string/includes? (str v) "NaN")))))
   v8_l53)))


(def v11_l64 (-> {:x [3], :y [7]} (sk/lay-point :x :y)))


(deftest
 t12_l67
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v11_l64)))


(def v14_l76 (-> {:x [1 10], :y [5 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t15_l80
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v14_l76)))


(def
 v17_l88
 (-> {:x [1 5 10], :y [5 25 50]} (sk/lay-point :x :y) sk/lay-lm))


(deftest
 t18_l92
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v17_l88)))


(def
 v20_l100
 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (sk/lay-point :x :y)))


(deftest
 t21_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v20_l100)))


(def
 v23_l111
 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (sk/lay-point :x :y)))


(deftest
 t24_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v23_l111)))


(def
 v26_l122
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (sk/lay-point :x :y)))


(deftest
 t27_l125
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v26_l122)))


(def
 v29_l131
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (sk/lay-point :x :y)))


(deftest
 t30_l134
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v29_l131)))


(def
 v32_l140
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (sk/lay-point :x :y)))


(deftest
 t33_l143
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v32_l140)))


(def
 v35_l151
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v36_l157 (-> large-data (sk/lay-point :x :y {:color :group})))


(deftest
 t37_l160
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v36_l157)))


(def
 v39_l168
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__93052#] (keyword (str "cat-" p1__93052#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (sk/lay-value-bar :category :value)))


(deftest
 t40_l173
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v39_l168)))


(def
 v42_l181
 (->
  (rdatasets/datasets-iris)
  (tc/map-columns :sepal-ratio [:sepal-length :sepal-width] /)
  (sk/lay-point :sepal-length :sepal-ratio {:color :species})
  (sk/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t43_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v42_l181)))


(def
 v45_l194
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__93053#] (= "setosa" (p1__93053# :species))))
  (sk/lay-point :sepal-length :sepal-width)
  sk/lay-lm
  (sk/options {:title "Setosa Only"})))


(deftest
 t46_l200
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v45_l194)))


(def
 v48_l210
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (sk/lay-value-bar :category :count {:position :stack})))


(deftest
 t49_l214
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v48_l210)))


(def
 v51_l221
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (sk/lay-bar :x {:color :g})))


(deftest
 t52_l225
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v51_l221)))


(def
 v54_l232
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (sk/lay-stacked-bar-fill :x {:color :g})))


(deftest
 t55_l236
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v54_l232)))


(def
 v57_l242
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point
   :sepal-length
   :sepal-width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t58_l245
 (is ((fn [v] (= 150 (:points (sk/svg-summary v)))) v57_l242)))


(def
 v60_l252
 (->
  {:x [1 2 3], :y [2 4 5]}
  (sk/lay-point :x :y)
  (sk/lay-lm {:se true})))


(deftest
 t61_l256
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v60_l252)))


(def
 v63_l264
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (sk/lay-stacked-area :x :y)))


(deftest
 t64_l269
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v63_l264)))


(def
 v66_l275
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/scale :y :log)))


(deftest
 t67_l281
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v66_l275)))


(def
 v69_l287
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t70_l292
 (is ((fn [v] (= 6 (:points (sk/svg-summary v)))) v69_l287)))


(def
 v72_l299
 (->
  {:x [0 -1 1 10 100], :y [1 2 3 4 5]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)))


(deftest
 t73_l303
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v72_l299)))


(def
 v75_l312
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (sk/lay-point :x :y {:color :c})))


(deftest
 t76_l315
 (is ((fn [v] (= 3 (:points (sk/svg-summary v)))) v75_l312)))


(def
 v78_l319
 (->
  {:x (range 20),
   :y (map (fn* [p1__93054#] (- p1__93054# 10)) (range 20)),
   :val (map (fn* [p1__93055#] (- p1__93055# 10.0)) (range 20))}
  (sk/lay-point :x :y {:color :val})
  (sk/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t79_l325
 (is ((fn [v] (= 20 (:points (sk/svg-summary v)))) v78_l319)))


(def
 v81_l331
 (->
  {:date [(jt/local-date 2025 1 1) (jt/local-date 2025 1 2)],
   :val [10 20]}
  (sk/lay-point :date :val)))


(deftest
 t82_l336
 (is ((fn [v] (= 2 (:points (sk/svg-summary v)))) v81_l331)))


(def
 v84_l343
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
    (map (fn* [p1__93056#] (* (long p1__93056#) 15)) (range 24))
    :minutes),
   :value
   (map
    (fn* [p1__93057#] (+ 18.0 (* 4.0 (Math/sin (* p1__93057# 0.3)))))
    (range 24))}
  (sk/lay-line :time :value)
  sk/lay-point))


(deftest
 t85_l350
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v84_l343)))


(def
 v87_l360
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/instant 1750003200000) 12)
    (range 12)
    :hours),
   :temp
   (map
    (fn* [p1__93058#] (+ 20.0 (* 5.0 (Math/sin (* p1__93058# 0.5)))))
    (range 12))}
  (sk/lay-line :time :temp)
  sk/lay-point))


(deftest
 t88_l367
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__93059#] (re-find #":\d\d" p1__93059#))
       (:texts s)))))
   v87_l360)))


(def
 v90_l376
 (->
  {:date
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date 2020 1 1) 20)
    (map (fn* [p1__93060#] (* (long p1__93060#) 120)) (range 20))
    :days),
   :value
   (map
    (fn* [p1__93061#] (+ 100 (* 50 (Math/sin (* p1__93061# 0.4)))))
    (range 20))}
  (sk/lay-line :date :value)
  sk/lay-point))


(deftest
 t91_l383
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v90_l376)))


(def
 v93_l391
 (->
  {:cat (map (fn* [p1__93062#] (str "cat-" p1__93062#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (sk/lay-value-bar :cat :val)
  (sk/coord :polar)))


(deftest
 t94_l396
 (is ((fn [v] (pos? (:polygons (sk/svg-summary v)))) v93_l391)))


(def
 v96_l404
 (->
  {:x [1 10 100 1000], :y [2 4 8 16]}
  (sk/lay-point :x :y)
  (sk/scale :x :log)
  (sk/coord :flip)))


(deftest
 t97_l409
 (is
  ((fn
    [v]
    (let
     [plan (sk/plan v) panel (first (:panels plan))]
     (and
      (= 4 (:points (sk/svg-summary v)))
      (= :flip (:coord panel))
      (= {:type :log} (:y-scale panel))
      (= {:type :linear} (:x-scale panel)))))
   v96_l404)))


(def
 v99_l422
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width)
  (sk/scale :y {:domain [0 6]})))


(deftest
 t100_l426
 (is
  ((fn
    [v]
    (let
     [plan (sk/plan v) panel (first (:panels plan))]
     (= [0 6] (:y-domain panel))))
   v99_l422)))


(def
 v102_l434
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (sk/lay-point :x :y)
  (sk/coord :fixed)))


(deftest
 t103_l438
 (is ((fn [v] (= 100 (:points (sk/svg-summary v)))) v102_l434)))


(def
 v105_l447
 (->
  (rdatasets/datasets-iris)
  (sk/view
   (sk/cross
    [:sepal-length :sepal-width :petal-length]
    [:sepal-length :sepal-width :petal-length]))
  (sk/lay-point {:color :species})))


(deftest
 t106_l451
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
       (fn* [p1__93063#] (re-find #"sepal|petal" p1__93063#))
       texts)]
     (and (= 9 (:panels s)) (= 6 (count strip-labels)))))
   v105_l447)))


(def
 v108_l462
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-point :nonexistent :y) sk/plot)
  (catch Exception e (ex-message e))))


(deftest t109_l469 (is ((fn [m] (string? m)) v108_l462)))


(def
 v111_l473
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-point :x :y {:color :bogus})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest t112_l480 (is ((fn [m] (string? m)) v111_l473)))


(def
 v114_l484
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (sk/lay-line :x :y)
   (sk/coord :polar)
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t115_l492
 (is ((fn [m] (re-find #"not supported with polar" m)) v114_l484)))


(def
 v117_l496
 (try
  (->
   {:x [1 2 3]}
   (sk/view :x)
   (sk/lay {:mark :boxplot, :stat :bin})
   sk/plot)
  (catch Exception e (ex-message e))))


(deftest
 t118_l504
 (is ((fn [m] (re-find #"must contain :boxes" m)) v117_l496)))


(def
 v120_l513
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (sk/lay-histogram :x :y))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t121_l519
 (is
  ((fn [m] (re-find #"lay-histogram uses only the x column" m))
   v120_l513)))
