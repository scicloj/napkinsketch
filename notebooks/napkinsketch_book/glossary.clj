;; # Glossary
;;
;; Key terms used throughout Napkinsketch, with brief definitions
;; and code examples.

(ns napkinsketch-book.glossary
  (:require
   ;; Datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; clojure2d -- color palettes and gradients
   [clojure2d.color :as c2d]))

;; ## Frame
;;
;; A **frame** is the unified composable value in Napkinsketch. A
;; leaf frame describes one plot panel; a composite frame contains
;; sub-frames arranged together. Every function in the API
;; (`sk/frame`, `sk/lay-*`, `sk/facet`, `sk/arrange`, `sk/options`,
;; `sk/scale`, `sk/coord`) takes a frame and returns a frame.
;; Frames auto-render in
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible
;; tools like [Clay](https://scicloj.github.io/clay/).

(def my-frame
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/options {:title "Iris"})))

my-frame

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A frame is a plain Clojure value -- inspect it with `kind/pprint`
;; or reach into its fields directly.

;; ## Leaf Frame
;;
;; A **leaf frame** is a frame that describes a single plot panel.
;; It carries `:data`, a `:mapping` from columns to aesthetics, and
;; `:layers` -- the chart-type layers attached to it. Created by
;; `sk/frame` or `sk/lay-*`.

;; ## Composite Frame
;;
;; A **composite frame** is a frame that contains other frames under
;; `:frames` plus an optional `:layout`. Created by `sk/arrange` (and
;; later, by `sk/mosaic` and `sk/with-marginals`). Its leaves render
;; independently and are tiled into the final figure.

;; ## Layer Type
;;
;; A **layer type** is the bundle of mark + stat + position that
;; determines how data becomes a visual element. See the
;; [Methods](./napkinsketch_book.methods.html) chapter for detailed
;; tables of all built-in layer types, marks, stats, and positions.
;;
;; Layer types attach to frames in two ways:
;;
;; - **Frame-level layer** -- `sk/lay-*` called without columns,
;;   after a frame with position mappings exists; the layer sees the
;;   frame's mapping.
;; - **New-leaf layer** -- `sk/lay-*` called with columns that do
;;   not match any existing leaf creates a fresh leaf frame with the
;;   layer attached.

;; ## Mark
;;
;; The **mark** is the visual shape shown for each data point or
;; group. Several layer types may share the same mark -- for
;; instance, `lm` and `loess` both produce lines, and `area`,
;; `stacked-area`, and `density` all produce filled regions.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for
;; a table of all built-in marks.

;; ## Stat
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each stat takes data-space inputs and produces the
;; geometry that its mark will show.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for
;; a table of all built-in stats.

;; ## Position
;;
;; A **position** adjustment determines how groups share a categorical
;; axis slot. Position runs between stat computation and rendering.
;; You can override the default position by passing `:position` in
;; the layer options.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together -- error bars automatically align with bars.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for
;; a table of all built-in positions.

(def tips {:day ["Mon" "Mon" "Tue" "Tue"]
           :count [30 20 45 15]
           :meal ["lunch" "dinner" "lunch" "dinner"]})

(-> tips
    (sk/lay-value-bar :day :count {:color :meal :position :stack})
    sk/plan
    (get-in [:panels 0 :layers 0 :groups 1 :y0s]))

(kind/test-last [(fn [y0s] (every? pos? y0s))])

;; ## Draft
;;
;; A **draft** is a vector of flat maps produced by `sk/draft`. Each
;; applicable layer in a frame resolves to one draft element by
;; merging the frame and layer mappings. Draft elements carry all
;; the information the pipeline needs: data, columns, mark, stat,
;; color, grouping.
;;
;; `sk/draft` is useful for inspecting exactly what the renderer
;; will consume before any domains, ticks, or coordinate math are
;; computed.

(-> my-frame sk/draft kind/pprint)

(kind/test-last [(fn [d] (and (vector? d)
                              (= 1 (count d))
                              (= :point (:mark (first d)))))])

