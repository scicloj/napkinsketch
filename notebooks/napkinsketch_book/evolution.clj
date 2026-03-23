;; # Evolution
;;
;; Line charts and their variants — showing change over a sequence.

(ns napkinsketch-book.evolution
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Line

;; Connected line through data points.

(def wave {:x (range 30)
           :y (mapv #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

;; ## Grouped Lines

;; Color separates multiple series.

(def waves {:x (vec (concat (range 30) (range 30)))
            :y (vec (concat (mapv #(Math/sin (* % 0.3)) (range 30))
                            (mapv #(Math/cos (* % 0.3)) (range 30))))
            :fn (vec (concat (repeat 30 :sin) (repeat 30 :cos)))})

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
    (sk/view [[:day :value]])
    (sk/lay-line {:color :group})
    (sk/lay-point {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 2 (:lines s)))))])

;; ## Step

;; Horizontal-then-vertical connected points.

(-> {:x [1 2 3 4 5]
     :y [2 4 1 5 3]}
    (sk/view [[:x :y]])
    sk/lay-step
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

;; ## Step by Group

;; Grouped step lines.

(-> growth
    (sk/view [[:day :value]])
    (sk/lay-step {:color :group})
    (sk/lay-point {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 2 (:lines s)))))])

;; ## Area

;; Filled area under a line.

(-> {:x (range 30)
     :y (mapv #(Math/sin (* % 0.3)) (range 30))}
    (sk/lay-area :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:polygons s)))))])

;; ## Stacked Area

;; Each group fills above the previous.

(-> {:x (vec (concat (range 10) (range 10) (range 10)))
     :y (vec (concat [1 2 3 4 5 4 3 2 1 0]
                     [2 2 2 3 3 3 2 2 2 2]
                     [1 1 1 1 2 2 2 1 1 1]))
     :group (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))}
    (sk/lay-stacked-area :x :y {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])