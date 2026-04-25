(ns scicloj.plotje.api
  "Public API for plotje -- composable plotting in Clojure."
  (:require [scicloj.plotje.impl.resolve :as resolve]
            [scicloj.plotje.impl.frame :as frame]
            [scicloj.plotje.impl.compositor :as compositor]
            [scicloj.plotje.impl.plan :as plan]
            [scicloj.plotje.impl.plan-schema :as ss]
            [scicloj.plotje.impl.defaults :as defaults]
            [scicloj.plotje.impl.render :as render-impl]
            [scicloj.plotje.impl.stat :as stat]
            [scicloj.plotje.impl.extract :as extract]
            [scicloj.plotje.impl.position :as position]
            [scicloj.plotje.impl.scale :as scale]
            [scicloj.plotje.impl.coord :as coord]
            [scicloj.plotje.render.membrane :as membrane]
            [scicloj.plotje.render.mark :as mark]
            [scicloj.plotje.render.svg :as svg]
            [scicloj.plotje.layer-type :as layer-type]
            [clojure.set :as set]
            [clojure.string :as str]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Type predicates ----

(defn plan?
  "Return true if x is a plan (the resolved data-space geometry from pj/plan)."
  [x]
  (resolve/plan? x))

(defn layer?
  "Return true if x is a layer (resolved geometry for one mark in a plan)."
  [x]
  (resolve/layer? x))

(defn layer-type?
  "Return true if x is a layer type (mark + stat + position bundle from the registry)."
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
   Merges: library defaults < plotje.edn < set-config! < *config*.
   Useful for inspecting which values are in effect.
   (config)  -- show current resolved config"
  []
  (defaults/config))

(def config-key-docs
  "Documentation metadata for configuration keys.
   Maps each config key to [category description].
   Use with (pj/config) to build reference tables."
  defaults/config-key-docs)

(def plot-option-docs
  "Documentation for plot-level option keys.
   These are accepted by pj/options, pj/plan, and pj/plot but are
   inherently per-plot (text content or nested config override).
   Maps each key to [category description]."
  defaults/plot-option-docs)

(def layer-option-docs
  "Documentation for layer option keys accepted by lay- functions.
   Maps each key to a description string."
  layer-type/layer-option-docs)

(defn set-config!
  "Set global config overrides. Persists across calls until reset.
   (set-config! {:palette :dark2 :theme {:bg \"#FFFFFF\"}})
   (set-config! nil)  -- reset to defaults"
  [m]
  (defaults/set-config! m))

(defn layer-type-lookup
  "Look up a registered layer type by keyword. Returns the layer-type map
   (with :mark, :stat, :position, :doc), or nil if not found.
   (layer-type-lookup :histogram) => {:mark :bar, :stat :bin, ...}"
  [k]
  (layer-type/lookup k))

(defn registered-layer-types
  "Return all registered layer types as a map of keyword -> layer-type map.
   Useful for generating documentation tables."
  []
  (layer-type/registered))

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
   (plan->membrane (plan fr))"
  [plan-data & {:as opts}]
  (expect-type plan-data resolve/plan? "plan (from pj/plan)" "pj/plan->membrane")
  (membrane/plan->membrane plan-data opts))

(defn membrane->plot
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on format keyword; :svg is always available.
   (membrane->plot (plan->membrane (plan views)) :svg {})"
  [membrane-tree format opts]
  (render-impl/membrane->plot membrane-tree format opts))

(defn plan->plot
  "Convert a plan into a figure for the given format.
   Dispatches on format keyword. Each renderer is a separate namespace
   that registers a defmethod; :svg is always available.
   (plan->plot (plan fr) :svg {})
   (plan->plot (plan fr) :plotly {})"
  [plan format opts]
  (expect-type plan resolve/plan? "plan (from pj/plan)" "pj/plan->plot")
  (render-impl/plan->plot plan format opts))

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

(defn- coerce-dataset
  "Coerce data to a tablecloth dataset. Returns nil for nil."
  [d]
  (when d
    (if (tc/dataset? d) d (tc/dataset d))))

(defn- try-infer-mapping
  "Infer a position/color mapping from the first 1-3 columns of a
   dataset. Returns nil if the dataset has 0 or 4+ columns -- callers
   decide whether to throw or fall through."
  [d]
  (let [cols (vec (tc/column-names d))
        n (count cols)]
    (case n
      1 {:x (cols 0)}
      2 {:x (cols 0) :y (cols 1)}
      3 {:x (cols 0) :y (cols 1) :color (cols 2)}
      nil)))

(defn- auto-infer-mapping
  "Auto-infer a position/color mapping from the first 1-3 columns of
   a dataset. Throws if the dataset has 4+ columns -- the user must
   pass explicit x/y.

   Applied when 1-arity (pj/lay-* data) lands on a fresh leaf-frame
   with data but no :mapping."
  [layer-type-key d]
  (or (try-infer-mapping d)
      (let [cols (sort (tc/column-names d))]
        (throw (ex-info (str "Cannot auto-infer columns from " (count cols) " columns. "
                             "Pass explicit x and y: (pj/lay-" (name layer-type-key)
                             " data :x :y). Available columns: " cols)
                        {:layer-type layer-type-key
                         :column-count (count cols)
                         :columns cols})))))

(defn frame?
  "Return true if x is a frame-shaped plain map (a map carrying at
   least one of :layers or :frames)."
  [x]
  (frame/frame? x))

(declare prepare-frame)

(defn- ensure-frame
  "Coerce input to a frame. Frames pass through. Raw data becomes a
   leaf frame with :data set and no mapping, and is run through
   prepare-frame so Kindly auto-render metadata is attached -- the
   metadata is preserved by subsequent assoc/update calls in the
   lay-*/options/frame pipelines."
  [x]
  (if (frame? x)
    x
    (let [d (coerce-dataset x)]
      (prepare-frame (cond-> {:layers []} d (assoc :data d))))))

(def ^:private view-mapping-keys
  "Keys accepted in a frame mapping."
  (into defaults/column-keys #{:data :color-type :x-type :y-type}))

(def ^:private plot-options-keys
  "Keys accepted by pj/options (top-level only; nested theme/config keys
   are validated separately by deep-merge)."
  (into (set (keys defaults/plot-option-docs))
        (keys defaults/config-key-docs)))

(defn- warn-and-strip-unknown-opts
  "Warn if `opts` contains keys outside `accepted` and return `opts`
   with only the accepted keys retained. `caller` is used in the
   warning message (e.g. \"pj/frame\", \"lay-point\"). If opts is
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

(def ^:private frame-keys
  "Allowed top-level keys on a frame at any depth. Outer-scope
   :layers distribute to every descendant leaf in a composite;
   outer-scope :mapping inherits downward and merges with each
   descendant's own mapping."
  #{:data :mapping :layers :opts :frames :layout :share-scales
    :grid-strip-labels})

(def ^:private frame-print-order
  "Key order used by pj/prepare-frame to make printed frame maps
   readable. Small declarative keys first; :data before :frames so
   each level's data stays visually bound to its own siblings rather
   than trailing past its children; :frames last since children can
   be heavy."
  [:opts :mapping :share-scales :grid-strip-labels :layout :layers :data :frames])

(defn- warn-unknown-frame-keys
  "Warn once about top-level keys in fr that are not in frame-keys.
   Returns fr unchanged."
  [fr]
  (let [unknown (remove frame-keys (keys fr))]
    (when (seq unknown)
      (println (str "Warning: pj/prepare-frame got unexpected "
                    "top-level key(s): " (vec unknown)
                    ". Known frame keys: " (vec (sort frame-keys)))))
    fr))

(defn- reorder-frame-keys
  "Return a copy of fr with known keys in frame-print-order, followed
   by any unknown keys in their original order (so extensions survive,
   they just print last)."
  [fr]
  (let [known (reduce (fn [acc k]
                        (if (contains? fr k) (assoc acc k (fr k)) acc))
                      {}
                      frame-print-order)
        extras (remove (set frame-print-order) (keys fr))]
    (reduce (fn [acc k] (assoc acc k (fr k))) known extras)))

(defn- elide-empty-maps
  "Strip :mapping and :opts keys whose value is an empty map. :layers
   [] is preserved because a leaf must carry :layers. Applied by
   normalize-frame so every builder path produces clean output."
  [m]
  (cond-> m
    (and (contains? m :mapping) (empty? (:mapping m))) (dissoc :mapping)
    (and (contains? m :opts)    (empty? (:opts m)))    (dissoc :opts)))

(defn- normalize-frame
  "Recursively validate top-level keys, coerce :data to a Tablecloth
   dataset at every depth, apply empty-map elision to the frame and
   its layers, and reorder keys for readable printing."
  [fr]
  (let [validated (warn-unknown-frame-keys fr)
        coerced (cond-> validated
                  (:data validated)
                  (update :data coerce-dataset)
                  (:frames validated)
                  (update :frames (partial mapv normalize-frame))
                  (:layers validated)
                  (update :layers (partial mapv elide-empty-maps)))]
    (reorder-frame-keys (elide-empty-maps coerced))))

(declare plot)

(defn- render-frame-map
  "Kindly render function that restores captured *config* and routes
   a frame map (leaf or composite) through pj/plot."
  [captured-config]
  (fn [fr]
    (if captured-config
      (binding [defaults/*config* captured-config]
        (plot fr))
      (plot fr))))

(defn prepare-frame
  "Prepare a plain-map frame for use: coerce :data at every depth,
   capture the current *config* for render-time restoration, and
   attach Kindly metadata so notebook viewers auto-render the frame.

   Use this when you construct a frame by hand to get features beyond
   what pj/arrange covers: unequal :weights in :layout, nested
   :frames, cross-sibling :share-scales on differing columns, or a
   composite-level :layers that should distribute to every descendant
   leaf. For a flat row or column of plots, prefer pj/arrange.

   Accepts any frame-shaped map. All of :data, :mapping, :layers,
   :opts, :frames, :layout, :share-scales are legal at any depth.
   Unknown top-level keys are warned and otherwise left in place.

   Keys are reordered for readable printing: small declarative keys
   first, :data before :frames so each level's data stays beside its
   level's other keys.

   (pj/prepare-frame {:data iris
                      :mapping {:x :a :y :b}
                      :layers [{:layer-type :point}]})
   (pj/prepare-frame {:data iris
                      :frames [...]
                      :layout {:direction :vertical :weights [1 3]}})
   (pj/prepare-frame {:data iris
                      :mapping {:x :a}
                      :layers [{:layer-type :point}]
                      :frames [{:mapping {:y :b}}
                               {:mapping {:y :c}}]})
      ;; outer :layers distributes to both sub-frames"
  [fr-map]
  (when-not (map? fr-map)
    (throw (ex-info (str "pj/prepare-frame expects a frame map, got "
                         (pr-str (type fr-map)))
                    {:got fr-map})))
  (let [prepared (normalize-frame fr-map)
        captured defaults/*config*]
    (kind/fn prepared
      {:kindly/f (render-frame-map captured)})))

;; ---- pj/frame polymorphism (Phase 6) ----

(defn- layer-has-position? [layer]
  (boolean (or (:x (:mapping layer))
               (:y (:mapping layer)))))

(defn- leaf-has-position? [leaf]
  (or (boolean (or (:x (:mapping leaf))
                   (:y (:mapping leaf))))
      (boolean (some layer-has-position? (:layers leaf)))))

(defn- position-mapping [m]
  (select-keys m [:x :y]))

(defn- aesthetic-mapping [m]
  (apply dissoc m [:x :y]))

(defn- partition-layers-by-position
  "Returns [root-origin-layers panel-origin-layers]. A layer is
   panel-origin if its own :mapping carries :x or :y, else root-origin."
  [layers]
  (let [{panel :panel root :root}
        (group-by #(if (layer-has-position? %) :panel :root) (or layers []))]
    [(vec (or root []))
     (vec (or panel []))]))

(defn- promote-leaf
  "Promote a leaf to a composite, folding a new incoming-mapping into
   the result. The leaf's position part + panel-origin layers become
   sub-frame 1; the leaf's aesthetic part + root-origin layers + the
   leaf's :opts move to the composite root; the incoming mapping
   splits the same way (aesthetic -> root, position -> sub-frame 2).
   When the incoming mapping carries no position, no new sub-frame
   is added."
  [leaf incoming-mapping]
  (let [[root-layers panel-layers] (partition-layers-by-position (:layers leaf))
        leaf-pos        (position-mapping (:mapping leaf))
        leaf-aesth      (aesthetic-mapping (:mapping leaf))
        incoming-pos    (position-mapping incoming-mapping)
        incoming-aesth  (aesthetic-mapping incoming-mapping)
        root-aesth      (merge leaf-aesth incoming-aesth)
        leaf-opts       (:opts leaf)
        panel-1         (cond-> {:layers panel-layers}
                          (seq leaf-pos) (assoc :mapping leaf-pos))
        panel-2         (when (seq incoming-pos)
                          {:mapping incoming-pos :layers []})
        frames          (filterv some? [panel-1 panel-2])]
    (cond-> {:frames frames
             ;; Threaded `(pj/frame fr :x :y)` over a leaf-with-position
             ;; promotes into a composite. By default the layout is
             ;; matrix: distinct x-cols become grid columns, distinct
             ;; y-cols become grid rows, leaves land at their (x, y)
             ;; intersection. The user can override later with
             ;; (pj/options fr {:layout {:direction :horizontal}}).
             :layout {:direction :matrix}}
      (:data leaf)      (assoc :data (:data leaf))
      (seq root-aesth)  (assoc :mapping root-aesth)
      (seq root-layers) (assoc :layers root-layers)
      (seq leaf-opts)   (assoc :opts leaf-opts))))

(defn- extend-leaf
  "Extend a leaf that carries no position yet, merging incoming-mapping
   into its :mapping. Used when neither the leaf's :mapping nor its
   layers carry :x or :y."
  [leaf incoming-mapping]
  (let [merged (merge (:mapping leaf) incoming-mapping)]
    (cond-> leaf
      (seq merged)   (assoc :mapping merged)
      (empty? merged) (dissoc :mapping))))

(defn- extend-composite
  "Extend a composite. Aesthetic part of incoming-mapping merges into
   the root :mapping; position part appends a new sub-frame."
  [composite incoming-mapping]
  (let [incoming-pos   (position-mapping incoming-mapping)
        incoming-aesth (aesthetic-mapping incoming-mapping)
        with-aesth (if (seq incoming-aesth)
                     (update composite :mapping (fnil merge {}) incoming-aesth)
                     composite)]
    (if (seq incoming-pos)
      (update with-aesth :frames conj {:mapping incoming-pos :layers []})
      with-aesth)))

(defn- extend-or-promote
  "Dispatch `(pj/frame existing-frame incoming-mapping)`: composite
   inputs extend in place; leaves extend or promote depending on
   whether they already carry position."
  [fr incoming-mapping]
  (cond
    (frame/composite? fr)    (extend-composite fr incoming-mapping)
    (leaf-has-position? fr)  (promote-leaf fr incoming-mapping)
    :else                    (extend-leaf fr incoming-mapping)))

(defn- frame-from-data
  "Build a leaf-frame map from raw data and an already-normalized
   mapping (use {} for no mapping). Empty mapping is elided by
   normalize-frame downstream."
  [data mapping]
  (cond-> {:layers []}
    (some? data) (assoc :data data)
    (seq mapping) (assoc :mapping mapping)))

(declare frame)

(defn- pairs->rows
  "Detect whether `pairs` forms a rectangular M x N grid -- every
   combination of unique first-elements with unique second-elements,
   in cross order. Returns a vec of row-vecs when rectangular, nil
   otherwise. Requires M >= 2 and N >= 2 so a single row or column
   stays flat (not a grid)."
  [pairs]
  (let [pairs  (vec pairs)
        xs     (vec (distinct (map first pairs)))
        ys     (vec (distinct (map second pairs)))
        m      (count xs)
        n      (count ys)]
    (when (and (<= 2 m) (<= 2 n)
               (= (count pairs) (* m n))
               (= pairs (vec (for [x xs y ys] [x y]))))
      (mapv (fn [x] (mapv (fn [y] [x y]) ys)) xs))))

(defn- grid-composite
  "Build a 2D rows-of-cols composite from `base` (a leaf or composite)
   and a rectangular grid of [x-col y-col] pairs. Each cell becomes a
   leaf carrying only its position mapping; the base's :data,
   :mapping, :layers, and :opts move to the new composite's root so
   they inherit into every cell via resolve-tree. :share-scales is
   stamped as #{:x :y} so columns share x-axis domains and rows share
   y-axis domains -- SPLOM behavior.

   Each cell also carries :opts:
   - :suppress-legend on every cell -- one shared legend at composite
     level.
   - :suppress-x-label and :suppress-y-label on every cell -- the
     strip labels carry the axis-variable name; per-cell axis labels
     would duplicate them.
   - :suppress-x-ticks on every cell except the bottom row -- only
     the bottom row's tick numbers stay, since tick scales are
     shared down the column via :share-scales.
   - :suppress-y-ticks on every cell except the leftmost column --
     same reasoning, tick scales shared across the row.

   The composite root carries :grid-strip-labels so the compositor
   can draw column strip labels above the top row and row strip
   labels to the left of the leftmost column (matching the legacy
   SPLOM chrome)."
  [base rows]
  (let [root-data (:data base)
        root-m    (:mapping base)
        root-l    (:layers base)
        root-o    (:opts base)
        n-rows    (count rows)
        col->name (fn [c] (if (keyword? c) (name c) (str c)))
        ;; Each column shares its y column; each row shares its x
        ;; column. Strip labels live at the composite root, not on
        ;; individual cells, so cell layout stays untouched.
        col-labels (when (seq rows)
                     (mapv (fn [[_ y]] (col->name y)) (first rows)))
        row-labels (mapv (fn [row] (col->name (first (first row)))) rows)
        cells      (fn [row-idx row]
                     (let [bottom? (= row-idx (dec n-rows))]
                       (vec
                        (map-indexed
                         (fn [col-idx [x y]]
                           (let [leftmost? (zero? col-idx)]
                             {:mapping {:x x :y y}
                              :opts (cond-> {:suppress-legend true
                                             :suppress-x-label true
                                             :suppress-y-label true}
                                      (not bottom?)   (assoc :suppress-x-ticks true)
                                      (not leftmost?) (assoc :suppress-y-ticks true))
                              :layers []}))
                         row))))
        row-frames (vec
                    (map-indexed
                     (fn [row-idx row]
                       {:layout {:direction :horizontal}
                        :frames (cells row-idx row)})
                     rows))
        composite (cond-> {:layout             {:direction :vertical}
                           :share-scales       #{:x :y}
                           :grid-strip-labels  {:col-labels col-labels
                                                :row-labels row-labels}
                           :frames             row-frames}
                    (some? root-data) (assoc :data root-data)
                    (seq root-m)      (assoc :mapping root-m)
                    (seq root-l)      (assoc :layers root-l)
                    (seq root-o)      (assoc :opts root-o))]
    (prepare-frame composite)))

(defn- multi-pair-frame
  "Iteratively apply pj/frame to each column or pair in cols-or-pairs.
   The ground case -- x a non-frame -- first lifts x into a leaf via
   (pj/frame x). Each element in cols-or-pairs may be a column
   reference (keyword or string) -> univariate panel, or a two-element
   sequential -> bivariate panel. Any mixture is accepted.

   When the elements form a rectangular M x N grid of pairs (e.g. the
   output of pj/cross cols cols), the result is a nested rows-of-cols
   composite with :share-scales #{:x :y} -- the canonical SPLOM shape.
   Non-rectangular pair lists and mixed/univariate lists fall through
   to the flat per-element reduce."
  [x cols-or-pairs]
  (let [;; Bare data-only leaf (no inferred :mapping) -- each pair or
        ;; column in `items` contributes its own sub-frame, so the base
        ;; must not carry a position mapping of its own.
        base     (if (frame? x) x (prepare-frame (frame-from-data x {})))
        items    (vec cols-or-pairs)
        first-el (first items)]
    (cond
      ;; Univariate -- columns
      (or (keyword? first-el) (string? first-el))
      (reduce (fn [fr col] (frame fr col)) base items)

      ;; Pairs -- check for rectangular grid before falling back
      (sequential? first-el)
      (if-let [rows (pairs->rows items)]
        (grid-composite base rows)
        (reduce (fn [fr [a b]] (frame fr a b)) base items))

      :else
      (throw (ex-info
              (str "pj/frame multi-pair element must be a column "
                   "reference or a two-element sequential, got: "
                   (pr-str first-el))
              {:item first-el :cols-or-pairs cols-or-pairs})))))

(defn frame
  "Construct or extend a frame.

   On raw data (first argument is not itself a frame):
     (pj/frame)                                -- empty leaf
     (pj/frame data)                           -- leaf with data; on 1-3
                                                  column datasets the
                                                  mapping is auto-inferred
                                                  (:x, then :y, then :color)
                                                  so the frame renders without
                                                  an explicit mapping call
     (pj/frame data {:color :species})         -- leaf with aesthetic mapping
     (pj/frame data :x-col)                    -- leaf with {:x :x-col}
     (pj/frame data :x-col {:color :c})        -- univariate x + opts
     (pj/frame data :x-col :y-col)             -- leaf with :x and :y
     (pj/frame data :x-col :y-col {:color :c}) -- positional x/y + opts
     (pj/frame data [[:a :b] [:c :d]])         -- multi-pair: N bivariate panels
     (pj/frame data [:a :b :c])                -- multi-pair: N univariate panels

   Threaded over an existing frame (first argument is a frame):
     (pj/frame fr)                        -- no-op, returns fr unchanged
     (pj/frame fr :x-col :y-col)          -- extend a leaf-without-position,
                                             or promote a leaf-with-position
                                             into a 2-panel composite, or
                                             append a panel to a composite
     (pj/frame fr :x-col :y-col {:color :c}) -- same, with aesthetic routed
                                             to the composite root on promote
     (pj/frame fr {:color :c})            -- aesthetic-only: extend mapping
                                             or (on leaf-with-position) promote
     (pj/frame fr [[:a :b] [:c :d]])      -- multi-pair: append N panels
     (pj/frame fr (pj/cross cols cols))   -- SPLOM N^2 panels in one call

   For hand-built composite frames (nested :frames, explicit :weights,
   etc.) use pj/prepare-frame."
  ([] (prepare-frame {:layers []}))
  ([x]
   (cond
     (frame? x) x
     :else      (let [d (coerce-dataset x)
                      mapping (or (try-infer-mapping d) {})]
                  (prepare-frame (frame-from-data x mapping)))))
  ([x y]
   (cond
     (and (sequential? y) (not (map? y)))
     (multi-pair-frame x y)

     :else
     (let [mapping (if (map? y)
                     (or (warn-and-strip-unknown-opts
                          "pj/frame" y view-mapping-keys)
                         {})
                     {:x y})]
       (if (frame? x)
         (prepare-frame (extend-or-promote x mapping))
         (prepare-frame (frame-from-data x mapping))))))
  ([x y z]
   (if (map? z)
     ;; (pj/frame data x-col opts-map) -- univariate position plus opts
     (let [opts      (warn-and-strip-unknown-opts "pj/frame" z view-mapping-keys)
           data-over (:data opts)
           mapping   (-> opts (dissoc :data) (merge {:x y}))]
       (if (frame? x)
         (prepare-frame (extend-or-promote x mapping))
         (prepare-frame (frame-from-data (or data-over x) mapping))))
     (let [mapping {:x y :y z}]
       (if (frame? x)
         (prepare-frame (extend-or-promote x mapping))
         (prepare-frame (frame-from-data x mapping))))))
  ([x y z opts]
   (let [opts      (warn-and-strip-unknown-opts "pj/frame" opts view-mapping-keys)
         data-over (:data opts)
         mapping   (-> opts (dissoc :data) (merge {:x y :y z}))]
     (if (frame? x)
       (prepare-frame (extend-or-promote x mapping))
       (prepare-frame (frame-from-data (or data-over x) mapping))))))

(def ^:private position-aesthetic-keys
  "Mapping keys whose keyword values are always column references
   (no literal-value path). Used by with-data's attach-time
   validation. :color / :size / :alpha keyword values are also
   column refs, but the validator ignores string values since
   those are ambiguous (\"red\" might be a CSS color)."
  [:x :y :color :size :alpha :group :text :y-min :y-max :fill :shape])

(defn- column-refs-in-mapping [m]
  (keep #(let [v (get m %)]
           (when (keyword? v) v))
        position-aesthetic-keys))

(defn- column-refs-in-frame
  "Collect every keyword column reference used by a frame's :mapping,
   :layers, :frames (recursively), and :facet-col/:facet-row on opts."
  [fr]
  (distinct
   (concat
    (column-refs-in-mapping (or (:mapping fr) {}))
    (mapcat #(column-refs-in-mapping (or (:mapping %) {})) (:layers fr))
    (mapcat column-refs-in-frame (:frames fr))
    (keep #(let [v (get-in fr [:opts %])]
             (when (keyword? v) v))
          [:facet-col :facet-row]))))

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
      (throw (ex-info (str "Cannot attach data: frame references column(s) "
                           missing
                           " not present in the dataset. Available columns: "
                           (vec (sort cols)) ".")
                      {:missing missing :available (vec (sort cols))})))))

(defn with-data
  "Supply or replace the top-level dataset on a frame.
   Useful for building a template once and applying it to different
   datasets:

       (def template (-> (pj/frame)
                         (pj/frame :x :y {:color :group})
                         pj/lay-point
                         (pj/lay-smooth {:stat :linear-model})))

       (-> template (pj/with-data my-data))
       (-> template (pj/with-data other-data))

   At attach time, every keyword column reference in the template's
   mapping, layers, sub-frames, and facet options must exist in the
   dataset -- otherwise an error is thrown naming the missing columns
   and listing what is available. Per-layer / per-sub-frame `:data`
   still overrides the top-level data.
   (with-data fr data)"
  [sk data]
  (let [fr (ensure-frame sk)
        ds (coerce-dataset data)]
    (when ds
      (validate-columns-present (column-refs-in-frame fr) ds))
    (prepare-frame (assoc fr :data ds))))

(defn- check-facet-keys
  "Throw a helpful error if a mapping or layer-options map contains
   :facet-col / :facet-row / :facet-x / :facet-y. Faceting is
   plot-level and is set via pj/facet or pj/facet-grid, never via a
   view or layer options map -- the silent-strip behaviour on such
   keys confused users (user-report-2 Issue 5)."
  [context m]
  (let [fk (select-keys m [:facet-col :facet-row :facet-x :facet-y])]
    (when (seq fk)
      (throw (ex-info (str "Faceting is plot-level, not " context "-level. "
                           "Use (pj/facet sk col) or (pj/facet-grid sk col-col row-col) "
                           "instead of putting "
                           (str/join " / " (map name (keys fk)))
                           " in a " context "'s options map.")
                      fk)))))

(defn- add-view-layer-to-composite
  "Walk the composite depth-first and append the layer to the last
   leaf whose effective :x/:y (after ancestor-merge) match
   `position-mapping`. On miss, append a fresh leaf at the root level."
  [fr position-mapping layer]
  (let [match-path (frame/last-matching-leaf-path fr position-mapping)]
    (if (some? match-path)
      (update-in fr
                 (conj (frame/path->update-in-path match-path) :layers)
                 (fnil conj []) layer)
      (update fr :frames (fnil conj [])
              {:mapping position-mapping :layers [layer]}))))

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

(defn- registered-marks []
  (->> (methods extract/extract-layer)
       keys
       (keep (fn [k] (cond (keyword? k) k
                           (and (vector? k) (keyword? (first k))) (first k))))
       (remove #{:default})
       set))

(defn- registered-stats []
  (->> (methods stat/compute-stat)
       keys
       (keep (fn [k] (cond (keyword? k) k
                           (and (vector? k) (keyword? (first k))) (first k))))
       (remove #{:default})
       set))

(defn- validate-mark-stat [fn-name opts]
  (when-let [m (:mark opts)]
    (when-not (contains? (registered-marks) m)
      (throw (ex-info (str fn-name " got :mark " (pr-str m)
                           ", which is not a registered mark. Registered marks: "
                           (vec (sort (registered-marks))))
                      {:mark m :registered (sort (registered-marks))}))))
  (when-let [s (:stat opts)]
    (when-not (contains? (registered-stats) s)
      (throw (ex-info (str fn-name " got :stat " (pr-str s)
                           ", which is not a registered stat. Registered stats: "
                           (vec (sort (registered-stats))))
                      {:stat s :registered (sort (registered-stats))})))))

(def ^:private layer-structural-keys
  "User-supplied layer options that are layer-structural (not
   column-to-aesthetic mappings). Promoted to top-level keys on the
   layer map; `:mapping` holds only true mappings."
  #{:stat :position :mark})

(defn- build-layer
  "Build a layer map from a layer-type-key and optional opts.
   Extracts :data if present. Extracts :stat, :position, :mark as
   first-class sibling keys -- :mapping holds only column-to-aesthetic
   bindings. Warns and strips unrecognized option keys. Rejects
   unknown :mark or :stat keywords (since both are universal layer
   options, a typo would silently fall through the accept-list)."
  [layer-type-key opts]
  (when opts
    (check-facet-keys "layer" opts)
    (check-position-mapping (str "lay-" (name layer-type-key)) opts)
    (validate-mark-stat (str "lay-" (name layer-type-key)) opts))
  (let [opts (if (and opts (keyword? layer-type-key))
               (let [reg (layer-type/lookup layer-type-key)
                     accepted (-> (set layer-type/universal-layer-options)
                                  (into (:accepts reg))
                                  (set/difference (set (:rejects reg))))]
                 (warn-and-strip-unknown-opts (str "lay-" (name layer-type-key))
                                              opts accepted))
               opts)
        opts-map (or opts {})
        d (:data opts-map)
        structural (select-keys opts-map layer-structural-keys)
        mapping (apply dissoc opts-map :data layer-structural-keys)]
    (cond-> (merge {:layer-type layer-type-key
                    :mapping mapping}
                   structural)
      d (assoc :data (coerce-dataset d)))))

(defn lay
  "Add a root-scope layer. The layer attaches to :layers and flows via
   frame/resolve-tree to every descendant leaf (composite) or renders
   on the single panel (leaf)."
  ([sk-or-data layer-type-key]
   (lay sk-or-data layer-type-key nil))
  ([sk-or-data layer-type-key opts]
   (let [layer (build-layer layer-type-key opts)]
     (update (ensure-frame sk-or-data) :layers (fnil conj []) layer))))

(defn- x-only?
  "True if layer-type-key is registered as x-only (rejects :y column)."
  [layer-type-key]
  (:x-only (layer-type/lookup layer-type-key)))

(defn- lay-on-frame
  "Append a layer to a frame following the DFS-last identity rule.

   Composite + position: the layer lands on the last leaf whose
   effective :x/:y match (via add-view-layer-to-composite), or a
   fresh sub-frame is appended at the root.

   Leaf whose own :mapping has no :x/:y, called with a position:
   extend the leaf's :mapping with the position and append a bare
   layer. (Matches the old view-creation semantics where a
   position-bearing lay-* on a bare frame sets the frame's position.)

   Leaf whose own :mapping already has position, called with a
   position: append a layer carrying its own :mapping so downstream
   partitioning treats it as panel-origin.

   No position (leaf or composite + aesthetic-only): append the
   bare / aesthetic layer to :layers."
  [fr layer-type-key position-mapping opts]
  (let [bare-layer (elide-empty-maps (build-layer layer-type-key opts))
        frame-pos? (or (:x (:mapping fr)) (:y (:mapping fr)))]
    (cond
      (and (frame/composite? fr) (seq position-mapping))
      (add-view-layer-to-composite fr position-mapping bare-layer)

      (and (seq position-mapping) (not frame-pos?))
      (-> fr
          (update :mapping (fnil merge {}) position-mapping)
          (update :layers (fnil conj []) bare-layer))

      (seq position-mapping)
      (update fr :layers (fnil conj [])
              (elide-empty-maps
               (update bare-layer :mapping (fnil merge {}) position-mapping)))

      :else
      (update fr :layers (fnil conj []) bare-layer))))

(defn- lay-layer-type
  "Shared implementation for all lay-* functions.

   Raw data coerces to a fresh leaf frame. Frames (leaf or composite)
   pass through. All dispatches then route through lay-on-frame, which
   follows the DFS-last identity rule in frame_rules.clj.

   1-arity: auto-infer columns for a fresh leaf-with-data (<= 3 cols),
            otherwise append a bare/aesthetic layer.
   2-arity: keyword/string -> position-bearing layer;
            vector of columns/pairs -> multi-pair broadcast;
            map -> aesthetic-only layer with opts.
   3-arity: two keywords -> bivariate; keyword+map -> univariate+opts;
            vector+map -> multi-pair broadcast with opts.
   4-arity: bivariate layer with opts."
  ([layer-type-key sk-or-data]
   (let [was-raw? (not (frame? sk-or-data))
         fr (ensure-frame sk-or-data)
         d (:data fr)]
     (if (and was-raw? d)
       ;; Raw-data 1-arity: auto-infer columns from the first 1-3 columns
       ;; so `(pj/lay-point data)` still produces a renderable plot.
       ;; Threaded `(-> data pj/frame pj/lay-point)` works too because
       ;; pj/frame 1-arity sets the mapping itself for 1-3 col data.
       ;; A frame with no mapping that reaches here (e.g. iris with 7
       ;; cols through pj/frame, or a hand-built map) stays bare so the
       ;; "root layer flows to every panel" M4 pattern keeps working.
       (let [mapping (auto-infer-mapping layer-type-key d)]
         (lay-on-frame (assoc fr :mapping mapping)
                       layer-type-key nil nil))
       (lay-on-frame fr layer-type-key nil nil))))
  ([layer-type-key sk-or-data x-or-opts]
   (let [fr (ensure-frame sk-or-data)]
     (cond
       (map? x-or-opts)
       (lay-on-frame fr layer-type-key nil x-or-opts)

       (or (keyword? x-or-opts) (string? x-or-opts))
       (lay-on-frame fr layer-type-key {:x x-or-opts} nil)

       ;; Sequential -> build a multi-panel composite via pj/frame, then
       ;; attach the layer at the root so it flows to every panel via
       ;; resolve-tree.
       (sequential? x-or-opts)
       (lay-on-frame (frame fr x-or-opts) layer-type-key nil nil)

       :else
       (lay-on-frame fr layer-type-key nil nil))))
  ([layer-type-key sk-or-data x y-or-opts]
   (let [fr (ensure-frame sk-or-data)]
     (cond
       ;; Parallel vectors -> build a multi-panel composite via pj/frame
       ;; with paired x/y, then attach the bare layer at the root so it
       ;; flows to every panel.
       (and (sequential? x) (sequential? y-or-opts))
       (lay-on-frame (frame fr (mapv vector x y-or-opts))
                     layer-type-key nil nil)

       ;; Sequential + opts -> build a multi-panel composite via pj/frame,
       ;; then attach a layer with opts at the root.
       (and (sequential? x) (map? y-or-opts))
       (lay-on-frame (frame fr x) layer-type-key nil y-or-opts)

       (map? y-or-opts)
       (lay-on-frame fr layer-type-key {:x x} y-or-opts)

       (or (keyword? y-or-opts) (string? y-or-opts))
       (do (when (x-only? layer-type-key)
             (throw (ex-info (str "lay-" (name layer-type-key) " uses only the x column; do not pass a y column")
                             {:layer-type layer-type-key :x x :y y-or-opts})))
           (lay-on-frame fr layer-type-key {:x x :y y-or-opts} nil))

       :else
       (lay-on-frame fr layer-type-key {:x x} y-or-opts))))
  ([layer-type-key sk-or-data x y opts]
   (when (x-only? layer-type-key)
     (throw (ex-info (str "lay-" (name layer-type-key) " uses only the x column; do not pass a y column")
                     {:layer-type layer-type-key :x x :y y})))
   (lay-on-frame (ensure-frame sk-or-data) layer-type-key {:x x :y y} opts)))

(defn lay-point
  "Add a :point (scatter) layer to a frame.
   Without columns -> bare layer at the frame's root (flows to every leaf).
   With columns -> position-bearing layer (attaches to the matching leaf
   via DFS-last identity, or appends a new sub-frame on miss).
   (lay-point fr)                         -- bare layer at root
   (lay-point fr {:color :species})        -- bare layer with aesthetic opts
   (lay-point data :x :y)                 -- coerce data to a leaf, then attach
   (lay-point data :x :y {:color :c})     -- same with aesthetic opts"
  ([sk-or-data] (lay-layer-type :point sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :point sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :point sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :point sk-or-data x y opts)))

(defn- last-opts
  "Return the trailing opts map from a lay-* arg list (or nil)."
  [args]
  (let [last-arg (last args)]
    (when (map? last-arg) last-arg)))

(defn- positional-hint
  "If the user passed a non-map last arg (e.g. a bare number), suggest
   wrapping it in an opts map. args here are the trailing args after
   the frame -- so the bad shape is `(lay-rule-h fr 3)` not `(lay-rule-h fr)`."
  [args]
  (when (and (seq args) (not (map? (last args))))
    (str " Got " (pr-str (last args)) " as the last argument; did you forget"
         " to wrap it in an opts map?")))

(def ^:private rule-position-key
  "Per-layer-type required position key for pj/lay-rule-*."
  {:rule-h :y-intercept :rule-v :x-intercept})

(def ^:private band-position-keys
  "Per-layer-type required [lo-key hi-key] for pj/lay-band-*."
  {:band-h [:y-min :y-max] :band-v [:x-min :x-max]})

(defn- assert-rule-opts! [layer-type-key args]
  (let [opts (last-opts args)
        k (rule-position-key layer-type-key)
        v (get opts k)]
    (when-not (and (number? v) (Double/isFinite (double v)))
      (throw (ex-info (str "lay-" (name layer-type-key) " requires a finite numeric " k " in its opts map. "
                           "Example: (pj/lay-" (name layer-type-key) " sk {" k " 3.0})."
                           (positional-hint args))
                      {:layer-type layer-type-key :opts opts})))))

(defn- assert-band-opts! [layer-type-key args]
  (let [opts (last-opts args)
        [lo-k hi-k] (band-position-keys layer-type-key)
        lo (get opts lo-k) hi (get opts hi-k)]
    (when-not (and (number? lo) (number? hi)
                   (Double/isFinite (double lo))
                   (Double/isFinite (double hi)))
      (throw (ex-info (str "lay-" (name layer-type-key) " requires finite numeric " lo-k " and " hi-k " in its opts map. "
                           "Example: (pj/lay-" (name layer-type-key) " sk {" lo-k " 2.0 " hi-k " 4.0})."
                           (positional-hint args))
                      {:layer-type layer-type-key :opts opts})))
    (when-not (<= (double lo) (double hi))
      (throw (ex-info (str "lay-" (name layer-type-key) " requires " lo-k " <= " hi-k ", got " lo-k " " lo " " hi-k " " hi ". "
                           "Swap the arguments or check the source of the values.")
                      {:layer-type layer-type-key :opts opts})))))

(defn lay-rule-h
  "Add :rule-h layer -- horizontal reference line at y = y-intercept.
   Position comes from opts (not data columns); :y-intercept is required.
   Accepts :y-intercept (required), :color (literal string), :alpha.
   The 4-arity finds or creates a sub-frame with these x/y columns
   and attaches the rule there (only panels matching that leaf show
   it).
   (lay-rule-h sk {:y-intercept 3})           -- root-level, flows to every panel
   (lay-rule-h sk :x :y {:y-intercept 3})     -- panel-scope (columns pick or create a sub-frame)
   (lay-rule-h sk {:y-intercept 3 :color \"red\" :alpha 0.5})"
  ([sk-or-data x-or-opts] (assert-rule-opts! :rule-h [x-or-opts]) (lay-layer-type :rule-h sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (assert-rule-opts! :rule-h [y-or-opts]) (lay-layer-type :rule-h sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (assert-rule-opts! :rule-h [opts]) (lay-layer-type :rule-h sk-or-data x y opts)))

(defn lay-rule-v
  "Add :rule-v layer -- vertical reference line at x = x-intercept.
   Position comes from opts (not data columns); :x-intercept is required.
   Accepts :x-intercept (required), :color (literal string), :alpha.
   The 4-arity finds or creates a sub-frame with these x/y columns
   and attaches the rule there (only panels matching that leaf show
   it).
   (lay-rule-v sk {:x-intercept 5})           -- root-level, flows to every panel
   (lay-rule-v sk :x :y {:x-intercept 5})     -- panel-scope (columns pick or create a sub-frame)
   (lay-rule-v sk {:x-intercept 5 :color \"red\" :alpha 0.5})"
  ([sk-or-data x-or-opts] (assert-rule-opts! :rule-v [x-or-opts]) (lay-layer-type :rule-v sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (assert-rule-opts! :rule-v [y-or-opts]) (lay-layer-type :rule-v sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (assert-rule-opts! :rule-v [opts]) (lay-layer-type :rule-v sk-or-data x y opts)))

(defn lay-band-h
  "Add :band-h layer -- horizontal shaded band between y = y-min and y = y-max.
   Position comes from opts (not data columns); :y-min and :y-max are
   required and :y-min must be <= :y-max.
   Accepts :y-min (required), :y-max (required), :color (literal string), :alpha.
   The 4-arity finds or creates a sub-frame with these x/y columns
   and attaches the band there (only panels matching that leaf show
   it).
   (lay-band-h sk {:y-min 2 :y-max 4})            -- root-level, flows to every panel
   (lay-band-h sk :x :y {:y-min 2 :y-max 4})      -- panel-scope (columns pick or create a sub-frame)
   (lay-band-h sk {:y-min 2 :y-max 4 :color \"blue\" :alpha 0.3})"
  ([sk-or-data x-or-opts] (assert-band-opts! :band-h [x-or-opts]) (lay-layer-type :band-h sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (assert-band-opts! :band-h [y-or-opts]) (lay-layer-type :band-h sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (assert-band-opts! :band-h [opts]) (lay-layer-type :band-h sk-or-data x y opts)))

(defn lay-band-v
  "Add :band-v layer -- vertical shaded band between x = x-min and x = x-max.
   Position comes from opts (not data columns); :x-min and :x-max are
   required and :x-min must be <= :x-max.
   Accepts :x-min (required), :x-max (required), :color (literal string), :alpha.
   The 4-arity finds or creates a sub-frame with these x/y columns
   and attaches the band there (only panels matching that leaf show
   it).
   (lay-band-v sk {:x-min 4 :x-max 6})            -- root-level, flows to every panel
   (lay-band-v sk :x :y {:x-min 4 :x-max 6})      -- panel-scope (columns pick or create a sub-frame)
   (lay-band-v sk {:x-min 4 :x-max 6 :color \"blue\" :alpha 0.3})"
  ([sk-or-data x-or-opts] (assert-band-opts! :band-v [x-or-opts]) (lay-layer-type :band-v sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (assert-band-opts! :band-v [y-or-opts]) (lay-layer-type :band-v sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (assert-band-opts! :band-v [opts]) (lay-layer-type :band-v sk-or-data x y opts)))

(defn lay-line
  "Add :line layer type --connected line through data points.
   Requires x (numerical) and y (numerical).
   Accepts :color, :alpha, :size (stroke width), :nudge-x, :nudge-y."
  ([sk-or-data] (lay-layer-type :line sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :line sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :line sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :line sk-or-data x y opts)))

(defn lay-step
  "Add :step layer type --staircase line (horizontal then vertical).
   Requires x and y (both numerical)."
  ([sk-or-data] (lay-layer-type :step sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :step sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :step sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :step sk-or-data x y opts)))

(defn lay-area
  "Add :area layer type --filled region between y and the baseline.
   Requires x and y (both numerical). Accepts :color, :alpha."
  ([sk-or-data] (lay-layer-type :area sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :area sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :area sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :area sk-or-data x y opts)))

(defn lay-histogram
  "Add :histogram layer type --bin numerical values into bars.
   X-only: pass one column. Accepts :bins (count), :binwidth, :color,
   :normalize (:density for density-normalized heights)."
  ([sk-or-data] (lay-layer-type :histogram sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :histogram sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :histogram sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :histogram sk-or-data x y opts)))

(defn lay-bar
  "Add :bar layer type --count occurrences of each category.
   X-only: pass one categorical column. Accepts :color for grouped bars."
  ([sk-or-data] (lay-layer-type :bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :bar sk-or-data x y opts)))

(defn lay-value-bar
  "Add :value-bar layer type --bars with pre-computed heights.
   Requires categorical x and numerical y. Unlike :bar (which counts),
   :value-bar uses the y value directly as the bar height."
  ([sk-or-data] (lay-layer-type :value-bar sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :value-bar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :value-bar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :value-bar sk-or-data x y opts)))

(defn lay-smooth
  "Add :smooth layer type --a smoothed trend line.
   Defaults to LOESS (local regression). Pass {:stat :linear-model} for
   ordinary least squares instead. Requires x and y (both numerical).
   Accepts {:confidence-band true} for a confidence ribbon."
  ([sk-or-data] (lay-layer-type :smooth sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :smooth sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :smooth sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :smooth sk-or-data x y opts)))

(defn lay-density
  "Add :density layer type --kernel density estimate curve.
   X-only: pass one numerical column. Accepts :color, :bandwidth."
  ([sk-or-data] (lay-layer-type :density sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :density sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :density sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :density sk-or-data x y opts)))

(defn lay-tile
  "Add :tile layer type --colored grid cells (heatmap).
   With :fill option: pre-computed tile colors from a column.
   Without :fill: auto-binned 2D histogram (stat :bin2d)."
  ([sk-or-data] (lay-layer-type :tile sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :tile sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :tile sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :tile sk-or-data x y opts)))

(defn lay-density-2d
  "Add :density-2d layer type --2D kernel density heatmap.
   Requires x and y (both numerical). Produces a smoothed density
   surface as colored tiles with a continuous gradient legend."
  ([sk-or-data] (lay-layer-type :density-2d sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :density-2d sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :density-2d sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :density-2d sk-or-data x y opts)))

(defn lay-contour
  "Add :contour layer type --iso-density contour lines from 2D KDE.
   Requires x and y (both numerical). Accepts {:levels 10} for
   the number of contour levels."
  ([sk-or-data] (lay-layer-type :contour sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :contour sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :contour sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :contour sk-or-data x y opts)))

(defn lay-boxplot
  "Add :boxplot layer type --box-and-whisker plot.
   Requires categorical x and numerical y. Shows median, quartiles,
   whiskers, and outliers. Accepts :color for grouped boxplots."
  ([sk-or-data] (lay-layer-type :boxplot sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :boxplot sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :boxplot sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :boxplot sk-or-data x y opts)))

(defn lay-violin
  "Add :violin layer type --mirrored density estimate by category.
   Requires categorical x and numerical y. Accepts :color, :bandwidth."
  ([sk-or-data] (lay-layer-type :violin sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :violin sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :violin sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :violin sk-or-data x y opts)))

(defn lay-ridgeline
  "Add :ridgeline layer type --stacked density curves by category.
   Requires categorical x and numerical y. Categories stack vertically
   with density curves rendered horizontally."
  ([sk-or-data] (lay-layer-type :ridgeline sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :ridgeline sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :ridgeline sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :ridgeline sk-or-data x y opts)))

(defn lay-summary
  "Add :summary layer type --mean ± standard error per category.
   Requires categorical x and numerical y. Shows a point at the mean
   with error bars for ± 1 SE. Accepts :color for grouped summaries."
  ([sk-or-data] (lay-layer-type :summary sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :summary sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :summary sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :summary sk-or-data x y opts)))

(defn lay-errorbar
  "Add :errorbar layer type --vertical error bars from pre-computed bounds.
   Requires x, y, and {:y-min :col :y-max :col} for lower/upper bounds."
  ([sk-or-data] (lay-layer-type :errorbar sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :errorbar sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :errorbar sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :errorbar sk-or-data x y opts)))

(defn lay-lollipop
  "Add :lollipop layer type --dot on a stem from the baseline.
   Requires categorical x and numerical y. Like value-bar but with
   a circle+line instead of a filled rectangle."
  ([sk-or-data] (lay-layer-type :lollipop sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :lollipop sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :lollipop sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :lollipop sk-or-data x y opts)))

(defn lay-text
  "Add :text layer type --text labels at data coordinates.
   Requires x, y, and {:text :column} for label content."
  ([sk-or-data] (lay-layer-type :text sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :text sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :text sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :text sk-or-data x y opts)))

(defn lay-label
  "Add :label layer type --text labels with background box at data coordinates.
   Like :text but with a rectangular background for readability."
  ([sk-or-data] (lay-layer-type :label sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :label sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :label sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :label sk-or-data x y opts)))

(defn lay-rug
  "Add :rug layer type --short tick marks along the axis showing individual values.
   X-only: pass one column. Often layered with density or scatter."
  ([sk-or-data] (lay-layer-type :rug sk-or-data))
  ([sk-or-data x-or-opts] (lay-layer-type :rug sk-or-data x-or-opts))
  ([sk-or-data x y-or-opts] (lay-layer-type :rug sk-or-data x y-or-opts))
  ([sk-or-data x y opts] (lay-layer-type :rug sk-or-data x y opts)))

(defn- deep-merge
  "Recursively merge maps. Non-map values are overwritten."
  [a b]
  (if (and (map? a) (map? b))
    (merge-with deep-merge a b)
    b))

(defn- update-opts
  "Update the root :opts of a frame. Non-frame inputs are coerced via
   ensure-frame first. resolve-tree merges root :opts into every leaf,
   so root-level writes act as plot-level options across the whole
   tree."
  [sk-or-frame f & args]
  (apply update (ensure-frame sk-or-frame) :opts f args))

(defn options
  "Set plot-level options (title, labels, width, height, etc.).
   Nested maps (e.g. :theme) are deep-merged.
   :width and :height are coerced to long (rounded) so the plan carries
   integer pixel dimensions through to render. On a composite frame
   the options attach to the root so every descendant leaf inherits
   them via resolve-tree."
  [sk opts]
  (let [opts (warn-and-strip-unknown-opts "pj/options" opts plot-options-keys)
        opts (reduce (fn [m k]
                       (if-let [v (get m k)]
                         (do (when-not (and (number? v) (pos? v))
                               (throw (ex-info (str k " must be a positive number, got: " (pr-str v))
                                               {:option k :value v})))
                             (assoc m k (long (Math/round (double v)))))
                         m))
                     opts
                     [:width :height])]
    (update-opts sk deep-merge opts)))

(defn- reject-composite-for-facet
  "Throw if the input is a composite frame. Facet on composites would
   cross the facet grid with the composite grid, which is deferred
   (see dev-notes/facet-composite-deferral.md)."
  [fr]
  (when (and (frame? fr) (frame/composite? fr))
    (throw (ex-info (str "pj/facet and pj/facet-grid are not yet supported on composite frames. "
                         "The facet grid would cross the composite layout; see "
                         "dev-notes/facet-composite-deferral.md. Flatten to a single leaf "
                         "(or wait for Option C).")
                    {:frame-kind :composite}))))

(defn facet
  "Facet a frame by a column.
   Direction is :col (default, horizontal row) or :row (vertical column).
   Faceting is plot-level -- every panel is faceted the same way.
   Composite frames are not supported yet (see
   dev-notes/facet-composite-deferral.md)."
  ([sk col] (facet sk col :col))
  ([sk col direction]
   (reject-composite-for-facet sk)
   (let [k (case direction :col :facet-col :row :facet-row)]
     (update-opts sk assoc k col))))

(defn facet-grid
  "Facet a frame by two columns (2D grid).
   Faceting is plot-level -- every panel is faceted the same way.
   Composite frames are not supported yet (see
   dev-notes/facet-composite-deferral.md)."
  [sk col-col row-col]
  (reject-composite-for-facet sk)
  (update-opts sk assoc :facet-col col-col :facet-row row-col))

(def ^:private valid-scale-types
  "Scale types accepted by pj/scale. :linear and :log are the two
   continuous types; :categorical lets users supply an explicit ordering
   via a :domain spec for categorical axes."
  #{:linear :log :categorical})

(defn scale
  "Set axis scale on a frame. Scale is plot-level -- it applies
   across every panel. Accepts a type keyword or a scale spec map
   with :type, optional :domain, and optional :breaks (explicit tick
   locations). On a composite frame the scale attaches to the root so
   every descendant leaf inherits it via resolve-tree.
   (scale fr :x :log)                                -- log scale on x-axis
   (scale fr :x {:type :categorical :domain [...]})  -- explicit category order
   (scale fr :y {:type :linear :breaks [0 5 10]})    -- pin tick locations
   (scale fr :y {:type :log :domain [1 1000]})       -- log scale with explicit range"
  [fr channel scale-type]
  (let [k (case channel :x :x-scale :y :y-scale
                (throw (ex-info (str "Scale channel must be :x or :y, got: " channel)
                                {:channel channel})))
        type-kw (if (map? scale-type) (:type scale-type) scale-type)]
    (when-not (or (nil? type-kw) (valid-scale-types type-kw))
      (throw (ex-info (str "Unknown scale type: " type-kw
                           ". Supported: " (vec (sort valid-scale-types)))
                      {:scale-type type-kw :supported (vec (sort valid-scale-types))})))
    (update-opts fr assoc k (if (map? scale-type)
                              (merge {:type :linear} scale-type)
                              {:type scale-type}))))

(defn coord
  "Set coordinate transform on a frame. Coord is plot-level -- it
   applies across every panel. On a composite frame the coord attaches
   to the root so every descendant leaf inherits it via resolve-tree.
   (coord fr :flip) -- flipped coordinates."
  [fr coord-type]
  (when-not (#{:cartesian :flip :polar :fixed} coord-type)
    (throw (ex-info (str "Coordinate must be :cartesian, :flip, :polar, or :fixed, got: " coord-type)
                    {:coord coord-type})))
  (update-opts fr assoc :coord coord-type))

(defn draft
  "Flatten a leaf frame into a draft -- a vector of flat maps, one per
   applicable layer, with all scope merged: data, mappings, and layer
   type fully determined.
   (draft fr)"
  [fr]
  (frame/leaf->draft (ensure-frame fr)))

(defn plan
  "Convert a frame into a plan. Each leaf is one panel. On a composite
   frame, returns a wrapper plan with :composite? true and :sub-plots
   tying each leaf path to its rect and sub-plan.
   (plan fr)
   (plan fr {:title \"My Plot\"})"
  ([sk]
   (when (plan? sk)
     (throw (ex-info (str "pj/plan expects a frame, not a plan. "
                          "Use the plan directly, or call pj/plot on the frame.")
                     {:got :plan})))
   (let [fr (ensure-frame sk)]
     (if (frame/composite? fr)
       (compositor/composite->plan fr)
       (plan/draft->plan (frame/leaf->draft fr) (:opts fr {})))))
  ([sk opts]
   (plan (options sk opts))))

(defn plot
  "Render a frame to SVG (or interactive HTML if tooltip/brush is
   set). On a composite frame, leaves are rendered individually and
   tiled via the compositor's layout.
   (plot fr)
   (plot fr {:width 800 :title \"My Plot\"})"
  ([sk]
   (let [fr (ensure-frame sk)]
     (if (frame/composite? fr)
       (compositor/composite->plot fr)
       (render-impl/plan->plot (plan fr) :svg (:opts fr {})))))
  ([sk opts]
   (plot (options sk opts))))

;; ---- SVG Summary ----

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts -- useful for asserting
   plot structure.
   Accepts SVG hiccup or a frame (auto-renders to SVG first).
   (svg-summary (plot fr))  -- summary of rendered SVG
   (svg-summary my-frame)   -- auto-renders frame (leaf or composite)"
  ([svg-or-frame]
   (if (frame? svg-or-frame)
     (svg/svg-summary (plot svg-or-frame))
     (svg/svg-summary svg-or-frame)))
  ([svg-or-frame theme]
   (if (frame? svg-or-frame)
     (svg/svg-summary (plot svg-or-frame) theme)
     (svg/svg-summary svg-or-frame theme))))

;; ---- Multi-Plot Composition ----

(defn- coerce-arrange-input
  "Turn one pj/arrange input into a leaf-frame plain map. Accepts
   frame-shaped leaf maps (passed through). Anything else -- including
   pre-rendered hiccup -- throws with guidance."
  [p idx]
  (cond
    (and (frame? p) (frame/leaf? p)) p
    (and (frame? p) (frame/composite? p))
    (throw (ex-info (str "pj/arrange input at index " idx
                         " is a composite frame. Nested arrangements "
                         "are not supported yet -- flatten first, or "
                         "open an issue.")
                    {:index idx}))
    :else
    (throw (ex-info (str "pj/arrange input at index " idx
                         " must be a leaf frame. Got: "
                         (pr-str (type p))
                         ". Pre-rendered hiccup is not supported; wrap "
                         "hiccup yourself with `[:div ...]` if you want "
                         "raw composition.")
                    {:index idx :type (type p)}))))

(defn- render-composite
  "Kindly render function for a composite frame returned by pj/arrange.
   Captures the config snapshot so theme/palette/config bindings at
   construction time survive into render time."
  [captured-config]
  (fn [composite]
    (let [fmt (or (:format (:opts composite)) :svg)]
      (if captured-config
        (binding [defaults/*config* captured-config]
          (compositor/composite->plot composite fmt))
        (compositor/composite->plot composite fmt)))))

(defn arrange
  "Arrange multiple leaf frames in a grid. Returns a composite frame
   that renders through the compositor via membrane -- so `:svg`,
   `:bufimg`, and any other membrane target work uniformly.

   Inputs must be leaf frames. Pre-rendered hiccup is not accepted;
   build your own `[:div ...]` if you need to combine already-rendered
   values outside the library.

   Opts:
     :cols N          explicit column count (default: min(4, n-plots))
     :title STRING    centered title band above the grid
     :width W         total composite width in pixels
     :height H        total composite height in pixels
     :share-scales S  subset of #{:x :y} shared across cells (default: #{})

   (arrange [fr-a fr-b])                           -- 1x2 row
   (arrange [fr-a fr-b fr-c] {:cols 2 :width 900}) -- 2x2 grid (wraps)
   (arrange [[fr-a fr-b] [fr-c fr-d]])             -- explicit 2x2 grid"
  ([plots] (arrange plots {}))
  ([plots opts]
   (let [cfg (defaults/config)
         {:keys [cols title share-scales]
          :or {share-scales #{}}} opts
         width  (or (:width opts)  (:width cfg))
         height (or (:height opts) (:height cfg))
         nested? (and (sequential? plots)
                      (sequential? (first plots))
                      (not (keyword? (ffirst plots))))
         rows-in (if nested? (vec plots) [(vec plots)])
         flat-plots (vec (apply concat rows-in))
         n-plots (count flat-plots)
         _ (when (zero? n-plots)
             (throw (ex-info "pj/arrange requires at least one plot." {:plots plots})))
         leaves (vec (map-indexed (fn [i p] (coerce-arrange-input p i)) flat-plots))
         n-cols (or cols
                    (if nested? (count (first rows-in))
                        (min 4 n-plots)))
         _ (when-not (pos? (long n-cols))
             (throw (ex-info ":cols must be a positive integer." {:cols cols})))
         row-partitions (if nested?
                          (map #(mapv (fn [i] (nth leaves i))
                                      (range (reduce + (map count (take % rows-in)))
                                             (reduce + (map count (take (inc %) rows-in)))))
                               (range (count rows-in)))
                          (partition-all n-cols leaves))
         row-frames (mapv (fn [row]
                            {:layout {:direction :horizontal}
                             :frames (vec row)})
                          row-partitions)
         composite (cond-> {:opts (cond-> {:width  (long (Math/round (double width)))
                                           :height (long (Math/round (double height)))}
                                    title (assoc :title title))
                            :layout {:direction :vertical}
                            :frames row-frames}
                     (seq share-scales) (assoc :share-scales (set share-scales)))]
     (kind/fn composite
       {:kindly/f (render-composite defaults/*config*)}))))

;; ---- Save ----

(defn save
  "Save a plot to an SVG file.
   fr   -- a frame.
   path -- file path (string or java.io.File).
   opts -- same options as plot (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save my-frame \"plot.svg\")
   (save my-frame \"plot.svg\" {:width 800 :height 600})"
  ([fr path]
   (save fr path {}))
  ([fr path opts]
   (let [path-str (str path)
         fr (ensure-frame fr)]
     (when-not (.endsWith path-str ".svg")
       (println (str "Warning: save produces SVG output, but path does not end with .svg: " path-str)))
     (let [fr (if (seq opts) (options fr opts) fr)
           ;; Composites render via the compositor (one membrane tree
           ;; built by tiling each leaf's plan). Plain leaves render
           ;; via the per-plan path. Without this branch, composite
           ;; frames save as empty SVG because the top-level plan
           ;; carries :sub-plots rather than :panels.
           svg-hiccup (if (frame/composite? fr)
                        (compositor/composite->plot fr :svg)
                        (render-impl/plan->plot (plan fr) :svg (:opts fr {})))]
       (spit path (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                       (svg/hiccup->svg-str svg-hiccup)))
       path))))

(defn save-png
  "Save a plot to a PNG file via membrane's Java2D backend.
   fr   -- a frame.
   path -- file path (string or java.io.File).
   opts -- same options as save (:width, :height, :title, :theme, etc.).
   Returns the path.
   (save-png my-frame \"plot.png\")
   (save-png my-frame \"plot.png\" {:width 800 :height 600})"
  ([fr path]
   (save-png fr path {}))
  ([fr path opts]
   (require 'scicloj.plotje.render.bufimg)
   (let [fr (ensure-frame fr)
         fr (if (seq opts) (options fr opts) fr)
         ;; Same composite/leaf branch as pj/save: composites go through
         ;; the compositor (one membrane tree), leaves through plan->plot.
         img (if (frame/composite? fr)
               (compositor/composite->plot fr :bufimg)
               (render-impl/plan->plot (plan fr) :bufimg (:opts fr {})))]
     ((resolve 'scicloj.plotje.render.bufimg/save-png) img path))))