;; ## Aesthetic
;;
;; An **aesthetic** is a visual property of a mark that can be mapped
;; to a data column. Napkinsketch supports these aesthetic mappings:
;;
;; | Key | Controls | Column type |
;; |:----|:---------|:------------|
;; | `:color` | Fill/stroke color | Categorical or numerical |
;; | `:size` | Point radius | Numerical |
;; | `:alpha` | Opacity | Numerical |
;; | `:shape` | Point shape | Categorical |
;; | `:text` | Label content | Any |
;; | `:fill` | Tile gradient color | Numerical |
;;
;; When a keyword is passed, it maps to a dataset column.
;; A literal value (e.g., `"#E74C3C"`, `"red"`, `0.5`) sets a fixed
;; aesthetic for all points.

(merge (sk/layer-type-lookup :point) {:color :species :size :petal-length :alpha 0.7})

(kind/test-last [(fn [m] (and (= :species (:color m))
                              (= :petal-length (:size m))
                              (= 0.7 (:alpha m))))])

;; ## Group
;;
;; A **group** is a subset of data that is processed and rendered
;; together. Mapping `:color` to a categorical column automatically
;; creates groups -- one per unique value. You can also create groups
;; without color using the `:group` key.

(-> (rdatasets/datasets-iris)
    (sk/lay-line :sepal-length :sepal-width {:group :species})
    sk/plan
    (get-in [:panels 0 :layers 0 :groups])
    count)

(kind/test-last [(fn [n] (= 3 n))])

;; ## Nudge
;;
;; A **nudge** shifts data coordinates by a constant offset.
;; It is orthogonal to position -- you can nudge within a dodge,
;; or nudge at identity. Applied via `:nudge-x` and `:nudge-y`
;; keys in the layer options.

(-> {:x [1 2 3] :y [4 5 6]}
    (sk/lay-point :x :y {:nudge-x 0.5})
    sk/plan
    (get-in [:panels 0 :layers 0 :groups 0 :xs]))

(kind/test-last [(fn [xs] (= [1.5 2.5 3.5] xs))])

;; ## Jitter
;;
;; **Jitter** adds random offsets in drawing units to reduce
;; overplotting. Unlike position and nudge, jitter operates after
;; scaling (not in data space) and is deterministic -- seeded by a
;; hash of the group's color so repeated renders produce identical
;; output.
;;
;; On categorical x-axes, jitter is applied along the band axis only.

(merge (sk/layer-type-lookup :point) {:jitter true})

(kind/test-last [(fn [m] (true? (:jitter m)))])

