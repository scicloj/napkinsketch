;; # Glossary
;;
;; Key terms used throughout Plotje, with brief definitions
;; and code examples.

(ns plotje-book.glossary
  (:require
   ;; Datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; clojure2d -- color palettes and gradients
   [clojure2d.color :as c2d]))

;; ## Pose
;;
;; A **pose** is the composable value in Plotje. A leaf pose
;; describes one plot panel; a composite pose contains sub-poses
;; arranged together. Every function in the API (`pj/pose`,
;; `pj/lay-*`, `pj/facet`, `pj/arrange`, `pj/options`, `pj/scale`,
;; `pj/coord`) takes a pose and returns a pose.
;; Poses auto-render in
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible
;; tools like [Clay](https://scicloj.github.io/clay/).

(def my-pose
  (-> (rdatasets/datasets-iris)
      (pj/lay-point :sepal-length :sepal-width {:color :species})
      (pj/options {:title "Iris"})))

my-pose

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; A pose is a plain Clojure value -- inspect it with `kind/pprint`
;; or reach into its fields directly.

;; ## Leaf Pose
;;
;; A **leaf pose** is a pose that describes a single plot panel.
;; It carries `:data`, a `:mapping` from columns to aesthetics, and
;; `:layers` -- the chart-type layers attached to it. Created by
;; `pj/pose` or `pj/lay-*`.

;; ## Composite Pose
;;
;; A **composite pose** is a pose that contains other poses under
;; `:poses` plus an optional `:layout`. Created by `pj/arrange` (and
;; later, by `pj/mosaic` and `pj/with-marginals`). Its leaves render
;; independently and are tiled into the final plot.
;;
;; For hand-built composite maps (needed when you want features beyond
;; what `pj/arrange` offers -- unequal weights, nested poses,
;; cross-sibling shared scales), wrap the map with `pj/prepare-pose`
;; so it carries the Kindly metadata needed to auto-render.

;; ## Prepare Pose
;;
;; `pj/prepare-pose` lifts a hand-built pose map (leaf or composite)
;; to a first-class pose value. It coerces `:data` at every depth,
;; captures the current configuration for render-time restoration,
;; and attaches Kindly metadata so the pose auto-renders in notebook
;; viewers. Use it when you construct a composite pose by literal
;; map and want it to behave like one built with `pj/pose` or
;; `pj/arrange`.

;; ## Layer Type
;;
;; A **layer type** is the bundle of mark + stat + position that
;; determines how data becomes a visual element. See the
;; [Layer Types](./plotje_book.layer_types.html) chapter for detailed
;; tables of all built-in layer types, marks, stats, and positions.
;;
;; Layer types attach to poses in three ways, depending on what you
;; pass to `pj/lay-*`:
;;
;; - **Bare** -- `pj/lay-*` without columns attaches the layer so it
;;   sees the current pose's mapping (inherited from `pj/pose` or
;;   a prior `pj/lay-*`).
;; - **Matching columns** -- `pj/lay-*` with columns that match the
;;   most recent matching leaf reuses that leaf, so the new layer
;;   joins the existing panel.
;; - **Non-matching columns** -- `pj/lay-*` with columns that do not
;;   match any existing leaf creates a fresh leaf pose with the
;;   layer attached.

;; ## Mark
;;
;; The **mark** is the visual shape shown for each data point or
;; group. Several layer types may share the same mark -- for
;; instance, `:line` and `:smooth` both produce lines, and `:area`
;; and `:density` both produce filled regions.
;; See the [Layer Types](./plotje_book.layer_types.html) chapter for
;; a table of all built-in marks.

;; ## Stat
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each stat takes data-space inputs and produces the
;; geometry that its mark will show.
;; See the [Layer Types](./plotje_book.layer_types.html) chapter for
;; a table of all built-in stats.

;; ## Position
;;
;; A **position** adjustment determines how groups share a categorical
;; axis slot. Position runs between stat computation and rendering.
;; You can override the default position by passing `:position` in
;; the layer options.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together -- error bars automatically align with bars.
;; See the [Layer Types](./plotje_book.layer_types.html) chapter for
;; a table of all built-in positions.

(def tips {:day ["Mon" "Mon" "Tue" "Tue"]
           :count [30 20 45 15]
           :meal ["lunch" "dinner" "lunch" "dinner"]})

