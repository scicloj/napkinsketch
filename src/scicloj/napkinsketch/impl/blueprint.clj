(ns scicloj.napkinsketch.impl.blueprint
  "Blueprint: the xkcd7 composable data model.
   A Blueprint is a record with :data :shared :entries :methods :opts.
   Resolution: merge(shared, entry, method). Nil cancels.
   Produces view maps for the xkcd7-views->plan pipeline."
  (:require [tablecloth.api :as tc]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Record ----

(defrecord Blueprint [data shared entries methods opts])

(defn blueprint?
  "True if x is a Blueprint."
  [x]
  (instance? Blueprint x))

;; ---- Resolution ----

(defn- ensure-dataset
  "Coerce raw data to a Tablecloth dataset. Returns nil for nil input."
  [d]
  (when d (if (tc/dataset? d) d (tc/dataset d))))

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

(defn resolve-blueprint
  "Resolve a Blueprint into a flat vector of view maps for views->plan.
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
                ;; Annotation entries (rule-h, band-v etc.) don't get global methods
                ann-entry? (some #(#{:rule-h :rule-v :band-h :band-v} (:mark %)) own-methods)
                combined (if ann-entry?
                           own-methods
                           (concat (or own-methods nil) global-methods))
                entry-methods (if (seq combined) (vec combined) [{:mark :infer}])
                base (merge shared (dissoc entry :methods))]
            (map (fn [m]
                   (let [resolved (merge base m)
                         d (ensure-dataset (or (:data resolved) data))]
                     (-> resolved
                         (assoc :data d
                                :__entry-idx entry-idx)
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 entry-methods)))
        expanded)))))

;; ---- Rendering ----

(defn render-blueprint
  "Render a Blueprint to SVG via xkcd7 grid pipeline.
   Called by Clay via kind/fn at display time.
   Restores config snapshot if present."
  [bp]
  (let [captured (:config-snapshot bp)
        render-fn (fn []
                    (let [opts (:opts bp {})
                          views (resolve-blueprint bp)
                          plan (sketch-impl/xkcd7-views->plan views opts)
                          rendered (render-impl/plan->figure plan :svg opts)]
                      (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))]
    (if captured
      (binding [defaults/*config* captured]
        (render-fn))
      (render-fn))))

;; ---- Constructor ----

(defn ->blueprint
  "Create a Blueprint annotated with kind/fn for auto-rendering.
   Snapshots current *config* for with-config support."
  [data shared entries methods opts]
  (kind/fn (cond-> (assoc (->Blueprint data shared entries methods opts)
                          :kindly/f #'render-blueprint)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))
