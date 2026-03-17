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

(def tiny (tc/dataset {:x [1 2 3 4 5]
                       :y [2 4 1 5 3]
                       :group [:a :a :b :b :b]}))

(def sales (tc/dataset {:product [:widget :gadget :gizmo :doohickey]
                        :revenue [120 340 210 95]}))

(def tips (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/tips.csv"
                      {:key-fn keyword}))

(def measurements (tc/dataset {:treatment ["A" "B" "C" "D"]
                               :mean [10.0 15.0 12.0 18.0]
                               :ci_lo [8.0 12.0 9.5 15.5]
                               :ci_hi [12.0 18.0 14.5 20.5]}))

;; ## Data Setup

(kind/doc #'sk/view)

;; Single scatter view — two columns as `[x y]`:

(-> iris (sk/view [[:sepal_length :sepal_width]]) (sk/lay (sk/point)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view — a single keyword means x = y (diagonal):

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views — a vector of `[x y]` pairs:

(-> iris
    (sk/view [[:sepal_length :sepal_width]
              [:petal_length :petal_width]])
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form — explicit keys:

(-> (sk/view iris {:x :sepal_length :y :sepal_width})
    (sk/lay (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/lay)

;; Apply marks to views:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Marks

(kind/doc #'sk/point)

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/line)

(def wave (tc/dataset {:x (range 30)
                       :y (mapv #(Math/sin (* % 0.3)) (range 30))}))

(sk/plot [(sk/line {:data wave :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:lines s))))])

(kind/doc #'sk/histogram)

(-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/bar)

(-> iris (sk/view :species) (sk/lay (sk/bar)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/stacked-bar)

(def penguins (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/penguins.csv"
                          {:key-fn keyword}))

(-> penguins (sk/view :island) (sk/lay (sk/stacked-bar {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/stacked-bar-fill)

(-> penguins (sk/view :island) (sk/lay (sk/stacked-bar-fill {:color :species})) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/value-bar)

(sk/plot [(sk/value-bar {:data sales :x :product :y :revenue})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lm)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/lm))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/loess)

(def noisy-wave (let [r (rng/rng :jdk 42)]
                  (tc/dataset {:x (range 50)
                               :y (mapv #(+ (Math/sin (* % 0.2)) (* 0.3 (- (rng/drandom r) 0.5)))
                                        (range 50))})))

(-> noisy-wave
    (sk/view [[:x :y]])
    (sk/lay (sk/point) (sk/loess))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/density)

(-> iris
    (sk/view [[:sepal_length]])
    (sk/lay (sk/density))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/area)

(-> wave (sk/view [[:x :y]]) (sk/lay (sk/area)) sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'sk/stacked-area)

(-> (tc/dataset {:x (vec (concat (range 10) (range 10) (range 10)))
                 :y (vec (concat [1 2 3 4 5 4 3 2 1 0]
                                 [2 2 2 3 3 3 2 2 2 2]
                                 [1 1 1 1 2 2 2 1 1 1]))
                 :group (vec (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C")))})
    (sk/view [[:x :y]])
    (sk/lay (sk/stacked-area {:color :group}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/text)

(-> (tc/dataset {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]})
    (sk/view [[:x :y]])
    (sk/lay (sk/text {:text :name}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (every? (set (:texts s)) ["A" "B" "C" "D"])))])

(kind/doc #'sk/label)

(-> (tc/dataset {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]})
    (sk/view [[:x :y]])
    (sk/lay (sk/point {:size 5})
            (sk/label {:text :name}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])
(kind/doc #'sk/boxplot)

(-> iris
    (sk/view [[:species :sepal_width]])
    (sk/lay (sk/boxplot))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/violin)

(-> tips
    (sk/view [[:day :total_bill]])
    (sk/lay (sk/violin))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/errorbar)

(-> measurements
    (sk/view [[:treatment :mean]])
    (sk/lay (sk/point)
            (sk/errorbar {:ymin :ci_lo :ymax :ci_hi}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'sk/lollipop)

(-> sales
    (sk/view [[:product :revenue]])
    (sk/lay (sk/lollipop))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

(kind/doc #'sk/tile)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/tile))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:tiles s))))])

(kind/doc #'sk/density2d)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/density2d))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:tiles s))))])

(kind/doc #'sk/contour)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/contour))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'sk/ridgeline)

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/ridgeline))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/rug)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/rug {:side :both}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 300 (:lines s))))])

(kind/doc #'sk/step)

(-> tiny
    (sk/view [[:x :y]])
    (sk/lay (sk/step) (sk/point))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'sk/summary)

(-> iris
    (sk/view [[:species :sepal_length]])
    (sk/lay (sk/summary))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Rendering

(kind/doc #'sk/plot)

;; See the Customization notebook for options (title, theme,
;; tooltip, brush, legend position, palette).

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 5 (:points s))))])

(kind/doc #'sk/sketch)

;; Returns the intermediate sketch data structure:

(def sk1 (sk/sketch [(sk/point {:data tiny :x :x :y :y})]))

sk1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

;; ## Pipeline

(kind/doc #'sk/views->sketch)

(def sk2 (sk/views->sketch [(sk/point {:data tiny :x :x :y :y})]))

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
    (sk/lay (sk/bar))
    (sk/coord :flip)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> iris
    (sk/view :species)
    (sk/lay (sk/bar))
    (sk/coord :polar)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/scale)

;; Log scale:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x :log)
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point))
    (sk/scale :x {:domain [3 9]})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/labs)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
    (sk/labs {:title "Iris Dimensions" :x "Sepal Length (cm)" :y "Sepal Width (cm)"})
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (some #{"Iris Dimensions"} (:texts s))))])

;; ## Annotations

(kind/doc #'sk/rule-v)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/rule-v 6.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/rule-h)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/rule-h 3.0))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/band-v)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/band-v 5.5 6.5))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/band-h)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point) (sk/band-h 2.5 3.5))
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
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

(kind/doc #'sk/pairs)

(sk/pairs [:a :b :c])

(kind/test-last [(fn [v] (= [[:a :b] [:a :c] [:b :c]] v))])

(-> iris
    (sk/view (sk/pairs [:sepal_length :sepal_width :petal_length]))
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 450 (:points s)))))])

(kind/doc #'sk/distribution)

(-> (sk/distribution iris :sepal_length :sepal_width)
    (sk/lay (sk/histogram))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Faceting

(kind/doc #'sk/facet)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/facet :species)
    (sk/lay (sk/point {:color :species}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/facet-grid)

(-> tips
    (sk/view [[:total_bill :tip]])
    (sk/facet-grid :smoker :sex)
    (sk/lay (sk/point {:color :sex}))
    sk/plot)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Inspection

(kind/doc #'sk/svg-summary)

(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species}))
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
