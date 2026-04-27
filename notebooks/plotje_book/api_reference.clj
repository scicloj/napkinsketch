;; # API Reference
;;
;; Complete reference for every public function in
;; `scicloj.plotje.api`.
;;
;; Each entry shows the docstring, a live example, and a test.
;; For galleries of mark variations, see the Reference notebooks
;; (Scatter, Distributions, Ranking, Change Over Time, Relationships).

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns plotje-book.api-reference
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
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

(kind/doc #'pj/pose)

;; Create a leaf pose with data and columns:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width)
    pj/lay-point)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

;; Map form -- include aesthetics on the pose so every layer sees them:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width {:color :species})
    pj/lay-point
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

(kind/doc #'pj/with-data)

;; Attach or replace the top-level dataset on a pose.
;; Useful for building a dataless template and applying it to many
;; datasets:

(def scatter-template
  (-> (pj/pose nil {:x :x :y :y :color :group})
      pj/lay-point))

(-> scatter-template
    (pj/with-data tiny))

(kind/test-last [(fn [v] (= 5 (:points (pj/svg-summary v))))])

;; Multi-pair pose -- a vector of `[x y]` pairs creates a composite
;; with one sub-pose per pair:

(-> (rdatasets/datasets-iris)
    (pj/pose [[:sepal-length :sepal-width]
              [:petal-length :petal-width]])
    (pj/lay-point {:color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (= 300 (:points s)))))])

;; Map form -- explicit keys on a pose:

(-> (rdatasets/datasets-iris)
    (pj/pose {:x :sepal-length :y :sepal-width})
    pj/lay-point)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'pj/cross)

(pj/cross [:a :b] [1 2 3])

(kind/test-last [(fn [v] (= [[:a 1] [:a 2] [:a 3] [:b 1] [:b 2] [:b 3]] v))])

;; Combine `pj/cross` with `pj/pose` to build a SPLOM:

(-> (rdatasets/datasets-iris)
    (pj/pose {:color :species})
    (pj/pose (pj/cross [:sepal-length :petal-length]
                       [:sepal-width :petal-width])))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 600 (:points s)))))])

;; Multi-column vector creates one panel per column:

(pj/lay-histogram (rdatasets/datasets-iris) [:sepal-length :sepal-width])

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 2 (:panels s))
                                (pos? (:polygons s)))))])

;; ## Layer Functions

(kind/doc #'pj/lay)

;; The generic layer adder. `pj/lay-point`, `pj/lay-bar`, etc. are
;; convenience wrappers around `pj/lay` with a registered layer-type
;; key. Use `pj/lay` directly when you have a custom layer type (from
;; `pj/layer-type-lookup` on a registered key, or a raw layer-type
;; map from an extension):

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width)
    (pj/lay :point))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; The above just delegates to `:point` -- equivalent to
;; `pj/lay-point`. The intended use of `pj/lay` is with a layer
;; type that isn't a built-in convenience: a registered custom
;; layer type from an extension, or a raw layer-type map. See
;; the [Waterfall Extension](./plotje_book.waterfall_extension.html)
;; chapter for the full pattern -- registering a `:waterfall`
;; layer type and calling `(pj/lay pose (layer-type/lookup :waterfall))`
;; or wrapping it in a `pj/lay-waterfall` convenience function.

