;; # Distributions
;;
;; [Histograms](https://en.wikipedia.org/wiki/Histogram), [density](https://en.wikipedia.org/wiki/Kernel_density_estimation) plots, [boxplots](https://en.wikipedia.org/wiki/Box_plot), [violins](https://en.wikipedia.org/wiki/Violin_plot), and ridgelines
;; for exploring the shape and spread of data.

(ns napkinsketch-book.distributions
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Histogram

;; Distribution of sepal length across all species.

(-> data/iris
    (sk/lay-histogram :sepal_length))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Histogram

;; Split by species — each group gets its own color.

(-> data/iris
    (sk/lay-histogram :sepal_length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> data/iris
    (sk/lay-histogram :petal_width))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Histogram with Custom Title

(-> data/tips
    (sk/lay-histogram :total_bill)
    (sk/options {:title "Distribution of Total Bill"
                       :x-label "Amount ($)"}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s))
                 (some #(= "Distribution of Total Bill" %) (:texts s)))))])

;; ## Density-Normalized Histogram
;;
;; Pass `{:normalize :density}` so the y-axis shows probability
;; density instead of raw counts. This makes the histogram directly
;; comparable with a density curve overlay.

(-> data/iris
    (sk/lay-histogram :sepal_length {:normalize :density :alpha 0.5})
    sk/lay-density)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])
;; ## Density Plot

;; A smooth curve estimating the probability density function.
;; Less sensitive to bin width than histograms.

(-> data/iris
    (sk/lay-density :sepal_length))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Grouped Density

;; Per-species density curves with automatic color mapping.

(-> data/iris
    (sk/lay-density :sepal_length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Density with Custom Bandwidth

;; A narrow bandwidth reveals more detail; a wide bandwidth smooths more.

(-> data/iris
    (sk/lay-density :sepal_length {:bandwidth 0.3}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Boxplot

;; Median, quartiles, whiskers at 1.5×IQR (interquartile range), and outlier points.

(-> data/iris
    (sk/lay-boxplot :species :sepal_width))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Grouped Boxplot

;; Side-by-side boxplots colored by a grouping variable.

(-> data/tips
    (sk/lay-boxplot :day :total_bill {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s))
                 (pos? (:lines s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [pl (-> data/tips
             (sk/lay-boxplot :day :total_bill {:color :smoker})
             sk/plan)
      panel (first (:panels pl))
      box-layer (first (filter #(= :boxplot (:mark %)) (:layers panel)))
      cats (:color-categories box-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Boxplot

;; Flipped coordinate for horizontal orientation.

(-> data/iris
    (sk/lay-boxplot :species :sepal_width)
    (sk/coord :flip))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Violin Plot
;;
;; A violin shows the full density shape per category — more
;; informative than a boxplot for multimodal distributions.

(-> data/tips
    (sk/lay-violin :day :total_bill))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 4 (:polygons s)))))])

;; ## Grouped Violin

;; Color splits each category into side-by-side violins.

(-> data/tips
    (sk/lay-violin :day :total_bill {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [pl (-> data/tips
             (sk/lay-violin :day :total_bill {:color :smoker})
             sk/plan)
      panel (first (:panels pl))
      viol-layer (first (filter #(= :violin (:mark %)) (:layers panel)))
      cats (:color-categories viol-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Violin

(-> data/iris
    (sk/lay-violin :species :petal_length)
    (sk/coord :flip))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## [Ridgeline](https://en.wikipedia.org/wiki/Ridgeline_plot) Plot
;;
;; Overlapping density curves stacked vertically by category — good
;; for comparing distribution shapes across many groups.

(-> data/iris
    (sk/lay-ridgeline :species :sepal_length))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Ridgeline

;; Map color to the same categorical column for distinct curves.

(-> data/iris
    (sk/lay-ridgeline :species :sepal_length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Comparing Multiple Columns
;;
;; Pass a vector of column names to `sk/lay-histogram` (or any
;; `lay-*` function) to create one panel per column. This is useful
;; for comparing the shape of different variables side by side.

(sk/lay-histogram data/iris [:sepal_length :sepal_width :petal_length])

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 3 (:panels s))
                 (pos? (:polygons s)))))])

;; The same works with `sk/view` and a separate `lay-*` step:

(-> data/iris
    (sk/view [:sepal_length :sepal_width :petal_length])
    sk/lay-histogram)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 3 (:panels s))
                 (pos? (:polygons s)))))])

;; Combine with `:color` to see group differences within each column.

(sk/lay-density data/iris [:sepal_length :sepal_width :petal_length] {:color :species})

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 3 (:panels s))
                 (pos? (:polygons s)))))])

;; The multi-column vector works with any `lay-*` function — histograms,
;; density curves, boxplots, violin plots, and more.

;; ## What's Next
;;
;; - [**Ranking**](./napkinsketch_book.ranking.html) — bar charts and lollipop plots for categorical comparisons
;; - [**Faceting**](./napkinsketch_book.faceting.html) — split distributions by groups into separate panels
