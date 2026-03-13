;; # Real-World Data
;;
;; Exploring classic datasets: penguins, tips, mpg.
;; Each section demonstrates a different analytical question.

(ns napkinsketch-book.real-world
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Palmer Penguins

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                           {:key-fn keyword}))

;; Bill dimensions separate the three species clearly.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Palmer Penguins: Bill Dimensions"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 342 (:points s))
                                (zero? (:lines s)))))])

;; Per-species regression reveals different slopes.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Bill Length vs Depth with Regression"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Without grouping, Simpson's paradox: overall trend is negative.

(-> penguins
    (sk/view [[:bill_length_mm :bill_depth_mm]])
    (sk/lay (sk/point {:color :species})
            (sk/lm))
    (sk/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 1 (:lines s)))))])

;; Species distribution across islands.

(-> penguins
    (sk/view :island)
    (sk/lay (sk/bar {:color :species}))
    (sk/plot {:title "Species by Island"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Flipper length vs body mass — a strong positive correlation.

(-> penguins
    (sk/view [[:flipper_length_mm :body_mass_g]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    (sk/plot {:title "Flipper Length vs Body Mass"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 342 (:points s))
                                (= 3 (:lines s)))))])

;; Body mass distribution by species.

(-> penguins
    (sk/view :body_mass_g)
    (sk/lay (sk/histogram {:color :species}))
    (sk/plot {:title "Body Mass Distribution"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Tips

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; Tipping behavior: smokers vs non-smokers.

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/lay (sk/point {:color :smoker})
            (sk/lm {:color :smoker}))
    (sk/plot {:title "Tipping: Smokers vs Non-Smokers"
              :x-label "Total Bill ($)" :y-label "Tip ($)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 244 (:points s))
                                (= 2 (:lines s)))))])

;; Tip amounts by day, colored by meal time.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :time}))
    (sk/plot {:title "Visits by Day and Meal Time"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Stacked view of the same data.

(-> tips
    (sk/view :day)
    (sk/lay (sk/stacked-bar {:color :time}))
    (sk/plot {:title "Visits by Day (Stacked)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Horizontal bar chart of party sizes.

(-> tips
    (sk/view :day)
    (sk/lay (sk/bar {:color :sex}))
    (sk/coord :flip)
    (sk/plot {:title "Day by Gender (Horizontal)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## MPG

(def mpg (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
                      {:key-fn keyword}))

;; Horsepower vs fuel efficiency, colored by origin.

(-> mpg
    (sk/view [[:horsepower :mpg]])
    (sk/lay (sk/point {:color :origin})
            (sk/lm {:color :origin}))
    (sk/plot {:title "Horsepower vs MPG by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 392 (:points s))
                                (= 3 (:lines s)))))])

;; Displacement vs MPG — another negative correlation.

(-> mpg
    (sk/view [[:displacement :mpg]])
    (sk/lay (sk/point {:color :origin}))
    (sk/plot {:title "Engine Displacement vs Fuel Efficiency"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 398 (:points s)))))])

;; Count of cars by origin.

(-> mpg
    (sk/view :origin)
    (sk/lay (sk/bar))
    (sk/plot {:title "Cars by Origin"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 3 (:polygons s)))))])
