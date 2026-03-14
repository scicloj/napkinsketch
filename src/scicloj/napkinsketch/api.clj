(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.plot :as plot-impl]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.render.membrane :as membrane]
            [scicloj.napkinsketch.render.svg :as svg]))

;; ---- Compositional API ----

(defn view
  "Create views from data and column specs.
   (view data [:x :y])         — single scatter view
   (view data [[:x1 :y1] ...]) — multiple views
   (view data :x)              — histogram view (x=y)"
  ([data spec-or-x] (view/view data spec-or-x))
  ([data x y] (view/view data x y)))

(defn lay
  "Apply one or more layers (marks) to views.
   (lay views (point) (lm))  — scatter + regression"
  [base-views & layer-specs]
  (apply view/lay base-views layer-specs))

(defn coord
  "Set coordinate system on views.
   (coord views :flip) — flipped coordinates"
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

(defn pairs
  "Upper-triangle pairs of columns, for pairwise scatter plots.
   (pairs [:a :b :c]) => [[:a :b] [:a :c] [:b :c]]"
  [cols]
  (view/pairs cols))

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
  "Set labels on views. Keys: :title, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})"
  [views label-opts]
  (view/labs views label-opts))

;; ---- Mark Constructors ----

(defn point
  "Point mark (scatter plot).
   (point)                    — default
   (point {:color :species})  — color by column"
  ([] (view/point))
  ([opts] (view/point opts)))

(defn line
  "Line mark (connected points).
   (line)                    — default
   (line {:color :group})    — one line per group"
  ([] (view/line))
  ([opts] (view/line opts)))

(defn step
  "Step line mark — connected points with horizontal-then-vertical steps.
   Useful for time series showing discrete changes.
   (step)                    — default
   (step {:color :group})    — one step line per group"
  ([] (view/step))
  ([opts] (view/step opts)))

(defn histogram
  "Histogram mark (binned counts).
   (histogram)               — default binning
   (histogram {:color :species}) — per-group histograms"
  ([] (view/histogram))
  ([opts] (view/histogram opts)))

(defn bar
  "Bar mark (categorical counts).
   (bar)                     — count occurrences
   (bar {:color :species})   — grouped bars"
  ([] (view/bar))
  ([opts] (view/bar opts)))

(defn stacked-bar
  "Stacked bar mark (categorical counts, stacked).
   (stacked-bar)                     — stacked bars
   (stacked-bar {:color :smoker})    — colored stacked bars"
  ([] (view/stacked-bar))
  ([opts] (view/stacked-bar opts)))

(defn value-bar
  "Value bar mark (categorical x, numeric y, no counting).
   (value-bar)                    — default
   (value-bar {:color :group})    — grouped value bars"
  ([] (view/value-bar))
  ([opts] (view/value-bar opts)))

(defn lm
  "Linear regression line.
   (lm)                      — single regression
   (lm {:color :species})    — per-group regression"
  ([] (view/lm))
  ([opts] (view/lm opts)))

(defn loess
  "LOESS smoothing line.
   (loess)                     — default bandwidth 0.75
   (loess {:color :species})   — per-group smoothing"
  ([] (view/loess))
  ([opts] (view/loess opts)))

(defn text
  "Text mark — data-driven labels at (x, y) positions.
   Requires :text key mapping to a column.
   (text {:text :name})                — label each point
   (text {:text :name :color :species}) — colored labels"
  ([] (view/text))
  ([opts] (view/text opts)))

(defn area
  "Area mark — filled region under a line.
   (area)                     — default
   (area {:color :species})   — one area per group"
  ([] (view/area))
  ([opts] (view/area opts)))

(defn stacked-area
  "Stacked area mark — filled regions stacked on top of each other.
   (stacked-area)                     — stacked areas
   (stacked-area {:color :group})     — colored stacked areas"
  ([] (view/stacked-area))
  ([opts] (view/stacked-area opts)))

(defn density
  "Density mark — kernel density estimation rendered as a filled area.
   (density)                    — default bandwidth
   (density {:color :species})  — per-group density curves
   (density {:bandwidth 0.5})   — custom bandwidth"
  ([] (view/density))
  ([opts] (view/density opts)))

