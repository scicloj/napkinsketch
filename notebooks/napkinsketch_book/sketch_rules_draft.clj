;; # Sketch Rules
;;
;; This chapter specifies how sketches are constructed -- where
;; layers, data, and mappings are placed by the API -- through
;; 12 tested rules.
;;
;; Read [The Sketch Model](./napkinsketch_book.sketch_model.html)
;; and [Core Concepts](./napkinsketch_book.core_concepts.html) first.
;; This chapter is the proof layer -- every rule is reproducible
;; and tested.

(ns napkinsketch-book.sketch-rules
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Overview
;;
;; A **sketch** is a record with five fields:
;;
;; | Field | Type | Set by |
;; |:------|:-----|:-------|
;; | `:data` | dataset or nil | `sketch`, or implicitly by `view`/`lay-*` |
;; | `:mapping` | map | `sketch` |
;; | `:views` | vector of view maps | `view`, `lay-*` with x/y columns |
;; | `:layers` | vector of layer maps | `lay-*` without x/y columns |
;; | `:opts` | map | `options`, `scale`, `coord`, `annotate`, `facet` |
;;
;; `sketch`, `view`, and `lay-*` build a sketch record by placing
;; three things through lexical scope:
;;
;; - **layers** -- inside a view, or at sketch level
;; - **data** -- at sketch, view, or layer level
;; - **mappings** -- at sketch, view, or layer level
;;
;; The 12 rules below make this precise. The assembly
;; semantics are:
;;
;; ```clojure
;; (for [view views]
;;   (let [layers (concat view-layers sketch-layers)]
;;     (for [layer layers]
;;       (let [data     (or layer-data view-data sketch-data)
;;             mappings (merge sketch-mapping view-mapping layer-mapping)]
;;         ...))))
;; ```
;;
;; `concat` joins both layer lists. `or` picks the first non-nil
;; data source. `merge` combines mappings -- innermost wins, `nil`
;; erases. After assembly, plan computation infers marks, runs
;; statistics, and builds the rendering geometry
;; (see [Inference Rules](./napkinsketch_book.inference_rules.html)).

;; ## Setup

;; A helper to inspect sketch structure. We use this throughout to
;; verify that layers and mappings land in the expected places.

(defn sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (-> (select-keys sk [:mapping :views :layers :opts])
      (update :views (partial mapv #(dissoc % :data)))
      kind/pprint))

;; ---
;; ## Layer Placement
;;
;; A sketch accumulates layers. Each layer lives either inside a
;; specific view (scoped to those columns) or at sketch level (shared
;; by all views). The deciding factor: whether you pass x/y column
;; names to `lay-*`.

;; ### Rule 1: `lay-*` with x/y columns places the layer inside a view
;;
;; When `lay-*` receives column names, it places the layer inside a
;; view. The columns define the view's axes.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 0 (count (:layers m)))
                        (= 1 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; The layer lives inside the view. Sketch-level `:layers` is empty.

;; ### Rule 2: `lay-*` without columns is sketch-level
;;
;; When `lay-*` is called without column names, the layer goes into
;; the sketch's `:layers` vector.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:layers m)))
                        (= :point (:method (first (:layers m))))
                        (= :lm (:method (second (:layers m))))
                        (nil? (:layers (first (:views m))))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Two sketch-level layers (point and lm). The view has no own
;; layers -- it uses the sketch's.

;; ### Rule 3: view layers come first, then sketch layers
;;
;; When a view has its own layers AND sketch-level layers exist,
;; the view uses both. Its own layers come first; sketch layers
;; are appended.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= :lm (:method (first (:layers m))))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Point is view-level (has columns); lm is sketch-level (no
;; columns). The view gets both: scatter plus regression line.

;; ### Rule 4: sketch layers apply to every view
;;
;; When there are multiple views, each one independently gets the
;; sketch-level layers.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= 2 (:lines s)))))])

;; Two views with different columns, each with its own point layer.
;; The sketch-level lm produces a regression line in each panel:
;; 2 panels, 300 points, 2 lines.

;; ---
;; ## View Identity
;;
;; Now that we know where layers land, the question is: how do views
;; come into existence? The two verbs -- `view` and `lay-*` -- handle
;; this differently.

;; ### Rule 5: `view` always creates a new view
;;
;; `sk/view` always creates a new view, even when the columns match
;; an existing one.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two views with identical columns -- two panels. This is how you
;; place different chart types on the same data column: create
;; separate views with `sk/view`, then target each with `lay-*`.

