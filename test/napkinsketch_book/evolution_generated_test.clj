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
   (mapv (fn* [p1__86109#] (Math/sin (* p1__86109# 0.3))) (range 30))}))


(def v4_l21 (-> wave (sk/view [[:x :y]]) sk/lay-line))


(deftest
 t5_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v4_l21)))


(def
 v7_l33
 (def
  waves
  {:x (vec (concat (range 30) (range 30))),
   :y
   (vec
    (concat
     (mapv (fn* [p1__86110#] (Math/sin (* p1__86110# 0.3))) (range 30))
     (mapv
      (fn* [p1__86111#] (Math/cos (* p1__86111# 0.3)))
      (range 30)))),
   :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))}))


(def v8_l38 (-> waves (sk/view [[:x :y]]) (sk/lay-line {:color :fn})))


(deftest
 t9_l42
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v8_l38)))


(def v11_l50 (-> wave (sk/view [[:x :y]]) (sk/lay-line {:size 4})))


(deftest
 t12_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v11_l50)))


(def
 v14_l62
 (def
  growth
  {:day [1 2 3 4 5 1 2 3 4 5],
   :value [10 15 13 18 22 8 12 11 16 19],
   :group [:a :a :a :a :a :b :b :b :b :b]}))


(def
 v15_l67
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay-line {:color :group})
  (sk/lay-point {:color :group})))


(deftest
 t16_l72
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v15_l67)))


(def
 v18_l80
 (->
  {:x [1 2 3 4 5], :y [2 4 1 5 3]}
  (sk/view [[:x :y]])
  sk/lay-step
  sk/lay-point))


(deftest
 t19_l86
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v18_l80)))


(def
 v21_l94
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay-step {:color :group})
  (sk/lay-point {:color :group})))


(deftest
 t22_l99
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v21_l94)))


(def
 v24_l107
 (->
  {:x (range 30),
   :y
   (mapv (fn* [p1__86112#] (Math/sin (* p1__86112# 0.3))) (range 30))}
  (sk/view [[:x :y]])
  sk/lay-area))


(deftest
 t25_l112
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l107)))


(def
 v27_l120
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
  (sk/lay-stacked-area {:color :group})))


(deftest
 t28_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v27_l120)))
