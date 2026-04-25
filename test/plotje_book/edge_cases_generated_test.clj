(ns
 plotje-book.edge-cases-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as pj]
  [fastmath.random :as rng]
  [java-time.api :as jt]
  [tech.v3.datatype.datetime :as dt-dt]
  [tech.v3.datatype :as dtype]
  [clojure.test :refer [deftest is]]))


(def
 v3_l30
 (def with-missing {:x [1 2 nil 4 5 nil 7], :y [3 nil 5 6 nil 8 9]}))


(def v4_l34 (-> with-missing (pj/lay-point :x :y)))


(deftest
 t5_l37
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v4_l34)))


(def
 v7_l46
 (def
  with-infinity
  {:x [1 2 3 4 5],
   :y
   [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]}))


(def v8_l50 (-> with-infinity (pj/lay-point :x :y)))


(deftest
 t9_l53
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 1 (:panels s))
      (= 3 (:points s))
      (not (clojure.string/includes? (str v) "NaN")))))
   v8_l50)))


(def v11_l61 (-> {:x [3], :y [7]} (pj/lay-point :x :y)))


(deftest
 t12_l64
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:points s)))))
   v11_l61)))


(def
 v14_l73
 (->
  {:x [1 10], :y [5 50]}
  (pj/lay-point :x :y)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t15_l77
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 2 (:points s)) (zero? (:lines s)))))
   v14_l73)))


(def
 v17_l85
 (->
  {:x [1 5 10], :y [5 25 50]}
  (pj/lay-point :x :y)
  (pj/lay-smooth {:stat :linear-model})))


(deftest
 t18_l89
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v17_l85)))


(def v20_l97 (-> {:x [5 5 5 5 5], :y [1 2 3 4 5]} (pj/lay-point :x :y)))


(deftest
 t21_l100
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v20_l97)))


(def
 v23_l108
 (-> {:x [1 2 3 4 5], :y [3 3 3 3 3]} (pj/lay-point :x :y)))


(deftest
 t24_l111
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v23_l108)))


(def
 v26_l119
 (-> {:x [-5 -3 0 3 5], :y [-2 4 0 -4 2]} (pj/lay-point :x :y)))


(deftest
 t27_l122
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 5 (:points s)))))
   v26_l119)))


(def
 v29_l128
 (->
  {:x [1000000.0 2000000.0 3000000.0], :y [1.0E9 2.0E9 3.0E9]}
  (pj/lay-point :x :y)))


(deftest
 t30_l131
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v29_l128)))


(def
 v32_l137
 (->
  {:x [0.001 0.002 0.003], :y [1.0E-4 2.0E-4 3.0E-4]}
  (pj/lay-point :x :y)))


(deftest
 t33_l140
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:points s)))))
   v32_l137)))


(def
 v35_l148
 (def
  large-data
  (let
   [r (rng/rng :jdk 42)]
   {:x (repeatedly 1000 (fn* [] (rng/drandom r))),
    :y (repeatedly 1000 (fn* [] (rng/drandom r))),
    :group (repeatedly 1000 (fn* [] ([:a :b :c] (rng/irandom r 3))))})))


(def v36_l154 (-> large-data (pj/lay-point :x :y {:color :group})))


(deftest
 t37_l157
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 1000 (:points s)))))
   v36_l154)))


(def
 v39_l165
 (->
  (let
   [r (rng/rng :jdk 99)]
   {:category
    (map
     (fn* [p1__75690#] (keyword (str "cat-" p1__75690#)))
     (range 12)),
    :value (repeatedly 12 (fn* [] (+ 10 (rng/irandom r 90))))})
  (pj/lay-value-bar :category :value)))


(deftest
 t40_l170
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 12 (:polygons s)))))
   v39_l165)))


(def
 v42_l178
 (->
  (rdatasets/datasets-iris)
  (tc/map-columns :sepal-ratio [:sepal-length :sepal-width] /)
  (pj/lay-point :sepal-length :sepal-ratio {:color :species})
  (pj/options {:title "Sepal Length/Width Ratio"})))


(deftest
 t43_l183
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v42_l178)))


(def
 v45_l191
 (->
  (rdatasets/datasets-iris)
  (tc/select-rows
   (fn* [p1__75691#] (= "setosa" (p1__75691# :species))))
  (pj/lay-point :sepal-length :sepal-width)
  (pj/lay-smooth {:stat :linear-model})
  (pj/options {:title "Setosa Only"})))


(deftest
 t46_l197
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 50 (:points s)) (= 1 (:lines s)))))
   v45_l191)))


