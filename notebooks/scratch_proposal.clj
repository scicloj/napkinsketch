;; # Proposal: Data Model + Verbs
;;
;; A self-contained proposal. Defines:
;; 1. The data model
;; 2. Verbs that produce it
;; 3. Resolution that feeds the pipeline
;; 4. All examples expressed through the verbs

(ns scratch-proposal
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dfn]
            [scicloj.napkinsketch.api :as sk]
            [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.method :as method]
            [fastmath.stats :as fstats]
            [fastmath.ml.regression :as regr]
            [fastmath.interpolation.acm :as interp]
            [java-time.api :as jt]
            [napkinsketch-book.datasets :as data]))

(def iris data/iris)
(def tips data/tips)
(def penguins data/penguins)

;; # The Data Model
;;
;; A sketch is a map:
;;
;; ```clojure
;; {:data    dataset
;;  :shared  {:color :species}
;;  :entries [{:x :a :y :b}]
;;  :methods [{:mark :point :stat :identity}]
;;  :opts    {}}
;; ```
;;
;; Resolution: `shared → entry → method`. Nil cancels.
;; Per-entry `:methods` override sketch-level. Per-entry `:data` overrides.
;; Keyword `:facet-col`/`:facet-row` expanded automatically.

;; # Resolution

(defn expand-facets
  "Expand keyword `:facet-col`/`:facet-row` into per-value entries."
  [entries data]
  (let [ds (if (tc/dataset? data) data (tc/dataset data))]
    (vec
     (mapcat
      (fn [entry]
        (let [fcol (:facet-col entry) frow (:facet-row entry)
              nc? (keyword? fcol) nr? (keyword? frow)]
          (cond
            (and nc? nr?)
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [cv (distinct (ed fcol)) rv (distinct (ed frow))]
                (-> entry (assoc :facet-col (str cv) :facet-row (str rv)
                                 :data (tc/select-rows ed (fn [r] (and (= (r fcol) cv) (= (r frow) rv))))))))
            nc?
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [cv (distinct (ed fcol))]
                (-> entry (assoc :facet-col (str cv)
                                 :data (tc/select-rows ed (fn [r] (= (r fcol) cv)))))))
            nr?
            (let [ed (or (:data entry) ds)
                  ed (if (tc/dataset? ed) ed (tc/dataset ed))]
              (for [rv (distinct (ed frow))]
                (-> entry (assoc :facet-row (str rv)
                                 :data (tc/select-rows ed (fn [r] (= (r frow) rv)))))))
            :else [entry])))
      entries))))

(defn resolve-sketch
  "Resolve a sketch map into view maps for the pipeline."
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
                   (let [d (or (:data resolved) data)
                         d (if (tc/dataset? d) d (tc/dataset d))]
                     (-> resolved
                         (assoc :data d)
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat)))))))
               entry-methods)))
      expanded))))

(defn render-sketch
  "Resolve and render a sketch."
  [sketch]
  (sk/plan->figure (sketch-impl/views->plan (resolve-sketch sketch) (:opts sketch {})) :svg {}))

;; # The Verbs
;;
;; Verbs produce and transform sketch maps.

(defn sketch
  "Create a sketch. Data is optional — omit it to create a reusable recipe.
   `(sketch)` — recipe (no data).
   `(sketch data)` — sketch with data.
   `(sketch data {:color :species})` — sketch with data and shared aesthetics."
  ([] {:data nil :shared {} :entries [] :methods [] :opts {}})
  ([data] (sketch data {}))
  ([data shared]
   {:data (when data (if (tc/dataset? data) data (tc/dataset data)))
    :shared shared
    :entries []
    :methods []
    :opts {}}))

(defn with-data
  "Supply or replace data in a sketch."
  [sk data]
  (assoc sk :data (if (tc/dataset? data) data (tc/dataset data))))

