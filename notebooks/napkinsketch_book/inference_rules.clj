;; # Inference Rules
;;
;; Napkinsketch infers many parameters automatically so you can write
;; less and get reasonable defaults. This notebook shows those rules
;; in action by examining the **plan** -- the resolved data structure
;; that captures every inference decision.
;;
;; This chapter is a reference: each rule in detail, with its default,
;; its override, and a plan-level check. For the conceptual overview,
;; read [The Sketch Model](./napkinsketch_book.sketch_model.html) and
;; [Core Concepts](./napkinsketch_book.core_concepts.html) first. The
;; examples here use small inline datasets so the full plan is readable.

(ns napkinsketch-book.inference-rules
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]
   ;; R datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## What Gets Inferred
;;
;; When you write `(-> data (sk/lay-point :x :y))` -- or even just
;; `(sk/lay-point data)` -- the library fills
;; in everything needed to render a plot. Here is the full list of
;; inference steps, in the order they happen:
;;
;; 1. **Column selection** -- which columns map to x, y, and color (inferred from dataset shape when omitted)
;; 2. **Column types** -- whether x and y are numerical, categorical, or temporal
;; 3. **Aesthetic resolution** -- whether `:color`, `:size`, `:alpha`, and `:text` are column references or fixed values
;; 4. **Grouping** -- which columns split data into subsets (explicit `:group` plus a categorical `:color`)
;; 5. **Method** -- which mark and stat to use (e.g., scatter, histogram, bar, line, boxplot)
;; 6. **Domains** -- data extent for each axis, with padding
;; 7. **Ticks** -- nice round values and formatted labels
;; 8. **Axis labels** -- derived from column names
;; 9. **Legends** -- entries and layout space for color, size, and alpha legends
;; 10. **Layout** -- whether panels form a single plot, a facet grid, or a multi-variable grid
;; 11. **Coordinate flipping** -- whether axes are swapped after layout (polar has its own chapter)
;;
;; Each rule has a sensible default and an explicit override.
;; The sections below demonstrate each rule with live examples.
;; Two cross-cutting sections follow the rule-by-rule tour: how the
;; rules combine in multi-layer plots, and a diagram of the full
;; resolution pipeline.

;; ## Inspecting the Plan
;;
;; A **plan** is the fully resolved data structure Napkinsketch builds
;; from a sketch right before rendering. It is a plain Clojure map
;; with domains, ticks, scales, resolved layers, legend, and layout
;; dimensions -- every inference decision made explicit in one place.
;;
;; `sk/plan` is the function that produces it. You can call it on
;; any sketch to see exactly what the library decided. Throughout
;; this chapter we use `sk/plan` to peek inside after each example
;; and check which rules fired.

(def five-points
  {:x [1.0 2.0 3.0 4.0 5.0]
   :y [2.1 4.3 3.0 5.2 4.8]})

(def scatter-views
  (-> five-points
      (sk/lay-point :x :y)))

;; Here is the full plan:

(sk/plan scatter-views)

(kind/test-last [(fn [pl] (and (= :single (:layout-type pl))
                               (= 1 (count (:panels pl)))
                               (= "x" (:x-label pl))
                               (= "y" (:y-label pl))
                               (nil? (:legend pl))
                               (zero? (get-in pl [:layout :legend-w]))
                               (let [p (first (:panels pl))
                                     g (first (:groups (first (:layers p))))]
                                 (and (= :linear (get-in p [:x-scale :type]))
                                      (= 1 (count (:groups (first (:layers p)))))
                                      (= (scicloj.napkinsketch.impl.defaults/hex->rgba (:default-color (scicloj.napkinsketch.impl.defaults/config))) (:color g))))))])

;; And the resulting plot:

scatter-views

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Notice in the plan above:
;;
;; - `:x-domain` is `[0.8 5.2]` -- wider than the data range `[1.0, 5.0]`
;;   because of 5% padding
;; - `:x-scale` is `{:type :linear}` -- inferred from numeric data
;; - `:x-ticks` has nice round values: `1.0, 1.5, 2.0, ...`
;; - `:x-label` is `"x"` -- derived from the column keyword
;; - `:legend` is `nil` -- no color mapping
;; - `:layout` has `:legend-w 0` -- no space reserved for a legend
;; - The single layer has `:mark :point` and a single `:groups` entry with all 5 data
;;   points, colored in the default color (steel blue)

