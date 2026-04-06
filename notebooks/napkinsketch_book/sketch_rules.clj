;; # Sketch Rules
;;
;; This chapter is the definitive reference for how sketches behave.
;; It specifies the data model, verb rules, and resolution semantics
;; through 18 rules — each demonstrated with a printed sketch
;; structure, a rendered plot, and a verified assertion.
;;
;; Read [The Sketch Model](./napkinsketch_book.sketch_model.html) first
;; for the mental model. Read
;; [Core Concepts](./napkinsketch_book.core_concepts.html) for detailed
;; explanations. This chapter is the "proof" layer — every rule is
;; reproducible and tested.

(ns napkinsketch-book.sketch-rules
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]))

;; ## The Data Model
;;
;; A **sketch** is a record with five fields:
;;
;; | Field | Type | Set by |
;; |:------|:-----|:-------|
;; | `:data` | dataset or nil | `sketch`, or implicitly by `view`/`lay-*` |
;; | `:shared` | map | `sketch`, `view` |
;; | `:entries` | vector of maps | `view`, `lay-*` (with `:x`/`:y`), `distribution`, `annotate`, `overlay` |
;; | `:methods` | vector of maps | `lay-*` (without `:x`/`:y`) |
;; | `:opts` | map | `options` |
;;
;; ## Resolution
;;
;; Each entry gets its own methods (if any) PLUS the global methods.
;; Entries without own methods use global methods only.
;; If no methods at all, `{:mark :infer}` is used.
;;
;; ```
;; for each entry:
;;   methods = concat(entry's :methods, global methods)
;;   for each method:
;;     view = merge(shared, entry, method)
;; ```
;;
;; Later maps win. `nil` values cancel earlier keys.
;;
;; ## Verb Rules
;;
;; | Verb | `:x`/`:y`? | What it does |
;; |:-----|:-----------|:-------------|
;; | `sketch` | — | Set data + shared aesthetics |
;; | `view` | yes | Add entry + set shared (opts → shared) |
;; | `lay-*` | no | Add **global** method (all entries get it) |
;; | `lay-*` | yes | Add **entry-specific** method (find or create entry) |
;; | `overlay` | yes | Add entry with own method (different columns, future: same panel) |
;; | `annotate` | — | Add self-contained annotation entries |
;; | `distribution` | yes | Add diagonal entries for SPLOM |
;; | `options` | — | Set plot-level options |
;;
;; The structural/non-structural distinction:
;;
;; - `:x`, `:y` → **structural** (define what data to plot, create entries)
;; - `:color`, `:alpha`, `:size`, `:se`, etc. → **non-structural** (aesthetics/parameters)

;; ## Setup

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; A helper to show the sketch structure without the dataset:

(defn sk-summary
  "Show the sketch fields that matter for understanding the rules."
  [sk]
  {:shared (:shared sk)
   :entries (mapv #(dissoc % :data) (:entries sk))
   :methods (:methods sk)
   :opts (:opts sk)})

;; ## Rule 1: `view` opts go into `:shared`
;;
;; When `view` receives an opts map, those aesthetics merge into
;; `:shared` and apply to ALL methods on ALL entries.

(let [sk (-> iris
             (sk/view :sepal_length :sepal_width {:color :species})
             sk/lay-point
             sk/lay-lm)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (= 1 (count (:entries m)))
                        (nil? (:color (first (:entries m))))
                        (= 2 (count (:methods m)))))])

(-> iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from shared.
;; Three regression lines — one per species.

;; ## Rule 2: `lay-*` without `:x`/`:y` → global method

(let [sk (-> iris
             (sk/view :sepal_length :sepal_width)
             sk/lay-point
             sk/lay-lm)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:methods m)))
                        (= :point (:mark (first (:methods m))))
                        (nil? (:methods (first (:entries m))))))])

(-> iris
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Entry has no own methods → uses the two global methods.

;; ## Rule 3: `lay-*` with `:x`/`:y` → entry-specific method

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width {:color :species}))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 0 (count (:methods m)))
                        (= 1 (count (:entries m)))
                        (= 1 (count (:methods (first (:entries m)))))
                        (= :species (:color (first (:methods (first (:entries m))))))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Global methods list is empty. The entry has its own `:methods`.
;; `:color :species` is in the method, not in shared.

;; ## Rule 4: entry-specific + global methods combine

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width {:color :species})
             sk/lay-lm)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:methods m)))
                        (= 1 (count (:methods (first (:entries m)))))
                        (= :point (:mark (first (:methods (first (:entries m))))))
                        (= :line (:mark (first (:methods m))))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Entry has own method (point with color). Global has lm (no color).
;; Combined: colored points + 1 overall regression line.

;; ## Rule 5: find-or-create entry by matching `:x`/`:y`

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width {:color :species})
             (sk/lay-lm :sepal_length :sepal_width))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:entries m)))
                        (= 0 (count (:methods m)))
                        (= 2 (count (:methods (first (:entries m)))))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/lay-lm :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Same `:x`/`:y` → lm found the existing entry and appended.
;; One entry with two entry-specific methods.

;; ## Rule 6: different `:x`/`:y` → separate entries

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width)
             (sk/lay-histogram :petal_length))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= 0 (count (:methods m)))
                        (= [1 1] (mapv #(count (:methods %)) (:entries m)))
                        (= :sepal_length (:x (first (:entries m))))
                        (= :petal_length (:x (second (:entries m))))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:polygons s)))))])

;; Two entries with different columns, each with its own method.
;; No cross product — scatter on entry 1, histogram on entry 2.

;; ## Rule 7: `sketch` opts go into `:shared`

