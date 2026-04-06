;; # Gallery
;;
;; Reproducing examples from mainstream visualization galleries:
;;
;; - [R Graph Gallery](https://r-graph-gallery.com/)
;; - [Vega-Lite Examples](https://vega.github.io/vega-lite/examples/)
;; - [Python Graph Gallery](https://python-graph-gallery.com/)
;; - [ECharts Examples](https://echarts.apache.org/examples/)
;; - [D3 Graph Gallery](https://d3-graph-gallery.com/)
;;
;; Each example includes a source URL. Datasets come from
;; [RDatasets](https://vincentarelbundock.github.io/Rdatasets/) via
;; `scicloj.metamorph.ml.rdatasets`.

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

;; ---
;; ## Additional Examples from Visualization Galleries

;; ### Scatter with text labels
;; Source: [Vega-Lite: Text Scatterplot](https://vega.github.io/vega-lite/examples/text_scatterplot_colored.html)

(def cars (rdatasets/datasets-mtcars))

(-> cars
    (sk/view :wt :mpg)
    sk/lay-point
    (sk/lay-text {:text :rownames})
    (sk/options {:title "Motor Trend Cars"
                 :x-label "Weight (1000 lbs)"
                 :y-label "Miles per Gallon"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (count (:texts s))))))])

;; ### Scatter with regression and confidence band
;; Source: [Vega-Lite: Scatter + Linear Regression](https://vega.github.io/vega-lite/examples/layer_point_line_regression.html)

(-> cars
    (sk/view :wt :mpg)
    sk/lay-point
    (sk/lay-lm {:se true})
    (sk/options {:title "Weight vs MPG with Linear Fit"
                 :x-label "Weight (1000 lbs)"
                 :y-label "Miles per Gallon"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Grouped bar chart
;; Source: [Vega-Lite: Grouped Bar](https://vega.github.io/vega-lite/examples/bar_grouped.html)

(def tips (rdatasets/reshape2-tips))

(-> tips
    (sk/lay-bar :day {:color :sex})
    (sk/options {:title "Tips by Day and Gender"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Log scale scatter
;; Source: [ECharts: Scatter Logarithmic](https://echarts.apache.org/examples/en/editor.html?c=scatter-logarithmic-regression)

(-> diamonds
    (sk/lay-point :carat :price {:alpha 0.1})
    (sk/scale :y :log)
    (sk/options {:title "Diamond Price by Carat (Log Scale)"
                 :x-label "Carat"
                 :y-label "Price ($, log scale)"}))

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ### Summary with error bars (mean ± SE)
;; Source: [Vega-Lite: Error Bars with CI](https://vega.github.io/vega-lite/examples/layer_point_errorbar_ci.html)

(-> tips
    (sk/lay-summary :day :total-bill {:color :sex})
    (sk/options {:title "Average Bill with Standard Error"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:points s))))])

;; ### Scatter with color and size (bubble)
;; Source: [D3 Graph Gallery: Bubble Chart](https://d3-graph-gallery.com/graph/bubble_basic.html)

(-> (rdatasets/gapminder-gapminder)
    (tc/select-rows #(= 2007 (:year %)))
    (sk/lay-point :gdpPercap :lifeExp {:color :continent :size :pop})
    (sk/scale :x :log)
    (sk/options {:title "Gapminder 2007: Life Expectancy vs GDP"
                 :x-label "GDP per Capita (log)"
                 :y-label "Life Expectancy"}))

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ### Multi-series line chart
;; Source: [Vega-Lite: Multi Series Line](https://vega.github.io/vega-lite/examples/line_color.html)

(-> (rdatasets/gapminder-gapminder)
    (tc/select-rows #(#{"Japan" "United States" "China" "India" "Brazil"} (:country %)))
    (sk/lay-line :year :lifeExp {:color :country})
    (sk/options {:title "Life Expectancy Over Time"
                 :x-label "Year"
                 :y-label "Life Expectancy"}))

(kind/test-last [(fn [v] (pos? (:lines (sk/svg-summary v))))])

;; ### Step chart
;; Source: [Vega-Lite: Step Chart](https://vega.github.io/vega-lite/examples/line_step.html)

(def economics (rdatasets/ggplot2-economics))

(-> economics
    (sk/lay-step :date :unemploy)
    (sk/options {:title "US Unemployment (Step)"
                 :x-label "Date"
                 :y-label "Unemployed (thousands)"}))

(kind/test-last [(fn [v] (pos? (:lines (sk/svg-summary v))))])

;; ### Density with rug marks
;; Source: [Python Graph Gallery: Density with Rug](https://python-graph-gallery.com/71-density-plot-with-shade-seaborn/)

(-> (rdatasets/datasets-iris)
    (sk/view :Sepal.Length)
    sk/lay-density
    sk/lay-rug
    (sk/options {:title "Iris Sepal Length: Density + Rug"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Scatter + LOESS with confidence band
;; Source: [Python Graph Gallery: Scatter with Smoothing](https://python-graph-gallery.com/42-custom-linear-regression-fit-seaborn/)

(-> tips
    (sk/view :total-bill :tip {:color :smoker})
    sk/lay-point
    (sk/lay-loess {:se true})
    (sk/options {:title "Tips: Bill vs Tip by Smoking Status"
                 :x-label "Total Bill ($)"
                 :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Faceted histogram
;; Source: [Vega-Lite: Faceted Histogram](https://vega.github.io/vega-lite/examples/trellis_bar_histogram.html)

(-> (rdatasets/ggplot2-mpg)
    (sk/lay-histogram :hwy {:color :drv})
    (sk/facet :drv)
    (sk/options {:title "Highway MPG by Drive Type"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ### Scatter with annotations
;; Source: [R Graph Gallery: Scatter with Reference Lines](https://r-graph-gallery.com/scatterplot.html)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :Sepal.Length :Sepal.Width {:color :Species})
    (sk/annotate (sk/rule-h 3.0)
                 (sk/rule-v 6.0)
                 (sk/band-v 5.0 6.0 {:alpha 0.1}))
    (sk/options {:title "Iris with Reference Lines and Band"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; ### Violin + boxplot overlay
;; Source: [Python Graph Gallery: Violin with Box](https://python-graph-gallery.com/violin-plot/)

(-> tips
    (sk/view :day :total-bill)
    sk/lay-violin
    sk/lay-boxplot
    (sk/options {:title "Tips Distribution by Day"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

;; ### Horizontal lollipop (ranked)
;; Source: [R Graph Gallery: Lollipop](https://r-graph-gallery.com/lollipop-plot.html)

(-> cars
    (sk/lay-lollipop :rownames :mpg)
    (sk/coord :flip)
    (sk/options {:title "Cars Ranked by MPG"}))

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ### Stacked bar (proportional / fill)
;; Source: [Vega-Lite: Stacked Bar Normalized](https://vega.github.io/vega-lite/examples/stacked_bar_normalize.html)

(-> tips
    (sk/lay-stacked-bar-fill :day {:color :sex})
    (sk/options {:title "Gender Proportion by Day (100% stacked)"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Scatter matrix (SPLOM) with color
;; Source: [Vega-Lite: Scatter Matrix](https://vega.github.io/vega-lite/examples/interactive_splom.html)

(-> (rdatasets/datasets-iris)
    (sk/view (sk/cross [:Sepal.Length :Sepal.Width :Petal.Length :Petal.Width]
                       [:Sepal.Length :Sepal.Width :Petal.Length :Petal.Width])
             {:color :Species}))

(kind/test-last [(fn [v] (= 16 (:panels (sk/svg-summary v))))])

;; ### Density normalized histogram overlay
;; Source: [Python Graph Gallery: Histogram + Density](https://python-graph-gallery.com/density-and-histogram-together/)

(-> (rdatasets/datasets-iris)
    (sk/view :Sepal.Length)
    (sk/lay-histogram {:normalize :density})
    sk/lay-density
    (sk/options {:title "Sepal Length: Histogram + Density Curve"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ---
;; ## Gaps and Low-Hanging Fruits
;;
;; Examples from the galleries that napkinsketch CANNOT reproduce yet,
;; but could with modest effort:
;;
;; - **Pie / Donut chart** — could work with polar coord + stacked bar
;;   if bar rendering supported 360° arcs. Currently polar only does
;;   rose charts (one wedge per bar).
;;
;; - **Slope chart / Dumbbell** — needs two x positions per row connected
;;   by a line segment. Could be done with a new `:segment` mark that
;;   draws from (x1,y) to (x2,y).
;;
;; - **Strip plot / Bee swarm** — needs jitter on a categorical axis.
;;   Currently jitter is pixel-based random offset on point marks,
;;   not category-aware layout.
;;
;; - **Marginal plots** — side panels with density/histogram along scatter
;;   axes. Needs layout support for panels with different sizes.
;;
;; - **Free scales** — faceted panels with independent y ranges. One of
;;   the most requested missing features (Persona 3).
;;
;; - **Gradient line** — line where color varies along the path (e.g.,
;;   temperature over time). Needs per-segment coloring.
