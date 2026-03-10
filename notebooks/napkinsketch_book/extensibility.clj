;; # Extensibility
;;
;; napkinsketch is built on multimethods — open dispatch points that let
;; you add new marks, statistics, scales, coordinate systems, and
;; output formats without modifying the core library.
;;
;; This notebook catalogs every multimethod, shows its dispatch
;; mechanism, lists the built-in implementations, and demonstrates
;; how to extend each one.

(ns napkinsketch-book.extensibility
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]
   [scicloj.napkinsketch.impl.stat :as stat]
   [scicloj.napkinsketch.impl.sketch :as sketch]
   [scicloj.napkinsketch.impl.mark :as mark]
   [scicloj.napkinsketch.impl.scale :as scale]
   [scicloj.napkinsketch.impl.coord :as coord]
   [scicloj.napkinsketch.impl.render :as render]
   [scicloj.napkinsketch.render.svg :as svg]))

;; ## Overview
;;
;; The pipeline from data to figure has several stages, each governed
;; by a multimethod:
;;
;; ```
;; views → compute-stat → extract-layer → resolve-sketch
;;                                              ↓
;;                                           sketch
;;                                              ↓
;;                               render-layer (scene path)
;;                                    or
;;                               render-figure (direct path)
;;                                              ↓
;;                                           figure
;; ```
;;
;; | Multimethod | Namespace | Dispatches on | Purpose |
;; |:------------|:----------|:--------------|:--------|
;; | `compute-stat` | `impl/stat.clj` | `:stat` key | Transform data (identity, bin, count, lm, loess, kde, boxplot) |
;; | `extract-layer` | `impl/sketch.clj` | `:mark` key | Convert stat result → sketch layer descriptor |
;; | `render-layer` | `impl/mark.clj` | `:mark` key | Render sketch layer → membrane scene |
;; | `render-figure` | `impl/render.clj` | format keyword | Render sketch → figure (:svg, etc.) |
;; | `make-scale` | `impl/scale.clj` | domain type + spec | Build a wadogo scale |
;; | `make-coord` | `impl/coord.clj` | coord-type keyword | Build a coordinate function |

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## `compute-stat`
;;
;; Transforms raw data into a statistical summary. Each mark type
;; uses a stat to prepare data for rendering.
;;
;; | Dispatch value | What it does |
;; |:---------------|:-------------|
;; | `:identity` | Pass through x/y values as-is (scatter, line) |
;; | `:bin` | Bin numerical data into histogram bars |
;; | `:count` | Count occurrences of categorical values |
;; | `:lm` | Linear regression (slope + intercept) |
;; | `:loess` | LOESS local regression smoothing |
;; | `:kde` | Kernel density estimation |
;; | `:boxplot` | Five-number summary with outlier detection |
;; | `:violin` | Kernel density estimation per category |
;;
;; Dispatch function: `(fn [view] (or (:stat view) :identity))`

;; The stat is chosen by the mark constructor. For example,
;; `(sk/histogram)` sets `:stat :bin`:

(sk/histogram)

(kind/test-last [(fn [m] (= :bin (:stat m)))])

;; `(sk/bar)` sets `:stat :count`:

(sk/bar)

(kind/test-last [(fn [m] (= :count (:stat m)))])

;; `(sk/point)` defaults to `:stat :identity`:

(sk/point)

(kind/test-last [(fn [m] (nil? (:stat m)))])

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
;; | Dispatch value | Output |
;; |:---------------|:-------|
;; | `:point` | Groups with `:xs`, `:ys`, `:color`, optional `:sizes`/`:alphas` |
;; | `:bar` | Groups with `:bars` (`:lo`, `:hi`, `:count`) |
;; | `:line` | Groups with `:xs`/`:ys` or line segments |
;; | `:rect` | Categorical bars with `:counts` or value bars |
;; | `:text` | Groups with `:xs`, `:ys`, `:labels` |
;; | `:area` | Groups with `:xs`, `:ys` for filled polygons |
;; | `:boxplot` | Boxes with five-number summary and outliers |
;; | `:violin` | Violin entries with density curves |
;; | `:errorbar` | Groups with `:xs`, `:ys`, `:ymins`, `:ymaxs` |
;; | `:lollipop` | Groups with `:xs`, `:ys` for stems + dots |
;;
;; Dispatch function: `(fn [view stat all-colors cfg] (:mark view))`

;; A sketch layer looks like this:

(let [s (sk/sketch (-> iris
                       (sk/view [[:sepal_length :sepal_width]])
                       (sk/lay (sk/point {:color :species}))))
      layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style]))

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (number? (get-in m [:style :opacity]))))])