;; ## Inference
;;
;; **Inference** is the automatic selection of a layer type
;; (mark + stat + position) when you bypass `sk/lay-*` and just pass
;; columns to `sk/frame` or rely on sketch-level data. Napkinsketch
;; picks a layer type based on column types: numerical x and y
;; defaults to `:point`, categorical x with numerical y to
;; `:boxplot`, a single numerical column to `:histogram`, and so on.
;; Use `:x-type` / `:y-type` on a frame or layer to override the
;; detected type.

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ## Plan
;;
;; A **plan** is the fully resolved intermediate representation --
;; a plain Clojure map containing everything needed to render a
;; plot: data-space geometry, domains, tick info, legend, layout
;; dimensions. No membrane types, no datasets, no scale objects.
;;
;; Created with `sk/plan`. Numeric arrays (`:xs`, `:ys`, etc.) are
;; [dtype-next](https://github.com/cnuernber/dtype-next) buffers for
;; efficiency.

(def my-plan (sk/plan my-frame))

(sort (keys my-plan))

(kind/test-last [(fn [ks] (every? keyword? ks))])

;; ## Panel
;;
;; A **panel** is a single plotting area within a plan. It contains
;; x/y domains, scale specs, tick info, coordinate type, and layers.
;; A simple plot has one panel; `sk/facet` and `sk/facet-grid`
;; produce multiple.

(sort (keys (first (:panels my-plan))))

(kind/test-last [(fn [ks] (some #{:x-domain :y-domain :layers} ks))])

;; ## Layer
;;
;; A **layer** is a plan-level descriptor: resolved mark type,
;; style, and groups of data-space geometry. Layers live inside
;; panels in the plan.

(-> my-frame
    sk/plan
    (get-in [:panels 0 :layers 0]))

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; ## Domain
;;
;; A **domain** is the range of data values along an axis.
;;
;; - Numerical: `[min max]` with padding (e.g., `[4.0 8.2]`)
;; - Categorical: sequence of distinct values (e.g., `["setosa" "versicolor" "virginica"]`)

(let [p (first (:panels my-plan))]
  {:x-domain (:x-domain p)
   :y-domain (:y-domain p)})

(kind/test-last [(fn [m] (and (= 2 (count (:x-domain m)))
                              (number? (first (:x-domain m)))))])

;; ## Tick
;;
;; A **tick** is an axis mark with a label at a domain value. Ticks
;; are chosen at layout time to fit the available pixel budget --
;; label widths, minimum spacing, and calendar boundaries (for
;; temporal axes) all feed into the selection. Each panel in the
;; plan carries its own `:x-ticks` and `:y-ticks` maps with parallel
;; `:values` and `:labels` vectors.

(-> my-plan :panels first :x-ticks (select-keys [:values :labels]))

(kind/test-last [(fn [m] (and (vector? (:values m))
                              (vector? (:labels m))
                              (= (count (:values m)) (count (:labels m)))))])

;; ## Scale
;;
;; A **scale** maps data values to pixel positions. Built from a
;; domain and a pixel range using [wadogo](https://github.com/scicloj/wadogo).
;;
;; | Type | Use |
;; |:-----|:----|
;; | `:linear` | Numerical data (default) |
;; | `:log` | Orders-of-magnitude data |
;; | `:categorical` | Distinct categories (band scale) |
;;
;; Scales are created at render time, not stored in the plan.
;; The plan stores scale *specs* (`:type`, `:domain`).
;;
;; **Temporal columns** (`LocalDate`, `LocalDateTime`, `Instant`,
;; `java.util.Date`) are automatically detected and treated as
;; numerical. Tick labels are calendar-aware -- snapped to year,
;; month, day, or hour boundaries depending on the time span.

;; ## Coord
;;
;; A **coord** (coordinate system) defines how data-space maps to
;; drawing units.
;;
;; | Type | Behavior |
;; |:-----|:---------|
;; | `:cartesian` | Standard: x rightward, y upward |
;; | `:flip` | Swap x and y axes |
;; | `:polar` | Radial: x as angle, y as radius |
;; | `:fixed` | Equal aspect ratio: 1 data unit = 1 data unit |

;; ## Facet
;;
;; A **facet** splits data into multiple panels by a categorical
;; column. Each panel shows a subset of the data.
;;
;; - `sk/facet` creates a row or column of panels
;; - `sk/facet-grid` creates a row-by-column grid from two columns
;;
;; By default each panel has its own domains, derived from the data
;; in that panel -- so scales are independent per panel. To force
;; shared axis ranges across panels, pin the domain explicitly with
;; `(sk/scale :x {:domain [lo hi]})` (and/or `:y`).

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species)
    sk/plan :panels count)

(kind/test-last [(fn [n] (= 3 n))])

;; ## Annotation
;;
;; An **annotation** is a non-data mark that adds visual reference
;; to a plot. Annotations are not connected to data columns -- they
;; overlay fixed positions passed via opts (`:y-intercept` or
;; `:x-intercept` for rules; `:y-min`/`:y-max` or `:x-min`/`:x-max`
;; for bands). They are regular layers, so they scope like any other
;; `lay-*` -- bare call attaches to the frame, columns attach to a
;; new leaf.
;;
;; | Constructor | What |
;; |:------------|:-----|
;; | `sk/lay-rule-v` | Vertical line at x = x-intercept |
;; | `sk/lay-rule-h` | Horizontal line at y = y-intercept |
;; | `sk/lay-band-v` | Vertical shaded region from x = x-min to x = x-max |
;; | `sk/lay-band-h` | Horizontal shaded region from y = y-min to y = y-max |

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-rule-h {:y-intercept 3.0})
    :layers first :layer-type)

(kind/test-last [(fn [m] (= :rule-h m))])

;; ## Legend
;;
;; A **legend** is generated automatically when a color (or shape)
;; aesthetic maps to a data column. It appears in the plan as a
;; `:legend` key containing entries with labels and colors.
;; Position is controlled via `{:legend-position :bottom}` in options.

(:legend my-plan)

(kind/test-last [(fn [leg] (and (map? leg)
                                (contains? leg :entries)))])

;; ## Theme
;;
;; A **theme** controls the visual appearance of non-data elements.
;; It is a nested map under `:theme` with three keys:
;;
;; | Key | Controls |
;; |:----|:---------|
;; | `:bg` | Panel background color |
;; | `:grid` | Gridline color |
;; | `:font-size` | Base font size in pixels |
;;
;; Passed as `{:theme {...}}` via `sk/options`, `sk/with-config`, or
;; `sk/set-config!`. Other visual knobs (margins, legend width, tick
;; spacing) are top-level configuration keys, not theme entries --
;; see `sk/config-key-docs`.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:theme {:bg "#2d2d2d" :grid "#444444" :font-size 10}})
    sk/svg-summary :points)

