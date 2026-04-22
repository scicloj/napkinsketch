;; # Sketch Rules
;;
;; This chapter is the definitive specification for how sketches
;; behave. It covers 24 rules in seven sections -- each demonstrated
;; with a rendered plot, a printed sketch structure, and verified
;; behavior.
;;
;; Read [Sketch Model](./napkinsketch_book.sketch_model.html)
;; and [Core Concepts](./napkinsketch_book.core_concepts.html) first.
;; This chapter is the proof layer -- every rule is reproducible
;; and tested.

(ns napkinsketch-book.sketch-rules
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Tablecloth -- dataset operations
   [tablecloth.api :as tc]
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
;; | `:opts` | map | `options`, `scale`, `coord`, `facet` |
;;
;; The API builds this record through lexical scope -- where you
;; write something determines who sees it:
;;
;; - **data** -- at sketch, view, or layer level; narrower wins
;; - **mappings** -- at sketch, view, or layer level; narrower wins
;; - **layers** -- at sketch or view level; sketch layers apply to every view
;;
;; The 24 rules below make this precise.

;; ## Setup

(def iris (rdatasets/datasets-iris))

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

;; ### Rule 1: `lay-*` with columns places the layer inside a view
;;
;; When `lay-*` receives column names, it creates (or finds) a view
;; and places the layer inside that view's `:layers` vector. The
;; sketch-level `:layers` is unaffected.

(-> iris
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 0 (count (:layers m)))
                        (= 1 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

;; The layer lives inside the view. Sketch-level `:layers` is empty.

;; ### Rule 2: `lay-*` without columns places the layer at sketch level
;;
;; When `lay-*` is called without column names, the layer goes into
;; the sketch's `:layers` vector, not into any view.

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s))
                                (= 2 (count d))
                                (= :point (:mark (first d)))
                                (= :sepal-length (:x (first d)))
                                (= :sepal-width (:y (first d)))
                                (= :identity (:stat (first d)))
                                (= :line (:mark (second d)))
                                (= :sepal-length (:x (second d)))
                                (= :sepal-width (:y (second d)))
                                (= :lm (:stat (second d))))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:layers m)))
                        (= :point (:method (first (:layers m))))
                        (= :lm (:method (second (:layers m))))
                        (nil? (:layers (first (:views m))))))])

;; Two sketch-level layers (point and lm). The view has no own
;; layers -- it uses the sketch's.

;; ### Rule 3: view layers and sketch layers combine
;;
;; When a view has its own layers AND sketch-level layers exist,
;; both apply to that view. View layers come first, then sketch
;; layers are appended.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s))
                                (= 2 (count d))
                                (= :point (:mark (first d)))
                                (= :species (:color (first d)))
                                (= :line (:mark (second d)))
                                (nil? (:color (second d))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= :lm (:method (first (:layers m))))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

;; Point is view-level (has columns); lm is sketch-level (no
;; columns). Colored scatter plus one overall regression line --
;; two layers rendered on one panel, one per (view, layer) pair.

;; ### Rule 4: sketch layers apply to every view
;;
;; Each view independently receives all sketch-level layers. Two
;; views with one sketch-level layer produce two panels, each with
;; that layer.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= 2 (:lines s))
                                (= 4 (count d)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width)
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= :lm (:method (first (:layers m))))
                        (= 2 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= 1 (count (:layers (second (:views m)))))))])

;; Two views with different columns, each with its own point layer.
;; The sketch-level lm produces a regression line in each panel --
;; four (view, layer) combinations total (2 views x 2 layers each).

;; ---
;; ## View Identity
;;
;; How views come into existence. The two verbs -- `view` and
;; `lay-*` -- handle this differently.

;; ### Rule 5: `view` always creates a new view
;;
;; `sk/view` always creates a new view, even when the columns match
;; an existing one.

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

;; Two views with identical columns -- two panels. `sk/view` always
;; creates, so repeated calls produce separate panels even when the
;; columns match.

