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
   [scicloj.napkinsketch.api :as sk]
   ;; Shared datasets
   [napkinsketch-book.datasets :as data]))

;; ## What Gets Inferred
;;
;; When you write `(-> data (sk/lay-point :x :y))` — or even just
;; `(sk/lay-point data)` — the library fills
;; in everything needed to render a plot. Here is the full list of
;; inference steps, in the order they happen:
;;
;; 1. **Column selection** — which columns map to x, y, and color (inferred from dataset shape when omitted)
;; 2. **Column types** — numerical, categorical, or temporal
;; 3. **Aesthetic resolution** — is `:color` a column reference, a hex string, or a CSS name?
;; 4. **Grouping** — which column(s) split data into subsets
;; 5. **Method** — which mark and stat to use (scatter, histogram, bar, ...)
;; 6. **Domains** — data extent for each axis, with padding
;; 7. **Ticks** — nice round values and formatted labels
;; 8. **Axis labels** — derived from column names
;; 9. **Legend** — type, entries, and layout space
;; 10. **Layout** — single panel, facet grid, or multi-variable
;; 11. **Coordinate transform** — cartesian, flip, or polar
;;
;; Each rule has a sensible default and an explicit override.
;; The sections below demonstrate each rule with live examples.

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
                                      (= (scicloj.napkinsketch.impl.defaults/hex->rgba (:default-color (scicloj.napkinsketch.impl.defaults/config))) (:color g))))))])

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
;;   points, colored in the default color (steel blue)

;; ## Column Selection
;;
;; When column names are omitted, napkinsketch infers them from
;; the dataset shape:
;;
;; | Number of columns | Inferred mapping |
;; |:------------------|:-----------------|
;; | 1 | first → x |
;; | 2 | first → x, second → y |
;; | 3 | first → x, second → y, third → color |
;; | 4+ | error — specify columns explicitly |
;;
;; One column:

(-> {:values [1 2 3 4 5 6]}
    sk/lay-histogram)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Two columns:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Three columns — the third becomes `:color`:

