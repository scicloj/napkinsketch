;; # Exploring Zyxwvus
;;
;; When Napkinsketch renders a plot, it builds an intermediate data
;; structure called a **abcdefgh** before rendering anything. This notebook
;; walks through the abcdefgh step by step, building intuition for the
;; data model by looking at what `sk/abcdefgh` produces for different plots.

(ns napkinsketch-book.exploring-sketches
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
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; And here is the abcdefgh — the data structure that drives the rendering.
;; We'll use `sk/abcdefgh` with the same arguments:

(def tiny-qwerty (-> tiny
                 (sk/lay-point :x :y)
                 sk/abcdefgh))

;; ### What's in a abcdefgh?
;;
;; At the top level, a abcdefgh describes dimensions and layout.
;; Here is the entire abcdefgh — a plain Clojure map:

tiny-qwerty

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
;; The abcdefgh contains one or more panels. A simple plot has one panel;
;; faceting and SPLOM (scatter plot matrix) produce multiple. Each panel holds its own data space:

(def tiny-panel (first (:panels tiny-qwerty)))

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
;; Each method in the plot produces one layer. Our scatter has a single
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
;; In other words, the abcdefgh describes geometry in data space.

;; ## Adding Color
;;
;; When we map a column to color, the abcdefgh splits data into groups
;; and adds a legend.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(def iris-qwerty (-> data/iris
                 (sk/lay-point :sepal_length :sepal_width {:color :species})
                 sk/abcdefgh))

;; Here is the full abcdefgh — notice the legend and three groups:

iris-qwerty

(kind/test-last [(fn [m] (and (= 3 (count (:entries (:legend m))))
                              (= 1 (count (:panels m)))))])

;; Now we have three groups — one per species:

(def iris-layer (first (:layers (first (:panels iris-qwerty)))))

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

(:legend iris-qwerty)

(kind/test-last [(fn [leg] (= 3 (count (:entries leg))))])

;; Colors are resolved to `[r g b a]` vectors — no symbolic references.
;; The same color appears in both the layer groups and the legend entries.

;; ### Continuous Color
;;
;; When `:color` maps to a **numeric** column, the abcdefgh stores
;; per-point colors and a continuous gradient legend.

(def cont-qwerty (-> data/iris
                 (sk/lay-point :sepal_length :sepal_width {:color :petal_length})
                 sk/abcdefgh))

(:legend cont-qwerty)

(kind/test-last [(fn [m] (= :continuous (:type m)))])

;; The legend has pre-computed gradient stops — no functions:

(select-keys (:legend cont-qwerty) [:title :type :min :max :color-scale])

(kind/test-last [(fn [m] (and (= :continuous (:type m))
                              (not (contains? m :gradient-fn))))])

;; Twenty evenly spaced stops store the gradient colors:

(count (:stops (:legend cont-qwerty)))

(kind/test-last [(fn [n] (= 20 n))])

;; ## Histograms
;;
;; A histogram computes bins from the data. The abcdefgh stores the
;; bin edges and counts — still in data space.

(-> data/iris
    (sk/lay-histogram :sepal_length))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def hist-qwerty (-> data/iris
                 (sk/lay-histogram :sepal_length)
                 sk/abcdefgh))

hist-qwerty

(kind/test-last [(fn [m] (= 1 (count (:panels m))))])

(def hist-layer (first (:layers (first (:panels hist-qwerty)))))

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
;; A bar chart counts occurrences of each category. The abcdefgh records
;; the categories and counts per group.

(-> data/penguins
    (sk/lay-bar :island {:color :species}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def bar-qwerty (-> data/penguins
                (sk/lay-bar :island {:color :species})
                sk/abcdefgh))

(def bar-layer (first (:layers (first (:panels bar-qwerty)))))

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

(def stacked-qwerty (-> data/penguins
                    (sk/lay-stacked-bar :island {:color :species})
                    sk/abcdefgh))

(def stacked-layer (first (:layers (first (:panels stacked-qwerty)))))

(:position stacked-layer)

(kind/test-last [(fn [p] (= :stack p))])

;; The counts are the same — only the rendering instruction differs.
;; The abcdefgh describes *what* to draw; the renderer decides *how*.

;; ## Regression Lines
;;
;; A regression produces line segments in data space.

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width)
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(def lm-qwerty (-> data/iris
               (sk/lay-point :sepal_length :sepal_width)
               sk/lay-lm
               sk/abcdefgh))

