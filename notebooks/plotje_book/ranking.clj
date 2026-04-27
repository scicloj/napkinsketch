;; # Ranking
;;
;; Bar charts and their variants -- comparing quantities across categories.

(ns plotje-book.ranking
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

;; ## Bar Chart

;; Count occurrences of a categorical column.

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Colored Bar Chart

;; Grouped (dodged) bars -- count by day, colored by smoking status.

(-> (rdatasets/reshape2-tips)
    (pj/lay-bar :day {:color :smoker}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar Chart

;; Same data, stacked instead of dodged.

(-> (rdatasets/reshape2-tips)
    (pj/lay-bar :day {:position :stack :color :smoker}))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Stacked Bar (Proportions)

;; 100% stacked bars -- shows proportions instead of counts.

(-> (rdatasets/palmerpenguins-penguins)
    (pj/lay-bar :island {:position :fill :color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Horizontal Bar Chart

;; Flip the bar chart for horizontal orientation.

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species)
    (pj/coord :flip))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; `(pj/coord :flip)` draws categories **bottom-to-top in data
;; order**, matching ggplot2's `coord_flip()`. For a ranking chart
;; where the biggest value should appear at the top, sort the data
;; ascending before plotting, e.g.
;; `(tc/order-by data [:value] [:asc])`.

;; ## Horizontal Colored Bars

;; Colored bars, flipped.

(-> (rdatasets/reshape2-tips)
    (pj/lay-bar :day {:color :time})
    (pj/coord :flip))

(kind/test-last
 [(fn [v] (let [s (pj/svg-summary v)]
            (and (= 1 (:panels s))
                 (pos? (:polygons s)))))])

;; ## Value Bar

;; Pre-computed y values (no counting).

(-> sales
    (pj/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## Value Bar (Horizontal)

;; Flip for horizontal orientation.

(-> sales
    (pj/lay-value-bar :product :revenue)
    (pj/coord :flip))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

;; ## [Lollipop](https://en.wikipedia.org/wiki/Lollipop_chart)

;; Stem + dot -- a lighter alternative to bar charts.

(-> sales
    (pj/lay-lollipop :product :revenue))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## Lollipop (Horizontal)

;; Flipped for horizontal orientation.

(-> sales
    (pj/lay-lollipop :product :revenue)
    (pj/coord :flip))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

;; ## What's Next
;;
;; - [**Change Over Time**](./plotje_book.change_over_time.html) -- line charts, step functions, and stacked areas
;; - [**Configuration**](./plotje_book.configuration.html) -- control dimensions, palettes, and themes
