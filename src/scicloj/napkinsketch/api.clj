(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.sketch :as sketch]
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
  (view/plan? x))

(defn layer?
  "Return true if x is a layer (resolved geometry for one mark in a plan)."
  [x]
  (view/layer? x))

(defn method?
  "Return true if x is a method (mark + stat + position bundle from the registry)."
  [x]
  (view/method? x))

(defn- expect-type
  "Validate that x is of the expected type. Throws with helpful message if not."
  [x pred expected-name fn-name]
  (when-not (pred x)
    (throw (ex-info (str fn-name " expects a " expected-name ". "
                         (cond (view/plan? x) "Got a plan."
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
   (config)  — show current resolved config"
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
   (set-config! nil)  — reset to defaults"
  [m]
  (defaults/set-config! m))

(defn method-lookup
  "Look up a registered method by keyword. Returns the method map
   (with :mark, :stat, :position, :doc), or nil if not found.
   (method-lookup :histogram) => {:mark :bar, :stat :bin, ...}"
  [k]
  (method/lookup k))

(defn method-registered
  "Return all registered methods as a map of keyword → method map.
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
   (coord-doc :polar) => \"Radial mapping: x→angle, y→radius\""
  [k]
  (try
    (let [r (coord/make-coord [k :doc] nil nil nil nil nil)]
      (if (string? r) r "(no description)"))
    (catch Exception _ "(no description)")))

;; ---- Annotations ----

(defn rule-v
  "Vertical reference line at x = intercept.
   (rule-v 5)  — line at x=5"
  [intercept]
  (view/rule-v intercept))

(defn rule-h
  "Horizontal reference line at y = intercept.
   (rule-h 3)  — line at y=3"
  [intercept]
  (view/rule-h intercept))

(defn band-v
  "Vertical shaded band from x = lo to x = hi.
   (band-v 4 6)              — shaded region between x=4 and x=6
   (band-v 4 6 {:alpha 0.3}) — with custom opacity"
  ([lo hi] (view/band-v lo hi))
  ([lo hi opts] (view/band-v lo hi opts)))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi.
   (band-h 2 4)              — shaded region between y=2 and y=4
   (band-h 2 4 {:alpha 0.3}) — with custom opacity"
  ([lo hi] (view/band-h lo hi))
  ([lo hi opts] (view/band-h lo hi opts)))

;; ---- Cross ----

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (view/cross xs ys))

;; ---- Pipeline Internals ----

(defn plan->membrane
  "Convert a plan into a membrane drawable tree.
   (plan->membrane (plan sketch))"
  [plan-data & {:as opts}]
  (expect-type plan-data view/plan? "plan (from sk/plan)" "sk/plan->membrane")
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
  (expect-type plan view/plan? "plan (from sk/plan)" "sk/plan->figure")
  (render-impl/plan->figure plan format opts))

;; ---- Plan Validation ----

(defn valid-plan?
  "Check if a plan conforms to the Malli schema.
   (valid-plan? (plan views))  — true if valid"
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
        (sequential? x))    (wrap-autorender
                             (sketch/->sketch (coerce-dataset x) {} [] [] {}))
    :else                    (wrap-autorender (sketch/->sketch nil {} [] [] {}))))

(defn sketch?
  "Return true if x is a sketch."
  [x]
  (sketch/sketch? x))

(defn sketch
  "Create a sketch."
  ([] (wrap-autorender (sketch/->sketch nil {} [] [] {})))
  ([data] (sketch data {}))
  ([data shared]
   (wrap-autorender
    (sketch/->sketch (coerce-dataset data) shared [] [] {}))))

(defn with-data
  "Supply or replace data in a sketch."
  [sk data]
  (assoc sk :data (coerce-dataset data)))

(defn view
  "Add entries to a sketch."
  ([sk-or-data]
   (let [sk (ensure-sk sk-or-data)]
     (if (:data sk)
       (let [cols (vec (tc/column-names (:data sk)))
             n (count cols)]
         (case n
           1 (update sk :entries conj {:x (cols 0)})
           2 (update sk :entries conj {:x (cols 0) :y (cols 1)})
           3 (update sk :entries conj {:x (cols 0) :y (cols 1) :color (cols 2)})
           (throw (ex-info (str "Cannot infer columns from " n " columns.") {:columns cols}))))
       sk)))
  ([sk-or-data x-or-entries]
   (let [sk (ensure-sk sk-or-data)]
     (cond
       (or (keyword? x-or-entries)
           (string? x-or-entries)) (update sk :entries conj {:x x-or-entries})
       (map? x-or-entries)       (update sk :entries conj x-or-entries)
       (sequential? x-or-entries)
       (let [first-el (first x-or-entries)]
         (if (or (keyword? first-el) (string? first-el))
           ;; Vector of keywords → univariate entries: [:a :b :c]
           (update sk :entries into (mapv (fn [col] {:x col}) x-or-entries))
           ;; Vector of pairs → bivariate entries: [[:x1 :y1] [:x2 :y2]]
           (update sk :entries into (mapv (fn [[x y]] {:x x :y y}) x-or-entries)))))))
  ([sk-or-data x y]
   (let [sk (ensure-sk sk-or-data)]
     (if (and (sequential? x) (map? y))
       ;; Columns/pairs + shared opts: (view sk [:a :b :c] {:color :species})
       (let [sk (update sk :shared merge y)
             first-el (first x)]
         (if (or (keyword? first-el) (string? first-el))
           (update sk :entries into (mapv (fn [col] {:x col}) x))
           (update sk :entries into (mapv (fn [[a b]] {:x a :y b}) x))))
       (update sk :entries conj {:x x :y y}))))
  ([sk-or-data x y opts]
   (let [sk (ensure-sk sk-or-data)]
     (-> sk
         (update :shared merge opts)
         (update :entries conj {:x x :y y})))))

(defn- add-entry-method
  "Add a method to a specific entry (found by matching :x/:y, or created new).
   Used when lay-* is called with structural columns."
  [sk entry-keys method-map]
  (let [entries (:entries sk)
        idx (first (keep-indexed
                    (fn [i e]
                      (when (and (= (:x e) (:x entry-keys))
                                 (= (:y e) (:y entry-keys)))
                        i))
                    entries))]
    (if idx
      ;; Found existing entry — append method to its :methods
      (update-in sk [:entries idx :methods]
                 (fn [ms] (conj (or ms []) method-map)))
      ;; No match — create new entry with this method
      (update sk :entries conj (assoc entry-keys :methods [method-map])))))

(defn- method-map
  "Build a method map from a method-key (keyword) or raw map, with optional opts."
  [method-key opts]
  (let [base (if (keyword? method-key)
               (let [m (method/lookup method-key)]
                 (or (not-empty (select-keys (or m {}) [:mark :stat :position]))
                     {:mark method-key :stat :identity}))
               ;; Raw map — pass through as-is
               method-key)]
    (merge base opts)))

(defn lay
  "Add a global method to a sketch (applies to all entries)."
  ([sk-or-data method-key]
   (let [sk (ensure-sk sk-or-data)]
     (update sk :methods conj (method-map method-key nil))))
  ([sk-or-data method-key opts]
   (let [sk (ensure-sk sk-or-data)]
     (update sk :methods conj (method-map method-key opts)))))

(def ^:private x-only-methods
  "Methods that accept only :x, not :y."
  #{:histogram :bar :density :stacked-bar :stacked-bar-fill})

(defn- lay-method
  "Shared implementation for all lay-* functions.
   1-arity: auto-infer columns for small datasets, otherwise global method.
   2-arity: keyword/string → univariate entry-specific; vector → multi-column;
            map → global with opts.
   3-arity: two keywords → bivariate entry-specific; keyword+map → univariate+opts;
            vector+map → multi-column with opts.
   4-arity: bivariate entry-specific with opts."
  ([method-key sk-or-data]
   (let [sk (ensure-sk sk-or-data)
         d (:data sk)
         col-count (when d (count (tc/column-names d)))]
     (if (and (empty? (:entries sk)) d (<= col-count 3))
       (let [sk2 (view sk)]
         (add-entry-method sk2 (first (:entries sk2))
                           (method-map method-key nil)))
       (lay sk method-key))))
  ([method-key sk-or-data x-or-opts]
   (cond
     (or (keyword? x-or-opts) (string? x-or-opts))
     (add-entry-method (ensure-sk sk-or-data)
                       {:x x-or-opts}
                       (method-map method-key nil))
     (sequential? x-or-opts)
     (-> (view sk-or-data x-or-opts)
         (lay method-key))
     :else
     (lay sk-or-data method-key x-or-opts)))
  ([method-key sk-or-data x y-or-opts]
   (cond
     (or (keyword? y-or-opts) (string? y-or-opts))
     (do (when (x-only-methods method-key)
           (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                           {:method method-key :x x :y y-or-opts})))
         (add-entry-method (ensure-sk sk-or-data)
                           {:x x :y y-or-opts}
                           (method-map method-key nil)))
     (and (sequential? x) (map? y-or-opts))
     (-> (view sk-or-data x y-or-opts)
         (lay method-key))
     :else
     (add-entry-method (ensure-sk sk-or-data)
                       {:x x}
                       (method-map method-key y-or-opts))))
  ([method-key sk-or-data x y opts]
   (when (x-only-methods method-key)
     (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                     {:method method-key :x x :y y})))
   (add-entry-method (ensure-sk sk-or-data)
                     {:x x :y y}
                     (method-map method-key opts))))

