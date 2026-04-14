;; # Core Concepts
;;
;; This chapter covers the core concepts you need for daily use.
;; If you have not read the
;; [Sketch Model](./napkinsketch_book.sketch_model.html)
;; chapter, start there -- it introduces the mental model behind
;; composable plotting.

(ns napkinsketch-book.core-concepts
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Data
;;
;; Napkinsketch accepts plain Clojure data -- maps, vectors of maps --
;; or columnar datasets. No wrapping needed for simple cases.
;; The [Datasets](./napkinsketch_book.datasets.html) chapter covers
;; data formats and loading in detail.
;;
;; We use the classic iris flower dataset throughout these examples.
;; Each column has a name (a keyword like `:sepal-length`) and holds
;; values of one type.

(rdatasets/datasets-iris)

(kind/test-last [(fn [ds] (= 150 (count (tc/rows ds))))])

;; The dataset has 150 rows and 5 columns. Four columns are
;; **numerical** (measurements in centimeters) and one is
;; **categorical** (the species name -- one of three strings).
;;
;; This distinction matters: Napkinsketch treats numerical and
;; categorical columns differently when choosing axes, colors, and
;; statistical transforms.
;;
;; Here is a scatter plot of sepal dimensions, colored by species:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### Input formats
;;
;; Napkinsketch accepts several common Clojure data shapes and
;; coerces them into a dataset internally.
;;
;; **Map of columns** -- keys are column names, values are sequences:

(-> {:x [1 2 3 4 5]
     :y [2 4 3 5 4]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; **Sequence of row maps** -- each map is one row:

(-> [{:city "Paris" :temperature 22}
     {:city "London" :temperature 18}
     {:city "Berlin" :temperature 20}
     {:city "Rome" :temperature 28}]
    (sk/lay-value-bar :city :temperature))

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; When the dataset has 1, 2, or 3 columns, you can omit the column
;; names entirely -- they are inferred by position (first -> x,
;; second -> y, third -> color):

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Datasets with four or more columns require explicit column names.
;; See the [Datasets](./napkinsketch_book.datasets.html) chapter for
;; loading from CSV, URLs, and other file formats.

;; ---
;; ## Views and Layers
;;
;; A sketch is built from two kinds of content:
;;
;; - **View** -- what to look at (which columns define the axes)
;; - **Layer** -- how to draw it (which chart type, with what options)
;;
;; A view says "show me sepal length versus sepal width."
;; A layer says "draw it as points" or "fit a regression line."
;; When you want multiple layers sharing the same axes, declare
;; the axes with `sk/view`, then add layers with `sk/lay-*`:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; One view, two layers: points and a regression line.
;; Let us look at the sketch structure:

(kind/pprint
 (-> (rdatasets/datasets-iris)
     (sk/view :sepal-length :sepal-width)
     sk/lay-point
     sk/lay-lm))

(kind/test-last
 [(fn [sk]
    (and (= 1 (count (:views sk)))
         (= 2 (count (:layers sk)))))])

;; `:views` has one view (the panel). `:layers` has two layers
;; (point and lm). These layers apply to every view -- we will
;; see why in the Scope section below.
;;
;; When `sk/lay-*` is called **with columns**, it creates a view with
;; a layer bound directly to it:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(kind/pprint
 (-> (rdatasets/datasets-iris)
     (sk/lay-point :sepal-length :sepal-width)))

(kind/test-last
 [(fn [sk]
    (and (= 1 (count (:views sk)))
         (= 0 (count (:layers sk)))
         (= 1 (count (:layers (first (:views sk)))))))])

;; The layer lives inside the view. No sketch-level layers.
;;
;; With multiple views, sketch-level layers apply to all of them --
;; the cross product:

(def two-panel-sketch
  (-> (rdatasets/datasets-iris)
      (sk/view :sepal-length :sepal-width)
      (sk/view :petal-length :petal-width)
      sk/lay-point))

two-panel-sketch

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

(kind/pprint two-panel-sketch)

(kind/test-last [(fn [sk] (and (= 2 (count (:views sk)))
                               (= 1 (count (:layers sk)))))])

;; Two views, one layer. Each view becomes a panel, and the
;; point layer applies to both.

;; ---
;; ## Scope
;;
;; A **mapping** connects a column to a visual property -- like
;; mapping `:species` to color. Where you write a mapping determines
;; who sees it. There are three levels:
;;
;; | Where you write it | What sees it |
;; |:-------------------|:-------------|
;; | `(sk/sketch ... {:color :c})` | All layers on all views |
;; | `(sk/view ... {:color :c})` | All layers on this view |
;; | `(sk/lay-point ... {:color :c})` | This layer only |
;;
;; This is **lexical scope** -- the same principle as in programming
;; languages. Lower levels override higher ones.

;; ### Sketch-level mapping
;;
;; `sk/sketch` sets mappings that flow to every view and every layer:

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (= 3 (:lines (sk/svg-summary v))))])

