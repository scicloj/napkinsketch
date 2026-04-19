;; # Troubleshooting
;;
;; Common mistakes and how to fix them.

(ns napkinsketch-book.troubleshooting
  (:require
   ;; rdatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

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

;; ## Wrong Chart Type from Inference
;;
;; **Symptom**: `sk/view` produces a chart type that isn't what you
;; wanted -- a boxplot when you wanted individual points, a line
;; when you wanted a scatter.
;;
;; **Cause**: `sk/view` infers the method from column types. The
;; defaults fit the most common use case for each column-type pair
;; (see [Inference Rules](./napkinsketch_book.inference_rules.html)),
;; but they can be overridden.
;;
;; **Fix**: Use an explicit `sk/lay-*` function. For example, a
;; categorical x with a numerical y defaults to a boxplot:

(-> (rdatasets/datasets-iris)
    (sk/view :species :sepal-width))

(kind/test-last [(fn [v] (pos? (:lines (sk/svg-summary v))))])

;; Use `sk/lay-point` if you want the individual points instead:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :species :sepal-width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

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
;; (sk/lay-line data :day :score {:color :subject})
;;
;; ;; Discrete groups (correct):
;; (sk/lay-line data :day :score {:color :subject
;;                                :color-type :categorical})
;; ```
;;
;; See [Inference Rules: Overriding color type](./napkinsketch_book.inference_rules.html)
;; for a worked example.

;; ## x-Only Methods Do Not Accept a y Column
;;
;; **Symptom**: `"lay-histogram uses only the x column"` error.
;;
;; **Cause**: Histogram, bar, density, and rug methods use only the
;; x column. Passing a y column is an error.
;;
;; **Fix**: Remove the y column:
;;
;; ```clojure
;; ;; Wrong:
;; (sk/lay-histogram data :sepal-length :sepal-width)
;;
;; ;; Correct:
;; (sk/lay-histogram data :sepal-length)
;; ```

;; ## Categorical Column with Log Scale
;;
;; **Symptom**: `"Log scale requires numeric data"` error.
;;
;; **Cause**: Log scales only work with numerical columns. Categorical
;; columns (strings, keywords) cannot be log-transformed.
;;
;; **Fix**: Use a numerical column for log scale, or remove the log
;; scale for categorical data.

;; ## Polar Coordinates with Unsupported Marks
;;
;; **Symptom**: Errors or unexpected output with `(sk/coord :polar)`.
;;
;; **Cause**: Not all marks support polar coordinates. Currently
;; `:point`, `:bar`, and `:line` work well with polar.
;;
;; **Fix**: Check the [Polar](./napkinsketch_book.polar.html) chapter for supported marks.

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
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    (sk/options {:tooltip true}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## What's Next
;;
;; - [**API Reference**](./napkinsketch_book.api_reference.html) -- complete function listing with docstrings
;; - [**Exploring Plans**](./napkinsketch_book.exploring_plans.html) -- inspect the data structures behind your plots
