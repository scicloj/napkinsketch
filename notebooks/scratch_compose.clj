;; # Stat Composition Prototype
;;
;; Exploring: can stat composition live in the data model?
;; And can we design verbs that produce the data model?

(ns scratch-compose
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dfn]
            [java-time.api :as jt]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [fastmath.stats :as fstats]
            [fastmath.ml.regression :as regr]
            [fastmath.interpolation.acm :as interp]
            [napkinsketch-book.datasets :as data]))

(def iris data/iris)
(def tips data/tips)

;; ================================================================
;; # Part 1: Stat Functions as Dataset Transformations
;; ================================================================
;;
;; Each stat is a function: `(stat-fn ds x-col y-col opts)` → dataset.
;; These are the building blocks. Registered by keyword.

(def stat-registry
  "Registry of stat functions. Each takes `(ds x-col y-col opts)` and returns a dataset."
  {:identity (fn [ds x-col y-col _] ds)

   :bin (fn [ds x-col _ opts]
          (let [hist (fstats/histogram (ds x-col) (:bin-method defaults/defaults))
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

   :lm (fn [ds x-col y-col _]
         (let [clean (tc/drop-missing ds [x-col y-col])]
           (when (>= (tc/row-count clean) 2)
             (let [reg (regr/lm (double-array (clean y-col)) (double-array (clean x-col)))
                   x-lo (dfn/reduce-min (clean x-col)) x-hi (dfn/reduce-max (clean x-col))
                   step (/ (- x-hi x-lo) 79)
                   gxs (mapv #(+ x-lo (* % step)) (range 80))
                   gys (mapv #(regr/predict reg [%]) gxs)]
               (tc/dataset {x-col gxs y-col gys})))))

   :loess (fn [ds x-col y-col _]
            (let [clean (tc/drop-missing ds [x-col y-col])
                  grouped (tc/group-by clean [x-col])
                  means (tc/aggregate grouped {y-col #(fstats/mean (% y-col))})
                  sorted (tc/order-by means [x-col])
                  sxs (double-array (sorted x-col)) sys (double-array (sorted y-col))
                  n (alength sxs)]
              (when (>= n 4)
                (let [f (interp/loess sxs sys)
                      x-lo (aget sxs 0) x-hi (aget sxs (dec n))
                      step (/ (- x-hi x-lo) 79)
                      gxs (mapv #(+ x-lo (* % step)) (range 80))
                      gys (mapv f gxs)]
                  (tc/dataset {x-col gxs y-col gys})))))

   :count (fn [ds x-col _ _]
            (let [clean (tc/drop-missing ds [x-col])
                  cats (vec (distinct (clean x-col)))
                  cts (mapv #(tc/row-count (tc/select-rows clean (fn [r] (= (r x-col) %))))
                            cats)]
              (tc/dataset {x-col cats :count cts})))

   :cumsum (fn [ds _ y-col _]
             (let [vals (ds y-col)
                   cum (vec (reductions + vals))
                   new-col (keyword (str (name y-col) "-cumulative"))]
               (tc/add-column ds new-col cum)))})

;; ================================================================
;; # Part 2: Running Stats
;; ================================================================

(defn run-stat
  "Run a single stat from the registry."
  [ds x-col y-col stat-key opts]
  (let [f (get stat-registry stat-key)]
    (when f (f ds x-col y-col opts))))

;; See the output of any stat:
(run-stat iris :sepal_length nil :bin nil)

(run-stat iris :sepal_length :sepal_width :lm nil)

;; ================================================================
;; # Part 3: Stat Chains
;; ================================================================
;;
;; A chain is a vector of stat keywords: `[:bin :cumsum]`.
;; Each stat receives the previous stat's output as its data.
;;
;; Column routing convention:
;; - The x-column is preserved throughout the chain (same axis).
;; - Each stat declares a "default y output" — the column that
;;   becomes the effective y for the next stat in the chain.
;; - The user can override the final y with an explicit `:y` binding.

(def stat-default-y
  "Default output y-column for each stat. Used for chain routing."
  {:bin :count
   :lm nil         ;; preserves input y-col name
   :loess nil      ;; preserves input y-col name
   :identity nil   ;; preserves input y-col name
   :count :count
   :cumsum nil})   ;; preserves input y-col name (adds -cumulative)

(defn run-chain
  "Run a chain of stats. Returns the output dataset.
   `y-col` tracks the effective y through the chain."
  [ds x-col y-col chain opts]
  (reduce
   (fn [[ds current-y] stat-key]
     (let [result (run-stat ds x-col current-y stat-key opts)
           new-y (or (get stat-default-y stat-key) current-y)]
       ;; For :cumsum, the new column is named <y>-cumulative
       (if (= stat-key :cumsum)
         [result (keyword (str (name current-y) "-cumulative"))]
         [result new-y])))
   [ds y-col]
   chain))

;; Test: bin → cumsum
(let [[result-ds result-y] (run-chain iris :sepal_length nil [:bin :cumsum] nil)]
  {:y-col result-y
   :data result-ds})

;; ================================================================
;; # Part 4: Stat Composition in the Data Model
;; ================================================================
;;
;; The sketch data model with stat composition:
;;
;; ```clojure
;; {:data iris
;;  :entries [{:x :sepal_length}]
;;  :methods [{:stat [:bin :cumsum]       ;; stat chain (vector)
;;             :mark :step                ;; explicit mark (required for chains)
;;             :y :count-cumulative}]}    ;; explicit y (from chain output)
;; ```
;;
;; Resolution:
;; 1. Merge `shared → entry → method` as before
;; 2. If `:stat` is a vector, run the chain
;; 3. Replace `:stat` with `:identity` and `:data` with the chain output
;; 4. The pipeline renders the result

(defn resolve-stat
  "Resolve a single view's stat. If `:stat` is a vector, run the chain.
   Returns the view with `:stat :identity` and `:data` set to the chain output."
  [view]
  (let [stat-spec (:stat view)
        ds (:data view)
        x-col (:x view)
        y-col (:y view)]
    (cond
      ;; Vector stat: run chain
      (vector? stat-spec)
      (let [[result-ds result-y] (run-chain ds x-col y-col stat-spec nil)]
        (-> view
            (assoc :stat :identity
                   :data result-ds
                   :y (or (:y view) result-y))))

      ;; Keyword stat that's in our registry AND not in the pipeline's native stats:
      ;; For now, let the pipeline handle all single-keyword stats natively.
      :else view)))

(defn resolve-stat-per-group
  "Run stat resolution per group when `:color` is present."
  [view]
  (let [color-col (:color view)]
    (if (and (vector? (:stat view)) color-col (keyword? color-col))
      ;; Per-group chain execution
      (let [ds (:data view)
            result (-> ds
                       (tc/group-by [color-col])
                       (tc/process-group-data
                        (fn [gds]
                          (let [[r _] (run-chain gds (:x view) (:y view) (:stat view) nil)]
                            r)))
                       (tc/ungroup {:add-group-as-column true}))]
        (-> view
            (assoc :stat :identity :data result)
            ;; Resolve the y column from the chain
            (assoc :y (or (:y view)
                          (let [[_ result-y] (run-chain (tc/head ds 1) (:x view) (:y view) (:stat view) nil)]
                            result-y)))))
      ;; No grouping or not a chain — resolve without grouping
      (resolve-stat view))))

;; ================================================================
;; # Part 5: Full Resolution with Stat Composition
;; ================================================================

(defn expand-facets
  "Expand entries with keyword `:facet-col`/`:facet-row` into per-value entries."
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
                (-> entry (assoc :facet-col (str cv)
                                 :data (tc/select-rows ed (fn [r] (= (r fcol) cv)))))))
            need-row?
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [rv (distinct (ed frow))]
                (-> entry (assoc :facet-row (str rv)
                                 :data (tc/select-rows ed (fn [r] (= (r frow) rv)))))))
            :else [entry])))
      entries))))

