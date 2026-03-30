;; # Extensibility
;;
;; Napkinsketch is built on multimethods — open dispatch points that let
;; you add new marks, statistics, scales, coordinate systems, and
;; output formats without modifying the core library.
;;
;; This notebook catalogs every multimethod, shows its dispatch
;; mechanism, lists the built-in implementations, and demonstrates
;; how to extend each one.

(ns napkinsketch-book.extensibility
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Method registry — for inspecting method data
   [scicloj.napkinsketch.method :as method]
   ;; Implementation namespaces — for extension points
   [scicloj.napkinsketch.impl.stat :as stat]
   [scicloj.napkinsketch.impl.extract :as extract]
   [scicloj.napkinsketch.impl.sketch :as sketch]
   [scicloj.napkinsketch.render.mark :as mark]
   [scicloj.napkinsketch.render.svg :as svg]
   [scicloj.napkinsketch.impl.render :as render]))

;; ## Overview
;;
;; The pipeline from data to figure has several stages, each governed
;; by a multimethod:
;;
;; ```
;; views → views->sketch (compute-stat, extract-layer, ...)
;;                                              ↓
;;                                           sketch
;;                                              ↓
;;                          sketch->figure (orchestrates full path)
;;                                              ↓
;;                              ┌───────────────┴───────────────┐
;;                     membrane path                     direct path
;;                  sketch→membrane                   sketch→figure
;;                        ↓
;;                  membrane→figure
;;                        ↓
;;                      figure                           figure
;; ```
;;
;; | Multimethod | Namespace | Dispatches on | Purpose |
;; |:------------|:----------|:--------------|:--------|
;; | `compute-stat` | `impl/stat.clj` | `:stat` key | Transform data (identity, bin, count, lm, loess, kde, boxplot) |
;; | `extract-layer` | `impl/extract.clj` | `:mark` key | Convert stat result → sketch layer descriptor |
;; | `layer->membrane` | `render/mark.clj` | `:mark` key | Render sketch layer → membrane drawables |
;; | `sketch->figure` | `impl/render.clj` | format keyword | Orchestrate sketch → figure (full path) |
;; | `membrane->figure` | `impl/render.clj` | format keyword | Convert membrane tree → figure |
;; | `make-scale` | `impl/scale.clj` | domain type + spec | Build a wadogo scale |
;; | `make-coord` | `impl/coord.clj` | coord-type keyword | Build a coordinate function |

;; ## `compute-stat`
;;
;; Transforms raw data into a statistical summary. Each method
;; uses a stat to prepare data for rendering.
;;
(kind/table
 {:column-names ["Dispatch value" "What it does"]
  :row-maps
  (->> (methods stat/compute-stat)
       keys
       (filter keyword?)
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "What it does" (sk/stat-doc k)})))})

(kind/test-last [(fn [t] (= 11 (count (:row-maps t))))])
;;
;; Dispatch function: `(fn [view] (or (:stat view) :identity))`

;; The stat is part of the **method** returned by the mark
;; map. For example, `(method/lookup :histogram)` returns a method
;; with `:stat :bin`:

(method/lookup :histogram)

(kind/test-last [(fn [m] (= :bin (:stat m)))])

;; `(method/lookup :bar)` returns a method with `:stat :count`:

(method/lookup :bar)

(kind/test-last [(fn [m] (= :count (:stat m)))])

;; `(method/lookup :point)` returns a method with `:stat :identity`:

(method/lookup :point)

(kind/test-last [(fn [m] (= :identity (:stat m)))])

;; ### How to extend: add a new stat
;;
;; To add a new statistical transform (e.g., `:loess` for local
;; regression), define a new `defmethod`:
;;
;; ```clojure
;; (defmethod stat/compute-stat :loess [view]
;;   ;; Compute LOESS smoothing from view's :data, :x, :y
;;   ;; Return {:points [...] :x-domain [...] :y-domain [...]}
;;   ...)
;; ```
;;
;; The return value must have the same shape as `:identity` — a map
;; with `:points` (groups of `:xs`, `:ys`), `:x-domain`, and
;; `:y-domain`.

