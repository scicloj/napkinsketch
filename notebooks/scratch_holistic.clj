;; # Holistic Data Model
;;
;; One notebook. One data model. All the use cases.
;;
;; Goal: demonstrate that a single composable data model handles
;; the full diversity of real-world plotting, and that a small set
;; of API verbs can produce it.

(ns scratch-holistic
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dfn]
            [java-time.api :as jt]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [napkinsketch-book.datasets :as data]))

(def iris data/iris)
(def tips data/tips)
(def penguins data/penguins)

;; ---
;; ## The Data Model
;;
;; A sketch is a map:
;;
;; | Key | Type | Role |
;; |:----|:-----|:-----|
;; | `:data` | dataset | default data for all entries |
;; | `:shared` | map | aesthetics merged into every entry |
;; | `:entries` | vector of maps | each describes a panel-layer seed |
;; | `:methods` | vector of maps | default methods for entries without `:methods` |
;; | `:opts` | map | plot-level options |
;;
;; Each **entry** maps column bindings (`:x`, `:y`, `:color`, ...) and
;; optionally carries its own `:methods`, `:data`, or `:facet-col`.
;;
;; Each **method** maps `:mark`, `:stat`, and layer options
;; (`:alpha`, `:position`, etc.).
;;
;; Resolution: `shared → entry → method` (later wins, `nil` cancels).

;; ---
;; ## Resolution

(defn expand-facets
  "Expand entries with keyword `:facet-col` and/or `:facet-row` into per-value entries.
   Handles 1D and 2D faceting. String facet values pass through unchanged."
  [entries data]
  (let [ds (if (tc/dataset? data) data (tc/dataset data))]
    (vec
     (mapcat
      (fn [entry]
        (let [fcol (:facet-col entry)
              frow (:facet-row entry)
              need-col? (keyword? fcol)
              need-row? (keyword? frow)]
          (cond
            (and need-col? need-row?)
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [cv (distinct (ed fcol)), rv (distinct (ed frow))]
                (-> entry
                    (assoc :facet-col (str cv) :facet-row (str rv)
                           :data (tc/select-rows ed (fn [r] (and (= (r fcol) cv) (= (r frow) rv))))))))

            need-col?
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [cv (distinct (ed fcol))]
                (-> entry
                    (assoc :facet-col (str cv)
                           :data (tc/select-rows ed (fn [r] (= (r fcol) cv)))))))

            need-row?
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [rv (distinct (ed frow))]
                (-> entry
                    (assoc :facet-row (str rv)
                           :data (tc/select-rows ed (fn [r] (= (r frow) rv)))))))

            :else [entry])))
      entries))))

(defn facet-wrap
  "Expand an entry into a wrapped grid. Categories from `group-col` are
   arranged left-to-right, top-to-bottom with `ncols` columns."
  [entry data group-col ncols]
  (let [ds (if (tc/dataset? data) data (tc/dataset data))
        categories (vec (distinct (ds group-col)))]
    (vec (for [[i cat] (map-indexed vector categories)]
           (-> entry
               (assoc :facet-col (str (mod i ncols))
                      :facet-row (str (quot i ncols))
                      :data (tc/select-rows ds (fn [r] (= (r group-col) cat)))))))))