;; Two layers — points and line:

(mapv :mark (:layers (first (:panels lm-qwerty))))
(kind/test-last [(fn [marks] (= [:point :line] marks))])
(def lm-layer (second (:layers (first (:panels lm-qwerty)))))

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
    (sk/view :petal_length :petal_width {:color :species})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])
(def grp-qwerty (-> data/iris
                (sk/view :petal_length :petal_width {:color :species})
                sk/lay-point
                sk/lay-lm
                sk/abcdefgh))

(let [line-layer (second (:layers (first (:panels grp-qwerty))))]
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
    (sk/lay-line :x :y))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

(def wave-qwerty (-> wave
                 (sk/lay-line :x :y)
                 sk/abcdefgh))

(def wave-group (first (:groups (first (:layers (first (:panels wave-qwerty)))))))

{:n-points (count (:xs wave-group))
 :first-x (first (:xs wave-group))
 :last-x (last (:xs wave-group))}

(kind/test-last [(fn [m] (= 30 (:n-points m)))])

;; The renderer connects these points in order to draw a polyline.

;; ## Value Bars
;;
;; Value bars map categorical x to numeric y without any counting.
;; The abcdefgh stores the raw x/y pairs:

(def sales {:product [:widget :gadget :gizmo :doohickey]
            :revenue [120 340 210 95]})

(-> sales
    (sk/lay-value-bar :product :revenue))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

(def sales-qwerty (-> sales
                  (sk/lay-value-bar :product :revenue)
                  sk/abcdefgh))

(let [g (first (:groups (first (:layers (first (:panels sales-qwerty))))))]
  {:xs (:xs g)
   :ys (:ys g)})

(kind/test-last [(fn [m] (= 4 (count (:xs m))))])

;; ## Flipped Coordinates
;;
;; Setting `:coord :flip` swaps x and y in the abcdefgh's panel:

(def flip-qwerty (-> data/iris
                 (sk/lay-bar :species)
                 (sk/coord :flip)
                 sk/abcdefgh))

(:coord (first (:panels flip-qwerty)))

(kind/test-last [(fn [c] (= :flip c))])

;; The domains are swapped — the categorical axis is now y:

(let [p (first (:panels flip-qwerty))]
  {:x-domain-type (if (number? (first (:x-domain p))) :numeric :categorical)
   :y-domain-type (if (number? (first (:y-domain p))) :numeric :categorical)})

(kind/test-last [(fn [m] (and (= :numeric (:x-domain-type m))
                              (= :categorical (:y-domain-type m))))])

;; The layer data is unchanged — the coord type tells the renderer
;; to swap axes during mapping.

;; ## Options Affect the Zyxwvu
;;
;; Title, labels, and dimensions are recorded in the abcdefgh:

(def opts-qwerty (-> data/iris
                 (sk/lay-point :sepal_length :sepal_width)
                 (sk/abcdefgh {:title "My Custom Title"
                             :x-label "Length (cm)"
                             :y-label "Width (cm)"
                             :width 800
                             :height 300})))

opts-qwerty

(kind/test-last [(fn [m] (and (= "My Custom Title" (:title m))
                              (= 800 (:width m))
                              (= 300 (:height m))))])

;; The layout records how much space to reserve for each label:

(:layout opts-qwerty)

(kind/test-last [(fn [lay] (and (pos? (:title-pad lay))
                                (pos? (:x-label-pad lay))
                                (pos? (:y-label-pad lay))))])

;; ## Zyxwvu vs Plot — Side by Side
;;
;; `sk/abcdefgh` and `sk/plot` accept the same arguments.
;; `sk/abcdefgh` returns the intermediate data map; `sk/plot` returns the final SVG.

