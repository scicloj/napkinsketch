;; # Architecture
;;
;; Napkinsketch transforms data into plots through a four-stage pipeline.
;; This notebook documents the pipeline, the key data models, and
;; how the codebase is organized.

(ns napkinsketch-book.architecture
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Malli schema validation
   [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  V[\"Views<br/>(API)\"] -->|resolve| S[\"Plan<br/>(data-space)\"]
  S -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]
  M -->|tree walk| F[\"Figure<br/>(output)\"]
  style V fill:#e8f5e9
  style S fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **Views** — user-facing compositional API: `view`, `lay-point`, `lay-histogram`, etc.
;;
;; - **Plan** — fully resolved plan. Data-space geometry,
;;   domains, tick info, legend. Plain Clojure maps. No rendering primitives.
;;
;; - **Membrane** — a value of the [Membrane](https://github.com/phronmophobic/membrane)
;;   library: positioned drawing primitives in pixel space
;;   (Translate, WithColor, Path, Label, etc.).
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

(kind/test-last [(fn [v] (and (sk/sketch? v)
                              (= :point (:mark (first (sk/views-of v))))))])

;; ### Plan
;;
;; `sk/plan` resolves the views into a plan — a pure-data map with
;; data-space geometry, resolved colors, computed domains, and tick info.
;; The values are still in data space — `x=1` means the original data
;; value 1, not a pixel position.

(def trace-plan (sk/plan trace-views))

trace-plan

(kind/test-last [(fn [v] (and (map? v) (contains? v :panels)))])

;; The plan validates against a [Malli](https://github.com/metosin/malli) schema:

(ss/valid? trace-plan)

(kind/test-last [true?])

;; Numeric arrays (`:xs`, `:ys`, etc.) are [dtype-next](https://github.com/cnuernber/dtype-next) buffers — efficient
;; primitive-backed arrays that work with `nth`, `count`, and all standard
;; sequence operations.

;; ### Membrane
;;
;; `sk/plan->membrane` converts the plan into a tree of membrane
;; drawing primitives positioned in pixel space. This is the
;; format-agnostic intermediate representation — `Translate`,
;; `WithColor`, `WithStyle`, `RoundedRectangle`, `Label`, `Path`, etc.

(def trace-membrane (sk/plan->membrane trace-plan))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v) (pos? (count v))))])

;; ### Figure
;; `sk/membrane->figure` converts the membrane tree into a figure.
;; The `:svg` format produces SVG hiccup.
;; `wrap-svg` adds the root `<svg>` element.

(def trace-figure
  (sk/membrane->figure trace-membrane :svg
                       {:total-width (:total-width trace-plan)
                        :total-height (:total-height trace-plan)}))

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
;; | Plan | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Pixel space |
;; | Figure | Hiccup vectors | Pixel space |

;; ## The Plan Boundary
;;
;; The plan separates **what** to draw from **how** to
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
  WHAT -->|plan| HOW
  style WHAT fill:#e8f5e9
  style HOW fill:#e3f2fd
")

;; The plan is **plain inspectable data** — maps, numbers, strings,
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
;; - Inspecting the plan without rendering
;;
;; - Validating plot structure with Malli
;;
;; - Adding other backends (Canvas, Plotly, Vega-Lite) that consume plans

;; ## Multi-Layer Example
;;
;; A plan can hold multiple layers. Here, scatter points and
;; per-species regression lines share the same panel.

(def multi-views
  (-> data/iris
      (sk/view :petal_length :petal_width {:color :species})
      sk/lay-point
      sk/lay-lm))

(def multi-plan (sk/plan multi-views {:title "Iris Petals with Regression"}))

;; Two layers in the plan — point and line:

(mapv (fn [layer]
        {:mark (:mark layer)
         :n-groups (count (:groups layer))})
      (:layers (first (:panels multi-plan))))

(kind/test-last [(fn [v] (and (= :point (:mark (first v)))
                              (= :line (:mark (second v)))
                              (= 3 (:n-groups (first v)))))])

;; Title and legend are top-level plan keys:

multi-plan

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
  API --> PLAN[\"impl/sketch.clj\"]
  PLAN --> VIEW
  PLAN --> STAT[\"impl/stat.clj\"]
  PLAN --> SCALE[\"impl/scale.clj\"]
  PLAN --> DEFAULTS[\"impl/defaults.clj\"]
  PLOT --> PLAN
  PLOT --> SVG[\"render/svg.clj\"]
  SVG --> MEMBRANE[\"render/membrane.clj\"]
  MEMBRANE --> PANEL[\"render/panel.clj\"]
  PANEL --> MARK[\"render/mark.clj\"]
  PANEL --> SCALE
  PANEL --> COORD[\"impl/coord.clj\"]
  style API fill:#c8e6c9
  style PLAN fill:#ffe0b2
  style PLOT fill:#bbdefb
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; The `impl/` directory is pure data — no membrane dependency.
;; The `render/` directory uses membrane for pixel-space layout and
;; SVG conversion.

;; ## Dependencies
;;
;; Napkinsketch builds on several excellent Clojure libraries:
;;
;; - [Tablecloth](https://scicloj.github.io/tablecloth/) &
;;   [dtype-next](https://github.com/cnuernber/dtype-next) —
;;   dataset manipulation and high-performance numeric arrays
;;
;; - [Membrane](https://github.com/phronmophobic/membrane) —
;;   rendering and layout
;;
;; - [Wadogo](https://github.com/scicloj/wadogo) — scales
;;
;; - [Clojure2d](https://github.com/Clojure2D/clojure2d) —
;;   color palettes and gradients
;;
;; - [Fastmath](https://github.com/generateme/fastmath) — statistics
;;
;; - [Malli](https://github.com/metosin/malli) — schema validation
;;
;; - [Kindly](https://scicloj.github.io/kindly/) &
;;   [Clay](https://scicloj.github.io/clay/) — notebook rendering

;; ## What's Next
;;
;; - [**Extensibility**](./napkinsketch_book.extensibility.html) — add custom marks, stats, scales, and renderers via multimethods
;; - [**Exploring Plans**](./napkinsketch_book.exploring_sketches.html) — inspect plan data structures at each stage
