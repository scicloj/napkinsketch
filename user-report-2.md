# napkinsketch — user report 2

**Reporter:** Daniel Slutsky (via Claude Code session)
**Date:** 2026-04-19
**Context:** Continuing the `zulipdata` integration. After the round
of fixes in user-report-1, I deliberately stretched napkinsketch with
a wider variety of plot types I'd actually want for community-history
analysis: distributions, ridgelines, lollipops, errorbars, faceted
small-multiples, custom palettes, annotations, log scales, stacked
bars with pre-aggregated values, and so on. Most things worked.
This report is the friction list from the rest.

## Environment

- napkinsketch: `:local/root "../napkinsketch"` (working tree as of 2026-04-19, post user-report-1 fixes)
- Clay: `2.0.9`
- Tablecloth: `7.029.2`
- JDK: Java 21 (Linux)

Each issue below has a copy-pasteable minimal reproduction. Run them
in a REPL with:

```clojure
(require '[scicloj.napkinsketch.api :as sk])
(require '[tablecloth.api :as tc])
```

---

## Issue 1 — No log-scale support; `:scale-x` / `:scale-y` silently warned, then ignored

> **PARTIALLY ADDRESSED** (2026-04-20). Log scale IS supported, just
> through `sk/scale` rather than layer-option keys:
>
> ```clojure
> (-> data (sk/lay-histogram :msgs) (sk/scale :x :log))
> ```
>
> The function was not being discovered on the layer-option path.
> Added a troubleshooting entry in `troubleshooting.clj`
> ("Log Scale via `:scale-x` / `:scale-y` Options") that explains
> the symptom, names `sk/scale`, and shows a working example.
> Post-alpha follow-ups (not blocking): accept `:scale-x` / `:scale-y`
> as sketch-level shortcuts; add a "log-scale" quickstart row.


**Severity:** Medium — every long-tail / power-law dataset wants this, and the warning doesn't tell the user what to do.

**Summary.** Passing `{:scale-x :log}` (or `{:scale-y :log}`) to a layer
or to `sk/options` prints a `Warning: ... does not recognize option(s)`
and produces a linear-scale chart. `sk/options`'s accept-list does not
include any `:scale-*` key. There appears to be no way to render a log
axis without manually computing the transform on the dataset side.

In the zulipdata case I had ~960 users with messages-per-user spanning
1 to 15,054 — the natural way to present that is a log-scale histogram
or a log-scale ranked-bar chart. Without log scale support, the only
options are pre-computing `(Math/log10 messages)` on the dataset (which
loses the meaningful tick labels) or accepting an unreadable chart.

**Minimal repro.**

```clojure
(-> {:user (range 1 1001) :msgs (map #(int (Math/exp (/ % 50.0))) (range 1 1001))}
    tc/dataset
    (sk/lay-histogram :msgs {:scale-x :log})
    sk/plot)
;; Warning: lay-histogram does not recognize option(s): [:scale-x].
;; Accepted: [:alpha :bins :binwidth :color :color-type :data :group
;;            :normalize :position :x :x-type :y :y-type]
;; → renders linear histogram, totally dominated by the few large values.
```

**Expected.** Either (a) accept `:scale-x :log` / `:scale-y :log` on
layers and on `sk/options`, or (b) document the manual `Math/log10`
recipe in a "common gotchas" section, or (c) provide a sk-level helper
like `(sk/scale :y :log10)`.

**Workaround used.** Compute `:log10-messages` by hand and label it as
`"log10(messages)"`, losing the ability to read the original units off
the axis.

---

## Issue 2 — `lay-stacked-bar` / `lay-stacked-bar-fill` reject pre-aggregated `y`

