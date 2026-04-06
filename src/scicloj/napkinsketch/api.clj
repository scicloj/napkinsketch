(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.theold-sketch :as sketch-impl]
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
            [scicloj.napkinsketch.impl.xkcd7-sketch :as xkcd7-sketch]
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

;; ---- xkcd7 API ----

(defn- xkcd7-wrap-autorender
  "Wrap a xkcd7-sketch with kind/fn for auto-rendering in Clay."
  [xkcd7-sk]
  (kind/fn (cond-> (assoc xkcd7-sk :kindly/f #'xkcd7-sketch/xkcd7-render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))

(defn- xkcd7-coerce-dataset
  "Coerce data to a tablecloth dataset. Returns nil for nil."
  [d]
  (when d
    (if (tc/dataset? d) d (tc/dataset d))))

(defn- xkcd7-ensure-xkcd7-sk
  "Coerce first arg to a xkcd7-sketch if it isn't one already.
   Data is eagerly coerced to a dataset so downstream code can
   uniformly use tc/column-names."
  [x]
  (cond
    (xkcd7-sketch/xkcd7-sketch? x) x
    (or (tc/dataset? x)
        (map? x)
        (sequential? x))    (xkcd7-wrap-autorender
                             (xkcd7-sketch/->xkcd7-sketch (xkcd7-coerce-dataset x) {} [] [] {}))
    :else                    (xkcd7-wrap-autorender (xkcd7-sketch/->xkcd7-sketch nil {} [] [] {}))))

(defn xkcd7-sketch?
  "Return true if x is a xkcd7-sketch."
  [x]
  (xkcd7-sketch/xkcd7-sketch? x))

(defn xkcd7-sketch
  "Create a xkcd7-sketch."
  ([] (xkcd7-wrap-autorender (xkcd7-sketch/->xkcd7-sketch nil {} [] [] {})))
  ([data] (xkcd7-sketch data {}))
  ([data shared]
   (xkcd7-wrap-autorender
    (xkcd7-sketch/->xkcd7-sketch (xkcd7-coerce-dataset data) shared [] [] {}))))

(defn xkcd7-with-data
  "Supply or replace data in a xkcd7-sketch."
  [xkcd7-sk data]
  (assoc xkcd7-sk :data (xkcd7-coerce-dataset data)))

(defn xkcd7-view
  "Add entries to a xkcd7-sketch."
  ([xkcd7-sk-or-data]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (if (:data xkcd7-sk)
       (let [cols (vec (tc/column-names (:data xkcd7-sk)))
             n (count cols)]
         (case n
           1 (update xkcd7-sk :entries conj {:x (cols 0)})
           2 (update xkcd7-sk :entries conj {:x (cols 0) :y (cols 1)})
           3 (update xkcd7-sk :entries conj {:x (cols 0) :y (cols 1) :color (cols 2)})
           (throw (ex-info (str "Cannot infer columns from " n " columns.") {:columns cols}))))
       xkcd7-sk)))
  ([xkcd7-sk-or-data x-or-entries]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (cond
       (or (keyword? x-or-entries)
           (string? x-or-entries)) (update xkcd7-sk :entries conj {:x x-or-entries})
       (map? x-or-entries)       (update xkcd7-sk :entries conj x-or-entries)
       (sequential? x-or-entries)
       (let [first-el (first x-or-entries)]
         (if (or (keyword? first-el) (string? first-el))
           ;; Vector of keywords → univariate entries: [:a :b :c]
           (update xkcd7-sk :entries into (mapv (fn [col] {:x col}) x-or-entries))
           ;; Vector of pairs → bivariate entries: [[:x1 :y1] [:x2 :y2]]
           (update xkcd7-sk :entries into (mapv (fn [[x y]] {:x x :y y}) x-or-entries)))))))
  ([xkcd7-sk-or-data x y]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (if (and (sequential? x) (map? y))
       ;; Columns/pairs + shared opts: (view xkcd7-sk [:a :b :c] {:color :species})
       (let [xkcd7-sk (update xkcd7-sk :shared merge y)
             first-el (first x)]
         (if (or (keyword? first-el) (string? first-el))
           (update xkcd7-sk :entries into (mapv (fn [col] {:x col}) x))
           (update xkcd7-sk :entries into (mapv (fn [[a b]] {:x a :y b}) x))))
       (update xkcd7-sk :entries conj {:x x :y y}))))
  ([xkcd7-sk-or-data x y opts]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (-> xkcd7-sk
         (update :shared merge opts)
         (update :entries conj {:x x :y y})))))

