;; # Customization
;;
;; How to customize plots: dimensions, labels, scales, mark styling,
;; annotations, palettes, themes, legend placement, and interactivity.

(ns napkinsketch-book.customization
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Dimensions

;; A wide, short plot.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 800 :height 250}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 800))))])

;; A tall, narrow plot.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:width 300 :height 500}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (>= (:width s) 300))))])

;; ## Titles and Labels

;; Override axis labels and add a title.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Iris Sepal Measurements"
              :x-label "Length (cm)"
              :y-label "Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Sepal Measurements"} (:texts s)))))])

;; `sk/labs` sets labels in the pipeline — equivalent to passing
;; `:title`, `:x-label`, `:y-label` in `sk/plot` opts.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/labs {:title "Pipeline Labels" :x "Length" :y "Width"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Pipeline Labels"} (:texts s)))))])

;; ## Scales

;; Use a log scale for data spanning orders of magnitude.

(def exponential-data
  (tc/dataset {:x (range 1 50)
               :y (mapv #(* 2 (Math/pow 1.1 %)) (range 1 50))}))

;; Linear scale — hard to see the structure.

(-> exponential-data
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    (sk/plot {:title "Linear Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Log y-scale — reveals the exponential trend.

(-> exponential-data
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    (sk/scale :y :log)
    (sk/plot {:title "Log Y Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 49 (:points s)))))])

;; Lock the y-axis to a specific range.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/scale :y {:type :linear :domain [0 6]})
    (sk/plot {:title "Fixed Y Domain [0, 6]"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Mark Styling

;; Pass `:alpha` and `:size` directly to mark constructors.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species :alpha 0.5 :size 5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Alpha works on bars and polygons too.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar {:alpha 0.4}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; ## Annotations

;; Add reference lines and shaded bands to highlight regions of interest.

;; Horizontal and vertical reference lines.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/rule-h 3.0)
            (sk/rule-v 6.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 2 (:lines s)))))])

;; Shaded bands use a default opacity of 0.15.
;; Pass `{:alpha …}` to override.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/band-v 5.5 6.5)
            (sk/band-h 3.0 3.5 {:alpha 0.3}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Custom Palette
;;
;; Pass `:palette` to override the default color cycle.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; The palette applies to all color-mapped marks.

(-> iris
    (sk/view :species)
    (sk/lay (sk/stacked-bar {:color :species}))
    (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Named Palette Presets
;;
;; Use a keyword to select a predefined palette.
;;
;; Available presets:
;;
;; - `:set1`, `:set2`, `:set3` — ColorBrewer qualitative
;; - `:pastel1`, `:pastel2` — ColorBrewer pastel
;; - `:dark2`, `:paired`, `:accent` — ColorBrewer
;; - `:tableau10` — Tableau default
;; - `:category10` — D3 default

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette :set2}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Dark, high-contrast palette.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette :dark2}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Theme
;;
;; Customize background color, grid color, and font size.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "White Theme"
              :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 10}}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Legend Position
;;
;; Control where the legend appears: `:right` (default), `:bottom`,
;; `:top`, or `:none`.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:legend-position :bottom}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (< (:width s) 700))))])

;; ## Tooltip
;;
;; Enable mouseover data values with `{:tooltip true}`.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:tooltip true}))

(kind/test-last [(fn [v] (= :div (first v)))])

;; ## Brush Selection
;;
;; Enable drag-to-select with `{:brush true}`. Click to reset.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:brush true}))

(kind/test-last [(fn [v] (= :div (first v)))])
