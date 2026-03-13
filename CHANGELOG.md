# Changelog

All notable changes to napkinsketch will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Public API** (`scicloj.napkinsketch.api`): `view`, `lay`, `plot`, `sketch`,
  `render-figure`, `point`, `line`, `histogram`, `bar`, `stacked-bar`,
  `value-bar`, `lm`, `loess`, `labs`, `coord`, `scale`, `cross`,
  `valid-sketch?`, `explain-sketch`
- **Marks**: point, line, histogram, tile (heatmap), ridgeline (stat :bin), bar (stat :count), stacked-bar,
  value-bar (stat :identity), lm (stat :lm), loess (stat :loess)
- **Aesthetics**: `:color` (column), `:alpha` (constant or column),
  `:size` (constant or column), `:shape` (column), `:group` (column) , `:palette` (custom color cycle)
- **Labels**: `labs` function for composable axis/title labels with priority
  chain: opts > view-level labs > scale label > auto-inferred
- **Scales**: `:linear`, `:log`, `:categorical`, fixed domain via `sk/scale` ; automatic date axis for temporal columns
- **Coordinates**: `:cartesian`, `:flip`, `:polar` (rose charts, polar scatter)
- **Annotations**: `rule-v`, `rule-h`, `band-v`, `band-h` as first-class
  constructors in the public API
- **Additive `lay`**: calling `lay` multiple times appends layers rather
  than overwriting
- **Rendering extensibility**: `render-figure` defmulti dispatching on format
  keyword; `:svg` renderer via membrane path
- **Sketch validation**: Malli schema with `valid-sketch?` and `explain-sketch`
- **Notebooks** (16): quickstart, scatter_gallery, distributions, layers, config,
  composability, real_world, edge_cases, api_reference, glossary,
  inference_rules, exploring_sketches, extensibility, architecture, polar
- **325 tests** across all notebooks
