;; # Datasets
;;
;; You do not need to know about datasets to plot with napkinsketch —
;; you can pass plain Clojure data (maps, vectors of maps) directly.
;; But understanding datasets is recommended background for two reasons:
;;
;; - **Performance and ergonomics**: datasets are columnar, typed, and
;;   memory-efficient. For anything beyond a handful of rows, they are
;;   faster and more convenient than plain maps.
;;
;; - **Understanding napkinsketch internals**: napkinsketch coerces your
;;   data to a dataset internally. Knowing what a dataset is helps you
;;   understand column names, types, and the inference rules.
;;
;; This chapter gives a brief introduction. For full documentation, see
;; the [Tablecloth](https://scicloj.github.io/tablecloth/) and
;; [tech.ml.dataset](https://techascent.github.io/tech.ml.dataset/) docs.

(ns napkinsketch-book.datasets
  (:require
   [tablecloth.api :as tc]
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.napkinsketch.api :as sk]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Plain data works
;;
;; Napkinsketch accepts plain Clojure data — a map of columns or a
;; vector of row maps. No dataset wrapping needed:

(-> [{:month "Jan" :temperature 5}
     {:month "Feb" :temperature 7}
     {:month "Mar" :temperature 12}
     {:month "Apr" :temperature 16}]
    (sk/lay-line :month :temperature)
    sk/lay-point)

(kind/test-last [(fn [v] (= 4 (:points (sk/svg-summary v))))])

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
;; higher-level wrapper with a more ergonomic API. Napkinsketch
;; uses tablecloth internally and in its documentation.

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

;; ### From a CSV or URL
;;
;; Use `:key-fn keyword` to convert string column headers to keywords:

(def iris-from-url
  (tc/dataset "https://vincentarelbundock.github.io/Rdatasets/csv/datasets/iris.csv"
              {:key-fn keyword}))

(tc/row-count iris-from-url)

(kind/test-last [(fn [n] (= 150 n))])

;; ## The RDatasets collection
;;
;; Many examples in this book use datasets from the
;; [RDatasets](https://vincentarelbundock.github.io/Rdatasets/articles/data.html)
;; collection — over 2,300 datasets from R packages, available as CSV
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

;; ## Useful tablecloth operations
;;
;; The examples in this book use a handful of tablecloth functions.
;; Here is a quick reference:

;; `tc/head` — first N rows:

(tc/head (rdatasets/datasets-iris) 3)

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/select-rows` — filter rows by predicate:

(-> (rdatasets/datasets-iris)
    (tc/select-rows #(= "setosa" (:species %)))
    tc/row-count)

(kind/test-last [(fn [n] (= 50 n))])

;; `tc/group-by` and `tc/aggregate` — split and summarize:

(-> (rdatasets/datasets-iris)
    (tc/group-by [:species])
    (tc/aggregate {:mean-sl (fn [ds] (/ (reduce + (ds :sepal-length))
                                        (tc/row-count ds)))}))

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/order-by` — sort rows:

(-> (rdatasets/datasets-mtcars)
    (tc/order-by [:mpg] :desc)
    (tc/head 3))

(kind/test-last [(fn [ds] (= 3 (tc/row-count ds)))])

;; `tc/column-names` — list columns:

(tc/column-names (rdatasets/datasets-iris))

(kind/test-last [(fn [cols] (= 6 (count cols)))])

;; `tc/row-count` — number of rows:

(tc/row-count (rdatasets/ggplot2-diamonds))

(kind/test-last [(fn [n] (= 53940 n))])

;; ## Datasets and napkinsketch
;;
;; When you pass plain data to napkinsketch, it is coerced to a dataset
;; internally. You can also pass a dataset directly — the result is the
;; same:

;; Plain data:

(-> {:x [1 2 3] :y [4 5 6]}
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Dataset:

(-> (tc/dataset {:x [1 2 3] :y [4 5 6]})
    (sk/lay-point :x :y))

(kind/test-last [(fn [v] (= 3 (:points (sk/svg-summary v))))])

;; Both produce the same plot. Use whichever is more convenient for
;; your workflow.

;; ## What's Next
;;
;; - [**The Sketch Model**](./napkinsketch_book.sketch_model.html) — how napkinsketch composes layers, aesthetics, and methods
;; - [**Quickstart**](./napkinsketch_book.quickstart.html) — if you skipped straight here, go back and build your first plots

