(ns scicloj.napkinsketch.impl.view
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [scicloj.napkinsketch.impl.defaults :as defaults]))

;; ---- Helpers ----

(defn column-ref?
  "True if v is a column reference (keyword)."
  [v]
  (keyword? v))

(defn parse-view-spec
  "Parse a view spec: a keyword becomes a histogram view (x=y),
  a vector becomes {:x ... :y ...}, a map passes through."
  [spec]
  (cond
    (keyword? spec) {:x spec :y spec}
    (map? spec) spec
    :else {:x (first spec) :y (second spec)}))

(defn validate-columns
  "Check that every column-referencing key in view-map names a real column in ds."
  ([ds view-map]
   (let [col-names (set (tc/column-names ds))]
     (doseq [k defaults/column-keys
             :let [col (get view-map k)]
             :when (and col (column-ref? col) (not (col-names col)))]
       (throw (ex-info (str "Column " col " (from " k ") not found in dataset. Available: " (sort col-names))
                       {:key k :column col :available (sort col-names)})))))
  ([ds role col]
   (let [col-names (set (tc/column-names ds))]
     (when-not (col-names col)
       (throw (ex-info (str "Column " col " (from " role ") not found in dataset. Available: " (sort col-names))
                       {:key role :column col :available (sort col-names)}))))))

(defn multi-spec?
  "True if specs is a sequence of view specs rather than a single spec."
  [specs]
  (and (sequential? specs)
       (let [fst (first specs)]
         (or (sequential? fst) (map? fst)))))

;; ---- View ----

(defn view
  "Create views from data and column specs."
  ([data spec-or-x]
   (let [ds (if (tc/dataset? data) data (tc/dataset data))]
     (if (multi-spec? spec-or-x)
       (mapv (fn [spec]
               (let [parsed (parse-view-spec spec)]
                 (validate-columns ds parsed)
                 (assoc parsed :data ds)))
             spec-or-x)
       (let [parsed (parse-view-spec spec-or-x)]
         (validate-columns ds parsed)
         [(assoc parsed :data ds)]))))
  ([data x y]
   (let [ds (if (tc/dataset? data) data (tc/dataset data))
         v {:x x :y y}]
     (validate-columns ds v)
     [(assoc v :data ds)])))

;; ---- Layer ----