(defn view
  "Add entries (column bindings) to a sketch.
   `(view sk)` — infer columns from dataset shape (1→x, 2→x+y, 3→x+y+color).
   `(view sk :x)` — univariate entry (histogram, bar).
   `(view sk :x :y)` — one entry.
   `(view sk :x :y {:color :species})` — one entry with aesthetics.
   `(view sk pairs)` — multiple entries from column pairs (e.g., from `cross`).
   `(view sk {:x :a :y :b :coord :flip})` — raw entry map."
  ([sk]
   (let [ds (:data sk)
         cols (vec (tc/column-names ds))
         n (count cols)]
     (case n
       1 (update sk :entries conj {:x (cols 0)})
       2 (update sk :entries conj {:x (cols 0) :y (cols 1)})
       3 (update sk :entries conj {:x (cols 0) :y (cols 1) :color (cols 2)})
       (throw (ex-info (str "Cannot infer columns from " n " columns. Specify explicitly.")
                       {:columns cols})))))
  ([sk x-or-entries]
   (cond
     (keyword? x-or-entries)  (update sk :entries conj {:x x-or-entries})
     (map? x-or-entries)      (update sk :entries conj x-or-entries)
     (sequential? x-or-entries)
     (update sk :entries into (mapv (fn [[x y]] {:x x :y y}) x-or-entries))))
  ([sk x y] (update sk :entries conj {:x x :y y}))
  ([sk x y opts] (update sk :entries conj (merge {:x x :y y} opts))))

(defn lay
  "Add a method to a sketch.
   `(lay sk :point)` — registered method keyword.
   `(lay sk {:mark :line :stat :lm :se true})` — raw method map.
   `(lay sk :point {:alpha 0.5})` — registered method with options."
  ([sk method-key]
   (if (keyword? method-key)
     (let [m (scicloj.napkinsketch.method/lookup method-key)]
       (update sk :methods conj (or (select-keys m [:mark :stat :position])
                                    {:mark method-key :stat :identity})))
     ;; Raw method map
     (update sk :methods conj method-key)))
  ([sk method-key opts]
   (if (keyword? method-key)
     (let [m (scicloj.napkinsketch.method/lookup method-key)]
       (update sk :methods conj (merge (or (select-keys m [:mark :stat :position])
                                           {:mark method-key :stat :identity})
                                       opts)))
     ;; method-key is a raw map, opts is extra keys
     (update sk :methods conj (merge method-key opts)))))

(defn facet
  "Facet a sketch by a column."
  ([sk col] (facet sk col :col))
  ([sk col direction]
   (let [k (case direction :col :facet-col :row :facet-row)]
     (update sk :entries (fn [entries]
                           (mapv #(assoc % k col) entries))))))

(defn facet-grid
  "Facet a sketch by two columns (2D grid)."
  [sk col-col row-col]
  (update sk :entries (fn [entries]
                        (mapv #(assoc % :facet-col col-col :facet-row row-col) entries))))

(defn overlay
  "Add an entry with its own methods — does NOT use sketch-level methods.
   `(overlay sk :x :y [methods])` — entry with explicit methods.
   `(overlay sk {:x :a :facet-col \"setosa\"} [methods])` — raw entry + methods."
  ([sk x y methods]
   (update sk :entries conj {:x x :y y :methods methods}))
  ([sk entry-map methods]
   (update sk :entries conj (assoc entry-map :methods methods))))

(defn options
  "Set plot-level options."
  [sk opts]
  (update sk :opts merge opts))

(defn cross
  "Generate column pairs for SPLOM-style grids."
  [xs ys]
  (vec (for [x xs y ys] [x y])))

;; # Rendering
;;
;; A sketch auto-renders when evaluated (like napkinsketch's `Sketch`).
;; For now, call `render-sketch` explicitly.

(def plot render-sketch)

;; # Examples: All Through Verbs

;; ---
;; ## Scatter

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.5})
    plot)

;; ---
;; ## Scatter + regression

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.5})
    (lay :lm)
    plot)

;; ---
;; ## Triple overlay: violin + jitter + boxplot

(-> (sketch iris)
    (view :species :sepal_width)
    (lay :violin {:alpha 0.3})
    (lay :point {:jitter true :alpha 0.4})
    (lay :boxplot)
    plot)

;; ---
;; ## Simpson's paradox
;;
;; Entry 1 uses sketch methods. Entry 2 overrides with its own.

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.4})
    (lay :lm)
    ;; `overlay` adds an entry with its own methods — cancels color
    (overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    plot)

;; ---
;; ## Per-entry data override

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.3})
    (overlay {:x :sepal_length :y :sepal_width
              :data (tc/select-rows iris #(> (% :sepal_length) 6.0))}
             [{:mark :line :stat :lm}])
    plot)

;; ---
;; ## Small multiples

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (view :sepal_length :petal_length)
    (view :sepal_length :petal_width)
    (lay :point)
    plot)

;; ---
;; ## SPLOM with inference

(-> (sketch iris {:color :species})
    (view (cross [:sepal_length :sepal_width :petal_length]
                 [:sepal_length :sepal_width :petal_length]))
    plot)

;; ---
;; ## SPLOM with explicit diagonal (density curves)

(let [cols [:sepal_length :sepal_width :petal_length]
      off-diag [{:mark :point :stat :identity :alpha 0.4} {:mark :line :stat :lm}]
      on-diag  [{:mark :area :stat :kde :alpha 0.4}]]
  (-> (reduce (fn [sk [x y]]
                (overlay sk x y (if (= x y) on-diag off-diag)))
              (sketch iris {:color :species})
              (cross cols cols))
      plot))

;; ---
;; ## Facet by column

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point)
    (lay :lm)
    (facet :species)
    plot)

