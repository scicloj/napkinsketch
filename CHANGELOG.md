# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

First public alpha release. The API and visual defaults are still
subject to change based on early adopter feedback.

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
sequence of small transformations. The resulting sketch is a plain
Clojure value that auto-renders in Kindly-compatible notebooks (Clay
and friends) -- no explicit render call required.

### Features

**Composable pipeline.** Everything you do to build a plot -- define
views (`sk/view`), add layers (`sk/lay-*`), set aesthetic mappings,
apply scales and coordinate transforms, add facets, attach
annotations, set plot options -- flows through `->`. Every function
takes a sketch and returns a sketch, so there is no plot-assembly
order to memorize.

**Three-level scope.** Aesthetic mappings (color, size, alpha,
shape, group, text) flow sketch -> view -> layer. Lower scopes
override higher ones; `nil` is an explicit cancellation. Scope is
lexical: you set a mapping where you want it to apply.

**25 layer functions** covering the common chart types: point, line,
step, area, stacked-area, histogram, bar, stacked-bar,
stacked-bar-fill, value-bar, lm (linear model), loess, text, label,
density, tile, density2d, contour, boxplot, violin, ridgeline, rug,
summary (mean + SE), errorbar, lollipop.

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

**Annotations.** Reference lines (`rule-v`, `rule-h`) and shaded
bands (`band-v`, `band-h`) as plot-level decorations.

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
`apply-position`, `plan->figure`, `membrane->figure` -- are open
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
Combined with a hand-written core test suite, the project runs ~850
tests / ~1200 assertions.

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
- SPLOMs with 6+ variables at the default 600x400 have tight panels.
  Bump `:width`/`:height` or pin `:panel-width`/`:panel-height`.

**Marks:**

- `:position :dodge` is silently ignored on nine marks including
  `summary`. Workaround: pre-compute dodge offsets via `tc/group-by`.
- Polar plots for bar-family marks don't auto-emit category labels
  -- rose charts currently render with zero text.
- Stacked bars don't split positive and negative values; all-positive
  data works, but mixed-sign data stacks incorrectly.
- `:shape` has no literal form -- `{:shape :triangle}` is a silent
  no-op. Only column mappings to `:shape` take effect.
- Faceted panels default to free scales per panel (ggplot2's default
  is fixed); an explicit `:facet-scales :fixed` option is pending.
- Large scatters produce large SVGs (~220 bytes/point). For >10k
  points, use `:format :bufimg` for raster output.
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

**ggplot2 features not yet implemented:**

- Per-layer `data`, `guides()` for per-aesthetic legend control,
  named theme presets, `scale_*_sqrt`/`reverse`/`date`. All tracked
  in the backlog.
