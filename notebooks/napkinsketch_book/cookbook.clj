;; # Cookbook
;;
;; Common plotting recipes — practical patterns for layering marks,
;; combining stats, and building publication-ready charts.

(ns napkinsketch-book.cookbook
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Boxplot with jittered points
;;
;; Overlay raw observations on a boxplot summary. The auto-jitter
;; detects the categorical axis and constrains points to the band width.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/boxplot)
            (sk/point {:jitter true :alpha 0.3}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (= 3 (:polygons s)))))])

;; ## Histogram with density overlay
;;
;; Compare the empirical histogram with a smooth KDE curve.

(-> iris
    (sk/view [[:sepal_length :sepal_length]])
    (sk/lay (sk/histogram {:alpha 0.5})
            (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Scatter with regression lines
;;
;; Fit a linear regression per group to reveal trends across species.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species :alpha 0.6})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Violin with jittered points
;;
;; Show the density shape and every observation together.

(-> iris
    (sk/view [[:species :petal_width]])
    (sk/lay (sk/violin {:alpha 0.3})
            (sk/point {:jitter true :alpha 0.4}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:polygons s)))))])

;; ## Time series with multiple layers
;;
;; Combine area, line, and points for a time series plot.
;; The date axis automatically adapts its tick labels to the time span.

(def ts-dates (mapv #(java.time.LocalDate/ofEpochDay (+ 18262 (* (long %) 7))) (range 52)))

(def ts-ds (tc/dataset {:date ts-dates
                         :value (mapv #(+ 100.0 (* 30.0 (Math/sin (* (double %) 0.12))))
                                      (range 52))}
                        {:key-fn keyword}))

(-> ts-ds
    (sk/view [[:date :value]])
    (sk/lay (sk/area {:alpha 0.2})
            (sk/line)
            (sk/point {:alpha 0.5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 52 (:points s))
                                (= 1 (:lines s))
                                (= 1 (:polygons s)))))])

;; ## Faceted comparison
;;
;; Split a scatter plot by species to compare patterns side by side.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/facet :species)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ## Annotated chart
;;
;; Add reference lines and shaded bands to highlight regions of interest.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/rule-h 3.0)
            (sk/band-v 5.5 6.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Ridgeline with color
;;
;; Compare distribution shapes across categories with overlapping
;; density curves. Grid lines at each baseline aid comparison.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (= 3 (:lines s)))))])

;; ## Stacked bars (proportions)
;;
;; Show the proportion of each species per island using 100% stacked bars.

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins
    (sk/view :island)
    (sk/lay (sk/stacked-bar-fill {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])
