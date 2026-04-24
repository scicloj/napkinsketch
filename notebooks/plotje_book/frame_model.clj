;; # Frame Model
;;
;; Plotje is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms -- threading, merge,
;; plain maps -- rather than a custom DSL.
;;
;; This chapter introduces the mental model in five ideas. Each idea
;; shows a rendered plot alongside the printed frame value, so the
;; curious reader can see both what the library did and how the
;; underlying data looks.

(ns plotje-book.frame-model
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## Idea 1: A frame describes a plot
;;
;; In Plotje, every plot you compose is a **frame** -- a plain
;; Clojure value that describes what to show. The simplest frame
;; names some data and picks columns. With no explicit chart type,
;; the library infers one from the column types:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Two numerical columns produced a scatter. And because a frame is
;; plain data, you can inspect it with `kind/pprint`:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width)
    kind/pprint)

(kind/test-last [(fn [v] (and (seq (:data v))
                              (= :sepal-length (:x (:mapping v)))))])

;; A frame is a plain Clojure map. The dataset lives under `:data`,
;; the column mapping under `:mapping`, chart-type layers (empty
;; here -- we never attached one) under `:layers`, and plot-level
;; options under `:opts`.

;; ## Idea 2: Frames carry mappings
;;
;; A **mapping** connects columns to visual properties. We added
;; `:x` and `:y` in Idea 1; the frame also accepts appearance
;; aesthetics like `:color`, `:size`, `:alpha`, and `:shape`:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; The printed frame shows the full mapping:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    kind/pprint)

(kind/test-last [(fn [v] (= :species (:color (:mapping v))))])

;; All three pairs -- `:x -> :sepal-length`, `:y -> :sepal-width`,
;; `:color -> :species` -- end up in the one `:mapping` map. Future
;; layers on this frame will inherit the whole set.

;; ## Idea 3: What to show, how to show it
;;
;; The API separates **what** to plot from **how** to show it:
;; a frame's `:mapping` holds the "what" (columns to aesthetics),
;; and its `:layers` holds the "how" (one entry per chart-type
;; layer). Declaring the mapping once lets several layers share it
;; -- scatter points and a regression line per species here:

(def multi-layer
  (pj/prepare-frame
   {:data (rdatasets/datasets-iris)
    :mapping {:x :sepal-length :y :sepal-width :color :species}
    :layers [{:layer-type :point}
             {:layer-type :smooth :mapping {:stat :linear-model}}]}))

multi-layer

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Printed, the mapping and the layers are visibly separate:

(kind/pprint multi-layer)

(kind/test-last [(fn [v] (and (= 2 (count (:layers v)))
                              (= :species (:color (:mapping v)))))])

;; `:mapping` answers the "what" question -- which columns flow to
;; which aesthetic -- and `:layers` answers the "how" question, with
;; each entry naming a chart type and optional layer-specific
;; options (`:stat :linear-model` on the smooth layer here).
;;
;; The threaded form builds the same frame step by step: `pj/frame`
;; sets the mapping, then each `pj/lay-*` appends a layer.

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Printed, the threaded form produces the same shape as the
;; explicit-map form shown earlier -- `:mapping` at the top, `:layers`
;; alongside it. `pj/frame` and `pj/lay-*` build the same frame value
;; step by step.

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count (:layers v)))
                              (= :species (get-in v [:mapping :color]))))])

;; ## Idea 4: Inference fills the gaps
;;
;; When you omit a choice, Plotje infers it from the data.
;; One numerical column becomes a histogram:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; The printed frame shows an empty `:layers` -- the histogram
;; layer is chosen by inference at render time, not stored on the
;; frame:

(-> (rdatasets/datasets-iris)
    (pj/frame :sepal-length)
    kind/pprint)

(kind/test-last [(fn [v] (empty? (:layers v)))])

;; The principle: **`resolved` = `(or your-choice (inferred-from-data))`**.
;;
;; This works for marks (the shape shown, like points or bars), stats
;; (the computation before rendering, like binning), color types, and
;; grouping. See [Inference Rules](./plotje_book.inference_rules.html)
;; for the full set.

;; ## Idea 5: Frames compose
;;
;; Every function in the API takes a frame and returns a frame.
;; A **composite** frame is a plain map too -- with `:frames`
;; holding its sub-frames and `:layout` describing how to tile
;; them. Here is a two-panel composite written as an explicit map:

(def two-panel
  (pj/prepare-frame
   {:data (rdatasets/datasets-iris)
    :layout {:direction :horizontal}
    :frames [{:mapping {:x :sepal-length :y :sepal-width :color :species}
              :layers [{:layer-type :point}]}
             {:mapping {:x :petal-length :y :petal-width :color :species}
              :layers [{:layer-type :point}]}]}))

two-panel

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Printed, its structure is visible at once:

(kind/pprint two-panel)

(kind/test-last [(fn [v] (and (= 2 (count (:frames v)))
                              (= :horizontal (get-in v [:layout :direction]))))])

;; The outer `:data` is inherited by every sub-frame. `pj/arrange`
;; is the ergonomic way to build this shape from a list of already-
;; built frames:

(pj/arrange
 [(-> (rdatasets/datasets-iris)
      (pj/frame :sepal-length :sepal-width {:color :species})
      pj/lay-point)
  (-> (rdatasets/datasets-iris)
      (pj/frame :petal-length :petal-width {:color :species})
      pj/lay-point)])

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; Printed, `pj/arrange` always wraps its plots in a top-level
;; vertical layout whose rows are horizontal strips -- a single
;; row of two panels shows up here as a vertical-of-horizontal
;; composite. The sub-frames themselves are clean leaf maps that
;; match the shape of the explicit-map form above:

(-> (pj/arrange
     [(-> (rdatasets/datasets-iris)
          (pj/frame :sepal-length :sepal-width {:color :species})
          pj/lay-point)
      (-> (rdatasets/datasets-iris)
          (pj/frame :petal-length :petal-width {:color :species})
          pj/lay-point)])
    kind/pprint)

(kind/test-last [(fn [v] (and (= :vertical (get-in v [:layout :direction]))
                              (= 1 (count (:frames v)))
                              (= 2 (count (:frames (first (:frames v)))))
                              (= :horizontal (get-in v [:frames 0 :layout :direction]))))])

;; `frame`, `lay-*`, `arrange`, `facet`, `options`, `scale`, `coord`
;; -- all take a frame and return a frame. The pipeline reads like a
;; sentence; composites nest the same shape inside `:frames`. The
;; [Composition](./plotje_book.composition.html) chapter
;; covers the multi-frame patterns in depth.

;; ## Summary
;;
;; | Idea | In code |
;; |:-----|:--------|
;; | A frame describes a plot | `pj/frame`, `pj/lay-*` return frames; inspect with `kind/pprint` |
;; | Frames carry mappings | Column-to-aesthetic pairs live in `:mapping` |
;; | What vs how | `pj/frame` declares what; `pj/lay-*` declares how |
;; | Inference fills gaps | Omit choices, the library infers from data |
;; | Frames compose | `pj/arrange` tiles sibling frames; composites nest under `:frames` |
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./plotje_book.core_concepts.html) -- data formats, marks, stats, color, grouping, coordinates
;; - [**Inference Rules**](./plotje_book.inference_rules.html) -- how Plotje chooses defaults
;; - [**Scatter Plots**](./plotje_book.scatter.html) -- chart type examples to explore
