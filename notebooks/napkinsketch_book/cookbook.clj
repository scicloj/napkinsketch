;; # Cookbook
;;
;; Practical plotting recipes — how to combine marks, overlay stats,
;; and build publication-ready charts.

(ns napkinsketch-book.cookbook
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation
   [fastmath.random :as rng]
   ;; Java-time — idiomatic date/time construction
   [java-time.api :as jt]))

;; ## Quick Recipes

;; ### Boxplot with jittered points
;;
;; Overlay raw observations on a boxplot summary. The auto-[jitter](https://en.wikipedia.org/wiki/Jitter)
;; detects the categorical axis and constrains points to the band width.

(-> data/iris
    (sk/lay-boxplot :species :sepal_length)
    (sk/lay-point {:jitter true :alpha 0.3}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (= 3 (:polygons s)))))])

;; ### Histogram with density overlay
;;
;; Normalize the histogram to density scale so it is comparable with the KDE (kernel density estimation) curve.

(-> data/iris
    (sk/lay-histogram :sepal_length {:normalize :density :alpha 0.5})
    sk/lay-density)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Scatter with regression lines
;;
;; Fit a linear regression per group to reveal trends across species.

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/lay-point {:alpha 0.6})
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ### Violin with jittered points
;;
;; Show the density shape and every observation together.

(-> data/iris
    (sk/lay-violin :species :petal_width {:alpha 0.3})
    (sk/lay-point {:jitter true :alpha 0.4}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:polygons s)))))])

;; ### Time series with multiple layers
;;
;; Combine area, line, and points. Date columns are detected
;; automatically — ticks snap to calendar boundaries.

(def ts-dates (take 52 (jt/iterate jt/plus (jt/local-date 2020 1 6) (jt/weeks 1))))

(def ts-ds {:date ts-dates
            :value (map #(+ 100.0 (* 30.0 (Math/sin (* (double %) 0.12))))
                        (range 52))})

(-> ts-ds
    (sk/lay-area :date :value {:alpha 0.2})
    sk/lay-line
    (sk/lay-point {:alpha 0.5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 52 (:points s))
                                (= 1 (:lines s))
                                (= 1 (:polygons s)))))])

;; ### Faceted comparison
;;
;; Split a scatter plot by species to compare patterns side by side.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ### Annotated chart
;;
;; Add reference lines and shaded bands to highlight regions of interest.
;; Pass `{:alpha …}` to control band opacity.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/annotate (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Ridgeline with color
;;
;; Compare distribution shapes across categories with overlapping
;; density curves. Grid lines at each baseline aid comparison.

(-> data/iris
    (sk/lay-ridgeline :species :sepal_length {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (= 3 (:lines s)))))])

;; ### Stacked bars (proportions)
;;
;; Show the proportion of each species per island using 100% stacked bars.

