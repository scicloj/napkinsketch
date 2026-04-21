(ns scicloj.napkinsketch.api
  "Public API for napkinsketch -- composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.impl.sketch :as sketch]
            [scicloj.napkinsketch.impl.plan :as plan]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.extract :as extract]
            [scicloj.napkinsketch.impl.position :as position]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.render.membrane :as membrane]
            [scicloj.napkinsketch.render.mark :as mark]
            [scicloj.napkinsketch.render.svg :as svg]
            [scicloj.napkinsketch.method :as method]
            [clojure.string :as str]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Type predicates ----

(defn plan?
  "Return true if x is a plan (the resolved data-space geometry from sk/plan)."
  [x]
  (resolve/plan? x))

(defn layer?
  "Return true if x is a layer (resolved geometry for one mark in a plan)."
  [x]
  (resolve/layer? x))

(defn method?
  "Return true if x is a method (mark + stat + position bundle from the registry)."
  [x]
  (resolve/method? x))

(defn- expect-type
  "Validate that x is of the expected type. Throws with helpful message if not."
  [x pred expected-name fn-name]
  (when-not (pred x)
    (throw (ex-info (str fn-name " expects a " expected-name ". "
                         (cond (resolve/plan? x) "Got a plan."
                               :else (str "Got: " (type x) ".")))
                    {:function fn-name :expected expected-name :got-type (str (type x))}))))

;; ---- Configuration ----

