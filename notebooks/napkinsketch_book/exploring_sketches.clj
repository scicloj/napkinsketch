;; # Exploring Sketches
;;
;; When you call `sk/plot`, Napkinsketch builds an intermediate data
;; structure called a **sketch** before rendering anything. This notebook
;; walks through the sketch step by step, building intuition for the
;; data model by looking at what `sk/sketch` produces for different plots.

(ns napkinsketch-book.exploring-sketches
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; Pretty-printing — for inspecting data structures
   [clojure.pprint :as pp]))

;; ## A Minimal Scatter Plot
;;
;; Let's start with the simplest possible plot: 5 points, no color,
;; no title.

(def tiny (tc/dataset {:x [1 2 3 4 5]
                       :y [2 4 1 5 3]}))

;; Here is the rendered plot:

(sk/plot [(sk/point {:data tiny :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 5 (:points s)))))])

;; And here is the sketch — the data structure that drives the rendering.
;; We'll use `sk/sketch` with the same arguments:

(def tiny-sk (sk/sketch [(sk/point {:data tiny :x :x :y :y})]))

;; ### What's in a sketch?
;;
;; At the top level, a sketch describes dimensions and layout:

tiny-sk

(kind/test-last [(fn [m] (and (= 600 (:width m)) (= 400 (:height m))))])

;; The plot area is 600×400 pixels with a 25-pixel margin inside.
;; `total-width` and `total-height` include extra space for axis labels.
;;
;; Labels are inferred from column names:

tiny-sk

(kind/test-last [(fn [m] (and (nil? (:title m))
                              (= "x" (:x-label m))
                              (= "y" (:y-label m))))])

;; No legend, since we didn't map any column to color:

(:legend tiny-sk)

(kind/test-last [nil?])

;; ### The panel
;;
;; The sketch contains one or more panels. A simple plot has one panel;
;; faceting and SPLOM produce multiple. Each panel holds its own data space:

(def tiny-panel (first (:panels tiny-sk)))

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
;; Each mark in the plot produces one layer. Our scatter has a single
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
;; This is the key insight: **the sketch describes geometry in data space**.

;; ## Adding Color
;;
;; When we map a column to color, the sketch splits data into groups
;; and adds a legend.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 150 (:points s)))))])

(def iris-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})]))

;; Now we have three groups — one per species:

(def iris-layer (first (:layers (first (:panels iris-sk)))))

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

(:legend iris-sk)

(kind/test-last [(fn [leg] (= 3 (count (:entries leg))))])

;; Colors are resolved to `[r g b a]` vectors — no symbolic references.
;; The same color appears in both the layer groups and the legend entries.


;; ### Continuous Color
;;
;; When `:color` maps to a **numeric** column, the sketch stores
;; per-point colors and a continuous gradient legend.

(def cont-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width
                                    :color :petal_length})]))

;; The legend has pre-computed gradient stops — no functions, fully serializable:

(select-keys (:legend cont-sk) [:title :type :min :max :color-scale])

(kind/test-last [(fn [m] (and (= :continuous (:type m))
                              (not (contains? m :gradient-fn))))])

;; Twenty evenly spaced stops store the gradient colors:

(count (:stops (:legend cont-sk)))

(kind/test-last [(fn [n] (= 20 n))])

;; ## Histograms
;;
;; A histogram computes bins from the data. The sketch stores the
;; bin edges and counts — still in data space.

(sk/plot [(sk/histogram {:data iris :x :sepal_length})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def hist-sk (sk/sketch [(sk/histogram {:data iris :x :sepal_length})]))

(def hist-layer (first (:layers (first (:panels hist-sk)))))

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
;; A bar chart counts occurrences of each category. The sketch records
;; the categories and counts per group.

(sk/plot [(sk/bar {:data iris :x :species :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (pos? (:polygons s)))))])

(def bar-sk (sk/sketch [(sk/bar {:data iris :x :species :color :species})]))

(def bar-layer (first (:layers (first (:panels bar-sk)))))

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

