# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Public API** (`scicloj.napkinsketch.api`): `view`, `lay`, `plot`, `sketch`,
  `views->sketch`, `sketch->membrane`, `membrane->figure`, `sketch->figure`,
  `svg-summary`, `valid-sketch?`, `explain-sketch`, `config`, `set-config!`,
  `with-config`, `arrange`, `save`
- **28 mark constructors**: point, line, step, histogram, bar, stacked-bar,
  stacked-bar-fill, value-bar, lm, loess, text, label, area, stacked-area,
  density, tile, density2d, contour, ridgeline, boxplot, violin, rug, summary,
  errorbar, lollipop, rule-v, rule-h, band-v, band-h
- **String column names accepted**: column references can be keywords or strings;
  datasets with string column names are auto-normalized to keywords
- **Named CSS colors**: `"red"`, `"steelblue"`, and other CSS color names
  accepted as fixed colors alongside hex strings like `"#FF0000"`
- **Aesthetics**: `:color` (column or numeric for continuous), `:alpha`,
  `:size`, `:shape`, `:group`, `:text`, `:ymin`, `:ymax`
- **Stats**: identity, bin, count, lm, loess, kde, boxplot, violin, bin2d,
  kde2d, summary
- **Scales**: linear, log, categorical, date (auto-detected)
- **Coordinates**: cartesian, flip, polar, fixed (1:1 aspect ratio)
- **Faceting**: `facet` (wrap), `facet-grid` (row×column), `cross`, `pairs`,
  `distribution` — with free/shared scales
- **Confidence ribbons**: `(sk/lm {:se true})` and `(sk/loess {:se true})`
  with configurable confidence level
- **Continuous color**: numeric `:color` → viridis gradient with continuous
  legend (20 pre-computed stops, serializable)
- **Diverging color**: `:color-scale :diverging` with `:color-midpoint`
- **7,000+ palettes** via clojure2d.color integration — keyword, vector, or
  explicit `{:category "#hex"}` map
- **Layered config system**: per-call options > `with-config` > `set-config!` >
  `napkinsketch.edn` > library defaults — theme deep-merges
- **Multi-plot composition**: `sk/arrange` for CSS grid layout of independent plots
- **Labels**: `sk/labs` with `:title`, `:subtitle`, `:caption`, `:x`, `:y`
- **Annotations**: `rule-v`, `rule-h`, `band-v`, `band-h`
- **Interactivity**: tooltip and brush selection via Scittle scripts
- **Sketch validation**: Malli schema with `valid-sketch?` and `explain-sketch`
- **dtype-next integration**: numeric arrays (xs, ys, sizes, etc.) stored as
  dtype-next buffers at the sketch boundary for efficiency
- **19 notebooks** (~500 tests, ~750 assertions): quickstart, core_concepts,
  inference_rules, glossary, scatter, distributions, ranking, evolution,
  relationships, polar, cookbook, configuration, customization, faceting,
  api_reference, exploring_sketches, architecture, extensibility, edge_cases

### Architecture
- **Four-stage pipeline**: views → sketch → membrane → figure
- **Sketch is plain data**: Clojure maps with dtype-next buffers, no functions/datasets/membrane types
- **`impl/` vs `render/` boundary**: pure data transformations vs membrane-dependent rendering
- **Multimethod extensibility**: `compute-stat`, `extract-layer`, `layer->membrane`,
  `sketch->figure`, `membrane->figure`
- **ToSVG protocol**: extend-protocol for membrane→SVG conversion
- **Position system**: `impl/position.clj` centralizes dodge/stack/fill transforms
