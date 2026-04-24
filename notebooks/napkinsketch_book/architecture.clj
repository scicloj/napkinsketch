;; # Architecture
;;
;; Napkinsketch has a five-stage pipeline. You compose a frame --
;; a declarative description of data, mappings, and layers --
;; that flattens into a draft automatically.
;;
;; This notebook traces a small example through every stage,
;; explains the plan boundary, and shows the namespace structure.
;;
;; **Note on internal types.** The pipeline trace below reaches into
;; `impl/` namespaces to show fields like `:views` and the Sketch
;; record. These are the internal shape used today; the frame
;; substrate (plain maps) is the unified spec type that Phase 6
;; will promote to the single internal type. The user-facing API
;; already uses "frame" vocabulary uniformly.

(ns napkinsketch-book.architecture
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Sketch internals -- record and draft
   [scicloj.napkinsketch.impl.sketch :as sketch-impl]
   ;; Plan pipeline -- draft->plan, domains, ticks, legends, layout
   [scicloj.napkinsketch.impl.plan :as plan-impl]
   ;; Malli schema validation
   [scicloj.napkinsketch.impl.plan-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  B[\"Frame<br/>(composable API)\"] -->|frame->draft| D[\"Draft<br/>(flat maps)\"]
  D -->|draft->plan| P[\"Plan<br/>(data-space)\"]
  P -->|scales + coords| M[\"Membrane<br/>(drawing primitives)\"]
  M -->|tree walk| F[\"Plot<br/>(output)\"]
  style B fill:#d1c4e9
  style D fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **Frame** -- the composable user API. Functions like
;;   `sk/frame`, `sk/lay-point`, `sk/lay-rule-h`, `sk/options`,
;;   `sk/facet`, `sk/arrange`, `sk/scale`, and `sk/coord` build up a
;;   frame. No computation has happened yet.
;;
;; - **Draft** -- a flat vector of maps produced by `sk/draft`.
;;   Each map has `:data`, `:x`, `:y`, `:mark`, `:stat`, and aesthetic
;;   keys -- one per leaf-and-layer combination, with all scope merged.
;;
;; - **Plan** -- fully resolved plan. Data-space geometry, domains, tick info,
;;   legend. Plain Clojure maps and dtype-next buffers. No rendering primitives.
;;
;; - **Membrane** -- positioned drawing primitives
;;   (Translate, WithColor, Path, Label, etc.).
;;
;; - **Plot** -- final output. A tree walk converts membrane records
;;   to SVG hiccup, which Clay/Kindly renders in notebooks.

;; Most users only interact with the frame stage and never need to
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

;; ### Frame
;;
;; The user composes a frame by threading data through
;; composable functions. The frame records what to plot
;; without doing any computation.

(def trace-sk
  (-> trace-data
      (sk/lay-point :x :y {:color :g})))

;; Internally today, threading `sk/lay-*` produces a Sketch record
;; (the adapter in place during the pre-alpha refactor). Phase 6
;; retires this record in favour of the plain-map frame; the public
;; API and pipeline semantics do not change. The fields below are
;; what you see while inspecting the threaded value today:
;;
;; - `:data` -- the dataset (coerced to Tablecloth)
;;
;; - `:mapping` -- mappings inherited by all layers
;;
;; - `:views` -- leaf descriptions, each with `:mapping` and optional `:layers`
;;
;; - `:layers` -- frame-level layers (from bare `lay-*` without columns)
;;
;; - `:opts` -- plot-level options (title, width, etc.)

(sketch-impl/sketch? trace-sk)

(kind/test-last [true?])

;; Let's look at the leaves and layers inside the frame:

(count (:views trace-sk))

(kind/test-last [(fn [n] (= 1 n))])

(:views trace-sk)

(kind/test-last [(fn [views]
                   (let [v (first views)]
                     (and (= :x (get-in v [:mapping :x]))
                          (= :y (get-in v [:mapping :y]))
                          (= 1 (count (:layers v))))))])

;; The view has one layer -- the point layer -- attached directly
;; because `lay-point` was called with columns.

(get-in (:views trace-sk) [0 :layers 0 :layer-type])

(kind/test-last [(fn [m] (= :point m))])

;; ### Draft
;;
;; `sk/draft` flattens the frame into a vector of
;; maps. Each map merges frame-level mappings, leaf mappings,
;; and layer details into one flat map with `:data`, `:x`, `:y`, `:mark`, etc.

(def trace-draft
  (sk/draft trace-sk))

(count trace-draft)

(kind/test-last [(fn [n] (= 1 n))])

(select-keys (first trace-draft) [:x :y :mark :color])

(kind/test-last [(fn [m] (and (= :x (:x m))
                              (= :y (:y m))
                              (= :point (:mark m))
                              (= :g (:color m))))])

;; ### Plan
;;
;; `draft->plan` converts the draft into a plan -- a pure-data map
;; with data-space geometry, resolved colors, computed domains, and tick info.
;; The values are still in data space.

(def trace-plan
  (plan-impl/draft->plan trace-draft {}))

trace-plan

(kind/test-last [(fn [v] (and (map? v) (contains? v :panels)))])

;; The plan validates against a Malli schema:

(ss/valid? trace-plan)

(kind/test-last [true?])

;; ### Membrane
;;
;; `plan->membrane` converts the plan into a tree of membrane
;; drawing primitives laid out for the rendered plot.

(def trace-membrane (sk/plan->membrane trace-plan))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v) (pos? (count v))))])

;; ### Plot
;;
;; `membrane->plot` converts the membrane tree into SVG hiccup.

(def trace-plot
  (sk/membrane->plot trace-membrane :svg
                     {:total-width (:total-width trace-plan)
                      :total-height (:total-height trace-plan)}))

(kind/pprint trace-plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; And this is what it looks like when rendered:

(kind/hiccup trace-plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ### Shortcut: Frame to Plan
;;
;; In practice, `sk/plan` does the frame-to-plan conversion
;; in one step -- computing the draft and running `draft->plan`
;; internally.

(def shortcut-plan (sk/plan trace-sk))

(ss/valid? shortcut-plan)

(kind/test-last [true?])

;; ### Pipeline Summary
;;
;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Frame | Plain map (leaf or composite) or Sketch record | N/A (declarative) |
;; | Draft | Vector of maps | N/A (declarative) |
;; | Plan | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Drawing units |
;; | Plot | Hiccup vectors | Drawing units |

;; ## The Plan Boundary
;;
;; The plan is the boundary between description and rendering. The
;; frame and draft stages assemble the description. The plan resolves
;; it into computed geometry, domains, ticks, and legend -- still as
;; plain data, before any layout. The membrane and plot stages
;; then produce the rendered output.

^:kindly/hide-code
(kind/mermaid "
graph LR
  A[\"Frame + draft\"] -->|plan| P[\"Plan\"]
  P --> R[\"membrane + plot\"]
  style A fill:#e8f5e9
  style P fill:#fff3e0
  style R fill:#e3f2fd
")

;; The plan is plain inspectable data -- maps, numbers, strings,
;; keywords, and dtype-next buffers for numeric arrays. It validates
;; against a Malli schema.
;;
;; This separation enables:
;;
;; - Inspecting the plan without rendering
;;
;; - Validating plot structure with Malli
;;
;; - Adding alternate backends that consume plans (SVG and raster are implemented today)

;; ## Multi-Layer Example
;;
;; A frame can hold multiple layers that share one mapping.
;; Here, scatter points and per-species regression lines share
;; the same panel because both `lay-point` and `lay-smooth`
;; target the same `:petal-length`/`:petal-width` mapping.

(def multi-sk
  (-> (rdatasets/datasets-iris)
      (sk/frame :petal-length :petal-width {:color :species})
      sk/lay-point
      (sk/lay-smooth {:stat :linear-model})))

;; The frame has one leaf with two frame-level layers:

(count (:layers multi-sk))

(kind/test-last [(fn [n] (= 2 n))])

(mapv :layer-type (:layers multi-sk))

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :smooth (second v))))])

;; The draft produces two maps -- one per layer -- both sharing
;; the same columns:

(def multi-draft (sk/draft multi-sk))

(count multi-draft)

(kind/test-last [(fn [n] (= 2 n))])

(mapv :mark multi-draft)

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

(-> (rdatasets/datasets-iris)
    (sk/frame :petal-length :petal-width {:color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Iris Petals with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> SK[\"impl/sketch.clj\"]
  API --> RES[\"impl/resolve.clj\"]
  API --> PL[\"impl/plan.clj\"]
  SK --> RES
  SK --> PL
  PL --> RES
  PL --> STAT[\"impl/stat.clj\"]
  PL --> SCALE[\"impl/scale.clj\"]
  PL --> DEFAULTS[\"impl/defaults.clj\"]
  PL --> SS[\"impl/sketch_schema.clj\"]
  SK --> RENDER[\"impl/render.clj\"]
  RENDER --> SVG[\"render/svg.clj\"]
  SVG --> MEMBRANE[\"render/membrane.clj\"]
  MEMBRANE --> PANEL[\"render/panel.clj\"]
  PANEL --> MARK[\"render/mark.clj\"]
  PANEL --> SCALE
  PANEL --> COORD[\"impl/coord.clj\"]
  style API fill:#c8e6c9
  style SK fill:#d1c4e9
  style PL fill:#d1c4e9
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; `impl/sketch.clj` holds the `Sketch` record, `sketch->draft`
;; (flattens views and layers into draft maps), and `render-sketch`
;; (drives the full pipeline for auto-rendering in notebooks).
;; `impl/plan.clj` holds `draft->plan` (domains, ticks, legends, layout).
;; `impl/resolve.clj` holds `resolve-draft-layer` (single draft layer resolution,
;; column type inference, grouping).
;;
;; The `impl/` directory is pure data -- no membrane dependency.
;; The `render/` directory uses membrane for layout and SVG conversion.

;; ## Dependencies
;;
;; Napkinsketch builds on several excellent Clojure libraries:
;;
;; - [Tablecloth](https://scicloj.github.io/tablecloth/) &
;;   [dtype-next](https://github.com/cnuernber/dtype-next) --
;;   dataset manipulation and high-performance numeric arrays
;;
;; - [Membrane](https://github.com/phronmophobic/membrane) --
;;   rendering and layout
;;
;; - [Wadogo](https://github.com/scicloj/wadogo) -- scales
;;
;; - [Clojure2d](https://github.com/Clojure2D/clojure2d) --
;;   color palettes and gradients
;;
;; - [Fastmath](https://github.com/generateme/fastmath) -- statistics
;;
;; - [Malli](https://github.com/metosin/malli) -- schema validation
;;
;; - [Kindly](https://scicloj.github.io/kindly/) &
;;   [Clay](https://scicloj.github.io/clay/) -- notebook rendering