;; ### Rule 6: `lay-*` finds the most recent matching view
;;
;; When `lay-*` receives columns that match an existing view, it
;; adds the layer to that view instead of creating a new one. When
;; several views match, it picks the most recent (last) one.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    (sk/lay-lm :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (= 1 (:lines s))
                                (= 2 (count d))
                                (= :point (:mark (first d)))
                                (= :line (:mark (second d))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/view :sepal-length :sepal-width)
    (sk/lay-lm :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))
                        (= 1 (count (:layers (second (:views m)))))
                        (= :lm (:method (first (:layers (second (:views m))))))))])

;; Two views with identical columns. `lay-point` created the first
;; view and placed itself there. `sk/view` created the second view.
;; `lay-lm` had two matches -- it appended to the most recent, so
;; one panel shows the scatter and the other shows the regression.

;; ### Rule 7: different columns create a new view
;;
;; When `lay-*` receives columns that do not match any existing
;; view, it creates a new view.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (pos? (:polygons s)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= :sepal-length (get-in m [:views 0 :mapping :x]))
                        (= :petal-length (get-in m [:views 1 :mapping :x]))))])

;; Two views, two panels: scatter on one, histogram on the other.

;; ### Rule 8: few-column datasets -- columns auto-inferred
;;
;; When the dataset has three or fewer columns and `lay-*` is called
;; without column names, columns are inferred by position:
;; first -- x, second -- y, third -- color.

;; Two columns -- inferred as x and y:

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

(-> {:x [1 2 3] :y [4 5 6]}
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= :x (get-in m [:views 0 :mapping :x]))
                        (= :y (get-in m [:views 0 :mapping :y]))
                        (= 1 (count (:layers (first (:views m)))))))])

;; Three columns -- third inferred as color:

(-> {:x [1 2 3] :y [4 5 6] :c ["a" "b" "a"]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

(-> {:x [1 2 3] :y [4 5 6] :c ["a" "b" "a"]}
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :x (get-in m [:views 0 :mapping :x]))
                        (= :y (get-in m [:views 0 :mapping :y]))
                        (= :c (get-in m [:views 0 :mapping :color]))))])

;; Datasets with four or more columns require explicit column names.

;; ---
;; ## Data Scoping
;;
;; Data follows the same scope rules as mappings. The sketch carries
;; a default dataset. A view or layer can override it with `:data`
;; in its options. Narrower wins.

;; ### Rule 9: data scopes like mappings -- narrower wins
;;
;; The sketch dataset is the default for all views and layers. A
;; view can carry its own dataset with `:data` in its options map.
;; A layer can do the same. Resolution order:
;; layer data > view data > sketch data.

;; Sketch data is the default -- all views share it:

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

;; Both views use iris -- 150 points per panel, 300 total.

;; View-level `:data` overrides sketch data for that view:

(def tiny {:x [1 2 3] :y [3 5 4]})

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :x :y {:data tiny})
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 153 (:points s))
                                (= 150 (tc/row-count (:data (first d))))
                                (= 3 (tc/row-count (:data (second d)))))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :x :y {:data tiny})
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

;; First view uses iris (150 points), second uses tiny (3 points).

;; Layer-level `:data` overrides view and sketch data for that layer:

(def iris-small (tc/head iris 10))

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:data iris-small})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 10 (:points s))
                                (= 1 (:lines s))
                                (= 10 (tc/row-count (:data (first d))))
                                (= 150 (tc/row-count (:data (second d)))))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:data iris-small})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= 2 (count (:layers m)))))])

;; The point layer uses iris-small (10 rows); the lm layer uses
;; iris (150 rows).

;; ---
;; ## Mapping Scope
;;
;; Where you write a mapping determines who sees it. There are three
;; levels, from broadest to narrowest.

;; ### Rule 10: `sketch` sets sketch-level mapping
;;
;; Mappings passed to `sk/sketch` live at sketch level. They flow
;; to every view and every layer.

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (every? #(= :species (:color %)) d))))])

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (:color (:mapping m)))
                        (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

;; Two views, one sketch-level layer, sketch-level color. Both
;; panels colored by species.

;; ### Rule 11: `view` options set view-level mapping
;;
;; The options map in `sk/view` scopes to that view. All layers on
;; the view inherit it; other views do not.

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= :species (:color (first d)))
                                (nil? (:color (second d))))))])

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= {} (:mapping m))
                        (= :species (get-in m [:views 0 :mapping :color]))
                        (nil? (get-in m [:views 1 :mapping :color]))))])

