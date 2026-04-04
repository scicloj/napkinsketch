;; # Relationships
;;
;; Regression, smoothing, density estimation, and heatmaps —
;; revealing structure between two variables.

(ns napkinsketch-book.relationships
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation
   [fastmath.random :as rng]))

;; ## Linear Regression

;; A single regression line through all data.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Per-Group Regression

;; Fit a regression line per group.

(-> data/iris
    (sk/xkcd7-view :petal_length :petal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Regression with Confidence Ribbon

;; Pass `{:se true}` to show a 95% confidence band around the line.

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:se true}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])
;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> data/tips
    (sk/xkcd7-view :total_bill :tip {:color :smoker})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; ## [LOESS](https://en.wikipedia.org/wiki/Local_regression) Smoothing

;; A smooth curve through noisy data.

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  {:x (range 50)
                   :y (map #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                           (range 50))}))

(-> noisy-wave
    (sk/xkcd7-lay-point :x :y)
    sk/xkcd7-lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## [Heatmap](https://en.wikipedia.org/wiki/Heat_map) (Auto-Binned)

;; Bin x and y into a grid, count points per cell.

(-> data/iris
    (sk/xkcd7-lay-tile :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:visible-tiles s)))))])

;; ## Heatmap (Pre-Computed)

;; Use a numeric column for tile color.

(def grid-data
  (let [r (rng/rng :jdk 99)]
    {:x (for [i (range 5) _j (range 5)] i)
     :y (for [_i (range 5) j (range 5)] j)
     :value (repeatedly 25 #(rng/irandom r 100))}))

(-> grid-data
    (sk/xkcd7-lay-tile :x :y {:fill :value}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:visible-tiles s)))))])

;; ## Density 2D

;; [KDE](https://en.wikipedia.org/wiki/Kernel_density_estimation)-smoothed 2D density heatmap.

(-> data/iris
    (sk/xkcd7-lay-density2d :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:visible-tiles s)))))])

;; ## Density 2D with Points

;; Overlay scatter points on the density heatmap.

(-> data/iris
    (sk/xkcd7-lay-density2d :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:visible-tiles s)))))])

;; ## [Contour](https://en.wikipedia.org/wiki/Contour_line) Lines

;; Iso-density contour lines from 2D KDE.

(-> data/iris
    (sk/xkcd7-lay-contour :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; ## Contour with Points

;; Contour lines overlaid on scatter points.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:alpha 0.3})
    (sk/xkcd7-lay-contour {:levels 8}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; ## What's Next
;;
;; - [**Polar**](./napkinsketch_book.polar.html) — radial charts and pie-style visualizations
;; - [**Faceting**](./napkinsketch_book.faceting.html) — split any chart into panels by category
