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

;; ---- Annotations ----

(defn rule-v
  "Vertical reference line at x = intercept.
   (rule-v 5)  -- line at x=5"
  [intercept]
  (resolve/rule-v intercept))

(defn rule-h
  "Horizontal reference line at y = intercept.
   (rule-h 3)  -- line at y=3"
  [intercept]
  (resolve/rule-h intercept))

(defn band-v
  "Vertical shaded band from x = lo to x = hi.
   (band-v 4 6)              -- shaded region between x=4 and x=6
   (band-v 4 6 {:alpha 0.3}) -- with custom opacity"
  ([lo hi] (resolve/band-v lo hi))
  ([lo hi opts] (resolve/band-v lo hi opts)))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi.
   (band-h 2 4)              -- shaded region between y=2 and y=4
   (band-h 2 4 {:alpha 0.3}) -- with custom opacity"
  ([lo hi] (resolve/band-h lo hi))
  ([lo hi opts] (resolve/band-h lo hi opts)))

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
  "Coerce data to a tablecloth dataset. Returns nil for nil."
  [d]
  (when d
    (if (tc/dataset? d) d (tc/dataset d))))

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
  (into defaults/column-keys #{:data :color-type}))

(def ^:private plot-options-keys
  "Keys accepted by sk/options (top-level only; nested theme/config keys
   are validated separately by deep-merge)."
  (into (set (keys defaults/plot-option-docs))
        (keys defaults/config-key-docs)))

(defn- warn-unknown-opts!
  "Print a warning when `opts` contains keys outside `accepted`.
   `caller` is used in the warning to disambiguate sk/view, sk/sketch,
   sk/options, etc. Mirrors the unknown-option warning that
   `build-layer` already emits for sk/lay-* calls."
  [caller opts accepted]
  (when (and (map? opts) (seq opts))
    (let [unknown (remove accepted (keys opts))]
      (when (seq unknown)
        (println (str "Warning: sk/" caller
                      " does not recognize option(s): " (vec unknown)
                      ". Accepted: " (vec (sort accepted))))))))

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
   (warn-unknown-opts! "sketch" mapping view-mapping-keys)
   ;; sk/sketch's mapping is for *appearance* aesthetics (color/size/etc.) that
   ;; flow into all views; it does not create a view. Reject :x/:y here so a
   ;; ggplot2-style `(ggplot data, aes(x, y))` pattern fails loudly instead of
   ;; producing a 0-panel plot.
   (when (or (:x mapping) (:y mapping))
     (throw (ex-info (str "sk/sketch does not create a view -- :x and :y are not allowed "
                          "in its mapping. Use (sk/view sk :x :y) to declare position "
                          "mappings, or (sk/lay-point sk :x :y) for a one-shot view+layer.")
                     {:mapping mapping})))
   (if (sketch/sketch? data)
     ;; Merge new mapping into existing sketch, preserving views/layers/opts
     (update data :mapping merge mapping)
     ;; Fresh sketch from raw data (or nil)
     (wrap-autorender
      (sketch/->sketch (coerce-dataset data) mapping [] [] {})))))

(defn with-data
  "Supply or replace data in a sketch."
  [sk data]
  (assoc (ensure-sk sk) :data (coerce-dataset data)))

