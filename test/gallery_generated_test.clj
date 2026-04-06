(ns
 gallery-generated-test
 (:require
  [scicloj.napkinsketch.api :as sk]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [tablecloth.api :as tc]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (def mpg (rdatasets/ggplot2-mpg)))


(def
 v5_l30
 (->
  mpg
  (sk/view :displ :hwy {:color :class})
  sk/lay-point
  sk/lay-loess
  (sk/options
   {:title "Fuel Efficiency by Engine Size",
    :x-label "Engine Displacement (L)",
    :y-label "Highway MPG"})))


(deftest
 t6_l38
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v5_l30)))


(def v8_l44 (def diamonds (rdatasets/ggplot2-diamonds)))


(def v9_l46 (def tips (rdatasets/reshape2-tips)))


(def v10_l48 (def mtcars (rdatasets/datasets-mtcars)))


(def v11_l50 (def economics (rdatasets/ggplot2-economics)))


(def v12_l52 (def iris (rdatasets/datasets-iris)))


(def v13_l54 (def gapminder (rdatasets/gapminder-gapminder)))


(def
 v15_l61
 (->
  diamonds
  (tc/select-rows (range 500))
  (sk/view :carat :price {:color :cut, :size :depth})
  sk/lay-point
  (sk/options
   {:title "Diamond Price vs Carat (bubble)",
    :x-label "Carat",
    :y-label "Price (USD)"})))
