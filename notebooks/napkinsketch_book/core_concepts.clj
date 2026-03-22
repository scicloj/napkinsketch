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
;; This distinction matters: Napkinsketch treats numerical and
;; categorical columns differently when choosing axes, colors, and
;; statistical transforms.
;;
;; You can also pass data as a plain Clojure map of columns —
;; Napkinsketch coerces it to a dataset internally:

(-> {:x [1 2 3 4 5]
     :y [2 4 3 5 4]}
    (sk/view :x :y)
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

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

;; ## Marks and Methods
;;
;; A **mark** is a visual shape that represents data: points, lines,
;; bars, and others. Mark constructors like `sk/point`, `sk/line`,
;; and `sk/histogram` return a **method** — a small map that bundles
;; the mark with its default statistical transform.

(sk/point)

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; `sk/lay` adds a mark to a view — it says "draw this data using
;; this visual shape." The result is still a vector of maps, now
;; with `:mark` and `:stat` keys added.

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

;; Those are the minimal ingredients: data, view, mark, plot.
;; Everything else in this chapter builds on this foundation.

;; ## Inference
;;
;; `sk/lay` is not always needed. When you omit it, Napkinsketch
;; infers the mark from the column types. Two numerical columns
;; produce a scatter plot; a single categorical column produces a
;; bar chart.

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single column produces a histogram:

(-> iris
    (sk/view :sepal_length)
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Use `sk/lay` when you want to choose a specific mark, pass
;; options like `:color`, or add multiple layers.

;; ## Layers
;;
;; A plot can have multiple **layers** — different marks drawn on
;; the same axes. Pass multiple marks to `sk/lay` and they are
;; drawn together, each contributing its own visual element.
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
;; **Categorical column** — when `:color` refers to a column with
;; text values (like `:species`), each unique value gets a distinct
;; color from the **palette** (an ordered set of colors). A
;; **legend** appears alongside the plot, mapping labels to colors.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Numeric column** — when `:color` refers to a numerical column
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
;; Compare: without `:color`, `sk/lm` fits one line to all the data:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; With `:color :species` on both the points and the regression,
;; each species becomes a separate group — three lines instead of
;; one:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Grouping reveals patterns within each species that the overall
;; trend line hides.

;; ## Faceting
;;
;; **Faceting** splits a plot into multiple **panels** — separate
;; plotting areas, one per value of a column. All panels share the
;; same axes, making it easy to compare subsets side by side.
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

;; Three panels, one per species. The shared axes let you compare
;; sepal dimensions across species at a glance.

;; ## Column Combinations
;;
;; `sk/cross` generates all combinations of two lists. Passing
;; column names to `sk/cross` and the result to `sk/view` creates
;; one panel per combination — a quick way to explore relationships
;; across many variables at once.

(def cols [:sepal_length :sepal_width :petal_length])

(sk/cross cols cols)

(kind/test-last [(fn [v] (= 9 (count v)))])

;; Three columns crossed with themselves produce nine panels —
;; a full grid where each row and column corresponds to a variable:

(-> iris
    (sk/view (sk/cross cols cols))
    sk/plot)

(kind/test-last [(fn [v] (= 9 (:panels (sk/svg-summary v))))])

;; Notice the diagonal: when x and y are the same column,
;; Napkinsketch infers a histogram instead of a scatter plot.
;; This is inference at work — each panel gets the mark that
;; fits its column types.

;; ## Coordinates and Scales
;;
;; **Coordinates** and **scales** are composable modifiers. They
;; change how data maps to visual space without changing the data
;; itself.
;;
;; `sk/coord` sets the coordinate system. `:flip` swaps the x and y
;; axes — useful for horizontal layouts or when axis labels are
;; long.
;;
;; Here we flip a scatter plot so sepal length runs vertically:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log` applies a
;; logarithmic transformation — useful when values span a wide
;; range, so that small and large values are both visible.
;;
;; Here we use a dataset where values vary by orders of magnitude:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area       [2     8    30    120     500     2100]}
    (sk/view :population :area)
    (sk/lay (sk/point))
    (sk/scale :x :log)
    (sk/scale :y :log)
    sk/plot)

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Without log scales, the small values would be crushed together
;; near the origin. Log scales spread them out proportionally.

;; ## What's Next
;;
;; This chapter covered the core building blocks. The rest of the
;; book builds on them:
;;
;; - **Inference Rules** — how Napkinsketch chooses defaults for
;;   marks, stats, and domains automatically
;; - **Glossary** — concise definitions of every term, including
;;   ones not covered here (stat, position, theme, annotation,
;;   and more)
;; - **Chart Types** — scatter plots, distributions, bar charts,
;;   time series, and polar charts
;; - **How-to Guides** — faceting, configuration, customization,
;;   and recipes for common tasks
