;; # Sketch Data Model
;;
;; Testing whether a single data model can represent ALL challenging
;; datavis use cases. We construct the sketches by hand (no API),
;; resolve them, and render through the real pipeline.

(ns scratch-datamodel
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
            [napkinsketch-book.datasets :as data]))

;; ## The Data Model
;;
;; A sketch is a map:
;;
;; | Key | What | Role |
;; |:----|:-----|:-----|
;; | `:data` | dataset | default data for all entries |
;; | `:shared` | map | aesthetics merged into everything |
;; | `:entries` | vector of maps | each describes a panel-layer seed |
;; | `:methods` | vector of maps | default methods for entries without `:methods` |
;; | `:opts` | map | plot-level options |
;;
;; Each **entry** is a map with column bindings (`:x`, `:y`, `:color`, ...),
;; and optionally its own `:methods` and/or `:data`.
;;
;; When an entry has `:methods`, it uses them.
;; When it doesn't, it uses the sketch-level `:methods`.
;; When neither has methods, inference fills in per entry.

;; ## Resolution

(defn expand-facets
  "Expand entries with :facet-col keyword into per-value entries with filtered data.
   Entries with a string :facet-col are already resolved — pass through."
  [entries data]
  (vec
   (mapcat
    (fn [entry]
      (if-let [fcol (:facet-col entry)]
        (if (keyword? fcol)
          ;; Keyword facet-col: expand into per-value entries
          (let [ds (or (:data entry) data)
                ds (if (tc/dataset? ds) ds (tc/dataset ds))
                vals (distinct (ds fcol))]
            (mapv (fn [fval]
                    (-> entry
                        (assoc :facet-col (str fval)
                               :data (tc/select-rows ds #(= (% fcol) fval)))))
                  vals))
          ;; String facet-col: already resolved, pass through
          [entry])
        [entry]))
    entries)))

(defn resolve-sketch
  "Resolve a sketch into a flat list of entry maps ready for rendering.
   Each entry is crossed with its own methods (or the sketch-level defaults).
   Entries with :facet-col as a keyword are expanded into per-value entries."
  [{:keys [data shared entries methods]}]
  (let [expanded (expand-facets entries data)
        default-methods (if (empty? methods) [{:mark :infer}] methods)]
    (vec
     (mapcat
      (fn [entry]
        (let [entry-methods (or (:methods entry) default-methods)
              base (merge shared (dissoc entry :methods))]
          (map (fn [m]
                 (let [resolved (merge base m)]
                   (-> resolved
                       (assoc :data (or (:data resolved) data))
                       (cond-> (= :infer (:mark resolved))
                         (-> (dissoc :mark :stat))))))
               entry-methods)))
      expanded))))

;; ## Bridge to rendering

(defn plot
  "Render a hand-built sketch through the real napkinsketch pipeline."
  [sk & [opts]]
  (let [views (resolve-sketch sk)
        plan (sketch-impl/views->plan views (or opts {}))]
    (sk/plan->figure plan :svg {})))

;; ## Datasets

(def iris data/iris)
(def tips data/tips)
(def cols [:sepal_length :sepal_width :petal_length])

;; ---
;; ## Use Case A: Per-group regression
;;
;; `:color :species` in shared creates groups.
;; Both point and lm inherit it → per-group regression.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ---
;; ## Use Case B: SPLOM with inference
;;
;; 9 entries, no methods → inference per entry.
;; Diagonal (x=y) gets histogram, off-diagonal gets scatter.

(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x cols, y cols] {:x x :y y}))
       :methods []
       :opts {}})

;; ---
;; ## Use Case B': SPLOM with explicit diagonal
;;
;; Diagonal entries carry their own `:methods` (histogram).
;; Off-diagonal entries use sketch-level methods (point + lm).
;; One list, correct grid order.

(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x cols, y cols]
                       (if (= x y)
                         {:x x :y y :methods [{:mark :bar :stat :bin}]}
                         {:x x :y y})))
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; 3 diagonal histograms + 6 off-diagonal × 2 methods = 15 resolved entries.

