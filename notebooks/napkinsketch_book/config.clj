;; # Configuration
;;
;; Customizing plots: dimensions, labels, titles, scales, and visual options.

(ns napkinsketch-book.config
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Custom Width and Height

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

;; ## Log Scale

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

;; ## Fixed Scale Domain

;; Lock the y-axis to a specific range.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/scale :y {:type :linear :domain [0 6]})
    (sk/plot {:title "Fixed Y Domain [0, 6]"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Direct Mark Styling

;; Pass `:alpha` and `:size` directly to mark constructors.
;; These map to opacity and radius (for points) or stroke-width (for lines).

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species :alpha 0.5 :size 5}))
    sk/plot)
(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Value Bar

;; Pre-computed values (no counting), using `value-bar`.

(def summary
  (tc/dataset {:category [:a :b :c :d]
               :value [42 28 35 19]}))

(-> summary
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    (sk/plot {:title "Pre-computed Values"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Value Bars Flipped

(-> summary
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    (sk/coord :flip)
    (sk/plot {:title "Horizontal Value Bars"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Custom Palette
;;
;; Pass `:palette` to override the default color cycle.
;; Values are hex color strings.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; The palette applies to all color-mapped marks — bars, lines, etc.

(-> iris
    (sk/view :species)
    (sk/lay (sk/stacked-bar {:color :species}))
    (sk/plot {:palette ["#8B5CF6" "#F59E0B" "#10B981"]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Named Palette Presets
;;
;; Instead of specifying hex colors, use a keyword to select a
;; predefined palette.
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

;; Dark, high-contrast palette:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette :dark2}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])