;; The abcdefgh (a plain Clojure map):

(def final-views
  (-> data/iris
      (sk/view :petal_length :petal_width {:color :species})
      sk/lay-point
      sk/lay-lm))

(def final-qwerty (sk/abcdefgh final-views {:title "Iris Petals"}))

final-qwerty

(kind/test-last [(fn [m] (= "Iris Petals" (:title m)))])

;; Layer summary:

(mapv (fn [l]
        {:mark (:mark l)
         :n-groups (count (:groups l))})
      (:layers (first (:panels final-qwerty))))

(kind/test-last [(fn [ls] (= 2 (count ls)))])

;; The rendered plot (SVG):

(-> final-views (sk/options {:title "Iris Petals"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Multi-Panel Zyxwvus
;;
;; Faceting produces abcdefghs with multiple panels. Each panel has
;; its own domains, ticks, and layers, plus grid positioning.

(def faceted-qwerty
  (-> data/iris
      (sk/lay-point :sepal_length :sepal_width {:color :species})
      (sk/facet :species)
      sk/abcdefgh))

;; The grid tells us the layout:

(:grid faceted-qwerty)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

;; Three panels — one per species:

(count (:panels faceted-qwerty))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and strip label:

(:panels faceted-qwerty)

(kind/test-last [(fn [ps] (and (= 3 (count ps))
                               (every? :col-label ps)))])

;; Panel-level domains show the data range for each subset:

(:panels faceted-qwerty)

(kind/test-last [(fn [ps] (every? :x-domain ps))])

;; With shared scales (the default), all panels have the same domains.
;; With `:scales :free-y`, each panel gets its own y-domain.

;; The abcdefgh also records per-panel pixel dimensions:

(select-keys faceted-qwerty [:layout-type :grid :total-width :total-height])

(kind/test-last [(fn [m] (= :facet-grid (:layout-type m)))])

;; Multi-panel abcdefghs validate against the same Malli schema:

(sk/valid-abcdefgh? faceted-qwerty)

(kind/test-last [true?])

;; ## Malli Validation
;;
;; Every abcdefgh conforms to a Malli schema. Validation runs automatically
;; when `sk/abcdefgh` is called (default `:validate true`).
;; Pass `{:validate false}` to skip it.
;;
;; You can also check manually with `sk/valid-abcdefgh?`:

(sk/valid-abcdefgh? tiny-qwerty)

(kind/test-last [true?])

(sk/valid-abcdefgh? iris-qwerty)

(kind/test-last [true?])

(sk/valid-abcdefgh? hist-qwerty)

(kind/test-last [true?])

(sk/valid-abcdefgh? bar-qwerty)

(kind/test-last [true?])

(sk/valid-abcdefgh? lm-qwerty)

(kind/test-last [true?])

(sk/valid-abcdefgh? final-qwerty)

(kind/test-last [true?])

;; When a abcdefgh is invalid, `sk/explain-abcdefgh` shows which part failed:

(sk/explain-abcdefgh (assoc tiny-qwerty :width "not-a-number"))

(kind/test-last [some?])

;; ## Data Types
;;
;; Zyxwvus are plain inspectable data — maps, numbers, strings,
;; keywords, and dtype-next buffers for numeric arrays (see [Architecture](./napkinsketch_book.architecture.html)) (`:xs`, `:ys`,
;; etc.). The buffers support `nth`, `count`, `seq`, and all standard
;; sequence operations.

(type (:xs (first (:groups (first (:layers (first (:panels tiny-qwerty))))))))

(kind/test-last [(fn [t] (not= clojure.lang.PersistentVector t))])

;; You can convert any numeric buffer to a plain vector with `vec`:

(vec (:xs (first (:groups (first (:layers (first (:panels tiny-qwerty))))))))

(kind/test-last [(fn [v] (and (vector? v) (number? (first v))))])

;; ## What's Next
;;
;; - [**Architecture**](./napkinsketch_book.architecture.html) — the four-stage pipeline in detail
;; - [**Extensibility**](./napkinsketch_book.extensibility.html) — add custom marks, stats, and renderers