;; ## Column Selection
;;
;; When column names are omitted, napkinsketch infers them from
;; the dataset shape:
;;
;; | Number of columns | Inferred mapping |
;; |:------------------|:-----------------|
;; | 1 | first column becomes x |
;; | 2 | first becomes x, second becomes y |
;; | 3 | first becomes x, second becomes y, third becomes color |
;; | 4+ | error -- specify columns explicitly |
;;
;; One column:

(-> {:values [1 2 3 4 5 6]}
    sk/lay-histogram)

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; Two columns:

(-> {:x [1 2 3 4 5] :y [2 4 3 5 4]}
    sk/lay-point)

(kind/test-last [(fn [v] (= 5 (:points (sk/svg-summary v))))])

;; Three columns -- the third becomes `:color`:

(-> {:x [1 2 3 4] :y [4 5 6 7] :g ["a" "a" "b" "b"]}
    sk/lay-point)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 4 (:points s))
                                (some #{"a"} (:texts s)))))])

;; When you provide explicit columns, inference is skipped -- you
;; are in full control:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :petal-length :petal-width {:color :species}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Column Types
;;
;; Once columns are selected, the next step is determining the type of each column: **numerical**, **categorical**,
;; or **temporal**? This determines the scale type, domain, tick style,
;; and the default mark.
;;
;; | Column dtype | Inferred type |
;; |:-------------|:--------------|
;; | float, int | `:numerical` |
;; | string, keyword, boolean, symbol, text | `:categorical` |
;; | LocalDate, LocalDateTime, Instant, java.util.Date | `:temporal` (numerical, with calendar-aware ticks) |
;;
;; Internally, `infer-column-types` in `resolve.clj` handles this step.
;;
;; A categorical column produces a band scale with string domain values.
;; Compare:

(def animals
  {:animal ["cat" "dog" "bird" "fish"]
   :count [12 8 15 5]})

(def bar-views
  (-> animals
      (sk/lay-value-bar :animal :count)))

(sk/plan bar-views)

(kind/test-last [(fn [pl] (let [p (first (:panels pl))]
                            (and (= ["cat" "dog" "bird" "fish"] (:x-domain p))
                                 (true? (:categorical? (:x-ticks p))))))])

bar-views

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; The x-domain is `["cat" "dog" "bird" "fish"]` -- strings in order of
;; appearance. The ticks have `:categorical? true`. The y-domain starts
;; at zero because this is a bar chart.

;; ### Temporal columns
;;
;; Dates are detected and converted to epoch-milliseconds internally,
;; with calendar-aware tick labels.
;; Clojure's `#inst` reader literal is a convenient way to write dates:

(let [pl (-> {:date [#inst "2024-01-01" #inst "2024-06-01" #inst "2024-12-01"]
              :val [10 25 18]}
             (sk/lay-point :date :val)
             sk/plan)
      p (first (:panels pl))]
  {:x-domain-numeric? (number? (first (:x-domain p)))
   :tick-count (count (:values (:x-ticks p)))
   :first-tick-label (first (:labels (:x-ticks p)))})

(kind/test-last [(fn [m] (and (true? (:x-domain-numeric? m))
                              (= 10 (:tick-count m))
                              (= "Feb-01" (:first-tick-label m))))])

;; The x-domain contains epoch-millisecond numbers, but the 10 tick
;; labels show human-readable dates like `"Feb-01"`. Napkinsketch accepts
;; `java.util.Date` (from `#inst`), `LocalDate`, `LocalDateTime`,
;; and `Instant` -- all are converted to epoch-milliseconds for
;; plotting, with calendar-aware tick formatting.

;; ## Aesthetic Resolution
;;
;; The `:color` parameter triggers different behaviors depending on
;; what you pass. Internally, `resolve-aesthetics` in `resolve.clj`
;; classifies each aesthetic channel (`:color`, `:size`, `:alpha`,
;; `:text`) as either a column reference or a fixed literal.

;; ### Column reference -- colored by palette

(def colored-views
  (-> {:x [1 2 3 4 5 6]
       :y [3 5 4 7 6 8]
       :g ["a" "a" "a" "b" "b" "b"]}
      (sk/lay-point :x :y {:color :g})))

(sk/plan colored-views)

(kind/test-last [(fn [pl] (let [layer (first (:layers (first (:panels pl))))]
                            (and (= 2 (count (:groups layer)))
                                 (some? (:legend pl))
                                 (= 100 (get-in pl [:layout :legend-w])))))])

colored-views

(kind/test-last [(fn [v] (= 6 (:points (sk/svg-summary v))))])

;; Two entries in `:groups`, each with its own `:color` (RGBA),
;; `:xs`, `:ys`, and `:label`. A `:legend` appeared with 2 entries.
;; The `:layout` now has `:legend-w 100` -- space reserved on the right.
;;
;; Why two entries? Because `:g` is a categorical column. The next
;; section explores this mechanism in detail.

;; ### Fixed color string -- single color, no legend

(def fixed-color-views
  (-> five-points
      (sk/lay-point :x :y {:color "#E74C3C"})))

(sk/plan fixed-color-views)

(kind/test-last [(fn [pl] (and (nil? (:legend pl))
                               (zero? (get-in pl [:layout :legend-w]))
                               (let [layer (first (:layers (first (:panels pl))))
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
;; Otherwise, it is treated as a color value -- first trying hex
;; parsing, then CSS color name lookup.
;;
;; Here is the full resolution order for a string `:color` value:
;;
;; 1. If the string matches a dataset column, it is a column reference (grouping)
;; 2. If it starts with `#`, it is a hex color (`"#E74C3C"`, `"#F00"`)
;; 3. If it parses as hex without `#`, it is a hex color (`"00FF00"`)
;; 4. If it matches a CSS color name, it is a named color (`"red"`, `"steelblue"`)
;; 5. Otherwise, error with a helpful message
;;
;; In practice, ambiguity is rare. Column names like `"species"` or
;; `"temperature"` are not valid CSS colors, and color names like
;; `"red"` are unlikely column names. When true ambiguity exists,
;; use a keyword for the column (`:red`) or a hex string for the
;; color (`"#FF0000"`).

;; Verify: `"red"` is a fixed color when the dataset has no `red` column:

(let [pl (-> five-points
             (sk/lay-point :x :y {:color "red"})
             sk/plan)]
  {:legend (:legend pl)
   :color (:color (first (:groups (first (:layers (first (:panels pl)))))))})

(kind/test-last [(fn [m] (and (nil? (:legend m))
                              (> (first (:color m)) 0.9)))])

;; No legend, red RGBA -- treated as a fixed color, not a column.

;; ### No color -- default gray

;; Look back at the first scatter plan above -- its single `:groups`
;; entry has the default color (steel blue). No legend.

;; ## Grouping
;;
;; The `:groups` entries you saw above reflect a key concept:
;; **grouping** controls how data is split into independent subsets.
;; Each group gets its own visual elements -- its own set of points,
;; its own regression line, its own density curve, its own bar in a
;; dodged layout.
;;
;; Internally, `infer-grouping` in `resolve.clj` builds the grouping
;; vector from explicit `:group` and categorical color.
;;
;; Grouping can be **derived** (from a categorical `:color` mapping)
;; or **explicit** (via the `:group` aesthetic).

;; ### Categorical color implies grouping
;;
;; When `:color` maps to a categorical column (as with `colored-views`
;; above), the data is split into one group per category. Each group
;; gets a distinct palette color and a legend entry:

(let [pl (sk/plan colored-views)
      layer (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer))
   :group-labels (mapv :label (:groups layer))
   :has-legend? (some? (:legend pl))})

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

(let [pl (-> {:x [1 2 3 4 5]
              :y [2 4 3 5 4]
              :val [10 20 30 40 50]}
             (sk/lay-point :x :y {:color :val})
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer))
   :legend-type (:type (:legend pl))
   :color-stops (count (:stops (:legend pl)))})

(kind/test-last [(fn [m] (and (= 1 (:group-count m))
                              (= :continuous (:legend-type m))
                              (= 20 (:color-stops m))))])

;; One group, continuous legend with 20 stops. No splitting occurred --
;; the color is a visual encoding, not a grouping variable.

;; ### Overriding color type with `:color-type`
;;
;; Sometimes a numeric column is really a categorical identifier -- for
;; example, subject IDs in a repeated-measures study. The inference
;; system sees numbers and treats them as continuous, but you want
;; discrete groups. The `:color-type :categorical` override tells the
;; library to treat the column as categorical despite its numeric dtype.
;;
;; This is a core principle of the library: **inference provides good
;; defaults, but the user can always override**.

;; Without override -- one group, continuous gradient:

(let [study {:subject [1 1 1 2 2 2 3 3 3]
             :day     [1 2 3 1 2 3 1 2 3]
             :score   [5 7 6 3 4 5 8 9 7]}
      pl (-> study
             (sk/lay-line :day :score {:color :subject})
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer))
   :legend-type (:type (:legend pl))})

(kind/test-last [(fn [m] (and (= 1 (:group-count m))
                              (= :continuous (:legend-type m))))])

;; With `:color-type :categorical` -- three groups, one per subject:

(let [study {:subject [1 1 1 2 2 2 3 3 3]
             :day     [1 2 3 1 2 3 1 2 3]
             :score   [5 7 6 3 4 5 8 9 7]}
      pl (-> study
             (sk/lay-line :day :score {:color :subject
                                       :color-type :categorical})
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer))
   :legend-entries (count (:entries (:legend pl)))})

(kind/test-last [(fn [m] (and (= 3 (:group-count m))
                              (= 3 (:legend-entries m))))])

;; The same data, the same columns -- but `:color-type :categorical`
;; changes inference from "one gradient" to "three distinct groups."
;; This affects grouping, line splitting, legend style, and palette
;; assignment. The rendered plots look completely different:

(-> {:subject [1 1 1 2 2 2 3 3 3]
     :day     [1 2 3 1 2 3 1 2 3]
     :score   [5 7 6 3 4 5 8 9 7]}
    (sk/lay-line :day :score {:color :subject
                              :color-type :categorical})
    sk/lay-point
    (sk/options {:title "Scores by Subject (categorical override)"}))

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (pos? (:lines s))
                                (pos? (:points s)))))])

