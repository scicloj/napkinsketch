;; # Core Concepts
;;
;; This chapter covers the core concepts you need for daily use.
;; If you have not read the
;; [Frame Model](./napkinsketch_book.frame_model.html)
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
;; names entirely -- they are inferred by position: the first column
;; becomes x, the second becomes y, the third becomes color.

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Datasets with four or more columns require explicit column names.
;; See the [Datasets](./napkinsketch_book.datasets.html) chapter for
;; loading from CSV, URLs, and other file formats.

;; ---
;; ## Mappings and Layers
;;
;; A frame is built from two kinds of content:
;;
;; - **Mapping** -- what to show (which columns define the axes and aesthetics)
;; - **Layer** -- how to show it (which chart type, with what options)
;;
;; The mapping says "show sepal length versus sepal width." A layer
;; says "show it as points" or "fit a regression line." When you want
;; multiple layers sharing the same axes, declare the mapping once
;; with `sk/frame`, then add layers with `sk/lay-*`:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; One mapping, two layers: points and a regression line.
;;
;; When `sk/lay-*` is called **with columns**, it creates a frame and
;; attaches a layer in one step:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; We will revisit where mappings flow (to all layers or to a single
;; one) in the Scope section below.

;; With multiple frames arranged side by side, use `sk/arrange`:

(def two-panel
  (sk/arrange
   [(-> (rdatasets/datasets-iris)
        (sk/lay-point :sepal-length :sepal-width))
    (-> (rdatasets/datasets-iris)
        (sk/lay-point :petal-length :petal-width))]))

two-panel

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; Each sub-frame has its own mapping and layers. `sk/arrange`
;; produces a composite frame that contains them as siblings.

;; ---
;; ## Scope
;;
;; A **mapping** connects a column to a visual property -- like
;; mapping `:species` to color. Where you write a mapping determines
;; who sees it. There are two levels:
;;
;; | Where you write it | What sees it |
;; |:-------------------|:-------------|
;; | `(sk/frame ... {:color :c})` | All layers on this frame |
;; | `(sk/lay-point ... {:color :c})` | This layer only |
;;
;; This is **lexical scope** -- the same principle as in programming
;; languages. Lower levels override higher ones.

;; ### Frame-level mapping
;;
;; `sk/frame`'s mapping flows to every layer attached to the frame:

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (= 3 (:lines (sk/svg-summary v))))])

;; Both point and smooth layers see `:color :species`. Three
;; regression lines -- one per species.

;; ### Layer-level mapping
;;
;; Opts in `sk/lay-*` scope to that layer alone:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Color is on the point layer. The smooth layer does not see it --
;; one overall regression line.

;; ### Override
;;
;; Lower scopes override higher ones. A layer can cancel a mapping
;; by setting it to `nil`:

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    (sk/lay-point {:color nil})
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; The frame says `:color :species`. The point layer cancels it with
;; `nil` -- uncolored points. The smooth layer has no override, so it
;; keeps the frame-level color -- three lines.

;; ### How scope is applied
;;
;; The same scoping principle governs three things -- mappings,
;; layers, and data -- each with its own combination rule:
;;
;; | What | Frame level | Layer level | Combination |
;; |:-----|:------------|:------------|:------------|
;; | Mapping | `sk/frame` mapping | `sk/lay-*` opts | `merge` -- innermost wins, `nil` erases |
;; | Layer | `sk/lay-*` | -- (leaf) | layers accumulate |
;; | Data | first argument | `:data` in layer opts | innermost non-nil wins |

;; ### Layer-level data
;;
;; Pass `:data` in the opts map of `sk/lay-*` to give that layer its
;; own dataset:

(def setosa
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "setosa" (:species %))))

