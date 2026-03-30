;; # Datasets
;;
;; This namespace loads the standard datasets used throughout the book.
;; Other notebooks require this namespace to avoid redundant loading.
;;
;; All datasets come from the
;; [seaborn-data](https://github.com/mwaskom/seaborn-data) collection.

(ns napkinsketch-book.datasets
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]))

;; ## Iris
;;
;; 150 iris flower measurements (sepal and petal length/width)
;; across three species: setosa, versicolor, and virginica.

(def iris
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
              {:key-fn keyword}))

iris

(kind/test-last [(fn [ds] (= 150 (tc/row-count ds)))])

;; ## Tips
;;
;; 244 restaurant bills with tip amount, party size, day, time,
;; and smoker status.

(def tips
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
              {:key-fn keyword}))

tips

(kind/test-last [(fn [ds] (= 244 (tc/row-count ds)))])

;; ## Penguins
;;
;; 344 penguin measurements (bill, flipper, body mass) across
;; three species on three islands in the [Palmer Archipelago](https://en.wikipedia.org/wiki/Palmer_Archipelago).

(def penguins
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
              {:key-fn keyword}))

penguins

(kind/test-last [(fn [ds] (= 344 (tc/row-count ds)))])

;; ## MPG
;;
;; 398 automobile records from the StatLib library: fuel efficiency,
;; engine displacement, horsepower, weight, and model year.

(def mpg
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/mpg.csv"
              {:key-fn keyword}))

mpg

(kind/test-last [(fn [ds] (= 398 (tc/row-count ds)))])