;; ### Explicit grouping with `:group`
;;
;; The `:group` aesthetic splits data into groups without
;; assigning distinct colors or creating a legend. This is useful
;; when you want per-group statistics but uniform appearance.

(def grouped-data
  {:x [1 2 3 4 5 6]
   :y [3 5 4 7 6 8]
   :g ["a" "a" "a" "b" "b" "b"]})

(let [pl (-> grouped-data
             (sk/lay-point :x :y {:group :g})
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  {:group-count (count (:groups layer))
   :has-legend? (some? (:legend pl))})

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

;; One regression line -- no grouping:

(-> grouped-data
    (sk/view :x :y)
    sk/lay-point
    sk/lay-lm)

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 6 (:points s))
                                (= 1 (:lines s)))))])

;; Two regression lines -- grouped by color:

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

;; ## Method
;;
;; When you use `sk/view` without an explicit `sk/lay-*` call,
;; Napkinsketch infers the **method** -- a mark + stat bundle --
;; from the column types of the referenced columns. Internally,
;; `infer-method` in `resolve.clj` applies these rules.
;;
;; ### Single-column cases (or x = y on the diagonal)
;;
;; | Column type | Inferred | Mark + stat |
;; |:------------|:---------|:------------|
;; | numerical | histogram | `:bar` + `:bin` |
;; | temporal | histogram (over epoch-ms, with calendar-aware ticks) | `:bar` + `:bin` |
;; | categorical | bar chart of category counts | `:rect` + `:count` |
;;
;; ### Two-column cases
;;
;; | x type | y type | Inferred | Mark + stat |
;; |:-------|:-------|:---------|:------------|
;; | numerical | numerical | scatter | `:point` + `:identity` |
;; | temporal | numerical | time-series line | `:line` + `:identity` |
;; | categorical | numerical | boxplot | `:boxplot` + `:boxplot` |
;; | any other pair | | scatter (fallback) | `:point` + `:identity` |
;;
;; Fallback pairs include numerical x + categorical y, temporal x +
;; categorical y, categorical x + categorical y, and temporal x +
;; temporal y. These are rarer in practice, and giving them a
;; dedicated inference is deferred. You can always override with an
;; explicit `sk/lay-*` call; the inferred method is only a default.
;;
;; When you use `sk/lay-point`, `sk/lay-histogram`, etc., the method's
;; stat takes precedence -- column-type inference is bypassed.
;;
;; A single numerical column produces a histogram:

(def hist-views
  (-> five-points
      (sk/view :x)))

(sk/plan hist-views)

(kind/test-last [(fn [pl] (let [layer (first (:layers (first (:panels pl))))]
                            (= :bar (:mark layer))))])

hist-views

(kind/test-last [(fn [v] (pos? (:polygons (sk/svg-summary v))))])

;; The layer mark is `:bar` -- the layer data contains `:bins` with
;; `:x0`, `:x1`, `:count` -- the result of the `:bin` stat.

;; A single temporal column also becomes a histogram, binned over
;; epoch-milliseconds with calendar-aware tick labels:

(let [pl (-> {:date [#inst "2024-01-01" #inst "2024-02-01" #inst "2024-03-01"
                     #inst "2024-04-01" #inst "2024-05-01"]}
             (sk/view :date)
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :bar m))])

;; A single categorical column produces a bar chart of counts:

(def count-views
  (-> animals
      (sk/view :animal)))

(sk/plan count-views)

(kind/test-last [(fn [pl] (let [layer (first (:layers (first (:panels pl))))]
                            (= :rect (:mark layer))))])

count-views

(kind/test-last [(fn [v] (= 4 (:polygons (sk/svg-summary v))))])

;; Mark is `:rect` with `:counts` -- the `:count` stat tallied each
;; of the 4 categories.

;; Two numerical columns produce a scatter (the chapter's opening
;; `scatter-views` shows this):

(let [pl (-> five-points
             (sk/view :x :y)
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :point m))])

