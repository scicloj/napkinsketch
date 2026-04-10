(ns scicloj.napkinsketch.impl.sketch
  "Sketch: the composable data model.
   A sketch is a record with :data :mapping :views :layers :opts.
   Resolution: merge(sketch-mapping, view-mapping, method-info, layer-mapping)
   -> flat view maps -> plan (via plan.clj)."
  (:require [tablecloth.api :as tc]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.impl.plan :as plan]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.method :as method]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Record ----
;;
;; Sketch [data mapping views layers opts]
;;
;; :data     -- dataset (sketch-level)
;; :mapping  -- sketch-level aesthetic mappings (visible to all views, all layers)
;; :views    -- vector of view maps, each with :mapping, :layers, optional :data
;; :layers   -- sketch-level layers (crossed with all views)
;; :opts     -- plot options + scale/coord/facet/annotations
;;
;; Three scope levels: sketch -> view -> layer.
;; Mappings flow downward; lower overrides higher (lexical scope).

(defrecord Sketch [data mapping views layers opts])

(defn sketch?
  "True if x is a sketch."
  [x]
  (instance? Sketch x))

;; ---- Resolution ----

(defn- ensure-dataset
  "Coerce raw data to a Tablecloth dataset. Returns nil for nil input."
  [d]
  (when d (if (tc/dataset? d) d (tc/dataset d))))

(defn- expand-facets
  "Expand views by facet columns from opts into per-value views.
   Each faceted view gets filtered :data and string :facet-col/:facet-row labels."
  [views data facet-col facet-row]
  (let [nc? (resolve/column-ref? facet-col)
        nr? (resolve/column-ref? facet-row)]
    (if-not (or nc? nr?)
      views
      (vec
       (mapcat
        (fn [view]
          (let [ds (ensure-dataset (or (:data view) data))]
            (cond
              (and nc? nr?)
              (let [fcol (resolve/resolve-col-name ds facet-col)
                    frow (resolve/resolve-col-name ds facet-row)]
                (for [cv (distinct (ds fcol)) rv (distinct (ds frow))]
                  (assoc view
                         :facet-col (str cv) :facet-row (str rv)
                         :data (tc/select-rows ds (fn [r] (and (= (r fcol) cv) (= (r frow) rv)))))))

              nc?
              (let [fcol (resolve/resolve-col-name ds facet-col)]
                (for [cv (distinct (ds fcol))]
                  (assoc view
                         :facet-col (str cv)
                         :data (tc/select-rows ds (fn [r] (= (r fcol) cv))))))

              nr?
              (let [frow (resolve/resolve-col-name ds facet-row)]
                (for [rv (distinct (ds frow))]
                  (assoc view
                         :facet-row (str rv)
                         :data (tc/select-rows ds (fn [r] (= (r frow) rv)))))))))
        views)))))

(defn- resolve-method-info
  "Look up method info from a layer's :method key.
   Keyword -> registry lookup. Map -> pass through. :infer -> sentinel."
  [method-key]
  (cond
    (= :infer method-key)
    {:mark :infer}

    (keyword? method-key)
    (let [m (method/lookup method-key)]
      (or (not-empty (select-keys (or m {}) [:mark :stat :position]))
          {:mark method-key :stat :identity}))

    :else ;; raw map -- pass through
    method-key))

(defn- validate-columns
  "Validate that every aesthetic column reference in the resolved mapping
   names a real column in the dataset. Covers :x, :y, :color, :size, :alpha,
   :shape, :group, :text, :ymin, :ymax, :fill. Accepts both keyword and
   string refs with cross-type matching.

   String values for :color are left alone: literal colors like \"red\" or
   \"#FF0000\" are legitimate. For other aesthetic keys, string values are
   treated as column refs and validated."
  [resolved d]
  (when d
    (let [col-names (set (tc/column-names d))
          col-exists? (fn [col]
                        (or (col-names col)
                            (and (keyword? col) (col-names (name col)))
                            (and (string? col) (col-names (keyword col)))))]
      (doseq [k defaults/column-keys
              :let [col (get resolved k)]
              :when (and col
                         (resolve/column-ref? col)
                         ;; :color strings are legitimate literals (color names, hex codes)
                         (not (and (= k :color) (string? col)))
                         (not (col-exists? col)))]
        (throw (ex-info (str "Column " col " (from " k ") not found in dataset. Available: " (sort col-names))
                        {:key k :column col :available (sort col-names)}))))))

