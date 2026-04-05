;; # Quickstart (PROPOSED API)
;;
;; The quickstart chapter migrated to the xkcd7- API.

(ns scratch-quickstart-xkcd7
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]))

;; ## Your First Plot

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(-> iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Plain Data

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Column inference:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/xkcd7-view)
    sk/xkcd7-plot)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## Color

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"setosa"} (:texts s)))))])

;; ## More Chart Types

;; Histogram:
(-> iris
    (sk/xkcd7-view :sepal_length)
    (sk/xkcd7-lay-histogram))

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Bar chart:
(-> iris
    (sk/xkcd7-view :species)
    (sk/xkcd7-lay-bar))

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; Line chart:
(-> {:x [1 2 3 4 5 6 7 8] :y [3 5 4 7 6 8 7 9]}
    (sk/xkcd7-lay-line :x :y))

(kind/test-last [(fn [v] (= 1 (:lines (sk/svg-summary v))))])

;; Boxplot:
(-> iris
    (sk/xkcd7-view :species :sepal_width)
    (sk/xkcd7-lay-boxplot))

(kind/test-last [(fn [v] (pos? (:lines (sk/svg-summary v))))])

;; ## Inference

;; Two numerical → scatter:
(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-plot)

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; One categorical → bar:
(-> iris
    (sk/xkcd7-view :species)
    sk/xkcd7-plot)

(kind/test-last [(fn [v] (= 3 (:polygons (sk/svg-summary v))))])

;; One numerical → histogram:
(-> iris
    (sk/xkcd7-view :sepal_length)
    sk/xkcd7-plot)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; ## Multiple Layers

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-lay-lm))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Titles and Labels

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :petal_length :petal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-options {:width 500 :height 350
                       :title "Iris Petals"
                       :x-label "Petal Length (cm)"
                       :y-label "Petal Width (cm)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (some #{"Iris Petals"} (:texts s)))))])
