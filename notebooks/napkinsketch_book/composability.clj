;; # Composable Plotting
;;
;; Napkinsketch is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms — threading, merge,
;; plain maps — rather than a custom DSL.
;;
;; The API has two verbs and one noun:
;;
;; | Phase | Verb | What it does | Example |
;; |:------|:-----|:-------------|:--------|
;; | **What** to plot | `sk/view` | Describe your views of the data | `(sk/view data :x :y)` |
;; | **How** to plot | `sk/lay-*` | Choose a drawing method | `sk/lay-point`, `sk/lay-histogram` |
;;
;; The noun is a **sketch** — the composable result of both verbs.
;; Sketches auto-render in notebooks, compose through threading (`->`),
;; and are plain inspectable data.

(ns napkinsketch-book.composability
  (:require
   ;; Shared datasets
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## One Layer
;;
;; The simplest sketch: data, columns, and a chart type.

(def scatter
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width)))

;; `sk/lay-point` returns a **sketch** — a lightweight wrapper that
;; auto-renders in notebooks. But it is also plain Clojure data.
;; Inside, a sketch wraps one or more **views** (maps describing
;; what to plot) together with options:

(sk/sketch? scatter)

(kind/test-last [(fn [v] (true? v))])

(kind/pprint (sk/views-of scatter))

(kind/test-last [(fn [v] (and (vector? v) (= 1 (count v))))])

;; Each view is a map with `:data`, `:x`, `:y`, and `:mark`.
;; No rendering has happened yet — the sketch is just a description.
;; When displayed, the notebook renders it:

scatter

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Adding Color
;;
;; Pass `:color` to group the data. Each group gets its own color
;; and a legend appears automatically.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; ## Multiple Layers
;;
;; `sk/view` describes **what** to plot — which columns and
;; aesthetics to use. Then `sk/lay-*` functions add **how** to
;; draw them. Multiple layers share the same views.

(def scatter-with-regression
  (-> data/iris
      (sk/view :sepal_length :sepal_width {:color :species})
      sk/lay-point
      sk/lay-lm))

;; The sketch now has two methods merged into its views — scatter
;; points and regression lines share the same columns and color:

(kind/pprint (sk/views-of scatter-with-regression))

(kind/test-last [(fn [v] (= 2 (count v)))])

;; Both layers render together — each species gets its own
;; fitted line:

scatter-with-regression

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Inference
;;
;; When you omit something, napkinsketch infers it from the data.
;; The principle is simple:
;;
;; ```
;; resolved-value = (or your-explicit-choice (inferred-from-data))
;; ```
;;
;; `sk/view` without a `sk/lay-*` infers the drawing method from
;; the column types. Two numerical columns produce a scatter:

(-> data/iris
    (sk/view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> data/iris
    (sk/view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Multiple Views
;;
;; `sk/view` accepts multiple column pairs. Each pair becomes
;; a separate panel:

(def two-panels
  (-> data/iris
      (sk/view [[:sepal_length :sepal_width]
                [:petal_length :petal_width]])))

;; The sketch wraps two views — one per column pair:

(kind/pprint (sk/views-of two-panels))

(kind/test-last [(fn [v] (= 2 (count v)))])

two-panels

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; ## The SPLOM
;;
;; `sk/cross` generates all combinations of columns — the same
;; idea as Wilkinson's cross operator (×). Passing the result to
;; `sk/view` produces a
;; [scatter plot matrix](https://en.wikipedia.org/wiki/Scatter_plot#Scatter_plot_matrices):

(def cols [:sepal_length :sepal_width :petal_length])

(-> data/iris
    (sk/view (sk/cross cols cols) {:color :species}))

(kind/test-last [(fn [v] (= 9 (:panels (sk/svg-summary v))))])

;; Nine panels — one per column pair. On the diagonal (where
;; x = y), inference produces histograms instead of scatters.
;; The color grouping applies to all views.

;; ## Options and Faceting
;;
;; `sk/options` adds titles and configuration.
;; `sk/facet` splits the data into panels by a column.
;; Everything threads together — each function takes a sketch
;; and returns a sketch:

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

;; ## Summary
;;
;; | Function | Role | Returns |
;; |:---------|:-----|:--------|
;; | `sk/view` | Describe views (what to show) | Sketch |
;; | `sk/lay-*` | Add a method (how to show it) | Sketch |
;; | `sk/options` | Set title, labels, config | Sketch |
;; | `sk/facet` | Split into panels | Sketch |
;; | `sk/coord` | Set coordinate system | Sketch |
;; | `sk/scale` | Set axis scale type | Sketch |
;;
;; Every function returns a sketch. Sketches compose through `->`.
;; They are plain data — vectors of maps you can inspect with
;; `sk/views-of` and transform with ordinary Clojure functions.
;;
;; The [Core Concepts](./napkinsketch_book.core_concepts.html) chapter
;; covers each concept in detail.

;; ## What's Next
;;
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) — data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) — how napkinsketch chooses defaults
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — chart type examples to explore
