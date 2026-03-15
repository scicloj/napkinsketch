(ns
 napkinsketch-book.ranking-generated-test
 (:require
  [tablecloth.api :as tc]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l11
 (def
  iris
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
   {:key-fn keyword})))


(def
 v3_l14
 (def
  tips
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
   {:key-fn keyword})))


(def
 v4_l17
 (def
  penguins
  (tc/dataset
   "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
   {:key-fn keyword})))


(def
 v5_l20
 (def
  sales
  (tc/dataset
   {:product [:widget :gadget :gizmo :doohickey],
    :revenue [120 340 210 95]})))


(def v7_l27 (-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot))


(deftest
 t8_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v7_l27)))


(def
 v10_l41
 (-> tips (sk/view :day) (sk/lay (sk/bar {:color :smoker})) sk/plot))


(deftest
 t11_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v10_l41)))


(def
 v13_l55
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/stacked-bar {:color :smoker}))
  sk/plot))


(deftest
 t14_l60
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l55)))


(def
 v16_l69
 (->
  penguins
  (sk/view :island)
  (sk/lay (sk/stacked-bar-fill {:color :species}))
  sk/plot))


(deftest
 t17_l74
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l69)))


(def
 v19_l82
 (->
  iris
  (sk/view :species)
  (sk/lay (sk/bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t20_l88
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v19_l82)))


(def
 v22_l97
 (->
  tips
  (sk/view :day)
  (sk/lay (sk/bar {:color :time}))
  (sk/coord :flip)
  sk/plot))


(deftest
 t23_l103
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v22_l97)))


(def
 v25_l112
 (sk/plot [(sk/value-bar {:data sales, :x :product, :y :revenue})]))


(deftest
 t26_l114
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v25_l112)))


(def
 v28_l122
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/value-bar))
  (sk/coord :flip)
  sk/plot))


(deftest
 t29_l128
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 4 (:polygons s)))))
   v28_l122)))


(def
 v31_l136
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  sk/plot))


(deftest
 t32_l141
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v31_l136)))


(def
 v34_l149
 (->
  sales
  (sk/view [[:product :revenue]])
  (sk/lay (sk/lollipop))
  (sk/coord :flip)
  sk/plot))


(deftest
 t35_l155
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:points s)) (= 4 (:lines s)))))
   v34_l149)))
