;; # Exploring Plans
;;
;; When Napkinsketch renders a plot, it builds an intermediate data
;; structure called a **plan** before rendering anything. This notebook
;; walks through the plan step by step, building intuition for the
;; data model by looking at what `sk/xkcd7-plan` produces for different
;; Blueprints.

(ns napkinsketch-book.xkcd7-exploring-sketches
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Method registry — lookup mark/stat/position by keyword
   [scicloj.napkinsketch.method :as method]))

;; ## A Minimal Scatter Plot
;;
;; Let's start with the simplest possible plot: 5 points, no color,
;; no title.

(def tiny {:x [1 2 3 4 5]
           :y [2 4 1 5 3]})

;; Here is the rendered plot:

(-> tiny
    (sk/xkcd7-lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; And here is the plan — the data structure that drives the rendering.
;; We'll use `sk/xkcd7-plan` with the same Blueprint:

(def tiny-pl (-> tiny
                 (sk/xkcd7-lay-point :x :y)
                 sk/xkcd7-plan))

;; ### What's in a plan?
;;
;; At the top level, a plan describes dimensions and layout.
;; Here is the entire plan — a plain Clojure map:

tiny-pl

(kind/test-last [(fn [m] (and (= 600 (:width m))
                              (= 400 (:height m))
                              (nil? (:title m))
                              (= "x" (:x-label m))
                              (= "y" (:y-label m))
                              (nil? (:legend m))))])

;; Notice:
;;
;; - Dimensions are 600×400 with a 25-pixel margin
;; - Labels `"x"` and `"y"` are inferred from column names
;; - No legend (we didn't map a column to color)
;; - One panel with `:x-domain`, `:y-domain`, ticks, and layers

;; ### The panel
;;
;; The plan contains one or more panels. A simple plot has one panel;
;; faceting and SPLOM (scatter plot matrix) produce multiple. Each panel holds its own data space:

(def tiny-panel (first (:panels tiny-pl)))

(keys tiny-panel)

(kind/test-last [(fn [ks] (every? (set ks) [:x-domain :y-domain :layers]))])

;; **Domains** — the numeric range of the data, with a small padding:

(:x-domain tiny-panel)

(kind/test-last [(fn [d] (and (<= (first d) 1) (>= (second d) 5)))])

(:y-domain tiny-panel)

(kind/test-last [(fn [d] (and (<= (first d) 1) (>= (second d) 5)))])

;; **Scale specs** — what kind of scale to use:

(:x-scale tiny-panel)

(kind/test-last [(fn [s] (= :linear (:type s)))])

;; **Ticks** — pre-computed tick positions and their text labels:

(:x-ticks tiny-panel)

(kind/test-last [(fn [t] (and (vector? (:values t))
                              (vector? (:labels t))
                              (= (count (:values t)) (count (:labels t)))))])

;; These are the actual numbers that will appear on the axis.
;; They are in data space — not pixel positions.

;; ### The layer
;;
;; Each method in the Blueprint produces one layer. Our scatter has a single
;; point layer:

(def tiny-layer (first (:layers tiny-panel)))

tiny-layer

(kind/test-last [(fn [m] (= :point (:mark m)))])

;; The style gives rendering hints (opacity, radius) but the geometry
;; is in the **groups**. Without a color mapping, there is one group:

(count (:groups tiny-layer))

(kind/test-last [(fn [n] (= 1 n))])

;; The group contains the actual data — x/y coordinates in data space,
;; plus a resolved RGBA color:

(first (:groups tiny-layer))

(kind/test-last [(fn [g] (and (= 4 (count (:color g)))
                              (= [1 2 3 4 5] (mapv int (:xs g)))
                              (= [2 4 1 5 3] (mapv int (:ys g)))))])

;; These are the original data values — not pixel positions.
;; The renderer maps them through scales to get pixel coordinates.
;;
;; In other words, the plan describes geometry in data space.

;; ## Adding Color
;;
;; When we map a column to color, the plan splits data into groups
;; and adds a legend.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(def iris-pl (-> data/iris
                 (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
                 sk/xkcd7-plan))

;; Here is the full plan — notice the legend and three groups:

iris-pl

(kind/test-last [(fn [m] (and (= 3 (count (:entries (:legend m))))
                              (= 1 (count (:panels m)))))])

;; Now we have three groups — one per species:

(def iris-layer (first (:layers (first (:panels iris-pl)))))

(count (:groups iris-layer))

(kind/test-last [(fn [n] (= 3 n))])

;; Each group has its own resolved color and a subset of the data:

(mapv (fn [g]
        {:color (:color g)
         :n-points (count (:xs g))})
      (:groups iris-layer))

(kind/test-last [(fn [gs] (and (= 3 (count gs))
                               (every? #(= 50 (:n-points %)) gs)))])

;; The legend describes the color mapping:

(:legend iris-pl)

(kind/test-last [(fn [leg] (= 3 (count (:entries leg))))])

;; Colors are resolved to `[r g b a]` vectors — no symbolic references.
;; The same color appears in both the layer groups and the legend entries.

;; ### Continuous Color
;;
;; When `:color` maps to a **numeric** column, the plan stores
;; per-point colors and a continuous gradient legend.

(def cont-pl (-> data/iris
                 (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :petal_length})
                 sk/xkcd7-plan))

(:legend cont-pl)

(kind/test-last [(fn [m] (= :continuous (:type m)))])

;; The legend has pre-computed gradient stops — no functions:

(select-keys (:legend cont-pl) [:title :type :min :max :color-scale])

(kind/test-last [(fn [m] (and (= :continuous (:type m))
                              (not (contains? m :gradient-fn))))])

;; Twenty evenly spaced stops store the gradient colors:

(count (:stops (:legend cont-pl)))

(kind/test-last [(fn [n] (= 20 n))])

;; ## Histograms
;;
;; A histogram computes bins from the data. The plan stores the
;; bin edges and counts — still in data space.

(-> data/iris
    (sk/xkcd7-lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def hist-pl (-> data/iris
                 (sk/xkcd7-lay-histogram :sepal_length)
                 sk/xkcd7-plan))

hist-pl

(kind/test-last [(fn [m] (= 1 (count (:panels m))))])

(def hist-layer (first (:layers (first (:panels hist-pl)))))

(:mark hist-layer)

(kind/test-last [(fn [m] (= :bar m))])

;; The geometry is in `:bars` — each bin has a lo edge, hi edge, and count:

(let [g (first (:groups hist-layer))]
  (:bars g))

(kind/test-last [(fn [bars] (and (> (count bars) 3)
                                 (every? #(< (:lo %) (:hi %)) bars)
                                 (every? #(pos? (:count %)) bars)))])

;; The renderer will draw a rectangle from `(lo, 0)` to `(hi, count)`
;; in data space, then map through scales to pixels.

;; ## Categorical Bars
;;
;; A bar chart counts occurrences of each category. The plan records
;; the categories and counts per group.

(-> data/penguins
    (sk/xkcd7-lay-bar :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def bar-pl (-> data/penguins
                (sk/xkcd7-lay-bar :island {:color :species})
                sk/xkcd7-plan))

(def bar-layer (first (:layers (first (:panels bar-pl)))))

;; The mark type is `:rect` and the layer knows the categories:

bar-layer

(kind/test-last [(fn [m] (and (= :rect (:mark m))
                              (= :dodge (:position m))
                              (= 3 (count (:categories m)))))])

;; Each group (one per color) has counts for every category:

(mapv (fn [g]
        {:label (:label g)
         :counts (:counts g)})
      (:groups bar-layer))

(kind/test-last [(fn [gs] (= 3 (count gs)))])

;; The `:position` field (`:dodge` or `:stack`) tells the renderer
;; how to arrange multiple groups within each category.

;; ## Stacked Bars
;;
;; Stacking changes the position field:

(def stacked-pl (-> data/penguins
                    (sk/xkcd7-lay-stacked-bar :island {:color :species})
                    sk/xkcd7-plan))

(def stacked-layer (first (:layers (first (:panels stacked-pl)))))

(:position stacked-layer)

(kind/test-last [(fn [p] (= :stack p))])

;; The counts are the same — only the rendering instruction differs.
;; The plan describes *what* to draw; the renderer decides *how*.

;; ## Regression Lines
;;
;; A regression produces line segments in data space.

(-> data/iris
    (sk/xkcd7-lay-point :sepal_length :sepal_width)
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(def lm-pl (-> data/iris
               (sk/xkcd7-lay-point :sepal_length :sepal_width)
               sk/xkcd7-lay-lm
               sk/xkcd7-plan))

;; Two layers — points and line:

(mapv :mark (:layers (first (:panels lm-pl))))
(kind/test-last [(fn [marks] (= [:point :line] marks))])
(def lm-layer (second (:layers (first (:panels lm-pl)))))

;; Its group has endpoints — a line segment in data space:

(first (:groups lm-layer))

(kind/test-last [(fn [m] (and (< (:x1 m) (:x2 m))
                              (number? (:x1 m))
                              (number? (:y2 m))))])

;; The renderer maps these two points through scales to get a
;; pixel-space line segment.

;; ## Per-Group Regression
;;
;; When both points and regression have a color mapping, the line
;; layer gets one segment per group:

(-> data/iris
    (sk/xkcd7-view :petal_length :petal_width {:color :species})
    sk/xkcd7-lay-point
    sk/xkcd7-lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])
(def grp-pl (-> data/iris
                (sk/xkcd7-view :petal_length :petal_width {:color :species})
                sk/xkcd7-lay-point
                sk/xkcd7-lay-lm
                sk/xkcd7-plan))

(let [line-layer (second (:layers (first (:panels grp-pl))))]
  (mapv (fn [g]
          {:color (:color g)
           :x1 (some-> (:x1 g) (Math/round) int)
           :x2 (some-> (:x2 g) (Math/round) int)})
        (:groups line-layer)))

(kind/test-last [(fn [gs] (= 3 (count gs)))])

;; Three line segments, each with its own color — one per species.

;; ## Connected Lines (Polylines)
;;
;; Line marks from identity data (not regression) store xs/ys vectors:

(def wave {:x (range 30)
           :y (map #(Math/sin (* % 0.3)) (range 30))})

(-> wave
    (sk/xkcd7-lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

(def wave-pl (-> wave
                 (sk/xkcd7-lay-line :x :y)
                 sk/xkcd7-plan))

(def wave-group (first (:groups (first (:layers (first (:panels wave-pl)))))))

{:n-points (count (:xs wave-group))
 :first-x (first (:xs wave-group))
 :last-x (last (:xs wave-group))}

(kind/test-last [(fn [m] (= 30 (:n-points m)))])

;; The renderer connects these points in order to draw a polyline.

;; ## Value Bars
;;
;; Value bars map categorical x to numeric y without any counting.
;; The plan stores the raw x/y pairs:

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

(-> sales
    (sk/xkcd7-lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

(def sales-pl (-> sales
                  (sk/xkcd7-lay-value-bar :product :revenue)
                  sk/xkcd7-plan))

(let [g (first (:groups (first (:layers (first (:panels sales-pl))))))]
  {:xs (:xs g)
   :ys (:ys g)})

(kind/test-last [(fn [m] (= 4 (count (:xs m))))])

;; ## Flipped Coordinates
;;
;; Setting `:coord :flip` swaps x and y in the plan's panel:

(def flip-pl (-> data/iris
                 (sk/xkcd7-lay-bar :species)
                 (sk/xkcd7-coord :flip)
                 sk/xkcd7-plan))

(:coord (first (:panels flip-pl)))

(kind/test-last [(fn [c] (= :flip c))])

;; The domains are swapped — the categorical axis is now y:

(let [p (first (:panels flip-pl))]
  {:x-domain-type (if (number? (first (:x-domain p))) :numeric :categorical)
   :y-domain-type (if (number? (first (:y-domain p))) :numeric :categorical)})

(kind/test-last [(fn [m] (and (= :numeric (:x-domain-type m))
                              (= :categorical (:y-domain-type m))))])

;; The layer data is unchanged — the coord type tells the renderer
;; to swap axes during mapping.

;; ## Options Affect the Plan
;;
;; Title, labels, and dimensions are recorded in the plan:

(def opts-pl (-> data/iris
                 (sk/xkcd7-lay-point :sepal_length :sepal_width)
                 (sk/xkcd7-plan {:title "My Custom Title"
                                 :x-label "Length (cm)"
                                 :y-label "Width (cm)"
                                 :width 800
                                 :height 300})))

opts-pl

(kind/test-last [(fn [m] (and (= "My Custom Title" (:title m))
                              (= 800 (:width m))
                              (= 300 (:height m))))])

;; The layout records how much space to reserve for each label:

(:layout opts-pl)

(kind/test-last [(fn [lay] (and (pos? (:title-pad lay))
                                (pos? (:x-label-pad lay))
                                (pos? (:y-label-pad lay))))])

;; ## Plan vs Blueprint — Side by Side
;;
;; `sk/xkcd7-plan` and `sk/xkcd7-plot` accept the same Blueprint.
;; `sk/xkcd7-plan` returns the intermediate data map; `sk/xkcd7-plot` returns the final SVG.

;; The plan (a plain Clojure map):

(def final-bp
  (-> data/iris
      (sk/xkcd7-view :petal_length :petal_width {:color :species})
      sk/xkcd7-lay-point
      sk/xkcd7-lay-lm))

(def final-pl (sk/xkcd7-plan final-bp {:title "Iris Petals"}))

final-pl

(kind/test-last [(fn [m] (= "Iris Petals" (:title m)))])

;; Layer summary:

(mapv (fn [l]
        {:mark (:mark l)
         :n-groups (count (:groups l))})
      (:layers (first (:panels final-pl))))

(kind/test-last [(fn [ls] (= 2 (count ls)))])

;; The rendered plot (SVG):

(-> final-bp (sk/xkcd7-options {:title "Iris Petals"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Multi-Panel Plans
;;
;; Faceting produces plans with multiple panels. Each panel has
;; its own domains, ticks, and layers, plus grid positioning.

(def faceted-pl
  (-> data/iris
      (sk/xkcd7-lay-point :sepal_length :sepal_width {:color :species})
      (sk/xkcd7-facet :species)
      sk/xkcd7-plan))

;; The grid tells us the layout:

(:grid faceted-pl)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

;; Three panels — one per species:

(count (:panels faceted-pl))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and strip label:

(:panels faceted-pl)

(kind/test-last [(fn [ps] (and (= 3 (count ps))
                               (every? :col-label ps)))])

;; Panel-level domains show the data range for each subset:

(:panels faceted-pl)

(kind/test-last [(fn [ps] (every? :x-domain ps))])

;; With shared scales (the default), all panels have the same domains.
;; With `:scales :free-y`, each panel gets its own y-domain.

;; The plan also records per-panel pixel dimensions:

(select-keys faceted-pl [:layout-type :grid :total-width :total-height])

(kind/test-last [(fn [m] (= :facet-grid (:layout-type m)))])

;; Multi-panel plans validate against the same Malli schema:

(sk/valid-plan? faceted-pl)

(kind/test-last [true?])

;; ## Malli Validation
;;
;; Every plan conforms to a Malli schema. Validation runs automatically
;; when `sk/xkcd7-plan` is called (default `:validate true`).
;; Pass `{:validate false}` to skip it.
;;
;; You can also check manually with `sk/valid-plan?`:

(sk/valid-plan? tiny-pl)

(kind/test-last [true?])

(sk/valid-plan? iris-pl)

(kind/test-last [true?])

(sk/valid-plan? hist-pl)

(kind/test-last [true?])

(sk/valid-plan? bar-pl)

(kind/test-last [true?])

(sk/valid-plan? lm-pl)

(kind/test-last [true?])

(sk/valid-plan? final-pl)

(kind/test-last [true?])

;; When a plan is invalid, `sk/explain-plan` shows which part failed:

(sk/explain-plan (assoc tiny-pl :width "not-a-number"))

(kind/test-last [some?])

;; ## Data Types
;;
;; Plans are plain inspectable data — maps, numbers, strings,
;; keywords, and dtype-next buffers for numeric arrays (see [Architecture](./napkinsketch_book.architecture.html)) (`:xs`, `:ys`,
;; etc.). The buffers support `nth`, `count`, `seq`, and all standard
;; sequence operations.

(type (:xs (first (:groups (first (:layers (first (:panels tiny-pl))))))))

(kind/test-last [(fn [t] (not= clojure.lang.PersistentVector t))])

;; You can convert any numeric buffer to a plain vector with `vec`:

(vec (:xs (first (:groups (first (:layers (first (:panels tiny-pl))))))))

(kind/test-last [(fn [v] (and (vector? v) (number? (first v))))])

;; ## What's Next
;;
;; - [**Architecture**](./napkinsketch_book.architecture.html) — the four-stage pipeline in detail
;; - [**Extensibility**](./napkinsketch_book.extensibility.html) — add custom marks, stats, and renderers
