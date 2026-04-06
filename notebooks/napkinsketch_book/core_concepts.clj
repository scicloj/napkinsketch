;; # Core Concepts
;;
;; This chapter covers the core concepts you need for daily use.
;; If you have not read the
;; [Sketch Model](./napkinsketch_book.sketch_model.html)
;; chapter, start there — it introduces the mental model behind
;; composable plotting.

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

;; **Sequence of sequences** — each inner sequence is a row. Pass
;; column names explicitly since there are no keys:

(-> (tc/dataset [[1 10] [2 20] [3 15] [4 25]]
                {:column-names [:x :y]})
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (= 1 (:lines (sk/svg-summary v))))])

;; ## The Sketch Record
;;
;; A **sketch** is a composable value with five fields:
;;
;; | Field | Contains | Set by |
;; |:------|:---------|:-------|
;; | `:data` | the dataset | `sk/lay-*` or `sk/sketch` |
;; | `:shared` | aesthetics for all entries | `sk/view` opts map |
;; | `:entries` | what to plot (column pairs) | `sk/view`, `sk/lay-*` with columns |
;; | `:methods` | how to plot (global methods) | `sk/lay-*` without columns |
;; | `:opts` | title, width, theme | `sk/options` |
;;
;; Here is a concrete sketch:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm
    kind/pprint)

(kind/test-last [(fn [sk] (and (= :species (get-in sk [:shared :color]))
                               (= 1 (count (:entries sk)))
                               (= 2 (count (:methods sk)))))])

;; `:shared` has `{:color :species}` — it applies to all layers.
;; `:entries` has one entry `{:x :sepal_length :y :sepal_width}`.
;; `:methods` has two global methods: point and lm.

;; ## Entries
;;
;; An **entry** is a plain map in `:entries` that declares **what** to
;; plot — which columns map to x and y. Each entry becomes one panel
;; in the rendered plot.
;;
;; `sk/view` adds entries:

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    (sk/view :petal_length :petal_width)
    sk/lay-point
    kind/pprint)

(kind/test-last [(fn [sk] (and (= 2 (count (:entries sk)))
                               (= :sepal_length (:x (first (:entries sk))))
                               (= :petal_length (:x (second (:entries sk))))))])

;; Two entries, two panels. Each `sk/view` call adds one entry.
;; The global `sk/lay-point` applies to both.

;; `sk/lay-*` with columns also creates entries:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length)
    kind/pprint)

(kind/test-last [(fn [sk] (and (= 2 (count (:entries sk)))
                               (= 0 (count (:methods sk)))
                               (= 1 (count (:methods (first (:entries sk)))))))])

;; Two entries — but here each entry carries its OWN method.
;; The global `:methods` is empty. This is the entry-local pattern.

;; ## Shared Aesthetics
;;
;; The opts map in `sk/view` goes into `:shared` — these aesthetics
;; apply to ALL entries and ALL methods:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from shared.
;; Three regression lines — one per species.
;;
;; Compare with per-method color:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Here `:color :species` is in the point's entry-local method, not
;; in shared. The lm (a global method) doesn't see it — one overall
;; regression line instead of three.

;; ## Global vs Entry-Local Methods
;;
;; **Global methods** are stored in `sketch[:methods]` and apply to
;; ALL entries. Created by bare `sk/lay-*` (without columns):
;;
;; ```clojure
;; (-> data (sk/view :x :y) sk/lay-point sk/lay-lm)
;; ;;                        ^^^^^^^^^^^^  ^^^^^^^^^^
;; ;;                        global         global
;; ```
;;
;; **Entry-local methods** are stored inside a specific entry's
;; `:methods` key. Created by `sk/lay-*` with columns:
;;
;; ```clojure
;; (-> data (sk/lay-point :x :y) (sk/lay-lm :x :y))
;; ;;        ^^^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^
;; ;;        entry-local            entry-local (same entry)
;; ```
;;
;; When both exist, entry methods come first, then global methods:
;;
;; ```
;; for each entry:
;;   methods = concat(entry's :methods, global methods)
;;   for each method:
;;     resolved = merge(shared, entry, method)
;; ```
;;
;; See [Sketch Rules](./napkinsketch_book.sketch_rules.html) for the
;; full set of 18 tested rules.