> **DEFERRED (post-alpha).** Real functionality gap. The cleanest
> resolution is a new `sk/lay-stacked-value-bar` (mirror of
> `sk/lay-value-bar` for stacks) rather than overloading the
> existing count-only method, since the two semantics differ. Not
> started pre-alpha.
>
> Logged in `CHANGELOG.md` under Known limitations → Marks
> ("`sk/lay-stacked-bar` ... are count-only and reject a `y`
> column..."). Workaround: expand pre-aggregated counts back into
> row duplicates so `:count` sums to the pre-aggregated value, or
> use `sk/lay-stacked-area` on a numeric x.


**Severity:** Medium-high — common pattern (precomputed counts → stacked bar) is blocked.

**Summary.** Both `lay-stacked-bar` and `lay-stacked-bar-fill` refuse
to accept a `y` argument with a clear error message:

```
lay-stacked-bar uses only the x column; do not pass a y column
lay-stacked-bar-fill uses only the x column; do not pass a y column
```

These marks appear to be count-only (they internally bin by `x` and
stack the row-counts). This means there is no clean way to render a
stacked bar chart of *pre-aggregated* values, e.g. a "messages-per-year
broken down by tenure-bucket" stacked bar where you have already
grouped/aggregated the data into one row per (x, color) cell.

The `lay-stacked-area` mark *does* accept x and y for pre-aggregated
data — so the pattern is supported there but not for bars, which is
inconsistent.

**Minimal repro.**

```clojure
(def yearly
  (tc/dataset [{:year "2019" :tenure "new"     :msgs 200}
               {:year "2019" :tenure "1-3 yr"  :msgs 100}
               {:year "2020" :tenure "new"     :msgs 50}
               {:year "2020" :tenure "1-3 yr"  :msgs 300}]))

(-> yearly
    (sk/lay-stacked-bar :year :msgs {:color :tenure :x-type :categorical})
    sk/plot)
;; ExceptionInfo: lay-stacked-bar uses only the x column; do not pass a y column

;; Workaround in our chapter: switch to lay-stacked-area on a numeric
;; x. Loses the discreteness that bars convey.
```

**Expected.** Add a `lay-stacked-value-bar` (and a `-fill` variant)
that mirrors `lay-value-bar` for stacked groups — i.e. accepts both
`x`, `y`, and `:color` and stacks the y values per (x, color).

Alternatively, lift the restriction on the existing `lay-stacked-bar`
when `y` is supplied (the current count-based behavior would still
apply when `y` is omitted).

---

## Issue 3 — `lay-text` with a scalar `:y` value throws an opaque `ClassCastException`

> **FIXED** (2026-04-20). `build-layer` in `api.clj` now validates
> `:x` and `:y` in a layer's options and throws a clear error
> when either is not a column reference (keyword or string). The
> new message is:
>
> > `lay-text :y must be a column reference (keyword or string),
> > but got 200. For a constant position, add a column to :data with
> > that value, e.g. (tc/add-column data :y (constantly 200)) and
> > pass :y :y.`
>
> Same validation applies to every `sk/lay-*` function that accepts
> options.


**Severity:** Medium — the natural annotation pattern (label at fixed y) crashes with no useful guidance.

**Summary.** When using `lay-text` with a separate annotation dataset,
it is natural to pass a constant `:y` value (so all the annotation
labels sit on a horizontal line). Doing so triggers a
`ClassCastException` from deep inside the extract code; the user has
no indication that the constant is being treated as a column name and
that the message strings are then being cast as numbers.

**Minimal repro.**

```clojure
(def main
  (tc/dataset {:date (map #(java.time.LocalDate/of 2024 % 1) (range 1 13))
               :msgs (range 100 220 10)}))

(def labels
  (tc/dataset {:date  [(java.time.LocalDate/of 2024 6 1)]
               :event ["release"]}))

(-> main
    (sk/lay-line :date :msgs)
    (sk/lay-text {:data labels :x :date :y 200 :text :event})
    sk/plot)
;; ClassCastException: class java.lang.String cannot be cast to
;;   class java.lang.Number
;;   (...the :event strings are being interpreted as if :y were a
;;    column reference and `200` had named a column.)
```

**Workaround we used.** Add a `:y` column to the annotation dataset:

```clojure
(-> labels
    (tc/add-column :y (constantly 200))
    (#(sk/lay-text main {:data % :x :date :y :y :text :event}))
    sk/plot)
```

**Expected.** Either (a) accept scalar values for `:x` / `:y` and
broadcast them across the rows of `:data`, or (b) check the type of
the `:y` argument early and throw a clear "expected column key, got
number 200 — did you mean to pass a constant via `(constantly 200)` or
add the column with `(tc/add-column :y (constantly 200))`?".

---

## Issue 4 — `lay-summary`'s "categorical x required" error mentions `lay-pointrange`

> **FIXED** (2026-04-20). `resolve-method-info` in `sketch.clj` now
> preserves `:method` (the user-invoked layer-function key) alongside
> `:mark`/`:stat`/`:position`. The validation in `resolve.clj` uses
> `:method` instead of `:mark` for error messages, so `lay-summary`
> on a numeric x now says "lay-summary requires a categorical :x
> column, ..." (not "lay-pointrange"). The message also appends the
> `{:x-type :categorical}` escape hatch hint on both categorical-x
> checks (x-only marks + boxplot/violin two-axis marks).


**Severity:** Low — confusing internal name leaks into the user-facing error message.

**Summary.** Calling `lay-summary` on a numeric x-axis throws an error
that references `lay-pointrange` (an internal method name), not the
function the user actually called. Users who don't know the internals
will be confused by an error blaming a function they never invoked.

**Minimal repro.**

```clojure
(-> {:date (map #(java.time.LocalDate/of 2024 % 1) (range 1 13))
     :y    (range 12)}
    tc/dataset
    (sk/lay-summary :date :y)
    sk/plot)
;; ExceptionInfo: lay-pointrange requires a categorical :x column,
;;   but :date is numerical. Use a categorical column (e.g., species
;;   names) for the x-axis.
```

The error message is also a missed teaching moment: it doesn't mention
the `{:x-type :categorical}` escape hatch (introduced in user-report-1
fix `5edf0ff`) which works here:

```clojure
(-> ds (sk/lay-summary :year :y {:x-type :categorical}) sk/plot)  ;; works
```

**Expected.** Replace `lay-pointrange` with the user-facing function
name in the error (`lay-summary`), and append a one-liner like
"or pass `{:x-type :categorical}` to treat a numeric column as
categorical." The same hint would help all the other "categorical x
required" error sites — `lay-ridgeline`, `lay-violin`, `lay-boxplot`,
etc.

---

## Issue 5 — Faceting via `:facet-col` / `:facet-x` in `view` is silently ignored

> **FIXED** (2026-04-20). The helpful error that `make-view` used
> to raise only for `:facet-col`/`:facet-row` is now factored into a
> shared `check-facet-keys` helper that `build-layer` also calls.
> `:facet-col`, `:facet-row`, `:facet-x`, and `:facet-y` in either
> a view's or a layer's options now all raise:
>
> > `Faceting is plot-level, not layer-level. Use (sk/facet sk col)
> > or (sk/facet-grid ...) instead of putting :facet-col in a
> > layer's options map.`


**Severity:** Low — `sk/facet` and `sk/facet-grid` work fine; the silent ignore for the wrong API is the friction.

**Summary.** Putting `:facet-x` (or `:facet-col`) into the mapping passed
to `sk/view` (or to a layer's options) produces only a `Warning: sk/view
does not recognize option(s): [:facet-x]` and renders a single-panel
chart. The internal code in `view`/`api.clj` actually *does* check for
`:facet-col` / `:facet-row` in the mapping and throws a helpful error
("Use (sk/facet sk col) or (sk/facet-grid …) instead") — but only when
the keys arrive there directly. Through the public path I used, the
keys were dropped at the option-validation step before that check ever
saw them.

**Minimal repro.**

```clojure
(def ds (tc/dataset {:x (range 30) :y (map #(* % %) (range 30))
                     :group (cycle ["A" "B" "C"])}))

;; Quietly does the wrong thing:
(-> ds (sk/lay-line :x :y {:facet-x :group}) sk/plot)
;; Warning: sk/view does not recognize option(s): [:facet-x].
;; Accepted: [:alpha :color :color-type :data :fill :group :shape
;;            :size :text :x :x-type :y :y-type :ymax :ymin]
;; → single-panel chart with all 30 points joined by one line.

;; Right way (works):
(-> ds (sk/lay-line :x :y) (sk/facet :group) sk/plot)
;; → 3 panels, A/B/C.
```

**Expected.** When `:facet-col` / `:facet-row` / `:facet-x` / `:facet-y`
is passed in a layer or view options map, raise the same helpful error
that the internal `view` code already raises ("Use `sk/facet`…"). It
already exists; it just isn't reached on this path.

---

## Minor observation — no obvious way to control stacking order

> **DEFERRED (post-alpha).** A `:stack-order` or `:color-order`
> option is a real gap but not alpha-blocking; the ordinal-prefix
> workaround (`"01: new"`, `"02: 1-3 yr"`) is ugly but functional.
>
> Logged in `CHANGELOG.md` under Known limitations → Marks
> ("Stack order in `sk/lay-stacked-area` and
> `sk/lay-stacked-bar` follows the sort order of the `:color`
> column...").


In `lay-stacked-area` the bottom-to-top stack order follows the sort
order of the `:color` column. To get a meaningful stack (e.g. tenure
bands going 3+ years at the bottom, "new" at the top), the workaround
is to prefix the bucket labels with sort-stable ordinal characters
(`"01: new"`, `"02: 1-3 mo"`, …). It works, but the labels in the
legend then carry the ordinal noise. A `:color-order` (or
`:stack-order`) option taking either a vector of values or a comparator
would clean this up.

---

## What worked well

A long list this round:

- **`lay-histogram`, `lay-density`, `lay-density2d`** — first try, on
  full-corpus distributions of message length and reactions.
- **`lay-boxplot`, `lay-violin`** — both worked when `{:x-type :categorical}`
  was supplied for a numeric year column.
- **`lay-ridgeline`** — same: works with `{:x-type :categorical}`.
- **`lay-step`, `lay-area`** — clean cumulative-user curve.
- **`lay-lollipop` + `(sk/coord :flip)`** — drop-in replacement for the
  horizontal-bar ranking; nicer for sparse data.
- **`lay-tile` with numeric x and y, `:fill :value`** — proper heatmap.
  (Categorical-y heatmap is still blocked — see user-report-1's tile
  notes — but the numeric-x/y path is solid.)
- **`lay-errorbar` layered onto `lay-line`** — monthly mean ± SD of
  message length came out clean.
- **Layered marks** — `lay-line + lay-point + lay-loess`, and
  `lay-density + lay-rug` both worked first try with no friction.
- **`{:color :year :color-type :categorical}` on `lay-density`** — clean
  per-year overlay of length distributions.
- **`sk/facet`** — works as advertised once you find it; produced 8
  per-channel panels of monthly activity. (See Issue 5 about
  discoverability.)
- **`sk/options {:palette […]}`** — custom palette accepted directly.
- **`sk/svg-summary`** — invaluable for verifying chart structure
  (panel count, line count, label set) from the REPL without rendering
  to a file. Used it heavily this session for sanity checks; please
  preserve.

## Suggested priorities

1. **Issue 2** (stacked-bar with pre-aggregated y) — most blocking;
   anyone doing aggregate-then-plot will hit it immediately.
2. **Issue 1** (no log scale) — affects every long-tail dataset; even
   a documented manual recipe would reduce friction.
3. **Issue 4** + the broader "categorical x required" family — small
   cleanup that improves teaching quality across many marks.
4. **Issue 3** (scalar `:y` in `lay-text`) — narrow but the failure mode
   is severe (opaque ClassCastException).
5. **Issue 5** (silent facet-key swallow) — easy fix, surfaces an
   already-existing helpful error on the right path.

Happy to expand any of these into PRs if useful.
