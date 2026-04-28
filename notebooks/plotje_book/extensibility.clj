;; # Extensibility
;;
;; Plotje is built on multimethods -- open dispatch points that let
;; you add new marks, statistics, scales, coordinate systems, and
;; output formats without modifying the core library.
;;
;; This notebook catalogs every multimethod, shows its dispatch
;; mechanism, lists the built-in implementations, and demonstrates
;; how to extend each one.

(ns plotje-book.extensibility
  (:require
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Layer-type registry -- for inspecting layer-type data
   [scicloj.plotje.layer-type :as layer-type]
   ;; Implementation namespaces -- for extension points
   [scicloj.plotje.impl.stat :as stat]
   [scicloj.plotje.impl.extract :as extract]
   [scicloj.plotje.render.mark :as mark]
   [scicloj.plotje.render.svg :as svg]
   [scicloj.plotje.impl.render :as render]))

;; ## Overview
;;
;; The pipeline from data to plot has several stages, each governed
;; by a multimethod. The pose API adds a composable front-end
;; that resolves into the same pipeline:
;;
;; ```
;; pose (pj/pose, pj/lay-*, pj/options, ...)
;;                        |
;;                   pose->draft
;;                        v
;;                      draft
;;                        |
;;                   draft->plan (compute-stat, extract-layer, ...)
;;                        v
;;                      plan
;;                        |
;;                    plan->plot (orchestrates full path)
;;                        v
;;             ----------------------
;;           membrane path      direct path
;;           plan->membrane     plan->plot
;;                |
;;           membrane->plot
;;                v
;;              plot                 plot
;; ```
;;
;; | Multimethod | Namespace | Dispatches on | Purpose |
;; |:------------|:----------|:--------------|:--------|
;; | `compute-stat` | `impl/stat.clj` | `:stat` key | Transform data (identity, bin, count, lm, loess, kde, boxplot) |
;; | `extract-layer` | `impl/extract.clj` | `:mark` key | Convert a stat result into a plan layer descriptor |
;; | `layer->membrane` | `render/mark.clj` | `:mark` key | Render a plan layer as membrane drawables |
;; | `plan->plot` | `impl/render.clj` | format keyword | Orchestrate the full path from plan to plot |
;; | `membrane->plot` | `impl/render.clj` | format keyword | Convert a membrane tree into a plot |
;; | `make-scale` | `impl/scale.clj` | domain type + spec | Build a wadogo scale |
;; | `make-coord` | `impl/coord.clj` | coord-type keyword | Build a coordinate function |
;; | `apply-position` | `impl/position.clj` | position keyword | Adjust group layout (dodge, stack, fill) |

;; ## `compute-stat`
;;
;; Transforms raw data into a statistical summary. Each layer type
;; uses a stat to prepare data for rendering.
;;
;; Dispatch function: `(fn [view] (or (:stat view) :identity))`

(kind/table
 {:column-names ["Dispatch value" "What it does"]
  :row-maps
  (->> (methods stat/compute-stat)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "What it does" (pj/stat-doc k)})))})

(kind/test-last [(fn [t] (= 11 (count (:row-maps t))))])

;; The stat is part of the **layer type** returned by
;; `layer-type/lookup`. For example, `(layer-type/lookup :histogram)`
;; returns a layer type with `:stat :bin`:

(layer-type/lookup :histogram)

(kind/test-last [(fn [m] (= :bin (:stat m)))])

;; `(layer-type/lookup :bar)` returns a layer type with `:stat :count`:

(layer-type/lookup :bar)

(kind/test-last [(fn [m] (= :count (:stat m)))])

;; `(layer-type/lookup :point)` returns a layer type with `:stat :identity`:

(layer-type/lookup :point)

(kind/test-last [(fn [m] (= :identity (:stat m)))])

