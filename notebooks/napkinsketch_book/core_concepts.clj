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
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Data
;;
;; A **dataset** is a table of rows and columns — like a spreadsheet.
;; Each column has a name (a keyword like `:sepal_length`) and holds
;; values of one type. Napkinsketch uses
;; [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset)
;; as its columnar data representation, typically through the
;; [Tablecloth](https://scicloj.github.io/tablecloth/) API.
;;
;; We use the classic iris flower dataset throughout these examples.
;; It is loaded in the [Datasets](./napkinsketch_book.datasets.html)
;; chapter and available as `data/iris`.

data/iris

(kind/test-last [(fn [ds] (= 150 (count (tc/rows ds))))])

;; The dataset has 150 rows and 5 columns. Four columns are
;; **numerical** (measurements in centimeters) and one is
;; **categorical** (the species name — one of three strings).
;;
;; This distinction matters: Napkinsketch treats numerical and
;; categorical columns differently when choosing axes, colors, and
;; statistical transforms.
;;
;; Here is a scatter plot of sepal dimensions, colored by species:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### Input formats
;;
;; You do not need to construct a Tablecloth dataset explicitly.
;; Napkinsketch accepts several common Clojure data shapes and
;; coerces them into a dataset internally.
;;
;; **Map of columns** — keys are column names, values are sequences:

(-> {:x [1 2 3 4 5]
     :y [2 4 3 5 4]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; **Sequence of row maps** — each map is one row. Missing keys
;; become nil:

(-> [{:city "Paris" :temperature 22}
     {:city "London" :temperature 18}
     {:city "Berlin" :temperature 20}
     {:city "Rome" :temperature 28}]
    (sk/lay-value-bar :city :temperature))

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; When the dataset has 1, 2, or 3 columns, you can omit the column
;; names entirely — they are inferred by position (first → x,
;; second → y, third → color):

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; With three columns, the third becomes the color grouping:

(-> {:x [1 2 3 4] :y [4 5 6 7] :group ["a" "a" "b" "b"]}
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (some #{"a"} (:texts s)))))])

;; Datasets with four or more columns require explicit column names.
;;
;; **Tablecloth dataset** — `tc/dataset` loads data from CSV files,
;; URLs, and other file formats. The `:key-fn keyword` option converts
;; string column headers to keywords. See the
;; [Tablecloth documentation](https://scicloj.github.io/tablecloth/)
;; for all supported formats (CSV, TSV, JSON, Parquet, and more).

;; `tc/dataset` also reads CSV, TSV, JSON, Parquet, and other file
;; formats directly from local paths or URLs — see the
;; [Tablecloth documentation](https://scicloj.github.io/tablecloth/)
;; for the full list.
;;
;; **Sequence of sequences** — each inner sequence is a row. Pass
;; column names explicitly since there are no keys:

(-> (tc/dataset [[1 10] [2 20] [3 15] [4 25]]
                {:column-names [:x :y]})
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (= 1 (:lines (sk/svg-summary v))))])

;; ## Views
;;
;; A **view** describes *what* to plot: which dataset, which column
;; goes on the x-axis, and which goes on the y-axis.
;;
;; `sk/view` creates a view. It returns a vector of maps — plain
;; Clojure data that you can inspect, store, and transform.

(def my-view (sk/view data/iris :sepal_length :sepal_width))

(kind/pprint my-view)

;; The view is a vector containing one map. That map holds the
;; dataset (`:data`) and the column mappings (`:x` and `:y`).
;; No rendering has happened yet — it is just a description.

;; You can also pass column mappings as a map, which lets you include
;; additional visual mappings like `:color`:

(kind/pprint (sk/view data/iris {:x :sepal_length :y :sepal_width :color :species}))

;; ## Methods
;;
;; A **method** tells Napkinsketch *how* to turn data into a visual.
;; It combines three concepts (each explained in detail below):
;;
;; - **mark** — what shape to draw (points, bars, lines)
;; - **stat** — what computation to perform first (use data as-is,
;;   bin into ranges, fit a line)
;; - **position** — how overlapping groups share space (stack, dodge);
;;   defaults to identity (no adjustment) when omitted
;;
;; `sk/method-lookup` retrieves a method by keyword. Most methods only
;; store `:mark` and `:stat` — when `:position` is absent, identity
;; is assumed:

(sk/method-lookup :point)

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (= :identity (:stat m))))])

;; `sk/lay` adds a method to a view — it says "draw this data using
;; this approach." The result is still a vector of maps, now with
;; the method's keys merged in.

(def view-with-method
  (sk/lay my-view (sk/method-lookup :point)))

(kind/pprint view-with-method)

;; In practice you rarely call `sk/lay` and `sk/method-lookup` separately.
;; `sk/lay-point` combines both steps — it creates the method and adds
;; the layer in one call. You will use `sk/lay-point`,
;; `sk/lay-histogram`, and similar functions throughout the rest of
;; the book.

;; ## Plotting
;;
;; Napkinsketch plot specifications auto-render in notebooks.
;; The pipeline resolves all the details — axis ranges, tick positions,
;; colors, layout — and produces a visual figure.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; What `sk/lay-point` returns is a **plot specification** (PlotSpec) — a
;; lightweight wrapper around one or more views that auto-renders in
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible tools
;; like Clay.

(sk/plot-spec? (sk/lay-point data/iris :sepal_length :sepal_width))

(kind/test-last [(fn [v] (true? v))])

;; Under the hood, auto-rendering transforms your views through
;; several stages — computing layout, drawing shapes, producing SVG.
;; The [Architecture](./napkinsketch_book.architecture.html) chapter traces every step;
;; the [Glossary](./napkinsketch_book.glossary.html) defines terms like **sketch** (the
;; intermediate data representation) and **membrane** (the drawable tree).
;;
;; You can also step through the pipeline manually:
;; `sk/sketch` returns the intermediate data, and `sk/plot` returns
;; the final SVG.

;; ## Options
;;
;; There are three kinds of options in Napkinsketch:
;;
;; - **Layer options** — per-layer settings like `:color`, `:size`,
;;   `:position`, and method-specific parameters (`:bandwidth`, `:se`, etc.).
;;   Passed in the options map of layer functions.
;;   See the [Methods](./napkinsketch_book.methods.html) chapter.
;;
;; - **Plot options** — per-plot text content: `:title`, `:subtitle`,
;;   `:caption`, and axis labels. Passed via `sk/options`.
;;
;; - **Configuration** — global rendering defaults: dimensions, theme,
;;   palette, color scale, and more. These follow a layered precedence
;;   chain. See the [Configuration](./napkinsketch_book.configuration.html) chapter.

;; Here is one option from each scope in a single pipeline:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.5}) ;; layer options
    (sk/options {:title "Iris Measurements"                                ;; plot option
                 :width 500 :palette :dark2}))                             ;; config options

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Measurements"} (:texts s)))))])

