;; # Blueprint Architecture
;;
;; The Blueprint pipeline extends napkinsketch's four-stage pipeline
;; with a composable front end. Instead of building view maps by hand,
;; you compose a Blueprint — a declarative description of entries,
;; methods, and shared options — that resolves into views automatically.
;;
;; This notebook traces a small example through every stage,
;; explains the plan boundary, and shows the namespace structure.

(ns napkinsketch-book.xkcd7-architecture
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Blueprint internals — for tracing resolution
   [scicloj.napkinsketch.impl.blueprint :as blueprint]
   ;; Sketch internals — for tracing views->plan
   [scicloj.napkinsketch.impl.sketch :as sketch-impl]
   ;; Render internals — for tracing plan->figure
   [scicloj.napkinsketch.impl.render :as render-impl]
   ;; Malli schema validation
   [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  B[\"Blueprint<br/>(composable API)\"] -->|resolve| V[\"Views<br/>(flat maps)\"]
  V -->|xkcd7-views->plan| P[\"Plan<br/>(data-space)\"]
  P -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]
  M -->|tree walk| F[\"Figure<br/>(output)\"]
  style B fill:#d1c4e9
  style V fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **Blueprint** — the composable user API. Functions like
;;   `xkcd7-sketch`, `xkcd7-view`, `xkcd7-lay-point`, `xkcd7-options`, and
;;   `xkcd7-facet` build up a Blueprint record. No computation has happened yet.
;;
;; - **Views** — a flat vector of maps produced by resolving the Blueprint.
;;   Each view map has `:data`, `:x`, `:y`, `:mark`, `:stat`, and aesthetic
;;   keys. This is the same format that the old pipeline used directly.
;;
;; - **Plan** — fully resolved plan. Data-space geometry, domains, tick info,
;;   legend. Plain Clojure maps and dtype-next buffers. No rendering primitives.
;;
;; - **Membrane** — positioned drawing primitives in pixel space
;;   (Translate, WithColor, Path, Label, etc.).
;;
;; - **Figure** — final output. A tree walk converts membrane records
;;   to SVG hiccup, which Clay/Kindly renders in notebooks.

;; ## Pipeline Trace
;;
;; Let's trace a small example through all five stages,
;; inspecting the intermediate values at each step.

(def trace-data
  {:x [1 2 3 4 5]
   :y [2 4 3 5 4]
   :g [:a :a :b :b :b]})

;; ### Blueprint
;;
;; The user composes a Blueprint by threading data through
;; composable functions. The Blueprint records what to plot
;; without doing any computation.

(def trace-bp
  (-> trace-data
      (sk/xkcd7-lay-point :x :y {:color :g})))

;; The Blueprint is a record with five fields:
;;
;; - `:data` — the dataset (coerced to tablecloth)
;;
;; - `:shared` — options inherited by all entries (from `xkcd7-view`)
;;
;; - `:entries` — structural entries, each with `:x`, `:y`, and optional `:methods`
;;
;; - `:methods` — global methods (from bare `xkcd7-lay-*` without columns)
;;
;; - `:opts` — rendering options (title, width, etc.)

(blueprint/blueprint? trace-bp)

(kind/test-last [true?])

;; Let's look at the entries and methods inside the Blueprint:

(count (:entries trace-bp))

(kind/test-last [(fn [n] (= 1 n))])

(:entries trace-bp)

(kind/test-last [(fn [entries]
                   (let [e (first entries)]
                     (and (= :x (:x e))
                          (= :y (:y e))
                          (= 1 (count (:methods e))))))])

;; The entry has one method — the point layer — attached directly
;; because `xkcd7-lay-point` was called with columns.

(get-in (:entries trace-bp) [0 :methods 0 :mark])

(kind/test-last [(fn [m] (= :point m))])

;; ### Views
;;
;; `resolve-blueprint` flattens the Blueprint into a vector of
;; view maps. Each view merges shared options, entry columns,
;; and method details into one map with `:data`, `:x`, `:y`, `:mark`, etc.

(def trace-views
  (blueprint/resolve-blueprint trace-bp))

(count trace-views)

(kind/test-last [(fn [n] (= 1 n))])

(select-keys (first trace-views) [:x :y :mark :color])

(kind/test-last [(fn [m] (and (= :x (:x m))
                              (= :y (:y m))
                              (= :point (:mark m))
                              (= :g (:color m))))])

;; ### Plan
;;
;; `xkcd7-views->plan` resolves the views into a plan — a pure-data map
;; with data-space geometry, resolved colors, computed domains, and tick info.
;; The values are still in data space.

(def trace-plan
  (sketch-impl/xkcd7-views->plan trace-views {}))

trace-plan

(kind/test-last [(fn [v] (and (map? v) (contains? v :panels)))])

;; The plan validates against a Malli schema:

(ss/valid? trace-plan)

(kind/test-last [true?])

;; ### Membrane
;;
;; `plan->membrane` converts the plan into a tree of membrane
;; drawing primitives positioned in pixel space.

(def trace-membrane (sk/plan->membrane trace-plan))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v) (pos? (count v))))])

