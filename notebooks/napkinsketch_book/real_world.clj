;; # Real-World Data
;;
;; Exploring classic datasets: penguins, diamonds, tips, mpg.
;; Each section demonstrates a different analytical question.

(ns napkinsketch-book.real-world
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Palmer Penguins

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                           {:key-fn keyword}))

;; Bill dimensions separate the three species clearly.

(-> penguins
    (ns/view [[:bill_length_mm :bill_depth_mm]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:title "Palmer Penguins: Bill Dimensions"}))

;; Per-species regression reveals different slopes.

(-> penguins
    (ns/view [[:bill_length_mm :bill_depth_mm]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    (ns/plot {:title "Bill Length vs Depth with Regression"}))

;; Without grouping, Simpson's paradox: overall trend is negative.

(-> penguins
    (ns/view [[:bill_length_mm :bill_depth_mm]])
    (ns/lay (ns/point {:color :species})
            (ns/lm))
    (ns/plot {:title "Simpson's Paradox: Overall vs Per-Group Trend"}))

;; Species distribution across islands.

(-> penguins
    (ns/view :island)
    (ns/lay (ns/bar {:color :species}))
    (ns/plot {:title "Species by Island"}))

;; Flipper length vs body mass — a strong positive correlation.

(-> penguins
    (ns/view [[:flipper_length_mm :body_mass_g]])
    (ns/lay (ns/point {:color :species})
            (ns/lm {:color :species}))
    (ns/plot {:title "Flipper Length vs Body Mass"}))

;; Body mass distribution by species.

(-> penguins
    (ns/view :body_mass_g)
    (ns/lay (ns/histogram {:color :species}))
    (ns/plot {:title "Body Mass Distribution"}))

;; ## Diamonds

(def diamonds (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/diamonds.csv"
                           {:key-fn keyword}))

;; Carat vs price — the classic non-linear relationship.

(-> diamonds
    (ns/view [[:carat :price]])
    (ns/lay (ns/point))
    (ns/plot {:title "Diamond Carat vs Price"
              :config {:point-radius 1 :point-opacity 0.3}}))

;; Log scale reveals the structure.

(-> diamonds
    (ns/view [[:carat :price]])
    (ns/lay (ns/point))
    (ns/scale :y :log)
    (ns/plot {:title "Carat vs Price (Log Scale)"
              :config {:point-radius 1 :point-opacity 0.3}}))

;; Price distribution — highly right-skewed.

(-> diamonds
    (ns/view :price)
    (ns/lay (ns/histogram))
    (ns/plot {:title "Diamond Price Distribution"}))

;; Clarity distribution by cut quality.

(-> diamonds
    (ns/view :clarity)
    (ns/lay (ns/bar {:color :cut}))
    (ns/plot {:title "Clarity by Cut Quality"}))

;; ## Tips

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                       {:key-fn keyword}))

;; Tipping behavior: smokers vs non-smokers.

(-> tips
    (ns/view [[:total_bill :tip]])
    (ns/lay (ns/point {:color :smoker})
            (ns/lm {:color :smoker}))
    (ns/plot {:title "Tipping: Smokers vs Non-Smokers"
              :x-label "Total Bill ($)" :y-label "Tip ($)"}))

;; Tip amounts by day, colored by meal time.

(-> tips
    (ns/view :day)
    (ns/lay (ns/bar {:color :time}))
    (ns/plot {:title "Visits by Day and Meal Time"}))

;; Stacked view of the same data.

(-> tips
    (ns/view :day)
    (ns/lay (ns/stacked-bar {:color :time}))
    (ns/plot {:title "Visits by Day (Stacked)"}))

;; Horizontal bar chart of party sizes.

(-> tips
    (ns/view :day)
    (ns/lay (ns/bar {:color :sex}))
    (ns/coord :flip)
    (ns/plot {:title "Day by Gender (Horizontal)"}))

;; ## MPG

(def mpg (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
                      {:key-fn keyword}))

;; Horsepower vs fuel efficiency, colored by origin.

(-> mpg
    (ns/view [[:horsepower :mpg]])
    (ns/lay (ns/point {:color :origin})
            (ns/lm {:color :origin}))
    (ns/plot {:title "Horsepower vs MPG by Origin"}))

;; Displacement vs MPG — another negative correlation.

(-> mpg
    (ns/view [[:displacement :mpg]])
    (ns/lay (ns/point {:color :origin}))
    (ns/plot {:title "Engine Displacement vs Fuel Efficiency"}))

;; Count of cars by origin.

(-> mpg
    (ns/view :origin)
    (ns/lay (ns/bar))
    (ns/plot {:title "Cars by Origin"}))
