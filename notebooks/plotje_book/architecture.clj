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
  X[\"Raw data\"] -->|pj/->pose| B[\"Pose\"]
  B -->|pj/options pj/lay-* ...| B
  B -->|pj/pose->draft| D[\"Draft\"]
  D -->|pj/draft->plan| P[\"Plan\"]
  P -->|pj/plan->membrane| M[\"Membrane\"]
  M -->|pj/membrane->plot| F[\"Plot\"]
  style X fill:#eee,stroke-dasharray:3 3
  style B fill:#d1c4e9
  style D fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; Two terms used throughout: **data space** is values in their
;; original units (centimeters, dollars, dates, species names);
;; **drawing space** is pixel coordinates inside the output canvas.
;; The plan stage holds geometry in data space; the membrane stage
;; holds geometry in drawing space.

;; The five stages:
;;
;; - **Pose** -- the composable specification you write. Built by
;;   `pj/pose`, `pj/lay-*`, `pj/options`, `pj/facet`, `pj/arrange`,
;;   `pj/scale`, and `pj/coord`. Lifted from raw data by `pj/->pose`,
;;   so a dataset can flow through the pipeline without an explicit
;;   constructor call. No computation has happened yet.
;;
;; - **Draft** -- the pose flattened. A `LeafDraft` record holds
;;   `:layers` (a vector of one map per applicable layer with all
;;   scope merged in -- `:data`, `:x`, `:y`, `:mark`, `:stat`, and
;;   aesthetic keys) and `:opts` (the pose-level options that flow
;;   into the plan stage). A composite pose produces a
;;   `CompositeDraft` instead, carrying per-leaf drafts
;;   (`:sub-drafts`), the resolved chrome geometry (`:chrome-spec`),
;;   the layout map from leaf path to rect (`:layout`), and the
;;   composite's overall dimensions (`:width`, `:height`). Produced by
;;   `pj/pose->draft`.
;;
;; - **Plan** -- fully resolved geometry in data space (domains,
;;   ticks, legends, computed shapes). A `Plan` record (composite
;;   plots use `CompositePlan`) holding panels as plain maps, layers
;;   as `PlanLayer` records, and numeric arrays as dtype-next
;;   buffers. Produced by `pj/draft->plan`. No rendering primitives
;;   yet.
;;
;; - **Membrane** -- positioned drawing primitives (Translate,
;;   WithColor, Path, Label, ...) in drawing space, from the
;;   [Membrane](https://github.com/phronmophobic/membrane) library
;;   that underpins this stage. Produced by `pj/plan->membrane`.
;;
;; - **Plot** -- rendered output (SVG hiccup or BufferedImage).
;;   Produced by `pj/membrane->plot`, dispatching on a `:format`
;;   keyword.
;;
;; The composition shortcuts `pj/draft`, `pj/plan`, `pj/membrane`,
;; and `pj/plot` run the chain from a pose up through the named
;; stage. They are introduced one section down.

;; Most users only interact with the pose stage and never need to
;; think about the others. The stages below matter when you are
;; debugging unexpected output, building a custom renderer, or
;; extending the library.

;; ## Why these stages?
;;
;; A simpler library could go from data to pixels in one function.
;; Plotje splits the work into five stages because each stage serves
;; a distinct concern, and each boundary between stages buys
;; something concrete:
;;
;; - The **pose** is what the user composes; the **draft** is the
;;   same specification flattened, with scope merged in. This
;;   boundary lets the layer engine run on a uniform input
;;   regardless of how the pose was built (single layer, faceted
;;   leaf, composite tree).
;; - The **plan** holds geometry in data space -- domains, ticks,
;;   computed shapes -- before any drawing. This boundary lets you
;;   inspect and validate plot structure with Malli, and it lets
;;   multiple renderers share the same computed plan.
;; - The **membrane** holds drawing primitives in drawing space.
;;   This boundary decouples "what to draw, where" from the output
;;   format, so SVG and raster renderers consume the same membrane
;;   tree.
;; - The **plot** is the format-specific output: SVG hiccup, a
;;   BufferedImage, or any other format a backend supports.
;;
;; Two payoffs follow. Every intermediate value can be inspected
;; with `kind/pprint`. And the same pipeline can be extended by
;; registering new methods at any stage -- mark, stat, scale,
;; coordinate system, output format -- without modifying the core.
;; The Extensibility chapter walks each extension point.

;; ## The Atomic Steps
;;
;; Each transition is its own public function. Walk the example
;; below to see what enters and what leaves at each step. The
;; per-function reference (arities, arguments, return types) lives
;; in the [API Reference](./plotje_book.api_reference.html).

;; ### Step 1: pj/->pose
;;
;; Lift raw data (or a pose) to a pose. Polymorphic on input: a
;; dataset becomes a leaf pose with `:data` set; an existing pose
;; flows through unchanged (idempotent). This is what lets every
;; downstream function accept either raw data or a pose.
;;
;; The example we'll trace through every stage: iris petal
;; measurements, with a scatter and per-species regression line.
;; Pose-level `:x`, `:y`, and `:color` mappings; two layers stacked
;; on top of that mapping. Layered enough that the flatten step has
;; visible work to do, small enough to print.

(def trace-pose
  (-> (rdatasets/datasets-iris)
      (pj/pose :petal-length :petal-width {:color :species})
      pj/lay-point
      (pj/lay-smooth {:stat :linear-model})))

;; The pose auto-renders as the plot it specifies:

trace-pose

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; And the underlying value is a plain Clojure map -- `kind/pprint`
;; reveals the structure (without it, the auto-render would re-show
;; the plot):

(kind/pprint trace-pose)

(kind/test-last [(fn [v] (and (pj/pose? v)
                              (= [:petal-length :petal-width :species]
                                 [(:x (:mapping v))
                                  (:y (:mapping v))
                                  (:color (:mapping v))])
                              (= 2 (count (:layers v)))
                              (= [:point :smooth]
                                 (mapv :layer-type (:layers v)))))])

;; ### Step 2: pj/pose->draft
;;
;; Flatten a pose into a draft. For a leaf, returns a `LeafDraft`
;; record carrying the merged layer maps and the pose-level opts.
;; The pose-level mapping (`:x :petal-length`, `:y :petal-width`,
;; `:color :species`) lands inside *each* of the two layer maps,
;; alongside layer-specific keys (`:mark`, `:stat`). The layer
;; engine downstream sees a uniform shape regardless of where each
;; mapping was originally specified. A draft renders awkwardly by
;; default (each layer carries an embedded dataset), so wrap it in
;; `kind/pprint` to inspect. Keys prefixed with double underscores
;; (e.g. `:__panel-idx`) are internal markers; they ride along
;; through the plan stage and follow the Clojure "do not consume"
;; convention.

(def trace-draft
  (pj/pose->draft trace-pose))

(kind/pprint trace-draft)

(kind/test-last [(fn [d] (and (pj/leaf-draft? d)
                              (= 2 (count (:layers d)))
                              (let [layers (:layers d)]
                                (and (= [:point :line] (mapv :mark layers))
                                     (every? #(= :petal-length (:x %)) layers)
                                     (every? #(= :petal-width (:y %)) layers)
                                     (every? #(= :species (:color %)) layers)))
                              (= {} (:opts d))))])

;; ### Step 3: pj/draft->plan
;;
;; Resolve the draft into computed geometry. Reads `:opts` from the
;; draft to apply title, dimensions, axis labels, and so on. The
;; smooth layer's `:linear-model` stat resolves into per-species
;; line segments here; the point layer keeps the raw observations
;; grouped by species. The legend gets one entry per species.

(def trace-plan
  (pj/draft->plan trace-draft))

;; The plan -- a `Plan` record carrying panels, total dimensions,
;; ticks, the legend spec, and per-layer geometry (groups of
;; dtype-next buffers):

(kind/pprint trace-plan)

(kind/test-last [(fn [v] (and (pj/leaf-plan? v)
                              (= 1 (count (:panels v)))
                              (some? (:total-width v))
                              (some? (:total-height v))
                              (= 3 (count (get-in v [:legend :entries])))
                              (let [layers (:layers (first (:panels v)))]
                                (and (= [:point :line] (mapv :mark layers))
                                     (= 3 (count (:groups (first layers))))
                                     (= 3 (count (:groups (second layers))))))))])

;; The plan validates against a Malli schema:

(ss/valid? trace-plan)

(kind/test-last [true?])

;; ### Step 4: pj/plan->membrane
;;
;; Convert the plan into a tree of membrane drawing primitives,
;; positioned in drawing-space coordinates.

(def trace-membrane (pj/plan->membrane trace-plan))

;; The membrane tree -- a vector of `membrane.ui` records, each
;; carrying a position and a drawing primitive:

(kind/pprint trace-membrane)

(kind/test-last [(fn [v] (and (vector? v)
                              (pos? (count v))
                              (every? #(.startsWith (.getName (class %))
                                                    "membrane.ui.")
                                      v)))])

;; ### Step 5: pj/membrane->plot
;;
;; Convert the membrane tree into the rendered output for a chosen
;; format. Dispatches on the format keyword; `:svg` is built in.
;; The membrane vector carries its plan-derived dimensions on
;; metadata, so `pj/membrane->plot` does not need them respelled
;; in opts:

(def trace-plot
  (pj/membrane->plot trace-membrane :svg {}))

(kind/pprint trace-plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; And rendered:

(kind/hiccup trace-plot)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Pipeline Shortcuts: pj/pose, pj/draft, pj/plan, pj/membrane, pj/plot
;;
;; Each pipeline stage has a user-facing convenience that runs the
;; chain from raw input up through that stage:
;;
;; - **pj/pose** -- the asymmetric shortcut. Beyond `pj/->pose`,
;;   it infers mappings from 1-3 column datasets, parses positional
;;   column arguments (e.g. `(pj/pose data :x :y)`), builds
;;   rectangular composites from `pj/cross` pair lists, and extends
;;   or promotes existing poses. Use `pj/->pose` when you only need
;;   to lift raw input to a pose without any of that.
;; - **pj/draft** -- raw input -> draft.
;; - **pj/plan** -- raw input -> plan.
;; - **pj/membrane** -- raw input -> membrane tree.
;; - **pj/plot** -- raw input -> rendered figure.
;;
;; The four stage-after-pose shortcuts (`pj/draft`, `pj/plan`,
;; `pj/membrane`, `pj/plot`) are literal compositions of the atomic
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
;;        draft)))
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
;;        plan)))
;;
;; (defn membrane
;;   ([x]
;;    (let [pose (->pose x)
;;          opts (:opts pose {})]
;;      (-> pose
;;          pose->draft
;;          draft->plan
;;          (plan->membrane opts))))
;;   ([x opts]
;;    (-> x
;;        ->pose
;;        (options opts)
;;        membrane)))
;;
;; (defn plot
;;   ([x]
;;    (let [pose (->pose x)
;;          opts (:opts pose {})
;;          fmt  (or (:format opts) :svg)]
;;      (-> pose
;;          pose->draft
;;          draft->plan
;;          (plan->membrane opts)
;;          (membrane->plot fmt opts))))
;;   ([x opts]
;;    (-> x
;;        ->pose
;;        (options opts)
;;        plot)))
;; ```
;;
;; In `plot`, the `let` lifts the pose, extracts `opts`, and reads
;; `:format` so the rest of the chain has what it needs; the
;; subsequent `->` thread runs the four atomic transitions
;; (`pose->draft`, `draft->plan`, `plan->membrane`, `membrane->plot`)
;; left to right. The plan-derived dimensions and title ride along
;; on the membrane tree as metadata, so `membrane->plot` can read
;; them without needing the plan back.
;;
;; The 2-arity of each function folds the options map into the pose
;; using `pj/options` before recursing into the 1-arity.
;;
;; `pj/membrane` is the analogous shortcut for the membrane stage,
;; useful for consumers that want a membrane tree without choosing
;; an output format yet -- a custom backend, a target Membrane
;; itself supports but Plotje has not wired in yet.
;;
;; Because the compositions actually call the atomic steps, swapping
;; an atomic step (with `with-redefs` for testing, or with a custom
;; `defmethod` for plan->membrane) automatically reaches every
;; user-facing function.

;; The composition holds at runtime:

(let [pose-with-opts (-> trace-pose
                         (pj/options {:title "Iris Petals"
                                      :x-label "Petal length"
                                      :width 700}))
      via-plan (pj/plan pose-with-opts)
      via-arrows (-> pose-with-opts
                     pj/->pose
                     pj/pose->draft
                     pj/draft->plan)]
  {:title-match (= (:title via-plan) (:title via-arrows))
   :x-label-match (= (:x-label via-plan) (:x-label via-arrows))
   :width-match (= (:width via-plan) (:width via-arrows))
   :title (:title via-plan)
   :x-label (:x-label via-plan)
   :width (:width via-plan)})

(kind/test-last [(fn [m] (and (:title-match m)
                              (:x-label-match m)
                              (:width-match m)
                              (= "Iris Petals" (:title m))
                              (= "Petal length" (:x-label m))
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

;; ## Where Inference Happens
;;
;; A pipeline that only shuffled structure would be of little use.
;; Plotje's pipeline is interesting because each atomic step also
;; **infers**: it fills in choices the user did not bother to
;; specify. Inference is what lets `(pj/pose trace-data)` -- the
;; dataset alone, no mapping, no layers, no opts -- still produce
;; a complete plot. Every one-line example in this book is the
;; work of the inference engine baked into the pipeline.

(pj/pose trace-data)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s))
                                (= 2 (count (filter #(.startsWith % "rgb")
                                                    (:colors s)))))))])

;; Where each kind of inference lives:
;;
;; - **Mapping inference** lives in `pj/pose` (1-arity on raw data)
;;   and in `pj/lay-*` (1-arity on raw data) -- not in `pj/->pose`,
;;   which only lifts the data into a bare leaf pose. With 1-3
;;   columns, position is auto-mapped: 1 column to `:x`, 2 columns
;;   to `:x` and `:y`, 3 columns add `:color`.
;; - **Layer-type inference** lives in `pj/draft->plan`. A pose
;;   without an explicit `pj/lay-*` call drafts to a layer with no
;;   `:mark` set; the plan stage detects the missing mark, looks
;;   at the column types, and picks a concrete mark + stat --
;;   categorical x with numerical y produces a boxplot, temporal x
;;   with numerical y produces a time-series line, numerical x and
;;   y produce a scatter, and so on.
;; - **Column-type inference** lives in `pj/draft->plan`.
;;   `:x-type`/`:y-type`/`:color-type` default from the data
;;   (numerical / categorical / temporal); a user-supplied
;;   `:x-type` overrides the default.
;; - **Geometry inference** lives in `pj/draft->plan`. Domains
;;   default to data ranges; ticks default to evenly-spaced
;;   values; legend entries are derived from the layers' aesthetic
;;   mappings; the coordinate system defaults to `:cartesian`.
;;
;; All but mapping inference happens in the plan stage. Mapping
;; inference runs at the front edge -- before any draft exists --
;; so the rest of the pipeline always sees a pose with mappings.
;; The plan stage is where the geometry, types, and layer choice
;; get filled in: the draft is a specification with deliberate
;; gaps, and the plan is the gap-filled, geometry-resolved snapshot
;; ready to render. Every inferred default has an explicit override
;; (`pj/scale`, `pj/coord`, `pj/options`, mapping keys like
;; `:x-type`), so the user can opt out of any single inference
;; without losing the others.

;; ## Composite Poses
;;
;; A composite pose -- one with `:poses` inside --
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

;; `pj/draft` (the composition `->pose ; pose->draft`) returns a
;; `CompositeDraft` -- wrap in `kind/pprint` to inspect:

(-> composite-pose pj/draft kind/pprint)

(kind/test-last [(fn [d] (and (pj/composite-draft? d)
                              (= 2 (count (:sub-drafts d)))))])

;; `pj/plan` returns a `CompositePlan`:

(pj/plan composite-pose)

(kind/test-last [(fn [p] (and (pj/composite-plan? p)
                              (= 2 (count (:sub-plots p)))))])

;; `pj/membrane` returns the vector of drawing primitives -- one
;; `Translate` per leaf plus chrome (column strip labels, shared
;; legend, title if any). Plan-derived dimensions and title travel
;; on the vector as metadata.

(pj/membrane composite-pose)

(kind/test-last [(fn [m] (and (vector? m)
                              (pos? (count m))
                              (let [{:keys [total-width total-height]} (meta m)]
                                (and (number? total-width)
                                     (number? total-height)))))])

;; And `pj/plot` (the full pipeline) returns the SVG hiccup -- the
;; same value `composite-pose` auto-renders to at the top of this
;; section, just produced explicitly:

(kind/pprint (pj/plot composite-pose))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; The composition holds for both leaf and composite poses --
;; `pj/plan` is `(-> pose pj/->pose pj/pose->draft pj/draft->plan)`
;; either way -- because each atomic step dispatches on shape at
;; the bottom of its call.

;; The composite path also does cross-leaf work the leaf path
;; cannot. When the composite carries `{:share-scales #{:x :y}}`,
;; `pj/pose->draft` computes domains across all leaves and injects
;; them into each per-leaf draft before `pj/draft->plan` runs --
;; so the resulting panels share axes. This is why shared-scale
;; resolution belongs to the composite stage and not to per-leaf
;; planning. See the [Composition](./plotje_book.composition.html)
;; chapter for worked examples.

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

;; ## The Membrane Stage
;;
;; The membrane is Plotje's second important boundary: it separates
;; data-space geometry from output-format bytes. Where the plan says
;; "draw a point at data-coordinate (3.4, 7.1) in the color assigned
;; to species `setosa`", the membrane says "translate to drawing
;; coordinate (218, 134) and place a colored shape there." The plan
;; is renderer-agnostic; the membrane is format-agnostic.
;;
;; This second boundary gives Plotje its **graphical modularity**:
;; one membrane tree can be rendered to many output formats. The
;; pose, draft, plan, and membrane stages are reused unchanged
;; across formats. A new format that consumes the membrane tree
;; registers a `defmethod membrane->plot :foo` (the dispatch step
;; `pj/plot` and `pj/membrane->plot` walk through). A new format
;; that goes from a plan straight to bytes (skipping membrane
;; entirely -- e.g., a Plotly-spec target) registers a
;; `defmethod plan->plot :foo` instead.
;;
;; The membrane stage of Plotje is built on
;; [Membrane](https://github.com/phronmophobic/membrane) -- the
;; library that defines the primitive types Plotje uses
;; (`Translate`, `WithColor`, `Path`, `Label`, `RoundedRectangle`,
;; ...) and provides the rendering backends. Plotje constructs a
;; membrane tree from a plan; Membrane renders it.
;;
;; Backends Plotje wires into Membrane today:
;;
;; - **SVG hiccup** -- the default. Renders in browsers, in
;;   notebooks via Kindly/Clay, and writes to `.svg` files.
;; - **Java2D / `BufferedImage`** -- raster output via
;;   Membrane's Java2D backend. Used for `.png` files and any
;;   consumer that wants a Java image.
;;
;; Membrane itself supports more rendering targets (terminal,
;; native GUI, GL, ...) than Plotje currently exposes. Wiring a new
;; target into Plotje has not been done end-to-end yet -- the
;; defmethod registration is the architectural plug, but each
;; backend has its own conventions for opts and interactivity that
;; need to be worked out. The promise of this boundary is that as
;; Membrane grows, Plotje can grow with it without rethinking how
;; plots are described.

;; ## Pipeline Summary

;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Pose | Plain map (leaf or composite) | N/A (declarative) |
;; | Draft | `LeafDraft` (`:layers`, `:opts`) or `CompositeDraft` (`:sub-drafts`, `:chrome-spec`, `:layout`, `:width`, `:height`) record | N/A (declarative) |
;; | Plan | `Plan` or `CompositePlan` record (with `PlanLayer` records and dtype buffers) | Data space |
;; | Membrane | Record tree (membrane.ui primitives in a vector) | Drawing units |
;; | Plot | Hiccup vector (`:svg`) or `BufferedImage` (`:bufimg`) | Drawing units |

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
;;
;; `impl/compositor.clj` handles composite chrome layout,
;; `composite-pose->draft`, and `composite-draft->plan` -- pure
;; data-side, no membrane dependency.
;;
;; `impl/plan.clj` holds the leaf-plan computation (domains, ticks,
;; legends, layout) that the public `pj/draft->plan` calls.
;;
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
