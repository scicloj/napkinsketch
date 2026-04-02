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

(kind/doc #'sk/view)

;; Single scatter view — two columns as `[x y]`:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> data/iris
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> data/iris
    (sk/view [[:sepal_length :sepal_width]
              [:petal_length :petal_width]])
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form — explicit keys:

(-> (sk/view data/iris {:x :sepal_length :y :sepal_width})
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/lay)

;; Add layers with `sk/lay-point`, `sk/lay-lm`, etc.:

(-> data/iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Layer Functions

(kind/doc #'sk/lay-point)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/lay-line)

(def wave {:x (range 30)
           :y (map #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:lines s))))])

(kind/doc #'sk/lay-histogram)

(-> data/iris
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-bar)

(-> data/iris
    (sk/lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar)

(-> data/penguins
    (sk/lay-stacked-bar :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar-fill)

(-> data/penguins
    (sk/lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-value-bar)

(-> sales
    (sk/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-lm)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-loess)

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  {:x (range 50)
                   :y (map #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                           (range 50))}))

(-> noisy-wave
    (sk/lay-point :x :y)
    sk/lay-loess)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-density)

(-> data/iris
    (sk/lay-density :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/lay-area)

(-> wave
    (sk/lay-area :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/lay-stacked-area)

(-> {:x (concat (range 10) (range 10) (range 10))
     :y (concat [1 2 3 4 5 4 3 2 1 0]
                [2 2 2 3 3 3 2 2 2 2]
                [1 1 1 1 2 2 2 1 1 1])
     :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
    (sk/lay-stacked-area :x :y {:color :group}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/lay-text)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/lay-text :x :y {:text :name}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (every? (set (:texts s)) ["A" "B" "C" "D"])))])

(kind/doc #'sk/lay-label)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/lay-point :x :y {:size 5})
    (sk/lay-label {:text :name}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])
(kind/doc #'sk/lay-boxplot)

(-> data/iris
    (sk/lay-boxplot :species :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/lay-violin)

(-> data/tips
    (sk/lay-violin :day :total_bill))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-errorbar)

(-> measurements
    (sk/lay-point :treatment :mean)
    (sk/lay-errorbar {:ymin :ci_lo :ymax :ci_hi}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'sk/lay-lollipop)

(-> sales
    (sk/lay-lollipop :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

(kind/doc #'sk/lay-tile)

(-> data/iris
    (sk/lay-tile :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/lay-density2d)

(-> data/iris
    (sk/lay-density2d :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/lay-contour)

(-> data/iris
    (sk/lay-contour :sepal_length :sepal_width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'sk/lay-ridgeline)

(-> data/iris
    (sk/lay-ridgeline :species :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-rug)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    (sk/lay-rug {:side :both}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 300 (:lines s))))])

(kind/doc #'sk/lay-step)

(-> tiny
    (sk/lay-step :x :y)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-summary)

(-> data/iris
    (sk/lay-summary :species :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rendering

(kind/doc #'sk/plot)

;; See the Customization notebook for options (title, theme,
;; tooltip, brush, legend position, palette).

(-> tiny
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:points s))))])

(kind/doc #'sk/options)

;; Set render options on a sketch:

(-> tiny
    (sk/lay-point :x :y)
    (sk/options {:width 400 :height 200 :title "Small Plot"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (< (:width s) 500)
                                (some #{"Small Plot"} (:texts s)))))])

(kind/doc #'sk/sketch?)

;; Check whether a value is a sketch:

(sk/sketch? (sk/lay-point tiny :x :y))

(kind/test-last [true?])

(kind/doc #'sk/views-of)

;; Extract the raw views from a sketch:

(count (sk/views-of (-> tiny (sk/lay-point :x :y) (sk/lay-lm))))

(kind/test-last [(fn [v] (= 2 v))])

(kind/doc #'sk/abcdefgh)

;; Returns the intermediate abcdefgh data structure:

(def sk1 (-> tiny
             (sk/lay-point :x :y)
             sk/abcdefgh))

sk1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

;; ## Pipeline

(kind/doc #'sk/views->abcdefgh)

(def sk2 (-> tiny
             (sk/lay-point :x :y)
             sk/views->abcdefgh))

(= (keys sk1) (keys sk2))

(kind/test-last [true?])

(kind/doc #'sk/abcdefgh->membrane)

(def m1 (sk/abcdefgh->membrane sk1))

(vector? m1)

(kind/test-last [true?])

(kind/doc #'sk/membrane->figure)

(first (sk/membrane->figure m1 :svg
                            {:total-width (:total-width sk1)
                             :total-height (:total-height sk1)}))

(kind/test-last [(fn [v] (= :svg v))])

(kind/doc #'sk/abcdefgh->figure)

(first (sk/abcdefgh->figure sk1 :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; ## Transforms

(kind/doc #'sk/coord)

;; Flip axes:

(-> data/iris
    (sk/lay-bar :species) (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> data/iris
    (sk/lay-bar :species) (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/scale)

;; Log scale:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/scale :x :log))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/scale :x {:domain [3 9]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])
;; ## Annotations

(kind/doc #'sk/rule-v)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/lay (sk/rule-v 6.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/rule-h)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/lay (sk/rule-h 3.0)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/band-v)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/lay (sk/band-v 5.5 6.5)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/band-h)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width) (sk/lay (sk/band-h 2.5 3.5)))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Utilities

(kind/doc #'sk/cross)

(sk/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

(-> data/iris
    (sk/view (sk/cross [:sepal_length :petal_length]
                       [:sepal_width :petal_width]))
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

(kind/doc #'sk/distribution)

(-> (sk/distribution data/iris :sepal_length :sepal_width)
    sk/lay-histogram)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceting

(kind/doc #'sk/facet)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/facet-grid)

(-> data/tips
    (sk/lay-point :total_bill :tip {:color :sex})
    (sk/facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Inspection

(kind/doc #'sk/svg-summary)

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}) sk/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))))])

(kind/doc #'sk/valid-abcdefgh?)

(sk/valid-abcdefgh? sk1)

(kind/test-last [true?])

(kind/doc #'sk/explain-abcdefgh)

(sk/explain-abcdefgh sk1)

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
                 (sk/lay-point :sepal_length :sepal_width {:color :species}) (sk/options {:width 250 :height 200}))
             (-> data/iris
                 (sk/lay-point :petal_length :petal_width {:color :species}) (sk/options {:width 250 :height 200}))]
            {:cols 2})

(kind/test-last [(fn [v] (= :div (first v)))])
;; ## Export

(kind/doc #'sk/save)

;; Save a plot to an SVG file:

(let [path (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save (-> data/iris (sk/lay-point :sepal_length :sepal_width {:color :species}))
           path
           {:title "Iris Export"})
  (.contains (slurp path) "<svg"))

(kind/test-last [true?])
