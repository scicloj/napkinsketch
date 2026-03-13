;; # Polar Coordinates
;;
;; `(sk/coord :polar)` maps x to angle and y to radius, wrapping any
;; chart type into a radial layout. Bars become wedges, scatters wrap
;; into a disc, and lines spiral around the center.

(ns napkinsketch-book.polar
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def wind (tc/dataset {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"]
                       :speed [12 8 15 10 7 13 9 11]}))

;; ## Polar Scatter
;;
;; The same scatter plot, wrapped into polar space. x maps to angle
;; (clockwise from 12 o'clock), y maps to radius (center = minimum,
;; edge = maximum).

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Rose Chart (Coxcomb Diagram)
;;
;; A bar chart in polar coordinates produces a
;; [rose chart](https://en.wikipedia.org/wiki/Coxcomb_diagram) —
;; wedge area encodes count. Bars are arc-interpolated for smooth
;; curved edges.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Wind Rose
;;
;; A value-bar chart in polar makes a wind rose — each direction
;; gets a wedge proportional to wind speed.

(-> wind
    (sk/view [:direction :speed])
    (sk/lay (sk/value-bar))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:polygons s)))))])

;; ## Polar Stacked Bar
;;
;; Stacked bars in polar show composition within each wedge.

(-> iris
    (sk/view :species)
    (sk/lay (sk/stacked-bar {:color :species}))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Polar Histogram
;;
;; A histogram in polar wraps bins around the circle. Useful for
;; circular distributions (e.g., time of day, compass bearing).

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Polar Line
;;
;; Lines projected into polar coordinates form spirals or closed
;; loops depending on the data.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/line {:color :species}))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:lines s)))))])

;; ## Polar with Regression
;;
;; A scatter plot with a linear regression line, projected into
;; polar space.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Polar Area
;;
;; Area marks fill the region between the data curve and the baseline,
;; creating petal-like shapes in polar space.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/area {:color :species}))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Polar Boxplot
;;
;; Boxplots in polar coordinates show distribution summaries arranged
;; radially.

(-> iris
    (sk/view [:species :sepal_length])
    (sk/lay (sk/boxplot))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s))
                                (pos? (:lines s)))))])

;; ## Polar Violin
;;
;; Violin plots in polar give a sense of distributional shape
;; around the circle.

(-> iris
    (sk/view [:species :sepal_length])
    (sk/lay (sk/violin))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Polar Errorbar
;;
;; Error bars projected into polar space.

(def summary-ds (tc/dataset {:species ["setosa" "versicolor" "virginica"]
                             :mean [5.0 5.9 6.6]
                             :lo [4.8 5.6 6.3]
                             :hi [5.2 6.2 6.9]}))

(-> summary-ds
    (sk/view [:species :mean])
    (sk/lay (sk/errorbar {:ymin :lo :ymax :hi}))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; ## Polar Lollipop
;;
;; Lollipop charts in polar — stems radiate from center, dots at tips.

(-> wind
    (sk/view [:direction :speed])
    (sk/lay (sk/lollipop))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:points s))
                                (= 8 (:lines s)))))])

;; ## Explicit Labels with Polar
;;
;; By default, polar suppresses auto-generated axis labels (since there
;; are no rectangular axes). You can still set explicit labels with `sk/labs`:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/coord :polar)
    (sk/labs {:title "Iris in Polar Space"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (some #{"Iris in Polar Space"} (:texts s)))))])
