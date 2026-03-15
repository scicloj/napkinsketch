;; # Ranking
;;
;; Bar charts and their variants — comparing quantities across categories.

(ns napkinsketch-book.ranking
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(def sales (tc/dataset {:product [:widget :gadget :gizmo :doohickey]
                        :revenue [120 340 210 95]}))

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Bar Chart

;; Grouped (dodged) bars — count by day, colored by smoking status.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar Chart

;; Same data, stacked instead of dodged.

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :smoker}))
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar (Proportions)

;; 100% stacked bars — shows proportions instead of counts.

(-> penguins
    (sk/view :island)
    (sk/lay (sk/stacked-bar-fill {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :time}))
    (sk/coord :flip)
    sk/plot)

(kind/test-last
 [(fn [v] (let [s (sk/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Value Bar

;; Pre-computed y values (no counting).

(sk/plot [(sk/value-bar {:data sales :x :product :y :revenue})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Value Bar (Horizontal)

;; Flip for horizontal orientation.

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/value-bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Lollipop

;; Stem + dot — a lighter alternative to bar charts.

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/lollipop))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## Lollipop (Horizontal)

;; Flipped for horizontal orientation.

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/lollipop))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])
