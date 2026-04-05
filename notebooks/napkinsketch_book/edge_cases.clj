;; # Edge Cases
;;
;; This chapter tests how napkinsketch handles unusual or boundary
;; inputs — missing values, extreme numbers, degenerate datasets,
;; and uncommon configurations.
;;
;; Testing robustness: missing data, extreme values, small datasets,
;; many categories, computed columns, and other tricky scenarios.

(ns napkinsketch-book.edge-cases
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
   [java-time.api :as jt]
   ;; dtype-next datetime — vectorized temporal arithmetic
   [tech.v3.datatype.datetime :as dt-dt]
   ;; dtype-next core — const-reader for temporal sequences
   [tech.v3.datatype :as dtype]))

;; ## Missing Data

;; Rows with `nil` values are dropped gracefully.

(def with-missing
  {:x [1 2 nil 4 5 nil 7]
   :y [3 nil 5 6 nil 8 9]})

(-> with-missing
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Infinite Values
;;
;; Rows with `Double/POSITIVE_INFINITY` or `Double/NEGATIVE_INFINITY`
;; are filtered automatically with a warning — similar to log-scale filtering.

(def with-infinity
  {:x [1 2 3 4 5]
   :y [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]})

(-> with-infinity
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s))
                                (not (clojure.string/includes? (str v) "NaN")))))])
;; ## Single Point

;; A lone data point should render without errors.

(-> {:x [3] :y [7]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:points s)))))])

;; ## Two Points with Regression

;; Regression requires at least 3 points. With only 2,
;; the line is gracefully omitted.

(-> {:x [1 10] :y [5 50]}
    (sk/xkcd7-lay-point :x :y)
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:points s))
                                (zero? (:lines s)))))])

;; ## Three Points with Regression

;; With 3 points, the regression line appears.

