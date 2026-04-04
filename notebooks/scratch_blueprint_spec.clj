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
;; | `:entries` | vector of maps | `view`, `distribution`, `annotate` |
;; | `:methods` | vector of maps | `lay-*` |
;; | `:opts` | map | `options` |
;;
;; ## Resolution
;;
;; When a Blueprint is rendered, each entry is crossed with each method:
;;
;; ```
;; for each entry:
;;   methods = entry's :methods (if present) or Blueprint's methods
;;   for each method:
;;     view = merge(shared, entry, method)
;; ```
;;
;; Later maps win. `nil` values cancel earlier keys.
;; If no methods are present, `{:mark :infer}` is used (inference).
;; If an entry has its own `:methods` key, those are used instead of
;; the Blueprint's methods list. All entries inherit `:shared`.
;;
;; ## Verb Rules
;;
;; | Verb | What it does | Where opts go |
;; |:-----|:-------------|:--------------|
;; | `sketch data opts` | Set data + shared | `:shared` |
;; | `view bp cols opts` | Add entry + set shared | `:shared` |
;; | `view bp entry-map` | Add entry (may include `:methods`) | entry |
;; | `lay-* bp opts` | Add method | the method |
;; | `lay-* data cols opts` | Add entry + method | the method |
;; | `options bp opts` | Set plot options | `:opts` |
;; | `annotate bp anns` | Add self-contained entries | entry |
;; | `distribution bp cols` | Add diagonal entries | entry |

;; ## Setup

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Rule 1: `view` opts go into `:shared`

;; When `view` receives an opts map, those aesthetics merge into
;; `:shared` and apply to ALL methods on ALL entries.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Both point and lm inherit `:color :species` from shared.
;; Three regression lines — one per species.

;; Verify the mechanism: shared has color, entry does not.

(let [bp (-> iris (sk/xkcd7-view :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (first (:entries bp))])

(kind/test-last [(fn [[shared entry]]
                   (and (= :species (:color shared))
                        (nil? (:color entry))
                        (= :sepal_length (:x entry))))])

;; ## Rule 2: `lay-*` opts go into the method

;; When `lay-*` receives an opts map, those aesthetics apply only
;; to that specific method — not to shared, not to the entry.

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Points are colored (`:color` is in the point method).
;; The lm method has no color — one overall regression line.

;; Verify the mechanism: shared is empty, method has color.

(let [bp (-> iris (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  [(:shared bp) (first (:methods bp))])

(kind/test-last [(fn [[shared method]]
                   (and (empty? shared)
                        (= :species (:color method))
                        (= :point (:mark method))))])

;; ## Rule 3: `sketch` opts go into `:shared`

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Same result as Rule 1 — both methods inherit shared color.

;; ## Rule 4: method opts override shared (`nil` cancels)

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Points are colored (from shared). The lm method cancels color
;; with `{:color nil}` — one overall regression line.

;; ## Rule 5: shared affects all entries

;; When multiple entries exist, shared merges into each one.

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :petal_length :petal_width)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two panels (two entries), both colored by species.

;; ## Rule 6: per-entry methods via `view`

;; Pass a map with `:methods` to `view` to add an entry that uses
;; its own method list instead of the Blueprint's. The entry still
;; inherits `:shared`.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-view {:x :sepal_length :y :sepal_width
                    :methods [{:mark :line :stat :lm :color nil}]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Points are colored (shared + default methods).
;; The second entry has its own `:methods` with `:color nil`,
;; cancelling shared — one overall regression line.

;; ## Rule 7: annotations are self-contained entries

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; The rule-h annotation has its own `:methods` and does not
;; participate in the entry × methods cross product.

;; ## Rule 8: `lay-*` shorthand creates entry + method separately

;; `(lay-point data :x :y {:color :c})` creates entry `{:x :y}`
;; and method `{:mark :point :color :c}`. Shared is untouched.
;; A subsequent bare `lay-*` adds a method with no aesthetics.

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Points are colored (method has `:color :species`).
;; Loess has no color — shared is empty, loess method has no color.
;; One overall smoothing line.

;; ## Summary
;;
;; The three scopes, from broadest to narrowest:
;;
;; | Scope | Set by | Affects |
;; |:------|:-------|:--------|
;; | **shared** | `sketch`, `view` (opts map) | all entries × all methods |
;; | **per-entry** | `view` (columns/map) | one entry × its methods |
;; | **per-method** | `lay-*` (opts map) | one method only |
;;
;; Resolution order: `merge(shared, entry, method)` — later wins, `nil` cancels.
;;
;; Entries with `:methods` use their own method list (from `view`
;; with a map, or `annotate`). Entries without `:methods` use the
;; Blueprint's method list (from `lay-*` calls).
