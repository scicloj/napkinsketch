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

;; ## Inspecting the Sketch
;;
;; Every call to `sk/sketch` returns a plain Clojure map: the **sketch**.
;; It contains everything needed to render a plot — domains, ticks,
;; scales, layers with positioned data, legend, layout dimensions.
;;
;; To understand what Napkinsketch inferred, look at the sketch.

(def five-points
  {:x [1.0 2.0 3.0 4.0 5.0]
   :y [2.1 4.3 3.0 5.2 4.8]})

(def scatter-views
  (-> five-points
      (sk/lay-point :x :y)))

;; Here is the full sketch:

(sk/sketch scatter-views)

(kind/test-last [(fn [sk] (and (= :single (:layout-type sk))
                               (= 1 (count (:panels sk)))
                               (= "x" (:x-label sk))
                               (= "y" (:y-label sk))
                               (nil? (:legend sk))
                               (zero? (get-in sk [:layout :legend-w]))
                               (let [p (first (:panels sk))
                                     g (first (:groups (first (:layers p))))]
                                 (and (= :linear (get-in p [:x-scale :type]))
                                      (= 1 (count (:groups (first (:layers p)))))
                                      (= [0.2 0.2 0.2 1.0] (:color g))))))])

;; And the resulting plot:

scatter-views

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
;; - The single layer has `:mark :point` and a single `:groups` entry with all 5 data
;;   points, colored in default gray `[0.2 0.2 0.2 1.0]`

;; ## Column Type Detection
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
  {:animal ["cat" "dog" "bird" "fish"]
   :count [12 8 15 5]})

(def bar-views
  (-> animals
      (sk/lay-value-bar :animal :count)))

(sk/sketch bar-views)

(kind/test-last [(fn [sk] (let [p (first (:panels sk))]
                            (and (= ["cat" "dog" "bird" "fish"] (:x-domain p))
                                 (true? (:categorical? (:x-ticks p))))))])

bar-views

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The x-domain is `["cat" "dog" "bird" "fish"]` — strings in order of
;; appearance. The ticks have `:categorical? true`. The y-domain starts
;; at zero because this is a bar chart.

;; Temporal columns — dates are detected and converted to
;; epoch-milliseconds internally, with calendar-aware tick labels:

(let [sk (-> {:date [(java.time.LocalDate/of 2024 1 1)
                     (java.time.LocalDate/of 2024 6 1)
                     (java.time.LocalDate/of 2024 12 1)]
              :val [10 25 18]}
             (sk/lay-point :date :val)             sk/sketch)
      p (first (:panels sk))]
  {:x-domain-numeric? (number? (first (:x-domain p)))
   :tick-labels (:labels (:x-ticks p))})

(kind/test-last [(fn [m] (and (true? (:x-domain-numeric? m))
                              (not-empty (:tick-labels m))))])

;; The x-domain contains epoch-millisecond numbers, but the tick
;; labels show human-readable dates. The system detected the
;; `LocalDate` type, converted to numbers for plotting, and
;; preserved the temporal extent for formatting.

;; ## Mark and Stat Inference
;;
;; `sk/lay-point`, `sk/lay-histogram`, and similar functions each add a layer with a
;; **method** — a bundle of mark, stat, and position. When you
;; add a layer, the method's stat takes precedence over
;; column-type inference.
;;
;; When you provide only a column (no explicit method), Napkinsketch
;; infers the method (mark + stat) from the column types.
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

(sk/sketch hist-views)

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (= :bar (:mark layer))))])

hist-views

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The layer mark is `:bar` — inferred because a single numerical column
;; means histogram. The layer data contains `:bins` with `:x0`, `:x1`,
;; `:count` — the result of the `:bin` stat.
;;
;; A single categorical column:

(def count-views
  (-> animals
      (sk/view :animal)))

(sk/sketch count-views)

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (= :rect (:mark layer))))])

count-views

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Mark is `:rect` with `:counts` — the `:count` stat tallied each category.

;; Mixed column types (categorical x, numerical y) also default to `:point`:

(let [sk (-> {:species ["a" "b" "c"] :val [10 20 15]}
             (sk/view :species :val)
             sk/sketch)
      layer (first (:layers (first (:panels sk))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :point m))])

;; ## Color Resolution
;;
;; The `:color` parameter triggers three different behaviors
;; depending on what you pass. Compare the sketches:

;; ### Column reference → colored by palette

