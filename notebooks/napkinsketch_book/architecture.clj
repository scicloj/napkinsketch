;; # sketch Architecture
;;
;; The sketch pipeline extends napkinsketch's four-stage pipeline
;; with a composable front end. Instead of building view maps by hand,
;; you compose a sketch — a declarative description of entries,
;; methods, and shared options — that resolves into views automatically.
;;
;; This notebook traces a small example through every stage,
;; explains the plan boundary, and shows the namespace structure.

(ns napkinsketch-book.architecture
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; sketch internals — for tracing resolution
   [scicloj.napkinsketch.impl.sketch :as sketch]
   ;; Sketch internals — for tracing views->plan
   [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
   ;; Render internals — for tracing plan->figure
   [scicloj.napkinsketch.impl.render :as render-impl]
   ;; Malli schema validation
   [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  B[\"sketch<br/>(composable API)\"] -->|resolve| V[\"Views<br/>(flat maps)\"]
  V -->|views->plan| P[\"Plan<br/>(data-space)\"]
  P -->|scales + coords| M[\"Membrane<br/>(pixel-space)\"]
  M -->|tree walk| F[\"Figure<br/>(output)\"]
  style B fill:#d1c4e9
  style V fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **sketch** — the composable user API. Functions like
;;   `sketch`, `view`, `lay-point`, `options`, and
;;   `facet` build up a sketch record. No computation has happened yet.
;;
;; - **Views** — a flat vector of maps produced by resolving the sketch.
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

;; Most users only interact with the sketch stage and never need to
;; think about the others. The stages below matter when you are debugging
;; unexpected output, building a custom renderer, or extending the library.

;; ## Pipeline Trace
;;
;; Let's trace a small example through all five stages,
;; inspecting the intermediate values at each step.

(def trace-data
  {:x [1 2 3 4 5]
   :y [2 4 3 5 4]
   :g [:a :a :b :b :b]})

;; ### sketch
;;
;; The user composes a sketch by threading data through
;; composable functions. The sketch records what to plot
;; without doing any computation.

(def trace-sk
  (-> trace-data
      (sk/lay-point :x :y {:color :g})))

;; The sketch is a record with five fields:
;;
;; - `:data` — the dataset (coerced to tablecloth)
;;
;; - `:shared` — options inherited by all entries (from `view`)
;;
;; - `:entries` — structural entries, each with `:x`, `:y`, and optional `:methods`
;;
;; - `:methods` — global methods (from bare `lay-*` without columns)
;;
;; - `:opts` — rendering options (title, width, etc.)

(sketch/sketch? trace-sk)

(kind/test-last [true?])

;; Let's look at the entries and methods inside the sketch:

(count (:entries trace-sk))

(kind/test-last [(fn [n] (= 1 n))])

(:entries trace-sk)

(kind/test-last [(fn [entries]
                   (let [e (first entries)]
                     (and (= :x (:x e))
                          (= :y (:y e))
                          (= 1 (count (:methods e))))))])

;; The entry has one method — the point layer — attached directly
;; because `lay-point` was called with columns.

(get-in (:entries trace-sk) [0 :methods 0 :mark])

(kind/test-last [(fn [m] (= :point m))])

;; ### Views
;;
;; `resolve-sketch` flattens the sketch into a vector of
;; view maps. Each view merges shared options, entry columns,
;; and method details into one map with `:data`, `:x`, `:y`, `:mark`, etc.

(def trace-views
  (sketch/resolve-sketch trace-sk))

(count trace-views)

(kind/test-last [(fn [n] (= 1 n))])

(select-keys (first trace-views) [:x :y :mark :color])

(kind/test-last [(fn [m] (and (= :x (:x m))
                              (= :y (:y m))
                              (= :point (:mark m))
                              (= :g (:color m))))])

;; ### Plan
;;
;; `views->plan` resolves the views into a plan — a pure-data map
;; with data-space geometry, resolved colors, computed domains, and tick info.
;; The values are still in data space.

(def trace-plan
  (sketch-impl/views->plan trace-views {}))

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

;; ### Shortcut: sketch to Plan
;;
;; In practice, `sk/plan` does the sketch-to-plan conversion
;; in one step — resolving the sketch and running `views->plan`
;; internally.

(def shortcut-plan (sk/plan trace-sk))

(ss/valid? shortcut-plan)

(kind/test-last [true?])

;; ### Pipeline Summary
;;
;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | sketch | sketch record | N/A (declarative) |
;; | Views | Vector of maps | N/A (declarative) |
;; | Plan | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Pixel space |
;; | Figure | Hiccup vectors | Pixel space |

;; ## The Plan Boundary
;;
;; The plan separates **what** to draw from **how** to
;; draw it. The sketch and view stages describe intent;
;; the membrane and figure stages handle rendering.

^:kindly/hide-code
(kind/mermaid "
graph LR
  subgraph WHAT [\"WHAT — data + semantics\"]
    B[\"sketch\"]
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
;; A sketch can hold multiple methods on the same entry.
;; Here, scatter points and per-species regression lines share
;; the same panel because both `lay-point` and `lay-lm`
;; target the same `:petal_length`/`:petal_width` entry.

(def multi-sk
  (-> data/iris
      (sk/view :petal_length :petal_width {:color :species})
      sk/lay-point
      sk/lay-lm))

;; The sketch has one entry with two global methods:

(count (:entries multi-sk))

(kind/test-last [(fn [n] (= 1 n))])

(mapv :mark (:methods multi-sk))

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :line (second v))))])

;; Resolving produces two views — one per method — both sharing
;; the same columns:

(def multi-views (sketch/resolve-sketch multi-sk))

(count multi-views)

(kind/test-last [(fn [n] (= 2 n))])

(mapv :mark multi-views)

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :line (second v))))])

;; Building a plan with a title and checking the layers:

(def multi-plan
  (sk/plan multi-sk {:title "Iris Petals with Regression"}))

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
    (sk/view :petal_length :petal_width {:color :species})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Iris Petals with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]
  API --> VIEW[\"impl/view.clj\"]
  API --> PLOT[\"impl/plot.clj\"]
  API --> PLAN[\"impl/sketch.clj\"]
  SK --> VIEW
  SK --> PLAN
  SK --> RENDER[\"impl/render.clj\"]
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
  style SK fill:#d1c4e9
  style PLAN fill:#ffe0b2
  style PLOT fill:#bbdefb
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; The `impl/sketch.clj` module is the new addition. It sits between
;; the public API and the plan resolution stage. It holds the `Sketch`
;; record, the `resolve-sketch` function that flattens entries and
;; methods into view maps, and the `render-sketch` function that
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