(defn lay-point
  "Add :point method (scatter) to a sketch.
   Without columns → global method (applies to all entries).
   With columns → entry-specific (find or create entry).
   (lay-point sk)                         — global method
   (lay-point sk {:color :species})        — global with opts
   (lay-point data :x :y)                 — entry-specific
   (lay-point data :x :y {:color :c})     — entry-specific with opts"
  ([sk-or-data] (lay-method :point sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :point sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :point sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :point sk-or-data x y opts)))

(defn lay-line
  "Add :line method to a sketch."
  ([sk-or-data] (lay-method :line sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :line sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :line sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :line sk-or-data x y opts)))

(defn lay-step
  "Add :step method to a sketch."
  ([sk-or-data] (lay-method :step sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :step sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :step sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :step sk-or-data x y opts)))

(defn lay-area
  "Add :area method to a sketch."
  ([sk-or-data] (lay-method :area sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :area sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :area sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :area sk-or-data x y opts)))

(defn lay-stacked-area
  "Add :stacked-area method to a sketch."
  ([sk-or-data] (lay-method :stacked-area sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-area sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-area sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-area sk-or-data x y opts)))

(defn lay-histogram
  "Add :histogram method to a sketch."
  ([sk-or-data] (lay-method :histogram sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :histogram sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :histogram sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :histogram sk-or-data x y opts)))

