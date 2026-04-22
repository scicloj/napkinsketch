;; # The Sketch Model
;;
;; Napkinsketch is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms -- threading, merge,
;; plain maps -- rather than a custom DSL.
;;
;; This chapter introduces the mental model in five ideas.

(ns napkinsketch-book.sketch-model
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Idea 1: The what/how split
;;
;; The API separates **what** to plot from **how** to draw it:
;;
;; | Verb | What it does | Example |
;; |:-----|:-------------|:--------|
;; | `sk/view` | Declare **what** to plot -- columns and aesthetics | `(sk/view data :x :y)` |
;; | `sk/lay-*` | Choose **how** to draw it -- the chart type | `sk/lay-point`, `sk/lay-histogram` |
;;
;; The result of either verb is a **sketch** -- a lightweight,
;; composable description of a plot.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `lay-point` takes data and columns, and returns a sketch.
;; The notebook auto-renders it as a plot.

;; ## Idea 2: Sketches are data
;;
;; A sketch is a plain Clojure record. To see its structure
;; instead of its rendered plot, wrap with `kind/pprint`:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    kind/pprint)

(kind/test-last [(fn [v] (and (:data v)
                              (vector? (:views v))
                              (empty? (:layers v))
                              (seq (:layers (first (:views v))))))])

;; Five fields:
;;
;; - `:data` -- the dataset
;; - `:mapping` -- aesthetic mappings (column-to-visual, like
;;   `:color :species`) that apply to all views
;; - `:views` -- what to plot (column pairs)
;; - `:layers` -- how to draw it (layers: method + optional mappings)
;; - `:opts` -- options for the plot as a whole (title, width, theme)
;;
;; [Core Concepts](./napkinsketch_book.core_concepts.html) explains
;; each field in detail.
;;
;; Because sketches are data, you can inspect them, store them,
;; and compose them with standard Clojure tools.

;; ## Idea 3: Separating what from how enables composition
;;
;; When you call `sk/lay-point data :x :y`, it creates both a
;; view (what) and a layer (how) in one step. But you can
;; separate the two with `sk/view`:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; `sk/view` declares what to plot -- columns and color grouping.
;; Then `sk/lay-point` and `sk/lay-lm` each add a layer.
;; Both layers share the same columns and aesthetics.
;;
;; **The key insight: `view` describes what, `lay-*` describes how.**
;; Separating them lets you add multiple layers that share the
;; same data mapping -- scatter points and regression lines here,
;; each species getting its own color and fitted line.

;; ## Idea 4: Inference fills the gaps
;;
;; When you omit a choice, Napkinsketch infers it from the data.
;; Two numerical columns with no `lay-*` are inferred as a scatter:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; One numerical column is inferred as a histogram:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The principle: **`resolved` = `(or your-choice (inferred-from-data))`**.
;;
;; This works for marks (the shape drawn, like points or bars),
;; stats (the computation before drawing, like binning), color
;; types, and grouping.
;; See [Inference Rules](./napkinsketch_book.inference_rules.html)
;; for the full set.

;; ## Idea 5: Everything returns a sketch
;;
;; Every function in the API returns a sketch. This means they all
;; compose through Clojure's threading macro `->`:

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/facet :species)
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Iris by Species"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s)))))])

;; `view`, `lay-*`, `facet`, `options`, `scale`, `coord` -- all
;; take a sketch and return a sketch. Order is flexible. The
;; pipeline reads like a sentence: "take this data, view these
;; columns with color, facet by species, add points and regression
;; lines, set a title."

;; ## Summary
;;
;; | Idea | In code |
;; |:-----|:--------|
;; | The what/how split | `sk/view` says what; `sk/lay-*` says how |
;; | Sketches are data | Plain records -- inspect with `kind/pprint` |
;; | Composition | Share columns across layers |
;; | Inference fills gaps | Omit choices, library infers from data |
;; | Everything composes | All functions return sketches, thread with `->` |
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) -- data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) -- how napkinsketch chooses defaults
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) -- chart type examples to explore
