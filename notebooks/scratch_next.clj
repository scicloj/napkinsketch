;; # Next Ground: Stats as Data
;;
;; What if everything is data — before and after the stat?
;;
;; Principle: a stat is a function from dataset to dataset.
;; The user can see both. Bindings reference columns.
;; No special syntax. No hidden state.

(ns scratch-next
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.argops :as argops]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [fastmath.stats :as fstats]
            [fastmath.ml.regression :as regr]
            [fastmath.interpolation.acm :as interp]
            [napkinsketch-book.datasets :as data]))

(def iris data/iris)
(def tips data/tips)

;; ---
;; ## Part 1: Stats as Functions
;;
;; Each stat is: dataset, column names → dataset.
;; The output has named columns. The user sees them.
;;
;; Key design choice: **stat output preserves the x column name** so it
;; lands on the same axis as the input data. Stats that predict or smooth
;; the y variable (`stat-lm`, `stat-loess`) also preserve the y column name.
;; Stats that produce new quantities (`stat-bin` → `:count`, `:density`;
;; `stat-residuals` → `:residual`) use new column names for y.

;; ---
;; ### `stat-bin`: bin a numeric column

(defn stat-bin
  "Bin a numeric column. Returns dataset with x-col, :count, :density, :x-min, :x-max, :width."
  [ds x-col & [opts]]
  (let [hist (fstats/histogram (ds x-col) (:bin-method defaults/defaults))
        bins (:bins-maps hist)
        n (reduce + 0 (map :count bins))]
    (tc/dataset
     {x-col    (mapv #(/ (+ (double (:min %)) (double (:max %))) 2.0) bins)
      :count   (mapv :count bins)
      :density (mapv (fn [b]
                       (let [bw (- (double (:max b)) (double (:min b)))]
                         (if (and (pos? n) (pos? bw)) (/ (double (:count b)) (* n bw)) 0.0)))
                     bins)
      :x-min   (mapv :min bins) :x-max (mapv :max bins)
      :width   (mapv #(- (double (:max %)) (double (:min %))) bins)})))

;; See what it produces:
(stat-bin iris :sepal_length)

;; ---
;; ### `stat-lm`: linear regression

(defn stat-lm
  "Fit a linear model. Returns dataset with x-col, y-col (predicted values)."
  [ds x-col y-col & [opts]]
  (let [clean (tc/drop-missing ds [x-col y-col]) n (tc/row-count clean)]
    (when (>= n 2)
      (let [reg (regr/lm (double-array (clean y-col)) (double-array (clean x-col)))
            x-lo (dfn/reduce-min (clean x-col)) x-hi (dfn/reduce-max (clean x-col))
            n-grid 80 step (/ (- x-hi x-lo) (dec n-grid))
            gxs (mapv #(+ x-lo (* % step)) (range n-grid))
            gys (mapv #(regr/predict reg [%]) gxs)]
        (tc/dataset {x-col gxs y-col gys})))))

(stat-lm iris :sepal_length :sepal_width)

;; ---
;; ### `stat-loess`: local regression

(defn stat-loess
  "LOESS smooth. Returns dataset with x-col, y-col (smoothed values)."
  [ds x-col y-col & [opts]]
  (let [clean (tc/drop-missing ds [x-col y-col])
        grouped (tc/group-by clean [x-col])
        means (tc/aggregate grouped {y-col #(fstats/mean (% y-col))})
        sorted (tc/order-by means [x-col])
        sxs (double-array (sorted x-col)) sys (double-array (sorted y-col))
        n (alength sxs)]
    (when (>= n 4)
      (let [f (interp/loess sxs sys)
            x-lo (aget sxs 0) x-hi (aget sxs (dec n))
            n-grid 80 step (/ (- x-hi x-lo) (dec n-grid))
            gxs (mapv #(+ x-lo (* % step)) (range n-grid))
            gys (mapv f gxs)]
        (tc/dataset {x-col gxs y-col gys})))))

(stat-loess iris :sepal_length :sepal_width)

;; ---
;; ### `stat-count`: count categories

(defn stat-count
  "Count occurrences. Returns dataset with x-col, :count."
  [ds x-col & [opts]]
  (let [clean (tc/drop-missing ds [x-col])
        cats (vec (distinct (clean x-col)))
        cts (mapv #(tc/row-count (tc/select-rows clean (fn [r] (= (r x-col) %)))) cats)]
    (tc/dataset {x-col cats :count cts})))

(stat-count iris :species)

;; ---
;; ### `stat-residuals`: dependent stat (lm → residuals)

(defn stat-residuals
  "Compute residuals: actual - predicted. Returns dataset with x-col, :residual."
  [ds x-col y-col & [opts]]
  (let [clean (tc/drop-missing ds [x-col y-col])
        reg (regr/lm (double-array (clean y-col)) (double-array (clean x-col)))
        predicted (mapv #(regr/predict reg [%]) (clean x-col))
        residuals (dfn/- (clean y-col) predicted)]
    (tc/dataset {x-col (clean x-col) :residual residuals})))

(stat-residuals iris :sepal_length :sepal_width)

;; ---
;; ### `stat-cumsum`: running cumulative sum (for chaining)

(defn stat-cumsum
  "Add a cumulative sum column. For chaining after stat-bin."
  [ds col & [opts]]
  (tc/add-column ds (keyword (str (name col) "-cumulative")) (vec (reductions + (ds col)))))

;; ---
;; ### `stat-per-group`: apply any stat per group, combine results
;;
;; Uses tablecloth's grouping: `tc/group-by` → `tc/process-group-data` → `tc/ungroup`.
;; The group column is added automatically by `tc/ungroup`.

(defn stat-per-group
  "Split by `group-col`, apply `stat-fn` per group, combine with group column."
  [ds group-col stat-fn x-col y-col & [opts]]
  (-> ds
      (tc/group-by [group-col])
      (tc/process-group-data #(stat-fn % x-col y-col opts))
      (tc/ungroup {:add-group-as-column true})))

;; ---
;; ### `render`: bridge to the real pipeline

(defn render
  "Render view maps through the napkinsketch pipeline."
  [views & [opts]]
  (sk/plan->figure (sketch-impl/views->plan views (or opts {})) :svg {}))

;; ---
;; ## Part 2: Seeing the Data
;;
;; Every stat output is a dataset you can inspect.

;; What does `:bin` produce?
(stat-bin iris :sepal_length)

;; What does `:lm` produce?
(tc/head (stat-lm iris :sepal_length :sepal_width) 5)

;; What does `:count` produce?
(stat-count iris :species)

;; What does `:residuals` produce?
(tc/head (stat-residuals iris :sepal_length :sepal_width) 5)

;; ---
;; ## Part 3: Rendering Stat Output
;;
;; Stat output is data — render it with `:stat :identity`.

;; ---
;; ### Scatter of raw data

(render [{:data iris :x :sepal_length :y :sepal_width
          :mark :point :stat :identity :color :species}])

;; ---
;; ### Regression line from stat output

(render [{:data (stat-lm iris :sepal_length :sepal_width)
          :x :sepal_length :y :sepal_width
          :mark :line :stat :identity}])

;; ---
;; ### LOESS from stat output

(render [{:data (stat-loess iris :sepal_length :sepal_width)
          :x :sepal_length :y :sepal_width
          :mark :line :stat :identity}])

;; ---
;; ## Part 4: Composing Raw + Stat in One Figure
;;
;; This is the power of stats-as-data: raw data and stat output
;; are both datasets, both renderable, and they share coordinate space.

;; ---
;; ### Scatter + LOESS overlay

(render [{:data iris :x :sepal_length :y :sepal_width
          :mark :point :stat :identity :color :species :alpha 0.3}
         {:data (stat-loess iris :sepal_length :sepal_width)
          :x :sepal_length :y :sepal_width
          :mark :line :stat :identity}])

;; ---
;; ### Scatter + LM + LOESS (two smoothers)

(render [{:data iris :x :sepal_length :y :sepal_width
          :mark :point :stat :identity :color :species :alpha 0.3}
         {:data (stat-lm iris :sepal_length :sepal_width)
          :x :sepal_length :y :sepal_width
          :mark :line :stat :identity}
         {:data (stat-loess iris :sepal_length :sepal_width)
          :x :sepal_length :y :sepal_width
          :mark :line :stat :identity}])

;; Two smoothers from two different stat functions, overlaid on scatter.

;; ---
;; ### Simpson's paradox

(let [species ["setosa" "versicolor" "virginica"]
      per-sp-lm (stat-per-group iris :species stat-lm :sepal_length :sepal_width)
      overall-lm (stat-lm iris :sepal_length :sepal_width)]
  (render
   [{:data iris :x :sepal_length :y :sepal_width
     :mark :point :stat :identity :color :species :alpha 0.4}
    {:data per-sp-lm :x :sepal_length :y :sepal_width
     :mark :line :stat :identity :color :species}
    {:data overall-lm :x :sepal_length :y :sepal_width
     :mark :line :stat :identity}]))

;; Per-species scatter + per-species lm (colored) + overall lm (black).
;; Each line is a dataset the user computed and inspected.

;; ---
;; ## Part 5: After-Stat Binding
;;
;; The bin stat produces both `:count` and `:density`.
;; The user chooses which to plot by binding different columns.

(def bins (stat-bin iris :sepal_length))

;; As density (area):
(render [{:data bins :x :sepal_length :y :density
          :mark :area :stat :identity}])

;; As count (points):
(render [{:data bins :x :sepal_length :y :count
          :mark :point :stat :identity}])

;; Same stat output, different bindings, different visualizations.
;; This IS `after_stat` — no special syntax needed.

;; ---
;; ## Part 6: Per-Group Density Comparison
;;
;; Bin each species, overlay their density curves.

(let [sp-bins (-> iris
                  (tc/group-by [:species])
                  (tc/process-group-data #(stat-bin % :sepal_length))
                  (tc/ungroup {:add-group-as-column true}))]
  (render
   [{:data sp-bins :x :sepal_length :y :density
     :mark :area :stat :identity :color :species :alpha 0.4}]))

;; Three overlapping density distributions, one per species.
;; The user computed each stat per group, combined them, and rendered.

;; ---
;; ## Part 7: Different Stats per Group
;;
;; This is impossible in ggplot2: each group gets a different stat.
;; setosa → LOESS, versicolor → LM, virginica → points only.

(let [setosa-loess (-> (tc/select-rows iris #(= (% :species) "setosa"))
                       (stat-loess :sepal_length :sepal_width)
                       (tc/add-column :species (repeat 80 "setosa")))
      versi-lm (-> (tc/select-rows iris #(= (% :species) "versicolor"))
                   (stat-lm :sepal_length :sepal_width)
                   (tc/add-column :species (repeat 80 "versicolor")))]
  (render
   [{:data iris :x :sepal_length :y :sepal_width
     :mark :point :stat :identity :color :species :alpha 0.4}
    {:data setosa-loess :x :sepal_length :y :sepal_width
     :mark :line :stat :identity :color :species}
    {:data versi-lm :x :sepal_length :y :sepal_width
     :mark :line :stat :identity :color :species}]))

;; setosa: LOESS curve. versicolor: LM line. virginica: points only.

;; ---
;; ## Part 8: Stat Chaining — Function Composition
;;
;; Stats are functions. Chaining is just `(-> ds stat1 stat2)`.

;; Cumulative histogram:
(let [cum-ds (-> iris (stat-bin :sepal_length) (stat-cumsum :count))]
  (render
   [{:data cum-ds :x :sepal_length :y :count-cumulative
     :mark :line :stat :identity}
    {:data cum-ds :x :sepal_length :y :count-cumulative
     :mark :point :stat :identity}]))

;; The chain: bin → cumsum. The user sees intermediate datasets.

;; ---
;; ## Part 9: Residual Plot — Dependent Stats
;;
;; Residuals depend on a model. `stat-residuals` computes
;; actual minus predicted from a linear model.

(let [resid (stat-residuals iris :sepal_length :sepal_width)]
  (render
   [{:data resid :x :sepal_length :y :residual
     :mark :point :stat :identity :alpha 0.5}
    {:data (tc/dataset {:sepal_length [4.3 7.9] :residual [0 0]})
     :x :sepal_length :y :residual
     :mark :line :stat :identity}]))

;; Scatter of residuals + zero reference line.

;; ---
;; ## Part 10: Side-by-Side — Scatter + Residuals
;;
;; Different `:y` values → different panels (multi-variable grid).
;; Raw data in one panel, derived data in another.

(let [resid (stat-residuals iris :sepal_length :sepal_width)
      lm-ds (stat-lm iris :sepal_length :sepal_width)]
  (render
   [{:data iris :x :sepal_length :y :sepal_width
     :mark :point :stat :identity :color :species :alpha 0.4}
    {:data lm-ds :x :sepal_length :y :sepal_width
     :mark :line :stat :identity}
    {:data resid :x :sepal_length :y :residual
     :mark :point :stat :identity :alpha 0.4}
    {:data (tc/dataset {:sepal_length [4.3 7.9] :residual [0 0]})
     :x :sepal_length :y :residual
     :mark :line :stat :identity}]))

;; Two panels: scatter+lm (left) and residuals (right).
;; All from visible, inspectable datasets.

;; ---
;; ## Part 11: Faceted + Broadcast with Stat-as-Data

(let [species ["setosa" "versicolor" "virginica"]
      scatter-views (for [sp species]
                      {:data (tc/select-rows iris #(= (% :species) sp))
                       :x :sepal_length :y :sepal_width
                       :mark :point :stat :identity :facet-col sp :alpha 0.5})
      per-sp-lm (for [sp species]
                  {:data (-> (tc/select-rows iris #(= (% :species) sp))
                             (stat-lm :sepal_length :sepal_width))
                   :x :sepal_length :y :sepal_width
                   :mark :line :stat :identity :facet-col sp})
      ;; Broadcast: overall LOESS in ALL panels
      overall-loess {:data (stat-loess iris :sepal_length :sepal_width)
                     :x :sepal_length :y :sepal_width
                     :mark :line :stat :identity :alpha 0.3}]
  (render (vec (concat scatter-views per-sp-lm [overall-loess]))))

;; 3 panels. Each: per-species scatter + per-species lm + overall LOESS.
;; The overall LOESS broadcasts because it has no `:facet-col`.

;; ---
;; ## Part 12: Composing with the Existing API
;;
;; Stats-as-data outputs are datasets. They feed directly into
;; the existing `sk/lay-*` API.

(-> (stat-bin iris :sepal_length)
    (sk/lay-point :sepal_length :density))

;; Bin stat output → scatter of (midpoint, density) via `sk/lay-point`.

(-> (stat-lm iris :sepal_length :sepal_width)
    (sk/lay-line :sepal_length :sepal_width))

;; LM stat output → regression line via `sk/lay-line`.

;; ---
;; ## Summary
;;
;; | Old | New |
;; |:----|:----|
;; | Stats produce bespoke maps | Stats produce datasets |
;; | Output invisible to user | Output is a regular inspectable dataset |
;; | `after_stat(density)` | `{:y :density}` — just a column name |
;; | No stat chaining | `(-> ds stat1 stat2)` |
;; | One stat per group, always | Different stat per group possible |
;; | Stat output can't be plotted directly | Stat output IS plottable data |
;;
;; The bridge: stat output feeds the existing pipeline as `:stat :identity` views.
;; The coordinate space is preserved (stat output uses the same column names).
;;
;; Methods remain the user-facing concept. Stats-as-data is the power
;; user's tool for inspection, customization, and composition.
;;
;; ## Design Observations
;;
;; 1. **Stat output preserves the x column name** — so raw data and
;;    stat output share the x-axis and land in the same panel.
;;    Stats that predict y (like `stat-lm`) also preserve the y name.
;;    Stats that produce new quantities (like `stat-bin` → `:density`)
;;    use new y column names that the user binds explicitly.
;;
;; 2. **Per-group stats** use tablecloth grouping:
;;    `tc/group-by` → `tc/process-group-data` → `tc/ungroup`.
;;    The group column is added automatically by `tc/ungroup`.
;;
;; 3. **Stats-as-data resolves `after_stat` elegantly**: the stat output
;;    has all computed columns. The user binds whichever column they want.
;;    No special pipeline stage, no opaque references.
;;
;; 4. **Stat chaining is function composition**: `(-> ds stat1 stat2)`.
;;    Each step produces a dataset. The user can inspect any intermediate.
;;    No need for a chaining DSL.
;;
;; 5. **The existing pipeline is the renderer**: stats-as-data feeds
;;    the pipeline via `:stat :identity` views. No new rendering code needed.
;;
;; 6. **Different stats per group** (impossible in ggplot2) falls out
;;    naturally: compute each stat separately, combine, render.
;;
;; 7. **The nil-color merge-order issue**: when composing data model
;;    entries with per-entry methods, the grouping decision must happen
;;    AFTER shared → entry → method merge (not before). This is a
;;    resolution-order concern, not a data model issue.