(def stacked-sk (sk/sketch [(sk/stacked-bar {:data iris :x :species :color :species})]))

(def stacked-layer (first (:layers (first (:panels stacked-sk)))))

(:position stacked-layer)

(kind/test-last [(fn [p] (= :stack p))])

;; The data is the same — only the rendering instruction differs.
;; The sketch describes *what* to draw; the renderer decides *how*.

;; ## Regression Lines
;;
;; A regression produces line segments in data space.

(sk/plot [(sk/point {:data iris :x :sepal_length :y :sepal_width})
          (sk/lm {:data iris :x :sepal_length :y :sepal_width})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 1 (:lines s)))))])

(def lm-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width})
                       (sk/lm {:data iris :x :sepal_length :y :sepal_width})]))

;; Two layers — points and line:

(mapv :mark (:layers (first (:panels lm-sk))))
(kind/test-last [(fn [marks] (= [:point :line] marks))])
(def lm-layer (second (:layers (first (:panels lm-sk)))))

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

(sk/plot [(sk/point {:data iris :x :petal_length :y :petal_width :color :species})
          (sk/lm {:data iris :x :petal_length :y :petal_width :color :species})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])
(def grp-sk (sk/sketch [(sk/point {:data iris :x :petal_length :y :petal_width :color :species})
                        (sk/lm {:data iris :x :petal_length :y :petal_width :color :species})]))

(let [line-layer (second (:layers (first (:panels grp-sk))))]
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

(def wave (tc/dataset {:x (range 30)
                       :y (mapv #(Math/sin (* % 0.3)) (range 30))}))

(sk/plot [(sk/line {:data wave :x :x :y :y})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 1 (:lines s)))))])

(def wave-sk (sk/sketch [(sk/line {:data wave :x :x :y :y})]))

(def wave-group (first (:groups (first (:layers (first (:panels wave-sk)))))))

{:n-points (count (:xs wave-group))
 :first-x (first (:xs wave-group))
 :last-x (last (:xs wave-group))}

(kind/test-last [(fn [m] (= 30 (:n-points m)))])

;; The renderer connects these points in order to draw a polyline.

;; ## Value Bars
;;
;; Value bars map categorical x to numeric y without any counting.
;; The sketch stores the raw x/y pairs:

(def sales (tc/dataset {:product [:widget :gadget :gizmo :doohickey]
                        :revenue [120 340 210 95]}))

(sk/plot [(sk/value-bar {:data sales :x :product :y :revenue})])

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 1 (:panels s))
                                (= 4 (:polygons s)))))])

(def sales-sk (sk/sketch [(sk/value-bar {:data sales :x :product :y :revenue})]))

(let [g (first (:groups (first (:layers (first (:panels sales-sk))))))]
  {:xs (:xs g)
   :ys (:ys g)})

(kind/test-last [(fn [m] (= 4 (count (:xs m))))])

;; ## Flipped Coordinates
;;
;; Setting `:coord :flip` swaps x and y in the sketch's panel:

(def flip-sk (sk/sketch [(-> (sk/bar {:data iris :x :species})
                             (assoc :coord :flip))]))

(:coord (first (:panels flip-sk)))

(kind/test-last [(fn [c] (= :flip c))])

;; The domains are swapped — the categorical axis is now y:

(let [p (first (:panels flip-sk))]
  {:x-domain-type (if (number? (first (:x-domain p))) :numeric :categorical)
   :y-domain-type (if (number? (first (:y-domain p))) :numeric :categorical)})

(kind/test-last [(fn [m] (and (= :numeric (:x-domain-type m))
                              (= :categorical (:y-domain-type m))))])

;; The layer data is unchanged — the coord type tells the renderer
;; to swap axes during mapping.

;; ## Options Affect the Sketch
;;
;; Title, labels, and dimensions are recorded in the sketch:

(def opts-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width})]
                        {:title "My Custom Title"
                         :x-label "Length (cm)"
                         :y-label "Width (cm)"
                         :width 800
                         :height 300}))

opts-sk

