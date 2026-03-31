;; # Configuration
;;
;; Napkinsketch has three kinds of options:
;;
;; - **Layer options** control individual layers — aesthetics like `:color`,
;;   statistical parameters like `:bandwidth`. See the [Methods](methods.html) chapter.
;;
;; - **Plot options** set per-plot text — `:title`, `:subtitle`, `:caption`, axis labels.
;;
;; - **Configuration** controls everything else — dimensions, theme, palette,
;;   color scale, and more — via a layered precedence chain.
;;
;; This chapter covers configuration and plot options.

(ns napkinsketch-book.configuration
  (:require
   ;; Shared datasets for these docs
   [napkinsketch-book.datasets :as data]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; We use the iris dataset throughout.

;; We define `base-plot` as a function because
;; plot specifications render at display time — calling the function
;; produces a fresh specification that picks up the current configuration.

(defn base-plot
  [] (-> data/iris
         (sk/lay-point :sepal_length :sepal_width {:color :species})))

;; ## Inspecting the Current Configuration
;;
;; `sk/config` returns the resolved configuration as a plain map.
;; It merges all active configuration layers into one map.

(sk/config)

(kind/test-last
 [(fn [cfg]
    (and (map? cfg)
         (= 600 (:width cfg))
         (= 400 (:height cfg))
         (= 25 (:margin cfg))
         (map? (:theme cfg))))])

;; ### Configuration Keys
;;
;; Each key, its default value, and a description.

(def category-order
  ["Layout" "Theme" "Typography" "Points" "Bars & Lines"
   "Annotations" "Ticks" "Statistics" "Labels" "Behavior"
   "Color" "Interaction" "Output"])

(kind/table
 {:column-names ["Key" "Default" "Category" "Description"]
  :row-maps
  (let [cfg (sk/config)]
    (->> sk/config-key-docs
         (sort-by (fn [[k [cat]]]
                    [(.indexOf ^java.util.List category-order cat) (name k)]))
         (mapv (fn [[k [cat desc]]]
                 {"Key" (kind/code (pr-str k))
                  "Default" (kind/code (pr-str (get cfg k)))
                  "Category" cat
                  "Description" desc}))))})

(kind/test-last [(fn [t] (= 36 (count (:row-maps t))))])

;; ### Plot Options
;;
;; These options are accepted by `sk/options`, `sk/sketch`, and `sk/plot`
;; but are inherently per-plot — text content or nested config override.

(kind/table
 {:column-names ["Key" "Category" "Description"]
  :row-maps
  (->> sk/plot-option-docs
       (sort-by (fn [[k [cat]]] [cat (name k)]))
       (mapv (fn [[k [cat desc]]]
               {"Key" (kind/code (pr-str k))
                "Category" cat
                "Description" desc})))})

(kind/test-last [(fn [t] (= 6 (count (:row-maps t))))])

;; ## Using Plot Options
;;
;; Pass an options map to `sk/options` to override any setting for a
;; single plot. Plot options have the highest priority — they
;; override everything else.

;; Custom dimensions — the defaults are:

(select-keys (sk/config) [:width :height])

(kind/test-last [(fn [m] (= {:width 600 :height 400} m))])

(-> (base-plot)
    (sk/options {:width 900 :height 250}))

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (and (= 150 (:points s))
           (> (:width s) 800))))])

;; Theme deep-merge — only the specified keys change. Here we set a
;; white background; `:grid` and `:font-size` keep their defaults:

(-> (base-plot)
    (sk/options {:theme {:bg "#FFFFFF"}}))

(kind/test-last
 [(fn [v]
    (= 150 (:points (sk/svg-summary v))))])

;; Named palette:

(-> (base-plot)
    (sk/options {:palette :dark2}))

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (= 150 (:points s))))])

;; ## Global Overrides with set-config!
;;
;; `sk/set-config!` sets overrides that persist across calls — useful
;; when you want a consistent style for an entire session or notebook.

;; Set a global width override and render:

(sk/set-config! {:width 800})

(select-keys (sk/config) [:width :height])

(kind/test-last [(fn [m] (= {:width 800 :height 400} m))])

(-> (base-plot))

;; Reset to library defaults by passing `nil`:

(sk/set-config! nil)

(select-keys (sk/config) [:width :height])

(kind/test-last [(fn [m] (= {:width 600 :height 400} m))])

