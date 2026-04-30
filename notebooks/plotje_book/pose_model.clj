;; # Pose Model
;;
;; Plotje is a composable plotting library inspired by
;; Wilkinson's [Grammar of Graphics](https://link.springer.com/book/10.1007/0-387-28695-0)
;; and Julia's [AlgebraOfGraphics.jl](https://aog.makie.org/stable/).
;; Its operators are shaped by Clojure idioms -- threading, merge,
;; plain maps -- rather than a custom DSL.
;;
;; This chapter introduces the pose value step by step. Each section
;; shows a rendered plot followed by the printed pose value, so you
;; can see both what the library produces and the data structure
;; underneath.

(ns plotje-book.pose-model
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## A pose describes a plot
;;
;; In Plotje, every plot you compose is a **pose** -- a plain
;; Clojure value that describes what to show. The composition
;; functions (`pj/pose`, `pj/lay-*`, `pj/options`, `pj/scale`,
;; `pj/coord`, `pj/facet`, `pj/arrange`) all take a pose and
;; return a pose, so plots build up through ordinary `->`
;; threading. Output functions (`pj/plan`, `pj/plot`, `pj/save`,
;; `pj/draft`) take a pose and return a different shape -- a plan,
;; an SVG, a file path -- and so close the pipeline.
;;
;; Two core operators do the work: `pj/pose` builds a pose by
;; declaring which columns carry which aesthetics, and `pj/lay-*`
;; adds a layer to a pose. A pose is what you arrange before
;; drawing -- which columns become axes, which become aesthetics.
;; Layers are the visual elements: points, lines, smooths, bars.
;; A composite pose contains sub-poses arranged side by side,
;; each able to carry its own layers.
;;
;; The simplest pose carries some data and picks columns. With no
;; explicit chart type, the library infers one from the column
;; types:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Two numerical columns produced a scatter. And because a pose is
;; plain data, you can inspect it with `kind/pprint`:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width)
    kind/pprint)

(kind/test-last [(fn [v] (and (seq (:data v))
                              (= :sepal-length (:x (:mapping v)))))])

;; A pose is a plain Clojure map. The dataset lives under `:data`,
;; the column mapping under `:mapping`, chart-type layers under
;; `:layers` (empty here, since none was attached), and plot-level
;; options under `:opts`.

;; ## Poses carry mappings
;;
;; A **mapping** connects columns to visual properties. We added
;; `:x` and `:y` above; the pose also accepts appearance
;; aesthetics like `:color`, `:size`, `:alpha`, and `:shape`:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; The printed pose shows the full mapping:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    kind/pprint)

(kind/test-last [(fn [v] (= :species (:color (:mapping v))))])

;; All three pairs -- `:x -> :sepal-length`, `:y -> :sepal-width`,
;; `:color -> :species` -- end up in the one `:mapping` map. Future
;; layers on this pose will inherit the whole set.

;; ## What to show, how to show it
;;
;; The API separates **what** to plot from **how** to show it:
;; a pose's `:mapping` holds the "what" (columns to aesthetics),
;; and its `:layers` holds the "how" (one entry per chart-type
;; layer). This split -- mapping for what, layer for how -- is the
;; principle the rest of the library builds on. Declaring the
;; mapping once lets several layers share it -- scatter points and
;; a regression line per species. Written as a literal map,
;; `pj/pose` accepts the nested-map shape directly:

(def multi-layer
  (pj/pose
   {:data (rdatasets/datasets-iris)
    :mapping {:x :sepal-length :y :sepal-width :color :species}
    :layers [{:layer-type :point}
             {:layer-type :smooth :stat :linear-model}]}))

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
;; The threaded form builds the same pose step by step: `pj/pose`
;; sets the mapping, then each `pj/lay-*` appends a layer.

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Printed, the threaded form produces the same shape as the
;; explicit-map form shown earlier -- `:mapping` at the top, `:layers`
;; alongside it. `pj/pose` and `pj/lay-*` build the same pose value
;; step by step.

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    kind/pprint)

(kind/test-last [(fn [v] (and (= 2 (count (:layers v)))
                              (= :species (get-in v [:mapping :color]))))])

;; ## Inference fills the gaps
;;
;; When you omit a choice, Plotje infers it from the data.
;; One numerical column becomes a histogram:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; The printed pose shows an empty `:layers` -- the histogram
;; layer is chosen by inference at render time, not stored on the
;; pose:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length)
    kind/pprint)

