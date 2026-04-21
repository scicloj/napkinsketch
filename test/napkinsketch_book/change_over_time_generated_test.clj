(ns
 napkinsketch-book.change-over-time-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l18
 (def
  wave
  {:x (range 30),
   :y
   (map (fn* [p1__84500#] (Math/sin (* p1__84500# 0.3))) (range 30))}))


(def v4_l21 (-> wave (sk/lay-line :x :y)))


(deftest
 t5_l24
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v4_l21)))


(def
 v7_l32
 (def
  waves
  {:x (concat (range 30) (range 30)),
   :y
   (concat
    (map (fn* [p1__84501#] (Math/sin (* p1__84501# 0.3))) (range 30))
    (map (fn* [p1__84502#] (Math/cos (* p1__84502# 0.3))) (range 30))),
   :fn (concat (repeat 30 :sin) (repeat 30 :cos))}))


(def v8_l37 (-> waves (sk/lay-line :x :y {:color :fn})))


(deftest
 t9_l40
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v8_l37)))


(def v11_l48 (-> wave (sk/lay-line :x :y {:size 4})))


(deftest
 t12_l51
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v11_l48)))


(def
 v14_l59
 (def
  growth
  {:day [1 2 3 4 5 1 2 3 4 5],
   :value [10 15 13 18 22 8 12 11 16 19],
   :group [:a :a :a :a :a :b :b :b :b :b]}))


(def
 v15_l64
 (->
  growth
  (sk/view :day :value {:color :group})
  sk/lay-line
  sk/lay-point))


(deftest
 t16_l69
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v15_l64)))


(def
 v18_l77
 (-> {:x [1 2 3 4 5], :y [2 4 1 5 3]} (sk/lay-step :x :y) sk/lay-point))


(deftest
 t19_l82
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v18_l77)))


(def
 v21_l90
 (->
  growth
  (sk/view :day :value {:color :group})
  sk/lay-step
  sk/lay-point))


(deftest
 t22_l95
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v21_l90)))


(def
 v24_l103
 (->
  {:x (range 30),
   :y
   (map (fn* [p1__84503#] (Math/sin (* p1__84503# 0.3))) (range 30))}
  (sk/lay-area :x :y)))


(deftest
 t25_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l103)))


(def
 v27_l115
 (->
  {:x (concat (range 10) (range 10) (range 10)),
   :y
   (concat
    [1 2 3 4 5 4 3 2 1 0]
    [2 2 2 3 3 3 2 2 2 2]
    [1 1 1 1 2 2 2 1 1 1]),
   :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
  (sk/lay-stacked-area :x :y {:color :group})))


(deftest
 t28_l122
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v27_l115)))


(def
 v30_l134
 (->
  {:date
   [#inst "2024-01-01T00:00:00.000-00:00"
    #inst "2024-02-01T00:00:00.000-00:00"
    #inst "2024-03-01T00:00:00.000-00:00"
    #inst "2024-04-01T00:00:00.000-00:00"
    #inst "2024-05-01T00:00:00.000-00:00"
    #inst "2024-06-01T00:00:00.000-00:00"],
   :temperature [3 5 9 14 19 23]}
  (sk/lay-line :date :temperature)
  sk/lay-point))


(deftest
 t31_l140
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 6 (:points s)) (= 1 (:lines s)))))
   v30_l134)))


(def
 v33_l150
 (def
  months
  [#inst "2024-01-01T00:00:00.000-00:00"
   #inst "2024-02-01T00:00:00.000-00:00"
   #inst "2024-03-01T00:00:00.000-00:00"
   #inst "2024-04-01T00:00:00.000-00:00"
   #inst "2024-05-01T00:00:00.000-00:00"
   #inst "2024-06-01T00:00:00.000-00:00"]))


(def
 v34_l154
 (->
  {:date (concat months months),
   :temperature [3 5 9 14 19 23 15 17 19 22 25 28],
   :city (concat (repeat 6 "Zurich") (repeat 6 "Athens"))}
  (sk/lay-line :date :temperature {:color :city})
  sk/lay-point))


(deftest
 t35_l162
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 12 (:points s)) (= 2 (:lines s)))))
   v34_l154)))
