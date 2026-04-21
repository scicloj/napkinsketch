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
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Fastmath -- random number generation
   [fastmath.random :as rng]))

;; ## Sample Data

(def tiny {:x [1 2 3 4 5]
           :y [2 4 1 5 3]
           :group [:a :a :b :b :b]})

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

(def measurements {:treatment ["A" "B" "C" "D"]
                   :mean [10.0 15.0 12.0 18.0]
                   :ci-lo [8.0 12.0 9.5 15.5]
                   :ci-hi [12.0 18.0 14.5 20.5]})

;; ## Construction

(kind/doc #'sk/sketch)

;; Create a sketch with sketch-level mappings visible to all views:

(-> (sk/sketch (rdatasets/datasets-iris) {:color :species})
    (sk/view :sepal-length :sepal-width)
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/with-data)

;; Attach or replace the sketch-level dataset. Useful for building a
;; template sketch without data and applying it to many datasets:

(def scatter-template
  (-> (sk/sketch)
      (sk/view :x :y {:color :group})
      sk/lay-point))

(-> scatter-template
    (sk/with-data tiny))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

(kind/doc #'sk/view)

;; Single scatter view -- two columns as `[x y]`:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Histogram view -- a single keyword means x = y (diagonal):

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

;; Multiple views -- a vector of `[x y]` pairs:

(-> (rdatasets/datasets-iris)
    (sk/view [[:sepal-length :sepal-width]
              [:petal-length :petal-width]])
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form -- explicit keys:

(-> (rdatasets/datasets-iris)
    (sk/view {:x :sepal-length :y :sepal-width})
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/cross)

(sk/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

(-> (rdatasets/datasets-iris)
    (sk/view (sk/cross [:sepal-length :petal-length]
                       [:sepal-width :petal-width]))
    (sk/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

;; Multi-column vector creates one panel per column:

(sk/lay-histogram (rdatasets/datasets-iris) [:sepal-length :sepal-width])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Layer Functions

(kind/doc #'sk/lay)

;; The generic layer adder. `sk/lay-point`, `sk/lay-bar`, etc. are
;; convenience wrappers around `sk/lay` with a registered method
;; key. Use `sk/lay` directly when you have a custom method (from
;; `sk/method-lookup` on a registered key, or a raw method map from
;; an extension):

(-> (rdatasets/datasets-iris)
    (sk/view :sepal-length :sepal-width)
    (sk/lay :point))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(kind/doc #'sk/lay-point)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}))

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

(-> (rdatasets/datasets-iris)
    (sk/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-bar)

(-> (rdatasets/datasets-iris)
    (sk/lay-bar :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar)

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-stacked-bar :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-stacked-bar-fill)

(-> (rdatasets/palmerpenguins-penguins)
    (sk/lay-stacked-bar-fill :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-value-bar)

(-> sales
    (sk/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-lm)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
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

(-> (rdatasets/datasets-iris)
    (sk/lay-density :sepal-length))

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

(-> (rdatasets/datasets-iris)
    (sk/lay-boxplot :species :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/lay-violin)

(-> (rdatasets/reshape2-tips)
    (sk/lay-violin :day :total-bill))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'sk/lay-errorbar)

(-> measurements
    (sk/lay-point :treatment :mean)
    (sk/lay-errorbar {:ymin :ci-lo :ymax :ci-hi}))

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

(-> (rdatasets/datasets-iris)
    (sk/lay-tile :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/lay-density2d)

(-> (rdatasets/datasets-iris)
    (sk/lay-density2d :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'sk/lay-contour)

(-> (rdatasets/datasets-iris)
    (sk/lay-contour :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'sk/lay-ridgeline)

(-> (rdatasets/datasets-iris)
    (sk/lay-ridgeline :species :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/lay-rug)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
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

(-> (rdatasets/datasets-iris)
    (sk/lay-summary :species :sepal-length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Annotations

;; Reference lines and shaded bands are regular layers. Position comes
;; from the opts map (`:y-intercept` for `lay-rule-h`, `:x-intercept`
;; for `lay-rule-v`; `:y-min`/`:y-max` for `lay-band-h`,
;; `:x-min`/`:x-max` for `lay-band-v`); appearance aesthetics like
;; `:color` and `:alpha` work the same as on any other layer. Without
;; x/y columns they attach to the sketch (every panel); with x/y
;; columns they attach to one view.

(kind/doc #'sk/lay-rule-v)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-rule-v {:x-intercept 6.0}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/lay-rule-h)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-rule-h {:y-intercept 3.0}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'sk/lay-band-v)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-band-v {:x-min 5.5 :x-max 6.5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'sk/lay-band-h)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/lay-band-h {:y-min 2.5 :y-max 3.5}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Transforms

(kind/doc #'sk/coord)

;; Flip axes:

(-> (rdatasets/datasets-iris)
    (sk/lay-bar :species) (sk/coord :flip))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> (rdatasets/datasets-iris)
    (sk/lay-bar :species) (sk/coord :polar))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'sk/scale)

;; Log scale:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width) (sk/scale :x :log))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width) (sk/scale :x {:domain [3 9]}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Faceting

(kind/doc #'sk/facet)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/facet :species))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'sk/facet-grid)

(-> (rdatasets/reshape2-tips)
    (sk/lay-point :total-bill :tip {:color :sex})
    (sk/facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Composition

(kind/doc #'sk/arrange)

(sk/arrange [(-> (rdatasets/datasets-iris)
                 (sk/lay-point :sepal-length :sepal-width {:color :species})
                 (sk/options {:width 250 :height 200}))
             (-> (rdatasets/datasets-iris)
                 (sk/lay-point :petal-length :petal-width {:color :species})
                 (sk/options {:width 250 :height 200}))]
            {:cols 2})

(kind/test-last [(fn [v] (= :div (first v)))])

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

;; ## Predicates

(kind/doc #'sk/sketch?)

;; Check whether a value is a sketch:

(sk/sketch? (sk/lay-point tiny :x :y))

(kind/test-last [true?])

(kind/doc #'sk/plan?)

;; Check whether a value is a plan (from `sk/plan`):

(sk/plan? (sk/plan (sk/lay-point tiny :x :y)))

(kind/test-last [true?])

(kind/doc #'sk/layer?)

;; Check whether a value is a resolved plan layer:

(sk/layer? (first (:layers (first (:panels (sk/plan (sk/lay-point tiny :x :y)))))))

(kind/test-last [true?])

(kind/doc #'sk/method?)

;; Check whether a value is a registered method map:

(sk/method? (sk/method-lookup :point))

(kind/test-last [true?])

;; ## Inspection

(kind/doc #'sk/draft)

;; Flatten a sketch into a vector of draft layers -- one per
;; (view, applicable-layer) pair, with all scope merged. Useful
;; for inspecting exactly what the renderer will draw:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    sk/draft
    kind/pprint)

(kind/test-last [(fn [d] (and (vector? d)
                              (= 1 (count d))
                              (= :point (:mark (first d)))))])

(kind/doc #'sk/plan)

;; Returns the intermediate plan data structure:

(def plan1 (-> tiny
               (sk/lay-point :x :y)
               sk/plan))

plan1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

(kind/doc #'sk/svg-summary)

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species}) sk/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))))])

(kind/doc #'sk/valid-plan?)

(sk/valid-plan? plan1)

(kind/test-last [true?])

(kind/doc #'sk/explain-plan)

(sk/explain-plan plan1)

(kind/test-last [nil?])

;; ## Pipeline

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

(kind/test-last [(fn [n] (= 11 n))])

(kind/doc #'sk/layer-option-docs)

(count sk/layer-option-docs)

(kind/test-last [(fn [n] (pos? n))])

;; ## Method Registry

(kind/doc #'sk/method-lookup)

(sk/method-lookup :lm)

(kind/test-last [(fn [m] (and (= :line (:mark m))
                              (= :lm (:stat m))))])

(kind/doc #'sk/registered-methods)

(count (sk/registered-methods))

(kind/test-last [(fn [n] (= 29 n))])

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

;; ## Export

(kind/doc #'sk/save)

;; Save a plot to an SVG file:

(let [path (str (java.io.File/createTempFile "napkinsketch-example" ".svg"))]
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg"))

(kind/test-last [true?])

(kind/doc #'sk/save-png)

;; Save a plot to a PNG file via the membrane Java2D raster path.
;; Returns the path:

(let [path (str (java.io.File/createTempFile "napkinsketch-example" ".png"))]
  (-> (rdatasets/datasets-iris)
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/save-png path))
  (.exists (java.io.File. ^String path)))

(kind/test-last [true?])
