# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

First public alpha release. The API and visual defaults are still
subject to change based on early adopter feedback.

### Overview

Napkinsketch is a composable plotting library for Clojure, inspired by
the Grammar of Graphics. Plots are built by threading data through a
sequence of small transformations. The resulting sketch is a plain
Clojure value that auto-renders in Kindly-compatible notebooks (Clay
and friends) -- no explicit render call required.

### Features

**Composable pipeline.** Position mappings (`sk/view`), layers
(`sk/lay-*`), aesthetics, scales, coordinate transforms, faceting, and
plot options all flow through `->`. Every function takes a sketch and
returns a sketch, so users never have to remember plot assembly rules.

**Four-level scope.** Aesthetic mappings (color, size, alpha, shape,
group, text) flow sketch -> view -> method -> layer. Lower scopes
override higher ones with `nil` as explicit cancellation. The scope
model is lexical and uniform across all layer functions.

**25 layer functions** covering the common chart types: point, line,
step, area, stacked-area, histogram, bar, stacked-bar,
stacked-bar-fill, value-bar, lm (linear model), loess, text, label,
density, tile/heatmap, density2d, contour, boxplot, violin, ridgeline,
rug, summary (mean + SE), errorbar, lollipop.

**Statistics built in.** Histogram binning, OLS regression (with
optional confidence ribbons), LOESS with bootstrap confidence bands,
1D and 2D kernel density estimation (Silverman bandwidth), boxplot
five-number summary with R type 7 quartiles, categorical counts, and
mean + standard error summaries. Every stat is a multimethod — you
can register new ones.

**Faceting.** `sk/facet` for wrap-style paneling, `sk/facet-grid` for
row x column grids, `sk/cross` for scatter-plot matrices (SPLOM), and
`sk/distribution` for diagonal histograms on a SPLOM.

**Coordinate systems.** Cartesian (default), flip, polar, and fixed
(1:1 aspect ratio).

**Scales.** Linear, log, categorical, and datetime — type is
auto-detected from the column and can be overridden via `sk/scale`.

**Continuous and categorical color.** Numeric columns map to a
viridis gradient with a color bar legend; categorical columns use one
of 7000+ palettes via clojure2d. Diverging color scales with custom
midpoints are supported.

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
- **Gallery** — ~130 examples reproducing charts from the R Graph
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

- `:coord :flip` renders `errorbar`/`pointrange` (and thus `summary`)
  incorrectly. Workaround: don't flip these marks.
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
