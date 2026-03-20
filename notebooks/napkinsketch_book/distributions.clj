;; # Distributions
;;
;; Histograms, density plots, boxplots, violins, and ridgelines
;; for exploring the shape and spread of data.

(ns napkinsketch-book.distributions
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

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Histogram

;; Distribution of sepal length across all species.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Histogram

;; Split by species — each group gets its own color.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Petal Width Histogram

;; Petal width has a bimodal distribution.

(-> iris
    (sk/view :petal_width)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Histogram with Custom Title

(-> tips
    (sk/view :total_bill)
    (sk/lay (sk/histogram))
    (sk/plot {:title "Distribution of Total Bill"
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

(-> iris
    (sk/view [[:sepal_length :sepal_length]])
    (sk/lay (sk/histogram {:normalize :density :alpha 0.5})
            (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])
;; ## Density Plot

;; A smooth curve estimating the probability density function.
;; Less sensitive to bin width than histograms.

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Grouped Density

;; Per-species density curves with automatic color mapping.

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Density with Custom Bandwidth

;; A narrow bandwidth reveals more detail; a wide bandwidth smooths more.

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density {:bandwidth 0.3}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 1 (:polygons s)))))])

;; ## Boxplot

;; Median, quartiles, whiskers at 1.5×IQR, and outlier points.

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/boxplot))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Grouped Boxplot

;; Side-by-side boxplots colored by a grouping variable.

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/boxplot {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s))
                 (pos? (:lines s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [sk (-> tips
             (sk/view [[:day :total_bill]])
             (sk/lay (sk/boxplot {:color :smoker}))
             sk/sketch)
      panel (first (:panels sk))
      box-layer (first (filter #(= :boxplot (:mark %)) (:layers panel)))
      cats (:color-categories box-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Boxplot

;; Flipped coordinate for horizontal orientation.

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/boxplot))
    (sk/plot {:coord :flip}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s))
                 (pos? (:lines s)))))])

;; ## Violin Plot
;;
;; A violin shows the full density shape per category — more
;; informative than a boxplot for multimodal distributions.

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 4 (:polygons s)))))])

;; ## Grouped Violin

;; Color splits each category into side-by-side violins.

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 8 (:polygons s)))))])

;; Verify dodge positioning: each color group gets a distinct offset.

(let [sk (-> tips
             (sk/view [[:day :total_bill]])
             (sk/lay (sk/violin {:color :smoker}))
             sk/sketch)
      panel (first (:panels sk))
      viol-layer (first (filter #(= :violin (:mark %)) (:layers panel)))
      cats (:color-categories viol-layer)]
  (count cats))

(kind/test-last
 [(fn [v] (= 2 v))])

;; ## Horizontal Violin

(-> iris
    (sk/view [[:species :petal_length]])
    (sk/lay (sk/violin))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])

;; ## Ridgeline Plot
;;
;; Overlapping density curves stacked vertically by category — good
;; for comparing distribution shapes across many groups.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Ridgeline

;; Map color to the same categorical column for distinct curves.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (= 3 (:polygons s)))))])