;; ---
;; ## 2D facet grid

(-> (sketch tips {:color :smoker})
    (view :total_bill :tip)
    (lay :point {:alpha 0.5})
    (facet-grid :day :sex)
    plot)

;; ---
;; ## Broadcast: overall LOESS across faceted panels

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point)
    (facet :species)
    ;; Broadcast: no `:facet-col` → appears in all panels
    (overlay :sepal_length :sepal_width [{:mark :line :stat :loess}])
    plot)

;; ---
;; ## Per-panel annotation

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point)
    (lay :lm)
    (facet :species)
    ;; Annotation in setosa panel only
    (overlay {:x :sepal_length :y :sepal_width :facet-col "setosa"}
             [{:mark :rule-h :intercept 3.0}])
    plot)

;; ---
;; ## Stacked bars

(-> (sketch tips {:color :smoker})
    (view :day)
    (lay {:mark :rect :stat :count :position :stack})
    plot)

;; ---
;; ## Dodged bars

(-> (sketch tips {:color :sex})
    (view :day)
    (lay {:mark :rect :stat :count :position :dodge})
    plot)

;; ---
;; ## Faceted Simpson's paradox
;;
;; Per-species scatter + per-species lm (from filtered data)
;; + overall lm (from ALL data, broadcasting into every panel).

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.4})
    (lay :lm)
    (facet :species)
    ;; Broadcast overall lm (`:color nil` cancels shared color)
    (overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    plot)

;; ---
;; ## Histogram + KDE

(-> (sketch iris)
    (view :sepal_length :sepal_length)
    (lay {:mark :bar :stat :bin :normalize :density :alpha 0.5})
    (lay {:mark :area :stat :kde})
    plot)

;; ---
;; ## Error bars

(def experiment {:condition ["A" "B" "C" "D"]
                 :mean [10.0 15.0 12.0 18.0]
                 :ci_lo [8.0 12.0 9.5 15.5]
                 :ci_hi [12.0 18.0 14.5 20.5]})

(-> (sketch experiment)
    (view :condition :mean)
    (lay :point)
    (lay :errorbar {:ymin :ci_lo :ymax :ci_hi})
    plot)

;; ---
;; ## Bubble chart

(-> (sketch iris {:color :species :size :petal_width})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.6})
    plot)

;; ---
;; ## Coord flip

(-> (sketch iris)
    (view {:x :species :y :sepal_width :coord :flip})
    (lay :boxplot)
    plot)

;; ---
;; ## Stats-as-data + verbs
;;
;; Per-species density from stat output, rendered through the verb API.