(kind/test-last [(fn [v] (empty? (:layers v)))])

;; The principle: **the inferred value fills in only when you have
;; not specified one yourself.** Explicit choices flow down the
;; pose tree and override inference. (One subtlety: an explicit
;; `nil` is a real choice and cancels inheritance -- it does not
;; fall back to inference. See Pose Rule S3 for the precise
;; semantics.)
;;
;; This works for marks (the shape shown, like points or bars), stats
;; (the computation before rendering, like binning), color types, and
;; grouping. See [Inference Rules](./plotje_book.inference_rules.html)
;; for the full set.

;; ## Poses compose
;;
;; Composition functions take a pose and return a pose.
;; A **composite** pose is a plain map too -- with `:poses`
;; holding its sub-poses and `:layout` describing how to tile
;; them. Here is a two-panel composite written as an explicit map:

(def two-panel
  (pj/pose
   {:data (rdatasets/datasets-iris)
    :layout {:direction :horizontal}
    :poses [{:mapping {:x :sepal-length :y :sepal-width :color :species}
             :layers [{:layer-type :point}]}
            {:mapping {:x :petal-length :y :petal-width :color :species}
             :layers [{:layer-type :point}]}]}))

two-panel

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Printed, its structure is visible at once:

(kind/pprint two-panel)

(kind/test-last [(fn [v] (and (= 2 (count (:poses v)))
                              (= :horizontal (get-in v [:layout :direction]))))])

;; The outer `:data` is inherited by every sub-pose. `pj/arrange`
;; is the ergonomic way to build this shape from a list of already-
;; built poses:

(pj/arrange
 [(-> (rdatasets/datasets-iris)
      (pj/pose :sepal-length :sepal-width {:color :species})
      pj/lay-point)
  (-> (rdatasets/datasets-iris)
      (pj/pose :petal-length :petal-width {:color :species})
      pj/lay-point)])

(kind/test-last [(fn [v] (= 2 (:panels (pj/svg-summary v))))])

;; Printed, `pj/arrange` always wraps its plots in a top-level
;; vertical layout whose rows are horizontal strips -- a single
;; row of two panels shows up here as a vertical-of-horizontal
;; composite. The sub-poses themselves are clean leaf maps that
;; match the shape of the explicit-map form above:

(-> (pj/arrange
     [(-> (rdatasets/datasets-iris)
          (pj/pose :sepal-length :sepal-width {:color :species})
          pj/lay-point)
      (-> (rdatasets/datasets-iris)
          (pj/pose :petal-length :petal-width {:color :species})
          pj/lay-point)])
    kind/pprint)

(kind/test-last [(fn [v] (and (= :vertical (get-in v [:layout :direction]))
                              (= 1 (count (:poses v)))
                              (= 2 (count (:poses (first (:poses v)))))
                              (= :horizontal (get-in v [:poses 0 :layout :direction]))))])

;; `pose`, `lay-*`, `arrange`, `facet`, `options`, `scale`, `coord`
;; -- all take a pose and return a pose. The pipeline reads like a
;; sentence; composites nest the same shape inside `:poses`. The
;; [Composition](./plotje_book.composition.html) chapter
;; covers the multi-pose patterns in depth.

;; ## Summary
;;
;; | Concept | In code |
;; |:--------|:--------|
;; | A pose describes a plot | `pj/pose`, `pj/lay-*` return poses; inspect with `kind/pprint` |
;; | Poses carry mappings | Column-to-aesthetic pairs live in `:mapping` |
;; | What vs how | `pj/pose` declares what; `pj/lay-*` declares how |
;; | Inference fills gaps | Omit choices, the library infers from data |
;; | Poses compose | `pj/arrange` tiles sibling poses; composites nest under `:poses` |
;;
;; ## What's Next
;;
;; - [**Core Concepts**](./plotje_book.core_concepts.html) -- data formats, marks, stats, color, grouping, coordinates
;; - [**Composition**](./plotje_book.composition.html) -- composite poses and multi-panel patterns in depth
;; - [**Inference Rules**](./plotje_book.inference_rules.html) -- how Plotje chooses defaults
;; - [**Scatter Plots**](./plotje_book.scatter.html) -- chart type examples to explore
