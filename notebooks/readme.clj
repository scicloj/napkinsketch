;; # Plotje
;; Simple and easy plotting
;;
;; Plotje is a Clojure library for composable plotting, inspired by
;; the Grammar of Graphics.
;;
;; ## General info
;;
;; |||
;; |-|-|
;; |Website | [https://scicloj.github.io/plotje/](https://scicloj.github.io/plotje/)
;; |Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/plotje)|
;; |Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/plotje.svg)](https://clojars.org/org.scicloj/plotje)|
;; |License |[MIT](https://github.com/scicloj/plotje/blob/main/LICENSE)|
;; |Status |🛠alpha🛠|
;;
;; ## Usage
;;
;; Add to your `deps.edn`:
;;
;; ```clojure
;; org.scicloj/plotje {:mvn/version "0.1.0"}
;; ```
;;
;; Plotje is intended to be used with data-visualization tools
;; that support the [Kindly](https://scicloj.github.io/kindly) convention
;; such as [Clay](https://scicloj.github.io/clay/).
;;
;; ## Quick example

(ns readme
  (:require [scicloj.plotje.api :as sk]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; Line chart with point markers from plain Clojure data:

(-> [{:month "Jan" :sales 120}
     {:month "Feb" :sales 95}
     {:month "Mar" :sales 140}
     {:month "Apr" :sales 175}
     {:month "May" :sales 160}
     {:month "Jun" :sales 210}]
    (sk/lay-line :month :sales)
    sk/lay-point
    (sk/options {:title "Monthly Sales"}))

;; Scatter plot matrix (SPLOM) — all pairwise combinations with color grouping:

(-> (rdatasets/datasets-iris)
    (sk/frame {:color :species})
    (sk/frame (sk/cross [:sepal-length :sepal-width
                         :petal-length :petal-width]
                        [:sepal-length :sepal-width
                         :petal-length :petal-width]))
    (sk/options {:title "Iris SPLOM"}))

;; ## Documentation
;;
;; [Full documentation](https://scicloj.github.io/plotje/)
;;
;; ## License
;;
;; Copyright © 2025-2026 Scicloj
;;
;; Distributed under the MIT License.
