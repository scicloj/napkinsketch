(ns scicloj.napkinsketch.api
  "Public API for napkinsketch — composable plotting in Clojure."
  (:require [scicloj.napkinsketch.impl.view :as view]
            [scicloj.napkinsketch.impl.plot :as plot-impl]
            [scicloj.napkinsketch.impl.sketch :as sketch-impl]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]
            [scicloj.napkinsketch.impl.render :as render-impl]))

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

(def scale
  "Set scale options for :x or :y across all views.
   (scale views :x :log)                — log x-axis
   (scale views :y {:type :linear :domain [0 100]}) — fixed domain"
  view/scale)

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