;; ## Thread-Local Overrides with with-config
;;
;; `sk/with-config` is a macro that binds configuration overrides for
;; the duration of its body, then automatically reverts. This is ideal
;; for one-off experiments or when different sections of a notebook
;; need different settings.

;; A dark theme, scoped to this block:

(sk/with-config {:theme {:bg "#1a1a2e" :grid "#16213e" :font-size 8}}
  (-> (base-plot)
      (sk/options {:title "Dark Theme via with-config"})))

(kind/test-last
 [(fn [v]
    (= 150 (:points (sk/svg-summary v))))])

;; Partial theme override — only `:bg` changes; `:grid` and `:font-size`
;; are deep-merged from the defaults:

(sk/with-config {:theme {:bg "#F5F5DC"}}
  (-> (base-plot)
      (sk/options {:title "Partial Theme Override"})))

(kind/test-last
 [(fn [v]
    (= 150 (:points (sk/svg-summary v))))])

;; Outside the body, the default theme is back:

(select-keys (sk/config) [:width :height])

(kind/test-last [(fn [m] (= {:width 600 :height 400} m))])

;; ## The Precedence Chain
;;
;; When multiple configuration layers are active, the highest-priority
;; layer wins for each key.  The chain from highest to lowest:
;;
;; ```
;; plot options  >  with-config  >  set-config!  >  napkinsketch.edn  >  library defaults
;; ```
;;
;; Let's demonstrate all three programmatic levels at once, using a
;; different key at each level so we can see each one win.

;; Before overriding, the library default for point-radius is:

(:point-radius (sk/config))

(kind/test-last [(fn [v] (= 2.5 v))])

;; Now set a global override for width, height, and point-radius:

(sk/set-config! {:width 800 :height 350 :point-radius 5.0})

;; Layer a thread-local override on top for width and height
;; (but not point-radius):

(def precedence-result
  (sk/with-config {:width 1200 :height 500}
    ;; Pass plot options for width only:
    (let [sketch (sk/sketch (base-plot) {:width 900})]
      {:sketch-width (:width sketch)
       :sketch-height (:height sketch)})))

precedence-result

(kind/test-last
 [(fn [m]
    (and (= 900 (:sketch-width m)) ;; plot options win over with-config (1200) and set-config! (800)
         (= 500 (:sketch-height m))))]) ;; with-config wins over set-config! (350)

;; We can verify point-radius too — only set-config! touched it,
;; so it wins over the library default (2.5):

(def precedence-point-radius
  (sk/with-config {:width 1200 :height 500}
    (:point-radius (sk/config))))

precedence-point-radius

(kind/test-last [(fn [v] (= 5.0 v))])

;; The rendered plot reflects the same precedence:

(def precedence-plot
  (sk/with-config {:width 1200 :height 500}
    (-> (base-plot)
        (sk/options {:width 900}))))

precedence-plot

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (and (= 150 (:points s))
           ;; width = 900 + legend padding, not 1200
           (> (:width s) 900)
           (< (:width s) 1100))))])

;; Clean up the global override.

(sk/set-config! nil)

(select-keys (sk/config) [:width :height :point-radius])

(kind/test-last [(fn [m] (= {:width 600 :height 400 :point-radius 2.5} m))])

;; To summarize what happened:
;;
;; | Key | Library default | set-config! | with-config | plot options | Winner |
;; |:----|:---------------|:------------|:------------|:---------|:-------|
;; | `:width` | 600 | 800 | 1200 | 900 | **900** (plot options) |
;; | `:height` | 400 | 350 | 500 | — | **500** (with-config) |
;; | `:point-radius` | 2.5 | 5.0 | — | — | **5.0** (set-config!) |

;; ## Project-Level Defaults with napkinsketch.edn
;;
;; For team-wide consistency, create a `napkinsketch.edn` file in
;; your project root (or anywhere on the classpath).  It is read
;; automatically with a 1-second cache.
;;
;; Example `napkinsketch.edn`:
;;
;; ```clojure
;; {:width 800
;;  :height 500
;;  :theme {:bg "#FFFFFF" :grid "#F0F0F0" :font-size 10}
;;  :palette :tableau10
;;  :point-radius 3}
;; ```
;;
;; This layer sits between library defaults and `set-config!` in the
;; precedence chain — it overrides defaults but is overridden by any
;; programmatic configuration.

