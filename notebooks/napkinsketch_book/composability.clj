;; # Composability
;;
;; The API is built on plain Clojure data — views are maps, layers are
;; merge operations, marks are constructor functions returning maps.
;; This means the full power of Clojure is available for building plots
;; programmatically.

(ns napkinsketch-book.composability
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation (for synthetic data)
   [fastmath.random :as rng]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Views Are Data

;; `view` returns a vector of maps. Let's inspect one.

(def views (sk/view iris [[:sepal_length :sepal_width]]))

(kind/pprint views)
;; ## Layers Merge Into Views

;; `lay` merges layer maps into each view. The result is still a vector of maps.

(def layered (sk/lay views (sk/point {:color :species})))

(kind/pprint layered)

;; ## Two-Arity View

;; `view` also accepts separate x and y arguments.

(-> iris
    (sk/view :petal_length :petal_width)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; ## Inline Datasets

;; No need to create a named dataset — inline maps work.

(-> (let [r (rng/rng :jdk 42)]
      {:x (range 1 11)
       :y (mapv #(+ (* 2 %) (- (rng/irandom r 5) 2)) (range 1 11))})
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Noisy Linear Trend"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 10 (:points s))
                                (= 1 (:lines s))
                                (some #{"Noisy Linear Trend"} (:texts s)))))])

;; ## Comparing Subsets Side by Side

;; Filter the same dataset to create comparative views.

(def species-plot
  (fn [species-name]
    (-> iris
        (tc/select-rows #(= species-name (% :species)))
        (sk/view [[:sepal_length :sepal_width]])
        (sk/lay (sk/point) (sk/lm))
        (sk/plot {:width 300 :height 250
                  :title species-name}))))

(sk/arrange (mapv species-plot ["setosa" "versicolor" "virginica"])
            {:cols 3})

(kind/test-last [(fn [v] (= :div (first v)))])

;; With faceting, one call replaces the manual filter-per-species pattern:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Combining All Measurements

;; `pairs` generates all unique pairs from a list of columns.
;; Pass the result to `view` for a multi-panel scatter plot matrix.

(def measurements [:sepal_length :sepal_width :petal_length :petal_width])

(sk/pairs measurements)

(kind/test-last [(fn [v] (= 6 (count v)))])

(-> iris
    (sk/view (sk/pairs measurements))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 6 (:panels s))))])

;; ## Layered Bar Charts

;; Programmatically build a comparison chart.

(def quarterly-data
  (fn []
    (tc/dataset {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4]
                 :revenue [100 120 90 140 80 95 110 130]
                 :year [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]})))

(-> (quarterly-data)
    (sk/view [[:quarter :revenue]])
    (sk/lay (sk/value-bar {:color :year}))
    (sk/plot {:title "Quarterly Revenue Comparison"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 8 (:polygons s))
                                (some #{"Quarterly Revenue Comparison"} (:texts s)))))])

;; Same data, flipped.

(-> (quarterly-data)
    (sk/view [[:quarter :revenue]])
    (sk/lay (sk/value-bar {:color :year}))
    (sk/coord :flip)
    (sk/plot {:title "Revenue (Horizontal)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 8 (:polygons s))
                                (some #{"Revenue (Horizontal)"} (:texts s)))))])

;; ## Simulated Data

;; Generate data from a known model and verify the regression recovers it.

(def simulated
  (let [r (rng/rng :jdk 77)
        xs (range 0 10 0.5)
        ys (mapv #(+ (* 3 %) 5 (* 2 (- (rng/drandom r) 0.5))) xs)]
    (tc/dataset {:x xs :y ys})))

(-> simulated
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Simulated: y = 3x + 5 + noise"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 20 (:points s))
                                (= 1 (:lines s))
                                (some #{"Simulated: y = 3x + 5 + noise"} (:texts s)))))])

;; ## Composing Diverse Features
;;
;; Napkinsketch's composability means every feature works with every
;; other. Here are a few combinations that exercise many features at once.

;; Faceted density — one density curve per species in each panel:

(-> iris
    (sk/view :sepal_length)
    (sk/facet :species)
    (sk/lay (sk/density {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 3 (:polygons s)))))])

;; Jittered scatter with regression per group:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species :jitter 3})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Continuous color with log scale:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :petal_length}))
    (sk/scale :x :log)
    (sk/labs {:title "Log-Scale with Gradient Color"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Log-Scale with Gradient Color"} (:texts s)))))])

;; Violin + boxplot overlay — shows both density shape and five-number summary:

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin {:alpha 0.3})
            (sk/boxplot))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 8 (:polygons s))
                                (pos? (:lines s)))))])
