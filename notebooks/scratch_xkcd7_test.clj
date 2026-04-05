;; # Testing the PROPOSED API (xkcd7- prefix)
;;
;; Every example uses the production `sk/xkcd7-*` functions.
;; No notebook-local verbs.

(ns scratch-xkcd7-test
  (:require [tablecloth.api :as tc]
            [tech.v3.datatype.functional :as dfn]
            [java-time.api :as jt]
            [scicloj.napkinsketch.api :as sk]
            [napkinsketch-book.datasets :as data]))

(def iris data/iris)
(def tips data/tips)
(def penguins data/penguins)

;; ---
;; ## Scatter + regression

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-lay-lm)
    sk/xkcd7-plot)

;; ---
;; ## Inference: no lay call

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    sk/xkcd7-plot)

;; ---
;; ## Inference: one numerical column → histogram

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :sepal_length)
    sk/xkcd7-plot)

;; ---
;; ## Inference: one categorical column → bar chart

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :species)
    sk/xkcd7-plot)

;; ---
;; ## Column inference: 2-column dataset

(-> (sk/xkcd7-sketch {:x [1 2 3 4 5] :y [2 4 3 5 4]})
    (sk/xkcd7-view)
    sk/xkcd7-plot)

;; ---
;; ## Data-first (no sketch call)

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-lay-lm)
    sk/xkcd7-plot)

;; ---
;; ## Triple overlay

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :species :sepal_width)
    (sk/xkcd7-lay-violin {:alpha 0.3})
    (sk/xkcd7-lay-point {:jitter true :alpha 0.4})
    (sk/xkcd7-lay-boxplot)
    sk/xkcd7-plot)

;; ---
;; ## Simpson's paradox

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.4})
    (sk/xkcd7-lay-lm)
    (sk/xkcd7-overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    sk/xkcd7-plot)

;; ---
;; ## Small multiples

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :sepal_length :petal_length)
    (sk/xkcd7-view :sepal_length :petal_width)
    (sk/xkcd7-lay-point)
    sk/xkcd7-plot)

;; ---
;; ## SPLOM with inference

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view (sk/cross [:sepal_length :sepal_width :petal_length]
                             [:sepal_length :sepal_width :petal_length]))
    sk/xkcd7-plot)

;; ---
;; ## Facet by column

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-lay-lm)
    (sk/xkcd7-facet :species)
    sk/xkcd7-plot)

;; ---
;; ## 2D facet grid

(-> (sk/xkcd7-sketch tips {:color :smoker})
    (sk/xkcd7-view :total_bill :tip)
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-facet-grid :day :sex)
    sk/xkcd7-plot)

;; ---
;; ## Broadcast

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-facet :species)
    (sk/xkcd7-overlay :sepal_length :sepal_width [{:mark :line :stat :loess}])
    sk/xkcd7-plot)

;; ---
;; ## Per-panel annotation

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-lay-lm)
    (sk/xkcd7-facet :species)
    (sk/xkcd7-overlay {:x :sepal_length :y :sepal_width :facet-col "setosa"}
                      [{:mark :rule-h :intercept 3.0}])
    sk/xkcd7-plot)

;; ---
;; ## Stacked bars

(-> (sk/xkcd7-sketch tips {:color :smoker})
    (sk/xkcd7-view :day)
    (sk/xkcd7-lay {:mark :rect :stat :count :position :stack})
    sk/xkcd7-plot)

;; ---
;; ## Mixed grid

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-view :sepal_length :petal_width)
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-facet :species)
    sk/xkcd7-plot)

;; ---
;; ## LM with SE band

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.3})
    (sk/xkcd7-lay-lm {:se true})
    sk/xkcd7-plot)

;; ---
;; ## Recipe

(def my-recipe
  (-> (sk/xkcd7-sketch)
      (sk/xkcd7-view :sepal_length :sepal_width)
      (sk/xkcd7-lay-point {:alpha 0.5})
      (sk/xkcd7-lay-lm)))

(-> my-recipe
    (sk/xkcd7-with-data iris)
    sk/xkcd7-plot)

;; ---
;; ## Faceted Simpson's paradox

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.4})
    (sk/xkcd7-lay-lm)
    (sk/xkcd7-facet :species)
    (sk/xkcd7-overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    sk/xkcd7-plot)

;; ---
;; ## Histogram

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view :sepal_length)
    (sk/xkcd7-lay-histogram)
    sk/xkcd7-plot)

;; ---
;; ## Density

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length)
    (sk/xkcd7-lay-density)
    sk/xkcd7-plot)

;; ---
;; ## Error bars

(-> (sk/xkcd7-sketch {:condition ["A" "B" "C" "D"]
                      :mean [10.0 15.0 12.0 18.0]
                      :ci_lo [8.0 12.0 9.5 15.5]
                      :ci_hi [12.0 18.0 14.5 20.5]})
    (sk/xkcd7-view :condition :mean)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-lay-errorbar {:ymin :ci_lo :ymax :ci_hi})
    sk/xkcd7-plot)

;; ---
;; ## Temporal x-axis

(-> (sk/xkcd7-sketch
     (tc/dataset {:date (mapv #(jt/local-date 2024 1 %) (range 1 29))
                  :value (mapv (fn [i] (+ 10.0 (* 5.0 (Math/sin (/ i 3.0))))) (range 28))}))
    (sk/xkcd7-view :date :value)
    (sk/xkcd7-lay-line)
    (sk/xkcd7-lay-point)
    sk/xkcd7-plot)

;; ---
;; ## Coord flip

(-> (sk/xkcd7-sketch iris)
    (sk/xkcd7-view {:x :species :y :sepal_width :coord :flip})
    (sk/xkcd7-lay-boxplot)
    sk/xkcd7-plot)

;; ---
;; ## Options: title + labels

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point)
    (sk/xkcd7-options {:title "Iris Scatter"
                       :x-label "Sepal Length (cm)"
                       :y-label "Sepal Width (cm)"
                       :width 500 :height 350})
    sk/xkcd7-plot)

;; ---
;; ## Auto-rendering (no explicit xkcd7-plot)
;;
;; In Clay, this should render as a plot automatically:

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view :sepal_length :sepal_width)
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-lay-lm))

;; ---
;; ## Lay-first (methods before view)

(-> iris
    (sk/xkcd7-lay-point {:alpha 0.5})
    (sk/xkcd7-lay-lm)
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-plot)
