;; # Core Concepts
;;
;; This chapter covers the core concepts you need for daily use.
;; If you have not read the
;; [Frame Model](./plotje_book.frame_model.html)
;; chapter, start there -- it introduces the mental model behind
;; composable plotting.

(ns plotje-book.core-concepts
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Data
;;
;; Plotje accepts plain Clojure data -- maps, vectors of maps --
;; or columnar datasets. No wrapping needed for simple cases.
;; The [Datasets](./plotje_book.datasets.html) chapter covers
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
;; This distinction matters: Plotje treats numerical and
;; categorical columns differently when choosing axes, colors, and
;; statistical transforms.
;;
;; Here is a scatter plot of sepal dimensions, colored by species:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Printed, the frame carries the data and the `:x`/`:y` position
;; mapping at the top, and one point layer with its own layer-scoped
;; `:color`:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:color :species})
    kind/pprint)

(kind/test-last [(fn [v] (and (= :sepal-length (get-in v [:mapping :x]))
                              (= 1 (count (:layers v)))
                              (= :species (get-in v [:layers 0 :mapping :color]))))])

;; ### Input formats
;;
;; Plotje accepts several common Clojure data shapes and
;; coerces them into a dataset internally.
;;
;; **Map of columns** -- keys are column names, values are sequences:

(-> {:x [1 2 3 4 5]
     :y [2 4 3 5 4]}
    (pj/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (pj/svg-summary v))))])

;; **Sequence of row maps** -- each map is one row:

(-> [{:city "Paris" :temperature 22}
     {:city "London" :temperature 18}
     {:city "Berlin" :temperature 20}
     {:city "Rome" :temperature 28}]
    (pj/lay-value-bar :city :temperature))

(kind/test-last [(fn [v] (= 4 (:polygons (pj/svg-summary v))))])

;; When the dataset has 1, 2, or 3 columns, you can omit the column
;; names entirely -- they are inferred by position: the first column
;; becomes x, the second becomes y, the third becomes color.

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    pj/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (pj/svg-summary v))))])

;; Datasets with four or more columns require explicit column names.
;; See the [Datasets](./plotje_book.datasets.html) chapter for
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
;; with `pj/frame`, then add layers with `pj/lay-*`:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

;; Printed, the mapping-and-layers split is visible in the frame
;; value -- `:mapping` at the top, `:layers` alongside it, one entry
;; per call:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count (:layers v)))
                              (= :sepal-length (get-in v [:mapping :x]))))])

;; One mapping, two layers: points and a regression line.
;;
;; When `pj/lay-*` is called **with columns**, it creates a frame and
;; attaches a layer in one step:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; We will revisit where mappings flow (to all layers or to a single
;; one) in the Scope section below.

;; With multiple frames arranged side by side, use `pj/arrange`:

(def two-panel
  (pj/arrange
   [(-> (rdatasets/datasets-iris)
        (pj/lay-point :sepal-length :sepal-width))
    (-> (rdatasets/datasets-iris)
        (pj/lay-point :petal-length :petal-width))]))

two-panel

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; Each sub-frame has its own mapping and layers. `pj/arrange`
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
;; | `(pj/frame ... {:color :c})` | All layers on this frame |
;; | `(pj/lay-point ... {:color :c})` | This layer only |
;;
;; This is **lexical scope** -- the same principle as in programming
;; languages. Lower levels override higher ones.

;; ### Frame-level mapping
;;
;; `pj/frame`'s mapping flows to every layer attached to the frame:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (= 3 (:lines (pj/svg-summary v))))])

;; Both point and smooth layers see `:color :species`. Three
;; regression lines -- one per species.

;; ### Layer-level mapping
;;
;; Opts in `pj/lay-*` scope to that layer alone:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:color :species})
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Printed, `:color` lives on the point layer's own `:mapping`, not
;; on the frame -- so only the point layer sees it:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:color :species})
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (and (= :species (get-in v [:layers 0 :mapping :color]))
                              (not (contains? (or (get-in v [:layers 1 :mapping]) {})
                                              :color))))])

;; Color is on the point layer. The smooth layer does not see it --
;; one overall regression line.

