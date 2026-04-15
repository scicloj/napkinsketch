(ns scicloj.napkinsketch.impl.resolve
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.datetime :as dt-dt]
            [java-time.api :as jt]
            [scicloj.napkinsketch.impl.defaults :as defaults]))

;; ---- Helpers ----

(defn column-ref?
  "True if v is a column reference (keyword or string).
   Both keyword and string column names are valid references."
  [v]
  (or (keyword? v) (string? v)))

(defn- normalize-col-ref
  "Pass through column references as-is (no string→keyword conversion)."
  [v]
  v)

(defn- normalize-col-refs
  "Pass through column references as-is (no string→keyword conversion)."
  [m]
  m)

(defn ensure-keyword-columns
  "DEPRECATED — now a pass-through. String column names are preserved."
  [ds]
  ds)

(defn parse-view-spec
  "Parse a view spec: a keyword or string becomes a histogram view (x=y),
  a vector becomes {:x ... :y ...}, a map passes through.
  Column references are preserved as-is (keywords stay keywords, strings stay strings)."
  [spec]
  (cond
    (keyword? spec) {:x spec :y spec}
    (string? spec) {:x spec :y spec}
    (map? spec) spec
    :else {:x (first spec)
           :y (second spec)}))

(defn validate-columns
  "Check that every column-referencing key in view-map names a real column in ds.
   Accepts both keyword and string column refs, and checks for cross-type matches."
  ([ds view-map]
   (let [col-names (set (tc/column-names ds))]
     (doseq [k defaults/column-keys
             :let [col (get view-map k)]
             :when (and col (column-ref? col)
                        (not (col-names col))
                         ;; Check cross-type: keyword ref vs string col name, or vice versa
                        (not (and (keyword? col) (col-names (name col))))
                        (not (and (string? col) (col-names (keyword col)))))]
       (throw (ex-info (str "Column " col " (from " k ") not found in dataset. Available: " (sort col-names))
                       {:key k :column col :available (sort col-names)})))))
  ([ds role col]
   (let [col-names (set (tc/column-names ds))]
     (when (and (column-ref? col)
                (not (col-names col))
                (not (and (keyword? col) (col-names (name col))))
                (not (and (string? col) (col-names (keyword col)))))
       (throw (ex-info (str "Column " col " (from " role ") not found in dataset. Available: " (sort col-names))
                       {:key role :column col :available (sort col-names)}))))))

(defn multi-spec?
  "True if specs is a sequence of view specs rather than a single spec."
  [specs]
  (and (sequential? specs)
       (let [fst (first specs)]
         (or (sequential? fst) (map? fst)))))

(defrecord Plan [panels width height])

(defn plan?
  "True if x is a plan (the resolved data-space geometry)."
  [x]
  (instance? Plan x))

(defrecord Layer [mark style])

(defn layer?
  "True if x is a layer (resolved geometry for one mark)."
  [x]
  (instance? Layer x))

(defrecord Method [mark stat])

(defn method?
  "True if x is a method (mark + stat + position bundle)."
  [x]
  (instance? Method x))

;; ---- Layer ----