(-> {:x [1 2 3 4] :y [4 5 6 7] :g ["a" "a" "b" "b"]}
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (some #{"a"} (:texts s)))))])

;; When you provide explicit columns, inference is skipped — you
;; are in full control:

(-> data/iris
    (sk/lay-point :petal_length :petal_width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Column Type Detection
;;
;; Once columns are selected, the next step is determining the type of each column: **numerical**, **categorical**,
;; or **temporal**? This determines the scale type, domain, tick style,
;; and the default mark.
;;
;; | Column dtype | Inferred type |
;; |:-------------|:--------------|
;; | float, int | `:numerical` |
;; | string, keyword, boolean, symbol, text | `:categorical` |
;; | LocalDate, LocalDateTime, Instant, java.util.Date | `:temporal` → numerical with calendar-aware ticks |
;;
;; Internally, `infer-column-types` in `view.clj` handles this step.
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

;; ### Temporal columns
;;
;; Dates are detected and converted to epoch-milliseconds internally,
;; with calendar-aware tick labels.
;; Clojure's `#inst` reader literal is a convenient way to write dates:

(let [sk (-> {:date [#inst "2024-01-01" #inst "2024-06-01" #inst "2024-12-01"]
              :val [10 25 18]}
             (sk/lay-point :date :val)
             sk/sketch)
      p (first (:panels sk))]
  {:x-domain-numeric? (number? (first (:x-domain p)))
   :tick-count (count (:values (:x-ticks p)))
   :first-tick-label (first (:labels (:x-ticks p)))})

(kind/test-last [(fn [m] (and (true? (:x-domain-numeric? m))
                              (= 10 (:tick-count m))
                              (= "Feb-01" (:first-tick-label m))))])

;; The x-domain contains epoch-millisecond numbers, but the 10 tick
;; labels show human-readable dates like `"Feb-01"`. Napkinsketch accepts
;; `java.util.Date` (from `#inst`), `LocalDate`, `LocalDateTime`,
;; and `Instant` — all are converted to epoch-milliseconds for
;; plotting, with calendar-aware tick formatting.

;; ## Aesthetic Resolution
;;
;; The `:color` parameter triggers different behaviors depending on
;; what you pass. Internally, `resolve-aesthetics` in `view.clj`
;; classifies each aesthetic channel (`:color`, `:size`, `:alpha`,
;; `:text`) as either a column reference or a fixed literal.

;; ### Column reference — colored by palette

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
;; `:xs`, `:ys`, and `:label`. A `:legend` appeared with 2 entries.
;; The `:layout` now has `:legend-w 100` — space reserved on the right.
;;
;; Why two entries? Because `:g` is a categorical column. The next
;; section explores this mechanism in detail.

;; ### Fixed color string — single color, no legend

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
             (sk/lay-point :x :y {:color "red"})
             sk/sketch)]
  {:legend (:legend sk)
   :color (:color (first (:groups (first (:layers (first (:panels sk)))))))})

(kind/test-last [(fn [m] (and (nil? (:legend m))
                              (> (first (:color m)) 0.9)))])

;; No legend, red RGBA — treated as a fixed color, not a column.

;; ### No color — default gray

;; Look back at the first scatter sketch above — its single `:groups`
;; entry has the default color (steel blue). No legend.

;; ## Grouping
;;
;; The `:groups` entries you saw above reflect a key concept:
;; **grouping** controls how data is split into independent subsets.
;; Each group gets its own visual elements — its own set of points,
;; its own regression line, its own density curve, its own bar in a
;; dodged layout.
;;
;; Internally, `infer-grouping` in `view.clj` builds the grouping
;; vector from explicit `:group` and categorical color.
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
;; gradient. There is one group, and the legend is continuous
;; with 20 pre-computed color stops.

(let [sk (-> {:x [1 2 3 4 5]
              :y [2 4 3 5 4]
              :val [10 20 30 40 50]}
             (sk/lay-point :x :y {:color :val})
             sk/sketch)
      layer (first (:layers (first (:panels sk))))]
  {:group-count (count (:groups layer))
   :legend-type (:type (:legend sk))
   :color-stops (count (:stops (:legend sk)))})

(kind/test-last [(fn [m] (and (= 1 (:group-count m))
                              (= :continuous (:legend-type m))
                              (= 20 (:color-stops m))))])

;; One group, continuous legend with 20 stops. No splitting occurred —
;; the color is a visual encoding, not a grouping variable.

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
             (sk/lay-point :x :y {:group :g})
             sk/sketch)
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
;; Without grouping, `sk/lay-lm` (linear model) fits one regression line through
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
    (sk/view :x :y {:color :g})
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 2 (:lines s)))))])

;; The same applies to other statistics: density curves, LOESS
;; smoothers, boxplots, and dodge/stack positioning all operate
;; per group.

;; ## Method Inference
;;
;; When you use `sk/view` without an explicit `sk/lay-*` call,
;; Napkinsketch infers the **method** — a mark + stat bundle —
;; from the column types. Internally, `infer-method` in `view.clj`
;; implements these rules:
;;
;; | Columns | Inferred mark | Inferred stat |
;; |:--------|:--------------|:--------------|
;; | one numerical | `:bar` | `:bin` (histogram) |
;; | one categorical | `:rect` | `:count` (bar chart) |
;; | two numerical | `:point` | `:identity` (scatter) |
;; | mixed (categorical + numerical) | `:point` | `:identity` (scatter) |
;;
;; When you use `sk/lay-point`, `sk/lay-histogram`, etc., the method's
;; stat takes precedence — column-type inference is bypassed.
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

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; Mark is `:rect` with `:counts` — the `:count` stat tallied each
;; of the 4 categories.
;;
;; Mixed column types (categorical x, numerical y) default to `:point`:

(let [sk (-> {:species ["a" "b" "c"] :val [10 20 15]}
             (sk/view :species :val)
             sk/sketch)
      layer (first (:layers (first (:panels sk))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :point m))])

;; ## Domain Inference
;;
;; Numerical domains extend 5% beyond the data range so points
;; aren't clipped at the edges. Internally, `pad-domain` in
;; `scale.clj` computes this padding.

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
;; Special domain rules apply in certain contexts:
;;
;; Bar chart y-domains always include zero:

(let [sk (sk/sketch bar-views)
      p (first (:panels sk))]
  {:y-domain (:y-domain p)})

(kind/test-last [(fn [m] (<= (first (:y-domain m)) 0))])

;; Percentage-filled layers normalize the y-domain to `[0.0, 1.0]`:

(let [fill-sk (-> {:x ["a" "a" "b" "b"]
                   :g ["m" "n" "m" "n"]}
                  (sk/lay-stacked-bar-fill :x {:color :g})
                  sk/sketch)
      p (first (:panels fill-sk))]
  (:y-domain p))

(kind/test-last [(fn [d] (and (== 0.0 (first d))
                              (== 1.0 (second d))))])

;; The y-domain is exactly `[0.0, 1.0]` — each category sums to 100%.

;; Multi-layer plots merge domains across layers — see
;; "Multi-Layer Sketches" below.

;; ## Tick Inference
;;
;; Once domains are computed, Napkinsketch selects "nice" round tick
;; values. The logic depends on the scale type:
;;
;; - **Linear** — wadogo selects ticks at round intervals (1, 2, 2.5, 5, ...)
;; - **Log** — ggplot2-style 1-2-5 nice numbers: powers of 10 when they
;;   give at least 3 ticks, otherwise intermediates at 1-2-5 or 1-2-3-5
;;   multiples per decade
;; - **Categorical** — tick at each category, in order of appearance
;; - **Temporal** — calendar-aware snapping (year, month, day, hour)
;;   with adaptive formatting
;;
;; Linear ticks for the scatter example:

(let [sk (sk/sketch scatter-views)
      p (first (:panels sk))]
  {:x-tick-values (:values (:x-ticks p))
   :x-tick-labels (:labels (:x-ticks p))})

(kind/test-last [(fn [m] (and (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0]
                                 (:x-tick-values m))
                              (= ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
                                 (:x-tick-labels m))))])

;; Nine ticks from 1.0 to 5.0 at 0.5 intervals — round and readable.
;;
;; Log ticks for a multi-decade range:

(let [sk (-> {:x [0.1 1.0 10.0 100.0 1000.0]
              :y [5 10 15 20 25]}
             (sk/lay-point :x :y)
             (sk/scale :x :log)
             sk/sketch)
      p (first (:panels sk))]
  {:tick-values (:values (:x-ticks p))
   :tick-labels (:labels (:x-ticks p))})

(kind/test-last [(fn [m] (and (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
                              (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))])

;; Five ticks at exact powers of 10 — no irrational intermediates.
;; Whole numbers display without decimals, sub-1 values use minimal
;; decimal places.

;; Categorical ticks match domain order:

(let [sk (sk/sketch bar-views)
      p (first (:panels sk))]
  (:values (:x-ticks p)))

(kind/test-last [(fn [v] (= ["cat" "dog" "bird" "fish"] v))])

;; ## Axis Label Inference
;;
;; Labels come from column names. Underscores and hyphens become spaces.
;; Internally, `resolve-labels` in `sketch.clj` handles this.

(def iris data/iris)

(let [sk (-> iris
             (sk/lay-point :sepal_length :sepal_width)
             sk/sketch)]
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
             (sk/lay-point :x :y)
             (sk/options {:x-label "Length (cm)" :y-label "Width (cm)"})
             sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (and (= "Length (cm)" (:x-label m))
                              (= "Width (cm)" (:y-label m))))])

;; ## Legend Inference
;;
;; A legend appears when a column is mapped to color. Internally,
;; `build-legend` in `sketch.clj` constructs the legend from
;; the collected color information. Three cases:
;;
;; Categorical color → discrete legend with one entry per category:

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

;; Numeric color → continuous legend (gradient bar):

(:legend (-> {:x [1 2 3] :y [4 5 6] :val [10 20 30]}
             (sk/lay-point :x :y {:color :val})
             sk/sketch))

(kind/test-last [(fn [leg] (and (= :continuous (:type leg))
                                (= 20 (count (:stops leg)))))])

;; ### Size Legend
;;
;; When `:size` maps to a numerical column, a size legend shows graduated
;; circles spanning the data range. Internally, `build-size-legend` in
;; `sketch.clj` generates five entries with proportional radii.

(:size-legend (-> {:x [1 2 3 4 5] :y [1 2 3 4 5] :s [10 20 30 40 50]}
                  (sk/lay-point :x :y {:size :s})
                  sk/sketch))

(kind/test-last [(fn [leg] (and (= :size (:type leg))
                                (= :s (:title leg))
                                (= 5 (count (:entries leg)))))])

;; Each entry has a `:value` and `:radius`. No size mapping → no size legend:

(:size-legend (sk/sketch scatter-views))

