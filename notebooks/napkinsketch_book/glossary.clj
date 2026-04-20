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
   [scicloj.napkinsketch.api :as sk]))

;; ## Sketch
;;
;; A **sketch** is a composable record with five fields: `:data`,
;; `:mapping`, `:views`, `:layers`, and `:opts`. Every function in
;; the API (`sk/view`, `sk/lay-*`, `sk/facet`, `sk/options`, `sk/scale`,
;; `sk/coord`, `sk/annotate`) takes a sketch and returns a sketch.
;; `sk/sketch` creates a sketch and optionally sets sketch-level
;; mappings visible to all views and layers.
;; Sketches auto-render in
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible
;; tools like [Clay](https://scicloj.github.io/clay/).

(def my-sketch
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/options {:title "Iris"})))

my-sketch

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A sketch is a plain Clojure value -- inspect it with `sk/sketch?`
;; and by reaching into its fields:

(sk/sketch? my-sketch)

(kind/test-last [true?])

(count (:views my-sketch))

(kind/test-last [(fn [n] (= 1 n))])

;; ## View
;;
;; A **view** is a plain map in `sketch[:views]` that declares
;; **what** to plot -- which columns map to x and y. Each view
;; becomes one panel in the rendered plot.
;;
;; Created by `sk/view` or by `sk/lay-*` with columns.
;; A view can optionally carry its own `:layers` (view-local
;; layers). Multiple views produce multi-panel layouts.
;;
;; See [Core Concepts](./napkinsketch_book.core_concepts.html) and
;; [Sketch Rules](./napkinsketch_book.sketch_rules.html) for details.

;; ## Method
;;
;; A **method** is the bundle of mark + stat + position that determines how
;; data becomes a visual element.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for detailed tables of all
;; built-in methods, marks, stats, and positions.
;;
;; Methods come in two scopes:
;;
;; - **Global layer** -- stored in `sketch[:layers]`, applied to ALL
;;   views. Created by bare `sk/lay-*` (without columns).
;;
;; - **View-local layer** -- stored in `view[:layers]`, applied
;;   only to that view. Created by `sk/lay-*` with columns.
;;
;; Resolution: each view uses `concat(own layers, global layers)`.
;; See [Sketch Rules](./napkinsketch_book.sketch_rules.html) Rules 2-5.

;; ## Mark
;;
;; The **mark** is the visual shape drawn for each data point or group.
;; Several methods may share the same mark -- for instance, `lm` and `loess`
;; both draw lines, and `area`, `stacked-area`, and `density` all draw
;; filled regions.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for a table of all built-in marks.

;; ## Stat
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each stat takes data-space inputs and produces
;; the geometry that its mark will draw.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for a table of all built-in stats.

;; ## Position
;;
;; A **position** adjustment determines how groups share a categorical
;; axis slot. Position runs between stat computation and rendering.
;; You can override the default position by passing `:position` in
;; the layer options.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together -- error bars automatically align with bars.
;; See the [Methods](./napkinsketch_book.methods.html) chapter for a table of all built-in positions.

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
;; applicable (view, layer) pair in a sketch resolves to one draft
;; element by merging the sketch, view, and layer mappings. Draft
;; elements carry all the information the pipeline needs: data,
;; columns, mark, stat, color, grouping.
;;
;; `sk/draft` is useful for inspecting exactly what the renderer will
;; consume before any domains, ticks, or pixel math are computed.

(sk/draft my-sketch)

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
;; A literal value (e.g., `"#E74C3C"`, `"red"`, `0.5`) sets a fixed aesthetic
;; for all points.

(merge (sk/method-lookup :point) {:color :species :size :petal-length :alpha 0.7})

(kind/test-last [(fn [m] (and (= :species (:color m))
                              (= :petal-length (:size m))
                              (= 0.7 (:alpha m))))])

;; ## Group
;;
;; A **group** is a subset of data that is processed and drawn
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
;; **Jitter** adds random pixel-space offsets to reduce overplotting.
;; Unlike position and nudge, jitter operates in pixel space (not
;; data space) and is deterministic -- seeded by a hash of the group's
;; color so repeated renders produce identical output.
;;
;; On categorical x-axes, jitter is applied along the band axis only.

(merge (sk/method-lookup :point) {:jitter true})

(kind/test-last [(fn [m] (true? (:jitter m)))])

;; ## Inference
;;
;; **Inference** is the automatic selection of a method (mark + stat
;; + position) when you bypass `sk/lay-*` and just pass columns to
;; `sk/view` or rely on sketch-level data. Napkinsketch picks a method
;; based on column types: numerical x and y defaults to `:point`,
;; categorical x with numerical y to `:boxplot`, a single numerical
;; column to `:histogram`, and so on. Use `:x-type` / `:y-type` on a
;; view or layer to override the detected type.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width))

(kind/test-last [(fn [v] (pos? (:points (sk/svg-summary v))))])

