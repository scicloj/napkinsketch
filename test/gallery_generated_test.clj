(ns
 gallery-generated-test
 (:require
  [scicloj.napkinsketch.api :as sk]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.metamorph.ml.rdatasets :as rdatasets]
  [tablecloth.api :as tc]
  [clojure.test :refer [deftest is]]))


(def v3_l18 (def mpg (rdatasets/ggplot2-mpg)))


(def
 v5_l24
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
 t6_l32
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v5_l24)))


(def v8_l38 (def diamonds (rdatasets/ggplot2-diamonds)))


(def v9_l40 (def tips (rdatasets/reshape2-tips)))


(def v10_l42 (def mtcars (rdatasets/datasets-mtcars)))


(def v11_l44 (def economics (rdatasets/ggplot2-economics)))


(def v12_l46 (def iris (rdatasets/datasets-iris)))


(def v13_l48 (def gapminder (rdatasets/gapminder-gapminder)))


(def
 v15_l55
 (->
  diamonds
  (tc/select-rows (range 500))
  (sk/view :carat :price {:color :cut, :size :depth})
  sk/lay-point
  (sk/options
   {:title "Diamond Price vs Carat (bubble)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t16_l63
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 500 (:points s)))) v15_l55)))


(def
 v18_l70
 (->
  tips
  (sk/view :day :total-bill {:color :sex})
  sk/lay-point
  (sk/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t19_l77
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 244 (:points s)))) v18_l70)))


(def
 v21_l86
 (->
  diamonds
  (sk/view :price)
  sk/lay-histogram
  (sk/options
   {:title "Distribution of Diamond Prices",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t22_l93
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s)))) v21_l86)))


(def
 v24_l98
 (->
  diamonds
  (sk/view :price {:color :cut})
  sk/lay-histogram
  (sk/options
   {:title "Diamond Prices by Cut",
    :x-label "Price (USD)",
    :y-label "Count"})))


(deftest
 t25_l105
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (< 1 (:polygons s)))) v24_l98)))


(def
 v27_l112
 (->
  diamonds
  (sk/view :carat {:color :cut})
  sk/lay-density
  (sk/options
   {:title "Carat Distribution by Cut",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t28_l119
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v27_l112)))


(def
 v30_l124
 (->
  diamonds
  (tc/select-rows (range 500))
  (sk/view :carat)
  sk/lay-density
  sk/lay-rug
  (sk/options
   {:title "Carat Distribution with Rug",
    :x-label "Carat",
    :y-label "Density"})))


(deftest
 t31_l133
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:lines s)))))
   v30_l124)))


(def
 v33_l141
 (->
  tips
  (sk/view :day :total-bill {:color :day})
  sk/lay-boxplot
  (sk/options
   {:title "Total Bill by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t34_l148
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v33_l141)))


(def
 v36_l153
 (->
  tips
  (sk/view :day :total-bill)
  sk/lay-boxplot
  sk/lay-point
  (sk/options
   {:title "Total Bill by Day (box + points)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t37_l161
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:polygons s)) (pos? (:points s)))))
   v36_l153)))


(def
 v39_l169
 (->
  tips
  (sk/view :day :total-bill {:color :day})
  sk/lay-violin
  (sk/options
   {:title "Total Bill by Day (violin)",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t40_l176
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v39_l169)))


(def
 v42_l181
 (->
  tips
  (sk/view :day :total-bill {:color :day})
  sk/lay-violin
  sk/lay-boxplot
  (sk/options
   {:title "Total Bill Distribution by Day",
    :x-label "Day",
    :y-label "Total Bill (USD)"})))


(deftest
 t43_l189
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v42_l181)))


(def
 v45_l196
 (->
  diamonds
  (sk/view :cut :price)
  sk/lay-ridgeline
  (sk/options
   {:title "Price Distribution by Cut (ridgeline)",
    :x-label "Cut",
    :y-label "Price (USD)"})))


(deftest
 t46_l203
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:polygons s))))
   v45_l196)))


(def
 v48_l212
 (->
  diamonds
  (sk/view :cut)
  sk/lay-bar
  (sk/options
   {:title "Diamond Count by Cut", :x-label "Cut", :y-label "Count"})))


(deftest
 t49_l219
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v48_l212)))


(def
 v51_l226
 (->
  diamonds
  (sk/view :cut)
  sk/lay-bar
  (sk/coord :flip)
  (sk/options
   {:title "Diamond Count by Cut (horizontal)",
    :x-label "Cut",
    :y-label "Count"})))


(deftest
 t52_l234
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v51_l226)))


(def
 v54_l241
 (def
  mpg-mfr-counts
  (->
   mpg
   (tc/group-by [:manufacturer])
   (tc/aggregate {:count tc/row-count})
   (tc/order-by [:count] :desc)
   (tc/select-rows (range 8)))))


(def
 v55_l248
 (->
  mpg-mfr-counts
  (sk/view :manufacturer :count)
  sk/lay-lollipop
  (sk/options
   {:title "Top Manufacturers by Model Count",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t56_l255
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v55_l248)))


(def
 v58_l261
 (->
  mpg-mfr-counts
  (sk/view :manufacturer :count)
  sk/lay-lollipop
  (sk/coord :flip)
  (sk/options
   {:title "Top Manufacturers (horizontal lollipop)",
    :x-label "Manufacturer",
    :y-label "Count"})))


(deftest
 t59_l269
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v58_l261)))


