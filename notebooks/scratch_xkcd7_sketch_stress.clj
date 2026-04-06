;; # xkcd7-sketch Stress Tests
;;
;; Edge cases and interactions that test the verb semantics
;; defined in `scratch_xkcd7_sketch_spec.clj`. Each example targets
;; a specific interaction or boundary condition.

(ns scratch-xkcd7-sketch-stress
  (:require [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.napkinsketch.api :as sk]))

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

;; ## Multiple `view` calls merge shared

;; Two `view` calls with different shared opts — they should merge.

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
                   (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4}))]
  [(:shared xkcd7-sk) (count (:entries xkcd7-sk))])

(kind/test-last [(fn [[shared n]]
                   (and (= :species (:color shared))
                        (= 0.4 (:alpha shared))
                        (= 2 n)))])

;; Both entries inherit both color and alpha.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-view :petal_length :petal_width {:alpha 0.4})
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; ## Shared overwrite on same key

;; A second `view` with `:color` overwrites the first.

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
                   (sk/xkcd7-view :petal_length :petal_width {:color :petal_length}))]
  (:color (:shared xkcd7-sk)))

(kind/test-last [(fn [v] (= :petal_length v))])

;; ## `lay-*` opts do NOT affect shared

;; Verify that `lay-*` with opts leaves shared empty.

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))]
  [(:shared xkcd7-sk) (:color (first (:methods (first (:entries xkcd7-sk)))))])

(kind/test-last [(fn [[shared method-color]]
                   (and (empty? shared)
                        (= :species method-color)))])

;; A second bare `lay-*` adds a global method (no color).

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
                   sk/xkcd7-lay-lm)]
  [(:color (first (:methods (first (:entries xkcd7-sk)))))
   (:color (first (:methods xkcd7-sk)))])

(kind/test-last [(fn [[entry-color global-color]]
                   (and (= :species entry-color)
                        (nil? global-color)))])

;; ## Faceting + shared color + per-method nil

;; Shared color with faceting. Points colored, lm overall per panel.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-lay-lm {:color nil})
    (sk/xkcd7-facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; Each faceted panel has colored points + 1 overall lm line.

;; ## Multi-column histogram with shared color

;; Vector of columns with shared color produce colored histograms.

(sk/xkcd7-lay-histogram (sk/xkcd7-sketch iris {:color :species})
                        [:sepal_length :sepal_width :petal_length])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Entry with `:methods` + shared

;; An entry with its own `:methods` still inherits shared.
;; Each entry = one panel. The second entry also gets global methods.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-view {:x :sepal_length :y :sepal_width
                    :methods [{:mark :line :stat :lm :color nil}]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= 1 (:lines s)))))])

;; 2 panels (each entry is its own panel, stacked because same x/y).
;; Both get the global point method → 300 total points.
;; Only the second entry has lm → 1 line (with :color nil cancelling shared).

;; Without `:color nil`, the lm entry inherits shared color → 3 lines.

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-view {:x :sepal_length :y :sepal_width
                    :methods [{:mark :line :stat :lm}]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s))
                                (= 3 (:lines s)))))])

;; ## Column inference + shared

;; 0-arg `view` infers columns from data. Shared is unaffected.

(let [xkcd7-sk (-> {:x [1 2 3] :y [4 5 6]}
                   (sk/xkcd7-view))]
  [(count (:entries xkcd7-sk)) (:shared xkcd7-sk)])

(kind/test-last [(fn [[n shared]]
                   (and (= 1 n)
                        (empty? shared)))])

;; Column inference respects shared set earlier.

(-> (sk/xkcd7-sketch {:x [1 2 3 4 5] :y [2 4 3 5 4] :g [:a :a :b :b :a]}
                     {:color :g})
    (sk/xkcd7-view)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## SPLOM with shared color

(def cols [:sepal_length :sepal_width :petal_length])

;; With `lay-point` (explicit mark), all 9 panels are scatter.
;; Use `view` alone for diagonal histogram inference (see spec Rule 17).

(-> (sk/xkcd7-sketch iris {:color :species})
    (sk/xkcd7-view (sk/cross cols cols))
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 9 (:panels s))
                                (= (* 9 150) (:points s)))))])

;; ## Multiple `lay-*` with columns create independent entries

;; Two `lay-*` calls with columns → two entries, each with own methods.
;; No global methods. No cross product.

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-lay-point :sepal_length :sepal_width)
                   (sk/xkcd7-lay-histogram :petal_length))]
  [(count (:entries xkcd7-sk)) (count (:methods xkcd7-sk))
   (mapv #(count (:methods %)) (:entries xkcd7-sk))])

(kind/test-last [(fn [[entries global-methods entry-method-counts]]
                   (and (= 2 entries)
                        (= 0 global-methods)
                        (= [1 1] entry-method-counts)))])

;; ## Plain data coercion

;; Maps of columns, vectors of row maps — all coerced.

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## String column names

(-> {"x" [1 2 3 4 5] "y" [2 4 3 5 4]}
    (sk/xkcd7-lay-point "x" "y"))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; ## Recipe pattern: sketch without data

(def recipe (-> (sk/xkcd7-sketch)
                (sk/xkcd7-view :sepal_length :sepal_width)
                sk/xkcd7-lay-point
                sk/xkcd7-lay-lm))

(-> recipe (sk/xkcd7-with-data iris))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## Annotations don't inherit shared color

;; Annotations have their own `:methods`, so they don't get the
;; default methods. But they DO inherit shared (consistent rule).

(-> iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    (sk/xkcd7-annotate (sk/rule-h 3.0) (sk/band-v 5.5 6.5 {:alpha 0.3})))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

;; ## `nil` cancellation at each scope

;; Shared sets color, entry doesn't override, method cancels.

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
                   (sk/xkcd7-lay-point)
                   (sk/xkcd7-lay-lm {:color nil}))]
  [(:color (:shared xkcd7-sk))
   (:color (first (:entries xkcd7-sk)))
   (:color (second (:methods xkcd7-sk)))])

(kind/test-last [(fn [[shared-c entry-c method-c]]
                   (and (= :species shared-c)
                        (nil? entry-c)
                        (nil? method-c)))])

;; ## Options do not affect shared or methods

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-lay-point :sepal_length :sepal_width)
                   (sk/xkcd7-options {:title "My Plot" :width 800}))]
  [(:title (:opts xkcd7-sk)) (:width (:opts xkcd7-sk)) (:shared xkcd7-sk)])

(kind/test-last [(fn [[title width shared]]
                   (and (= "My Plot" title)
                        (= 800 width)
                        (empty? shared)))])

;; ## Scale and coord apply to entries

(let [xkcd7-sk (-> iris
                   (sk/xkcd7-lay-point :sepal_length :sepal_width)
                   (sk/xkcd7-scale :y :log)
                   (sk/xkcd7-coord :flip))]
  [(-> xkcd7-sk :entries first :y-scale)
   (-> xkcd7-sk :entries first :coord)])

(kind/test-last [(fn [[yscale coord]]
                   (and (= {:type :log} yscale)
                        (= :flip coord)))])