(let [sk (-> (sk/sketch iris {:color :species})
             (sk/view :sepal_length :sepal_width)
             sk/lay-point
             sk/lay-lm)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (= :species (get-in m [:shared :color])))])

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Same as Rule 1 — `sketch` and `view` both set shared.

;; ## Rule 8: method opts override shared (`nil` cancels)

(let [sk (-> iris
             (sk/view :sepal_length :sepal_width {:color :species})
             sk/lay-point
             (sk/lay-lm {:color nil}))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (nil? (:color (second (:methods m))))))])

(-> iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Shared has color. Lm method has `:color nil` → cancels.

;; ## Rule 9: shared affects all entries

(let [sk (-> (sk/sketch iris {:color :species})
             (sk/view :sepal_length :sepal_width)
             (sk/view :petal_length :petal_width)
             sk/lay-point)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (= 2 (count (:entries m)))
                        (= 1 (count (:methods m)))))])

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/view :petal_length :petal_width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two entries, one global method, shared color. Both panels colored.

;; ## Rule 10: annotations are self-contained

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width {:color :species})
             (sk/annotate (sk/rule-h 3.0)))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= :rule-h (:mark (second (:entries m))))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Annotation entry has its own `:methods`.

;; ## Rule 11: `overlay` — entry with different columns + own method

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width)
             (sk/overlay :sepal_length :petal_width :lm))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= :sepal_width (:y (first (:entries m))))
                        (= :petal_width (:y (second (:entries m))))
                        (= :line (:mark (first (:methods (second (:entries m))))))))])

;; Two entries: scatter on sepal_width, lm on petal_width.
;; Currently rendered as separate panels (same-panel is a future feature).

;; ## Rule 12: `lay-*` shorthand with auto-infer

(let [sk (-> {:x [1 2 3] :y [4 5 6]}
             sk/lay-point)]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:entries m)))
                        (= :x (:x (first (:entries m))))))])

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Small dataset (≤3 columns) → columns auto-inferred, entry-specific.

;; ---
;; # Grid Layout Rules
;;
;; Each entry produces its own panel. The grid is determined by
;; structural columns: entries sharing an x-variable align in the
;; same grid column; entries sharing a y-variable align in the same
;; grid row. Per-entry axes — no range leaking.

;; ## Rule 13: each entry = one panel

;; Two `lay-*` with different columns → two separate panels.
;; No cross-product — scatter stays on its entry, histogram on its.

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width)
             (sk/lay-histogram :petal_length))]
  (kind/pprint (sk-summary sk)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= 0 (count (:methods m)))))])

(-> iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :petal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; Two panels: scatter (150 points) and histogram (polygons).
;; Each has its own axis range — no leaking.

;; ## Rule 14: shared x-variable → same grid column

;; Scatter and histogram of the same x-variable share the x-axis.

(-> iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; Two panels stacked vertically (same x-column), shared x-axis.
;; This is a marginal distribution layout.

;; ## Rule 15: methods within one entry → one panel (overlay)

;; Multiple methods on the same entry produce one panel with
;; overlaid layers — not separate panels.

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/lay-lm :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; One panel: colored scatter + overall regression line.
;; Both methods are entry-specific (same :x/:y → found same entry).

;; ## Rule 16: global methods apply to all entries

(-> iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-point :petal_length :petal_width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:panels s))
                                (pos? (:points s))
                                (pos? (:lines s)))))])

;; Two entries (different columns), each gets the global lm method.
;; Both panels have scatter + regression line.

;; ## Rule 17: SPLOM — cross product of columns

(def splom-cols [:sepal_length :sepal_width :petal_length])

(-> (sk/sketch iris {:color :species})
    (sk/view (sk/cross splom-cols splom-cols)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 9 (:panels s))
                                (= (* 6 150) (:points s))
                                (pos? (:polygons s)))))])

;; 9 panels (3×3 grid), colored by species.
;; Off-diagonal: scatter (6 × 150 = 900 points).
;; Diagonal: histograms (inferred when x=y).

;; ## Rule 18: per-panel scale and coord specs

;; Scale and coord specs are extracted per-panel, not globally.
;; Each panel uses the scale/coord from its own entry's views.

(def scale-plan
  (-> iris
      (sk/view :sepal_length :sepal_width)
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
 [(fn [m] (and (= "sepal_width" (:x-label m))
               (= "sepal_length" (:y-label m))))])

;; ## Summary
;;
;; ### Verb scopes
;;
;; | Scope | Set by | Affects |
;; |:------|:-------|:--------|
;; | **shared** | `sketch`, `view` (opts map) | all entries × all methods |
;; | **per-entry** | `lay-*` (with `:x`/`:y`), `overlay` | one entry's own methods |
;; | **per-method** | `lay-*` opts map | one method only |
;; | **global** | `lay-*` (no `:x`/`:y`) | all entries |
;;
;; Resolution: `merge(shared, entry, method)` — later wins, `nil` cancels.
;;
;; Entry methods = `concat(own-methods, global-methods)`.
;; Entries without own methods use global methods only.
;;
;; Structural aesthetics (`:x`, `:y`) → entry-specific.
;; Non-structural aesthetics (`:color`, `:alpha`, etc.) → method-level.
;;
;; ### Grid layout
;;
;; - Each entry = one panel
;; - Methods within one entry = overlay (same panel)
;; - Entries sharing x-variable → same grid column (shared x-axis)
;; - Entries sharing y-variable → same grid row (shared y-axis)
;; - Same position → stacked sub-panels
;; - User override: `{:grid-cols [...] :grid-rows [...]}`
