;; # Inference Rules
;;
;; napkinsketch infers many parameters automatically so you can write
;; less and get reasonable defaults. This notebook catalogs every
;; inference rule, shows what triggers it, and demonstrates the
;; before/after with concrete examples.

(ns napkinsketch-book.inference-rules
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]))

;; ## Overview
;;
;; When you write a plotting expression like:
;;
;; ```clojure
;; (-> iris (sk/view :sepal_length) (sk/lay (sk/point)) sk/plot)
;; ```
;;
;; napkinsketch fills in many blanks: What mark? What statistic?
;; What domains? What axis labels? What colors? These are
;; **inference rules** — defaults derived from the data and context.

^:kindly/hide-code
(kind/mermaid "
graph TD
  INPUT[\"User Input<br/>(data + columns + mark)\"] --> CT[\"Column Types<br/>numerical / categorical\"]
  CT --> MARK[\"Default Mark<br/>point / bar / rect\"]
  CT --> STAT[\"Default Stat<br/>identity / bin / count\"]
  CT --> SCALE[\"Scale Type<br/>linear / categorical\"]
  INPUT --> COLOR[\"Color Resolution<br/>column → groups + palette<br/>string → fixed RGBA<br/>nil → default gray\"]
  INPUT --> GROUP[\"Grouping<br/>from color column\"]
  STAT --> DOMAIN[\"Domain<br/>from data extent + padding\"]
  SCALE --> TICKS[\"Tick Values + Labels<br/>from domain + pixel range\"]
  DOMAIN --> LABEL[\"Axis Labels<br/>from column names\"]
  DOMAIN --> LAYOUT[\"Layout<br/>padding from presence of title/labels/legend\"]
  style INPUT fill:#e8f5e9
  style CT fill:#fff3e0
  style MARK fill:#fff3e0
  style STAT fill:#fff3e0
  style SCALE fill:#fff3e0
  style COLOR fill:#fff3e0
  style GROUP fill:#fff3e0
  style DOMAIN fill:#e3f2fd
  style TICKS fill:#e3f2fd
  style LABEL fill:#e3f2fd
  style LAYOUT fill:#e3f2fd
")

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; ## Column Type Detection
;;
;; The first inference: is each column **numerical** or **categorical**?
;; This drives almost every other decision.
;;
;; | Column dtype | Inferred type |
;; |:-------------|:--------------|
;; | float32, float64, int8–int64 | `:numerical` |
;; | string, keyword, boolean | `:categorical` |
;; | other (sampled) | whichever fits the first 100 values |
;;
;; For iris, `:sepal_length` is numerical and `:species` is categorical:

(def scatter-sk
  (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width})]))

(let [p (first (:panels scatter-sk))]
  {:x-domain-kind (if (number? (first (:x-domain p))) :numerical :categorical)
   :y-domain-kind (if (number? (first (:y-domain p))) :numerical :categorical)
   :x-scale-type (get-in p [:x-scale :type])
   :y-scale-type (get-in p [:y-scale :type])})

(kind/test-last [(fn [m] (and (= :numerical (:x-domain-kind m))
                              (= :numerical (:y-domain-kind m))
                              (= :linear (:x-scale-type m))))])

;; A categorical column produces a band scale:

(def bar-sk (sk/sketch [(sk/bar {:data iris :x :species})]))

;; The x-domain contains categorical values, and the ticks are categorical:

(let [p (first (:panels bar-sk))]
  {:x-domain (:x-domain p)
   :x-ticks-categorical? (:categorical? (:x-ticks p))})

(kind/test-last [(fn [m] (and (every? string? (:x-domain m))
                              (:x-ticks-categorical? m)))])


;; ## Mark and Stat Inference
;;
;; When you don't specify a mark, napkinsketch infers one from
;; the column types and structure.
;;
;; | x column | y column | Inferred mark | Inferred stat |
;; |:---------|:---------|:--------------|:--------------|
;; | numerical | numerical | `:point` | `:identity` |
;; | numerical | (same as x) | `:bar` | `:bin` (histogram) |
;; | categorical | (same as x) | `:rect` | `:count` |
;; | numerical | categorical | `:point` | `:identity` |
;;
;; The mark constructors override these defaults. For example,
;; `(sk/histogram)` forces `:mark :bar, :stat :bin` regardless
;; of column types.
;;
;; Here's the inference in action — a single keyword becomes a histogram:

;; Passing just `:sepal_length` means x = y = :sepal_length (diagonal).
;; Diagonal + numerical → histogram:

(def hist-sk (sk/sketch
              (-> iris (sk/view :sepal_length) (sk/lay (sk/histogram)))))

(let [layer (first (:layers (first (:panels hist-sk))))]
  {:mark (:mark layer)})

(kind/test-last [(fn [m] (= :bar (:mark m)))])

;; And a categorical column with the same pattern → bar chart:

(def count-sk (sk/sketch
               (-> iris (sk/view :species) (sk/lay (sk/bar)))))

(let [layer (first (:layers (first (:panels count-sk))))]
  {:mark (:mark layer)})

(kind/test-last [(fn [m] (= :rect (:mark m)))])

;; ## Color Resolution
;;
;; The `:color` parameter triggers three different behaviors depending
;; on what you pass.

;; ### Column reference → grouped by palette
;;
;; A keyword like `:species` splits the data into groups, each assigned
;; a color from the palette.

(def colored-sk
  (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})]))

(let [layer (first (:layers (first (:panels colored-sk))))]
  (mapv (fn [g] {:color (:color g) :n (count (:xs g))})
        (:groups layer)))

(kind/test-last [(fn [gs] (and (= 3 (count gs))
                               (every? #(= 4 (count (:color %))) gs)))])

;; Three groups, each with a distinct RGBA color from the palette.

;; ### Fixed color string → single RGBA
;;
;; A hex string is converted to RGBA and applied to all points.

(def fixed-sk
  (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width
                         :color "#E74C3C"})]))

(let [g (first (:groups (first (:layers (first (:panels fixed-sk))))))]
  (:color g))

(kind/test-last [(fn [c] (and (= 4 (count c))
                              (> (first c) 0.8)))])

;; One group with red color. No legend generated:

(:legend fixed-sk)

(kind/test-last [nil?])

;; ### No color → default gray
;;
;; When no color is specified, a single group uses the default color.

(let [g (first (:groups (first (:layers (first (:panels scatter-sk))))))]
  (:color g))

(kind/test-last [(fn [c] (= 4 (count c)))])

;; ## Grouping
;;
;; Grouping is inferred from the color column. When `:color` references
;; a categorical column, data is split by its distinct values.

(def grp-sk
  (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width :color :species})
              (sk/lm {:data iris :x :sepal_length :y :sepal_width :color :species})]))

;; Both layers get 3 groups (one per species):

(mapv (fn [layer]
        {:mark (:mark layer)
         :n-groups (count (:groups layer))})
      (:layers (first (:panels grp-sk))))

(kind/test-last [(fn [ls] (and (= 2 (count ls))
                               (every? #(= 3 (:n-groups %)) ls)))])

;; ## Domain Inference
;;
;; Domains are computed from the data extent, with padding.
;;
;; | Data type | Domain |
;; |:----------|:-------|
;; | Numerical | `[min - pad, max + pad]` where pad = 5% of range |
;; | Categorical | distinct values in order of appearance |
;; | Bar/rect y-axis | always starts at 0 |
;;
;; Numerical domain with padding:

(let [p (first (:panels scatter-sk))]
  {:x-domain (:x-domain p)
   :actual-min (reduce min (map :sepal_length (tc/rows iris :as-maps)))
   :actual-max (reduce max (map :sepal_length (tc/rows iris :as-maps)))})

(kind/test-last [(fn [m] (and (< (first (:x-domain m)) (:actual-min m))
                              (> (second (:x-domain m)) (:actual-max m))))])

;; The domain extends slightly beyond the actual data — that's the
;; 5% padding, so points aren't clipped at the edges.
;;
;; Categorical domain — distinct values:

(let [p (first (:panels bar-sk))]
  (:x-domain p))

(kind/test-last [(fn [d] (= 3 (count d)))])

;; Bar chart y-domain starts at or below zero (padding may extend below):

(let [p (first (:panels bar-sk))]
  (first (:y-domain p)))

(kind/test-last [(fn [v] (<= v 0))])

;; ### Multi-layer domain merging
;;
;; When multiple layers share a panel, their domains are merged:

(let [p (first (:panels grp-sk))]
  {:x-domain (:x-domain p)
   :y-domain (:y-domain p)})

(kind/test-last [(fn [m] (and (< (first (:x-domain m)) (second (:x-domain m)))
                              (< (first (:y-domain m)) (second (:y-domain m)))))])

;; Both the point layer and the regression layer contribute to
;; the same domain range.

;; ### Stacked bar domain
;;
;; For stacked bars, the y-domain is the maximum stack height
;; (sum of counts), not the maximum single count:

(def stacked-sk
  (sk/sketch [(sk/stacked-bar {:data iris :x :species :color :species})]))

(let [p (first (:panels stacked-sk))]
  {:y-max (second (:y-domain p))})

(kind/test-last [(fn [m] (>= (:y-max m) 50))])

;; ## Axis Label Inference
;;
;; Axis labels are derived from column names. The keyword is converted
;; to a readable string: underscores and hyphens become spaces.
;;
;; | Column keyword | Inferred label |
;; |:---------------|:---------------|
;; | `:sepal_length` | `"sepal length"` |
;; | `:body-mass-g` | `"body mass g"` |
;; | `:x` | `"x"` |

(:x-label scatter-sk)

(kind/test-last [(fn [l] (= "sepal length" l))])

(:y-label scatter-sk)

(kind/test-last [(fn [l] (= "sepal width" l))])

;; ### Y-label suppression
;;
;; When x and y reference the same column (histograms), the y-label
;; is suppressed to avoid redundancy:

(:y-label hist-sk)

(kind/test-last [nil?])

;; ### Explicit labels override inference

(def custom-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width})]
                          {:x-label "Length (cm)" :y-label "Width (cm)"}))

(:x-label custom-sk)

(kind/test-last [(fn [l] (= "Length (cm)" l))])

;; ## Tick Inference
;;
;; Tick positions and labels are computed from the domain and available
;; pixel space. The algorithm:
;;
;; - Numerical axes: wadogo chooses "nice" round numbers, spaced
;;   according to `:tick-spacing-x` / `:tick-spacing-y` in the config
;;
;; - Categorical axes: one tick per category

(let [p (first (:panels scatter-sk))]
  {:n-x-ticks (count (:values (:x-ticks p)))
   :x-categorical? (:categorical? (:x-ticks p))
   :first-x-tick (first (:values (:x-ticks p)))
   :first-x-label (first (:labels (:x-ticks p)))})

(kind/test-last [(fn [m] (and (> (:n-x-ticks m) 2)
                              (not (:x-categorical? m))
                              (number? (:first-x-tick m))
                              (string? (:first-x-label m))))])

;; Categorical ticks — one per category:

(let [p (first (:panels bar-sk))]
  {:values (:values (:x-ticks p))
   :labels (:labels (:x-ticks p))
   :categorical? (:categorical? (:x-ticks p))})

(kind/test-last [(fn [m] (and (:categorical? m)
                              (= (count (:values m)) (count (:labels m)))))])

;; ## Layout Inference
;;
;; The layout computes padding based on which elements are present.
;;
;; | Element | Present? | Padding added |
;; |:--------|:---------|:--------------|
;; | Title | yes | `title-pad` > 0 |
;; | Title | no | `title-pad` = 0 |
;; | X-label | yes | `x-label-pad` > 0 |
;; | Y-label | yes | `y-label-pad` > 0 |
;; | Legend | yes | `legend-w` > 0 |
;; | Legend | no | `legend-w` = 0 |

;; Without title, no title padding:

(:layout scatter-sk)

(kind/test-last [(fn [lay] (zero? (:title-pad lay)))])

;; With title, title padding is added:

(def titled-sk (sk/sketch [(sk/point {:data iris :x :sepal_length :y :sepal_width})]
                          {:title "My Plot"}))

(:layout titled-sk)

(kind/test-last [(fn [lay] (pos? (:title-pad lay)))])

;; Legend width is added only when color mapping is used:

(:layout colored-sk)

(kind/test-last [(fn [lay] (pos? (:legend-w lay)))])

(:layout scatter-sk)

(kind/test-last [(fn [lay] (zero? (:legend-w lay)))])

;; Total dimensions follow from the base size plus padding:

(select-keys scatter-sk [:width :height :total-width :total-height])

(kind/test-last [(fn [m] (and (>= (:total-width m) (:width m))
                              (>= (:total-height m) (:height m))))])

;; ## Coordinate Flipping
;;
;; Setting `:coord :flip` swaps the x and y domains and scale specs.
;; The layer data stays the same — only the panel-level mapping changes.

(def normal-sk (sk/sketch [(sk/bar {:data iris :x :species})]))
(def flip-sk (sk/sketch [(-> (sk/bar {:data iris :x :species})
                             (assoc :coord :flip))]))

(let [np (first (:panels normal-sk))
      fp (first (:panels flip-sk))]
  {:normal-x-categorical? (:categorical? (:x-ticks np))
   :normal-y-categorical? (:categorical? (:y-ticks np))
   :flipped-x-categorical? (:categorical? (:x-ticks fp))
   :flipped-y-categorical? (:categorical? (:y-ticks fp))})

(kind/test-last [(fn [m] (and (:normal-x-categorical? m)
                              (not (:normal-y-categorical? m))
                              (not (:flipped-x-categorical? m))
                              (:flipped-y-categorical? m)))])

;; The categorical axis moved from x to y. The layer data is unchanged;
;; the renderer reads `:coord :flip` and swaps axes during rendering.

;; ## Legend Inference
;;
;; A legend appears when and only when a column is mapped to color.
;;
;; | Color mapping | Legend |
;; |:-------------|:-------|
;; | Column keyword (e.g. `:species`) | title = column name, one entry per distinct value |
;; | Fixed string (e.g. `"#E74C3C"`) | no legend |
;; | None | no legend |

(:legend colored-sk)

(kind/test-last [(fn [leg] (and (= :species (:title leg))
                                (= 3 (count (:entries leg)))))])

(:legend fixed-sk)

(kind/test-last [nil?])

(:legend scatter-sk)

(kind/test-last [nil?])

;; ## Summary
;;
;; Every inference can be overridden by the user:
;;
;; | Inferred from | Override with |
;; |:-------------|:-------------|
;; | Column dtype → scale type | `(sk/scale views :x :log)` |
;; | Data extent → domain | `(sk/scale views :x {:domain [0 10]})` |
;; | Column name → axis label | `{:x-label "Custom Label"}` in opts |
;; | No title → no padding | `{:title "My Plot"}` in opts |
;; | Column → mark type | explicit mark constructor: `(sk/histogram)` |
;;
;; The design principle: **sensible defaults, explicit overrides**.
;; The sketch captures the result of all inference — inspecting it
;; with `sk/sketch` shows exactly what was inferred.
