;; # Cookbook
;;
;; Practical plotting recipes — how to combine marks, overlay stats,
;; and build publication-ready charts.

(ns napkinsketch-book.cookbook
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Quick Recipes

;; ### Boxplot with jittered points
;;
;; Overlay raw observations on a boxplot summary. The auto-jitter
;; detects the categorical axis and constrains points to the band width.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/boxplot)
            (sk/point {:jitter true :alpha 0.3}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (= 3 (:polygons s)))))])

;; ### Histogram with density overlay
;;
;; Compare the empirical histogram with a smooth KDE curve.

(-> iris
    (sk/view [[:sepal_length :sepal_length]])
    (sk/lay (sk/histogram {:alpha 0.5})
            (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Scatter with regression lines
;;
;; Fit a linear regression per group to reveal trends across species.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species :alpha 0.6})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ### Violin with jittered points
;;
;; Show the density shape and every observation together.

(-> iris
    (sk/view [[:species :petal_width]])
    (sk/lay (sk/violin {:alpha 0.3})
            (sk/point {:jitter true :alpha 0.4}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:polygons s)))))])

;; ### Time series with multiple layers
;;
;; Combine area, line, and points. The date axis automatically
;; adapts its tick labels to the time span.

(def ts-dates (mapv #(java.time.LocalDate/ofEpochDay (+ 18262 (* (long %) 7))) (range 52)))

(def ts-ds (tc/dataset {:date ts-dates
                        :value (mapv #(+ 100.0 (* 30.0 (Math/sin (* (double %) 0.12))))
                                     (range 52))}
                       {:key-fn keyword}))

(-> ts-ds
    (sk/view [[:date :value]])
    (sk/lay (sk/area {:alpha 0.2})
            (sk/line)
            (sk/point {:alpha 0.5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 52 (:points s))
                                (= 1 (:lines s))
                                (= 1 (:polygons s)))))])

;; ### Faceted comparison
;;
;; Split a scatter plot by species to compare patterns side by side.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/facet :species)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ### Annotated chart
;;
;; Add reference lines and shaded bands to highlight regions of interest.
;; Pass `{:alpha …}` to control band opacity.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/rule-h 3.0)
            (sk/band-v 5.5 6.5 {:alpha 0.3}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Ridgeline with color
;;
;; Compare distribution shapes across categories with overlapping
;; density curves. Grid lines at each baseline aid comparison.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (= 3 (:lines s)))))])

;; ### Stacked bars (proportions)
;;
;; Show the proportion of each species per island using 100% stacked bars.

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins
    (sk/view :island)
    (sk/lay (sk/stacked-bar-fill {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Multi-Layer Compositions

;; ### Overall regression with per-group points
;;
;; Color points by group, but fit a single overall regression line.

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Points with error bars
;;
;; Combining `point` and `errorbar` layers shows measurements
;; with uncertainty.

(def experiment
  (tc/dataset {:condition ["A" "B" "C" "D"]
               :mean [10.0 15.0 12.0 18.0]
               :ci_lo [8.0 12.0 9.5 15.5]
               :ci_hi [12.0 18.0 14.5 20.5]}))

(-> experiment
    (sk/view [[:condition :mean]])
    (sk/lay (sk/point {:size 5})
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

;; ### Lollipop with error bars
;;
;; Composing lollipop stems with error bars.

(-> experiment
    (sk/view [[:condition :mean]])
    (sk/lay (sk/lollipop)
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 16 (:lines s)))))])

;; ### Summary (Mean ± SE) with raw data
;;
;; The `summary` mark computes mean and standard error per category.

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/point {:alpha 0.3 :jitter 5})
            (sk/summary {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 153 (:points s))
                                (= 3 (:lines s)))))])

;; ### Tipping behavior
;;
;; Scatter + per-group regression to compare smoker tipping patterns.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    (sk/plot {:title "Tipping Behavior"
              :x-label "Total Bill ($)"
              :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (= 2 (:lines s))
                                (some #{"Tipping Behavior"} (:texts s)))))])

;; ## More Recipes

;; ### Confidence ribbon

;; A scatter plot with per-group linear regressions and 95%
;; confidence ribbons.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species :alpha 0.5})
            (sk/lm {:color :species :se true}))
    (sk/plot {:title "Sepal Regression with Confidence Bands"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Stacked vs grouped bars

;; Side-by-side comparison: default dodged bars vs stacked bars.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :sex}))
    (sk/plot {:title "Dodged Bars (default)"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :sex}))
    (sk/plot {:title "Stacked Bars"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Step line

;; A step plot for discrete time series data — useful when values
;; hold constant between observations.

(def daily-temps
  (tc/dataset {:day (range 1 15)
               :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]}))

(-> daily-temps
    (sk/view :day :temp)
    (sk/lay (sk/step {:color "#2196F3"})
            (sk/point {:color "#2196F3" :size 3}))
    (sk/plot {:title "Daily Temperature (Step)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:lines s))
                                (pos? (:points s)))))])

;; ### Density histogram

;; Overlay a kernel density curve on a normalized histogram —
;; useful for checking distributional assumptions.

