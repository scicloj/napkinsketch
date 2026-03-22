;; # Relationships
;;
;; Regression, smoothing, density estimation, and heatmaps —
;; revealing structure between two variables.

(ns napkinsketch-book.relationships
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation (for synthetic data)
   [fastmath.random :as rng]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Linear Regression

;; A single regression line through all data.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    sk/lay-lm
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Per-Group Regression

;; Fit a regression line per group.

(-> iris
    (sk/view [[:petal_length :petal_width]])
    (sk/lay-point {:color :species})
    (sk/lay-lm {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Regression with Confidence Ribbon

;; Pass `{:se true}` to show a 95% confidence band around the line.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    (sk/lay-lm {:se true :color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])
;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay-point {:color :smoker})
    (sk/lay-lm {:color :smoker})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; ## LOESS Smoothing

;; A smooth curve through noisy data.

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  {:x (range 50)
                   :y (mapv #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                            (range 50))}))

(-> noisy-wave
    (sk/view [[:x :y]])
    sk/lay-point
    sk/lay-loess
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## Heatmap (Auto-Binned)

;; Bin x and y into a grid, count points per cell.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-tile
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

;; ## Heatmap (Pre-Computed)

;; Use a numeric column for tile color.

(def grid-data
  (let [r (rng/rng :jdk 99)]
    {:x (for [i (range 5) _j (range 5)] i)
     :y (for [_i (range 5) j (range 5)] j)
     :value (vec (repeatedly 25 #(rng/irandom r 100)))}))

(-> grid-data
    (sk/lay-tile :x :y {:fill :value})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

;; ## Density 2D

;; KDE-smoothed 2D density heatmap.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-density2d
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:tiles s)))))])

;; ## Density 2D with Points

;; Overlay scatter points on the density heatmap.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-density2d
    (sk/lay-point {:alpha 0.5})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:tiles s)))))])

;; ## Contour Lines

;; Iso-density contour lines from 2D KDE.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-contour
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; ## Contour with Points

;; Contour lines overlaid on scatter points.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:alpha 0.3})
    (sk/lay-contour {:levels 8})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])