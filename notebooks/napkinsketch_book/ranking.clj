;; # Ranking
;;
;; Bar charts and their variants — comparing quantities across categories.

(ns napkinsketch-book.ranking
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> data/iris
    (sk/lay-bar :species))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Bar Chart

;; Grouped (dodged) bars — count by day, colored by smoking status.

(-> data/tips
    (sk/lay-bar :day {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar Chart

;; Same data, stacked instead of dodged.

(-> data/tips
    (sk/lay-stacked-bar :day {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar (Proportions)

;; 100% stacked bars — shows proportions instead of counts.

(-> data/penguins
    (sk/lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> data/iris
    (sk/lay-bar :species)
    (sk/coord :flip))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> data/tips
    (sk/lay-bar :day {:color :time})
    (sk/coord :flip))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Value Bar

;; Pre-computed y values (no counting).

(-> sales
    (sk/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Value Bar (Horizontal)

;; Flip for horizontal orientation.

(-> sales
    (sk/lay-value-bar :product :revenue)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## [Lollipop](https://en.wikipedia.org/wiki/Lollipop_chart)

;; Stem + dot — a lighter alternative to bar charts.

(-> sales
    (sk/lay-lollipop :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## Lollipop (Horizontal)

;; Flipped for horizontal orientation.

(-> sales
    (sk/lay-lollipop :product :revenue)
    (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## What's Next
;;
;; - [**Change over Time**](./napkinsketch_book.change_over_time.html) — line charts, step functions, and stacked areas
;; - [**Configuration**](./napkinsketch_book.configuration.html) — control dimensions, palettes, and themes
