# Changelog

All notable changes to plotje will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

Staged for the upcoming first public alpha release. The API and
visual defaults are still subject to change based on early adopter
feedback.

### Internal: `plan->membrane` dispatch on `:composite?` flag

The internal `plan->membrane` rendering pass now dispatches on a
boolean `:composite?` flag carried on every plan, instead of on
the runtime class of the plan record. This eliminates a
class-retention vector that caused long-lived nREPL sessions to
accumulate stale `Plan` and `CompositePlan` entries in the
dispatch table across `:reload` cycles -- the dispatch table now
stays at two boolean entries, regardless of how many times the
plan namespace is reloaded.

A separate, deeper class-retention path in `clojure.core/eval`
was identified during the same investigation; that one lives
upstream and is unaffected by this change. See
`dev-notes/heap-leak-investigation-2026-04-28.md` and
`dev-notes/clay-leak-diagnosis-2026-04-28.md`.

### Breaking: terminology rename batch

A six-step rename clarifies vocabulary across the pipeline. One
public-API breaking change; the rest are internal:

Public:

- `pj/layer?` -> `pj/plan-layer?` (predicate for the plan-stage
  layer record produced by `pj/plan`).

Internal:

- `Layer` defrecord -> `PlanLayer`.
- `Layer` Malli schema -> `PoseLayer`.
- `Method` defrecord -> `LayerType`.
- `view` (as a noun naming a plan-stage element) -> `draft-layer`
  across ~210 internal references.
- `:__entry-idx` -> `:__panel-idx`, with follow-on identifier
  renames.
- Internal `method?` -> `layer-type?`. The public
  `pj/layer-type?` was already correct.

The rename batch also removed ~108 lines of dead code in
`impl/resolve.clj` (six unused helpers, three private stubs)
surfaced during the audit.

Migration: `s/pj\/layer\?/pj\/plan-layer?/g` on consumer code.
Internal record and schema names are not part of the public API.

### `pj/lay-interval-h` (Gantt primitive) and temporal rule intercepts

A new mark for horizontal interval ("Gantt") plots: each row
gets a bar from `:x` to `:x-end`. Categorical and numeric color,
`:interval-thickness` for bar height, and `(pj/coord :flip)` for
vertical Gantts all participate.

Companion fix: `pj/lay-rule-h` and `pj/lay-rule-v` now accept
`LocalDate`, `Instant`, and `java.util.Date` intercepts on
temporal axes. Previously only numeric intercepts were honored
even on date x-axes.

Also: schema relaxation around log color (which incidentally
unblocks `pj/lay-tile` with `:log` color), tooltip and brush
participation, and cross-panel linking. Worked examples in the
`timelines.clj` and `interactivity.clj` exploratory notebooks
(not currently in `chapters.edn`).

### Input-validation guards close three more silent-failure paths

Three additional entry-point inputs that used to crash deep or
produce silent confusion now throw at the boundary:

- `:x-type :categorical` on a `LocalDate` column now plans
  cleanly. Previously failed with `ClassCastException` from
  `dfn/finite?` -- the `:packed-local-date` representation
  reports as numeric, so the infinity-filter path was wrong
  for user-declared categorical date columns.
