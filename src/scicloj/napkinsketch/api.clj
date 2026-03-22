(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.plot :as plot-impl]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.render.membrane :as membrane]
            [scicloj.napkinsketch.render.svg :as svg]
            [scicloj.kindly.v4.kind :as kind]))

;; ---- Compositional API ----

(defn view
  "Create views from data and column specs.
   Data can be a Tablecloth dataset, a map of columns ({:x [1 2 3]}),
   a sequence of row maps ([{:x 1 :y 2} ...]), or a CSV path/URL.
   Column references must be keywords.
   (view data :x :y)            — two keywords, one scatter view
   (view data [:x :y])          — pair as vector, same result
   (view data [[:x1 :y1] ...])  — multiple views
   (view data :x)               — histogram view (x=y)"
  ([data spec-or-x] (view/view data spec-or-x))
  ([data x y] (view/view data x y)))

(defn lay
  "Apply one or more methods to views.
   (lay views (point) (lm))  — scatter + regression"
  [base-views & layer-specs]
  (apply view/lay base-views layer-specs))

(defn coord
  "Set coordinate system on views.
   Options: :cartesian (default), :flip, :polar, :fixed.
   (coord views :flip)   — flipped coordinates
   (coord views :fixed)  — fixed 1:1 aspect ratio"
  [views c]
  (view/coord views c))

(defn cross
  "Cartesian product of two sequences."
  [xs ys]
  (view/cross xs ys))

(defn facet
  "Split views by a categorical column into separate panels.
   Default is a horizontal row of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  ([views col] (view/facet views col))
  ([views col direction] (view/facet views col direction)))

(defn facet-grid
  "Split views by two categorical columns for a row × column grid.
   Either column may be nil for a single-dimension facet.
   (facet-grid views :smoker :sex)   — 2D grid
   (facet-grid views nil :species)   — same as facet"
  [views row-col col-col]
  (view/facet-grid views row-col col-col))

(defn distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  [data & cols]
  (apply view/distribution data cols))

(defn scale
  "Set scale options for :x or :y across all views.
   (scale views :x :log)                — log x-axis
   (scale views :y {:type :linear :domain [0 100]}) — fixed domain"
  ([views channel type-or-opts] (view/scale views channel type-or-opts))
  ([views channel type opts] (view/scale views channel type opts)))