(defn resolve-sketch
  "Full resolution: facets → cross product → stat composition."
  [{:keys [data shared entries methods]}]
  (let [expanded (expand-facets entries data)
        default-methods (if (empty? methods) [{:mark :infer}] methods)]
    (vec
     (mapcat
      (fn [entry]
        (let [entry-methods (or (:methods entry) default-methods)
              base (merge shared (dissoc entry :methods))]
          (map (fn [m]
                 (let [resolved (merge base m)
                       resolved (-> resolved
                                    (assoc :data (or (:data resolved) data))
                                    (cond-> (= :infer (:mark resolved))
                                      (-> (dissoc :mark :stat))))
                       ;; Stat composition: resolve chains
                       resolved (resolve-stat-per-group resolved)]
                   resolved))
               entry-methods)))
      expanded))))

(defn plot
  "Render a sketch through the pipeline."
  [sk & [opts]]
  (let [views (resolve-sketch sk)
        plan (sketch-impl/views->plan views (or opts {}))]
    (sk/plan->figure plan :svg {})))

;; ================================================================
;; # Part 6: Testing Stat Composition
;; ================================================================

;; ---
;; ## Level 0: Everything works as before

;; Scatter + lm (no stat composition)
(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}]
       :methods [{:mark :point :stat :identity :alpha 0.5}
                 {:mark :line :stat :lm}]
       :opts {}})