;; A temporal x with a numerical y infers a time-series line. Row
;; order is preserved, so pre-sort temporal data to avoid zigzag:

(let [pl (-> {:date [#inst "2024-01-01" #inst "2024-02-01" #inst "2024-03-01"]
              :val  [10 25 18]}
             (sk/view :date :val)
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :line m))])

;; A categorical x with a numerical y infers a boxplot -- the default
;; for summarizing a distribution across groups:

(let [pl (-> {:species ["a" "a" "a" "b" "b" "b" "c" "c" "c"]
              :val     [8  10  12  18  20  22  14  15  17]}
             (sk/view :species :val)
             sk/plan)
      layer (first (:layers (first (:panels pl))))]
  (:mark layer))

(kind/test-last [(fn [m] (= :boxplot m))])

;; Pairs without a dedicated rule (numerical x + categorical y,
;; categorical x + categorical y, and so on) fall back to scatter
;; via the `:else` branch. Some of these orientations are awkward in
;; the current plan construction; in practice you will name an
;; explicit `sk/lay-*` when you reach for them.

;; ## Domains
;;
;; Numerical domains extend 5% beyond the data range so points
;; aren't clipped at the edges. Internally, `pad-domain` in
;; `scale.clj` computes this padding.

(let [pl (sk/plan scatter-views)
      p (first (:panels pl))]
  {:x-domain (:x-domain p)
   :data-range [1.0 5.0]
   :padding-each-side (* 0.05 (- 5.0 1.0))})

(kind/test-last [(fn [m] (and (== 0.8 (first (:x-domain m)))
                              (== 5.2 (second (:x-domain m)))
                              (== 0.2 (:padding-each-side m))))])

;; The domain `[0.8, 5.2]` = data range `[1.0, 5.0]` +/- 0.2 (5% of 4.0).
;;
;; Special domain rules apply in certain contexts:
;;
;; Bar chart y-domains always include zero:

(let [pl (sk/plan bar-views)
      p (first (:panels pl))]
  {:y-domain (:y-domain p)})

(kind/test-last [(fn [m] (<= (first (:y-domain m)) 0))])

;; Percentage-filled layers normalize the y-domain to `[0.0, 1.0]`:

(let [fill-pl (-> {:x ["a" "a" "b" "b"]
                   :g ["m" "n" "m" "n"]}
                  (sk/lay-stacked-bar-fill :x {:color :g})
                  sk/plan)
      p (first (:panels fill-pl))]
  (:y-domain p))