(-> iris
    (tc/group-by [:species])
    (tc/process-group-data
     (fn [ds]
       (let [hist (fastmath.stats/histogram (ds :sepal_length) (:bin-method defaults/defaults))
             bins (:bins-maps hist)
             n (reduce + 0 (map :count bins))]
         (tc/dataset
          {:sepal_length (mapv #(/ (+ (double (:min %)) (double (:max %))) 2.0) bins)
           :density (mapv (fn [b]
                            (let [bw (- (double (:max b)) (double (:min b)))]
                              (if (and (pos? n) (pos? bw))
                                (/ (double (:count b)) (* n bw)) 0.0)))
                          bins)}))))
    (tc/ungroup {:add-group-as-column true})
    ;; Now it's a tidy dataset — use verbs:
    (sketch {:color :species})
    (view :sepal_length :density)
    (lay :area {:alpha 0.4})
    plot)

;; # What the Verbs Reveal
;;
;; ## What composes beautifully
;;
;; - `(-> (sketch data shared) (view ...) (lay ...) (facet ...) plot)`
;;   is a clean pipeline for the common case.
;; - Multiple `view` calls add entries (small multiples, SPLOM).
;; - Multiple `lay` calls add methods (multi-layer).
;; - `facet` and `facet-grid` apply to all entries.
;; - Stats-as-data flows naturally into `sketch` → `view` → `lay`.
;;
;; ## What `overlay` solves
;;
;; The `overlay` verb adds an entry with its own methods:
;; - **Simpson's paradox**: `(overlay :x :y [{:mark :line :stat :lm :color nil}])`
;; - **Broadcast**: `(overlay :x :y [{:mark :line :stat :loess}])` after `facet`
;; - **Per-panel annotation**: `(overlay {:x :a :facet-col "setosa"} [{:mark :rule-h ...}])`
;; - **Per-entry data**: `(overlay {:x :a :y :b :data subset} [{:mark :line :stat :lm}])`
;; - **SPLOM diagonal**: `(reduce (fn [sk [x y]] (overlay sk x y ...)) ...)`
;;
;; No `(update :entries ...)` needed.
;;
;; ## What's still open
;;
;; - `facet-wrap` for wrapping many categories into a grid
;; - Stat chains in the data model (explored in `scratch_compose.clj`)
;; - Stat output column override: `(lay sk :histogram {:y :density})`

;; # More Examples

;; ---
;; ## Rug + scatter

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.5})
    (lay :rug)
    plot)

;; ---
;; ## Column inference: 2-column dataset

(-> (sketch {:x [1 2 3 4 5] :y [2 4 3 5 4]})
    (view)
    plot)

;; `(view)` with no columns infers `:x` and `:y` from the dataset shape.

;; ---
;; ## Column inference: 3-column dataset → x, y, color

(-> (sketch {:x [1 2 3 4] :y [2 4 3 5] :g ["a" "a" "b" "b"]})
    (view)
    plot)

;; ---
;; ## Inference: one numerical column → histogram

(-> (sketch iris)
    (view :sepal_length)
    plot)

;; ---
;; ## Inference: one categorical column → bar chart

(-> (sketch iris)
    (view :species)
    plot)

;; ---
;; ## Inference: two numerical columns → scatter

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    plot)

;; ---
;; ## Inference: categorical × numerical → scatter

(-> (sketch iris)
    (view :species :sepal_width)
    plot)

;; ---
;; ## Inference with color: two numerical + color → grouped scatter

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    plot)

;; ---
;; ## Inference + faceting

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (facet :species)
    plot)

;; ---
;; ## Reusable recipe (no data)

(def scatter+lm
  "A recipe: scatter + regression. No data yet."
  (-> (sketch)
      (view :sepal_length :sepal_width)
      (lay :point {:alpha 0.5})
      (lay :lm)))

;; Apply the recipe to iris:
(-> scatter+lm (with-data iris) plot)

;; ---
;; ## Recipe + shared aesthetics

(-> scatter+lm
    (with-data iris)
    (assoc :shared {:color :species})
    plot)

;; ---
;; ## Recipe + faceting

(-> scatter+lm
    (with-data iris)
    (assoc :shared {:color :species})
    (facet :species)
    plot)

;; ---
;; ## Contour + scatter

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :contour)
    (lay :point {:alpha 0.3})
    plot)

;; ---
;; ## Paired samples

(def paired {:subject ["A" "A" "B" "B" "C" "C" "D" "D"]
             :time ["before" "after" "before" "after"
                    "before" "after" "before" "after"]
             :score [5 8 7 6 3 9 6 7]})

(-> (sketch paired)
    (view :time :score)
    (lay :line {:group :subject})
    (lay :point)
    plot)