(kind/doc #'pj/lay-point)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'pj/lay-line)

(def wave {:x (range 30)
           :y (map #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (pj/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 1 (:lines s))))])

(kind/doc #'pj/lay-histogram)

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'pj/lay-bar)

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Stacked bars: pass `{:position :stack}` to `pj/lay-bar`.

(-> (rdatasets/palmerpenguins-penguins)
    (pj/lay-bar :island {:position :stack :color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:polygons s))))])

;; 100% stacked bars: pass `{:position :fill}` to `pj/lay-bar`.

(-> (rdatasets/palmerpenguins-penguins)
    (pj/lay-bar :island {:position :fill :color :species}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'pj/lay-value-bar)

(-> sales
    (pj/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 4 (:polygons s))))])

;; Linear regression: pass `{:stat :linear-model}` to `pj/lay-smooth`.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-smooth {:stat :linear-model}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'pj/lay-smooth)

(-> (let [r (rng/rng :jdk 42)
          xs (vec (range 50))]
      {:x xs
       :y (mapv #(+ (Math/sin (* % 0.2))
                    (* 0.3 (- (rng/drandom r) 0.5)))
                xs)})
    (pj/lay-point :x :y)
    (pj/lay-smooth {:bandwidth 0.2}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 50 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'pj/lay-density)

(-> (rdatasets/datasets-iris)
    (pj/lay-density :sepal-length))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 1 (:polygons s))))])

(kind/doc #'pj/lay-area)

(-> wave
    (pj/lay-area :x :y))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 1 (:polygons s))))])

;; Stacked areas: pass `{:position :stack}` to `pj/lay-area`.

(-> {:x (concat (range 10) (range 10) (range 10))
     :y (concat [1 2 3 4 5 4 3 2 1 0]
                [2 2 2 3 3 3 2 2 2 2]
                [1 1 1 1 2 2 2 1 1 1])
     :group (concat (repeat 10 "A") (repeat 10 "B") (repeat 10 "C"))}
    (pj/lay-area :x :y {:position :stack :color :group}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 3 (:polygons s))))])

(kind/doc #'pj/lay-text)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (pj/lay-text :x :y {:text :name}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (every? (set (:texts s)) ["A" "B" "C" "D"])))])

(kind/doc #'pj/lay-label)

(-> {:x [1 2 3 4] :y [4 7 5 8] :name ["A" "B" "C" "D"]}
    (pj/lay-point :x :y {:size 5})
    (pj/lay-label {:text :name}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:points s))
                                (every? (set (:texts s)) ["A" "B" "C" "D"]))))])

(kind/doc #'pj/lay-boxplot)

(-> (rdatasets/datasets-iris)
    (pj/lay-boxplot :species :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:polygons s))
                                (pos? (:lines s)))))])

(kind/doc #'pj/lay-violin)

(-> (rdatasets/reshape2-tips)
    (pj/lay-violin :day :total-bill))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 4 (:polygons s))))])

(kind/doc #'pj/lay-errorbar)

(-> measurements
    (pj/lay-point :treatment :mean)
    (pj/lay-errorbar {:y-min :ci-lo :y-max :ci-hi}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 12 (:lines s)))))])

(kind/doc #'pj/lay-lollipop)

(-> sales
    (pj/lay-lollipop :product :revenue))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:points s))
                                (= 4 (:lines s)))))])

(kind/doc #'pj/lay-tile)

(-> (rdatasets/datasets-iris)
    (pj/lay-tile :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'pj/lay-density-2d)

(-> (rdatasets/datasets-iris)
    (pj/lay-density-2d :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:visible-tiles s))))])

(kind/doc #'pj/lay-contour)

(-> (rdatasets/datasets-iris)
    (pj/lay-contour :sepal-length :sepal-width))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:lines s))))])

(kind/doc #'pj/lay-ridgeline)

(-> (rdatasets/datasets-iris)
    (pj/lay-ridgeline :species :sepal-length))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'pj/lay-rug)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-rug {:side :both}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 300 (:lines s))))])

(kind/doc #'pj/lay-step)

(-> tiny
    (pj/lay-step :x :y)
    pj/lay-point)

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

(kind/doc #'pj/lay-summary)

(-> (rdatasets/datasets-iris)
    (pj/lay-summary :species :sepal-length))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:points s))
                                (= 3 (:lines s)))))])

;; ## Annotations

;; Reference lines and shaded bands are regular layers. Position comes
;; from the options map (`:y-intercept` for `lay-rule-h`, `:x-intercept`
;; for `lay-rule-v`; `:y-min`/`:y-max` for `lay-band-h`,
;; `:x-min`/`:x-max` for `lay-band-v`); appearance aesthetics like
;; `:color` and `:alpha` work the same as on any other layer. Without
;; x/y columns they attach at the root (every panel); with x/y
;; columns they attach to one matching leaf.

