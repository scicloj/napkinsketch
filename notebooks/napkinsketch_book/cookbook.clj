;; # Cookbook
;;
;; Practical plotting recipes -- how to combine marks, overlay stats,
;; and build publication-ready charts.

(ns napkinsketch-book.cookbook
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath -- random number generation
   [fastmath.random :as rng]
   ;; Java-time -- idiomatic date/time construction
   [java-time.api :as jt]
   ;; RDatasets -- additional datasets beyond the shared ones
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Quick Recipes

;; ### Boxplot with jittered points
;;
;; Overlay raw observations on a boxplot summary. The auto-[jitter](https://en.wikipedia.org/wiki/Jitter)
;; detects the categorical axis and constrains points to the band width.

(-> (rdatasets/datasets-iris)
    (sk/lay-boxplot :species :sepal-length)
    (sk/lay-point {:jitter true :alpha 0.3}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (= 3 (:polygons s)))))])

;; ### Histogram with density overlay
;;
;; Normalize the histogram to density scale so it is comparable with the KDE (kernel density estimation) curve.

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram :sepal-length {:normalize :density :alpha 0.5})
    sk/lay-density)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Scatter with regression lines
;;
;; Fit a linear regression per group to reveal trends across species.

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    (sk/lay-point {:alpha 0.6})
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ### Violin with jittered points
;;
;; Show the density shape and every observation together.

(-> (rdatasets/datasets-iris)
    (sk/lay-violin :species :petal-width {:alpha 0.3})
    (sk/lay-point {:jitter true :alpha 0.4}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:polygons s)))))])

;; ### Time series with multiple layers
;;
;; Combine area, line, and points. Date columns are detected
;; automatically -- ticks snap to calendar boundaries.

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

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:panels s))))])

;; ### Annotated chart
;;
;; Add reference lines and shaded bands to highlight regions of interest.
;; Pass `{:alpha ...}` to control band opacity.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-rule-h {:y-intercept 3.0})
    (sk/lay-band-v {:x-min 5.5 :x-max 6.5 :alpha 0.3}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Ridgeline with color
;;
;; Compare distribution shapes across categories with overlapping
;; density curves. Grid lines at each baseline aid comparison.

(-> (rdatasets/datasets-iris)
    (sk/lay-ridgeline :species :sepal-length {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (= 3 (:lines s)))))])

;; ### Stacked bars (proportions)
;;
;; Show the proportion of each species per island using 100% stacked bars.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-bar :island {:position :fill :color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Multi-Layer Compositions

;; ### Overall regression with per-group points
;;
;; Color points by group, but fit a single overall regression line.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/lay-smooth {:stat :linear-model :color nil}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ### Different data per layer
;;
;; Each `lay-*` accepts `{:data ...}` to override the sketch-level
;; dataset. This lets you overlay marks from two different tables --
;; ggplot2's `geom_line(data=df2) + geom_point(data=df1)` pattern.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:alpha 0.3})
    (sk/lay-point {:data {:sepal-length [5.0 6.5]
                          :sepal-width [3.5 3.0]}
                   :x :sepal-length :y :sepal-width
                   :color "red" :size 6}))

(kind/test-last [(fn [v] (= 152 (:points (sk/svg-summary v))))])

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
    (sk/lay-errorbar {:y-min :ci_lo :y-max :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

;; ### Lollipop with error bars
;;
;; Composing lollipop stems with error bars.

(-> experiment
    (sk/lay-lollipop :condition :mean)
    (sk/lay-errorbar {:y-min :ci_lo :y-max :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 16 (:lines s)))))])

;; ### Summary (Mean +/- SE) with Raw Data
;;
;; The `summary` method computes mean and SE (standard error) per category.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :species :sepal-length {:alpha 0.3 :jitter 5})
    (sk/lay-summary {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 153 (:points s))
                                (= 3 (:lines s)))))])

;; ### Tipping behavior
;;
;; Scatter + per-group regression to compare smoker tipping patterns.

(-> (rdatasets/reshape2-tips)
    (sk/frame {:x :total-bill :y :tip :color :smoker})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Tipping Behavior"
                 :x-label "Total Bill ($)"
                 :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (= 2 (:lines s))
                                (some #{"Tipping Behavior"} (:texts s)))))])

;; ## More Recipes

;; ### Confidence band

;; A scatter plot with per-group linear regressions and 95%
;; confidence bands.

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    (sk/lay-point {:alpha 0.5})
    (sk/lay-smooth {:stat :linear-model :confidence-band true})
    (sk/options {:title "Sepal Regression with Confidence Bands"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Stacked vs grouped bars

;; Side-by-side comparison: default dodged bars vs stacked bars.

(-> (rdatasets/reshape2-tips)
    (sk/lay-bar :day {:color :sex})
    (sk/options {:title "Dodged Bars (default)"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

(-> (rdatasets/reshape2-tips)
    (sk/lay-bar :day {:position :stack :color :sex})
    (sk/options {:title "Stacked Bars"}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Step line

;; A step plot for discrete time series data -- useful when values
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

;; Density contour lines overlaid on a scatter plot -- reveals
;; high-density regions in a point cloud.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species :alpha 0.4})
    (sk/lay-contour {:levels 5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])

;; ### Label marks

;; Annotate specific data points with text labels.

(def top5 (-> (rdatasets/datasets-iris) (tc/order-by :sepal-length :desc) (tc/head 5)))

(-> top5
    (sk/lay-point :sepal-length :sepal-width {:size 5})
    (sk/lay-label {:text :species :nudge-y 0.15}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (some #(= "virginica" %) (:texts s)))))])

;; ### Custom palette map

;; Assign specific colors to each category using a palette map.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
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

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
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
;; Add `{:confidence-band true}` to a LOESS smoother for a bootstrap confidence band.

(-> (rdatasets/datasets-iris)
    (sk/frame {:x :sepal-length :y :sepal-width :color :species})
    sk/lay-point
    (sk/lay-smooth {:confidence-band true})
    (sk/options {:title "LOESS with 95% CI"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s))
                                (= 3 (:polygons s)))))])

;; ### Multi-plot dashboard
;;
;; Use `sk/arrange` to combine independent plots into a grid layout.

(def iris-sepal
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/options {:title "Sepal" :width 300 :height 250})))

(def iris-petal
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :petal-length :petal-width {:color :species})
      (sk/options {:title "Petal" :width 300 :height 250})))

(sk/arrange [iris-sepal iris-petal]
            {:title "Iris Dashboard" :cols 2})

(kind/test-last [(fn [v] (and (sk/frame? v)
                              (= "Iris Dashboard" (-> v :opts :title))))])

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
      (sk/lay-smooth {:stat :linear-model})
      (sk/options {:title "Simulated: y = 3x + 5 + noise"})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s))
                                (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))])

