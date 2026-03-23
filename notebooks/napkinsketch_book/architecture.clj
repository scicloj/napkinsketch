;; # Architecture
;;
;; Napkinsketch transforms data into plots through a four-stage pipeline.
;; This notebook documents the pipeline, the key data models, and
;; how the codebase is organized.

(ns napkinsketch-book.architecture
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Sketch schema — Malli validation for sketch maps
   [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  V[\"Views<br/>(API)\"] -->|resolve| S[\"Sketch<br/>(data-space)\"]
  S -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]
  M -->|tree walk| F[\"Figure<br/>(output)\"]
  style V fill:#e8f5e9
  style S fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **Views** — user-facing compositional API: `view`, `lay-point`, `lay-histogram`, etc.
;;
;; - **Sketch** — fully resolved plot specification. Data-space geometry,
;;   domains, tick info, legend. Plain Clojure maps. No rendering primitives.
;;
;; - **Membrane** — positioned drawing primitives in pixel space.
;;   Translate, WithColor, Path, Label, etc.
;;
;; - **Figure** — final output. A tree walk converts membrane records
;;   to SVG hiccup, which Clay/Kindly renders in notebooks.

;; ## Pipeline Trace
;;
;; Let's trace a small example through all four stages,
;; inspecting the intermediate values at each step.

(def trace-data
  {:x [1 2 3 4 5]
   :y [2 4 3 5 4]
   :g [:a :a :b :b :b]})

;; ### Views
;;
;; The user composes views — a vector of plain maps describing
;; what data to plot and how. No computation has happened yet.

(def trace-views
  (-> trace-data
      (sk/lay-point :x :y {:color :g})))

(kind/pprint trace-views)

(kind/test-last [(fn [v] (and (sk/plot-spec? v)
                              (= :point (:mark (first (sk/views-of v))))))])

;; ### Sketch
;;
;; `sk/sketch` resolves the views into a sketch — a pure-data map with
;; data-space geometry, resolved colors, computed domains, and tick info.
;; The values are still in data space — `x=1` means the original data
;; value 1, not a pixel position.

(def trace-sketch (sk/sketch trace-views))

trace-sketch

(kind/test-last [(fn [v] (and (map? v) (contains? v :panels)))])

;; The sketch validates against a Malli schema:

(ss/valid? trace-sketch)

(kind/test-last [true?])

;; Numeric arrays (`:xs`, `:ys`, etc.) are dtype-next buffers — efficient
;; primitive-backed arrays that work with `nth`, `count`, and all standard
;; sequence operations.

;; ### Membrane
;;
;; `sk/sketch->membrane` converts the sketch into a tree of membrane
;; drawing primitives positioned in pixel space. This is the
;; format-agnostic intermediate representation — `Translate`,
;; `WithColor`, `WithStyle`, `RoundedRectangle`, `Label`, `Path`, etc.

(def trace-membrane (sk/sketch->membrane trace-sketch))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v) (pos? (count v))))])

;; ### Figure
;; `sk/membrane->figure` converts the membrane tree into a figure.
;; The `:svg` format produces SVG hiccup.
;; `wrap-svg` adds the root `<svg>` element.

(def trace-figure
  (sk/membrane->figure trace-membrane :svg
                       {:total-width (:total-width trace-sketch)
                        :total-height (:total-height trace-sketch)}))

(kind/pprint trace-figure)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; And this is what it looks like when rendered:

(kind/hiccup trace-figure)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ### Pipeline Summary
;;
;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Views | Clojure maps | N/A (declarative) |
;; | Sketch | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Pixel space |
;; | Figure | Hiccup vectors | Pixel space |

;; ## The Sketch Boundary
;;
;; The sketch separates **what** to draw from **how** to
;; draw it. It sits between the two concerns.