(kind/doc #'pj/lay-rule-v)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-rule-v {:x-intercept 6.0}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'pj/lay-rule-h)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-rule-h {:y-intercept 3.0}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (pos? (:lines s)))))])

(kind/doc #'pj/lay-band-v)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-band-v {:x-min 5.5 :x-max 6.5}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

(kind/doc #'pj/lay-band-h)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-band-h {:y-min 2.5 :y-max 3.5}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Transforms

(kind/doc #'pj/coord)

;; Flip axes:

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species) (pj/coord :flip))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 3 (:polygons s))))])

;; Polar coordinates:

(-> (rdatasets/datasets-iris)
    (pj/lay-bar :species) (pj/coord :polar))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (pos? (:polygons s))))])

(kind/doc #'pj/scale)

;; Log scale:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width) (pj/scale :x :log))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

;; Fixed domain:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width) (pj/scale :x {:domain [3 9]}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 150 (:points s))))])

;; ## Faceting

(kind/doc #'pj/facet)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/facet :species))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 3 (:panels s))
                                (= 150 (:points s)))))])

(kind/doc #'pj/facet-grid)

(-> (rdatasets/reshape2-tips)
    (pj/lay-point :total-bill :tip {:color :sex})
    (pj/facet-grid :smoker :sex))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 4 (:panels s))
                                (= 244 (:points s)))))])

;; ## Composition

(kind/doc #'pj/arrange)

(pj/arrange [(-> (rdatasets/datasets-iris)
                 (pj/lay-point :sepal-length :sepal-width {:color :species})
                 (pj/options {:width 250 :height 200}))
             (-> (rdatasets/datasets-iris)
                 (pj/lay-point :petal-length :petal-width {:color :species})
                 (pj/options {:width 250 :height 200}))]
            {:cols 2})

(kind/test-last [(fn [v] (pj/pose? v))])

;; ## Rendering

(kind/doc #'pj/plot)

;; See the Customization notebook for options (title, theme,
;; tooltip, brush, legend position, palette).

(-> tiny
    (pj/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (= 5 (:points s))))])