(defn resolve-sketch
  "Resolve a sketch into a flat list of view maps for the pipeline."
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

(defn plot
  "Render a sketch through the napkinsketch pipeline."
  [sk & [opts]]
  (let [views (resolve-sketch sk)
        plan (sketch-impl/views->plan views (or opts {}))]
    (sk/plan->figure plan :svg {})))

;; ================================================================
;; # PART A: Validated Use Cases
;; ================================================================

;; ---
;; ## 1. Single Panel

;; ### Scatter
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ### Multi-layer: scatter + regression
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ### Triple overlay: violin + jitter + boxplot
(plot {:data iris
       :shared {}
       :entries [{:x :species :y :sepal_width}]
       :methods [{:mark :violin :stat :violin :alpha 0.3}
                 {:mark :point :stat :identity :jitter true :alpha 0.4}
                 {:mark :boxplot :stat :boxplot}]
       :opts {}})

;; ---
;; ## 2. Aesthetic Override (nil Cancellation)

;; ### Simpson's paradox
;;
;; Entry 1 inherits `:color :species` → per-group scatter + lm.
;; Entry 2 carries `:color nil` → cancels grouping → overall regression.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ### Per-layer aesthetic override: colored scatter + uncolored LOESS
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ---
;; ## 3. Per-Entry Data Override

;; Scatter of all data + regression from a subset only.

(def large-sepals (tc/select-rows iris #(> (% :sepal_length) 6.0)))

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :data large-sepals
                  :methods [{:mark :line :stat :lm}]}]
       :methods [{:mark :point :stat :identity :alpha 0.3}]
       :opts {}})

;; ---
;; ## 4. Multi-Variable Grid (Structural Axes)

;; ### Small multiples: same x, different y
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :petal_length}
                 {:x :sepal_length :y :petal_width}]
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; ### SPLOM with inference
(def cols [:sepal_length :sepal_width :petal_length])

(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x cols y cols] {:x x :y y}))
       :methods []
       :opts {}})

;; ### SPLOM with explicit diagonal
;;
;; Diagonal entries carry their own `:methods` (density curves).
;; Off-diagonal entries use sketch-level methods (scatter + lm).
;; Inference would put histograms on the diagonal — per-entry methods
;; let you choose something different.

(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x cols y cols]
                       (if (= x y)
                         {:x x :y y :methods [{:mark :area :stat :kde :alpha 0.4}]}
                         {:x x :y y})))
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ### Asymmetric grid: 2 x-columns × 3 y-columns
(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x [:sepal_length :petal_length]
                           y [:sepal_width :petal_width :petal_length]]
                       {:x x :y y}))
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ---
;; ## 5. Faceted Grid (Data Axes)

;; ### Facet by column (horizontal panels)
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ### Facet by row (vertical panels)
(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :total_bill :y :tip :facet-row :sex}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ### 2D facet grid: day × sex
;;
;; Keyword `:facet-col` and `:facet-row` are expanded automatically by
;; `resolve-sketch` — no manual `for` or `tc/select-rows` needed.
(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :total_bill :y :tip :facet-col :day :facet-row :sex}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ---
;; ## 6. Broadcast
;;
;; An entry without grid coordinates appears in ALL panels.

;; ### Non-faceted LOESS broadcasts into faceted panels
(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess}]}]
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; ### Annotation in one specific panel
;;
;; Literal `:facet-col "setosa"` targets one cell.
;; If `:facet-col` were absent, it would broadcast to all.
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :facet-col "setosa"
                  :methods [{:mark :rule-h :intercept 3.0}]}]
       :methods [{:mark :point :stat :identity}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ### Targeted annotation in SPLOM
;;
;; A reference line at `y = 3.0` only makes sense in panels where `:y` is
;; `:sepal_width` (range 2.0–4.4). Specifying `:y :sepal_width` targets
;; only the matching row.
(plot {:data iris
       :shared {:color :species}
       :entries (conj
                 (vec (for [x [:sepal_length :sepal_width]
                            y [:sepal_length :sepal_width]] {:x x :y y}))
                 {:y :sepal_width
                  :methods [{:mark :rule-h :intercept 3.0}]})
       :methods [{:mark :point :stat :identity}]
       :opts {}})

;; ---
;; ## 7. Mixed Grid (Structural + Data Axes)
;;
;; The unified grid enables mixing column variation with faceting.
;; Columns = species (data axis), rows = measurement (structural axis).

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ### Mixed grid + broadcast regression
(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm}]}
                 {:x :sepal_length :y :petal_width
                  :methods [{:mark :line :stat :lm}]}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ---
;; ## 8. Bar Charts and Positioning

