(ns scicloj.napkinsketch.impl.stat
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [fastmath.ml.regression :as regr]
            [fastmath.stats :as stats]
            [fastmath.stats.bootstrap :as boot]
            [fastmath.interpolation.acm :as interp]
            [fastmath.kernel :as kernel]
            [fastmath.random :as frand]
            [scicloj.napkinsketch.impl.defaults :as defaults]))

;; ---- Helpers ----

(defn numeric-extent
  "Min/max pair from a numeric column."
  [col]
  [(dfn/reduce-min col) (dfn/reduce-max col)])

(defn group-by-columns
  "Split dataset by grouping columns, apply f to each group."
  [ds group-cols f]
  (if (seq group-cols)
    (for [[gk gds] (tc/group-by ds group-cols {:result-type :as-map})]
      (f gds (if (= 1 (count group-cols))
               (get gk (first group-cols))
               (mapv gk group-cols))))
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
  "Clean data, compute domains, group by columns."
  [view]
  (let [{:keys [data x y color color-type size alpha shape text-col x-type y-type group mark ymin ymax]} view
        data-idx (tc/add-column data :__row-idx (range (tc/row-count data)))
        clean (cond-> (tc/drop-missing data-idx [x y])
                (= x-type :categorical) (tc/map-columns x [x] str))]
    (if (zero? (tc/row-count clean))
      {:points [] :x-domain [0 1] :y-domain [0 1]}
      (let [xs-col (clean x)
            ys-col (clean y)
            cat-x? (= x-type :categorical)
            cat-y? (= y-type :categorical)
            x-dom (if cat-x? (distinct xs-col) (numeric-extent xs-col))
            y-dom (if cat-y?
                    (distinct ys-col)
                    (let [[lo hi] (numeric-extent ys-col)]
                      (if (= mark :rect) [(min 0 lo) (max 0 hi)]
                          ;; Extend domain to include ymin/ymax if present
                          (if (and ymin ymax)
                            [(min lo (reduce min (clean ymin)))
                             (max hi (reduce max (clean ymax)))]
                            [lo hi]))))
            numeric-color? (and color (= color-type :numerical))
            ;; Extract color value from group key when color is part of group
            color-idx (when color (.indexOf ^java.util.List (vec group) color))
            extract-color (fn [group-val]
                            (cond
                              (nil? group-val) nil
                              ;; Single group col — group-val is the value itself
                              (= 1 (count group)) group-val
                              ;; Multiple group cols — extract color column value
                              (and color-idx (>= color-idx 0)) (nth group-val color-idx)
                              :else nil))
            point-group (fn [ds group-val]
                          (cond-> {:xs (ds x) :ys (ds y)
                                   :row-indices (ds :__row-idx)}
                            group-val (assoc :color (extract-color group-val))
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

(defmethod compute-stat :identity [{:keys [mark x y x-type] :as view}]
  (when (and (#{:lollipop :errorbar :pointrange} mark)
             (or (nil? y) (= x y))
             (= x-type :categorical))
    (throw (ex-info (str "Mark :" (name mark) " requires both :x and :y columns with numeric :y.")
                    {:mark mark :x x :y y})))
  (prepare-points view))

;; ---- Binning ----

(defmethod compute-stat :bin [{:keys [data x x-type group cfg normalize] :as view}]
  (let [clean (cond-> (tc/drop-missing data [x])
                (= x-type :categorical) (tc/map-columns x [x] str))
        xs-col (clean x)]
    (if (zero? (tc/row-count clean))
      {:bins [] :max-count 0 :x-domain [0 1] :y-domain [0 1]}
      (let [all-bin-data (group-by-columns
                          clean (or group [])
                          (fn [ds gv]
                            (let [hist (stats/histogram (ds x) (:bin-method (or cfg defaults/defaults)))]
                              (cond-> {:bin-maps (:bins-maps hist)}
                                gv (assoc :color gv)))))
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
            max-count (reduce max 1 (for [{:keys [bin-maps]} all-bin-data
                                          b bin-maps]
                                      (:count b)))]
        {:bins all-bin-data
         :max-count max-count
         :x-domain (numeric-extent xs-col)
         :y-domain [0 max-count]}))))

;; ---- Counting ----

(defmethod compute-stat :count [view]
  (let [{:keys [data x x-type group]} view
        group-cols (or group [])
        clean (cond-> (tc/drop-missing data [x])
                (= x-type :categorical) (tc/map-columns x [x] str))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:categories [] :bars [] :max-count 0 :x-domain ["?"] :y-domain [0 1]}
      (if (seq group-cols)
        (let [color-col (first group-cols)
              clean-c (tc/drop-missing clean group-cols)
              color-cats (sort (distinct (clean-c color-col)))
              all-group-cols (distinct (cons x group-cols))
              grouped (tc/group-by clean-c all-group-cols {:result-type :as-map})
              count-fn (fn [cat cc]
                         (let [key (merge {x cat} (zipmap group-cols
                                                          (if (= 1 (count group-cols))
                                                            [cc] cc)))]
                           (if-let [ds (get grouped key)]
                             (tc/row-count ds) 0)))
              max-count (reduce max 1 (for [cat categories, cc color-cats]
                                        (count-fn cat cc)))]
          {:categories categories
           :bars (for [cc color-cats]
                   {:color cc
                    :counts (mapv (fn [cat] {:category cat :count (count-fn cat cc)})
                                  categories)})
           :max-count max-count
           :x-domain categories
           :y-domain [0 max-count]})
        (let [grouped (tc/group-by clean [x] {:result-type :as-map})
              counts-by-cat (mapv (fn [cat]
                                    {:category cat
                                     :count (if-let [ds (get grouped {x cat})]
                                              (tc/row-count ds) 0)})
                                  categories)
              max-count (reduce max 1 (map :count counts-by-cat))]
          {:categories categories
           :bars [{:counts counts-by-cat}]
           :max-count max-count
           :x-domain categories
           :y-domain [0 max-count]})))))

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
        step (/ (- x-max x-min) (dec (double n-grid)))
        grid-xs (mapv #(+ x-min (* step (double %))) (range n-grid))
        grid-ys (mapv #(regr/predict model [%]) grid-xs)
        grid-ymins (mapv (fn [xi yi]
                           (let [h (+ a00 (* 2.0 a01 xi) (* a11 xi xi))
                                 se (* sigma (Math/sqrt h))]
                             (- (double yi) (* t-val se))))
                         grid-xs grid-ys)
        grid-ymaxs (mapv (fn [xi yi]
                           (let [h (+ a00 (* 2.0 a01 xi) (* a11 xi xi))
                                 se (* sigma (Math/sqrt h))]
                             (+ (double yi) (* t-val se))))
                         grid-xs grid-ys)]
    {:xs grid-xs :ys grid-ys
     :ymins grid-ymins :ymaxs grid-ymaxs
     :x1 x-min :y1 (regr/predict model [x-min])
     :x2 x-max :y2 (regr/predict model [x-max])}))

(defmethod compute-stat :lm [view]
  (let [{:keys [data x y group cfg]} view
        se (:se view)
        level (or (:level view) 0.95)
        n-grid (or (:se-n-grid (or cfg defaults/defaults)) 80)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)]
    (if (or (< n 3)
            (= (dfn/reduce-min (clean x)) (dfn/reduce-max (clean x))))
      {:lines []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
      (if se
        ;; With confidence band
        (let [results (group-by-columns
                       clean (or group [])
                       (fn [ds gv]
                         (when (and (>= (tc/row-count ds) 3)
                                    (not= (dfn/reduce-min (ds x))
                                          (dfn/reduce-max (ds x))))
                           (cond-> (fit-lm-with-se (ds x) (ds y) n-grid level)
                             gv (assoc :color gv)))))
              results (remove nil? results)
              lines (mapv (fn [{:keys [x1 y1 x2 y2 color]}]
                            (cond-> {:x1 x1 :y1 y1 :x2 x2 :y2 y2}
                              color (assoc :color color)))
                          results)
              ribbons (mapv (fn [{:keys [xs ys ymins ymaxs color]}]
                              (cond-> {:xs xs :ys ys :ymins ymins :ymaxs ymaxs}
                                color (assoc :color color)))
                            results)
              all-ymins (mapcat :ymins results)
              all-ymaxs (mapcat :ymaxs results)
              y-ext (numeric-extent (clean y))
              y-lo (min (first y-ext) (reduce min all-ymins))
              y-hi (max (second y-ext) (reduce max all-ymaxs))]
          {:lines lines
           :ribbons ribbons
           :x-domain (numeric-extent (clean x))
           :y-domain [y-lo y-hi]})
        ;; Without confidence band (original behavior)
        (let [lines (group-by-columns
                     clean (or group [])
                     (fn [ds gv]
                       (when (and (>= (tc/row-count ds) 3)
                                  (not= (dfn/reduce-min (ds x))
                                        (dfn/reduce-max (ds x))))
                         (cond-> (fit-lm (ds x) (ds y))
                           gv (assoc :color gv)))))]
          {:lines (remove nil? lines)
           :x-domain (numeric-extent (clean x))
           :y-domain (numeric-extent (clean y))})))))

;; ---- LOESS Smoothing ----

(defn- dedup-xy
  "Average y-values for duplicate x-values in sorted arrays.
   Returns [unique-xs averaged-ys] as double arrays."
  [xs ys]
  (let [pairs (map vector (seq xs) (seq ys))
        grouped (group-by first pairs)
        sorted-keys (sort (keys grouped))
        deduped (mapv (fn [x]
                        (let [yvals (map second (grouped x))]
                          [x (/ (reduce + yvals) (count yvals))]))
                      sorted-keys)]
    [(double-array (map first deduped))
     (double-array (map second deduped))]))

(defn fit-loess
  "Fit a LOESS curve, return {:xs ... :ys ...} evaluated on a grid."
  [xs-col ys-col n-grid bandwidth]
  (let [order (dtype/->int-array (tech.v3.datatype.argops/argsort xs-col))
        sorted-xs (dtype/->double-array (dtype/indexed-buffer order xs-col))
        sorted-ys (dtype/->double-array (dtype/indexed-buffer order ys-col))
        [uxs uys] (dedup-xy sorted-xs sorted-ys)
        loess-fn (interp/loess uxs uys {:bandwidth bandwidth})
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (/ (- x-max x-min) (dec (double n-grid)))
        grid-xs (mapv #(+ x-min (* step (double %))) (range n-grid))
        grid-ys (mapv loess-fn grid-xs)]
    {:xs grid-xs :ys grid-ys}))

(defn fit-loess-with-se
  "Fit LOESS with bootstrap confidence band. Returns
   {:xs :ys :ymins :ymaxs} evaluated on a grid."
  [xs-col ys-col n-grid bandwidth level n-boot]
  (let [;; Original fit (with dedup for duplicate x-values)
        order (dtype/->int-array (tech.v3.datatype.argops/argsort xs-col))
        sorted-xs (dtype/->double-array (dtype/indexed-buffer order xs-col))
        sorted-ys (dtype/->double-array (dtype/indexed-buffer order ys-col))
        [uxs uys] (dedup-xy sorted-xs sorted-ys)
        loess-fn (interp/loess uxs uys {:bandwidth bandwidth})
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (/ (- x-max x-min) (dec (double n-grid)))
        grid-xs (mapv #(+ x-min (* step (double %))) (range n-grid))
        grid-ys (mapv loess-fn grid-xs)

        ;; Bootstrap: resample (x,y) pairs, fit LOESS, evaluate on grid
        pairs (mapv vector (seq (dtype/->double-array xs-col))
                    (seq (dtype/->double-array ys-col)))
        rng-inst (frand/rng :jdk 42)
        boot-result (boot/bootstrap {:data pairs} nil
                                    {:samples n-boot :rng rng-inst})
        boot-grid-ys (for [sample (:samples boot-result)]
                       (let [[bsx bsy] (dedup-xy (map first sample)
                                                 (map second sample))]
                         (when (>= (alength bsx) 4)
                           (try
                             (let [bfn (interp/loess bsx bsy {:bandwidth bandwidth})]
                               (mapv bfn grid-xs))
                             (catch Exception _ nil)))))
        valid-boots (vec (remove nil? boot-grid-ys))
        n-valid (count valid-boots)
        alpha (- 1.0 (double level))
        lo-idx (max 0 (long (* (/ alpha 2.0) n-valid)))
        hi-idx (min (dec (max 1 n-valid))
                    (long (* (- 1.0 (/ alpha 2.0)) n-valid)))
        ymins (mapv (fn [i]
                      (let [vals (sort (map #(nth % i) valid-boots))]
                        (nth vals lo-idx)))
                    (range n-grid))
        ymaxs (mapv (fn [i]
                      (let [vals (sort (map #(nth % i) valid-boots))]
                        (nth vals hi-idx)))
                    (range n-grid))]
    {:xs grid-xs :ys grid-ys :ymins ymins :ymaxs ymaxs}))

(defmethod compute-stat :loess [view]
  (let [{:keys [data x y group cfg]} view
        se (:se view)
        level (or (:level view) 0.95)
        n-boot (or (:se-boot view) 200)
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)
        n-grid (or (:loess-n-grid (or cfg defaults/defaults)) 80)
        bandwidth (or (:loess-bandwidth (or cfg defaults/defaults)) 0.75)]
    (if (or (< n 4)
            (= (dfn/reduce-min (clean x)) (dfn/reduce-max (clean x))))
      {:points []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
      (if se
        ;; With confidence band (bootstrap)
        (let [results (group-by-columns
                       clean (or group [])
                       (fn [ds gv]
                         (when (and (>= (tc/row-count ds) 4)
                                    (not= (dfn/reduce-min (ds x))
                                          (dfn/reduce-max (ds x))))
                           (cond-> (fit-loess-with-se (ds x) (ds y)
                                                      n-grid bandwidth level n-boot)
                             gv (assoc :color gv)))))
              results (remove nil? results)
              curves (mapv (fn [{:keys [xs ys color]}]
                             (cond-> {:xs xs :ys ys}
                               color (assoc :color color)))
                           results)
              ribbons (mapv (fn [{:keys [xs ys ymins ymaxs color]}]
                              (cond-> {:xs xs :ys ys :ymins ymins :ymaxs ymaxs}
                                color (assoc :color color)))
                            results)
              all-ymins (mapcat :ymins results)
              all-ymaxs (mapcat :ymaxs results)
              y-ext (numeric-extent (clean y))
              y-lo (min (first y-ext) (if (seq all-ymins) (reduce min all-ymins) (first y-ext)))
              y-hi (max (second y-ext) (if (seq all-ymaxs) (reduce max all-ymaxs) (second y-ext)))]
          {:points curves
           :ribbons ribbons
           :x-domain (numeric-extent (clean x))
           :y-domain [y-lo y-hi]})
        ;; Without confidence band (original behavior)
        (let [curves (group-by-columns
                      clean (or group [])
                      (fn [ds gv]
                        (when (and (>= (tc/row-count ds) 4)
                                   (not= (dfn/reduce-min (ds x))
                                         (dfn/reduce-max (ds x))))
                          (cond-> (fit-loess (ds x) (ds y) n-grid bandwidth)
                            gv (assoc :color gv)))))
              curves (remove nil? curves)
              all-ys (mapcat :ys curves)
              y-min (if (seq all-ys) (reduce min all-ys) (dfn/reduce-min (clean y)))
              y-max (if (seq all-ys) (reduce max all-ys) (dfn/reduce-max (clean y)))]
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
        step (/ (- hi lo) (double (dec n-grid)))
        grid-xs (mapv #(+ lo (* step (double %))) (range n-grid))
        grid-ys (mapv kd grid-xs)]
    {:xs grid-xs :ys grid-ys}))

(defmethod compute-stat :kde [view]
  (validate-numeric-column view :x :kde)
  (let [{:keys [data x group cfg]} view
        clean (tc/drop-missing data [x])
        n (tc/row-count clean)
        n-grid (or (:kde-n-grid (or cfg defaults/defaults)) 100)
        bandwidth (:kde-bandwidth (or cfg defaults/defaults))]
    (if (or (< n 2)
            (= (dfn/reduce-min (clean x)) (dfn/reduce-max (clean x))))
      {:points []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain [0 1]}
      (let [curves (group-by-columns
                    clean (or group [])
                    (fn [ds gv]
                      (when (>= (tc/row-count ds) 2)
                        (cond-> (fit-kde (ds x) n-grid bandwidth)
                          gv (assoc :color gv)))))
            curves (remove nil? curves)
            all-ys (mapcat :ys curves)
            y-max (if (seq all-ys) (reduce max all-ys) 1)
            all-xs (mapcat :xs curves)
            x-lo (reduce min all-xs)
            x-hi (reduce max all-xs)]
        {:points curves
         :x-domain [x-lo x-hi]
         :y-domain [0 y-max]}))))

;; ---- Boxplot ----

(defn- per-category-stat
  "Shared iteration for boxplot/violin: clean data, group by category and
   optional color, apply per-group-fn to each subset's y-column.
   per-group-fn: (fn [y-col category color-or-nil]) -> map
   min-n: minimum row count to include a group."
  [view min-n per-group-fn]
  (let [{:keys [data x y x-type group]} view
        clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] str))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:items [] :categories [] :color-categories nil
       :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            color-col (first group-cols)
            has-color? (and (seq group-cols) color-col)
            clean-c (if has-color? (tc/drop-missing clean group-cols) clean)
            color-cats (when has-color? (vec (sort (distinct (clean-c color-col)))))
            items (if has-color?
                    (vec (for [cat categories
                               cc color-cats
                               :let [rows (tc/select-rows clean-c
                                                          (fn [row] (and (= (get row x) cat)
                                                                         (= (get row color-col) cc))))
                                     n (tc/row-count rows)]
                               :when (>= n min-n)]
                           (per-group-fn (rows y) cat cc)))
                    (vec (for [cat categories
                               :let [rows (tc/select-rows clean-c
                                                          (fn [row] (= (get row x) cat)))
                                     n (tc/row-count rows)]
                               :when (>= n min-n)]
                           (per-group-fn (rows y) cat nil))))
            all-ys (clean y)
            y-min (dfn/reduce-min all-ys)
            y-max (dfn/reduce-max all-ys)]
        {:items items
         :categories categories
         :color-categories color-cats
         :x-domain categories
         :y-domain [y-min y-max]}))))

(defn- five-number-summary
  "Compute boxplot five-number summary for a numeric column.
   Returns {:median :q1 :q3 :whisker-lo :whisker-hi :outliers}."
  [col]
  (let [q1 (stats/quantile col 0.25)
        median (stats/quantile col 0.5)
        q3 (stats/quantile col 0.75)
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
        all-kde-ys (mapcat :ys (:items result))
        y-domain (if (seq all-kde-ys)
                   [(reduce min all-kde-ys) (reduce max all-kde-ys)]
                   (:y-domain result))]
    {:violins (:items result)
     :categories (:categories result)
     :color-categories (:color-categories result)
     :x-domain (:x-domain result)
     :y-domain y-domain}))

;; ---- 2D Binning (for heatmap/tile) ----

;; ---- Summary (mean ± SE per category) ----

(defmethod compute-stat :summary [{:keys [data x y x-type group] :as view}]
  (let [clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] str))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:points [] :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            all-groups (group-by-columns
                        clean group-cols
                        (fn [ds gv]
                          (let [per-cat (for [cat categories
                                              :let [rows (tc/select-rows ds (fn [row] (= (get row x) cat)))
                                                    n (tc/row-count rows)]
                                              :when (pos? n)
                                              :let [ys-col (rows y)
                                                    mean-val (stats/mean ys-col)
                                                    se (if (>= n 2)
                                                         (/ (stats/stddev ys-col) (Math/sqrt (double n)))
                                                         0.0)]]
                                          {:cat cat :mean mean-val :se se})]
                            (cond-> {:xs (mapv :cat per-cat)
                                     :ys (mapv :mean per-cat)
                                     :ymins (mapv #(- (:mean %) (:se %)) per-cat)
                                     :ymaxs (mapv #(+ (:mean %) (:se %)) per-cat)}
                              gv (assoc :color gv)))))
            all-ys (mapcat (fn [g] (concat (:ymins g) (:ymaxs g))) all-groups)
            y-min (if (seq all-ys) (reduce min all-ys) 0)
            y-max (if (seq all-ys) (reduce max all-ys) 1)]
        {:points all-groups
         :x-domain categories
         :y-domain [y-min y-max]}))))

(defmethod compute-stat :bin2d [{:keys [data x y cfg] :as view}]
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
            tiles (vec (for [[[xi yi] cnt] counts]
                         {:x-lo (+ x-min (* xi x-step))
                          :x-hi (+ x-min (* (inc xi) x-step))
                          :y-lo (+ y-min (* yi y-step))
                          :y-hi (+ y-min (* (inc yi) y-step))
                          :fill cnt}))]
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
      {:tiles [] :x-domain [0 1] :y-domain [0 1] :fill-range [0 1]}
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
            x-step (/ (- x-hi x-lo) n-grid)
            y-step (/ (- y-hi y-lo) n-grid)
            ;; Bandwidth: Silverman's rule per axis
            bw-cfg (:kde2d-bandwidth cfg)
            x-std (stats/stddev (seq xs))
            y-std (stats/stddev (seq ys))
            n-pow (Math/pow (double n) -0.2)
            bw-x (if bw-cfg (first bw-cfg) (* 1.06 x-std n-pow))
            bw-y (if bw-cfg (second bw-cfg) (* 1.06 y-std n-pow))
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
            max-d (reduce max 0.0 (seq densities))
            ;; Build tiles with density as fill value
            tiles (vec (for [gi (range n-grid)
                             gj (range n-grid)
                             :let [d (aget densities (+ (* gi n-grid) gj))]
                             :when (> d (* 0.01 max-d))]
                         {:x-lo (+ x-lo (* gi x-step))
                          :x-hi (+ x-lo (* (inc gi) x-step))
                          :y-lo (+ y-lo (* gj y-step))
                          :y-hi (+ y-lo (* (inc gj) y-step))
                          :fill d}))]
        {:tiles tiles
         :x-domain [x-lo x-hi]
         :y-domain [y-lo y-hi]
         :fill-range [0 max-d]
         ;; Raw grid data for contour extraction
         :grid {:densities (vec (seq densities))
                :n-grid n-grid
                :x-lo x-lo :x-hi x-hi
                :y-lo y-lo :y-hi y-hi
                :x-step x-step :y-step y-step
                :max-d max-d}}))))