(kind/test-last [(fn [d] (and (== 0.0 (first d))
                              (== 1.0 (second d))))])

;; The y-domain is exactly `[0.0, 1.0]` -- each category sums to 100%.

;; Multi-layer plots merge domains across layers -- see
;; "Multi-Layer Plans" below.

;; ## Ticks
;;
;; Once domains are computed, Napkinsketch selects "nice" round tick
;; values. The logic depends on the scale type:
;;
;; - **Linear** -- wadogo selects ticks at round intervals (1, 2, 2.5, 5, ...)
;; - **Log** -- ggplot2-style 1-2-5 nice numbers: powers of 10 when they
;;   give at least 3 ticks, otherwise intermediates at 1-2-5 or 1-2-3-5
;;   multiples per decade
;; - **Categorical** -- tick at each category, in order of appearance
;; - **Temporal** -- calendar-aware snapping (year, month, day, hour)
;;   with adaptive formatting
;;
;; Linear ticks for the scatter example:

(let [pl (sk/plan scatter-views)
      p (first (:panels pl))]
  {:x-tick-values (:values (:x-ticks p))
   :x-tick-labels (:labels (:x-ticks p))})

(kind/test-last [(fn [m] (and (= [1.0 1.5 2.0 2.5 3.0 3.5 4.0 4.5 5.0]
                                 (:x-tick-values m))
                              (= ["1.0" "1.5" "2.0" "2.5" "3.0" "3.5" "4.0" "4.5" "5.0"]
                                 (:x-tick-labels m))))])

;; Nine ticks from 1.0 to 5.0 at 0.5 intervals -- round and readable.
;;
;; Log ticks for a multi-decade range:

(let [pl (-> {:x [0.1 1.0 10.0 100.0 1000.0]
              :y [5 10 15 20 25]}
             (sk/lay-point :x :y)
             (sk/scale :x :log)
             sk/plan)
      p (first (:panels pl))]
  {:tick-values (:values (:x-ticks p))
   :tick-labels (:labels (:x-ticks p))})

(kind/test-last [(fn [m] (and (= [0.1 1.0 10.0 100.0 1000.0] (:tick-values m))
                              (= ["0.1" "1" "10" "100" "1000"] (:tick-labels m))))])

;; Five ticks at exact powers of 10 -- no irrational intermediates.
;; Whole numbers display without decimals, sub-1 values use minimal
;; decimal places.

;; Categorical ticks match domain order:

(let [pl (sk/plan bar-views)
      p (first (:panels pl))]
  (:values (:x-ticks p)))

(kind/test-last [(fn [v] (= ["cat" "dog" "bird" "fish"] v))])

;; ## Axis Labels
;;
;; Labels come from column names. Underscores and hyphens become spaces.
;; Internally, `resolve-labels` in `plan.clj` handles this.

(let [pl (-> (rdatasets/datasets-iris)
             (sk/lay-point :sepal-length :sepal-width)
             sk/plan)]
  {:x-label (:x-label pl)
   :y-label (:y-label pl)})

(kind/test-last [(fn [m] (and (= "sepal length" (:x-label m))
                              (= "sepal width" (:y-label m))))])

;; When only one column is specified, the y-axis shows computed counts.
;; The system omits the y-label since it would repeat the column name:

(let [pl (-> five-points (sk/view :x) sk/plan)]
  {:x-label (:x-label pl)
   :y-label (:y-label pl)})

(kind/test-last [(fn [m] (and (= "x" (:x-label m))
                              (nil? (:y-label m))))])

;; Explicit labels override inference:

(let [pl (-> five-points
             (sk/lay-point :x :y)
             (sk/options {:x-label "Length (cm)" :y-label "Width (cm)"})
             sk/plan)]
  {:x-label (:x-label pl)
   :y-label (:y-label pl)})

(kind/test-last [(fn [m] (and (= "Length (cm)" (:x-label m))
                              (= "Width (cm)" (:y-label m))))])

;; ## Legends
;;
;; A legend appears when a column is mapped to color. Internally,
;; `build-legend` in `plan.clj` constructs the legend from
;; the collected color information. Three cases:
;;
;; A categorical color mapping produces a discrete legend with one entry per category:

(:legend (sk/plan colored-views))

