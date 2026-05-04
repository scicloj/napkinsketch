# Change Log

All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.2.0 - 2026-05-05]
- the membrane stage now returns a `PlotjeMembrane` record implementing the [Membrane](https://github.com/phronmophobic/membrane) UI protocols (`IOrigin`, `IBounds`, `IChildren`), so Plotje plots compose with hand-built Membrane elements. Width and height read via `(membrane.ui/width m)`/`(height m)`; title rides as `:plotje/title`. Replaces the prior metadata-tagged-vector contract.
- new `pj/membrane?` predicate

## [0.1.0 - 2026-05-03]
- initial public alpha release
- composable five-stage pipeline: pose -> draft -> plan -> membrane -> plot
- layer types for distributions, ranking, time series, relationships, and polar
- composite poses with faceting and shared scales
- SVG and PNG rendering via membrane