;; ## Plan
;;
;; A **plan** is the fully resolved intermediate representation --
;; a plain Clojure map containing everything needed to render a plot:
;; data-space geometry, domains, tick info, legend, layout dimensions.
;; No membrane types, no datasets, no scale objects.
;;
;; Created with `sk/plan`. Numeric arrays (`:xs`, `:ys`, etc.) are
;; [dtype-next](https://github.com/cnuernber/dtype-next) buffers for efficiency.

(def my-plan (sk/plan my-sketch))

(sort (keys my-plan))

(kind/test-last [(fn [ks] (every? keyword? ks))])

;; ## Panel
;;
;; A **panel** is a single plotting area within a plan. It contains
;; x/y domains, scale specs, tick info, coordinate type, and layers.
;; A simple plot has one panel; `sk/facet` and `sk/facet-grid` produce multiple.

(sort (keys (first (:panels my-plan))))

(kind/test-last [(fn [ks] (some #{:x-domain :y-domain :layers} ks))])

;; ## Layer
;;
;; A **layer** is a plan-level descriptor: resolved mark type,
;; style, and groups of data-space geometry. Layers live inside
;; panels in the plan.

(-> my-sketch
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
;; pixel-space.
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
;; overlay fixed positions.
;;
;; | Constructor | What |
;; |:------------|:-----|
;; | `sk/rule-v` | Vertical line at x = value |
;; | `sk/rule-h` | Horizontal line at y = value |
;; | `sk/band-v` | Vertical shaded region from x1 to x2 |
;; | `sk/band-h` | Horizontal shaded region from y1 to y2 |
;;
;; **Planned refactor:** Before 0.1.0, these constructors will be
;; replaced by `sk/lay-rule-v`, `sk/lay-rule-h`, `sk/lay-band-v`,
;; `sk/lay-band-h` -- annotations become regular layers, scopable
;; like any other layer.

(:mark (sk/rule-h 5))

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
;; A **theme** controls the visual appearance of non-data elements:
;; background color, grid lines, font sizes, margins.
;; Passed as `{:theme {...}}` via `sk/options` or directly to `sk/plan`.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:theme {:background "#2d2d2d" :grid "#444444"
                         :text "#cccccc" :tick "#999999"}})
    sk/svg-summary :panels)

(kind/test-last [(fn [n] (= 1 n))])

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

;; ## Figure
;;
;; A **figure** is the final rendered output -- the result of rendering
;; a plan to a specific format. For SVG, the figure is hiccup markup
;; wrapped in `kind/hiccup`.
;;
;; Created by `sk/plot` or by auto-rendering a sketch.

(def my-figure (sk/plan->figure my-plan :svg {}))

(first my-figure)

(kind/test-last [(fn [v] (= :svg v))])

;; ## Palette
;;
;; A **palette** is an ordered set of colors used for categorical
;; aesthetics. When `:color` maps to a categorical column, colors
;; are assigned from the active palette in order.
;;
;; Napkinsketch uses clojure2d for palettes -- over 7,000 named palettes
;; are available. Set via `{:palette :set2}` in options.

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
;; method-specific parameters (`:bandwidth`, `:se`, `:normalize`, etc.).
;;
;; Four keys are universal -- accepted by every layer -- and each method
;; may accept additional keys. The [Methods](./napkinsketch_book.methods.html) chapter lists
;; which options each method accepts. See also `sk/layer-option-docs`
;; for descriptions, or inspect a specific method with `sk/method-lookup`.

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
;; | Sketch | Composable value: data + mapping + views + layers + opts | All `sk/` functions return sketches |
;; | View (sketch) | Map in `:views` declaring what to plot (column pairs) | `sk/view`, `sk/lay-*` with columns |
;; | Sketch mapping | Mappings in `:mapping` that apply to all views | `sk/sketch` opts map |
;; | Global layer | Layer in `:layers` applied to all views | `sk/lay-*` without columns |
;; | View-local layer | Layer in `view[:layers]` applied to one view | `sk/lay-*` with columns |
;; | Draft | Vector of flat maps from merging sketch, view, and layer mappings | `sk/draft`, automatic during `sk/plan` |
;; | Method | Mark + stat + position bundle | `sk/method-lookup`, `sk/lay-*` |
;; | Mark | Visual shape: point, line, bar, area, ... | Key in method map |
;; | Stat | Data transform: identity, bin, count, lm, kde, ... | Key in method map |
;; | Position | How groups share space: dodge, stack, fill, identity | Key in method map |
;; | Inference | Auto-choosing mark/stat from column types | When `sk/lay-*` is omitted |
;; | Aesthetic | Data-driven visual property: color, size, alpha | Key in mapping or layer |
;; | Group | Subset of data drawn together | From `:color` or `:group` |
;; | Plan | Fully resolved plot description | `sk/plan` |
;; | Panel | One plotting area (domain, ticks, layers) | One or more per plan |
;; | Layer | Resolved geometry + style for one mark | Inside plan panels |
;; | Domain | Data range on an axis | Part of panel |
;; | Tick | Axis mark with label at a domain value | Part of panel |
;; | Scale | Data-to-pixel mapping (linear, log, categorical) | `sk/scale` |
;; | Coord | Coordinate system (cartesian, flip, polar, fixed) | `sk/coord` |
;; | Facet | Split into panels by a categorical column | `sk/facet`, `sk/facet-grid` |
;; | Annotation | Non-data reference marks (rules, bands) | `sk/annotate` |
;; | Legend | Color/size/alpha key from aesthetic mappings | Automatic in plan |
;; | Plot options | Title, subtitle, caption, labels, dimensions | `sk/options` |
;; | Layer options | Per-layer aesthetics and method parameters | `sk/lay-*` opts map |
;; | Theme | Visual styling: background, grid, fonts | `:theme` in `sk/options` |
;; | Palette | Ordered color set for categorical aesthetics | `:palette` in `sk/options` |
;; | Gradient | Continuous color ramp for numerical mappings | `:color-scale` in `sk/options` |
;; | Configuration | Global rendering defaults | `sk/config`, `sk/set-config!`, `sk/with-config` |
;; | Membrane | Drawable tree (membrane library) | Internal rendering step |
;; | Figure | Final output (SVG hiccup) | `sk/plot`, `sk/save` |
;; | Arrange | Compose multiple sketches into a grid | `sk/arrange` |
;; | Tooltip / Brush | JavaScript hover and selection interactions | `{:tooltip true}` in options |