(-> data/penguins
    (sk/lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Multi-Layer Compositions

;; ### Overall regression with per-group points
;;
;; Color points by group, but fit a single overall regression line.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/lay-lm {:color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Points with [Error Bars](https://en.wikipedia.org/wiki/Error_bar)
;;
;; Combining `point` and `errorbar` layers shows measurements
;; with uncertainty.

(def experiment
  {:condition ["A" "B" "C" "D"]
   :mean [10.0 15.0 12.0 18.0]
   :ci_lo [8.0 12.0 9.5 15.5]
   :ci_hi [12.0 18.0 14.5 20.5]})

(-> experiment
    (sk/lay-point :condition :mean {:size 5})
    (sk/lay-errorbar {:ymin :ci_lo :ymax :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

;; ### Lollipop with error bars
;;
;; Composing lollipop stems with error bars.

(-> experiment
    (sk/lay-lollipop :condition :mean)
    (sk/lay-errorbar {:ymin :ci_lo :ymax :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 16 (:lines s)))))])

;; ### Summary (Mean ± SE) with Raw Data
;;
;; The `summary` method computes mean and SE (standard error) per category.

(-> data/iris
    (sk/lay-point :species :sepal_length {:alpha 0.3 :jitter 5})
    (sk/lay-summary {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 153 (:points s))
                                (= 3 (:lines s)))))])

;; ### Tipping behavior
;;
;; Scatter + per-group regression to compare smoker tipping patterns.

(-> data/tips
    (sk/view :total_bill :tip {:color :smoker})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Tipping Behavior"
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

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/lay-point {:alpha 0.5})
    (sk/lay-lm {:se true})
    (sk/options {:title "Sepal Regression with Confidence Bands"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Stacked vs grouped bars

;; Side-by-side comparison: default dodged bars vs stacked bars.

(-> data/tips
    (sk/lay-bar :day {:color :sex})
    (sk/options {:title "Dodged Bars (default)"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

(-> data/tips
    (sk/lay-stacked-bar :day {:color :sex})
    (sk/options {:title "Stacked Bars"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Step line

;; A step plot for discrete time series data — useful when values
;; hold constant between observations.

(def daily-temps
  {:day (range 1 15)
   :temp [12 14 14 16 18 17 15 13 14 16 19 21 20 18]})

(-> daily-temps
    (sk/lay-step :day :temp {:color "#2196F3"})
    (sk/lay-point {:color "#2196F3" :size 3})
    (sk/options {:title "Daily Temperature (Step)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:lines s))
                                (pos? (:points s)))))])

;; ### Contour + scatter

;; Density contour lines overlaid on a scatter plot — reveals
;; high-density regions in a point cloud.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species :alpha 0.4})
    (sk/lay-contour {:levels 5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Label marks

;; Annotate specific data points with text labels.

(def top5 (-> data/iris (tc/order-by :sepal_length :desc) (tc/head 5)))

(-> top5
    (sk/lay-point :sepal_length :sepal_width {:size 5})
    (sk/lay-label {:text :species :nudge-y 0.15}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (some #(= "virginica" %) (:texts s)))))])

;; ### Custom palette map

;; Assign specific colors to each category using a palette map.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:palette {:setosa "#E91E63"
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

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm
    (sk/coord :fixed)
    (sk/options {:title "Fixed Aspect Ratio"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (= 3 (:lines s)))))])

;; ### Diverging color scale
;;
;; Use `:color-scale :diverging` with `:color-midpoint` to center
;; a red-white-blue gradient on a meaningful value (e.g., zero).

(-> {:x (range 20)
     :y (map #(Math/sin (/ % 3.0)) (range 20))
     :change (map #(- % 10) (range 20))}
    (sk/lay-point :x :y {:color :change})
    (sk/options {:color-scale :diverging
                       :color-midpoint 0
                       :title "Diverging Color Scale"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 20 (:points s)))))])

;; ### LOESS (Local Regression) [Confidence Ribbon](https://en.wikipedia.org/wiki/Confidence_band)
;;
;; Add `{:se true}` to a LOESS smoother for a bootstrap confidence band.

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    (sk/lay-loess {:se true})
    (sk/options {:title "LOESS with 95% CI"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])

;; ### Multi-plot dashboard
;;
;; Use `sk/arrange` to combine independent plots into a grid layout.

(def iris-sepal
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width {:color :species})
      (sk/options {:title "Sepal" :width 300 :height 250})))

(def iris-petal
  (-> data/iris
      (sk/lay-point :petal_length :petal_width {:color :species})
      (sk/options {:title "Petal" :width 300 :height 250})))

(sk/arrange [iris-sepal iris-petal]
            {:title "Iris Dashboard" :cols 2})

(kind/test-last [(fn [v] (and (= :div (first v))
                              (= :kind/hiccup (:kindly/kind (meta v)))))])

;; ### Labeled scatter

;; Combine points with text labels, using nudge to offset text from data points.

(def top-cities
  {:city ["Tokyo" "Delhi" "Shanghai" "São Paulo" "Mumbai"]
   :population [37.4 32.9 29.2 22.4 21.7]
   :area [2194 1484 6341 1521 603]})

(-> top-cities
    (sk/lay-point :area :population)
    (sk/lay-text {:text :city :nudge-y 1.0})
    (sk/options {:title "Population vs Area"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (every? (set (:texts s)) ["Tokyo" "Delhi"]))))])

;; ## Simulated Data
;;
;; Generate data from a known model and verify the regression recovers it.

(let [r (rng/rng :jdk 77)
      xs (range 0 10 0.5)
      ys (map #(+ (* 3 %)
                  5
                  (* 2 (- (rng/drandom r) 0.5)))
              xs)]
  (-> {:x xs :y ys}
      (sk/lay-point :x :y)
      sk/lay-lm
      (sk/options {:title "Simulated: y = 3x + 5 + noise"})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s))
                                (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))])