(def
 v48_l207
 (->
  {:category ["a" "b" "c"], :count [10 20 15]}
  (pj/lay-value-bar :category :count {:position :stack})))


(deftest
 t49_l211
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v48_l207)))


(def
 v51_l218
 (->
  {:x ["a" "b" "a"], :g ["g1" "g1" "g2"]}
  (pj/lay-bar :x {:color :g})))


(deftest
 t52_l222
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v51_l218)))


(def
 v54_l229
 (->
  {:x ["a" "a" "b" "b" "b"], :g ["g1" "g2" "g1" "g1" "g1"]}
  (pj/lay-bar :x {:position :fill, :color :g})))


(deftest
 t55_l233
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v54_l229)))


(def
 v57_l239
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point
   :sepal-length
   :sepal-width
   {:nudge-x 0.1, :nudge-y -0.05})))


(deftest
 t58_l242
 (is ((fn [v] (= 150 (:points (pj/svg-summary v)))) v57_l239)))


(def
 v60_l249
 (->
  {:x [1 2 3], :y [2 4 5]}
  (pj/lay-point :x :y)
  (pj/lay-smooth {:stat :linear-model, :confidence-band true})))


(deftest
 t61_l253
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 3 (:points s)) (= 1 (:lines s)))))
   v60_l249)))


(def
 v63_l261
 (->
  (let
   [r (rng/rng :jdk 55)]
   {:x (range 10), :y (repeatedly 10 (fn* [] (rng/irandom r 20)))})
  (pj/lay-area :x :y {:position :stack})))


(deftest
 t64_l266
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v63_l261)))


(def
 v66_l272
 (->
  {:x [1 10 100 1000 10000], :y [2 20 200 2000 20000]}
  (pj/lay-point :x :y)
  (pj/scale :x :log)
  (pj/scale :y :log)))


(deftest
 t67_l278
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:panels s)))))
   v66_l272)))


(def
 v69_l284
 (->
  {:x [0.001 0.01 0.1 1 10 100], :y [1 2 3 4 5 6]}
  (pj/lay-point :x :y)
  (pj/scale :x :log)))


(deftest
 t70_l289
 (is ((fn [v] (= 6 (:points (pj/svg-summary v)))) v69_l284)))


(def
 v72_l296
 (->
  {:x [0 -1 1 10 100], :y [1 2 3 4 5]}
  (pj/lay-point :x :y)
  (pj/scale :x :log)))


(deftest
 t73_l300
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v72_l296)))


(def
 v75_l309
 (->
  {:x [1 2 3], :y [4 5 6], :c [5 5 5]}
  (pj/lay-point :x :y {:color :c})))


(deftest
 t76_l312
 (is ((fn [v] (= 3 (:points (pj/svg-summary v)))) v75_l309)))


(def
 v78_l316
 (->
  {:x (range 20),
   :y (map (fn* [p1__75692#] (- p1__75692# 10)) (range 20)),
   :val (map (fn* [p1__75693#] (- p1__75693# 10.0)) (range 20))}
  (pj/lay-point :x :y {:color :val})
  (pj/options {:color-scale :diverging, :color-midpoint 0})))


(deftest
 t79_l322
 (is ((fn [v] (= 20 (:points (pj/svg-summary v)))) v78_l316)))


(def
 v81_l328
 (->
  {:date [(jt/local-date 2025 1 1) (jt/local-date 2025 1 2)],
   :val [10 20]}
  (pj/lay-point :date :val)))


(deftest
 t82_l333
 (is ((fn [v] (= 2 (:points (pj/svg-summary v)))) v81_l328)))


(def
 v84_l340
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
    (map (fn* [p1__75694#] (* (long p1__75694#) 15)) (range 24))
    :minutes),
   :value
   (map
    (fn* [p1__75695#] (+ 18.0 (* 4.0 (Math/sin (* p1__75695# 0.3)))))
    (range 24))}
  (pj/lay-line :time :value)
  pj/lay-point))


(deftest
 t85_l347
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 24 (:points s)) (= 1 (:lines s)))))
   v84_l340)))


(def
 v87_l357
 (->
  {:time
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/instant 1750003200000) 12)
    (range 12)
    :hours),
   :temp
   (map
    (fn* [p1__75696#] (+ 20.0 (* 5.0 (Math/sin (* p1__75696# 0.5)))))
    (range 12))}
  (pj/lay-line :time :temp)
  pj/lay-point))


(deftest
 t88_l364
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and
      (= 12 (:points s))
      (= 1 (:lines s))
      (some
       (fn* [p1__75697#] (re-find #":\d\d" p1__75697#))
       (:texts s)))))
   v87_l357)))


