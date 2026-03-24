(ns scicloj.napkinsketch.impl.view
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [scicloj.napkinsketch.impl.defaults :as defaults])
  (:import [java.time LocalDate LocalDateTime Instant ZoneOffset]))

;; ---- Helpers ----

(defn column-ref?
  "True if v is a column reference (keyword).
   String column names are normalized to keywords at the API boundary,
   so by the time column-ref? is called, column references are keywords."
  [v]
  (keyword? v))

(defn- normalize-col-ref
  "Convert a string column reference to a keyword. Pass through other values."
  [v]
  (if (string? v) (keyword v) v))

(defn- normalize-col-refs
  "Normalize string column references to keywords in a map's column-key positions.
   Excludes :color because it can be a literal color string (e.g. \"#FF0000\").
   Color strings are normalized later in resolve-view where dataset context is available."
  [m]
  (reduce (fn [acc k]
            (let [v (get acc k)]
              (if (and (string? v) (not= k :color))
                (assoc acc k (keyword v))
                acc)))
          m
          defaults/column-keys))

(defn- ensure-keyword-columns
  "If dataset has string column names, rename them to keywords."
  [ds]
  (let [renames (into {} (for [c (tc/column-names ds) :when (string? c)]
                           [c (keyword c)]))]
    (if (seq renames)
      (tc/rename-columns ds renames)
      ds)))

(defn parse-view-spec
  "Parse a view spec: a keyword or string becomes a histogram view (x=y),
  a vector becomes {:x ... :y ...}, a map passes through.
  String column references are normalized to keywords."
  [spec]
  (cond
    (keyword? spec) {:x spec :y spec}
    (string? spec) (let [k (keyword spec)] {:x k :y k})
    (map? spec) (normalize-col-refs spec)
    :else {:x (normalize-col-ref (first spec))
           :y (normalize-col-ref (second spec))}))

(defn validate-columns
  "Check that every column-referencing key in view-map names a real column in ds."
  ([ds view-map]
   (let [col-names (set (tc/column-names ds))]
     (doseq [k defaults/column-keys
             :let [col (get view-map k)]
             :when (and col (keyword? col) (not (col-names col)))]
       (throw (ex-info (str "Column " col " (from " k ") not found in dataset. Available: " (sort col-names))
                       {:key k :column col :available (sort col-names)})))))
  ([ds role col]
   (let [col-names (set (tc/column-names ds))]
     (when (and (keyword? col) (not (col-names col)))
       (throw (ex-info (str "Column " col " (from " role ") not found in dataset. Available: " (sort col-names))
                       {:key role :column col :available (sort col-names)}))))))

(defn multi-spec?
  "True if specs is a sequence of view specs rather than a single spec."
  [specs]
  (and (sequential? specs)
       (let [fst (first specs)]
         (or (sequential? fst) (map? fst)))))

(defrecord View [data x])

(defn views?
  "True if x is a vector of View records (i.e., output of `view` or `lay`)."
  [x]
  (and (sequential? x) (seq x) (instance? View (first x))))

(defrecord PlotSpec [views opts])

(defn plot-spec?
  "True if x is a PlotSpec (auto-rendering plot specification)."
  [x]
  (instance? PlotSpec x))

;; ---- View ----

(defn view
  "Create views from data and column specs.
   An optional opts map sets shared aesthetics (e.g. {:color :species})
   that all layers inherit. Layer opts override view-level aesthetics."
  ([data spec-or-x]
   (let [ds (ensure-keyword-columns (if (tc/dataset? data) data (tc/dataset data)))]
     (if (multi-spec? spec-or-x)
       (mapv (fn [spec]
               (let [parsed (parse-view-spec spec)]
                 (validate-columns ds parsed)
                 (map->View (assoc parsed :data ds))))
             spec-or-x)
       (let [parsed (parse-view-spec spec-or-x)]
         (validate-columns ds parsed)
         [(map->View (assoc parsed :data ds))]))))
  ([data x-or-spec y-or-opts]
   (if (map? y-or-opts)
     ;; (view data :x {:color :species}) or (view data [pairs] {:color :species})
     (let [opts (normalize-col-refs y-or-opts)
           base-views (view data x-or-spec)]
       (doseq [v base-views]
         (validate-columns (:data v) opts))
       (mapv #(merge % opts) base-views))
     ;; (view data :x :y) — two column refs (keywords or strings)
     (let [ds (ensure-keyword-columns (if (tc/dataset? data) data (tc/dataset data)))
           v {:x (normalize-col-ref x-or-spec) :y (normalize-col-ref y-or-opts)}]
       (validate-columns ds v)
       [(map->View (assoc v :data ds))])))
  ([data x y opts]
   (let [ds (ensure-keyword-columns (if (tc/dataset? data) data (tc/dataset data)))
         v (merge {:x (normalize-col-ref x) :y (normalize-col-ref y)}
                  (normalize-col-refs opts))]
     (validate-columns ds v)
     [(map->View (assoc v :data ds))])))

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
  (let [ann-specs (filter #(and (map? %) (annotation-marks (:mark %))) layer-specs)
        data-specs (remove #(and (map? %) (annotation-marks (:mark %))) layer-specs)
        ;; Separate existing annotations from data views
        existing-anns (filter #(annotation-marks (:mark %)) base-views)
        data-views (remove #(annotation-marks (:mark %)) base-views)
        has-marks? (some :mark data-views)
        ;; Recover unique bare bases from __base or strip mark/stat/position
        bare-views (if has-marks?
                     (let [bases (map #(or (:__base %) (dissoc % :mark :stat :position)) data-views)]
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
  (mapv #(assoc % :coord c) views))

(defn labs
  "Set labels on views. Keys: :title, :subtitle, :caption, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})
   (labs views {:title \"Title\" :subtitle \"More detail\" :caption \"Source: ...\"})"
  [views label-opts]
  (let [m (cond-> {}
            (:title label-opts) (assoc :title (:title label-opts))
            (:subtitle label-opts) (assoc :subtitle (:subtitle label-opts))
            (:caption label-opts) (assoc :caption (:caption label-opts))
            (:x label-opts) (assoc :x-label (:x label-opts))
            (:y label-opts) (assoc :y-label (:y label-opts)))]
    (mapv #(merge % m) views)))

;; ---- Annotation Constructors ----

(defn rule-v
  "Vertical reference line at x = intercept."
  [intercept]
  {:mark :rule-v :intercept intercept})

(defn rule-h
  "Horizontal reference line at y = intercept."
  [intercept]
  {:mark :rule-h :intercept intercept})

(defn band-v
  "Vertical shaded band from x = lo to x = hi.
  Optional opts map: {:alpha 0.3} overrides band opacity."
  ([lo hi] (band-v lo hi {}))
  ([lo hi opts] (merge {:mark :band-v :lo lo :hi hi} opts)))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi.
  Optional opts map: {:alpha 0.3} overrides band opacity."
  ([lo hi] (band-h lo hi {}))
  ([lo hi opts] (merge {:mark :band-h :lo lo :hi hi} opts)))

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (for [x xs, y ys] [x y]))

;; ---- Faceting ----

(defn facet-grid
  "Split each view by two categorical columns for a row × column grid.
   Either column may be nil for a single-dimension facet.
   Each resulting view gets :facet-row and :facet-col keys."
  [views row-col col-col]
  (let [row-col (normalize-col-ref row-col)
        col-col (normalize-col-ref col-col)]
    (vec
     (mapcat
      (fn [v]
        (if-not (:data v)
          [v]
          (do
            (when row-col (validate-columns (:data v) :facet-row row-col))
            (when col-col (validate-columns (:data v) :facet-col col-col))
            (let [group-cols (filterv some? [row-col col-col])
                  groups (tc/group-by (:data v) group-cols {:result-type :as-map})]
              (map (fn [[gk gds]]
                     (assoc v :data gds
                            :facet-row (if row-col (get gk row-col) "_")
                            :facet-col (if col-col (get gk col-col) "_")))
                   groups)))))
      views))))

(defn facet
  "Split each view by a categorical column.
   Default layout is a horizontal row of panels.
   Pass :col as direction for a vertical column of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  ([views col] (facet views col :row))
  ([views col direction]
   (let [col (normalize-col-ref col)]
     (case direction
       :row (facet-grid views nil col)
       :col (facet-grid views col nil)))))

(defn distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  [data & cols]
  (view data (mapv (fn [c] (let [c (normalize-col-ref c)] [c c])) cols)))

;; ---- Column Type Detection ----

(defn column-type
  "Classify a dataset column as :categorical, :numerical, or :temporal."
  [ds col]
  (let [t (try (tcc/typeof (ds col)) (catch Exception _ nil))]
    (cond
      (#{:string :keyword :boolean :symbol :text} t) :categorical
      (#{:float32 :float64 :int8 :int16 :int32 :int64} t) :numerical
      ;; Check for temporal types by sampling first value
      (let [v (first (ds col))]
        (or (instance? LocalDate v)
            (instance? LocalDateTime v)
            (instance? Instant v)
            (instance? java.util.Date v))) :temporal
      (every? number? (take 100 (ds col))) :numerical
      :else :categorical)))

(defn temporal->epoch-ms
  "Convert a temporal value to epoch-milliseconds (double)."
  [v]
  (cond
    (instance? LocalDate v)
    (double (* (.toEpochDay ^LocalDate v) 86400000))
    (instance? LocalDateTime v)
    (double (.toEpochMilli (.toInstant ^LocalDateTime v ZoneOffset/UTC)))
    (instance? Instant v)
    (double (.toEpochMilli ^Instant v))
    (instance? java.util.Date v)
    (double (.getTime ^java.util.Date v))
    :else (double v)))

(defn- temporalize-column
  "Replace a temporal column in a dataset with its epoch-ms numeric equivalent."
  [ds col]
  (tc/map-columns ds col [col] temporal->epoch-ms))

(defn- temporal->local-date-time
  "Convert any supported temporal value to LocalDateTime (required by wadogo :datetime scale).
   LocalDate gets midnight, Instant and java.util.Date get UTC conversion."
  [v]
  (cond
    (instance? LocalDateTime v) v
    (instance? LocalDate v) (.atStartOfDay ^LocalDate v)
    (instance? Instant v) (LocalDateTime/ofInstant ^Instant v ZoneOffset/UTC)
    (instance? java.util.Date v) (LocalDateTime/ofInstant (.toInstant ^java.util.Date v) ZoneOffset/UTC)
    :else v))

(defn- temporal-extent
  "Return [min max] of original temporal values in a column, as LocalDateTime.
   All temporal types are normalized to LocalDateTime for wadogo :datetime scale."
  [ds col]
  (let [vals (vec (remove nil? (ds col)))]
    (when (seq vals)
      (let [ldts (mapv temporal->local-date-time vals)]
        [(reduce #(if (neg? (.compareTo ^Comparable %1 %2)) %1 %2) ldts)
         (reduce #(if (pos? (.compareTo ^Comparable %1 %2)) %1 %2) ldts)]))))

;; ---- Resolve View ----

(defn infer-column-types
  "Detect x and y column types (:categorical, :numerical, :temporal).
   Temporal columns are converted to epoch-ms numbers; their original
   extents (as LocalDateTime) are preserved for wadogo :datetime ticks.
   Returns a map with keys :ds, :x-type, :y-type, :x-temporal?, :y-temporal?,
   :x-temporal-extent, :y-temporal-extent."
  [ds v]
  (let [x-type (or (:x-type v) (column-type ds (:x v)))
        y-type (or (:y-type v) (when (and (:y v) (not= (:x v) (:y v)))
                                 (column-type ds (:y v))))
        x-temporal? (= x-type :temporal)
        y-temporal? (= y-type :temporal)
        x-temp-extent (when x-temporal? (temporal-extent ds (:x v)))
        y-temp-extent (when y-temporal? (temporal-extent ds (:y v)))
        ds (cond-> ds
             x-temporal? (temporalize-column (:x v))
             y-temporal? (temporalize-column (:y v)))]
    {:ds ds
     :x-type (if x-temporal? :numerical x-type)
     :y-type (if y-temporal? :numerical y-type)
     :x-temporal? x-temporal?
     :y-temporal? y-temporal?
     :x-temporal-extent x-temp-extent
     :y-temporal-extent y-temp-extent}))

(defn resolve-aesthetics
  "Classify each aesthetic channel (:color, :size, :alpha, :text) as either
   a column reference or a fixed literal value.
   Returns a map with keys :color, :color-is-col?, :color-type, :fixed-color,
   :size, :size-is-col?, :fixed-size, :alpha, :alpha-is-col?, :fixed-alpha,
   :text-col."
  [ds v]
  (let [color-val (let [cv (:color v)]
                    (if (and (string? cv)
                             ((set (tc/column-names ds)) (keyword cv)))
                      (keyword cv)
                      cv))
        color-is-col? (and color-val (column-ref? color-val))
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
   Explicit groups are normalized; categorical color columns are appended.
   Returns a vector of column keywords."
  [v color-type color-col]
  (let [explicit-group (let [g (normalize-col-ref (:group v))]
                         (cond (nil? g) nil
                               (keyword? g) [g]
                               (sequential? g) (mapv normalize-col-ref g)
                               :else [g]))
        color-group (when (= color-type :categorical) [color-col])
        group (vec (distinct (concat (or explicit-group color-group [])
                                     (when (and color-group explicit-group) color-group))))]
    group))

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

(defn resolve-view
  "Resolve a single view: infer column types, aesthetics, grouping, and method.
   Delegates to `infer-column-types`, `resolve-aesthetics`, `infer-grouping`,
   and `infer-method` — each named for the inference step it performs.
   Also normalizes user-facing shorthand options:
     - :bandwidth → :cfg {:kde-bandwidth ...}
     - :tile with :fill → stat :identity"
  [v]
  (if-not (:data v)
    v
    (let [ds (let [d (:data v)] (if (tc/dataset? d) d (tc/dataset d)))
          {:keys [x-type y-type x-temporal? y-temporal?
                  x-temporal-extent y-temporal-extent]
           resolved-ds :ds} (infer-column-types ds v)
          {:keys [color color-type fixed-color
                  size fixed-size alpha fixed-alpha text-col]} (resolve-aesthetics resolved-ds v)
          group (infer-grouping v color-type color)
          {:keys [mark stat]} (infer-method v x-type y-type)
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
          ;; Tile with explicit :fill → override stat to :identity
          resolved (if (and (= (:mark resolved) :tile) (:fill resolved))
                     (assoc resolved :stat :identity)
                     resolved)]
      resolved)))

;; ---- Scale Setter ----

(defn scale
  "Set scale options for :x or :y across all views."
  ([views channel type-or-opts]
   (if (map? type-or-opts)
     (scale views channel (or (:type type-or-opts) :linear) (dissoc type-or-opts :type))
     (scale views channel type-or-opts {})))
  ([views channel type opts]
   (let [k (case channel :x :x-scale :y :y-scale)]
     (mapv #(assoc % k (merge {:type type} opts)) views))))
