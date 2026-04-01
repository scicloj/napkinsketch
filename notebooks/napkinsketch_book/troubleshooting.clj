;; # Troubleshooting
;;
;; Common mistakes and how to fix them.

(ns napkinsketch-book.troubleshooting
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Column Not Found
;;
;; **Symptom**: `"Column :foo not found in dataset"` error.
;;
;; **Cause**: The column name does not exist in the dataset. CSV
;; headers are strings by default — without `{:key-fn keyword}`,
;; columns have string names like `"sepal_length"` instead of
;; `:sepal_length`.
;;
;; **Fix**: Pass `{:key-fn keyword}` when loading the dataset:
;;
;; ```clojure
;; (tc/dataset "data.csv" {:key-fn keyword})
;; ```
;;
;; You can check available columns with:

(tc/column-names data/iris)

(kind/test-last [(fn [v] (some #{:sepal_length} v))])

;; ## Wrong Chart Type from Inference
;;
;; **Symptom**: `sk/view` produces a scatter when you expected a
;; boxplot, or a histogram when you expected a bar chart.
;;
;; **Cause**: `sk/view` infers the chart type from column types.
;; Mixed types (categorical x, numerical y) produce a scatter,
;; not a boxplot.
;;
;; **Fix**: Use an explicit `sk/lay-*` function instead of relying
;; on inference:

;; This infers a scatter (categorical x numerical):

(-> data/iris
    (sk/view :species :sepal_width))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Use `sk/lay-boxplot` if you want a boxplot:

(-> data/iris
    (sk/lay-boxplot :species :sepal_width))

(kind/test-last [(fn [v] (pos? (:lines (sk/svg-summary v))))])

;; See the [Inference Rules](./napkinsketch_book.inference_rules.html) chapter for the
;; full set of rules.

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
;; (sk/lay-histogram data :sepal_length :sepal_width)
;;
;; ;; Correct:
;; (sk/lay-histogram data :sepal_length)
;; ```

;; ## Categorical Column with Log Scale
;;
;; **Symptom**: `"Log scale requires numerical data"` error.
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

(-> data/iris
    (sk/lay-point :sepal_length :sepal_width {:color :species})
    (sk/options {:tooltip true}))

(kind/test-last [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## What's Next
;;
;; - [**API Reference**](./napkinsketch_book.api_reference.html) — complete function listing with docstrings
;; - [**Exploring Sketches**](./napkinsketch_book.exploring_sketches.html) — inspect the data structures behind your plots
