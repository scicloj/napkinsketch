;; # Glossary
;;
;; Key terms used throughout Napkinsketch, with brief definitions
;; and code examples.

(ns napkinsketch-book.glossary
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## View
;;
;; A **view** is a map describing what to plot: data, column mappings
;; (`:x`, `:y`), mark type, color, and other aesthetics.
;; Created with `sk/view` and refined with `sk/lay`.

(def views
  (-> iris
      (sk/view [[:sepal_length :sepal_width]])
      (sk/lay (sk/point {:color :species}))))

(kind/pprint views)

(kind/test-last [(fn [v] (and (vector? v) (= 1 (count v))))])

;; ## Mark
;;
;; A **mark** is the visual representation of data: points, lines,
;; bars, rectangles. A mark is one component of a **method** — see
;; the Method section below.

(sk/point {:color :species :alpha 0.5})

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (= :species (:color m))))])

;; ## Method
;;
;; A **method** is the bundle of mark, stat, and position that
;; determines how data becomes a visual element. Method constructors
;; (`sk/point`, `sk/line`, `sk/histogram`, `sk/bar`, `sk/lm`,
;; `sk/boxplot`, `sk/violin`, `sk/density`, etc.) each return a method.
;;
;; When you provide a method via `sk/lay`, its stat takes precedence
;; over column-type inference. When no method is provided,
;; Napkinsketch infers one from the column types.

(sk/histogram)

(kind/test-last [(fn [m] (and (= :bar (:mark m))
                              (= :bin (:stat m))))])

(sk/point)

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (= :identity (:stat m))))])

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
;;
;; When a keyword is passed, it maps to a dataset column.
;; A literal value (e.g., `"#E74C3C"`, `"red"`, `0.5`) sets a fixed aesthetic
;; for all points.

(sk/point {:color :species :size :petal_length :alpha 0.7})

(kind/test-last [(fn [m] (and (= :species (:color m))
                              (= :petal_length (:size m))
                              (= 0.7 (:alpha m))))])

;; ## Group
;;
;; A **group** is a subset of data that is processed and drawn
;; together. Mapping `:color` to a categorical column automatically
;; creates groups — one per unique value. You can also create groups
;; without color using the `:group` key.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/line {:group :species}))
    sk/sketch
    (get-in [:panels 0 :layers 0 :groups])
    count)

(kind/test-last [(fn [n] (= 3 n))])

;; ## Stat
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each method bundles a default stat:
;;
;; | Mark | Default stat | What it does |
;; |:-----|:-------------|:-------------|
;; | `point` | `:identity` | Pass through as-is |
;; | `line` | `:identity` | Pass through as-is |
;; | `histogram` | `:bin` | Bin into ranges |
;; | `bar` | `:count` | Count categories |
;; | `lm` | `:lm` | Linear regression |
;; | `loess` | `:loess` | LOESS local regression |
;; | `density` | `:kde` | Kernel density estimation |
;; | `boxplot` | `:boxplot` | Five-number summary + outliers |
;; | `violin` / `ridgeline` | `:violin` | KDE per category |
;; | `tile` | `:bin2d` | 2D grid binning (heatmap) |
;; | `density2d` | `:kde2d` | 2D Gaussian KDE (smooth heatmap) |
;; | `contour` | `:kde2d` | Iso-density contour lines (marching squares) |

;; ## Position
;;
;; A **position** adjustment determines how groups share a categorical
;; position. It runs between stat computation and rendering.
;;
;; | Position | Behavior | Default for |
;; |:---------|:---------|:------------|
;; | `:identity` | Plot at exact data coordinates (overlap OK) | point, line, text |
;; | `:dodge` | Shift groups side-by-side within a band | bar, boxplot, violin, lollipop |
;; | `:stack` | Pile groups on top of each other (cumulative y) | `sk/stacked-bar`, `sk/stacked-area` |
;; | `:fill` | Stack normalized to [0, 1] (proportions) | `sk/stacked-bar-fill` |
;;
;; Any mark can override its position via the `:position` key.
;; When multiple layers share `:position :dodge`, they are coordinated
;; together — errorbars automatically align with bars.

(def tips {:day ["Mon" "Mon" "Tue" "Tue"]
           :count [30 20 45 15]
           :meal ["lunch" "dinner" "lunch" "dinner"]})

(-> tips
    (sk/view :day :count)
    (sk/lay (sk/value-bar {:color :meal :position :stack}))
    sk/sketch
    (get-in [:panels 0 :layers 0 :groups 1 :y0s]))

(kind/test-last [(fn [y0s] (every? pos? y0s))])

;; ## Nudge
;;
;; A **nudge** shifts data coordinates by a constant offset.
;; It is orthogonal to position — you can nudge within a dodge,
;; or nudge at identity. Applied via `:nudge-x` and `:nudge-y`
;; keys on any mark.

(-> {:x [1 2 3] :y [4 5 6]}
    (sk/view :x :y)
    (sk/lay (sk/point {:nudge-x 0.5}))
    sk/sketch
    (get-in [:panels 0 :layers 0 :groups 0 :xs]))

