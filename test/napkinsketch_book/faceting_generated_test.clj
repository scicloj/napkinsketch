(ns
 napkinsketch-book.faceting-generated-test
 (:require
  [napkinsketch-book.datasets :as data]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.napkinsketch.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v3_l21
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)))


(deftest
 t4_l25
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v3_l21)))


(def
 v6_l37
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species :col)))


(deftest
 t7_l41
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v6_l37)))


(def
 v9_l49
 (->
  data/tips
  (sk/xkcd7-lay-point :total_bill :tip {:color :sex})
  (sk/xkcd7-facet-grid :smoker :sex)))


(deftest
 t10_l53
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)))))
   v9_l49)))


(def
 v12_l61
 (->
  data/iris
  (sk/xkcd7-lay-histogram :sepal_length {:color :species})
  (sk/xkcd7-facet :species)))


(deftest
 t13_l65
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v12_l61)))


(def
 v15_l73
 (->
  data/tips
  (sk/xkcd7-view :total_bill :tip {:color :sex})
  sk/xkcd7-lay-point
  sk/xkcd7-lay-lm
  (sk/xkcd7-facet-grid :smoker :sex)))


(deftest
 t16_l79
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 4 (:panels s)) (= 244 (:points s)) (= 4 (:lines s)))))
   v15_l73)))


(def
 v18_l91
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)
  (sk/xkcd7-options {:scales :shared})))


(deftest
 t19_l96
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v18_l91)))


(def
 v21_l102
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)
  (sk/xkcd7-options {:scales :free-y})))


(deftest
 t22_l107
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 150 (:points s)))))
   v21_l102)))


(def
 v24_l117
 (def
  faceted-pl
  (->
   data/iris
   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
   (sk/xkcd7-facet :species)
   sk/xkcd7-plan)))


(def v25_l123 (:grid faceted-pl))


(deftest
 t26_l125
 (is ((fn [g] (and (= 1 (:rows g)) (= 3 (:cols g)))) v25_l123)))


(def v27_l127 (count (:panels faceted-pl)))


(deftest t28_l129 (is ((fn [n] (= 3 n)) v27_l127)))


(def v30_l133 (:panels faceted-pl))


(deftest t31_l135 (is ((fn [ps] (= 3 (count ps))) v30_l133)))


(def
 v33_l142
 (def cols [:sepal_length :sepal_width :petal_length :petal_width]))


(def
 v34_l144
 (-> data/iris (sk/xkcd7-view (sk/cross cols cols) {:color :species})))


(deftest
 t35_l147
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 16 (:panels s))
      (= (* 12 150) (:points s))
      (pos? (:polygons s)))))
   v34_l144)))


(def
 v37_l160
 (sk/xkcd7-lay-histogram
  data/iris
  [:sepal_length :sepal_width :petal_length]
  {:color :species}))


(deftest
 t38_l162
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (pos? (:polygons s)))))
   v37_l160)))


(def
 v40_l168
 (->
  data/penguins
  (sk/xkcd7-lay-bar :species {:color :species})
  (sk/xkcd7-facet :island)))


(deftest
 t41_l172
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 3 (:panels s)) (= 5 (:polygons s)))))
   v40_l168)))


(def
 v43_l180
 (->
  data/iris
  (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
  (sk/xkcd7-facet :species)
  (sk/xkcd7-options
   {:title "Iris by Species",
    :x-label "Sepal Length (cm)",
    :y-label "Sepal Width (cm)"})))


(deftest
 t44_l186
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 3 (:panels s))
      (= 150 (:points s))
      (some #{"Iris by Species"} (:texts s))
      (some #{"Sepal Length (cm)"} (:texts s)))))
   v43_l180)))