;; ## Options
;;
;; There are three scopes for options in Napkinsketch:
;;
;; - **Shared** — aesthetics like `:color` and `:alpha` that apply to
;;   all entries and methods. Set via `sk/view` opts map.
;;
;; - **Per-method** — aesthetics and parameters that apply to one
;;   method only. Set via `sk/lay-*` opts map. Includes
;;   `:color`, `:alpha`, `:size`, and method-specific parameters
;;   like `:bandwidth`, `:se`, `:jitter`.
;;   See the [Methods](./napkinsketch_book.methods.html) chapter.
;;
;; - **Plot options** — per-plot text content: `:title`, `:subtitle`,
;;   `:caption`, and axis labels. Set via `sk/options`.
;;
;; Resolution order: `merge(shared, entry, method)` — later wins,
;; `nil` cancels.

;; Here is one option from each scope in a single pipeline:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.5}) ;; per-method
    (sk/options {:title "Iris Measurements"                                ;; plot option
                 :width 500 :palette :dark2}))                             ;; config via options

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
;; - **Method** — when using `sk/view` instead of an explicit
;;   `sk/lay-*`, the chart type is chosen from the column types
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

;; Use `sk/lay-point`, `sk/lay-histogram`, etc. when you
;; want to choose a specific method, pass options like `:color`, or
;; add multiple layers.

;; ## Layers
;;
;; A plot can have multiple **layers** — different methods drawn on
;; the same axes. Use `sk/view` to set shared column mappings
;; and aesthetics, then add methods with `sk/lay-*`.
;;
;; Here we add a linear model regression line on top of scatter
;; points. Both layers share the same columns and aesthetics:

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

;; ### Shared vs per-method aesthetics
;;
;; The key distinction: `sk/view` opts go into **shared**
;; (all methods inherit), while `sk/lay-*` opts go into the
;; **method** (only that method gets them).

;; Shared color — both layers are colored:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Per-method color — only points are colored, lm fits one overall line:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; `sk/annotate` adds annotation entries (`sk/rule-h`,
;; `sk/band-v`, etc.) — see the
;; [Customization](./napkinsketch_book.customization.html) chapter.

;; ### When to use `sk/view`
;;
;; There are five common patterns:
;;
;; - **Minimal** — `(sk/lay-point data)` — columns inferred from dataset shape
;; - **Explicit columns** — `(sk/lay-point data :x :y)` — no `view` needed
;; - **Per-method color** — `(sk/lay-point data :x :y {:color :c})` — color on this method only
;; - **Inferred method** — `(sk/view data :x :y)` — the library picks the chart type
;; - **Shared aesthetics** — `(-> data (sk/view :x :y {:color :c}) sk/lay-point sk/lay-lm)` — all methods inherit

;; ## Incremental Building
;;
;; Because sketches are plain data, you can save a partial plot and
;; extend it later. Each `sk/lay-*` call adds a method without
;; changing the original.

(def scatter-base
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width)))

;; Add a regression line:

(-> scatter-base
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead:

(-> scatter-base
    sk/lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; You can also create a **multi-panel layout** by adding multiple
;; entries with `sk/view`, then a single method:

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    (sk/view :petal_length :petal_width)
    sk/lay-point)

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
;; Compare: without color in shared, `sk/lay-lm` fits one
;; line to all the data:

(-> data/iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Passing `:color :species` in `sk/view` makes it a shared
;; aesthetic — all methods inherit it. Each species becomes a separate
;; group, so the regression fits three lines instead of one:

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
;; column names to `sk/cross` and the result to `sk/view`
;; creates one panel per combination — a quick way to explore
;; relationships across many variables at once.

(def cols [:sepal_length :sepal_width :petal_length])

(sk/cross cols cols)

(kind/test-last [(fn [v] (= 9 (count v)))])

;; Three columns crossed with themselves produce nine panels —
;; a full grid where each row and column corresponds to a variable:

(-> data/iris
    (sk/view (sk/cross cols cols))
    sk/lay-point)

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
;; `sk/coord` sets the coordinate system. `:flip` swaps the
;; x and y axes — useful for horizontal layouts or when axis labels
;; are long.
;;
;; Here we flip a scatter plot so sepal length runs vertically:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/coord :flip))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log`
;; applies a [logarithmic](https://en.wikipedia.org/wiki/Logarithmic_scale) transformation — useful when values span a
;; wide range, so that small and large values are both visible.
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
