;; # Customization
;;
;; How to customize plots: dimensions, labels, scales, mark styling,
;; annotations, palettes, themes, legend placement, and interactivity.

(ns plotje-book.customization
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Clojure2d -- palette and gradient discovery
   [clojure2d.color :as c2d]))

;; ## Dimensions

;; A wide, short plot.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:width 800 :height 250}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 800))))])

;; A tall, narrow plot.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:width 300 :height 500}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 300))))])

;; ## Titles and Labels

;; Override axis labels and add a title.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:title "Iris Sepal Measurements"
                 :x-label "Length (cm)"
                 :y-label "Width (cm)"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Sepal Measurements"} (:texts s)))))])

;; Add a subtitle and caption for context.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:title "Iris Measurements"
                 :subtitle "Sepal dimensions across three species"
                 :caption "Source: Fisher's Iris dataset (1936)"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Measurements"} (:texts s))
                                (some (fn [t] (.contains ^String t "Sepal dimensions")) (:texts s)))))])

;; ## Scales

;; Use a log scale for data spanning orders of magnitude.

(def exponential-data
  {:x (range 1 50)
   :y (map #(* 2 (Math/pow 1.1 %)) (range 1 50))})

;; Linear scale -- hard to see the structure.

(-> exponential-data
    (pj/lay-point :x :y)
    (pj/options {:title "Linear Scale"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Log y-scale -- reveals the exponential trend.

(-> exponential-data
    (pj/lay-point :x :y)
    (pj/scale :y :log)
    (pj/options {:title "Log Y Scale"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Lock the y-axis to a specific range.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/scale :y {:type :linear :domain [0 6]})
    (pj/options {:title "Fixed Y Domain [0, 6]"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Pin exact tick locations with `:breaks` (ggplot2's
;; `scale_*_continuous(breaks=...)`).

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/scale :y {:type :linear :breaks [2.0 3.0 4.0]}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (every? (set (:texts s)) ["2" "3" "4"]))))])

;; ## Mark Styling

;; Pass `:alpha` and `:size` directly to layer functions.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species :alpha 0.5 :size 5}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s))
                                (contains? (:alphas s) 0.5)
                                (contains? (:sizes s) 5.0))))])

;; `:size` controls line thickness on line-based marks:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (pj/lay-line :x :y {:size 3}))

(kind/test-last [(fn [v] (= 1 (:lines (pj/svg-summary v))))])

;; Alpha works on bars and polygons too.

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species {:alpha 0.4}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (contains? (:alphas s) 0.4))))])

;; ## Annotations

;; Reference lines and shaded bands are layers added with
;; `pj/lay-rule-h`, `pj/lay-rule-v`, `pj/lay-band-h`, `pj/lay-band-v`.
;; Position comes from the options map (`:y-intercept` or `:x-intercept`
;; for rules; `:y-min`/`:y-max` or `:x-min`/`:x-max` for bands);
;; appearance aesthetics (`:color`, `:alpha`) work the same way they
;; do on any other layer.

;; Horizontal and vertical reference lines.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/lay-rule-h {:y-intercept 3.0})
    (pj/lay-rule-v {:x-intercept 6.0}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 2 (:lines s)))))])

;; Shaded bands use a default opacity of 0.15.
;; Pass `{:alpha ...}` to override.

(:band-opacity (pj/config))

(kind/test-last [(fn [v] (= 0.15 v))])

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/lay-band-v {:x-min 5.5 :x-max 6.5})
    (pj/lay-band-h {:y-min 3.0 :y-max 3.5 :alpha 0.3}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

;; Note: position values must be literal numbers in this release. A
;; faceted plot with a different reference value per panel (column-mapped
;; intercept, ggplot2's `geom_hline(aes(yintercept=...))`) is on the
;; post-alpha roadmap. Today, an annotation added once with the same
;; intercept appears on every panel of the faceted pose.

;; ## Palettes
;;
;; Pass `:palette` to override the default color cycle. It accepts a
;; vector of hex strings, a map from category to hex, or a keyword
;; naming one of the built-in palettes (`:set1`, `:set2`, `:dark2`,
;; `:tableau-10`, `:category10`, `:pastel1`, `:accent`, `:paired`, and
;; many more).
;;
;; For the full list of forms, the project-level / thread-local /
;; plot-level precedence chain, and the key table, see the
;; [Configuration](./plotje_book.configuration.html) chapter.

;; Custom vector:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; Named preset -- here `:dark2` for a high-contrast qualitative palette:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:palette :dark2}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; ## Discovering Palettes and Gradients
;;
;; Plotje delegates color to the
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
;; Each color is a clojure2d `Vec4` (RGBA, 0-255 range).

(c2d/palette :grand-budapest-1)

(kind/test-last [(fn [v] (and (sequential? v) (pos? (count v))))])

;; ### Colorblind-friendly palettes
;;
;; For presentations and publications, consider palettes designed for
;; colorblind readers. Several good options are built in:
;;
;; - `:set2` -- muted qualitative, 8 colors
;; - `:dark2` -- dark qualitative, 8 colors
;; - `:khroma/okabeito` -- designed specifically for color vision deficiency
;; - `:tableau-10` -- Tableau default, high contrast

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:palette :khroma/okabeito}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; ## Theme
;;
;; Customize background color, grid color, and font size.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:title "White Theme"
                 :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 10}}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Legend Position
;;
;; Control where the legend appears: `:right` (default), `:bottom`,
;; `:top`, or `:none`.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:legend-position :bottom}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (< (:width s) 700))))])

;; ## Tooltip
;;
;; Enable mouseover data values with `{:tooltip true}`.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:tooltip true}))

(kind/test-last [(fn [v] (= :div (first (pj/plot v))))])

;; ## Brush Selection
;;
;; Enable drag-to-select with `{:brush true}`. Click to reset.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:brush true}))

(kind/test-last [(fn [v] (= :div (first (pj/plot v))))])

;; Brushing becomes especially useful in a SPLOM (scatter plot matrix).
;; Drag to select points in any panel -- the selection
;; highlights across all panels, revealing multivariate structure.

(def splom-cols [:sepal-length :sepal-width :petal-length :petal-width])

(-> (rdatasets/datasets-iris)
    (pj/pose {:color :species})
    pj/lay-point
    (pj/pose (pj/cross splom-cols splom-cols))
    (pj/options {:brush true}))

(kind/test-last [(fn [v] (= :div (first (pj/plot v))))])

;; ## What's Next
;;
;; - [**Faceting**](./plotje_book.faceting.html) -- split any chart into panels by one or two variables
;; - [**API Reference**](./plotje_book.api_reference.html) -- complete function listing with docstrings
