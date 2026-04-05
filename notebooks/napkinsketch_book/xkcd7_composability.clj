;; # Composable Plotting with Blueprints
;;
;; Napkinsketch is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms — threading, merge,
;; plain maps — rather than a custom DSL.
;;
;; The Blueprint API has two verbs and one noun:
;;
;; | Phase | Verb | What it does | Example |
;; |:------|:-----|:-------------|:--------|
;; | **What** to plot | `sk/xkcd7-view` | Declare entries (columns and aesthetics) | `(sk/xkcd7-view data :x :y)` |
;; | **How** to plot | `sk/xkcd7-lay-*` | Add a drawing method | `sk/xkcd7-lay-point`, `sk/xkcd7-lay-histogram` |
;;
;; The noun is a **Blueprint** — the composable result of both verbs.
;; Blueprints auto-render in notebooks, compose through threading (`->`),
;; and are plain inspectable data.

(ns napkinsketch-book.xkcd7-composability
  (:require
   ;; Shared datasets
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## One Layer
;;
;; The simplest Blueprint: data, columns, and a chart type — all in
;; one call.

(def scatter
  (-> data/iris
      (sk/xkcd7-lay-point :sepal_length :sepal_width)))

;; `sk/xkcd7-lay-point` returns a **Blueprint** — a lightweight record
;; that auto-renders in notebooks.
;; When displayed, the notebook renders it:

scatter

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Inspecting a Blueprint
;;
;; A Blueprint is a Clojure record with five fields:
;;
;; - `:data` — the dataset
;; - `:shared` — options that apply to all entries (from `xkcd7-view` opts)
;; - `:entries` — what to plot (column declarations, one per panel)
;; - `:methods` — global drawing methods (from bare `xkcd7-lay-*`)
;; - `:opts` — plot-level options (title, width, height)
;;
;; You can inspect these directly with `keys` and standard map access:

(keys scatter)

(kind/test-last [(fn [v] (every? (set v) [:data :shared :entries :methods :opts]))])

;; The entries tell us which columns are mapped.
;; Each entry is a map with `:x`, `:y`, and its own `:methods`:

(:entries scatter)

(kind/test-last [(fn [v] (and (= 1 (count v))
                              (= :sepal_length (:x (first v)))
                              (= :sepal_width (:y (first v)))))])

;; The entry has its own `:methods` because we used `xkcd7-lay-point`
;; with columns — that makes the method entry-specific:

(:methods (first (:entries scatter)))

(kind/test-last [(fn [v] (and (= 1 (count v))
                              (= :point (:mark (first v)))))])

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
;; Use `sk/xkcd7-view` to declare **what** to plot — which columns and
;; aesthetics. Then add **how** to draw with `sk/xkcd7-lay-*`.
;; Multiple bare `lay-*` calls (without columns) become global methods
;; that apply to every entry.

(def scatter-with-regression
  (-> data/iris
      (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
      sk/xkcd7-lay-point
      sk/xkcd7-lay-lm))

;; The Blueprint now has one entry (the column pair) and two global
;; methods (point and linear model):

(:entries scatter-with-regression)

(kind/test-last [(fn [v] (= 1 (count v)))])

(:methods scatter-with-regression)

(kind/test-last [(fn [v] (= 2 (count v)))])

;; Both layers render together — each species gets its own
;; fitted line:

scatter-with-regression

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Three Scopes
;;
;; A Blueprint has three levels where options can live, from broadest
;; to narrowest:
;;
;; - **Shared** — from `xkcd7-view` opts, applies to all entries
;; - **Entry-specific** — from `xkcd7-lay-*` with columns, on one entry
;; - **Global methods** — from bare `xkcd7-lay-*`, applies to all entries
;;
;; At resolution time, each view is:
;; `merge(shared, entry, method)`.
;;
;; Here is an example showing shared color with two entry-specific
;; layers on the same columns:

(def layered
  (-> data/iris
      (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
      (sk/xkcd7-lay-point :sepal_length :sepal_width)
      (sk/xkcd7-lay-lm :sepal_length :sepal_width)))

;; Both methods land on the same entry because they match the same
;; `:x` and `:y`. The entry now has two methods:

(count (:methods (first (:entries layered))))

(kind/test-last [(fn [v] (= 2 v))])

;; The global `:methods` list is empty — everything is entry-specific:

(:methods layered)

(kind/test-last [(fn [v] (= 0 (count v)))])

;; ## Inference
;;
;; When you omit something, napkinsketch infers it from the data.
;; The principle is simple:
;;
;; ```
;; resolved-value = (or your-explicit-choice (inferred-from-data))
;; ```
;;
;; `sk/xkcd7-view` without a `sk/xkcd7-lay-*` infers the drawing
;; method from the column types. Two numerical columns produce a scatter:

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> data/iris
    (sk/xkcd7-view :sepal_length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Multiple Views
;;
;; `sk/xkcd7-view` accepts a vector of column pairs. Each pair becomes
;; a separate entry — and therefore a separate panel:

(def two-panels
  (-> data/iris
      (sk/xkcd7-view [[:sepal_length :sepal_width]
                      [:petal_length :petal_width]])))

;; The Blueprint wraps two entries — one per column pair:

(:entries two-panels)

(kind/test-last [(fn [v] (= 2 (count v)))])

two-panels

(kind/test-last [(fn [v] (= 2 (:panels (sk/svg-summary v))))])

;; A vector of keywords creates univariate entries — one histogram
;; per column:

(-> data/iris
    (sk/xkcd7-view [:sepal_length :sepal_width :petal_length]))

(kind/test-last [(fn [v] (= 3 (:panels (sk/svg-summary v))))])

;; You can also create multiple histograms directly with
;; `xkcd7-lay-histogram` and a vector of columns:

(-> data/iris
    (sk/xkcd7-lay-histogram [:sepal_length :sepal_width :petal_length]))

(kind/test-last [(fn [v] (= 3 (:panels (sk/svg-summary v))))])

;; ## The SPLOM
;;
;; `sk/cross` generates all combinations of columns — the same
;; idea as Wilkinson's cross operator. Passing the result to
;; `sk/xkcd7-view` produces a
;; [scatter plot matrix](https://en.wikipedia.org/wiki/Scatter_plot#Scatter_plot_matrices):

(def cols [:sepal_length :sepal_width :petal_length])

(-> data/iris
    (sk/xkcd7-view (sk/cross cols cols) {:color :species}))

(kind/test-last [(fn [v] (= 9 (:panels (sk/svg-summary v))))])

;; Nine panels — one per column pair. On the diagonal (where
;; x = y), inference produces histograms instead of scatters.
;; The color grouping applies to all entries through the shared scope.

;; ## Options and Faceting
;;
;; `sk/xkcd7-options` adds titles and configuration.
;; `sk/xkcd7-facet` splits the data into panels by a column.
;; Everything threads together — each function takes a Blueprint
;; and returns a Blueprint:

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

;; ## Penguins Example
;;
;; The same patterns work with any dataset. Here are penguins
;; with bill dimensions, colored by species:

(-> data/penguins
    (sk/xkcd7-view :bill_length_mm :bill_depth_mm {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm
    (sk/xkcd7-options {:title "Palmer Penguins"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (some #{"Palmer Penguins"} (:texts s))))])

;; ## Summary
;;
;; | Function | Role | Returns |
;; |:---------|:-----|:--------|
;; | `sk/xkcd7-view` | Declare entries (what to show) | Blueprint |
;; | `sk/xkcd7-lay-*` | Add a method (how to show it) | Blueprint |
;; | `sk/xkcd7-options` | Set title, labels, configuration | Blueprint |
;; | `sk/xkcd7-facet` | Split into panels | Blueprint |
;; | `sk/xkcd7-coord` | Set coordinate system | Blueprint |
;; | `sk/xkcd7-scale` | Set axis scale type | Blueprint |
;;
;; Every function returns a Blueprint. Blueprints compose through `->`.
;; They are plain data — records you can inspect with `keys`, `:entries`,
;; `:methods`, and `:shared`.
;;
;; A Blueprint has three scopes for options:
;;
;; - **Shared** — from `xkcd7-view` opts, inherited by all entries
;; - **Entry-specific** — from `xkcd7-lay-*` with columns
;; - **Global methods** — from bare `xkcd7-lay-*`
;;
;; Resolution merges them: `merge(shared, entry, method)`.