;; Color is in the first view's mapping only. The first panel is
;; colored by species; the second has no color grouping.

;; ### Rule 12: `lay-*` options set layer-level mapping
;;
;; The options map in `sk/lay-*` scopes to that layer alone. Other
;; layers -- even on the same view -- do not see it.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s))
                                (= :species (:color (first d)))
                                (nil? (:color (second d))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
                        (= 1 (count (:layers m)))
                        (= {} (:mapping (first (:layers m))))))])

;; Color is in the point layer's mapping. The sketch-level lm does
;; not see it -- one overall regression line, not three.

;; ### Rule 13: innermost scope wins; `nil` cancels
;;
;; When the same aesthetic appears at multiple levels, the narrowest
;; scope wins. Setting a key to `nil` cancels the inherited value.

;; The view has `:color :species`. The lm layer sets `:color nil`,
;; canceling the inherited mapping -- one uncolored regression line:

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s))
                                (= :species (:color (first d)))
                                (nil? (:color (second d))))))])

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil})
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :mapping :color]))
                        (= 2 (count (:layers m)))
                        (contains? (:mapping (second (:layers m))) :color)
                        (nil? (get-in m [:layers 1 :mapping :color]))))])

;; Cancellation works across any scope boundary. Here a layer-level
;; `nil` cancels a sketch-level mapping:

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:color nil})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/lay-point {:color nil})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (:color (:mapping m)))
                        (= 2 (count (:layers m)))
                        (contains? (:mapping (first (:layers m))) :color)
                        (nil? (get-in m [:layers 0 :mapping :color]))
                        (= {} (:mapping (second (:layers m))))))])

;; Points are uncolored (layer `nil` cancels sketch color). The lm
;; layer has no override, so it inherits the sketch color -- three
;; lines, one per species.

;; ---
;; ## Options and Modifiers
;;
;; Functions that set plot-level options in `:opts`. Unlike
;; mappings, layers, and data -- which live in the scope hierarchy
;; (see [Options and Scopes](./napkinsketch_book.options_and_scopes.html))
;; -- plot-level options configure the whole rendered plot and
;; cannot be scoped down to a view or layer.

;; ### Rule 14: `sk/options` sets plot-level options
;;
;; `sk/options` writes to `:opts`. Title, dimensions, theme,
;; palette, labels -- all go here. Options are NOT mappings -- they
;; do not flow into layers.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:title "Iris Scatter"}))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= "Iris Scatter" (:title p))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/options {:title "Iris Scatter"})
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= "Iris Scatter" (get-in m [:opts :title]))
                        (= {} (:mapping m))))])

;; ### Rule 15: `sk/scale` and `sk/coord` are plot-level
;;
;; Scale and coord go into `:opts`. They apply to all views.

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    (sk/scale :x :log)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               p (sk/plan v)
                               panel (first (:panels p))]
                           (and (= 150 (:points s))
                                (= :flip (:coord panel))
                                (= :log (:type (:y-scale panel))))))])

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    (sk/scale :x :log)
    (sk/coord :flip)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= {:type :log} (get-in m [:opts :x-scale]))
                        (= :flip (get-in m [:opts :coord]))))])

;; Coord `:flip` swaps axes -- the original x-scale (log) becomes
;; the y-scale in the rendered plot.

;; ### Rule 16: `sk/lay-rule-*` and `sk/lay-band-*` add reference marks
;;
;; Reference lines and shaded bands are layers, added with
;; `sk/lay-rule-h`, `sk/lay-rule-v`, `sk/lay-band-h`, `sk/lay-band-v`.
;; Position comes from the opts map (`:y-intercept` or `:x-intercept`
;; for rules; `:y-min`/`:y-max` or `:x-min`/`:x-max` for bands), not
;; from data columns. They scope like any other `lay-*`: a bare call
;; applies to every panel; passing `:x`/`:y` columns attaches the
;; annotation to one view.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-rule-h {:y-intercept 3.0}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-rule-h {:y-intercept 3.0})
    sk-summary)

