
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
```clj
(ns readme
  (:require [scicloj.napkinsketch.api :as sk]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]))
```
Line chart with point markers from plain Clojure data:
```clj
(-> [{:month "Jan" :sales 120}
     {:month "Feb" :sales 95}
     {:month "Mar" :sales 140}
     {:month "Apr" :sales 175}
     {:month "May" :sales 160}
     {:month "Jun" :sales 210}]
    (sk/lay-line :month :sales)
    sk/lay-point
    (sk/options {:title "Monthly Sales"}))
```
![](readme_files/image0.svg)

Scatter plot matrix (SPLOM) — all pairwise combinations with color grouping:
```clj
(-> (rdatasets/datasets-iris)
    (sk/frame {:color :species})
    sk/lay-point
    (sk/frame (sk/cross [:sepal-length :sepal-width
                         :petal-length :petal-width]
                        [:sepal-length :sepal-width
                         :petal-length :petal-width]))
    (sk/options {:title "Iris SPLOM"}))
```
![](readme_files/image1.svg)


## Documentation

[Full documentation](https://scicloj.github.io/napkinsketch/)


## License

Copyright © 2025-2026 Scicloj

Distributed under the MIT License.