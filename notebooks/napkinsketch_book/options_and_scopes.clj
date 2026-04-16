;; # Options and Scopes
;;
;; When you build a sketch, the values you set land in one of three
;; places:
;;
;; - **The scope hierarchy** -- mappings, layers, and data, scoped at
;;   sketch, view, or layer level.
;; - **The `:opts` field** -- plot-level options that configure the
;;   whole rendered plot.
;; - **Configuration** -- defaults that live outside any single sketch
;;   (library defaults, project file, session overrides).
;;
;; Earlier chapters introduced the scope hierarchy ([Core Concepts](./napkinsketch_book.core_concepts.html))
;; and the configuration precedence chain is covered in the
;; [Configuration](./napkinsketch_book.configuration.html) chapter.
;; This chapter is the bridge -- it places the three together and
;; shows which knob belongs where.

(ns napkinsketch-book.options-and-scopes
  (:require
   ;; Kindly -- notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; RDatasets -- standard datasets
   [scicloj.metamorph.ml.rdatasets :as rdatasets]
   ;; Napkinsketch -- composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; ## Setup

(def iris (rdatasets/datasets-iris))

;; A helper to inspect sketch structure without the dataset.

(defn sk-summary
  "Print sketch structure without :data (for readability)."
  [sk]
  (-> (select-keys sk [:mapping :views :layers :opts])
      (update :views (partial mapv #(dissoc % :data)))
      kind/pprint))

;; ---
;; ## The scope hierarchy
;;
;; Mappings, layers, and data each have a three-level scope. Values
;; at a higher scope flow down into every lower scope; values at a
;; lower scope override values at a higher one.
;;
;; | Scope | How to set | Who sees it |
;; |:------|:-----------|:------------|
;; | Sketch | `sk/sketch` mapping, bare `sk/lay-*`, first argument as data | every view, every layer |
;; | View | `sk/view` opts, `sk/lay-*` with columns | layers on that view |
;; | Layer | `sk/lay-*` opts | that layer only |
;;
;; A `:color` mapping placed in the options map of `sk/lay-*` becomes
;; a layer-level mapping. The same `:color` placed in `sk/sketch`
;; becomes a sketch-level mapping and flows into every layer.
;;
;; [Core Concepts](./napkinsketch_book.core_concepts.html) teaches
;; this hierarchy in full. The key point here: scope is about
;; **how far a value flows inside the sketch**.

;; ---
;; ## The `:opts` field
;;
;; The sketch's fifth field, `:opts`, holds **plot-level options** --
;; values that configure the whole rendered plot and cannot be scoped
;; down to a view or layer.
;;
;; Plot-level options are set by:
;;
;; - `sk/options` -- title, subtitle, caption, axis labels, width,
;;   height, theme overrides, palette, legend position.
;; - `sk/scale` -- axis scales (log, categorical, fixed domain).
;; - `sk/coord` -- coordinate system (cartesian, flipped, polar, fixed).
;; - `sk/facet` and `sk/facet-grid` -- panel splits by column.
;; - `sk/annotate` -- reference lines and bands.
;;
;; A plot-level option applies to the whole plot uniformly. There is
;; no "view-level title" or "layer-level log scale" -- the title is
;; a property of the plot, and the scale applies to every panel that
;; shares the aesthetic.

(-> iris
    (sk/lay-point :sepal-length :sepal-width)
    (sk/options {:title "Iris"})
    (sk/scale :x :log)
    sk-summary)

(kind/test-last
 [(fn [m]
    (and (= "Iris" (get-in m [:opts :title]))
         (= {:type :log} (get-in m [:opts :x-scale]))))])

;; Both the title and the log scale landed in `:opts`. Neither
;; belongs to the scope hierarchy.

;; ---
;; ## Configuration
;;
;; **Configuration** controls rendering behavior that carries across
;; many plots: palette defaults, theme defaults, default dimensions,
;; color scale defaults. It lives **outside** the sketch entirely.
;;
;; Configuration is layered, with the following precedence (highest
;; first):
;;
;; 1. Plot options set on the sketch (`sk/options`).
;; 2. Thread-local overrides (`sk/with-config`).
;; 3. Global overrides (`sk/set-config!`).
;; 4. Project file (`napkinsketch.edn`).
;; 5. Library defaults.
;;
;; A configuration key can be overridden on a specific plot by
;; writing it as a plot option. For example, `:palette` is a
;; configuration key; `(sk/options {:palette :dark2})` sets it for
;; one plot only.
;;
;; The [Configuration](./napkinsketch_book.configuration.html)
;; chapter covers the precedence chain and lists every configuration
;; key.

;; ---
;; ## Where each concept lives
;;
;; | Concept | Lives in | Notes |
;; |:--------|:---------|:------|
;; | Aesthetic mapping | scope hierarchy (sketch, view, or layer) | `:color`, `:size`, `:alpha`, `:shape`, `:group` |
;; | Layer | scope hierarchy (sketch or view) | structural, not an option |
;; | Data | scope hierarchy (sketch, view, or layer) | `:data` key at any scope |
;; | Title, subtitle, labels, width, height, theme | `:opts` | plot-level |
;; | Axis scale, coordinate system, facets, annotations | `:opts` | plot-level, set via dedicated functions |
;; | Palette, theme, dimension defaults | configuration | carries across plots |
;;
;; The three columns of "lives in" correspond to the three places
;; from the chapter opening. The distinction matters because it
;; determines **how** you change each value:
;;
;; - To change an aesthetic for one group of layers -- pick a scope
;;   in the hierarchy.
;; - To change something about the whole plot -- use a plot-level
;;   option.
;; - To change a default for many plots -- use configuration.

;; ---
;; ## A worked example
;;
;; The sketch below touches all three places: a layer-level
;; aesthetic mapping, a plot-level title and scale, and (at render
;; time) configuration for palette and theme.

(def demo
  (-> iris
      (sk/lay-point :sepal-length :sepal-width {:color :species})
      (sk/options {:title "Iris measurements"})
      (sk/scale :x :log)))

;; The rendered plot:

demo

;; The sketch structure:

(sk-summary demo)

(kind/test-last
 [(fn [m]
    (and
     ;; layer-level mapping
     (= :species (get-in m [:views 0 :layers 0 :mapping :color]))
     ;; plot-level options
     (= "Iris measurements" (get-in m [:opts :title]))
     (= {:type :log} (get-in m [:opts :x-scale]))))])

;; Reading the summary:
;;
;; - `:color :species` landed inside the layer's mapping -- it is a
;;   layer-level mapping, so it is scoped to this one layer.
;; - `:title` and `:x-scale` landed in `:opts` -- they are
;;   plot-level, so they apply to the whole rendered plot.
;;
;; Configuration does not appear in the sketch structure. The
;; theme, default dimensions, and other defaults are consulted by
;; the renderer from the resolved configuration:

(select-keys (sk/config) [:width :height :margin])

(kind/test-last
 [(fn [m]
    (and (number? (:width m))
         (number? (:height m))
         (number? (:margin m))))])

;; Pseudocode (Clojure syntax) for setting a configuration override
;; that would carry across many plots:
;;
;;     (sk/set-config! {:palette :dark2})
;;
;; The sketch would not change; the rendered result would pick up
;; the new palette because rendering consults configuration.

;; ---
;; ## When to use which
;;
;; A short decision guide:
;;
;; - A visual property that varies by data point or group (color by
;;   species, size by population) -- an aesthetic mapping. Choose a
;;   scope based on how widely it should apply.
;; - A property of the whole plot that does not depend on row-level
;;   data (title, log axis, flipped coordinates, facets, annotation
;;   lines) -- a plot-level option.
;; - A default to carry across many plots (palette, theme, default
;;   dimensions) -- configuration.

;; ---
;; ## See also
;;
;; - [Core Concepts](./napkinsketch_book.core_concepts.html) --
;;   the scope hierarchy in full.
;; - [Sketch Rules](./napkinsketch_book.sketch_rules.html) --
;;   precise rules for each category, rule by rule.
;; - [Configuration](./napkinsketch_book.configuration.html) --
;;   the configuration precedence chain, with the full list of keys
;;   and plot options.
;; - [Glossary](./napkinsketch_book.glossary.html) -- definitions
;;   of "aesthetic", "mapping", "plot option", "configuration".
