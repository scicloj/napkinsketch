;; # Architecture
;;
;; Plotje has a five-stage pipeline: **pose** -> **draft** -> **plan**
;; -> **membrane** -> **plot**. Each stage is produced from the
;; previous one by a single atomic step. The user-facing functions
;; `pj/draft`, `pj/plan`, and `pj/plot` are literal compositions of
;; those atomic steps, with `pj/options` folded in to inject
;; pose-level options. Building the API as composition makes each
;; intermediate value inspectable, each transition independently
;; testable, and the pipeline as a whole transparent.
;;
;; This chapter introduces the atomic steps, walks a small example
;; through every stage, shows the user-facing functions as
;; compositions, and explains how composite poses traverse the same
;; pipeline through internal shape dispatch.

(ns plotje-book.architecture
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Malli schema validation
   [scicloj.plotje.impl.plan-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  B[\"Pose\"] -->|pj/pose->draft| D[\"Draft\"]
  D -->|pj/draft->plan| P[\"Plan\"]
  P -->|pj/plan->membrane| M[\"Membrane\"]
  M -->|pj/membrane->plot| F[\"Plot\"]
  style B fill:#d1c4e9
  style D fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; The five stages, and what each atomic step adds:
;;
;; - **Pose** -- the composable specification you write. Built by
;;   `pj/pose`, `pj/lay-*`, `pj/options`, `pj/facet`, `pj/arrange`,
;;   `pj/scale`, and `pj/coord`. No computation has happened yet.
;;   `pj/->pose` lifts raw data into an empty leaf pose, so a dataset
;;   can flow through the pipeline without an explicit constructor
;;   call.
;;
;; - **Draft** -- the pose flattened. A `LeafDraft` record holds
;;   `:layers` (a vector of one map per applicable layer with all
;;   scope merged in -- `:data`, `:x`, `:y`, `:mark`, `:stat`, and
;;   aesthetic keys) and `:opts` (the pose-level options that flow
;;   into the plan stage). A composite pose produces a
;;   `CompositeDraft` instead, carrying per-leaf drafts plus chrome
;;   geometry. Produced by `pj/pose->draft`.
;;
;; - **Plan** -- fully resolved geometry in data space (domains,
;;   ticks, legends, computed shapes). A `Plan` record (composite
;;   plots use `CompositePlan`) holding panels as plain maps, layers
;;   as `PlanLayer` records, and numeric arrays as dtype-next
;;   buffers. Produced by `pj/draft->plan`. No rendering primitives
;;   yet.
;;
;; - **Membrane** -- positioned drawing primitives (Translate,
;;   WithColor, Path, Label, ...) in drawing space. Produced by
;;   `pj/plan->membrane`.
;;
;; - **Plot** -- rendered output (SVG hiccup or BufferedImage).
;;   Produced by `pj/membrane->plot`, dispatching on a `:format`
;;   keyword.

;; Most users only interact with the pose stage and never need to
;; think about the others. The stages below matter when you are
;; debugging unexpected output, building a custom renderer, or
;; extending the library.

;; ## The Atomic Steps
;;
;; Each transition is its own public function. Walk the example
;; below to see what enters and what leaves at each step.

(def trace-data
  {:x [1 2 3 4 5]
   :y [2 4 3 5 4]
   :g [:a :a :b :b :b]})

;; ### Step 1: pj/->pose
;;
;; Lift raw data (or a pose) to a pose. Polymorphic on input: a
;; dataset becomes a leaf pose with `:data` set; an existing pose
;; flows through unchanged (idempotent). This is what lets every
;; downstream function accept either raw data or a pose.

(def trace-pose
  (-> trace-data
      pj/->pose
      (pj/lay-point :x :y {:color :g})))

;; The pose auto-renders as the plot it specifies:

trace-pose

(kind/test-last [(fn [v] (= 5 (:points (pj/svg-summary v))))])

;; And the underlying value is a plain Clojure map -- `kind/pprint`
;; reveals the structure (without it, the auto-render would re-show
;; the plot):

(kind/pprint trace-pose)

(kind/test-last [(fn [v] (and (pj/pose? v)
                              (= [:x :y] [(:x (:mapping v)) (:y (:mapping v))])
                              (= 1 (count (:layers v)))
                              (= :point (:layer-type (first (:layers v))))
                              (= :g (:color (:mapping (first (:layers v)))))))])

;; ### Step 2: pj/pose->draft
;;
;; Flatten a pose into a draft. For a leaf, returns a `LeafDraft`
;; record carrying the merged layer maps and the pose-level opts.
;; A draft renders awkwardly by default (each layer carries an
;; embedded dataset), so wrap it in `kind/pprint` to inspect:

(def trace-draft
  (pj/pose->draft trace-pose))

(kind/pprint trace-draft)

(kind/test-last [(fn [d] (and (pj/leaf-draft? d)
                              (= 1 (count (:layers d)))
                              (let [l (first (:layers d))]
                                (and (= :x (:x l))
                                     (= :y (:y l))
                                     (= :point (:mark l))
                                     (= :g (:color l))))
                              (= {} (:opts d))))])

;; ### Step 3: pj/draft->plan
;;
;; Resolve the draft into computed geometry. Reads `:opts` from the
;; draft to apply title, dimensions, axis labels, and so on.

(def trace-plan
  (pj/draft->plan trace-draft))

trace-plan

(kind/test-last [(fn [v] (and (pj/leaf-plan? v)
                              (= 1 (count (:panels v)))
                              (some? (:total-width v))
                              (some? (:total-height v))
                              (some? (:legend v))))])

;; The plan validates against a Malli schema:

(ss/valid? trace-plan)

(kind/test-last [true?])

;; ### Step 4: pj/plan->membrane
;;
;; Convert the plan into a tree of membrane drawing primitives,
;; positioned in drawing-space coordinates.

(def trace-membrane (pj/plan->membrane trace-plan))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v)
                              (pos? (count v))
                              (every? #(.startsWith (.getName (class %))
                                                    "membrane.ui.")
                                      v)))])

