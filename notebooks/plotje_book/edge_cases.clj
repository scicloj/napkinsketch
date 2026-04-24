;; # Edge Cases
;;
;; This chapter tests how Plotje handles unusual or boundary
;; inputs -- missing values, extreme numbers, degenerate datasets,
;; and uncommon configurations.

(ns plotje-book.edge-cases
  (:require
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as sk]
   ;; Fastmath -- random number generation
   [fastmath.random :as rng]
   ;; Java-time -- idiomatic date/time construction
   [java-time.api :as jt]
   ;; dtype-next datetime -- vectorized temporal arithmetic
   [tech.v3.datatype.datetime :as dt-dt]
   ;; dtype-next core -- const-reader for temporal sequences
   [tech.v3.datatype :as dtype]))

;; ## Missing Data

;; Rows with `nil` values are dropped before rendering.

(def with-missing
  {:x [1 2 nil 4 5 nil 7]
   :y [3 nil 5 6 nil 8 9]})

(-> with-missing
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Infinite Values
;;
;; Rows with `Double/POSITIVE_INFINITY` or `Double/NEGATIVE_INFINITY`
;; are filtered automatically with a warning -- similar to log-scale filtering.

(def with-infinity
  {:x [1 2 3 4 5]
   :y [10.0 Double/POSITIVE_INFINITY 30.0 Double/NEGATIVE_INFINITY 50.0]})

(-> with-infinity
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s))
                                (not (clojure.string/includes? (str v) "NaN")))))])
;; ## Single Point

;; A lone data point should render without errors.

(-> {:x [3] :y [7]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:points s)))))])

;; ## Two Points with Regression

;; Regression requires at least 3 points. With only 2,
;; the line is omitted and the points still render.

(-> {:x [1 10] :y [5 50]}
    (sk/lay-point :x :y)
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:points s))
                                (zero? (:lines s)))))])

;; ## Three Points with Regression

;; With 3 points, the regression line appears.

(-> {:x [1 5 10] :y [5 25 50]}
    (sk/lay-point :x :y)
    (sk/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ## Constant X

;; All x values are the same -- the plot should still render.

(-> {:x [5 5 5 5 5] :y [1 2 3 4 5]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Constant Y

;; All y values are the same.

(-> {:x [1 2 3 4 5] :y [3 3 3 3 3]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Negative Values

;; Data spanning positive and negative ranges.

(-> {:x [-5 -3 0 3 5] :y [-2 4 0 -4 2]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; ## Very Large Values

(-> {:x [1e6 2e6 3e6] :y [1e9 2e9 3e9]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:points s)))))])

;; ## Very Small Values

(-> {:x [0.001 0.002 0.003] :y [0.0001 0.0002 0.0003]}
    (sk/lay-point :x :y))

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
    (sk/lay-point :x :y {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1000 (:points s)))))])

;; ## Many Categories

;; A bar chart with 12 categories.

(-> (let [r (rng/rng :jdk 99)]
      {:category (map #(keyword (str "cat-" %)) (range 12))
       :value (repeatedly 12 #(+ 10 (rng/irandom r 90)))})
    (sk/lay-value-bar :category :value))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 12 (:polygons s)))))])

;; ## Computed Columns

;; Derive a new column and plot it.

