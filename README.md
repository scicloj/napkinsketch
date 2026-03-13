# napkinsketch

Composable plotting in Clojure.

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/napkinsketch/](https://scicloj.github.io/napkinsketch/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/napkinsketch)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/napkinsketch.svg)](https://clojars.org/org.scicloj/napkinsketch)|
|License |[MIT](https://github.com/scicloj/napkinsketch/blob/main/LICENSE)|
|Status |🛠alpha🛠|

## Quick Example

```clojure
(require '[tablecloth.api :as tc]
         '[scicloj.napkinsketch.api :as sk])

(def iris (tc/dataset "https://...iris.csv" {:key-fn keyword}))

;; Scatter plot with color grouping and regression lines
(-> iris
    (sk/view [[:sepal_length :sepal_width]])
    (sk/lay (sk/point {:color :species})
            (sk/lm {:color :species}))
    sk/plot)
```

## Features

### Marks

| Mark | Constructor | Default stat |
|:-----|:------------|:-------------|
| Scatter | `sk/point` | `:identity` |
| Line | `sk/line` | `:identity` |
| Histogram | `sk/histogram` | `:bin` |
| Bar | `sk/bar` | `:count` |
| Stacked bar | `sk/stacked-bar` | `:count` |
| Value bar | `sk/value-bar` | `:identity` |
| Area | `sk/area` | `:identity` |
| Density | `sk/density` | `:kde` |
| Boxplot | `sk/boxplot` | `:boxplot` |
| Violin | `sk/violin` | `:violin` |
| Ridgeline | `sk/ridgeline` | `:violin` |
| Heatmap / tile | `sk/tile` | `:bin2d` |
| Text labels | `sk/text` | `:identity` |
| Error bars | `sk/errorbar` | `:identity` |
| Lollipop | `sk/lollipop` | `:identity` |
| Linear regression | `sk/lm` | `:lm` |
| LOESS smoothing | `sk/loess` | `:loess` |

### Aesthetics

- `:color` — map to a column (categorical palette or continuous viridis gradient)
- `:size` — constant or column-mapped point radius
- `:alpha` — constant or column-mapped transparency
- `:shape` — column-mapped point shape
- `:group` — explicit grouping without color

### Coordinates

- `:cartesian` — standard x→right, y→up (default)
- `:flip` — swap x and y axes
- `:polar` — radial: x→angle, y→radius (rose charts, polar scatter)

### Scales

- `:linear` — continuous (default for numeric data)
- `:log` — logarithmic
- `:categorical` — band scale for discrete values
- Fixed domains via `(sk/scale views :y {:domain [0 100]})`
- Automatic date axis for temporal columns (`LocalDate`, `LocalDateTime`, `Instant`, `java.util.Date`)

### Layout

- `sk/facet` — split by one column into a row or column of panels
- `sk/facet-grid` — split by two columns into a row × column grid
- `sk/pairs` / `sk/cross` — pairwise or cartesian column combinations
- `sk/distribution` — diagonal histograms for SPLOM layouts
- Free/shared scale options (`:free-x`, `:free-y`, `:free`)

### Customization

- `sk/labs` — set title, x-label, y-label
- `sk/scale` — scale type, fixed domain, axis label
- Custom color palette: `(sk/plot views {:palette ["#E74C3C" "#3498DB" "#2ECC71"]})`
- Plot dimensions: `(sk/plot views {:width 800 :height 500})`

### Annotations

- `sk/rule-v`, `sk/rule-h` — vertical/horizontal reference lines
- `sk/band-v`, `sk/band-h` — vertical/horizontal shaded bands

### Architecture

```
views → sketch → membrane → figure
```

- **Sketch** — plain Clojure map with data-space geometry, domains, ticks, legend. Serializable, inspectable.
- **Membrane** — drawable tree (membrane library). Intermediate step for SVG rendering.
- **Figure** — final output (SVG hiccup via `kind/hiccup`)
- Extensible via multimethods: `compute-stat`, `extract-layer`, `render-layer`, `render-figure`, `make-scale`, `make-coord`

## Usage

Add to your `deps.edn`:

```clojure
org.scicloj/napkinsketch {:mvn/version "0.1.0"}
```

## Development

```bash
clojure -M:dev -m nrepl.cmdline   # start REPL
./run_tests.sh                     # run tests
```

## License

MIT
