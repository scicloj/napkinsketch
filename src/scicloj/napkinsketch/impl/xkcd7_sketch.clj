(ns scicloj.napkinsketch.impl.xkcd7-sketch
  "xkcd7-sketch: the xkcd7 composable data model.
   A xkcd7-sketch is a record with :data :shared :entries :methods :opts.
   Resolution: merge(shared, entry, method). Nil cancels.
   Produces view maps for the xkcd7-views->plan pipeline."
  (:require [tablecloth.api :as tc]
            [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Record ----

(defrecord Xkcd7Sketch [data shared entries methods opts])

(defn xkcd7-sketch?
  "True if x is a xkcd7-sketch."
  [x]
  (instance? Xkcd7Sketch x))

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

(defn xkcd7-resolve-sketch
  "Resolve a xkcd7-sketch into a flat vector of view maps for views->plan.
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
                         d (ensure-dataset (or (:data resolved) data))]
                     (-> resolved
                         (assoc :data d
                                :__entry-idx entry-idx)
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 entry-methods)))
        expanded)))))

;; ---- Rendering ----

(defn xkcd7-render-sketch
  "Render a xkcd7-sketch to SVG via xkcd7 grid pipeline.
   Called by Clay via kind/fn at display time.
   Restores config snapshot if present."
  [xkcd7-sk]
  (let [captured (:config-snapshot xkcd7-sk)
        render-fn (fn []
                    (let [opts (:opts xkcd7-sk {})
                          views (xkcd7-resolve-sketch xkcd7-sk)
                          plan (sketch-impl/xkcd7-views->plan views opts)
                          rendered (render-impl/plan->figure plan :svg opts)]
                      (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))]
    (if captured
      (binding [defaults/*config* captured]
        (render-fn))
      (render-fn))))

;; ---- Constructor ----

(defn ->xkcd7-sketch
  "Create a xkcd7-sketch annotated with kind/fn for auto-rendering.
   Snapshots current *config* for with-config support."
  [data shared entries methods opts]
  (kind/fn (cond-> (assoc (->Xkcd7Sketch data shared entries methods opts)
                          :kindly/f #'xkcd7-render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))
