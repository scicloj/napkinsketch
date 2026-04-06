;; # Customization
;;
;; How to customize plots: dimensions, labels, scales, mark styling,
;; annotations, palettes, themes, legend placement, and interactivity.

(ns napkinsketch-book.customization
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Clojure2d — palette and gradient discovery
   [clojure2d.color :as c2d]))

;; ## Dimensions

;; A wide, short plot.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:width 800 :height 250}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 800))))])

;; A tall, narrow plot.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:width 300 :height 500}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 300))))])

;; ## Titles and Labels

;; Override axis labels and add a title.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:title "Iris Sepal Measurements"
                       :x-label "Length (cm)"
                       :y-label "Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Sepal Measurements"} (:texts s)))))])

;; Add a subtitle and caption for context.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:title "Iris Measurements"
                       :subtitle "Sepal dimensions across three species"
                       :caption "Source: Fisher's Iris dataset (1936)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Measurements"} (:texts s))
                                (some (fn [t] (.contains ^String t "Sepal dimensions")) (:texts s)))))])

;; ## Scales

;; Use a log scale for data spanning orders of magnitude.

(def exponential-data
  {:x (range 1 50)
   :y (map #(* 2 (Math/pow 1.1 %)) (range 1 50))})

;; Linear scale — hard to see the structure.

(-> exponential-data
    (sk/lay-point :x :y)
    (sk/options {:title "Linear Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Log y-scale — reveals the exponential trend.

(-> exponential-data
    (sk/lay-point :x :y)
    (sk/scale :y :log)
    (sk/options {:title "Log Y Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Lock the y-axis to a specific range.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/scale :y {:type :linear :domain [0 6]})
    (sk/options {:title "Fixed Y Domain [0, 6]"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Mark Styling

;; Pass `:alpha` and `:size` directly to methods.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.5 :size 5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; `:size` controls line thickness on line-based marks:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/lay-line :x :y {:size 3}))

(kind/test-last [(fn [v] (= 1 (:lines (sk/svg-summary v))))])

;; Alpha works on bars and polygons too.

(-> data/iris
    (sk/lay-bar :species {:alpha 0.4}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; ## Annotations

;; Add reference lines and shaded bands to highlight regions of interest.

;; Horizontal and vertical reference lines.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/annotate (sk/rule-h 3.0) (sk/rule-v 6.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 2 (:lines s)))))])

;; Shaded bands use a default opacity of 0.15.
;; Pass `{:alpha …}` to override.

(:band-opacity (sk/config))

(kind/test-last [(fn [v] (= 0.15 v))])

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/annotate (sk/band-v 5.5 6.5) (sk/band-h 3.0 3.5 {:alpha 0.3})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Custom Palette
;;
;; Pass `:palette` to override the default color cycle.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; The palette applies to all color-mapped marks.

(-> data/penguins
    (sk/lay-stacked-bar :island {:color :species})
    (sk/options {:palette ["#8B5CF6" "#F59E0B" "#10B981"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Pass a map to assign specific colors to specific categories.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette {:setosa "#E74C3C"
                                 :versicolor "#3498DB"
                                 :virginica "#2ECC71"}}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])
;; ## Named Palette Presets
;;
;; Use a keyword to select a predefined palette.  Napkinsketch accepts
;; any palette name from the
;; [clojure2d](https://github.com/Clojure2D/clojure2d) color library,
;; which includes hundreds of palettes from [ColorBrewer](https://colorbrewer2.org/), Wes Anderson,
;; thi.ng, paletteer (R packages), and more.
;;
;; Common examples:
;;
;; - `:set1`, `:set2`, `:set3` — ColorBrewer qualitative
;; - `:pastel1`, `:pastel2` — ColorBrewer pastel
;; - `:dark2`, `:paired`, `:accent` — ColorBrewer
;; - `:tableau10` — Tableau default
;; - `:category10` — D3 default
;;
;; This is a small sample — thousands more are available.
;; See the Discovering Palettes and Gradients section below.
;;
;; See the Discovering Palettes and Gradients section below for how to
;; search all available names.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette :set2}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Dark, high-contrast palette.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette :dark2}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Discovering Palettes and Gradients
;;
;; Napkinsketch delegates color to the
;; [clojure2d](https://github.com/Clojure2D/clojure2d) library, which
;; bundles thousands of named palettes and gradients.  Use
;; `clojure2d.color/find-palette` and `clojure2d.color/find-gradient`
;; to search by regex pattern.

;; Find palettes whose name contains "budapest".

(c2d/find-palette #"budapest")

(kind/test-last [(fn [v] (and (sequential? v) (some #{:grand-budapest-1} v)))])

;; Find palettes whose name contains "set".

(c2d/find-palette #"^:set")

(kind/test-last [(fn [v] (and (sequential? v) (some #{:set1} v)))])

;; Find gradients related to "viridis".

(c2d/find-gradient #"viridis")

(kind/test-last [(fn [v] (and (sequential? v) (some #{:viridis/viridis} v)))])

;; `c2d/palette` returns the colors for a given name.
;; Each color is a clojure2d `Vec4` (RGBA, 0–255 range).

(c2d/palette :grand-budapest-1)

(kind/test-last [(fn [v] (and (sequential? v) (pos? (count v))))])

;; ### Colorblind-friendly palettes
;;
;; For presentations and publications, consider palettes designed for
;; colorblind readers. Several good options are built in:
;;
;; - `:set2` — muted qualitative, 8 colors
;; - `:dark2` — dark qualitative, 8 colors
;; - `:khroma/okabeito` — designed specifically for color vision deficiency
;; - `:tableau-10` — Tableau default, high contrast

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette :khroma/okabeito}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Theme
;;
;; Customize background color, grid color, and font size.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:title "White Theme"
                       :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 10}}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Legend Position
;;
;; Control where the legend appears: `:right` (default), `:bottom`,
;; `:top`, or `:none`.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:legend-position :bottom}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (< (:width s) 700))))])

;; ## Tooltip
;;
;; Enable mouseover data values with `{:tooltip true}`.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:tooltip true}))

(kind/test-last [(fn [v] (= :div (first (sk/plot v))))])

;; ## Brush Selection
;;
;; Enable drag-to-select with `{:brush true}`. Click to reset.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:brush true}))

(kind/test-last [(fn [v] (= :div (first (sk/plot v))))])

;; Brushing becomes especially useful in a scatter plot matrix
;; (SPLOM — scatter plot matrix). Drag to select points in any panel — the selection
;; highlights across all panels, revealing multivariate structure.

(def splom-cols [:sepal_length :sepal_width :petal_length :petal_width])

(-> data/iris
    (sk/view (sk/cross splom-cols splom-cols) {:color :species})
    sk/lay-point
    (sk/options {:brush true}))

(kind/test-last [(fn [v] (= :div (first (sk/plot v))))])

;; ## What's Next
;;
;; - [**Faceting**](./napkinsketch_book.faceting.html) — split any chart into panels by one or two variables
;; - [**API Reference**](./napkinsketch_book.api_reference.html) — complete function listing with docstrings
