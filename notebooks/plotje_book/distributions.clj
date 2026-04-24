;; # Distributions
;;
;; [Histograms](https://en.wikipedia.org/wiki/Histogram), [density](https://en.wikipedia.org/wiki/Kernel_density_estimation) plots, [boxplots](https://en.wikipedia.org/wiki/Box_plot), [violins](https://en.wikipedia.org/wiki/Violin_plot), and ridgelines
;; for exploring the shape and spread of data.

(ns plotje-book.distributions
  (:require
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## Histogram

;; Distribution of sepal length across all species.

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Histogram

;; Split by species -- each group gets its own color.

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :petal-width))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Histogram with Custom Title

(-> (rdatasets/reshape2-tips)
    (pj/lay-histogram :total-bill)
    (pj/options {:title "Distribution of Total Bill"
                 :x-label "Amount ($)"}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s))
                 (some #(= "Distribution of Total Bill" %) (:texts s)))))])

;; ## Density-Normalized Histogram
;;
;; Pass `{:normalize :density}` so the y-axis shows probability
;; density instead of raw counts. This makes the histogram directly
;; comparable with a density curve overlay.

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length {:normalize :density :alpha 0.5})
    pj/lay-density)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])
;; ## Density Plot

;; A smooth curve estimating the probability density function.
;; Less sensitive to bin width than histograms.

(-> (rdatasets/datasets-iris)
    (pj/lay-density :sepal-length))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Grouped Density

;; Per-species density curves with automatic color mapping.

(-> (rdatasets/datasets-iris)
    (pj/lay-density :sepal-length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Density with Custom Bandwidth

;; A narrow bandwidth reveals more detail; a wide bandwidth smooths more.

(-> (rdatasets/datasets-iris)
    (pj/lay-density :sepal-length {:bandwidth 0.3}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Boxplot

;; Median, quartiles, whiskers at 1.5xIQR (interquartile range), and outlier points.

(-> (rdatasets/datasets-iris)
    (pj/lay-boxplot :species :sepal-width))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Grouped Boxplot

;; Side-by-side boxplots colored by a grouping variable.

(-> (rdatasets/reshape2-tips)
    (pj/lay-boxplot :day :total-bill {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s))
                 (pos? (:lines s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [pl (-> (rdatasets/reshape2-tips)
             (pj/lay-boxplot :day :total-bill {:color :smoker})
             pj/plan)
      panel (first (:panels pl))
      box-layer (first (filter #(= :boxplot (:mark %)) (:layers panel)))
      cats (:color-categories box-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Boxplot

;; Flipped coordinate for horizontal orientation.

(-> (rdatasets/datasets-iris)
    (pj/lay-boxplot :species :sepal-width)
    (pj/coord :flip))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Violin Plot
;;
;; A violin shows the full density shape per category -- more
;; informative than a boxplot for multimodal distributions.

(-> (rdatasets/reshape2-tips)
    (pj/lay-violin :day :total-bill))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 4 (:polygons s)))))])

;; ## Grouped Violin

;; Color splits each category into side-by-side violins.

(-> (rdatasets/reshape2-tips)
    (pj/lay-violin :day :total-bill {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [pl (-> (rdatasets/reshape2-tips)
             (pj/lay-violin :day :total-bill {:color :smoker})
             pj/plan)
      panel (first (:panels pl))
      viol-layer (first (filter #(= :violin (:mark %)) (:layers panel)))
      cats (:color-categories viol-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Violin

(-> (rdatasets/datasets-iris)
    (pj/lay-violin :species :petal-length)
    (pj/coord :flip))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## [Ridgeline](https://en.wikipedia.org/wiki/Ridgeline_plot) Plot
;;
;; Overlapping density curves stacked vertically by category -- good
;; for comparing distribution shapes across many groups.

(-> (rdatasets/datasets-iris)
    (pj/lay-ridgeline :species :sepal-length))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Ridgeline

;; Map color to the same categorical column for distinct curves.

(-> (rdatasets/datasets-iris)
    (pj/lay-ridgeline :species :sepal-length {:color :species}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Comparing Multiple Columns
;;
;; Pass a vector of column names to `pj/lay-histogram` (or any
;; `lay-*` function) to create one panel per column. This is useful
;; for comparing the shape of different variables side by side.

(pj/lay-histogram (rdatasets/datasets-iris) [:sepal-length :sepal-width :petal-length])

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 3 (:panels s))
                 (pos? (:polygons s)))))])

;; Combine with `:color` to see group differences within each column.

(pj/lay-density (rdatasets/datasets-iris) [:sepal-length :sepal-width :petal-length] {:color :species})

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 3 (:panels s))
                 (pos? (:polygons s)))))])

;; The multi-column vector works with any `lay-*` function -- histograms,
;; density curves, boxplots, violin plots, and more.

;; ## What's Next
;;
;; - [**Ranking**](./plotje_book.ranking.html) -- bar charts and lollipop plots for categorical comparisons
;; - [**Faceting**](./plotje_book.faceting.html) -- split distributions by groups into separate panels