;; ## Analytical Walkthroughs

;; ### Palmer Penguins

;; Bill dimensions separate the three species clearly.

(-> data/penguins
    (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
    (sk/options {:title "Palmer Penguins: Bill Dimensions"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 342 (:points s)))))])

;; Per-species regression reveals different slopes.

(-> data/penguins
    (sk/view :bill_length_mm :bill_depth_mm {:color :species})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Bill Length vs Depth with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Without grouping, the overall trend appears negative — an example
;; of Simpson's paradox.

(-> data/penguins
    (sk/lay-point :bill_length_mm :bill_depth_mm {:color :species})
    (sk/lay-lm {:color nil})
    (sk/options {:title "Simpson's Paradox: Overall vs Per-Group Trend"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 1 (:lines s)))))])

;; Species distribution across islands.

(-> data/penguins
    (sk/lay-bar :island {:color :species})
    (sk/options {:title "Species by Island"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Flipper length vs body mass — a strong positive correlation.

(-> data/penguins
    (sk/view :flipper_length_mm :body_mass_g {:color :species})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Flipper Length vs Body Mass"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Body mass distribution by species.

(-> data/penguins
    (sk/lay-histogram :body_mass_g {:color :species})
    (sk/options {:title "Body Mass Distribution"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Tips

;; Tipping behavior: smokers vs non-smokers.

(-> data/tips
    (sk/view :total_bill :tip {:color :smoker})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Tipping: Smokers vs Non-Smokers"
                       :x-label "Total Bill ($)" :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; Tip amounts by day, colored by meal time.

(-> data/tips
    (sk/lay-bar :day {:color :time})
    (sk/options {:title "Visits by Day and Meal Time"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Stacked view of the same data.

(-> data/tips
    (sk/lay-stacked-bar :day {:color :time})
    (sk/options {:title "Visits by Day (Stacked)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Horizontal bar chart of party sizes.

(-> data/tips
    (sk/lay-bar :day {:color :sex})
    (sk/coord :flip)
    (sk/options {:title "Day by Gender (Horizontal)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### MPG

;; Horsepower vs fuel efficiency, colored by origin.

(-> data/mpg
    (sk/view :horsepower :mpg {:color :origin})
    sk/lay-point
    sk/lay-lm
    (sk/options {:title "Horsepower vs MPG by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 392 (:points s))
                                (= 3 (:lines s)))))])

;; Displacement vs MPG — another negative correlation.

(-> data/mpg
    (sk/lay-point :displacement :mpg {:color :origin})
    (sk/options {:title "Engine Displacement vs Fuel Efficiency"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 398 (:points s)))))])

;; Count of cars by origin.

(-> data/mpg
    (sk/lay-bar :origin)
    (sk/options {:title "Cars by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## What's Next
;;
;; - [**Configuration**](./napkinsketch_book.configuration.html) — control dimensions, palettes, and themes at every scope
;; - [**Customization**](./napkinsketch_book.customization.html) — annotations, tooltips, and brush selection
