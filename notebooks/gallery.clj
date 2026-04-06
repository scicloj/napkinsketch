;; # Gallery
;;
;; Reproducing examples from the
;; [R Graph Gallery](https://r-graph-gallery.com/) using napkinsketch.
;; Each section corresponds to a chart type. Examples use datasets from
;; the [RDatasets](https://vincentarelbundock.github.io/Rdatasets/)
;; collection via `scicloj.metamorph.ml.rdatasets`.

(ns gallery
  (:require
   [scicloj.napkinsketch.api :as sk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   [tablecloth.api :as tc]))

;; ## Datasets

(def mpg (rdatasets/ggplot2-mpg))

;; ## Scatter

;; Colored scatter with LOESS smoothing — one curve per vehicle class:

(-> mpg
    (sk/view :displ :hwy {:color :class})
    sk/lay-point
    sk/lay-loess
    (sk/options {:title "Fuel Efficiency by Engine Size"
                 :x-label "Engine Displacement (L)"
                 :y-label "Highway MPG"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; Load remaining datasets used throughout the gallery:

(def diamonds (rdatasets/ggplot2-diamonds))

(def tips (rdatasets/reshape2-tips))

(def mtcars (rdatasets/datasets-mtcars))

(def economics (rdatasets/ggplot2-economics))

(def iris (rdatasets/datasets-iris))

(def gapminder (rdatasets/gapminder-gapminder))

;; ### Bubble chart

;; Mapping a third variable to point size produces a bubble chart.
;; Here, diamond depth controls bubble size:

(-> diamonds
    (tc/select-rows (range 500))
    (sk/view :carat :price {:color :cut :size :depth})
    sk/lay-point
    (sk/options {:title "Diamond Price vs Carat (bubble)"
                 :x-label "Carat"
                 :y-label "Price (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 500 (:points s))))])

;; ### Scatter by category

;; Categorical x-axis with points — useful for comparing groups:

(-> tips
    (sk/view :day :total-bill {:color :sex})
    sk/lay-point
    (sk/options {:title "Total Bill by Day"
                 :x-label "Day"
                 :y-label "Total Bill (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 244 (:points s))))])

;; ## Distributions

;; ### Histogram

;; Basic histogram of diamond prices:

(-> diamonds
    (sk/view :price)
    sk/lay-histogram
    (sk/options {:title "Distribution of Diamond Prices"
                 :x-label "Price (USD)"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; Colored histogram by cut quality:

(-> diamonds
    (sk/view :price {:color :cut})
    sk/lay-histogram
    (sk/options {:title "Diamond Prices by Cut"
                 :x-label "Price (USD)"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (< 1 (:polygons s))))])

;; ### Density

;; Overlaid density curves for carat weight by cut:

(-> diamonds
    (sk/view :carat {:color :cut})
    sk/lay-density
    (sk/options {:title "Carat Distribution by Cut"
                 :x-label "Carat"
                 :y-label "Density"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; Density with rug marks showing individual observations:

(-> diamonds
    (tc/select-rows (range 500))
    (sk/view :carat)
    sk/lay-density
    sk/lay-rug
    (sk/options {:title "Carat Distribution with Rug"
                 :x-label "Carat"
                 :y-label "Density"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:polygons s))
                                (pos? (:lines s)))))])

;; ### Boxplot

;; Grouped boxplot of restaurant tips by day:

(-> tips
    (sk/view :day :total-bill {:color :day})
    sk/lay-boxplot
    (sk/options {:title "Total Bill by Day"
                 :x-label "Day"
                 :y-label "Total Bill (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; Boxplot with individual points overlaid:

(-> tips
    (sk/view :day :total-bill)
    sk/lay-boxplot
    sk/lay-point
    (sk/options {:title "Total Bill by Day (box + points)"
                 :x-label "Day"
                 :y-label "Total Bill (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:polygons s))
                                (pos? (:points s)))))])

;; ### Violin

;; Violin plots show the full distribution shape:

(-> tips
    (sk/view :day :total-bill {:color :day})
    sk/lay-violin
    (sk/options {:title "Total Bill by Day (violin)"
                 :x-label "Day"
                 :y-label "Total Bill (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; Violin with embedded boxplot for summary statistics:

(-> tips
    (sk/view :day :total-bill {:color :day})
    sk/lay-violin
    sk/lay-boxplot
    (sk/options {:title "Total Bill Distribution by Day"
                 :x-label "Day"
                 :y-label "Total Bill (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; ### Ridgeline

;; Ridgeline plots stack density curves vertically by category:

(-> diamonds
    (sk/view :cut :price)
    sk/lay-ridgeline
    (sk/options {:title "Price Distribution by Cut (ridgeline)"
                 :x-label "Cut"
                 :y-label "Price (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; ## Ranking

;; ### Bar chart

;; Count of diamonds by cut quality:

(-> diamonds
    (sk/view :cut)
    sk/lay-bar
    (sk/options {:title "Diamond Count by Cut"
                 :x-label "Cut"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:polygons s))))])

;; ### Horizontal bar

;; Flip coordinates for horizontal bars:

(-> diamonds
    (sk/view :cut)
    sk/lay-bar
    (sk/coord :flip)
    (sk/options {:title "Diamond Count by Cut (horizontal)"
                 :x-label "Cut"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:polygons s))))])

;; ### Lollipop

;; Lollipop chart of the top manufacturers by model count:

(def mpg-mfr-counts
  (-> mpg
      (tc/group-by [:manufacturer])
      (tc/aggregate {:count tc/row-count})
      (tc/order-by [:count] :desc)
      (tc/select-rows (range 8))))

(-> mpg-mfr-counts
    (sk/view :manufacturer :count)
    sk/lay-lollipop
    (sk/options {:title "Top Manufacturers by Model Count"
                 :x-label "Manufacturer"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; Horizontal lollipop:

(-> mpg-mfr-counts
    (sk/view :manufacturer :count)
    sk/lay-lollipop
    (sk/coord :flip)
    (sk/options {:title "Top Manufacturers (horizontal lollipop)"
                 :x-label "Manufacturer"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ## Evolution

;; ### Line chart

;; US unemployment over time from the economics dataset:

(-> economics
    (sk/view :date :unemploy)
    sk/lay-line
    (sk/options {:title "US Unemployment Over Time"
                 :x-label "Date"
                 :y-label "Unemployed (thousands)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

;; Multi-series line chart — life expectancy for selected countries:

(def gapminder-subset
  (tc/select-rows gapminder
                  #(#{"Japan" "Brazil" "Germany" "Nigeria" "Australia"}
                    (:country %))))

(-> gapminder-subset
    (sk/view :year :life-exp {:color :country})
    sk/lay-line
    sk/lay-point
    (sk/options {:title "Life Expectancy Over Time"
                 :x-label "Year"
                 :y-label "Life Expectancy"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:lines s))
                                (pos? (:points s)))))])

;; ### Area chart

;; Filled area chart for unemployment:

(-> economics
    (sk/view :date :unemploy)
    sk/lay-area
    (sk/options {:title "US Unemployment Over Time (area)"
                 :x-label "Date"
                 :y-label "Unemployed (thousands)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

;; ## Relationships

;; ### Heatmap (density2d)

;; Two-dimensional density estimate for diamond carat vs price:

(-> diamonds
    (tc/select-rows (range 2000))
    (sk/view :carat :price)
    sk/lay-density2d
    (sk/options {:title "Diamond Carat vs Price (density)"
                 :x-label "Carat"
                 :y-label "Price (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

;; ### Scatter with regression

;; Linear regression lines overlaid on a scatter plot:

(-> tips
    (sk/view :total-bill :tip {:color :sex})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Tip vs Total Bill (with regression)"
                 :x-label "Total Bill (USD)"
                 :y-label "Tip (USD)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Contour

;; Contour lines on iris sepal dimensions, colored by species:

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/lay-contour
    (sk/options {:title "Iris Sepal Dimensions (contour)"
                 :x-label "Sepal Length"
                 :y-label "Sepal Width"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ## Multi-panel

;; ### Faceted scatter

;; Scatter plot of engine size vs highway MPG, faceted by drive type:

(-> mpg
    (sk/view :displ :hwy {:color :class})
    sk/lay-point
    (sk/facet-grid :drv nil)
    (sk/options {:title "Highway MPG by Engine Size, faceted by Drive"
                 :x-label "Displacement"
                 :y-label "Highway MPG"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; Faceted histogram — highway MPG distribution by drive type:

(-> mpg
    (sk/view :hwy)
    sk/lay-histogram
    (sk/facet-grid :drv nil)
    (sk/options {:title "Highway MPG by Drive Type"
                 :x-label "Highway MPG"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ### SPLOM (scatter plot matrix)

;; All pairwise combinations of iris measurements. Diagonal panels
;; show histograms; off-diagonal panels show scatter plots:

(def iris-cols [:sepal-length :sepal-width :petal-length :petal-width])

(-> iris
    (sk/view (sk/cross iris-cols iris-cols) {:color :species})
    (sk/options {:title "Iris SPLOM"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 16 (:panels s))))])

;; ## Composition

;; ### Stacked bar

;; Counts by day, stacked by sex:

(-> tips
    (sk/view :day {:color :sex})
    sk/lay-stacked-bar
    (sk/options {:title "Tips by Day and Sex (stacked bar)"
                 :x-label "Day"
                 :y-label "Count"}))

(kind/test-last [(fn [v] (sk/sketch? v))])

;; Proportional stacked bar (stacked fill):

(-> tips
    (sk/view :day {:color :sex})
    sk/lay-stacked-bar-fill
    (sk/options {:title "Proportion by Day and Sex"
                 :x-label "Day"
                 :y-label "Proportion"}))

(kind/test-last [(fn [v] (sk/sketch? v))])

;; ### Stacked area

;; World population by continent over time:

(def continent-pop
  (-> gapminder
      (tc/group-by [:year :continent])
      (tc/aggregate {:pop (fn [ds] (reduce + (ds :pop)))})
      (tc/order-by [:year :continent])))

(-> continent-pop
    (sk/view :year :pop {:color :continent})
    sk/lay-stacked-area
    (sk/options {:title "World Population by Continent"
                 :x-label "Year"
                 :y-label "Population"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:polygons s))))])

;; ## Polar

;; ### Rose chart

;; Bar chart in polar coordinates produces a rose (coxcomb) chart:

(-> diamonds
    (sk/view :cut)
    sk/lay-bar
    (sk/coord :polar)
    (sk/options {:title "Diamond Cut (rose chart)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:polygons s))))])
