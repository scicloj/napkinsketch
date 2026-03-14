;; # Glossary
;;
;; Key terms used throughout Napkinsketch, with brief definitions
;; and code examples.

(ns napkinsketch-book.glossary
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## View
;;
;; A **view** is a map describing what to plot: data, column mappings
;; (`:x`, `:y`), mark type, color, and other aesthetics.
;; Created with `sk/view` and refined with `sk/lay`.

(def views
  (-> iris
      (sk/view [[:sepal_length :sepal_width]])
      (sk/lay (sk/point {:color :species}))))

(kind/pprint
 (mapv #(select-keys % [:x :y :mark :color]) views))

(kind/test-last [(fn [v] (and (vector? v) (= 1 (count v))))])

;; ## Mark
;;
;; A **mark** is the visual representation of data: points, lines,
;; bars, rectangles. Mark constructors (`sk/point`, `sk/line`, `sk/tile`,
;; `sk/bar`, `sk/histogram`, `sk/boxplot`, `sk/violin`, `sk/ridgeline`, etc.) return maps that are merged
;; into views by `sk/lay`.

(sk/point {:color :species :alpha 0.5})

(kind/test-last [(fn [m] (and (= :point (:mark m))
                              (= :species (:color m))))])

;; ## Stat
;;
;; A **stat** (statistical transform) processes raw data before
;; rendering. Each mark has a default stat:
;;
;; | Mark | Default stat | What it does |
;; |:-----|:-------------|:-------------|
;; | `point` | `:identity` | Pass through as-is |
;; | `line` | `:identity` | Pass through as-is |
;; | `histogram` | `:bin` | Bin into ranges |
;; | `bar` | `:count` | Count categories |
;; | `lm` | `:lm` | Linear regression |
;; | `loess` | `:loess` | LOESS local regression |
;; | `density` | `:kde` | Kernel density estimation |
;; | `boxplot` | `:boxplot` | Five-number summary + outliers |
;; | `violin` / `ridgeline` | `:violin` | KDE per category |
;; | `tile` | `:bin2d` | 2D grid binning (heatmap) |

;; ## Layer
;;
;; A **layer** is a sketch-level descriptor: resolved mark type,
;; style, and groups of data-space geometry. Layers live inside
;; panels in the sketch.

(let [s (sk/sketch views)
      layer (first (:layers (first (:panels s))))]
  (select-keys layer [:mark :style]))

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; ## Sketch
;;
;; A **sketch** is the fully resolved intermediate representation —
;; a plain Clojure map containing everything needed to render a plot:
;; data-space geometry, domains, tick info, legend, layout dimensions.
;; No membrane types, no datasets, no scale objects.
;;
;; Created with `sk/sketch`. Serializable and inspectable.

(def my-sketch (sk/sketch views))

(sort (keys my-sketch))

(kind/test-last [(fn [ks] (every? keyword? ks))])

;; ## Panel
;;
;; A **panel** is a single plotting area within a sketch. It contains
;; x/y domains, scale specs, tick info, coordinate type, and layers.
;; A simple plot has one panel; `sk/facet` and `sk/facet-grid` produce multiple.

(sort (keys (first (:panels my-sketch))))

(kind/test-last [(fn [ks] (some #{:x-domain :y-domain :layers} ks))])

;; ## Domain
;;
;; A **domain** is the range of data values along an axis.
;;
;; - Numerical: `[min max]` with padding (e.g., `[4.0 8.2]`)
;; - Categorical: sequence of distinct values (e.g., `["setosa" "versicolor" "virginica"]`)

(let [p (first (:panels my-sketch))]
  {:x-domain (:x-domain p)
   :y-domain (:y-domain p)})

(kind/test-last [(fn [m] (and (= 2 (count (:x-domain m)))
                              (number? (first (:x-domain m)))))])

;; ## Scale
;;
;; A **scale** maps data values to pixel positions. Built from a
;; domain and a pixel range using wadogo.
;;
;; | Type | Use |
;; |:-----|:----|
;; | `:linear` | Numerical data (default) |
;; | `:log` | Orders-of-magnitude data |
;; | `:categorical` | Distinct categories (band scale) |
;;
;; Scales are created at render time, not stored in the sketch.
;; The sketch stores scale *specs* (`:type`, `:domain`).
;;
;; **Temporal columns** (`LocalDate`, `LocalDateTime`, `Instant`,
;; `java.util.Date`) are automatically detected and converted to
;; epoch-day numbers. Tick labels display as date strings (e.g. `2024-01-15`).

;; ## Coord
;;
;; A **coord** (coordinate system) defines how data-space maps to
;; pixel-space.
;;
;; | Type | Behavior |
;; |:-----|:---------|
;; | `:cartesian` | Standard x→right, y→up |
;; | `:flip` | Swap x and y axes |
;; | `:polar` | Radial: x→angle, y→radius |

;; ## Membrane (drawable tree)
;;
;; A **membrane** (drawable tree) is a tree of layout and
;; drawing primitives (`Translate`, `WithColor`, `RoundedRectangle`,
;; `Label`, etc.) that represents a complete plot.
;;
;; The membrane is an intermediate step in the SVG rendering path:
;; sketch → membrane → SVG hiccup. Direct renderers (e.g., Plotly)
;; skip the membrane entirely.

(def my-membrane (sk/sketch->membrane my-sketch))

(vector? my-membrane)

(kind/test-last [(fn [v] (true? v))])

(count my-membrane)

(kind/test-last [(fn [n] (pos? n))])

;; ## Figure
;;
;; A **figure** is the final rendered output — the result of rendering
;; a sketch to a specific format. For SVG, the figure is hiccup markup
;; wrapped in `kind/hiccup`.
;;
;; Created by `sk/plot` (which calls `sk/sketch->figure` internally).

(def my-figure (sk/sketch->figure my-sketch :svg {}))

(first my-figure)

(kind/test-last [(fn [v] (= :svg v))])

;; ## Summary Table
;;
;; | Term | What | Lifetime |
;; |:-----|:-----|:---------|
;; | View | Map: data + column mappings + mark | User builds, consumed by `sketch` |
;; | Mark | Visual type: point, line, bar, ... | Key in view map |
;; | Stat | Data transform: identity, bin, count, lm, kde, etc. | Computed during sketch resolution |
;; | Layer | Resolved geometry + style for one mark | Lives inside sketch panels |
;; | Panel | One plotting area (domain, ticks, layers) | One or more per sketch |
;; | Sketch | Complete resolved plot description | Serializable, inspectable |
;; | Domain | Data range on an axis | Part of panel |
;; | Scale | Data → pixel mapping | Created at render time |
;; | Coord | Coordinate system (cartesian, flip, polar) | Applied at render time |
;; | Membrane | Drawable tree (membrane library) | Intermediate (SVG path only) |
;; | Figure | Final output (SVG hiccup, Plotly spec, ...) | Returned to user |
