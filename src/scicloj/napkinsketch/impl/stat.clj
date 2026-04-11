(ns scicloj.napkinsketch.impl.stat
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.argops :as argops]
            [tech.v3.tensor :as tensor]
            [fastmath.ml.regression :as regr]
            [fastmath.stats :as stats]
            [fastmath.interpolation.acm :as interp]
            [fastmath.kernel :as kernel]
            [fastmath.random :as frand]
            [scicloj.napkinsketch.impl.defaults :as defaults]))

;; ---- Helpers ----

(defn numeric-extent
  "Min/max pair from a numeric column."
  [col]
  [(dfn/reduce-min col) (dfn/reduce-max col)])

(defn- near-constant?
  "True if the span of the column is numerically negligible. Used to detect
   degenerate inputs (single point, or near-identical x values like
   [1.0000001 1.0000002]) that would otherwise make OLS try to invert a
   singular matrix or LOESS divide by a tiny step.
   Empirically, fastmath OLS becomes singular when span/scale < ~1e-4
   in double precision, so we use that as the cutoff."
  [col]
  (let [lo (double (dfn/reduce-min col))
        hi (double (dfn/reduce-max col))
        span (- hi lo)
        scale (max (Math/abs lo) (Math/abs hi) 1.0)]
    (<= span (* 1e-4 scale))))

