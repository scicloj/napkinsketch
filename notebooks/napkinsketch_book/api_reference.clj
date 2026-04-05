;; # API Reference
;;
;; Complete reference for every public function in
;; `scicloj.napkinsketch.api`.
;;
;; Each entry shows the docstring, a live example, and a test.
;; For galleries of mark variations, see the Reference notebooks
;; (Scatter, Distributions, Ranking, Change Over Time, Relationships).

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns napkinsketch-book.api-reference
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation
   [fastmath.random :as rng]))

;; ## Sample Data

(def tiny {:x [1 2 3 4 5]
           :y [2 4 1 5 3]
           :group [:a :a :b :b :b]})

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

(def measurements {:treatment ["A" "B" "C" "D"]
                   :mean [10.0 15.0 12.0 18.0]
                   :ci_lo [8.0 12.0 9.5 15.5]
                   :ci_hi [12.0 18.0 14.5 20.5]})

;; ## Data Setup

(kind/doc #'sk/xkcd7-view)

;; Single scatter view — two columns as `[x y]`:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> data/iris
    (sk/xkcd7-lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> data/iris
    (sk/xkcd7-view [[:sepal_length :sepal_width]
                    [:petal_length :petal_width]])
    (sk/xkcd7-lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form — explicit keys:

(-> data/iris
    (sk/xkcd7-view {:x :sepal_length :y :sepal_width})
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/xkcd7-annotate)

;; Add layers with `sk/xkcd7-lay-point`, `sk/xkcd7-lay-lm`, etc.:

(-> data/iris
    (sk/xkcd7-view :sepal_length :sepal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Layer Functions

(kind/doc #'sk/xkcd7-lay-point)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/xkcd7-lay-line)

(def wave {:x (range 30)
           :y (map #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/xkcd7-lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:lines s))))])

(kind/doc #'sk/xkcd7-lay-histogram)

(-> data/iris
    (sk/xkcd7-lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-bar)

(-> data/iris
    (sk/xkcd7-lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-stacked-bar)

(-> data/penguins
    (sk/xkcd7-lay-stacked-bar :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-stacked-bar-fill)

(-> data/penguins
    (sk/xkcd7-lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-value-bar)

(-> sales
    (sk/xkcd7-lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-lm)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-loess)

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  {:x (range 50)
                   :y (map #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                           (range 50))}))

(-> noisy-wave
    (sk/xkcd7-lay-point :x :y)
    sk/xkcd7-lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-density)

(-> data/iris
    (sk/xkcd7-lay-density :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-area)

(-> wave
    (sk/xkcd7-lay-area :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-stacked-area)

(-> {:x (concat (range 10) (range 10) (range 10))
     :y (concat [1 2 3 4 5 4 3 2 1 0]
                [2 2 2 3 3 3 2 2 2 2]
                [1 1 1 1 2 2 2 1 1 1])
     :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
    (sk/xkcd7-lay-stacked-area :x :y {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-text)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/xkcd7-lay-text :x :y {:text :name}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (every? (set (:texts s)) ["A" "B" "C" "D"])))])

(kind/doc #'sk/xkcd7-lay-label)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/xkcd7-lay-point :x :y {:size 5})
    (sk/xkcd7-lay-label {:text :name}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])
(kind/doc #'sk/xkcd7-lay-boxplot)

(-> data/iris
    (sk/xkcd7-lay-boxplot :species :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-violin)

(-> data/tips
    (sk/xkcd7-lay-violin :day :total_bill))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-errorbar)

(-> measurements
    (sk/xkcd7-lay-point :treatment :mean)
    (sk/xkcd7-lay-errorbar {:ymin :ci_lo :ymax :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-lollipop)

(-> sales
    (sk/xkcd7-lay-lollipop :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-tile)

(-> data/iris
    (sk/xkcd7-lay-tile :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/xkcd7-lay-density2d)

(-> data/iris
    (sk/xkcd7-lay-density2d :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/xkcd7-lay-contour)

(-> data/iris
    (sk/xkcd7-lay-contour :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'sk/xkcd7-lay-ridgeline)

(-> data/iris
    (sk/xkcd7-lay-ridgeline :species :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/xkcd7-lay-rug)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    (sk/xkcd7-lay-rug {:side :both}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 300 (:lines s))))])

(kind/doc #'sk/xkcd7-lay-step)

(-> tiny
    (sk/xkcd7-lay-step :x :y)
    sk/xkcd7-lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/xkcd7-lay-summary)

(-> data/iris
    (sk/xkcd7-lay-summary :species :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rendering

(kind/doc #'sk/xkcd7-plot)

;; See the Customization notebook for options (title, theme,
;; tooltip, brush, legend position, palette).

(-> tiny
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:points s))))])

(kind/doc #'sk/xkcd7-options)

;; Set render options on a blueprint:

(-> tiny
    (sk/xkcd7-lay-point :x :y)
    (sk/xkcd7-options {:width 400 :height 200 :title "Small Plot"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (< (:width s) 500)
                                (some #{"Small Plot"} (:texts s)))))])

(kind/doc #'sk/sketch?)

;; Check whether a value is a sketch:

(sk/sketch? (sk/xkcd7-lay-point tiny :x :y))

(kind/test-last [true?])

(kind/doc #'sk/xkcd7-plan)

;; Extract the entries (views) from a blueprint:

(count (:entries (-> tiny (sk/xkcd7-lay-point :x :y) (sk/xkcd7-lay-lm))))

(kind/test-last [(fn [v] (= 2 v))])

(kind/doc #'sk/xkcd7-plan)

;; Returns the intermediate plan data structure:

(def plan1 (-> tiny
               (sk/xkcd7-lay-point :x :y)
               sk/xkcd7-plan))

plan1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

;; ## Pipeline

(kind/doc #'sk/views->plan)

(def plan2 (-> tiny
               (sk/xkcd7-lay-point :x :y)
               sk/xkcd7-plan))

(= (keys plan1) (keys plan2))

(kind/test-last [true?])

(kind/doc #'sk/plan->membrane)

(def m1 (sk/plan->membrane plan1))

(vector? m1)

(kind/test-last [true?])

(kind/doc #'sk/membrane->figure)

(first (sk/membrane->figure m1 :svg
                            {:total-width (:total-width plan1)
                             :total-height (:total-height plan1)}))

(kind/test-last [(fn [v] (= :svg v))])

(kind/doc #'sk/plan->figure)

(first (sk/plan->figure plan1 :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; ## Transforms

(kind/doc #'sk/xkcd7-coord)

;; Flip axes:

(-> data/iris
    (sk/xkcd7-lay-bar :species) (sk/xkcd7-coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> data/iris
    (sk/xkcd7-lay-bar :species) (sk/xkcd7-coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/xkcd7-scale)

;; Log scale:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-scale :x :log))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-scale :x {:domain [3 9]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])
;; ## Annotations

(kind/doc #'sk/rule-v)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-annotate (sk/rule-v 6.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/rule-h)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-annotate (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/band-v)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-annotate (sk/band-v 5.5 6.5)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/band-h)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width) (sk/xkcd7-annotate (sk/band-h 2.5 3.5)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Utilities

(kind/doc #'sk/cross)

(sk/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

(-> data/iris
    (sk/xkcd7-view (sk/cross [:sepal_length :petal_length]
                             [:sepal_width :petal_width]))
    (sk/xkcd7-lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

(kind/doc #'sk/xkcd7-distribution)

(-> data/iris
    (sk/xkcd7-distribution :sepal_length :sepal_width)
    sk/xkcd7-lay-histogram)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceting

(kind/doc #'sk/xkcd7-facet)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
    (sk/xkcd7-facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/xkcd7-facet-grid)

(-> data/tips
    (sk/xkcd7-lay-point :total_bill :tip {:color :sex})
    (sk/xkcd7-facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Inspection

(kind/doc #'sk/svg-summary)

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}) sk/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))))])

(kind/doc #'sk/valid-plan?)

(sk/valid-plan? plan1)

(kind/test-last [true?])

(kind/doc #'sk/explain-plan)

(sk/explain-plan plan1)

(kind/test-last [nil?])

;; ## Configuration

(kind/doc #'sk/config)

(sk/config)

(kind/test-last [(fn [m] (map? m))])

(kind/doc #'sk/set-config!)

(kind/doc #'sk/with-config)

(sk/with-config {:palette :pastel1}
  (:palette (sk/config)))

(kind/test-last [(fn [p] (= :pastel1 p))])

;; ### Documentation Metadata

;; Three maps document the option keys at each scope level.

(kind/doc #'sk/config-key-docs)

(count sk/config-key-docs)

(kind/test-last [(fn [n] (= 36 n))])

(kind/doc #'sk/plot-option-docs)

(count sk/plot-option-docs)

(kind/test-last [(fn [n] (= 6 n))])

(kind/doc #'sk/layer-option-docs)

(count sk/layer-option-docs)

(kind/test-last [(fn [n] (= 20 n))])

;; ## Method Registry

(kind/doc #'sk/method-lookup)

(sk/method-lookup :lm)

(kind/test-last [(fn [m] (and (= :line (:mark m))
                              (= :lm (:stat m))))])

(kind/doc #'sk/method-registered)

(count (sk/method-registered))

(kind/test-last [(fn [n] (= 25 n))])

;; ## Documentation Helpers
;;
;; Query the self-documenting dispatch tables for any extensible concept.

(kind/doc #'sk/stat-doc)

(sk/stat-doc :lm)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'sk/mark-doc)

(sk/mark-doc :point)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'sk/position-doc)

(sk/position-doc :dodge)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'sk/scale-doc)

(sk/scale-doc :linear)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'sk/coord-doc)

(sk/coord-doc :cartesian)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'sk/membrane-mark-doc)

(sk/membrane-mark-doc :point)

(kind/test-last [(fn [s] (string? s))])

;; ## Composition

(kind/doc #'sk/arrange)

(sk/arrange [(-> data/iris
                 (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}) (sk/xkcd7-options {:width 250 :height 200}))
             (-> data/iris
                 (sk/xkcd7-lay-point :petal_length :petal_width {:color :species}) (sk/xkcd7-options {:width 250 :height 200}))]
            {:cols 2})

(kind/test-last [(fn [v] (= :div (first v)))])
;; ## Export

(kind/doc #'sk/save)

;; Save a plot to an SVG file:

(let [path (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save (-> data/iris (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))
           path
           {:title "Iris Export"})
  (.contains (slurp path) "<svg"))

(kind/test-last [true?])
