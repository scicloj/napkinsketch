;; # Datasets
;;
;; **You do not need to know about datasets to plot with Plotje** --
;; you can pass plain Clojure data (maps, vectors of maps) directly.
;; But understanding datasets is recommended background for four reasons:
;;
;; - **Performance**: datasets are columnar and backed by typed arrays.
;;   For large data (thousands of rows and above), they are significantly
;;   faster than plain Clojure maps and vectors.
;;
;; - **Ergonomics**: many people find that dataset operations -- filtering,
;;   grouping, aggregation -- read more naturally as a pipeline than the
;;   equivalent Clojure core code. This is a matter of taste, but the
;;   convention is widespread in the Clojure data science ecosystem.
;;
;; - **Column types matter for plotting**: dataset columns carry type
;;   information (numeric, categorical, temporal) that Plotje uses
;;   to choose scales, axis formatting, and statistical transforms. A column
;;   of doubles gets a continuous axis; a column of strings gets a
;;   categorical axis. When you pass plain Clojure data, types are
;;   inferred on coercion -- sometimes not the way you expect. Working
;;   with datasets gives you explicit control.
;;
;; - **Understanding Plotje internals**: Plotje coerces your
;;   data to a dataset internally. Knowing what a dataset is helps you
;;   understand column names, types, and the inference rules.
;;
;; This chapter gives a brief introduction. For full documentation, see
;; the [Tablecloth](https://scicloj.github.io/tablecloth/) and
;; [tech.ml.dataset](https://techascent.github.io/tech.ml.dataset/) docs.

(ns plotje-book.datasets
  (:require
   ;; Tablecloth -- dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Plotje -- composable plotting
   [scicloj.plotje.api :as pj]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Plain data works
;;
;; Plotje accepts plain Clojure data -- a map of columns or a
;; vector of row maps. No dataset wrapping needed:

(-> [{:month "Jan" :temperature 5}
     {:month "Feb" :temperature 7}
     {:month "Mar" :temperature 12}
     {:month "Apr" :temperature 16}]
    (pj/lay-line :month :temperature)
    pj/lay-point)

(kind/test-last [(fn [v] (= 4 (:points (pj/svg-summary v))))])

;; This is all you need for quick plots. The rest of this chapter
;; covers datasets, which become useful as your data grows.

;; ## What is a dataset?
;;
;; A dataset is a columnar table backed by efficient typed arrays.
;; It is the Clojure equivalent of an R data frame or a Python pandas
;; DataFrame.
;;
;; The core implementation is
;; [tech.ml.dataset](https://techascent.github.io/tech.ml.dataset/).
;; [Tablecloth](https://scicloj.github.io/tablecloth/) is a
;; higher-level wrapper with a more ergonomic API. Plotje
;; uses Tablecloth internally and in its documentation.

;; ## Creating datasets
;;
;; ### From a map of columns

(tc/dataset {:x [1 2 3 4 5]
             :y [10 20 15 30 25]})

(kind/test-last [(fn [ds] (= 5 (tc/row-count ds)))])

;; ### From a vector of row maps

(tc/dataset [{:name "Alice" :score 92}
             {:name "Bob"   :score 85}
             {:name "Carol" :score 97}])

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; ### From a sequence of row vectors

(tc/dataset [["Alice" 92]
             ["Bob"   85]
             ["Carol" 97]]
            {:column-names [:name :score]})

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; ### From a CSV or URL

(tc/dataset "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
            {:key-fn keyword})

(kind/test-last [(fn [ds] (= 150 (tc/row-count ds)))])

;; (The `:key-fn keyword` option converts CSV string headers like
;; `"Sepal.Length"` to keywords like `:Sepal.Length`. Without it,
;; column names remain strings.)

;; ## The RDatasets collection
;;
;; Many examples in this book use datasets from the
;; [RDatasets](https://vincentarelbundock.github.io/Rdatasets/articles/data.html)
;; collection -- over 2,300 datasets from R packages, available as CSV
;; files.
;;
;; The Clojure bridge is provided by the
;; [metamorph.ml](https://github.com/scicloj/metamorph.ml) library.
;; You can add it as a direct dependency:
;;
;; ```clojure
;; org.scicloj/metamorph.ml {:mvn/version "..."}
;; ```
;;
;; Or use the [Noj](https://scicloj.github.io/noj/) toolkit, which
;; includes it along with other data science libraries.
;;
;; Each dataset has a memoized accessor function. The first call
;; fetches the CSV from the web; subsequent calls return the cached
;; dataset instantly:

(rdatasets/datasets-iris)

(kind/test-last [(fn [ds] (and (tc/dataset? ds)
                               (= 150 (tc/row-count ds))))])

;; Column names are kebab-case keywords (`:sepal-length`, not
;; `Sepal.Length`).
;;
;; A few datasets used throughout this book:
;;
;; | Function | Rows | Description |
;; |:---------|-----:|:------------|
;; | `rdatasets/datasets-iris` | 150 | Iris flower measurements by species |
;; | `rdatasets/reshape2-tips` | 244 | Restaurant tips with bill, day, time, smoker |
;; | `rdatasets/ggplot2-mpg` | 234 | Fuel economy for 38 car models |
;; | `rdatasets/ggplot2-diamonds` | 53,940 | Diamond price, carat, cut, color, clarity |
;; | `rdatasets/gapminder-gapminder` | 1,704 | Country-level life expectancy and GDP |
;; | `rdatasets/datasets-mtcars` | 32 | Motor Trend car road tests |

;; ## Useful Tablecloth operations
;;
;; The examples in this book use a handful of Tablecloth functions.
;; Here is a quick reference:

;; `tc/head` -- first N rows:

(tc/head (rdatasets/datasets-iris) 3)

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/select-rows` -- filter rows by predicate:

(-> (rdatasets/datasets-iris)
    (tc/select-rows #(= "setosa" (:species %))))

(kind/test-last [(fn [ds] (= 50 (tc/row-count ds)))])

;; `tc/group-by` and `tc/aggregate` -- split and summarize:

(-> (rdatasets/datasets-iris)
    (tc/group-by [:species])
    (tc/aggregate {:mean-sl (fn [ds] (/ (reduce + (ds :sepal-length))
                                        (tc/row-count ds)))}))

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/order-by` -- sort rows:

(-> (rdatasets/datasets-mtcars)
    (tc/order-by [:mpg] :desc)
    (tc/head 3))

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/column-names` -- list columns:

(tc/column-names (rdatasets/datasets-iris))

(kind/test-last [(fn [cols] (= 6 (count cols)))])

;; `tc/row-count` -- number of rows:

(tc/row-count (rdatasets/ggplot2-diamonds))

(kind/test-last [(fn [n] (= 53940 n))])

;; ## Datasets and Plotje
;;
;; When you pass plain data to Plotje, it is coerced to a dataset
;; internally. You can also pass a dataset directly -- the result is the
;; same:

;; Plain data:

(-> {:x [1 2 3] :y [4 5 6]}
    (pj/lay-point :x :y))

(kind/test-last [(fn [v] (= 3 (:points (pj/svg-summary v))))])

;; Dataset:

(-> (tc/dataset {:x [1 2 3] :y [4 5 6]})
    (pj/lay-point :x :y))

(kind/test-last [(fn [v] (= 3 (:points (pj/svg-summary v))))])

;; Both produce the same plot. Use whichever is more convenient for
;; your workflow.

;; ## What's Next
;;
;; - [**Pose Model**](./plotje_book.pose_model.html) -- how Plotje composes layers, aesthetics, and layer types
;; - [**Quickstart**](./plotje_book.quickstart.html) -- if you skipped straight here, go back and build your first plots
