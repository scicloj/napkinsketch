(ns
 napkinsketch-book.evolution-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l15
 (def
  wave
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__80208#] (Math/sin (* p1__80208# 0.3)))
     (range 30))})))


(def v4_l18 (-> wave (sk/view [[:x :y]]) (sk/lay (sk/line)) sk/plot))


(deftest
 t5_l23
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v4_l18)))


(def
 v7_l31
 (def
  waves
  (tc/dataset
   {:x (vec (concat (range 30) (range 30))),
    :y
    (vec
     (concat
      (mapv
       (fn* [p1__80209#] (Math/sin (* p1__80209# 0.3)))
       (range 30))
      (mapv
       (fn* [p1__80210#] (Math/cos (* p1__80210# 0.3)))
       (range 30)))),
    :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})))


(def
 v8_l36
 (-> waves (sk/view [[:x :y]]) (sk/lay (sk/line {:color :fn})) sk/plot))


(deftest
 t9_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 2 (:lines s)))))
   v8_l36)))


(def
 v11_l49
 (-> wave (sk/view [[:x :y]]) (sk/lay (sk/line {:size 4})) sk/plot))


(deftest
 t12_l54
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:lines s)))))
   v11_l49)))


(def
 v14_l62
 (def
  growth
  (tc/dataset
   {:day [1 2 3 4 5 1 2 3 4 5],
    :value [10 15 13 18 22 8 12 11 16 19],
    :group [:a :a :a :a :a :b :b :b :b :b]})))


(def
 v15_l67
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay (sk/line {:color :group}) (sk/point {:color :group}))
  sk/plot))


(deftest
 t16_l73
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v15_l67)))


(def
 v18_l81
 (->
  {:x [1 2 3 4 5], :y [2 4 1 5 3]}
  (sk/view [[:x :y]])
  (sk/lay (sk/step) (sk/point))
  sk/plot))


(deftest
 t19_l87
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 5 (:points s)) (= 1 (:lines s)))))
   v18_l81)))


(def
 v21_l95
 (->
  growth
  (sk/view [[:day :value]])
  (sk/lay (sk/step {:color :group}) (sk/point {:color :group}))
  sk/plot))


(deftest
 t22_l101
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 10 (:points s)) (= 2 (:lines s)))))
   v21_l95)))


(def
 v24_l109
 (->
  (tc/dataset
   {:x (range 30),
    :y
    (mapv
     (fn* [p1__80211#] (Math/sin (* p1__80211# 0.3)))
     (range 30))})
  (sk/view [[:x :y]])
  (sk/lay (sk/area))
  sk/plot))


(deftest
 t25_l115
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 1 (:polygons s)))))
   v24_l109)))


(def
 v27_l123
 (->
  (tc/dataset
   {:x (vec (concat (range 10) (range 10) (range 10))),
    :y
    (vec
     (concat
      [1 2 3 4 5 4 3 2 1 0]
      [2 2 2 3 3 3 2 2 2 2]
      [1 1 1 1 2 2 2 1 1 1])),
    :group
    (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))})
  (sk/view [[:x :y]])
  (sk/lay (sk/stacked-area {:color :group}))
  sk/plot))


(deftest
 t28_l132
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v27_l123)))