;; Both point and lm see `:color :species`. Three regression
;; lines -- one per species.

;; ### View-level mapping
;;
;; The opts map in `sk/view` scopes to that view. All layers on
;; the view inherit it, but other views do not:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (= 3 (:lines (sk/svg-summary v))))])

;; Same result here -- one view, both layers see the color.
;; The difference shows with two views:

(def view-scoped
  (-> (rdatasets/datasets-iris)
      (sk/view :sepal-length :sepal-width {:color :species})
      (sk/view :petal-length :petal-width)
      sk/lay-point))

view-scoped

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

(kind/pprint view-scoped)

(kind/test-last
 [(fn [sk]
    (and (= {} (:mapping sk))
         (= :species (:color (:mapping (first (:views sk)))))
         (nil? (:color (:mapping (second (:views sk)))))))])

;; Color is in the first view's mapping only. The second view
;; has no color. Sketch-level mapping is empty.

;; ### Layer-level mapping
;;
;; Opts in `sk/lay-*` scope to that layer alone:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Color is in the point layer. The lm (a sketch-level layer)
;; does not see it -- one overall regression line.

;; ### Override
;;
;; Lower scopes override higher ones. A layer can cancel a mapping
;; by setting it to `nil`:

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:color nil})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Sketch says `:color :species`. The point layer cancels it
;; with `nil` -- uncolored points. The lm layer has no override,
;; so it keeps the sketch-level color -- three lines.

;; ### How scope is applied
;;
;; The same scoping principle governs three things -- mappings,
;; layers, and data -- each with its own combination rule:
;;
;; | What | Sketch level | View level | Layer level | Combination |
;; |:-----|:-------------|:-----------|:------------|:------------|
;; | Mapping | `sk/sketch` | `sk/view` opts | `sk/lay-*` opts | `merge` -- innermost wins, `nil` erases |
;; | Layer | `sk/lay-*` (bare) | `sk/lay-*` (with columns) | -- (leaf) | `concat` -- both apply |
;; | Data | first argument | `:data` in view opts | `:data` in layer opts | `or` -- innermost non-nil wins |

;; ### Layer scope
;;
;; Sketch-level lm -- both panels get a regression line:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 2 (:lines s)))))])

;; View-level lm -- only the first panel gets one:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-lm :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 1 (:lines s)))))])

;; ### Data scope
;;
;; Data enters at sketch level -- the first argument to `sk/sketch`
;; or `sk/lay-*`. All views see it:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Both panels draw from the same 150-row dataset (300 points total).
;;
;; **View-level data** -- pass `:data` in the opts map of `sk/view`
;; to give a view its own dataset:

(def setosa
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "setosa" (:species %))))

(def versicolor
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "versicolor" (:species %))))

(-> (sk/sketch (rdatasets/datasets-iris))
    (sk/view :sepal-length :sepal-width {:data setosa})
    (sk/view :sepal-length :sepal-width {:data versicolor})
    sk/lay-point)

(kind/test-last [(fn [v] (= 100 (:points (sk/svg-summary v))))])