;; SPLOM with inference (no stat composition)
(plot {:data iris
       :shared {:color :species}
       :entries (vec (for [x [:sepal_length :sepal_width :petal_length]
                           y [:sepal_length :sepal_width :petal_length]]
                       {:x x :y y}))
       :methods []
       :opts {}})

;; ---
;; ## Level 1: Stat output as visible data
;;
;; The user calls `run-stat` to see what a stat produces.

(run-stat iris :sepal_length nil :bin nil)

(run-stat iris :sepal_length :sepal_width :lm nil)

(run-stat iris :species nil :count nil)

;; ---
;; ## Level 2: Stat chain in the data model
;;
;; `[:bin :cumsum]` produces a cumulative histogram.

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length}]
       :methods [{:stat [:bin :cumsum] :mark :line :y :count-cumulative}
                 {:stat [:bin :cumsum] :mark :point :y :count-cumulative}]
       :opts {}})

;; The stat chain runs during resolution. The pipeline sees
;; `:stat :identity` with the chain's output as data.

;; ---
;; ## Level 2 + grouping: per-species cumulative histogram

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length}]
       :methods [{:stat [:bin :cumsum] :mark :line :y :count-cumulative}]
       :opts {}})

;; Three colored lines — one cumulative histogram per species.
;; Grouping is handled by `resolve-stat-per-group`.

;; ---
;; ## Level 1.5: Stats-as-data rendered through the existing API
;;
;; The user calls the stat, sees the output, then plots it with `sk/lay-*`.
;; This works TODAY — no changes to the pipeline.

(-> iris
    (run-stat :sepal_length nil :bin nil)
    (sk/lay-point :sepal_length :density))

;; `:density` is a stat output column, bound as y.
;; No special syntax — just a column name.

;; ---
;; ## Faceting + stat chain

(plot {:data iris
       :shared {}
       :entries [{:x :sepal_length :facet-col :species}]
       :methods [{:stat [:bin :cumsum] :mark :line :y :count-cumulative}]
       :opts {}})

;; 3 panels, each with its own cumulative histogram.

;; ---
;; ## Simpson's paradox (no stat composition — works as before)

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width}
                 {:x :sepal_length :y :sepal_width
                  :methods [{:mark :line :stat :lm :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}
                 {:mark :line :stat :lm}]
       :opts {}})

;; ---
;; ## Mixed: stat chain + broadcast

(plot {:data iris
       :shared {:color :species}
       :entries [{:x :sepal_length :y :sepal_width :facet-col :species}
                 ;; Broadcast: overall cumulative histogram in all panels
                 {:x :sepal_length
                  :methods [{:stat [:bin :cumsum] :mark :line
                             :y :count-cumulative :color nil}]}]
       :methods [{:mark :point :stat :identity :alpha 0.4}]
       :opts {}})

;; Each panel: per-species scatter.
;; All panels: broadcast overall cumulative histogram (black).

;; ================================================================
;; # Part 7: The Verb API
;; ================================================================
;;
;; How do the verbs produce this data model?
;; The key verbs and what they do:

;; ### `sk/view` — sets column bindings for entries
;;
;; `(sk/view iris :sepal_length :sepal_width {:color :species})`
;; produces entries with `:x :sepal_length :y :sepal_width :color :species`.

;; ### `sk/lay-*` — adds a method
;;
;; `sk/lay-point` adds `{:mark :point :stat :identity}`.
;; `sk/lay-lm` adds `{:mark :line :stat :lm}`.

;; ### `sk/lay` — adds a raw method map (for composition)
;;
;; `(sk/lay views {:stat [:bin :cumsum] :mark :step :y :count-cumulative})`
;; would add the full method map. This is the power-user verb.

;; ### `sk/facet` — sets faceting
;;
;; `(sk/facet views :species)` sets `:facet-col :species` on entries.

;; ---
;; ## Verb pipelines for the holistic use cases

;; ### Scatter + regression (current API)
(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.5})
    sk/lay-lm)