;; ### Override
;;
;; Lower scopes override higher ones. A layer can cancel a mapping
;; by setting it to `nil`:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    (pj/lay-point {:color nil})
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Printed, the override appears as `:color nil` in the point
;; layer's own `:mapping`, erasing the frame-level color for that
;; layer only:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    (pj/lay-point {:color nil})
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (and (= :species (get-in v [:mapping :color]))
                              (contains? (get (first (:layers v)) :mapping) :color)
                              (nil? (get-in (first (:layers v)) [:mapping :color]))))])

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
;; | Mapping | `pj/frame` mapping | `pj/lay-*` opts | `merge` -- innermost wins, `nil` erases |
;; | Layer | `pj/lay-*` | -- (leaf) | layers accumulate |
;; | Data | first argument | `:data` in layer opts | innermost non-nil wins |

;; ### Layer-level data
;;
;; Pass `:data` in the opts map of `pj/lay-*` to give that layer its
;; own dataset:

(def setosa
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "setosa" (:species %))))

(def versicolor
  (tc/select-rows (rdatasets/datasets-iris)
                  #(= "versicolor" (:species %))))

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:data setosa})
    (pj/lay-smooth {:stat :linear-model :data versicolor}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; Printed, each layer carries its own `:data` alongside its
;; `:mapping`; the frame-level `:data` remains as a default for
;; any layer that does not override it:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/lay-point {:data setosa})
    (pj/lay-smooth {:stat :linear-model :data versicolor})
    kind/pprint)

(kind/test-last [(fn [v] (and (some? (:data v))
                              (contains? (first (:layers v)) :data)
                              (contains? (second (:layers v)) :data)))])

;; Points from setosa (50 rows), regression from versicolor.
;; Same frame, different data per layer.
;;
;; Faceting splits a single dataset into panels automatically:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/facet :species))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Three panels, each with its own data subset.

;; ---
;; ## Identity
;;
;; Scope determines what each frame and layer sees. But how do
;; layers find their frame? The rule is:
;;
;; - `pj/lay-*` with columns **finds the most recent leaf frame**
;;   whose position mappings match, or creates a new one if none
;;   matches.
;;
;; This makes the threading pipeline sequential and predictable.
;; When the columns match, the new layer attaches to the existing
;; frame:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-smooth :sepal-length :sepal-width {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; One frame, two layers: scatter points and a regression line
;; sharing the same axes.
;;
;; To place two plots side by side with different columns, use an
;; explicit `pj/frame` call to add a second panel:

(-> (rdatasets/datasets-iris)
    (pj/frame [[:sepal-length :sepal-width] [:petal-length :petal-width]])
    (pj/lay-point))

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; Printed, the two-panel outcome is a composite with two sub-frames:

(-> (rdatasets/datasets-iris)
    (pj/frame [[:sepal-length :sepal-width] [:petal-length :petal-width]])
    (pj/lay-point)
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count (:frames v)))
                              (= :sepal-length (get-in v [:frames 0 :mapping :x]))
                              (= :sepal-width  (get-in v [:frames 0 :mapping :y]))
                              (= :petal-length (get-in v [:frames 1 :mapping :x]))
                              (= :petal-width  (get-in v [:frames 1 :mapping :y]))))])

;; Two panels, arranged side by side. For plots with different
;; layer kinds (a scatter and a histogram, say), use `pj/arrange`
;; to combine independent frames:

(pj/arrange
 [(-> (rdatasets/datasets-iris) (pj/lay-histogram :sepal-width))
  (-> (rdatasets/datasets-iris) (pj/lay-density :sepal-width))])

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; ---
;; ## The Frame
;;
;; A frame is a composable value. A simple leaf frame carries:
;;
;; | Field | Contains | Set by |
;; |:------|:---------|:-------|
;; | `:data` | the dataset | `pj/frame`, `pj/lay-*`, or `pj/with-data` |
;; | `:mapping` | frame-level mappings | `pj/frame` |
;; | `:layers` | layers attached to the frame | `pj/lay-*` |
;; | `:opts` | title, width, theme, scale, coord | `pj/options`, `pj/scale`, `pj/coord` |
;;
;; Here is a frame with all four fields set, constructed as an
;; explicit map through `pj/prepare-frame`:

(def my-frame
  (pj/prepare-frame
   {:data (rdatasets/datasets-iris)
    :mapping {:x :sepal-length :y :sepal-width :color :species}
    :layers [{:layer-type :point}
             {:layer-type :smooth :mapping {:stat :linear-model}}]
    :opts {:title "Iris"}}))