^:kindly/hide-code
(kind/mermaid "
graph LR
  subgraph WHAT [\"WHAT — data + semantics\"]
    V[\"Views\"]
    ST[\"Statistics\"]
    D[\"Domains\"]
    C[\"Colors\"]
  end
  subgraph HOW [\"HOW — pixels + rendering\"]
    SC[\"Scales (wadogo)\"]
    CO[\"Coord transforms\"]
    MS[\"Membrane tree\"]
    SV[\"SVG conversion\"]
  end
  WHAT -->|sketch| HOW
  style WHAT fill:#e8f5e9
  style HOW fill:#e3f2fd
")

;; The sketch is **plain inspectable data** — maps, numbers, strings,
;; keywords, and dtype-next buffers for numeric arrays. No membrane
;; types, no datasets, no scale objects. It validates against a Malli
;; schema.
;;
;; The membrane tree is **Java objects** — `Translate`, `WithColor`,
;; `RoundedRectangle`, `Label`, etc. All positions are resolved to
;; pixel coordinates. Not serializable.
;;
;; This separation enables:
;;
;; - Inspecting the plot specification without rendering
;;
;; - Validating plot structure with Malli
;;
;; - Adding other backends (Canvas, Plotly, Vega-Lite) that consume sketches

;; ## Multi-Layer Example
;;
;; A sketch can hold multiple layers. Here, scatter points and
;; per-species regression lines share the same panel.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def multi-views
  (-> iris
      (sk/lay-point :petal_length :petal_width {:color :species})
      (sk/lay-lm {:color :species})))

(def multi-sketch (sk/sketch multi-views {:title "Iris Petals with Regression"}))

;; Two layers in the sketch — point and line:

(mapv (fn [layer]
        {:mark (:mark layer)
         :n-groups (count (:groups layer))})
      (:layers (first (:panels multi-sketch))))

(kind/test-last [(fn [v] (and (= :point (:mark (first v)))
                              (= :line (:mark (second v)))
                              (= 3 (:n-groups (first v)))))])

;; Title and legend are top-level sketch keys:

multi-sketch

(kind/test-last [(fn [m] (and (= "Iris Petals with Regression" (:title m))
                              (= 3 (count (get-in m [:legend :entries])))))])

;; And it renders:

(-> multi-views (sk/options {:title "Iris Petals with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> VIEW[\"impl/view.clj\"]
  API --> PLOT[\"impl/plot.clj\"]
  API --> SKETCH[\"impl/sketch.clj\"]
  SKETCH --> VIEW
  SKETCH --> STAT[\"impl/stat.clj\"]
  SKETCH --> SCALE[\"impl/scale.clj\"]
  SKETCH --> DEFAULTS[\"impl/defaults.clj\"]
  PLOT --> SKETCH
  PLOT --> SVG[\"render/svg.clj\"]
  SVG --> MEMBRANE[\"render/membrane.clj\"]
  MEMBRANE --> PANEL[\"render/panel.clj\"]
  PANEL --> MARK[\"render/mark.clj\"]
  PANEL --> SCALE
  PANEL --> COORD[\"impl/coord.clj\"]
  style API fill:#c8e6c9
  style SKETCH fill:#ffe0b2
  style PLOT fill:#bbdefb
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; The `impl/` directory is pure data — no membrane dependency.
;; The `render/` directory uses membrane for pixel-space layout and
;; SVG conversion.

;; ## Sketch Resolution DAG
;;
;; `views->sketch` orchestrates a DAG of helper functions. Each node
;; computes a piece of the sketch; arrows show data dependencies.

^:kindly/hide-code
(kind/mermaid "
graph TD
  VIEWS[\"views + opts\"]
  VIEWS --> INFER[\"infer-layout\"]
  VIEWS --> COLORS[\"collect-colors<br/>(resolve-view × N)\"]
  VIEWS --> ANNOTS[\"annotations\"]

  INFER --> GRID[\"compute-grid\"]
  INFER --> DIMS[\"compute-panel-dims\"]
  INFER --> GROUP[\"group-panels\"]

  COLORS --> GROUP
  GROUP --> RPV[\"resolve-panel-views<br/>(compute-stat + extract-layer)\"]
  COLORS --> RPV

  RPV --> BUILD[\"build-panels<br/>(domains, ticks)\"]
  GRID --> BUILD
  DIMS --> BUILD

  BUILD --> LABELS[\"resolve-labels\"]
  COLORS --> LEGEND[\"build-legend\"]
  LABELS --> LAYOUT[\"compute-layout-dims\"]
  LEGEND --> LAYOUT
  DIMS --> LAYOUT

  BUILD --> SKETCH[\"sketch\"]
  LABELS --> SKETCH
  LEGEND --> SKETCH
  LAYOUT --> SKETCH

  style VIEWS fill:#e8f5e9
  style SKETCH fill:#fff3e0
  style RPV fill:#e3f2fd
  style BUILD fill:#e3f2fd
")