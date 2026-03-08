# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**napkinsketch** — composable plotting in Clojure

<!-- TODO: Add architecture description, main components, data model, etc. -->

## Development Commands

### Running Tests

```bash
./run_tests.sh
```

Tests include:
- Hand-written tests in `test/scicloj/napkinsketch/`
- Generated tests from notebooks (via `kind/test-last`) in `test/napkinsketch_book/`

### Building and Deployment

```bash
clojure -T:build ci        # test + build JAR
clojure -T:build deploy    # deploy to Clojars
./release.sh               # build + deploy
./snapshot.sh              # build + deploy snapshot
```

### REPL Development

```bash
clojure -M:dev -m nrepl.cmdline
```

Always use `:reload` when requiring namespaces.

## Test Structure

- `test/scicloj/napkinsketch/` — hand-written tests
- `test/napkinsketch_book/*_generated_test.clj` — generated from notebooks via Clay GFM rendering

### Generated Test Workflow

Notebooks use `kind/test-last` to embed assertions. When rendered to GFM via Clay,
test files are generated in `test/napkinsketch_book/`. These are run by the standard test runner.

To regenerate tests after editing a notebook:
```clojure
(require '[dev :as dev] :reload)
(dev/make-gfm! "napkinsketch_book/<name>.clj")
```

## Project Organization