;; ### Stacked bars
(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :day}]
       :methods [{:mark :rect :stat :count :position :stack}]
       :opts {}})

;; ### Dodged bars
(plot {:data tips
       :shared {:color :sex}
       :entries [{:x :day}]
       :methods [{:mark :rect :stat :count :position :dodge}]
       :opts {}})

;; ### Fill bars (proportions)
(plot {:data penguins
       :shared {:color :species}
       :entries [{:x :island}]
       :methods [{:mark :rect :stat :count :position :fill}]
       :opts {}})

;; ### Faceted stacked bars
(plot {:data tips
       :shared {:color :smoker}
       :entries [{:x :day :facet-col :sex}]
       :methods [{:mark :rect :stat :count :position :stack}]
       :opts {}})

;; ---
;; ## 9. Specialty Marks

;; ### Error bars
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

;; ### Rug + scatter
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}
                 {:mark :rug :stat :identity}]
       :opts {}})

;; ### Density contour + scatter
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :contour :stat :kde2d}
                 {:mark :point :stat :identity :alpha 0.3}]
       :opts {}})

;; ### Histogram + KDE
(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_length}]
       :methods [{:mark :bar :stat :bin :normalize :density :alpha 0.5}
                 {:mark :area :stat :kde}]
       :opts {}})

;; ### Paired samples with connecting lines
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

;; ### Coord flip
(plot {:data iris
       :shared {}
       :entries [{:x :species :y :sepal_width :coord :flip}]
       :methods [{:mark :boxplot :stat :boxplot}]
       :opts {}})

;; ### Bubble chart (multiple aesthetics)
(plot {:data iris
       :shared {:color :species :size :petal_width}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.6}]
       :opts {}})

;; ### Time series
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

