# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Public API** (`scicloj.napkinsketch.api`): `view`, `lay`, `plot`, `abcdefgh`,
  `views->abcdefgh`, `abcdefgh->membrane`, `membrane->figure`, `abcdefgh->figure`,
  `svg-summary`, `valid-abcdefgh?`, `explain-abcdefgh`, `config`, `set-config!`,
  `with-config`, `arrange`, `save`, `options`, `facet`, `facet-grid`, `coord`,
  `scale`, `cross`, `distribution`, `views-of`, `sketch?`
- **25 layer functions**: `lay-point`, `lay-line`, `lay-step`, `lay-histogram`,
  `lay-bar`, `lay-stacked-bar`, `lay-stacked-bar-fill`, `lay-value-bar`,
  `lay-lm`, `lay-loess`, `lay-text`, `lay-label`, `lay-area`, `lay-stacked-area`,
  `lay-density`, `lay-tile`, `lay-density2d`, `lay-contour`, `lay-ridgeline`,
  `lay-boxplot`, `lay-violin`, `lay-rug`, `lay-summary`, `lay-errorbar`,
  `lay-lollipop`
- **Annotations**: `rule-v`, `rule-h`, `band-v`, `band-h`
- **Sketch auto-rendering**: layer functions return a Sketch that
  auto-renders in Kindly-compatible notebooks — no explicit `sk/plot` needed
- **Input formats**: Tablecloth datasets, maps of columns, sequences of row
  maps, CSV/URL paths — all coerced to tech.ml.dataset internally
- **String column names**: column references can be keywords or strings
- **Named CSS colors**: `"red"`, `"steelblue"`, etc. alongside hex strings
- **Aesthetics**: `:color` (categorical or continuous), `:alpha`, `:size`,
  `:shape`, `:group`, `:text`, `:ymin`, `:ymax`
- **Stats**: identity, bin, count, lm (linear model), loess (local regression),
  kde (kernel density), boxplot, violin, bin2d, kde2d, summary
- **Scales**: linear, log, categorical, datetime (auto-detected)
- **Coordinates**: cartesian, flip, polar, fixed (1:1 aspect ratio)
- **Faceting**: `facet` (wrap), `facet-grid` (row×column), `cross` (SPLOM),
  `distribution` (diagonal histograms)
- **Confidence ribbons**: `{:se true}` on lm and loess methods
- **Continuous color**: numeric `:color` → viridis gradient with color bar legend
- **Diverging color**: `:color-scale :diverging` with `:color-midpoint`
- **7,000+ palettes** via clojure2d integration — keyword, vector, or explicit map
- **Layered config**: per-call > `with-config` > `set-config!` > `napkinsketch.edn`
  \> library defaults — theme deep-merges
- **Multi-plot dashboards**: `sk/arrange` for CSS grid layout
- **Annotations**: vertical/horizontal rules and shaded bands
- **Interactivity**: tooltip and brush selection via JavaScript
- **Abcdefgh validation**: Malli schema with `valid-abcdefgh?` and `explain-abcdefgh`;
  open to custom marks via `keyword?` (not closed enum)
- **Column inference**: `(sk/view data)` and `(sk/lay-point data)` infer columns
  from dataset shape (1→x, 2→x y, 3→x y color, 4+→error)
- **Unknown option warnings**: `lay-*` functions warn on unrecognized option keys
- **Extensibility**: 8 multimethod extension points with `[:key :doc]`
  self-documenting pattern; end-to-end waterfall chart example in notebooks
- **24 notebooks** (~595 tests, ~948 assertions): quickstart, datasets,
  core_concepts, inference_rules, methods, glossary, scatter, distributions,
  ranking, change_over_time, relationships, polar, cookbook, configuration,
  customization, faceting, troubleshooting, api_reference, exploring_sketches,
  architecture, extensibility, waterfall_extension, edge_cases, development

### Architecture
- **Four-stage pipeline**: views → abcdefgh → membrane → figure
- **Abcdefgh is plain data**: Clojure maps with dtype-next buffers, serializable
- **`impl/` vs `render/` boundary**: pure data transformations vs membrane rendering
- **Method registry**: keyword → {mark, stat, position, doc, accepts} with
  `method/register!` for third-party extensions
- **Tidy data**: aggregation stats use `tc/group-by` for O(n) grouping;
  tile pipeline uses `tc/dataset` + `tc/add-column`
- **Visual defaults tuned**: larger fonts, larger points, thinner grid, more margin
  (guided by Claude Vision API review tool)
- **R type 7 quartiles**: boxplot uses ggplot2/R-compatible quantile method
- **2D Silverman bandwidth**: KDE2D uses correct d=2 formula