(kind/test-last [(fn [m] (and (= "My Custom Title" (:title m))
                              (= 800 (:width m))
                              (= 300 (:height m))))])

;; The layout records how much space to reserve for each label:

(:layout opts-sk)

(kind/test-last [(fn [lay] (and (pos? (:title-pad lay))
                                (pos? (:x-label-pad lay))
                                (pos? (:y-label-pad lay))))])

;; ## Sketch vs Plot — Side by Side
;;
;; `sk/sketch` and `sk/plot` accept the same arguments.
;; The sketch is the intermediate data; the plot is the final SVG.

;; The sketch (a plain Clojure map):

(def final-views
  [(sk/point {:data iris :x :petal_length :y :petal_width :color :species})
   (sk/lm {:data iris :x :petal_length :y :petal_width :color :species})])

(def final-sk (sk/sketch final-views {:title "Iris Petals"}))

final-sk

(kind/test-last [(fn [m] (= "Iris Petals" (:title m)))])

;; Layer summary:

(mapv (fn [l]
        {:mark (:mark l)
         :n-groups (count (:groups l))})
      (:layers (first (:panels final-sk))))

(kind/test-last [(fn [ls] (= 2 (count ls)))])

;; The rendered plot (SVG):

(sk/plot final-views {:title "Iris Petals"})

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 150 (:points s))
                                (= 3 (:lines s)))))])

;; ## Multi-Panel Sketches
;;
;; Faceting produces sketches with multiple panels. Each panel has
;; its own domains, ticks, and layers, plus grid positioning.

(def faceted-sk
  (-> iris
      (sk/view [[:sepal_length :sepal_width]])
      (sk/facet :species)
      (sk/lay (sk/point {:color :species}))
      sk/sketch))

;; The grid tells us the layout:

(:grid faceted-sk)

(kind/test-last [(fn [g] (and (= 1 (:rows g)) (= 3 (:cols g))))])

;; Three panels — one per species:

(count (:panels faceted-sk))

(kind/test-last [(fn [n] (= 3 n))])

;; Each panel has a grid position and strip label:

(:panels faceted-sk)

(kind/test-last [(fn [ps] (and (= 3 (count ps))
                               (every? :col-label ps)))])

;; Panel-level domains show the data range for each subset:

(:panels faceted-sk)

(kind/test-last [(fn [ps] (every? :x-domain ps))])

;; With shared scales (the default), all panels have the same domains.
;; With `:scales :free-y`, each panel gets its own y-domain.

;; The sketch also records per-panel pixel dimensions:

faceted-sk

(kind/test-last [(fn [m] (= :facet-grid (:layout-type m)))])

;; Multi-panel sketches validate against the same Malli schema:

(sk/valid-sketch? faceted-sk)

(kind/test-last [true?])

;; ## Malli Validation
;;
;; Every sketch conforms to a Malli schema. Validation runs automatically
;; when `sk/sketch` or `sk/plot` is called (default `:validate true`).
;; Pass `{:validate false}` to skip it.
;;
;; You can also check manually with `sk/valid-sketch?`:

(sk/valid-sketch? tiny-sk)

(kind/test-last [true?])

(sk/valid-sketch? iris-sk)

(kind/test-last [true?])

(sk/valid-sketch? hist-sk)

(kind/test-last [true?])

(sk/valid-sketch? bar-sk)

(kind/test-last [true?])

(sk/valid-sketch? lm-sk)

(kind/test-last [true?])

(sk/valid-sketch? final-sk)

(kind/test-last [true?])

;; When a sketch is invalid, `sk/explain-sketch` shows which part failed:

(sk/explain-sketch (assoc tiny-sk :width "not-a-number"))

(kind/test-last [some?])

;; ## Serialization
;;
;; Sketches are plain Clojure data — maps, vectors, numbers, strings,
;; keywords. They serialize cleanly with `pr-str` and read back with
;; `read-string`.

(let [s (pr-str tiny-sk)
      back (read-string s)]
  (= tiny-sk back))

(kind/test-last [true?])

;; This makes sketches suitable for caching, logging, and snapshot testing.