(kind/test-last [(fn [xs] (= [1.5 2.5 3.5] xs))])

;; ## Jitter
;;
;; **Jitter** adds random pixel-space offsets to reduce overplotting.
;; Unlike position and nudge, jitter operates in pixel space (not
;; data space) and is deterministic — seeded by group color for
;; reproducibility.
;;
;; On categorical x-axes, jitter is applied along the band axis only.

(sk/point {:jitter true})

(kind/test-last [(fn [m] (true? (:jitter m)))])

;; ## Layer
;;
;; A **layer** is a sketch-level descriptor: resolved mark type,
;; style, and groups of data-space geometry. Layers live inside
;; panels in the sketch.

(let [s (sk/sketch views)
      layer (first (:layers (first (:panels s))))]
  layer)

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; ## Sketch
;;
;; A **sketch** is the fully resolved intermediate representation —
;; a plain Clojure map containing everything needed to render a plot:
;; data-space geometry, domains, tick info, legend, layout dimensions.
;; No membrane types, no datasets, no scale objects.
;;
;; Created with `sk/sketch`. Numeric arrays (`:xs`, `:ys`, etc.) are
;; dtype-next buffers for efficiency.

(def my-sketch (sk/sketch views))

(sort (keys my-sketch))

(kind/test-last [(fn [ks] (every? keyword? ks))])

;; ## Panel
;;
;; A **panel** is a single plotting area within a sketch. It contains
;; x/y domains, scale specs, tick info, coordinate type, and layers.
;; A simple plot has one panel; `sk/facet` and `sk/facet-grid` produce multiple.

(sort (keys (first (:panels my-sketch))))

(kind/test-last [(fn [ks] (some #{:x-domain :y-domain :layers} ks))])

;; ## Facet
;;
;; A **facet** splits data into multiple panels by a categorical
;; column. Each panel shows a subset of the data, sharing the same
;; scales for easy comparison.
;;
;; - `sk/facet` creates a row or column of panels
;; - `sk/facet-grid` creates a row × column grid from two columns

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point))
    (sk/facet :species)
    sk/sketch :panels count)

(kind/test-last [(fn [n] (= 3 n))])

;; ## Domain
;;
;; A **domain** is the range of data values along an axis.
;;
;; - Numerical: `[min max]` with padding (e.g., `[4.0 8.2]`)
;; - Categorical: sequence of distinct values (e.g., `["setosa" "versicolor" "virginica"]`)

(let [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p)
   :y-domain (:y-domain p)})

(kind/test-last [(fn [m] (and (= 2 (count (:x-domain m)))
                              (number? (first (:x-domain m)))))])

;; ## Scale
;;
;; A **scale** maps data values to pixel positions. Built from a
;; domain and a pixel range using wadogo.
;;
;; | Type | Use |
;; |:-----|:----|
;; | `:linear` | Numerical data (default) |
;; | `:log` | Orders-of-magnitude data |
;; | `:categorical` | Distinct categories (band scale) |
;;
;; Scales are created at render time, not stored in the sketch.
;; The sketch stores scale *specs* (`:type`, `:domain`).
;;
;; **Temporal columns** (`LocalDate`, `LocalDateTime`, `Instant`,
;; `java.util.Date`) are automatically detected and treated as
;; numerical. Tick labels are calendar-aware — snapped to year,
;; month, day, or hour boundaries depending on the time span.

;; ## Coord
;;
;; A **coord** (coordinate system) defines how data-space maps to
;; pixel-space.
;;
;; | Type | Behavior |
;; |:-----|:---------|
;; | `:cartesian` | Standard x→right, y→up |
;; | `:flip` | Swap x and y axes |
;; | `:polar` | Radial: x→angle, y→radius |

;; ## Annotation
;;
;; An **annotation** is a non-data mark that adds visual reference
;; to a plot. Annotations are not connected to data columns — they
;; overlay fixed positions.
;;
;; | Constructor | What |
;; |:------------|:-----|
;; | `sk/rule-v` | Vertical line at x = value |
;; | `sk/rule-h` | Horizontal line at y = value |
;; | `sk/band-v` | Vertical shaded region from x₁ to x₂ |
;; | `sk/band-h` | Horizontal shaded region from y₁ to y₂ |

(:mark (sk/rule-h 5))

(kind/test-last [(fn [m] (= :rule-h m))])

;; ## Legend
;;
;; A **legend** is generated automatically when a color (or shape)
;; aesthetic maps to a data column. It appears in the sketch as a
;; `:legend` key containing entries with labels and colors.
;; Position is controlled via `{:legend-position :bottom}` in options.

(:legend my-sketch)

(kind/test-last [(fn [leg] (and (map? leg)
                                (contains? leg :entries)))])

;; ## Theme
;;
;; A **theme** controls the visual appearance of non-data elements:
;; background color, grid lines, font sizes, margins.
;; Passed as `{:theme {...}}` in the options map to `sk/plot` or `sk/sketch`.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:theme {:background "#2d2d2d" :grid "#444444"
                      :text "#cccccc" :tick "#999999"}})
    sk/svg-summary :panels)

