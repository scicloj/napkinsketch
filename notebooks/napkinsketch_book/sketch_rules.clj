;; # Sketch Rules
;;
;; This chapter is the definitive specification for how sketches
;; behave. It covers 24 rules in seven sections -- each demonstrated
;; with a printed sketch structure, a rendered plot, and verified
;; assertions that reach into `sk/draft` and `sk/plan` for precision.
;;
;; Read [The Sketch Model](./napkinsketch_book.sketch_model.html)
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
;; | `:opts` | map | `options`, `scale`, `coord`, `annotate`, `facet` |
;;
;; The API builds this record through lexical scope:
;;
;; - **layers** -- inside a view, or at sketch level
;; - **data** -- at sketch or view level
;; - **mappings** -- at sketch, view, or layer level
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
    (sk/lay-point :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 0 (count (:layers m)))
                        (= 1 (count (:views m)))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; The layer lives inside the view. Sketch-level `:layers` is empty.

;; ### Rule 2: `lay-*` without columns is sketch-level
;;
;; When `lay-*` is called without column names, the layer goes into
;; the sketch's `:layers` vector, not into any view.

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

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Two sketch-level layers (point and lm). The view has no own
;; layers -- it uses the sketch's.

;; ### Rule 3: view layers and sketch layers combine
;;
;; When a view has its own layers AND sketch-level layers exist,
;; the draft includes both. View layers come first, then sketch
;; layers are appended.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:layers m)))
                        (= :lm (:method (first (:layers m))))
                        (= 1 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Point is view-level (has columns); lm is sketch-level (no
;; columns). The rendered plot shows colored scatter plus one overall
;; regression line.
;;
;; The draft confirms the combination -- two draft layers, one per
;; (view, layer) pair. The point draft layer inherits `:color :species`
;; from its layer mapping; the lm draft layer does not.

(let [d (sk/draft (-> iris
                      (sk/lay-point :sepal-length :sepal-width {:color :species})
                      sk/lay-lm))]
  {:count (count d)
   :first-mark (:mark (first d))
   :first-color (:color (first d))
   :second-mark (:mark (second d))
   :second-color (:color (second d))})

(kind/test-last [(fn [m]
                   (and (= 2 (:count m))
                        (= :point (:first-mark m))
                        (= :species (:first-color m))
                        (= :line (:second-mark m))
                        (nil? (:second-color m))))])

;; ### Rule 4: sketch layers apply to every view
;;
;; Each view independently receives all sketch-level layers. Two
;; views with one sketch-level layer produce two panels, each with
;; that layer.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-point :petal-length :petal-width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= 2 (:lines s)))))])

;; Two views with different columns, each with its own point layer.
;; The sketch-level lm produces a regression line in each panel.
;;
;; The draft has four layers: 2 views x 2 layers (point + lm) each.

(let [d (sk/draft (-> iris
                      (sk/lay-point :sepal-length :sepal-width)
                      (sk/lay-point :petal-length :petal-width)
                      sk/lay-lm))]
  (count d))

(kind/test-last [(fn [n] (= 4 n))])

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
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

(-> iris
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

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-lm :sepal-length :sepal-width)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= 2 (count (:layers (first (:views m)))))
                        (= :point (:method (first (:layers (first (:views m))))))
                        (= :lm (:method (second (:layers (first (:views m))))))))])

(-> iris
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

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 2 (count (:views m)))
                        (= 0 (count (:layers m)))
                        (= :sepal-length (get-in m [:views 0 :mapping :x]))
                        (= :petal-length (get-in m [:views 1 :mapping :x]))))])

(-> iris
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
;; ## Data Scoping
;;
;; The sketch carries one dataset, shared by all views.

;; ### Rule 9: sketch data is shared by all views
;;
;; The dataset is set once -- by the first argument to `sk/sketch`,
;; `sk/view`, or `sk/lay-*`. All views in the sketch use it.

(-> iris
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two views, one sketch-level point layer, both using iris -- 150
;; points per panel, 300 total. The draft confirms both draft layers
;; share the same dataset:

(let [d (sk/draft (-> iris
                      (sk/view :sepal-length :sepal-width)
                      (sk/view :petal-length :petal-width)
                      sk/lay-point))]
  (mapv #(tc/row-count (:data %)) d))

(kind/test-last [(fn [v] (= [150 150] v))])

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
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (:color (:mapping m)))
                        (= 2 (count (:views m)))
                        (= 1 (count (:layers m)))))])

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal-length :sepal-width)
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Two views, one sketch-level layer, sketch-level color. Both
;; panels colored by species.
;;
;; The draft confirms every draft layer inherits the sketch mapping:

(let [d (sk/draft (-> (sk/sketch iris {:color :species})
                      (sk/view :sepal-length :sepal-width)
                      (sk/view :petal-length :petal-width)
                      sk/lay-point))]
  (mapv :color d))