;; ### Temporal x-axis
(def daily
  (tc/dataset {:date (mapv #(jt/local-date 2024 1 %) (range 1 29))
               :value (mapv (fn [i] (+ 10.0 (* 5.0 (Math/sin (/ i 3.0))))) (range 28))}))

(plot {:data daily
       :shared {}
       :entries [{:x :date :y :value}]
       :methods [{:mark :line :stat :identity}
                 {:mark :point :stat :identity}]
       :opts {}})

;; ---
;; ## 10. Faceted Simpson's Paradox
;;
;; Combining: faceting + broadcast + per-entry methods.
;; Each panel: per-species scatter + per-species lm (from filtered data)
;; + overall lm (from ALL data, broadcasting into every panel).
;; The overall trend visibly diverges from the per-species trends.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 ;; Broadcast: no `:facet-col` → appears in all panels with full data
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ================================================================
;; # PART B: Addressing the Gaps
;; ================================================================

;; ---
;; ## Gap 1: Wide Data → Tidy Data
;;
;; Multiple y-columns in one panel. Entries with different `:y` values
;; create panels. The solution: reshape to long format before plotting.
;; This is the tidy data principle — the data model rewards tidy data.

(def wide
  (tc/dataset {:time [1 2 3 4 5 6]
               :nyc  [30 35 45 55 65 75]
               :la   [58 60 62 68 72 78]
               :chi  [25 28 38 50 62 72]}))

;; Reshape to long — one call to `tc/pivot->longer`:
(def long-temps
  (tc/pivot->longer wide [:nyc :la :chi]
                    {:target-columns [:city]
                     :value-column-name :temp}))

long-temps

(plot {:data long-temps
       :shared {:color :city}
       :entries [{:x :time :y :temp}]
       :methods [{:mark :line :stat :identity}
                 {:mark :point :stat :identity}]
       :opts {}})

;; Three lines, one panel. The reshape IS the solution — not a workaround.
;; Tablecloth's `tc/pivot->longer` makes this a one-liner for real data.

;; ---
;; ## Gap 2: Facet Wrap
;;
;; 12 categories wrapped into a 4×3 grid. Currently requires manual
;; row/col assignment. A `facet-wrap` helper makes it clean.

(def random-groups
  (tc/dataset {:x (vec (repeatedly 120 rand))
               :y (vec (repeatedly 120 rand))
               :cat (vec (mapcat #(repeat 10 (str "group-" %)) (range 12)))}))

;; `facet-wrap` (defined above) takes an entry, data, group column, and column count:
(plot {:data random-groups
       :shared {}
       :entries (facet-wrap {:x :x :y :y} random-groups :cat 4)
       :methods [{:mark :point :stat :identity :alpha 0.6}]
       :opts {}})

;; 12 panels in a 4×3 grid. One line — as concise as ggplot2's `facet_wrap`.

;; ---
;; ## Gap 3: Dumbbell Chart
;;
;; Paired before/after comparison. Reshape to long format,
;; use `:group` to connect pairs with lines.

(def before-after
  (tc/dataset {:category ["A" "A" "B" "B" "C" "C" "D" "D"]
               :condition ["before" "after" "before" "after"
                           "before" "after" "before" "after"]
               :value [10 15 12 18 8 14 20 22]}))

(plot {:data before-after
       :shared {}
       :entries [{:x :category :y :value}]
       :methods [{:mark :line :stat :identity :group :category}
                 {:mark :point :stat :identity :color :condition}]
       :opts {}})

;; Each category gets a connecting line, points colored by condition.

;; ---
;; ## Gap 4: Stacked Area
;;
;; Revenue by product over time, stacked.

(def revenue
  (tc/dataset {:year [2020 2020 2020 2021 2021 2021 2022 2022 2022]
               :product ["A" "B" "C" "A" "B" "C" "A" "B" "C"]
               :amount [100 80 60 120 90 70 130 110 80]}))

(plot {:data revenue
       :shared {:color :product}
       :entries [{:x :year :y :amount}]
       :methods [{:mark :area :stat :identity :position :stack}]
       :opts {}})

;; ---
;; ## Gap 5: Stat Output Selection (after_stat)
;;
;; The `:bin` stat produces both `:count` and `:density`.
;; Currently the method always uses `:count`.
;;
;; **Proposed**: a method can include `:y :density` to override the
;; stat's default output column.
;;
;; This doesn't work in the current pipeline yet, but the DATA MODEL
;; already supports it — `:y :density` in the method map is just a key
;; that participates in `shared → entry → method` resolution.
;;
;; What it WOULD look like:
;;
;; ```clojure
;; {:entries [{:x :sepal_length}]
;;  :methods [{:mark :bar :stat :bin :y :density}]}
;; ```
;;
;; For now, the workaround is stats-as-data (compute the stat,
;; then plot the output):

(defn stat-bin [ds x-col]
  (let [hist (fastmath.stats/histogram (ds x-col)
                                       (:bin-method scicloj.napkinsketch.impl.defaults/defaults))
        bins (:bins-maps hist)
        n (reduce + 0 (map :count bins))]
    (tc/dataset
     {x-col    (mapv #(/ (+ (double (:min %)) (double (:max %))) 2.0) bins)
      :count   (mapv :count bins)
      :density (mapv (fn [b]
                       (let [bw (- (double (:max b)) (double (:min b)))]
                         (if (and (pos? n) (pos? bw))
                           (/ (double (:count b)) (* n bw)) 0.0)))
                     bins)})))

;; The stat output — visible as data:
(stat-bin iris :sepal_length)

;; Plot density from stat output:
(-> (stat-bin iris :sepal_length)
    (sk/lay-point :sepal_length :density))

;; Plot count from stat output:
(-> (stat-bin iris :sepal_length)
    (sk/lay-point :sepal_length :count))

;; Same stat, different column, different plot. No special syntax.

;; ---
;; ## Gap 6: Per-Group Stat-as-Data
;;
;; When the user needs explicit control — compute the stat per group
;; using tablecloth, then render the output.

(def per-species-bins
  (-> iris
      (tc/group-by [:species])
      (tc/process-group-data #(stat-bin % :sepal_length))
      (tc/ungroup {:add-group-as-column true})))

per-species-bins

;; Overlaid per-species density curves:
(plot {:data per-species-bins
       :shared {:color :species}
       :entries [{:x :sepal_length :y :density}]
       :methods [{:mark :area :stat :identity :alpha 0.4}]
       :opts {}})

;; ================================================================
;; # PART C: The Unified Grid
;; ================================================================
;;
;; All grid types are one mechanism: two axes, each with a key and values.
;;
;; | Grid type | Column axis | Row axis |
;; |:----------|:------------|:---------|
;; | Single | nil | nil |
;; | Small multiples | nil | `:y` (structural) |
;; | SPLOM | `:x` (structural) | `:y` (structural) |
;; | Facet-col | `:facet-col` (data) | nil |
;; | Facet-grid | `:facet-col` (data) | `:facet-row` (data) |
;; | Mixed | `:facet-col` (data) | `:y` (structural) |
;; | Facet-wrap | `:facet-col` (data) | `:facet-row` (data) — auto-assigned |
;;
;; Placement: match view's value at axis key. `nil` = broadcast.
;; Structural axes get per-row/col domains. Data axes get global domains.

;; ================================================================
;; # PART D: What the API Verbs Produce
;; ================================================================
;;
;; The data model is the target. The API verbs compose to produce it.
;;
;; | Verb | What it does | Data model effect |
;; |:-----|:-------------|:------------------|
;; | `sk/view` | Set column bindings | add to `:entries` |
;; | `sk/lay-*` | Add a method | add to `:methods` |
;; | `sk/facet` | Split by column | set `:facet-col` on entries |
;; | `sk/facet-wrap` | Wrap into grid | set `:facet-col`/`:facet-row` |
;; | `sk/options` | Plot-level options | merge into `:opts` |
;; | `sk/scale` | Axis scale type | set `:x-scale`/`:y-scale` |
;; | `sk/coord` | Coordinate transform | set `:coord` |
;; | `sk/cross` | Generate column pairs | produce entries |
;;
;; The verbs don't need to know about the grid.
;; The grid is inferred from what varies across the resolved entries.

;; ================================================================
;; # PART E: Summary of Findings
;; ================================================================
;;
;; ## What works
;;
;; The data model handles 25+ use cases across:
;;
;; - Single and multi-layer composition
;; - Aesthetic override with nil cancellation
;; - Per-entry data and method overrides
;; - Structural grids (SPLOM, small multiples, asymmetric)
;; - Data grids (facet by column, row, or both)
;; - Mixed grids (structural + data axes)
;; - Broadcast (non-faceted entries in all panels)
;; - Targeted annotations (literal facet value)
;; - Bar positioning (stack, dodge, fill)
;; - Specialty marks (error bars, rug, contour, paired lines, etc.)
;; - Temporal data
;;
;; ## Gaps addressed by data preparation
;;
;; - **Wide data** → reshape to long format (`tc/pivot->longer`)
;; - **Per-group stats** → `tc/group-by` → `tc/process-group-data` → `tc/ungroup`
;; - **Stat output selection** → compute stat as dataset, plot specific columns
;; - **Stat chaining** → `(-> ds stat1 stat2)` then plot the result
;;
;; ## Gaps addressed by helpers
;;
;; - **Facet wrap** → `facet-wrap` assigns row/col from category index
;;
;; ## Remaining design questions
;;
;; - **Stat output selection in the model**: `{:stat :bin :y :density}`
;;   would let the method map override which stat column is used.
;;   The data model supports it (`:y :density` is just a key); the pipeline
;;   doesn't honor it yet.
;;
;; - **Ribbon/fill-between mark**: No way to express "fill between
;;   `:y-upper` and `:y-lower`" as a mark. Would need a `:ribbon` mark.
;;
;; - **Literal vs binding**: `:size 4` (literal) vs `:size :petal_width`
;;   (binding). Convention: keywords = bindings, scalars = literals.
;;
;; - **Dual y-axes**: `:y-axis :left`/`:right` to overlay different scales.
;;
;; - **Custom scale transforms**: Only `:linear`, `:log`, `:categorical`.
;;   No `:sqrt`, `:reverse`, `:logit`.
;;
;; ================================================================
;; # Stress Tests
;; ================================================================

;; ---
;; ## Per-facet method variation
;;
;; Each facet gets a different smoother: setosa → lm, versicolor → loess,
;; virginica → points only. Uses literal `:facet-col` with per-entry methods.

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width :facet-col "setosa"
                  :data (tc/select-rows iris (fn [r] (= (r :species) "setosa")))
                  :methods [{:mark :line :stat :lm}]}
                 {:x :sepal_length :y :sepal_width :facet-col "versicolor"
                  :data (tc/select-rows iris (fn [r] (= (r :species) "versicolor")))
                  :methods [{:mark :line :stat :loess}]}]
       :methods [{:mark :point :stat :identity :alpha 0.5}]
       :opts {}})

;; ---
;; ## Regression with SE confidence band

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.3}
                 {:mark :line :stat :lm :se true}]
       :opts {}})