;; 100 points -- each view draws from its own 50-row subset.
;; Faceting does the same thing automatically:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Three panels, each with its own data subset.
;;
;; **Layer-level data** -- pass `:data` in the opts map of `sk/lay-*`:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:data setosa})
    (sk/lay-lm {:data versicolor}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; Points from setosa (50 rows), regression from versicolor.
;; Same panel, different data per layer.
;;
;; One principle, three things it governs, each at every level.

;; ---
;; ## Identity
;;
;; Scope determines what each view and layer sees. But how do views
;; come into existence? The two verbs handle this differently:
;;
;; - `sk/view` **always creates** a new view.
;; - `sk/lay-*` with columns **finds the most recent** view that
;;   has the same x and y columns, or creates a new one.
;;
;; This makes the threading pipeline sequential and predictable:
;; each `lay-*` refers to the view you just established.

(def targeted
  (-> (rdatasets/datasets-iris)
      (sk/view :sepal-width)
      (sk/lay-histogram :sepal-width)
      (sk/view :sepal-width)
      (sk/lay-density :sepal-width)))

targeted

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

(kind/pprint targeted)

(kind/test-last
 [(fn [sk]
    (and (= 2 (count (:views sk)))
         (= :histogram (:method (first (:layers (first (:views sk))))))
         (= :density (:method (first (:layers (second (:views sk))))))))])

;; Two views with the same column but different layers. The first
;; `lay-histogram` found the first `view`. The second `lay-density`
;; found the second `view` -- the most recent match.

;; ---
;; ## The Sketch Record
;;
;; A sketch is a composable value with five fields:
;;
;; | Field | Contains | Set by |
;; |:------|:---------|:-------|
;; | `:data` | the dataset | `sk/lay-*` or `sk/sketch` |
;; | `:mapping` | sketch-level mappings | `sk/sketch` |
;; | `:views` | what to plot | `sk/view`, `sk/lay-*` with columns |
;; | `:layers` | sketch-level layers | `sk/lay-*` without columns |
;; | `:opts` | title, width, theme, scale, coord | `sk/options`, `sk/scale`, `sk/coord` |

(def my-sketch
  (-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
      (sk/view :sepal-length :sepal-width)
      sk/lay-point
      sk/lay-lm
      (sk/options {:title "Iris"})))

my-sketch

(kind/pprint my-sketch)

(kind/test-last
 [(fn [sk]
    (and (tc/dataset? (:data sk))
         (= :species (:color (:mapping sk)))
         (= 1 (count (:views sk)))
         (= 2 (count (:layers sk)))
         (= "Iris" (:title (:opts sk)))))])

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ---
;; ## Mark, Stat, and Position
;;
;; Each layer contains a **method** -- a rendering recipe with three parts:
;;
;; - **Mark** -- the visual shape (point, bar, line, area, tile, ...)
;; - **Stat** -- the computation before drawing (identity, bin, lm, kde, ...)
;; - **Position** -- how overlapping groups share space (identity, dodge, stack, ...)
;;
;; A method's name describes its intent. The mark describes the shape:

(sk/method-lookup :histogram)

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; A histogram: stat `:bin` computes ranges, mark `:bar` draws them:

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; A regression: stat `:lm` fits a line, mark `:line` draws it:

(sk/method-lookup :lm)

(kind/test-last [(fn [m] (= :lm (:stat m)))])

;; Position `:stack` places groups on top of each other:

(-> {:day ["Mon" "Mon" "Tue" "Tue"]
     :count [30 20 45 15]
     :meal ["lunch" "dinner" "lunch" "dinner"]}
    (sk/lay-value-bar :day :count {:color :meal :position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; See the [Methods](./napkinsketch_book.methods.html) chapter for
;; complete tables of every mark, stat, and position.

;; ---
;; ## Inference
;;
;; Napkinsketch infers two things automatically:
;;
;; - **Columns** -- when omitted, inferred from the dataset shape
;;   (1 column -> x, 2 -> x y, 3 -> x y color)
;; - **Method** -- when using `sk/view` instead of an explicit
;;   `sk/lay-*`, the chart type is chosen from the column types
;;
;; Two numerical columns produce a scatter plot:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single column produces a histogram:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Use `sk/lay-point`, `sk/lay-histogram`, etc. when you want to
;; choose a specific method, pass options, or add multiple layers.
;; See the [Inference Rules](./napkinsketch_book.inference_rules.html)
;; chapter for the full decision logic.

;; ---
;; ## Incremental Building
;;
;; Because sketches are plain data, you can save a partial plot and
;; extend it later. Each call returns a new sketch without changing
;; the original.

(def scatter-base
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width)))

;; Add a regression line:

(-> scatter-base sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead:

(-> scatter-base sk/lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ---
;; ## Color and Grouping
;;
;; `:color` controls point and line colors. Its behavior depends on
;; what you pass.
;;
;; **Categorical column** -- each unique value gets a distinct color.
;; A legend maps labels to colors:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Numeric column** -- values map to a continuous gradient:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :petal-length}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; **Fixed color string** -- all points colored uniformly:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color "steelblue"}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Categorical color does more than set colors -- it creates
;; **groups**. Each group is processed independently: it gets its
;; own regression line, density curve, or boxplot:

(-> (rdatasets/datasets-iris)
    (sk/lay-density :sepal-length {:color :species}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Other visual properties include `:alpha` (transparency), `:size`,
;; and `:shape`. The `:group` option creates groups without changing
;; colors:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:group :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Three regression lines but all the same color.

;; ---
;; ## Plot Options and Annotations
;;
;; `sk/options` sets plot-level settings -- title, axis labels, size,
;; theme overrides:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:title "Iris Measurements"
                 :width 500 :palette :dark2}))

(kind/test-last [(fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))])

