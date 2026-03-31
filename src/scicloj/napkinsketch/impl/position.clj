(ns scicloj.napkinsketch.impl.position
  "Position adjustment — composable transforms on layer descriptors.
   Runs between extract-layer and build-panels in the sketch pipeline.

   Position types:
     :identity — no adjustment (default)
     :dodge    — side-by-side within a categorical band (annotation)
     :stack    — cumulative y-values across groups (data transform)
     :fill     — normalized cumulative y, sums to 1.0 (data transform)")

;; ---- Helpers ----

(defn- layer-group-labels
  "Extract group labels from a layer, regardless of structure."
  [layer]
  (cond
    (:groups layer) (keep :label (:groups layer))
    (:boxes layer) (keep (comp str :color-category) (:boxes layer))
    (:violins layer) (keep (comp str :color-category) (:violins layer))
    :else nil))

;; ---- Multimethod ----

(defmulti apply-position
  "Apply position adjustment to layers sharing a position type.
   Returns a vec of adjusted layers."
  (fn [position layers] position))

;; ---- Doc methods (dispatching on [position-key :doc]) ----

(defmethod apply-position [:identity :doc] [_ _] "Plot at exact data coordinates (groups overlap)")
(defmethod apply-position [:dodge :doc] [_ _] "Shift groups side-by-side within a band")
(defmethod apply-position [:stack :doc] [_ _] "Pile groups cumulatively")
(defmethod apply-position [:fill :doc] [_ _] "Stack normalized to [0, 1] (proportions)")

(defmethod apply-position :identity [_ layers] (vec layers))

;; ---- Dodge ----

(defmethod apply-position :dodge [_ layers]
  (let [all-labels (vec (distinct (mapcat layer-group-labels layers)))
        n-groups (max 1 (count all-labels))
        label->idx (zipmap all-labels (range))
        dodge-ctx {:n-groups n-groups}]
    (mapv
     (fn [layer]
       (cond-> (assoc layer :dodge-ctx dodge-ctx)
         (:groups layer)
         (update :groups
                 (fn [gs]
                   (mapv #(assoc % :dodge-idx
                                 (get label->idx (:label %) 0))
                         gs)))
         (:boxes layer)
         (update :boxes
                 (fn [bs]
                   (mapv #(assoc % :dodge-idx
                                 (get label->idx (str (:color-category %)) 0))
                         bs)))
         (:violins layer)
         (update :violins
                 (fn [vs]
                   (mapv #(assoc % :dodge-idx
                                 (get label->idx (str (:color-category %)) 0))
                         vs)))))
     layers)))

;; ---- Stack ----

(defn- stack-rect-layer
  "Apply stack to a :rect layer with categorical bar counts.
   Adds :y0 and :y1 to each count entry."
  [layer]
  (let [{:keys [groups]} layer
        {:keys [adjusted-groups]}
        (reduce
         (fn [{:keys [adjusted-groups cum]} group]
           (let [new-counts
                 (mapv (fn [{:keys [category count]}]
                         (let [base (get cum category 0.0)]
                           {:category category
                            :count count
                            :y0 base
                            :y1 (+ base (double count))}))
                       (:counts group))
                 new-cum (reduce (fn [c {:keys [category y1]}]
                                   (assoc c category (double y1)))
                                 cum new-counts)]
             {:adjusted-groups (conj adjusted-groups
                                     (assoc group :counts new-counts))
              :cum new-cum}))
         {:adjusted-groups [] :cum {}}
         groups)]
    (assoc layer :groups adjusted-groups)))

(defn- stack-area-layer
  "Apply stack to an :area layer.
   Adds :y0s baseline vector to each group."
  [layer]
  (let [{:keys [groups]} layer
        group-maps (mapv (fn [{:keys [xs ys]}]
                           (into (sorted-map) (map vector xs ys)))
                         groups)
        all-xs (vec (sort (distinct (mapcat keys group-maps))))
        {:keys [adjusted-groups]}
        (reduce
         (fn [{:keys [adjusted-groups cum]} [group gm]]
           (let [y0s (mapv #(get cum % 0.0) all-xs)
                 ys (mapv #(+ (get cum % 0.0) (get gm % 0.0)) all-xs)
                 new-cum (into cum (map vector all-xs ys))]
             {:adjusted-groups (conj adjusted-groups
                                     (assoc group
                                            :xs all-xs :ys ys :y0s y0s))
              :cum new-cum}))
         {:adjusted-groups [] :cum {}}
         (map vector groups group-maps))]
    (assoc layer :groups adjusted-groups)))

(defmethod apply-position :stack [_ layers]
  (mapv (fn [layer]
          (cond
            (:categories layer) (stack-rect-layer layer)
            (:groups layer) (stack-area-layer layer)
            :else layer))
        layers))

;; ---- Fill ----

(defn- normalize-fill-rect
  "Normalize bar counts per category to sum to 1.0."
  [layer]
  (let [groups (:groups layer)
        cat-totals (reduce (fn [acc g]
                             (reduce (fn [a {:keys [category count]}]
                                       (update a category (fnil + 0) count))
                                     acc
                                     (:counts g)))
                           {}
                           groups)
        normalized (mapv (fn [g]
                           (update g :counts
                                   (fn [counts]
                                     (mapv (fn [{:keys [category count]}]
                                             (let [total (get cat-totals category 1)]
                                               {:category category
                                                :count (if (pos? total)
                                                         (/ (double count) (double total))
                                                         0.0)}))
                                           counts))))
                         groups)]
    (assoc layer :groups normalized)))

(defmethod apply-position :fill [_ layers]
  (mapv (fn [layer]
          (cond
            (:categories layer) (-> layer normalize-fill-rect stack-rect-layer)
            (:groups layer) (stack-area-layer layer)
            :else layer))
        layers))

;; ---- Entry point ----

(defmethod apply-position :default [position _]
  (if (and (vector? position) (= :doc (second position)))
    "(no description)"
    (throw (ex-info (str "Position must be :dodge, :stack, :fill, or nil, got: " (pr-str position))
                    {:position position}))))

(defn apply-positions
  "Apply position adjustments to all layers in a panel.
   Groups layers by position type and applies adjustments per group.
   Stack/fill: modifies y-values (data transform).
   Dodge: annotates groups with indices (layout annotation)."
  [layers]
  (let [by-pos (group-by #(or (:position %) :identity) layers)]
    (vec (mapcat (fn [[pos ls]] (apply-position pos ls)) by-pos))))