(defn- make-view
  "Build a view map from a mapping, extracting :data if present."
  [mapping]
  (warn-unknown-opts! "view" mapping view-mapping-keys)
  (when (or (:facet-col mapping) (:facet-row mapping))
    (throw (ex-info (str "Faceting is plot-level, not view-level. "
                         "Use (sk/facet sk col) or (sk/facet-grid sk col-col row-col) "
                         "instead of putting :facet-col / :facet-row in a view's mapping.")
                    {:facet-col (:facet-col mapping)
                     :facet-row (:facet-row mapping)})))
  (let [d (:data mapping)]
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

(defn- add-view-layer
  "Add a layer to a specific view (found by matching position mappings, or created new).
   Matches the *last* view with the same :x/:y in its :mapping, so that
   `lay-*` naturally targets the most recently created view.
   Used when lay-* is called with positional columns."
  [sk position-mapping layer]
  (let [views (:views sk)
        idx (last (keep-indexed
                   (fn [i v]
                     (when (and (= (:x (:mapping v)) (:x position-mapping))
                                (= (:y (:mapping v)) (:y position-mapping)))
                       i))
                   views))]
    (if idx
      ;; Found existing view -- append layer to its :layers
      (update-in sk [:views idx :layers]
                 (fn [ls] (conj (or ls []) layer)))
      ;; No match -- create new view with this layer
      (update sk :views conj {:mapping position-mapping :layers [layer]}))))

(defn- build-layer
  "Build a layer map from a method-key and optional opts.
   Extracts :data if present. Warns on unrecognized option keys."
  [method-key opts]
  (when (and opts (keyword? method-key))
    (let [reg (method/lookup method-key)
          accepted (into (set method/universal-layer-options)
                         (:accepts reg))
          unknown (remove accepted (keys (dissoc opts :data)))]
      (when (seq unknown)
        (println (str "Warning: lay-" (name method-key)
                      " does not recognize option(s): " (vec unknown)
                      ". Accepted: " (vec (sort accepted)))))))
  (let [d (:data opts)]
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
  (warn-unknown-opts! "options" opts plot-options-keys)
  (doseq [k [:width :height]
          :let [v (get opts k)]
          :when v]
    (when-not (and (number? v) (pos? v))
      (throw (ex-info (str k " must be a positive number, got: " (pr-str v))
                      {:option k :value v}))))
  (update (ensure-sk sk) :opts deep-merge opts))

(def ^:private valid-scale-types
  "Scale types accepted by sk/scale. Categorical is inferred from data,
   not user-passed."
  #{:linear :log})

(defn scale
  "Set axis scale on a sketch. Scale is plot-level -- applies to all views.
   (scale sk :x :log) -- log scale on x-axis."
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

(defn plan
  "Resolve a sketch into a plan using view-based grid layout.
   Each view = one panel. Grid position from structural columns.
   (plan sk)
   (plan sk {:title \"My Plot\"})"
  ([sk]
   (let [sk (ensure-sk sk)
         views (sketch/resolve-sketch sk)]
     (plan/views->plan views (:opts sk {}))))
  ([sk opts]
   (plan (options sk opts))))

(defn annotate
  "Add annotations to a sketch. Annotations are plot-level decorations
   (reference lines and bands) that don't participate in the view x layer
   cross product.
   (annotate sk (sk/rule-h 5) (sk/band-v 3 7))"
  [sk & annotations]
  (doseq [ann annotations]
    (when-not (and (map? ann) (resolve/annotation-marks (:mark ann)))
      (throw (ex-info (str "annotate expects annotation maps (from rule-h, rule-v, band-h, band-v), got: "
                           (pr-str ann))
                      {:annotation ann}))))
  (update-in (ensure-sk sk) [:opts :annotations]
             (fn [existing] (into (or existing []) annotations))))

(defn overlay
  "Add a layer with different columns on the same panel as an existing view.
   Use when you want two different column mappings sharing the same axes --
   e.g., scatter of :x/:y with a line of :x/:y_predicted.
   The overlay creates a new view with its own layer.
   (overlay sk :x :y_predicted :line)
   (overlay sk :x :y_predicted :line {:color \"red\"})"
  ([sk x y method-key]
   (overlay sk x y method-key {}))
  ([sk x y method-key opts]
   (let [sk (ensure-sk sk)]
     (update sk :views conj {:mapping {:x x :y y}
                             :layers [(build-layer method-key opts)]}))))

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

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or sketches, or a vector of vectors (explicit rows).
   opts:  {:cols N, :title \"...\", :gap \"8px\"}.
   (arrange [plot-a plot-b])                -- 1x2 row
   (arrange [plot-a plot-b plot-c] {:cols 2}) -- 2-column grid, wraps
   (arrange [[plot-a plot-b] [plot-c plot-d]]) -- explicit 2x2 grid"
  ([plots] (arrange plots {}))
  ([plots opts]
   (let [{:keys [cols title gap]} opts
         nested? (and (sequential? (first plots))
                      (not (keyword? (ffirst plots))))
         flat-plots (if nested? (vec (apply concat plots)) (vec plots))
         ;; Auto-render any sketches to SVG hiccup
         flat-plots (mapv #(if (sketch/sketch? %)
                             (plot %)
                             %)
                          flat-plots)
         n-cols (or cols
                    (if nested? (count (first plots)) (count flat-plots)))
         grid-style {:display "grid"
                     :grid-template-columns (str "repeat(" n-cols ", 1fr)")
                     :gap (or gap "8px")}
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
