;; # Ranking
;;
;; Bar charts and their variants — comparing quantities across categories.

(ns napkinsketch-book.ranking
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

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/lay-bar :species))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Bar Chart

;; Grouped (dodged) bars — count by day, colored by smoking status.

(-> tips
    (sk/lay-bar :day {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar Chart

;; Same data, stacked instead of dodged.

(-> tips
    (sk/lay-stacked-bar :day {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar (Proportions)

;; 100% stacked bars — shows proportions instead of counts.

(-> penguins
    (sk/lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> iris
    (sk/lay-bar :species)
    (sk/coord :flip))

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> tips
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

;; ## Lollipop

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