;; ---
;; ## Time series

(def ts-data {:month (mapcat (fn [_] (range 1 13)) (range 3))
              :city (mapcat #(repeat 12 %) ["NYC" "LA" "Chicago"])
              :temp (concat [32 35 45 55 65 75 80 78 68 55 42 34]
                            [58 60 62 68 72 78 82 83 80 72 64 59]
                            [25 28 38 50 62 72 78 76 66 52 38 27])})

(-> (sketch ts-data {:color :city})
    (view :month :temp)
    (lay :line)
    (lay :point)
    plot)

;; ---
;; ## Temporal x-axis

(def daily
  (tc/dataset {:date (mapv #(jt/local-date 2024 1 %) (range 1 29))
               :value (mapv (fn [i] (+ 10.0 (* 5.0 (Math/sin (/ i 3.0))))) (range 28))}))

(-> (sketch daily)
    (view :date :value)
    (lay :line)
    (lay :point)
    plot)

;; ---
;; ## Fill bars (proportions)

(-> (sketch penguins {:color :species})
    (view :island)
    (lay {:mark :rect :stat :count :position :fill})
    plot)

;; ---
;; ## Faceted stacked bars

(-> (sketch tips {:color :smoker})
    (view :day)
    (lay {:mark :rect :stat :count :position :stack})
    (facet :sex)
    plot)

;; ---
;; ## LM with SE confidence band

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.3})
    (lay :lm {:se true})
    plot)

;; ---
;; ## Asymmetric SPLOM (2×3)

(-> (sketch iris {:color :species})
    (view (cross [:sepal_length :petal_length]
                 [:sepal_width :petal_width :petal_length]))
    (lay :point {:alpha 0.5})
    plot)

;; ---
;; ## Mixed grid (structural + data axes)

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (view :sepal_length :petal_width)
    (lay :point {:alpha 0.5})
    (facet :species)
    plot)

;; ---
;; ## Mixed grid + broadcast regression

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (view :sepal_length :petal_width)
    (lay :point {:alpha 0.5})
    (facet :species)
    (overlay :sepal_length :sepal_width [{:mark :line :stat :lm}])
    (overlay :sepal_length :petal_width [{:mark :line :stat :lm}])
    plot)

;; ---
;; ## Per-facet method variation
;;
;; setosa gets lm, versicolor gets loess, virginica gets scatter only.

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.5})
    (facet :species)
    (overlay {:x :sepal_length :y :sepal_width :facet-col "setosa"
              :data (tc/select-rows iris (fn [r] (= (r :species) "setosa")))}
             [{:mark :line :stat :lm}])
    (overlay {:x :sepal_length :y :sepal_width :facet-col "versicolor"
              :data (tc/select-rows iris (fn [r] (= (r :species) "versicolor")))}
             [{:mark :line :stat :loess}])
    plot)

;; ---
;; ## Stacked area

(def revenue
  (tc/dataset {:year [2020 2020 2020 2021 2021 2021 2022 2022 2022]
               :product ["A" "B" "C" "A" "B" "C" "A" "B" "C"]
               :amount [100 80 60 120 90 70 130 110 80]}))

(-> (sketch revenue {:color :product})
    (view :year :amount)
    (lay {:mark :area :stat :identity :position :stack})
    plot)

;; ---
;; ## Wide data via `tc/pivot->longer`

(-> (tc/dataset {:time [1 2 3 4 5 6]
                 :nyc  [30 35 45 55 65 75]
                 :la   [58 60 62 68 72 78]
                 :chi  [25 28 38 50 62 72]})
    (tc/pivot->longer [:nyc :la :chi]
                      {:target-columns [:city]
                       :value-column-name :temp})
    (sketch {:color :city})
    (view :time :temp)
    (lay :line)
    (lay :point)
    plot)

;; ---
;; ## Dumbbell chart

(def before-after
  (tc/dataset {:category ["A" "A" "B" "B" "C" "C" "D" "D"]
               :condition ["before" "after" "before" "after"
                           "before" "after" "before" "after"]
               :value [10 15 12 18 8 14 20 22]}))

(-> (sketch before-after)
    (view :category :value)
    (lay :line {:group :category})
    (lay :point {:color :condition})
    plot)

