(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.plot :as plot-impl]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
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
            [scicloj.napkinsketch.impl.blueprint :as blueprint]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind])
  (:import [scicloj.napkinsketch.impl.view Sketch]))

;; ---- Sketch: auto-rendering sketch ----

(defn render-sketch
  "Render a Sketch to SVG. Called by Clay via kind/fn at display time.
   Restores the config snapshot captured at creation time so that
   with-config overrides are preserved across the lazy render boundary.
   Wraps output in a div with bottom margin for notebook spacing."
  [sketch]
  (let [captured (:config-snapshot sketch)
        rendered (if captured
                   (binding [defaults/*config* captured]
                     (plot-impl/plot (:views sketch) (:opts sketch)))
                   (plot-impl/plot (:views sketch) (:opts sketch)))]
    (kind/hiccup [:div {:style {:margin-bottom "1em"}} rendered])))

(defn- ->sketch
  "Wrap views + opts in a Sketch annotated with kind/fn for auto-rendering.
   Snapshots the current *config* binding so with-config overrides survive
   the lazy render boundary."
  [views & [opts]]
  (kind/fn (cond-> (assoc (view/->Sketch views (or opts {}))
                          :kindly/f #'render-sketch)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))

(defn- extract-views
  "Extract raw views vector from Sketch or pass through.
   Throws if a vector contains nested Sketches (use the Sketch pipeline directly)."
  [x]
  (cond (view/sketch? x) (:views x)
        (and (sequential? x) (some view/sketch? x))
        (throw (ex-info (str "Vector contains a Sketch. Pass the Sketch directly "
                             "instead of wrapping in a vector — e.g. "
                             "(-> data (sk/lay-point :x :y) (sk/options {...}))")
                        {}))
        (view/views? x) x
        :else x))

(defn- carry-opts
  "Extract opts from Sketch, or empty map."
  [x]
  (if (view/sketch? x) (:opts x) {}))

(defmethod print-method Sketch [^Sketch sketch ^java.io.Writer w]
  (.write w "#sketch ")
  (print-method (:views sketch) w))

;; ---- Compositional API ----

(defn view
  "Create views from data and column specs.
   Data can be a Tablecloth dataset, a map of columns ({:x [1 2 3]}),
   a sequence of row maps ([{:x 1 :y 2} ...]), or a CSV path/URL.
   Column references must be keywords.
   (view data)                   — infer columns (1→x, 2→x y, 3→x y color)
   (view data :x :y)            — two keywords, one scatter view
   (view data [:x :y])          — pair as vector, same result
   (view data [[:x1 :y1] ...])  — multiple views
   (view data :x)               — histogram view (x=y)
   An optional opts map sets shared aesthetics that all layers inherit:
   (view data :x :y {:color :species})  — layers inherit color grouping
   (view data :x {:color :species})     — single column + shared aesthetics
   Layer opts override view-level aesthetics."
  ([data] (->sketch (view/view data)))
  ([data spec-or-x] (->sketch (view/view data spec-or-x)))
  ([data x-or-spec y-or-opts] (->sketch (view/view data x-or-spec y-or-opts)))
  ([data x y opts] (->sketch (view/view data x y opts))))

(defn lay
  "Apply one or more methods to views. Primarily used for annotations;
   for data layers prefer sk/lay-point, sk/lay-histogram, etc.
   (lay views (rule-h 5))            — horizontal reference line
   (lay views (rule-v 3) (band-h 1 2)) — multiple annotations"
  [sketch-or-views & layer-specs]
  (->sketch (apply view/lay (extract-views sketch-or-views) layer-specs)
            (carry-opts sketch-or-views)))

(defn coord
  "Set coordinate system on views.
   Options: :cartesian (default), :flip, :polar, :fixed.
   (coord views :flip)   — flipped coordinates
   (coord views :fixed)  — fixed 1:1 aspect ratio"
  [sketch-or-views c]
  (->sketch (view/coord (extract-views sketch-or-views) c)
            (carry-opts sketch-or-views)))

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (view/cross xs ys))

(defn facet
  "Split views by a categorical column into separate panels.
   Default is a horizontal row of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  ([sketch-or-views col]
   (->sketch (view/facet (extract-views sketch-or-views) col)
             (carry-opts sketch-or-views)))
  ([sketch-or-views col direction]
   (->sketch (view/facet (extract-views sketch-or-views) col direction)
             (carry-opts sketch-or-views))))

(defn facet-grid
  "Split views by two categorical columns for a row × column grid.
   Either column may be nil for a single-dimension facet.
   (facet-grid views :smoker :sex)   — 2D grid
   (facet-grid views nil :species)   — same as facet"
  [sketch-or-views row-col col-col]
  (->sketch (view/facet-grid (extract-views sketch-or-views) row-col col-col)
            (carry-opts sketch-or-views)))

(defn distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  [data & cols]
  (->sketch (apply view/distribution data cols)))

(defn scale
  "Set scale options for :x or :y across all views.
   (scale views :x :log)                — log x-axis
   (scale views :y {:type :linear :domain [0 100]}) — fixed domain"
  ([sketch-or-views channel type-or-opts]
   (->sketch (view/scale (extract-views sketch-or-views) channel type-or-opts)
             (carry-opts sketch-or-views)))
  ([sketch-or-views channel type opts]
   (->sketch (view/scale (extract-views sketch-or-views) channel type opts)
             (carry-opts sketch-or-views))))

(defn options
  "Set plot-level options on a sketch.
   Options are deep-merged — nested maps like :theme are merged recursively.
   (options sketch {:width 800 :palette :dark2})
   (options sketch {:theme {:bg \"#FFF\"} :legend-position :bottom})"
  [sketch-or-views opts]
  (->sketch (extract-views sketch-or-views)
            (kindly/deep-merge (carry-opts sketch-or-views) opts)))

(defn sketch?
  "Return true if x is a Sketch (auto-rendering sketch)."
  [x]
  (view/sketch? x))

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
                         (cond (view/sketch? x) "Got a sketch."
                               (view/plan? x) "Got a plan."
                               :else (str "Got: " (type x) ".")))
                    {:function fn-name :expected expected-name :got-type (str (type x))}))))

(defn views-of
  "Extract the raw views vector from a sketch.
   Useful for inspecting view data: (kind/pprint (views-of my-sketch))"
  [sketch-or-views]
  (extract-views sketch-or-views))

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

;; ---- Layer Functions ----

(defn- col-ref?
  "True if v is a column reference — keyword or string."
  [v]
  (or (keyword? v) (string? v)))

(defn- warn-unknown-opts
  "Warn when user-provided layer options contain unrecognized keys."
  [method-key user-opts]
  (when (map? user-opts)
    (let [m (method/lookup method-key)
          allowed (into #{} (concat method/universal-layer-options (:accepts m)))]
      (doseq [k (keys user-opts)]
        (when (and (keyword? k) (not (allowed k)))
          (println (str "Warning: lay-" (name method-key)
                        " does not recognize option " k
                        ". Accepted options: "
                        (sort allowed))))))))

(defn- check-x-only
  "Throw if an x-only method (histogram, bar, density, rug) is given a :y column."
  [method-key x y]
  (when (:x-only (method/lookup method-key))
    (throw (ex-info (str "lay-" (name method-key) " uses only the x column; "
                         ":y column " y " is not supported. "
                         "Use (lay-" (name method-key) " data " x ") instead.")
                    {:method method-key :x x :y y}))))

(defn- lay-on-views
  "Add a method layer to existing views (Sketch path)."
  [method-key views layer-overrides carry-opts]
  (let [layer (merge (method/lookup method-key) layer-overrides)]
    (->sketch (view/lay views layer) carry-opts)))

(defn- lay-on-data
  "Create views from raw data, then add a method layer."
  [method-key data view-args & [layer-opts]]
  (let [views (apply view/view data view-args)
        layer (if layer-opts
                (merge (method/lookup method-key) layer-opts)
                (method/lookup method-key))]
    (->sketch (view/lay views layer))))

(defn- lay-method
  "Internal dispatch for lay-X functions.
   All arities detect Sketch/views as the first arg and add a layer with
   column overrides when applicable. When creating from raw data, validates
   that x-only methods are not given a :y column."
  ([method-key sketch-or-views]
   (let [extracted (extract-views sketch-or-views)]
     (if (view/views? extracted)
       (lay-on-views method-key extracted {} (carry-opts sketch-or-views))
       (lay-on-data method-key sketch-or-views []))))
  ([method-key sketch-or-data x-or-opts]
   (let [extracted (extract-views sketch-or-data)
         opts (carry-opts sketch-or-data)]
     (if (view/views? extracted)
       (if (col-ref? x-or-opts)
         (lay-on-views method-key extracted {:x x-or-opts} opts)
         (do (warn-unknown-opts method-key x-or-opts)
             (lay-on-views method-key extracted x-or-opts opts)))
       (lay-on-data method-key extracted [x-or-opts]))))
  ([method-key sketch-or-data x y-or-opts]
   (let [extracted (extract-views sketch-or-data)
         opts (carry-opts sketch-or-data)]
     (if (view/views? extracted)
       (if (col-ref? y-or-opts)
         (lay-on-views method-key extracted {:x x :y y-or-opts} opts)
         (do (warn-unknown-opts method-key y-or-opts)
             (lay-on-views method-key extracted (merge {:x x} y-or-opts) opts)))
       (if (col-ref? y-or-opts)
         (do (check-x-only method-key x y-or-opts)
             (lay-on-data method-key sketch-or-data [x y-or-opts]))
         (do (warn-unknown-opts method-key y-or-opts)
             (lay-on-data method-key sketch-or-data [x] y-or-opts))))))
  ([method-key sketch-or-data x y opts]
   (let [extracted (extract-views sketch-or-data)
         carry (carry-opts sketch-or-data)]
     (warn-unknown-opts method-key opts)
     (if (view/views? extracted)
       (lay-on-views method-key extracted (merge {:x x :y y} opts) carry)
       (do (check-x-only method-key x y)
           (lay-on-data method-key sketch-or-data [x y] opts))))))

(defn lay-point
  "Add a scatter layer — shows individual data points.
   (lay-point views)                          — add to views
   (lay-point views {:color :species})        — with options
   (lay-point data :x :y)                    — from data
   (lay-point data :x :y {:color :species})  — from data with options"
  ([views] (lay-method :point views))
  ([views-or-data x-or-opts] (lay-method :point views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :point data x y-or-opts))
  ([data x y opts] (lay-method :point data x y opts)))

(defn lay-line
  "Add a line layer — connects data points in order.
   (lay-line views)                          — add to views
   (lay-line views {:color :group})          — with options
   (lay-line data :x :y)                    — from data"
  ([views] (lay-method :line views))
  ([views-or-data x-or-opts] (lay-method :line views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :line data x y-or-opts))
  ([data x y opts] (lay-method :line data x y opts)))

(defn lay-step
  "Add a step layer — horizontal-then-vertical connected points.
   (lay-step views)                          — add to views
   (lay-step views {:color :group})          — with options
   (lay-step data :x :y)                    — from data"
  ([views] (lay-method :step views))
  ([views-or-data x-or-opts] (lay-method :step views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :step data x y-or-opts))
  ([data x y opts] (lay-method :step data x y opts)))

(defn lay-histogram
  "Add a histogram layer — bins numerical data into counted bars.
   (lay-histogram views)                     — add to views
   (lay-histogram views {:color :species})   — with options
   (lay-histogram data :x)                  — from data, single column"
  ([views] (lay-method :histogram views))
  ([views-or-data x-or-opts] (lay-method :histogram views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :histogram data x y-or-opts))
  ([data x y opts] (lay-method :histogram data x y opts)))

(defn lay-bar
  "Add a bar layer — counts categorical values.
   (lay-bar views)                           — add to views
   (lay-bar views {:color :species})         — with options
   (lay-bar data :x)                        — from data, single column"
  ([views] (lay-method :bar views))
  ([views-or-data x-or-opts] (lay-method :bar views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :bar data x y-or-opts))
  ([data x y opts] (lay-method :bar data x y opts)))

(defn lay-stacked-bar
  "Add a stacked bar layer — counts categorical values (position :stack).
   (lay-stacked-bar views)                   — add to views
   (lay-stacked-bar views {:color :smoker})  — with options"
  ([views] (lay-method :stacked-bar views))
  ([views-or-data x-or-opts] (lay-method :stacked-bar views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :stacked-bar data x y-or-opts))
  ([data x y opts] (lay-method :stacked-bar data x y opts)))

(defn lay-stacked-bar-fill
  "Add a percentage stacked bar layer — proportions sum to 1.0.
   (lay-stacked-bar-fill views)                   — add to views
   (lay-stacked-bar-fill views {:color :smoker})  — with options"
  ([views] (lay-method :stacked-bar-fill views))
  ([views-or-data x-or-opts] (lay-method :stacked-bar-fill views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :stacked-bar-fill data x y-or-opts))
  ([data x y opts] (lay-method :stacked-bar-fill data x y opts)))

(defn lay-value-bar
  "Add a value bar layer — categorical x with pre-computed numeric y.
   (lay-value-bar views)                     — add to views
   (lay-value-bar data :product :revenue)    — from data"
  ([views] (lay-method :value-bar views))
  ([views-or-data x-or-opts] (lay-method :value-bar views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :value-bar data x y-or-opts))
  ([data x y opts] (lay-method :value-bar data x y opts)))

(defn lay-lm
  "Add a linear regression layer — fits a straight line to data.
   (lay-lm views)                            — add to views
   (lay-lm views {:color :species})          — per-group regression
   (lay-lm views {:se true})                 — with confidence ribbon"
  ([views] (lay-method :lm views))
  ([views-or-data x-or-opts] (lay-method :lm views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :lm data x y-or-opts))
  ([data x y opts] (lay-method :lm data x y opts)))

(defn lay-loess
  "Add a LOESS layer — local regression smoothing.
   (lay-loess views)                         — add to views
   (lay-loess views {:color :species})       — per-group smoothing
   (lay-loess views {:se true})              — with confidence ribbon"
  ([views] (lay-method :loess views))
  ([views-or-data x-or-opts] (lay-method :loess views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :loess data x y-or-opts))
  ([data x y opts] (lay-method :loess data x y opts)))

(defn lay-text
  "Add a text layer — data-driven labels at (x, y) positions.
   (lay-text views {:text :name})            — label each point
   (lay-text data :x :y {:text :name})       — from data"
  ([views] (lay-method :text views))
  ([views-or-data x-or-opts] (lay-method :text views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :text data x y-or-opts))
  ([data x y opts] (lay-method :text data x y opts)))

(defn lay-label
  "Add a label layer — text with a filled background box.
   (lay-label views {:text :name})           — labeled points
   (lay-label data :x :y {:text :name})      — from data"
  ([views] (lay-method :label views))
  ([views-or-data x-or-opts] (lay-method :label views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :label data x y-or-opts))
  ([data x y opts] (lay-method :label data x y opts)))

(defn lay-area
  "Add an area layer — filled region under a line.
   (lay-area views)                          — add to views
   (lay-area views {:color :species})        — with options"
  ([views] (lay-method :area views))
  ([views-or-data x-or-opts] (lay-method :area views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :area data x y-or-opts))
  ([data x y opts] (lay-method :area data x y opts)))

(defn lay-stacked-area
  "Add a stacked area layer — filled regions stacked cumulatively.
   (lay-stacked-area views)                  — add to views
   (lay-stacked-area views {:color :group})  — with options"
  ([views] (lay-method :stacked-area views))
  ([views-or-data x-or-opts] (lay-method :stacked-area views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :stacked-area data x y-or-opts))
  ([data x y opts] (lay-method :stacked-area data x y opts)))

(defn lay-density
  "Add a density layer — kernel density estimation as filled area.
   (lay-density views)                       — add to views
   (lay-density views {:color :species})     — per-group density curves"
  ([views] (lay-method :density views))
  ([views-or-data x-or-opts] (lay-method :density views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :density data x y-or-opts))
  ([data x y opts] (lay-method :density data x y opts)))

(defn lay-tile
  "Add a tile/heatmap layer — filled rectangles colored by value.
   (lay-tile views)                          — 2D binned heatmap
   (lay-tile views {:fill :value})           — pre-computed fill values"
  ([views] (lay-method :tile views))
  ([views-or-data x-or-opts] (lay-method :tile views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :tile data x y-or-opts))
  ([data x y opts] (lay-method :tile data x y opts)))

(defn lay-density2d
  "Add a 2D density layer — KDE-smoothed heatmap.
   (lay-density2d views)                     — add to views
   (lay-density2d views {:kde2d-grid 40})    — finer grid resolution"
  ([views] (lay-method :density2d views))
  ([views-or-data x-or-opts] (lay-method :density2d views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :density2d data x y-or-opts))
  ([data x y opts] (lay-method :density2d data x y opts)))

(defn lay-contour
  "Add a contour layer — iso-density contour lines from 2D KDE.
   (lay-contour views)                       — add to views
   (lay-contour views {:levels 8})           — custom iso-levels"
  ([views] (lay-method :contour views))
  ([views-or-data x-or-opts] (lay-method :contour views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :contour data x y-or-opts))
  ([data x y opts] (lay-method :contour data x y opts)))

(defn lay-ridgeline
  "Add a ridgeline layer — vertically stacked density curves per category.
   (lay-ridgeline views)                     — add to views
   (lay-ridgeline views {:color :species})   — colored ridgelines"
  ([views] (lay-method :ridgeline views))
  ([views-or-data x-or-opts] (lay-method :ridgeline views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :ridgeline data x y-or-opts))
  ([data x y opts] (lay-method :ridgeline data x y opts)))

(defn lay-boxplot
  "Add a boxplot layer — median, quartiles, whiskers, and outliers.
   (lay-boxplot views)                       — add to views
   (lay-boxplot views {:color :smoker})      — grouped boxplots"
  ([views] (lay-method :boxplot views))
  ([views-or-data x-or-opts] (lay-method :boxplot views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :boxplot data x y-or-opts))
  ([data x y opts] (lay-method :boxplot data x y opts)))

(defn lay-violin
  "Add a violin layer — mirrored density curve per category.
   (lay-violin views)                        — add to views
   (lay-violin views {:color :smoker})       — grouped violins"
  ([views] (lay-method :violin views))
  ([views-or-data x-or-opts] (lay-method :violin views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :violin data x y-or-opts))
  ([data x y opts] (lay-method :violin data x y opts)))

(defn lay-rug
  "Add a rug layer — tick marks along axis margins.
   (lay-rug views)                           — ticks on x-axis
   (lay-rug views {:side :both})             — ticks on both axes"
  ([views] (lay-method :rug views))
  ([views-or-data x-or-opts] (lay-method :rug views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :rug data x y-or-opts))
  ([data x y opts] (lay-method :rug data x y opts)))

(defn lay-summary
  "Add a summary layer — mean ± standard error per category.
   (lay-summary views)                       — add to views
   (lay-summary views {:color :species})     — per-group summary"
  ([views] (lay-method :summary views))
  ([views-or-data x-or-opts] (lay-method :summary views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :summary data x y-or-opts))
  ([data x y opts] (lay-method :summary data x y opts)))

(defn lay-errorbar
  "Add an errorbar layer — vertical error bars at (x, y) positions.
   (lay-errorbar views {:ymin :ci_lo :ymax :ci_hi})  — with error columns"
  ([views] (lay-method :errorbar views))
  ([views-or-data x-or-opts] (lay-method :errorbar views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :errorbar data x y-or-opts))
  ([data x y opts] (lay-method :errorbar data x y opts)))

(defn lay-lollipop
  "Add a lollipop layer — stem + dot at (x, y) positions.
   (lay-lollipop views)                      — add to views
   (lay-lollipop views {:color :group})      — colored stems"
  ([views] (lay-method :lollipop views))
  ([views-or-data x-or-opts] (lay-method :lollipop views-or-data x-or-opts))
  ([data x y-or-opts] (lay-method :lollipop data x y-or-opts))
  ([data x y opts] (lay-method :lollipop data x y opts)))

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

;; ---- Rendering ----

(defn plot
  "Render views as a figure (default: SVG hiccup wrapped with kind/hiccup).
   (plot views)              — default 600×400 SVG
   (plot views {:width 800 :height 500 :title \"My Plot\"})
   (plot views {:format :svg})  — explicit format
   Options: :width, :height, :title, :subtitle, :caption, :x-label, :y-label,
   :palette (keyword, vector, or map), :theme {:bg :grid :font-size},
   :legend-position (:right :bottom :top :none), :tooltip, :brush,
   :color-scale (:sequential, :diverging, or {:low :mid :high} map),
   :color-midpoint (number — centers diverging gradient on this value),
   :validate (default true — validates plan against Malli schema)."
  ([sketch-or-views]
   (plot-impl/plot (extract-views sketch-or-views)
                   (carry-opts sketch-or-views)))
  ([sketch-or-views opts]
   (plot-impl/plot (extract-views sketch-or-views)
                   (kindly/deep-merge (carry-opts sketch-or-views) opts))))

(defn plan
  "Resolve views into a plan — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   (plan views)              — default 600×400
   (plan views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([sketch-or-views]
   (let [views (extract-views sketch-or-views)
         opts (carry-opts sketch-or-views)]
     (if (seq opts)
       (sketch-impl/views->plan views opts)
       (sketch-impl/views->plan views))))
  ([sketch-or-views opts]
   (sketch-impl/views->plan (extract-views sketch-or-views)
                            (kindly/deep-merge (carry-opts sketch-or-views) opts))))

(defn views->plan
  "Convert views into a plan — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   Same as `plan` but with an explicit pipeline-style name.
   (views->plan views)              — default 600×400
   (views->plan views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([sketch-or-views]
   (let [views (extract-views sketch-or-views)
         opts (carry-opts sketch-or-views)]
     (if (seq opts)
       (sketch-impl/views->plan views opts)
       (sketch-impl/views->plan views))))
  ([sketch-or-views opts]
   (sketch-impl/views->plan (extract-views sketch-or-views)
                            (kindly/deep-merge (carry-opts sketch-or-views) opts))))

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

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts — useful for asserting
   plot structure.
   Accepts SVG hiccup or a Sketch (auto-renders to SVG first).
   (svg-summary (plot views))  — summary of rendered SVG
   (svg-summary my-sketch)       — auto-renders Sketch, then summarizes"
  ([svg-or-sketch]
   (cond
     (view/sketch? svg-or-sketch) (svg/svg-summary (plot svg-or-sketch))
     (blueprint/blueprint? svg-or-sketch)
     (let [views (blueprint/resolve-blueprint svg-or-sketch)
           plan (sketch-impl/xkcd7-views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {})))
     :else (svg/svg-summary svg-or-sketch)))
  ([svg-or-sketch theme]
   (cond
     (view/sketch? svg-or-sketch) (svg/svg-summary (plot svg-or-sketch) theme)
     (blueprint/blueprint? svg-or-sketch)
     (let [views (blueprint/resolve-blueprint svg-or-sketch)
           plan (sketch-impl/xkcd7-views->plan views (:opts svg-or-sketch {}))]
       (svg/svg-summary (render-impl/plan->figure plan :svg {}) theme))
     :else (svg/svg-summary svg-or-sketch theme))))

;; ---- Multi-Plot Composition ----

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or Sketches, or a vector of vectors (explicit rows).
   opts:  {:cols N, :title \"...\", :gap \"8px\"}.
   (arrange [plot-a plot-b])                — 1×2 row
   (arrange [plot-a plot-b plot-c] {:cols 2}) — 2-column grid, wraps
   (arrange [[plot-a plot-b] [plot-c plot-d]]) — explicit 2×2 grid"
  ([plots] (arrange plots {}))
  ([plots opts]
   (let [{:keys [cols title gap]} opts
         ;; Detect explicit rows: [[a b] [c d]]
         nested? (and (sequential? (first plots))
                      (not (keyword? (ffirst plots))))
         flat-plots (if nested? (vec (apply concat plots)) (vec plots))
         ;; Auto-render any Sketches to SVG hiccup
         flat-plots (mapv #(if (view/sketch? %) (plot %) %) flat-plots)
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

(defn save
  "Save a plot to an SVG file.
   views — a vector of view maps, a Sketch, or a Blueprint.
   path  — file path (string or java.io.File).
   opts  — same options as `plot` (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save views \"plot.svg\")
   (save views \"plot.svg\" {:width 800 :height 600})"
  ([sketch-or-views path]
   (save sketch-or-views path {}))
  ([sketch-or-views path opts]
   (let [path-str (str path)]
     (when-not (.endsWith path-str ".svg")
       (println (str "Warning: save produces SVG output, but path does not end with .svg: " path-str)))
     (if (blueprint/blueprint? sketch-or-views)
       ;; Blueprint: use xkcd7 pipeline
       (let [views (blueprint/resolve-blueprint sketch-or-views)
             all-opts (kindly/deep-merge (:opts sketch-or-views {}) opts)
             plan (sketch-impl/xkcd7-views->plan views all-opts)
             svg-hiccup (render-impl/plan->figure plan :svg {})]
         (spit path (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                         (svg/hiccup->svg-str svg-hiccup)))
         path)
       ;; Sketch/views: use old pipeline
       (let [views (extract-views sketch-or-views)
             all-opts (kindly/deep-merge (carry-opts sketch-or-views) opts)]
         (svg/save views path all-opts))))))

;; ================================================================
;; PROPOSED API — temporary xkcd7- prefix (will be renamed)
;; ================================================================

(defn- xkcd7-wrap-autorender
  "Wrap a Blueprint with kind/fn for auto-rendering in Clay."
  [bp]
  (kind/fn (cond-> (assoc bp :kindly/f #'blueprint/render-blueprint)
             defaults/*config* (assoc :config-snapshot defaults/*config*))))

(defn- xkcd7-ensure-bp
  "Coerce first arg to a Blueprint if it isn't one already."
  [x]
  (cond
    (blueprint/blueprint? x) x
    (tc/dataset? x)          (xkcd7-wrap-autorender (blueprint/->blueprint x {} [] [] {}))
    (map? x)                 (xkcd7-wrap-autorender (blueprint/->blueprint (tc/dataset x) {} [] [] {}))
    (sequential? x)          (xkcd7-wrap-autorender (blueprint/->blueprint (tc/dataset x) {} [] [] {}))
    :else                    (xkcd7-wrap-autorender (blueprint/->blueprint nil {} [] [] {}))))

(defn xkcd7-sketch
  "Create a Blueprint."
  ([] (xkcd7-wrap-autorender (blueprint/->blueprint nil {} [] [] {})))
  ([data] (xkcd7-sketch data {}))
  ([data shared]
   (xkcd7-wrap-autorender
    (blueprint/->blueprint
     (when data (if (tc/dataset? data) data (tc/dataset data)))
     shared [] [] {}))))

(defn xkcd7-with-data
  "Supply or replace data in a Blueprint."
  [bp data]
  (assoc bp :data (if (tc/dataset? data) data (tc/dataset data))))

(defn xkcd7-view
  "Add entries to a Blueprint."
  ([bp-or-data]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (if (:data bp)
       (let [ds (:data bp)
             cols (vec (tc/column-names ds))
             n (count cols)]
         (case n
           1 (update bp :entries conj {:x (cols 0)})
           2 (update bp :entries conj {:x (cols 0) :y (cols 1)})
           3 (update bp :entries conj {:x (cols 0) :y (cols 1) :color (cols 2)})
           (throw (ex-info (str "Cannot infer columns from " n " columns.") {:columns cols}))))
       bp)))
  ([bp-or-data x-or-entries]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (cond
       (or (keyword? x-or-entries)
           (string? x-or-entries)) (update bp :entries conj {:x x-or-entries})
       (map? x-or-entries)       (update bp :entries conj x-or-entries)
       (sequential? x-or-entries)
       (update bp :entries into (mapv (fn [[x y]] {:x x :y y}) x-or-entries)))))
  ([bp-or-data x y]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (if (and (sequential? x) (map? y))
       ;; Pairs + shared opts: (view bp pairs {:color :species})
       (let [bp (update bp :shared merge y)]
         (update bp :entries into (mapv (fn [[a b]] {:x a :y b}) x)))
       (update bp :entries conj {:x x :y y}))))
  ([bp-or-data x y opts]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (-> bp
         (update :shared merge opts)
         (update :entries conj {:x x :y y})))))

(defn- xkcd7-add-entry-method
  "Add a method to a specific entry (found by matching :x/:y, or created new).
   Used when lay-* is called with structural columns."
  [bp entry-keys method-map]
  (let [entries (:entries bp)
        idx (first (keep-indexed
                    (fn [i e]
                      (when (and (= (:x e) (:x entry-keys))
                                 (= (:y e) (:y entry-keys)))
                        i))
                    entries))]
    (if idx
      ;; Found existing entry — append method to its :methods
      (update-in bp [:entries idx :methods]
                 (fn [ms] (conj (or ms []) method-map)))
      ;; No match — create new entry with this method
      (update bp :entries conj (assoc entry-keys :methods [method-map])))))

(defn xkcd7-lay
  "Add a method to a Blueprint."
  ([bp-or-data method-key]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (if (keyword? method-key)
       (let [m (method/lookup method-key)]
         (update bp :methods conj (or (select-keys m [:mark :stat :position])
                                      {:mark method-key :stat :identity})))
       (update bp :methods conj method-key))))
  ([bp-or-data method-key opts]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (if (keyword? method-key)
       (let [m (method/lookup method-key)]
         (update bp :methods conj (merge (or (select-keys m [:mark :stat :position])
                                             {:mark method-key :stat :identity})
                                         opts)))
       (update bp :methods conj (merge method-key opts))))))

(defn- xkcd7-method-map
  "Build a method map from a method-key and optional opts."
  [method-key opts]
  (let [m (when (keyword? method-key) (method/lookup method-key))
        base (or (select-keys (or m {}) [:mark :stat :position])
                 {:mark method-key :stat :identity})]
    (merge base opts)))

(defmacro ^:private def-xkcd7-lay [method-key]
  (let [fn-name (symbol (str "xkcd7-lay-" (name method-key)))]
    `(defn ~fn-name
       ~(str "Add :" (name method-key) " method to a Blueprint.\n"
             "  Without columns → global method (applies to all entries).\n"
             "  With columns → entry-specific (find or create entry).\n"
             "  (xkcd7-lay-" (name method-key) " bp) — global method.\n"
             "  (xkcd7-lay-" (name method-key) " bp {:alpha 0.5}) — global with opts.\n"
             "  (xkcd7-lay-" (name method-key) " data :x :y) — entry-specific.\n"
             "  (xkcd7-lay-" (name method-key) " data :x :y {:color :c}) — entry-specific with opts.")
       ([bp-or-data#]
        (let [bp# (xkcd7-ensure-bp bp-or-data#)]
          (if (and (empty? (:entries bp#))
                   (:data bp#)
                   (<= (count (tc/column-names (:data bp#))) 3))
            ;; Auto-infer columns for small datasets, then entry-specific
            (let [bp2# (xkcd7-view bp#)]
              (xkcd7-add-entry-method bp2# (first (:entries bp2#))
                                      (xkcd7-method-map ~method-key nil)))
            ;; No columns → global method
            (xkcd7-lay bp# ~method-key))))
       ([bp-or-data# x-or-opts#]
        (if (or (keyword? x-or-opts#) (string? x-or-opts#))
          ;; Single column (univariate): entry-specific
          (xkcd7-add-entry-method (xkcd7-ensure-bp bp-or-data#)
                                  {:x x-or-opts#}
                                  (xkcd7-method-map ~method-key nil))
          ;; Options map: global method
          (xkcd7-lay bp-or-data# ~method-key x-or-opts#)))
       ([bp-or-data# x# y-or-opts#]
        (if (or (keyword? y-or-opts#) (string? y-or-opts#))
          ;; data + x + y: entry-specific
          (xkcd7-add-entry-method (xkcd7-ensure-bp bp-or-data#)
                                  {:x x# :y y-or-opts#}
                                  (xkcd7-method-map ~method-key nil))
          ;; bp + x-column + opts (univariate + opts): entry-specific
          (xkcd7-add-entry-method (xkcd7-ensure-bp bp-or-data#)
                                  {:x x#}
                                  (xkcd7-method-map ~method-key y-or-opts#))))
       ([bp-or-data# x# y# opts#]
        (xkcd7-add-entry-method (xkcd7-ensure-bp bp-or-data#)
                                {:x x# :y y#}
                                (xkcd7-method-map ~method-key opts#))))))

(def-xkcd7-lay :point)
(def-xkcd7-lay :line)
(def-xkcd7-lay :step)
(def-xkcd7-lay :area)
(def-xkcd7-lay :stacked-area)
(def-xkcd7-lay :histogram)
(def-xkcd7-lay :bar)
(def-xkcd7-lay :stacked-bar)
(def-xkcd7-lay :stacked-bar-fill)
(def-xkcd7-lay :value-bar)
(def-xkcd7-lay :lm)
(def-xkcd7-lay :loess)
(def-xkcd7-lay :density)
(def-xkcd7-lay :tile)
(def-xkcd7-lay :density2d)
(def-xkcd7-lay :contour)
(def-xkcd7-lay :boxplot)
(def-xkcd7-lay :violin)
(def-xkcd7-lay :ridgeline)
(def-xkcd7-lay :summary)
(def-xkcd7-lay :errorbar)
(def-xkcd7-lay :lollipop)
(def-xkcd7-lay :text)
(def-xkcd7-lay :label)
(def-xkcd7-lay :rug)

(defn xkcd7-overlay
  "Add an entry with its own methods."
  ([bp-or-data x y methods]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (update bp :entries conj {:x x :y y :methods methods})))
  ([bp-or-data entry-map methods]
   (let [bp (xkcd7-ensure-bp bp-or-data)]
     (update bp :entries conj (assoc entry-map :methods methods)))))

(defn xkcd7-facet
  "Facet a Blueprint by a column."
  ([bp col] (xkcd7-facet bp col :col))
  ([bp col direction]
   (let [k (case direction :col :facet-col :row :facet-row)]
     (update bp :entries (fn [entries] (mapv #(assoc % k col) entries))))))

(defn xkcd7-facet-grid
  "Facet a Blueprint by two columns (2D grid)."
  [bp col-col row-col]
  (update bp :entries (fn [entries]
                        (mapv #(assoc % :facet-col col-col :facet-row row-col) entries))))

(defn xkcd7-options
  "Set plot-level options (title, labels, width, height, etc.)."
  [bp opts]
  (update bp :opts merge opts))

(defn xkcd7-scale
  "Set axis scale on a Blueprint.
   (xkcd7-scale bp :x :log) — log scale on x-axis."
  [bp channel scale-type]
  (let [k (case channel :x :x-scale :y :y-scale
                (throw (ex-info (str "Scale channel must be :x or :y, got: " channel)
                                {:channel channel})))]
    (update bp :entries (fn [entries]
                          (mapv #(assoc % k (if (map? scale-type)
                                              (merge {:type :linear} scale-type)
                                              {:type scale-type})) entries)))))

(defn xkcd7-coord
  "Set coordinate transform on a Blueprint.
   (xkcd7-coord bp :flip) — flipped coordinates."
  [bp coord-type]
  (update bp :entries (fn [entries]
                        (mapv #(assoc % :coord coord-type) entries))))

(defn xkcd7-plan
  "Resolve a Blueprint into a plan using entry-based grid layout.
   Each entry = one panel. Grid position from structural columns.
   (xkcd7-plan bp)
   (xkcd7-plan bp {:title \"My Plot\"})"
  ([bp]
   (let [views (blueprint/resolve-blueprint bp)]
     (sketch-impl/xkcd7-views->plan views (:opts bp {}))))
  ([bp opts]
   (let [views (blueprint/resolve-blueprint bp)]
     (sketch-impl/xkcd7-views->plan views (merge (:opts bp {}) opts)))))

(defn xkcd7-annotate
  "Add annotation entries to a Blueprint.
   Annotations (rule-h, rule-v, band-h, band-v) are view maps that
   don't participate in the entry × methods cross product.
   (xkcd7-annotate bp (sk/rule-h 5) (sk/band-v 3 7))"
  [bp & annotations]
  (reduce (fn [bp ann]
            (update bp :entries conj (assoc ann :methods [ann])))
          bp annotations))

(defn xkcd7-overlay
  "Add a layer with different columns on the same panel as an existing entry.
   Use when you want two different column mappings sharing the same axes —
   e.g., scatter of :x/:y with a line of :x/:y_predicted.
   The overlay creates a new entry with its own :methods.
   (xkcd7-overlay bp :x :y_predicted :line)
   (xkcd7-overlay bp :x :y_predicted :line {:color \"red\"})"
  ([bp x y method-key]
   (xkcd7-overlay bp x y method-key {}))
  ([bp x y method-key opts]
   (let [method-map (xkcd7-method-map method-key opts)]
     (update bp :entries conj {:x x :y y :methods [method-map]}))))

(defn xkcd7-distribution
  "Add diagonal entries (x=y) for each column — used for histograms in SPLOM.
   (xkcd7-distribution bp :a :b :c) adds entries [{:x :a :y :a} {:x :b :y :b} {:x :c :y :c}]"
  [bp-or-data & cols]
  (let [bp (xkcd7-ensure-bp bp-or-data)]
    (reduce (fn [bp col] (update bp :entries conj {:x col :y col}))
            bp cols)))

(defn xkcd7-plot
  "Render a Blueprint to SVG (or interactive HTML if tooltip/brush is set)."
  [bp]
  (let [opts (:opts bp {})
        views (blueprint/resolve-blueprint bp)
        plan (sketch-impl/xkcd7-views->plan views opts)]
    (render-impl/plan->figure plan :svg opts)))
