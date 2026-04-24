# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Retired: sk/sketch, sk/view, and impl/sketch.clj

The Sketch record, its constructors, and its adapter surface are
gone. The frame substrate is now the only internal spec type.

Removed user-facing API:

- `sk/sketch`, `sk/view`, `sk/sketch?` -- use `sk/frame`, `sk/lay-*`,
  and `sk/frame?` instead.
- The `Sketch` record itself (`impl/sketch.clj`, ~348 lines).

Internal changes the deletion required:

- `sk/lay-*` on raw data now coerces through `sk/frame` and returns a
  frame; previously the adapter returned a Sketch record.
- `sk/with-data`, `sk/options`, `sk/scale`, `sk/coord`, `sk/save`, and
  `sk/save-png` all coerce non-frame inputs via a new private
  `ensure-frame` helper (raw data -> leaf frame) rather than the old
  `ensure-sk` (raw data -> Sketch).
- `sk/facet` and `sk/facet-grid` write to the frame's `:opts` instead
  of routing through `ensure-sk`. The facet-expansion logic moved
  from `impl/sketch.clj`'s `expand-facets` into
  `impl/frame/leaf->draft`: when a leaf's `:opts` carry
  `:facet-col`/`:facet-row`, the draft is multiplied across distinct
  facet values, each variant carrying filtered `:data` and a
  `:facet-col`/`:facet-row` label that `plan.clj` detects to build
  the facet grid. Composite frames are rejected explicitly (see
  `dev-notes/facet-composite-deferral.md`).
- `impl/sketch_schema.clj` renamed to `impl/plan_schema.clj` -- it
  was misnamed; the namespace holds Malli schemas for the plan data
  model, not the Sketch record.
- Multi-column `sk/lay-*` (`(sk/lay-histogram data [:a :b :c])`) now
  builds a multi-pair composite via `sk/frame` and attaches a bare
  layer at the root; the layer flows to every panel via
  `resolve-tree`. Previously this path created N views with N
  identical layers.
- The `sketch_rules.clj` notebook was retired -- `frame_rules.clj`
  is the live replacement.

Known shape changes from the retirement:

- `(sk/lay-point data :x :y {:color :g})` now places `:color` on the
  layer's own `:mapping`, not on the frame's root `:mapping`. Semantic
  behaviour at render time is unchanged.
- Chaining two `sk/lay-*` calls with different position columns on
  the same leaf no longer silently creates two panels; both layers
  attach to the same leaf. To create two panels, promote explicitly
  via `(sk/frame data [[:a :b] [:c :d]])`.

### Frame-native pipeline (Phase 6 slice 2)

The frame substrate now runs the full draft emission pipeline on its
own. `sk/plan`, `sk/plot`, and `sk/draft` read a leaf frame directly
via a new `impl.frame/leaf->draft`; the compositor reads each
resolved leaf the same way. The Sketch detour
(`leaf-frame->sketch` -> `sketch->draft`) is no longer on the frame
path.

User-visible behavior:

