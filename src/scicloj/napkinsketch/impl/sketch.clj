(ns scicloj.napkinsketch.impl.sketch
  "Sketch: the composable data model.
   A sketch is a record with :data :mapping :views :layers :opts.
   sketch->draft: merge(sketch-mapping, view-mapping, method-info, layer-mapping)
   -> flat draft maps -> plan (via plan.clj)."
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
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
;; :opts     -- plot options + scale/coord/facet
;;
;; Three scope levels: sketch -> view -> layer.
;; Mappings flow downward; lower overrides higher (lexical scope).

(defrecord Sketch [data mapping views layers opts])

(defn sketch?
  "True if x is a sketch."
  [x]
  (instance? Sketch x))

;; ---- Draft (sketch -> flat view maps) ----

(defn- ensure-dataset
  "Coerce raw data to a Tablecloth dataset. Returns nil for nil input.
   Rejects Sketch records to prevent silent coercion to a bogus
   5-column dataset (:data :mapping :views :layers :opts)."
  [d]
  (when d
    (cond
      (instance? Sketch d)
      (throw (ex-info (str ":data must be a dataset or map of columns, not a sketch. "
                           "Pass tabular data (dataset, map of columns, or row maps), "
                           "or remove the :data override.")
                      {:data-type 'Sketch}))

      (tc/dataset? d) d
      :else (tc/dataset d))))

(defn- resolve-facet-col
  "Resolve a facet column ref against a dataset; throw with a clear error
   message if the column is missing."
  [ds role ref]
  (let [col-names (set (tc/column-names ds))
        fname (resolve/resolve-col-name ds ref)]
    (when-not (contains? col-names fname)
      (throw (ex-info (str "Facet column " ref " (from " role ") not found in dataset. Available: " (sort col-names))
                      {:role role :column ref :available (sort col-names)})))
    fname))

(defn- expand-facets
  "Expand views by facet columns from opts into per-value views.
   Each faceted view gets filtered :data and string :facet-col/:facet-row labels."
  [views data facet-col facet-row]
  ;; Reject vector facet specs with a clear error until nested faceting is a feature.
  (when (and facet-col (sequential? facet-col))
    (throw (ex-info (str "Facet column must be a single keyword or string, got vector: " (pr-str facet-col)
                         ". For 2D grids use (sk/facet-grid sk col-col row-col).")
                    {:facet-col facet-col})))
  (when (and facet-row (sequential? facet-row))
    (throw (ex-info (str "Facet row must be a single keyword or string, got vector: " (pr-str facet-row)
                         ". For 2D grids use (sk/facet-grid sk col-col row-col).")
                    {:facet-row facet-row})))
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
              (let [fcol (resolve-facet-col ds :facet-col facet-col)
                    frow (resolve-facet-col ds :facet-row facet-row)]
                (for [cv (distinct (ds fcol)) rv (distinct (ds frow))]
                  (assoc view
                         :facet-col (defaults/fmt-category-label cv)
                         :facet-row (defaults/fmt-category-label rv)
                         :data (tc/select-rows ds (fn [r] (and (= (r fcol) cv) (= (r frow) rv)))))))

              nc?
              (let [fcol (resolve-facet-col ds :facet-col facet-col)]
                (for [cv (distinct (ds fcol))]
                  (assoc view
                         :facet-col (defaults/fmt-category-label cv)
                         :data (tc/select-rows ds (fn [r] (= (r fcol) cv))))))

              nr?
              (let [frow (resolve-facet-col ds :facet-row facet-row)]
                (for [rv (distinct (ds frow))]
                  (assoc view
                         :facet-row (defaults/fmt-category-label rv)
                         :data (tc/select-rows ds (fn [r] (= (r frow) rv)))))))))
        views)))))

(defn- resolve-method-info
  "Look up method info from a layer's :method key.
   Keyword -> registry lookup (throws on unknown). Map -> pass through.
   :infer -> sentinel."
  [method-key]
  (cond
    (= :infer method-key)
    {:mark :infer}

    (keyword? method-key)
    (let [m (method/lookup method-key)]
      (if m
        ;; Keep the method-key itself so downstream error messages
        ;; (e.g. in resolve.clj) can blame the lay-* function the
        ;; user called, not the internal :mark.
        (-> (select-keys m [:mark :stat :position :x-only])
            (assoc :method method-key))
        (let [registered (sort (keys (method/registered)))]
          (throw (ex-info (str "Unknown method: " method-key
                               ". Use sk/lay-* with a registered method, or "
                               "(sk/method-lookup ...) to inspect. Registered methods: "
                               (vec registered))
                          {:method method-key :registered registered})))))

    :else ;; raw map -- pass through
    method-key))