;; `sk/annotate` adds reference lines and bands as plot decorations:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/annotate (sk/rule-h 3.0)
                 (sk/band-v 5.0 6.0 {:alpha 0.1})))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; See the [Customization](./napkinsketch_book.customization.html)
;; chapter for themes, palettes, and annotation details.

;; ---
;; ## Coordinates and Scales
;;
;; `sk/coord` sets the coordinate system. `:flip` swaps the axes:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/coord :flip))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/scale` changes how a numeric axis is drawn. `:log` applies
;; a logarithmic transformation:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area [2 8 30 120 500 2100]}
    (sk/lay-point :population :area)
    (sk/scale :x :log)
    (sk/scale :y :log))

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Both are plot-level -- they apply to all views uniformly.

;; ---
;; ## Faceting and Multi-Panel Layouts
;;
;; **Faceting** splits a plot into panels by a column's values:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/facet :species)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; A vector of column names creates one panel per variable:

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram [:sepal-length :sepal-width :petal-length]))

(kind/test-last [(fn [v] (= 3 (:panels (sk/svg-summary v))))])

;; `sk/view` also accepts a vector of `[x y]` pairs -- each pair
;; becomes one panel:

(-> (rdatasets/datasets-iris)
    (sk/view [[:sepal-length :sepal-width]
              [:petal-length :petal-width]])
    sk/lay-point)

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; Two panels, each with its own x/y columns. `sk/cross` automates
;; this for every combination of columns -- useful for scatter
;; matrices:

(def cols [:sepal-length :sepal-width :petal-length])

(sk/cross cols cols)

;; Nine `[x y]` pairs. Passing them to `sk/view` creates a 3x3 grid:

(-> (rdatasets/datasets-iris)
    (sk/view (sk/cross cols cols)))

(kind/test-last [(fn [v] (= 9 (:panels (sk/svg-summary v))))])

;; On the diagonal, where x and y are the same column, Napkinsketch
;; infers a histogram instead of a scatter plot.

;; ---
;; ## Summary
;;
;; Three ideas explain the entire model:
;;
;; **1. View and layer.** A view says what (which columns -> panel).
;; A layer says how (which chart type, with what options). Views x
;; applicable layers -> rendered result.
;;
;; **2. Scope.** Where you write it determines who sees it.
;; Mappings, layers, and data all flow downward: sketch -> view ->
;; layer. Lower overrides higher.
;;
;; **3. Identity.** `view` always creates a new view. `lay-*` finds
;; the most recent view with the same x and y columns, or creates one.
;;
;; These ideas connect: scope determines what each view and layer
;; inherits; identity determines which view a layer attaches to;
;; and the view-layer split makes both possible.
;;
;; The [Sketch Rules](./napkinsketch_book.sketch_rules.html) chapter
;; formalizes these ideas as 18 tested rules.
