;; # Relationships
;;
;; Regression, smoothing, density estimation, and heatmaps --
;; revealing structure between two variables.

(ns plotje-book.relationships
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; R datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Fastmath -- random number generation
   [fastmath.random :as rng]))

;; ## Linear Regression

;; A single regression line through all data.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Per-Group Regression

;; Fit a regression line per group.

(-> (rdatasets/datasets-iris)
    (pj/pose :petal-length :petal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Regression with Confidence Ribbon

;; Pass `{:confidence-band true}` to show a 95% confidence band around the line.

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model :confidence-band true}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])
;; ## Tips with Regression

;; Do smokers and non-smokers tip differently?

(-> (rdatasets/reshape2-tips)
    (pj/pose :total-bill :tip {:color :smoker})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; ## [LOESS](https://en.wikipedia.org/wiki/Local_regression) Smoothing

;; A smooth curve through noisy data.

(-> (let [r (rng/rng :jdk 42)
          xs (vec (range 50))]
      {:x xs
       :y (mapv #(+ (Math/sin (* % 0.2))
                    (* 0.3 (- (rng/drandom r) 0.5)))
                xs)})
    (pj/lay-point :x :y)
    (pj/lay-smooth {:bandwidth 0.2}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## [Heatmap](https://en.wikipedia.org/wiki/Heat_map) (Auto-Binned)

;; Bin x and y into a grid, count points per cell.

(-> (rdatasets/datasets-iris)
    (pj/lay-tile :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
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
    (pj/lay-tile :x :y {:fill :value}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:visible-tiles s)))))])

;; ## Density 2D

;; [KDE](https://en.wikipedia.org/wiki/Kernel_density_estimation)-smoothed 2D density heatmap.

(-> (rdatasets/datasets-iris)
    (pj/lay-density-2d :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:visible-tiles s)))))])

;; ## Density 2D with Points

;; Overlay scatter points on the density heatmap.

(-> (rdatasets/datasets-iris)
    (pj/lay-density-2d :sepal-length :sepal-width)
    (pj/lay-point {:alpha 0.5}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:visible-tiles s)))))])

;; ## [Contour](https://en.wikipedia.org/wiki/Contour_line) Lines

;; Iso-density contour lines from 2D KDE.

(-> (rdatasets/datasets-iris)
    (pj/lay-contour :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; ## Contour with Points

;; Contour lines overlaid on scatter points.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:alpha 0.3})
    (pj/lay-contour {:levels 8}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; ## What's Next
;;
;; - [**Polar Coordinates**](./plotje_book.polar.html) -- radial charts and pie-style visualizations
;; - [**Faceting**](./plotje_book.faceting.html) -- split any chart into panels by category