(def annotation-marks
  "Mark types that render as annotations (rules, bands) rather than data layers."
  #{:rule-h :rule-v :band-h :band-v})

(defn merge-layer
  "Merge a layer into each view, preserving :__base for additive lay."
  [views overrides]
  (let [overrides (normalize-col-refs overrides)]
    (mapv (fn [v]
            (when (:data v)
              (validate-columns (:data v) overrides))
            (let [base (or (:__base v) v)]
              (assoc (merge v overrides) :__base base)))
          views)))

(defn lay
  "Apply one or more methods to views. Additive: calling lay on
   already-layered views appends new methods rather than overwriting."
  [base-views & layer-specs]
  (doseq [spec layer-specs]
    (when-not (map? spec)
      (throw (ex-info (str "Layer spec must be a map, got: " (type spec)
                           ". Use lay-point, lay-line, etc. to add layers.")
                      {:spec spec}))))
  (let [ann-specs (filter #(and (map? %) (annotation-marks (:mark %))) layer-specs)
        data-specs (remove #(and (map? %) (annotation-marks (:mark %))) layer-specs)
        ;; Separate existing annotations from data views
        existing-anns (filter #(annotation-marks (:mark %)) base-views)
        data-views (remove #(annotation-marks (:mark %)) base-views)
        has-marks? (some :mark data-views)
        ;; Recover unique bare bases — strip mark/stat/position but keep
        ;; facet keys and faceted dataset so new layers inherit the facet split.
        ;; Only fall back to __base when there are no facet keys (pre-facet case).
        bare-views (if has-marks?
                     (let [bases (map (fn [v]
                                        (if (or (:facet-row v) (:facet-col v))
                                          (dissoc v :mark :stat :position)
                                          (or (:__base v) (dissoc v :mark :stat :position))))
                                      data-views)]
                       (vec (distinct bases)))
                     data-views)
        new-layers (apply concat (map #(merge-layer bare-views %) data-specs))]
    (vec (concat (when has-marks? data-views)
                 new-layers
                 existing-anns
                 ann-specs))))

;; ---- Coord ----

(defn coord
  "Set coordinate system on views."
  [views c]
  (when-not (#{:cartesian :flip :polar :fixed} c)
    (throw (ex-info (str "Coordinate must be :cartesian, :flip, :polar, or :fixed, got: " (pr-str c))
                    {:coord c})))
  (mapv #(assoc % :coord c) views))

;; ---- Annotation Constructors ----

(defn- check-annotation-arg [name v]
  (when-not (number? v)
    (throw (ex-info (str name " requires a number, got: " (pr-str v))
                    {:argument v}))))

(defn rule-v
  "Vertical reference line at x = intercept."
  [intercept]
  (check-annotation-arg "rule-v intercept" intercept)
  {:mark :rule-v :intercept intercept})

(defn rule-h
  "Horizontal reference line at y = intercept."
  [intercept]
  (check-annotation-arg "rule-h intercept" intercept)
  {:mark :rule-h :intercept intercept})

(defn band-v
  "Vertical shaded band from x = lo to x = hi.
  Optional opts map: {:alpha 0.3} overrides band opacity."
  ([lo hi] (band-v lo hi {}))
  ([lo hi opts]
   (check-annotation-arg "band-v lo" lo)
   (check-annotation-arg "band-v hi" hi)
   (merge {:mark :band-v :lo lo :hi hi} opts)))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi.
  Optional opts map: {:alpha 0.3} overrides band opacity."
  ([lo hi] (band-h lo hi {}))
  ([lo hi opts]
   (check-annotation-arg "band-h lo" lo)
   (check-annotation-arg "band-h hi" hi)
   (merge {:mark :band-h :lo lo :hi hi} opts)))

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (for [x xs, y ys] [x y]))

;; ---- Faceting ----

(defn resolve-col-name
  "Find the actual column name in a dataset that matches a column reference.
   Handles keyword/string mismatch: if ref is :x, checks for both :x and \"x\".
   Returns the matching name, or ref unchanged if no match is found."
  [ds ref]
  (when (and ds ref)
    (let [names (set (tc/column-names ds))]
      (cond
        (contains? names ref) ref
        (and (keyword? ref) (contains? names (name ref))) (name ref)
        (and (string? ref) (contains? names (keyword ref))) (keyword ref)
        :else ref))))

(defn facet-grid
  "Split each view by two categorical columns for a row x column grid.
   Either column may be nil for a single-dimension facet.
   Each resulting view gets :facet-row and :facet-col keys."
  [views row-col col-col]
  (vec
   (mapcat
    (fn [v]
      (if-not (:data v)
        [v]
        (do
          (when row-col (validate-columns (:data v) :facet-row row-col))
          (when col-col (validate-columns (:data v) :facet-col col-col))
          (let [row-col (when row-col (resolve-col-name (:data v) row-col))
                col-col (when col-col (resolve-col-name (:data v) col-col))
                group-cols (filterv some? [row-col col-col])
                groups (tc/group-by (:data v) group-cols {:result-type :as-map})]
            (map (fn [[gk gds]]
                   (assoc v :data gds
                          :facet-row (if row-col (get gk row-col) "_")
                          :facet-col (if col-col (get gk col-col) "_")))
                 groups)))))
    views)))

(defn facet
  "Split each view by a categorical column.
   Default layout is a horizontal row of panels.
   Pass :col as direction for a vertical column of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  ([views col] (facet views col :row))
  ([views col direction]
   (case direction
     :row (facet-grid views nil col)
     :col (facet-grid views col nil))))

;; ---- Column Type Detection ----

(defn column-type
  "Classify a dataset column as :categorical, :numerical, or :temporal.
   Resolves keyword/string column-name mismatches via resolve-col-name
   so callers don't have to."
  [ds col]
  (let [resolved (resolve-col-name ds col)
        c (when resolved (ds resolved))
        n (count c)]
    (if (or (nil? c) (zero? n))
      ;; Missing or empty column — treat as numerical (can't infer, let
      ;; downstream handle gracefully)
      :numerical
      (let [dt (dtype/elemwise-datatype c)
            t (try (tcc/typeof c) (catch Exception _ nil))
            ;; All-missing columns (e.g., [##NaN ##NaN]) get :boolean dtype
            ;; with nil values. Treat as numerical since the input was numeric.
            all-missing? (every? nil? (take 100 c))]
        (cond
          all-missing? :numerical
          ;; Check dtype first to catch numeric columns
          (#{:float32 :float64 :int8 :int16 :int32 :int64} dt) :numerical
          (#{:string :keyword :symbol :text} t) :categorical
          ;; Check for temporal types via dtype-next metadata
          (dt-dt/datetime-datatype? dt) :temporal
          ;; Fallback for java.util.Date (:object dtype)
          (instance? java.util.Date (first c)) :temporal
          ;; Check actual values
          (every? number? (take 100 c)) :numerical
          :else :categorical)))))

(defn temporal->epoch-ms
  "Convert a temporal value to epoch-milliseconds (double).
   Accepts LocalDate, LocalDateTime, Instant, and java.util.Date.
   Returns ##NaN for nil input."
  [v]
  (cond
    (nil? v) ##NaN
    (jt/instant? v) (double (jt/to-millis-from-epoch v))
    (jt/local-date-time? v) (double (jt/to-millis-from-epoch (jt/instant v (jt/zone-offset 0))))
    (jt/local-date? v) (double (jt/to-millis-from-epoch (jt/instant (jt/local-date-time v (jt/local-time 0)) (jt/zone-offset 0))))
    (instance? java.util.Date v) (double (.getTime ^java.util.Date v))
    :else (double v)))

(defn- temporalize-column
  "Replace a temporal column in a dataset with its epoch-ms numeric equivalent.
   Uses vectorized dt-dt/datetime->epoch for typed temporal columns;
   falls back to scalar map-columns for java.util.Date (:object dtype).
   Casts to :float64 so NaN from nil temporal values is recognized as missing."
  [ds col]
  (if (dt-dt/datetime-datatype? (dtype/elemwise-datatype (ds col)))
    (tc/add-column ds col (dtype/elemwise-cast
                           (dt-dt/datetime->epoch :epoch-milliseconds (ds col))
                           :float64))
    (tc/map-columns ds col [col] temporal->epoch-ms)))

(defn- temporal->local-date-time
  "Convert any supported temporal value to LocalDateTime (required by wadogo :datetime scale).
   LocalDate gets midnight, Instant and java.util.Date get UTC conversion."
  [v]
  (cond
    (jt/local-date-time? v) v
    (jt/local-date? v) (jt/local-date-time v (jt/local-time 0))
    (jt/instant? v) (jt/local-date-time v "UTC")
    (instance? java.util.Date v) (jt/local-date-time (jt/instant v) "UTC")
    :else v))

(defn- temporal-extent
  "Return [min max] of original temporal values in a column, as LocalDateTime.
   All temporal types are normalized to LocalDateTime for wadogo :datetime scale."
  [ds col]
  (let [vals (vec (remove nil? (ds col)))]
    (when (seq vals)
      (let [ldts (mapv temporal->local-date-time vals)]
        [(apply jt/min ldts) (apply jt/max ldts)]))))

;; ---- Resolve Draft Layer ----

(defn infer-column-types
  "Detect x and y column types (:categorical, :numerical, :temporal).
   Temporal columns are converted to epoch-ms numbers; their original
   extents (as LocalDateTime) are preserved for wadogo :datetime ticks.
   Resolves column names to match the dataset's actual column names
   (handles keyword/string mismatch).
   Returns a map with keys :ds, :x-type, :y-type, :x-temporal?, :y-temporal?,
   :x-temporal-extent, :y-temporal-extent, :x-resolved, :y-resolved."
  [ds v]
  (let [x-res (resolve-col-name ds (:x v))
        y-res (resolve-col-name ds (:y v))
        x-type (or (:x-type v) (column-type ds x-res))
        ;; When x and y reference the same column, propagate x-type to y-type
        ;; rather than returning nil — callers (e.g., `validate-numeric-column`)
        ;; rely on y-type being populated for validation.
        y-type (or (:y-type v) (when y-res
                                 (if (= x-res y-res)
                                   x-type
                                   (column-type ds y-res))))
        x-temporal? (= x-type :temporal)
        y-temporal? (= y-type :temporal)
        x-temp-extent (when x-temporal? (temporal-extent ds x-res))
        y-temp-extent (when y-temporal? (temporal-extent ds y-res))
        ds (cond-> ds
             x-temporal? (temporalize-column x-res)
             y-temporal? (temporalize-column y-res))]
    {:ds ds
     :x-type (if x-temporal? :numerical x-type)
     :y-type (if y-temporal? :numerical y-type)
     :x-temporal? x-temporal?
     :y-temporal? y-temporal?
     :x-temporal-extent x-temp-extent
     :y-temporal-extent y-temp-extent
     :x-resolved x-res
     :y-resolved y-res}))

(defn resolve-aesthetics
  "Classify each aesthetic channel (:color, :size, :alpha, :text) as either
   a column reference or a fixed literal value.
   For :color, a string value is checked against dataset column names
   (both string and keyword) — if it matches, it's treated as a column ref;
   otherwise it's a literal color string.
   Returns a map with keys :color, :color-is-col?, :color-type, :fixed-color,
   :size, :size-is-col?, :fixed-size, :alpha, :alpha-is-col?, :fixed-alpha,
   :text-col."
  [ds v]
  (let [color-val (let [cv (:color v)]
                    (if (and (string? cv))
                      ;; String :color — check if it matches a dataset column name
                      (let [names (set (tc/column-names ds))]
                        (cond
                          (contains? names cv) cv
                          (contains? names (keyword cv)) (keyword cv)
                          :else cv))
                      cv))
        color-is-col? (and color-val (column-ref? color-val)
                           ;; After resolution, if color-val is a string that doesn't
                           ;; match any column, it's a literal color
                           (let [names (set (tc/column-names ds))]
                             (contains? names (resolve-col-name ds color-val))))
        c-type (when color-is-col?
                 (or (:color-type v) (column-type ds color-val)))
        fixed-color (when (and color-val (not color-is-col?)) color-val)
        size-val (:size v)
        size-is-col? (and size-val (column-ref? size-val))
        fixed-size (when (and size-val (not size-is-col?)) size-val)
        alpha-val (:alpha v)
        alpha-is-col? (and alpha-val (column-ref? alpha-val))
        fixed-alpha (when (and alpha-val (not alpha-is-col?)) alpha-val)
        text-val (:text v)
        text-col (when (and text-val (column-ref? text-val)) text-val)]
    {:color (when color-is-col? color-val)
     :color-is-col? color-is-col?
     :color-type c-type
     :fixed-color fixed-color
     :size (when size-is-col? size-val)
     :size-is-col? size-is-col?
     :fixed-size fixed-size
     :alpha (when alpha-is-col? alpha-val)
     :alpha-is-col? alpha-is-col?
     :fixed-alpha fixed-alpha
     :text-col text-col}))

(defn infer-grouping
  "Build the grouping vector from explicit :group and categorical color column.
   Explicit groups are passed through; categorical color columns are appended.
   Returns a vector of column references (keywords or strings)."
  [v color-type color-col]
  (let [explicit-group (let [g (:group v)]
                         (cond (nil? g) nil
                               (column-ref? g) [g]
                               (sequential? g) (vec g)
                               :else [g]))
        color-group (when (= color-type :categorical) [color-col])
        group (vec (distinct (concat (or explicit-group color-group [])
                                     (when (and color-group explicit-group) color-group))))]
    group))

(def ^:private x-only-stats
  "Stats that consume only an x column and synthesize y themselves
   (counts, bins, densities). Used to permit x-only views for marks
   driven by these stats even when the method registry's :x-only flag
   is missing (e.g., tests that construct views directly)."
  #{:bin :count :kde})

(defn infer-method
  "Choose mark and stat from column types when the user hasn't specified them.
   Rules:
     - x only, categorical → :rect + :count (bar chart)
     - x only, numerical   → :bar  + :bin  (histogram)
     - x and y, mixed types → :point + :identity (scatter)
     - x and y, same type   → :point + :identity (scatter)
   When the user provides an explicit mark, stat defaults to :identity
   unless they also provided an explicit stat."
  [v x-type y-type]
  (let [diagonal? (= (:x v) (:y v))
        [default-mark default-stat]
        (cond
          (or diagonal? (nil? (:y v)))
          (if (= x-type :categorical) [:rect :count] [:bar :bin])
          (not= x-type y-type) [:point :identity]
          :else [:point :identity])
        mark (or (:mark v) default-mark)
        stat (or (:stat v) (if (:mark v) :identity default-stat))]
    {:mark mark :stat stat}))

(defn resolve-draft-layer
  "Resolve a single draft layer: infer column types, aesthetics, grouping, and method.
   Delegates to `infer-column-types`, `resolve-aesthetics`, `infer-grouping`,
   and `infer-method` — each named for the inference step it performs.
   Resolves column names to match the dataset's actual names (keyword/string).
   Also normalizes user-facing shorthand options:
     - :bandwidth → :cfg {:kde-bandwidth ...}
     - :tile with :fill → stat :identity"
  [v]
  (if-not (:data v)
    v
    (let [ds (let [d (:data v)] (if (tc/dataset? d) d (tc/dataset d)))
          {:keys [x-type y-type x-temporal? y-temporal?
                  x-temporal-extent y-temporal-extent
                  x-resolved y-resolved]
           resolved-ds :ds} (infer-column-types ds v)
          ;; Update v with resolved column names so downstream code
          ;; (stat.clj, extract.clj) can use (ds (:x v)) directly.
          v (cond-> v
              x-resolved (assoc :x x-resolved)
              y-resolved (assoc :y y-resolved))
          ;; Also resolve aesthetic column refs
          v (cond-> v
              (and (:size v) (column-ref? (:size v)))
              (assoc :size (resolve-col-name resolved-ds (:size v)))
              (and (:alpha v) (column-ref? (:alpha v)))
              (assoc :alpha (resolve-col-name resolved-ds (:alpha v)))
              (and (:text v) (column-ref? (:text v)))
              (assoc :text (resolve-col-name resolved-ds (:text v)))
              (and (:group v) (column-ref? (:group v)))
              (assoc :group (resolve-col-name resolved-ds (:group v))))
          {:keys [color color-type fixed-color
                  size fixed-size alpha fixed-alpha text-col]} (resolve-aesthetics resolved-ds v)
          group (infer-grouping v color-type color)
          {:keys [mark stat]} (infer-method v x-type y-type)
          ;; Validate that marks requiring categorical x are not given numeric x.
          ;; Only check when stat is the natural stat for the mark (not an explicit override).
          categorical-x-marks {:boxplot :boxplot :violin :violin
                               :lollipop :identity :summary :summary
                               :ridgeline :violin :pointrange :summary}
          _ (when (and (= x-type :numerical)
                       (contains? categorical-x-marks mark)
                       (= stat (categorical-x-marks mark)))
              (throw (ex-info (str "lay-" (name mark) " requires a categorical :x column, but "
                                   (pr-str (:x v)) " is numerical. Use a categorical column "
                                   "(e.g., species names) for the x-axis.")
                              {:mark mark :x (:x v) :x-type x-type})))
          ;; Reject x-only views (no :y) for methods that require y.
          ;; Otherwise prepare-points silently fabricates y=0 for every
          ;; point and renders a flat line at the bottom of a [0, 1] domain.
          ;; Three sources of x-only permission:
          ;;   1. :x-only true from the method registry (e.g., :histogram, :rug)
          ;;   2. stat is in `x-only-stats` — :bin/:count/:kde synthesize y
          ;;      from x alone (covers the :rect mark + bar stat too)
          ;;   3. mark is :rug, which is structurally x-only even when
          ;;      constructed without the method registry
          _ (when (and (nil? y-resolved)
                       (not (:x-only v))
                       (not (contains? x-only-stats stat))
                       (not= :rug mark))
              (throw (ex-info (str ":" (name mark) " requires both :x and :y columns. "
                                   "Either pass a y column (e.g., (sk/lay-" (name mark)
                                   " data :x :y)) or use an x-only mark like histogram, "
                                   "density, bar, or rug.")
                              {:mark mark :x (:x v)})))
          resolved (cond-> (assoc v :data resolved-ds :x-type x-type :y-type y-type
                                  :color-type color-type :group group :mark mark :stat stat
                                  :color color :fixed-color fixed-color
                                  :size size :fixed-size fixed-size
                                  :alpha alpha :fixed-alpha fixed-alpha
                                  :text-col text-col)
                     x-temporal? (assoc :x-temporal? true)
                     y-temporal? (assoc :y-temporal? true)
                     x-temporal-extent (assoc :x-temporal-extent x-temporal-extent)
                     y-temporal-extent (assoc :y-temporal-extent y-temporal-extent))
          ;; Normalize :bandwidth shorthand for KDE-based stats
          bw (:bandwidth resolved)
          resolved (if bw
                     (-> resolved (dissoc :bandwidth)
                         (assoc-in [:cfg :kde-bandwidth] bw))
                     resolved)
          ;; Tile + default bin2d stat with a user-supplied :fill (or
          ;; :color as a synonym) → override stat to :identity so the
          ;; pre-computed fill values drive the tile colors directly.
          ;; Only applies to lay-tile (which defaults to :bin2d) -- NOT
          ;; to lay-density2d (:kde2d) or lay-contour, which intentionally
          ;; compute their own fill values from x/y.
          ;; Accepting :color keeps lay-tile friendly for users who
          ;; reach for :color by habit from the other marks. :fill wins
          ;; when both are set.
          tile-override? (and (= (:mark resolved) :tile)
                              (= (:stat resolved) :bin2d)
                              (or (:fill resolved) (:color resolved)))
          resolved (if tile-override?
                     (cond-> (assoc resolved :stat :identity)
                       ;; Promote :color to :fill when :fill is absent so
                       ;; the downstream extract path finds the data.
                       (and (not (:fill resolved)) (:color resolved))
                       (assoc :fill (:color resolved)))
                     resolved)]
      resolved)))

;; ---- Scale Setter ----

(defn scale
  "Set scale options for :x or :y across all views."
  ([views channel type-or-opts]
   (when-not (#{:x :y} channel)
     (throw (ex-info (str "Scale channel must be :x or :y, got: " (pr-str channel))
                     {:channel channel})))
   (if (map? type-or-opts)
     (scale views channel (or (:type type-or-opts) :linear) (dissoc type-or-opts :type))
     (scale views channel type-or-opts {})))
  ([views channel type opts]
   (let [k (case channel :x :x-scale :y :y-scale)]
     (mapv #(assoc % k (merge {:type type} opts)) views))))
