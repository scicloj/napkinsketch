;; # Options and Scopes
;;
;; Napkinsketch has three kinds of values you set, each answering
;; a different question:
;;
;; - **Layer options** -- *how is this layer drawn?* Its
;;   aesthetics (`:color`, `:size`), its method parameters
;;   (`:bandwidth`, `:bins`), its data, its grouping and
;;   position. Per-layer by nature, but scopable up to apply
;;   across multiple layers.
;; - **Plot options** -- *what describes this plot as a whole?*
;;   Its title and labels, its axis scales, its coordinate
;;   system, its facets, its annotations. Per-plot by nature;
;;   there is one of each per plot.
;; - **Configuration** -- *what are the rendering defaults?*
;;   Palette, theme, default dimensions, color scale. Shared
;;   across every plot you render; any one plot can override a
;;   configuration key.
;;
;; The distinguishing test is simple: **can this kind of value
;; meaningfully have a cross-plot default?** A title cannot --
;; each plot needs its own. A palette can -- a whole project can
;; share one. A layer's color mapping is per-layer, but when
;; several layers should share it, you lift it to a wider scope.

;; ## Setup

(ns napkinsketch-book.options-and-scopes
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; A helper to inspect sketch structure. It shows `:mapping`,
;; `:views`, `:layers`, and `:opts` -- the full record also has
;; `:data` and `:kindly/f`, which this helper omits for
;; readability.

(defn sk-summary
  "Print sketch structure without :data and :kindly/f (for readability)."
  [sk]
  (-> (select-keys sk [:mapping :views :layers :opts])
      (update :views (partial mapv #(dissoc % :data)))
      kind/pprint))

;; ---
;; ## Layer options
;;
;; Layer options describe a specific layer. They include:
;;
;; - **The method** -- the drawing recipe: a mark (point, line,
;;   bar, histogram, ...), a stat (identity, lm, density,
;;   binning, ...), and a position adjustment (dodge, stack,
;;   jitter). The method is chosen by which `sk/lay-*` function
;;   you call; its constituents can be overridden via `:mark`,
;;   `:stat`, and `:position` keys in the opts map.
;; - **Method parameters** -- knobs specific to the method, like
;;   `:bandwidth` for `sk/lay-kde` or `:bins` for
;;   `sk/lay-histogram`.
;; - **Aesthetics** -- how the method maps data to visuals:
;;   `:color`, `:size`, `:alpha`, `:shape`, `:group`. Either
;;   mapped from a column (`:color :species`) or given as a
;;   constant (`:alpha 0.3`).
;; - **Data** -- a per-layer `:data` key, if the layer should
;;   use a different dataset from the rest of the sketch.
;;
;; The primary way to set them is in the opts map of `sk/lay-*`:

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width {:color :species})
    sk-summary)

(kind/test-last
 [(fn [m]
    (= :species (get-in m [:views 0 :layers 0 :mapping :color])))])

;; ### Scope: generalizing layer options upward
;;
;; Method parameters, aesthetics, and per-layer data can also be
;; set at wider **scopes** when they should apply to more than
;; one layer. The scope hierarchy has three levels, from narrow
;; to broad:
;;
;; | Scope  | Set via           | Reaches                    |
;; |:-------|:------------------|:---------------------------|
;; | Layer  | `sk/lay-*` opts   | that one layer             |
;; | View   | `sk/view` opts    | every layer on that view   |
;; | Sketch | `sk/sketch` opts  | every layer on every view  |
;;
;; When the same key is set at multiple scopes, narrower scope
;; wins.
;;
;; The method itself is chosen per layer -- you pick which
;; `sk/lay-*` to call, and that is where the method belongs.
;; [Core Concepts](./napkinsketch_book.core_concepts.html)
;; teaches how layers themselves (sketch-level vs view-level)
;; combine, and the detailed combination rules for each category
;; of layer option.

;; ---
;; ## Plot options
;;
;; Plot options describe the plot as a whole: its title, labels,
;; axis scales, coordinate system, facets, annotations. A plot
;; has one of each -- there is no scope here, because there is
;; nothing to vary over.
;;
;; Five functions write plot options to the sketch's `:opts`
;; field:
;;
;; - `sk/options` -- plot text (title, subtitle, caption, axis
;;   and legend labels) and panel dimensions. It also accepts
;;   configuration keys as per-plot overrides (see Configuration
;;   below).
;; - `sk/scale` -- axis scale (log, categorical, fixed domain).
;; - `sk/coord` -- coordinate system (cartesian, flipped, polar,
;;   fixed).
;; - `sk/facet` and `sk/facet-grid` -- split the plot into panels
;;   by a column.
;; - `sk/annotate` -- reference lines and shaded bands.
;;
;; **Planned refactor:** Before 0.1.0, annotations will become
;; regular layers (`sk/lay-rule-h`, `sk/lay-rule-v`,
;; `sk/lay-band-h`, `sk/lay-band-v`), scopable like any other
;; layer. The current `sk/annotate` API will be replaced, and
;; annotations will no longer be plot options.

(-> (rdatasets/datasets-iris)
    (sk/lay-point :sepal-length :sepal-width)
    (sk/options {:title "Iris"})
    (sk/coord :flip)
    sk-summary)

(kind/test-last
 [(fn [m]
    (and (= "Iris" (get-in m [:opts :title]))
         (= :flip (get-in m [:opts :coord]))))])

;; Both the title and the coordinate system landed in `:opts`.
;; Neither is at a scope; they belong to the plot as a whole.
;;
;; A note on faceted plots: a scale has two parts that behave
;; differently across panels.
;;
;; - Scale **type** (log, categorical, linear, etc.) is shared
;;   across all panels -- if you set `sk/scale :x :log` on a
;;   faceted sketch, every panel has a log x-axis.
;; - Scale **domain** (the numeric range actually shown) is
;;   computed per panel by default, so different panels may
;;   display different numeric ranges.
;;
;; A note on scope of scales and coord: whether `sk/scale` and
;; `sk/coord` should support scope variation is an open design
;; question; the underlying plan structure allows per-panel
;; scales, but the current API treats them as plot-level.
;;
;; A note on terminology: other chapters may refer to these
;; values as **plot-level** -- a category name. That is not the
;; same as **sketch-level**, which names a scope position (the
;; top of the layer-options scope hierarchy) within another
;; category. Trying to set a plot option inside an `sk/lay-*`
;; opts map -- for example `{:x-scale {:type :log}}` -- is a
;; category mistake: plot options belong in `:opts` via their
;; dedicated functions above.

;; ---
;; ## Configuration
;;
;; Configuration controls the rendering defaults -- palette,
;; theme, default dimensions, color scale. A configuration value
;; is resolved at render time from a layered stack of sources,
;; from highest priority to lowest:
;;
;; 1. **Plot options** on the sketch (`sk/options`) -- a
;;    per-plot override that wins for that one plot.
;; 2. **Thread-local overrides** via `sk/with-config`.
;; 3. **Global overrides** via `sk/set-config!`.
;; 4. **Project file** (`napkinsketch.edn`), if present.
;; 5. **Library defaults** -- the baseline shipped with
;;    Napkinsketch.
;;
;; Sources 2-5 sit outside any specific sketch and carry across
;; every plot you render. Source 1 is how a specific sketch dips
;; into the chain to override a configuration key for itself --
;; `(sk/options {:palette :dark2})` sets `:palette` on one
;; sketch, and at render time wins over any palette set through
;; the other four sources.
;;
;; The [Configuration](./napkinsketch_book.configuration.html)
;; chapter covers each source in depth and lists every
;; configuration key.
;;
;; `sk/config` returns the resolved configuration -- the merged
;; result of all five sources above.

(select-keys (sk/config) [:width :height :margin])

(kind/test-last
 [(fn [m]
    (and (number? (:width m))
         (number? (:height m))
         (number? (:margin m))))])

;; ---
;; ## A worked example
;;
;; The sketch below touches two categories explicitly. The third,
;; configuration, shows up at render time.

(def demo
  (-> (rdatasets/datasets-iris)
      ;; layer option -> layer-scope color mapping
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      ;; plot options -> :opts
      (sk/options {:title "Iris measurements"})
      (sk/coord :flip)))

;; The rendered plot:

demo

;; The sketch structure:

(sk-summary demo)

(kind/test-last
 [(fn [m]
    (and
     ;; layer-scope mapping
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     ;; plot options
     (= "Iris measurements" (get-in m [:opts :title]))
     (= :flip (get-in m [:opts :coord]))))])

;; Reading the summary:
;;
;; - `:color :species` is inside the layer's mapping -- a layer
;;   option at layer scope.
;; - `:title` and `:coord` are in `:opts` -- plot options, no
;;   scope.
;; - Configuration does not appear in the sketch. The renderer
;;   will consult `(sk/config)` for theme, default dimensions,
;;   and other defaults.

;; ---
;; ## Coming from ggplot2
;;
;; For readers familiar with ggplot2, the three categories map
;; roughly as follows:
;;
;; | ggplot2 construct                            | napkinsketch category       |
;; |:---------------------------------------------|:----------------------------|
;; | `geom_*(aes(...))`                           | layer option (layer scope)  |
;; | `ggplot(aes(...))`                           | layer option (sketch scope) |
;; | `+ geom_*()`                                 | a layer                     |
;; | `+ scale_*()`, `+ coord_*()`, `+ facet_*()`  | plot option                 |
;; | `+ labs(...)`, one-off `+ theme(...)`        | plot option                 |
;; | `theme_set(...)`, `options(...)`             | configuration               |

;; ---
;; ## See also
;;
;; - [Core Concepts](./napkinsketch_book.core_concepts.html) --
;;   the scope hierarchy for layer options in full.
;; - [Sketch Rules](./napkinsketch_book.sketch_rules.html) --
;;   precise rules for each option function.
;; - [Configuration](./napkinsketch_book.configuration.html) --
;;   the four configuration sources and every configuration key.
;; - [Glossary](./napkinsketch_book.glossary.html) -- definitions
;;   of layer option, plot option, and configuration.
