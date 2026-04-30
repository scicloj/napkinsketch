;; # Troubleshooting
;;
;; Common mistakes and how to fix them.

(ns plotje-book.troubleshooting
  (:require
   ;; Rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]))

;; ## Column Not Found
;;
;; **Symptom**: `"Column :foo (from :x) not found in dataset"` error.
;;
;; **Cause**: The column name does not exist in the dataset. CSV
;; headers are strings by default -- without `{:key-fn keyword}`,
;; columns have string names like `"sepal_length"` instead of
;; `:sepal-length`.
;;
;; **Fix**: Pass `{:key-fn keyword}` when loading the dataset:
;;
;; ```clojure
;; (tc/dataset "data.csv" {:key-fn keyword})
;; ```
;;
;; You can check available columns with:

(tc/column-names (rdatasets/datasets-iris))

(kind/test-last [(fn [v] (some #{:sepal-length} v))])

;; ## Mixed Keyword and String Column References

;; **Symptom**: A plot that should show data renders empty -- no
;; points, no lines, no bars -- and no error is raised. Most
;; commonly seen with CSVs loaded without `:key-fn keyword`,
;; where the dataset has string column names but mappings use
;; keywords.
;;
;; **Cause**: Plotje normalizes string vs. keyword refs in many
;; paths -- a keyword-keyed dataset referenced with strings
;; renders fine. The reverse direction (string-keyed dataset
;; referenced with keywords) is not yet fully normalized, and
;; the data-extraction step resolves to nothing, leaving an
;; empty layer.
;;
;; **Fix**: Pick one form -- keyword or string -- and use it
;; consistently with the dataset's column keys. CSVs loaded with
;; `{:key-fn keyword}` keep the keyword convention used elsewhere
;; in this book.

(let [string-keyed (-> (rdatasets/datasets-iris)
                       (assoc "species-str"
                              (mapv str ((rdatasets/datasets-iris)
                                         :species))))]
  (-> string-keyed
      (pj/pose {:color "species-str"})
      (pj/lay-point :sepal-length :sepal-width)))

(kind/test-last [(fn [v] (let [s (pj/svg-summary v)]
                           (and (= 150 (:points s))
                                (< 1 (count (:colors s))))))])

;; ## Wrong Chart Type from Inference
;;
;; **Symptom**: `pj/pose` produces a chart type that isn't what you
;; wanted -- a boxplot when you wanted individual points, a line
;; when you wanted a scatter.
;;
;; **Cause**: `pj/pose` infers the layer type from column types. The
;; defaults fit the most common use case for each column-type pair
;; (see [Inference Rules](./plotje_book.inference_rules.html)),
;; but they can be overridden.
;;
;; **Fix**: Use an explicit `pj/lay-*` function. For example, a
;; categorical x with a numerical y defaults to a boxplot:

(-> (rdatasets/datasets-iris)
    (pj/pose :species :sepal-width))

(kind/test-last [(fn [v] (pos? (:lines (pj/svg-summary v))))])

;; Use `pj/lay-point` if you want the individual points instead:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :species :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; ## Numeric IDs Treated as Continuous Color
;;
;; **Symptom**: You color by a subject/group ID column that contains
;; numbers (e.g., 1, 2, 3), but instead of discrete colored groups you
;; get a single continuous gradient.
;;
;; **Cause**: The inference system sees a numeric column and treats it
;; as continuous. Continuous color means no grouping -- all data stays
;; in one group with a gradient legend.
;;
;; **Fix**: Add `:color-type :categorical` to override the inference:
;;
;; ```clojure
;; ;; Gradient (wrong for IDs):
;; (pj/lay-line data :day :score {:color :subject})
;;
;; ;; Discrete groups (correct):
;; (pj/lay-line data :day :score {:color :subject
;;                                :color-type :categorical})
;; ```
;;
;; See [Inference Rules](./plotje_book.inference_rules.html)
;; for a worked example.

;; ## Numeric Column Rejected by a Categorical-Axis Mark
;;
;; **Symptom**: An error like `"lay-value-bar requires a categorical
;; column for :x, but :hour is numerical"`, or the equivalent for
;; `:boxplot`, `:violin`, `:lollipop`, or similar marks that need a
;; categorical axis.
;;
;; **Cause**: The column you passed (e.g., hour of day, year, subject
;; ID) contains numbers, so column-type inference classifies it as
;; `:numerical`. The mark needs `:categorical`.
;;
;; **Fix**: Add `:x-type :categorical` (or `:y-type :categorical` for
;; horizontal layouts) to override the inferred type. No need to
;; convert the column itself:

(-> {:hour [9 10 11 12] :count [5 8 12 7]}
    (pj/lay-value-bar :hour :count {:x-type :categorical}))

(kind/test-last [(fn [v] (= 4 (:polygons (pj/svg-summary v))))])

;; The override propagates into `infer-column-types`, so every
;; downstream step (scale type, tick placement, domain) treats
;; `:hour` as categorical. The same switch works for `:y-type` when
;; a numeric column is on the y axis of a horizontal boxplot or
;; similar layout. See
;; [Inference Rules](./plotje_book.inference_rules.html)
;; for a worked example.

;; ## Log Scale via `:scale-x` / `:scale-y` Options
;;
;; **Symptom**: Passing `{:scale-x :log}` (or `{:scale-y :log}`)
;; to a layer or to `pj/options` prints a warning --
;; `"does not recognize option(s): [:scale-x]"` -- and the chart
;; comes out on a linear axis.
;;
;; **Cause**: Scales are plot-level, not layer-level or option-map
;; keys. They are set by the `pj/scale` function, not by a
;; `:scale-*` key.
;;
;; **Fix**: Use `pj/scale`:

(-> (rdatasets/ggplot2-diamonds)
    (pj/lay-point :carat :price {:alpha 0.1})
    (pj/scale :y :log))

(kind/test-last [(fn [v] (pos? (:points (pj/svg-summary v))))])

;; `pj/scale` takes the pose, the axis (`:x` or `:y`), and
;; either a type keyword (`:linear`, `:log`) or a scale spec
;; map with `:type` and an optional `:domain` override.
;; See the [Inference Rules](./plotje_book.inference_rules.html)
;; chapter for how scale types and domains interact with column
;; inference.

;; ## x-Only Layer Types Do Not Accept a y Column
;;
;; **Symptom**: `"lay-histogram uses only the x column"` error.
;;
;; **Cause**: Histogram, bar, density, and rug layer types use only
;; the x column. Passing a y column is an error.
;;
;; **Fix**: Remove the y column:

(-> (rdatasets/datasets-iris)
    (pj/lay-histogram :sepal-length))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; ## Categorical Column with Log Scale
;;
;; **Symptom**: `"Log scale requires numeric data"` error.
;;
;; **Cause**: Log scales only work with numerical columns. Categorical
;; columns (strings, keywords) have no meaningful log transform.
;;
;; **Fix**: Use a numerical column for the log-scaled axis, or drop
;; the log scale on the categorical axis:

(try
  (-> (rdatasets/datasets-iris)
      (pj/lay-bar :species)
      (pj/scale :x :log)
      pj/plan)
  (catch Exception e (.getMessage e)))

(kind/test-last [(fn [msg] (and (string? msg)
                                (re-find #"[Ll]og scale" msg)))])

;; ## Polar Coordinates with Unsupported Marks
;;
;; **Symptom**: Errors or unexpected output with `(pj/coord :polar)`.
;;
;; **Cause**: Polar coordinates currently support a subset of marks:
;; `:bar`, `:point`, `:rect`, `:rug`, and `:text` (verified by the
;; polar coord function in source). Layer types built on these marks
;; (such as `:value-bar` and `:histogram`, which both render as
;; bars) work too.
;;
;; **Fix for now**: Use a supported mark. A bar chart flipped to polar
;; becomes a rose chart:

(-> (rdatasets/datasets-chickwts)
    (pj/pose :feed)
    pj/lay-bar
    (pj/coord :polar))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; Support for `:line`, `:area`, and other marks in polar is
;; planned. See the [Polar Coordinates](./plotje_book.polar.html)
;; chapter for the full set of currently supported marks and
;; examples.

;; ## Tooltip and Brush Not Working
;;
;; **Symptom**: You set `{:tooltip true}` but no tooltip appears when
;; hovering over points.
;;
;; **Cause**: Tooltip and brush interactivity use JavaScript that
;; requires a compatible notebook viewer. Static HTML export or some
;; viewers may not support it.
;;
;; **Fix**: Use [Clay](https://scicloj.github.io/clay/) or another
;; Kindly-compatible tool that supports `kind/hiccup` with embedded
;; scripts.

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width {:color :species})
    (pj/options {:tooltip true}))

(kind/test-last [(fn [v] (= 150 (:points (pj/svg-summary v))))])

;; ## Faceting Keys in a Layer or Pose Options Map
;;
;; **Symptom**: An error like
;; `"Faceting is plot-level, not layer-level. Use (pj/facet pose col) ..."`
;; when you put `:facet-col`, `:facet-row`, `:facet-x`, or
;; `:facet-y` inside a `pj/pose` or `pj/lay-*` options map.
;;
;; **Cause**: Faceting configures the plot as a whole, not a single
;; pose or layer. Those keys are not accepted in pose/layer
;; mappings.
;;
;; **Fix**: Use `pj/facet` (single-axis) or `pj/facet-grid`
;; (two-axis) as a top-level step in the pipeline:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/facet :species))

(kind/test-last [(fn [v] (= 3 (:panels (pj/svg-summary v))))])

;; ## Constant `:x` or `:y` in a Layer's Options
;;
;; **Symptom**: An error like
;; `"lay-text :y must be a column reference (keyword or string),
;; but got 200"`, typically when adding a text or label layer at a
;; fixed horizontal or vertical position. (Reference lines use
;; `pj/lay-rule-h` with `:y-intercept` or `pj/lay-rule-v` with
;; `:x-intercept` instead.)
;;
;; **Cause**: `:x` and `:y` are position **mappings** -- they must
;; name a column that the stat can index into, not hold a scalar
;; constant.
;;
;; **Fix**: Provide a small one-row dataset via `:data` whose columns
;; hold the constant values, then reference those columns:

(-> (rdatasets/datasets-iris)
    (pj/lay-point :sepal-length :sepal-width)
    (pj/lay-text {:data (-> (tc/dataset {:sepal-length [6.5]
                                         :species     ["mean"]})
                            (tc/add-column :yy (constantly 3.5)))
                  :x :sepal-length :y :yy :text :species}))

(kind/test-last [(fn [v] (some #{"mean"} (:texts (pj/svg-summary v))))])

;; ## Dataset Missing Columns a Template References
;;
;; **Symptom**: An error like
;; `"Cannot attach data: pose references column(s) [:group] not
;; present in the dataset. Available columns: [:x :y]"` when
;; calling `pj/with-data` on a dataless template pose.
;;
;; **Cause**: `pj/with-data` validates at attach time -- every
;; keyword column reference in the template must exist in the
;; dataset, or the attachment fails fast.
;;
;; **Fix**: Either rename the dataset columns to match the
;; template (`tc/rename-columns`), or adjust the template to
;; reference the columns the dataset has.

(def template
  (-> (pj/pose nil {:x :x :y :y})
      pj/lay-point))

(-> template
    (pj/with-data {:x [1 2 3] :y [4 5 6]}))

(kind/test-last [(fn [v] (= 3 (:points (pj/svg-summary v))))])

;; ## Horizontal Ranking Bars Draw Biggest-at-Bottom
;;
;; **Symptom**: A horizontal bar chart made with `(pj/coord :flip)`
;; shows the first row of the data at the bottom of the chart.
;; A descending-sorted "top-N" dataset ends up with the biggest
;; bar at the bottom instead of the top.
;;
;; **Cause**: `coord :flip` draws categories bottom-to-top in the
;; order they appear in the data (matching ggplot2's
;; `coord_flip()`).
;;
;; **Fix for now**: Sort the dataset ascending before plotting -- the
;; ascending order shows up top-to-bottom on the flipped axis,
;; so the biggest value lands at the top:

(-> [{:category "A" :value 100}
     {:category "B" :value 50}
     {:category "C" :value 25}]
    (tc/dataset)
    (tc/order-by [:value] :asc)
    (pj/lay-value-bar :category :value)
    (pj/coord :flip))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; A future opt-in option (e.g. `(pj/coord :flip
;; {:reverse-categorical true})`) would spare the sort dance.
;; Tracked in `CHANGELOG.md` Known limitations.

;; ## Stacked Bars Reject Pre-Aggregated Counts
;;
;; **Symptom**: `"lay-bar uses only the x column; do not pass a
;; y column"` when you have already grouped and aggregated the
;; data and want a stacked bar chart of the computed values.
;;
;; **Cause**: `pj/lay-bar {:position :stack}` is count-only -- it
;; bins by `x` internally and sums counts. It has no mode that
;; accepts a pre-computed `y`.
;;
;; **Fix for now**: Either use `(pj/lay-area ... {:position :stack})`
;; on a numeric x (it accepts pre-aggregated `y`), or expand
;; aggregated rows back into count-many duplicates so the count
;; stat sums to the pre-aggregated value. A proper stacked value-bar
;; is tracked in `CHANGELOG.md` Known limitations.

(-> {:x     (concat (range 5) (range 5))
     :y     [1  2  3  4  5  2  2  2  3  3]
     :group (concat (repeat 5 "A") (repeat 5 "B"))}
    (pj/lay-area :x :y {:position :stack :color :group}))

(kind/test-last [(fn [v] (pos? (:polygons (pj/svg-summary v))))])

;; ## Dodge Has No Effect on Point Layers

;; **Symptom**: Adding `:position :dodge` to `pj/lay-point` (or other
;; non-bar marks) does not spread points apart by group -- the plot
;; looks identical to the version without `:position :dodge`.
;;
;; **Cause**: `:position :dodge` is implemented for bar-family marks
;; (`pj/lay-bar`, `pj/lay-value-bar`). On point/line/jitter and
;; several other marks the option is accepted but silently ignored.
;;
;; **Fix for now**: For grouped categorical layouts use
;; `pj/lay-value-bar` (or `pj/lay-bar` when binning a count); dodge
;; works there. To distinguish overlapping points by group on a
;; numeric x, encode the group with `:color`, `:shape`, or
;; pre-compute small offsets in the data. A proper dodge for points
;; is tracked in `CHANGELOG.md` Known limitations.

(-> {:cat   ["A" "A" "B" "B" "C" "C"]
     :y     [10 20 30 40 50 60]
     :group ["a" "b" "a" "b" "a" "b"]}
    (pj/lay-value-bar :cat :y {:color :group :position :dodge}))

(kind/test-last [(fn [v] (= 6 (:polygons (pj/svg-summary v))))])

;; ## Polar Bar Chart Has No Category Labels

;; **Symptom**: A bar chart flipped to polar (`(pj/coord :polar)`)
;; renders as a rose chart, but no category text appears anywhere
;; around the wedges.
;;
;; **Cause**: Polar coord does not currently emit angular tick labels
;; for bar-family marks -- the underlying axis machinery places
;; labels along Cartesian axes that polar replaces with a circular
;; layout, and the equivalent angular ticks are not yet implemented.
;;
;; **Fix for now**: Drop `(pj/coord :polar)` for the labeled view, or
;; combine the polar plot with a separate Cartesian-coord version
;; for the legend. A proper rose-chart label pass is tracked in
;; `CHANGELOG.md` Known limitations.

(-> (rdatasets/datasets-chickwts)
    (pj/pose :feed)
    pj/lay-bar)

(kind/test-last [(fn [v] (pos? (count (filter #{"casein" "horsebean" "linseed"
                                                "meatmeal" "soybean" "sunflower"}
                                              (:texts (pj/svg-summary v))))))])

;; ## Heatmap with Categorical Axes
;;
;; **Symptom**: `"class java.lang.String cannot be cast to class
;; java.lang.Number"` when passing a string column to
;; `pj/lay-tile`.
;;
;; **Cause**: `pj/lay-tile` (and the underlying `:bin2d` stat)
;; requires numeric x and y columns -- the tile boundaries are
;; numeric intervals. Categorical axes are not yet supported for
;; tile.
;;
;; **Fix for now**: Either render a numeric-indexed grid (convert
;; categorical labels to integer ticks and reposition the tick
;; labels afterwards), or approach the problem with
;; `pj/lay-value-bar` coloured by value for a
;; "categorical-heatmap" effect. A proper categorical-axis tile
;; is tracked in `CHANGELOG.md` Known limitations.

;; ## What's Next
;;
;; - [**Inference Rules**](./plotje_book.inference_rules.html) -- how defaults are chosen and overridden
;; - [**API Reference**](./plotje_book.api_reference.html) -- complete function listing with docstrings
;; - [**Exploring Plans**](./plotje_book.exploring_plans.html) -- inspect the data structures behind your plots
;; - [**Gallery**](./plotje_book.gallery.html) -- more working examples by chart type
