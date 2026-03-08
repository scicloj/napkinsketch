;; # Edge Cases
;;
;; Testing robustness: missing data, extreme values, small datasets,
;; many categories, computed columns, and other tricky scenarios.

(ns napkinsketch-book.edge-cases
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as ns]))

;; ## Missing Data

;; Rows with `nil` values are dropped gracefully.

(def with-missing
  (tc/dataset {:x [1 2 nil 4 5 nil 7]
               :y [3 nil 5 6 nil 8 9]}))

(-> with-missing
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Single Point

;; A lone data point should render without errors.

(-> {:x [3] :y [7]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Two Points with Regression

;; Regression requires at least 3 points. With only 2,
;; the line is gracefully omitted.

(-> {:x [1 10] :y [5 50]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point) (ns/lm))
    ns/plot)

;; ## Three Points with Regression

;; With 3 points, the regression line appears.

(-> {:x [1 5 10] :y [5 25 50]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point) (ns/lm))
    ns/plot)

;; ## Constant X

;; All x values are the same — the plot should still render.

(-> {:x [5 5 5 5 5] :y [1 2 3 4 5]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Constant Y

;; All y values are the same.

(-> {:x [1 2 3 4 5] :y [3 3 3 3 3]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Negative Values

;; Data spanning positive and negative ranges.

(-> {:x [-5 -3 0 3 5] :y [-2 4 0 -4 2]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Very Large Values

(-> {:x [1e6 2e6 3e6] :y [1e9 2e9 3e9]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Very Small Values

(-> {:x [0.001 0.002 0.003] :y [0.0001 0.0002 0.0003]}
    (ns/view [[:x :y]])
    (ns/lay (ns/point))
    ns/plot)

;; ## Large Dataset

;; 1000 random points, colored by group.

(def ^:private large-data
  (fn []
    (tc/dataset {:x (repeatedly 1000 #(rand))
                 :y (repeatedly 1000 #(rand))
                 :group (repeatedly 1000 #(rand-nth [:a :b :c]))})))

(-> (large-data)
    (ns/view [[:x :y]])
    (ns/lay (ns/point {:color :group}))
    ns/plot)

;; ## Many Categories

;; A bar chart with 12 categories.

(-> (tc/dataset {:category (mapv #(keyword (str "cat-" %)) (range 12))
                 :value (repeatedly 12 #(+ 10 (rand-int 90)))})
    (ns/view [[:category :value]])
    (ns/lay (ns/value-bar))
    ns/plot)

;; ## Computed Columns

;; Derive a new column and plot it.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                       {:key-fn keyword}))

(-> iris
    (tc/map-columns :sepal_ratio [:sepal_length :sepal_width] /)
    (ns/view [[:sepal_length :sepal_ratio]])
    (ns/lay (ns/point {:color :species}))
    (ns/plot {:title "Sepal Length/Width Ratio"}))

;; ## Filtered Subset

;; Plot only one species.

(-> iris
    (tc/select-rows #(= "setosa" (% :species)))
    (ns/view [[:sepal_length :sepal_width]])
    (ns/lay (ns/point) (ns/lm))
    (ns/plot {:title "Setosa Only"}))