;; ---
;; ## Histogram

(-> (sketch iris)
    (view :sepal_length)
    (lay :histogram)
    plot)

;; ---
;; ## Bar chart

(-> (sketch iris)
    (view :species)
    (lay :bar)
    plot)

;; ---
;; ## Density

(-> (sketch iris {:color :species})
    (view :sepal_length)
    (lay :density)
    plot)

;; ---
;; ## Ridgeline

(-> (sketch iris)
    (view :species :sepal_width)
    (lay :ridgeline)
    plot)

;; ---
;; ## Lollipop

(-> (sketch {:item ["A" "B" "C" "D" "E"] :value [23 45 12 67 34]})
    (view :item :value)
    (lay :lollipop)
    plot)

;; ---
;; ## Summary (mean ± SE)

(-> (sketch iris {:color :species})
    (view :species :sepal_width)
    (lay :summary)
    plot)

;; ---
;; ## Heatmap (tile)

(-> (sketch iris)
    (view :sepal_length :sepal_width)
    (lay :tile)
    plot)

;; ---
;; ## SPLOM with explicit diagonal (density vs scatter+lm)

(let [cols [:sepal_length :sepal_width :petal_length]
      off-diag [{:mark :point :stat :identity :alpha 0.4} {:mark :line :stat :lm}]
      on-diag  [{:mark :area :stat :kde :alpha 0.4}]]
  (-> (reduce (fn [sk [x y]]
                (overlay sk x y (if (= x y) on-diag off-diag)))
              (sketch iris {:color :species})
              (cross cols cols))
      plot))

;; # Stats-as-Data + Stat Chains
;;
;; Stats are functions: dataset → dataset.
;; They compose with the verbs naturally.