(-> iris
    (sk/view :sepal_length)
    (sk/lay (sk/histogram {:normalize :density :alpha 0.4})
            (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (> (:polygons s) 1)))])

;; ### Contour + scatter

;; Density contour lines overlaid on a scatter plot — reveals
;; high-density regions in a point cloud.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species :alpha 0.4})
            (sk/contour {:levels 5}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Label marks

;; Annotate specific data points with text labels.

(def top5 (-> iris (tc/order-by :sepal_length :desc) (tc/head 5)))

(-> top5
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:size 5})
            (sk/label {:text :species :nudge-y 0.15}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (some #(= "virginica" %) (:texts s)))))])

;; ### Custom palette map

;; Assign specific colors to each category using a palette map.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:palette {:setosa "#E91E63"
                        :versicolor "#4CAF50"
                        :virginica "#2196F3"}
              :title "Custom Palette Map"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s)))))])

;; ### Fixed aspect ratio
;;
;; Use `sk/coord :fixed` so one unit on x equals one unit on y.
;; This makes the plot square when x and y have equal ranges.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species}) (sk/lm {:color :species}))
    (sk/coord :fixed)
    (sk/plot {:title "Fixed Aspect Ratio"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (= 3 (:lines s)))))])

;; ### Diverging color scale
;;
;; Use `:color-scale :diverging` with `:color-midpoint` to center
;; a red-white-blue gradient on a meaningful value (e.g., zero).

(-> (tc/dataset {:x (range 20)
                 :y (map #(Math/sin (/ % 3.0)) (range 20))
                 :change (map #(- % 10) (range 20))})
    (sk/view :x :y)
    (sk/lay (sk/point {:color :change}))
    (sk/plot {:color-scale :diverging
              :color-midpoint 0
              :title "Diverging Color Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 20 (:points s)))))])

;; ### LOESS confidence ribbon
;;
;; Add `{:se true}` to a LOESS smoother for a bootstrap confidence band.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:color :species})
            (sk/loess {:se true :color :species}))
    (sk/plot {:title "LOESS with 95% CI"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])


;; ### Multi-plot dashboard
;;
;; Use `sk/arrange` to combine independent plots into a grid layout.

(def iris-sepal
  (-> iris
      (sk/view :sepal_length :sepal_width)
      (sk/lay (sk/point {:color :species}))
      (sk/plot {:title "Sepal" :width 300 :height 250})))

(def iris-petal
  (-> iris
      (sk/view :petal_length :petal_width)
      (sk/lay (sk/point {:color :species}))
      (sk/plot {:title "Petal" :width 300 :height 250})))

(sk/arrange [iris-sepal iris-petal]
            {:title "Iris Dashboard" :cols 2})

(kind/test-last [(fn [v] (and (= :div (first v))
                              (= :kind/hiccup (:kindly/kind (meta v)))))])


;; ## Analytical Walkthroughs

;; ### Palmer Penguins

;; Bill dimensions separate the three species clearly.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Palmer Penguins: Bill Dimensions"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 342 (:points s)))))])

;; Per-species regression reveals different slopes.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Bill Length vs Depth with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Without grouping, Simpson's paradox: overall trend is negative.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species})
            (sk/lm))
    (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 1 (:lines s)))))])

;; Species distribution across islands.

(-> penguins
    (sk/view :island)
    (sk/lay (sk/bar {:color :species}))
    (sk/plot {:title "Species by Island"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Flipper length vs body mass — a strong positive correlation.

(-> penguins
    (sk/view [[:flipper_length_mm :body_mass_g]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Flipper Length vs Body Mass"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Body mass distribution by species.

(-> penguins
    (sk/view :body_mass_g)
    (sk/lay (sk/histogram {:color :species}))
    (sk/plot {:title "Body Mass Distribution"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Tips

;; Tipping behavior: smokers vs non-smokers.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    (sk/plot {:title "Tipping: Smokers vs Non-Smokers"
              :x-label "Total Bill ($)" :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; Tip amounts by day, colored by meal time.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :time}))
    (sk/plot {:title "Visits by Day and Meal Time"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Stacked view of the same data.

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :time}))
    (sk/plot {:title "Visits by Day (Stacked)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Horizontal bar chart of party sizes.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :sex}))
    (sk/coord :flip)
    (sk/plot {:title "Day by Gender (Horizontal)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### MPG

(def mpg (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
                     {:key-fn keyword}))

;; Horsepower vs fuel efficiency, colored by origin.

(-> mpg
    (sk/view [[:horsepower :mpg]])
    (sk/lay (sk/point {:color :origin})
            (sk/lm {:color :origin}))
    (sk/plot {:title "Horsepower vs MPG by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 392 (:points s))
                                (= 3 (:lines s)))))])

;; Displacement vs MPG — another negative correlation.

(-> mpg
    (sk/view [[:displacement :mpg]])
    (sk/lay (sk/point {:color :origin}))
    (sk/plot {:title "Engine Displacement vs Fuel Efficiency"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 398 (:points s)))))])

;; Count of cars by origin.

(-> mpg
    (sk/view :origin)
    (sk/lay (sk/bar))
    (sk/plot {:title "Cars by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])