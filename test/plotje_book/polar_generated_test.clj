(ns
 plotje-book.polar-generated-test
 (:require
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.plotje.api :as sk]
  [clojure.test :refer [deftest is]]))


(def
 v2_l18
 (def
  wind
  {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"],
   :speed [12 8 15 10 7 13 9 11]}))


(def
 v4_l27
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :polar)))


(deftest
 t5_l31
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 150 (:points s)))))
   v4_l27)))


(def
 v7_l42
 (-> (rdatasets/datasets-iris) (sk/lay-bar :species) (sk/coord :polar)))


(deftest
 t8_l46
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 3 (:polygons s)))))
   v7_l42)))


(def
 v10_l55
 (-> wind (sk/lay-value-bar :direction :speed) (sk/coord :polar)))


(deftest
 t11_l59
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (= 8 (:polygons s)))))
   v10_l55)))


(def
 v13_l67
 (->
  (rdatasets/palmerpenguins-penguins)
  (sk/lay-bar :island {:position :stack, :color :species})
  (sk/coord :polar)))


(deftest
 t14_l71
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v13_l67)))


(def
 v16_l80
 (->
  (rdatasets/datasets-iris)
  (sk/lay-histogram :sepal-length)
  (sk/coord :polar)))


(deftest
 t17_l84
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (= 1 (:panels s)) (pos? (:polygons s)))))
   v16_l80)))


(def
 v19_l93
 (->
  (rdatasets/datasets-iris)
  (sk/lay-point :sepal-length :sepal-width {:color :species})
  (sk/coord :polar)
  (sk/options {:title "Iris in Polar Space"})))


(deftest
 t20_l98
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and
      (= 1 (:panels s))
      (some #{"Iris in Polar Space"} (:texts s)))))
   v19_l93)))
