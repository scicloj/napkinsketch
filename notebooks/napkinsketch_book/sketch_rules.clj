;; # Sketch Rules
;;
;; This chapter is the definitive reference for how sketches behave.
;; It specifies the data model, verb rules, and resolution semantics
;; through 18 rules -- each demonstrated with a printed sketch
;; structure, a rendered plot, and a verified assertion.
;;
;; Read [The Sketch Model](./napkinsketch_book.sketch_model.html) first
;; for the mental model. Read
;; [Core Concepts](./napkinsketch_book.core_concepts.html) for detailed
;; explanations. This chapter is the "proof" layer -- every rule is
;; reproducible and tested.

(ns napkinsketch-book.sketch-rules
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## The Data Model
;;
;; A **sketch** is a record with five fields:
;;
;; | Field | Type | Set by |
;; |:------|:-----|:-------|
;; | `:data` | dataset or nil | `sketch`, or implicitly by `view`/`lay-*` |
;; | `:mapping` | map | `sketch` |
;; | `:views` | vector of view maps | `view`, `lay-*` (with `:x`/`:y`), `distribution`, `overlay` |
;; | `:layers` | vector of layer maps | `lay-*` (without `:x`/`:y`) |
;; | `:opts` | map | `options`, `scale`, `coord`, `annotate` |
;;
;; ## Resolution
;;
;; Resolution converts a sketch into a **plan** -- the fully resolved
;; data structure used to render the plot. Call `sk/plan` on any
;; sketch to get this plan as a plain Clojure map; it is a public
;; API, not an internal detail, and the rules in this chapter are
;; verified against its contents.
;;
;; See [Inference Rules](./napkinsketch_book.inference_rules.html)
;; for a full walkthrough of what a plan contains.
;;
;; Each view gets its own layers (if any) PLUS the sketch-level layers.
;; Views without own layers use sketch-level layers only.
;; If no layers at all, `{:method :infer}` is used.
;;
;; ```
;; for each view:
;;   layers = concat(view's :layers, sketch :layers)
;;   for each layer:
;;     resolved = merge(sketch :mapping, view :mapping, layer :mapping)
;; ```
;;
;; Later maps win. `nil` values cancel earlier keys.
;;
;; ## Verb Rules
;;
;; | Verb | `:x`/`:y`? | What it does |
;; |:-----|:-----------|:-------------|
;; | `sketch` | -- | Set data + sketch-level mapping |
;; | `view` | yes | Add view (opts go into the view's mapping) |
;; | `lay-*` | no | Add **sketch-level** layer (all views get it) |
;; | `lay-*` | yes | Add **view-specific** layer (find or create view) |
;; | `overlay` | yes | Add view with own layer (different columns, future: same panel) |
;; | `annotate` | -- | Add annotations to `:opts` |
;; | `distribution` | yes | Add diagonal views for SPLOM |
;; | `options` | -- | Set plot-level options |
;;
;; The structural/non-structural distinction:
;;
;; - `:x`, `:y` -> **structural** (define what data to plot, create views)
;; - `:color`, `:alpha`, `:size`, `:se`, etc. -> **non-structural** (aesthetics/parameters)

;; ## Setup

;; A helper to show the sketch structure without the dataset:

(defn sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:mapping (:mapping sk)
   :views (mapv #(dissoc % :data) (:views sk))
   :layers (:layers sk)
   :opts (:opts sk)})

;; ## Rule 1: `view` opts go into the view's `:mapping`
;;
;; When `view` receives an opts map, those aesthetics merge into
;; the view's own `:mapping` and apply to ALL layers on that view.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/lay-lm
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :mapping :color]))
                        (= 1 (count (:views m)))
                        (= {} (:mapping m))
                        (= 2 (count (:layers m)))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from the view's mapping.
;; Three regression lines -- one per species.

;; ## Rule 2: `lay-*` without `:x`/`:y` -> sketch-level layer

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:layers m)))
                        (= :point (:method (first (:layers m))))
                        (nil? (:layers (first (:views m))))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; View has no own layers -> uses the two sketch-level layers.

;; ## Rule 3: `lay-*` with `:x`/`:y` -> view-specific layer

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 0 (count (:layers m)))
                        (= 1 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :species (get-in m [:views 0 :layers 0 :mapping :color]))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Sketch-level layers list is empty. The view has its own `:layers`.
;; `:color :species` is in the layer's mapping, not in sketch mapping.

;; ## Rule 4: view-specific + sketch-level layers combine

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))
                        (= :lm (:method (first (:layers m))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; View has own layer (point with color). Sketch has lm (no color).
;; Combined: colored points + 1 overall regression line.

;; ## Rule 5: find-or-create view by matching `:x`/`:y`

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width)
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= 2 (count (:layers (first (:views m)))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Same `:x`/`:y` -> lm found the existing view and appended.
;; One view with two view-specific layers.

;; ## Rule 6: different `:x`/`:y` -> separate views

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= [1 1] (mapv #(count (:layers %)) (:views m)))
                        (= :sepal-length (get-in m [:views 0 :mapping :x]))
                        (= :petal-length (get-in m [:views 1 :mapping :x]))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:polygons s)))))])

;; Two views with different columns, each with its own layer.
;; No cross product -- scatter on view 1, histogram on view 2.

;; ## Rule 7: `sketch` opts go into sketch-level `:mapping`

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (= :species (get-in m [:mapping :color])))])

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; `sketch` sets sketch-level mapping (all views inherit it).
;; `view` opts set view-level mapping (only that view sees it).