;; ---
;; ## Use Case D: Simpson's paradox
;;
;; Entry 1: uses sketch methods (per-group point + per-group lm).
;; Entry 2: carries its own method with `:color nil` → cancels shared
;; color → overall regression, no grouping.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; Three colored regression lines + one overall black line.

;; ---
;; ## Use Case E: Different data per layer
;;
;; Entry 1: scatter of all data (uses sketch :data).
;; Entry 2: regression on subset only (carries its own :data).

(def large-sepals (tc/select-rows iris #(> (% :sepal_length) 6.0)))

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :data large-sepals
                  :methods [{:mark :line :stat :lm}]}]
       :methods [{:mark :point :stat :identity :alpha 0.3}]
       :opts {}})

;; Points from all data, regression from subset only.

;; ---
;; ## Use Case G: Small multiples — same x, different y

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :petal_length}
                 {:x :sepal_length :y :petal_width}]
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; Three panels, all sharing x = sepal_length, different y columns.

;; ---
;; ## Use Case H: Three overlaid methods

(plot {:data iris
       :shared {}
       :entries [{:x :species :y :sepal_width}]
       :methods [{:mark :violin :stat :violin :alpha 0.3}
                 {:mark :point :stat :identity :jitter true :alpha 0.4}
                 {:mark :boxplot :stat :boxplot}]
       :opts {}})

;; ---
;; ## Use Case C: Stacked bars

(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :day}]
       :methods [{:mark :rect :stat :count :position :stack}]
       :opts {}})

;; ---
;; ## Use Case I: Per-layer aesthetic override
;;
;; Same as Simpson's paradox — one layer removes the shared color.
;; Here: colored points + colored lm + uncolored loess.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; Per-group points + per-group regression + one overall LOESS.

;; ---
;; ## Use Case J: Panel-specific annotation
;;
;; A faceted scatter where ONE facet panel gets a reference line.
;; The annotation entry uses a literal facet value to target one panel.
;; (Using :facet-col :species would expand to ALL panels.)

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :facet-col "setosa"
                  :methods [{:mark :rule-h :intercept 3.0}]}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ---
;; ## Use Case L: Paired samples with connecting lines
;;
;; Each subject measured twice. Lines connect before/after.

(def paired {:subject ["A" "A" "B" "B" "C" "C" "D" "D"]
             :time    ["before" "after" "before" "after"
                       "before" "after" "before" "after"]
             :score   [5 8 7 6 3 9 6 7]})

(plot {:data paired
       :shared {}
       :entries [{:x :time :y :score}]
       :methods [{:mark :line :stat :identity :group :subject}
                 {:mark :point :stat :identity}]
       :opts {}})

;; ---
;; ## Use Case N: Per-layer different stat
;;
;; Raw points + density-normalized histogram on same data.

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_length}]
       :methods [{:mark :bar :stat :bin :normalize :density :alpha 0.5}
                 {:mark :area :stat :kde}]
       :opts {}})

;; ---
;; ## Use Case: Faceted scatter + regression

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; 3 facet panels × 2 methods = scatter + regression per species.

;; ---
;; ## Use Case: Coord flip

(plot {:data iris
       :shared {}
       :entries [{:x :species :y :sepal_width :coord :flip}]
       :methods [{:mark :boxplot :stat :boxplot}]
       :opts {}})

;; ---
;; ## Use Case: Bubble chart (multiple aesthetics)

(plot {:data iris
       :shared {:color :species :size :petal_width}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.6}]
       :opts {}})

;; Color legend + size legend — two aesthetic mappings.

;; ---
;; ## Use Case: Bar chart with error bars

(def experiment {:condition ["A" "B" "C" "D"]
                 :mean [10.0 15.0 12.0 18.0]
                 :ci_lo [8.0 12.0 9.5 15.5]
                 :ci_hi [12.0 18.0 14.5 20.5]})

