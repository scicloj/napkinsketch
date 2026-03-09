;; # Composability
;;
;; The API is built on plain Clojure data — views are maps, layers are
;; merge operations, marks are constructor functions returning maps.
;; This means the full power of Clojure is available for building plots
;; programmatically.

(ns napkinsketch-book.composability
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

;; ## Views Are Data

;; `view` returns a vector of maps. Let's inspect one.

(def views (sk/view iris [[:sepal_length :sepal_width]]))

(kind/pprint
 (mapv #(dissoc % :data) views))

;; ## Layers Merge Into Views

;; `lay` merges layer maps into each view. The result is still a vector of maps.

(def layered (sk/lay views (sk/point {:color :species})))

(kind/pprint
 (mapv #(dissoc % :data) layered))

;; ## Building Layers Programmatically

;; Since mark constructors return maps, you can compose them dynamically.

(def ^:private mark-for-type
  (fn [col-type]
    (case col-type
      :scatter (sk/point)
      :trend   (sk/lm)
      :dist    (sk/histogram))))

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (mark-for-type :scatter)
            (mark-for-type :trend))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Two-Arity View

;; `view` also accepts separate x and y arguments.

(-> iris
    (sk/view :petal_length :petal_width)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Inline Datasets

;; No need to create a named dataset — inline maps work.

(-> {:x (range 1 11)
     :y (mapv #(+ (* 2 %) (- (rand-int 5) 2)) (range 1 11))}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Noisy Linear Trend"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Comparing Subsets Side by Side

;; Filter the same dataset to create comparative views.

(def ^:private species-plot
  (fn [species-name]
    (-> iris
        (tc/select-rows #(= species-name (% :species)))
        (sk/view [[:sepal_length :sepal_width]])
        (sk/lay (sk/point) (sk/lm))
        (sk/plot {:width 300 :height 250
                  :title species-name}))))

(species-plot "setosa")

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs)
                     (= 300 (:width attrs))))))])

(species-plot "versicolor")

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

(species-plot "virginica")

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Combining All Measurements

;; Use `cross` to generate all pairs of measurements.

(def measurements [:sepal_length :sepal_width :petal_length :petal_width])

;; Pick a few interesting pairs.

(-> iris
    (sk/view [[:sepal_length :petal_length]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Sepal Length vs Petal Length"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

(-> iris
    (sk/view [[:sepal_width :petal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Sepal Width vs Petal Width"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Layered Bar Charts

;; Programmatically build a comparison chart.

(def ^:private quarterly-data
  (fn []
    (tc/dataset {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4]
                 :revenue [100 120 90 140 80 95 110 130]
                 :year    [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]})))

(-> (quarterly-data)
    (sk/view [[:quarter :revenue]])
    (sk/lay (sk/value-bar {:color :year}))
    (sk/plot {:title "Quarterly Revenue Comparison"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; Same data, flipped.

(-> (quarterly-data)
    (sk/view [[:quarter :revenue]])
    (sk/lay (sk/value-bar {:color :year}))
    (sk/coord :flip)
    (sk/plot {:title "Revenue (Horizontal)"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Simulated Data

;; Generate data from a known model and verify the regression recovers it.

(def ^:private simulated
  (fn []
    (let [xs (range 0 10 0.5)
          ys (mapv #(+ (* 3 %) 5 (* 2 (- (rand) 0.5))) xs)]
      (tc/dataset {:x xs :y ys}))))

(-> (simulated)
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Simulated: y = 3x + 5 + noise"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])