(defn tile
  "Tile/heatmap mark — filled rectangles colored by a numeric value.
   With no options, bins x and y into a 2D grid (heatmap of counts).
   With :fill, uses a pre-computed numeric column for tile color.
   (tile)                          — 2D binned heatmap
   (tile {:fill :value})           — pre-computed fill values"
  ([] (view/tile))
  ([opts] (view/tile opts)))

(defn density2d
  "2D density estimate — KDE-smoothed heatmap.
   Computes a 2D Gaussian kernel density estimate on a grid and renders
   as filled tiles colored by density (viridis gradient).
   (density2d)                     — default bandwidth and grid
   (density2d {:kde2d-grid 40})    — finer grid resolution"
  ([] (view/density2d))
  ([opts] (view/density2d opts)))

(defn ridgeline
  "Ridgeline mark — vertically stacked KDE density curves per category.
   x should be categorical, y numeric.
   (ridgeline)                    — default
   (ridgeline {:color :species})  — colored ridgelines"
  ([] (view/ridgeline))
  ([opts] (view/ridgeline opts)))

(defn boxplot
  "Boxplot mark — displays median, quartiles, whiskers, and outliers.
   x should be categorical, y numeric.
   (boxplot)                    — single color
   (boxplot {:color :smoker})   — side-by-side grouped boxplots"
  ([] (view/boxplot))
  ([opts] (view/boxplot opts)))

(defn violin
  "Violin mark — mirrored density curve per category.
   x should be categorical, y numeric.
   (violin)                    — single color
   (violin {:color :smoker})   — side-by-side grouped violins"
  ([] (view/violin))
  ([opts] (view/violin opts)))

(defn rug
  "Rug mark — tick marks along axis margins showing individual observations.
   (rug)                     — ticks on x-axis
   (rug {:side :y})          — ticks on y-axis
   (rug {:side :both})       — ticks on both axes
   (rug {:color :species})   — colored ticks"
  ([] (view/rug))
  ([opts] (view/rug opts)))

(defn summary
  "Summary mark — mean ± standard error per category.
   Displays as a point at the mean with a vertical line showing ±1 SE.
   x should be categorical, y numeric.
   (summary)                    — single summary
   (summary {:color :species})  — per-group summary"
  ([] (view/summary))
  ([opts] (view/summary opts)))

(defn errorbar
  "Errorbar mark — vertical error bars at (x, y) positions.
   Requires :ymin and :ymax keys mapping to columns.
   (errorbar {:ymin :ci_lo :ymax :ci_hi})
   (errorbar {:ymin :ci_lo :ymax :ci_hi :color :group})"
  ([] (view/errorbar))
  ([opts] (view/errorbar opts)))

(defn lollipop
  "Lollipop mark — stem + dot at (x, y) positions.
   Like value-bar but lighter: a line from baseline to y with a dot.
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
   (band-v 4 6)  — shaded region between x=4 and x=6"
  [lo hi]
  (view/band-v lo hi))

(defn band-h
  "Horizontal shaded band from y = lo to y = hi.
   (band-h 2 4)  — shaded region between y=2 and y=4"
  [lo hi]
  (view/band-h lo hi))

;; ---- Rendering ----

(defn plot
  "Render views as a figure (default: SVG hiccup wrapped with kind/hiccup).
   (plot views)              — default 600×400 SVG
   (plot views {:width 800 :height 500 :title \"My Plot\"})
   (plot views {:format :svg})  — explicit format"
  ([views] (plot-impl/plot views))
  ([views opts] (plot-impl/plot views opts)))

(defn sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   (sketch views)              — default 600×400
   (sketch views {:width 800 :title \"My Plot\"})"
  ([views] (sketch-impl/views->sketch views))
  ([views opts] (sketch-impl/views->sketch views opts)))

(defn views->sketch
  "Convert views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   Same as `sketch` but with an explicit pipeline-style name.
   (views->sketch views)              — default 600×400
   (views->sketch views {:width 800 :title \"My Plot\"})"
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
   (svg-summary (plot views))  — summary of rendered SVG"
  [svg]
  (svg/svg-summary svg))