- Fixes an edge case where a leaf frame with `:x`/`:y` only on a
  layer (not on the frame's own `:mapping`) used to emit an empty
  draft. The layer's position is now read and the draft carries one
  entry per layer.
- `sk/with-data` accepts frames directly and keeps them as frames
  (it used to force frames through the Sketch adapter, returning a
  Sketch record).
- `sk/frame` gains a multi-pair arity: `(sk/frame fr [[:a :b]
  [:c :d] ...])` and `(sk/frame fr [:a :b :c])` append panels in
  one call.
- `(sk/frame fr (sk/cross cols cols))` builds a SPLOM: when the
  pairs form an M x N Cartesian rectangle, the result is a nested
  rows-of-cols composite with `:share-scales #{:x :y}`. The
  compositor renders one shared legend on the right (when the
  composite root carries a color/size/alpha aesthetic), suppresses
  x-axis labels on non-bottom rows and y-axis labels on non-leftmost
  columns, and lets per-cell inference pick the layer type --
  scatter off-diagonal, histogram on the diagonal where x = y
  (matching the legacy sk/view SPLOM behaviour). The idiomatic SPLOM
  omits `sk/lay-point`:
  ```clojure
  (-> data
      (sk/frame {:color :species})
      (sk/frame (sk/cross cols cols)))
  ```

User-facing examples across the book and gallery have been migrated
to `sk/frame`. SPLOM examples in gallery, scatter, faceting,
customization, and edge_cases all use the new frame-native pattern.

Also fixed: `promote-leaf` was dropping `:opts` when the leaf got
wrapped into a composite. Plot-level options set via `sk/options`
before promotion now correctly carry to the new composite's root.

### Breaking: `sk/arrange` returns a composite frame

`sk/arrange` now returns a composite frame (a plain-map value) instead
of CSS-grid hiccup. The composite renders through the membrane
rendering pipeline, so both `:svg` (default) and `:bufimg` targets work
the same way they do for single plots.

- Inputs must be sketches or leaf frames. Pre-rendered hiccup is no
  longer accepted -- combine hiccup yourself with `[:div ...]` if you
  want raw hiccup composition.
- `:gap` (CSS string) is removed. Leaves tile tightly; add spacing
  later at the composite level if needed.
- `:title` still works; it renders as a centered band above the grid.
- New option `:share-scales` (default `#{}`) shares x/y domains across
  arranged cells when set to a subset of `#{:x :y}`.

Migration for the common case:

```clojure
;; Before
(sk/arrange [(sk/plot sk-a) (sk/plot sk-b)])

;; After
(sk/arrange [sk-a sk-b])
```

The composite frame auto-renders in notebooks via `kind/fn`, and
passing it to `sk/plot` returns SVG hiccup as before.

### Facet deferral (internal)

The pre-alpha refactor plan listed `sk/facet` and `sk/facet-grid` for
migration onto the composite substrate in this slice. We chose to
defer -- see `dev-notes/facet-composite-deferral.md` for the rationale
(unified-membrane guarantee, Option C for shared chrome).

## [0.1.0] - 2026-04-21

First public alpha release. The API and visual defaults are still
subject to change based on early adopter feedback.

### Breaking: annotations are now layers

Reference lines and shaded bands used to live in `[:opts :annotations]`,
applied uniformly to every panel, and were attached with a separate
`sk/annotate` wrapper around `sk/rule-h`/`sk/rule-v`/`sk/band-h`/`sk/band-v`.
This release replaces that entire path with first-class layers:

- `sk/lay-rule-h` / `sk/lay-rule-v` -- horizontal / vertical reference line
- `sk/lay-band-h` / `sk/lay-band-v` -- horizontal / vertical shaded band

These are ordinary `lay-*` functions: bare arity is sketch-scope (every
panel), the four-argument arity with column refs is view-scope (only
panels matching the view's x/y mapping), and the position lives in the
opts map (`:y-intercept` for `lay-rule-h`, `:x-intercept` for
`lay-rule-v`, `:y-min`/`:y-max` for `lay-band-h`, `:x-min`/`:x-max` for
`lay-band-v`). Appearance aesthetics (`:color`, `:alpha`) are literal
values in the same opts map.

The old API is removed: `sk/annotate`, `sk/rule-h`, `sk/rule-v`,
`sk/band-h`, `sk/band-v`, and the `[:opts :annotations]` slot no longer
exist. Migration is mechanical -- replace each call with the corresponding
`sk/lay-*` form. Column-mapped positions (one rule per row, ggplot2's
`geom_hline(aes(yintercept=...))`) are deferred to a future milestone;
literal positions only in this release.

### Breaking: `:width`/`:height` are now total SVG dimensions

Prior to this release, `:width` and `:height` in plot options
referred to the **panel drawing area** only -- the actual SVG came
out wider and taller than the user asked for, once axis labels,
legends, and facet strips were added. This never matched the
convention in matplotlib, plotly, vega-lite, or ggplot2's `ggsave`.

As of this release, `:width` and `:height` are the **total SVG
dimensions**. Panel dimensions are derived by subtracting layout
overhead (y-axis label + tick labels, x-axis label, title, legend,
facet strips) from the total. The plot you ask for is the plot you
get.

Two new escape-hatch options, `:panel-width` and `:panel-height`,
pin the panel size on their axis when you do want a specific panel
size regardless of overhead. Either or both can be set.

The new semantics apply to every layout type: single-panel plots,
facet grids, SPLOMs, and `sk/arrange` dashboards all honor the
total `:width`/`:height` as a hard bound. SPLOMs in particular
used to use `:panel-size` from cfg and silently ignore
`:width`/`:height`; now they derive panel dimensions from the
total the same way faceted layouts do. Large SPLOMs (6+ variables)
at the default 600x400 will end up with small panels -- bump
`:width`/`:height` or pin `:panel-width`/`:panel-height`
explicitly. The `:panel-size` cfg key still exists for backwards
compatibility but is no longer consulted by the layout pipeline.

`sk/arrange` defaults `:width`/`:height` from cfg (600 and 400)
when they aren't passed, always re-plans sub-sketches at the
derived per-cell size so text stays at native resolution, and
wraps the result in a fixed-width CSS grid. Pre-rendered hiccup
plots pass through unchanged and inherit the CSS grid cell size.
To restore the old "each plot at its own full size" behavior,
pre-render each sketch with `sk/plot` before passing to arrange.

The layout pipeline was rewritten around three pure functions
(`compute-scene` / `compute-padding` / `compute-dims`, in
`impl/layout.clj`) that feed off the data-derived scene and never
touch pixel math twice. A single reformulation -- running the tick
picker at a pixel budget equal to `:height` instead of the unknown
real panel height -- breaks the classic
`y-label-pad ↔ panel-width` cycle. Label widths are monotonic
non-decreasing in tick count for every supported scale type, so
the over-estimate is always safe. See
`dev-notes/design-width-inference.md` for the full design.

Most existing plots shrink visibly (panels previously ~600x400 now
render around ~560x368 at the default total size). Generated tests
count panel/point/line counts rather than pixel positions, so the
test suite passes with no changes beyond updating a handful of
explicit width assertions.

### Visual default changes

Alongside the width/height rewrite, the inner spacing was tuned
down after visual review:

- `:margin` **30 -> 10** (inner panel breathing room)
- `:margin-multi` **30 -> 10** (inner padding for multi-panel layouts)

`:point-radius` stays at 3.0 (the original default). Tick labels
are drawn outside the panel (in `x-label-pad` / `y-label-pad`),
so `:margin` only reserves breathing room for point radius -- 10px
is plenty.

Legend text font sizes were rebalanced: title 11 (was 9), entries
10 (unchanged), continuous endpoints 10 (was 8). The legend title
no longer clips at the top of the canvas.

Tick-label positioning now emits proper SVG `text-anchor` --
`"middle"` for x-axis tick labels, `"end"` for y-axis tick labels.
The previous approach guessed at label width from a char-count
heuristic and offset the translation; the browser now handles
centering with exact glyph metrics.

### Palette vs color-scale

Passing a continuous-gradient keyword (`:viridis`, `:inferno`,
`:plasma`, `:magma`, `:turbo`, `:rocket`, `:mako`, `:cividis`,
`:RdBu`, `:RdYlBu`, `:BrBG`, `:coolwarm`) to `:palette` on
categorical data now prints a warning pointing at `:color-scale`
(the canonical continuous-color path) or at a designed-discrete
palette like `:set1`, `:dark2`, or `:tableau-10`. Unknown palette
keywords (typos like `:tableu-10`) also warn and identify the
fallback instead of silently returning the default.

The spread-index palette sampling from an earlier draft was
reverted. Authorial ordering of discrete palettes (the first N
colors of `:set1` etc.) is preserved, so existing categorical
plots look identical to pre-rewrite renders.

### Overview

Napkinsketch is a composable plotting library for Clojure, inspired by
the Grammar of Graphics. Plots are built by threading data through a
sequence of small transformations. The resulting frame is a plain
Clojure value that auto-renders in Kindly-compatible notebooks (Clay
and friends) -- no explicit render call required.

### Features

**Composable pipeline.** Everything you do to build a plot -- create
a frame (`sk/frame`), add layers (`sk/lay-*`), set aesthetic
mappings, apply scales and coordinate transforms, add facets, attach
annotations, set plot options -- flows through `->`. Every function
takes a frame and returns a frame, so there is no plot-assembly
order to memorize.

**Scoped mappings.** Aesthetic mappings (color, size, alpha, shape,
group, text) flow down a frame's tree from the root to every leaf
and then into each layer. Lower scopes override higher ones; `nil`
is an explicit cancellation. Scope is lexical: you set a mapping
where you want it to apply.

**29 layer functions** covering the common chart types: point, line,
step, area, stacked-area, histogram, bar, stacked-bar,
stacked-bar-fill, value-bar, lm (linear model), loess, text, label,
density, tile, density2d, contour, boxplot, violin, ridgeline, rug,
summary (mean + SE), errorbar, lollipop, plus four annotation layers
(lay-rule-h, lay-rule-v, lay-band-h, lay-band-v).

**Statistics built in.** Histogram binning, categorical counts, OLS
regression (with optional confidence ribbons), LOESS with bootstrap
confidence bands, 1D and 2D kernel density estimation (the 2D case
uses Silverman's rule for the product-kernel bandwidth), 2D binning
for heatmaps, boxplot five-number summary with R type 7 quartiles,
violin density per category, and mean + standard error summaries.
Every stat is a multimethod — you can register new ones.

**Faceting.** `sk/facet` for single-variable paneling (one row or
column of panels), `sk/facet-grid` for row x column grids, `sk/cross`
as a helper for building scatter-plot matrices (SPLOM), and
`sk/distribution` for the diagonal of a SPLOM (univariate
distributions).

**Coordinate systems.** Cartesian (default), flip, polar, and fixed
(1:1 aspect ratio).

**Scales.** Linear, log, categorical, and datetime — type is
auto-detected from the column and can be overridden via `sk/scale`.

**Continuous and categorical color.** Numeric columns map to a
continuous gradient (ggplot2-style dark-to-light blue by default;
`:viridis`, `:inferno`, `:plasma`, `:magma`, `:turbo`, etc. available
via `:color-scale`) with a color bar legend. Categorical columns draw
from one of ~7000 palettes exposed by clojure2d. Diverging color
scales with custom midpoints are also supported.

**Annotations.** Reference lines (`sk/lay-rule-h`, `sk/lay-rule-v`)
and shaded bands (`sk/lay-band-h`, `sk/lay-band-v`) as first-class
layers. Sketch-scope annotations apply to every panel; view-scope
annotations apply only to panels matching the view's x/y mapping.

**Per-scope data override.** `:data` can be set at sketch, view, or
layer level for mixed-data composites like
`layer A with dataset 1 + layer B with dataset 2`.

**Flexible input.** Tablecloth datasets, plain maps of columns,
sequences of row maps, or CSV/URL paths. Column names can be keywords
or strings. Literal colors can be hex strings, named CSS colors, or
keywords.

**Two render backends.** SVG (default, via membrane) for notebooks
and the web; raster PNG via `:format :bufimg` for large scatters or
Java2D-based export. Both are exercised by the same plan -- only the
terminal stage differs.

**Multi-plot dashboards.** `sk/arrange` composes several plots into a
CSS grid, with optional titles and flexible row/column layouts.

**Interactivity.** Opt-in hover tooltips (`{:tooltip true}`) and
drag-to-select brushes (`{:brush true}`) via lightweight inline
JavaScript.

**Layered configuration.** Per-plot options > thread-local
`with-config` > global `set-config!` > `napkinsketch.edn` > library
defaults. Theme values deep-merge so partial overrides don't clobber
the rest.

**Clear errors on natural mistakes.** Missing or typoed column
references, mixed-type columns, unknown methods, unknown options on
`view`/`sketch`/`options`/`lay-*`, categorical columns passed to
numeric stats, and numeric x passed to categorical marks all fail
with a clear, actionable error pointing at the offending input.

**Plan inspection.** `sk/plan` returns a plain Clojure data structure
(domains, ticks, legends, layout, resolved layers) that can be
inspected, serialized, or re-rendered by alternative backends.
`sk/svg-summary`, `sk/valid-plan?`, and `sk/explain-plan` support
structural testing.

**Extensibility.** Eight multimethods -- `compute-stat`,
`extract-layer`, `layer->membrane`, `make-scale`, `make-coord`,
`apply-position`, `plan->plot`, `membrane->plot` -- are open
extension points. The `waterfall_extension` chapter in the book walks
through a complete end-to-end custom mark.

### Book

Twenty-eight executable chapters:

- **Getting Started** — quickstart
- **Foundations** — datasets, sketch model, core concepts, options
  and scopes, sketch rules, inference rules, methods, glossary
- **Chart Types** — scatter, distributions, ranking, change over
  time, relationships, polar
- **How-to Guides** — cookbook, configuration, customization,
  faceting, troubleshooting
- **Reference** — api reference
- **Gallery** — ~140 examples reproducing charts from the R Graph
  Gallery, Vega-Lite Examples, Python Graph Gallery, ECharts, and D3
- **Internals** — exploring plans, architecture, extensibility,
  waterfall extension, edge cases, development

Every notebook embeds runnable regression tests via `kind/test-last`.
Combined with a hand-written core test suite, the project runs ~920
tests / ~1340 assertions.

### Architecture

Five-stage pipeline:

```
sketch -> draft -> plan -> membrane -> figure
```

- **Sketch** — user-facing composable value (`data + mapping + views
  + layers + opts`)
- **Draft** — flat maps produced by `sk/draft` (one per view-layer
  combination), the bridge between API composition and planning
- **Plan** — plain Clojure data with dtype-next buffers (domains,
  ticks, legends, layout, resolved layers); serializable, inspectable,
  rendered by multiple backends
- **Membrane** — drawable primitives (rectangles, paths, text,
  translated/colored groups)
- **Figure** — terminal output: SVG hiccup or raster PNG

The pipeline's layering is strict: data transformations live under
`impl/`; drawing primitives live under `render/`; nothing in
`render/` knows about datasets or column names.

### Known limitations

These are documented and tracked for post-alpha work. None of them
produce crashes on canonical inputs.

**Layout and visuals:**

- Multi-layer overlays like `(-> data (sk/lay-point ...) (sk/lay-lm ...)
  (sk/lay-loess ...))` do not auto-generate a layer-kind legend to
  distinguish the two regression curves. Workaround: color each
  layer explicitly.
- SPLOM row labels render left-anchored instead of right-anchored
  (column labels are centered correctly).
- Histograms, stacked bars, step plots, and other stat-derived
  marks do not default to a `"count"` or `"density"` y-label.
- Continuous color legends (numeric `:color` mapping) label only
  the endpoint tick marks on the gradient bar. Intermediate
  values are unlabeled, making it hard to map interior colors
  back to data values.
- SPLOMs with 6+ variables at the default 600x400 have tight panels.
  Bump `:width`/`:height` or pin `:panel-width`/`:panel-height`.
- Horizontal bars from `(sk/coord :flip)` render the first row of
  data at the bottom of the chart, so a dataset sorted descending
  (natural "top N" order) produces the biggest bar at the bottom.
  The behavior matches ggplot2's `coord_flip()`. Workaround: sort
  the dataset ascending before plotting, e.g.
  `(tc/order-by data [:value] [:asc])`. A future opt-in flag such
  as `(sk/coord :flip {:reverse-categorical true})` would spare
  users the sort. Reported in user-report-1 Issue 3.

**Marks:**

- `:position :dodge` is silently ignored on nine marks including
  `summary`. Workaround: pre-compute dodge offsets via `tc/group-by`.
- Polar plots for bar-family marks don't auto-emit category labels
  -- rose charts currently render with zero text.
- Stacked bars don't split positive and negative values; all-positive
  data works, but mixed-sign data stacks incorrectly.
- `sk/lay-tile` (and the underlying `:bin2d` stat) requires numeric
  x and y columns. Passing categorical axes throws
  `ClassCastException: String cannot be cast to Number`. Workaround:
  bin externally and render with explicit numeric bin centers, or
  use `sk/lay-value-bar` with `{:color :value}` for a
  categorical-axis "heatmap" look.
- `sk/lay-stacked-bar` / `sk/lay-stacked-bar-fill` are count-only
  and reject a `y` column -- there is no clean way to render a
  stacked bar chart of pre-aggregated values (e.g. a "messages
  per year broken down by tenure bucket" chart where the counts
  are already computed). `sk/lay-stacked-area` does accept
  pre-aggregated `y`, so the pattern works there. Workarounds:
  lift the aggregation (expand each row back into count-many
  duplicate rows so `:count` sums to the pre-aggregated value),
  or use `sk/lay-stacked-area` on a numeric x. A proper fix
  (either lift the restriction when `y` is supplied, or add a
  `sk/lay-stacked-value-bar`) is planned. Reported in
  user-report-2 Issue 2.
- Stack order in `sk/lay-stacked-area` and
  `sk/lay-stacked-bar` follows the sort order of the `:color`
  column. There is no `:stack-order` / `:color-order` option yet,
  so forcing a specific bottom-to-top order requires prefixing
  category labels with sort-stable ordinal characters
  (`"01: ..."`, `"02: ..."`), which leaks into the legend.
  Reported in user-report-2 minor observation.
- `:shape` has no literal form -- `{:shape :triangle}` is a silent
  no-op. Only column mappings to `:shape` take effect.
- Faceted panels default to free scales per panel (ggplot2's default
  is fixed); an explicit `:facet-scales :fixed` option is pending.
  A consequence: a sketch-scope annotation (`sk/lay-rule-*` /
  `sk/lay-band-*`) is invisible in any panel whose per-panel domain
  doesn't contain the intercept -- for example, `{:y-intercept 6.0}`
  on a species-faceted scatter won't appear in setosa if setosa's
  y-range is [4, 5.5]. Workaround: pin the y-domain explicitly via
  `(sk/scale :y {:domain [...]})`.
- Annotations are silently skipped under `(sk/coord :polar)`. A polar
  rule would need to render as a circle (fixed radius) or spoke
  (fixed angle); those shapes are not implemented. Use Cartesian or
  flip coords for annotated plots.
- `sk/facet` and `sk/facet-grid` require a categorical column --
  passing a numeric column (e.g. mpg's `:cyl`) produces empty
  panels. Workaround: convert to string or keyword before faceting,
  or pick an already-categorical column.
- Large scatters produce large SVGs (~220 bytes/point). For >10k
  points, use `:format :bufimg` for raster output.
- `sk/save-png` (and the `:bufimg` raster path generally) truncates
  the rotated y-axis label after ~6 characters. The SVG path
  (`sk/plot` + Clay GFM, or `rsvg-convert` on the saved SVG)
  renders the full label. Root cause lives in membrane's Java2D
  backend (the rotated-text bounding box is clipped). Workaround:
  render to SVG and rasterize externally, or pad `:y-label` to
  stay short. Reported in user-report-1 Issue 4; needs an upstream
  fix in `membrane`.
- LOESS with confidence bands is O(n^2); subsample above ~5k rows.

**Options and config:**

- `:panel-size` is a legacy config key from the pre-total-first
  layout. It now emits a deprecation warning and is ignored. Use
  `:panel-width` / `:panel-height` (total-first escape hatches).
- The `:width` key on a `sk/plan` result preserves the user's
  original request even when `:panel-width` pins the real size --
  inspect `:total-width`/`:total-height` for the rendered canvas.
- `sk/plan` called on a plan or on a hiccup value now throws a
  clear error. Call `sk/plan` only on sketches.

**Schema errors that could be friendlier:**

- Non-integer `:width`/`:height` (e.g. `(/ 800 2.0)` = 400.0)
  fails with a Malli schema dump. Use `long` or integer literals.
- Boolean columns in `:x`/`:y` fail with a Malli schema dump.
  Convert to 0/1 or use as `:color` / `:shape` categorical.

**Mixing keyword and string column references:**

- Mapping the same column with a keyword in one place and a string
  in another (e.g. `(sk/frame ds {:color :group})` then
  `(sk/lay-point :x :y {:color "group"})`) is not normalized: the
  scope hierarchy treats them as different keys and the result is a
  silent empty plot. Workaround: pick one form (keyword or string)
  and use it consistently within a frame.

**ggplot2 features not yet implemented:**

- Per-layer `data`, `guides()` for per-aesthetic legend control,
  named theme presets, `scale_*_sqrt`/`reverse`/`date`. All tracked
  in the backlog.