(kind/doc #'pj/options)

;; Set render options on a pose:

(-> tiny
    (pj/lay-point :x :y)
    (pj/options {:width 400 :height 200 :title "Small Plot"}))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (< (:width s) 500)
                                (some #{"Small Plot"} (:texts s)))))])

;; ## Predicates

(kind/doc #'pj/pose?)

;; Check whether a value is a pose (leaf or composite):

(pj/pose? (-> tiny (pj/pose :x :y) pj/lay-point))

(kind/test-last [true?])

(kind/doc #'pj/plan?)

;; Check whether a value is a plan (from `pj/plan`):

(pj/plan? (pj/plan (pj/lay-point tiny :x :y)))

(kind/test-last [true?])

(kind/doc #'pj/layer?)

;; Check whether a value is a resolved plan layer:

(pj/layer? (first (:layers (first (:panels (pj/plan (pj/lay-point tiny :x :y)))))))

(kind/test-last [true?])

(kind/doc #'pj/layer-type?)

;; Check whether a value is a registered layer-type map:

(pj/layer-type? (pj/layer-type-lookup :point))

(kind/test-last [true?])

;; ## Inspection

(kind/doc #'pj/draft)

;; Flatten a pose into a vector of draft layers -- one
;; per applicable layer, with all scope merged. Useful for
;; inspecting exactly what the renderer will draw:

(-> (rdatasets/datasets-iris)
    (pj/pose :sepal-length :sepal-width)
    pj/lay-point
    pj/draft
    kind/pprint)

(kind/test-last [(fn [d] (and (vector? d)
                              (= 1 (count d))
                              (= :point (:mark (first d)))))])

(kind/doc #'pj/plan)

;; Returns the intermediate plan data structure:

(def plan1 (-> tiny
               (pj/lay-point :x :y)
               pj/plan))

plan1

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= "x" (:x-label m))))])

(kind/doc #'pj/svg-summary)

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species}) pj/svg-summary)

(kind/test-last [(fn [m] (and (= 1 (:panels m))
                              (= 150 (:points m))))])

(kind/doc #'pj/valid-plan?)

(pj/valid-plan? plan1)

(kind/test-last [true?])

(kind/doc #'pj/explain-plan)

(pj/explain-plan plan1)

(kind/test-last [nil?])

;; ## Pipeline

(kind/doc #'pj/plan->membrane)

(def m1 (pj/plan->membrane plan1))

(vector? m1)

(kind/test-last [true?])

(kind/doc #'pj/membrane->plot)

(first (pj/membrane->plot m1 :svg
                          {:total-width (:total-width plan1)
                           :total-height (:total-height plan1)}))

(kind/test-last [(fn [v] (= :svg v))])

(kind/doc #'pj/plan->plot)

(first (pj/plan->plot plan1 :svg {}))

(kind/test-last [(fn [v] (= :svg v))])

;; ## Configuration

(kind/doc #'pj/config)

(pj/config)

(kind/test-last [(fn [m] (map? m))])

(kind/doc #'pj/set-config!)

(kind/doc #'pj/with-config)

(pj/with-config {:palette :pastel1}
  (:palette (pj/config)))

(kind/test-last [(fn [p] (= :pastel1 p))])

;; ### Documentation Metadata

;; Three maps document the option keys at each scope level.

(kind/doc #'pj/config-key-docs)

(count pj/config-key-docs)

(kind/test-last [(fn [n] (= 37 n))])

(kind/doc #'pj/plot-option-docs)

(count pj/plot-option-docs)

(kind/test-last [(fn [n] (= 13 n))])

(kind/doc #'pj/layer-option-docs)

(count pj/layer-option-docs)

(kind/test-last [(fn [n] (pos? n))])

;; ## Method Registry

(kind/doc #'pj/layer-type-lookup)

(pj/layer-type-lookup :smooth)

(kind/test-last [(fn [m] (and (= :line (:mark m))
                              (= :loess (:stat m))))])

(kind/doc #'pj/registered-layer-types)

(count (pj/registered-layer-types))

(kind/test-last [(fn [n] (= 25 n))])

;; ## Documentation Helpers
;;
;; Query the self-documenting dispatch tables for any extensible concept.

(kind/doc #'pj/stat-doc)

(pj/stat-doc :linear-model)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'pj/mark-doc)

(pj/mark-doc :point)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'pj/position-doc)

(pj/position-doc :dodge)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'pj/scale-doc)

(pj/scale-doc :linear)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'pj/coord-doc)

(pj/coord-doc :cartesian)

(kind/test-last [(fn [s] (string? s))])

(kind/doc #'pj/membrane-mark-doc)

(pj/membrane-mark-doc :point)

(kind/test-last [(fn [s] (string? s))])

;; ## Export

(kind/doc #'pj/save)

;; Save a plot to an SVG file:

(let [path (str (java.io.File/createTempFile "plotje-example" ".svg"))]
  (-> (rdatasets/datasets-iris)
      (pj/lay-point :sepal-length :sepal-width {:color :species})
      (pj/save path {:title "Iris Export"}))
  (.contains (slurp path) "<svg"))

(kind/test-last [true?])

(kind/doc #'pj/save-png)

;; Save a plot to a PNG file via membrane's Java2D backend.
;; Returns the path:

(let [path (str (java.io.File/createTempFile "plotje-example" ".png"))]
  (-> (rdatasets/datasets-iris)
      (pj/lay-point :sepal-length :sepal-width {:color :species})
      (pj/save-png path))
  (.exists (java.io.File. ^String path)))

(kind/test-last [true?])
