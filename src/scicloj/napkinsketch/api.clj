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
            [scicloj.kindly.v4.kind :as kind])
  (:import [scicloj.napkinsketch.impl.view PlotSpec]))

;; ---- PlotSpec: auto-rendering plot specification ----

(defn render-plot-spec
  "Render a PlotSpec to SVG. Called by Clay via kind/fn at display time."
  [spec]
  (plot-impl/plot (:views spec) (:opts spec)))

(defn- ->plot-spec
  "Wrap views + opts in a PlotSpec annotated with kind/fn for auto-rendering."
  [views & [opts]]
  (kind/fn (assoc (view/->PlotSpec views (or opts {}))
                  :kindly/f #'render-plot-spec)))

(defn- extract-views
  "Extract raw views vector from PlotSpec or pass through."
  [x]
  (cond (view/plot-spec? x) (:views x)
        (view/views? x) x
        :else x))

(defn- carry-opts
  "Extract opts from PlotSpec, or empty map."
  [x]
  (if (view/plot-spec? x) (:opts x) {}))

(defmethod print-method PlotSpec [^PlotSpec spec ^java.io.Writer w]
  (.write w "#plot ")
  (print-method (:views spec) w))

;; ---- Compositional API ----

(defn view
  "Create views from data and column specs.
   Data can be a Tablecloth dataset, a map of columns ({:x [1 2 3]}),
   a sequence of row maps ([{:x 1 :y 2} ...]), or a CSV path/URL.
   Column references must be keywords.
   (view data :x :y)            — two keywords, one scatter view
   (view data [:x :y])          — pair as vector, same result
   (view data [[:x1 :y1] ...])  — multiple views
   (view data :x)               — histogram view (x=y)
   An optional opts map sets shared aesthetics that all layers inherit:
   (view data :x :y {:color :species})  — layers inherit color grouping
   (view data :x {:color :species})     — single column + shared aesthetics
   Layer opts override view-level aesthetics."
  ([data spec-or-x] (->plot-spec (view/view data spec-or-x)))
  ([data x-or-spec y-or-opts] (->plot-spec (view/view data x-or-spec y-or-opts)))
  ([data x y opts] (->plot-spec (view/view data x y opts))))

(defn lay
  "Apply one or more methods to views. Primarily used for annotations;
   for data layers prefer sk/lay-point, sk/lay-histogram, etc.
   (lay views (rule-h 5))            — horizontal reference line
   (lay views (rule-v 3) (band-h 1 2)) — multiple annotations"
  [spec-or-views & layer-specs]
  (->plot-spec (apply view/lay (extract-views spec-or-views) layer-specs)
               (carry-opts spec-or-views)))

(defn coord
  "Set coordinate system on views.
   Options: :cartesian (default), :flip, :polar, :fixed.
   (coord views :flip)   — flipped coordinates
   (coord views :fixed)  — fixed 1:1 aspect ratio"
  [spec-or-views c]
  (->plot-spec (view/coord (extract-views spec-or-views) c)
               (carry-opts spec-or-views)))

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (view/cross xs ys))

(defn facet
  "Split views by a categorical column into separate panels.
   Default is a horizontal row of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  ([spec-or-views col]
   (->plot-spec (view/facet (extract-views spec-or-views) col)
                (carry-opts spec-or-views)))
  ([spec-or-views col direction]
   (->plot-spec (view/facet (extract-views spec-or-views) col direction)
                (carry-opts spec-or-views))))

(defn facet-grid
  "Split views by two categorical columns for a row × column grid.
   Either column may be nil for a single-dimension facet.
   (facet-grid views :smoker :sex)   — 2D grid
   (facet-grid views nil :species)   — same as facet"
  [spec-or-views row-col col-col]
  (->plot-spec (view/facet-grid (extract-views spec-or-views) row-col col-col)
               (carry-opts spec-or-views)))

(defn distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  [data & cols]
  (->plot-spec (apply view/distribution data cols)))

(defn scale
  "Set scale options for :x or :y across all views.
   (scale views :x :log)                — log x-axis
   (scale views :y {:type :linear :domain [0 100]}) — fixed domain"
  ([spec-or-views channel type-or-opts]
   (->plot-spec (view/scale (extract-views spec-or-views) channel type-or-opts)
                (carry-opts spec-or-views)))
  ([spec-or-views channel type opts]
   (->plot-spec (view/scale (extract-views spec-or-views) channel type opts)
                (carry-opts spec-or-views))))

(defn labs
  "Set labels on views. Keys: :title, :subtitle, :caption, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})
   (labs views {:title \"Title\" :subtitle \"Detail\" :caption \"Source: ...\"})"
  [spec-or-views label-opts]
  (->plot-spec (view/labs (extract-views spec-or-views) label-opts)
               (carry-opts spec-or-views)))

(defn options
  "Set plot-level options on a plot specification.
   Options are applied when the plot is rendered (by Clay or by `plot`).
   (options spec {:width 800 :palette :dark2})
   (options spec {:theme {:bg \"#FFF\"} :legend-position :bottom})"
  [spec-or-views opts]
  (->plot-spec (extract-views spec-or-views)
               (merge (carry-opts spec-or-views) opts)))

(defn plot-spec?
  "Return true if x is a PlotSpec (auto-rendering plot specification)."
  [x]
  (view/plot-spec? x))

(defn views-of
  "Extract the raw views vector from a plot specification.
   Useful for inspecting view data: (kind/pprint (views-of my-spec))"
  [spec-or-views]
  (extract-views spec-or-views))

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
   These are accepted by sk/options, sk/sketch, and sk/plot but are
   inherently per-plot (text content or nested config override).
   Maps each key to [category description]."
  defaults/plot-option-docs)

(def ^:deprecated per-call-key-docs
  "Deprecated: use plot-option-docs instead."
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

(defn- lay-method
  "Internal dispatch for lay-X functions.
   `method-key` is a keyword that looks up defaults from the method registry."
  ([method-key spec-or-views]
   (->plot-spec (view/lay (extract-views spec-or-views) (method/lookup method-key))
                (carry-opts spec-or-views)))
  ([method-key spec-or-data x-or-opts]
   (let [extracted (extract-views spec-or-data)
         opts (carry-opts spec-or-data)]
     (if (view/views? extracted)
       (->plot-spec (view/lay extracted (merge (method/lookup method-key) x-or-opts)) opts)
       (->plot-spec (-> extracted (view/view x-or-opts) (view/lay (method/lookup method-key)))))))
  ([method-key spec-or-data x y-or-opts]
   (if (keyword? y-or-opts)
     (->plot-spec (-> spec-or-data (view/view x y-or-opts) (view/lay (method/lookup method-key))))
     (->plot-spec (-> spec-or-data (view/view x) (view/lay (merge (method/lookup method-key) y-or-opts))))))
  ([method-key data x y opts]
   (->plot-spec (-> data (view/view x y) (view/lay (merge (method/lookup method-key) opts))))))

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
   :validate (default true — validates sketch against Malli schema)."
  ([spec-or-views]
   (plot-impl/plot (extract-views spec-or-views)
                   (carry-opts spec-or-views)))
  ([spec-or-views opts]
   (plot-impl/plot (extract-views spec-or-views)
                   (merge (carry-opts spec-or-views) opts))))

(defn sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   (sketch views)              — default 600×400
   (sketch views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([spec-or-views]
   (let [views (extract-views spec-or-views)
         opts (carry-opts spec-or-views)]
     (if (seq opts)
       (sketch-impl/views->sketch views opts)
       (sketch-impl/views->sketch views))))
  ([spec-or-views opts]
   (sketch-impl/views->sketch (extract-views spec-or-views)
                              (merge (carry-opts spec-or-views) opts))))

(defn views->sketch
  "Convert views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   Same as `sketch` but with an explicit pipeline-style name.
   (views->sketch views)              — default 600×400
   (views->sketch views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([spec-or-views]
   (let [views (extract-views spec-or-views)
         opts (carry-opts spec-or-views)]
     (if (seq opts)
       (sketch-impl/views->sketch views opts)
       (sketch-impl/views->sketch views))))
  ([spec-or-views opts]
   (sketch-impl/views->sketch (extract-views spec-or-views)
                              (merge (carry-opts spec-or-views) opts))))

(defn sketch->membrane
  "Convert a sketch into a membrane drawable tree.
   (sketch->membrane (sketch views))"
  [sketch & {:as opts}]
  (membrane/sketch->membrane sketch opts))

(defn membrane->figure
  "Convert a membrane drawable tree into a figure for the given format.
   Dispatches on format keyword; :svg is always available.
   (membrane->figure (sketch->membrane (sketch views)) :svg {})"
  [membrane-tree format opts]
  (render-impl/membrane->figure membrane-tree format opts))

(defn sketch->figure
  "Convert a sketch into a figure for the given format.
   Dispatches on format keyword. Each renderer is a separate namespace
   that registers a defmethod; :svg is always available.
   (sketch->figure (sketch views) :svg {})
   (sketch->figure (sketch views) :plotly {})"
  [sketch format opts]
  (render-impl/sketch->figure sketch format opts))

;; ---- Sketch Validation ----

(defn valid-sketch?
  "Check if a sketch conforms to the Malli schema.
   (valid-sketch? (sketch views))  — true if valid"
  [sketch]
  (ss/valid? sketch))

(defn explain-sketch
  "Explain why a sketch does not conform to the Malli schema.
   Returns nil if valid, or a Malli explanation map if invalid.
   (explain-sketch (sketch views))"
  [sketch]
  (ss/explain sketch))

(defn svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, :tiles, :visible-tiles, and :texts — useful for asserting
   plot structure.
   Accepts SVG hiccup or a PlotSpec (auto-renders to SVG first).
   (svg-summary (plot views))  — summary of rendered SVG
   (svg-summary my-spec)       — auto-renders PlotSpec, then summarizes"
  ([svg-or-spec]
   (if (view/plot-spec? svg-or-spec)
     (svg/svg-summary (plot svg-or-spec))
     (svg/svg-summary svg-or-spec)))
  ([svg-or-spec theme]
   (if (view/plot-spec? svg-or-spec)
     (svg/svg-summary (plot svg-or-spec) theme)
     (svg/svg-summary svg-or-spec theme))))

;; ---- Multi-Plot Composition ----

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots or PlotSpecs, or a vector of vectors (explicit rows).
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
         ;; Auto-render any PlotSpecs to SVG hiccup
         flat-plots (mapv #(if (view/plot-spec? %) (plot %) %) flat-plots)
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
   views — a vector of view maps, or a PlotSpec.
   path  — file path (string or java.io.File).
   opts  — same options as `plot` (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save views \"plot.svg\")
   (save views \"plot.svg\" {:width 800 :height 600})"
  ([spec-or-views path]
   (let [views (extract-views spec-or-views)
         opts (carry-opts spec-or-views)]
     (if (seq opts)
       (svg/save views path opts)
       (svg/save views path))))
  ([spec-or-views path opts]
   (svg/save (extract-views spec-or-views) path
             (merge (carry-opts spec-or-views) opts))))