;; ### Rule 6: `lay-*` finds the most recent matching view
;;
;; When `lay-*` receives columns that match an existing view, it
;; adds the layer to that view instead of creating a new one. It
;; matches the most recent (last) view with the same x and y.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= 2 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))
                        (= :lm (:method (second (:layers (first (:views m))))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Same columns -- lm found the existing view and appended as the
;; second layer. One panel with colored scatter plus overall
;; regression.

;; ### Rule 7: different columns create a new view
;;
;; When `lay-*` receives columns that do not match any existing
;; view, it creates a new view.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= :sepal-length (get-in m [:views 0 :mapping :x]))
                        (= :petal-length (get-in m [:views 1 :mapping :x]))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

;; Two views, two panels: scatter on one, histogram on the other.

;; ### Rule 8: small datasets -- columns auto-inferred
;;
;; When the dataset has three or fewer columns and `lay-*` is called
;; without column names, columns are inferred by position:
;; first -- x, second -- y, third -- color.

;; Two columns -- inferred as x and y:

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= :x (get-in m [:views 0 :mapping :x]))
                        (= :y (get-in m [:views 0 :mapping :y]))
                        (= 1 (count (:layers (first (:views m)))))))])

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Three columns -- third inferred as color:

(-> {:x [1 2 3] :y [4 5 6] :c ["a" "b" "a"]}
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :x (get-in m [:views 0 :mapping :x]))
                        (= :y (get-in m [:views 0 :mapping :y]))
                        (= :c (get-in m [:views 0 :mapping :color]))))])

;; Datasets with four or more columns require explicit column names.

;; ---
;; ## Mapping Scope
;;
;; The previous section covered where *layers* go. This section
;; covers where *mappings* go -- the same principle of scope, applied
;; to visual properties like color, size, and alpha.
;;
;; Where you write a mapping determines who sees it. There are three
;; levels, from broadest to narrowest.

;; ### Rule 9: `sketch` sets sketch-level mapping
;;
;; Mappings passed to `sk/sketch` live at sketch level. They flow
;; to every view and every layer.

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (:color (:mapping m)))
                        (= 1 (count (:views m)))
                        (= 2 (count (:layers m)))))])

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Color is in the sketch mapping. Both layers inherit it --
;; three regression lines, one per species.

;; ### Rule 10: `view` options set view-level mapping
;;
;; The options map in `sk/view` scopes to that view. All layers on
;; the view inherit it; other views do not.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= {} (:mapping m))
                        (= :species (get-in m [:views 0 :mapping :color]))
                        (nil? (get-in m [:views 1 :mapping :color]))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Color is in the first view's mapping only. The first panel is
;; colored by species; the second has no color grouping.

;; ### Rule 11: `lay-*` options set layer-level mapping
;;
;; The options map in `sk/lay-*` scopes to that layer alone. Other
;; layers -- even on the same view -- do not see it.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
                        (= 1 (count (:layers m)))
                        (= {} (:mapping (first (:layers m))))))])

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Color is in the point layer's mapping. The sketch-level lm does
;; not see it -- one overall regression line, not three.

;; ### Rule 12: innermost scope wins; `nil` cancels
;;
;; When the same aesthetic appears at multiple levels, the narrowest
;; scope wins. Setting a key to `nil` cancels the inherited value.
;; This is useful when a broad color scheme should not apply to one
;; specific layer -- for instance, an overall regression line that
;; should ignore per-group coloring.

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil})
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :mapping :color]))
                        (= 2 (count (:layers m)))
                        (contains? (:mapping (second (:layers m))) :color)
                        (nil? (get-in m [:layers 1 :mapping :color]))))])

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; The view has `:color :species`. The point layer has no override,
;; so it inherits the color -- colored scatter. The lm layer
;; explicitly sets `:color nil`, canceling the inherited mapping --
;; one uncolored line.

;; Cancellation works across any scope boundary. Here a layer-level
;; `nil` cancels a sketch-level mapping:

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:color nil})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Points are uncolored (layer `nil` cancels sketch color). The lm
;; layer has no override, so it inherits the sketch color -- three
;; lines, one per species.

;; ---
;; ## Summary
;;
;; ### Layer placement
;;
;; | Rule | Condition | Result |
;; |:-----|:----------|:-------|
;; | 1 | `lay-*` with x/y columns | layer placed inside a view |
;; | 2 | `lay-*` without columns | layer at sketch level (all views) |
;; | 3 | both present | view layers first, then sketch layers |
;; | 4 | multiple views | each view gets sketch layers |
;;
;; ### View identity
;;
;; | Rule | Verb | Result |
;; |:-----|:-----|:-------|
;; | 5 | `view` | always creates a new view |
;; | 6 | `lay-*`, same columns | finds the most recent matching view |
;; | 7 | `lay-*`, different columns | creates a new view |
;; | 8 | `lay-*`, no columns, small dataset | auto-infers x, y, color |
;;
;; ### Mapping scope
;;
;; | Rule | Set by | Who sees it |
;; |:-----|:-------|:------------|
;; | 9 | `sketch` | all views, all layers |
;; | 10 | `view` options | all layers on that view |
;; | 11 | `lay-*` options | that layer only |
;; | 12 | -- | innermost scope wins; `nil` cancels |