(defn- xkcd7-add-entry-method
  "Add a method to a specific entry (found by matching :x/:y, or created new).
   Used when lay-* is called with structural columns."
  [xkcd7-sk entry-keys method-map]
  (let [entries (:entries xkcd7-sk)
        idx (first (keep-indexed
                    (fn [i e]
                      (when (and (= (:x e) (:x entry-keys))
                                 (= (:y e) (:y entry-keys)))
                        i))
                    entries))]
    (if idx
      ;; Found existing entry — append method to its :methods
      (update-in xkcd7-sk [:entries idx :methods]
                 (fn [ms] (conj (or ms []) method-map)))
      ;; No match — create new entry with this method
      (update xkcd7-sk :entries conj (assoc entry-keys :methods [method-map])))))

(defn- xkcd7-method-map
  "Build a method map from a method-key (keyword) or raw map, with optional opts."
  [method-key opts]
  (let [base (if (keyword? method-key)
               (let [m (method/lookup method-key)]
                 (or (not-empty (select-keys (or m {}) [:mark :stat :position]))
                     {:mark method-key :stat :identity}))
               ;; Raw map — pass through as-is
               method-key)]
    (merge base opts)))

(defn xkcd7-lay
  "Add a global method to a xkcd7-sketch (applies to all entries)."
  ([xkcd7-sk-or-data method-key]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (update xkcd7-sk :methods conj (xkcd7-method-map method-key nil))))
  ([xkcd7-sk-or-data method-key opts]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)]
     (update xkcd7-sk :methods conj (xkcd7-method-map method-key opts)))))

(def ^:private x-only-methods
  "Methods that accept only :x, not :y."
  #{:histogram :bar :density :stacked-bar :stacked-bar-fill})

(defn- xkcd7-lay-method
  "Shared implementation for all xkcd7-lay-* functions.
   1-arity: auto-infer columns for small datasets, otherwise global method.
   2-arity: keyword/string → univariate entry-specific; vector → multi-column;
            map → global with opts.
   3-arity: two keywords → bivariate entry-specific; keyword+map → univariate+opts;
            vector+map → multi-column with opts.
   4-arity: bivariate entry-specific with opts."
  ([method-key xkcd7-sk-or-data]
   (let [xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)
         d (:data xkcd7-sk)
         col-count (when d (count (tc/column-names d)))]
     (if (and (empty? (:entries xkcd7-sk)) d (<= col-count 3))
       (let [xkcd7-sk2 (xkcd7-view xkcd7-sk)]
         (xkcd7-add-entry-method xkcd7-sk2 (first (:entries xkcd7-sk2))
                                 (xkcd7-method-map method-key nil)))
       (xkcd7-lay xkcd7-sk method-key))))
  ([method-key xkcd7-sk-or-data x-or-opts]
   (cond
     (or (keyword? x-or-opts) (string? x-or-opts))
     (xkcd7-add-entry-method (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)
                             {:x x-or-opts}
                             (xkcd7-method-map method-key nil))
     (sequential? x-or-opts)
     (-> (xkcd7-view xkcd7-sk-or-data x-or-opts)
         (xkcd7-lay method-key))
     :else
     (xkcd7-lay xkcd7-sk-or-data method-key x-or-opts)))
  ([method-key xkcd7-sk-or-data x y-or-opts]
   (cond
     (or (keyword? y-or-opts) (string? y-or-opts))
     (do (when (x-only-methods method-key)
           (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                           {:method method-key :x x :y y-or-opts})))
         (xkcd7-add-entry-method (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)
                                 {:x x :y y-or-opts}
                                 (xkcd7-method-map method-key nil)))
     (and (sequential? x) (map? y-or-opts))
     (-> (xkcd7-view xkcd7-sk-or-data x y-or-opts)
         (xkcd7-lay method-key))
     :else
     (xkcd7-add-entry-method (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)
                             {:x x}
                             (xkcd7-method-map method-key y-or-opts))))
  ([method-key xkcd7-sk-or-data x y opts]
   (when (x-only-methods method-key)
     (throw (ex-info (str "lay-" (name method-key) " uses only the x column; do not pass a y column")
                     {:method method-key :x x :y y})))
   (xkcd7-add-entry-method (xkcd7-ensure-xkcd7-sk xkcd7-sk-or-data)
                           {:x x :y y}
                           (xkcd7-method-map method-key opts))))

