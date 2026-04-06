;; # Gallery
;;
;; Reproducing examples from the
;; [R Graph Gallery](https://r-graph-gallery.com/) using napkinsketch.
;; Each section corresponds to a chart type. Examples use datasets from
;; the [RDatasets](https://vincentarelbundock.github.io/Rdatasets/)
;; collection via `scicloj.metamorph.ml.rdatasets`.

(ns gallery
  (:require
   [scicloj.napkinsketch.api :as sk]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   [tablecloth.api :as tc]))

;; ## Datasets

(def mpg (rdatasets/ggplot2-mpg))

;; ## Scatter

;; Colored scatter with LOESS smoothing — one curve per vehicle class:

(-> mpg
    (sk/view :displ :hwy {:color :class})
    sk/lay-point
    sk/lay-loess
    (sk/options {:title "Fuel Efficiency by Engine Size"
                 :x-label "Engine Displacement (L)"
                 :y-label "Highway MPG"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:points s))
                                (pos? (:lines s)))))])