(defn lay-bar
  "Add :bar method to a sketch."
  ([sk-or-data] (lay-method :bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :bar sk-or-data x y opts)))

(defn lay-stacked-bar
  "Add :stacked-bar method to a sketch."
  ([sk-or-data] (lay-method :stacked-bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-bar sk-or-data x y opts)))

(defn lay-stacked-bar-fill
  "Add :stacked-bar-fill method to a sketch."
  ([sk-or-data] (lay-method :stacked-bar-fill sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :stacked-bar-fill sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :stacked-bar-fill sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :stacked-bar-fill sk-or-data x y opts)))

(defn lay-value-bar
  "Add :value-bar method to a sketch."
  ([sk-or-data] (lay-method :value-bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :value-bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :value-bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :value-bar sk-or-data x y opts)))

(defn lay-lm
  "Add :lm (linear model) method to a sketch."
  ([sk-or-data] (lay-method :lm sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :lm sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :lm sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :lm sk-or-data x y opts)))

(defn lay-loess
  "Add :loess (local regression) method to a sketch."
  ([sk-or-data] (lay-method :loess sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :loess sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :loess sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :loess sk-or-data x y opts)))

(defn lay-density
  "Add :density (KDE) method to a sketch."
  ([sk-or-data] (lay-method :density sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :density sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :density sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :density sk-or-data x y opts)))