(kind/test-last [(fn [v] (every? #(= :species %) v))])

;; ### Rule 11: `view` options set view-level mapping
;;
;; The options map in `sk/view` scopes to that view. All layers on
;; the view inherit it; other views do not.

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= {} (:mapping m))
                        (= :species (get-in m [:views 0 :mapping :color]))
                        (nil? (get-in m [:views 1 :mapping :color]))))])

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    (sk/view :petal-length :petal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Color is in the first view's mapping only. The first panel is
;; colored by species; the second has no color grouping.
;;
;; The draft confirms: first view's draft layer has color, second
;; view's does not.

(let [d (sk/draft (-> iris
                      (sk/view :sepal-length :sepal-width {:color :species})
                      (sk/view :petal-length :petal-width)
                      sk/lay-point))]
  (mapv :color d))

(kind/test-last [(fn [v] (= [:species nil] v))])

;; ### Rule 12: `lay-*` options set layer-level mapping
;;
;; The options map in `sk/lay-*` scopes to that layer alone. Other
;; layers -- even on the same view -- do not see it.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
                        (= 1 (count (:layers m)))
                        (= {} (:mapping (first (:layers m))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; Color is in the point layer's mapping. The sketch-level lm does
;; not see it -- one overall regression line, not three.
;;
;; The draft confirms the scope difference:

(let [d (sk/draft (-> iris
                      (sk/lay-point :sepal-length :sepal-width {:color :species})
                      sk/lay-lm))]
  (mapv :color d))

(kind/test-last [(fn [v] (= [:species nil] v))])

;; ### Rule 13: innermost scope wins; `nil` cancels
;;
;; When the same aesthetic appears at multiple levels, the narrowest
;; scope wins. Setting a key to `nil` cancels the inherited value.

;; The view has `:color :species`. The lm layer sets `:color nil`,
;; canceling the inherited mapping -- one uncolored regression line:

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

(-> iris
    (sk/view :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; The draft confirms the merge result -- the lm draft layer has
;; `:color nil` (overriding the view mapping):

(let [d (sk/draft (-> iris
                      (sk/view :sepal-length :sepal-width {:color :species})
                      sk/lay-point
                      (sk/lay-lm {:color nil})))]
  {:point-color (:color (first d))
   :lm-color (:color (second d))})

(kind/test-last [(fn [m]
                   (and (= :species (:point-color m))
                        (nil? (:lm-color m))))])

;; Cancellation works across any scope boundary. Here a layer-level
;; `nil` cancels a sketch-level mapping:

(-> (sk/sketch iris {:color :species})
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
;; ## Options and Modifiers
;;
;; Functions that set plot-level options in `:opts`. These do not
;; create views or layers -- they configure how the sketch renders.

;; ### Rule 14: `sk/options` sets plot-level options
;;
;; `sk/options` writes to `:opts`. Title, dimensions, theme,
;; palette, labels -- all go here. Options are NOT mappings -- they
;; do not flow into layers.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/options {:title "Iris Scatter"})
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= "Iris Scatter" (get-in m [:opts :title]))
                        (= {} (:mapping m))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:title "Iris Scatter"}))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= "Iris Scatter" (:title p))))])

;; ### Rule 15: `sk/scale` and `sk/coord` are plot-level
;;
;; Scale and coord go into `:opts`. They apply to all views.

(-> iris
    (sk/view :sepal-length :sepal-width)
    sk/lay-point
    (sk/scale :x :log)
    (sk/coord :flip)
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= {:type :log} (get-in m [:opts :x-scale]))
                        (= :flip (get-in m [:opts :coord]))))])

;; The plan confirms the specs propagate to the panel. Coord `:flip`
;; swaps axes -- the original x-scale (log) becomes the y-scale:

(let [plan (sk/plan (-> iris
                        (sk/view :sepal-length :sepal-width)
                        sk/lay-point
                        (sk/scale :x :log)
                        (sk/coord :flip)))
      panel (first (:panels plan))]
  {:coord (:coord panel)
   :y-scale-type (:type (:y-scale panel))})

(kind/test-last [(fn [m]
                   (and (= :flip (:coord m))
                        (= :log (:y-scale-type m))))])

;; ### Rule 16: `sk/annotate` adds reference marks
;;
;; Annotations go into `:opts :annotations`. They are self-contained
;; -- no interaction with views or layers.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/annotate (sk/rule-h 3.0))
    sk-summary)

(kind/test-last [(fn [m]
                   (and (= 1 (count (get-in m [:opts :annotations])))
                        (= :rule-h (:mark (first (get-in m [:opts :annotations]))))))])

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Rule 17: `sk/facet` sets the faceting column
;;
;; `sk/facet` and `sk/facet-grid` write facet specs into `:opts`.
;; The layout effect is in Section 7.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species)
    sk-summary)

