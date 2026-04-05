;; # Blueprint Data Model & Verb Semantics
;;
;; This notebook defines the data model, verb rules, and resolution
;; semantics for the proposed Blueprint API. Every rule is demonstrated
;; with an example and verified with `kind/test-last`.

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
;; | `:entries` | vector of maps | `view`, `lay-*` (with columns), `distribution`, `annotate`, `overlay` |
;; | `:methods` | vector of maps | `lay-*` (without columns) |
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
;; | Verb | Columns? | What it does |
;; |:-----|:---------|:-------------|
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

;; ## Rule 1: `view` opts go into `:shared`

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from shared.

;; Verify the mechanism:

(let [bp (-> iris (sk/xkcd7-view :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (first (:entries bp))])

(kind/test-last [(fn [[shared entry]]
                   (and (= :species (:color shared))
                        (nil? (:color entry))))])

;; ## Rule 2: `lay-*` without columns → global method

(let [bp (-> iris
             (sk/xkcd7-view :sepal_length :sepal_width)
             sk/xkcd7-lay-point
             sk/xkcd7-lay-lm)]
  [(count (:methods bp)) (nil? (:methods (first (:entries bp))))])

(kind/test-last [(fn [[n-global entry-has-no-methods]]
                   (and (= 2 n-global)
                        entry-has-no-methods))])

;; ## Rule 3: `lay-*` with columns → entry-specific method

(let [bp (-> iris (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  [(count (:methods bp)) (:methods (first (:entries bp)))])

(kind/test-last [(fn [[n-global entry-methods]]
                   (and (= 0 n-global)
                        (= 1 (count entry-methods))
                        (= :point (:mark (first entry-methods)))))])

;; ## Rule 4: entry-specific methods + global methods combine

;; Entry gets its own methods AND global methods.

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Points are colored (entry-specific method has `:color`).
;; Lm is global (no color) → 1 overall line.

;; Verify the mechanism:

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
             sk/xkcd7-lay-lm)]
  [(count (:methods bp))
   (count (:methods (first (:entries bp))))])

(kind/test-last [(fn [[n-global n-entry]]
                   (and (= 1 n-global)
                        (= 1 n-entry)))])

;; ## Rule 5: find-or-create entry by matching columns

;; A second `lay-*` with the same columns finds the existing entry.

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
             (sk/xkcd7-lay-lm :sepal_length :sepal_width))]
  [(count (:entries bp))
   (count (:methods (first (:entries bp))))
   (count (:methods bp))])

(kind/test-last [(fn [[n-entries n-entry-methods n-global]]
                   (and (= 1 n-entries)
                        (= 2 n-entry-methods)
                        (= 0 n-global)))])

;; One entry with two methods (point + lm), both entry-specific.

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-lay-lm :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Rule 6: two `lay-*` with different columns → separate entries

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-histogram :petal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:polygons s)))))])

;; Verify structure: two entries, each with own methods.

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width)
             (sk/xkcd7-lay-histogram :petal_length))]
  [(count (:entries bp))
   (count (:methods bp))
   (mapv #(count (:methods %)) (:entries bp))])

(kind/test-last [(fn [[n-entries n-global entry-method-counts]]
                   (and (= 2 n-entries)
                        (= 0 n-global)
                        (= [1 1] entry-method-counts)))])

;; ## Rule 7: `sketch` opts go into `:shared`

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rule 8: method opts override shared (`nil` cancels)

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Rule 9: shared affects all entries

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :petal_length :petal_width)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; ## Rule 10: annotations are self-contained

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Rule 11: `overlay` — different columns, own methods

;; `overlay` adds an entry with its own method. Currently rendered as
;; a separate panel (same-panel rendering is a future pipeline feature).

(let [bp (-> iris
             (sk/xkcd7-lay-point :sepal_length :sepal_width)
             (sk/xkcd7-overlay :sepal_length :petal_width :lm))]
  [(count (:entries bp))
   (mapv #(count (:methods %)) (:entries bp))])

(kind/test-last [(fn [[n-entries method-counts]]
                   (and (= 2 n-entries)
                        (= [1 1] method-counts)))])

;; ## Rule 12: `lay-*` shorthand with auto-infer

(-> {:x [1 2 3] :y [4 5 6]}
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ## Summary
;;
;; | Scope | Set by | Affects |
;; |:------|:-------|:--------|
;; | **shared** | `sketch`, `view` (opts map) | all entries × all methods |
;; | **per-entry** | `lay-*` (with columns), `overlay` | one entry's own methods |
;; | **per-method** | `lay-*` opts map | one method only |
;; | **global** | `lay-*` (no columns) | all entries |
;;
;; Resolution: `merge(shared, entry, method)` — later wins, `nil` cancels.
;;
;; Entry methods = `concat(own-methods, global-methods)`.
;; Entries without own methods use global methods only.
;;
;; Structural aesthetics (`:x`, `:y`) → entry-specific.
;; Non-structural aesthetics (`:color`, `:alpha`, etc.) → method-level.
