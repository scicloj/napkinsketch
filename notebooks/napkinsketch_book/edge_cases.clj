;; # Edge Cases
;;
;; Testing robustness: missing data, extreme values, small datasets,
;; many categories, computed columns, and other tricky scenarios.

(ns napkinsketch-book.edge-cases
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Missing Data

;; Rows with `nil` values are dropped gracefully.

(def with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7]
               :y [3 nil 5 6 nil 8 9]}))

(-> with-missing
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])

;; ## Single Point

;; A lone data point should render without errors.

(-> {:x [3] :y [7]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Two Points with Regression

;; Regression requires at least 3 points. With only 2,
;; the line is gracefully omitted.

(-> {:x [1 10] :y [5 50]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Three Points with Regression

;; With 3 points, the regression line appears.

(-> {:x [1 5 10] :y [5 25 50]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Constant X

;; All x values are the same — the plot should still render.

(-> {:x [5 5 5 5 5] :y [1 2 3 4 5]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Constant Y

;; All y values are the same.

(-> {:x [1 2 3 4 5] :y [3 3 3 3 3]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Negative Values

;; Data spanning positive and negative ranges.

(-> {:x [-5 -3 0 3 5] :y [-2 4 0 -4 2]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Very Large Values

(-> {:x [1e6 2e6 3e6] :y [1e9 2e9 3e9]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Very Small Values

(-> {:x [0.001 0.002 0.003] :y [0.0001 0.0002 0.0003]}
    (sk/view [[:x :y]])
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Large Dataset

;; 1000 random points, colored by group.

(def ^:private large-data
  (fn []
    (tc/dataset {:x (repeatedly 1000 #(rand))
                 :y (repeatedly 1000 #(rand))
                 :group (repeatedly 1000 #(rand-nth [:a :b :c]))})))

(-> (large-data)
    (sk/view [[:x :y]])
    (sk/lay (sk/point {:color :group}))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])

;; ## Many Categories

;; A bar chart with 12 categories.

(-> (tc/dataset {:category (mapv #(keyword (str "cat-" %)) (range 12))
                 :value (repeatedly 12 #(+ 10 (rand-int 90)))})
    (sk/view [[:category :value]])
    (sk/lay (sk/value-bar))
    sk/plot)

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Computed Columns

;; Derive a new column and plot it.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(-> iris
    (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
    (sk/view [[:sepal_length :sepal_ratio]])
    (sk/lay (sk/point {:color :species}))
    (sk/plot {:title "Sepal Length/Width Ratio"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (map? (second v))
              (vector? (nth v 2))))])

;; ## Filtered Subset

;; Plot only one species.

(-> iris
    (tc/select-rows #(= "setosa" (% :species)))
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    (sk/plot {:title "Setosa Only"}))

(kind/test-last
 [(fn [v] (and (vector? v) (= :svg (first v))
              (let [attrs (second v)]
                (and (map? attrs) (number? (:width attrs)) (number? (:height attrs))))
              (let [body (nth v 2)]
                (and (vector? body) (= :g (first body))))))])