;; ---
;; ## Different methods per panel in small multiples
;;
;; Row 1: scatter + lm. Row 2: scatter + loess. Row 3: scatter only.
;; Per-entry `:methods` with a structural grid.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width
                  :methods [{:mark :point :stat :identity :alpha 0.4}
                            {:mark :line :stat :lm}]}
                 {:x :sepal_length :y :petal_width
                  :methods [{:mark :point :stat :identity :alpha 0.4}
                            {:mark :line :stat :loess}]}
                 {:x :sepal_length :y :petal_length
                  :methods [{:mark :point :stat :identity :alpha 0.4}]}]
       :methods []
       :opts {}})

;; ---
;; ## Dodged points + dodged error bars

(def summary-ds
  (let [ds (tc/dataset
            {:species ["setosa" "setosa" "versicolor" "versicolor" "virginica" "virginica"]
             :measure ["sepal" "petal" "sepal" "petal" "sepal" "petal"]
             :mean [5.0 1.5 5.9 4.3 6.6 5.6]
             :se [0.35 0.17 0.52 0.47 0.64 0.55]})]
    (-> ds
        (tc/add-column :ymin (dfn/- (ds :mean) (ds :se)))
        (tc/add-column :ymax (dfn/+ (ds :mean) (ds :se))))))