;; ## Mark
;;
;; The **mark** is the visual shape drawn on the plot. The scatter
;; plot above used `:mark :point` — each data point became a dot.
;;
;; A method's name describes its *intent* while the mark describes
;; the *shape*. The `:histogram` method uses `:mark :bar` because a
;; histogram is drawn with bar shapes:

(sk/method-lookup :histogram)

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; A histogram draws bar shapes filled to show binned counts:

(-> data/iris
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Stat
;;
;; The **stat** is the computation applied to data before drawing.
;; The scatter plot used `:stat :identity` — every row became one
;; point, unchanged.
;;
;; The `:histogram` method uses `:stat :bin` — it groups values into
;; ranges and counts how many fall in each range. The stat transforms
;; the data; the mark renders the result. Together, `:stat :bin` and
;; `:mark :bar` produce the familiar histogram shape.
;;
;; The `:lm` (linear model) method uses `:stat :lm` — it fits a straight line to
;; the data and returns a polyline of predicted values:

(sk/method-lookup :lm)

(kind/test-last [(fn [m] (= :lm (:stat m)))])

;; A [regression](https://en.wikipedia.org/wiki/Linear_regression) line fitted through the scatter data:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Position
;;
;; The **position** controls how overlapping groups share space.
;; Most methods leave it unset — groups are drawn independently
;; (`:position :identity`). The `:stacked-bar` method includes
;; `:position :stack`, which places groups on top of each other:

(sk/method-lookup :stacked-bar)

(kind/test-last [(fn [m] (= :stack (:position m)))])

;; A stacked bar chart — each meal's count stacks on the previous:

(-> {:day ["Mon" "Mon" "Tue" "Tue"]
     :count [30 20 45 15]
     :meal ["lunch" "dinner" "lunch" "dinner"]}
    (sk/lay-value-bar :day :count {:color :meal :position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Inference
;;
;; Napkinsketch infers two things automatically:
;;
;; - **Columns** — when omitted, inferred from the dataset shape
;;   (1 column → x, 2 → x y, 3 → x y color)
;; - **Method** — when using `sk/view` instead of an explicit `sk/lay-*`,
;;   the chart type is chosen from the column types
;;
;; Two numerical columns produce a scatter plot; a single numerical
;; column produces a histogram.

(-> data/iris
    (sk/view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single column produces a histogram:

(-> data/iris
    (sk/view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Use `sk/lay-point`, `sk/lay-histogram`, etc. when you want to choose a specific method, pass
;; options like `:color`, or add multiple layers.

;; ## Layers
;;
;; A plot can have multiple **layers** — different methods drawn on
;; the same axes. Each `sk/lay-X` call adds one layer; thread them and they are
;; drawn together, each contributing its own visual element.
;;
;; Here we add a linear model regression line (`sk/lay-lm`) on top of the
;; scatter points. A regression line is a straight line fitted to
;; the data — it shows the overall trend.

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or with a [LOESS](https://en.wikipedia.org/wiki/Local_regression) (local regression) smoother — a flexible curve that follows local
;; trends instead of fitting a straight line:

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; The same plot without `sk/view` — the first `sk/lay-X` call sets
;; the column mappings and subsequent layers inherit them:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; `sk/lay` also accepts annotation maps (`sk/rule-h`, `sk/band-v`,
;; etc.) — see the [Customization](./napkinsketch_book.customization.html) chapter.

;; ### When to use `sk/view`
;;
;; There are three common patterns:
;;
;; - **Minimal** — `(sk/lay-point data)` — columns inferred from dataset shape
;; - **Explicit columns** — `(sk/lay-point data :x :y)` — no `sk/view` needed
;; - **Inferred method** — `(sk/view data :x :y)` — the library picks the chart type
;; - **Shared aesthetics** — `(-> data (sk/view :x :y {:color :g}) sk/lay-point sk/lay-lm)` — all layers inherit
;;
;; ## Incremental Building
;;
;; Because views are plain data, you can save a partial plot and
;; extend it later. Each `sk/lay-X` call adds a layer without changing
;; the original.

(def scatter-base
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width)))

;; Add a regression line:

(-> scatter-base
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a [LOESS](https://en.wikipedia.org/wiki/Local_regression) smoother instead — a flexible curve that follows local
;; patterns in the data:

(-> scatter-base
    sk/lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; You can also add a layer with **different columns** by passing them
;; explicitly. This creates a multi-panel layout, one panel per column
;; pair:

(-> scatter-base
    (sk/lay-point :petal_length :petal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; ## Color
;;
;; The `:color` option controls point and line colors. Its behavior
;; depends on what you pass.
;;
;; **Categorical column** — when `:color` refers to a column with
;; text values (like `:species`), each unique value gets a distinct
;; color from the **palette** (an ordered set of colors). A
;; **legend** appears alongside the plot, mapping labels to colors.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Numeric column** — when `:color` refers to a numerical column
;; (like `:petal_length`), values map to a continuous **gradient** —
;; a smooth color ramp from low to high. The legend shows a color
;; bar instead of discrete entries.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :petal_length}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; **Fixed color string** — a literal color name like `"steelblue"`
;; colors all points uniformly. No legend appears because there is
;; nothing to distinguish.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color "steelblue"}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Grouping
;;
;; Categorical color does more than set colors — it creates
;; **groups**. Each group is processed independently: it gets its
;; own regression line, density curve, or bar.
;;
;; Compare: without `:color`, `sk/lay-lm` (linear model) fits one line to all the data:

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Passing `:color :species` in `sk/view` makes it a shared aesthetic —
;; all layers inherit it. Each species becomes a separate group, so the
;; regression fits three lines instead of one:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

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

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    (sk/facet :species)
    sk/lay-point
    sk/lay-lm)

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

(-> data/iris
    (sk/view (sk/cross cols cols)))

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

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/coord :flip))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log` applies a
;; [logarithmic](https://en.wikipedia.org/wiki/Logarithmic_scale) transformation — useful when values span a wide
;; range, so that small and large values are both visible.
;;
;; Here we use a dataset where values vary by orders of magnitude:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area [2 8 30 120 500 2100]}
    (sk/lay-point :population :area)
    (sk/scale :x :log)
    (sk/scale :y :log))

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Without log scales, the small values would be crushed together
;; near the origin. Log scales spread them out proportionally.

;; ## What's Next
;;
;; This chapter covered the core building blocks. The rest of the
;; book builds on them:
;;
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) — how napkinsketch chooses defaults for marks, stats, and domains
;; - [**Methods**](./napkinsketch_book.methods.html) — complete tables of every mark, stat, and position
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — the most common chart type, a good place to start exploring

