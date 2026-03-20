;; # Inference Rules
;;
;; Napkinsketch infers many parameters automatically so you can write
;; less and get reasonable defaults. This notebook shows those rules
;; in action by examining the **sketch** — the resolved data structure
;; that captures every inference decision.
;;
;; The examples use small inline datasets so the full sketch is
;; readable.

(ns napkinsketch-book.inference-rules
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## The Sketch Is the Answer
;;
;; Every call to `sk/sketch` returns a plain Clojure map: the **sketch**.
;; It contains everything needed to render a plot — domains, ticks,
;; scales, layers with positioned data, legend, layout dimensions.
;; No functions, no datasets, no opaque objects.
;;
;; To understand what napkinsketch inferred, look at the sketch.

(def five-points
  (tc/dataset {:x [1.0 2.0 3.0 4.0 5.0]
               :y [2.1 4.3 3.0 5.2 4.8]}))

(def scatter-views
  (-> five-points
      (sk/view :x :y)
      (sk/lay (sk/point))))

;; Here is the full sketch:

(kind/pprint (sk/sketch scatter-views))

(kind/test-last [(fn [sk] (and (= :single (:layout-type sk))
                               (= 1 (count (:panels sk)))
                               (= "x" (:x-label sk))
                               (= "y" (:y-label sk))))])

;; And the resulting plot:

(sk/plot scatter-views)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Notice in the sketch above:
;;
;; - `:x-domain` is `[0.8 5.2]` — wider than the data range `[1.0, 5.0]`
;;   because of 5% padding
;; - `:x-scale` is `{:type :linear}` — inferred from numeric data
;; - `:x-ticks` has nice round values: `1.0, 1.5, 2.0, ...`
;; - `:x-label` is `"x"` — derived from the column keyword
;; - `:legend` is `nil` — no color mapping
;; - `:layout` has `:legend-w 0` — no space reserved for a legend
;; - The single layer has `:mark :point` and one group with all 5 data
;;   points, colored in default gray `[0.2 0.2 0.2 1.0]`

;; ## Column Types Drive Everything
;;
;; The first inference: is each column **numerical** or **categorical**?
;; This determines the scale type, domain, tick style, and default mark.
;;
;; | Column dtype | Inferred type |
;; |:-------------|:--------------|
;; | float, int | `:numerical` |
;; | string, keyword, boolean | `:categorical` |
;; | LocalDate, LocalDateTime | `:temporal` → numerical with calendar-aware ticks |
;;
;; A categorical column produces a band scale with string domain values.
;; Compare:

(def animals
  (tc/dataset {:animal ["cat" "dog" "bird" "fish"]
               :count [12 8 15 5]}))

(def bar-views
  (-> animals
      (sk/view :animal :count)
      (sk/lay (sk/value-bar))))

(kind/pprint (sk/sketch bar-views))

(kind/test-last [(fn [sk] (let [p (first (:panels sk))]
                            (and (= ["cat" "dog" "bird" "fish"] (:x-domain p))
                                 (true? (:categorical? (:x-ticks p))))))])

(sk/plot bar-views)

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The x-domain is `["cat" "dog" "bird" "fish"]` — strings in order of
;; appearance. The ticks have `:categorical? true`. The y-domain starts
;; at zero because this is a bar chart.

;; ## Mark and Stat Inference
;;
;; When you provide only a column (no explicit mark), napkinsketch
;; infers the mark and stat from the column types.
;;
;; | Columns | Inferred mark | Inferred stat |
;; |:--------|:--------------|:--------------|
;; | one numerical | `:bar` | `:bin` (histogram) |
;; | one categorical | `:rect` | `:count` |
;; | two numerical | `:point` | `:identity` |
;;
;; A single numerical column:

(def hist-views
  (-> five-points
      (sk/view :x)))

(kind/pprint (sk/sketch hist-views))

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (= :bar (:mark layer))))])

(sk/plot hist-views)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The layer mark is `:bar` — inferred because a single numerical column
;; means histogram. The layer data contains `:bins` with `:x0`, `:x1`,
;; `:count` — the result of the `:bin` stat.
;;
;; A single categorical column:

(def count-views
  (-> animals
      (sk/view :animal)))

(kind/pprint (sk/sketch count-views))

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (= :rect (:mark layer))))])