(defn group-by-columns
  "Split dataset by grouping columns, apply f to each group.
   Iterates groups in the order they first appear in the dataset, so
   results are deterministic regardless of `tc/group-by`'s underlying
   map type (which is a PersistentHashMap, scrambled by murmur3 hash
   for >8 groups). This also preserves user-visible stacking and
   draw order."
  [ds group-cols f]
  (if (seq group-cols)
    (let [grouped (tc/group-by ds group-cols {:result-type :as-map})
          ;; Determine canonical order: zip the group columns and walk
          ;; the dataset rows in order, taking the first occurrence of
          ;; each composite key.
          canonical-keys
          (let [n (tc/row-count ds)
                cols (mapv #(get ds %) group-cols)]
            (loop [i 0, seen #{}, out (transient [])]
              (if (>= i n)
                (persistent! out)
                (let [k (zipmap group-cols (mapv #(nth % i) cols))]
                  (if (contains? seen k)
                    (recur (inc i) seen out)
                    (recur (inc i) (conj seen k) (conj! out k)))))))]
      (for [gk canonical-keys
            :let [gds (get grouped gk)]
            :when gds]
        (f gds (if (= 1 (count group-cols))
                 (get gk (first group-cols))
                 (mapv gk group-cols)))))
    [(f ds nil)]))

;; ---- Prepare Points ----

(defn- validate-numeric-column
  "Throw a clear error if the column referenced by `col-key` in `view` is categorical
   but the stat requires numeric data."
  [view col-key stat-name]
  (let [type-key (keyword (str (name col-key) "-type"))
        col-type (get view type-key)]
    (when (= col-type :categorical)
      (throw (ex-info (str "Stat :" (name stat-name) " requires a numeric column for :" (name col-key)
                           ", but :" (name (get view col-key)) " is categorical.")
                      {:stat stat-name :column (get view col-key) :column-type col-type})))))

(defn prepare-points
  "Clean data, compute domains, group by columns.
   Drops rows with missing values in x/y AND in any referenced numeric
   aesthetic column (color/size/alpha/ymin/ymax/fill) so downstream code
   never sees nil/NaN values where it tries to coerce to double."
  [view]
  (let [{:keys [data x y color color-type size alpha shape text-col x-type y-type group mark ymin ymax fill]} view
        x-only? (or (nil? y) (= x y))
        data-idx (tc/add-column data :__row-idx (range (tc/row-count data)))
        ds-cols (set (tc/column-names data-idx))
        col-ref? (fn [v] (and v (or (keyword? v) (string? v)) (contains? ds-cols v)))
        ;; Referenced numeric aesthetic columns that exist in the dataset
        aesthetic-cols (cond-> []
                         (col-ref? color) (conj color)
                         (col-ref? size)  (conj size)
                         (col-ref? alpha) (conj alpha)
                         (col-ref? ymin)  (conj ymin)
                         (col-ref? ymax)  (conj ymax)
                         (col-ref? fill)  (conj fill))
        drop-cols (vec (distinct (concat (if x-only? [x] [x y]) aesthetic-cols)))
        clean (cond-> (tc/drop-missing data-idx drop-cols)
                (= x-type :categorical) (tc/map-columns x [x] defaults/fmt-category-label))]
    (if (zero? (tc/row-count clean))
      {:points [] :x-domain [0 1] :y-domain [0 1]}
      (let [xs-col (clean x)
            ys-col (if x-only? nil (clean y))
            cat-x? (= x-type :categorical)
            cat-y? (= y-type :categorical)
            x-dom (if cat-x? (distinct xs-col) (numeric-extent xs-col))
            y-dom (cond
                    x-only? nil
                    cat-y? (distinct ys-col)
                    :else (let [[lo hi] (numeric-extent ys-col)]
                            (if (= mark :rect) [(min 0 lo) (max 0 hi)]
                                ;; Extend domain to include ymin/ymax if present
                                (if (and ymin ymax)
                                  [(min lo (dfn/reduce-min (clean ymin)))
                                   (max hi (dfn/reduce-max (clean ymax)))]
                                  [lo hi]))))
            numeric-color? (and color (= color-type :numerical))
            ;; Extract color value from group key when color is part of group
            color-idx (when color (.indexOf ^java.util.List group color))
            extract-color (fn [group-val]
                            (cond
                              (nil? group-val) nil
                              ;; Single group col — group-val is the value itself
                              (= 1 (count group)) group-val
                              ;; Multiple group cols — extract color column value
                              (and color-idx (>= color-idx 0)) (nth group-val color-idx)
                              :else nil))
            zero-ys (fn [ds] (dtype/const-reader 0.0 (tc/row-count ds)))
            point-group (fn [ds group-val]
                          (cond-> {:xs (ds x) :ys (if x-only? (zero-ys ds) (ds y))
                                   :row-indices (ds :__row-idx)}
                            (some? group-val) (assoc :color (extract-color group-val))
                            numeric-color? (assoc :color-values (ds color))
                            size (assoc :sizes (ds size))
                            alpha (assoc :alphas (ds alpha))
                            shape (assoc :shapes (ds shape))
                            text-col (assoc :labels (ds text-col))
                            ymin (assoc :ymins (ds ymin))
                            ymax (assoc :ymaxs (ds ymax))))
            groups (group-by-columns clean (or group []) point-group)]
        {:points groups :x-domain x-dom :y-domain y-dom}))))

;; ---- compute-stat multimethod ----

(defmulti compute-stat
  "Compute a statistical transform for a view."
  (fn [view] (or (:stat view) :identity)))

(defmethod compute-stat :default [view]
  (let [stat-key (or (:stat view) :identity)
        registered (sort (filter keyword?
                                 (remove #(or (vector? %) (= :default %))
                                         (keys (methods compute-stat)))))]
    (throw (ex-info (str "Unknown stat: " (pr-str stat-key)
                         ". Supported stats: " (vec registered))
                    {:stat stat-key :supported (vec registered)}))))

;; ---- Doc methods (dispatching on [stat-key :doc]) ----

(defmethod compute-stat [:identity :doc] [_] "Pass-through — no transform")
(defmethod compute-stat [:bin :doc] [_] "Bin numerical values into ranges")
(defmethod compute-stat [:count :doc] [_] "Count occurrences per category")
(defmethod compute-stat [:lm :doc] [_] "Linear model (lm) — OLS regression line + optional confidence band")
(defmethod compute-stat [:loess :doc] [_] "LOESS (local regression) smoothing")
(defmethod compute-stat [:kde :doc] [_] "KDE (kernel density estimation) — 1D")
(defmethod compute-stat [:boxplot :doc] [_] "Five-number summary + outliers")
(defmethod compute-stat [:violin :doc] [_] "KDE per category (density profile)")
(defmethod compute-stat [:summary :doc] [_] "Mean ± standard error per category")
(defmethod compute-stat [:bin2d :doc] [_] "2D grid binning (heatmap counts)")
(defmethod compute-stat [:kde2d :doc] [_] "2D Gaussian KDE (kernel density estimation)")

(defmethod compute-stat :identity [{:keys [mark x y x-type] :as view}]
  (when (and (#{:lollipop :errorbar :pointrange} mark)
             (or (nil? y) (= x y))
             (= x-type :categorical))
    (throw (ex-info (str "Mark :" (name mark) " requires both :x and :y columns with numeric :y.")
                    {:mark mark :x x :y y})))
  (prepare-points view))

;; ---- Binning ----

(defmethod compute-stat :bin [{:keys [data x x-type group cfg normalize] :as view}]
  (validate-numeric-column view :x :bin)
  (when-let [b (:bins view)]
    (when-not (and (number? b) (pos? b))
      (throw (ex-info (str ":bins must be a positive number, got: " (pr-str b))
                      {:bins b}))))
  (when-let [bw (:binwidth view)]
    (when-not (and (number? bw) (pos? bw))
      (throw (ex-info (str ":binwidth must be a positive number, got: " (pr-str bw))
                      {:binwidth bw}))))
  (let [clean (cond-> (tc/drop-missing data [x])
                (= x-type :categorical) (tc/map-columns x [x] defaults/fmt-category-label))
        xs-col (clean x)]
    (if (zero? (tc/row-count clean))
      {:bins [] :max-count 0 :x-domain [0 1] :y-domain [0 1]}
      (let [;; Determine bin method: explicit :bins count > :binwidth > cfg heuristic
            bin-arg (or (:bins view)
                        (when-let [bw (:binwidth view)]
                          (let [[lo hi] (numeric-extent xs-col)
                                span (- (double hi) (double lo))]
                            (if (pos? span)
                              (max 1 (long (Math/ceil (/ span (double bw)))))
                              1)))
                        (:bin-method (or cfg defaults/defaults)))
            all-bin-data (group-by-columns
                          clean (or group [])
                          (fn [ds gv]
                            (let [hist (stats/histogram (ds x) bin-arg)]
                              (cond-> {:bin-maps (:bins-maps hist)}
                                (some? gv) (assoc :color gv)))))
            ;; When normalize=:density, convert counts to density (area integrates to 1)
            all-bin-data (if (= normalize :density)
                           (mapv (fn [bd]
                                   (let [n (reduce + 0 (map :count (:bin-maps bd)))
                                         bin-maps' (mapv (fn [b]
                                                           (let [bw (- (double (:max b)) (double (:min b)))
                                                                 density (if (and (pos? n) (pos? bw))
                                                                           (/ (double (:count b)) (* n bw))
                                                                           0.0)]
                                                             (assoc b :count density)))
                                                         (:bin-maps bd))]
                                     (assoc bd :bin-maps bin-maps')))
                                 all-bin-data)
                           all-bin-data)
            ;; Floor at 1 for raw counts; floor at 0 for density (values are typically < 1)
            floor (if (= normalize :density) 0 1)
            max-count (reduce max floor (for [{:keys [bin-maps]} all-bin-data
                                              b bin-maps]
                                          (:count b)))]
        {:bins all-bin-data
         :max-count max-count
         :x-domain (numeric-extent xs-col)
         :y-domain [0 max-count]}))))

;; ---- Counting ----

(defmethod compute-stat :count [view]
  (when-not (= (:x-type view) :categorical)
    (throw (ex-info (str "Stat :count (used by lay-bar) requires a categorical column for :x, "
                         "but " (:x view) " is " (name (or (:x-type view) :unknown))
                         ". Use lay-histogram for numeric data, or convert " (:x view) " to a string column.")
                    {:stat :count :x (:x view) :x-type (:x-type view)})))
  (let [{:keys [data x x-type group]} view
        group-cols (or group [])
        clean (cond-> (tc/drop-missing data [x])
                (= x-type :categorical) (tc/map-columns x [x] defaults/fmt-category-label))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:categories [] :bars [] :max-count 0 :x-domain ["?"] :y-domain [0 1]}
      ;; Use tc/group-by for efficient counting
      (let [color-col (first group-cols)
            has-color? (seq group-cols)
            clean-c (if has-color? (tc/drop-missing clean group-cols) clean)
            color-cats (when has-color? (sort (distinct (clean-c color-col))))
            ;; Single tc/group-by handles both colored and uncolored cases
            grouped (tc/group-by clean-c
                                 (if has-color? [x color-col] [x])
                                 {:result-type :as-map})]
        (if has-color?
          (let [count-fn (fn [cat cc]
                           ;; When color column == x column, cat must equal cc
                           ;; (otherwise the combination is impossible)
                           (if (and (= x color-col) (not= cat cc))
                             0
                             (if-let [ds (get grouped (zipmap [x color-col] [cat cc]))]
                               (tc/row-count ds) 0)))
                max-count (reduce max 1 (for [cat categories, cc color-cats]
                                          (count-fn cat cc)))]
            {:categories categories
             :bars (vec (for [cc color-cats]
                          {:color cc
                           :counts (mapv (fn [cat] {:category cat :count (count-fn cat cc)})
                                         categories)}))
             :max-count max-count
             :x-domain categories
             :y-domain [0 max-count]})
          (let [counts-by-cat (mapv (fn [cat]
                                      {:category cat
                                       :count (if-let [ds (get grouped {x cat})]
                                                (tc/row-count ds) 0)})
                                    categories)
                max-count (reduce max 1 (map :count counts-by-cat))]
            {:categories categories
             :bars [{:counts counts-by-cat}]
             :max-count max-count
             :x-domain categories
             :y-domain [0 max-count]}))))))

;; ---- Linear Regression ----

(defn fit-lm
  "Fit a linear model, return {:x1 :y1 :x2 :y2}."
  [xs-col ys-col]
  (let [model (regr/lm ys-col xs-col)
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)]
    {:x1 x-min :y1 (regr/predict model [x-min])
     :x2 x-max :y2 (regr/predict model [x-max])}))

(defn fit-lm-with-se
  "Fit a linear model and compute confidence band on a grid of x-values.
   Returns {:xs [...] :ys [...] :ymins [...] :ymaxs [...] :x1 :y1 :x2 :y2}."
  [xs-col ys-col n-grid level]
  (let [model (regr/lm ys-col xs-col)
        sigma (double (:sigma model))
        xtxinv (:xtxinv model)
        df-resid (long (get-in model [:df :residual]))
        ;; t critical value for confidence level
        t-dist (frand/distribution :t {:degrees-of-freedom df-resid})
        t-val (frand/icdf t-dist (+ 0.5 (/ (double level) 2.0)))
        ;; Matrix entries for se computation: [1, x] * xtxinv * [1, x]^T
        a00 (.getEntry xtxinv 0 0)
        a01 (.getEntry xtxinv 0 1)
        a11 (.getEntry xtxinv 1 1)
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (if (<= n-grid 1) 0.0 (/ (- x-max x-min) (dec (double n-grid))))
        grid-xs (dfn/+ x-min (dfn/* step (range n-grid)))
        grid-ys (dtype/emap #(regr/predict model [%]) :float64 grid-xs)
        h (dfn/+ a00 (dfn/* 2.0 a01 grid-xs) (dfn/* a11 grid-xs grid-xs))
        se (dfn/* sigma (dfn/sqrt h))
        grid-ymins (dfn/- grid-ys (dfn/* t-val se))
        grid-ymaxs (dfn/+ grid-ys (dfn/* t-val se))]
    {:xs grid-xs :ys grid-ys
     :ymins grid-ymins :ymaxs grid-ymaxs
     :x1 x-min :y1 (regr/predict model [x-min])
     :x2 x-max :y2 (regr/predict model [x-max])}))

(defmethod compute-stat :lm [{:keys [data x y group cfg] :as view}]
  (validate-numeric-column view :x :lm)
  (validate-numeric-column view :y :lm)
  (let [se (:se view)
        level (or (:level view) 0.95)
        n-grid (or (:se-n-grid (or cfg defaults/defaults)) 80)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)]
    (if (or (< n 3)
            (near-constant? (clean x)))
      (cond-> {:lines []
               :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
               :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
        se (assoc :ribbons []))
      (if se
        ;; With confidence band
        (let [results (group-by-columns
                       clean (or group [])
                       (fn [ds gv]
                         (when (and (>= (tc/row-count ds) 3)
                                    (not (near-constant? (ds x))))
                           (cond-> (fit-lm-with-se (ds x) (ds y) n-grid level)
                             (some? gv) (assoc :color gv)))))
              results (remove nil? results)
              lines (mapv (fn [{:keys [x1 y1 x2 y2 color]}]
                            (cond-> {:x1 x1 :y1 y1 :x2 x2 :y2 y2}
                              (some? color) (assoc :color color)))
                          results)
              ribbons (mapv (fn [{:keys [xs ys ymins ymaxs color]}]
                              (cond-> {:xs xs :ys ys :ymins ymins :ymaxs ymaxs}
                                (some? color) (assoc :color color)))
                            results)
              ymin-bufs (seq (map :ymins results))
              ymax-bufs (seq (map :ymaxs results))
              y-ext (numeric-extent (clean y))
              y-lo (min (first y-ext) (if ymin-bufs (dfn/reduce-min (dtype/concat-buffers ymin-bufs)) (first y-ext)))
              y-hi (max (second y-ext) (if ymax-bufs (dfn/reduce-max (dtype/concat-buffers ymax-bufs)) (second y-ext)))]
          {:lines lines
           :ribbons ribbons
           :x-domain (numeric-extent (clean x))
           :y-domain [y-lo y-hi]})
        ;; Without confidence band (original behavior)
        (let [lines (group-by-columns
                     clean (or group [])
                     (fn [ds gv]
                       (when (and (>= (tc/row-count ds) 3)
                                  (not (near-constant? (ds x))))
                         (cond-> (fit-lm (ds x) (ds y))
                           (some? gv) (assoc :color gv)))))]
          {:lines (vec (remove nil? lines))
           :x-domain (numeric-extent (clean x))
           :y-domain (numeric-extent (clean y))})))))

;; ---- LOESS Smoothing ----

(defn- dedup-xy
  "Average y-values for duplicate x-values in sorted arrays.
   Returns [unique-xs averaged-ys] as double arrays.
   Inputs must be sorted double arrays of the same length."
  [^doubles xs ^doubles ys]
  (let [n (alength xs)]
    (if (zero? n)
      [(double-array 0) (double-array 0)]
      (let [;; worst case: all unique → n entries
            out-x (double-array n)
            out-y (double-array n)]
        (loop [i 1
               run-start 0
               y-sum (aget xs 0) ;; will be overwritten below
               out-idx 0]
          (let [cur-x (aget xs run-start)
                y-sum (if (== i 1) (aget ys 0) y-sum)]
            (if (< i n)
              (let [xi (aget xs i)]
                (if (== xi cur-x)
                  (recur (inc i) run-start (+ y-sum (aget ys i)) out-idx)
                  (do
                    (aset out-x out-idx cur-x)
                    (aset out-y out-idx (/ y-sum (double (- i run-start))))
                    (recur (inc i) i (aget ys i) (inc out-idx)))))
              ;; flush last run
              (let [cnt (- i run-start)]
                (aset out-x out-idx cur-x)
                (aset out-y out-idx (/ y-sum (double cnt)))
                (let [total (inc out-idx)]
                  [(java.util.Arrays/copyOf out-x total)
                   (java.util.Arrays/copyOf out-y total)])))))))))

(defn fit-loess
  "Fit a LOESS curve, return {:xs ... :ys ...} evaluated on a grid."
  [xs-col ys-col n-grid bandwidth]
  (let [order (dtype/->int-array (argops/argsort xs-col))
        sorted-xs (dtype/->double-array (dtype/indexed-buffer order xs-col))
        sorted-ys (dtype/->double-array (dtype/indexed-buffer order ys-col))
        [uxs uys] (dedup-xy sorted-xs sorted-ys)
        ;; iters 4 matches R's loess default; Apache Commons' default of 2
        ;; is less robust against outliers.
        loess-fn (interp/loess uxs uys {:bandwidth bandwidth :iters 4})
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (if (<= n-grid 1) 0.0 (/ (- x-max x-min) (dec (double n-grid))))
        grid-xs (dfn/+ x-min (dfn/* step (range n-grid)))
        grid-ys (dtype/emap loess-fn :float64 grid-xs)]
    {:xs grid-xs :ys grid-ys}))

(defn fit-loess-with-se
  "Fit LOESS with bootstrap confidence band. Returns
   {:xs :ys :ymins :ymaxs} evaluated on a grid."
  [xs-col ys-col n-grid bandwidth level n-boot]
  (let [;; Original fit (with dedup for duplicate x-values)
        order (dtype/->int-array (argops/argsort xs-col))
        sorted-xs (dtype/->double-array (dtype/indexed-buffer order xs-col))
        sorted-ys (dtype/->double-array (dtype/indexed-buffer order ys-col))
        [uxs uys] (dedup-xy sorted-xs sorted-ys)
        ;; iters 4 matches R's loess default; Apache Commons' default of 2
        ;; is less robust against outliers.
        loess-fn (interp/loess uxs uys {:bandwidth bandwidth :iters 4})
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (if (<= n-grid 1) 0.0 (/ (- x-max x-min) (dec (double n-grid))))
        grid-xs (dfn/+ x-min (dfn/* step (range n-grid)))
        grid-ys (dtype/emap loess-fn :float64 grid-xs)

        ;; Bootstrap: resample via tensor, fit LOESS, evaluate on grid
        t (tensor/->tensor [xs-col ys-col] :datatype :float64)
        n (long (second (dtype/shape t)))
        rng-inst (frand/rng :jdk 42)
        boot-grid-ys (for [_ (range n-boot)]
                       (let [indices (int-array (repeatedly n #(frand/irandom rng-inst (int n))))
                             sampled (tensor/select t :all indices)
                             order (argops/argsort (tensor/select sampled 0 :all))
                             sorted (tensor/select sampled :all order)
                             sorted-xs (dtype/->double-array (tensor/select sorted 0 :all))
                             sorted-ys (dtype/->double-array (tensor/select sorted 1 :all))
                             [bsx bsy] (dedup-xy sorted-xs sorted-ys)]
                         (when (>= (alength bsx) 4)
                           (try
                             (let [bfn (interp/loess bsx bsy {:bandwidth bandwidth :iters 4})]
                               (dtype/emap bfn :float64 grid-xs))
                             (catch Exception _ nil)))))
        valid-boots (vec (remove nil? boot-grid-ys))
        n-valid (count valid-boots)]
    (if (zero? n-valid)
      ;; No valid bootstrap samples — fall back to the point estimate curve
      ;; with zero-width ribbons so downstream code has shape stability.
      {:xs grid-xs :ys grid-ys
       :ymins (vec grid-ys) :ymaxs (vec grid-ys)}
      (let [alpha (- 1.0 (double level))
            lo-idx (max 0 (long (* (/ alpha 2.0) n-valid)))
            hi-idx (min (dec n-valid)
                        (long (* (- 1.0 (/ alpha 2.0)) n-valid)))
            ymins (mapv (fn [i]
                          (let [vals (sort (map #(% i) valid-boots))]
                            (nth vals lo-idx)))
                        (range n-grid))
            ymaxs (mapv (fn [i]
                          (let [vals (sort (map #(% i) valid-boots))]
                            (nth vals hi-idx)))
                        (range n-grid))]
        {:xs grid-xs :ys grid-ys :ymins ymins :ymaxs ymaxs}))))

(defmethod compute-stat :loess [{:keys [data x y group cfg] :as view}]
  (validate-numeric-column view :x :loess)
  (validate-numeric-column view :y :loess)
  (let [se (:se view)
        level (or (:level view) 0.95)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)
        ;; Adaptive bootstrap count: LOESS fits are O(n^1.7), so running
        ;; 200 bootstraps on 100k rows is prohibitive. Scale the sample
        ;; count down for large inputs while preserving good CI coverage
        ;; on small/medium datasets. Clamp to at least 20 so the
        ;; quantile estimates remain usable. Users can still override
        ;; via an explicit `:se-boot N` on the view.
        n-boot (or (:se-boot view)
                   (cond
                     (<= n 500)    200
                     (<= n 2000)   100
                     (<= n 10000)  50
                     :else         30))
        n-grid (or (:loess-n-grid (or cfg defaults/defaults)) 80)
        bandwidth (or (:loess-bandwidth (or cfg defaults/defaults)) 0.75)]
    (if (or (< n 4)
            (near-constant? (clean x)))
      (cond-> {:points []
               :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
               :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
        se (assoc :ribbons []))
      (if se
        ;; With confidence band (bootstrap)
        (let [results (group-by-columns
                       clean (or group [])
                       (fn [ds gv]
                         (when (and (>= (tc/row-count ds) 4)
                                    (not (near-constant? (ds x))))
                           (cond-> (fit-loess-with-se (ds x) (ds y)
                                                      n-grid bandwidth level n-boot)
                             (some? gv) (assoc :color gv)))))
              results (remove nil? results)
              curves (mapv (fn [{:keys [xs ys color]}]
                             (cond-> {:xs xs :ys ys}
                               (some? color) (assoc :color color)))
                           results)
              ribbons (mapv (fn [{:keys [xs ys ymins ymaxs color]}]
                              (cond-> {:xs xs :ys ys :ymins ymins :ymaxs ymaxs}
                                (some? color) (assoc :color color)))
                            results)
              ymin-bufs (seq (map :ymins results))
              ymax-bufs (seq (map :ymaxs results))
              y-ext (numeric-extent (clean y))
              y-lo (min (first y-ext) (if ymin-bufs (dfn/reduce-min (dtype/concat-buffers ymin-bufs)) (first y-ext)))
              y-hi (max (second y-ext) (if ymax-bufs (dfn/reduce-max (dtype/concat-buffers ymax-bufs)) (second y-ext)))]
          {:points curves
           :ribbons ribbons
           :x-domain (numeric-extent (clean x))
           :y-domain [y-lo y-hi]})
        ;; Without confidence band (original behavior)
        (let [curves (group-by-columns
                      clean (or group [])
                      (fn [ds gv]
                        (when (and (>= (tc/row-count ds) 4)
                                   (not (near-constant? (ds x))))
                          (cond-> (fit-loess (ds x) (ds y) n-grid bandwidth)
                            (some? gv) (assoc :color gv)))))
              curves (remove nil? curves)
              ys-bufs (seq (map :ys curves))
              y-min (if ys-bufs (dfn/reduce-min (dtype/concat-buffers ys-bufs)) (dfn/reduce-min (clean y)))
              y-max (if ys-bufs (dfn/reduce-max (dtype/concat-buffers ys-bufs)) (dfn/reduce-max (clean y)))]
          {:points curves
           :x-domain (numeric-extent (clean x))
           :y-domain [y-min y-max]})))))

;; ---- KDE (kernel density estimation) ----

(defn- fit-kde
  "Compute KDE for a numeric column. Returns {:xs [...] :ys [...]}."
  [col n-grid bandwidth]
  (let [xs (double-array col)
        kd (if bandwidth
             (kernel/kernel-density :gaussian xs bandwidth)
             (kernel/kernel-density :gaussian xs))
        x-min (dfn/reduce-min col)
        x-max (dfn/reduce-max col)
        range-w (- (double x-max) (double x-min))
        lo (- (double x-min) (* 0.5 range-w))
        hi (+ (double x-max) (* 0.5 range-w))
        step (if (<= n-grid 1) 0.0 (/ (- hi lo) (double (dec n-grid))))
        grid-xs (dfn/+ lo (dfn/* step (range n-grid)))
        grid-ys (dtype/emap kd :float64 grid-xs)]
    {:xs grid-xs :ys grid-ys}))

(defmethod compute-stat :kde [view]
  (validate-numeric-column view :x :kde)
  (let [{:keys [data x group cfg]} view
        clean (tc/drop-missing data [x])
        n (tc/row-count clean)
        n-grid (or (:kde-n-grid (or cfg defaults/defaults)) 100)
        bandwidth (:kde-bandwidth (or cfg defaults/defaults))]
    (if (or (< n 2)
            (near-constant? (clean x)))
      {:points []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain [0 1]}
      (let [curves (group-by-columns
                    clean (or group [])
                    (fn [ds gv]
                      (when (>= (tc/row-count ds) 2)
                        (cond-> (fit-kde (ds x) n-grid bandwidth)
                          (some? gv) (assoc :color gv)))))
            curves (remove nil? curves)
            ys-bufs (seq (map :ys curves))
            y-max (if ys-bufs (dfn/reduce-max (dtype/concat-buffers ys-bufs)) 1)
            xs-bufs (seq (map :xs curves))
            x-lo (dfn/reduce-min (dtype/concat-buffers xs-bufs))
            x-hi (dfn/reduce-max (dtype/concat-buffers xs-bufs))]
        {:points curves
         :x-domain [x-lo x-hi]
         :y-domain [0 y-max]}))))

;; ---- Boxplot ----

(defn- per-category-stat
  "Shared iteration for boxplot/violin: clean data, group by category and
   optional color, apply per-group-fn to each subset's y-column.
   Uses tc/group-by for efficient O(n) grouping instead of per-category filtering.
   per-group-fn: (fn [y-col category color-or-nil]) -> map
   min-n: minimum row count to include a group."
  [view min-n per-group-fn]
  (let [{:keys [data x y x-type group]} view
        clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] defaults/fmt-category-label))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:items [] :categories [] :color-categories nil
       :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            color-col (first group-cols)
            has-color? (and (seq group-cols) color-col)
            clean-c (if has-color? (tc/drop-missing clean group-cols) clean)
            color-cats (when has-color? (vec (sort (distinct (clean-c color-col)))))
            ;; Single tc/group-by replaces O(cats × colors) select-rows calls
            group-keys (if has-color? [x color-col] [x])
            grouped (tc/group-by clean-c group-keys {:result-type :as-map})
            ;; When x == color-col, each category IS its own color group
            ;; (no cross-product — a "setosa" category can only have "setosa" color)
            x-is-color? (and has-color? (= x color-col))
            items (vec
                   (cond
                     x-is-color?
                     (for [cat categories
                           :let [gk {x cat}
                                 ds (get grouped gk)]
                           :when (and ds (>= (tc/row-count ds) min-n))]
                       (per-group-fn (ds y) cat cat))

                     has-color?
                     (for [cat categories
                           cc color-cats
                           :let [gk (zipmap group-keys [cat cc])
                                 ds (get grouped gk)]
                           :when (and ds (>= (tc/row-count ds) min-n))]
                       (per-group-fn (ds y) cat cc))

                     :else
                     (for [cat categories
                           :let [gk {x cat}
                                 ds (get grouped gk)]
                           :when (and ds (>= (tc/row-count ds) min-n))]
                       (per-group-fn (ds y) cat nil))))
            all-ys (clean y)
            y-min (dfn/reduce-min all-ys)
            y-max (dfn/reduce-max all-ys)]
        {:items items
         :categories categories
         :color-categories color-cats
         :x-domain categories
         :y-domain [y-min y-max]}))))

(defn- quantile-r7
  "R type 7 quantile (ggplot2/R default). Uses linear interpolation:
   h = (n-1)*p + 1, Q = x[floor(h)] + (h - floor(h)) * (x[ceil(h)] - x[floor(h)]).
   This matches R's quantile(x, p, type=7) and ggplot2's boxplot quartiles."
  [sorted-arr p]
  (let [n (alength sorted-arr)
        h (+ (* (dec n) (double p)) 1.0)
        lo-idx (max 0 (min (dec n) (dec (long (Math/floor h)))))
        hi-idx (max 0 (min (dec n) (dec (long (Math/ceil h)))))
        frac (- h (Math/floor h))]
    (+ (aget sorted-arr lo-idx)
       (* frac (- (aget sorted-arr hi-idx) (aget sorted-arr lo-idx))))))

(defn- five-number-summary
  "Compute boxplot five-number summary for a numeric column.
   Uses R type 7 quantiles (ggplot2/R default) for Q1, median, Q3.
   Returns {:median :q1 :q3 :whisker-lo :whisker-hi :outliers}."
  [col]
  (let [sorted (double-array (sort col))
        q1 (quantile-r7 sorted 0.25)
        median (quantile-r7 sorted 0.5)
        q3 (quantile-r7 sorted 0.75)
        iqr (- (double q3) (double q1))
        fence-lo (- (double q1) (* 1.5 iqr))
        fence-hi (+ (double q3) (* 1.5 iqr))
        ;; Whiskers: closest data values within fences
        ;; Outliers: data values outside fences
        whisker-lo (atom (double q1))
        whisker-hi (atom (double q3))
        outliers (java.util.ArrayList.)]
    (dotimes [i (count col)]
      (let [v (double (col i))]
        (cond
          (< v fence-lo) (.add outliers v)
          (> v fence-hi) (.add outliers v)
          :else (do (when (< v @whisker-lo) (reset! whisker-lo v))
                    (when (> v @whisker-hi) (reset! whisker-hi v))))))
    {:median median :q1 q1 :q3 q3
     :whisker-lo @whisker-lo :whisker-hi @whisker-hi
     :outliers (vec outliers)}))

(defmethod compute-stat :boxplot [view]
  (let [result (per-category-stat view 1
                                  (fn [y-col cat cc]
                                    (cond-> (merge (five-number-summary y-col)
                                                   {:category cat})
                                      cc (assoc :color cc))))]
    {:boxes (:items result)
     :categories (:categories result)
     :color-categories (:color-categories result)
     :x-domain (:x-domain result)
     :y-domain (:y-domain result)}))

;; ---- Violin ----

(defmethod compute-stat :violin [view]
  (validate-numeric-column view :y :violin)
  (let [{:keys [cfg]} view
        n-grid (or (:kde-n-grid (or cfg defaults/defaults)) 80)
        bandwidth (:kde-bandwidth (or cfg defaults/defaults))
        result (per-category-stat view 2
                                  (fn [y-col cat cc]
                                    (let [kde (fit-kde y-col n-grid bandwidth)]
                                      (cond-> {:category cat
                                               :ys (:xs kde) :densities (:ys kde)}
                                        cc (assoc :color cc)))))
        ;; Expand y-domain to cover full KDE curve extent (tails beyond raw data)
        kde-ys-bufs (seq (map :ys (:items result)))
        y-domain (if kde-ys-bufs
                   (let [all-kde-ys (dtype/concat-buffers kde-ys-bufs)]
                     [(dfn/reduce-min all-kde-ys) (dfn/reduce-max all-kde-ys)])
                   (:y-domain result))]
    {:violins (:items result)
     :categories (:categories result)
     :color-categories (:color-categories result)
     :x-domain (:x-domain result)
     :y-domain y-domain}))

;; ---- 2D Binning (for heatmap/tile) ----

;; ---- Summary (mean ± SE per category) ----

(defmethod compute-stat :summary [{:keys [data x y x-type group] :as view}]
  (validate-numeric-column view :y :summary)
  (let [clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] defaults/fmt-category-label))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:points [] :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            ;; Use tc/group-by for O(n) grouping instead of per-category select-rows
            all-groups (group-by-columns
                        clean group-cols
                        (fn [ds gv]
                          (let [cat-grouped (tc/group-by ds [x] {:result-type :as-map})
                                per-cat (for [cat categories
                                              :let [cat-ds (get cat-grouped {x cat})]
                                              :when (and cat-ds (pos? (tc/row-count cat-ds)))
                                              :let [ys-col (cat-ds y)
                                                    n (tc/row-count cat-ds)
                                                    mean-val (stats/mean ys-col)
                                                    se (if (>= n 2)
                                                         (/ (stats/stddev ys-col) (Math/sqrt (double n)))
                                                         0.0)]]
                                          {:cat cat :mean mean-val :se se})]
                            (cond-> {:xs (mapv :cat per-cat)
                                     :ys (mapv :mean per-cat)
                                     :ymins (mapv #(- (:mean %) (:se %)) per-cat)
                                     :ymaxs (mapv #(+ (:mean %) (:se %)) per-cat)}
                              (some? gv) (assoc :color gv)))))
            all-ys (mapcat (fn [g] (concat (:ymins g) (:ymaxs g))) all-groups)
            y-min (if (seq all-ys) (reduce min all-ys) 0)
            y-max (if (seq all-ys) (reduce max all-ys) 1)]
        {:points all-groups
         :x-domain categories
         :y-domain [y-min y-max]}))))

(defmethod compute-stat :bin2d [{:keys [data x y cfg] :as view}]
  (validate-numeric-column view :x :bin2d)
  (validate-numeric-column view :y :bin2d)
  (let [cfg (or cfg defaults/defaults)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)]
    (if (zero? n)
      {:tiles [] :x-domain [0 1] :y-domain [0 1] :fill-range [0 1]}
      (let [xs-col (clean x)
            ys-col (clean y)
            [x-min x-max] (numeric-extent xs-col)
            [y-min y-max] (numeric-extent ys-col)
            n-bins (or (:tile-bins cfg) 15)
            x-step (/ (- (double x-max) (double x-min)) n-bins)
            y-step (/ (- (double y-max) (double y-min)) n-bins)
            ;; Count points in each bin
            counts (reduce (fn [acc i]
                             (let [xv (double (xs-col i))
                                   yv (double (ys-col i))
                                   xi (min (int (/ (- xv x-min) (max 1e-10 x-step)))
                                           (dec n-bins))
                                   yi (min (int (/ (- yv y-min) (max 1e-10 y-step)))
                                           (dec n-bins))
                                   k [xi yi]]
                               (assoc acc k (inc (get acc k 0)))))
                           {}
                           (range n))
            max-count (reduce max 1 (vals counts))
            ;; Build tile dataset — each tile is an observation with bounds and fill.
            ;; Iterate in row-major order (yi, xi) so the output is deterministic
            ;; regardless of the hash map's internal key ordering.
            tile-data (reduce (fn [acc [xi yi]]
                                (let [cnt (get counts [xi yi])]
                                  (if cnt
                                    (-> acc
                                        (update :x-lo conj (+ x-min (* xi x-step)))
                                        (update :x-hi conj (+ x-min (* (inc xi) x-step)))
                                        (update :y-lo conj (+ y-min (* yi y-step)))
                                        (update :y-hi conj (+ y-min (* (inc yi) y-step)))
                                        (update :fill conj cnt))
                                    acc)))
                              {:x-lo [] :x-hi [] :y-lo [] :y-hi [] :fill []}
                              (for [yi (range n-bins) xi (range n-bins)] [xi yi]))
            tiles (tc/dataset tile-data)]
        {:tiles tiles
         :x-domain [x-min x-max]
         :y-domain [y-min y-max]
         :fill-range [0 max-count]}))))

(defmethod compute-stat :kde2d [{:keys [data x y cfg] :as view}]
  (validate-numeric-column view :x :kde2d)
  (validate-numeric-column view :y :kde2d)
  (let [cfg (or cfg defaults/defaults)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)]
    (if (< n 2)
      ;; Empty-data branch: return :grid nil explicitly so downstream
      ;; contour extraction (which reads :grid) has shape stability.
      {:tiles [] :x-domain [0 1] :y-domain [0 1] :fill-range [0 1] :grid nil}
      (let [xs-col (clean x)
            ys-col (clean y)
            xs (double-array xs-col)
            ys (double-array ys-col)
            [x-min x-max] (numeric-extent xs-col)
            [y-min y-max] (numeric-extent ys-col)
            x-range (- (double x-max) (double x-min))
            y-range (- (double y-max) (double y-min))
            ;; Extend domain by 30% for smooth falloff at edges
            x-lo (- (double x-min) (* 0.3 x-range))
            x-hi (+ (double x-max) (* 0.3 x-range))
            y-lo (- (double y-min) (* 0.3 y-range))
            y-hi (+ (double y-max) (* 0.3 y-range))
            ;; Grid resolution
            n-grid (or (:kde2d-grid cfg) 25)
            x-step (max 1e-10 (/ (- x-hi x-lo) n-grid))
            y-step (max 1e-10 (/ (- y-hi y-lo) n-grid))
            ;; Bandwidth: Silverman's rule for 2D product-kernel KDE.
            ;; h_j = sigma_j * n^(-1/6), matching the d=2 case of the general
            ;; formula h = (4/(d+2))^(1/(d+4)) * sigma * n^(-1/(d+4)).
            ;; ggplot2 uses MASS::bandwidth.nrd which is slightly different
            ;; (Sheather-Jones plug-in), but Silverman-2D is a good default.
            bw-cfg (:kde2d-bandwidth cfg)
            x-std (stats/stddev xs)
            y-std (stats/stddev ys)
            n-pow (Math/pow (double n) (/ -1.0 6.0))
            bw-x (max 1e-10 (if bw-cfg (first bw-cfg) (* x-std n-pow)))
            bw-y (max 1e-10 (if bw-cfg (second bw-cfg) (* y-std n-pow)))
            inv-bwx2 (/ 1.0 (* 2.0 bw-x bw-x))
            inv-bwy2 (/ 1.0 (* 2.0 bw-y bw-y))
            ;; Compute density at each grid cell center
            densities (double-array (* n-grid n-grid))
            _ (dotimes [gi n-grid]
                (dotimes [gj n-grid]
                  (let [gx (+ x-lo (* (+ gi 0.5) x-step))
                        gy (+ y-lo (* (+ gj 0.5) y-step))
                        d (loop [k 0 acc 0.0]
                            (if (< k n)
                              (let [dx (- gx (aget xs k))
                                    dy (- gy (aget ys k))
                                    w (Math/exp (- (+ (* dx dx inv-bwx2)
                                                      (* dy dy inv-bwy2))))]
                                (recur (inc k) (+ acc w)))
                              acc))]
                    (aset densities (+ (* gi n-grid) gj) d))))
            max-d (dfn/reduce-max densities)
            ;; Build tile dataset with density as fill value
            tile-data (reduce (fn [acc [gi gj]]
                                (let [d (aget densities (+ (* gi n-grid) gj))]
                                  (if (> d (* 0.01 max-d))
                                    (-> acc
                                        (update :x-lo conj (+ x-lo (* gi x-step)))
                                        (update :x-hi conj (+ x-lo (* (inc gi) x-step)))
                                        (update :y-lo conj (+ y-lo (* gj y-step)))
                                        (update :y-hi conj (+ y-lo (* (inc gj) y-step)))
                                        (update :fill conj d))
                                    acc)))
                              {:x-lo [] :x-hi [] :y-lo [] :y-hi [] :fill []}
                              (for [gi (range n-grid) gj (range n-grid)] [gi gj]))
            tiles (tc/dataset tile-data)]
        {:tiles tiles
         :x-domain [x-lo x-hi]
         :y-domain [y-lo y-hi]
         :fill-range [0 max-d]
         ;; Raw grid data for contour extraction
         :grid {:densities densities
                :n-grid n-grid
                :x-lo x-lo :x-hi x-hi
                :y-lo y-lo :y-hi y-hi
                :x-step x-step :y-step y-step
                :max-d max-d}}))))