(defn resolve-sketch
  "Resolve a sketch into a flat vector of view maps for views->plan.
   Reads facet/scale/coord from opts. Expands facets on views.
   Crosses views x layers: each view's own :layers ∪ sketch :layers.
   Merges mappings downward: sketch-mapping < view-mapping < method-info < layer-mapping.
   Annotation views (with annotation marks in own layers) skip sketch layers.
   Views with no applicable layers get {:mark :infer} for downstream inference."
  [{:keys [data mapping views layers opts]}]
  (let [;; Plot-level settings from opts
        x-scale (:x-scale opts)
        y-scale (:y-scale opts)
        coord-type (:coord opts)
        ;; Expand facets
        expanded (expand-facets views data (:facet-col opts) (:facet-row opts))
        sketch-layers layers
        sketch-mapping (or mapping {})]
    (let [idx (atom -1)]
      (vec
       (mapcat
        (fn [view]
          (let [view-idx (swap! idx inc)
                view-mapping (or (:mapping view) {})
                view-layers (:layers view)
                view-data (:data view)
                ;; Annotation views: own layers contain annotation marks
                ann-view? (some (fn [layer]
                                  (let [mk (:method layer)]
                                    (resolve/annotation-marks
                                     (if (keyword? mk) mk (:mark mk)))))
                                view-layers)
                ;; Combine: view layers ∪ sketch layers (unless annotation)
                combined (if ann-view?
                           view-layers
                           (concat (or view-layers nil) sketch-layers))
                applicable (if (seq combined) (vec combined) [{:method :infer}])]
            (map (fn [layer]
                   (let [method-info (resolve-method-info (:method layer))
                         layer-mapping (or (:mapping layer) {})
                         ;; Four-level merge: sketch < view < method < layer
                         ;; Method sets mark/stat/position; layer can override all
                         resolved (merge sketch-mapping
                                         view-mapping
                                         method-info
                                         layer-mapping)
                         ;; Data: layer > view > sketch
                         d (ensure-dataset (or (:data layer) view-data data))]
                     (validate-columns resolved d)
                     (-> resolved
                         (assoc :data d
                                :__entry-idx view-idx)
                         ;; Stamp plot-level settings from opts
                         (cond->
                          x-scale (assoc :x-scale x-scale)
                          y-scale (assoc :y-scale y-scale)
                          coord-type (assoc :coord coord-type)
                          (:facet-col view) (assoc :facet-col (:facet-col view))
                          (:facet-row view) (assoc :facet-row (:facet-row view)))
                         ;; Mark inference: remove :mark/:stat so downstream infers
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 applicable)))
        expanded)))))

;; ---- Rendering ----

(defn render-sketch
  "Render a sketch via the grid pipeline.
   Called by Clay via kind/fn at display time.
   Restores config snapshot if present.
   When opts contain :format :bufimg, renders to BufferedImage (via
   membrane's Java2D backend) instead of SVG. Clay displays BufferedImage
   values as inline images automatically."
  [sk]
  (let [captured (:config-snapshot sk)
        render-fn (fn []
                    (let [opts (:opts sk {})
                          fmt (or (:format opts) :svg)
                          views (resolve-sketch sk)
                          p (plan/views->plan views opts)]
                      (if (= fmt :bufimg)
                        (do
                          ;; Ensure the bufimg renderer is loaded
                          (require 'scicloj.napkinsketch.render.bufimg)
                          (render-impl/plan->figure p :bufimg opts))
                        (let [rendered (render-impl/plan->figure p :svg opts)]
                          (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))))]
    (if captured
      (binding [defaults/*config* captured]
        (render-fn))
      (render-fn))))

;; ---- Constructor ----

(defn ->sketch
  "Create a sketch annotated with kind/fn for auto-rendering.
   Snapshots current *config* for with-config support."
  [data mapping views layers opts]
  (kind/fn (cond-> (assoc (->Sketch data mapping views layers opts)
                          :kindly/f #'render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))
