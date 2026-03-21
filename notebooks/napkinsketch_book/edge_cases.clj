;; # Edge Cases
;;
;; Testing robustness: missing data, extreme values, small datasets,
;; many categories, computed columns, and other tricky scenarios.

(ns napkinsketch-book.edge-cases
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation (for synthetic data)
   [fastmath.random :as rng]))

;; ## Missing Data

;; Rows with `nil` values are dropped gracefully.

(def with-missing
  {:x [1 2 nil 4 5 nil 7]
   :y [3 nil 5 6 nil 8 9]})

(-> with-missing
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Single Point

;; A lone data point should render without errors.

(-> {:x [3] :y [7]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:points s)))))])

;; ## Two Points with Regression

;; Regression requires at least 3 points. With only 2,
;; the line is gracefully omitted.

(-> {:x [1 10] :y [5 50]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:points s))
                                (zero? (:lines s)))))])

;; ## Three Points with Regression

;; With 3 points, the regression line appears.

(-> {:x [1 5 10] :y [5 25 50]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ## Constant X

;; All x values are the same — the plot should still render.

(-> {:x [5 5 5 5 5] :y [1 2 3 4 5]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Constant Y

;; All y values are the same.

(-> {:x [1 2 3 4 5] :y [3 3 3 3 3]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Negative Values

;; Data spanning positive and negative ranges.

(-> {:x [-5 -3 0 3 5] :y [-2 4 0 -4 2]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Very Large Values

(-> {:x [1e6 2e6 3e6] :y [1e9 2e9 3e9]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Very Small Values

(-> {:x [0.001 0.002 0.003] :y [0.0001 0.0002 0.0003]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

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
    (sk/view [[:x :y]])
    (sk/lay (sk/point {:color :group}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1000 (:points s)))))])

;; ## Many Categories

;; A bar chart with 12 categories.

(-> (let [r (rng/rng :jdk 99)]
      {:category (mapv #(keyword (str "cat-" %)) (range 12))
       :value (repeatedly 12 #(+ 10 (rng/irandom r 90)))})
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 12 (:polygons s)))))])

;; ## Computed Columns

;; Derive a new column and plot it.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(-> iris
    (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
    (sk/view [[:sepal_length :sepal_ratio]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Sepal Length/Width Ratio"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Filtered Subset

;; Plot only one species.

(-> iris
    (tc/select-rows #(= "setosa" (% :species)))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Setosa Only"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## Position Edge Cases

;; ### Stacked bar — single group

;; Stack with only one color value — no actual stacking needed.

(-> {:category ["a" "b" "c"]
     :count [10 20 15]}
    (sk/view :category :count)
    (sk/lay (sk/value-bar {:position :stack}))
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Dodge — missing category in one group

;; Group "g1" has data for "a" and "b", but "g2" only has "a".
;; Dodge should still align correctly.

(-> {:x ["a" "b" "a"]
     :g ["g1" "g1" "g2"]}
    (sk/view :x)
    (sk/lay (sk/bar {:color :g}))
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Fill — zero count category

;; One group has zero count for a category.
;; Fill should handle the zero gracefully.

(-> {:x ["a" "a" "b" "b" "b"]
     :g ["g1" "g2" "g1" "g1" "g1"]}
    (sk/view :x)
    (sk/lay (sk/stacked-bar-fill {:color :g}))
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Nudge on scatter

;; Nudge-x on continuous data — shifts points without error.

(-> iris
    (sk/view :sepal_length :sepal_width)
    (sk/lay (sk/point {:nudge-x 0.1 :nudge-y -0.05}))
    sk/plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### Confidence ribbon — small n

;; Linear regression with se=true on exactly 3 points
;; (minimum for lm).

(-> {:x [1 2 3] :y [2 4 5]}
    (sk/view :x :y)
    (sk/lay (sk/point) (sk/lm {:se true}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ### Stacked area — single series

;; Stack with a single color group — should render as a plain area.

(-> (let [r (rng/rng :jdk 55)]
      {:x (range 10)
       :y (repeatedly 10 #(rng/irandom r 20))})
    (sk/view :x :y)
    (sk/lay (sk/stacked-area))
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Log Scale Edge Cases

;; ### Log scale with clean powers of 10

(-> {:x [1 10 100 1000 10000]
     :y [2 20 200 2000 20000]}
    (sk/view :x :y)
    (sk/lay (sk/point))
    (sk/scale :x :log)
    (sk/scale :y :log)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:panels s)))))])

;; ### Log scale spanning decimals to large values

(-> {:x [0.001 0.01 0.1 1 10 100]
     :y [1 2 3 4 5 6]}
    (sk/view :x :y)
    (sk/lay (sk/point))
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; ## Continuous Color Edge Cases

;; ### Continuous color — constant value
;;
;; All points have the same numeric color value. The gradient
;; should still render and not divide by zero.

(-> {:x [1 2 3] :y [4 5 6] :c [5 5 5]}
    (sk/view :x :y)
    (sk/lay (sk/point {:color :c}))
    sk/plot)

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ### Diverging color with midpoint at zero

(-> {:x (range 20)
     :y (map #(- % 10) (range 20))
     :val (map #(- % 10.0) (range 20))}
    (sk/view :x :y)
    (sk/lay (sk/point {:color :val}))
    (sk/plot {:color-scale :diverging :color-midpoint 0}))

(kind/test-last [(fn [v] (= 20 (:points (sk/svg-summary v))))])

;; ## Temporal Scale Edge Cases

;; ### Dates with very narrow range (two days apart)

(-> {:date [(java.time.LocalDate/of 2025 1 1)
            (java.time.LocalDate/of 2025 1 2)]
     :val [10 20]}
    (sk/view :date :val)
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (= 2 (:points (sk/svg-summary v))))])

;; ### Sub-day precision (LocalDateTime spanning hours)
;;
;; `LocalDateTime` values preserve sub-day precision. Tick labels
;; show `HH:MM` format when the range is less than a day.

(-> {:time (mapv #(java.time.LocalDateTime/of 2025 3 15
                                              (+ 8 (int (/ % 4)))
                                              (* 15 (mod (int %) 4))
                                              0)
                 (range 24))
     :value (mapv #(+ 18.0 (* 4.0 (Math/sin (* % 0.3)))) (range 24))}
    (sk/view :time :value)
    (sk/lay (sk/line) (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 24 (:points s))
                                (= 1 (:lines s)))))])

;; ### Instant with sub-day precision
;;
;; `java.time.Instant` values are converted to `LocalDateTime` (UTC) for
;; calendar-aware tick formatting. Tick labels show hours when the range
;; spans less than a day.

(-> {:time (mapv #(java.time.Instant/ofEpochSecond
                   (+ 1750003200 (* % 3600)))
                 (range 12))
     :temp (mapv #(+ 20.0 (* 5.0 (Math/sin (* % 0.5)))) (range 12))}
    (sk/view :time :temp)
    (sk/lay (sk/line) (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 12 (:points s))
                                (= 1 (:lines s))
                                (some #(re-find #":\d\d" %) (:texts s)))))])

;; ### Multi-year date range
;;
;; With a date range spanning several years, tick labels show year values.

(-> {:date (mapv #(java.time.LocalDate/ofEpochDay (+ 18262 (* (long %) 120)))
                 (range 20))
     :value (mapv #(+ 100 (* 50 (Math/sin (* % 0.4)))) (range 20))}
    (sk/view :date :value)
    (sk/lay (sk/line) (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s)))))])

;; ## Coordinate Edge Cases

;; ### Polar with many categories

(-> {:cat (map #(str "cat-" %) (range 12))
     :val (repeatedly 12 #(rand-int 100))}
    (sk/view :cat :val)
    (sk/lay (sk/bar))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Fixed aspect ratio with extreme domain ratio

(-> {:x (range 100) :y (range 0 10 0.1)}
    (sk/view :x :y)
    (sk/lay (sk/point))
    (sk/coord :fixed)
    sk/plot)

(kind/test-last [(fn [v] (= 100 (:points (sk/svg-summary v))))])

;; ## Multi-Panel Edge Cases

;; ### Triangular grid — pairs plot
;;
;; `sk/pairs` produces a triangular layout (lower triangle of an N×N grid).
;; Strip labels must appear for every column and row, not just corners.

(-> iris
    (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length :petal_width]))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               texts (:texts s)
                               strip-labels (filter #(re-find #"sepal|petal" %) texts)]
                           (and (= 6 (:panels s))
                                (= 6 (count strip-labels)))))])