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
size regardless of overhead. Either or both can be set. SPLOMs
(multi-variable layouts) continue to use the `:panel-size` config
default for each panel.

`sk/arrange` now re-plans each sub-sketch at the derived per-cell
dimensions when you pass a container `:width`/`:height` and the
inputs are sketches ("sketch-mode"). Pre-rendered hiccup plots
pass through unchanged and are scaled by the browser via CSS
("figure-mode"), same as before.

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

Twenty-seven executable chapters:

- **Getting Started** — quickstart
- **Foundations** — datasets, sketch model, core concepts, sketch
  rules, inference rules, methods, glossary
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
sketch -> views -> plan -> membrane -> figure
```

- **Sketch** — user-facing composable value (`data + mapping + views
  + layers + opts`)
- **Views** — flat view maps produced by `resolve-sketch`, the bridge
  between API composition and planning
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

- `:position :dodge` is silently ignored on nine marks including
  `summary`. Workaround: pre-compute dodge offsets via `tc/group-by`.
- Polar plots for bar-family marks don't auto-emit category labels.
- Faceted panels default to free scales per panel (ggplot2's default
  is fixed); an explicit `:facet-scales :fixed` option is pending.
- Large scatters produce large SVGs (~220 bytes/point). For >10k
  points, use `:format :bufimg` for raster output.
- LOESS with confidence bands is O(n^2); subsample above ~5k rows.
- A number of ggplot2 features are not yet implemented: per-layer
  `data`, `guides()` for per-aesthetic legend control, named theme
  presets, `scale_*_sqrt`/`reverse`/`date`. All tracked in the
  backlog.