my-frame

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (some #{"Iris"} (:texts s)))))])

;; And the same value printed, showing the four fields above:

(kind/pprint my-frame)

(kind/test-last [(fn [fr] (= #{:data :mapping :layers :opts}
                             (set (keys fr))))])

;; The more common way to build a frame of this shape is the
;; threaded form -- `pj/frame` to set data and mapping, `pj/lay-*`
;; for each layer, `pj/options` for plot-level options:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    (pj/options {:title "Iris"}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Printed, the threaded form carries the same fields as
;; `my-frame` above -- `:mapping` up top, `:layers` alongside it,
;; `:opts` holding the title:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    (pj/options {:title "Iris"})
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count (:layers v)))
                              (= :species (get-in v [:mapping :color]))
                              (= "Iris" (get-in v [:opts :title]))))])

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

(pj/layer-type-lookup :histogram)

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; A histogram: stat `:bin` computes ranges, mark `:bar` shows them:

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; A regression: stat `:linear-model` fits a line, mark `:line` shows it:

(pj/layer-type-lookup :smooth)

(kind/test-last [(fn [m] (= :loess (:stat m)))])

;; Position `:stack` places groups on top of each other:

(-> {:day ["Mon" "Mon" "Tue" "Tue"]
     :count [30 20 45 15]
     :meal ["lunch" "dinner" "lunch" "dinner"]}
    (pj/lay-value-bar :day :count {:color :meal :position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; See the [Layer Types](./plotje_book.layer_types.html) chapter
;; for complete tables of every mark, stat, and position.

;; ---
;; ## Inference
;;
;; Plotje tries to make small frames work without you having
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
;; columns and you call `pj/frame` (or a `pj/lay-*`) without
;; naming any column. Plotje pairs the columns with
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
    pj/lay-point)

(kind/test-last [(fn [v] (= 4 (:points (pj/svg-summary v))))])

;; **Layer-type inference** fires when a frame has no explicit
;; layer. The library inspects the types of the columns the
;; mapping refers to and picks a chart type that fits. Two
;; numerical columns produce a scatter plot:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; In both cases the inferred plot is the same one you would
;; get from `pj/lay-point` or `pj/lay-histogram`. Inference is a
;; shorthand, not a separate rendering path.
;;
;; Every inferred choice can be overridden. Want a specific
;; chart type? Use the matching `pj/lay-*` function instead of
;; leaving the frame bare. Want a particular scale? Pass
;; `pj/scale`. Want a numeric column to be treated as categorical?
;; Add `{:x-type :categorical}`, `{:y-type :categorical}`, or
;; `{:color-type :categorical}` to the frame or layer, depending
;; on which axis the column feeds. Inference exists so small
;; frames stay small, not to take decisions away from you. The
;; [Inference Rules](./plotje_book.inference_rules.html)
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
      (pj/lay-point :sepal-length :sepal-width)))

;; Add a regression line:

(-> scatter-base (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Or a LOESS smoother instead:

(-> scatter-base pj/lay-smooth)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ---
;; ## Reusable Frame Templates
;;
;; A frame does not need to carry data. `(pj/frame)` creates an
;; empty frame you can evolve like any other -- adding layers,
;; options -- and then attach a dataset at the end with
;; `pj/with-data`. The result is a plotting *instrument* that can
;; be applied to many datasets:

(def scatter-with-regression
  (-> (pj/frame nil {:x :x :y :y :color :group})
      pj/lay-point
      (pj/lay-smooth {:stat :linear-model})
      (pj/options {:title "Scatter with Regression"})))

;; Printed, the template has `:data nil` -- a frame that carries
;; mapping, layers, and options but no data yet:

(kind/pprint scatter-with-regression)

(kind/test-last [(fn [v] (and (nil? (:data v))
                              (= 2 (count (:layers v)))
                              (= "Scatter with Regression" (get-in v [:opts :title]))))])

;; Apply to one dataset:

(-> scatter-with-regression
    (pj/with-data {:x [1 2 3 4 5 6]
                   :y [2 4 3 5 6 8]
                   :group ["a" "a" "a" "b" "b" "b"]}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; Apply the same template to a different dataset:

(-> scatter-with-regression
    (pj/with-data {:x [10 20 30 40 50 60]
                   :y [15 18 22 20 25 28]
                   :group ["x" "x" "x" "y" "y" "y"]}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; `pj/with-data` validates at attach time: if the dataset is
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
    (pj/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; **Numeric column** -- values map to a continuous gradient:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :petal-length}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; **Fixed color string** -- all points colored uniformly:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color "steelblue"}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Categorical color does more than set colors -- it creates
;; **groups**. Each group is processed independently: it gets its
;; own regression line, density curve, or boxplot:

(-> (rdatasets/datasets-iris)
    (pj/lay-density :sepal-length {:color :species}))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; Other visual properties include `:alpha` (transparency), `:size`,
;; and `:shape`. The `:group` option creates groups without changing
;; colors:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:group :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
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
;; [Options and Scopes](./plotje_book.options_and_scopes.html)
;; for the full picture.
;;
;; `pj/options` sets plot-level settings -- title, axis labels, size,
;; theme overrides:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:title "Iris Measurements"
                 :width 500 :palette :dark2}))

(kind/test-last [(fn [v] (some #{"Iris Measurements"} (:texts (pj/svg-summary v))))])

;; Reference lines and shaded bands are themselves layers, added with
;; `pj/lay-rule-h`, `pj/lay-rule-v`, `pj/lay-band-h`, `pj/lay-band-v`.
;; Positions come from the opts map (`:y-intercept` / `:x-intercept` for
;; rules; `:y-min`/`:y-max` or `:x-min`/`:x-max` for bands); appearance
;; aesthetics like `:color` and `:alpha` work the same way they do on
;; any other layer.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/lay-rule-h {:y-intercept 3.0})
    (pj/lay-band-v {:x-min 5.0 :x-max 6.0 :alpha 0.1}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Printed, annotation layers carry their positions (`:y-intercept`,
;; `:x-min`, `:x-max`) and appearance (`:alpha`) inside the `:mapping`
;; slot, the same slot chart layers use for their mappings:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/lay-rule-h {:y-intercept 3.0})
    (pj/lay-band-v {:x-min 5.0 :x-max 6.0 :alpha 0.1})
    kind/pprint)

(kind/test-last [(fn [v] (and (= :point (get-in v [:layers 0 :layer-type]))
                              (= :rule-h (get-in v [:layers 1 :layer-type]))
                              (= 3.0 (get-in v [:layers 1 :mapping :y-intercept]))
                              (= :band-v (get-in v [:layers 2 :layer-type]))
                              (= 5.0 (get-in v [:layers 2 :mapping :x-min]))))])

;; See the [Customization](./plotje_book.customization.html)
;; chapter for themes, palettes, and annotation details.

;; ---
;; ## Coordinates and Scales
;;
;; `pj/coord` sets the coordinate system. `:flip` swaps the axes:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/coord :flip))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; `pj/scale` changes how a numeric axis is shown. `:log` applies
;; a logarithmic transformation:

(-> {:population [1000 5000 50000 200000 1000000 5000000]
     :area [2 8 30 120 500 2100]}
    (pj/lay-point :population :area)
    (pj/scale :x :log)
    (pj/scale :y :log))

(kind/test-last [(fn [v] (= 6 (:points (pj/svg-summary v))))])

;; Both are plot-level -- they apply uniformly across the whole frame.

;; ---
;; ## Faceting and Multi-Panel Layouts
;;
;; **Faceting** splits a frame into panels by a column's values:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/facet :species)
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

;; Printed, the facet column lives in `:opts` as `:facet-col` --
;; the frame itself is not split until render time:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    (pj/facet :species)
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (= :species (get-in v [:opts :facet-col])))])

;; A vector of column names creates one panel per variable:

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram [:sepal-length :sepal-width :petal-length]))

(kind/test-last [(fn [v] (= 3 (:panels (pj/svg-summary v))))])

;; Printed, each named column becomes a sub-frame with its own x
;; mapping; the bare `pj/lay-histogram` attaches at the root and
;; flows into every panel via `resolve-tree`:

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram [:sepal-length :sepal-width :petal-length])
    kind/pprint)

(kind/test-last [(fn [v] (and (= 3 (count (:frames v)))
                              (= :sepal-length (get-in v [:frames 0 :mapping :x]))
                              (= :sepal-width (get-in v [:frames 1 :mapping :x]))
                              (= :petal-length (get-in v [:frames 2 :mapping :x]))))])

;; To place whole frames side by side, use `pj/arrange`:

(pj/arrange
 [(-> (rdatasets/datasets-iris)
      (pj/lay-point :sepal-length :sepal-width))
  (-> (rdatasets/datasets-iris)
      (pj/lay-point :petal-length :petal-width))])

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; Each sub-frame inside `pj/arrange` can have its own data, mapping,
;; layers, and options -- they are independent plots tiled into a
;; single rendered image.

;; ## What's Next
;;
;; - [**Frame Rules**](./plotje_book.frame_rules.html) -- 28 rules that formalize the model with tested assertions
