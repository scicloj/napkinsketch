;; # Polar Coordinates
;;
;; `(sk/coord :polar)` maps x to angle and y to radius. Bars become
;; arc-interpolated wedges (rose charts), and scatter points wrap into
;; a disc. Currently best suited for point and bar-family marks.

(ns napkinsketch-book.polar
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
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