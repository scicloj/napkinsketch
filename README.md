# Napkinsketch

Simple and easy plotting

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/napkinsketch/](https://scicloj.github.io/napkinsketch/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/napkinsketch)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/napkinsketch.svg)](https://clojars.org/org.scicloj/napkinsketch)|
|License |[MIT](https://github.com/scicloj/napkinsketch/blob/main/LICENSE)|
|Status |🛠alpha🛠|

## Usage

Add to your `deps.edn`:

```clojure
org.scicloj/napkinsketch {:mvn/version "0.1.0"}
```

Napkinsketch is intended to be used with data-visualization tools
that support the [Kindly](https://scicloj.github.io/kindly) convention
such as [Clay](https://scicloj.github.io/clay/).

## Quick example

```clojure
(require '[tablecloth.api :as tc]
         '[scicloj.napkinsketch.api :as sk])

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv" {:key-fn keyword}))

;; Scatter plot with color grouping and regression lines
(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)
```

## Development

```bash
clojure -M:dev -m nrepl.cmdline   # start REPL
./run_tests.sh                    # run tests
```

## License

MIT