(def versicolor
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "versicolor" (:species %))))

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width)
    (sk/lay-point {:data setosa})
    (sk/lay-smooth {:stat :linear-model :data versicolor}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; Points from setosa (50 rows), regression from versicolor.
;; Same frame, different data per layer.
;;
;; Faceting splits a single dataset into panels automatically:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Three panels, each with its own data subset.

;; ---
;; ## Identity
;;
;; Scope determines what each frame and layer sees. But how do
;; layers find their frame? The rule is:
;;
;; - `sk/lay-*` with columns **finds the most recent leaf frame**
;;   whose position mappings match, or creates a new one if none
;;   matches.
;;
;; This makes the threading pipeline sequential and predictable.
;; When the columns match, the new layer attaches to the existing
;; frame:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-smooth :sepal-length :sepal-width {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; One frame, two layers: scatter points and a regression line
;; sharing the same axes.
;;
;; When the columns differ, `sk/lay-*` creates a new frame:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; Two frames, arranged side by side: one scatter and one histogram.
;;
;; To force two separate frames that share a column but carry
;; different layers, arrange them explicitly:

(sk/arrange
 [(-> (rdatasets/datasets-iris) (sk/lay-histogram :sepal-width))
  (-> (rdatasets/datasets-iris) (sk/lay-density :sepal-width))])

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; ---
;; ## The Frame
;;
;; A frame is a composable value. A simple leaf frame carries:
;;
;; | Field | Contains | Set by |
;; |:------|:---------|:-------|
;; | `:data` | the dataset | `sk/frame`, `sk/lay-*`, or `sk/with-data` |
;; | `:mapping` | frame-level mappings | `sk/frame` |
;; | `:layers` | layers attached to the frame | `sk/lay-*` |
;; | `:opts` | title, width, theme, scale, coord | `sk/options`, `sk/scale`, `sk/coord` |

(def my-frame
  (-> (rdatasets/datasets-iris)
      (sk/frame {:x :sepal-length :y :sepal-width :color :species})
      sk/lay-point
      (sk/lay-smooth {:stat :linear-model})
      (sk/options {:title "Iris"})))

my-frame

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ---
;; ## Mark, Stat, and Position
;;
;; Each layer has a **layer-type** -- a rendering recipe with three parts:
;;
;; - **Mark** -- the visual shape (point, bar, line, area, tile, ...)
;; - **Stat** -- the computation before rendering (identity, bin, linear-model, density, ...)
;; - **Position** -- how overlapping groups share space (identity, dodge, stack, ...)
;;
;; A layer-type's name describes its intent. The mark describes the shape:

(sk/layer-type-lookup :histogram)

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; A histogram: stat `:bin` computes ranges, mark `:bar` shows them:

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; A regression: stat `:linear-model` fits a line, mark `:line` shows it:

(sk/layer-type-lookup :smooth)

(kind/test-last [(fn [m] (= :loess (:stat m)))])

;; Position `:stack` places groups on top of each other:

(-> {:day ["Mon" "Mon" "Tue" "Tue"]
     :count [30 20 45 15]
     :meal ["lunch" "dinner" "lunch" "dinner"]}
    (sk/lay-value-bar :day :count {:color :meal :position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; See the [Layer Types](./napkinsketch_book.layer_types.html) chapter
;; for complete tables of every mark, stat, and position.

;; ---
;; ## Inference
;;
;; Napkinsketch tries to make small frames work without you having
;; to specify everything. You give it what you know -- a dataset,
;; perhaps a column or two -- and it fills in the rest by looking
;; at the data.
;;
;; The underlying principle is short: **resolved = your-choice,
;; or else inferred-from-data**. Wherever you make a choice it
;; wins; wherever you don't, the library picks something
;; sensible.
;;
;; Among the things that get inferred this way are the columns
;; used for aesthetics, the chart type, the type of each column
;; (numerical, categorical, or temporal), the scale applied to
;; each axis, and the palette or gradient chosen for the color
;; aesthetic. Other details -- legend structure, scale domain,
;; tick placement, stat parameters, grouping -- are inferred
;; too, but mostly stay out of the way until you want to adjust
;; them.
;;
;; Two kinds of inference show up often enough to be worth seeing
;; directly: column inference and layer-type inference.
;;
;; **Column inference** kicks in when a dataset has up to three
;; columns and you call `sk/frame` (or a `sk/lay-*`) without
;; naming any column. Napkinsketch pairs the columns with
;; aesthetics in dataset order:
;;
;; | Columns in data | Inferred mapping |
;; |:----------------|:-----------------|
;; | 1 | `:x` |
;; | 2 | `:x`, `:y` |
;; | 3 | `:x`, `:y`, `:color` |
;;
;; With four or more columns the library does not guess -- you
;; have to name the columns you want. Column inference is most
;; useful for quick sketches of small, focused datasets.

(-> {:height [170 180 165 175] :weight [70 80 65 75]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 4 (:points (sk/svg-summary v))))])

;; **Layer-type inference** fires when a frame has no explicit
;; layer. The library inspects the types of the columns the
;; mapping refers to and picks a chart type that fits. Two
;; numerical columns produce a scatter plot:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; In both cases the inferred plot is the same one you would
;; get from `sk/lay-point` or `sk/lay-histogram`. Inference is a
;; shorthand, not a separate rendering path.
;;
;; Every inferred choice can be overridden. Want a specific
;; chart type? Use the matching `sk/lay-*` function instead of
;; leaving the frame bare. Want a particular scale? Pass
;; `sk/scale`. Want a numeric column to be treated as categorical?
;; Add `{:x-type :categorical}`, `{:y-type :categorical}`, or
;; `{:color-type :categorical}` to the frame or layer, depending
;; on which axis the column feeds. Inference exists so small
;; frames stay small, not to take decisions away from you. The
;; [Inference Rules](./napkinsketch_book.inference_rules.html)
;; chapter lists the full decision logic and the override for
;; each case.

;; ---
;; ## Incremental Building
;;
;; Because frames are plain data, you can save a partial plot and
;; extend it later. Each call returns a new frame without changing
;; the original.

(def scatter-base
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width)))

;; Add a regression line:

(-> scatter-base (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead:

(-> scatter-base sk/lay-smooth)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ---
;; ## Reusable Frame Templates
;;
;; A frame does not need to carry data. `(sk/frame)` creates an
;; empty frame you can evolve like any other -- adding layers,
;; options -- and then attach a dataset at the end with
;; `sk/with-data`. The result is a plotting *instrument* that can
;; be applied to many datasets:

(def scatter-with-regression
  (-> (sk/frame nil {:x :x :y :y :color :group})
      sk/lay-point
      (sk/lay-smooth {:stat :linear-model})
      (sk/options {:title "Scatter with Regression"})))

;; Apply to one dataset:

(-> scatter-with-regression
    (sk/with-data {:x [1 2 3 4 5 6]
                   :y [2 4 3 5 6 8]
                   :group ["a" "a" "a" "b" "b" "b"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; Apply the same template to a different dataset:

(-> scatter-with-regression
    (sk/with-data {:x [10 20 30 40 50 60]
                   :y [15 18 22 20 25 28]
                   :group ["x" "x" "x" "y" "y" "y"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; `sk/with-data` validates at attach time: if the dataset is
;; missing a column the frame references, you get a clear error
;; naming the missing columns -- no cryptic failure deep in the
;; rendering path.

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
    (sk/frame {:x :sepal-length :y :sepal-width :group :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Three regression lines but all the same color.

;; ---
;; ## Plot Options and Annotations
;;
;; So far you've seen mappings, layers, and data -- all scoped at
;; frame or layer level. The functions in this section set
;; **plot-level options** instead: values that configure the whole
;; rendered plot and cannot be scoped down. See
;; [Options and Scopes](./napkinsketch_book.options_and_scopes.html)
;; for the full picture.
;;
;; `sk/options` sets plot-level settings -- title, axis labels, size,
;; theme overrides:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:title "Iris Measurements"
                 :width 500 :palette :dark2}))

(kind/test-last [(fn [v] (some #{"Iris Measurements"} (:texts (sk/svg-summary v))))])

;; Reference lines and shaded bands are themselves layers, added with
;; `sk/lay-rule-h`, `sk/lay-rule-v`, `sk/lay-band-h`, `sk/lay-band-v`.
;; Positions come from the opts map (`:y-intercept` / `:x-intercept` for
;; rules; `:y-min`/`:y-max` or `:x-min`/`:x-max` for bands); appearance
;; aesthetics like `:color` and `:alpha` work the same way they do on
;; any other layer.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-rule-h {:y-intercept 3.0})
    (sk/lay-band-v {:x-min 5.0 :x-max 6.0 :alpha 0.1}))

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

;; `sk/scale` changes how a numeric axis is shown. `:log` applies
;; a logarithmic transformation:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area [2 8 30 120 500 2100]}
    (sk/lay-point :population :area)
    (sk/scale :x :log)
    (sk/scale :y :log))

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Both are plot-level -- they apply uniformly across the whole frame.

;; ---
;; ## Faceting and Multi-Panel Layouts
;;
;; **Faceting** splits a frame into panels by a column's values:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width)
    (sk/facet :species)
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; A vector of column names creates one panel per variable:

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram [:sepal-length :sepal-width :petal-length]))

(kind/test-last [(fn [v] (= 3 (:panels (sk/svg-summary v))))])

;; To place whole frames side by side, use `sk/arrange`:

(sk/arrange
 [(-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width))
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :petal-length :petal-width))])

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; Each sub-frame inside `sk/arrange` can have its own data, mapping,
;; layers, and options -- they are independent plots arranged in a
;; single figure.

;; ## What's Next
;;
;; - [**Sketch Rules**](./napkinsketch_book.sketch_rules.html) -- 24 rules that formalize the model