(def colored-views
  (-> {:x [1 2 3 4 5 6]
       :y [3 5 4 7 6 8]
       :g ["a" "a" "a" "b" "b" "b"]}
      (sk/lay-point :x :y {:color :g})))

(sk/sketch colored-views)

(kind/test-last [(fn [sk] (let [layer (first (:layers (first (:panels sk))))]
                            (and (= 2 (count (:groups layer)))
                                 (some? (:legend sk))
                                 (= 100 (get-in sk [:layout :legend-w])))))])

colored-views

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Two entries in `:groups`, each with its own `:color` (RGBA),
;; `:xs`, `:ys`, and `:label`. A `:legend` appeared with two entries.
;; The `:layout` now has `:legend-w 100` — space reserved on the right.
;;
;; Why two entries? Because `:g` is a categorical column. The next
;; section explores this mechanism in detail.

;; ### Fixed color string → single color, no legend

(def fixed-color-views
  (-> five-points
      (sk/lay-point :x :y {:color "#E74C3C"})))

(sk/sketch fixed-color-views)

(kind/test-last [(fn [sk] (and (nil? (:legend sk))
                               (zero? (get-in sk [:layout :legend-w]))
                               (let [layer (first (:layers (first (:panels sk))))
                                     c (:color (first (:groups layer)))]
                                 (and (= 1 (count (:groups layer)))
                                      (> (nth c 0) 0.85)
                                      (< (nth c 1) 0.35)
                                      (< (nth c 2) 0.30)
                                      (== 1.0 (nth c 3))))))])

fixed-color-views

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; A single `:groups` entry with red RGBA values. No `:legend`,
;; `:legend-w` is 0. The hex string was converted to
;; `[0.906 0.298 0.235 1.0]`.

;; ### Named colors and string disambiguation
;;
;; CSS color names like `"red"` and `"steelblue"` also work as
;; fixed colors:

(-> five-points
    (sk/lay-point :x :y {:color "steelblue"}))

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; This raises a question: since `:color` also accepts column names
;; as strings (like `"species"`), how does the system decide whether
;; `"red"` means the column `:red` or the color red?
;;
;; The rule is: **check the dataset first**. If the string matches
;; a column name in the dataset, it is treated as a column reference.
;; Otherwise, it is treated as a color value — first trying hex
;; parsing, then CSS color name lookup.
;;
;; Here is the full resolution order for a string `:color` value:
;;
;; 1. If the string matches a dataset column → column reference (grouping)
;; 2. If it starts with `#` → hex color (`"#E74C3C"`, `"#F00"`)
;; 3. If it parses as hex without `#` → hex color (`"00FF00"`)
;; 4. If it matches a CSS color name → named color (`"red"`, `"steelblue"`)
;; 5. Otherwise → error with a helpful message
;;
;; In practice, ambiguity is rare. Column names like `"species"` or
;; `"temperature"` are not valid CSS colors, and color names like
;; `"red"` are unlikely column names. When true ambiguity exists,
;; use a keyword for the column (`:red`) or a hex string for the
;; color (`"#FF0000"`).

;; Verify: `"red"` is a fixed color when the dataset has no `red` column:

(let [sk (-> five-points
             (sk/lay-point :x :y {:color "red"})             sk/sketch)]
  {:legend (:legend sk)
   :color (:color (first (:groups (first (:layers (first (:panels sk)))))))})

(kind/test-last [(fn [m] (and (nil? (:legend m))
                              (> (first (:color m)) 0.9)))])

;; No legend, red RGBA — treated as a fixed color, not a column.

;; ### No color → default gray

;; Look back at the first scatter sketch above — its single `:groups`
;; entry has `:color [0.2 0.2 0.2 1.0]` (dark gray). No legend.

;; ## Grouping
;;
;; The `:groups` entries you saw above reflect a key concept:
;; **grouping** controls how data is split into independent subsets.
;; Each group gets its own visual elements — its own set of points,
;; its own regression line, its own density curve, its own bar in a
;; dodged layout.
;;
;; Grouping can be **derived** (from a categorical `:color` mapping)
;; or **explicit** (via the `:group` aesthetic).

;; ### Categorical color implies grouping
;;
;; When `:color` maps to a categorical column (as with `colored-views`
;; above), the data is split into one group per category. Each group
;; gets a distinct palette color and a legend entry:

(let [sk (sk/sketch colored-views)
      layer (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer))
   :group-labels (mapv :label (:groups layer))
   :has-legend? (some? (:legend sk))})