(kind/test-last [(fn [m]
                   (let [layers (:layers m)
                         rule (some #(when (= :rule-h (:method %)) %) layers)]
                     (and (some? rule)
                          (= 3.0 (get-in rule [:mapping :y-intercept])))))])

;; ### Rule 17: `sk/facet` sets the faceting column
;;
;; `sk/facet` and `sk/facet-grid` write facet specs into `:opts`.
;; The layout effect is in the Layout section below.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species))

(kind/test-last [(fn [v] (= 3 (count (:panels (sk/plan v)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species)
    sk-summary)

(kind/test-last [(fn [m]
                   (= :species (get-in m [:opts :facet-col])))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet-grid :species :species))

(kind/test-last [(fn [v] (= 9 (count (:panels (sk/plan v)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet-grid :species :species)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:opts :facet-col]))
                        (= :species (get-in m [:opts :facet-row]))))])

;; ---
;; ## Assembly
;;
;; The rules above determine how the sketch turns into rendered
;; panels. Each view is crossed with its applicable layers, and all
;; scope is merged for every (view, layer) pair:
;;
;; Pseudocode:
;;
;; ```clojure
;; (for [view  (:views sketch)
;;       :let  [layers (concat (:layers view) (:layers sketch))]
;;       layer layers]
;;   {:data     (or (:data layer) (:data view) (:data sketch))
;;    :mappings (merge (:mapping sketch)
;;                     (:mapping view)
;;                     (:mapping layer))})
;; ```
;;
;; `concat` places view layers first, then sketch layers.
;; `or` picks the narrowest data. `merge` combines mappings --
;; innermost wins, `nil` erases.

;; ### Rule 18: one rendered layer per applicable (view, layer) pair
;;
;; For each view, the number of layers rendered equals its own
;; layers plus the sketch-level layers.

;; Two views: view 1 has one own layer (point), view 2 has none.
;; One sketch-level layer (lm). View 1 gets point + lm = 2,
;; view 2 gets lm = 1. Total: 3.

(def assembly-sketch
  (-> iris
      (sk/lay-point :sepal-length :sepal-width)
      (sk/view :petal-length :petal-width)
      sk/lay-lm))

assembly-sketch

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (sk/draft v)]
                           (and (= 2 (:panels s))
                                (= 150 (:points s))
                                (= 2 (:lines s))
                                (= 3 (count d))
                                (= [:point :line :line] (mapv :mark d)))))])

(sk-summary assembly-sketch)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= 2 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (nil? (:layers (second (:views m))))))])

;; ### Rule 19: each rendered layer carries fully merged scope
;;
;; All scope levels are merged for each rendered layer. The layer
;; sees the resolved data, columns, mark, stat, and all aesthetic
;; mappings.

(-> (sk/sketch iris {:color :species})
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               d (first (sk/draft v))]
                           (and (= 150 (:points s))
                                (= :sepal-length (:x d))
                                (= :sepal-width (:y d))
                                (= :species (:color d))
                                (= :point (:mark d)))))])

(-> (sk/sketch iris {:color :species})
    (sk/lay-point :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (:color (:mapping m)))
                        (= 1 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))))])

;; The sketch-level color, view-level columns, and layer-level mark
;; are all present when the layer is rendered.

;; ---
;; ## Layout
;;
;; How views become panels in the rendered plot. These rules describe
;; what you see -- the decisions the renderer makes from the sketch
;; you built.

;; ### Rule 20: each view becomes one panel
;;
;; Each view in the sketch produces one panel in the rendered plot.
;; Two views, two panels -- each with its own axis ranges.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= 2 (count (:panels p)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))))])

;; ### Rule 21: layers within one view overlay in one panel
;;
;; Multiple layers on the same view produce one panel with overlaid
;; marks -- not separate panels.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (and (= 1 (count (:panels p)))
                                (= 2 (count (:layers (first (:panels p))))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= 2 (count (:layers (first (:views m)))))))])

