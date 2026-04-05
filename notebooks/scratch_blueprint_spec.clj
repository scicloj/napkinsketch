;; # Blueprint Data Model & Verb Semantics
;;
;; This notebook defines the data model, verb rules, and resolution
;; semantics for the proposed Blueprint API. Every rule is demonstrated
;; with a printed Blueprint structure, a rendered plot, and a
;; `kind/test-last` verification.

(ns scratch-blueprint-spec
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]))

;; ## The Data Model
;;
;; A **Blueprint** is a record with five fields:
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

;; A helper to show the Blueprint structure without the dataset:

(defn bp-summary
  "Show the Blueprint fields that matter for understanding the rules."
  [bp]
  {:shared (:shared bp)
   :entries (mapv #(dissoc % :data) (:entries bp))
   :methods (:methods bp)
   :opts (:opts bp)})

;; ## Rule 1: `view` opts go into `:shared`
;;
;; When `view` receives an opts map, those aesthetics merge into
;; `:shared` and apply to ALL methods on ALL entries.

(let [bp (-> iris
             (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
             sk/xkcd7-lay-point
             sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (= 1 (count (:entries m)))
                        (nil? (:color (first (:entries m))))
                        (= 2 (count (:methods m)))))])

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from shared.
;; Three regression lines — one per species.

;; ## Rule 2: `lay-*` without `:x`/`:y` → global method

(let [bp (-> iris
             (sk/xkcd7-view :sepal_length :sepal_width)
             sk/xkcd7-lay-point
             sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:methods m)))
                        (= :point (:mark (first (:methods m))))
                        (nil? (:methods (first (:entries m))))))])

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Entry has no own methods → uses the two global methods.

;; ## Rule 3: `lay-*` with `:x`/`:y` → entry-specific method

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 0 (count (:methods m)))
                        (= 1 (count (:entries m)))
                        (= 1 (count (:methods (first (:entries m)))))
                        (= :species (:color (first (:methods (first (:entries m))))))))])

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Global methods list is empty. The entry has its own `:methods`.
;; `:color :species` is in the method, not in shared.

;; ## Rule 4: entry-specific + global methods combine

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
             sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:methods m)))
                        (= 1 (count (:methods (first (:entries m)))))
                        (= :point (:mark (first (:methods (first (:entries m))))))
                        (= :line (:mark (first (:methods m))))))])

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Entry has own method (point with color). Global has lm (no color).
;; Combined: colored points + 1 overall regression line.

;; ## Rule 5: find-or-create entry by matching `:x`/`:y`

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
             (sk/xkcd7-lay-lm :sepal_length :sepal_width))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:entries m)))
                        (= 0 (count (:methods m)))
                        (= 2 (count (:methods (first (:entries m)))))))])

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-lay-lm :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Same `:x`/`:y` → lm found the existing entry and appended.
;; One entry with two entry-specific methods.

;; ## Rule 6: different `:x`/`:y` → separate entries

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width)
             (sk/xkcd7-lay-histogram :petal_length))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= 0 (count (:methods m)))
                        (= [1 1] (mapv #(count (:methods %)) (:entries m)))
                        (= :sepal_length (:x (first (:entries m))))
                        (= :petal_length (:x (second (:entries m))))))])

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-histogram :petal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:polygons s)))))])

;; Two entries with different columns, each with its own method.
;; No cross product — scatter on entry 1, histogram on entry 2.

;; ## Rule 7: `sketch` opts go into `:shared`

(let [bp (-> (sk/xkcd7-sketch iris {:color :species})
             (sk/xkcd7-view :sepal_length :sepal_width)
             sk/xkcd7-lay-point
             sk/xkcd7-lay-lm)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (= :species (get-in m [:shared :color])))])

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Same as Rule 1 — `sketch` and `view` both set shared.

;; ## Rule 8: method opts override shared (`nil` cancels)

(let [bp (-> iris
             (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
             sk/xkcd7-lay-point
             (sk/xkcd7-lay-lm {:color nil}))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (nil? (:color (second (:methods m))))))])

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Shared has color. Lm method has `:color nil` → cancels.

;; ## Rule 9: shared affects all entries

(let [bp (-> (sk/xkcd7-sketch iris {:color :species})
             (sk/xkcd7-view :sepal_length :sepal_width)
             (sk/xkcd7-view :petal_length :petal_width)
             sk/xkcd7-lay-point)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:shared :color]))
                        (= 2 (count (:entries m)))
                        (= 1 (count (:methods m)))))])

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :petal_length :petal_width)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two entries, one global method, shared color. Both panels colored.

;; ## Rule 10: annotations are self-contained

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
             (sk/xkcd7-annotate (sk/rule-h 3.0)))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= :rule-h (:mark (second (:entries m))))))])

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Annotation entry has its own `:methods`.

;; ## Rule 11: `overlay` — entry with different columns + own method

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width)
             (sk/xkcd7-overlay :sepal_length :petal_width :lm))]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 2 (count (:entries m)))
                        (= :sepal_width (:y (first (:entries m))))
                        (= :petal_width (:y (second (:entries m))))
                        (= :line (:mark (first (:methods (second (:entries m))))))))])

;; Two entries: scatter on sepal_width, lm on petal_width.
;; Currently rendered as separate panels (same-panel is a future feature).

;; ## Rule 12: `lay-*` shorthand with auto-infer

(let [bp (-> {:x [1 2 3] :y [4 5 6]}
             sk/xkcd7-lay-point)]
  (kind/pprint (bp-summary bp)))

(kind/test-last [(fn [m]
                   (and (= 1 (count (:entries m)))
                        (= :x (:x (first (:entries m))))))])

(-> {:x [1 2 3] :y [4 5 6]}
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Small dataset (≤3 columns) → columns auto-inferred, entry-specific.

;; ## Summary
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
