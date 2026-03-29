;; # Polar Coordinates
;;
;; `(sk/coord :polar)` maps x to angle and y to radius. Bars become
;; arc-interpolated wedges (rose charts), and scatter points wrap into
;; a disc. Currently best suited for point and bar-family marks.

(ns napkinsketch-book.polar
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def wind {:direction ["N" "NE" "E" "SE" "S" "SW" "W" "NW"]
           :speed [12 8 15 10 7 13 9 11]})

;; ## Polar Scatter
;;
;; The same scatter plot, wrapped into polar space. x maps to angle
;; (clockwise from 12 o'clock), y maps to radius (center = minimum,
;; edge = maximum).

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Rose Chart (Coxcomb Diagram)
;;
;; A bar chart in polar coordinates produces a
;; [rose chart](https://en.wikipedia.org/wiki/Coxcomb_diagram) —
;; wedge area encodes count. Bars are arc-interpolated for smooth
;; curved edges.

(-> data/iris
    (sk/lay-bar :species)
    (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Wind Rose
;;
;; A value-bar chart in polar makes a wind rose — each direction
;; gets a wedge proportional to wind speed.

(-> wind
    (sk/lay-value-bar :direction :speed)
    (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:polygons s)))))])

;; ## Polar Stacked Bar
;;
;; Stacked bars in polar show composition within each wedge.

(-> data/penguins
    (sk/lay-stacked-bar :island {:color :species})
    (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Polar Histogram
;;
;; A histogram in polar wraps bins around the circle. Useful for
;; circular distributions (e.g., time of day, compass bearing).

(-> data/iris
    (sk/lay-histogram :sepal_length)
    (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Explicit Labels with Polar
;;
;; By default, polar suppresses auto-generated axis labels (since there
;; are no rectangular axes). You can still set explicit labels with `sk/labs`:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/coord :polar)
    (sk/labs {:title "Iris in Polar Space"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (some #{"Iris in Polar Space"} (:texts s)))))])