(kind/test-last [nil?])

;; ### Alpha Legend
;;
;; When `:alpha` maps to a numerical column, an alpha legend shows
;; graduated opacity squares. Internally, `build-alpha-legend` in
;; `sketch.clj` generates five entries with proportional opacity.

(:alpha-legend (-> {:x [1 2 3 4 5] :y [1 2 3 4 5] :a [0.1 0.3 0.5 0.7 0.9]}
                   (sk/lay-point :x :y {:alpha :a})
                   sk/sketch))

(kind/test-last [(fn [leg] (and (= :alpha (:type leg))
                                (= :a (:title leg))
                                (= 5 (count (:entries leg)))))])

;; No alpha mapping → no alpha legend:

(:alpha-legend (sk/sketch scatter-views))

(kind/test-last [nil?])

;; ## Layout Inference
;;
;; The `:layout` map adjusts padding based on what elements are
;; present. Internally, `compute-layout-dims` in `sketch.clj`
;; calculates the space needed for titles, labels, and legends.
;;
;; Compare a bare plot to one with title, labels, and legend:

(let [bare (sk/sketch scatter-views)
      full (-> {:x [1 2 3 4 5 6]
                :y [3 5 4 7 6 8]
                :g ["a" "a" "a" "b" "b" "b"]}
               (sk/lay-point :x :y {:color :g})
               (sk/options {:title "My Plot"})
               sk/sketch)]
  {:bare-title-pad (get-in bare [:layout :title-pad])
   :full-title-pad (get-in full [:layout :title-pad])
   :bare-legend-w (get-in bare [:layout :legend-w])
   :full-legend-w (get-in full [:layout :legend-w])})

(kind/test-last [(fn [m] (and (zero? (:bare-title-pad m))
                              (pos? (:full-title-pad m))
                              (zero? (:bare-legend-w m))
                              (= 100 (:full-legend-w m))))])

;; The bare plot has zero title padding and zero legend width.
;; The full plot adds padding for the title and 100 pixels for the legend.

;; Layout type is also inferred from the view structure:
;;
;; - Single panel → `:single`
;; - Facet grid (`:facet-row` or `:facet-col`) → `:facet-grid`
;; - Multiple x-y pairs (scatter plot matrix) → `:multi-variable`

(let [sk (sk/sketch scatter-views)]
  (:layout-type sk))

(kind/test-last [(fn [lt] (= :single lt))])

;; ## Coordinate Flipping
;;
;; Setting `:coord :flip` swaps axes in the sketch. The layer data
;; stays the same — the panel-level domains and ticks are swapped.
;; Internally, `make-coord` in `coord.clj` handles the transformation.

(def normal-sk
  (-> animals
      (sk/lay-value-bar :animal :count)
      sk/sketch))

(def flip-sk
  (-> animals
      (sk/lay-value-bar :animal :count)
      (sk/coord :flip)
      sk/sketch))

(let [np (first (:panels normal-sk))
      fp (first (:panels flip-sk))]
  {:normal {:x-categorical? (:categorical? (:x-ticks np))
            :y-categorical? (:categorical? (:y-ticks np))}
   :flipped {:x-categorical? (:categorical? (:x-ticks fp))
             :y-categorical? (:categorical? (:y-ticks fp))}})

(kind/test-last [(fn [m] (and (true? (get-in m [:normal :x-categorical?]))
                              (not (get-in m [:normal :y-categorical?]))
                              (not (get-in m [:flipped :x-categorical?]))
                              (true? (get-in m [:flipped :y-categorical?]))))])

(-> animals
    (sk/lay-value-bar :animal :count)
    (sk/coord :flip))

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The categorical axis moved from x to y.
;;
;; Labels are also swapped — the x-label and y-label follow their
;; visual axis, not the data axis:

(let [sk (-> five-points
             (sk/lay-point :x :y)
             (sk/coord :flip)
             sk/sketch)]
  {:x-label (:x-label sk)
   :y-label (:y-label sk)})

(kind/test-last [(fn [m] (and (= "y" (:x-label m))
                              (= "x" (:y-label m))))])

;; After flipping, the visual x-axis shows "y" and the visual y-axis
;; shows "x" — labels track the visual axes.

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

;; ## Resolution Overview
;;
;; All of the inference rules above feed into `views->sketch`, which
;; orchestrates a resolution pipeline. The diagram below shows the
;; key steps and their data dependencies:

^:kindly/hide-code
(kind/mermaid "
graph TD
  VIEWS[\"views + options\"]
  VIEWS --> CT[\"Column Types<br/>(infer-column-types)\"]
  VIEWS --> AE[\"Aesthetics<br/>(resolve-aesthetics)\"]
  CT --> GR[\"Grouping<br/>(infer-grouping)\"]
  AE --> GR
  CT --> ME[\"Method<br/>(infer-method)\"]
  GR --> STATS[\"Statistics<br/>(compute-stat)\"]
  ME --> STATS

  STATS --> DOM[\"Domains<br/>(collect-domain + pad-domain)\"]
  DOM --> TK[\"Ticks<br/>(compute-ticks)\"]

  VIEWS --> LBL[\"Labels<br/>(resolve-labels)\"]
  AE --> LEG[\"Color Legend<br/>(build-legend)\"]
  AE --> SLEG[\"Size Legend<br/>(build-size-legend)\"]
  AE --> ALEG[\"Alpha Legend<br/>(build-alpha-legend)\"]

  DOM --> LAYOUT[\"Layout<br/>(compute-layout-dims)\"]
  LBL --> LAYOUT
  LEG --> LAYOUT
  SLEG --> LAYOUT
  ALEG --> LAYOUT

  DOM --> SKETCH[\"Sketch\"]
  TK --> SKETCH
  LBL --> SKETCH
  LEG --> SKETCH
  SLEG --> SKETCH
  ALEG --> SKETCH
  LAYOUT --> SKETCH
  STATS --> SKETCH

  style VIEWS fill:#e8f5e9
  style SKETCH fill:#fff3e0
  style STATS fill:#e3f2fd
  style DOM fill:#e3f2fd
")

;; Each box corresponds to a named function in the codebase.
;; The top four boxes — Column Types, Aesthetics, Grouping, and
;; Method — are the per-view inference steps (in `view.clj`).
;; The remaining boxes are the sketch-level orchestration steps
;; (in `sketch.clj` and `scale.clj`).

;; ## Summary
;;
;; Every inference can be overridden. Here is the complete list:
;;
;; | What is inferred | Default | Override |
;; |:-----------------|:--------|:---------|
;; | Column selection | 1→x, 2→x y, 3→x y color | explicit column args in `sk/view` or `sk/lay-*` |
;; | Column type | dtype inspection | `:x-type`, `:y-type`, `:color-type` in view options |
;; | Aesthetic classification | keyword = column, string = color/column | explicit `:color` keyword vs hex string |
;; | Grouping | categorical color column | `:group` aesthetic |
;; | Method (mark + stat) | column types (see table above) | `sk/lay-point`, `sk/lay-histogram`, etc. |
;; | Domain extent | data range + 5% padding | `(sk/scale views :x {:domain [0 10]})` |
;; | Domain zero-anchor | bar/stacked charts include zero | `(sk/scale views :y {:domain [5 20]})` |
;; | Fill domain | `[0.0, 1.0]` for fill position | `(sk/scale views :y {:domain [0 2]})` |
;; | Tick values | round intervals (linear), powers of 10 (log) | wadogo scale configuration |
;; | Tick labels | number formatting, calendar formatting | wadogo label formatting |
;; | Axis labels | column name, underscores → spaces | `(sk/options {:x-label "Custom"})` |
;; | Color legend | categorical = discrete, numerical = continuous, none = no legend | `:color` mapping controls presence |
;; | Size legend | 5 graduated circles when `:size` maps to numerical column | `:size` mapping controls presence |
;; | Alpha legend | 5 graduated opacity squares when `:alpha` maps to numerical column | `:alpha` mapping controls presence |
;; | Layout padding | adjusts for title, labels, legend | `:width`, `:height` in options |
;; | Layout type | single, facet-grid, multi-variable | `sk/facet`, multiple x-y pairs |
;; | Coordinate system | `:cartesian` | `(sk/coord :flip)`, `(sk/coord :polar)` |
;;
;; The sketch captures the result of all inference. When in doubt,
;; look at the sketch.

;; ## What's Next
;;
;; - [**Methods**](./napkinsketch_book.methods.html) — the full registry of marks, stats, and positions that inference selects from
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) — see inference in action with the most common chart type