(defn- heterogeneous-types
  "If the column has :object dtype and the first 100 values have more
   than one distinct (clojure.core/type), return a sorted list of those
   type names. Otherwise nil."
  [col]
  (when (and col (= :object (dtype/elemwise-datatype col)))
    (let [sample (take 100 col)
          types (->> sample
                     (remove nil?)
                     (map type)
                     distinct)]
      (when (> (count types) 1)
        (sort (map #(.getSimpleName ^Class %) types))))))

(defn- validate-columns
  "Validate that every aesthetic column reference in the resolved mapping
   names a real column in the dataset. Covers :x, :y, :color, :size, :alpha,
   :shape, :group, :text, :ymin, :ymax, :fill. Accepts both keyword and
   string refs with cross-type matching. Also rejects columns whose values
   are heterogeneous in type (mixed numbers/strings/keywords) -- these would
   produce a Malli schema dump deeper in the pipeline.

   String values for :color are left alone: literal colors like \"red\" or
   \"#FF0000\" are legitimate. For other aesthetic keys, string values are
   treated as column refs and validated."
  [resolved d]
  (when d
    (let [col-names (set (tc/column-names d))
          col-exists? (fn [col]
                        (or (col-names col)
                            (and (keyword? col) (col-names (name col)))
                            (and (string? col) (col-names (keyword col)))))
          col-lookup (fn [col]
                       (or (get d col)
                           (and (keyword? col) (get d (name col)))
                           (and (string? col) (get d (keyword col)))))]
      (doseq [k defaults/column-keys
              :let [col (get resolved k)]
              :when (and col
                         (resolve/column-ref? col)
                         ;; :color strings are legitimate literals (color names, hex codes)
                         (not (and (= k :color) (string? col))))]
        (when-not (col-exists? col)
          (throw (ex-info (str "Column " col " (from " k ") not found in dataset. Available: " (sort col-names))
                          {:key k :column col :available (sort col-names)})))
        (when-let [types (heterogeneous-types (col-lookup col))]
          (throw (ex-info (str "Column " col " (from " k ") has mixed value types: " (vec types)
                               ". Convert it to a single type (number, string, etc.) before plotting.")
                          {:key k :column col :types types})))))))

(defn- annotation-method? [layer]
  (contains? resolve/annotation-marks (:mark (method/lookup (:method layer)))))

(defn sketch->draft
  "Flatten a sketch into a draft -- a vector of flat maps, one per
   view-layer combination, with all scope merged. The draft is the
   bridge between composable sketches and the plan pipeline.
   Reads facet/scale/coord from opts. Expands facets on views.
   Crosses views x layers: each view's own :layers union sketch :layers.
   Merges mappings downward: sketch-mapping < view-mapping < method-info < layer-mapping.
   Views with no applicable layers get {:mark :infer} for downstream inference.
   A sketch with only sketch-level annotation layers and no views gets
   one synthesized empty view so the annotations land in a panel."
  [{:keys [data mapping views layers opts]}]
  (let [;; Plot-level settings from opts
        x-scale (:x-scale opts)
        y-scale (:y-scale opts)
        coord-type (:coord opts)
        ;; Expand facets
        expanded (expand-facets views data (:facet-col opts) (:facet-row opts))
        sketch-layers layers
        sketch-mapping (or mapping {})
        ;; Annotation-only sketches (no views, only annotation layers) need
        ;; one synthesized empty view so the annotations get a panel.
        ;; synthesize-annotation-domain in plan.clj derives the domain from
        ;; the sketch's data + annotation positions.
        expanded (if (and (empty? expanded)
                          (seq sketch-layers)
                          (every? annotation-method? sketch-layers))
                   [{:mapping {} :layers []}]
                   expanded)]
    (let [idx (atom -1)]
      (vec
       (mapcat
        (fn [view]
          (let [view-idx (swap! idx inc)
                view-mapping (or (:mapping view) {})
                view-layers (:layers view)
                view-data (:data view)
                ;; Combine: view layers ∪ sketch layers
                combined (concat (or view-layers nil) sketch-layers)
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
                         d (ensure-dataset (or (:data layer) view-data data))
                         ;; Sketch-scope marker: annotations coming from
                         ;; sketch :layers get duplicated by the view cross-
                         ;; product. Tag them so plan.clj can dedupe.
                         sketch-scope? (some #(identical? % layer) sketch-layers)]
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
                          (:facet-row view) (assoc :facet-row (:facet-row view))
                          sketch-scope? (assoc :__sketch-scope true))
                         ;; Mark inference: remove :mark/:stat so downstream infers
                         (cond-> (= :infer (:mark resolved))
                           (-> (dissoc :mark :stat))))))
                 applicable)))
        expanded)))))

;; ---- Rendering ----

(defn render-sketch
  "Render a sketch via the grid pipeline.
   Called by Clay via kind/fn at display time.
   When opts contain :format :bufimg, renders to BufferedImage (via
   membrane's Java2D backend) instead of SVG. Clay displays BufferedImage
   values as inline images automatically.

   The SVG case returns the raw kind/hiccup [:svg ...] directly.
   An earlier version wrapped it in a [:div ...] for inter-plot spacing
   in interactive HTML, but that wrapper prevented Clay's GFM renderer
   from recognizing the SVG and extracting it to a file, so bare sketches
   rendered blank in :format [:gfm]. Spacing is now handled downstream by
   the consumer / stylesheet."
  [sk]
  (let [opts (:opts sk {})
        fmt (or (:format opts) :svg)
        draft (sketch->draft sk)
        p (plan/draft->plan draft opts)]
    (if (= fmt :bufimg)
      (do
        ;; Ensure the bufimg renderer is loaded
        (require 'scicloj.napkinsketch.render.bufimg)
        (render-impl/plan->figure p :bufimg opts))
      (render-impl/plan->figure p :svg opts))))

;; ---- Constructor ----

(defn ->sketch
  "Create a sketch annotated with kind/fn for auto-rendering.
   The sketch record stays clean; Clay's :kindly/f lives in the
   kindly-options metadata channel. A with-config snapshot is
   captured in a closure so restoration happens at render time
   without mutating the sketch."
  [data mapping views layers opts]
  (let [sk (->Sketch data mapping views layers opts)
        captured defaults/*config*]
    (kind/fn sk
      {:kindly/f (if captured
                   (fn [sk]
                     (binding [defaults/*config* captured]
                       (render-sketch sk)))
                   #'render-sketch)})))