(plot {:data experiment
       :shared {}
       :entries [{:x :condition :y :mean}]
       :methods [{:mark :point :stat :identity}
                 {:mark :errorbar :stat :identity :ymin :ci_lo :ymax :ci_hi}]
       :opts {}})

;; ---
;; ## Use Case: Dodged bars

(plot {:data tips
       :shared {:color :sex}
       :entries [{:x :day}]
       :methods [{:mark :rect :stat :count :position :dodge}]
       :opts {}})

;; ---
;; ## Use Case: Stacked bar fill (proportions)

(plot {:data data/penguins
       :shared {:color :species}
       :entries [{:x :island}]
       :methods [{:mark :rect :stat :count :position :fill}]
       :opts {}})

;; ---
;; ## Use Case: Faceted with per-facet annotation
;;
;; Faceted scatter, but only ONE facet gets a reference line.
;; The annotation entry targets a specific facet value directly.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :facet-col "setosa"
                  :methods [{:mark :rule-h :intercept 3.5}]}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; The first entry expands to 3 facet panels.
;; The second entry has a literal facet value — it only appears in the setosa panel.

;; ---
;; ## Use Case: Multiple datasets in one figure
;;
;; Two completely independent datasets rendered as separate entries.

(def ds-a {:x [1 2 3 4 5] :y [2 4 3 5 4]})
(def ds-b {:x [1 2 3 4 5] :y [5 3 4 2 3]})

(plot {:data ds-a
       :shared {}
       :entries [{:x :x :y :y
                  :methods [{:mark :point :stat :identity}
                            {:mark :line :stat :lm}]}
                 {:x :x :y :y :data ds-b
                  :methods [{:mark :point :stat :identity}
                            {:mark :line :stat :lm}]}]
       :methods []
       :opts {}})

;; Two separate datasets, each with its own scatter + regression.

;; ---
;; ## Use Case: Rug plot on margins
;;
;; Scatter plot with marginal rug marks.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}
                 {:mark :rug :stat :identity}]
       :opts {}})

;; ---
;; ## Use Case: Density 2D contour
;;
;; A 2D density contour beneath a scatter plot.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :contour :stat :kde2d}
                 {:mark :point :stat :identity :alpha 0.3}]
       :opts {}})

;; ---
;; ## Use Case: Mixed facet + non-facet entries (broadcast)
;;
;; Faceted scatter panels + a non-faceted LOESS overlay.
;; The non-faceted entry has no :facet-col, so it broadcasts
;; into ALL panels (like ggplot2 layers).

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :point :stat :identity :alpha 0.2}
                            {:mark :line :stat :loess}]}]
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; Three panels: each gets the faceted scatter (filtered data)
;; PLUS the non-faceted overlay (all data) — points + LOESS broadcast.

;; ---
;; ## The Unified Grid
;;
;; A grid has two axes. Each axis is defined by a key and its distinct values.
;; An entry is placed in a cell by matching its value at the axis key.
;; Nil means broadcast — appear in all cells on that axis.
;;
;; The axis key can be:
;;
;; - `:x` or `:y` — **structural**: cells show different columns, same data
;; - `:facet-col` or `:facet-row` — **data**: cells show same columns, different data
;;
;; This is one mechanism. No special cases.

;; ---
;; ## Mixed Grid: species columns x measurement rows
;;
;; Columns = facet by species (data variation).
;; Rows = different y-variable (structural variation).
;; This was impossible before the unified grid — it required mixing
;; faceting and column variation in the same figure.

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; 3 species × 2 measurements = 6 panels.

;; ---
;; ## Mixed Grid + Broadcast Regression
;;
;; Same 3×2 grid, but add an overall regression line per y-variable
;; that broadcasts across all species columns.

(plot {:data iris
       :shared {}
       :entries [;; Faceted scatter: expands to 6 entries
                 {:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}
                 ;; Broadcast regression: no :facet-col → appears in all 3 columns
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm}]}
                 {:x :sepal_length :y :petal_width
                  :methods [{:mark :line :stat :lm}]}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; 6 panels, each with scatter + overall regression line.
