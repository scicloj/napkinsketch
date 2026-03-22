(ns
 napkinsketch-book.evolution-generated-test
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
   (mapv (fn* [p1__87407#] (Math/sin (* p1__87407# 0.3))) (range 30))}))


(def v4_l21 (-> wave (sk/view [[:x :y]]) sk/lay-line sk/plot))


(deftest
 t5_l26
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v4_l21)))


(def
 v7_l34
 (def
  waves
  {:x (vec (concat (range 30) (range 30))),
   :y
   (vec
    (concat
     (mapv (fn* [p1__87408#] (Math/sin (* p1__87408# 0.3))) (range 30))
     (mapv
      (fn* [p1__87409#] (Math/cos (* p1__87409# 0.3)))
      (range 30)))),
   :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))}))


(def
 v8_l39
 (-> waves (sk/view [[:x :y]]) (sk/lay-line {:color :fn}) sk/plot))


(deftest
 t9_l44
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v8_l39)))


(def
 v11_l52
 (-> wave (sk/view [[:x :y]]) (sk/lay-line {:size 4}) sk/plot))


(deftest
 t12_l57
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v11_l52)))


(def
 v14_l65
 (def
  growth
  {:day [1 2 3 4 5 1 2 3 4 5],
   :value [10 15 13 18 22 8 12 11 16 19],
   :group [:a :a :a :a :a :b :b :b :b :b]}))


(def
 v15_l70
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay-line {:color :group})
  (sk/lay-point {:color :group})
  sk/plot))


(deftest
 t16_l76
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v15_l70)))


(def
 v18_l84
 (->
  {:x [1 2 3 4 5], :y [2 4 1 5 3]}
  (sk/view [[:x :y]])
  sk/lay-step
  sk/lay-point
  sk/plot))


(deftest
 t19_l91
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v18_l84)))


(def
 v21_l99
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay-step {:color :group})
  (sk/lay-point {:color :group})
  sk/plot))


(deftest
 t22_l105
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v21_l99)))


(def
 v24_l113
 (->
  {:x (range 30),
   :y
   (mapv (fn* [p1__87410#] (Math/sin (* p1__87410# 0.3))) (range 30))}
  (sk/view [[:x :y]])
  sk/lay-area
  sk/plot))


(deftest
 t25_l119
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l113)))


(def
 v27_l127
 (->
  {:x (vec (concat (range 10) (range 10) (range 10))),
   :y
   (vec
    (concat
     [1 2 3 4 5 4 3 2 1 0]
     [2 2 2 3 3 3 2 2 2 2]
     [1 1 1 1 2 2 2 1 1 1])),
   :group
   (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))}
  (sk/view [[:x :y]])
  (sk/lay-stacked-area {:color :group})
  sk/plot))


(deftest
 t28_l136
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v27_l127)))