(kind/test-last [(fn [m] (and (= 2 (:group-count m))
                              (= ["a" "b"] (:group-labels m))
                              (true? (:has-legend? m))))])

;; Two groups, two legend entries. Each group has its own `:xs`,
;; `:ys`, and `:color`.

;; ### Numeric color does not create groups
;;
;; When `:color` maps to a numerical column, data is NOT split.
;; Instead, each point gets an individual color from a continuous
;; gradient. There is one group, and the legend is continuous.

(let [sk (-> {:x [1 2 3 4 5]
              :y [2 4 3 5 4]
              :val [10 20 30 40 50]}
             (sk/lay-point :x :y {:color :val})             sk/sketch)
      layer (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer))
   :legend-type (:type (:legend sk))})

(kind/test-last [(fn [m] (and (= 1 (:group-count m))
                              (= :continuous (:legend-type m))))])

;; One group, continuous legend. No splitting occurred — the color
;; is a visual encoding, not a grouping variable.

;; ### Explicit grouping with `:group`
;;
;; The `:group` aesthetic splits data into groups without
;; assigning distinct colors or creating a legend. This is useful
;; when you want per-group statistics but uniform appearance.

(def grouped-data
  {:x [1 2 3 4 5 6]
   :y [3 5 4 7 6 8]
   :g ["a" "a" "a" "b" "b" "b"]})

(let [sk (-> grouped-data
             (sk/lay-point :x :y {:group :g})             sk/sketch)
      layer (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer))
   :has-legend? (some? (:legend sk))})

(kind/test-last [(fn [m] (and (= 2 (:group-count m))
                              (false? (:has-legend? m))))])

;; Two groups, but no legend and no color differentiation.
;; Use `:group` when you need separate statistical fits but
;; want a uniform visual style.

;; ### What grouping affects
;;
;; Grouping determines how statistical transformations operate.
;; Without grouping, `sk/lay-lm` fits one regression line through
;; all the data. With grouping, it fits one line per group.

;; One regression line — no grouping:

(-> grouped-data
    (sk/view :x :y)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 1 (:lines s)))))])

;; Two regression lines — grouped by color:

(-> grouped-data
    (sk/view :x :y)
    (sk/lay-point {:color :g})
    (sk/lay-lm {:color :g}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; The same applies to other statistics: density curves, LOESS
;; smoothers, boxplots, and dodge/stack positioning all operate
;; per group.

;; ## Domain Padding
;;
;; Numerical domains extend 5% beyond the data range so points
;; aren't clipped at the edges:

(let [sk (sk/sketch scatter-views)
      p (first (:panels sk))]
  {:x-domain (:x-domain p)
   :data-range [1.0 5.0]
   :padding-each-side (* 0.05 (- 5.0 1.0))})

(kind/test-last [(fn [m] (and (== 0.8 (first (:x-domain m)))
                              (== 5.2 (second (:x-domain m)))
                              (== 0.2 (:padding-each-side m))))])

;; The domain `[0.8, 5.2]` = data range `[1.0, 5.0]` ± 0.2 (5% of 4.0).
;;
;; Bar chart y-domains always include zero:

(let [sk (sk/sketch bar-views)
      p (first (:panels sk))]
  {:y-domain (:y-domain p)})

(kind/test-last [(fn [m] (<= (first (:y-domain m)) 0))])

;; Percentage-filled layers normalize the y-domain to `[0.0, 1.0]`:

(let [fill-sk (-> {:x ["a" "a" "b" "b"]
                   :g ["m" "n" "m" "n"]}
                  (sk/lay-stacked-bar-fill :x {:color :g})                  sk/sketch)
      p (first (:panels fill-sk))]
  (:y-domain p))

(kind/test-last [(fn [d] (and (= 0.0 (first d))
                              (= 1.0 (second d))))])

;; The y-domain is exactly `[0.0, 1.0]` — each category sums to 100%.

;; ## Axis Labels
;;
;; Labels come from column names. Underscores and hyphens become spaces:

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width)             sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (and (= "sepal length" (:x-label m))
                              (= "sepal width" (:y-label m))))])

;; When only one column is specified, the y-axis shows computed counts.
;; The system omits the y-label since it would repeat the column name:

(let [sk (-> five-points (sk/view :x) sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (and (= "x" (:x-label m))
                              (nil? (:y-label m))))])