;; ### SPLOM (current API)
(-> iris
    (sk/view (sk/cross [:sepal_length :sepal_width :petal_length]
                       [:sepal_length :sepal_width :petal_length]))
    sk/lay-point
    (sk/options {:color :species}))

;; Wait — `sk/options` doesn't set shared aesthetics.
;; The current API uses `sk/view` with aesthetics for shared color.

;; ### Faceted scatter + regression
(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/lay-lm
    (sk/facet :species))

;; ### Stats-as-data through the API
(-> iris
    (run-stat :sepal_length nil :bin nil)
    (sk/lay-point :sepal_length :density))

;; This works NOW — stat output is a dataset, `sk/lay-point` accepts datasets.

;; ================================================================
;; # Part 8: What This Reveals
;; ================================================================
;;
;; ## What works
;;
;; - Stat output override (Level 1): `run-stat` + `sk/lay-*` with different column.
;;   No data model change needed — just Clojure data flowing through existing API.
;;
;; - Simple chaining (Level 2): `{:stat [:bin :cumsum] :mark :line :y :count-cumulative}`
;;   works in the data model. Resolution runs the chain, pipeline renders the result.
;;   Per-group chains work via tablecloth grouping.
;;
;; - The existing `sk/lay-*` API composes well for the common cases.
;;
;; ## What's awkward
;;
;; - Column routing in chains: `:cumsum` implicitly knows to sum the y column.
;;   This works for simple cases but doesn't generalize. A stat that needs
;;   TWO columns from the previous stat's output can't express its needs.
;;
;; - Grouping for chains runs in resolution, not in the pipeline. This means
;;   grouping logic exists in two places. Not terrible (resolution handles
;;   the chain, pipeline handles single stats) but not beautiful either.
;;
;; - The `sk/lay` verb (raw method map) doesn't exist yet. Power users
;;   need it for stat composition.
;;
;; ## What the verbs need
;;
;; The current verb set covers the common cases well:
;; - `sk/view` for column bindings
;; - `sk/lay-*` for registered methods
;; - `sk/facet` for faceting
;; - `sk/options` for plot-level settings
;; - `sk/cross` for column pair generation
;;
;; What's missing:
;; - `sk/lay` (raw method map) for power users
;; - A way to specify stat output column override in `sk/lay-*` options
;;   (e.g., `(sk/lay-histogram iris :sepal_length {:y :density})`)
;; - `sk/facet-wrap` for wrapping many categories
;;
;; ## The design principle
;;
;; **Level 1 (methods) is the common path.** Simple, inference-powered,
;; one verb per chart type. This is where most users live.
;;
;; **Level 2 (stat-as-data) is the power path.** Call the stat, see the
;; output, plot it with `sk/lay-*`. Tablecloth grouping for per-group.
;; This is Clojure — functions compose.
;;
;; **Level 3 (stat chains in the model) is the declarative power path.**
;; `{:stat [:bin :cumsum]}` in the method map. Works for simple chains.
;; Complex cases fall back to Level 2.
;;
;; The beauty: all three levels produce the same thing — view maps that
;; the pipeline renders. The levels differ in how much the user specifies
;; vs how much is automated.

;; ================================================================
;; # Part 9: Stress Tests
;; ================================================================

;; ---
;; ## Per-species density via stats-as-data + verbs
;;
;; A `->` pipeline starting from `iris`.

(-> iris
    (tc/group-by [:species])
    (tc/process-group-data (fn [ds] (run-stat ds :sepal_length nil :bin nil)))
    (tc/ungroup {:add-group-as-column true})
    (sk/lay-area :sepal_length :density {:color :species :alpha 0.4}))

;; Three species density curves. The pipeline flows:
;; `iris` → group → stat per group → ungroup → `sk/lay-area`.

;; ---
;; ## Chain + facet + broadcast (raw data model — no verb for chains yet)
;;
;; This uses the raw data model because the current `sk/lay-*` verbs
;; don't support stat chains. A `sk/lay` verb for raw method maps
;; would make this cleaner.

(plot {:data iris :shared {}
       :entries [{:x :sepal_length :facet-col :species
                  :methods [{:stat [:bin :cumsum] :mark :line :y :count-cumulative}]}
                 {:x :sepal_length
                  :methods [{:stat [:bin :cumsum] :mark :line
                             :y :count-cumulative :alpha 0.3}]}]
       :methods []
       :opts {}})