(kind/test-last [(fn [n] (= 150 n))])

;; ## Membrane
;;
;; A **membrane** is a value of the
;; [Membrane](https://github.com/phronmophobic/membrane) library --
;; a tree of layout and drawing primitives (`Translate`, `WithColor`,
;; `RoundedRectangle`, `Label`, etc.) that represents a complete plot.
;;
;; The membrane is an intermediate step in the SVG rendering path:
;; the plan becomes a membrane, which becomes SVG hiccup. Direct
;; renderers (e.g., Plotly) skip the membrane entirely.

(def my-membrane (sk/plan->membrane my-plan))

(vector? my-membrane)

(kind/test-last [true?])

(count my-membrane)

(kind/test-last [(fn [n] (pos? n))])

;; ## Plot
;;
;; A **plot** is the final rendered output -- the result of rendering
;; a plan to a specific format. For SVG, the plot is hiccup markup
;; wrapped in `kind/hiccup`.
;;
;; Created by `sk/plot` or by auto-rendering a frame.

(def my-plot (sk/plan->plot my-plan :svg {}))

(first my-plot)

(kind/test-last [(fn [v] (= :svg v))])

;; ## Palette
;;
;; A **palette** is an ordered set of colors used for categorical
;; aesthetics. When `:color` maps to a categorical column, colors
;; are assigned from the active palette in order.
;;
;; Napkinsketch uses [clojure2d](https://github.com/Clojure2D/clojure2d)
;; for palettes. Set via `{:palette :set2}` in options. The number of
;; named palettes available:

(count (c2d/find-palette #".*"))

(kind/test-last [(fn [n] (< 1000 n))])

;; ## Gradient
;;
;; A **gradient** (or color scale) maps a continuous numeric range
;; to a smooth color ramp. Used when `:color` maps to a numerical
;; column.
;;
;; Common gradients: `:viridis`, `:inferno`, `:plasma`,
;; `:magma`. Diverging gradients center on a midpoint value.
;; Set via `{:color-scale :inferno}` in options.

;; ## Configuration
;;
;; **Configuration** controls rendering behavior -- dimensions, theme,
;; palette, color scale, margins, and more. It is one of three option
;; scopes -- the others are [plot options](#plot-options) and
;; [layer options](#layer-options). Configuration follows a
;; precedence chain:
;;
;; plot options > `sk/with-config` > `sk/set-config!` > `napkinsketch.edn` > library defaults
;;
;; `napkinsketch.edn` is an optional file in your project root that provides
;; project-level defaults (e.g., a consistent palette or theme across all plots).
;;
;; See the Configuration chapter for details.

;; ## Plot Options
;;
;; **Plot options** are per-plot settings passed to `sk/options`,
;; `sk/plan`, or `sk/plot`. They include text content (title,
;; subtitle, caption, axis labels) and a nested `:config` override.
;; Unlike configuration keys, plot options are inherently per-plot --
;; a title does not make sense as a global default.
;;
;; See `sk/plot-option-docs` for the full list, or the
;; [Configuration](./napkinsketch_book.configuration.html) chapter for usage examples.

(count sk/plot-option-docs)

(kind/test-last [(fn [n] (= 11 n))])

;; ## Layer Options
;;
;; **Layer options** are per-layer settings passed in the options map
;; of layer functions (`sk/lay-point`, `sk/lay-histogram`, etc.).
;; They control aesthetics (`:color`, `:size`, `:alpha`, `:shape`),
;; grouping (`:group`), position adjustment (`:position`), and
;; layer-type-specific parameters (`:bandwidth`, `:confidence-band`,
;; `:normalize`, etc.).
;;
;; Four keys are universal -- accepted by every layer -- and each
;; layer type may accept additional keys. The
;; [Methods](./napkinsketch_book.methods.html) chapter lists which
;; options each layer type accepts. See also `sk/layer-option-docs`
;; for descriptions, or inspect a specific layer type with
;; `sk/layer-type-lookup`.

(count sk/layer-option-docs)

(kind/test-last [(fn [n] (pos? n))])

;; ## Tooltip and Brush
;;
;; A **tooltip** shows data values on hover. A **brush** enables
;; click-and-drag selection that highlights a rectangular region.
;; Both are JavaScript-based interactions added to the SVG output.
;;
;; Enabled via `{:tooltip true}` and `{:brush true}` in options.

;; ## Summary Table
;;
;; | Term | What | Key functions |
;; |:-----|:-----|:-------------|
;; | Frame | Composable value: data + mapping + layers (+ sub-frames) | All `sk/` functions return frames |
;; | Leaf frame | Frame describing one plot panel | `sk/frame`, `sk/lay-*` with columns |
;; | Composite frame | Frame containing sub-frames and a layout | `sk/arrange`, `sk/facet`, `sk/facet-grid` |
;; | Mapping | Column-to-aesthetic association on a frame or layer | `sk/frame` mapping, `sk/lay-*` opts |
;; | Layer | Method attached to a frame | `sk/lay-*` |
;; | Draft | Vector of flat maps from merging frame and layer mappings | `sk/draft`, automatic during `sk/plan` |
;; | Layer type | Mark + stat + position bundle | `sk/layer-type-lookup`, `sk/lay-*` |
;; | Mark | Visual shape: point, line, bar, area, ... | Key in layer-type map |
;; | Stat | Data transform: identity, bin, count, linear-model, density, ... | Key in layer-type map |
;; | Position | How groups share space: dodge, stack, fill, identity | Key in layer-type map |
;; | Inference | Auto-choosing mark/stat from column types | When `sk/lay-*` is omitted |
;; | Aesthetic | Data-driven visual property: color, size, alpha | Key in mapping or layer |
;; | Group | Subset of data rendered together | From `:color` or `:group` |
;; | Plan | Fully resolved plot description | `sk/plan` |
;; | Panel | One plotting area (domain, ticks, layers) | One or more per plan |
;; | Plan layer | Resolved geometry + style for one mark | Inside plan panels |
;; | Domain | Data range on an axis | Part of panel |
;; | Tick | Axis mark with label at a domain value | Part of panel |
;; | Scale | Data-to-pixel mapping (linear, log, categorical) | `sk/scale` |
;; | Coord | Coordinate system (cartesian, flip, polar, fixed) | `sk/coord` |
;; | Facet | Split into panels by a categorical column | `sk/facet`, `sk/facet-grid` |
;; | Arrange | Compose multiple frames into a grid | `sk/arrange` |
;; | Annotation | Non-data reference marks (rules, bands) | `sk/lay-rule-*`, `sk/lay-band-*` |
;; | Legend | Color/size/alpha key from aesthetic mappings | Automatic in plan |
;; | Plot options | Title, subtitle, caption, labels, dimensions | `sk/options` |
;; | Layer options | Per-layer aesthetics and layer-type parameters | `sk/lay-*` opts map |
;; | Theme | Visual styling: background, grid, fonts | `:theme` in `sk/options` |
;; | Palette | Ordered color set for categorical aesthetics | `:palette` in `sk/options` |
;; | Gradient | Continuous color ramp for numerical mappings | `:color-scale` in `sk/options` |
;; | Configuration | Global rendering defaults | `sk/config`, `sk/set-config!`, `sk/with-config` |
;; | Membrane | Drawable tree (membrane library) | Internal rendering step |
;; | Plot | Final output (SVG hiccup) | `sk/plot`, `sk/save` |
;; | Tooltip / Brush | JavaScript hover and selection interactions | `{:tooltip true}` in options |