(defn stat-bin
  "Bin a numeric column. Returns dataset with `x-col`, `:count`, `:density`."
  [ds x-col]
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

(defn stat-lm
  "Fit linear model. Returns dataset with `x-col`, `y-col` (predicted)."
  [ds x-col y-col]
  (let [clean (tc/drop-missing ds [x-col y-col])]
    (when (>= (tc/row-count clean) 2)
      (let [reg (regr/lm (double-array (clean y-col)) (double-array (clean x-col)))
            x-lo (dfn/reduce-min (clean x-col)) x-hi (dfn/reduce-max (clean x-col))
            step (/ (- x-hi x-lo) 79)
            gxs (mapv #(+ x-lo (* % step)) (range 80))
            gys (mapv #(regr/predict reg [%]) gxs)]
        (tc/dataset {x-col gxs y-col gys})))))

(defn stat-loess
  "LOESS smooth. Returns dataset with `x-col`, `y-col` (smoothed)."
  [ds x-col y-col]
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

(defn stat-residuals
  "Compute residuals: actual - predicted."
  [ds x-col y-col]
  (let [clean (tc/drop-missing ds [x-col y-col])
        reg (regr/lm (double-array (clean y-col)) (double-array (clean x-col)))
        predicted (mapv #(regr/predict reg [%]) (clean x-col))
        residuals (dfn/- (clean y-col) predicted)]
    (tc/dataset {x-col (clean x-col) :residual residuals})))

;; ---
;; ## Multiple independent datasets

(let [ds-a {:x [1 2 3 4 5] :y [2 4 3 5 4]}
      ds-b {:x [1 2 3 4 5] :y [5 3 4 2 3]}]
  (-> (sketch ds-a)
      (overlay :x :y [{:mark :point :stat :identity} {:mark :line :stat :lm}])
      (overlay {:x :x :y :y :data ds-b}
               [{:mark :point :stat :identity} {:mark :line :stat :lm}])
      plot))

;; ---
;; ## Simpson's paradox from visible stat output

(let [per-sp (-> iris
                 (tc/group-by [:species])
                 (tc/process-group-data (fn [ds] (stat-lm ds :sepal_length :sepal_width)))
                 (tc/ungroup {:add-group-as-column true}))
      overall (stat-lm iris :sepal_length :sepal_width)]
  (-> (sketch iris {:color :species})
      (view :sepal_length :sepal_width)
      (lay :point {:alpha 0.4})
      (overlay {:data per-sp :x :sepal_length :y :sepal_width}
               [{:mark :line :stat :identity :color :species}])
      (overlay {:data overall :x :sepal_length :y :sepal_width}
               [{:mark :line :stat :identity :color nil}])
      plot))

;; ---
;; ## Different stats per group (impossible in ggplot2)

(let [setosa-smooth (-> (tc/select-rows iris (fn [r] (= (r :species) "setosa")))
                        (stat-loess :sepal_length :sepal_width)
                        (tc/add-column :species (repeat 80 "setosa")))
      versi-lm (-> (tc/select-rows iris (fn [r] (= (r :species) "versicolor")))
                   (stat-lm :sepal_length :sepal_width)
                   (tc/add-column :species (repeat 80 "versicolor")))]
  (-> (sketch iris {:color :species})
      (view :sepal_length :sepal_width)
      (lay :point {:alpha 0.4})
      (overlay {:data setosa-smooth :x :sepal_length :y :sepal_width}
               [{:mark :line :stat :identity :color :species}])
      (overlay {:data versi-lm :x :sepal_length :y :sepal_width}
               [{:mark :line :stat :identity :color :species}])
      plot))

;; ---
;; ## Residual plot

(let [resid (stat-residuals iris :sepal_length :sepal_width)]
  (-> (sketch resid)
      (view :sepal_length :residual)
      (lay :point {:alpha 0.5})
      (overlay {:data (tc/dataset {:sepal_length [4.3 7.9] :residual [0 0]})
                :x :sepal_length :y :residual}
               [{:mark :line :stat :identity}])
      plot))

;; ---
;; ## Side-by-side: scatter + residuals

(let [resid (stat-residuals iris :sepal_length :sepal_width)
      lm-ds (stat-lm iris :sepal_length :sepal_width)]
  (-> (sketch iris {:color :species})
      (view :sepal_length :sepal_width)
      (lay :point {:alpha 0.4})
      (overlay {:data lm-ds :x :sepal_length :y :sepal_width}
               [{:mark :line :stat :identity :color nil}])
      (overlay {:data resid :x :sepal_length :y :residual}
               [{:mark :point :stat :identity :alpha 0.4 :color nil}])
      (overlay {:data (tc/dataset {:sepal_length [4.3 7.9] :residual [0 0]})
                :x :sepal_length :y :residual}
               [{:mark :line :stat :identity :color nil}])
      plot))

;; ---
;; ## Per-species cumulative histogram

(let [cum-ds (-> iris
                 (tc/group-by [:species])
                 (tc/process-group-data
                  (fn [ds]
                    (let [bins (stat-bin ds :sepal_length)]
                      (tc/add-column bins :count-cumulative
                                     (vec (reductions + (:count bins)))))))
                 (tc/ungroup {:add-group-as-column true}))]
  (-> (sketch cum-ds {:color :species})
      (view :sepal_length :count-cumulative)
      (lay :line)
      (lay :point)
      plot))

;; ---
;; ## Per-species cumulative density

(-> iris
    (tc/group-by [:species])
    (tc/process-group-data
     (fn [ds]
       (let [bins (stat-bin ds :sepal_length)]
         (tc/add-column bins :density-cumulative
                        (vec (reductions + (:density bins)))))))
    (tc/ungroup {:add-group-as-column true})
    (sketch {:color :species})
    (view :sepal_length :density-cumulative)
    (lay :line)
    plot)

;; ---
;; ## Scatter + LOESS (raw + stat in same panel)

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.3})
    (overlay {:data (stat-loess iris :sepal_length :sepal_width)
              :x :sepal_length :y :sepal_width}
             [{:mark :line :stat :identity :color nil}])
    plot)

;; ---
;; ## Two smoothers (LM + LOESS)

(-> (sketch iris {:color :species})
    (view :sepal_length :sepal_width)
    (lay :point {:alpha 0.3})
    (overlay {:data (stat-lm iris :sepal_length :sepal_width)
              :x :sepal_length :y :sepal_width}
             [{:mark :line :stat :identity :color nil}])
    (overlay {:data (stat-loess iris :sepal_length :sepal_width)
              :x :sepal_length :y :sepal_width}
             [{:mark :line :stat :identity :color nil}])
    plot)