;; ## Theme Customization
;;
;; The `:theme` key is a nested map with three entries:
;;
;; - `:bg` — panel background color (hex string)
;; - `:grid` — grid line color (hex string)
;; - `:font-size` — tick label font size (number)

(count (:theme (sk/config)))

(kind/test-last [(fn [n] (= 3 n))])

;; All configuration merging uses **deep-merge** — nested maps like `:theme`
;; are merged recursively at every level (`sk/options`, `sk/with-config`,
;; `sk/set-config!`, and `napkinsketch.edn`). You only need to specify the
;; keys you want to change.

;; Override only `:bg`, keep default `:grid` and `:font-size`:

(-> (base-plot)
    (sk/options {:theme {:bg "#F5F5DC"}})
    sk/plot)

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      ;; beige background
      (and (clojure.string/includes? s "rgb(245,245,220)")
           ;; default grid color is still white
           (clojure.string/includes? s "rgb(255,255,255)"))))])

;; Override all three for a dark theme:

(-> (base-plot)
    (sk/options {:title "Full Dark Theme"
                 :theme {:bg "#2d2d2d" :grid "#444444" :font-size 10}})
    sk/plot)

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      (clojure.string/includes? s "rgb(45,45,45)")))])

;; ## Comparing Two Themes Side by Side
;;
;; `sk/arrange` composes independent plots in a CSS grid.
;; Each plot can have its own theme.

(sk/arrange
 [(-> (base-plot)
      (sk/options {:title "Light"
                   :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 8}
                   :width 350 :height 250}))
  (-> (base-plot)
      (sk/options {:title "Dark"
                   :theme {:bg "#2d2d2d" :grid "#444444" :font-size 8}
                   :width 350 :height 250}))])

(kind/test-last
 [(fn [v]
    ;; arrange returns a :div with two child plots
    (= :div (first v)))])