;; The regression uses ALL data (not per-species) because the
;; broadcast entry inherits the sketch-level :data (all iris).

;; ---
;; ## Broadcast: Global LOESS over Faceted Scatter
;;
;; Faceted scatter with per-species data.
;; Non-faceted LOESS overlay broadcasts into all panels with full data.
;; Classic Simpson's paradox visualization — per-group vs overall.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess :color nil}]}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; Each panel: colored scatter + colored per-species regression (from filtered data)
;; + one black LOESS curve (from all data, broadcasting into each panel).

;; ---
;; ## Mixed Grid + Per-entry Methods + Broadcast
;;
;; The full power of the unified grid.
;; Columns: species (data). Rows: measurement (structural).
;; Faceted entries: scatter with filtered data.
;; Broadcast entries: LOESS with all data, per measurement.
;; Per-entry methods: one entry uses point, another uses line.

(plot {:data iris
       :shared {}
       :entries [;; Scatter per species per measurement
                 {:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}
                 ;; LOESS broadcast: all data, per measurement
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess}]}
                 {:x :sepal_length :y :petal_width
                  :methods [{:mark :line :stat :loess}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}]
       :opts {}})

;; 6 panels. Each: scatter from species subset + LOESS from all data.

;; ---
;; ## Challenge: Asymmetric SPLOM
;;
;; Different column sets for x (2) and y (3) = 2x3 grid.

(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x [:sepal_length :petal_length]
                           y [:sepal_width :petal_width :petal_length]]
                       {:x x :y y}))
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ---
;; ## Challenge: 2D Facet Grid
;;
;; Rows = sex, columns = day. Both axes are data variation.