(-> tips
    (pj/lay-value-bar :day :count {:color :meal :position :stack})
    pj/plan
    (get-in [:panels 0 :layers 0 :groups 1 :y0s]))

(kind/test-last [(fn [y0s] (every? pos? y0s))])

;; ## Draft
;;
;; A **draft** is a vector of flat maps produced by `pj/draft`. Each
;; applicable layer in a pose resolves to one draft element by
;; merging the pose and layer mappings. Draft elements carry all
;; the information the pipeline needs: data, columns, mark, stat,
;; color, grouping.
;;
;; `pj/draft` is useful for inspecting exactly what the renderer
;; will consume before any domains, ticks, or coordinate math are
;; computed.

(-> my-pose pj/draft kind/pprint)

(kind/test-last [(fn [d] (and (vector? d)
                              (= 1 (count d))
                              (= :point (:mark (first d)))))])

;; ## Aesthetic
;;
;; An **aesthetic** is a visual property of a mark that can be mapped
;; to a data column. Plotje supports these aesthetic mappings:
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

(merge (pj/layer-type-lookup :point) {:color :species :size :petal-length :alpha 0.7})

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
    (pj/lay-line :sepal-length :sepal-width {:group :species})
    pj/plan
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
    (pj/lay-point :x :y {:nudge-x 0.5})
    pj/plan
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

(merge (pj/layer-type-lookup :point) {:jitter true})

(kind/test-last [(fn [m] (true? (:jitter m)))])

;; ## Inference
;;
;; **Inference** is the automatic selection of a layer type
;; (mark + stat + position) when you bypass `pj/lay-*` and just pass
;; columns to `pj/pose`. Plotje picks a layer type based on
;; column types: numerical x and y defaults to `:point`, categorical
;; x with numerical y to `:boxplot`, a single numerical column to
;; `:histogram`, and so on. Use `:x-type` / `:y-type` on a pose or
;; layer to override the detected type.

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width)
    pj/lay-point)

(kind/test-last [(fn [v] (pos? (:points (pj/svg-summary v))))])

