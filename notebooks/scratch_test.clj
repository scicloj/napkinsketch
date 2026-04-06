;; # Testing the PROPOSED API ( prefix)
;;
;; Every example uses the production `sk/*` functions.
;; No notebook-local verbs.

(ns scratch-test
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

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:alpha 0.5})
    (sk/lay-lm)
    sk/plot)

;; ---
;; ## Inference: no lay call

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    sk/plot)

;; ---
;; ## Inference: one numerical column → histogram

(-> (sk/sketch iris)
    (sk/view :sepal_length)
    sk/plot)

;; ---
;; ## Inference: one categorical column → bar chart

(-> (sk/sketch iris)
    (sk/view :species)
    sk/plot)

;; ---
;; ## Column inference: 2-column dataset

(-> (sk/sketch {:x [1 2 3 4 5] :y [2 4 3 5 4]})
    (sk/view)
    sk/plot)

;; ---
;; ## Data-first (no sketch call)

(-> iris
    (sk/view :sepal_length :sepal_width {:color :species})
    (sk/lay-point {:alpha 0.5})
    (sk/lay-lm)
    sk/plot)

;; ---
;; ## Triple overlay

(-> (sk/sketch iris)
    (sk/view :species :sepal_width)
    (sk/lay-violin {:alpha 0.3})
    (sk/lay-point {:jitter true :alpha 0.4})
    (sk/lay-boxplot)
    sk/plot)

;; ---
;; ## Simpson's paradox

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:alpha 0.4})
    (sk/lay-lm)
    (sk/overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    sk/plot)

;; ---
;; ## Small multiples

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/view :sepal_length :petal_length)
    (sk/view :sepal_length :petal_width)
    (sk/lay-point)
    sk/plot)

;; ---
;; ## SPLOM with inference

(-> (sk/sketch iris {:color :species})
    (sk/view (sk/cross [:sepal_length :sepal_width :petal_length]
                             [:sepal_length :sepal_width :petal_length]))
    sk/plot)

;; ---
;; ## Facet by column

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point)
    (sk/lay-lm)
    (sk/facet :species)
    sk/plot)

;; ---
;; ## 2D facet grid

(-> (sk/sketch tips {:color :smoker})
    (sk/view :total_bill :tip)
    (sk/lay-point {:alpha 0.5})
    (sk/facet-grid :day :sex)
    sk/plot)

;; ---
;; ## Broadcast

(-> (sk/sketch iris)
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point)
    (sk/facet :species)
    (sk/overlay :sepal_length :sepal_width [{:mark :line :stat :loess}])
    sk/plot)

;; ---
;; ## Per-panel annotation

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point)
    (sk/lay-lm)
    (sk/facet :species)
    (sk/overlay {:x :sepal_length :y :sepal_width :facet-col "setosa"}
                      [{:mark :rule-h :intercept 3.0}])
    sk/plot)

;; ---
;; ## Stacked bars

(-> (sk/sketch tips {:color :smoker})
    (sk/view :day)
    (sk/lay {:mark :rect :stat :count :position :stack})
    sk/plot)

;; ---
;; ## Mixed grid

(-> (sk/sketch iris)
    (sk/view :sepal_length :sepal_width)
    (sk/view :sepal_length :petal_width)
    (sk/lay-point {:alpha 0.5})
    (sk/facet :species)
    sk/plot)

;; ---
;; ## LM with SE band

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:alpha 0.3})
    (sk/lay-lm {:se true})
    sk/plot)

;; ---
;; ## Recipe

(def my-recipe
  (-> (sk/sketch)
      (sk/view :sepal_length :sepal_width)
      (sk/lay-point {:alpha 0.5})
      (sk/lay-lm)))

(-> my-recipe
    (sk/with-data iris)
    sk/plot)

;; ---
;; ## Faceted Simpson's paradox

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:alpha 0.4})
    (sk/lay-lm)
    (sk/facet :species)
    (sk/overlay :sepal_length :sepal_width [{:mark :line :stat :lm :color nil}])
    sk/plot)

;; ---
;; ## Histogram

(-> (sk/sketch iris)
    (sk/view :sepal_length)
    (sk/lay-histogram)
    sk/plot)

;; ---
;; ## Density

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length)
    (sk/lay-density)
    sk/plot)

;; ---
;; ## Error bars

(-> (sk/sketch {:condition ["A" "B" "C" "D"]
                      :mean [10.0 15.0 12.0 18.0]
                      :ci_lo [8.0 12.0 9.5 15.5]
                      :ci_hi [12.0 18.0 14.5 20.5]})
    (sk/view :condition :mean)
    (sk/lay-point)
    (sk/lay-errorbar {:ymin :ci_lo :ymax :ci_hi})
    sk/plot)

;; ---
;; ## Temporal x-axis

(-> (sk/sketch
     (tc/dataset {:date (mapv #(jt/local-date 2024 1 %) (range 1 29))
                  :value (mapv (fn [i] (+ 10.0 (* 5.0 (Math/sin (/ i 3.0))))) (range 28))}))
    (sk/view :date :value)
    (sk/lay-line)
    (sk/lay-point)
    sk/plot)

;; ---
;; ## Coord flip

(-> (sk/sketch iris)
    (sk/view {:x :species :y :sepal_width :coord :flip})
    (sk/lay-boxplot)
    sk/plot)

;; ---
;; ## Options: title + labels

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point)
    (sk/options {:title "Iris Scatter"
                       :x-label "Sepal Length (cm)"
                       :y-label "Sepal Width (cm)"
                       :width 500 :height 350})
    sk/plot)

;; ---
;; ## Auto-rendering (no explicit plot)
;;
;; In Clay, this should render as a plot automatically:

(-> (sk/sketch iris {:color :species})
    (sk/view :sepal_length :sepal_width)
    (sk/lay-point {:alpha 0.5})
    (sk/lay-lm))

;; ---
;; ## Lay-first (methods before view)

(-> iris
    (sk/lay-point {:alpha 0.5})
    (sk/lay-lm)
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/plot)
