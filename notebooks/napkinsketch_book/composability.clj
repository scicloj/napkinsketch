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
   [scicloj.napkinsketch.api :as ns]))

;; ## Datasets

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

;; ## Views Are Data

;; `view` returns a vector of maps. Let's inspect one.

(def views (ns/view iris [[:sepal_length :sepal_width]]))

(kind/pprint
 (mapv #(dissoc % :data) views))

;; ## Layers Merge Into Views

;; `lay` merges layer maps into each view. The result is still a vector of maps.

(def layered (ns/lay views (ns/point {:color :species})))

(kind/pprint
 (mapv #(dissoc % :data) layered))

;; ## Building Layers Programmatically

;; Since mark constructors return maps, you can compose them dynamically.

(def ^:private mark-for-type
  (fn [col-type]
    (case col-type
      :scatter (ns/point)
      :trend   (ns/lm)
      :dist    (ns/histogram))))

(-> iris
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (mark-for-type :scatter)
            (mark-for-type :trend))
    ns/plot)

;; ## Two-Arity View

;; `view` also accepts separate x and y arguments.

(-> iris
    (ns/view :petal_length :petal_width)
    (ns/lay (ns/point {:color :species}))
    ns/plot)

;; ## Inline Datasets

;; No need to create a named dataset — inline maps work.

(-> {:x (range 1 11)
     :y (mapv #(+ (* 2 %) (- (rand-int 5) 2)) (range 1 11))}
    (ns/view [[:x :y]])
    (ns/lay (ns/point) (ns/lm))
    (ns/plot {:title "Noisy Linear Trend"}))

;; ## Comparing Subsets Side by Side

;; Filter the same dataset to create comparative views.

(def ^:private species-plot
  (fn [species-name]
    (-> iris
        (tc/select-rows #(= species-name (% :species)))
        (ns/view [[:sepal_length :sepal_width]])
        (ns/lay (ns/point) (ns/lm))
        (ns/plot {:width 300 :height 250
                  :title species-name}))))

(species-plot "setosa")

(species-plot "versicolor")

(species-plot "virginica")

;; ## Combining All Measurements

;; Use `cross` to generate all pairs of measurements.

(def measurements [:sepal_length :sepal_width :petal_length :petal_width])

;; Pick a few interesting pairs.

(-> iris
    (ns/view [[:sepal_length :petal_length]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:title "Sepal Length vs Petal Length"}))

(-> iris
    (ns/view [[:sepal_width :petal_width]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:title "Sepal Width vs Petal Width"}))

;; ## Layered Bar Charts

;; Programmatically build a comparison chart.

(def ^:private quarterly-data
  (fn []
    (tc/dataset {:quarter [:Q1 :Q2 :Q3 :Q4 :Q1 :Q2 :Q3 :Q4]
                 :revenue [100 120 90 140 80 95 110 130]
                 :year    [:2024 :2024 :2024 :2024 :2025 :2025 :2025 :2025]})))

(-> (quarterly-data)
    (ns/view [[:quarter :revenue]])
    (ns/lay (ns/value-bar {:color :year}))
    (ns/plot {:title "Quarterly Revenue Comparison"}))

;; Same data, flipped.

(-> (quarterly-data)
    (ns/view [[:quarter :revenue]])
    (ns/lay (ns/value-bar {:color :year}))
    (ns/coord :flip)
    (ns/plot {:title "Revenue (Horizontal)"}))

;; ## Simulated Data

;; Generate data from a known model and verify the regression recovers it.

(def ^:private simulated
  (fn []
    (let [xs (range 0 10 0.5)
          ys (mapv #(+ (* 3 %) 5 (* 2 (- (rand) 0.5))) xs)]
      (tc/dataset {:x xs :y ys}))))

(-> (simulated)
    (ns/view [[:x :y]])
    (ns/lay (ns/point) (ns/lm))
    (ns/plot {:title "Simulated: y = 3x + 5 + noise"}))