(defn xkcd7-lay-point
  "Add :point method (scatter) to a xkcd7-sketch.
   Without columns → global method (applies to all entries).
   With columns → entry-specific (find or create entry).
   (xkcd7-lay-point xkcd7-sk)                         — global method
   (xkcd7-lay-point xkcd7-sk {:color :species})        — global with opts
   (xkcd7-lay-point data :x :y)                 — entry-specific
   (xkcd7-lay-point data :x :y {:color :c})     — entry-specific with opts"
  ([xkcd7-sk-or-data] (xkcd7-lay-method :point xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :point xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :point xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :point xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-line
  "Add :line method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :line xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :line xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :line xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :line xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-step
  "Add :step method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :step xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :step xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :step xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :step xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-area
  "Add :area method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :area xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :area xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :area xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :area xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-stacked-area
  "Add :stacked-area method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :stacked-area xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :stacked-area xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :stacked-area xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :stacked-area xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-histogram
  "Add :histogram method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :histogram xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :histogram xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :histogram xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :histogram xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-bar
  "Add :bar method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :bar xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :bar xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :bar xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :bar xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-stacked-bar
  "Add :stacked-bar method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :stacked-bar xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :stacked-bar xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :stacked-bar xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :stacked-bar xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-stacked-bar-fill
  "Add :stacked-bar-fill method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :stacked-bar-fill xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :stacked-bar-fill xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :stacked-bar-fill xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :stacked-bar-fill xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-value-bar
  "Add :value-bar method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :value-bar xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :value-bar xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :value-bar xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :value-bar xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-lm
  "Add :lm (linear model) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :lm xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :lm xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :lm xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :lm xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-loess
  "Add :loess (local regression) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :loess xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :loess xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :loess xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :loess xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-density
  "Add :density (KDE) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :density xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :density xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :density xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :density xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-tile
  "Add :tile (heatmap) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :tile xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :tile xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :tile xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :tile xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-density2d
  "Add :density2d (2D KDE heatmap) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :density2d xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :density2d xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :density2d xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :density2d xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-contour
  "Add :contour method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :contour xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :contour xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :contour xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :contour xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-boxplot
  "Add :boxplot method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :boxplot xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :boxplot xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :boxplot xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :boxplot xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-violin
  "Add :violin method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :violin xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :violin xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :violin xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :violin xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-ridgeline
  "Add :ridgeline method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :ridgeline xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :ridgeline xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :ridgeline xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :ridgeline xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-summary
  "Add :summary (mean ± SE) method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :summary xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :summary xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :summary xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :summary xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-errorbar
  "Add :errorbar method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :errorbar xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :errorbar xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :errorbar xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :errorbar xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-lollipop
  "Add :lollipop method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :lollipop xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :lollipop xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :lollipop xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :lollipop xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-text
  "Add :text method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :text xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :text xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :text xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :text xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-label
  "Add :label method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :label xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :label xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :label xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :label xkcd7-sk-or-data x y opts)))

(defn xkcd7-lay-rug
  "Add :rug method to a xkcd7-sketch."
  ([xkcd7-sk-or-data] (xkcd7-lay-method :rug xkcd7-sk-or-data))
  ([xkcd7-sk-or-data x-or-opts] (xkcd7-lay-method :rug xkcd7-sk-or-data x-or-opts))
  ([xkcd7-sk-or-data x y-or-opts] (xkcd7-lay-method :rug xkcd7-sk-or-data x y-or-opts))
  ([xkcd7-sk-or-data x y opts] (xkcd7-lay-method :rug xkcd7-sk-or-data x y opts)))

(defn- normalize-col
  "Normalize a string column reference to a keyword."
  [col]
  (if (string? col) (keyword col) col))

(defn xkcd7-facet
  "Facet a xkcd7-sketch by a column."
  ([xkcd7-sk col] (xkcd7-facet xkcd7-sk col :col))
  ([xkcd7-sk col direction]
   (let [k (case direction :col :facet-col :row :facet-row)
         col (normalize-col col)]
     (update xkcd7-sk :entries (fn [entries] (mapv #(assoc % k col) entries))))))

(defn xkcd7-facet-grid
  "Facet a xkcd7-sketch by two columns (2D grid)."
  [xkcd7-sk col-col row-col]
  (let [col-col (normalize-col col-col)
        row-col (normalize-col row-col)]
    (update xkcd7-sk :entries (fn [entries]
                                (mapv #(assoc % :facet-col col-col :facet-row row-col) entries)))))

(defn- deep-merge
  "Recursively merge maps. Non-map values are overwritten."
  [a b]
  (if (and (map? a) (map? b))
    (merge-with deep-merge a b)
    b))

(defn xkcd7-options
  "Set plot-level options (title, labels, width, height, etc.).
   Nested maps (e.g. :theme) are deep-merged."
  [xkcd7-sk opts]
  (update xkcd7-sk :opts deep-merge opts))

(defn xkcd7-scale
  "Set axis scale on a xkcd7-sketch.
   (xkcd7-scale xkcd7-sk :x :log) — log scale on x-axis."
  [xkcd7-sk channel scale-type]
  (let [k (case channel :x :x-scale :y :y-scale
                (throw (ex-info (str "Scale channel must be :x or :y, got: " channel)
                                {:channel channel})))]
    (update xkcd7-sk :entries (fn [entries]
                                (mapv #(assoc % k (if (map? scale-type)
                                                    (merge {:type :linear} scale-type)
                                                    {:type scale-type})) entries)))))

(defn xkcd7-coord
  "Set coordinate transform on a xkcd7-sketch.
   (xkcd7-coord xkcd7-sk :flip) — flipped coordinates."
  [xkcd7-sk coord-type]
  (when-not (#{:cartesian :flip :polar :fixed} coord-type)
    (throw (ex-info (str "Coordinate must be :cartesian, :flip, :polar, or :fixed, got: " coord-type)
                    {:coord coord-type})))
  (update xkcd7-sk :entries (fn [entries]
                              (mapv #(assoc % :coord coord-type) entries))))

(defn xkcd7-plan
  "Resolve a xkcd7-sketch into a plan using entry-based grid layout.
   Each entry = one panel. Grid position from structural columns.
   (xkcd7-plan xkcd7-sk)
   (xkcd7-plan xkcd7-sk {:title \"My Plot\"})"
  ([xkcd7-sk]
   (let [views (xkcd7-sketch/xkcd7-resolve-sketch xkcd7-sk)]
     (sketch-impl/xkcd7-views->plan views (:opts xkcd7-sk {}))))
  ([xkcd7-sk opts]
   (let [views (xkcd7-sketch/xkcd7-resolve-sketch xkcd7-sk)]
     (sketch-impl/xkcd7-views->plan views (merge (:opts xkcd7-sk {}) opts)))))

(defn xkcd7-annotate
  "Add annotation entries to a xkcd7-sketch.
   Annotations (rule-h, rule-v, band-h, band-v) are view maps that
   don't participate in the entry × methods cross product.
   (xkcd7-annotate xkcd7-sk (sk/rule-h 5) (sk/band-v 3 7))"
  [xkcd7-sk & annotations]
  (reduce (fn [xkcd7-sk ann]
            (update xkcd7-sk :entries conj (assoc ann :methods [ann])))
          xkcd7-sk annotations))

(defn xkcd7-overlay
  "Add a layer with different columns on the same panel as an existing entry.
   Use when you want two different column mappings sharing the same axes —
   e.g., scatter of :x/:y with a line of :x/:y_predicted.
   The overlay creates a new entry with its own :methods.
   (xkcd7-overlay xkcd7-sk :x :y_predicted :line)
   (xkcd7-overlay xkcd7-sk :x :y_predicted :line {:color \"red\"})"
  ([xkcd7-sk x y method-key]
   (xkcd7-overlay xkcd7-sk x y method-key {}))
  ([xkcd7-sk x y method-key opts]
   (let [method-map (xkcd7-method-map method-key opts)]
     (update xkcd7-sk :entries conj {:x x :y y :methods [method-map]}))))

(defn xkcd7-plot
  "Render a xkcd7-sketch to SVG (or interactive HTML if tooltip/brush is set).
   (xkcd7-plot xkcd7-sk)
   (xkcd7-plot xkcd7-sk {:width 800 :title \"My Plot\"})"
  ([xkcd7-sk]
   (let [opts (:opts xkcd7-sk {})
         views (xkcd7-sketch/xkcd7-resolve-sketch xkcd7-sk)
         plan (sketch-impl/xkcd7-views->plan views opts)]
     (render-impl/plan->figure plan :svg opts)))
  ([xkcd7-sk opts]
   (xkcd7-plot (xkcd7-options xkcd7-sk opts))))

;; ---- SVG Summary ----

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts — useful for asserting
   plot structure.
   Accepts SVG hiccup or a xkcd7-sketch (auto-renders to SVG first).
   (svg-summary (xkcd7-plot xkcd7-sk))  — summary of rendered SVG
   (svg-summary my-sketch)              — auto-renders xkcd7-sketch, then summarizes"
  ([svg-or-sketch]
   (if (xkcd7-sketch/xkcd7-sketch? svg-or-sketch)
     (let [views (xkcd7-sketch/xkcd7-resolve-sketch svg-or-sketch)
           plan (sketch-impl/xkcd7-views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {})))
     (svg/svg-summary svg-or-sketch)))
  ([svg-or-sketch theme]
   (if (xkcd7-sketch/xkcd7-sketch? svg-or-sketch)
     (let [views (xkcd7-sketch/xkcd7-resolve-sketch svg-or-sketch)
           plan (sketch-impl/xkcd7-views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {}) theme))
     (svg/svg-summary svg-or-sketch theme))))

;; ---- Multi-Plot Composition ----

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or xkcd7-sketches, or a vector of vectors (explicit rows).
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
         ;; Auto-render any xkcd7-sketches to SVG hiccup
         flat-plots (mapv #(if (xkcd7-sketch/xkcd7-sketch? %)
                             (xkcd7-plot %)
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
   xkcd7-sk — a xkcd7-sketch.
   path     — file path (string or java.io.File).
   opts     — same options as xkcd7-plot (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save my-sketch \"plot.svg\")
   (save my-sketch \"plot.svg\" {:width 800 :height 600})"
  ([xkcd7-sk path]
   (save xkcd7-sk path {}))
  ([xkcd7-sk path opts]
   (let [path-str (str path)
         xkcd7-sk (xkcd7-ensure-xkcd7-sk xkcd7-sk)]
     (when-not (.endsWith path-str ".svg")
       (println (str "Warning: save produces SVG output, but path does not end with .svg: " path-str)))
     (let [views (xkcd7-sketch/xkcd7-resolve-sketch xkcd7-sk)
           all-opts (kindly/deep-merge (:opts xkcd7-sk {}) opts)
           plan (sketch-impl/xkcd7-views->plan views all-opts)
           svg-hiccup (render-impl/plan->figure plan :svg {})]
       (spit path (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                       (svg/hiccup->svg-str svg-hiccup)))
       path))))
