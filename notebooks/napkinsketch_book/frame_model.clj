;; # Frame Model
;;
;; Napkinsketch is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms -- threading, merge,
;; plain maps -- rather than a custom DSL.
;;
;; This chapter introduces the mental model in five ideas.

(ns napkinsketch-book.frame-model
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Idea 1: A frame describes a plot
;;
;; In Napkinsketch, every plot you compose is a **frame** -- a plain
;; Clojure value that describes what to show and how to show it.
;; The simplest frame names some data, picks columns, and chooses a
;; chart type:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; `lay-point` takes data and columns, and returns a frame.
;; The notebook auto-renders it as a plot.

;; ## Idea 2: Frames are data
;;
;; A frame is a plain Clojure value you can inspect. Wrap it with
;; `kind/pprint` to see its structure instead of its rendered plot:

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    kind/pprint)

(kind/test-last [(fn [v] (and (seq (:data v))
                              (= :sepal-length (:x (:mapping v)))))])

;; A frame carries the dataset, a column mapping, and any layers
;; attached to it (empty here, until we add some). Options set by
;; `sk/options`, `sk/scale`, and `sk/coord` live under an `:opts`
;; key. [Core Concepts](./napkinsketch_book.core_concepts.html)
;; walks through each field in detail.
;;
;; Because frames are plain data, you can store them, transform them,
;; and compose them with ordinary Clojure tools.

;; ## Idea 3: What to show, how to show it
;;
;; The API separates **what** to plot from **how** to show it:
;;
;; | Verb | What it does | Example |
;; |:-----|:-------------|:--------|
;; | `sk/frame` | Declare what to show -- columns and aesthetics | `(sk/frame data {:x :a :y :b})` |
;; | `sk/lay-*` | Choose how to show it -- the chart type | `sk/lay-point`, `sk/lay-histogram` |
;;
;; When you call `sk/lay-point data :x :y`, it does both in one step:
;; a column mapping and a chart-type layer. But you can declare the
;; mapping once and attach several layers to it:

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; `sk/frame` declares what to show -- columns and color grouping.
;; Then `sk/lay-point` and `(sk/lay-smooth {:stat :linear-model})` each add
;; a layer. Both layers share the same columns and aesthetics, so each
;; species gets its own color and its own fitted line.
;;
;; **The key insight: `sk/frame` describes what, `sk/lay-*` describes how.**
;; Separating the two lets you add multiple layers that share the same
;; data mapping -- scatter points and regression lines here.

;; ## Idea 4: Inference fills the gaps
;;
;; When you omit a choice, Napkinsketch infers it from the data.
;; Two numerical columns with no `lay-*` are shown as a scatter:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; One numerical column becomes a histogram:

(-> (rdatasets/datasets-iris)
    (sk/frame :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The principle: **`resolved` = `(or your-choice (inferred-from-data))`**.
;;
;; This works for marks (the shape shown, like points or bars), stats
;; (the computation before rendering, like binning), color types, and
;; grouping. See [Inference Rules](./napkinsketch_book.inference_rules.html)
;; for the full set.

;; ## Idea 5: Frames compose
;;
;; Every function in the API takes a frame and returns a frame. This
;; means they all thread naturally with `->`:

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    (sk/facet :species)
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Iris by Species"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (some #{"Iris by Species"} (:texts s)))))])

;; `frame`, `lay-*`, `facet`, `options`, `scale`, `coord` -- all take
;; a frame and return a frame. The pipeline reads like a sentence:
;; "take this data, frame these columns with color, facet by species,
;; add points and regression lines, set a title."
;;
;; Composition shows up in two ways. Within a single frame, layers
;; stack -- scatter points plus regression lines, sharing the same
;; axes. Across several frames, operations like `sk/facet` and
;; `sk/arrange` tile whole plots into a single rendered image. The
;; details of multi-frame composition are covered in later chapters.

;; ## Summary
;;
;; | Idea | In code |
;; |:-----|:--------|
;; | A frame describes a plot | `sk/frame`, `sk/lay-*` return frames |
;; | Frames are data | Plain Clojure values -- inspect with `kind/pprint` |
;; | What vs how | `sk/frame` declares what; `sk/lay-*` declares how |
;; | Inference fills gaps | Omit choices, the library infers from data |
;; | Frames compose | All functions return frames, thread with `->` |
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./napkinsketch_book.core_concepts.html) -- data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./napkinsketch_book.inference_rules.html) -- how Napkinsketch chooses defaults
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) -- chart type examples to explore
