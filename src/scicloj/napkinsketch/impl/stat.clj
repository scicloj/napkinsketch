(ns scicloj.napkinsketch.impl.stat
  (:require [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [fastmath.ml.regression :as regr]
            [fastmath.stats :as stats]
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
  (let [{:keys [data x y color size shape text-col x-type y-type group mark]} view
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
                      (if (= mark :rect) [(min 0 lo) (max 0 hi)] [lo hi])))
            point-group (fn [ds group-val]
                          (cond-> {:xs (ds x) :ys (ds y)
                                   :row-indices (ds :__row-idx)}
                            group-val (assoc :color group-val)
                            size (assoc :sizes (ds size))
                            shape (assoc :shapes (ds shape))
                            text-col (assoc :labels (ds text-col))))
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