;; One panel: colored scatter plus overall regression line. Both
;; layers share the same axes.

;; ### Rule 22: faceting splits each view into panels by category
;;
;; `sk/facet` produces one panel per category value. `sk/facet-grid`
;; produces a row-by-column grid.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= 3 (count (:panels p)))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= :species (get-in m [:opts :facet-col]))))])

;; Three species, three panels.

;; ### Rule 23: views sharing a variable align in the grid
;;
;; When two views share the same x-column, they are placed in the
;; same grid column (stacked vertically). When they share the same
;; y-column, they are placed in the same grid row.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (and (= 2 (count (:panels p)))
                                (= 1 (get-in p [:grid :cols])))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :sepal-length)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= :sepal-length (get-in m [:views 0 :mapping :x]))
                        (= :sepal-length (get-in m [:views 1 :mapping :x]))))])

;; Two panels in one grid column -- shared x-axis for
;; sepal-length. This is a marginal distribution layout.

;; ### Rule 24: `sk/cross` generates the full view grid
;;
;; `sk/cross` produces all pairs of columns. When used with
;; `sk/view`, it creates a SPLOM (scatter plot matrix).

(def splom-cols [:sepal-length :sepal-width :petal-length])

(-> (sk/sketch iris {:color :species})
    (sk/view (sk/cross splom-cols splom-cols)))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (and (= 9 (count (:panels p)))
                                (= 3 (get-in p [:grid :rows]))
                                (= 3 (get-in p [:grid :cols])))))])

(-> (sk/sketch iris {:color :species})
    (sk/view (sk/cross splom-cols splom-cols))
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 9 (count (:views m)))
                        (= :species (:color (:mapping m)))))])

;; Nine panels (3 x 3 grid), colored by species. Off-diagonal panels
;; are scatter plots; diagonal panels are histograms (inferred when
;; x and y are the same column).

;; ---
;; ## Summary
;;
;; ### Construction rules
;;
;; | Section | Rule | Statement |
;; |:--------|:-----|:----------|
;; | Layer placement | 1 | `lay-*` with columns -- layer inside a view |
;; | | 2 | `lay-*` without columns -- sketch-level layer |
;; | | 3 | View layers + sketch layers combine (view first) |
;; | | 4 | Sketch layers apply to every view |
;; | View identity | 5 | `view` always creates a new view |
;; | | 6 | `lay-*` finds most recent matching view |
;; | | 7 | Different columns create a new view |
;; | | 8 | Few-column datasets -- columns auto-inferred |
;; | Data scoping | 9 | Data scopes like mappings -- narrower wins |
;; | Mapping scope | 10 | `sketch` -- sketch-level mapping (all views, all layers) |
;; | | 11 | `view` options -- view-level mapping (one view) |
;; | | 12 | `lay-*` options -- layer-level mapping (one layer) |
;; | | 13 | Innermost scope wins; `nil` cancels |
;; | Options | 14 | `sk/options` -- plot options in `:opts` |
;; | | 15 | `sk/scale` and `sk/coord` -- plot-level rendering specs |
;; | | 16 | `sk/lay-rule-*` / `sk/lay-band-*` -- reference marks as layers |
;; | | 17 | `sk/facet` -- faceting column in `:opts` |
;;
;; ### Assembly and layout rules
;;
;; | Section | Rule | Statement |
;; |:--------|:-----|:----------|
;; | Assembly | 18 | One rendered layer per applicable (view, layer) pair |
;; | | 19 | Each rendered layer carries fully merged scope |
;; | Layout | 20 | Each view becomes one panel |
;; | | 21 | Layers within one view overlay in one panel |
;; | | 22 | Faceting splits views into panels by category |
;; | | 23 | Views sharing a variable align in the grid |
;; | | 24 | `sk/cross` generates the full view grid |
;;
;; ### The scope merge
;;
;; Pseudocode:
;;
;; ```clojure
;; {:data     (or (:data layer) (:data view) (:data sketch))
;;  :mappings (merge (:mapping sketch)
;;                   (:mapping view)
;;                   (:mapping layer))}
;; ```
;;
;; `merge` -- later wins. `nil` -- erases the key.