(defn lay-tile
  "Add :tile (heatmap) method to a sketch."
  ([sk-or-data] (lay-method :tile sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :tile sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :tile sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :tile sk-or-data x y opts)))

(defn lay-density2d
  "Add :density2d (2D KDE heatmap) method to a sketch."
  ([sk-or-data] (lay-method :density2d sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :density2d sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :density2d sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :density2d sk-or-data x y opts)))

(defn lay-contour
  "Add :contour method to a sketch."
  ([sk-or-data] (lay-method :contour sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :contour sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :contour sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :contour sk-or-data x y opts)))

(defn lay-boxplot
  "Add :boxplot method to a sketch."
  ([sk-or-data] (lay-method :boxplot sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :boxplot sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :boxplot sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :boxplot sk-or-data x y opts)))

(defn lay-violin
  "Add :violin method to a sketch."
  ([sk-or-data] (lay-method :violin sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :violin sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :violin sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :violin sk-or-data x y opts)))

(defn lay-ridgeline
  "Add :ridgeline method to a sketch."
  ([sk-or-data] (lay-method :ridgeline sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :ridgeline sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :ridgeline sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :ridgeline sk-or-data x y opts)))

(defn lay-summary
  "Add :summary (mean ± SE) method to a sketch."
  ([sk-or-data] (lay-method :summary sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :summary sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :summary sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :summary sk-or-data x y opts)))

(defn lay-errorbar
  "Add :errorbar method to a sketch."
  ([sk-or-data] (lay-method :errorbar sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :errorbar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :errorbar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :errorbar sk-or-data x y opts)))

(defn lay-lollipop
  "Add :lollipop method to a sketch."
  ([sk-or-data] (lay-method :lollipop sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :lollipop sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :lollipop sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :lollipop sk-or-data x y opts)))

(defn lay-text
  "Add :text method to a sketch."
  ([sk-or-data] (lay-method :text sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :text sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :text sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :text sk-or-data x y opts)))

(defn lay-label
  "Add :label method to a sketch."
  ([sk-or-data] (lay-method :label sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :label sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :label sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :label sk-or-data x y opts)))

(defn lay-rug
  "Add :rug method to a sketch."
  ([sk-or-data] (lay-method :rug sk-or-data))
  ([sk-or-data x-or-opts] (lay-method :rug sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-method :rug sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-method :rug sk-or-data x y opts)))

(defn- normalize-col
  "Normalize a string column reference to a keyword."
  [col]
  (if (string? col) (keyword col) col))

