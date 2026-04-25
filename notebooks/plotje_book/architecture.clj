;; # Architecture
;;
;; Plotje has a five-stage pipeline. You compose a pose --
;; a declarative description of data, mappings, and layers --
;; that flattens into a draft automatically.
;;
;; This notebook traces a small example through every stage,
;; explains the plan boundary, and shows the namespace structure.

(ns plotje-book.architecture
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Pose substrate -- leaf->draft, resolve-tree
   [scicloj.plotje.impl.pose :as pose-impl]
   ;; Plan pipeline -- draft->plan, domains, ticks, legends, layout
   [scicloj.plotje.impl.plan :as plan-impl]
   ;; Malli schema validation
   [scicloj.plotje.impl.plan-schema :as ss]))

;; ## Pipeline Overview

^:kindly/hide-code
(kind/mermaid "
graph LR
  B[\"Pose<br/>(composable API)\"] -->|pose->draft| D[\"Draft<br/>(flat maps)\"]
  D -->|draft->plan| P[\"Plan<br/>(data-space)\"]
  P -->|scales + coords| M[\"Membrane<br/>(drawing primitives)\"]
  M -->|tree walk| F[\"Plot<br/>(output)\"]
  style B fill:#d1c4e9
  style D fill:#e8f5e9
  style P fill:#fff3e0
  style M fill:#e3f2fd
  style F fill:#fce4ec
")

;; - **Pose** -- the composable user API. Functions like
;;   `pj/pose`, `pj/lay-point`, `pj/lay-rule-h`, `pj/options`,
;;   `pj/facet`, `pj/arrange`, `pj/scale`, and `pj/coord` build up a
;;   pose. No computation has happened yet.
;;
;; - **Draft** -- a flat vector of maps produced by `pj/draft`.
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

;; Most users only interact with the pose stage and never need to
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

;; ### Pose
;;
;; The user composes a pose by threading data through
;; composable functions. The pose records what to plot
;; without doing any computation.

(def trace-pose
  (-> trace-data
      (pj/lay-point :x :y {:color :g})))

;; A pose is a plain Clojure map. The fields below are what you see
;; while inspecting the threaded value:
;;
;; - `:data` -- the dataset (coerced to Tablecloth)
;;
;; - `:mapping` -- mappings that flow into every layer on this pose
;;
;; - `:layers` -- layers placed on this pose
;;
;; - `:poses` -- sub-poses; a leaf has none
;;
;; - `:opts` -- plot-level options (title, width, etc.)

(pj/pose? trace-pose)

(kind/test-last [true?])

;; Because a leaf pose has no sub-poses, this is a leaf:

(pose-impl/leaf? trace-pose)

(kind/test-last [true?])

;; The mapping carries the position aesthetics (from the positional
;; :x / :y arguments); the color aesthetic (from the opts map) rides
;; on the layer so a subsequent `pj/lay-*` with different opts does
;; not disturb it:

(:mapping trace-pose)

(kind/test-last [(fn [m] (and (= :x (:x m))
                              (= :y (:y m))))])

(get-in trace-pose [:layers 0 :layer-type])

(kind/test-last [(fn [m] (= :point m))])

;; The :color mapping lives on the layer's own :mapping:

(get-in trace-pose [:layers 0 :mapping :color])

(kind/test-last [(fn [m] (= :g m))])

;; ### Draft
;;
;; `pj/draft` flattens the pose into a vector of
;; maps. Each map merges pose-level mappings, leaf mappings,
;; and layer details into one flat map with `:data`, `:x`, `:y`, `:mark`, etc.

(def trace-draft
  (pj/draft trace-pose))

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

(def trace-membrane (pj/plan->membrane trace-plan))

trace-membrane

(kind/test-last [(fn [v] (and (vector? v) (pos? (count v))))])

;; ### Plot
;;
;; `membrane->plot` converts the membrane tree into SVG hiccup.

(def trace-plot
  (pj/membrane->plot trace-membrane :svg
                     {:total-width (:total-width trace-plan)
                      :total-height (:total-height trace-plan)}))

(kind/pprint trace-plot)

(kind/test-last [(fn [v] (and (vector? v) (= :svg (first v))))])

;; And this is what it looks like when rendered:

(kind/hiccup trace-plot)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ### Shortcut: Pose to Plan
;;
;; In practice, `pj/plan` does the pose-to-plan conversion
;; in one step -- computing the draft and running `draft->plan`
;; internally.

(def shortcut-plan (pj/plan trace-pose))

(ss/valid? shortcut-plan)

(kind/test-last [true?])

;; ### Pipeline Summary
;;
;; | Stage | Type | Coordinates |
;; |:------|:-----|:------------|
;; | Pose | Plain map (leaf or composite) | N/A (declarative) |
;; | Draft | Vector of maps | N/A (declarative) |
;; | Plan | Clojure maps + dtype buffers | Data space |
;; | Membrane | Record tree | Drawing units |
;; | Plot | Hiccup vectors | Drawing units |

;; ## The Plan Boundary
;;
;; The plan is the boundary between description and rendering. The
;; pose and draft stages assemble the description. The plan resolves
;; it into computed geometry, domains, ticks, and legend -- still as
;; plain data, before any layout. The membrane and plot stages
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
;; A pose can hold multiple layers that share one mapping.
;; Here, scatter points and per-species regression lines share
;; the same panel because both `lay-point` and `lay-smooth`
;; target the same `:petal-length`/`:petal-width` mapping.

(def multi-sk
  (-> (rdatasets/datasets-iris)
      (pj/pose :petal-length :petal-width {:color :species})
      pj/lay-point
      (pj/lay-smooth {:stat :linear-model})))

;; The pose has one leaf with two pose-level layers:

(count (:layers multi-sk))

(kind/test-last [(fn [n] (= 2 n))])

(mapv :layer-type (:layers multi-sk))

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :smooth (second v))))])