;; ## Plan
;;
;; A **plan** is the fully resolved intermediate representation --
;; a plain Clojure map containing everything needed to render a
;; plot: data-space geometry, domains, tick info, legend, layout
;; dimensions. No membrane types, no datasets, no scale objects.
;;
;; Created with `pj/plan`. Numeric arrays (`:xs`, `:ys`, etc.) are
;; [dtype-next](https://github.com/cnuernber/dtype-next) buffers for
;; efficiency.

(def my-plan (pj/plan my-pose))

(sort (keys my-plan))

(kind/test-last [(fn [ks] (every? keyword? ks))])

;; ## Panel
;;
;; A **panel** is a single plotting area within a plan. It contains
;; x/y domains, scale specs, tick info, coordinate type, and layers.
;; A simple plot has one panel; `pj/facet` and `pj/facet-grid`
;; produce multiple.

(sort (keys (first (:panels my-plan))))

(kind/test-last [(fn [ks] (some #{:x-domain :y-domain :layers} ks))])

;; ## Layer
;;
;; A **layer** is a layer type placed on a pose, optionally with
;; scoped mappings. Created by `pj/lay-*`. Layers attach to a pose
;; either at the root (`(pj/lay-point pose)`) where they flow to
;; every leaf, or with columns (`(pj/lay-point pose :x :y)`) where
;; they attach to the matching leaf.

(-> my-pose :layers first :layer-type)

(kind/test-last [(fn [k] (= :point k))])

;; ## Plan Layer
;;
;; A **plan layer** is the resolved descriptor inside a plan panel:
;; resolved mark type, style, and groups of data-space geometry.
;; The user-level layer becomes the plan layer through `pj/plan`.

(-> my-pose
    pj/plan
    (get-in [:panels 0 :layers 0]))

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; ## Dataset
;;
;; A **dataset** is the tabular data backing a plot. Plotje uses
;; [Tablecloth](https://scicloj.github.io/tablecloth/) datasets
;; internally; raw input (a map of `{column-name [values]}`, a
;; sequence of row-maps, or a CSV/URL string) is coerced via
;; `tablecloth.api/dataset` at construction time. The dataset lives
;; on a pose under `:data`; per-layer and per-sub-pose `:data`
;; overrides are also supported.

;; ## Pipeline
;;
;; The **pipeline** is the five-stage flow from user code to
;; rendered output: `pose -> draft -> plan -> membrane -> figure`.
;; A pose is what you compose; `pj/draft` flattens it into a vector
;; of maps; `pj/plan` resolves geometry and layout; the membrane
;; layer turns the plan into drawable primitives; the figure is the
;; terminal SVG hiccup or PNG output. See the
;; [Architecture](./plotje_book.architecture.html) chapter for the
;; per-stage details.

;; ## Sub-plot
;;
;; A **sub-plot** is one resolved sub-pose in a composite pose's
;; plan. Where a plain leaf-pose's plan carries `:panels` (one per
;; faceted variant), a composite pose's plan carries `:sub-plots`
;; (one per sub-pose), each with its own nested `:plan` map. The
;; compositor reads `:sub-plots` and tiles their rendered membranes
;; into the final canvas.

;; ## Resolve Tree
;;
;; The **resolve tree** is the scope-merge walk that propagates a
;; root pose's `:mapping`, `:opts`, and root-attached `:layers`
;; downward into every descendant leaf during plan construction.
;; Lower (narrower) scopes override higher ones; root-attached
;; layers reach every applicable leaf in the tree.

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
;; - `pj/facet` creates a row or column of panels
;; - `pj/facet-grid` creates a row-by-column grid from two columns
;;
;; By default each panel has its own domains, derived from the data
;; in that panel -- so scales are independent per panel. To force
;; shared axis ranges across panels, pin the domain explicitly with
;; `(pj/scale :x {:domain [lo hi]})` (and/or `:y`).

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/facet :species)
    pj/plan :panels count)

(kind/test-last [(fn [n] (= 3 n))])

;; ## Annotation
;;
;; An **annotation** is a non-data mark that adds visual reference
;; to a plot. Annotations are not connected to data columns -- they
;; overlay fixed positions passed via opts (`:y-intercept` or
;; `:x-intercept` for rules; `:y-min`/`:y-max` or `:x-min`/`:x-max`
;; for bands). They are regular layers, so they attach under the
;; same three cases as any `lay-*`: bare call sits on the pose,
;; matching columns join the most recent matching leaf, non-matching
;; columns create a new leaf.
;;
;; | Constructor | What |
;; |:------------|:-----|
;; | `pj/lay-rule-v` | Vertical line at x = x-intercept |
;; | `pj/lay-rule-h` | Horizontal line at y = y-intercept |
;; | `pj/lay-band-v` | Vertical shaded region from x = x-min to x = x-max |
;; | `pj/lay-band-h` | Horizontal shaded region from y = y-min to y = y-max |

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-rule-h {:y-intercept 3.0})
    :layers (nth 1) :layer-type)

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
;; Passed as `{:theme {...}}` via `pj/options`, `pj/with-config`, or
;; `pj/set-config!`. Other visual knobs (margins, legend width, tick
;; spacing) are top-level configuration keys, not theme entries --
;; see `pj/config-key-docs`.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:theme {:bg "#2d2d2d" :grid "#444444" :font-size 10}})
    pj/svg-summary :points)

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

(def my-membrane (pj/plan->membrane my-plan))

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
;; Created by `pj/plot` or by auto-rendering a pose.

(def my-plot (pj/plan->plot my-plan :svg {}))

(first my-plot)

(kind/test-last [(fn [v] (= :svg v))])

