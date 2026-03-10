(ns scicloj.napkinsketch.impl.stat
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [fastmath.ml.regression :as regr]
            [fastmath.stats :as stats]
            [fastmath.interpolation.acm :as interp]
            [fastmath.kernel :as kernel]
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

(defmethod compute-stat :identity [view]
  (prepare-points view))

;; ---- Binning ----

(defmethod compute-stat :bin [{:keys [data x x-type group cfg] :as view}]
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

(defmethod compute-stat :lm [view]
  (let [{:keys [data x y group]} view
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)]
    (if (or (< n 3)
            (= (dfn/reduce-min (clean x)) (dfn/reduce-max (clean x))))
      {:lines []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
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
         :y-domain (numeric-extent (clean y))}))))

;; ---- LOESS Smoothing ----

(defn fit-loess
  "Fit a LOESS curve, return {:xs ... :ys ...} evaluated on a grid."
  [xs-col ys-col n-grid bandwidth]
  (let [order (dtype/->int-array (tech.v3.datatype.argops/argsort xs-col))
        sorted-xs (dtype/indexed-buffer order xs-col)
        sorted-ys (dtype/indexed-buffer order ys-col)
        loess-fn (interp/loess (dtype/->double-array sorted-xs)
                               (dtype/->double-array sorted-ys)
                               {:bandwidth bandwidth})
        x-min (dfn/reduce-min xs-col)
        x-max (dfn/reduce-max xs-col)
        step (/ (- x-max x-min) (dec (double n-grid)))
        grid-xs (mapv #(+ x-min (* step (double %))) (range n-grid))
        grid-ys (mapv loess-fn grid-xs)]
    {:xs grid-xs :ys grid-ys}))

(defmethod compute-stat :loess [view]
  (let [{:keys [data x y group cfg]} view
        clean (tc/drop-missing data [x y])
        n (tc/row-count clean)
        n-grid (or (:loess-n-grid (or cfg defaults/defaults)) 80)
        bandwidth (or (:loess-bandwidth (or cfg defaults/defaults)) 0.75)]
    (if (or (< n 4)
            (= (dfn/reduce-min (clean x)) (dfn/reduce-max (clean x))))
      {:points []
       :x-domain (if (pos? n) (numeric-extent (clean x)) [0 1])
       :y-domain (if (pos? n) (numeric-extent (clean y)) [0 1])}
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
         :y-domain [y-min y-max]}))))

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
            y-max (if (seq all-ys) (reduce max all-ys) 1)]
        {:points curves
         :x-domain (numeric-extent (clean x))
         :y-domain [0 y-max]}))))

;; ---- Boxplot ----

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
        sorted-vals (sort col)
        whisker-lo (reduce (fn [best v] (if (and (>= (double v) fence-lo)
                                                 (< (double v) (double best)))
                                          v best))
                           (double q1) sorted-vals)
        whisker-hi (reduce (fn [best v] (if (and (<= (double v) fence-hi)
                                                 (> (double v) (double best)))
                                          v best))
                           (double q3) sorted-vals)
        outliers (filterv #(or (< (double %) fence-lo) (> (double %) fence-hi)) sorted-vals)]
    {:median median :q1 q1 :q3 q3
     :whisker-lo whisker-lo :whisker-hi whisker-hi
     :outliers outliers}))

(defmethod compute-stat :boxplot [view]
  (let [{:keys [data x y x-type group]} view
        clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] str))
        categories (distinct (clean x))]
    (if (empty? categories)
      {:boxes [] :categories [] :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            color-col (first group-cols)
            has-color? (and (seq group-cols) color-col)
            clean-c (if has-color? (tc/drop-missing clean group-cols) clean)
            color-cats (when has-color? (vec (sort (distinct (clean-c color-col)))))
            boxes (if has-color?
                    (vec (for [cat categories
                               cc color-cats
                               :let [rows (tc/select-rows clean-c
                                                          (fn [row] (and (= (get row x) cat)
                                                                         (= (get row color-col) cc))))
                                     n (tc/row-count rows)]
                               :when (pos? n)]
                           (merge (five-number-summary (rows y))
                                  {:category cat :color cc})))
                    (vec (for [cat categories
                               :let [rows (tc/select-rows clean-c
                                                          (fn [row] (= (get row x) cat)))
                                     n (tc/row-count rows)]
                               :when (pos? n)]
                           (merge (five-number-summary (rows y))
                                  {:category cat}))))
            all-ys (clean y)
            y-min (dfn/reduce-min all-ys)
            y-max (dfn/reduce-max all-ys)]
        {:boxes boxes
         :categories categories
         :color-categories color-cats
         :x-domain categories
         :y-domain [y-min y-max]}))))

;; ---- Violin ----

(defmethod compute-stat :violin [view]
  (let [{:keys [data x y x-type group cfg]} view
        clean (cond-> (tc/drop-missing data [x y])
                (= x-type :categorical) (tc/map-columns x [x] str))
        categories (distinct (clean x))
        n-grid (or (:kde-n-grid (or cfg defaults/defaults)) 80)
        bandwidth (:kde-bandwidth (or cfg defaults/defaults))]
    (if (empty? categories)
      {:violins [] :categories [] :x-domain ["?"] :y-domain [0 1]}
      (let [group-cols (or group [])
            color-col (first group-cols)
            has-color? (and (seq group-cols) color-col)
            clean-c (if has-color? (tc/drop-missing clean group-cols) clean)
            color-cats (when has-color? (vec (sort (distinct (clean-c color-col)))))
            violins (if has-color?
                      (vec (for [cat categories
                                 cc color-cats
                                 :let [rows (tc/select-rows clean-c
                                                            (fn [row] (and (= (get row x) cat)
                                                                           (= (get row color-col) cc))))
                                       n (tc/row-count rows)]
                                 :when (>= n 2)]
                             (let [kde (fit-kde (rows y) n-grid bandwidth)]
                               {:category cat :color cc
                                :ys (:xs kde) :densities (:ys kde)})))
                      (vec (for [cat categories
                                 :let [rows (tc/select-rows clean-c
                                                            (fn [row] (= (get row x) cat)))
                                       n (tc/row-count rows)]
                                 :when (>= n 2)]
                             (let [kde (fit-kde (rows y) n-grid bandwidth)]
                               {:category cat
                                :ys (:xs kde) :densities (:ys kde)}))))
            all-ys (clean y)
            y-min (dfn/reduce-min all-ys)
            y-max (dfn/reduce-max all-ys)]
        {:violins violins
         :categories categories
         :color-categories color-cats
         :x-domain categories
         :y-domain [y-min y-max]}))))