;; ### Figure
;;
;; `membrane->figure` converts the membrane tree into SVG hiccup.

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

;; ### Shortcut: Blueprint to Plan
;;
;; In practice, `sk/xkcd7-plan` does the Blueprint-to-plan conversion
;; in one step — resolving the Blueprint and running `xkcd7-views->plan`
;; internally.

(def shortcut-plan (sk/xkcd7-plan trace-bp))

(ss/valid? shortcut-plan)

(kind/test-last [true?])

;; ### Pipeline Summary
;;
;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Blueprint | Blueprint record | N/A (declarative) |
;; | Views | Vector of maps | N/A (declarative) |
;; | Plan | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Pixel space |
;; | Figure | Hiccup vectors | Pixel space |

;; ## The Plan Boundary
;;
;; The plan separates **what** to draw from **how** to
;; draw it. The Blueprint and view stages describe intent;
;; the membrane and figure stages handle rendering.

^:kindly/hide-code
(kind/mermaid "
graph LR
  subgraph WHAT [\"WHAT — data + semantics\"]
    B[\"Blueprint\"]
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
;; A Blueprint can hold multiple methods on the same entry.
;; Here, scatter points and per-species regression lines share
;; the same panel because both `xkcd7-lay-point` and `xkcd7-lay-lm`
;; target the same `:petal_length`/`:petal_width` entry.

(def multi-bp
  (-> data/iris
      (sk/xkcd7-view :petal_length :petal_width {:color :species})
      sk/xkcd7-lay-point
      sk/xkcd7-lay-lm))

;; The Blueprint has one entry with two global methods:

(count (:entries multi-bp))

(kind/test-last [(fn [n] (= 1 n))])

(mapv :mark (:methods multi-bp))

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :line (second v))))])

;; Resolving produces two views — one per method — both sharing
;; the same columns:

(def multi-views (blueprint/resolve-blueprint multi-bp))

(count multi-views)

(kind/test-last [(fn [n] (= 2 n))])

(mapv :mark multi-views)

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :line (second v))))])

;; Building a plan with a title and checking the layers:

(def multi-plan
  (sk/xkcd7-plan multi-bp {:title "Iris Petals with Regression"}))

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

;; And the rendered result:

(-> data/iris
    (sk/xkcd7-view :petal_length :petal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm
    (sk/xkcd7-options {:title "Iris Petals with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> BP[\"impl/blueprint.clj\"]
  API --> VIEW[\"impl/view.clj\"]
  API --> PLOT[\"impl/plot.clj\"]
  API --> PLAN[\"impl/sketch.clj\"]
  BP --> VIEW
  BP --> PLAN
  BP --> RENDER[\"impl/render.clj\"]
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
  style BP fill:#d1c4e9
  style PLAN fill:#ffe0b2
  style PLOT fill:#bbdefb
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; The `impl/blueprint.clj` module is the new addition. It sits between
;; the public API and the plan resolution stage. It holds the `Blueprint`
;; record, the `resolve-blueprint` function that flattens entries and
;; methods into view maps, and the `render-blueprint` function that
;; drives the full pipeline for auto-rendering in notebooks.
;;
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