;; Explicit labels override inference:

(let [sk (-> five-points
             (sk/lay-point :x :y)             (sk/labs {:x "Length (cm)" :y "Width (cm)"})
             sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (= "Length (cm)" (:x-label m)))])

;; ## Automatic Layout
;;
;; The `:layout` map adjusts padding based on what elements are
;; present. Compare a bare plot to one with title, labels, and legend:

(let [bare (sk/sketch scatter-views)
      full (-> {:x [1 2 3 4 5 6]
                :y [3 5 4 7 6 8]
                :g ["a" "a" "a" "b" "b" "b"]}
               (sk/lay-point :x :y {:color :g})               (sk/labs {:title "My Plot"})
               sk/sketch)]
  {:bare-layout (:layout bare)
   :bare-total-width (:total-width bare)
   :full-layout (:layout full)
   :full-total-width (:total-width full)})

(kind/test-last [(fn [m] (and (zero? (get-in m [:bare-layout :title-pad]))
                              (pos? (get-in m [:full-layout :title-pad]))
                              (zero? (get-in m [:bare-layout :legend-w]))
                              (= 100 (get-in m [:full-layout :legend-w]))
                              (> (:full-total-width m) (:bare-total-width m))))])

;; The bare plot has zero title padding and zero legend width.
;; The full plot adds padding for the title and 100px for the legend.
;; Total width grows accordingly.

;; ## Coordinate Flipping
;;
;; Setting `:coord :flip` swaps axes in the sketch. The layer data
;; stays the same — the panel-level domains and ticks are swapped.

(def normal-sk
  (-> animals
      (sk/lay-value-bar :animal :count)      sk/sketch))

(def flip-sk
  (-> animals
      (sk/lay-value-bar :animal :count)      (sk/coord :flip)
      sk/sketch))

(let [np (first (:panels normal-sk))
      fp (first (:panels flip-sk))]
  {:normal {:x-categorical? (:categorical? (:x-ticks np))
            :y-categorical? (:categorical? (:y-ticks np))}
   :flipped {:x-categorical? (:categorical? (:x-ticks fp))
             :y-categorical? (:categorical? (:y-ticks fp))}})

(kind/test-last [(fn [m] (and (get-in m [:normal :x-categorical?])
                              (not (get-in m [:normal :y-categorical?]))
                              (not (get-in m [:flipped :x-categorical?]))
                              (get-in m [:flipped :y-categorical?])))])

(-> animals
    (sk/lay-value-bar :animal :count)    (sk/coord :flip))

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The categorical axis moved from x to y.

;; Labels are also swapped — the x-label and y-label follow their
;; visual axis, not the data axis:

(let [sk (-> five-points
             (sk/lay-point :x :y)             (sk/coord :flip)
             sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (and (= "y" (:x-label m))
                              (= "x" (:y-label m))))])

;; After flipping, the visual x-axis shows "y" and the visual y-axis
;; shows "x" — labels track the visual axes.

;; ## Legend Inference
;;
;; A legend appears when a column is mapped to color. Examine the
;; legend in a colored sketch:

(:legend (sk/sketch colored-views))
(kind/test-last [(fn [leg] (and (= :g (:title leg))
                                (= 2 (count (:entries leg)))))])

;; Title is the column name. Each entry has a `:label` and `:color` (RGBA).
;;
;; No color mapping → no legend:

(:legend (sk/sketch scatter-views))

(kind/test-last [nil?])

;; Fixed color string → no legend:

(:legend (sk/sketch fixed-color-views))

(kind/test-last [nil?])

;; ## Multi-Layer Sketches
;;
;; When multiple layers share a panel, their domains are merged:

(def multi-views
  (-> five-points
      (sk/view :x :y)
      sk/lay-point
      sk/lay-lm))

(sk/sketch multi-views)

(kind/test-last [(fn [sk] (let [p (first (:panels sk))]
                            (= 2 (count (:layers p)))))])

multi-views

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
;; | Column types → method | explicit method: `(method/histogram)` or `sk/lay-histogram` |
;; | Temporal detection → epoch-ms | `(sk/scale views :x {:domain [min max]})` |
;; | Fill domain → [0, 1] | `(sk/scale views :y {:domain [0 2]})` |
;; | Flip swaps labels | `(sk/labs {:x "keep-this"})` overrides |
;;
;; The sketch captures the result of all inference. When in doubt,
;; look at the sketch.
