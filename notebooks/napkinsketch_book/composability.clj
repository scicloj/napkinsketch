;; # Composable Plotting with xkcd7-sketches
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
;; | **What** to plot | `sk/xkcd7-view` | Declare what columns and aesthetics to use | `(sk/xkcd7-view data :x :y)` |
;; | **How** to plot | `sk/xkcd7-lay-*` | Choose a drawing method | `sk/xkcd7-lay-point`, `sk/xkcd7-lay-histogram` |
;;
;; The noun is a **xkcd7-sketch** — the composable result of both verbs.
;; xkcd7-sketches auto-render in notebooks, compose through threading (`->`),
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
;; The simplest plot: data, columns, and a chart type.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `sk/xkcd7-lay-point` creates a xkcd7-sketch — a lightweight description
;; of the plot. Nothing is computed until the notebook displays it.

;; ## Adding Color
;;
;; Pass `:color` in the options map to group the data. Each group gets
;; its own color and a legend appears automatically.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; ## Multiple Layers
;;
;; This is where composability begins. Use `sk/xkcd7-view` to declare
;; **what** to plot — which columns and aesthetics. Then add **how** to
;; draw with one or more `sk/xkcd7-lay-*` calls:

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both layers share the same columns and color. The scatter points
;; and regression lines render together — each species gets its own
;; fitted line.
;;
;; The key insight: **`view` describes what, `lay-*` describes how.**
;; Separating them lets you reuse the same "what" with different "how"s.

;; ## Inference
;;
;; When you omit a `lay-*`, napkinsketch infers the drawing method
;; from the column types:

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Two numerical columns → scatter. One numerical column → histogram:

(-> data/iris
    (sk/xkcd7-view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The principle: `resolved = (or your-choice (inferred-from-data))`.
;; See [**Inference Rules**](./napkinsketch_book.inference_rules.html) for the full set.

;; ## Comparing Multiple Variables
;;
;; Pass a vector of column names to create one panel per variable.
;; Keywords create univariate panels (one column each):

(sk/xkcd7-lay-histogram data/iris [:sepal_length :sepal_width :petal_length])

(kind/test-last [(fn [v] (= 3 (:panels (sk/svg-summary v))))])

;; Pairs create bivariate panels:

(-> data/iris
    (sk/xkcd7-view [[:sepal_length :sepal_width]
                    [:petal_length :petal_width]]))

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; `sk/cross` generates all combinations — a
;; [scatter plot matrix](https://en.wikipedia.org/wiki/Scatter_plot#Scatter_plot_matrices):

(def cols [:sepal_length :sepal_width :petal_length])

(-> data/iris
    (sk/xkcd7-view (sk/cross cols cols) {:color :species}))

(kind/test-last [(fn [v] (= 9 (:panels (sk/svg-summary v))))])

;; Nine panels. On the diagonal (where x = y), inference produces
;; histograms instead of scatters.

;; ## Options and Faceting
;;
;; `sk/xkcd7-options` adds titles and configuration.
;; `sk/xkcd7-facet` splits the data into panels by a column.
;; Everything threads together:

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm
    (sk/xkcd7-options {:title "Iris by Species"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s)))))])

;; ## Annotations

;; Reference lines and shaded bands layer on top of any plot:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-annotate (sk/rule-h 3.0)
                       (sk/band-v 5.5 7.0 {:alpha 0.15})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Inside a xkcd7-sketch
;;
;; A xkcd7-sketch is a Clojure record with five fields. You can inspect
;; it with standard map operations:

(def my-xkcd7-sk
  (-> data/iris
      (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
      sk/xkcd7-lay-point
      sk/xkcd7-lay-lm))

;; `:shared` holds the options from `xkcd7-view` — these apply to
;; all entries:

(:shared my-xkcd7-sk)

(kind/test-last [(fn [v] (= :species (:color v)))])

;; `:entries` holds the column declarations — one per panel:

(:entries my-xkcd7-sk)

(kind/test-last [(fn [v] (and (= 1 (count v))
                              (= :sepal_length (:x (first v)))))])

;; `:methods` holds the global drawing methods — these apply to
;; all entries:

(mapv :mark (:methods my-xkcd7-sk))

(kind/test-last [(fn [v] (= [:point :line] v))])

;; When `lay-*` is called **with columns**, the method attaches to
;; a specific entry instead of going global. When called **without
;; columns** (bare), it goes into the global `:methods` list.
;; This is the distinction between entry-specific and global methods.

;; ## Summary
;;
;; | Function | Role | Returns |
;; |:---------|:-----|:--------|
;; | `sk/xkcd7-view` | Declare entries (what to show) | xkcd7-sketch |
;; | `sk/xkcd7-lay-*` | Add a method (how to show it) | xkcd7-sketch |
;; | `sk/xkcd7-options` | Set title, labels, configuration | xkcd7-sketch |
;; | `sk/xkcd7-facet` | Split into panels | xkcd7-sketch |
;; | `sk/xkcd7-coord` | Set coordinate system | xkcd7-sketch |
;; | `sk/xkcd7-scale` | Set axis scale type | xkcd7-sketch |
;; | `sk/xkcd7-annotate` | Add reference lines and bands | xkcd7-sketch |
;;
;; Every function returns a xkcd7-sketch. xkcd7-sketches compose through `->`.
;;
;; The [Core Concepts](./napkinsketch_book.core_concepts.html) chapter
;; covers each concept in detail.
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) — data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) — how napkinsketch chooses defaults
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — chart type examples to explore