(plot {:data summary-ds
       :shared {:color :measure}
       :entries [{:x :species :y :mean}]
       :methods [{:mark :point :stat :identity :position :dodge}
                 {:mark :errorbar :stat :identity :ymin :ymin :ymax :ymax :position :dodge}]
       :opts {}})

;; ---
;; ## The ultimate stress test
;;
;; Mixed grid + broadcast + per-entry methods + nil cancellation + SE bands
;; + targeted annotation. Everything in one figure.
;;
;; Grid: 3 species columns × 2 measurement rows (mixed: data + structural).
;; Each cell: colored scatter + colored lm with SE band.
;; Broadcast: overall LOESS (black) in all panels.
;; Annotation: reference line in setosa/sepal_width cell only.

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 {:x :sepal_length :y :petal_width :facet-col :species}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :loess :color nil}]}
                 {:x :sepal_length :y :petal_width
                  :methods [{:mark :line :stat :loess :color nil}]}
                 {:x :sepal_length :y :sepal_width :facet-col "setosa"
                  :methods [{:mark :rule-h :intercept 3.5}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm :se true}]
       :opts {}})

;; 6 panels. Each: scatter + lm with SE band (colored) + LOESS (black).
;; Reference line only in the top-left cell.
;; This exercises: unified grid, broadcast, per-panel annotation,
;; per-entry methods, nil cancellation, SE bands, and mixed axes.