;; ## `extract-layer`
;;
;; Converts a stat result into a sketch layer descriptor — a plain
;; map with data-space geometry and resolved colors.
;;
(kind/table
 {:column-names ["Dispatch value" "Output"]
  :row-maps
  (->> (methods extract/extract-layer)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Output" (sk/mark-doc k)})))})

(kind/test-last [(fn [t] (= 17 (count (:row-maps t))))])
;;
;; Dispatch function: `(fn [view stat all-colors cfg] (:mark view))`

;; A sketch layer looks like this:

(let [s (-> data/iris
            (sk/lay-point :sepal_length :sepal_width {:color :species})
            sk/sketch)
      layer (first (:layers (first (:panels s))))]
  layer)

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (number? (get-in m [:style :opacity]))))])

;; ## `layer->membrane`
;;
;; Renders a sketch layer descriptor into membrane drawable primitives.
;; This is the "membrane path" — used when the target format goes through
;; membrane (e.g., SVG).
;;
(kind/table
 {:column-names ["Dispatch value" "Membrane output"]
  :row-maps
  (->> (methods mark/layer->membrane)
       keys
       (filter keyword?)
       (remove #{:default})
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Membrane output" (sk/membrane-mark-doc k)})))})

(kind/test-last [(fn [t] (= 17 (count (:row-maps t))))])
;;
;; Dispatch function: `(fn [layer ctx] (:mark layer))`
;;
;; ### How to extend: add a new mark type
;;
;; Adding a new mark (e.g., `:area` for area charts) requires methods
;; on both `extract-layer` and `layer->membrane`:
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

;; ## `sketch->figure`
;;
;; Orchestrates the full sketch → figure path. The `:svg` implementation
;; goes through the membrane path: `sketch → membrane → figure`.
;; Other renderers can skip membrane and go directly from sketch
;; to their target format.
;;
;; | Dispatch value | Path |
;; |:---------------|:-----|
;; | `:svg` | sketch → membrane → `membrane->figure :svg` |
;;
;; Dispatch function: `(fn [sketch format opts] format)`

;; Using `sketch->figure` directly:

(def my-sketch
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width {:color :species})
      sk/sketch))

(first (sk/sketch->figure my-sketch :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; The same sketch can be rendered to different formats:

(def my-figure (sk/sketch->figure my-sketch :svg {}))

(vector? my-figure)

(kind/test-last [(fn [v] (true? v))])

;; ### How to extend: add a new direct format
;;
;; To add a Plotly renderer that reads sketch data directly
;; (no membrane needed), register a `sketch->figure` defmethod:
;;
;; ```clojure
;; (ns mylib.render.plotly
;;   (:require [scicloj.napkinsketch.impl.render :as render]))
;;
;; (defmethod render/sketch->figure :plotly [sketch _ opts]
;;   ;; Read sketch domains, layers, legend, layout
;;   ;; Build a Plotly.js spec directly — no membrane needed
;;   {:data (mapcat sketch-layer->plotly-traces
;;                  (:layers (first (:panels sketch))))
;;    :layout {:xaxis {:title (:x-label sketch)}
;;             :yaxis {:title (:y-label sketch)}}})
;; ```
;;
;; Then users opt in by requiring the namespace:
;;
;; ```clojure
;; (require '[mylib.render.plotly])
;; (sk/plot views {:format :plotly})
;; ```

;; ## `membrane->figure`
;;
;; Converts a membrane drawable tree into a figure for a given format.
;; This is the extensibility point for membrane-based output formats —
;; formats that share the same drawable tree but walk it differently.
;;
;; | Dispatch value | Output |
;; |:---------------|:-------|
;; | `:svg` | SVG hiccup wrapped in `kind/hiccup` |
;;
;; Dispatch function: `(fn [membrane-tree format opts] format)`

;; `sk/sketch->membrane` builds the tree, `sk/membrane->figure` converts it:

(def my-membrane (sk/sketch->membrane my-sketch))

(vector? my-membrane)

(kind/test-last [(fn [v] (true? v))])

(first (sk/membrane->figure my-membrane :svg
                            {:total-width (:total-width my-sketch)
                             :total-height (:total-height my-sketch)}))

(kind/test-last [(fn [v] (= :svg v))])

;; ### How to extend: add a new membrane-based format
;;
;; To add a format that reuses the membrane tree (e.g., Canvas, PDF),
;; register a `membrane->figure` defmethod:
;;
;; ```clojure
;; (ns mylib.render.canvas
;;   (:require [scicloj.napkinsketch.impl.render :as render]
;;             [scicloj.napkinsketch.render.membrane :as membrane]))
;;
;; (defmethod render/membrane->figure :canvas [membrane-tree _ opts]
;;   ;; Walk the same drawable tree, emit canvas draw calls
;;   (canvas-walk membrane-tree))
;;
;; ;; Also register sketch->figure to orchestrate the full path:
;; (defmethod render/sketch->figure :canvas [sketch _ opts]
;;   (let [mt (membrane/sketch->membrane sketch)]
;;     (render/membrane->figure mt :canvas
;;                               {:total-width (:total-width sketch)
;;                                :total-height (:total-height sketch)})))
;; ```

;; ## `make-scale`
;;
;; Builds a wadogo scale from a domain and pixel range.
;;
(kind/table
 {:column-names ["Dispatch value" "Scale type"]
  :row-maps
  (->> (methods scicloj.napkinsketch.impl.scale/make-scale)
       keys
       (filter keyword?)
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Scale type" (sk/scale-doc k)})))})

(kind/test-last [(fn [t] (= 3 (count (:row-maps t))))])
;;
;; Dispatch: inferred from the domain type and scale spec.
;; Categorical domains → `:categorical`. Numerical domains default to
;; `:linear`, overridden to `:log` by `(sk/scale views :x :log)`.

;; ## `make-coord`
;;
;; Builds a coordinate function that maps data-space (x, y) to
;; pixel-space (px, py).
;;
(kind/table
 {:column-names ["Dispatch value" "Behavior"]
  :row-maps
  (->> (methods scicloj.napkinsketch.impl.coord/make-coord)
       keys
       (filter keyword?)
       sort
       (mapv (fn [k] {"Dispatch value" (kind/code (pr-str k))
                      "Behavior" (sk/coord-doc k)})))})

(kind/test-last [(fn [t] (= 4 (count (:row-maps t))))])
;;
;; All four use the same scales — `:flip` swaps which scale
;; maps to which pixel axis, and `:polar` maps x to angle and y to radius.

;; A flipped bar chart uses `:flip` coordinates:

(-> data/iris
    (sk/lay-bar :species)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
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

(sk/stat-doc :quantile)

(kind/test-last [(fn [v] (= "Quantile regression bands" v))])

;; ### Missing documentation degrades gracefully
;;
;; If you skip the `[:key :doc]` defmethod, the table still renders —
;; the description falls back to "(no description)" instead of
;; throwing an error. Let us remove the doc defmethod and verify:

(remove-method stat/compute-stat [:quantile :doc])

(sk/stat-doc :quantile)

(kind/test-last [(fn [v] (= "(no description)" v))])

;; ### Cleanup
;;
;; Remove the example extension so it does not affect other tests:

(remove-method stat/compute-stat :quantile)

(count (filter keyword? (keys (methods stat/compute-stat))))

(kind/test-last [(fn [v] (= 11 v))])

;; ## Summary
;;
;; | To add... | Extend... |
;; |:----------|:----------|
;; | A new statistical transform | `compute-stat` |
;; | A new mark type | `extract-layer` + `layer->membrane` |
;; | A new output format (direct) | `sketch->figure` |
;; | A new output format (membrane-based) | `membrane->figure` + `sketch->figure` |
;; | A new scale type | `make-scale` |
;; | A new coordinate system | `make-coord` |