(defmacro with-config
  "Execute body with thread-local config overrides.
   Overrides take precedence over set-config! and defaults,
   but plot options still win.
   (with-config {:theme {:bg \"#FFF\"}} (plot ...))"
  [config-map & body]
  `(binding [defaults/*config* ~config-map]
     ~@body))

(defn config
  "Return the effective resolved configuration as a map.
   Merges: library defaults < napkinsketch.edn < set-config! < *config*.
   Useful for inspecting which values are in effect.
   (config)  -- show current resolved config"
  []
  (defaults/config))

(def config-key-docs
  "Documentation metadata for configuration keys.
   Maps each config key to [category description].
   Use with (sk/config) to build reference tables."
  defaults/config-key-docs)

(def plot-option-docs
  "Documentation for plot-level option keys.
   These are accepted by sk/options, sk/plan, and sk/plot but are
   inherently per-plot (text content or nested config override).
   Maps each key to [category description]."
  defaults/plot-option-docs)

(def layer-option-docs
  "Documentation for layer option keys accepted by lay- functions.
   Maps each key to a description string."
  method/layer-option-docs)

(defn set-config!
  "Set global config overrides. Persists across calls until reset.
   (set-config! {:palette :dark2 :theme {:bg \"#FFFFFF\"}})
   (set-config! nil)  -- reset to defaults"
  [m]
  (defaults/set-config! m))

(defn method-lookup
  "Look up a registered method by keyword. Returns the method map
   (with :mark, :stat, :position, :doc), or nil if not found.
   (method-lookup :histogram) => {:mark :bar, :stat :bin, ...}"
  [k]
  (method/lookup k))

(defn registered-methods
  "Return all registered methods as a map of keyword -> method map.
   Useful for generating documentation tables."
  []
  (method/registered))

(defn mark-doc
  "Return the prose description for a mark keyword.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (mark-doc :point) => \"Filled circle\""
  [k]
  (try
    (let [r (extract/extract-layer {:mark [k :doc]} nil nil nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

(defn stat-doc
  "Return the prose description for a stat keyword.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (stat-doc :bin) => \"Bin numerical values into ranges\""
  [k]
  (try
    (let [r (stat/compute-stat {:stat [k :doc]})]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

(defn position-doc
  "Return the prose description for a position keyword.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (position-doc :dodge) => \"Shift groups side-by-side within a band\""
  [k]
  (try
    (let [r (position/apply-position [k :doc] nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

(defn membrane-mark-doc
  "Return the prose description for how a mark renders to membrane drawables.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (membrane-mark-doc :point) => \"Translated colored rounded-rectangles\""
  [k]
  (try
    (let [r (mark/layer->membrane {:mark [k :doc]} nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

(defn scale-doc
  "Return the prose description for a scale keyword.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (scale-doc :linear) => \"Continuous linear mapping\""
  [k]
  (try
    (let [r (scale/make-scale [k :doc] nil nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

(defn coord-doc
  "Return the prose description for a coordinate type keyword.
   Returns \"(no description)\" if no [:key :doc] defmethod is registered.
   (coord-doc :polar) => \"Radial mapping: x->angle, y->radius\""
  [k]
  (try
    (let [r (coord/make-coord [k :doc] nil nil nil nil nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (resolve/cross xs ys))

;; ---- Pipeline Internals ----

(defn plan->membrane
  "Convert a plan into a membrane drawable tree.
   (plan->membrane (plan sketch))"
  [plan-data & {:as opts}]
  (expect-type plan-data resolve/plan? "plan (from sk/plan)" "sk/plan->membrane")
  (membrane/plan->membrane plan-data opts))

(defn membrane->figure
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on format keyword; :svg is always available.
   (membrane->figure (plan->membrane (plan views)) :svg {})"
  [membrane-tree format opts]
  (render-impl/membrane->figure membrane-tree format opts))

(defn plan->figure
  "Convert a plan into a figure for the given format.
   Dispatches on format keyword. Each renderer is a separate namespace
   that registers a defmethod; :svg is always available.
   (plan->figure (plan sketch) :svg {})
   (plan->figure (plan sketch) :plotly {})"
  [plan format opts]
  (expect-type plan resolve/plan? "plan (from sk/plan)" "sk/plan->figure")
  (render-impl/plan->figure plan format opts))

;; ---- Plan Validation ----

(defn valid-plan?
  "Check if a plan conforms to the Malli schema.
   (valid-plan? (plan views))  -- true if valid"
  [plan]
  (ss/valid? plan))

(defn explain-plan
  "Explain why a plan does not conform to the Malli schema.
   Returns nil if valid, or a Malli explanation map if invalid.
   (explain-plan (plan views))"
  [plan]
  (ss/explain plan))

;; ---- API ----

(defn- wrap-autorender
  "Wrap a sketch with kind/fn for auto-rendering in Clay."
  [sk]
  (kind/fn (cond-> (assoc sk :kindly/f #'sketch/render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))

(defn- coerce-dataset
  "Coerce data to a tablecloth dataset. Returns nil for nil.
   Rejects Sketch records to prevent silent coercion to a bogus
   6-column dataset made of the sketch's own fields."
  [d]
  (when d
    (cond
      (sketch/sketch? d)
      (throw (ex-info (str ":data must be a dataset or map of columns, not a sketch. "
                           "Pass tabular data (dataset, map of columns, or row maps), "
                           "or remove the :data override.")
                      {:data-type 'Sketch}))
      (tc/dataset? d) d
      :else (tc/dataset d))))

(defn- ensure-sk
  "Coerce first arg to a sketch if it isn't one already.
   Data is eagerly coerced to a dataset so downstream code can
   uniformly use tc/column-names."
  [x]
  (cond
    (sketch/sketch? x) x
    (or (tc/dataset? x)
        (map? x)
        (sequential? x)) (wrap-autorender
                          (sketch/->sketch (coerce-dataset x) {} [] [] {}))
    :else (wrap-autorender (sketch/->sketch nil {} [] [] {}))))

(defn sketch?
  "Return true if x is a sketch."
  [x]
  (sketch/sketch? x))

(def ^:private view-mapping-keys
  "Keys accepted in view/sketch mapping options."
  (into defaults/column-keys #{:data :color-type :x-type :y-type}))

(def ^:private plot-options-keys
  "Keys accepted by sk/options (top-level only; nested theme/config keys
   are validated separately by deep-merge)."
  (into (set (keys defaults/plot-option-docs))
        (keys defaults/config-key-docs)))

(defn- warn-and-strip-unknown-opts
  "Warn if `opts` contains keys outside `accepted` and return `opts`
   with only the accepted keys retained. `caller` is used in the
   warning message (e.g. \"sk/sketch\", \"lay-point\"). If opts is
   nil or not a map, returns it unchanged. Stripping makes the
   warning honest: keys the library does not recognize are dropped
   rather than silently propagated into mapping maps, where they
   could leak into downstream resolution."
  [caller opts accepted]
  (if-not (and (map? opts) (seq opts))
    opts
    (let [unknown (remove accepted (keys opts))]
      (when (seq unknown)
        (println (str "Warning: " caller
                      " does not recognize option(s): " (vec unknown)
                      ". Accepted: " (vec (sort accepted)))))
      (if (seq unknown)
        (select-keys opts (filter accepted (keys opts)))
        opts))))

(defn sketch
  "Create or augment a sketch with an optional sketch-level mapping.
   Use for sketch-level aesthetics that apply to all views and layers.

   (sketch)                          -- empty sketch
   (sketch data)                     -- sketch with data only
   (sketch data {:color :species})   -- sketch with sketch-level color mapping
   (sketch existing-sketch {:color :c}) -- merge mapping into existing sketch
                                          (preserves :views/:layers/:opts)"
  ([] (wrap-autorender (sketch/->sketch nil {} [] [] {})))
  ([data] (sketch data {}))
  ([data mapping]
   ;; sk/sketch's mapping is for *appearance* aesthetics (color/size/etc.) that
   ;; flow into all views; it does not create a view. Reject :x/:y here so a
   ;; ggplot2-style `(ggplot data, aes(x, y))` pattern fails loudly instead of
   ;; producing a 0-panel plot.
   (when (or (:x mapping) (:y mapping))
     (throw (ex-info (str "sk/sketch does not create a view -- :x and :y are not allowed "
                          "in its mapping. Use (sk/view sk :x :y) to declare position "
                          "mappings, or (sk/lay-point sk :x :y) for a one-shot view+layer.")
                     {:mapping mapping})))
   (let [mapping (warn-and-strip-unknown-opts "sk/sketch" mapping view-mapping-keys)]
     (if (sketch/sketch? data)
       ;; Merge new mapping into existing sketch, preserving views/layers/opts
       (update data :mapping merge mapping)
       ;; Fresh sketch from raw data (or nil)
       (wrap-autorender
        (sketch/->sketch (coerce-dataset data) mapping [] [] {}))))))

(def ^:private position-aesthetic-keys
  "Mapping keys whose keyword values are always column references
   (no literal-value path). Used by with-data's attach-time
   validation. :color / :size / :alpha keyword values are also
   column refs, but the validator ignores string values since
   those are ambiguous (\"red\" might be a CSS color)."
  [:x :y :color :size :alpha :group :text :ymin :ymax :fill :shape])

(defn- column-refs-in-mapping [m]
  (keep #(let [v (get m %)]
           (when (keyword? v) v))
        position-aesthetic-keys))

(defn- column-refs-in-sketch
  "Collect every keyword column reference used by the sketch's
   mapping, views, layers, and facet options. Returns a distinct
   sequence of keywords."
  [sk]
  (let [views (:views sk)
        from-views (mapcat (fn [v]
                             (concat (column-refs-in-mapping (:mapping v))
                                     (mapcat #(column-refs-in-mapping (:mapping %))
                                             (:layers v))))
                           views)
        from-layers (mapcat #(column-refs-in-mapping (:mapping %))
                            (:layers sk))
        facet-refs (keep #(when (keyword? (get-in sk [:opts %]))
                            (get-in sk [:opts %]))
                         [:facet-col :facet-row])]
    (distinct (concat (column-refs-in-mapping (:mapping sk))
                      from-views
                      from-layers
                      facet-refs))))

(defn- validate-columns-present
  "Throw a helpful error if any of `refs` is absent from the
   dataset's column-name set. Tolerates keyword/string mismatches
   the same way resolve-col-name does."
  [refs ds]
  (let [cols (set (tc/column-names ds))
        matches? (fn [r]
                   (or (contains? cols r)
                       (and (keyword? r) (contains? cols (name r)))
                       (and (string? r) (contains? cols (keyword r)))))
        missing (vec (remove matches? refs))]
    (when (seq missing)
      (throw (ex-info (str "Cannot attach data: sketch references column(s) "
                           missing
                           " not present in the dataset. Available columns: "
                           (vec (sort cols)) ".")
                      {:missing missing :available (vec (sort cols))})))))

(defn with-data
  "Supply or replace the sketch-level dataset on a sketch. Useful
   for building a template once and applying it to different
   datasets:

       (def template (-> (sk/sketch)
                         (sk/view :x :y {:color :group})
                         sk/lay-point
                         sk/lay-lm))

       (-> template (sk/with-data my-data))
       (-> template (sk/with-data other-data))

   At attach time, every keyword column reference in the sketch's
   mapping, views, layers, and facet options must exist in the
   dataset -- otherwise an error is thrown naming the missing
   columns and listing what is available. Per-view / per-layer
   `:data` still overrides the sketch-level data.
   (with-data sk data)"
  [sk data]
  (let [sk (ensure-sk sk)
        ds (coerce-dataset data)]
    (when ds
      (validate-columns-present (column-refs-in-sketch sk) ds))
    (assoc sk :data ds)))

(defn- check-facet-keys
  "Throw a helpful error if a mapping or layer-options map contains
   :facet-col / :facet-row / :facet-x / :facet-y. Faceting is
   plot-level and is set via sk/facet or sk/facet-grid, never via a
   view or layer options map -- the silent-strip behaviour on such
   keys confused users (user-report-2 Issue 5)."
  [context m]
  (let [fk (select-keys m [:facet-col :facet-row :facet-x :facet-y])]
    (when (seq fk)
      (throw (ex-info (str "Faceting is plot-level, not " context "-level. "
                           "Use (sk/facet sk col) or (sk/facet-grid sk col-col row-col) "
                           "instead of putting "
                           (str/join " / " (map name (keys fk)))
                           " in a " context "'s options map.")
                      fk)))))

(defn- make-view
  "Build a view map from a mapping, extracting :data if present."
  [mapping]
  (check-facet-keys "view" mapping)
  (let [mapping (warn-and-strip-unknown-opts "sk/view" mapping view-mapping-keys)
        d (:data mapping)]
    (cond-> {:mapping (dissoc mapping :data)}
      d (assoc :data (coerce-dataset d)))))

(defn view
  "Declare what to look at -- add a view with position mappings.
   Opts scope to this view (not to all views). For sketch-level
   mappings, use sk/sketch.
   (view data :x :y)             -- one bivariate view
   (view data :x)                -- one univariate view
   (view data [[:a :b] [:c :d]]) -- multiple bivariate views
   (view data :x :y {:color :g}) -- view with color mapping (view-level)
   (view data {:x :a :y :b :color :c}) -- view from map (view-level)
   (view sk)                     -- auto-infer columns from small dataset"
  ([sk-or-data]
   (let [sk (ensure-sk sk-or-data)]
     (if (:data sk)
       (let [cols (vec (tc/column-names (:data sk)))
             n (count cols)]
         (case n
           1 (update sk :views conj {:mapping {:x (cols 0)}})
           2 (update sk :views conj {:mapping {:x (cols 0) :y (cols 1)}})
           3 (update sk :views conj {:mapping {:x (cols 0) :y (cols 1) :color (cols 2)}})
           (throw (ex-info (str "Cannot infer columns from " n " columns.") {:columns cols}))))
       sk)))
  ([sk-or-data x-or-cols]
   (let [sk (ensure-sk sk-or-data)]
     (cond
       (or (keyword? x-or-cols)
           (string? x-or-cols))
       (update sk :views conj {:mapping {:x x-or-cols}})

       (map? x-or-cols)
       ;; Map form: all keys go into view mapping (extract :data if present)
       (update sk :views conj (make-view x-or-cols))

       (sequential? x-or-cols)
       (let [first-el (first x-or-cols)]
         (if (or (keyword? first-el) (string? first-el))
           ;; Vector of keywords -> univariate views: [:a :b :c]
           (update sk :views into (mapv (fn [col] {:mapping {:x col}}) x-or-cols))
           ;; Vector of pairs -> bivariate views: [[:x1 :y1] [:x2 :y2]]
           (update sk :views into (mapv (fn [[x y]] {:mapping {:x x :y y}}) x-or-cols)))))))
  ([sk-or-data x y]
   (let [sk (ensure-sk sk-or-data)]
     (cond
       ;; Columns/pairs + view opts: (view sk [:a :b :c] {:color :species})
       (and (sequential? x) (map? y))
       (let [first-el (first x)]
         (if (or (keyword? first-el) (string? first-el))
           (update sk :views into (mapv (fn [col] (make-view (assoc y :x col))) x))
           (update sk :views into (mapv (fn [[a b]] (make-view (merge y {:x a :y b}))) x))))
       ;; Single column + view opts: (view data :x {:color :species})
       (map? y)
       (update sk :views conj (make-view (assoc y :x x)))
       ;; Two columns: (view data :x :y)
       :else
       (update sk :views conj {:mapping {:x x :y y}}))))
  ([sk-or-data x y opts]
   (let [sk (ensure-sk sk-or-data)]
     ;; Opts merge into this view's mapping (view-level scope)
     (update sk :views conj (make-view (merge opts {:x x :y y}))))))

(defn- col-key
  "Canonicalize a column reference to a string key for view matching.
   `:x` and `\"x\"` are semantically the same column, so matching should
   treat them as equal. Returns nil for nil (x-only cases)."
  [col]
  (cond
    (nil? col) nil
    (keyword? col) (name col)
    :else (str col)))

(defn- add-view-layer
  "Add a layer to a specific view (found by matching position mappings, or created new).
   Matches the *last* view with the same :x/:y in its :mapping, so that
   `lay-*` naturally targets the most recently created view.
   Matching is keyword/string tolerant — `:x` matches `\"x\"`.
   Used when lay-* is called with positional columns."
  [sk position-mapping layer]
  (let [views (:views sk)
        px (col-key (:x position-mapping))
        py (col-key (:y position-mapping))
        idx (last (keep-indexed
                   (fn [i v]
                     (when (and (= (col-key (:x (:mapping v))) px)
                                (= (col-key (:y (:mapping v))) py))
                       i))
                   views))]
    (if idx
      ;; Found existing view -- append layer to its :layers
      (update-in sk [:views idx :layers]
                 (fn [ls] (conj (or ls []) layer)))
      ;; No match -- create new view with this layer
      (update sk :views conj {:mapping position-mapping :layers [layer]}))))

(defn- check-position-mapping
  "Throw a helpful error if :x or :y in a layer's options is a
   non-column-reference value (e.g. a scalar number). Positions
   must be column references (keyword or string); fixed scalars are
   a common mistake from annotation-style usage and previously
   produced an opaque ClassCastException deep in the stat pipeline
   (user-report-2 Issue 3)."
  [context opts]
  (doseq [k [:x :y]]
    (when-let [v (get opts k)]
      (when-not (or (keyword? v) (string? v))
        (throw (ex-info (str context " " k " must be a column reference "
                             "(keyword or string), but got "
                             (pr-str v) ". For a constant position, add a "
                             "column to :data with that value, e.g. "
                             "`(tc/add-column data " k " (constantly "
                             (pr-str v) "))` and pass "
                             k " "
                             (pr-str (keyword (name k))) ".")
                        {:option k :value v}))))))

(defn- build-layer
  "Build a layer map from a method-key and optional opts.
   Extracts :data if present. Warns and strips unrecognized option keys."
  [method-key opts]
  (when opts
    (check-facet-keys "layer" opts)
    (check-position-mapping (str "lay-" (name method-key)) opts))
  (let [opts (if (and opts (keyword? method-key))
               (let [reg (method/lookup method-key)
                     accepted (into (set method/universal-layer-options)
                                    (:accepts reg))]
                 (warn-and-strip-unknown-opts (str "lay-" (name method-key))
                                              opts accepted))
               opts)
        d (:data opts)]
    (cond-> {:method method-key
             :mapping (dissoc (or opts {}) :data)}
      d (assoc :data (coerce-dataset d)))))

(defn lay
  "Add a sketch-level layer (applies to all views)."
  ([sk-or-data method-key]
   (let [sk (ensure-sk sk-or-data)]
     (update sk :layers conj (build-layer method-key nil))))
  ([sk-or-data method-key opts]
   (let [sk (ensure-sk sk-or-data)]
     (update sk :layers conj (build-layer method-key opts)))))

(defn- x-only?
  "True if method-key is registered as x-only (rejects :y column)."
  [method-key]
  (:x-only (method/lookup method-key)))

(defn- lay-method
  "Shared implementation for all lay-* functions.
   1-arity: auto-infer columns for small datasets, otherwise sketch-level layer.
   2-arity: keyword/string -> view-specific; vector -> view-specific per column;
            map -> sketch-level with opts.
   3-arity: two keywords -> bivariate view-specific; keyword+map -> univariate+opts;
            vector+map -> view-specific per column with opts.
   4-arity: bivariate view-specific with opts."
  ([method-key sk-or-data]
   (let [sk (ensure-sk sk-or-data)
         d (:data sk)
         col-count (when d (count (tc/column-names d)))
         fresh? (and (empty? (:views sk)) (empty? (:layers sk)))]
     (cond
       ;; Fresh sketch, small dataset -> auto-infer columns
       (and fresh? d (<= col-count 3))
       (let [sk2 (view sk)]
         (add-view-layer sk2
                         (:mapping (first (:views sk2)))
                         (build-layer method-key nil)))

       ;; Fresh sketch, 4+ columns -> reject with a clear error (was: silent empty plot).
       ;; This only fires on the very first lay-* call on a fresh sketch; the
       ;; "lay-first, view-later" pattern keeps working because it has layers
       ;; by the time subsequent 1-arity lay-* calls arrive.
       (and fresh? d (> col-count 3))
       (throw (ex-info (str "Cannot auto-infer columns from " col-count " columns. "
                            "Pass explicit x and y: (sk/lay-" (name method-key)
                            " data :x :y). Available columns: "
                            (sort (tc/column-names d)))
                       {:method method-key
                        :column-count col-count
                        :columns (sort (tc/column-names d))}))

       ;; Has views, has sketch-level layers, or no data: sketch-level layer
       :else
       (lay sk method-key))))
  ([method-key sk-or-data x-or-opts]
   (cond
     (or (keyword? x-or-opts) (string? x-or-opts))
     (add-view-layer (ensure-sk sk-or-data)
                     {:x x-or-opts}
                     (build-layer method-key nil))
     ;; Sequential -> create view-specific layers (not global)
     (sequential? x-or-opts)
     (reduce (fn [sk col-or-pair]
               (if (sequential? col-or-pair)
                 (let [[x y] col-or-pair]
                   (add-view-layer sk {:x x :y y} (build-layer method-key nil)))
                 (add-view-layer sk {:x col-or-pair} (build-layer method-key nil))))
             (ensure-sk sk-or-data)
             x-or-opts)
     :else
     (lay sk-or-data method-key x-or-opts)))
  ([method-key sk-or-data x y-or-opts]
   (cond
     (or (keyword? y-or-opts) (string? y-or-opts))
     (do (when (x-only? method-key)
           (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                           {:method method-key :x x :y y-or-opts})))
         (add-view-layer (ensure-sk sk-or-data)
                         {:x x :y y-or-opts}
                         (build-layer method-key nil)))
     ;; Parallel vectors -> bivariate view-specific layers per (x_i, y_i) pair.
     ;; Supports (lay-point data [:x1 :x2] [:y1 :y2]) which previously
     ;; ClassCastException-d in build-layer.
     (and (sequential? x) (sequential? y-or-opts))
     (reduce (fn [sk [a b]]
               (add-view-layer sk {:x a :y b} (build-layer method-key nil)))
             (ensure-sk sk-or-data)
             (map vector x y-or-opts))
     ;; Sequential + opts -> view-specific layers with opts
     (and (sequential? x) (map? y-or-opts))
     (reduce (fn [sk col-or-pair]
               (if (sequential? col-or-pair)
                 (let [[a b] col-or-pair]
                   (add-view-layer sk {:x a :y b} (build-layer method-key y-or-opts)))
                 (add-view-layer sk {:x col-or-pair} (build-layer method-key y-or-opts))))
             (ensure-sk sk-or-data)
             x)
     :else
     (add-view-layer (ensure-sk sk-or-data)
                     {:x x}
                     (build-layer method-key y-or-opts))))
  ([method-key sk-or-data x y opts]
   (when (x-only? method-key)
     (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                     {:method method-key :x x :y y})))
   (add-view-layer (ensure-sk sk-or-data)
                   {:x x :y y}
                   (build-layer method-key opts))))

(defn lay-point
  "Add :point layer (scatter) to a sketch.
   Without columns -> sketch-level layer (applies to all views).
   With columns -> view-specific (find or create view).
   (lay-point sk)                         -- sketch-level layer
   (lay-point sk {:color :species})        -- sketch-level with opts
   (lay-point data :x :y)                 -- view-specific
   (lay-point data :x :y {:color :c})     -- view-specific with opts"
  ([sk-or-data] (lay-method :point sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :point sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :point sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :point sk-or-data x y opts)))

(defn lay-rule-h
  "Add :rule-h layer -- horizontal reference line at y = intercept.
   Position comes from opts (not data columns).
   (lay-rule-h sk {:intercept 3})           -- sketch-level, all panels
   (lay-rule-h sk :x :y {:intercept 3})     -- view-specific (columns pick the view)
   (lay-rule-h sk {:intercept 3 :color \"red\" :alpha 0.5})"
  ([sk-or-data] (lay-method :rule-h sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :rule-h sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :rule-h sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :rule-h sk-or-data x y opts)))

(defn lay-rule-v
  "Add :rule-v layer -- vertical reference line at x = intercept.
   Position comes from opts (not data columns).
   (lay-rule-v sk {:intercept 5})           -- sketch-level, all panels
   (lay-rule-v sk :x :y {:intercept 5})     -- view-specific (columns pick the view)
   (lay-rule-v sk {:intercept 5 :color \"red\" :alpha 0.5})"
  ([sk-or-data] (lay-method :rule-v sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :rule-v sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :rule-v sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :rule-v sk-or-data x y opts)))

(defn lay-band-h
  "Add :band-h layer -- horizontal shaded band between y = lo and y = hi.
   Position comes from opts (not data columns).
   (lay-band-h sk {:lo 2 :hi 4})            -- sketch-level, all panels
   (lay-band-h sk :x :y {:lo 2 :hi 4})      -- view-specific (columns pick the view)
   (lay-band-h sk {:lo 2 :hi 4 :color \"blue\" :alpha 0.3})"
  ([sk-or-data] (lay-method :band-h sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :band-h sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :band-h sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :band-h sk-or-data x y opts)))

(defn lay-band-v
  "Add :band-v layer -- vertical shaded band between x = lo and x = hi.
   Position comes from opts (not data columns).
   (lay-band-v sk {:lo 4 :hi 6})            -- sketch-level, all panels
   (lay-band-v sk :x :y {:lo 4 :hi 6})      -- view-specific (columns pick the view)
   (lay-band-v sk {:lo 4 :hi 6 :color \"blue\" :alpha 0.3})"
  ([sk-or-data] (lay-method :band-v sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :band-v sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :band-v sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :band-v sk-or-data x y opts)))

(defn lay-line
  "Add :line method -- connected line through data points.
   Requires x (numerical) and y (numerical).
   Accepts :color, :alpha, :size (stroke width), :nudge-x, :nudge-y."
  ([sk-or-data] (lay-method :line sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :line sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :line sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :line sk-or-data x y opts)))

(defn lay-step
  "Add :step method -- staircase line (horizontal then vertical).
   Requires x and y (both numerical)."
  ([sk-or-data] (lay-method :step sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :step sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :step sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :step sk-or-data x y opts)))

(defn lay-area
  "Add :area method -- filled region between y and the baseline.
   Requires x and y (both numerical). Accepts :color, :alpha."
  ([sk-or-data] (lay-method :area sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :area sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :area sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :area sk-or-data x y opts)))

(defn lay-stacked-area
  "Add :stacked-area method -- areas stacked on top of each other.
   Requires x, y, and :color (for grouping). Groups stack vertically."
  ([sk-or-data] (lay-method :stacked-area sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-area sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-area sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-area sk-or-data x y opts)))

(defn lay-histogram
  "Add :histogram method -- bin numerical values into bars.
   X-only: pass one column. Accepts :bins (count), :binwidth, :color,
   :normalize (:density for density-normalized heights)."
  ([sk-or-data] (lay-method :histogram sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :histogram sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :histogram sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :histogram sk-or-data x y opts)))

(defn lay-bar
  "Add :bar method -- count occurrences of each category.
   X-only: pass one categorical column. Accepts :color for grouped bars."
  ([sk-or-data] (lay-method :bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :bar sk-or-data x y opts)))

(defn lay-stacked-bar
  "Add :stacked-bar method -- bars stacked by color group.
   X-only with :color. Heights represent counts per category per group."
  ([sk-or-data] (lay-method :stacked-bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-bar sk-or-data x y opts)))

(defn lay-stacked-bar-fill
  "Add :stacked-bar-fill method -- 100% stacked bars (proportional).
   Same as stacked-bar but normalized so each bar totals 100%."
  ([sk-or-data] (lay-method :stacked-bar-fill sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-bar-fill sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-bar-fill sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-bar-fill sk-or-data x y opts)))

(defn lay-value-bar
  "Add :value-bar method -- bars with pre-computed heights.
   Requires categorical x and numerical y. Unlike :bar (which counts),
   :value-bar uses the y value directly as the bar height."
  ([sk-or-data] (lay-method :value-bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :value-bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :value-bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :value-bar sk-or-data x y opts)))

(defn lay-lm
  "Add :lm method -- linear regression line.
   Requires x and y (both numerical). Accepts {:se true} for a
   95% confidence band around the fit."
  ([sk-or-data] (lay-method :lm sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :lm sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :lm sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :lm sk-or-data x y opts)))

(defn lay-loess
  "Add :loess method -- local regression (LOESS) smooth curve.
   Requires x and y (both numerical). Accepts {:se true} for a
   confidence band, {:bandwidth 0.5} for smoothing control."
  ([sk-or-data] (lay-method :loess sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :loess sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :loess sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :loess sk-or-data x y opts)))

(defn lay-density
  "Add :density method -- kernel density estimate curve.
   X-only: pass one numerical column. Accepts :color, :bandwidth."
  ([sk-or-data] (lay-method :density sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :density sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :density sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :density sk-or-data x y opts)))

(defn lay-tile
  "Add :tile method -- colored grid cells (heatmap).
   With :fill option: pre-computed tile colors from a column.
   Without :fill: auto-binned 2D histogram (stat :bin2d)."
  ([sk-or-data] (lay-method :tile sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :tile sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :tile sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :tile sk-or-data x y opts)))

(defn lay-density2d
  "Add :density2d method -- 2D kernel density heatmap.
   Requires x and y (both numerical). Produces a smoothed density
   surface as colored tiles with a continuous gradient legend."
  ([sk-or-data] (lay-method :density2d sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :density2d sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :density2d sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :density2d sk-or-data x y opts)))

(defn lay-contour
  "Add :contour method -- iso-density contour lines from 2D KDE.
   Requires x and y (both numerical). Accepts {:levels 10} for
   the number of contour levels."
  ([sk-or-data] (lay-method :contour sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :contour sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :contour sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :contour sk-or-data x y opts)))

(defn lay-boxplot
  "Add :boxplot method -- box-and-whisker plot.
   Requires categorical x and numerical y. Shows median, quartiles,
   whiskers, and outliers. Accepts :color for grouped boxplots."
  ([sk-or-data] (lay-method :boxplot sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :boxplot sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :boxplot sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :boxplot sk-or-data x y opts)))

(defn lay-violin
  "Add :violin method -- mirrored density estimate by category.
   Requires categorical x and numerical y. Accepts :color, :bandwidth."
  ([sk-or-data] (lay-method :violin sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :violin sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :violin sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :violin sk-or-data x y opts)))

(defn lay-ridgeline
  "Add :ridgeline method -- stacked density curves by category.
   Requires categorical x and numerical y. Categories stack vertically
   with density curves rendered horizontally."
  ([sk-or-data] (lay-method :ridgeline sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :ridgeline sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :ridgeline sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :ridgeline sk-or-data x y opts)))

(defn lay-summary
  "Add :summary method -- mean ± standard error per category.
   Requires categorical x and numerical y. Shows a point at the mean
   with error bars for ± 1 SE. Accepts :color for grouped summaries."
  ([sk-or-data] (lay-method :summary sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :summary sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :summary sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :summary sk-or-data x y opts)))

(defn lay-errorbar
  "Add :errorbar method -- vertical error bars from pre-computed bounds.
   Requires x, y, and {:ymin :col :ymax :col} for lower/upper bounds."
  ([sk-or-data] (lay-method :errorbar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :errorbar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :errorbar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :errorbar sk-or-data x y opts)))

(defn lay-lollipop
  "Add :lollipop method -- dot on a stem from the baseline.
   Requires categorical x and numerical y. Like value-bar but with
   a circle+line instead of a filled rectangle."
  ([sk-or-data] (lay-method :lollipop sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :lollipop sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :lollipop sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :lollipop sk-or-data x y opts)))

(defn lay-text
  "Add :text method -- text labels at data coordinates.
   Requires x, y, and {:text :column} for label content."
  ([sk-or-data] (lay-method :text sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :text sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :text sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :text sk-or-data x y opts)))

(defn lay-label
  "Add :label method -- text labels with background box at data coordinates.
   Like :text but with a rectangular background for readability."
  ([sk-or-data] (lay-method :label sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :label sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :label sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :label sk-or-data x y opts)))

(defn lay-rug
  "Add :rug method -- short tick marks along the axis showing individual values.
   X-only: pass one column. Often layered with density or scatter."
  ([sk-or-data] (lay-method :rug sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :rug sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :rug sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :rug sk-or-data x y opts)))

(defn facet
  "Facet a sketch by a column.
   Direction is :col (default, horizontal row) or :row (vertical column).
   Faceting is plot-level -- all views are faceted the same way."
  ([sk col] (facet sk col :col))
  ([sk col direction]
   (let [sk (ensure-sk sk)
         k (case direction :col :facet-col :row :facet-row)]
     (update sk :opts assoc k col))))

(defn facet-grid
  "Facet a sketch by two columns (2D grid).
   Faceting is plot-level -- all views are faceted the same way."
  [sk col-col row-col]
  (let [sk (ensure-sk sk)]
    (update sk :opts assoc :facet-col col-col :facet-row row-col)))

(defn- deep-merge
  "Recursively merge maps. Non-map values are overwritten."
  [a b]
  (if (and (map? a) (map? b))
    (merge-with deep-merge a b)
    b))

(defn options
  "Set plot-level options (title, labels, width, height, etc.).
   Nested maps (e.g. :theme) are deep-merged."
  [sk opts]
  (let [opts (warn-and-strip-unknown-opts "sk/options" opts plot-options-keys)]
    (doseq [k [:width :height]
            :let [v (get opts k)]
            :when v]
      (when-not (and (number? v) (pos? v))
        (throw (ex-info (str k " must be a positive number, got: " (pr-str v))
                        {:option k :value v}))))
    (update (ensure-sk sk) :opts deep-merge opts)))

(def ^:private valid-scale-types
  "Scale types accepted by sk/scale. :linear and :log are the two
   continuous types; :categorical lets users supply an explicit ordering
   via a :domain spec for categorical axes."
  #{:linear :log :categorical})

(defn scale
  "Set axis scale on a sketch. Scale is plot-level -- applies to all views.
   (scale sk :x :log)                                -- log scale on x-axis
   (scale sk :x {:type :categorical :domain [...]})  -- explicit category order"
  [sk channel scale-type]
  (let [sk (ensure-sk sk)
        k (case channel :x :x-scale :y :y-scale
                (throw (ex-info (str "Scale channel must be :x or :y, got: " channel)
                                {:channel channel})))
        type-kw (if (map? scale-type) (:type scale-type) scale-type)]
    (when-not (or (nil? type-kw) (valid-scale-types type-kw))
      (throw (ex-info (str "Unknown scale type: " type-kw
                           ". Supported: " (vec (sort valid-scale-types)))
                      {:scale-type type-kw :supported (vec (sort valid-scale-types))})))
    (update sk :opts assoc k (if (map? scale-type)
                               (merge {:type :linear} scale-type)
                               {:type scale-type}))))

(defn coord
  "Set coordinate transform on a sketch. Coord is plot-level -- applies to all views.
   (coord sk :flip) -- flipped coordinates."
  [sk coord-type]
  (when-not (#{:cartesian :flip :polar :fixed} coord-type)
    (throw (ex-info (str "Coordinate must be :cartesian, :flip, :polar, or :fixed, got: " coord-type)
                    {:coord coord-type})))
  (let [sk (ensure-sk sk)]
    (update sk :opts assoc :coord coord-type)))

(defn draft
  "Flatten a sketch into a draft -- a vector of flat maps, one per
   view-layer combination, with all scope merged: data, mappings,
   and method fully determined.
   (draft sk)"
  [sk]
  (let [sk (ensure-sk sk)]
    (sketch/sketch->draft sk)))

(defn plan
  "Convert a sketch into a plan using view-based grid layout.
   Each view = one panel. Grid position from structural columns.
   (plan sk)
   (plan sk {:title \"My Plot\"})"
  ([sk]
   (when (plan? sk)
     (throw (ex-info (str "sk/plan expects a sketch, not a plan. "
                          "Use the plan directly, or call sk/plot on a sketch.")
                     {:got :plan})))
   (let [sk (ensure-sk sk)
         d (sketch/sketch->draft sk)]
     (plan/draft->plan d (:opts sk {}))))
  ([sk opts]
   (plan (options sk opts))))

(defn plot
  "Render a sketch to SVG (or interactive HTML if tooltip/brush is set).
   (plot sk)
   (plot sk {:width 800 :title \"My Plot\"})"
  ([sk]
   (let [sk (ensure-sk sk)
         p (plan sk)]
     (render-impl/plan->figure p :svg (:opts sk {}))))
  ([sk opts]
   (plot (options sk opts))))

;; ---- SVG Summary ----

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts -- useful for asserting
   plot structure.
   Accepts SVG hiccup or a sketch (auto-renders to SVG first).
   (svg-summary (plot sk))  -- summary of rendered SVG
   (svg-summary my-sketch)              -- auto-renders sketch, then summarizes"
  ([svg-or-sketch]
   (if (sketch/sketch? svg-or-sketch)
     (svg/svg-summary (plot svg-or-sketch))
     (svg/svg-summary svg-or-sketch)))
  ([svg-or-sketch theme]
   (if (sketch/sketch? svg-or-sketch)
     (svg/svg-summary (plot svg-or-sketch) theme)
     (svg/svg-summary svg-or-sketch theme))))

;; ---- Multi-Plot Composition ----

(defn- derived-cell-width
  "Pixel width available for each cell in an arrange grid."
  [arrange-width cols gap-px]
  (let [w (- (double arrange-width) (* (dec (long cols)) (double gap-px)))]
    (max 50.0 (/ w (long cols)))))

(defn- derived-cell-height
  "Pixel height available for each cell in an arrange grid."
  [arrange-height rows gap-px title-pad]
  (let [h (- (double arrange-height) (* (dec (long rows)) (double gap-px)) (double title-pad))]
    (max 50.0 (/ h (long rows)))))

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or sketches, or a vector of vectors (explicit rows).
   opts:  {:cols N, :title \"...\", :gap \"8px\", :width W, :height H}.

   `:width` and `:height` are total-dashboard dimensions. They default
   to the cfg defaults (600 and 400) when not passed, so the returned
   arrangement always fits within a predictable bounding box. Every
   sub-sketch is re-planned at the derived per-cell dimensions so
   text and line widths stay at native resolution (\"sketch-mode\").
   Pre-rendered hiccup plots pass through unchanged -- they inherit
   the container's CSS grid cell size (\"figure-mode\").

   To restore the old \"each plot at its own full size\" behavior,
   pre-render each sketch with `sk/plot` and pass the hiccup in.

   (arrange [plot-a plot-b])                       -- 1x2 row at default width
   (arrange [sk-a sk-b sk-c] {:cols 2 :width 900}) -- 2x2 grid (wraps)
   (arrange [[plot-a plot-b] [plot-c plot-d]])     -- explicit 2x2 grid"
  ([plots] (arrange plots {}))
  ([plots opts]
   (let [cfg (defaults/config)
         {:keys [cols title gap]
          :or {gap "8px"}} opts
         width  (or (:width opts)  (:width cfg))
         height (or (:height opts) (:height cfg))
         nested? (and (sequential? (first plots))
                      (not (keyword? (ffirst plots))))
         flat-plots (if nested? (vec (apply concat plots)) (vec plots))
         n-plots (count flat-plots)
         _ (when (zero? n-plots)
             (throw (ex-info "sk/arrange requires at least one plot."
                             {:plots plots})))
         n-cols (or cols
                    (if nested? (count (first plots))
                        (min 4 n-plots)))
         n-rows (if (pos? n-cols)
                  (long (Math/ceil (/ (double n-plots) (double n-cols))))
                  1)
         ;; Derive a numeric pixel gap from the CSS-string gap.
         ;; Falls back to 8 when the value doesn't match a "NNpx" pattern.
         gap-px (or (when (string? gap)
                      (when-let [m (re-find #"(\d+)\s*px" gap)]
                        (Long/parseLong (second m))))
                    8)
         title-reserve 28
         ;; Cell sizes derived from the total dashboard dims. Schema
         ;; requires :width/:height to be positive integers, so round.
         cell-w (long (Math/round (derived-cell-width width n-cols gap-px)))
         cell-h (long (Math/round (derived-cell-height height n-rows gap-px
                                                       (if title title-reserve 0))))
         ;; Sketch-mode: every sub-sketch is re-planned at the cell
         ;; size so it renders at the target resolution natively.
         ;; Figure-mode (pre-rendered hiccup) passes through -- the
         ;; browser sizes it via the CSS grid cell.
         flat-plots (mapv
                     (fn [p]
                       (cond
                         (sketch/sketch? p)
                         (plot (options p {:width cell-w :height cell-h}))
                         :else p))
                     flat-plots)
         grid-style {:display "grid"
                     :grid-template-columns (str "repeat(" n-cols ", 1fr)")
                     :gap gap
                     :width (str (long width) "px")}
         title-div (when title
                     [:div {:style {:grid-column "1 / -1"
                                    :text-align "center"
                                    :font-weight "bold"
                                    :font-size "16px"
                                    :padding "4px 0"}}
                      title])]
     (kind/hiccup
      (into [:div {:style grid-style}]
            (cond-> []
              title (conj title-div)
              true (into flat-plots)))))))

;; ---- Save ----

(defn save
  "Save a plot to an SVG file.
   sk -- a sketch.
   path     -- file path (string or java.io.File).
   opts     -- same options as plot (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save my-sketch \"plot.svg\")
   (save my-sketch \"plot.svg\" {:width 800 :height 600})"
  ([sk path]
   (save sk path {}))
  ([sk path opts]
   (let [path-str (str path)
         sk (ensure-sk sk)]
     (when-not (.endsWith path-str ".svg")
       (println (str "Warning: save produces SVG output, but path does not end with .svg: " path-str)))
     (let [sk (if (seq opts) (options sk opts) sk)
           p (plan sk)
           svg-hiccup (render-impl/plan->figure p :svg (:opts sk {}))]
       (spit path (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                       (svg/hiccup->svg-str svg-hiccup)))
       path))))

(defn save-png
  "Save a plot to a PNG file via membrane's Java2D backend.
   sk -- a sketch.
   path     -- file path (string or java.io.File).
   opts     -- same options as save (:width, :height, :title, :theme, etc.).
   Returns the path.
   (save-png my-sketch \"plot.png\")
   (save-png my-sketch \"plot.png\" {:width 800 :height 600})"
  ([sk path]
   (save-png sk path {}))
  ([sk path opts]
   (require 'scicloj.napkinsketch.render.bufimg)
   (let [sk (ensure-sk sk)
         sk (if (seq opts) (options sk opts) sk)
         p (plan sk)
         img (render-impl/plan->figure p :bufimg (:opts sk {}))]
     ((resolve 'scicloj.napkinsketch.render.bufimg/save-png) img path))))
