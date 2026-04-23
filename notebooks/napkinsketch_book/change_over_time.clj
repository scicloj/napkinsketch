;; # Change Over Time
;;
;; Line charts and their variants -- showing change over a sequence.

(ns napkinsketch-book.change-over-time
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Line

;; Connected line through data points.

(def wave {:x (range 30)
           :y (map #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

;; ## Grouped Lines

;; Color separates multiple series.

(def waves {:x (concat (range 30) (range 30))
            :y (concat (map #(Math/sin (* % 0.3)) (range 30))
                       (map #(Math/cos (* % 0.3)) (range 30)))
            :fn (concat (repeat 30 :sin) (repeat 30 :cos))})

(-> waves
    (sk/lay-line :x :y {:color :fn}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 2 (:lines s)))))])

;; ## Thick Line

;; Constant stroke width via `:size`.

(-> wave
    (sk/lay-line :x :y {:size 4}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

;; ## Line with Points

;; Overlay points on a grouped line plot.

(def growth
  {:day [1 2 3 4 5 1 2 3 4 5]
   :value [10 15 13 18 22 8 12 11 16 19]
   :group [:a :a :a :a :a :b :b :b :b :b]})

(-> growth
    (sk/frame {:x :day :y :value :color :group})
    sk/lay-line
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 2 (:lines s)))))])

;; ## Step

;; Horizontal-then-vertical connected points.

(-> {:x [1 2 3 4 5]
     :y [2 4 1 5 3]}
    (sk/lay-step :x :y)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

;; ## Step by Group

;; Grouped step lines.

(-> growth
    (sk/frame {:x :day :y :value :color :group})
    sk/lay-step
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 2 (:lines s)))))])

;; ## Area

;; Filled area under a line.

(-> {:x (range 30)
     :y (map #(Math/sin (* % 0.3)) (range 30))}
    (sk/lay-area :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:polygons s)))))])

;; ## [Stacked Area](https://en.wikipedia.org/wiki/Area_chart)

;; Each group fills above the previous.

(-> {:x (concat (range 10) (range 10) (range 10))
     :y (concat [1 2 3 4 5 4 3 2 1 0]
                [2 2 2 3 3 3 2 2 2 2]
                [1 1 1 1 2 2 2 1 1 1])
     :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
    (sk/lay-area :x :y {:position :stack :color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])

;; ## Dates on the x-axis

;; Real time-series usually have an actual date column, not an
;; integer step. Napkinsketch detects temporal columns
;; (`java.util.Date` via `#inst`, `java.time.LocalDate`,
;; `LocalDateTime`, `Instant`) and picks calendar-aware tick
;; labels automatically.

(-> {:date [#inst "2024-01-01" #inst "2024-02-01" #inst "2024-03-01"
            #inst "2024-04-01" #inst "2024-05-01" #inst "2024-06-01"]
     :temperature [3 5 9 14 19 23]}
    (sk/lay-line :date :temperature)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 1 (:lines s)))))])

;; ## Multiple Series Over Time

;; Pass `{:color :group}` to get one line per category. Rows are
;; drawn in their given order, so pre-sort by date if your data
;; is not already sorted.

(def months
  [#inst "2024-01-01" #inst "2024-02-01" #inst "2024-03-01"
   #inst "2024-04-01" #inst "2024-05-01" #inst "2024-06-01"])

(-> {:date        (concat months months)
     :temperature [3  5  9 14 19 23
                   15 17 19 22 25 28]
     :city        (concat (repeat 6 "Zurich")
                          (repeat 6 "Athens"))}
    (sk/lay-line :date :temperature {:color :city})
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 12 (:points s))
                                (= 2 (:lines s)))))])

;; See [Inference Rules](./napkinsketch_book.inference_rules.html)
;; for details on how dates are detected and formatted.

;; ## What's Next
;;
;; - [**Relationships**](./napkinsketch_book.relationships.html) -- heatmaps, contours, and 2D density
;; - [**Polar Coordinates**](./napkinsketch_book.polar.html) -- radial charts for cyclical data
;; - [**Gallery**](./napkinsketch_book.gallery.html) -- more chart variations with side-by-side code