;; The draft produces two maps -- one per layer -- both sharing
;; the same columns:

(def multi-draft (pj/draft multi-sk))

(count multi-draft)

(kind/test-last [(fn [n] (= 2 n))])

(mapv :mark multi-draft)

(kind/test-last [(fn [v] (and (= :point (first v))
                              (= :line (second v))))])

;; Building a plan with a title and checking the layers:

(def multi-plan
  (pj/plan multi-sk {:title "Iris Petals with Regression"}))

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
    (pj/pose :petal-length :petal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model})
    (pj/options {:title "Iris Petals with Regression"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Namespace Structure

^:kindly/hide-code
(kind/mermaid "
graph TD
  API[\"api.clj\"] --> FR[\"impl/pose.clj\"]
  API --> RES[\"impl/resolve.clj\"]
  API --> PL[\"impl/plan.clj\"]
  API --> COMP[\"impl/compositor.clj\"]
  FR --> RES
  COMP --> FR
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
  style API fill:#c8e6c9
  style FR fill:#d1c4e9
  style COMP fill:#d1c4e9
  style PL fill:#d1c4e9
  style SVG fill:#f8bbd0
  style MEMBRANE fill:#f8bbd0
")

;; `impl/pose.clj` holds the pose substrate: `resolve-tree` (merges
;; mappings/data/opts down from root to every leaf), `leaf->draft`
;; (flattens a leaf into draft maps with optional facet expansion),
;; and the multi-pair / grid composite utilities.
;; `impl/compositor.clj` handles composite rendering -- each leaf
;; becomes a sub-plot, tiled via layout.
;; `impl/plan.clj` holds `draft->plan` (domains, ticks, legends, layout).
;; `impl/resolve.clj` holds `resolve-draft-layer` (single draft layer resolution,
;; column type inference, grouping).
;;
;; The `impl/` directory is pure data -- no membrane dependency.
;; The `render/` directory uses membrane for layout and SVG conversion.

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