(kind/test-last [(fn [m]
                   (= :species (get-in m [:opts :facet-col])))])

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
;; The rules above determine what `sk/draft` produces. The assembly
;; is a cross product of views and their applicable layers, with all
;; scope merged:
;;
;; ```
;; for each view:
;;   layers = concat(view-layers, sketch-layers)
;;   for each layer:
;;     data     = or(view-data, sketch-data)
;;     mappings = merge(sketch-mapping, view-mapping, layer-mapping)
;;     --> one draft layer
;; ```
;;
;; `concat` joins both layer lists. `merge` combines mappings --
;; innermost wins, `nil` erases. `sk/draft` makes this inspectable.

;; ### Rule 18: one draft layer per view x applicable-layer combination
;;
;; The draft layer count is predictable from the sketch structure:
;; for each view, count its own layers plus the sketch-level layers.

;; Two views: view 1 has one own layer (point), view 2 has none.
;; One sketch-level layer (lm). Draft: view 1 gets point + lm = 2,
;; view 2 gets lm = 1. Total: 3.

(def assembly-sketch
  (-> iris
      (sk/lay-point :sepal-length :sepal-width)
      (sk/view :petal-length :petal-width)
      sk/lay-lm))

(count (sk/draft assembly-sketch))

(kind/test-last [(fn [n] (= 3 n))])

;; Verify the marks in order: view 1 gets point (own) then lm
;; (sketch), view 2 gets lm (sketch):

(mapv :mark (sk/draft assembly-sketch))

(kind/test-last [(fn [v] (= [:point :line :line] v))])

;; ### Rule 19: each draft layer carries fully merged scope
;;
;; All scope levels are merged into each draft layer. The draft
;; layer has the resolved data, columns, mark, stat, and all
;; aesthetic mappings.

(let [sk (-> (sk/sketch iris {:color :species})
             (sk/lay-point :sepal-length :sepal-width))
      d (first (sk/draft sk))]
  (select-keys d [:x :y :color :mark]))

(kind/test-last [(fn [m]
                   (and (= :sepal-length (:x m))
                        (= :sepal-width (:y m))
                        (= :species (:color m))
                        (= :point (:mark m))))])

;; The sketch-level color, view-level columns, and layer-level mark
;; are all present in one flat map.

;; ---
;; ## Layout
;;
;; How views become panels in the rendered plot. These rules describe
;; what you see -- the decisions the plan makes from the sketch you
;; built.

;; ### Rule 20: each view becomes one panel
;;
;; Each view in the sketch produces one panel in the rendered plot.
;; Two views, two panels -- each with its own axis ranges.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-histogram :petal-length))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= 2 (count (:panels p)))))])

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

;; One panel: colored scatter plus overall regression line. Both
;; layers share the same axes.

;; ### Rule 22: faceting splits each view into panels by category
;;
;; `sk/facet` produces one panel per category value. `sk/facet-grid`
;; produces a row x column grid.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/facet :species))

(kind/test-last [(fn [v] (let [p (sk/plan v)]
                           (= 3 (count (:panels p)))))])

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
;; | | 8 | Small datasets -- columns auto-inferred |
;; | Data scoping | 9 | View data overrides sketch data |
;; | Mapping scope | 10 | `sketch` -- sketch-level mapping (all views, all layers) |
;; | | 11 | `view` options -- view-level mapping (one view) |
;; | | 12 | `lay-*` options -- layer-level mapping (one layer) |
;; | | 13 | Innermost scope wins; `nil` cancels |
;; | Options | 14 | `sk/options` -- plot options in `:opts` |
;; | | 15 | `sk/scale` and `sk/coord` -- plot-level rendering specs |
;; | | 16 | `sk/annotate` -- reference marks in `:opts` |
;; | | 17 | `sk/facet` -- faceting column in `:opts` |
;;
;; ### Assembly and layout rules
;;
;; | Section | Rule | Statement |
;; |:--------|:-----|:----------|
;; | Assembly | 18 | One draft layer per view x applicable-layer combination |
;; | | 19 | Each draft layer carries fully merged scope |
;; | Layout | 20 | Each view becomes one panel |
;; | | 21 | Layers within one view overlay in one panel |
;; | | 22 | Faceting splits views into panels by category |
;; | | 23 | Views sharing a variable align in the grid |
;; | | 24 | `sk/cross` generates the full view grid |
;;
;; ### The scope merge
;;
;; ```
;; data     = or(view-data, sketch-data)
;; mappings = merge(sketch-mapping, view-mapping, layer-mapping)
;; ```
;;
;; `merge` -- later wins. `nil` -- erases the key.
