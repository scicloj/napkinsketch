# Napkinsketch
Simple and easy plotting

NapkinSketch is a Clojure library for composable plotting, inspired by 
the Grammar of Graphics.

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

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

;; Scatter plot with color grouping
(-> iris
    (sk/lay-point :sepal_length :sepal_width {:color :species}))

;; Multiple layers — shared aesthetics via sk/view
(-> iris
    (sk/view :sepal_length :sepal_width {:color :species})
    sk/lay-point
    sk/lay-lm)
```

## Documentation

See the [book](https://scicloj.github.io/napkinsketch/) for a full guide
covering chart types, configuration, faceting, and API reference.

## Development

See the [development guide](https://scicloj.github.io/napkinsketch/napkinsketch_book.development.html)
for REPL setup, testing, notebook authoring, and deployment.

## Acknowledgments

Napkinsketch builds on several excellent Clojure libraries:

- [Tablecloth](https://scicloj.github.io/tablecloth/) & [dtype-next](https://github.com/cnuernber/dtype-next) — dataset manipulation and high-performance numeric arrays
- [Membrane](https://github.com/phronmophobic/membrane) — rendering and layout
- [Wadogo](https://github.com/scicloj/wadogo) — scales
- [Clojure2d](https://github.com/Clojure2D/clojure2d) — color palettes and gradients
- [Fastmath](https://github.com/generateme/fastmath) — statistics
- [Malli](https://github.com/metosin/malli) — schema validation
- [Kindly](https://scicloj.github.io/kindly/) & [Clay](https://scicloj.github.io/clay/) — notebook rendering

## License

MIT
