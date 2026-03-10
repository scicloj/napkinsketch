(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.plot :as plot-impl]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.render :as render-impl]
            [scicloj.napkinsketch.render.svg :as svg]))

;; ---- Compositional API ----

(def view
  "Create views from data and column specs.
   (view data [:x :y])         — single scatter view
   (view data [[:x1 :y1] ...]) — multiple views
   (view data :x)              — histogram view (x=y)"
  view/view)

(def lay
  "Apply one or more layers (marks) to views.
   (lay views (point) (lm))  — scatter + regression"
  view/lay)

(def coord
  "Set coordinate system on views.
   (coord views :flip) — flipped coordinates"
  view/coord)

(def cross
  "Cartesian product of two sequences."
  view/cross)

(def facet
  "Split views by a categorical column into separate panels.
   Default is a horizontal row of panels.
   (facet views :species)        — horizontal row
   (facet views :species :col)   — vertical column"
  view/facet)

(def facet-grid
  "Split views by two categorical columns for a row × column grid.
   Either column may be nil for a single-dimension facet.
   (facet-grid views :smoker :sex)   — 2D grid
   (facet-grid views nil :species)   — same as facet"
  view/facet-grid)

(def pairs
  "Upper-triangle pairs of columns, for pairwise scatter plots.
   (pairs [:a :b :c]) => [[:a :b] [:a :c] [:b :c]]"
  view/pairs)

(def distribution
  "Create diagonal views (x=y) for each column, used for histograms in SPLOM.
   (distribution data :a :b :c) => views with [[:a :a] [:b :b] [:c :c]]"
  view/distribution)

(def scale
  "Set scale options for :x or :y across all views.
   (scale views :x :log)                — log x-axis
   (scale views :y {:type :linear :domain [0 100]}) — fixed domain"
  view/scale)

(def labs
  "Set labels on views. Keys: :title, :x, :y.
   (labs views {:title \"My Plot\" :x \"X Axis\" :y \"Y Axis\"})"
  view/labs)

;; ---- Mark Constructors ----

(def point
  "Point mark (scatter plot).
   (point)                    — default
   (point {:color :species})  — color by column"
  view/point)

(def line
  "Line mark (connected points).
   (line)                    — default
   (line {:color :group})    — one line per group"
  view/line)

(def histogram
  "Histogram mark (binned counts).
   (histogram)               — default binning"
  view/histogram)

(def bar
  "Bar mark (categorical counts).
   (bar)                     — count occurrences
   (bar {:color :species})   — grouped bars"
  view/bar)

(def stacked-bar
  "Stacked bar mark (categorical counts, stacked)."
  view/stacked-bar)

(def value-bar
  "Value bar mark (categorical x, numeric y, no counting)."
  view/value-bar)

(def lm
  "Linear regression line.
   (lm)                      — single regression
   (lm {:color :species})    — per-group regression"
  view/lm)

(def loess
  "LOESS smoothing line.
   (loess)                     — default bandwidth 0.75
   (loess {:color :species})   — per-group smoothing"
  view/loess)

;; ---- Annotations ----

(def rule-v
  "Vertical reference line at x = intercept.
   (rule-v 5)  — line at x=5"
  view/rule-v)

(def rule-h
  "Horizontal reference line at y = intercept.
   (rule-h 3)  — line at y=3"
  view/rule-h)

(def band-v
  "Vertical shaded band from x = lo to x = hi.
   (band-v 4 6)  — shaded region between x=4 and x=6"
  view/band-v)

(def band-h
  "Horizontal shaded band from y = lo to y = hi.
   (band-h 2 4)  — shaded region between y=2 and y=4"
  view/band-h)

;; ---- Rendering ----

(def plot
  "Render views as a figure (default: SVG hiccup wrapped with kind/hiccup).
   (plot views)              — default 600×400 SVG
   (plot views {:width 800 :height 500 :title \"My Plot\"})
   (plot views {:format :svg})  — explicit format"
  plot-impl/plot)

(def sketch
  "Resolve views into a sketch — a plain Clojure map with data-space
   geometry, domains, tick info, legend, and layout. No membrane types,
   no datasets, no scale objects in the output. Serializable data.
   (sketch views)              — default 600×400
   (sketch views {:width 800 :title \"My Plot\"})"
  sketch-impl/resolve-sketch)

(def render-figure
  "Render a sketch into a figure for the given format.
   Dispatches on format keyword. Each renderer is a separate namespace
   that registers a defmethod; :svg is always available.
   (render-figure (sketch views) :svg {})
   (render-figure (sketch views) :plotly {})"
  render-impl/render-figure)

;; ---- Sketch Validation ----

(def valid-sketch?
  "Check if a sketch conforms to the Malli schema.
   (valid-sketch? (sketch views))  — true if valid"
  ss/valid?)

(def explain-sketch
  "Explain why a sketch does not conform to the Malli schema.
   Returns nil if valid, or a Malli explanation map if invalid.
   (explain-sketch (sketch views))"
  ss/explain)

(def svg-summary
  "Extract structural summary from SVG hiccup for testing.
   Returns a map with :width, :height, :panels, :points, :lines,
   :polygons, and :texts — useful for asserting plot structure.
   (svg-summary (plot views))  — summary of rendered SVG"
  svg/svg-summary)