(def annotation-marks
  #{:rule-h :rule-v :band-h :band-v})

(defn merge-layer
  "Merge a layer into each view, preserving :__base for additive lay."
  [views overrides]
  (mapv (fn [v]
          (when (:data v)
            (validate-columns (:data v) overrides))
          (let [base (or (:__base v) v)]
            (assoc (merge v overrides) :__base base)))
        views))

(defn lay
  "Apply one or more layers to views. Additive: calling lay on
   already-layered views appends new layers rather than overwriting."
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
  "Set labels on views. Keys: :title, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})"
  [views label-opts]
  (let [m (cond-> {}
            (:title label-opts) (assoc :title (:title label-opts))
            (:x label-opts) (assoc :x-label (:x label-opts))
            (:y label-opts) (assoc :y-label (:y label-opts)))]
    (mapv #(merge % m) views)))

;; ---- Mark Constructors ----

(defn point
  ([] {:mark :point})
  ([opts] (merge {:mark :point} opts)))

(defn line
  ([] {:mark :line :stat :identity})
  ([opts] (merge {:mark :line :stat :identity} opts)))

(defn histogram
  ([] {:mark :bar :stat :bin})
  ([opts] (merge {:mark :bar :stat :bin} opts)))

(defn bar
  ([] {:mark :rect :stat :count})
  ([opts] (merge {:mark :rect :stat :count} opts)))

(defn stacked-bar
  ([] {:mark :rect :stat :count :position :stack})
  ([opts] (merge {:mark :rect :stat :count :position :stack} opts)))

(defn value-bar
  ([] {:mark :rect :stat :identity})
  ([opts] (merge {:mark :rect :stat :identity} opts)))

(defn lm
  ([] {:mark :line :stat :lm})
  ([opts] (merge {:mark :line :stat :lm} opts)))

(defn loess
  ([] {:mark :line :stat :loess})
  ([opts] (merge {:mark :line :stat :loess} opts)))

(defn text
  "Text mark — data-driven labels at (x, y) positions.
   Requires :text key mapping to a column.
   (text {:text :name})                — label each point
   (text {:text :name :color :species}) — colored labels"
  ([] {:mark :text :stat :identity})
  ([opts] (merge {:mark :text :stat :identity} opts)))

(defn area
  "Area mark — filled region under a line.
   (area)                     — default
   (area {:color :species})   — one area per group"
  ([] {:mark :area :stat :identity})
  ([opts] (merge {:mark :area :stat :identity} opts)))

(defn density
  "Density mark — kernel density estimation rendered as a filled area.
   (density)                    — default bandwidth
   (density {:color :species})  — per-group density curves
   (density {:bandwidth 0.5})   — custom bandwidth"
  ([] {:mark :area :stat :kde})
  ([opts]
   (let [bw (:bandwidth opts)
         base (merge {:mark :area :stat :kde} (dissoc opts :bandwidth))]
     (if bw
       (assoc base :cfg {:kde-bandwidth bw})
       base))))

(defn boxplot
  "Boxplot mark — displays median, quartiles, whiskers, and outliers.
   x should be categorical, y numeric.
   (boxplot)                    — single color
   (boxplot {:color :smoker})   — side-by-side grouped boxplots"
  ([] {:mark :boxplot :stat :boxplot})
  ([opts] (merge {:mark :boxplot :stat :boxplot} opts)))

(defn rule-v
  "Vertical reference line at x = intercept."
  [intercept]
  {:mark :rule-v :intercept intercept})

(defn rule-h
  "Horizontal reference line at y = intercept."
  [intercept]
  {:mark :rule-h :intercept intercept})

(defn band-v
  "Vertical shaded band from x = lo to x = hi."
  [lo hi]
  {:mark :band-v :lo lo :hi hi})

(defn band-h
  "Horizontal shaded band from y = lo to y = hi."
  [lo hi]
  {:mark :band-h :lo lo :hi hi})

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (for [x xs, y ys] [x y]))

(defn pairs
  "Upper-triangle pairs of columns, for pairwise scatter plots.
   (pairs [:a :b :c]) => [[:a :b] [:a :c] [:b :c]]"
  [cols]
  (vec (for [i (range (count cols))
             j (range (inc i) (count cols))]
         [(nth cols i) (nth cols j)])))

;; ---- Faceting ----

(defn facet-grid
  "Split each view by two categorical columns for a row × column grid.
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
          (let [group-cols (filterv some? [row-col col-col])
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

(defn distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  [data & cols]
  (view data (mapv (fn [c] [c c]) cols)))

;; ---- Column Type Detection ----

(defn column-type
  "Classify a dataset column as :categorical or :numerical."
  [ds col]
  (let [t (try (tcc/typeof (ds col)) (catch Exception _ nil))]
    (cond
      (#{:string :keyword :boolean :symbol :text} t) :categorical
      (#{:float32 :float64 :int8 :int16 :int32 :int64} t) :numerical
      (every? number? (take 100 (ds col))) :numerical
      :else :categorical)))

;; ---- Resolve View ----

(defn resolve-view
  "Resolve a single view: infer column types, mark, stat, grouping."
  [v]
  (if-not (:data v)
    v
    (let [ds (:data v)
          x-type (or (:x-type v) (column-type ds (:x v)))
          y-type (or (:y-type v) (when (and (:y v) (not= (:x v) (:y v)))
                                   (column-type ds (:y v))))
          color-val (:color v)
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
          text-col (when (and text-val (column-ref? text-val)) text-val)
          ;; Group: normalize keyword to [kw], combine with color column
          explicit-group (let [g (:group v)]
                           (cond (nil? g) nil
                                 (keyword? g) [g]
                                 (sequential? g) (vec g)
                                 :else [g]))
          color-group (when (= c-type :categorical) [color-val])
          group (vec (distinct (concat (or explicit-group color-group [])
                                       (when (and color-group explicit-group) color-group))))
          diagonal? (= (:x v) (:y v))
          [default-mark default-stat]
          (cond
            (or diagonal? (nil? (:y v)))
            (if (= x-type :categorical) [:rect :count] [:bar :bin])
            (not= x-type y-type) [:point :identity]
            :else [:point :identity])
          mark (or (:mark v) default-mark)
          stat (or (:stat v) default-stat)]
      (assoc v :x-type x-type :y-type y-type :color-type c-type
             :group group :mark mark :stat stat
             :color (when color-is-col? color-val)
             :fixed-color fixed-color
             :size (when size-is-col? size-val)
             :fixed-size fixed-size
             :alpha (when alpha-is-col? alpha-val)
             :fixed-alpha fixed-alpha
             :text-col text-col))))

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