(kind/test-last [(fn [leg] (and (= :g (:title leg))
                                (= 2 (count (:entries leg)))))])

;; Title is the column name. Each entry has a `:label` and `:color` (RGBA).
;;
;; No color mapping means no legend:

(:legend (sk/plan scatter-views))

(kind/test-last [nil?])

;; A fixed color string also suppresses the legend:

(:legend (sk/plan fixed-color-views))

(kind/test-last [nil?])

;; A numeric color mapping produces a continuous legend (gradient bar):

(:legend (-> {:x [1 2 3] :y [4 5 6] :val [10 20 30]}
             (sk/lay-point :x :y {:color :val})
             sk/plan))

(kind/test-last [(fn [leg] (and (= :continuous (:type leg))
                                (= 20 (count (:stops leg)))))])

;; ### Size Legend
;;
;; When `:size` maps to a numerical column, a size legend shows graduated
;; circles spanning the data range. Internally, `build-size-legend` in
;; `plan.clj` generates five entries with proportional radii.

(:size-legend (-> {:x [1 2 3 4 5] :y [1 2 3 4 5] :s [10 20 30 40 50]}
                  (sk/lay-point :x :y {:size :s})
                  sk/plan))

(kind/test-last [(fn [leg] (and (= :size (:type leg))
                                (= :s (:title leg))
                                (= 5 (count (:entries leg)))))])

;; Each entry has a `:value` and `:radius`. No size mapping means no size legend:

(:size-legend (sk/plan scatter-views))

(kind/test-last [nil?])

;; ### Alpha Legend
;;
;; When `:alpha` maps to a numerical column, an alpha legend shows
;; graduated opacity squares. Internally, `build-alpha-legend` in
;; `plan.clj` asks for about five nice 1/2/5 breaks; the exact count
;; depends on the range (here the range [0.1, 0.9] yields four).

(:alpha-legend (-> {:x [1 2 3 4 5] :y [1 2 3 4 5] :a [0.1 0.3 0.5 0.7 0.9]}
                   (sk/lay-point :x :y {:alpha :a})
                   sk/plan))

(kind/test-last [(fn [leg] (and (= :alpha (:type leg))
                                (= :a (:title leg))
                                (pos? (count (:entries leg)))))])

;; No alpha mapping means no alpha legend:

(:alpha-legend (sk/plan scatter-views))

(kind/test-last [nil?])

;; ## Layout
;;
;; The `:layout` map adjusts padding based on what elements are
;; present. Internally, `compute-layout-dims` in `plan.clj`
;; calculates the space needed for titles, labels, and legends.
;;
;; Compare a bare plot to one with title, labels, and legend:

(let [bare (sk/plan scatter-views)
      full (-> {:x [1 2 3 4 5 6]
                :y [3 5 4 7 6 8]
                :g ["a" "a" "a" "b" "b" "b"]}
               (sk/lay-point :x :y {:color :g})
               (sk/options {:title "My Plot"})
               sk/plan)]
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
;; - A single panel is `:single`
;; - A facet grid (`:facet-row` or `:facet-col`) is `:facet-grid`
;; - Multiple x-y pairs (scatter plot matrix) are `:multi-variable`

(let [pl (sk/plan scatter-views)]
  (:layout-type pl))

(kind/test-last [(fn [lt] (= :single lt))])

;; ## Coordinate Flipping
;;
;; Setting `:coord :flip` swaps axes in the plan. The layer data
;; stays the same -- the panel-level domains and ticks are swapped.
;; Internally, `make-coord` in `coord.clj` handles the transformation.

(def normal-pl
  (-> animals
      (sk/lay-value-bar :animal :count)
      sk/plan))

(def flip-pl
  (-> animals
      (sk/lay-value-bar :animal :count)
      (sk/coord :flip)
      sk/plan))

(let [np (first (:panels normal-pl))
      fp (first (:panels flip-pl))]
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
;; Labels are also swapped -- the x-label and y-label follow their
;; visual axis, not the data axis:

(let [pl (-> five-points
             (sk/lay-point :x :y)
             (sk/coord :flip)
             sk/plan)]
  {:x-label (:x-label pl)
   :y-label (:y-label pl)})

(kind/test-last [(fn [m] (and (= "y" (:x-label m))
                              (= "x" (:y-label m))))])

;; After flipping, the visual x-axis shows "y" and the visual y-axis
;; shows "x" -- labels track the visual axes.
;;
;; Polar coordinates (`:coord :polar`) are a larger transform -- rose
;; charts, radial bars, and related plots. The
;; [Polar](./napkinsketch_book.polar.html) chapter covers them in depth.

;; ## Multi-Layer Plans
;;
;; When multiple layers share a panel, their domains are merged:

(def multi-views
  (-> five-points
      (sk/view :x :y)
      sk/lay-point
      sk/lay-lm))

(sk/plan multi-views)

(kind/test-last [(fn [pl] (let [p (first (:panels pl))]
                            (= 2 (count (:layers p)))))])

multi-views

(kind/test-last [(fn [v] (let [s (sk/svg-summary v)]
                           (and (= 5 (:points s))
                                (= 1 (:lines s)))))])

;; Two layers -- one `:point`, one `:line` -- sharing the same domain.
;; The `:line` layer has `:mark :line` and its groups contain
;; `:polyline-xs` and `:polyline-ys` -- the regression curve.

;; ## Resolution Overview
;;
;; All of the inference rules above feed into `draft->plan`, which
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

  DOM --> PLAN[\"Plan\"]
  TK --> PLAN
  LBL --> PLAN
  LEG --> PLAN
  SLEG --> PLAN
  ALEG --> PLAN
  LAYOUT --> PLAN
  STATS --> PLAN

  style VIEWS fill:#e8f5e9
  style PLAN fill:#fff3e0
  style STATS fill:#e3f2fd
  style DOM fill:#e3f2fd
")

;; Each box corresponds to a named function in the codebase.
;; The top four boxes -- Column Types, Aesthetics, Grouping, and
;; Method -- are the per-view inference steps (in `resolve.clj`).
;; The remaining boxes are the plan-level orchestration steps
;; (in `plan.clj` and `scale.clj`).

;; ## Summary
;;
;; Every inference can be overridden. Here is the complete list:
;;
;; | What is inferred | Default | Override |
;; |:-----------------|:--------|:---------|
;; | Column selection | one column fills x; two fill x, y; three fill x, y, color | explicit column args in `sk/view` or `sk/lay-*` |
;; | Column type | dtype inspection | `:x-type`, `:y-type`, `:color-type` in view options |
;; | Aesthetic classification | keyword = column, string = color/column | explicit `:color` keyword vs hex string |
;; | Grouping | categorical color column | `:group` aesthetic |
;; | Method (mark + stat) | column types (see table above) | `sk/lay-point`, `sk/lay-histogram`, etc. |
;; | Domain extent | data range + 5% padding | `(sk/scale views :x {:domain [0 10]})` |
;; | Domain zero-anchor | bar/stacked charts include zero | `(sk/scale views :y {:domain [5 20]})` |
;; | Fill domain | `[0.0, 1.0]` for fill position | `(sk/scale views :y {:domain [0 2]})` |
;; | Tick values | round intervals (linear), powers of 10 (log) | wadogo scale configuration |
;; | Tick labels | number formatting, calendar formatting | wadogo label formatting |
;; | Axis labels | column name, with underscores replaced by spaces | `(sk/options {:x-label "Custom"})` |
;; | Color legend | categorical = discrete, numerical = continuous, none = no legend | `:color` mapping controls presence |
;; | Size legend | 5 graduated circles when `:size` maps to numerical column | `:size` mapping controls presence |
;; | Alpha legend | 5 graduated opacity squares when `:alpha` maps to numerical column | `:alpha` mapping controls presence |
;; | Layout padding | adjusts for title, labels, legend | `:width`, `:height` in options |
;; | Layout type | single, facet-grid, multi-variable | `sk/facet`, multiple x-y pairs |
;; | Coordinate system | `:cartesian` | `(sk/coord :flip)`, `(sk/coord :polar)` |
;;
;; The plan captures the result of all inference. When in doubt,
;; look at the plan.

;; ## What's Next
;;
;; - [**Methods**](./napkinsketch_book.methods.html) -- the full registry of marks, stats, and positions that inference selects from
;; - [**Scatter Plots**](./napkinsketch_book.scatter.html) -- see inference in action with the most common chart type
