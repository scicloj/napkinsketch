;; # Core Concepts
;;
;; This chapter introduces the ideas behind Napkinsketch: how data
;; becomes a plot, and how the pieces compose. Every concept is
;; explained as it appears — no prior knowledge of plotting libraries
;; is assumed.

(ns napkinsketch-book.core-concepts
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Data
;;
;; A **dataset** is a table of rows and columns — like a spreadsheet.
;; Each column has a name (a keyword like `:sepal_length`) and holds
;; values of one type.
;;
;; We load the classic iris flower dataset from a CSV file.
;; `{:key-fn keyword}` converts the CSV header strings to Clojure
;; keywords.

(def iris
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
              {:key-fn keyword}))

iris

(kind/test-last [(fn [ds] (= 150 (tc/row-count ds)))])

;; The dataset has 150 rows and 5 columns. Four columns are
;; **numerical** (measurements in centimeters) and one is
;; **categorical** (the species name — one of three strings).
;;
;; This distinction matters: napkinsketch treats numerical and
;; categorical columns differently when choosing axes, colors, and
;; statistical transforms.

;; You can also pass data as a plain Clojure map of columns — no
;; `tc/dataset` call needed:

{:x [1 2 3 4 5]
 :y [2 4 3 5 4]}

;; ## Views
;;
;; A **view** describes *what* to plot: which dataset, which column
;; goes on the x-axis, and which goes on the y-axis.
;;
;; `sk/view` creates a view. It returns a vector of maps — plain
;; Clojure data that you can inspect, store, and transform.

(def my-view (sk/view iris :sepal_length :sepal_width))

(kind/pprint my-view)

;; The view is a vector containing one map. That map holds the
;; dataset (`:data`) and the column mappings (`:x` and `:y`).
;; No rendering has happened yet — it is just a description.

;; ## Marks
;;
;; A **mark** is a visual shape that represents data: points, lines,
;; bars, and others. Mark constructors like `sk/point`, `sk/line`,
;; and `sk/bar` return small maps describing the mark type.

(sk/point)

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; `sk/lay` merges a mark into a view — it says "draw this data
;; using this visual shape." The result is still a vector of maps,
;; now with `:mark` and `:stat` keys added.

(def view-with-mark
  (sk/lay my-view (sk/point)))

(kind/pprint view-with-mark)

;; ## Plotting
;;
;; `sk/plot` takes views and renders them into an SVG image.
;; It resolves all the details — axis ranges, tick positions,
;; colors, layout — and produces a visual figure.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; That is the minimal pipeline: data → view → mark → plot.
;; Everything else in this chapter builds on this foundation.

;; ## Layers
;;
;; A plot can have multiple **layers** — different marks drawn on
;; the same axes. Pass multiple marks to `sk/lay` and they stack
;; visually.
;;
;; Here we add a linear regression line (`sk/lm`) on top of the
;; scatter points. A regression line is a straight line fitted to
;; the data — it shows the overall trend.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Incremental Building
;;
;; Because views are plain data, you can save a partial plot and
;; extend it later. Each `sk/lay` call adds layers without changing
;; the original.

(def scatter-base
  (-> iris
      (sk/view :sepal_length :sepal_width)
      (sk/lay (sk/point))))

;; Add a regression line:

(-> scatter-base
    (sk/lay (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead — a flexible curve that follows local
;; patterns in the data:

(-> scatter-base
    (sk/lay (sk/loess))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Color
;;
;; The `:color` option controls point and line colors. Its behavior
;; depends on what you pass.
;;
;; **Categorical column** — when `:color` maps to a column with
;; text values (like `:species`), each unique value gets a distinct
;; color from the **palette** (an ordered set of colors). A
;; **legend** appears, mapping labels to colors.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Numeric column** — when `:color` maps to a numerical column
;; (like `:petal_length`), values map to a continuous **gradient** —
;; a smooth color ramp from low to high. The legend shows a color
;; bar instead of discrete entries.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :petal_length}))
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; **Fixed color string** — a literal color name like `"steelblue"`
;; colors all points uniformly. No legend appears because there is
;; nothing to distinguish.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color "steelblue"}))
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Grouping
;;
;; Categorical color does more than set colors — it creates
;; **groups**. Each group is processed independently: it gets its
;; own regression line, density curve, or bar.
;;
;; Below, `:color :species` on both the points and the regression
;; produces three separate regression lines — one per species:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Three lines instead of one — grouping lets you see patterns
;; within each species, not just the overall trend.

;; ## Faceting
;;
;; **Faceting** splits a plot into multiple **panels** — one per
;; value of a column. Each panel is a separate plotting area that
;; shares the same axes, making it easy to compare subsets
;; side by side.
;;
;; `sk/facet` specifies which column to split on:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/facet :species)
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Three panels, one per species. The axes are shared, so you can
;; compare sepal dimensions across species at a glance.

;; ## Multi-Panel Plots
;;
;; `sk/pairs` generates all unique pairs from a list of columns.
;; Passing the result to `sk/view` creates a multi-panel plot —
;; one panel per column pair. This is useful for exploring
;; relationships across many variables at once.

(def measurements [:sepal_length :sepal_width :petal_length :petal_width])

(sk/pairs measurements)

(kind/test-last [(fn [v] (= 6 (count v)))])

;; Four columns produce six unique pairs. Each pair becomes a
;; scatter panel:

(-> iris
    (sk/view (sk/pairs measurements))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (= 6 (:panels (sk/svg-summary v))))])

;; ## Coordinates and Scales
;;
;; **Coordinates** and **scales** are composable modifiers. They
;; change how data maps to visual space without changing the data
;; itself.
;;
;; `sk/coord` sets the coordinate system. `:flip` swaps the x and y
;; axes — useful for horizontal bar charts or when axis labels are
;; long:

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log` applies a
;; logarithmic transformation — useful when values span several
;; orders of magnitude:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## What's Next
;;
;; This chapter covered the core building blocks. The rest of the
;; book builds on them:
;;
;; - **Inference Rules** — how napkinsketch chooses defaults for
;;   marks, stats, and domains automatically
;; - **Glossary** — concise definitions of every term, including
;;   ones not covered here (stat, position, theme, annotation,
;;   and more)
;; - **Chart Types** — scatter plots, distributions, bar charts,
;;   time series, and polar charts
;; - **How-to Guides** — faceting, configuration, customization,
;;   and recipes for common tasks
