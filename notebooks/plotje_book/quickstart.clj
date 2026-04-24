;; # Quickstart
;;
;; A minimal introduction to Plotje

;; ## Setup
;;
;; Add Plotje to your `deps.edn`:
;;
;; ```clojure
;; {:deps {org.scicloj/plotje {:mvn/version "..."}}}
;; ```
;;
;; Then require the API:

(ns plotje-book.quickstart
  (:require
   ;; R datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as sk]))

;; Use [Clay](https://scicloj.github.io/clay/) or other
;; [Kindly](https://scicloj.github.io/kindly-noted/)-compatible tools
;; to visualize the examples below.

;; ## Your First Plot
;;
;; Load the classic [iris](https://en.wikipedia.org/wiki/Iris_flower_data_set) dataset and scatter two columns:

(def iris (rdatasets/datasets-iris))

(-> iris
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; - `rdatasets/datasets-iris` loads the classic iris dataset from
;; [R datasets](https://vincentarelbundock.github.io/Rdatasets/) as a
;; [Tablecloth](https://scicloj.github.io/tablecloth/) dataset with
;; keyword column names.
;; - `sk/lay-point` shows each row as a dot (scatter plot).

;; ## Plain Data
;;
;; You do not need to load a CSV -- Plotje accepts plain Clojure
;; data and coerces it into a dataset internally.
;; A map of columns works directly:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; When the dataset has few columns, you can skip the column names --
;; Plotje infers them from the dataset shape:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; See [**Core Concepts**](./plotje_book.core_concepts.html) for more input formats.

;; String column names also work -- keywords are conventional but not
;; required:

(-> {"x" [1 2 3 4 5] "y" [2 4 3 5 4]}
    (sk/lay-point "x" "y"))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## Color

;; Map a column to `:color` to color points by group.

(-> iris
    (sk/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s))
                                (some #{"sepal length"} (:texts s)))))])

;; ## More Chart Types
;;
;; Each `sk/lay-*` function adds a different chart type.
;;
;; **Histogram** -- pass a single column for automatic binning:

(-> iris
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s))
                                (zero? (:points s)))))])

;; **Bar chart** -- count occurrences of a categorical column:

(-> iris
    (sk/lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; **Horizontal bars** -- flip with `sk/coord`:

(-> iris
    (sk/lay-bar :species)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; **Line chart** -- connect points in order:

(-> {:x [1 2 3 4 5 6 7 8]
     :y [3 5 4 7 6 8 7 9]}
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:lines s))
                                (zero? (:points s)))))])

;; **Boxplot** -- compare distributions across categories:

(-> iris
    (sk/lay-boxplot :species :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:lines s)))))])

;; See the [**Layer Types**](./plotje_book.layer_types.html) chapter for the full list of chart types.

;; ## Inference
;;
;; `sk/frame` declares which columns to plot without committing to a
;; chart type. When a pipeline ends at `sk/frame` (no `sk/lay-*`),
;; Plotje picks the chart type from the column types.
;; Two numerical columns produce a scatter plot:

(-> iris
    (sk/frame :sepal-length :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; A single categorical column produces a bar chart:

(-> iris
    (sk/frame :species))

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; A single numerical column produces a histogram:

(-> iris
    (sk/frame :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; See the [**Inference Rules**](./plotje_book.inference_rules.html) chapter for the full set of rules.

;; ## Multiple Layers
;;
;; Use `sk/frame` to set column mappings for a frame,
;; then add layers with `sk/lay-*`. All layers on this frame
;; inherit the frame's mappings. Here `(sk/lay-smooth {:stat :linear-model})` adds a linear model
;; (regression line) per group:

(-> iris
    (sk/frame :sepal-length :sepal-width {:color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Titles and Labels
;;
;; Use `sk/options` for width, height, title, and axis labels:

(-> iris
    (sk/lay-point :petal-length :petal-width {:color :species})
    (sk/options {:width 500 :height 350
                 :title "Iris Petals"
                 :x-label "Petal Length (cm)"
                 :y-label "Petal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Petals"} (:texts s))
                                (some #{"Petal Length (cm)"} (:texts s)))))])

;; ## Dashboards
;;
;; Combine multiple plots with `sk/arrange`:

(sk/arrange [(sk/lay-point iris :sepal-length :sepal-width {:color :species})
             (sk/lay-histogram iris :sepal-length {:color :species})]
            {:cols 2})

(kind/test-last [(fn [v] (sk/frame? v))])

;; ## Export

;; Save a plot to SVG with `sk/save`. It writes the file and returns the path:

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/save "/tmp/iris-scatter.svg"))

(kind/test-last [(fn [p] (and (string? p) (.endsWith ^String p ".svg")))])

;; For PNG output, `sk/save-png` goes through a raster backend;
;; see the Cookbook for other export paths.

;; ## What's Next
;;
;; - [**Frame Model**](./plotje_book.frame_model.html) -- the mental model behind composable plotting
;; - [**Core Concepts**](./plotje_book.core_concepts.html) -- data formats, marks, stats, color, grouping, coordinates
;; - [**Scatter Plots**](./plotje_book.scatter.html) -- the most common starting point for chart types
;; - [**Cookbook**](./plotje_book.cookbook.html) -- recipes for common multi-layer plots
;; - [**Configuration**](./plotje_book.configuration.html) -- themes, backgrounds, palettes, and other plot-level defaults
;; - [**Gallery**](./plotje_book.gallery.html) -- many more chart variations with side-by-side code
