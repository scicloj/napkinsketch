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
  "Merge a layer into each view."
  [views overrides]
  (mapv (fn [v]
          (when (:data v)
            (validate-columns (:data v) overrides))
          (merge v overrides))
        views))

(defn lay
  "Apply one or more layers to views."
  [base-views & layer-specs]
  (let [ann-specs (filter #(and (map? %) (annotation-marks (:mark %))) layer-specs)
        data-specs (remove #(and (map? %) (annotation-marks (:mark %))) layer-specs)]
    (vec (concat (apply concat (map #(merge-layer base-views %) data-specs))
                 ann-specs))))

;; ---- Coord ----

(defn coord
  "Set coordinate system on views."
  [views c]
  (mapv #(assoc % :coord c) views))

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

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (for [x xs, y ys] [x y]))

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
  "Fill in derived properties: types, grouping, mark, stat."
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
          group (or (:group v)
                    (when (= c-type :categorical) [color-val])
                    [])
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
             :fixed-size fixed-size))))

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