;; ## Rule 8: layer mapping overrides view mapping (`nil` cancels)

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil})
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :mapping :color]))
                        (nil? (get-in m [:layers 1 :mapping :color]))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; View mapping has color. Lm layer has `:color nil` -> cancels.

;; ## Rule 9: sketch mapping affects all views

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:mapping :color]))
                        (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two views, one sketch-level layer, sketch-level color. Both panels colored.

;; ## Rule 10: annotations are self-contained

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/annotate (sk/rule-h 3.0))
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Annotations live in `:opts`, separate from views and layers.

;; ## Rule 11: `overlay` -- view with different columns + own layer

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/overlay :sepal-length :petal-width :lm)
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= :sepal-width (get-in m [:views 0 :mapping :y]))
                        (= :petal-width (get-in m [:views 1 :mapping :y]))
                        (= :lm (:method (first (:layers (second (:views m))))))))])

;; Two views: scatter on sepal-width, lm on petal-width.
;; Currently rendered as separate panels (same-panel is a future feature).

;; ## Rule 12: `lay-*` shorthand with auto-infer

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= :x (get-in m [:views 0 :mapping :x]))))])

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Small dataset (<=3 columns) -> columns auto-inferred, view-specific.

;; ---
;; ## Grid Layout Rules
;;
;; Each view produces its own panel. The grid is determined by
;; structural columns: views sharing an x-variable align in the
;; same grid column; views sharing a y-variable align in the same
;; grid row. Per-view axes -- no range leaking.

;; ## Rule 13: each view = one panel

;; Two `lay-*` with different columns -> two separate panels.
;; No cross-product -- scatter stays on its view, histogram on its.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary kind/pprint)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; Two panels: scatter (150 points) and histogram (polygons).
;; Each view has its own axis range -- no leaking.

;; ## Rule 14: shared x-variable -> same grid column

;; Scatter and histogram of the same x-variable share the x-axis.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; Two panels stacked vertically (same x-column), shared x-axis.
;; This is a marginal distribution layout.

;; ## Rule 15: layers within one view -> one panel (overlay)

;; Multiple layers on the same view produce one panel with
;; overlaid layers -- not separate panels.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; One panel: colored scatter + overall regression line.
;; Both layers are view-specific (same :x/:y -> found same view).

;; ## Rule 16: sketch-level layers apply to all views

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:panels s))
                                (pos? (:points s))
                                (pos? (:lines s)))))])

;; Two views (different columns), each gets the sketch-level lm layer.
;; Both panels have scatter + regression line.

;; ## Rule 17: SPLOM -- cross product of columns

(def splom-cols [:sepal-length :sepal-width :petal-length])

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view (sk/cross splom-cols splom-cols)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 9 (:panels s))
                                (= (* 6 150) (:points s))
                                (pos? (:polygons s)))))])

;; 9 panels (3x3 grid), colored by species.
;; Off-diagonal: scatter (6 x 150 = 900 points).
;; Diagonal: histograms (inferred when x=y).

;; ## Rule 18: per-panel scale and coord specs

;; Scale and coord specs are stored in `:opts` and applied per-panel.
;; Each panel uses the scale/coord from the sketch's options.

(def scale-plan
  (-> (rdatasets/datasets-iris)
      (sk/view :sepal-length :sepal-width)
      sk/lay-point
      (sk/scale :x :log)
      (sk/scale :y {:domain [0 6]})
      (sk/coord :flip)
      sk/plan))

(let [panel (first (:panels scale-plan))]
  {:coord (:coord panel)
   :x-scale (:x-scale panel)
   :y-scale (:y-scale panel)
   :x-domain (:x-domain panel)})

(kind/test-last
 [(fn [m]
    (and (= :flip (:coord m))
         ;; After flip: x-scale was the original y-scale (domain [0 6])
         (= [0 6] (:x-domain m))
         (= {:type :linear :domain [0 6]} (:x-scale m))
         ;; After flip: y-scale was the original x-scale (log)
         (= {:type :log} (:y-scale m))))])

;; Coord `:flip` swaps domains, ticks, and scale specs per-panel.
;; The plan's labels also swap to match: x-label becomes y-label and vice versa.

(select-keys scale-plan [:x-label :y-label])

(kind/test-last
 [(fn [m] (and (= "sepal width" (:x-label m))
               (= "sepal length" (:y-label m))))])

;; ## Summary
;;
;; ### Verb scopes
;;
;; | Scope | Set by | Affects |
;; |:------|:-------|:--------|
;; | **sketch mapping** | `sketch` | all views x all layers |
;; | **view mapping** | `view` (opts map), `lay-*` (with `:x`/`:y`) | one view's own layers |
;; | **layer mapping** | `lay-*` opts map | one layer only |
;; | **sketch-level** | `lay-*` (no `:x`/`:y`) | all views |
;;
;; Resolution: `merge(sketch mapping, view mapping, layer mapping)` -- later wins, `nil` cancels.
;;
;; View layers = `concat(own-layers, sketch-level-layers)`.
;; Views without own layers use sketch-level layers only.
;;
;; Structural aesthetics (`:x`, `:y`) -> view-specific.
;; Non-structural aesthetics (`:color`, `:alpha`, etc.) -> layer-level.
;;
;; ### Grid layout
;;
;; - Each view = one panel
;; - Layers within one view = overlay (same panel)
;; - Views sharing x-variable -> same grid column (shared x-axis)
;; - Views sharing y-variable -> same grid row (shared y-axis)
;; - Same position -> stacked sub-panels
;; - User override: `{:grid-cols [...] :grid-rows [...]}`