;; ### How to extend: add a new stat
;;
;; To add a new statistical transform (e.g., `:loess` for local
;; regression), define a new `defmethod`.
;;
;; Pseudocode:
;;
;; ```clojure
;; (defmethod stat/compute-stat :loess [view]
;;   ;; Compute LOESS smoothing from view's :data, :x, :y
;;   ;; Return {:points [...] :x-domain [...] :y-domain [...]}
;;   ...)
;; ```
;;
;; The return value must always include `:x-domain` and `:y-domain`.
;; The rest of the shape depends on what the paired `extract-layer`
;; expects -- the stat and extractor are a matched pair. For
;; point-like marks, return `:points` (groups of `:xs`, `:ys`).
;; For other marks, study a similar existing pair as a template:
;;
;; - `:identity` returns `{:points [...] :x-domain [...] :y-domain [...]}`
;; - `:bin` returns `{:bins [...] :max-count ... :x-domain [...] :y-domain [...]}`
;; - `:boxplot` returns `{:boxes [...] :categories [...] :x-domain [...] :y-domain [...]}`

;; ## `extract-layer`
;;
;; Converts a stat result into a plan layer descriptor -- a plain
;; map with data-space geometry and resolved colors.
;;
;; Dispatch function: `(fn [view stat all-colors cfg] (:mark view))`

(kind/table
 {:column-names ["Dispatch value" "Output"]
  :row-maps
  (->> (methods extract/extract-layer)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Output" (pj/mark-doc k)})))})

(kind/test-last [(fn [t] (= 18 (count (:row-maps t))))])

;; A plan layer looks like this:

(let [s (-> (rdatasets/datasets-iris)
            (pj/lay-point :sepal-length :sepal-width {:color :species})
            pj/plan)
      layer (first (:layers (first (:panels s))))]
  layer)

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (number? (get-in m [:style :opacity]))))])

;; ## `layer->membrane`
;;
;; Renders a plan layer descriptor into membrane drawable primitives.
;; This is the "membrane path" -- used when the target format goes through
;; membrane (e.g., SVG).
;;
;; Dispatch function: `(fn [layer ctx] (:mark layer))`