;; ## Analytical Walkthroughs

;; ### Palmer Penguins

;; Bill dimensions separate the three species clearly.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
    (sk/options {:title "Palmer Penguins: Bill Dimensions"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 342 (:points s)))))])

;; Per-species regression reveals different slopes.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/frame {:x :bill-length-mm :y :bill-depth-mm :color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Bill Length vs Depth with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Without grouping, the overall trend appears negative -- an example
;; of Simpson's paradox.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-point :bill-length-mm :bill-depth-mm {:color :species})
    (sk/lay-smooth {:stat :linear-model :color nil})
    (sk/options {:title "Simpson's Paradox: Overall vs Per-Group Trend"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 1 (:lines s)))))])

;; Species distribution across islands.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-bar :island {:color :species})
    (sk/options {:title "Species by Island"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Flipper length vs body mass -- a strong positive correlation.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/frame {:x :flipper-length-mm :y :body-mass-g :color :species})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Flipper Length vs Body Mass"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Body mass distribution by species.

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-histogram :body-mass-g {:color :species})
    (sk/options {:title "Body Mass Distribution"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### Tips

;; Tipping behavior: smokers vs non-smokers.

(-> (rdatasets/reshape2-tips)
    (sk/frame {:x :total-bill :y :tip :color :smoker})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Tipping: Smokers vs Non-Smokers"
                 :x-label "Total Bill ($)" :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; Tip amounts by day, colored by meal time.

(-> (rdatasets/reshape2-tips)
    (sk/lay-bar :day {:color :time})
    (sk/options {:title "Visits by Day and Meal Time"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Stacked view of the same data.

(-> (rdatasets/reshape2-tips)
    (sk/lay-bar :day {:position :stack :color :time})
    (sk/options {:title "Visits by Day (Stacked)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Horizontal bar chart of party sizes.

(-> (rdatasets/reshape2-tips)
    (sk/lay-bar :day {:color :sex})
    (sk/coord :flip)
    (sk/options {:title "Day by Gender (Horizontal)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ### MPG

;; Engine displacement vs highway fuel efficiency, colored by vehicle class.

(-> (rdatasets/ggplot2-mpg)
    (sk/frame {:x :displ :y :hwy :color :class})
    sk/lay-point
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Displacement vs Highway MPG by Class"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 234 (:points s))
                                (pos? (:lines s)))))])

;; Displacement vs city MPG -- a similar negative correlation.

(-> (rdatasets/ggplot2-mpg)
    (sk/lay-point :displ :cty {:color :drv})
    (sk/options {:title "Engine Displacement vs City Fuel Efficiency"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 234 (:points s)))))])

;; Count of cars by drive type.

(-> (rdatasets/ggplot2-mpg)
    (sk/lay-bar :drv)
    (sk/options {:title "Cars by Drive Type"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Large Datasets and Raster Output
;;
;; By default napkinsketch renders to SVG -- great for crisp, scalable
;; charts. But when a plot has tens of thousands of points, the browser
;; must parse and layout a huge SVG DOM. For example, the full diamonds
;; dataset (53,940 rows) produces an 11 MB SVG file.
;;
;; Setting `:format :bufimg` renders the plot to a
;; `java.awt.image.BufferedImage` via membrane's Java2D backend instead
;; of SVG. For plots with many thousands of points, the raster output
;; is substantially smaller than the equivalent SVG.

;; ### SVG (default)

;; This is the default SVG output for a smaller subset:

(-> (rdatasets/ggplot2-diamonds)
    (tc/head 500)
    (sk/lay-point :carat :price {:color :cut})
    (sk/options {:title "Diamonds (500 rows, SVG)"}))

(kind/test-last [(fn [v] (= 500 (:points (sk/svg-summary v))))])

;; ### BufferedImage output
;;
;; With `:format :bufimg`, the full dataset renders as a raster image
;; in the notebook:

(-> (rdatasets/ggplot2-diamonds)
    (sk/lay-point :carat :price {:color :cut :alpha 0.3})
    (sk/options {:title "Diamonds (53,940 rows, BufferedImage)"
                 :format :bufimg}))

(kind/test-last [(fn [v] (some? v))])

;; ### Saving to PNG
;;
;; Use `sk/save-png` to write a raster image to disk:

;; ```clojure
;; (-> (rdatasets/ggplot2-diamonds)
;;     (sk/lay-point :carat :price {:color :cut})
;;     (sk/save-png "diamonds.png"))
;; ```

;; ## What's Next
;;
;; - [**Configuration**](./napkinsketch_book.configuration.html) -- control dimensions, palettes, and themes at every scope
;; - [**Customization**](./napkinsketch_book.customization.html) -- annotations, tooltips, and brush selection