(defn labs
  "Set labels on views. Keys: :title, :subtitle, :caption, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})
   (labs views {:title \"Title\" :subtitle \"Detail\" :caption \"Source: ...\"})"
  [views label-opts]
  (view/labs views label-opts))

;; ---- Configuration ----

(defmacro with-config
  "Execute body with thread-local config overrides.
   Overrides take precedence over set-config! and defaults,
   but per-call opts still win.
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

(defn set-config!
  "Set global config overrides. Persists across calls until reset.
   (set-config! {:palette :dark2 :theme {:bg \"#FFFFFF\"}})
   (set-config! nil)  — reset to defaults"
  [m]
  (defaults/set-config! m))

;; ---- Mark Constructors ----

(defn point
  "Scatter method — shows individual data points.
   Options: :color, :alpha, :size, :shape, :jitter, :group,
   :position, :nudge-x, :nudge-y.
   (point)                    — default
   (point {:color :species})  — color by column"
  ([] (view/point))
  ([opts] (view/point opts)))

(defn line
  "Line method — connects data points in order.
   Options: :color, :alpha, :size (stroke width), :group,
   :position, :nudge-x, :nudge-y.
   (line)                    — default
   (line {:color :group})    — one line per group"
  ([] (view/line))
  ([opts] (view/line opts)))

(defn step
  "Step method — horizontal-then-vertical connected points.
   Options: :color, :alpha, :size (stroke width), :group.
   (step)                    — default
   (step {:color :group})    — one step line per group"
  ([] (view/step))
  ([opts] (view/step opts)))

(defn histogram
  "Histogram method — bins numerical data into counted bars.
   Options: :color, :alpha, :group, :normalize (:density).
   (histogram)                       — default Sturges binning
   (histogram {:color :species})     — per-group histograms
   (histogram {:normalize :density}) — probability density axis"
  ([] (view/histogram))
  ([opts] (view/histogram opts)))

(defn bar
  "Bar method — counts categorical values (dodged by default).
   Options: :color, :alpha, :group, :position.
   (bar)                     — count occurrences
   (bar {:color :species})   — grouped (dodged) bars"
  ([] (view/bar))
  ([opts] (view/bar opts)))

(defn stacked-bar
  "Stacked bar method — counts categorical values (position :stack).
   Options: :color, :alpha, :group.
   (stacked-bar)                     — stacked bars
   (stacked-bar {:color :smoker})    — colored stacked bars"
  ([] (view/stacked-bar))
  ([opts] (view/stacked-bar opts)))

(defn stacked-bar-fill
  "Percentage stacked bar method — proportions sum to 1.0 (position :fill).
   Options: :color, :alpha, :group.
   (stacked-bar-fill)                     — 100% stacked bars
   (stacked-bar-fill {:color :smoker})    — colored 100% stacked bars"
  ([] (view/stacked-bar-fill))
  ([opts] (view/stacked-bar-fill opts)))

(defn value-bar
  "Value bar method — categorical x with pre-computed numeric y (no counting).
   Options: :color, :alpha, :group, :position.
   (value-bar)                    — default
   (value-bar {:color :group})    — grouped value bars"
  ([] (view/value-bar))
  ([opts] (view/value-bar opts)))

(defn lm
  "Linear regression method — fits a straight line to data.
   Options: :color, :alpha, :group, :se, :level, :nudge-x, :nudge-y.
   (lm)                              — single regression
   (lm {:color :species})            — per-group regression
   (lm {:se true})                   — with 95% confidence ribbon
   (lm {:se true :level 0.99})       — with 99% confidence ribbon"
  ([] (view/lm))
  ([opts] (view/lm opts)))

(defn loess
  "LOESS method — local regression smoothing (requires n >= 4).
   Options: :color, :alpha, :group, :se (boolean), :level (default 0.95), :se-boot (default 200).
   (loess)                     — default bandwidth 0.75
   (loess {:color :species})   — per-group smoothing
   (loess {:se true})          — with 95% confidence ribbon"
  ([] (view/loess))
  ([opts] (view/loess opts)))

(defn text
  "Text method — data-driven labels at (x, y) positions.
   Options: :text (required), :color, :alpha, :group, :nudge-x, :nudge-y.
   (text {:text :name})                — label each point
   (text {:text :name :color :species}) — colored labels"
  ([] (view/text))
  ([opts] (view/text opts)))

(defn label
  "Label method — text with a filled background box at (x, y) positions.
   Options: :text (required), :color, :alpha, :group, :nudge-x, :nudge-y.
   (label {:text :name})                — labeled points with background
   (label {:text :name :color :species}) — colored labels with background"
  ([] (view/label))
  ([opts] (view/label opts)))

(defn area
  "Area method — filled region under a line.
   Options: :color, :alpha, :group, :position.
   (area)                     — default
   (area {:color :species})   — one area per group"
  ([] (view/area))
  ([opts] (view/area opts)))

(defn stacked-area
  "Stacked area method — filled regions stacked cumulatively.
   Options: :color, :alpha, :group.
   (stacked-area)                     — stacked areas
   (stacked-area {:color :group})     — colored stacked areas"
  ([] (view/stacked-area))
  ([opts] (view/stacked-area opts)))

(defn density
  "Density method — kernel density estimation rendered as filled area.
   Options: :color, :alpha, :group, :bandwidth.
   (density)                    — default bandwidth
   (density {:color :species})  — per-group density curves
   (density {:bandwidth 0.5})   — custom bandwidth"
  ([] (view/density))
  ([opts] (view/density opts)))

(defn tile
  "Tile/heatmap method — filled rectangles colored by a numeric value.
   Options: :fill, :kde2d-grid, :color, :alpha.
   (tile)                          — 2D binned heatmap
   (tile {:fill :value})           — pre-computed fill values"
  ([] (view/tile))
  ([opts] (view/tile opts)))

(defn density2d
  "2D density method — KDE-smoothed heatmap.
   Options: :kde2d-grid, :bandwidth, :alpha.
   (density2d)                     — default bandwidth and grid
   (density2d {:kde2d-grid 40})    — finer grid resolution"
  ([] (view/density2d))
  ([opts] (view/density2d opts)))

(defn contour
  "Contour method — iso-density contour lines from 2D KDE.
   Options: :levels, :kde2d-grid, :bandwidth, :alpha.
   (contour)                       — default 5 levels
   (contour {:levels 8})           — custom number of iso-levels
   (contour {:kde2d-grid 40})      — finer grid resolution"
  ([] (view/contour))
  ([opts] (view/contour opts)))

(defn ridgeline
  "Ridgeline method — vertically stacked density curves per category.
   Options: :color, :alpha, :bandwidth, :group.
   (ridgeline)                    — default
   (ridgeline {:color :species})  — colored ridgelines"
  ([] (view/ridgeline))
  ([opts] (view/ridgeline opts)))

(defn boxplot
  "Boxplot method — displays median, quartiles, whiskers, and outliers.
   Options: :color, :alpha, :position, :group.
   (boxplot)                    — single color
   (boxplot {:color :smoker})   — side-by-side grouped boxplots"
  ([] (view/boxplot))
  ([opts] (view/boxplot opts)))

(defn violin
  "Violin method — mirrored density curve per category.
   Options: :color, :alpha, :bandwidth, :position, :group.
   (violin)                    — single color
   (violin {:color :smoker})   — side-by-side grouped violins"
  ([] (view/violin))
  ([opts] (view/violin opts)))

(defn rug
  "Rug method — tick marks along axis margins showing individual observations.
   Options: :side (:x, :y, :both), :color, :alpha, :group.
   (rug)                     — ticks on x-axis
   (rug {:side :y})          — ticks on y-axis
   (rug {:side :both})       — ticks on both axes"
  ([] (view/rug))
  ([opts] (view/rug opts)))

(defn summary
  "Summary method — mean ± standard error per category.
   Options: :color, :alpha, :position, :nudge-x, :nudge-y, :group.
   (summary)                    — single summary
   (summary {:color :species})  — per-group summary"
  ([] (view/summary))
  ([opts] (view/summary opts)))

(defn errorbar
  "Errorbar method — vertical error bars at (x, y) positions.
   Options: :ymin (required), :ymax (required), :color, :alpha,
   :position, :nudge-x, :nudge-y, :group.
   (errorbar {:ymin :ci_lo :ymax :ci_hi})
   (errorbar {:ymin :ci_lo :ymax :ci_hi :color :group})"
  ([] (view/errorbar))
  ([opts] (view/errorbar opts)))

(defn lollipop
  "Lollipop method — stem + dot at (x, y) positions.
   Options: :color, :alpha, :position, :nudge-x, :nudge-y, :group.
   (lollipop)                  — default
   (lollipop {:color :group})  — colored stems"
  ([] (view/lollipop))
  ([opts] (view/lollipop opts)))

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
  ([views] (plot-impl/plot views))
  ([views opts] (plot-impl/plot views opts)))

(defn sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   (sketch views)              — default 600×400
   (sketch views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([views] (sketch-impl/views->sketch views))
  ([views opts] (sketch-impl/views->sketch views opts)))

(defn views->sketch
  "Convert views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   Same as `sketch` but with an explicit pipeline-style name.
   (views->sketch views)              — default 600×400
   (views->sketch views {:width 800 :title \"My Plot\"})
   Pass {:validate false} to skip Malli schema validation."
  ([views] (sketch-impl/views->sketch views))
  ([views opts] (sketch-impl/views->sketch views opts)))

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
   :polygons, and :texts — useful for asserting plot structure.
   (svg-summary (plot views))  — summary of rendered SVG
   (svg-summary (plot views) {:grid \"#EEE\"}) — with custom theme"
  ([svg] (svg/svg-summary svg))
  ([svg theme] (svg/svg-summary svg theme)))

;; ---- Multi-Plot Composition ----

(defn arrange
  "Arrange multiple plots in a CSS grid layout.
   plots: a flat vector of plots, or a vector of vectors (explicit rows).
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
   views — a vector of view maps (same as `plot` accepts).
   path  — file path (string or java.io.File).
   opts  — same options as `plot` (:width, :height, :title, :theme, etc.).
   Tooltip and brush interactivity are not included in saved files.
   Returns the path.
   (save views \"plot.svg\")
   (save views \"plot.svg\" {:width 800 :height 600})"
  ([views path] (svg/save views path))
  ([views path opts] (svg/save views path opts)))