(-> (rdatasets/datasets-iris)
    (tc/map-columns :sepal-ratio [:sepal-length :sepal-width] /)
    (sk/lay-point :sepal-length :sepal-ratio {:color :species})
    (sk/options {:title "Sepal Length/Width Ratio"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; ## Filtered Subset

;; Plot only one species.

(-> (rdatasets/datasets-iris)
    (tc/select-rows #(= "setosa" (% :species)))
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-smooth {:stat :linear-model})
    (sk/options {:title "Setosa Only"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

;; ## Position Edge Cases

;; ### Stacked bar -- single group

;; Stack with only one color value -- no actual stacking needed.

(-> {:category ["a" "b" "c"]
     :count [10 20 15]}
    (sk/lay-value-bar :category :count {:position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Dodge -- missing category in one group

;; Group "g1" has data for "a" and "b", but "g2" only has "a".
;; Dodge should still align correctly.

(-> {:x ["a" "b" "a"]
     :g ["g1" "g1" "g2"]}
    (sk/lay-bar :x {:color :g}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Fill -- zero count category

;; One group has zero count for a category.
;; Fill should handle the zero gracefully.

(-> {:x ["a" "a" "b" "b" "b"]
     :g ["g1" "g2" "g1" "g1" "g1"]}
    (sk/lay-bar :x {:position :fill :color :g}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Nudge on scatter

;; Nudge-x on continuous data -- shifts points without error.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:nudge-x 0.1 :nudge-y -0.05}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### Confidence ribbon -- small n

;; Linear regression with se=true on exactly 3 points
;; (minimum for lm -- linear model).

(-> {:x [1 2 3] :y [2 4 5]}
    (sk/lay-point :x :y)
    (sk/lay-smooth {:stat :linear-model :confidence-band true}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 1 (:lines s)))))])

;; ### Stacked area -- single series

;; Stack with a single color group -- should render as a plain area.

(-> (let [r (rng/rng :jdk 55)]
      {:x (range 10)
       :y (repeatedly 10 #(rng/irandom r 20))})
    (sk/lay-area :x :y {:position :stack}))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Log Scale Edge Cases

;; ### Log scale with clean powers of 10

(-> {:x [1 10 100 1000 10000]
     :y [2 20 200 2000 20000]}
    (sk/lay-point :x :y)
    (sk/scale :x :log)
    (sk/scale :y :log))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:panels s)))))])

;; ### Log scale spanning decimals to large values

(-> {:x [0.001 0.01 0.1 1 10 100]
     :y [1 2 3 4 5 6]}
    (sk/lay-point :x :y)
    (sk/scale :x :log))

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; ### Log scale with non-positive values
;;
;; Non-positive values are filtered on log-scaled axes, since `log`
;; requires positive inputs. Here x includes 0 and -1:

(-> {:x [0 -1 1 10 100] :y [1 2 3 4 5]}
    (sk/lay-point :x :y)
    (sk/scale :x :log))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ## Continuous Color Edge Cases

;; ### Continuous color -- constant value
;;
;; All points have the same numeric color value. The gradient
;; should still render and not divide by zero.

(-> {:x [1 2 3] :y [4 5 6] :c [5 5 5]}
    (sk/lay-point :x :y {:color :c}))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; ### Diverging color with midpoint at zero

(-> {:x (range 20)
     :y (map #(- % 10) (range 20))
     :val (map #(- % 10.0) (range 20))}
    (sk/lay-point :x :y {:color :val})
    (sk/options {:color-scale :diverging :color-midpoint 0}))

(kind/test-last [(fn [v] (= 20 (:points (sk/svg-summary v))))])

;; ## Temporal Scale Edge Cases

;; ### Dates with very narrow range (two days apart)

(-> {:date [(jt/local-date 2025 1 1)
            (jt/local-date 2025 1 2)]
     :val [10 20]}
    (sk/lay-point :date :val))

(kind/test-last [(fn [v] (= 2 (:points (sk/svg-summary v))))])

;; ### Sub-day precision (LocalDateTime spanning hours)
;;
;; `LocalDateTime` values preserve sub-day precision. Tick labels
;; show `HH:MM` format when the range is less than a day.

(-> {:time (dt-dt/plus-temporal-amount
            (dtype/const-reader (jt/local-date-time 2025 3 15 8 0) 24)
            (map #(* (long %) 15) (range 24)) :minutes)
     :value (map #(+ 18.0 (* 4.0 (Math/sin (* % 0.3)))) (range 24))}
    (sk/lay-line :time :value)
    sk/lay-point)

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
    (sk/lay-line :time :temp)
    sk/lay-point)

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
    (sk/lay-line :date :value)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s)))))])

;; ## Coordinate Edge Cases

;; ### Polar with many categories

(-> {:cat (map #(str "cat-" %) (range 12))
     :val (repeatedly 12 #(rand-int 100))}
    (sk/lay-value-bar :cat :val)
    (sk/coord :polar))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ### Log scale + coord flip combined
;;
;; When log scale and coord flip are both applied, the panel should
;; have log ticks on the (now vertical) axis and the domain should
;; reflect the flipped layout.

(-> {:x [1 10 100 1000] :y [2 4 8 16]}
    (sk/lay-point :x :y)
    (sk/scale :x :log)
    (sk/coord :flip))

(kind/test-last
 [(fn [v]
    (let [plan (sk/plan v)
          panel (first (:panels plan))]
      (and (= 4 (:points (sk/svg-summary v)))
           (= :flip (:coord panel))
           ;; After flip: y-scale is original x-scale (log)
           (= {:type :log} (:y-scale panel))
           ;; After flip: x-scale is original y-scale (linear)
           (= {:type :linear} (:x-scale panel)))))])

;; ### Scale with explicit domain

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/scale :y {:domain [0 6]}))

(kind/test-last
 [(fn [v]
    (let [plan (sk/plan v)
          panel (first (:panels plan))]
      (= [0 6] (:y-domain panel))))])

;; ### Fixed aspect ratio with extreme domain ratio

(-> {:x (range 100) :y (range 0 10 0.1)}
    (sk/lay-point :x :y)
    (sk/coord :fixed))

(kind/test-last [(fn [v] (= 100 (:points (sk/svg-summary v))))])

;; ## Multi-Panel Edge Cases

;; ### Full grid -- cross plot
;;
;; `sk/cross` produces a full NxN grid of panels. Column names
;; appear as axis labels on each cell.

(-> (rdatasets/datasets-iris)
    (sk/frame {:color :species})
    (sk/frame (sk/cross [:sepal-length :sepal-width :petal-length]
                        [:sepal-length :sepal-width :petal-length])))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)
                               texts (:texts s)
                               col-label? #(re-find #"sepal|petal" %)]
                           (and (= 9 (:panels s))
                                (seq (filter col-label? texts)))))])
;; ## Error Messages
;;
;; Plotje produces clear error messages for common mistakes.

;; ### Non-existent column

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/lay-point :nonexistent :y)
      sk/plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (string? m))])

;; ### Non-existent color column

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/lay-point :x :y {:color :bogus})
      sk/plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (string? m))])

;; ### Unsupported polar mark

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/lay-line :x :y)
      (sk/coord :polar)
      sk/plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (re-find #"not supported with polar" m))])

;; ### Mismatched mark and stat

(try
  (-> {:x [1 2 3]}
      (sk/frame :x)
      (sk/lay {:mark :boxplot :stat :bin})
      sk/plot)
  (catch Exception e
    (ex-message e)))

(kind/test-last [(fn [m] (re-find #"must contain :boxes" m))])

;; ### x-only layer type with y column
;;
;; Layer types that use only the x column (histogram, bar, density,
;; rug) reject a y column with a clear message.

;; Histogram uses only the x column. Passing a y column is now an error:

(try
  (-> {:x [1 2 3] :y [4 5 6]}
      (sk/lay-histogram :x :y))
  (catch clojure.lang.ExceptionInfo e
    (ex-message e)))

(kind/test-last [(fn [m] (re-find #"lay-histogram uses only the x column" m))])