- A pre-rendered hiccup vector at the input gate now throws
  with guidance ("looks like a rendered hiccup vector -- pass
  the pose itself, not the result of pj/plot") instead of a
  deep `Tensors must be 2 dimensional` from `tc/dataset`.
- `:y-type :categorical` on the marks that don't support
  arbitrary y categoricals (`:lollipop`, `:summary`,
  `:ridgeline`, `:pointrange`) now throws pointing at
  `pj/coord :flip` as the correct path for vertical layouts.

### `pj/scale` extended to visual channels

`pj/scale` previously accepted only `:x` and `:y`. It now also
accepts the four continuous visual channels `:size`, `:alpha`,
`:fill`, and `:color`, with `:linear` and `:log` scale types.
`:categorical` does not apply to a continuous encoding and is
rejected on these channels.

```clojure
(pj/scale pose :size :log)         ; log-spaced point sizes
(pj/scale pose :fill :log)         ; log-spaced tile fill
(pj/scale pose :color
          {:type :log :domain [0.001 100]})
```

Log-scaled fill legends carry intermediate tick labels along the
gradient bar; non-positive values raise a clear error mirroring
the axis-side log-scale guard. Documentation in
`customization.clj`, `options_and_scopes.clj`, `pose_rules.clj`
(Rule O2 generalized to all six channels), and
`api_reference.clj`.

### `pj/pose` 3-arity: multi-pair plus aesthetic opts

`(pj/pose data multi-pair-spec opts)` folds the prior two-call
SPLOM idiom into a single call:

```clojure
;; Before
(-> data
    (pj/pose {:color :species})
    (pj/pose (pj/cross cols cols)))

;; After
(pj/pose data (pj/cross cols cols) {:color :species})
```

The opts map attaches at the composite root and flows into every
panel, so the resulting structure is identical to the two-call
form (Rule C9 with Property P-C9 in `pose_rules.clj`). SPLOM
call sites in README, scatter, api_reference, gallery,
edge_cases, and customization migrated to the new shape.

### Faceted scale defaults

Faceted plots default to shared scales across panels (one y-domain
spans every panel), matching ggplot2's `facet_wrap(scales="fixed")`
default. An opt-in `:facet-scales :free` for per-panel domains is
still pending; pin per-panel ranges explicitly with
`(pj/scale :y {:domain [...]})` when needed.

Root-pose annotations like `pj/lay-rule-*` / `pj/lay-band-*` are
visible in every panel by default, since the shared domain
encompasses the intercept.

### Retired: `pj/prepare-pose` (consolidated into `pj/pose`)

`pj/pose` is now the single public constructor. The 1-arity
`(pj/pose hand-typed-map)` accepts a literal pose-shaped map --
previously the role of `pj/prepare-pose`, which retires from the
public API. Existing notebooks and tests have been migrated.

The literal-map path validates the map (warns on unknown top-level
keys and `:mapping` keys at every depth, throws on non-column-ref
position mappings) and tags it for notebook auto-render. It does
not normalize the shape: keys are not reordered, `:data` is not
coerced at construction time (the pipeline coerces per leaf at
draft time), and empty `:mapping`/`:opts` are not elided. The
user's typed shape is preserved verbatim apart from the metadata.
The cosmetic cleanup is reserved for constructor paths where
`pj/pose`, `pj/lay-*`, `pj/options`, `pj/arrange`, and friends
build the map themselves.

Validation also now fires at every public entry point -- `pj/plot`,
`pj/plan`, `pj/draft`, `pj/lay-*` -- so a literal map with a typo
surfaces a single warning at the entry point rather than crashing
deep in the pipeline. (Idempotent on already-tagged input, so the
same map flowing through several public functions warns once, not
once per call.)

### Renamed: `frame` -> `pose`

The library's central concept is now **`pose`** -- the deliberate
arrangement of a data subject before laying layers on it. Where a
`frame` was a composable specification of a plot, a `pose` is the
same value with a name that better matches the painter's-studio
metaphor already implicit in `lay-*` and `plot`. A study composed
of poses; a tableau of poses.

This is a mechanical rename, no API behaviour change:

- Constructor `pj/frame` -> `pj/pose`.
- Predicate `pj/frame?` -> `pj/pose?`.
- Builder `pj/prepare-frame` -> `pj/prepare-pose`.
- Sub-pose slot `:frames` -> `:poses` on every map shape.
- Internal namespace `scicloj.plotje.impl.frame` ->
  `scicloj.plotje.impl.pose`; schema `Frame` -> `Pose`.
- Two foundations chapters renamed:
  `frame_model.clj` -> `pose_model.clj`,
  `frame_rules.clj` -> `pose_rules.clj`. `chapters.edn` updated.
- `lay-*` constructors are unchanged -- only the central
  concept-word changes; the layer registry, mark and stat names,
  and the rest of the API stay put.

The on-disk working directory is still named `napkinsketch/`; the
GitHub remote was renamed to `scicloj/plotje` after the pose rename
landed.

This is the third central-name change pre-0.1.0
(`Sketch` -> `frame` -> `pose`). The earlier renames were driven
by the move to a recursive plain-map substrate; this one is a
naming refinement only. The painter's-studio metaphor that
`lay-*` and `plot` already used now reaches the type at the
center of the API.

### Renamed: napkinsketch -> Plotje (and `sk` -> `pj`)

The library is now **Plotje** -- Dutch for "a little plot". Two
atomic mechanical commits, no API behaviour change:

- Maven coordinate `org.scicloj/napkinsketch` -> `org.scicloj/plotje`.
- Namespace prefix `scicloj.napkinsketch.*` -> `scicloj.plotje.*`.
- Documentation alias `sk` -> `pj` across every `(:require [...
  :as sk])`. All notebooks, tests, and prose use `pj` now.
- Resource and config files: `resources/napkinsketch-defaults.edn`
  -> `resources/plotje-defaults.edn`; user config lookup
  `napkinsketch.edn` -> `plotje.edn`.
- GitHub repo URLs and Clay remote-repo updated accordingly.

The GitHub remote was later renamed to `scicloj/plotje`; the on-disk
working directory is still named `napkinsketch/` (a deliberate
carryover, distinct from the rename).
Migration is mechanical: `s/napkinsketch/plotje/g` plus update the
`:as` alias on every require.

### Retired: pj/sketch, pj/view, and impl/sketch.clj

The Sketch record, its constructors, and its adapter surface are
gone. The pose substrate is now the only internal spec type.

Removed user-facing API:

- `pj/sketch`, `pj/view`, `pj/sketch?` -- use `pj/pose`, `pj/lay-*`,
  and `pj/pose?` instead.
- The `Sketch` record itself (`impl/sketch.clj`, ~348 lines).

Internal changes the deletion required:

- `pj/lay-*` on raw data now coerces through `pj/pose` and returns a
  pose; previously the adapter returned a Sketch record.
- `pj/with-data`, `pj/options`, `pj/scale`, `pj/coord`, `pj/save`, and
  `pj/save-png` all coerce non-pose inputs via a new private
  `ensure-pose` helper (raw data -> leaf pose) rather than the old
  `ensure-sk` (raw data -> Sketch).
- `pj/facet` and `pj/facet-grid` write to the pose's `:opts` instead
  of routing through `ensure-sk`. The facet-expansion logic moved
  from `impl/sketch.clj`'s `expand-facets` into
  `impl/pose/leaf->draft`: when a leaf's `:opts` carry
  `:facet-col`/`:facet-row`, the draft is multiplied across distinct
  facet values, each variant carrying filtered `:data` and a
  `:facet-col`/`:facet-row` label that `plan.clj` detects to build
  the facet grid. Composite poses are rejected explicitly (see
  `dev-notes/facet-composite-deferral.md`).
- `impl/sketch_schema.clj` renamed to `impl/plan_schema.clj` -- it
  was misnamed; the namespace holds Malli schemas for the plan data
  model, not the Sketch record.
- Multi-column `pj/lay-*` (`(pj/lay-histogram data [:a :b :c])`) now
  builds a multi-pair composite via `pj/pose` and attaches a bare
  layer at the root; the layer flows to every panel via
  `resolve-tree`. Previously this path created N views with N
  identical layers.
- The `sketch_rules.clj` notebook was retired -- `pose_rules.clj`
  is the live replacement.

Known shape changes from the retirement:

- `(pj/lay-point data :x :y {:color :g})` now places `:color` on the
  layer's own `:mapping`, not on the pose's root `:mapping`. Semantic
  behaviour at render time is unchanged.
- Chaining two `pj/lay-*` calls with different position columns on
  the same leaf no longer silently creates two panels; both layers
  attach to the same leaf. To create two panels, promote explicitly
  via `(pj/pose data [[:a :b] [:c :d]])`.

### Breaking: `pj/arrange` returns a composite pose

`pj/arrange` now returns a composite pose (a plain-map value) instead
of CSS-grid hiccup. The composite renders through the membrane
rendering pipeline, so both `:svg` (default) and `:bufimg` targets work
the same way they do for single plots.

- Inputs must be poses. Pre-rendered hiccup is no longer accepted --
  combine hiccup yourself with `[:div ...]` if you want raw hiccup
  composition.
- `:gap` (CSS string) is removed. Leaves tile tightly; add spacing
  later at the composite level if needed.
- `:title` still works; it renders as a centered band above the grid.
- New option `:share-scales` (default `#{}`) shares x/y domains across
  arranged cells when set to a subset of `#{:x :y}`.

Migration for the common case:

```clojure
;; Before
(pj/arrange [(pj/plot pose-a) (pj/plot pose-b)])

;; After
(pj/arrange [pose-a pose-b])
```

The composite pose auto-renders in notebooks via `kind/fn`, and
passing it to `pj/plot` returns SVG hiccup as before.

### Breaking: annotations are now layers

Reference lines and shaded bands used to live in `[:opts :annotations]`,
applied uniformly to every panel, and were attached with a separate
`pj/annotate` wrapper around `pj/rule-h`/`pj/rule-v`/`pj/band-h`/`pj/band-v`.
This release replaces that entire path with first-class layers:

- `pj/lay-rule-h` / `pj/lay-rule-v` -- horizontal / vertical reference line
- `pj/lay-band-h` / `pj/lay-band-v` -- horizontal / vertical shaded band

These are ordinary `lay-*` functions: bare arity attaches at the root
pose (every panel sees it), the four-argument arity with column refs
attaches at view scope (only panels matching the view's x/y mapping),
and the position lives in the opts map (`:y-intercept` for
`lay-rule-h`, `:x-intercept` for `lay-rule-v`, `:y-min`/`:y-max` for
`lay-band-h`, `:x-min`/`:x-max` for `lay-band-v`). Appearance
aesthetics (`:color`, `:alpha`) are literal values in the same opts
map.

The old API is removed: `pj/annotate`, `pj/rule-h`, `pj/rule-v`,
`pj/band-h`, `pj/band-v`, and the `[:opts :annotations]` slot no longer
exist. Migration is mechanical -- replace each call with the corresponding
`pj/lay-*` form. Column-mapped positions (one rule per row, ggplot2's
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
facet grids, SPLOMs, and `pj/arrange` dashboards all honor the
total `:width`/`:height` as a hard bound. SPLOMs in particular
used to use `:panel-size` from cfg and silently ignore
`:width`/`:height`; now they derive panel dimensions from the
total the same way faceted layouts do. Large SPLOMs (6+ variables)
at the default 600x400 will end up with small panels -- bump
`:width`/`:height` or pin `:panel-width`/`:panel-height`
explicitly. The `:panel-size` cfg key still exists for backwards
compatibility but is no longer consulted by the layout pipeline.

The layout pipeline was rewritten around three pure functions
(`compute-scene` / `compute-padding` / `compute-dims`, in
`impl/layout.clj`) that feed off the data-derived scene and never
touch pixel math twice. A single reformulation -- running the tick
picker at a pixel budget equal to `:height` instead of the unknown
real panel height -- breaks the classic
`y-label-pad <-> panel-width` cycle. Label widths are monotonic
non-decreasing in tick count for every supported scale type, so
the over-estimate is always safe. See
`dev-notes/design-width-inference.md` for the full design.

Most existing plots shrink visibly (panels previously ~600x400 now
render around ~560x368 at the default total size). Generated tests
count panel/point/line counts rather than pixel positions, so the
test suite passes with no changes beyond updating a handful of
explicit width assertions.

### Pose-native pipeline (Phase 6 slice 2)

The pose substrate now runs the full draft emission pipeline on its
own. `pj/plan`, `pj/plot`, and `pj/draft` read a leaf pose directly
via a new `impl.pose/leaf->draft`; the compositor reads each
resolved leaf the same way. The Sketch detour
(`leaf-pose->sketch` -> `sketch->draft`) is no longer on the pose
path.

User-visible behavior:

- Fixes an edge case where a leaf pose with `:x`/`:y` only on a
  layer (not on the pose's own `:mapping`) used to emit an empty
  draft. The layer's position is now read and the draft carries one
  entry per layer.
- `pj/with-data` accepts poses directly and keeps them as poses
  (it used to force poses through the Sketch adapter, returning a
  Sketch record).
- `pj/pose` gains a multi-pair arity: `(pj/pose fr [[:a :b]
  [:c :d] ...])` and `(pj/pose fr [:a :b :c])` append panels in
  one call.
- `(pj/pose fr (pj/cross cols cols))` builds a SPLOM: when the
  pairs form an M x N Cartesian rectangle, the result is a nested
  rows-of-cols composite with `:share-scales #{:x :y}`. The
  compositor renders one shared legend on the right (when the
  composite root carries a color/size/alpha aesthetic), suppresses
  x-axis labels on non-bottom rows and y-axis labels on non-leftmost
  columns, and lets per-cell inference pick the layer type --
  scatter off-diagonal, histogram on the diagonal where x = y
  (matching the legacy sk/view SPLOM behaviour). The idiomatic SPLOM
  omits `pj/lay-point`:
  ```clojure
  (-> data
      (pj/pose {:color :species})
      (pj/pose (pj/cross cols cols)))
  ```

User-facing examples across the book and gallery have been migrated
to `pj/pose`. SPLOM examples in gallery, scatter, faceting,
customization, and edge_cases all use the new pose-native pattern.

Also fixed: `promote-leaf` was dropping `:opts` when the leaf got
wrapped into a composite. Plot-level options set via `pj/options`
before promotion now correctly carry to the new composite's root.

### Matrix layout for threaded multi-position composites

Threading `(pj/pose fr :x :y)` over a leaf-with-position now
promotes to a composite whose layout is `:matrix`: distinct x-cols
become grid columns, distinct y-cols become grid rows, leaves land
at their `(col-of-x, row-of-y)` intersection. Empty cells render
the theme background. Strip labels above each column carry the
x-col name, strip labels left of each row carry the y-col name.
Per-panel x/y axis labels are suppressed under the strip labels.
Duplicate `(x, y)` pairs stack into new rows in DFS order rather
than colliding.

This restores (and generalises) sketch-era `sk/view` behaviour.
The marginal-density-plot pattern falls out naturally: a 2x2
matrix where the off-axis cells hold the marginal densities and
the main scatter sits at `(:x, :y)`.

The previous flat-row layout is still reachable via:
`(pj/options fr {:layout {:direction :horizontal}})`.

`(pj/pose data [[:a :b] [:c :d]])` (multi-pair literal) and
`pj/arrange` keep their current row layouts. SPLOM via `pj/cross`
still uses its own grid composite shape (could be unified with
matrix later).

### SPLOM rendering fixes

The SPLOM (`(pj/pose data (pj/cross cols cols))`) had three
visible defects on diagonal cells; all three are now fixed.

**Per-cell axis-name labels and tick numbers suppressed.** Inside
each cell, the y-axis label (e.g. "sl" in the leftmost column's
cells) is no longer drawn -- the col/row strip labels carry the
axis-variable name. Per-cell tick numbers now appear only on the
bottom row (x-ticks) and leftmost column (y-ticks); the
`:share-scales #{:x :y}` guarantee makes per-cell ticks redundant
across the rest of the grid.

**Diagonal histograms render against a count axis.**
`pose/inject-shared-scales` now skips the `:y-scale-domain` stamp
on any leaf whose every effective layer resolves to a stat in
`#{:bin :count :density}`. The diagonal cells (which per-cell
inference picks as `:bar :bin` histograms) get their own count
y-axis instead of inheriting the shared data domain that their
column's other cells use. Off-diagonal scatters and
`:bin2d` / `:density-2d` heatmaps are unaffected -- the latter
have their count on the fill aesthetic, not on y, so they still
participate in shared y-domain coordination. The exemption is
bounded by predicted stat (the same precedence `leaf->draft` and
`resolve/resolve-draft-layer` use), so it correctly handles
explicit `:stat`, registered `:layer-type` entries, explicit
`:mark` (defaults to `:identity`), and the empty-layers +
non-empty-mapping shape that SPLOM diagonal cells take. Worked
examples + visual diversity coverage in
`notebooks/scale_coordination_exploration.clj`.

**Strip labels.** Grid composites built from
`(pj/pose data (pj/cross cols cols))` now render strip labels
above the top row (one per column, naming the y column) and to
the left of the leftmost column (one per row, naming the x
column) -- matching the legacy `sk/view` SPLOM chrome. Labels are
drawn at the compositor level rather than plumbed through each
cell's plan, so tight SPLOM layouts with long column names don't
squeeze the per-panel width. The `cross-grid-strip-labels-test`
no longer carries a FIXME.

Together these resolve the last visual regression in the
post-Phase-6 corpus.

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

Composite text color (titles + strip labels in `pj/arrange`,
SPLOM, matrix-layout composites) was lifted from RGB `25` to
`51`, matching the leaf-title default. Previously composite chrome
text was perceptibly darker than per-leaf chrome; the two now
read as one design.

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

### Pose inference and rendering fixes

A handful of bugs in the post-Phase-6 pose pipeline, surfaced by
a regression corpus comparing pre-Phase-6 Sketch behaviour to
current pose behaviour. All shipped:

- `pj/pose` 1-arity now auto-infers a position mapping from the
  first 1-3 columns of its data (`(pj/pose {:x [...] :y [...]})`
  yields a pose with `:mapping {:x :x :y :y}` and renders without
  an explicit `pj/lay-*` call). 4+ column data leaves the mapping
  empty -- gentler than the sketch-era `sk/view` which threw.
- `ensure-pose` now routes raw-data inputs through `prepare-pose`,
  so Kindly auto-render metadata is attached on every entry path
  (previously only `pj/pose` itself attached it; `(pj/lay-point
  raw-data)` bypassed the wrapper and the result didn't auto-render
  in notebooks).
- `pj/save` and `pj/save-png` now route composite poses through
  the compositor. They used to call `plan->plot` directly on the
  top-level plan, which has empty `:panels` for composites and
  rendered as a blank document.
- `render.membrane/plan->membrane` now defaults `:strip-h` and
  `:strip-w` to 0 for composites without strip labels, fixing an
  NPE in `clojure.lang.Numbers/isPos`.

### Input-validation guards close four silent-failure paths

Four entry-point inputs that used to silently produce nonsense or
leak raw exceptions now throw with clear `ex-info`:

- `(pj/pose nil)` and `(pj/lay-* nil ...)` -- previously returned an
  empty pose / empty plot. Now throw with guidance to use `(pj/pose)`
  for an explicit empty pose or pass real data. The template idiom
  `(pj/pose nil {:x :x :y :y})` (mapping without data, attached
  later via `pj/with-data`) still works -- nil is rejected only
  when no other arguments distinguish it from an empty pose.
- `(pj/pose 42)`, `(pj/pose "string")`, `(pj/pose :keyword)` --
  previously coerced through `tc/dataset` to a 1-row, 2-column
  garbage frame whose second column literally contained an
  exception message. Now throw naming the offending type.
- `(pj/save (pj/pose) "x.svg")` and `pj/save-png` on an empty
  pose -- previously wrote a blank file. Now throw with guidance.
- `(pj/pose data x y :not-a-map)` -- previously leaked a raw
  `ClassCastException` from a `dissoc` on a keyword. Now throws
  with a one-line message naming the bad opts argument.

### `pj/plot` honors `:format` from pose `:opts`

Setting `{:format :bufimg}` (or any registered backend keyword) in
a pose's `:opts` now selects the renderer for both leaf and
composite poses. Previously only the Kindly auto-render path for
composites read `:format`; explicit `pj/plot` calls and the leaf
auto-render hardcoded `:svg` regardless. The fix consolidates
format dispatch in `pj/plot`; `render-composite` now delegates
to `pj/plot` instead of duplicating the dispatch.

The fix is specific to `:format` -- audit confirmed every other
opt that affects rendering (`:tooltip`, `:brush`, `:title`,
`:width`, `:height`, `:theme`, `:palette`, `:color-scale`,
`:color-midpoint`) was already flowing through correctly via the
opts payload. Only `:format` was blocked because it occupies the
dispatch position rather than the payload.

The `:bufimg` backend is lazy-loaded on first use so SVG-only
users don't pay the load cost. `pj/save` and `pj/save-png` keep
their per-function fixed format (the function name carries the
backend choice); `:format` in opts only affects `pj/plot` and the
notebook auto-render paths.

### Opt-in strict option-key checking

New `:strict` config flag (default `false`). When `false`, an
unrecognized option key on `pj/pose`, `pj/lay-*`, or `pj/options`
prints a warning to stdout and the key is silently stripped (the
historical behavior). When `true`, the same case throws an
`ex-info` naming the unknown key and listing the accepted set.

Set via `plotje.edn`, `(pj/with-config {:strict true} ...)`, or
`(pj/set-config! {:strict true})`. Aimed at users who want
typo-mistakes (`{:colour :species}`, `{:scale-x :log}`) to surface
as errors instead of warnings that get lost in notebook output.
The default may move to `true` in 0.2.0 once we have a sense of
how often it would catch real mistakes vs. fire on benign
forward-compat keys.

### Faceted plots share scales by default

`pj/facet` and `pj/facet-grid` now coordinate the x-domain and
y-domain across all panels, matching ggplot2's `facet_wrap`
default. Previously each panel's domain was computed from its own
data, so the leftmost panel's tick labels (the only ones rendered)
implied a shared scale that didn't actually exist; points at the
same numeric position landed at different pixel heights across
panels.

The `:scales` option, documented in the `Faceting` chapter but
silently dropped in prior releases, is now wired up:

  | Value      | Behavior                                    |
  |:-----------|:--------------------------------------------|
  | `:shared`  | both axes coordinated (default)             |
  | `:free-y`  | x coordinated; y per-panel                  |
  | `:free-x`  | y coordinated; x per-panel                  |
  | `:free`    | both axes per-panel (the previous behavior) |

User-set domains via `pj/scale` continue to win over coordination.
Multi-pair composites built via `pj/pose` (where panels carry
different x/y columns) are unaffected -- coordination only fires
when the layout is a `:facet-grid`.

### Pose Rule LP2: distinct positional aesthetics mean distinct poses

Calling `pj/lay-*` on a leaf-with-position with a *different* x or
y column now throws. The rule the library was already trying to
teach -- a panel has a single x-axis and a single y-axis --
becomes structural: a layer can't override the pose's position to
a different column.

```clojure
;; Throws: "lay-point was given position columns that conflict
;; with the pose's existing position..."
(-> iris
    (pj/pose :sepal-length :sepal-width)
    (pj/lay-point :petal-length :petal-width))
```

Previously the layer's distinct mapping silently overlaid on the
same panel with a union x/y domain. Bucketing for shared scales
arbitrarily picked the first non-nil layer column (an alpha-only
"PoC simplification" called out in the source). That overlay
behavior was both undocumented in the spec ("Pose Rule LP2"
described an overlay note that contradicted the rule's own name)
and contradicted the prose in the gallery's multi-line examples
("each in its own panel" -- but they were on one panel).

To draw with different x/y columns, use a multi-pair pose for
separate panels:

```clojure
(-> data
    (pj/pose [[:date :unemploy] [:date :uempmed]])
    pj/lay-line)
```

Or arrange explicit sub-poses for mixed layer types per panel:

```clojure
(pj/arrange
 [(-> data (pj/lay-point :displ :hwy))
  (-> data (pj/lay-line :displ :cty))])
```

String-vs-keyword equivalence (Rule LI2) is honored by the check:
`:a` and `"a"` are the same column ref and don't trigger the
rejection. The same-column case (`(pj/lay-point pose :a :b)` on a
leaf with `:mapping {:x :a :y :b}`) continues to pass through
silently.

### Composite `:share-scales` is now a plot option

`:share-scales` (the set of axes coordinated across composite
cells) used to live at the top level of a composite pose --
alongside `:poses` and `:layout`. It is now a plot option that
lives in `:opts`, so users can tweak it after the fact:

```clojure
(-> (pj/arrange [a b c])
    (pj/options {:share-scales #{:x}}))
```

Previously the same call silently dropped the key, since `pj/options`
did not recognize it as a plot option. The compositor reads
`:share-scales` from `:opts` first and falls back to the legacy
top-level location, so hand-built composites that put the key at
the top continue to work without changes.

`pj/arrange` and the SPLOM grid constructor now write into
`:opts`. Test code that asserts the storage location should look
in `(get-in pose [:opts :share-scales])`.

### Overview

Plotje is a composable plotting library for Clojure, inspired by
the Grammar of Graphics. Plots are built by threading data through a
sequence of small transformations. The resulting pose is a plain
Clojure value that auto-renders in Kindly-compatible notebooks (Clay
and friends) -- no explicit render call required.

### Features

**Composable pipeline.** Everything you do to build a plot -- create
a pose (`pj/pose`), add layers (`pj/lay-*`), set aesthetic
mappings, apply scales and coordinate transforms, add facets, attach
annotations, set plot options -- flows through `->`. Every function
takes a pose and returns a pose, so there is no plot-assembly
order to memorize.

**Scoped mappings.** Aesthetic mappings (color, size, alpha, shape,
group, text) flow down a pose's tree from the root to every leaf
and then into each layer. Lower scopes override higher ones; `nil`
is an explicit cancellation. Scope is lexical: you set a mapping
where you want it to apply.

**~25 layer functions** covering the common chart types: point, line,
step, area, histogram, bar, value-bar, smooth (linear-model and loess
stats), text, label, density, tile, density2d, contour, boxplot,
violin, ridgeline, rug, summary (mean + SE), errorbar, lollipop, plus
four annotation layers (lay-rule-h, lay-rule-v, lay-band-h,
lay-band-v). Stack/dodge/fill positions are now options on lay-bar
and lay-area rather than separate layer functions.

**Statistics built in.** Histogram binning, categorical counts, OLS
regression (with optional confidence ribbons), LOESS with bootstrap
confidence bands, 1D and 2D kernel density estimation (the 2D case
uses Silverman's rule for the product-kernel bandwidth), 2D binning
for heatmaps, boxplot five-number summary with R type 7 quartiles,
violin density per category, and mean + standard error summaries.
Every stat is a multimethod -- you can register new ones.

**Faceting.** `pj/facet` for single-variable paneling (one row or
column of panels), `pj/facet-grid` for row x column grids, and
`pj/cross` as a helper for building scatter-plot matrices (SPLOM).
On the SPLOM diagonal, per-cell inference picks histograms
(univariate distributions) without an explicit lay-* call.

**Coordinate systems.** Cartesian (default), flip, polar, and fixed
(1:1 aspect ratio).

**Scales.** Linear, log, categorical, and datetime -- type is
auto-detected from the column and can be overridden via `pj/scale`.

**Continuous and categorical color.** Numeric columns map to a
continuous gradient (ggplot2-style dark-to-light blue by default;
`:viridis`, `:inferno`, `:plasma`, `:magma`, `:turbo`, etc. available
via `:color-scale`) with a color bar legend. Categorical columns draw
from one of ~7000 palettes exposed by clojure2d. Diverging color
scales with custom midpoints are also supported.

**Annotations.** Reference lines (`pj/lay-rule-h`, `pj/lay-rule-v`)
and shaded bands (`pj/lay-band-h`, `pj/lay-band-v`) as first-class
layers. Annotations attached at the root pose apply to every panel;
annotations attached at a leaf pose apply only to that panel.

**Per-scope data override.** `:data` can be set at the root pose,
nested pose, or layer level for mixed-data composites like
`layer A with dataset 1 + layer B with dataset 2`.

**Flexible input.** Tablecloth datasets, plain maps of columns,
sequences of row maps, or CSV/URL paths. Column names can be keywords
or strings. Literal colors can be hex strings, named CSS colors, or
keywords.

**Two render backends.** SVG (default, via membrane) for notebooks
and the web; raster PNG via `:format :bufimg` for large scatters or
Java2D-based export. Both are exercised by the same plan -- only the
terminal stage differs.

**Multi-plot dashboards.** `pj/arrange` composes several plots into a
CSS grid, with optional titles and flexible row/column layouts.

**Interactivity.** Opt-in hover tooltips (`{:tooltip true}`) and
drag-to-select brushes (`{:brush true}`) via lightweight inline
JavaScript.

**Layered configuration.** Per-plot options > thread-local
`with-config` > global `set-config!` > `plotje.edn` > library
defaults. Theme values deep-merge so partial overrides don't clobber
the rest.

**Clear errors on natural mistakes.** Missing or typoed column
references, mixed-type columns, unknown layer types, unknown options
on `pj/pose`/`pj/options`/`pj/lay-*`, categorical columns passed
to numeric stats, and numeric x passed to categorical marks all fail
with a clear, actionable error pointing at the offending input.

**Plan inspection.** `pj/plan` returns a plain Clojure data structure
(domains, ticks, legends, layout, resolved layers) that can be
inspected, serialized, or re-rendered by alternative backends.
`pj/svg-summary`, `pj/valid-plan?`, and `pj/explain-plan` support
structural testing.

**Extensibility.** Eight multimethods -- `compute-stat`,
`extract-layer`, `layer->membrane`, `make-scale`, `make-coord`,
`apply-position`, `plan->plot`, `membrane->plot` -- are open
extension points. The `waterfall_extension` chapter in the book walks
through a complete end-to-end custom mark.

### Book

Twenty-nine executable chapters:

- **Getting Started** -- quickstart
- **Foundations** -- datasets, pose model, core concepts, options
  and scopes, pose rules, inference rules, layer types, glossary
- **Chart Types** -- scatter, distributions, ranking, change over
  time, relationships, polar
- **How-to Guides** -- cookbook, configuration, customization,
  faceting, composition, troubleshooting
- **Reference** -- api reference
- **Gallery** -- ~150 examples reproducing charts from the R Graph
  Gallery, Vega-Lite Examples, Python Graph Gallery, ECharts, and D3
- **Internals** -- exploring plans, architecture, extensibility,
  waterfall extension, edge cases, development

Every notebook embeds runnable regression tests via `kind/test-last`.
Combined with a hand-written core test suite, the project runs over
1000 tests / over 1600 assertions.

### Architecture

Five-stage pipeline:

```
pose -> draft -> plan -> membrane -> figure
```

- **Pose** -- user-facing composable value (`data + mapping + layers`,
  with optional `sub-poses + layout + opts`). Plain recursive map.
- **Draft** -- flat maps produced by `pj/draft` (one per applicable
  layer in each leaf pose), the bridge between API composition and
  planning.
- **Plan** -- plain Clojure data with dtype-next buffers (domains,
  ticks, legends, layout, resolved layers); serializable, inspectable,
  rendered by multiple backends.
- **Membrane** -- drawable primitives (rectangles, paths, text,
  translated/colored groups).
- **Figure** -- terminal output: SVG hiccup or raster PNG.

The pipeline's layering is strict: data transformations live under
`impl/`; drawing primitives live under `render/`; nothing in
`render/` knows about datasets or column names.

### Known limitations

These are documented and tracked for post-alpha work. None of them
produce crashes on canonical inputs.

**Layout and visuals:**

- Multi-layer overlays like `(-> data (pj/lay-point ...) (pj/lay-lm ...)
  (pj/lay-loess ...))` do not auto-generate a layer-kind legend to
  distinguish the two regression curves. Workaround: color each
  layer explicitly.
- Histograms, stacked bars, step plots, and other stat-derived
  marks do not default to a `"count"` or `"density"` y-label.
- Linear continuous color legends (numeric `:color` mapping with
  `:linear` scale) label only the endpoint tick marks on the
  gradient bar. Intermediate values are unlabeled, making it hard
  to map interior colors back to data values. Log-scaled
  continuous color and fill legends do carry intermediate ticks.
- SPLOMs with 6+ variables at the default 600x400 have tight panels.
  Bump `:width`/`:height` or pin `:panel-width`/`:panel-height`.
- Horizontal bars from `(pj/coord :flip)` render the first row of
  data at the bottom of the chart, so a dataset sorted descending
  (natural "top N" order) produces the biggest bar at the bottom.
  The behavior matches ggplot2's `coord_flip()`. Workaround: sort
  the dataset ascending before plotting, e.g.
  `(tc/order-by data [:value] [:asc])`. A future opt-in flag such
  as `(pj/coord :flip {:reverse-categorical true})` would spare
  users the sort. Reported in user-report-1 Issue 3.

**Marks:**

- `:position :dodge` is ignored at render-time on nine marks
  including `summary` -- on `lay-bar` and `lay-summary` the dodge
  request is dropped at construction; on `lay-point` and `lay-line`
  the dodge metadata reaches the plan but no geometric offset is
  applied. Workaround: pre-compute dodge offsets via `tc/group-by`.
- Polar plots for bar-family marks don't auto-emit category labels
  -- rose charts currently render with zero text.
- Stacked bars don't split positive and negative values; all-positive
  data works, but mixed-sign data stacks incorrectly.
- `pj/lay-tile` (and the underlying `:bin2d` stat) requires numeric
  x and y columns. Passing a categorical axis throws a clear
  "Stat :bin2d requires a numeric column" error at plan time.
  Workaround: bin externally and render with explicit numeric bin
  centers, or use `pj/lay-value-bar` with `{:color :value}` for a
  categorical-axis "heatmap" look.
- `pj/lay-bar` with `:position :stack` (or `:fill`) is count-only
  and rejects a `y` column -- there is no clean way to render a
  stacked bar chart of pre-aggregated values (e.g. a "messages
  per year broken down by tenure bucket" chart where the counts
  are already computed). `pj/lay-area` with `:position :stack`
  does accept pre-aggregated `y`, so the pattern works there.
  Workarounds: lift the aggregation (expand each row back into
  count-many duplicate rows so `:count` sums to the pre-aggregated
  value), or use `pj/lay-area` with `:position :stack` on a
  numeric x. A proper fix (lifting the count-only restriction
  when `y` is supplied) is planned. Reported in user-report-2
  Issue 2.
- Stack order in `pj/lay-area` and `pj/lay-bar` (with
  `:position :stack`) follows the sort order of the `:color`
  column. There is no `:stack-order` / `:color-order` option yet,
  so forcing a specific bottom-to-top order requires prefixing
  category labels with sort-stable ordinal characters
  (`"01: ..."`, `"02: ..."`), which leaks into the legend.
  Reported in user-report-2 minor observation.
- Annotations are silently skipped under `(pj/coord :polar)`. A polar
  rule would need to render as a circle (fixed radius) or spoke
  (fixed angle); those shapes are not implemented. Use Cartesian or
  flip coords for annotated plots.
- Large scatters produce large SVGs (~220 bytes/point). For >10k
  points, use `:format :bufimg` for raster output.
- `pj/save-png` (and the `:bufimg` raster path generally) truncates
  the rotated y-axis label after ~6 characters. The SVG path
  (`pj/plot` + Clay GFM, or `rsvg-convert` on the saved SVG)
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
- The `:width` key on a `pj/plan` result preserves the user's
  original request even when `:panel-width` pins the real size --
  inspect `:total-width`/`:total-height` for the rendered canvas.
- `pj/plan` called on a plan or on a hiccup value now throws a
  clear error. Call `pj/plan` only on poses.

**Mixing keyword and string column references:**

- Mapping the same column with a keyword in one place and a string
  in another (e.g. `(pj/pose ds {:color :group})` then
  `(pj/lay-point :x :y {:color "group"})`) is not normalized: the
  scope hierarchy treats them as different keys and the result is a
  silent empty plot. Workaround: pick one form (keyword or string)
  and use it consistently within a pose.

**ggplot2 features not yet implemented:**

- Per-layer `data`, `guides()` for per-aesthetic legend control,
  named theme presets, `scale_*_sqrt`/`reverse`/`date`. All tracked
  in the backlog.