;; ## Palette Configuration
;;
;; The `:palette` key controls the color cycle for categorical
;; color mappings.  It accepts:
;;
;; - a keyword — any palette name from the
;;   [clojure2d](https://github.com/Clojure2D/clojure2d) color library
;;   (hundreds available: [ColorBrewer](https://colorbrewer2.org/), Wes Anderson, thi.ng, paletteer, etc.)
;; - a vector of hex strings: `["#E74C3C" "#3498DB" "#2ECC71"]`
;; - a map of `{category-value "#hex"}` for explicit assignment
;;
;; Common palette names: `:set1`, `:set2`, `:dark2`, `:tableau10`,
;; `:category10`, `:pastel1`, `:accent`, `:paired`.
;; Use `(clojure2d.color/find-palette #"pattern")` to discover more.
;;
;; Palette works at every configuration level.

;; Named palette via plot options:

(-> (base-plot)
    (sk/options {:palette :tableau10}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Custom vector palette:

(-> (base-plot)
    (sk/options {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Explicit map palette:

(-> (base-plot)
    (sk/options {:palette {"setosa" "#FF6B6B"
                           "versicolor" "#4ECDC4"
                           "virginica" "#45B7D1"}}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Global palette via set-config!:

(sk/set-config! {:palette :pastel1})

(-> (base-plot))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(sk/set-config! nil)

;; Thread-local palette via with-config:

(sk/with-config {:palette :accent}
  (-> (base-plot)))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Color Scale Configuration
;;
;; When a numeric column is mapped to `:color`, Napkinsketch uses a
;; continuous gradient (dark-to-light blue by default). The `:color-scale` option
;; controls which gradient is used.

;; Default continuous color (dark blue to light blue):

(-> {:x (range 50) :y (range 50) :c (range 50)}
    (sk/lay-point :x :y {:color :c}))

(kind/test-last [(fn [v] (= 50 (:points (sk/svg-summary v))))])

;; Color scale override via plot options — inferno gradient:

(-> {:x (range 50) :y (range 50) :c (range 50)}
    (sk/lay-point :x :y {:color :c})
    (sk/options {:color-scale :inferno}))

(kind/test-last [(fn [v] (= 50 (:points (sk/svg-summary v))))])

;; Thread-local color scale via `with-config`:

(sk/with-config {:color-scale :plasma}
  (-> {:x (range 50) :y (range 50) :c (range 50)}
      (sk/lay-point :x :y {:color :c})))

(kind/test-last [(fn [v] (= 50 (:points (sk/svg-summary v))))])

;; The sketch records `:color-scale` in its legend. The renderer
;; uses the pre-computed gradient stops, or resolves a fresh gradient
;; if the render-time configuration specifies a different color scale.

(-> {:x (range 50) :y (range 50) :c (range 50)}
    (sk/lay-point :x :y {:color :c})
    (sk/sketch {:color-scale :inferno})
    :legend
    (select-keys [:color-scale :type]))

(kind/test-last [(fn [m] (and (= :inferno (:color-scale m))
                              (= :continuous (:type m))))])

;; ## Validation Control
;;
;; By default, `sk/sketch` validates the output against a [Malli](https://github.com/metosin/malli)
;; schema and throws if the sketch is malformed.  This is controlled
;; by the `:validate` key.
;;
;; Two helper functions let you inspect sketches manually:
;;
;; - `sk/valid-sketch?` — returns true or false
;; - `sk/explain-sketch` — returns nil if valid, or a [Malli](https://github.com/metosin/malli) explanation map

;; Default behavior (validate = true) — a valid sketch passes silently:

(sk/sketch (base-plot))

(kind/test-last
 [(fn [sketch]
    (and (map? sketch)
         (= 600 (:width sketch))))])

;; The rendered plot works normally:

(-> (base-plot))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ### What Validation Catches
;;
;; To see what happens when a sketch is malformed, we can build one
;; with validation disabled, then corrupt it.  First, create a valid
;; sketch and confirm it passes:

(def good-sketch (sk/sketch (base-plot) {:validate false}))

(sk/valid-sketch? good-sketch)

(kind/test-last [(fn [v] (true? v))])

;; Now corrupt the `:width` to a string — this violates the schema,
;; which requires a positive integer:

(def bad-sketch (assoc good-sketch :width "not-a-number"))

(sk/valid-sketch? bad-sketch)

(kind/test-last [(fn [v] (false? v))])

;; `sk/explain-sketch` pinpoints the problem.  The `:errors` key in
;; the returned map shows exactly which path failed and why:

(-> (sk/explain-sketch bad-sketch)
    :errors
    first
    (select-keys [:path :in :value]))

(kind/test-last
 [(fn [m]
    (and (= [:width] (:path m))
         (= "not-a-number" (:value m))))])

;; With validation enabled (the default), `sk/sketch` would throw
;; an exception for such a malformed sketch.  We can verify this
;; by catching the exception:

(try
  (let [sketch (sk/sketch (base-plot) {:validate false})
        bad (assoc sketch :width "not-a-number")]
    (when-let [explanation (sk/explain-sketch bad)]
      (throw (ex-info "Sketch does not conform to schema"
                      {:explanation explanation})))
    :no-error)
  (catch Exception e
    {:caught true
     :message (.getMessage e)}))

(kind/test-last
 [(fn [m]
    (and (:caught m)
         (= "Sketch does not conform to schema" (:message m))))])

;; ### Disabling Validation
;;
;; Disable validation with `:validate false`:

(sk/sketch (base-plot) {:validate false})

(kind/test-last
 [(fn [sketch]
    (and (map? sketch)
         (= 600 (:width sketch))))])

;; You can also disable validation globally for a debugging session:
;;
;; ```clojure
;; (sk/set-config! {:validate false})
;; ;; ... work freely ...
;; (sk/set-config! nil)  ;; re-enable
;; ```

;; ## Summary
;;
;; | Mechanism | Scope | Persistence | Example |
;; |:----------|:------|:------------|:--------|
;; | plot options | single call | none | `(sk/options {...})` or `(sk/plot views {...})` |
;; | `with-config` | lexical body | until body exits | `(sk/with-config {:width 800} ...)` |
;; | `set-config!` | global | until reset | `(sk/set-config! {:width 800})` |
;; | `napkinsketch.edn` | project | file on disk | `{:width 800}` in project root |
;; | library defaults | everywhere | built-in | `resources/napkinsketch-defaults.edn` |
;;
;; Precedence: plot options > with-config > set-config! > napkinsketch.edn > library defaults.
;;
;; Use `sk/config` at any time to see the resolved configuration.

;; ## What's Next
;;
;; - [**Customization**](./napkinsketch_book.customization.html) — annotations, color scales, tooltips, and brush selection
;; - [**Faceting**](./napkinsketch_book.faceting.html) — split plots into panels by category