(defn facet
  "Facet a sketch by a column."
  ([sk col] (facet sk col :col))
  ([sk col direction]
   (let [k (case direction :col :facet-col :row :facet-row)
         col (normalize-col col)]
     (update sk :entries (fn [entries] (mapv #(assoc % k col) entries))))))

(defn facet-grid
  "Facet a sketch by two columns (2D grid)."
  [sk col-col row-col]
  (let [col-col (normalize-col col-col)
        row-col (normalize-col row-col)]
    (update sk :entries (fn [entries]
                          (mapv #(assoc % :facet-col col-col :facet-row row-col) entries)))))

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
  (update sk :opts deep-merge opts))

(defn scale
  "Set axis scale on a sketch.
   (scale sk :x :log) — log scale on x-axis."
  [sk channel scale-type]
  (let [k (case channel :x :x-scale :y :y-scale
                (throw (ex-info (str "Scale channel must be :x or :y, got: " channel)
                                {:channel channel})))]
    (update sk :entries (fn [entries]
                          (mapv #(assoc % k (if (map? scale-type)
                                              (merge {:type :linear} scale-type)
                                              {:type scale-type})) entries)))))

(defn coord
  "Set coordinate transform on a sketch.
   (coord sk :flip) — flipped coordinates."
  [sk coord-type]
  (when-not (#{:cartesian :flip :polar :fixed} coord-type)
    (throw (ex-info (str "Coordinate must be :cartesian, :flip, :polar, or :fixed, got: " coord-type)
                    {:coord coord-type})))
  (update sk :entries (fn [entries]
                        (mapv #(assoc % :coord coord-type) entries))))

(defn plan
  "Resolve a sketch into a plan using entry-based grid layout.
   Each entry = one panel. Grid position from structural columns.
   (plan sk)
   (plan sk {:title \"My Plot\"})"
  ([sk]
   (let [views (sketch/resolve-sketch sk)]
     (sketch/views->plan views (:opts sk {}))))
  ([sk opts]
   (let [views (sketch/resolve-sketch sk)]
     (sketch/views->plan views (merge (:opts sk {}) opts)))))

(defn annotate
  "Add annotation entries to a sketch.
   Annotations (rule-h, rule-v, band-h, band-v) are view maps that
   don't participate in the entry × methods cross product.
   (annotate sk (sk/rule-h 5) (sk/band-v 3 7))"
  [sk & annotations]
  (reduce (fn [sk ann]
            (update sk :entries conj (assoc ann :methods [ann])))
          sk annotations))

(defn overlay
  "Add a layer with different columns on the same panel as an existing entry.
   Use when you want two different column mappings sharing the same axes —
   e.g., scatter of :x/:y with a line of :x/:y_predicted.
   The overlay creates a new entry with its own :methods.
   (overlay sk :x :y_predicted :line)
   (overlay sk :x :y_predicted :line {:color \"red\"})"
  ([sk x y method-key]
   (overlay sk x y method-key {}))
  ([sk x y method-key opts]
   (let [method-map (method-map method-key opts)]
     (update sk :entries conj {:x x :y y :methods [method-map]}))))

(defn plot
  "Render a sketch to SVG (or interactive HTML if tooltip/brush is set).
   (plot sk)
   (plot sk {:width 800 :title \"My Plot\"})"
  ([sk]
   (let [opts (:opts sk {})
         views (sketch/resolve-sketch sk)
         plan (sketch/views->plan views opts)]
     (render-impl/plan->figure plan :svg opts)))
  ([sk opts]
   (plot (options sk opts))))

;; ---- SVG Summary ----

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts — useful for asserting
   plot structure.
   Accepts SVG hiccup or a sketch (auto-renders to SVG first).
   (svg-summary (plot sk))  — summary of rendered SVG
   (svg-summary my-sketch)              — auto-renders sketch, then summarizes"
  ([svg-or-sketch]
   (if (sketch/sketch? svg-or-sketch)
     (let [views (sketch/resolve-sketch svg-or-sketch)
           plan (sketch/views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {})))
     (svg/svg-summary svg-or-sketch)))
  ([svg-or-sketch theme]
   (if (sketch/sketch? svg-or-sketch)
     (let [views (sketch/resolve-sketch svg-or-sketch)
           plan (sketch/views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {}) theme))
     (svg/svg-summary svg-or-sketch theme))))

;; ---- Multi-Plot Composition ----

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or sketches, or a vector of vectors (explicit rows).
   opts:  {:cols N, :title \"...\", :gap \"8px\"}.
   (arrange [plot-a plot-b])                — 1x2 row
   (arrange [plot-a plot-b plot-c] {:cols 2}) — 2-column grid, wraps
   (arrange [[plot-a plot-b] [plot-c plot-d]]) — explicit 2x2 grid"
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
   sk — a sketch.
   path     — file path (string or java.io.File).
   opts     — same options as plot (:width, :height, :title, :theme, etc.).
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
     (let [views (sketch/resolve-sketch sk)
           all-opts (kindly/deep-merge (:opts sk {}) opts)
           plan (sketch/views->plan views all-opts)
           svg-hiccup (render-impl/plan->figure plan :svg all-opts)]
       (spit path (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                       (svg/hiccup->svg-str svg-hiccup)))
       path))))