(-> {:x [1 5 10] :y [5 25 50]}
    (sk/xkcd7-lay-point :x :y)
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ## Constant X

;; All x values are the same — the plot should still render.

(-> {:x [5 5 5 5 5] :y [1 2 3 4 5]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Constant Y

;; All y values are the same.

(-> {:x [1 2 3 4 5] :y [3 3 3 3 3]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Negative Values

;; Data spanning positive and negative ranges.

(-> {:x [-5 -3 0 3 5] :y [-2 4 0 -4 2]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Very Large Values

(-> {:x [1e6 2e6 3e6] :y [1e9 2e9 3e9]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Very Small Values

(-> {:x [0.001 0.002 0.003] :y [0.0001 0.0002 0.0003]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Large Dataset

;; 1000 random points, colored by group.

(def large-data
  (let [r (rng/rng :jdk 42)]
    {:x (repeatedly 1000 #(rng/drandom r))
     :y (repeatedly 1000 #(rng/drandom r))
     :group (repeatedly 1000 #([:a :b :c] (rng/irandom r 3)))}))

(-> large-data
    (sk/xkcd7-lay-point :x :y {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1000 (:points s)))))])

;; ## Many Categories

;; A bar chart with 12 categories.

(-> (let [r (rng/rng :jdk 99)]
      {:category (map #(keyword (str "cat-" %)) (range 12))
       :value (repeatedly 12 #(+ 10 (rng/irandom r 90)))})
    (sk/xkcd7-lay-value-bar :category :value))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 12 (:polygons s)))))])

;; ## Computed Columns

;; Derive a new column and plot it.

(-> data/iris
    (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
    (sk/xkcd7-lay-point :sepal_length :sepal_ratio {:color :species})
    (sk/xkcd7-options {:title "Sepal Length/Width Ratio"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Filtered Subset

;; Plot only one species.

(-> data/iris
    (tc/select-rows #(= "setosa" (% :species)))
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    sk/xkcd7-lay-lm
    (sk/xkcd7-options {:title "Setosa Only"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## Position Edge Cases

;; ### Stacked bar — single group

;; Stack with only one color value — no actual stacking needed.

(-> {:category ["a" "b" "c"]
     :count [10 20 15]}
    (sk/xkcd7-lay-value-bar :category :count {:position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Dodge — missing category in one group

;; Group "g1" has data for "a" and "b", but "g2" only has "a".
;; Dodge should still align correctly.

(-> {:x ["a" "b" "a"]
     :g ["g1" "g1" "g2"]}
    (sk/xkcd7-lay-bar :x {:color :g}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Fill — zero count category

;; One group has zero count for a category.
;; Fill should handle the zero gracefully.

(-> {:x ["a" "a" "b" "b" "b"]
     :g ["g1" "g2" "g1" "g1" "g1"]}
    (sk/xkcd7-lay-stacked-bar-fill :x {:color :g}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Nudge on scatter

;; Nudge-x on continuous data — shifts points without error.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:nudge-x 0.1 :nudge-y -0.05}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### Confidence ribbon — small n

;; Linear regression with se=true on exactly 3 points
;; (minimum for lm — linear model).

(-> {:x [1 2 3] :y [2 4 5]}
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-lay-lm {:se true}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ### Stacked area — single series

;; Stack with a single color group — should render as a plain area.

(-> (let [r (rng/rng :jdk 55)]
      {:x (range 10)
       :y (repeatedly 10 #(rng/irandom r 20))})
    (sk/xkcd7-lay-stacked-area :x :y))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Log Scale Edge Cases

;; ### Log scale with clean powers of 10

(-> {:x [1 10 100 1000 10000]
     :y [2 20 200 2000 20000]}
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-scale :x :log)
    (sk/xkcd7-scale :y :log))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:panels s)))))])

;; ### Log scale spanning decimals to large values

(-> {:x [0.001 0.01 0.1 1 10 100]
     :y [1 2 3 4 5 6]}
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-scale :x :log))

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; ### Log scale with non-positive values
;;
;; Non-positive values are filtered on log-scaled axes
;; (following ggplot2 behavior). Here x includes 0 and -1:

(-> {:x [0 -1 1 10 100] :y [1 2 3 4 5]}
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-scale :x :log))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ## Continuous Color Edge Cases

;; ### Continuous color — constant value
;;
;; All points have the same numeric color value. The gradient
;; should still render and not divide by zero.

(-> {:x [1 2 3] :y [4 5 6] :c [5 5 5]}
    (sk/xkcd7-lay-point :x :y {:color :c}))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ### Diverging color with midpoint at zero

(-> {:x (range 20)
     :y (map #(- % 10) (range 20))
     :val (map #(- % 10.0) (range 20))}
    (sk/xkcd7-lay-point :x :y {:color :val})
    (sk/xkcd7-options {:color-scale :diverging :color-midpoint 0}))

(kind/test-last [(fn [v] (= 20 (:points (sk/svg-summary v))))])

;; ## Temporal Scale Edge Cases

;; ### Dates with very narrow range (two days apart)

(-> {:date [(jt/local-date 2025 1 1)
            (jt/local-date 2025 1 2)]
     :val [10 20]}
    (sk/xkcd7-lay-point :date :val))

(kind/test-last [(fn [v] (= 2 (:points (sk/svg-summary v))))])

;; ### Sub-day precision (LocalDateTime spanning hours)
;;
;; `LocalDateTime` values preserve sub-day precision. Tick labels
;; show `HH:MM` format when the range is less than a day.

(-> {:time (dt-dt/plus-temporal-amount
            (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
            (map #(* (long %) 15) (range 24)) :minutes)
     :value (map #(+ 18.0 (* 4.0 (Math/sin (* % 0.3)))) (range 24))}
    (sk/xkcd7-lay-line :time :value)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 24 (:points s))
                                (= 1 (:lines s)))))])

;; ### Instant with sub-day precision
;;
;; `java.time.Instant` values are converted to `LocalDateTime` (UTC) for
;; calendar-aware tick formatting. Tick labels show hours when the range
;; spans less than a day.

(-> {:time (dt-dt/plus-temporal-amount
            (dtype/const-reader (jt/instant 1750003200000) 12)
            (range 12) :hours)
     :temp (map #(+ 20.0 (* 5.0 (Math/sin (* % 0.5)))) (range 12))}
    (sk/xkcd7-lay-line :time :temp)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 12 (:points s))
                                (= 1 (:lines s))
                                (some #(re-find #":\d\d" %) (:texts s)))))])

;; ### Multi-year date range
;;
;; With a date range spanning several years, tick labels show year values.

(-> {:date (dt-dt/plus-temporal-amount
            (dtype/const-reader (jt/local-date 2020 1 1) 20)
            (map #(* (long %) 120) (range 20)) :days)
     :value (map #(+ 100 (* 50 (Math/sin (* % 0.4)))) (range 20))}
    (sk/xkcd7-lay-line :date :value)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s)))))])

;; ## Coordinate Edge Cases

;; ### Polar with many categories

(-> {:cat (map #(str "cat-" %) (range 12))
     :val (repeatedly 12 #(rand-int 100))}
    (sk/xkcd7-lay-value-bar :cat :val)
    (sk/xkcd7-coord :polar))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Fixed aspect ratio with extreme domain ratio

(-> {:x (range 100) :y (range 0 10 0.1)}
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-coord :fixed))

(kind/test-last [(fn [v] (= 100 (:points (sk/svg-summary v))))])

;; ## Multi-Panel Edge Cases

;; ### Full grid — cross plot
;;
;; `sk/cross` produces a full N×N grid of panels.
;; Strip labels must appear for every column and row.

(-> data/iris
    (sk/xkcd7-view (sk/cross [:sepal_length :sepal_width :petal_length] [:sepal_length :sepal_width :petal_length]))
    (sk/xkcd7-lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               texts (:texts s)
                               strip-labels (filter #(re-find #"sepal|petal" %) texts)]
                           (and (= 9 (:panels s))
                                (= 6 (count strip-labels)))))])
;; ## Error Messages
;;
;; Napkinsketch produces clear error messages for common mistakes.

;; ### Non-existent column

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/xkcd7-lay-point :nonexistent :y)
      sk/xkcd7-plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (string? m))])

;; ### Non-existent color column

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/xkcd7-lay-point :x :y {:color :bogus})
      sk/xkcd7-plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (string? m))])

;; ### Unsupported polar mark

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/xkcd7-lay-line :x :y)
      (sk/xkcd7-coord :polar)
      sk/xkcd7-plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (re-find #"not supported with polar" m))])

;; ### Mismatched mark and stat

(try
  (-> {:x [1 2 3]}
      (sk/xkcd7-view :x)
      (sk/xkcd7-lay {:mark :boxplot :stat :bin})
      sk/xkcd7-plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (re-find #"must contain :boxes" m))])

;; ### x-only method with y column
;;
;; Methods that use only the x column (histogram, bar, density, rug)
;; reject a y column with a clear message.

;; Note: in the xkcd7 API, `(lay-histogram data :x :y)` creates an
;; entry with both :x and :y. The histogram stat ignores the y column
;; and renders based on x alone.

(-> {:x [1 2 3] :y [4 5 6]}
    (sk/xkcd7-lay-histogram :x :y)
    sk/svg-summary
    :polygons)

(kind/test-last [(fn [n] (pos? n))])
