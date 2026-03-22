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
   [scicloj.napkinsketch.api :as sk]
   ;; Method constructors — for inspecting method maps
   [scicloj.napkinsketch.method :as method]))

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
    sk/lay-point
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

;; ## Methods
;;
;; A **method** tells Napkinsketch *how* to turn data into a visual.
;; It is a small map with up to three keys:
;;
;; - **mark** — what shape to draw (points, bars, lines)
;; - **stat** — what computation to perform first (use data as-is,
;;   bin into ranges, fit a line)
;; - **position** — how overlapping groups share space (stack, dodge)
;;
;; Functions like `method/point`, `method/histogram`, and `method/lm` each
;; return a method:

(method/point)

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (= :identity (:stat m))))])

;; `sk/lay` adds a method to a view — it says "draw this data using
;; this approach." The result is still a vector of maps, now with
;; the method's keys merged in.

(def view-with-method
  (sk/lay my-view (method/point)))

(kind/pprint view-with-method)

;; ## Plotting
;;
;; `sk/plot` takes views and renders them into an SVG image.
;; It resolves all the details — axis ranges, tick positions,
;; colors, layout — and produces a visual figure.

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Data, view, method, plot — those are the minimal ingredients.
;; The next three sections unpack what each part of a method
;; contributes.

;; ## Mark
;;
;; The **mark** is the visual shape drawn on the plot. The scatter
;; plot above used `:mark :point` — each data point became a dot.
;;
;; Notice that a method's name describes its *intent* while the mark
;; describes the *shape*. `method/histogram` uses `:mark :bar` because a
;; histogram is drawn with bar shapes:

(method/histogram)

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; ## Stat
;;
;; The **stat** is the computation applied to data before drawing.
;; The scatter plot used `:stat :identity` — every row became one
;; point, unchanged.
;;
;; `method/histogram` uses `:stat :bin` — it groups values into ranges
;; and counts how many fall in each range:

(-> iris
    (sk/view :sepal_length)
    sk/lay-histogram
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The stat transforms the data; the mark renders the result.
;; Together, `:stat :bin` and `:mark :bar` produce the familiar
;; histogram shape.

;; ## Position
;;
;; The **position** controls how overlapping groups share space.
;; Most methods leave it unset — groups are drawn independently.
;; `method/stacked-bar` includes `:position :stack`, which places groups
;; on top of each other:

(method/stacked-bar)

(kind/test-last [(fn [m] (= :stack (:position m)))])

;; You will see position in action once color introduces grouping,
;; later in this chapter.

;; ## Inference
;;
;; You do not always need to choose a method yourself. When you omit
;; `sk/lay`, Napkinsketch infers the method from the column types.
;; Two numerical columns produce a scatter plot; a single numerical
;; column produces a histogram.

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single column produces a histogram:

(-> iris
    (sk/view :sepal_length)
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Use `sk/lay-point`, `sk/lay-histogram`, etc. when you want to choose a specific method, pass
;; options like `:color`, or add multiple layers.

;; ## Layers
;;
;; A plot can have multiple **layers** — different methods drawn on
;; the same axes. Each `sk/lay-X` call adds one layer; thread them and they are
;; drawn together, each contributing its own visual element.
;;
;; Here we add a linear regression line (`sk/lay-lm`) on top of the
;; scatter points. A regression line is a straight line fitted to
;; the data — it shows the overall trend.

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Incremental Building
;;
;; Because views are plain data, you can save a partial plot and
;; extend it later. Each `sk/lay-X` call adds a layer without changing
;; the original.

(def scatter-base
  (-> iris
      (sk/view :sepal_length :sepal_width)
      sk/lay-point))

;; Add a regression line:

(-> scatter-base
    sk/lay-lm
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead — a flexible curve that follows local
;; patterns in the data:

(-> scatter-base
    sk/lay-loess
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
    (sk/lay-point {:color :species})
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
    (sk/lay-point {:color :petal_length})
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; **Fixed color string** — a literal color name like `"steelblue"`
;; colors all points uniformly. No legend appears because there is
;; nothing to distinguish.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:color "steelblue"})
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Grouping
;;
;; Categorical color does more than set colors — it creates
;; **groups**. Each group is processed independently: it gets its
;; own regression line, density curve, or bar.
;;
;; Compare: without `:color`, `sk/lay-lm` fits one line to all the data:

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; With `:color :species` on both the points and the regression,
;; each species becomes a separate group — three lines instead of
;; one:

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:color :species})
    (sk/lay-lm {:color :species})
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
    sk/lay-point
    sk/lay-lm
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
;; This is inference at work — each panel gets the method that
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
    (sk/lay-point {:color :species})
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log` applies a
;; logarithmic transformation — useful when values span a wide
;; range, so that small and large values are both visible.
;;
;; Here we use a dataset where values vary by orders of magnitude:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area [2 8 30 120 500 2100]}
    (sk/view :population :area)
    sk/lay-point
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
;;   ones not covered here (theme, annotation, and more)
;; - **Chart Types** — scatter plots, distributions, bar charts,
;;   time series, and polar charts
;; - **How-to Guides** — faceting, configuration, customization,
;;   and recipes for common tasks