(def
 v90_l373
 (->
  {:date
   (dt-dt/plus-temporal-amount
    (dtype/const-reader (jt/local-date 2020 1 1) 20)
    (map (fn* [p1__75698#] (* (long p1__75698#) 120)) (range 20))
    :days),
   :value
   (map
    (fn* [p1__75699#] (+ 100 (* 50 (Math/sin (* p1__75699# 0.4)))))
    (range 20))}
  (pj/lay-line :date :value)
  pj/lay-point))


(deftest
 t91_l380
 (is
  ((fn
    [v]
    (let
     [s (pj/svg-summary v)]
     (and (= 20 (:points s)) (= 1 (:lines s)))))
   v90_l373)))


(def
 v93_l388
 (->
  {:cat (map (fn* [p1__75700#] (str "cat-" p1__75700#)) (range 12)),
   :val (repeatedly 12 (fn* [] (rand-int 100)))}
  (pj/lay-value-bar :cat :val)
  (pj/coord :polar)))


(deftest
 t94_l393
 (is ((fn [v] (pos? (:polygons (pj/svg-summary v)))) v93_l388)))


(def
 v96_l401
 (->
  {:x [1 10 100 1000], :y [2 4 8 16]}
  (pj/lay-point :x :y)
  (pj/scale :x :log)
  (pj/coord :flip)))


(deftest
 t97_l406
 (is
  ((fn
    [v]
    (let
     [plan (pj/plan v) panel (first (:panels plan))]
     (and
      (= 4 (:points (pj/svg-summary v)))
      (= :flip (:coord panel))
      (= {:type :log} (:y-scale panel))
      (= {:type :linear} (:x-scale panel)))))
   v96_l401)))


(def
 v99_l419
 (->
  (rdatasets/datasets-iris)
  (pj/lay-point :sepal-length :sepal-width)
  (pj/scale :y {:domain [0 6]})))


(deftest
 t100_l423
 (is
  ((fn
    [v]
    (let
     [plan (pj/plan v) panel (first (:panels plan))]
     (= [0 6] (:y-domain panel))))
   v99_l419)))


(def
 v102_l431
 (->
  {:x (range 100), :y (range 0 10 0.1)}
  (pj/lay-point :x :y)
  (pj/coord :fixed)))


(deftest
 t103_l435
 (is ((fn [v] (= 100 (:points (pj/svg-summary v)))) v102_l431)))


(def
 v105_l444
 (->
  (rdatasets/datasets-iris)
  (pj/pose {:color :species})
  (pj/pose
   (pj/cross
    [:sepal-length :sepal-width :petal-length]
    [:sepal-length :sepal-width :petal-length]))))


(deftest
 t106_l449
 (is
  ((fn
    [v]
    (let
     [s
      (pj/svg-summary v)
      texts
      (:texts s)
      col-label?
      (fn* [p1__75701#] (re-find #"sepal|petal" p1__75701#))]
     (and (= 9 (:panels s)) (seq (filter col-label? texts)))))
   v105_l444)))


(def
 v108_l460
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-point :nonexistent :y) pj/plot)
  (catch Exception e (ex-message e))))


(deftest t109_l467 (is ((fn [m] (string? m)) v108_l460)))


(def
 v111_l471
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (pj/lay-point :x :y {:color :bogus})
   pj/plot)
  (catch Exception e (ex-message e))))


(deftest t112_l478 (is ((fn [m] (string? m)) v111_l471)))


(def
 v114_l482
 (try
  (->
   {:x [1 2 3], :y [4 5 6]}
   (pj/lay-line :x :y)
   (pj/coord :polar)
   pj/plot)
  (catch Exception e (ex-message e))))


(deftest
 t115_l490
 (is ((fn [m] (re-find #"not supported with polar" m)) v114_l482)))


(def
 v117_l494
 (try
  (->
   {:x [1 2 3]}
   (pj/pose :x)
   (pj/lay {:mark :boxplot, :stat :bin})
   pj/plot)
  (catch Exception e (ex-message e))))


(deftest
 t118_l502
 (is ((fn [m] (re-find #"must contain :boxes" m)) v117_l494)))


(def
 v120_l511
 (try
  (-> {:x [1 2 3], :y [4 5 6]} (pj/lay-histogram :x :y))
  (catch clojure.lang.ExceptionInfo e (ex-message e))))


(deftest
 t121_l517
 (is
  ((fn [m] (re-find #"lay-histogram uses only the x column" m))
   v120_l511)))
