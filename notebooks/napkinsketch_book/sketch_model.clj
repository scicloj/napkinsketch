;; # The Sketch Model
;;
;; Napkinsketch is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms — threading, merge,
;; plain maps — rather than a custom DSL.
;;
;; This chapter introduces the mental model. Five ideas, and
;; everything else in the book is details.

(ns napkinsketch-book.sketch-model
  (:require
   [napkinsketch-book.datasets :as data]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Idea 1: The what/how split
;;
;; The API separates **what** to plot from **how** to draw it:
;;
;; | Verb | What it does | Example |
;; |:-----|:-------------|:--------|
;; | `sk/view` | Declare **what** to plot — columns and aesthetics | `(sk/view data :x :y)` |
;; | `sk/lay-*` | Choose **how** to draw it — the chart type | `sk/lay-point`, `sk/lay-histogram` |
;;
;; The result of either verb is a **sketch** — a lightweight,
;; composable description of a plot.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `lay-point` takes data and columns, and returns a sketch.
;; The notebook auto-renders it as a plot.

;; ## Idea 2: Sketches are data
;;
;; A sketch is a plain Clojure record. To see its structure
;; instead of its rendered plot, wrap with `kind/pprint`:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    kind/pprint)

(kind/test-last [(fn [v] (and (:data v) (vector? (:entries v)) (vector? (:methods v))))])

;; Five fields:
;;
;; - `:data` — the dataset
;; - `:shared` — aesthetics that apply to all entries
;; - `:entries` — what to plot (column pairs)
;; - `:methods` — how to plot (mark + stat)
;; - `:opts` — plot-level options (title, width, theme)
;;
;; Because sketches are data, you can inspect them, store them,
;; and compose them with standard Clojure tools.

;; ## Idea 3: Separating what from how enables composition
;;
;; When you call `sk/lay-point data :x :y`, it creates both an
;; entry (what) and a method (how) in one step. But you can
;; separate the two with `sk/view`:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; `sk/view` declares what to plot — columns and color grouping.
;; Then `sk/lay-point` and `sk/lay-lm` each add a drawing method.
;; Both methods share the same columns and aesthetics.
;;
;; **The key insight: `view` describes what, `lay-*` describes how.**
;; Separating them lets you add multiple layers that share the
;; same data mapping — scatter points and regression lines here,
;; each species getting its own color and fitted line.

;; ## Idea 4: Inference fills the gaps
;;
;; When you omit a choice, napkinsketch infers it from the data.
;; Two numerical columns with no `lay-*` → scatter:

(-> data/iris
    (sk/view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; One numerical column → histogram:

(-> data/iris
    (sk/view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The principle: **`resolved` = `(or your-choice (inferred-from-data))`**.
;;
;; This works for marks, stats, color types, and grouping.
;; See [Inference Rules](./napkinsketch_book.inference_rules.html)
;; for the full set.

;; ## Idea 5: Everything returns a sketch
;;
;; Every function in the API returns a sketch. This means they all
;; compose through Clojure's threading macro `->`:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/facet :species)
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Iris by Species"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s)))))])

;; `view`, `lay-*`, `facet`, `options`, `scale`, `coord`,
;; `annotate` — all take a sketch and return a sketch. Order
;; is flexible. The pipeline reads like a sentence: "take this
;; data, view these columns with color, facet by species, add
;; points and regression lines, set a title."

;; ## Summary
;;
;; | Idea | In code |
;; |:-----|:--------|
;; | The what/how split | `sk/view` (what) + `sk/lay-*` (how) → sketch |
;; | Sketches are data | Plain records — inspect with `kind/pprint` |
;; | What/how separation | Share columns across layers |
;; | Inference fills gaps | Omit choices, library infers from data |
;; | Everything composes | All functions return sketches, thread with `->` |
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) — data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) — how napkinsketch chooses defaults
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — chart type examples to explore