- **build.clj** — Build automation with tools.build
- **clay.edn** — Documentation generation configuration
- **deps.edn** — Dependencies and aliases (:dev, :test, :build)
- **notebooks/** — Clay-based documentation
  - `chapters.edn` — Chapter ordering
  - `index.clj` — Book index
  - `dev.clj` — Dev helper (make-book!, make-gfm!)
  - `napkinsketch_book/` — Chapter notebooks
- **src/** — Source code
- **test/** — Tests (hand-written + generated from notebooks)
- **dev-notes/** — Development notes

## Notebook Style Conventions

- **`kind/test-last` is invisible**: These forms only generate tests and never appear in rendered output. Do not add comments to them.
- **Show values explicitly**: Use a plain form to display a value. Follow with a separate `kind/test-last` if an assertion is needed.
- **Comments precede forms**: Use `;;` comment lines before a form, not `; comment` trailing.
- **Use `(deref x)` not `@x` at top level**: Clay ignores top-level forms starting with `@`.
- **No auto-resolved keywords in notebooks**: Use fully qualified keywords, not `::alias/keyword`.
- **Blank line before lists**: In `;;` comment blocks, always insert an empty `;;` line before bullet or numbered lists. Without it, Quarto does not recognize the list.
- **No hardcoded results**: Notebooks must be reproducible. Never hardcode computed values (e.g., "pass" strings in summary tables). Either compute results from live code, or verify displayed values with `kind/test-last`.
- **No section numbering**: Do not number section headings (e.g., `## 3. Foo`). Numbers are fragile — they break when sections are added, removed, or reordered. Use descriptive titles only. Cross-reference sections by name, not number.
- **Use `def ^:private` not `defn-` in notebooks**: `defn-` displays its var name (`#'napkinsketch_book...`) in rendered output. Use `(def ^:private name (fn [...] ...))` to suppress this noise.
- **Use public API in notebooks**: Notebooks should require the public API namespaces, not `impl` namespaces. This avoids coupling to internal structure and demonstrates the intended user-facing API.
- **`kind/doc` renders as `###`**: `(kind/doc #'some-ns/fn)` renders the var's docstring under a `###` heading. Place these under `##` sections so the heading hierarchy makes sense.

## Notebook Style Conventions (Tables)

- **Static tables**: Use Markdown tables in `;;` comment lines — rendered natively by Quarto
- **Dynamic tables** (with computed values): Use `kind/table`

## Numerical Computing Style

Prefer **vectorized operations** over scalar loops, lazy sequences, and Clojure vectors for numerical work.

### dtype-next lazy & noncaching buffers

`dfn` operations (e.g., `dfn/*`, `dfn/+`, `dfn/-`) return **lazy, noncaching buffers** — abstract random-access readers that recompute from scratch on each element access, without allocating memory. This is a core dtype-next concept:

```clojure
;; No memory allocated — returns a lazy reader over `signal`
(def negated (dfn/* signal -1.0))

;; Each (dtype/get-value negated i) computes (* (aget signal i) -1.0) on the fly

;; Chaining is free — just layers of lazy readers, no intermediate arrays
(def result (dfn/+ (dfn/* a b) (dfn/* c d)))
```

**When to realize into memory:**
- When the same buffer will be read **multiple times** (avoid redundant recomputation)
- When passing to Java interop that requires a concrete array
- At the **end of a pipeline**, not between steps
- Use `dtype/clone` (idiomatic) or `double-array` (when Java `double[]` is specifically needed) — both allocate an array, similar performance

```clojure
;; Pipeline: lazy readers compose, realize once at the end if needed
(-> intensities
    (dfn/* scale)
    (dfn/+ offset)
    dtype/clone)  ;; single allocation at the end

;; DO NOT realize between steps — wastes memory
;; Bad:  (dfn/+ (double-array (dfn/* a b)) c)
;; Good: (dfn/+ (dfn/* a b) c)
```

**When realization is NOT needed:**
- Single-pass consumers (loops that read each element once)
- Passing to other `dfn` operations (they accept any buffer)
- Reductions like `dfn/sum`, `dfn/reduce-min`, `dfn/reduce-max`

`tcc/*`, `tcc/+`, etc. are equivalent to `dfn` ops but wrap results in Column structures — slightly more overhead, prefer `dfn` in impl code.

### dtype-next functional namespace

Use `tech.v3.datatype.functional` (aliased as `dfn`) for element-wise array operations:

```clojure
(require '[tech.v3.datatype.functional :as dfn])

;; Element-wise arithmetic on buffers/columns — no intermediate seqs
(dfn/+ col-a col-b)
(dfn/* col-a col-b 0.5)  ; variadic
(dfn/- col-a col-b)

;; Shifting (fills edge with repeated boundary value)
(dfn/shift col -1)  ; [a b c d] -> [b c d d]
(dfn/shift col  1)  ; [a b c d] -> [a a b c]

;; Reduction to scalar
(dfn/sum col)
```

### Anti-patterns to avoid in numerical code

| Instead of | Use |
|:-----------|:----|
| `(map f seq1 seq2)` + `(reduce + ...)` | `(dfn/sum (dfn/* col1 col2))` |
| `(double-array (map #(max 0.0 %) arr))` | `(dfn/max arr 0.0)` |
| `(mapv #(dtype/get-value buf %) (range s e))` | `(dtype/sub-buffer buf s (- e s))` — zero-copy view |
| `(vec intensities)` then `(mapv f vals)` | `(dfn/abs (dfn/- col median-val))` |
| `(doseq [i (range n)] ...)` with `aset` | `(dotimes [i n] ...)` — no seq allocation |
| `(tc/select-rows ds #(>= (:col %) val))` | Column predicates: `(tc/select-rows ds (dfn/>= (:col ds) val))` |
| `(aget some-reader i)` | `(some-reader i)` — readers implement IFn |
| `(dotimes [i n] (aset result i (f i)))` | `(dtype/make-reader :float64 n (f idx))` — lazy, no allocation |
| `(tcc/+ column 0.0)` to force double type | `(dtype/elemwise-cast column :float64)` |
| `(doseq [i (range a b)] (aset arr i v))` | `(java.util.Arrays/fill arr (int a) (int b) (double v))` |
| `(vec (seq (dfn/* a b)))` | `(dfn/* a b)` — use the lazy reader directly, realize only at pipeline end |
| `(subvec (vec (seq buf)) s e)` | `(dtype/sub-buffer buf s (- e s))` — zero-copy view |
| `(apply min (seq buf))` | `(dfn/reduce-min buf)` — native reduction, no seq |
| `(alength arr)` | `(count arr)` — works on arrays and all dtype buffers |

**`vec` and `seq` on numeric buffers are a code smell.** If you find yourself wrapping a dtype result in `vec` or `seq`, it usually means the consuming code should be using dtype operations instead. Common causes: `subvec` (use `dtype/sub-buffer`), `apply min/max` (use `dfn/reduce-min/max`), `nth` (readers are callable: `(buf i)`). The only place `vec`/`seq` are acceptable is when building dataset columns for plots (where the data leaves the numeric pipeline).

### dtype-next make-reader

`dtype/make-reader` creates a custom lazy noncaching buffer defined by an index expression. Use it to replace imperative `dotimes`+`aset` patterns with functional style:

```clojure
;; Imperative (avoid):
(let [result (double-array n)]
  (dotimes [i n]
    (aset result i (some-computation i)))
  result)

;; Functional (prefer):
(dtype/make-reader :float64 n
  (some-computation idx))
```

The implicit binding is `idx`. The result is a lazy reader — compose with `dfn` ops, or realize with `dtype/clone` / `double-array` if needed.

Good for: per-element computations that are pure functions of the index (e.g., windowed statistics, element-wise transforms). Not appropriate for inherently sequential computations where each element depends on the previous.

### dtype-next readers as functions

All dtype-next readers (from `dfn` ops, `make-reader`, `sub-buffer`, etc.) implement `IFn` — call them directly as functions to access elements:

```clojure
(def buf (dfn/* signal 2.0))
(buf 0)    ;; => first element — works on any reader
(buf 42)   ;; => element at index 42

;; IMPORTANT: aget only works on Java arrays (double-array, int-array, etc.)
;; DO NOT use aget on readers — use (reader idx) or dtype/get-value instead
;; (aget some-reader i)       ;; IllegalArgumentException
;; (some-reader i)            ;; correct
;; (dtype/get-value reader i) ;; also correct, but more verbose
```

### dtype-next type casting

Use `dtype/elemwise-cast` for lazy type conversion (no allocation):

```clojure
(dtype/elemwise-cast column :float64)  ;; lazy reader, casts on access
(dtype/->double-array column)          ;; realized copy as Java double[]
```

### When scalar loops are OK

- Inherently sequential computations (each iteration depends on previous)
- Simple inner loops that are already using `double-array` + `aset`
- Building plot datasets in notebooks (runs once, not a hot path)

## Tableplot Plotly Pipelines

A Tableplot pipeline like `(-> data (plotly/layer-point) plotly/layer-line)` produces a **template** — a data structure with substitution keys that describe what you want declaratively. Clay/Kindly knows how to render this automatically in notebooks, so `plotly/plot` is not strictly needed for rendering.

However, `plotly/plot` **realizes** the template into the final Plotly.js spec — a plain map with `:data`, `:layout`, etc. This is essential for development:

- In the REPL, calling `plotly/plot` lets you `pprint` or inspect the concrete spec to debug layout, axes, data traces, etc.
- It is also required if you need to manipulate the realized spec directly (e.g., `(assoc-in [:layout :yaxis :scaleanchor] "x")`).

**Current guideline (development phase):** Always include `plotly/plot` at the end of plotting pipelines in notebooks. This way, any team member can evaluate the expression in the REPL and inspect the result. We will remove these calls before publishing.

```clojure
;; DO (during development):
(-> data
    (plotly/base {:=x :hour :=y :temperature})
    (plotly/layer-line)
    (plotly/layer-point {:=mark-size 10})
    plotly/plot)   ;; <-- include for REPL inspection

;; For direct spec manipulation, plotly/plot is required:
(-> data
    (plotly/base {:=x :x :=y :y})
    (plotly/layer-line)
    plotly/plot
    (assoc-in [:layout :yaxis :scaleanchor] "x"))
```

## Python Interop (libpython-clj)

If your project uses Python libraries via libpython-clj:

### Setup

```clojure
;; In ns :require — enables zero-copy numpy ↔ dtype-next bridging
[libpython-clj2.python.np-array]
```

With this require, numpy ndarrays become dtype-next native buffers. You can pass them directly to `double-array`, `dfn/*`, `dfn/sum`, etc. — no `py/->jvm` needed for numeric arrays.

### Negative-stride arrays

Some scipy functions (notably `sosfiltfilt`) return reversed-view ndarrays with negative strides. The np-array bridge interprets the negative stride as a negative element count, breaking all dtype-next operations.

**Fix:** wrap with `np/ascontiguousarray` before using on the JVM side:

```clojure
(-> (some-scipy-fn input)
    np/ascontiguousarray   ;; makes it contiguous (one memcpy on Python side)
    double-array)          ;; now works correctly
```

### What to use where

| Data type | Conversion | Notes |
|:----------|:-----------|:------|
| numpy ndarray (contiguous) | Use directly or `double-array` | Zero-copy via np-array bridge |
| numpy ndarray (negative stride) | `np/ascontiguousarray` first | Then use as above |
| Python dict / DataFrame | `py/->jvm` | Works fine for non-array types |
| Clojure → Python | `py/->python` for collections | numpy arrays pass through directly |

**Avoid** `py/->jvm` on numpy arrays — it hits a native buffer bug with negative-stride arrays and is unnecessary when np-array bridging is loaded.

## R Interop (clojisr)

If your project uses R via clojisr:

- `clj->r` of `double-array` passes as character to R — use Clojure `vec` with backtick template `~` instead
- `tc/dataset?` is the proper way to check for tablecloth datasets

## Key Libraries Reference

| Library | Alias | Purpose |
|:--------|:------|:--------|
| `tablecloth.api` | `tc` | Dataset manipulation |
| `tech.v3.datatype` | `dtype` | Low-level buffer operations |
| `tech.v3.datatype.functional` | `dfn` | Element-wise array arithmetic |
| `tablecloth.column.api` | `tcc` | Column-level operations |
| `scicloj.kindly.v4.kind` | `kind` | Visualization annotations |
| `scicloj.tableplot.v1.plotly` | `plotly` | Plotly chart pipelines |
| `fastmath.stats` | `fstats` | Statistical functions |

## Useful Patterns

- `fstats/median` with default `:legacy` strategy matches R's `median()` — prefer over `dfn/median` or `tcc/median` (which have a "high median" bug on even-length data)
- `fstats/stddev` uses n-1 denominator (matches R's `sd()`)
- `fstats/mad` returns unscaled MAD (no 1.4826 factor) — multiply manually if needed
- `argops/argfilter identity bool-mask` — returns indices of trues
- `dtype/indexed-buffer indexes item` — reorders item by index array (indexes FIRST)
- `dtype/sub-buffer buf start len` — zero-copy view into a buffer
- `dtype/copy!` writes lazy reader into existing array in-place