;; ## Palette
;;
;; A **palette** is an ordered set of colors used for categorical
;; aesthetics. When `:color` maps to a categorical column, colors
;; are assigned from the active palette in order.
;;
;; Plotje uses [clojure2d](https://github.com/Clojure2D/clojure2d)
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
;; plot options > `pj/with-config` > `pj/set-config!` > `plotje.edn` > library defaults
;;
;; `plotje.edn` is an optional file in your project root that provides
;; project-level defaults (e.g., a consistent palette or theme across all plots).
;;
;; See the Configuration chapter for details.

;; ## Plot Options
;;
;; **Plot options** are per-plot settings passed to `pj/options`,
;; `pj/plan`, or `pj/plot`. They include text content (title,
;; subtitle, caption, axis labels) and a nested `:config` override.
;; Unlike configuration keys, plot options are inherently per-plot --
;; a title does not make sense as a global default.
;;
;; See `pj/plot-option-docs` for the full list, or the
;; [Configuration](./plotje_book.configuration.html) chapter for usage examples.

(count pj/plot-option-docs)

(kind/test-last [(fn [n] (= 13 n))])

;; ## Layer Options
;;
;; **Layer options** are per-layer settings passed in the options map
;; of layer functions (`pj/lay-point`, `pj/lay-histogram`, etc.).
;; They control aesthetics (`:color`, `:size`, `:alpha`, `:shape`),
;; grouping (`:group`), position adjustment (`:position`), and
;; layer-type-specific parameters (`:bandwidth`, `:confidence-band`,
;; `:normalize`, etc.).
;;
;; Four keys are universal -- accepted by every layer -- and each
;; layer type may accept additional keys. The
;; [Layer Types](./plotje_book.layer_types.html) chapter lists which
;; options each layer type accepts. See also `pj/layer-option-docs`
;; for descriptions, or inspect a specific layer type with
;; `pj/layer-type-lookup`.

(count pj/layer-option-docs)

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
;; | Pose | Composable value: data + mapping + layers (+ sub-poses) | All `pj/` functions return poses |
;; | Leaf pose | Pose describing one plot panel | `pj/pose`, `pj/lay-*` with columns |
;; | Composite pose | Pose containing sub-poses and a layout | `pj/arrange`, `pj/facet`, `pj/facet-grid` |
;; | Mapping | Column-to-aesthetic association on a pose or layer | `pj/pose` mapping, `pj/lay-*` opts |
;; | Layer | Layer type attached to a pose, optionally with scoped mappings | `pj/lay-*` |
;; | Dataset | Tabular data backing a plot (Tablecloth) | `:data` slot, `pj/with-data` |
;; | Pipeline | Five-stage flow `pose -> draft -> plan -> membrane -> figure` | Architecture chapter |
;; | Sub-plot | One resolved sub-pose in a composite pose's plan | `:sub-plots` in plan |
;; | Resolve tree | Scope-merge walk: root mappings propagate to every leaf | Internal to `pj/plan` |
;; | Draft | Vector of flat maps from merging pose and layer mappings | `pj/draft`, automatic during `pj/plan` |
;; | Layer type | Mark + stat + position bundle | `pj/layer-type-lookup`, `pj/lay-*` |
;; | Mark | Visual shape: point, line, bar, area, ... | Key in layer-type map |
;; | Stat | Data transform: identity, bin, count, linear-model, density, ... | Key in layer-type map |
;; | Position | How groups share space: dodge, stack, fill, identity | Key in layer-type map |
;; | Inference | Auto-choosing mark/stat from column types | When `pj/lay-*` is omitted |
;; | Aesthetic | Data-driven visual property: color, size, alpha | Key in mapping or layer |
;; | Group | Subset of data rendered together | From `:color` or `:group` |
;; | Plan | Fully resolved plot description | `pj/plan` |
;; | Panel | One plotting area (domain, ticks, layers) | One or more per plan |
;; | Plan layer | Resolved geometry + style for one mark | Inside plan panels |
;; | Domain | Data range on an axis | Part of panel |
;; | Tick | Axis mark with label at a domain value | Part of panel |
;; | Scale | Data-to-pixel mapping (linear, log, categorical) | `pj/scale` |
;; | Coord | Coordinate system (cartesian, flip, polar, fixed) | `pj/coord` |
;; | Facet | Split into panels by a categorical column | `pj/facet`, `pj/facet-grid` |
;; | Arrange | Compose multiple poses into a grid | `pj/arrange` |
;; | Annotation | Non-data reference marks (rules, bands) | `pj/lay-rule-*`, `pj/lay-band-*` |
;; | Legend | Color/size/alpha key from aesthetic mappings | Automatic in plan |
;; | Plot options | Title, subtitle, caption, labels, dimensions | `pj/options` |
;; | Layer options | Per-layer aesthetics and layer-type parameters | `pj/lay-*` opts map |
;; | Theme | Visual styling: background, grid, fonts | `:theme` in `pj/options` |
;; | Palette | Ordered color set for categorical aesthetics | `:palette` in `pj/options` |
;; | Gradient | Continuous color ramp for numerical mappings | `:color-scale` in `pj/options` |
;; | Configuration | Global rendering defaults | `pj/config`, `pj/set-config!`, `pj/with-config` |
;; | Membrane | Drawable tree (membrane library) | Internal rendering step |
;; | Plot | Final output (SVG hiccup) | `pj/plot`, `pj/save` |
;; | Tooltip / Brush | JavaScript hover and selection interactions | `{:tooltip true}` in options |