;; ### Step 5: pj/membrane->plot
;;
;; Convert the membrane tree into the rendered output for a chosen
;; format. Dispatches on the format keyword; `:svg` is built in.

(def trace-plot
  (pj/membrane->plot trace-membrane :svg
                     {:total-width (:total-width trace-plan)
                      :total-height (:total-height trace-plan)}))

(kind/pprint trace-plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; And rendered:

(kind/hiccup trace-plot)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Compositions: pj/draft, pj/plan, pj/plot
;;
;; The user-facing functions are literal compositions of the atomic
;; steps. Reading the source spells out the pipeline:
;;
;; Pseudocode:
;; ```clojure
;; (defn draft
;;   ([x]
;;    (-> x
;;        ->pose
;;        pose->draft))
;;   ([x opts]
;;    (-> x
;;        ->pose
;;        (options opts)
;;        pose->draft)))
;;
;; (defn plan
;;   ([x]
;;    (-> x
;;        ->pose
;;        pose->draft
;;        draft->plan))
;;   ([x opts]
;;    (-> x
;;        ->pose
;;        (options opts)
;;        pose->draft
;;        draft->plan)))
;;
;; (defn plot
;;   ([x]
;;    (-> x
;;        ->pose
;;        pose->draft
;;        draft->plan
;;        (plan->plot fmt opts)))
;;   ([x opts]
;;    (-> x
;;        ->pose
;;        (options opts)
;;        pose->draft
;;        draft->plan
;;        (plan->plot fmt opts))))
;; ```
;;
;; The 2-arity folds the options map into the pose using
;; `pj/options` before dispatch. The transformation is then a clean
;; left-to-right pipeline in which each step's output is the next
;; step's input.
;;
;; Because the compositions actually call the atomic steps, swapping
;; an atomic step (with `with-redefs` for testing, or with a custom
;; `defmethod` for plan->membrane) automatically reaches every
;; user-facing function.

;; The composition holds at runtime:

(let [pose-with-opts (-> trace-data
                         (pj/lay-point :x :y {:color :g})
                         (pj/options {:title "trace" :x-label "X" :width 700}))
      via-plan (pj/plan pose-with-opts)
      via-arrows (-> pose-with-opts pj/pose->draft pj/draft->plan)]
  {:title-match (= (:title via-plan) (:title via-arrows))
   :x-label-match (= (:x-label via-plan) (:x-label via-arrows))
   :width-match (= (:width via-plan) (:width via-arrows))
   :title (:title via-plan)
   :x-label (:x-label via-plan)
   :width (:width via-plan)})

(kind/test-last [(fn [m] (and (:title-match m)
                              (:x-label-match m)
                              (:width-match m)
                              (= "trace" (:title m))
                              (= "X" (:x-label m))
                              (= 700 (:width m))))])

;; Plot-level options (title, x-label, width, ...) live on the
;; pose's `:opts`, ride along on the `LeafDraft`'s `:opts`, and are
;; consumed by `pj/draft->plan`. Going around the user-facing
;; convenience and using the atomic steps directly produces the
;; identical plan.
;;
;; The same property gives you free inspectability: stop the chain
;; at any stage to see what's there. `(-> data ... pj/pose->draft
;; kind/pprint)` shows the draft; `(-> data ... pj/pose->draft
;; pj/draft->plan)` shows the plan; and so on.

;; ## Composite Poses
;;
;; A composite pose -- one with `:poses` --
;; flows through the same atomic steps. Each step dispatches
;; internally on shape: a leaf pose produces a `LeafDraft`; a
;; composite pose produces a `CompositeDraft`. The user-facing
;; pipeline is unchanged.

(def composite-pose
  (-> (rdatasets/datasets-iris)
      (pj/pose [[:petal-length :petal-width]
                [:sepal-length :sepal-width]]
               {:color :species})
      pj/lay-point))

composite-pose

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; `pj/pose->draft` returns a `CompositeDraft` -- wrap in
;; `kind/pprint` to inspect:

(-> composite-pose pj/pose->draft kind/pprint)

(kind/test-last [(fn [d] (and (pj/composite-draft? d)
                              (= 2 (count (:sub-drafts d)))))])

;; And `pj/draft->plan` on a `CompositeDraft` returns a
;; `CompositePlan`:

(-> composite-pose pj/pose->draft pj/draft->plan)

(kind/test-last [(fn [p] (and (pj/composite-plan? p)
                              (= 2 (count (:sub-plots p)))))])

;; The composition `pj/plan = pj/->pose ; pj/pose->draft ;
;; pj/draft->plan` holds for both leaf and composite poses; the
;; shape dispatch happens at the bottom of each atomic step.

;; ## Pipeline Summary

;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Pose | Plain map (leaf or composite) | N/A (declarative) |
;; | Draft | `LeafDraft` or `CompositeDraft` record | N/A (declarative) |
;; | Plan | `Plan` or `CompositePlan` record (with `PlanLayer` records and dtype buffers) | Data space |
;; | Membrane | Record tree (membrane.ui primitives in a vector) | Drawing units |
;; | Plot | Hiccup vector (`:svg`) or `BufferedImage` (`:bufimg`) | Drawing units |

;; ## The Plan Boundary
;;
;; The plan is the boundary between description and rendering. The
;; pose and draft stages assemble the description. The plan resolves
;; it into computed geometry, domains, ticks, and legend -- still as
;; inspectable data, before any layout. The membrane and plot stages
;; then produce the rendered output.

^:kindly/hide-code
(kind/mermaid "
graph LR
  A[\"Pose + draft\"] -->|plan| P[\"Plan\"]
  P --> R[\"membrane + plot\"]
  style A fill:#e8f5e9
  style P fill:#fff3e0
  style R fill:#e3f2fd
")

;; The plan is inspectable as data -- `Plan` and `PlanLayer` records
;; (which behave as maps), plain maps, numbers, strings, keywords,
;; and dtype-next buffers for numeric arrays. It validates against a
;; Malli schema.
;;
;; This separation enables:
;;
;; - Inspecting the plan without rendering
;;
;; - Validating plot structure with Malli
;;
;; - Adding alternate backends that consume plans (SVG and raster
;;   are implemented today)

;; ## Multi-Layer Example
;;
;; A pose can hold multiple layers that share one mapping. Here,
;; scatter points and per-species regression lines share the same
;; panel because both `lay-point` and `lay-smooth` target the same
;; `:petal-length`/`:petal-width` mapping.

(def multi-pose
  (-> (rdatasets/datasets-iris)
      (pj/pose :petal-length :petal-width {:color :species})
      pj/lay-point
      (pj/lay-smooth {:stat :linear-model})))

multi-pose

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; The draft has two layer maps -- one per applicable layer -- both
;; sharing the same `:petal-length`/`:petal-width` mapping. The
;; `:point` layer keeps its mark; the `:smooth` layer-type expands
;; to `:line` after stat resolution:

(def multi-draft (pj/pose->draft multi-pose))

(kind/pprint multi-draft)

(kind/test-last [(fn [d] (and (pj/leaf-draft? d)
                              (= 2 (count (:layers d)))
                              (= [:point :line] (mapv :mark (:layers d)))))])

;; The plan resolves the smooth layer into per-species regression
;; geometry: three groups of computed line geometry, plus the
;; original three groups of point data:

(def multi-plan
  (pj/plan multi-pose {:title "Iris Petals with Regression"}))

multi-plan

(kind/test-last [(fn [m] (and (= "Iris Petals with Regression" (:title m))
                              (= 3 (count (get-in m [:legend :entries])))
                              (let [layers (:layers (first (:panels m)))]
                                (and (= [:point :line] (mapv :mark layers))
                                     (= 3 (count (:groups (first layers))))
                                     (= 3 (count (:groups (second layers))))))))])

;; And the rendered result with the title:

(pj/options multi-pose {:title "Iris Petals with Regression"})

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> POSE[\"impl/pose.clj\"]
  API --> RES[\"impl/resolve.clj\"]
  API --> PL[\"impl/plan.clj\"]
  API --> COMP[\"impl/compositor.clj\"]
  POSE --> RES
  COMP --> POSE
  COMP --> PL
  PL --> RES
  PL --> STAT[\"impl/stat.clj\"]
  PL --> SCALE[\"impl/scale.clj\"]
  PL --> DEFAULTS[\"impl/defaults.clj\"]
  PL --> PS[\"impl/plan_schema.clj\"]
  API --> RENDER[\"impl/render.clj\"]
  RENDER --> SVG[\"render/svg.clj\"]
  SVG --> MEMBRANE[\"render/membrane.clj\"]
  MEMBRANE --> PANEL[\"render/panel.clj\"]
  PANEL --> MARK[\"render/mark.clj\"]
  PANEL --> SCALE
  PANEL --> COORD[\"impl/coord.clj\"]
  API --> RC[\"render/composite.clj\"]
  RC --> MEMBRANE
  style API fill:#c8e6c9
  style COMP fill:#d1c4e9
  style PL fill:#d1c4e9
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
  style RC fill:#f8bbd0
")

;; `impl/pose.clj` holds the pose substrate: `resolve-tree` (merges
;; mappings/data/options down from root to every leaf), `leaf->draft`
;; (the leaf-pose flattening that the public `pj/pose->draft` calls),
;; and the multi-pair / grid composite utilities.
;; `impl/compositor.clj` handles composite chrome layout,
;; `composite-pose->draft`, and `composite-draft->plan` -- pure
;; data-side, no membrane dependency.
;; `impl/plan.clj` holds the leaf-plan computation (domains, ticks,
;; legends, layout) that the public `pj/draft->plan` calls.
;; `impl/resolve.clj` defines the `Plan`, `CompositePlan`,
;; `LeafDraft`, `CompositeDraft`, `PlanLayer`, and `LayerType`
;; records, and holds `resolve-draft-layer` (single draft layer
;; resolution, column type inference, grouping).
;;
;; The `impl/` directory is pure data with no membrane dependency.
;; The `render/` directory uses membrane for layout and SVG/raster
;; conversion. `render/composite.clj` carries the composite
;; `plan->membrane` defmethod and the membrane drawables for
;; composite chrome (title, strip labels, shared legend).

;; ## Dependencies
;;
;; Plotje builds on several excellent Clojure libraries:
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

;; ## What's Next
;;
;; - [**Exploring Plans**](./plotje_book.exploring_plans.html) -- a hands-on tour of the plan stage, building intuition for the data shape that the pipeline produces
;; - [**Extensibility**](./plotje_book.extensibility.html) -- add custom marks, stats, scales, coordinate systems, and output formats by extending the multimethods at each pipeline stage
