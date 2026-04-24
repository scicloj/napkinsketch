# plotje — user report 1

**Reporter:** Daniel Slutsky (via Claude Code session)
**Date:** 2026-04-19
**Context:** Integrating plotje into the `zulipdata` prototype — replacing a `tableplot` setup with `plotje` for the first time. Hit several friction points worth reporting.

## Environment

- plotje: `:local/root "../plotje"` (working tree as of 2026-04-19)
- Clay: `2.0.9`
- Noj: `2-beta24`
- Tablecloth: `7.029.2` (zulipdata's top-level pin; plotje's own deps request `7.067`, but the top-level wins in dep resolution — no runtime issues observed)
- JDK: Java 21 (Linux)

The notebook we rewrote produces four charts: a multi-series line (messages-per-month by channel), two horizontal bars (top senders, top emojis via `coord :flip`), and a vertical bar (messages per hour-of-day). Data is a `tc/dataset` from tablecloth.

Each issue below has a copy-pasteable minimal reproduction. Run them in a REPL with:

```clojure
(require '[scicloj.plotje.api :as sk])
```

---

## Issue 1 — `sk/plot` is required for GFM render; no warning if omitted

> **FIXED** (2026-04-19, commit `ad5592d`). Root cause: `render-sketch`
> wrapped its SVG hiccup in `[:div {:style {...}} svg]` for spacing in
> interactive HTML. Clay's GFM renderer recognises a top-level
> `kind/hiccup [:svg ...]` as an image to extract, and does NOT when the
> SVG is nested in a `[:div ...]`. `render-sketch` now returns the raw
> `kind/hiccup [:svg ...]` directly. `gfm/plotje_book.ranking.md`
> went from 0 to 10 image references in one regeneration pass.


**Severity:** Medium — silent failure, unclear from the README/example hierarchy.

**Summary.** If a pipeline ends at `sk/lay-line` (or any `lay-*`) without an explicit `sk/plot`, the expression returns a `scicloj.plotje.impl.sketch.Sketch` record. Clay treats it as a Kindly-kinded value — and in interactive HTML/Clay rendering this works fine — but **in `:format [:gfm]` the Sketch renders as nothing**: no SVG file is written, and the markdown has empty lines where the chart should be.

Appending `sk/plot` converts the Sketch into a hiccup `[:svg ...]` vector that Clay's GFM renderer picks up, writes as `image<N>.svg`, and references with a markdown `![]()` tag.

**Minimal repro.**

```clojure
;; Without sk/plot — renders blank in GFM
(-> {:x [1 2 3] :y [1 4 9]}
    (sk/lay-line :x :y))
;; => #scicloj.plotje.impl.sketch.Sketch{...}

;; With sk/plot — renders as an SVG image in GFM
(-> {:x [1 2 3] :y [1 4 9]}
    (sk/lay-line :x :y)
    sk/plot)
;; => [:svg ...] (hiccup vector)
```

Repro the render asymmetry by authoring a Clay notebook with both forms and running `clay/make!` with `:format [:gfm]`. Only the second form produces an `image<N>.svg` in the `*_files/` directory.

**Expected.** Either (a) `sk/plot` should be implicit in `:format [:gfm]`, or (b) the README's "Quickstart" examples should make clear that bare `lay-*` expressions work only in interactive kinds, and a trailing `sk/plot` is mandatory for GFM/static export.

Note: plotje's own `notebooks/plotje_book/ranking.clj` omits `sk/plot` at the ends of its pipelines; the rendered `gfm/plotje_book.ranking.md` also has no `![]()` image references (though stale `image<N>.svg` files from an earlier render sit in the `_files/` directory). It looks like the GFM flow broke for this chapter at some point and wasn't caught.

---

## Issue 2 — `sk/lay-value-bar` rejects numeric `:x` at render time (reported; the user is already working on this)

> **FIXED** (2026-04-19, commit `5edf0ff`). The `:x-type :categorical`
> / `:y-type :categorical` layer and view option now propagates through
> the accept-lists (`view-mapping-keys`, `universal-layer-options`) that
> previously stripped it. The `tc/convert-types` workaround is no
> longer required:
>
> ```clojure
> (-> hourly-activity (sk/lay-value-bar :hour :messages {:x-type :categorical}))
> ```
>
> Documented in:
>
> - inference_rules.clj, Column Types section
> - troubleshooting.clj, new "Numeric Column Rejected by a Categorical-Axis Mark" entry
> - core_concepts.clj, Inference section ("every inferred choice can be overridden")
> - methods.clj reference table, via `layer-option-docs` entries for `:x-type` and `:y-type`.


