;; # API Reference
;;
;; Complete reference for every public function in
;; `scicloj.napkinsketch.api`.
;;
;; Each entry shows the docstring, a live example, and a test.
;; For galleries of mark variations, see the Reference notebooks
;; (Scatter, Distributions, Ranking, Evolution, Relationships).

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns napkinsketch-book.api-reference
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath — random number generation (for synthetic data)
   [fastmath.random :as rng]))

;; ## Sample Data

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def tiny {:x [1 2 3 4 5]
           :y [2 4 1 5 3]
           :group [:a :a :b :b :b]})

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

(def measurements {:treatment ["A" "B" "C" "D"]
                   :mean [10.0 15.0 12.0 18.0]
                   :ci_lo [8.0 12.0 9.5 15.5]
                   :ci_hi [12.0 18.0 14.5 20.5]})

;; ## Data Setup

(kind/doc #'sk/view)

;; Single scatter view — two columns as `[x y]`:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> iris
    (sk/view :sepal_length)
    sk/lay-histogram
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> iris
    (sk/view [[:sepal_length :sepal_width]
              [:petal_length :petal_width]])
    (sk/lay-point {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form — explicit keys:

(-> (sk/view iris {:x :sepal_length :y :sepal_width})
    sk/lay-point
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/lay)

;; Add layers with `sk/lay-point`, `sk/lay-lm`, etc.:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    (sk/lay-lm {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Layer Functions

(kind/doc #'sk/lay-point)

(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/lay-line)

(def wave {:x (range 30)
           :y (mapv #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/lay-line :x :y)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:lines s))))])

(kind/doc #'sk/lay-histogram)

(-> iris
    (sk/view :sepal_length)
    sk/lay-histogram
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-bar)

(-> iris
    (sk/view :species)
    sk/lay-bar
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar)

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins
    (sk/view :island)
    (sk/lay-stacked-bar {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar-fill)

(-> penguins
    (sk/view :island)
    (sk/lay-stacked-bar-fill {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-value-bar)

(-> sales
    (sk/lay-value-bar :product :revenue)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-lm)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    sk/lay-lm
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-loess)

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  {:x (range 50)
                   :y (mapv #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                            (range 50))}))

(-> noisy-wave
    (sk/view [[:x :y]])
    sk/lay-point
    sk/lay-loess
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-density)

(-> iris
    (sk/view [[:sepal_length]])
    sk/lay-density
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/lay-area)

(-> wave
    (sk/view [[:x :y]])
    sk/lay-area
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/lay-stacked-area)

(-> {:x (vec (concat (range 10) (range 10) (range 10)))
     :y (vec (concat [1 2 3 4 5 4 3 2 1 0]
                     [2 2 2 3 3 3 2 2 2 2]
                     [1 1 1 1 2 2 2 1 1 1]))
     :group (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))}
    (sk/view [[:x :y]])
    (sk/lay-stacked-area {:color :group})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/lay-text)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/view [[:x :y]])
    (sk/lay-text {:text :name})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (every? (set (:texts s)) ["A" "B" "C" "D"])))])

(kind/doc #'sk/lay-label)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (sk/view [[:x :y]])
    (sk/lay-point {:size 5})
    (sk/lay-label {:text :name})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])
(kind/doc #'sk/lay-boxplot)

(-> iris
    (sk/view [[:species :sepal_width]])
    sk/lay-boxplot
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/lay-violin)

(-> tips
    (sk/view [[:day :total_bill]])
    sk/lay-violin
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-errorbar)

(-> measurements
    (sk/view [[:treatment :mean]])
    sk/lay-point
    (sk/lay-errorbar {:ymin :ci_lo :ymax :ci_hi})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'sk/lay-lollipop)

(-> sales
    (sk/view [[:product :revenue]])
    sk/lay-lollipop
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

(kind/doc #'sk/lay-tile)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-tile
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:tiles s))))])

(kind/doc #'sk/lay-density2d)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-density2d
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:tiles s))))])