;; ## `render-layer`
;;
;; Renders a sketch layer descriptor into membrane scene primitives.
;; This is the "scene path" — used when the target format goes through
;; membrane (e.g., SVG).
;;
;; | Dispatch value | Scene output |
;; |:---------------|:-------------|
;; | `:point` | Translated colored rounded-rectangles |
;; | `:bar` | Filled polygons (histogram bars) |
;; | `:line` | Stroked polylines |
;; | `:rect` | Filled polygons (categorical/value bars) |
;; | `:text` | Translated text labels |
;; | `:area` | Closed filled polygons with baseline |
;; | `:boxplot` | Box + whiskers + median line + outlier points |
;; | `:violin` | Mirrored filled density polygon |
;; | `:errorbar` | Vertical lines with caps |
;; | `:lollipop` | Stems with dots at category positions |
;;
;; Dispatch function: `(fn [layer ctx] (:mark layer))`
;;
;; ### How to extend: add a new mark type
;;
;; Adding a new mark (e.g., `:area` for area charts) requires methods
;; on both `extract-layer` and `render-layer`:
;;
;; ```clojure
;; ;; 1. Extract geometry from stat result
;; (defmethod sketch/extract-layer :area [view stat all-colors cfg]
;;   {:mark :area
;;    :style {:opacity 0.5}
;;    :groups (vec (for [{:keys [color xs ys]} (:points stat)]
;;                   {:color (sketch/resolve-color ...)
;;                    :xs (vec xs) :ys (vec ys)}))})
;;
;; ;; 2. Render to membrane scene
;; (defmethod mark/render-layer :area [layer ctx]
;;   ;; Build filled polygon from xs/ys + baseline
;;   ...)
;; ```

;; ## `render-figure`
;;
;; Renders a sketch into a final figure. This is the top-level
;; extensibility point for output formats.
;;
;; | Dispatch value | Output |
;; |:---------------|:-------|
;; | `:svg` | SVG hiccup wrapped in `kind/hiccup` |
;;
;; Dispatch function: `(fn [sketch format opts] format)`
;;
;; The `:svg` renderer goes through the membrane scene path:
;; `sketch → scene → SVG hiccup`.
;; Other renderers can skip membrane and go directly from sketch
;; to their target format.

;; Using `render-figure` directly:

(def my-sketch
  (sk/sketch (-> iris
                 (sk/view [[:sepal_length :sepal_width]])
                 (sk/lay (sk/point {:color :species})))))

(first (sk/render-figure my-sketch :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; The same sketch can be rendered to different formats:

(def my-figure (sk/render-figure my-sketch :svg {}))

(vector? my-figure)

(kind/test-last [(fn [v] (true? v))])

;; ### How to extend: add a new output format
;;
;; To add a Plotly renderer, create a new namespace and register
;; a `defmethod`:
;;
;; ```clojure
;; (ns mylib.render.plotly
;;   (:require [scicloj.napkinsketch.impl.render :as render]))
;;
;; (defmethod render/render-figure :plotly [sketch _ opts]
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

;; ## `make-scale`
;;
;; Builds a wadogo scale from a domain and pixel range.
;;
;; | Dispatch value | Scale type |
;; |:---------------|:-----------|
;; | `:categorical` | Band scale (one band per category) |
;; | `:linear` | Continuous linear mapping |
;; | `:log` | Logarithmic mapping |
;;
;; Dispatch: inferred from the domain type and scale spec.
;; Categorical domains → `:categorical`. Numerical domains default to
;; `:linear`, overridden to `:log` by `(sk/scale views :x :log)`.

;; ## `make-coord`
;;
;; Builds a coordinate function that maps data-space (x, y) to
;; pixel-space (px, py).
;;
;; | Dispatch value | Behavior |
;; |:---------------|:---------|
;; | `:cartesian` | Standard x-right, y-up mapping |
;; | `:flip` | Swap x and y axes |
;;
;; Both use the same scales — `:flip` simply swaps which scale
;; maps to which pixel axis.

;; A flipped bar chart uses `:flip` coordinates:

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; ## The Scene Path
;;
;; For the SVG renderer, the sketch goes through an intermediate
;; membrane scene before becoming SVG hiccup. The `sketch->scene`
;; function in `render/svg.clj` builds this scene:

(def my-scene (svg/sketch->scene my-sketch))

(vector? my-scene)

(kind/test-last [(fn [v] (true? v))])

;; The scene is a tree of membrane drawables — `Translate`,
;; `WithColor`, `RoundedRectangle`, `Label`, etc. The `scene->svg`
;; function walks this tree and emits SVG hiccup:

(let [svg-body (svg/scene->svg my-scene)
      svg (svg/wrap-svg (:total-width my-sketch) (:total-height my-sketch) svg-body)]
  (first svg))

(kind/test-last [(fn [v] (= :svg v))])

;; This two-step process (sketch → scene → SVG) means that adding
;; a new scene-based target (e.g., Canvas, PDF) only requires writing
;; a new scene walker — the scene construction is shared.

;; ## Summary
;;
;; | To add... | Extend... |
;; |:----------|:----------|
;; | A new statistical transform | `compute-stat` |
;; | A new mark type | `extract-layer` + `render-layer` |
;; | A new output format (direct) | `render-figure` |
;; | A new output format (scene-based) | `render-figure` + scene walker |
;; | A new scale type | `make-scale` |
;; | A new coordinate system | `make-coord` |