**Severity:** Medium — legitimate error but fires late (at `sk/plot`, not at `lay-value-bar`), and the remedy is to cast the column outside plotje.

**Summary.** Passing a numeric column as the categorical axis of a value-bar throws `ExceptionInfo` from `scicloj.plotje.impl.extract`. The message is clear and suggests the cast to string, which works. The friction is only that "hour of day" feels conceptually categorical but arrives as `:int64` from `tc/group-by`, so the error reads as surprising until you realize the library treats "categorical" by dtype, not by intent.

**Minimal repro.**

```clojure
;; Throws at plot time:
(-> {:x [1 2 3] :y [10 20 15]}
    (sk/lay-value-bar :x :y)
    sk/plot)
;; ExceptionInfo: Mark :rect (lay-value-bar) requires a categorical column
;;   for :x, but :x is numerical. Use lay-line/lay-point for numeric x,
;;   or convert :x to a string column.
```

**Workaround we used.**

```clojure
(-> hourly-activity
    (tablecloth.api/convert-types {:hour :string})
    (sk/lay-value-bar :hour :messages)
    sk/plot)
```

(User message noted this is already being worked on.)

---

## Issue 3 — `(sk/coord :flip)` draws the first row at the bottom, not the top

> **DEFERRED (post-alpha).** The behavior matches ggplot2's
> `coord_flip()`; changing the default would also change every existing
> ranking plot. A targeted opt-in such as
> `(sk/coord :flip {:reverse-categorical true})` or
> `(sk/options {:categorical-order :reverse})` is the likely path,
> pending the design call.
>
> Logged in `CHANGELOG.md` under Known limitations → Layout and visuals
> (see "Horizontal bars from `(sk/coord :flip)` render the first row
> ... at the bottom").


**Severity:** Low/medium — usability / principle-of-least-surprise for ranking charts.

**Summary.** In a horizontal bar chart made with `(sk/coord :flip)`, the rows are drawn **bottom-to-top in data order**. So a dataset sorted **descending** (biggest value first, which is the natural table ordering for "top N") produces a chart with the biggest bar at the *bottom*. To get "biggest at top" — the conventional ranking view — you must sort the dataset **ascending** before plotting.

This is familiar to ggplot2 users (ggplot2's `coord_flip()` has the same behavior and needs `scale_x_discrete(limits = rev(...))`), so it's defensible as "same as ggplot2." But it's surprising for newcomers and couples the plot order to the dataset order.

**Minimal repro.**

```clojure
(-> {:category ["A" "B" "C"] :value [100 50 25]}
    (sk/lay-value-bar :category :value)
    (sk/coord :flip)
    sk/plot
    ;; A (biggest) appears at the BOTTOM of the vertical axis; C at the top.
    )
```

**Workaround we used.**

```clojure
(-> top-senders
    (tc/order-by [:messages] [:asc])   ;; reverse sort just for the chart
    (sk/lay-value-bar :sender :messages)
    (sk/coord :flip)
    sk/plot)
```

**Suggested change.** Either (a) provide an option like `(sk/coord :flip {:y-order :reverse})`, or (b) have flipped value-bar auto-reverse the categorical axis. (a) is safer and keeps current behavior explicit.

---

## Issue 4 — `sk/save-png` truncates the y-axis label to ~6 characters

> **DEFERRED (post-alpha, upstream bug).** The SVG path is correct;
> the `:bufimg` / Java2D rasterisation path
> (`membrane.java2d/draw-to-image`) truncates the rotated y-label
> after ~6 characters. The bug lives in `membrane`, not in
> plotje's own code. A clean fix needs a reproducer against
> membrane and either a fix there or a local workaround in
> `render/bufimg.clj` (for example, widening the Java2D raster target
> before rasterising so the rotated-text bounding box is not clipped).
>
> Logged in `CHANGELOG.md` under Known limitations → Marks (see "`sk/save-png`
> ... truncates the rotated y-axis label"). Workarounds: render to SVG
> and rasterize externally, or keep `:y-label` short.


**Severity:** Medium — affects any PNG export where the y-axis column name is longer than ~6 chars.

**Summary.** When exporting a chart via `sk/save-png`, the rotated y-axis label is clipped after roughly 6 characters. The SVG produced by `sk/plot` contains the full label (verified by inspecting the hiccup tree and by rasterizing the SVG separately with `rsvg-convert`, which produces the correct full label). So the bug lives in the PNG rasterization path (likely the Java2D backend), not in the SVG generation.

Observed clippings:

| Intended y-label       | save-png output |
|------------------------|-----------------|
| `messages`             | `messag`        |
| `category`             | `catego`        |
| `really-long-label`    | `really`        |
| `sender`               | `sender` (fits) |

**Minimal repro.**

```clojure
(sk/save-png
 (-> {:x ["a" "b" "c"] :really-long-label [10 20 15]}
     (sk/lay-value-bar :x :really-long-label))
 "/tmp/repro-label.png")
;; Open /tmp/repro-label.png — y-axis label shows "really", not "really long label".
```

For comparison, rendering the same sketch via Clay in `:format [:gfm]` (which writes SVG and references it from markdown) produces an SVG whose `<text>` element contains the full string. Rasterizing that SVG via `rsvg-convert` yields the full label.

**Workaround.** Render to SVG via Clay GFM and convert externally, or inline-embed the SVG.

**Suggested fix.** Investigate the Java2D/membrane rasterization path for the rotated axis label — probably a clip rectangle or margin that isn't being extended to cover the rotated text's bounding box.

---

## Issue 5 — Legend entries clip at the right edge of the default-size chart

> **FIXED** (2026-04-19, commit `2d7b837`). `pad-legend-w` now extends
> the legend column width based on the longest legend label (title or
> entry). Short labels still fit within the 100-pixel default;
> "tech.ml.dataset.dev" now gets enough room (~165 px) to render
> fully. Implemented by adding `:legend-max-chars` to the scene in
> `compute-scene` and consulting it from `pad-legend-w`.


**Severity:** Low — cosmetic, easily worked around with `(sk/options {:width N})`.

**Summary.** With the default chart size (600×400), a color-mapped line chart whose legend values are longer than ~15 characters clips the last few characters at the SVG right boundary. The SVG text element contains the full string, but the canvas isn't wide enough, so the visible text is cut off.

**Minimal repro.**

```clojure
(sk/save-png
 (-> {:x [1 2 3 1 2 3]
      :y [1 2 3 2 4 6]
      :category ["short" "short" "short"
                 "tech.ml.dataset.dev" "tech.ml.dataset.dev" "tech.ml.dataset.dev"]}
     (sk/lay-line :x :y {:color :category}))
 "/tmp/repro-legend.png")
;; "tech.ml.dataset.dev" in the legend shows as "tech.ml.dataset."
```

**Workaround.** Pass a wider `:width` via `(sk/options {:width 900 :height 420})`.

**Suggested improvement.** Auto-compute legend column width from the longest entry and extend the SVG/panel accordingly, or clip-with-ellipsis instead of hard-cut.

---

## Minor observation — hyphens in column names become spaces in axis labels

Column name `:really-long-label` is rendered on the axis as `"really long label"` (hyphens replaced with spaces). This is a stylistic decision and not a bug in our view; flagging it because a user who has a hyphen-sensitive naming convention (e.g. `"rpm-200"` meaning "200 rpm") might find it surprising. A short note in the docs would suffice.

> **Documented** in `inference_rules.clj`, Summary table row "Axis labels"
> (line: "column name, with underscores replaced by spaces"). The same row
> could also mention hyphens, since `resolve-labels` does both. Minor
> wording polish; not a behavior change.

---

## What worked well

- `sk/lay-line` with `{:color :channel}` — the multi-series coloring on a `tc/dataset` worked on the first try.
- `java.time.LocalDate` x values for time series — plotje auto-selected sparse calendar-aligned tick labels, which was the main thing we lost when we tried using `"YYYY-MM"` strings.
- `sk/svg-summary` — made it possible to assert chart structure (number of polygons, label set) from a REPL or test without rendering, a nice capability for regression-testing that the other obvious libraries lack.
- Error message for Issue 2 was clear and suggested the exact fix.

## Suggested priorities

1. **Issue 1** — tighten the docs (or change behavior) so GFM rendering doesn't silently drop charts. Biggest footgun for anyone using Clay + GFM.
2. **Issue 4** — the `save-png` truncation is the most visible correctness bug; anyone using `save-png` for publication-quality figures will hit it.
3. **Issue 3** — consider an option like `(sk/coord :flip {:reverse-categorical true})` to spare every ranking-chart author from doing the ascending-sort dance.
4. **Issue 5** — nice-to-have; worked around trivially with `:width`.

Happy to expand any of these into PRs if that would help.