(kind/doc #'sk/lay-contour)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-contour
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'sk/lay-ridgeline)

(-> iris
    (sk/view [[:species :sepal_length]])
    sk/lay-ridgeline
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-rug)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/lay-rug {:side :both})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 300 (:lines s))))])

(kind/doc #'sk/lay-step)

(-> tiny
    (sk/view [[:x :y]])
    sk/lay-step
    sk/lay-point
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/lay-summary)

(-> iris
    (sk/view [[:species :sepal_length]])
    sk/lay-summary
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rendering

(kind/doc #'sk/plot)

;; See the Customization notebook for options (title, theme,
;; tooltip, brush, legend position, palette).

(-> tiny
    (sk/lay-point :x :y)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:points s))))])

(kind/doc #'sk/sketch)

;; Returns the intermediate sketch data structure:

(def sk1 (-> tiny
             (sk/lay-point :x :y)
             sk/sketch))

sk1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

;; ## Pipeline

(kind/doc #'sk/views->sketch)

(def sk2 (-> tiny
             (sk/lay-point :x :y)
             sk/views->sketch))

(= (keys sk1) (keys sk2))

(kind/test-last [true?])

(kind/doc #'sk/sketch->membrane)

(def m1 (sk/sketch->membrane sk1))

(vector? m1)

(kind/test-last [true?])

(kind/doc #'sk/membrane->figure)

(first (sk/membrane->figure m1 :svg
                            {:total-width (:total-width sk1)
                             :total-height (:total-height sk1)}))

(kind/test-last [(fn [v] (= :svg v))])

(kind/doc #'sk/sketch->figure)

(first (sk/sketch->figure sk1 :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; ## Transforms

(kind/doc #'sk/coord)

;; Flip axes:

(-> iris
    (sk/view :species)
    sk/lay-bar
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> iris
    (sk/view :species)
    sk/lay-bar
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/scale)

;; Log scale:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/scale :x {:domain [3 9]})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/labs)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    (sk/labs {:title "Iris Dimensions" :x "Sepal Length (cm)" :y "Sepal Width (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (some #{"Iris Dimensions"} (:texts s))))])

;; ## Annotations

(kind/doc #'sk/rule-v)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/lay (sk/rule-v 6.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/rule-h)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/lay (sk/rule-h 3.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/band-v)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/lay (sk/band-v 5.5 6.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/band-h)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    sk/lay-point
    (sk/lay (sk/band-h 2.5 3.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Utilities

(kind/doc #'sk/cross)

(sk/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

(-> iris
    (sk/view (sk/cross [:sepal_length :petal_length]
                       [:sepal_width :petal_width]))
    (sk/lay-point {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

(kind/doc #'sk/distribution)

(-> (sk/distribution iris :sepal_length :sepal_width)
    sk/lay-histogram
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceting

(kind/doc #'sk/facet)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay-point {:color :species})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/facet-grid)

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay-point {:color :sex})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Inspection

(kind/doc #'sk/svg-summary)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay-point {:color :species})
    sk/plot
    sk/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))))])

(kind/doc #'sk/valid-sketch?)

(sk/valid-sketch? sk1)

(kind/test-last [true?])

(kind/doc #'sk/explain-sketch)

(sk/explain-sketch sk1)

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

;; ## Composition

(kind/doc #'sk/arrange)

(sk/arrange [(-> iris
                 (sk/view :sepal_length :sepal_width)
                 (sk/lay-point {:color :species})
                 (sk/plot {:width 250 :height 200}))
             (-> iris
                 (sk/view :petal_length :petal_width)
                 (sk/lay-point {:color :species})
                 (sk/plot {:width 250 :height 200}))]
            {:cols 2})

(kind/test-last [(fn [v] (= :div (first v)))])
;; ## Export

(kind/doc #'sk/save)

;; Save a plot to an SVG file:

(let [path (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (sk/save (-> iris (sk/lay-point :sepal_length :sepal_width {:color :species}))
           path
           {:title "Iris Export"})
  (.contains (slurp path) "<svg"))

(kind/test-last [true?])