(kind/table
 {:column-names ["Dispatch value" "Membrane output"]
  :row-maps
  (->> (methods mark/layer->membrane)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Membrane output" (pj/membrane-mark-doc k)})))})

(kind/test-last [(fn [t] (= 18 (count (:row-maps t))))])
;;
;; ### How to extend: add a new mark type
;;
;; Adding a new mark (e.g., `:area` for area charts) requires methods
;; on both `extract-layer` and `layer->membrane`.
;;
;; Pseudocode:
;;
;; ```clojure
;; ;; 1. Extract geometry from stat result
;; (defmethod extract/extract-layer :area [view stat all-colors cfg]
;;   {:mark :area
;;    :style {:opacity 0.5}
;;    :groups (vec (for [{:keys [color xs ys]} (:points stat)]
;;                   {:color (extract/resolve-color ...)
;;                    :xs xs :ys ys}))})
;;
;; ;; 2. Render to membrane drawables
;; (defmethod mark/layer->membrane :area [layer ctx]
;;   ;; Build filled polygon from xs/ys + baseline
;;   ...)
;; ```
;;
;; ### How to extend: register a layer type and create a pose-compatible layer function
;;
;; After defining `compute-stat` and `extract-layer` for your custom
;; mark, register a layer type and create a convenience function that
;; works with the pose API:
;;
;; ```clojure
;; ;; Register the layer type
;; (layer-type/register! :waterfall
;;   {:mark :waterfall :stat :waterfall
;;    :doc "Waterfall -- running total with increase/decrease bars."})
;;
;; ;; Users can then call:
;; ;; (pj/lay data (layer-type/lookup :waterfall))
;;
;; ;; Or create a convenience function using lay:
;; (defn lay-waterfall
;;   ([pose] (pj/lay pose (layer-type/lookup :waterfall)))
;;   ([data x y] (-> data (pj/pose x y) (pj/lay (layer-type/lookup :waterfall))))
;;   ([data x y opts] (-> data (pj/pose x y) (pj/lay (merge (layer-type/lookup :waterfall) opts)))))
;; ```
;;
;; Users can then call `(lay-waterfall data :category :amount)`.
;;
;; Note: if your custom mark is not one of the built-in marks, you also
;; need a `layer->membrane` defmethod for the SVG renderer. Without one,
;; the library throws an error explaining which defmethod to add.

;; ## `plan->plot`
;;
;; Orchestrates the full path from plan to plot. The `:svg`
;; implementation goes through the membrane path: plan, then membrane,
;; then plot. Other renderers can skip membrane and go directly from
;; plan to their target format.
;;
;; Dispatch function: `(fn [plan format opts] format)`
;;
;; | Dispatch value | Path |
;; |:---------------|:-----|
;; | `:svg` | plan, then membrane, then `membrane->plot :svg` |
;; | `:bufimg` | plan, then membrane, then `membrane->plot :bufimg` (raster image) |

;; Using `plan->plot` directly:

(def my-plan
  (-> (rdatasets/datasets-iris)
      (pj/lay-point :sepal-length :sepal-width {:color :species})
      pj/plan))

(first (pj/plan->plot my-plan :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; The same plan can be rendered to different formats:

(def my-figure (pj/plan->plot my-plan :svg {}))

(vector? my-figure)

(kind/test-last [(fn [v] (true? v))])

;; ### How to extend: add a new direct format
;;
;; To add a Plotly renderer that reads plan data directly
;; (no membrane needed), register a `plan->plot` defmethod.
;;
;; Pseudocode:
;;
;; ```clojure
;; (ns mylib.render.plotly
;;   (:require [scicloj.plotje.impl.render :as render]))
;;
;; (defmethod render/plan->plot :plotly [plan _ opts]
;;   ;; Read plan domains, layers, legend, layout
;;   ;; Build a Plotly.js spec directly -- no membrane needed
;;   {:data (mapcat plan-layer->plotly-traces
;;                  (:layers (first (:panels plan))))
;;    :layout {:xaxis {:title (:x-label plan)}
;;             :yaxis {:title (:y-label plan)}}})
;; ```
;;
;; Then users opt in by requiring the namespace:
;;
;; ```clojure
;; (require '[mylib.render.plotly])
;; (pj/plot pose {:format :plotly})
;; ```

;; ## `membrane->plot`
;;
;; Converts a membrane drawable tree into a plot for a given format.
;; This is the extensibility point for membrane-based output formats --
;; formats that share the same drawable tree but walk it differently.
;;
;; Dispatch function: `(fn [membrane-tree format opts] format)`
;;
;; | Dispatch value | Output |
;; |:---------------|:-------|
;; | `:svg` | SVG hiccup wrapped in `kind/hiccup` |
;; | `:bufimg` | Java BufferedImage wrapped in `kind/buffered-image` (raster) |

;; `pj/plan->membrane` builds the tree, `pj/membrane->plot` converts it:

(def my-membrane (pj/plan->membrane my-plan))

(vector? my-membrane)

(kind/test-last [(fn [v] (true? v))])

(first (pj/membrane->plot my-membrane :svg
                          {:total-width (:total-width my-plan)
                           :total-height (:total-height my-plan)}))

(kind/test-last [(fn [v] (= :svg v))])

;; ### How to extend: add a new membrane-based format
;;
;; To add a format that reuses the membrane tree (e.g., Canvas, PDF),
;; register a `membrane->plot` defmethod.
;;
;; Pseudocode:
;;
;; ```clojure
;; (ns mylib.render.canvas
;;   (:require [scicloj.plotje.impl.render :as render]
;;             [scicloj.plotje.render.membrane :as membrane]))
;;
;; (defmethod render/membrane->plot :canvas [membrane-tree _ opts]
;;   ;; Walk the same drawable tree, emit canvas draw calls
;;   (canvas-walk membrane-tree))
;;
;; ;; Also register plan->plot to orchestrate the full path:
;; (defmethod render/plan->plot :canvas [plan _ opts]
;;   (let [mt (membrane/plan->membrane plan)]
;;     (render/membrane->plot mt :canvas
;;                               {:total-width (:total-width plan)
;;                                :total-height (:total-height plan)})))
;; ```

;; ## `make-scale`
;;
;; Builds a wadogo scale from a domain and pixel range.
;;
(kind/table
 {:column-names ["Dispatch value" "Scale type"]
  :row-maps
  (->> (methods scicloj.plotje.impl.scale/make-scale)
       keys
       (filter keyword?)
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Scale type" (pj/scale-doc k)})))})

(kind/test-last [(fn [t] (= 3 (count (:row-maps t))))])
;;
;; Dispatch: inferred from the domain type and scale spec.
;; Categorical domains dispatch to `:categorical`. Numerical domains default to
;; `:linear`, overridden to `:log` by `(pj/scale pose :x :log)`.

;; ## `make-coord`
;;
;; Builds a coordinate function that maps data-space (x, y) to
;; drawing units (px, py).
;;
(kind/table
 {:column-names ["Dispatch value" "Behavior"]
  :row-maps
  (->> (methods scicloj.plotje.impl.coord/make-coord)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Behavior" (pj/coord-doc k)})))})

(kind/test-last [(fn [t] (= 4 (count (:row-maps t))))])
;;
;; All four use the same scales -- `:flip` swaps which scale
;; maps to which pixel axis, and `:polar` maps x to angle and y to radius.

;; A flipped bar chart uses `:flip` coordinates:

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species)
    (pj/coord :flip))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Self-Documenting Extensions
;;
;; Every generated dispatch table in this notebook is built by
;; introspecting multimethod keys at render time. When you extend
;; a multimethod, your new dispatch value automatically appears in
;; the table. You can also register a `[:key :doc]` defmethod to
;; provide a description.
;;
;; ### Adding a documented extension
;;
;; Register both the implementation and a `[:key :doc]` defmethod:

(defmethod stat/compute-stat :quantile [view]
  {:points [] :x-domain [0 1] :y-domain [0 1]})

(defmethod stat/compute-stat [:quantile :doc] [_]
  "Quantile regression bands")

;; The doc helper picks it up immediately:

(pj/stat-doc :quantile)

(kind/test-last [(fn [v] (= "Quantile regression bands" v))])

;; ### Missing documentation degrades gracefully
;;
;; If you skip the `[:key :doc]` defmethod, the table still renders --
;; the description falls back to "(no description)" instead of
;; throwing an error. Let us remove the doc defmethod and verify:

(remove-method stat/compute-stat [:quantile :doc])

(pj/stat-doc :quantile)

(kind/test-last [(fn [v] (= "(no description)" v))])

;; ### Cleanup
;;
;; Remove the example extension so it does not affect other tests:

(remove-method stat/compute-stat :quantile)

(count (remove #{:default} (filter keyword? (keys (methods stat/compute-stat)))))

(kind/test-last [(fn [v] (= 11 v))])

;; ## Summary
;;
;; | To add... | Extend... |
;; |:----------|:----------|
;; | A new statistical transform | `compute-stat` |
;; | A new mark type | `extract-layer` + `layer->membrane` |
;; | A new output format (direct) | `plan->plot` |
;; | A new output format (membrane-based) | `membrane->plot` + `plan->plot` |
;; | A new scale type | `make-scale` |
;; | A new coordinate system | `make-coord` |
;; | A new position adjustment | `apply-position` |

;; ## Background
;;
;; - [**Architecture**](./plotje_book.architecture.html) -- the five-stage pipeline and key libraries

;; ## What's Next
;;
;; - [**Waterfall Extension**](./plotje_book.waterfall_extension.html) -- a worked example that uses the extension points above to add a new chart type
;; - [**Edge Cases**](./plotje_book.edge_cases.html) -- how the library handles unusual inputs
