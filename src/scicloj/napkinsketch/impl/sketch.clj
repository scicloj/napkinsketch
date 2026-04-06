(ns scicloj.napkinsketch.impl.sketch
  "sketch: the composable data model.
   A sketch is a record with :data :shared :entries :methods :opts.
   Resolution: merge(shared, entry, method). Nil cancels.
   Produces view maps for the views->plan pipeline."
  (:require [tablecloth.api :as tc]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Record ----

(defrecord Sketch [data shared entries methods opts])

(defn sketch?
  "True if x is a sketch."
  [x]
  (instance? Sketch x))

;; ---- Resolution ----

(defn- ensure-keyword-columns
  "If dataset has string column names, rename them to keywords."
  [ds]
  (let [renames (into {} (for [c (tc/column-names ds) :when (string? c)]
                           [c (keyword c)]))]
    (if (seq renames)
      (tc/rename-columns ds renames)
      ds)))

(defn- ensure-dataset
  "Coerce raw data to a Tablecloth dataset with keyword column names.
   Returns nil for nil input."
  [d]
  (when d (ensure-keyword-columns (if (tc/dataset? d) d (tc/dataset d)))))

(defn- expand-facets
  "Expand entries with keyword :facet-col/:facet-row into per-value entries."
  [entries data]
  (let [ds (ensure-dataset data)]
    (vec
     (mapcat
      (fn [entry]
        (let [fcol (:facet-col entry)
              frow (:facet-row entry)
              nc? (keyword? fcol)
              nr? (keyword? frow)]
          (cond
            (and nc? nr?)
            (let [ed (ensure-dataset (or (:data entry) ds))]
              (for [cv (distinct (ed fcol)) rv (distinct (ed frow))]
                (-> entry
                    (assoc :facet-col (str cv) :facet-row (str rv)
                           :data (tc/select-rows ed (fn [r] (and (= (r fcol) cv) (= (r frow) rv))))))))

            nc?
            (let [ed (ensure-dataset (or (:data entry) ds))]
              (for [cv (distinct (ed fcol))]
                (-> entry
                    (assoc :facet-col (str cv)
                           :data (tc/select-rows ed (fn [r] (= (r fcol) cv)))))))

            nr?
            (let [ed (ensure-dataset (or (:data entry) ds))]
              (for [rv (distinct (ed frow))]
                (-> entry
                    (assoc :facet-row (str rv)
                           :data (tc/select-rows ed (fn [r] (= (r frow) rv)))))))

            :else [entry])))
      entries))))

(defn resolve-sketch
  "Resolve a sketch into a flat vector of view maps for views->plan.
   Expands facets, crosses entries × methods, merges shared → entry → method.
   Each entry uses: own :methods (if any) + global methods.
   Entries without own :methods use global methods only.
   If no global methods exist and entry has no own methods, {:mark :infer} is used."
  [{:keys [data shared entries methods]}]
  (let [expanded (expand-facets entries data)
        global-methods methods]
    (let [idx (atom -1)]
      (vec
       (mapcat
        (fn [entry]
          (let [entry-idx (swap! idx inc)
                own-methods (:methods entry)
                ;; Annotation entries don't get global methods
                ann-entry? (some #(view/annotation-marks (:mark %)) own-methods)
                combined (if ann-entry?
                           own-methods
                           (concat (or own-methods nil) global-methods))
                entry-methods (if (seq combined) (vec combined) [{:mark :infer}])
                base (merge shared (dissoc entry :methods))]
            (map (fn [m]
                   (let [resolved (merge base m)
                         d (ensure-dataset (or (:data resolved) data))
                         ;; Normalize string column refs to keywords.
                         ;; Skip :color — it can be a literal color string (e.g. "#FF0000").
                         ;; Color normalization is handled in view/resolve-aesthetics.
                         resolved (reduce (fn [v k]
                                            (let [val (get v k)]
                                              (if (string? val)
                                                (assoc v k (keyword val))
                                                v)))
                                          resolved
                                          [:x :y :size :alpha :text :group])]
                     (-> resolved
                         (assoc :data d
                                :__entry-idx entry-idx)
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 entry-methods)))
        expanded)))))

;; ---- Rendering ----

(defn render-sketch
  "Render a sketch to SVG via grid pipeline.
   Called by Clay via kind/fn at display time.
   Restores config snapshot if present."
  [sk]
  (let [captured (:config-snapshot sk)
        render-fn (fn []
                    (let [opts (:opts sk {})
                          views (resolve-sketch sk)
                          plan (sketch-impl/views->plan views opts)
                          rendered (render-impl/plan->figure plan :svg opts)]
                      (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))]
    (if captured
      (binding [defaults/*config* captured]
        (render-fn))
      (render-fn))))

;; ---- Constructor ----

(defn ->sketch
  "Create a sketch annotated with kind/fn for auto-rendering.
   Snapshots current *config* for with-config support."
  [data shared entries methods opts]
  (kind/fn (cond-> (assoc (->Sketch data shared entries methods opts)
                          :kindly/f #'render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))