(def
 v61_l279
 (->
  economics
  (sk/view :date :unemploy)
  sk/lay-line
  (sk/options
   {:title "US Unemployment Over Time",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t62_l286
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:lines s)))) v61_l279)))


(def
 v64_l291
 (def
  gapminder-subset
  (tc/select-rows
   gapminder
   (fn*
    [p1__765374#]
    (#{"Australia" "Brazil" "Japan" "Nigeria" "Germany"}
     (:country p1__765374#))))))


(def
 v65_l296
 (->
  gapminder-subset
  (sk/view :year :life-exp {:color :country})
  sk/lay-line
  sk/lay-point
  (sk/options
   {:title "Life Expectancy Over Time",
    :x-label "Year",
    :y-label "Life Expectancy"})))


(deftest
 t66_l304
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:lines s)) (pos? (:points s)))))
   v65_l296)))


(def
 v68_l312
 (->
  economics
  (sk/view :date :unemploy)
  sk/lay-area
  (sk/options
   {:title "US Unemployment Over Time (area)",
    :x-label "Date",
    :y-label "Unemployed (thousands)"})))


(deftest
 t69_l319
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 1 (:polygons s)))) v68_l312)))


(def
 v71_l328
 (->
  diamonds
  (tc/select-rows (range 2000))
  (sk/view :carat :price)
  sk/lay-density2d
  (sk/options
   {:title "Diamond Carat vs Price (density)",
    :x-label "Carat",
    :y-label "Price (USD)"})))


(deftest
 t72_l336
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (pos? (:visible-tiles s))))
   v71_l328)))


(def
 v74_l343
 (->
  tips
  (sk/view :total-bill :tip {:color :sex})
  sk/lay-point
  sk/lay-lm
  (sk/options
   {:title "Tip vs Total Bill (with regression)",
    :x-label "Total Bill (USD)",
    :y-label "Tip (USD)"})))


(deftest
 t75_l351
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v74_l343)))


(def
 v77_l359
 (->
  iris
  (sk/view :sepal-length :sepal-width {:color :species})
  sk/lay-point
  sk/lay-contour
  (sk/options
   {:title "Iris Sepal Dimensions (contour)",
    :x-label "Sepal Length",
    :y-label "Sepal Width"})))


(deftest
 t78_l367
 (is
  ((fn
    [v]
    (let
     [s (sk/svg-summary v)]
     (and (pos? (:points s)) (pos? (:lines s)))))
   v77_l359)))


(def
 v80_l377
 (->
  mpg
  (sk/view :displ :hwy {:color :class})
  sk/lay-point
  (sk/facet-grid :drv nil)
  (sk/options
   {:title "Highway MPG by Engine Size, faceted by Drive",
    :x-label "Displacement",
    :y-label "Highway MPG"})))


(deftest
 t81_l385
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v80_l377)))


(def
 v83_l390
 (->
  mpg
  (sk/view :hwy)
  sk/lay-histogram
  (sk/facet-grid :drv nil)
  (sk/options
   {:title "Highway MPG by Drive Type",
    :x-label "Highway MPG",
    :y-label "Count"})))


(deftest
 t84_l398
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 3 (:panels s)))) v83_l390)))


(def
 v86_l406
 (def
  iris-cols
  [:sepal-length :sepal-width :petal-length :petal-width]))


(def
 v87_l408
 (->
  iris
  (sk/view (sk/cross iris-cols iris-cols) {:color :species})
  (sk/options {:title "Iris SPLOM"})))


(deftest
 t88_l412
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 16 (:panels s)))) v87_l408)))


(def
 v90_l421
 (->
  tips
  (sk/view :day {:color :sex})
  sk/lay-stacked-bar
  (sk/options
   {:title "Tips by Day and Sex (stacked bar)",
    :x-label "Day",
    :y-label "Count"})))


(deftest t91_l428 (is ((fn [v] (sk/sketch? v)) v90_l421)))


(def
 v93_l432
 (->
  tips
  (sk/view :day {:color :sex})
  sk/lay-stacked-bar-fill
  (sk/options
   {:title "Proportion by Day and Sex",
    :x-label "Day",
    :y-label "Proportion"})))


(deftest t94_l439 (is ((fn [v] (sk/sketch? v)) v93_l432)))


(def
 v96_l445
 (def
  continent-pop
  (->
   gapminder
   (tc/group-by [:year :continent])
   (tc/aggregate {:pop (fn [ds] (reduce + (ds :pop)))})
   (tc/order-by [:year :continent]))))


(def
 v97_l451
 (->
  continent-pop
  (sk/view :year :pop {:color :continent})
  sk/lay-stacked-area
  (sk/options
   {:title "World Population by Continent",
    :x-label "Year",
    :y-label "Population"})))


(deftest
 t98_l458
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s)))) v97_l451)))


(def
 v100_l467
 (->
  diamonds
  (sk/view :cut)
  sk/lay-bar
  (sk/coord :polar)
  (sk/options {:title "Diamond Cut (rose chart)"})))


(deftest
 t101_l473
 (is
  ((fn [v] (let [s (sk/svg-summary v)] (= 5 (:polygons s))))
   v100_l467)))