;; 3 panels: per-species cumulative (solid) + overall cumulative (faded, broadcast).

;; ---
;; ## Scatter + lm via verbs, cumulative via stats-as-data
;;
;; Mixing the two levels in one figure: verb pipeline for the scatter,
;; stats-as-data for the cumulative histogram.

(let [cum-ds (-> iris
                 (run-stat :sepal_length nil :bin nil)
                 (tc/add-column :count-cumulative
                                (vec (reductions + (:count (run-stat iris :sepal_length nil :bin nil))))))]
  (sk/arrange
   [(-> iris
        (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.3})
        (sk/lay-lm {:se true}))
    (-> cum-ds
        (sk/lay-line :sepal_length :count-cumulative))]
   {:cols 2}))

;; Two panels side by side via `sk/arrange`: scatter + lm (left),
;; cumulative histogram (right). Different data, different methods.

;; ---
;; ## Column routing v2: cumulative density
;;
;; `{:cumsum :density}` in the chain routes the `:density` column.

(defn run-chain-v2
  "Run a chain with optional mid-chain column routing.
   Each element is either a keyword (use default y) or a map `{stat-key column}` (route)."
  [ds x-col y-col chain opts]
  (reduce
   (fn [[ds current-y] step]
     (let [[stat-key route-col] (if (map? step)
                                  [(first (keys step)) (first (vals step))]
                                  [step nil])
           effective-y (or route-col current-y)
           result (run-stat ds x-col effective-y stat-key opts)
           new-y (cond
                   (= stat-key :cumsum)
                   (keyword (str (name effective-y) "-cumulative"))
                   (get stat-default-y stat-key)
                   (get stat-default-y stat-key)
                   :else effective-y)]
       [result new-y]))
   [ds y-col]
   chain))

;; `[:bin {:cumsum :density}]` — bin, then cumsum the `:density` column:
(let [[result-ds _] (run-chain-v2 iris :sepal_length nil
                                  [:bin {:cumsum :density}] nil)]
  result-ds)

;; Per-species cumulative density: data prep → verb pipeline.
(-> iris
    (tc/group-by [:species])
    (tc/process-group-data
     (fn [ds]
       (let [bins (run-stat ds :sepal_length nil :bin nil)]
         (tc/add-column bins :density-cumulative
                        (vec (reductions + (:density bins)))))))
    (tc/ungroup {:add-group-as-column true})
    (sk/lay-line :sepal_length :density-cumulative {:color :species}))

;; Three colored cumulative density curves.

;; ================================================================
;; # Part 10: Findings
;; ================================================================
;;
;; ## What works well
;;
;; - **Simple chains** (`[:bin :cumsum]`) compose naturally in the data model.
;;   Resolution runs the chain, the pipeline renders the result.
;;
;; - **Grouping + chains** work via `resolve-stat-per-group`, which uses
;;   tablecloth's `tc/group-by` → `tc/process-group-data` → `tc/ungroup`.
;;
;; - **Faceting + chains + broadcast** all compose correctly.
;;
;; - **Native pipeline stats and stat chains coexist** in one figure
;;   (different entries can use either).
;;
;; - **Stats-as-data** (Level 2) covers anything the chain can't express.
;;   Tablecloth grouping makes per-group stat computation clean.
;;
;; ## What's limited
;;
;; - **Column routing** in chains: the default convention (thread the y column)
;;   covers cumulative count but not cumulative density.
;;   `run-chain-v2` with `{:cumsum :density}` notation addresses this,
;;   but adds complexity. The question: is this worth putting in the data model,
;;   or should it remain as external data prep?
;;
;; - **Stat chains that need intermediate objects** (not just datasets):
;;   residuals from a model, bootstrap CI. These stay as monolithic stats
;;   or external computation.
;;
;; - **Inference skips chains**: the user must specify `:mark` explicitly
;;   for stat chains. This is the right tradeoff — chains are for power users.
;;
;; ## The three levels, refined
;;
;; | Level | User writes | Handles |
;; |:------|:------------|:--------|
;; | 1. Methods | `sk/lay-histogram` | 90% of use cases. Inference, registry. |
;; | 2. Stats-as-data | `(run-stat ...) + sk/lay-*` | Anything. Explicit, inspectable. |
;; | 3. Chains in model | `{:stat [:bin :cumsum]}` | Simple composition. Declarative. |
;;
;; Level 1 is the on-ramp. Level 3 is the declarative sweet spot.
;; Level 2 is the escape hatch — always available, always works.