(plot {:data tips
       :shared {:color :smoker}
       :entries (vec (for [d ["Sun" "Sat" "Thur" "Fri"]
                           s ["Female" "Male"]]
                       {:x :total_bill :y :tip
                        :facet-col d :facet-row s
                        :data (tc/select-rows tips #(and (= (% :day) d) (= (% :sex) s)))}))
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; 4 columns × 2 rows = 8 panels.

;; ---
;; ## Challenge: Broadcast Annotation across SPLOM
;;
;; A horizontal reference line that broadcasts into all 4 panels.

(plot {:data iris
       :shared {:color :species}
       :entries (conj
                 (vec (for [x [:sepal_length :sepal_width]
                            y [:sepal_length :sepal_width]] {:x x :y y}))
                 {:methods [{:mark :rule-h :intercept 3.0}]})
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; The annotation entry has no :x or :y, so it broadcasts into all 4 cells.

;; ---
;; ## Challenge: Faceted Stacked Bars

(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :day :facet-col :sex}]
       :methods [{:mark :rect :stat :count :position :stack}]
       :opts {}})

;; 2 panels (Male/Female), each with stacked day×smoker bars.

;; ---
;; ## Challenge: Faceted Simpson's Paradox
;;
;; Per-species scatter + per-species regression + overall LOESS,
;; all faceted by species. The LOESS entry also expands to per-species
;; but uses all data (no :data override) with :color nil.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width :facet-col :species
                  :methods [{:mark :line :stat :loess :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ---
;; ## Challenge: Time Series with Groups

(def ts-data {:month (mapcat (fn [_] (range 1 13)) (range 3))
              :city (mapcat #(repeat 12 %) ["NYC" "LA" "Chicago"])
              :temp (concat [32 35 45 55 65 75 80 78 68 55 42 34]
                            [58 60 62 68 72 78 82 83 80 72 64 59]
                            [25 28 38 50 62 72 78 76 66 52 38 27])})

(plot {:data ts-data
       :shared {:color :city}
       :entries [{:x :month :y :temp}]
       :methods [{:mark :line :stat :identity}
                 {:mark :point :stat :identity}]
       :opts {}})

;; 3 colored lines + 36 points. Grouping by :city in shared.

;; ---
;; ## Findings
;;
;; **Literal aesthetic values**: Putting `:size 4` in a method map doesn't work
;; as expected — the pipeline treats it as a column name. Literal aesthetic values
;; (fixed size, fixed alpha) need to go through `:alpha` (which works because it's
;; a known option key), while `:size` is interpreted as a column binding.
;; This is a design decision to revisit: should the data model distinguish
;; between column bindings and literal values?
;;
;; **Faceting requires expansion**: The pipeline expects `:facet-col "setosa"`
;; with pre-filtered data, not `:facet-col :species`. The `expand-facets`
;; function bridges this by splitting entries before resolution.
;;
;; **Unified grid**: The grid mechanism was unified — faceting and column
;; variation are the same concept: an axis with a key and its distinct values.
;; One placement algorithm, no sentinels, no special cases. Mixed grids
;; (structural + data axes) work naturally.
;;
;; **Mixed column types fail**: Entries with different column TYPES on the
;; same axis (e.g., categorical :species and numeric :sepal_length both as :x)
;; crash during domain computation — categorical values can't be treated as
;; numbers. This is a hard constraint: panels that share an axis must agree
;; on column type. Use `sk/arrange` for truly heterogeneous layouts.

;; ---
;; ## Summary
;;
;; The data model:
;;
;; ```clojure
;; {:data     dataset
;;  :shared   {:color :species}
;;  :entries  [{:x :a :y :b}                                ;; sketch-level methods
;;             {:x :a :y :a :methods [{:mark :histogram}]}]  ;; own methods
;;  :methods  [{:mark :point} {:mark :lm}]
;;  :opts     {}}
;; ```
;;
;; Resolution per entry:
;;
;; - Has `:methods` → cross with its own
;; - No `:methods` → cross with sketch-level
;; - Neither → inference
;; - Merge order: `shared → entry → method` (later wins, `nil` cancels)
;; - `:data` in entry overrides sketch `:data`
;;
;; One list, correct grid order, no patching.
;;
;; | Use case | How entries work |
;; |:---------|:-----------------|
;; | A: Per-group regression | 1 entry, 2 sketch methods, shared color |
;; | B: SPLOM inference | 9 entries, no methods → inference |
;; | B': SPLOM explicit | 9 entries, diagonal has own :methods |
;; | C: Stacked bars | 1 entry, 1 method with :position |
;; | D: Simpson's paradox | 2 entries sharing columns, 2nd cancels :color with nil |
;; | E: Different data | 2 entries, 2nd has :data override |
;; | G: Small multiples | 3 entries with different :y |
;; | H: Triple overlay | 1 entry, 3 sketch methods |
;; | I: Aesthetic override | 2 entries, 2nd overrides shared color |
;; | J: Faceted annotation | facet-col expansion + literal facet entry |
;; | L: Paired samples | 1 entry, :group in method |
;; | N: Different stats | 1 entry, 2 methods with different :stat |
;; | Faceted scatter | :facet-col expands to per-value entries |
;; | Coord flip | :coord in entry |
;; | Bubble chart | :size in shared |
;; | Error bars | :ymin/:ymax in method |
;; | Dodged bars | :position :dodge in method |
;; | Fill bars | :position :fill in method |
;; | Per-facet annotation | mixed :facet-col keyword + literal |
;; | Multiple datasets | per-entry :data override |
;; | Rug plot | 2 methods, different marks |
;; | Density 2D | :contour mark with :kde2d stat |
;; | Broadcast (LOESS overlay) | non-faceted entry broadcasts to all panels |
;; | Per-panel annotation (J) | literal :facet-col targets one cell |
;; | Mixed grid (species × measurement) | facet columns + structural rows |
;; | Mixed grid + broadcast | mixed grid with broadcast regression |
;; | Mixed + per-entry + broadcast | full unified grid power |
;; | Asymmetric SPLOM | 2×3 grid from unequal column sets |
;; | 2D facet grid | day columns × sex rows |
;; | SPLOM broadcast annotation | rule-h in all 4 cells |
;; | Faceted stacked bars | per-sex stacked day bars |
;; | Faceted Simpson's paradox | per-species facet with overall LOESS |
;; | Time series | lines + points, grouped by city |