(sk/plot count-views)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Mark is `:rect` with `:counts` — the `:count` stat tallied each category.

;; ## Color Resolution
;;
;; The `:color` parameter triggers three different behaviors.
;; Compare the sketches:

;; ### Column reference → grouped by palette

(def colored-views
  (-> (tc/dataset {:x [1 2 3 4 5 6]
                   :y [3 5 4 7 6 8]
                   :g ["a" "a" "a" "b" "b" "b"]})
      (sk/view :x :y)
      (sk/lay (sk/point {:color :g}))))

(kind/pprint (sk/sketch colored-views))

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (and (= 2 (count (:groups layer)))
                                 (some? (:legend sk)))))])

(sk/plot colored-views)

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Two groups under `:groups`, each with its own `:color` (RGBA),
;; `:xs`, `:ys`, and `:label`. A `:legend` appeared with two entries.
;; The `:layout` now has `:legend-w 100` — space reserved on the right.

;; ### Fixed color string → no grouping, no legend

(def fixed-color-views
  (-> five-points
      (sk/view :x :y)
      (sk/lay (sk/point {:color "#E74C3C"}))))

(kind/pprint (sk/sketch fixed-color-views))

(kind/test-last [(fn [sk] (and (nil? (:legend sk))
                               (let [c (:color (first (:groups (first (:layers (first (:panels sk)))))))]
                                 (> (first c) 0.8))))])

(sk/plot fixed-color-views)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; One group with red RGBA values. No `:legend`, `:legend-w` is 0.
;; The hex string was converted to `[0.906 0.298 0.235 1.0]`.

;; ### No color → default gray

;; Look back at the first scatter sketch above — the single group
;; has `:color [0.2 0.2 0.2 1.0]` (dark gray). No legend.

;; ## Domain Padding
;;
;; Numerical domains extend 5% beyond the data range so points
;; aren't clipped at the edges:

(kind/pprint
 (let [sk (sk/sketch scatter-views)
       p (first (:panels sk))]
   {:x-domain (:x-domain p)
    :data-range [1.0 5.0]
    :padding-each-side (* 0.05 (- 5.0 1.0))}))

(kind/test-last [(fn [m] (and (< (first (:x-domain m)) 1.0)
                              (> (second (:x-domain m)) 5.0)))])

;; The domain `[0.8, 5.2]` = data range `[1.0, 5.0]` ± 0.2 (5% of 4.0).
;;
;; Bar chart y-domains always include zero:

(kind/pprint
 (let [sk (sk/sketch bar-views)
       p (first (:panels sk))]
   {:y-domain (:y-domain p)}))

(kind/test-last [(fn [m] (<= (first (:y-domain m)) 0))])

;; ## Axis Labels
;;
;; Labels come from column names. Underscores and hyphens become spaces:

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(kind/pprint
 (let [sk (-> iris
              (sk/view :sepal_length :sepal_width)
              (sk/lay (sk/point))
              sk/sketch)]
   {:x-label (:x-label sk)
    :y-label (:y-label sk)}))

(kind/test-last [(fn [m] (and (= "sepal length" (:x-label m))
                              (= "sepal width" (:y-label m))))])

;; Histograms suppress the y-label to avoid redundancy (x and y
;; reference the same column):

(kind/pprint
 (let [sk (sk/sketch (-> five-points (sk/view :x)))]
   {:x-label (:x-label sk)
    :y-label (:y-label sk)}))

(kind/test-last [(fn [m] (and (= "x" (:x-label m))
                              (nil? (:y-label m))))])

;; Explicit labels override inference:

(kind/pprint
 (let [sk (-> five-points
              (sk/view :x :y)
              (sk/lay (sk/point))
              (sk/labs {:x "Length (cm)" :y "Width (cm)"})
              sk/sketch)]
   {:x-label (:x-label sk)
    :y-label (:y-label sk)}))

(kind/test-last [(fn [m] (= "Length (cm)" (:x-label m)))])

;; ## Layout Adapts to Content
;;
;; The `:layout` map adjusts padding based on what elements are
;; present. Compare a bare plot to one with title, labels, and legend:

(kind/pprint
 (let [bare (sk/sketch scatter-views)
       full (-> (tc/dataset {:x [1 2 3 4 5 6]
                             :y [3 5 4 7 6 8]
                             :g ["a" "a" "a" "b" "b" "b"]})
                (sk/view :x :y)
                (sk/lay (sk/point {:color :g}))
                (sk/labs {:title "My Plot"})
                sk/sketch)]
   {:bare-layout (:layout bare)
    :bare-total-width (:total-width bare)
    :full-layout (:layout full)
    :full-total-width (:total-width full)}))

(kind/test-last [(fn [m] (and (zero? (get-in m [:bare-layout :title-pad]))
                              (pos? (get-in m [:full-layout :title-pad]))
                              (zero? (get-in m [:bare-layout :legend-w]))
                              (pos? (get-in m [:full-layout :legend-w]))
                              (> (:full-total-width m) (:bare-total-width m))))])

;; The bare plot has zero title padding and zero legend width.
;; The full plot adds padding for the title and 100px for the legend.
;; Total width grows accordingly.

;; ## Coordinate Flipping
;;
;; Setting `:coord :flip` swaps axes in the sketch. The layer data
;; stays the same — the panel-level domains and ticks are swapped.

(def normal-sk
  (sk/sketch
   (-> animals
       (sk/view :animal :count)
       (sk/lay (sk/value-bar)))))

(def flip-sk
  (sk/sketch
   (-> animals
       (sk/view :animal :count)
       (sk/lay (sk/value-bar))
       (sk/coord :flip))))

(kind/pprint
 (let [np (first (:panels normal-sk))
       fp (first (:panels flip-sk))]
   {:normal {:x-categorical? (:categorical? (:x-ticks np))
             :y-categorical? (:categorical? (:y-ticks np))}
    :flipped {:x-categorical? (:categorical? (:x-ticks fp))
              :y-categorical? (:categorical? (:y-ticks fp))}}))

(kind/test-last [(fn [m] (and (get-in m [:normal :x-categorical?])
                              (not (get-in m [:normal :y-categorical?]))
                              (not (get-in m [:flipped :x-categorical?]))
                              (get-in m [:flipped :y-categorical?])))])

(sk/plot (-> animals
             (sk/view :animal :count)
             (sk/lay (sk/value-bar))
             (sk/coord :flip)))

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The categorical axis moved from x to y.

;; ## Legend Inference
;;
;; A legend appears when a column is mapped to color. Examine the
;; legend in a colored sketch:

(kind/pprint (:legend (sk/sketch colored-views)))

(kind/test-last [(fn [leg] (and (= :g (:title leg))
                                (= 2 (count (:entries leg)))))])

;; Title is the column name. Each entry has a `:label` and `:color` (RGBA).
;;
;; No color mapping → no legend:

(kind/pprint (:legend (sk/sketch scatter-views)))

(:legend (sk/sketch scatter-views))

(kind/test-last [nil?])

;; Fixed color string → no legend:

(kind/pprint (:legend (sk/sketch fixed-color-views)))

(:legend (sk/sketch fixed-color-views))

(kind/test-last [nil?])

;; ## Multi-Layer Sketches
;;
;; When multiple layers share a panel, their domains are merged:

(def multi-views
  (-> five-points
      (sk/view :x :y)
      (sk/lay (sk/point) (sk/lm))))

(kind/pprint (sk/sketch multi-views))

(kind/test-last [(fn [sk] (let [p (first (:panels sk))]
                            (= 2 (count (:layers p)))))])

(sk/plot multi-views)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

;; Two layers — one `:point`, one `:line` — sharing the same domain.
;; The `:line` layer has `:mark :line` and its groups contain
;; `:polyline-xs` and `:polyline-ys` — the regression curve.

;; ## Summary
;;
;; Every inference can be overridden:
;;
;; | Inferred from | Override with |
;; |:-------------|:-------------|
;; | Column dtype → scale type | `(sk/scale views :x :log)` |
;; | Data extent → domain | `(sk/scale views :x {:domain [0 10]})` |
;; | Column name → axis label | `(sk/labs {:x "Custom Label"})` |
;; | No title → no padding | `:title "My Plot"` in options |
;; | Column types → mark | explicit mark constructor: `(sk/histogram)` |
;;
;; The sketch captures the result of all inference. When in doubt,
;; look at the sketch.