(kind/test-last [(fn [n] (= 1 n))])

;; ## Membrane (drawable tree)
;;
;; A **membrane** (drawable tree) is a tree of layout and
;; drawing primitives (`Translate`, `WithColor`, `RoundedRectangle`,
;; `Label`, etc.) that represents a complete plot.
;;
;; The membrane is an intermediate step in the SVG rendering path:
;; sketch → membrane → SVG hiccup. Direct renderers (e.g., Plotly)
;; skip the membrane entirely.

(def my-membrane (sk/sketch->membrane my-sketch))

(vector? my-membrane)

(kind/test-last [(fn [v] (true? v))])

(count my-membrane)

(kind/test-last [(fn [n] (pos? n))])

;; ## Figure
;;
;; A **figure** is the final rendered output — the result of rendering
;; a sketch to a specific format. For SVG, the figure is hiccup markup
;; wrapped in `kind/hiccup`.
;;
;; Created by `sk/plot` (which calls `sk/sketch->figure` internally).

(def my-figure (sk/sketch->figure my-sketch :svg {}))

(first my-figure)

(kind/test-last [(fn [v] (= :svg v))])

;; ## Palette
;;
;; A **palette** is an ordered set of colors used for categorical
;; aesthetics. When `:color` maps to a categorical column, colors
;; are assigned from the active palette in order.
;;
;; Napkinsketch uses clojure2d for palettes — over 7,000 named palettes
;; are available. Set via `{:palette :set2}` in options.

;; ## Gradient
;;
;; A **gradient** (or color scale) maps a continuous numeric range
;; to a smooth color ramp. Used when `:color` maps to a numerical
;; column.
;;
;; Common gradients: `:viridis` (default), `:inferno`, `:plasma`,
;; `:magma`. Diverging gradients center on a midpoint value.
;; Set via `{:color-scale :inferno}` in options.

;; ## Configuration
;;
;; **Configuration** controls rendering behavior — dimensions, theme,
;; palette, color scale, margins, and more. Configuration follows a
;; precedence chain:
;;
;; per-call options > `sk/with-config` > `sk/set-config!` > `napkinsketch.edn` > library defaults
;;
;; `napkinsketch.edn` is an optional file in your project root that provides
;; project-level defaults (e.g., a consistent palette or theme across all plots).
;;
;; See the Configuration chapter for details.

;; ## Tooltip and Brush
;;
;; A **tooltip** shows data values on hover. A **brush** enables
;; click-and-drag selection that highlights a rectangular region.
;; Both are JavaScript-based interactions added to the SVG output.
;;
;; Enabled via `{:tooltip true}` and `{:brush true}` in options.

;; ## Summary Table
;;
;; | Term | What | Lifetime |
;; |:-----|:-----|:---------|
;; | View | Map: data + column mappings + mark | User builds, consumed by `sketch` |
;; | Method | Mark + stat + position bundle | Returned by `sk/point`, `sk/histogram`, etc.; merged by `sk/lay` |
;; | Mark | Visual type: point, line, bar, ... | Key in view map |
;; | Aesthetic | Data-driven visual property: color, size, alpha, shape | Key in view map |
;; | Group | Subset of data drawn together (from `:color` or `:group`) | Created during stat computation |
;; | Stat | Data transform: identity, bin, count, lm, kde, etc. | Computed during sketch resolution |
;; | Position | How groups share space: dodge, stack, fill, identity | Applied between stat and rendering |
;; | Nudge | Constant data-space offset (`:nudge-x`, `:nudge-y`) | Applied during layer extraction |
;; | Jitter | Random pixel offset to reduce overplotting | Applied at render time |
;; | Layer | Resolved geometry + style for one mark | Lives inside sketch panels |
;; | Sketch | Complete resolved plot description | Inspectable (dtype-next buffers for numerics) |
;; | Panel | One plotting area (domain, ticks, layers) | One or more per sketch |
;; | Facet | Split data into panels by a categorical column | Configured on views, realized in sketch |
;; | Domain | Data range on an axis | Part of panel |
;; | Scale | Data → pixel mapping | Created at render time |
;; | Coord | Coordinate system (cartesian, flip, polar) | Applied at render time |
;; | Annotation | Non-data reference marks (rules, bands) | Overlay on panels |
;; | Legend | Color/shape key generated from aesthetic mappings | Part of sketch |
;; | Theme | Visual styling: background, grid, fonts, margins | Passed in options, merged with defaults |
;; | Membrane | Drawable tree (membrane library) | Intermediate (SVG path only) |
;; | Figure | Final output (SVG hiccup, Plotly spec, ...) | Returned to user |
;; | Palette | Ordered color set for categorical aesthetics | Resolved at render time |
;; | Gradient | Continuous color ramp for numerical color mappings | Resolved at render time |
;; | Configuration | Rendering options: dimensions, theme, palette, etc. | Layered precedence chain |
;; | Tooltip / Brush | JavaScript hover and selection interactions | Added to SVG output |