;; # Configuration
;;
;; napkinsketch uses a layered configuration system.  Every visual
;; default — dimensions, font sizes, point radius, grid stroke,
;; annotation colors — can be overridden at multiple levels.
;; This notebook explains each level and how they compose.

(ns napkinsketch-book.configuration
  (:require
   ;; Tablecloth — dataset manipulation
   [tablecloth.api :as tc]
   ;; Kindly — notebook rendering protocol
   [scicloj.kindly.v4.kind :as kind]
   ;; Napkinsketch — composable plotting
   [scicloj.napkinsketch.api :as sk]))

;; We'll use the iris dataset throughout.

(def iris (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
                      {:key-fn keyword}))

(def ^:private base-views
  (fn [] (-> iris
             (sk/view [[:sepal_length :sepal_width]])
             (sk/lay (sk/point {:color :species})))))

;; ## Inspecting the Current Config
;;
;; `sk/config` returns the resolved configuration as a plain map.
;; It merges all active config layers into one map.

(sk/config)

(kind/test-last
 [(fn [cfg]
    (and (map? cfg)
         (= 600 (:width cfg))
         (= 400 (:height cfg))
         (= 25 (:margin cfg))
         (map? (:theme cfg))))])

;; The config contains keys grouped by purpose.
;;
;; **Layout:** `:width`, `:height`, `:margin`, `:margin-multi`, `:panel-size`, `:legend-width`
;;
;; **Theme:** `:theme` — a nested map `{:bg :grid :font-size}`
;;
;; **Typography:** `:label-font-size`, `:title-font-size`, `:strip-font-size`
;;
;; **Points:** `:point-radius`, `:point-opacity`, `:point-stroke`, `:point-stroke-width`
;;
;; **Bars and lines:** `:bar-opacity`, `:line-width`, `:grid-stroke-width`
;;
;; **Annotations:** `:annotation-stroke`, `:annotation-dash`, `:band-opacity`
;;
;; **Ticks:** `:tick-spacing-x`, `:tick-spacing-y`
;;
;; **Statistics:** `:bin-method`, `:domain-padding`
;;
;; **Labels:** `:label-offset`, `:title-offset`, `:strip-height`
;;
;; **Behavior:** `:validate`, `:default-color`

;; ## Per-Call Options
;;
;; The highest-priority mechanism. Pass an opts map to `sk/plot`
;; (or `sk/sketch`). Per-call opts override everything else.

;; Default 600×400:

(-> (base-views) (sk/plot))

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (and (= 150 (:points s))
           (< (:width s) 800))))])

;; Wide plot via per-call opts:

(-> (base-views) (sk/plot {:width 900 :height 250}))

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (and (= 150 (:points s))
           (> (:width s) 800))))])

;; Theme override — only the specified keys are changed.
;; Here we set a white background; `:grid` and `:font-size` are
;; preserved from the defaults.

(-> (base-views)
    (sk/plot {:theme {:bg "#FFFFFF"}}))

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      (clojure.string/includes? s "rgb(255,255,255)")))])

;; Palette override:

(-> (base-views)
    (sk/plot {:palette :dark2}))

(kind/test-last
 [(fn [v]
    (let [s (sk/svg-summary v)]
      (= 150 (:points s))))])

;; ## Global Overrides with set-config!
;;
;; `sk/set-config!` sets overrides that persist across calls — useful
;; when you want a consistent style for an entire session or notebook.

;; Set a global width override:

(sk/set-config! {:width 800})

(:width (sk/config))

(kind/test-last [(fn [v] (= 800 v))])

;; All subsequent plots pick it up automatically:

(-> (base-views) (sk/sketch))

(kind/test-last
 [(fn [sketch]
    (= 800 (:width sketch)))])

;; Reset to library defaults by passing `nil`:

(sk/set-config! nil)

(:width (sk/config))

(kind/test-last [(fn [v] (= 600 v))])

;; ## Thread-Local Overrides with with-config
;;
;; `sk/with-config` is a macro that binds config overrides for the
;; duration of its body, then automatically reverts.  This is ideal
;; for one-off experiments or when different sections of a notebook
;; need different settings.

(sk/with-config {:width 1000 :height 300}
  (:width (sk/config)))

(kind/test-last [(fn [v] (= 1000 v))])

;; Outside the `with-config` body, the value reverts:

(:width (sk/config))

(kind/test-last [(fn [v] (= 600 v))])

;; `with-config` works on the rendered plot too:

(sk/with-config {:theme {:bg "#1a1a2e" :grid "#16213e" :font-size 8}}
  (-> (base-views)
      (sk/plot {:title "Dark Theme via with-config"})))

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      (and (clojure.string/includes? s "rgb(26,26,46)")
           (clojure.string/includes? s "Dark Theme via with-config"))))])

;; ## The Precedence Chain
;;
;; When multiple config layers are active, the highest-priority
;; layer wins for each key.  The chain from highest to lowest:
;;
;; ```
;; per-call opts  >  with-config  >  set-config!  >  napkinsketch.edn  >  library defaults
;; ```
;;
;; Let's demonstrate all three programmatic levels at once.

;; Set a global override:

(sk/set-config! {:width 800 :height 350})

;; Layer a thread-local override on top:

(def ^:private precedence-result
  (sk/with-config {:width 1200}
    ;; per-call opts win for :width; with-config wins for :height
    (let [sketch (sk/sketch (base-views) {:width 900})]
      {:sketch-width (:width sketch)
       :sketch-height (:height sketch)})))

precedence-result

(kind/test-last
 [(fn [m]
    (and (= 900 (:sketch-width m))    ;; per-call wins
         (= 350 (:sketch-height m)))  ;; set-config! wins (with-config didn't set :height)
  )])

;; Clean up the global override.

(sk/set-config! nil)

;; To summarize what happened:
;;
;; | Key | Library default | set-config! | with-config | per-call | Winner |
;; |:----|:---------------|:------------|:------------|:---------|:-------|
;; | `:width` | 600 | 800 | 1200 | 900 | **900** (per-call) |
;; | `:height` | 400 | 350 | — | — | **350** (set-config!) |

;; ## Project-Level Defaults with napkinsketch.edn
;;
;; For team-wide consistency, create a `napkinsketch.edn` file in
;; your project root (or anywhere on the classpath).  It is read
;; automatically with a 1-second cache.
;;
;; Example `napkinsketch.edn`:
;;
;; ```edn
;; {:width 800
;;  :height 500
;;  :theme {:bg "#FFFFFF" :grid "#F0F0F0" :font-size 10}
;;  :palette :tableau10
;;  :point-radius 3}
;; ```
;;
;; This layer sits between library defaults and `set-config!` in the
;; precedence chain — it overrides defaults but is overridden by any
;; programmatic config.

;; ## Theme Customization
;;
;; The `:theme` key is a nested map with three entries:
;;
;; - `:bg` — panel background color (hex string)
;; - `:grid` — grid line color (hex string)
;; - `:font-size` — tick label font size (number)
;;
;; Per-call theme is **deep-merged** — you only need to specify the
;; keys you want to change.

;; Override only `:bg`, keep default `:grid` and `:font-size`:

(-> (base-views)
    (sk/plot {:theme {:bg "#F5F5DC"}}))

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      ;; beige background
      (and (clojure.string/includes? s "rgb(245,245,220)")
           ;; default grid color is still white
           (clojure.string/includes? s "rgb(255,255,255)"))))])

;; Override all three for a dark theme:

(-> (base-views)
    (sk/plot {:title "Full Dark Theme"
              :theme {:bg "#2d2d2d" :grid "#444444" :font-size 10}}))

(kind/test-last
 [(fn [v]
    (let [s (str v)]
      (clojure.string/includes? s "rgb(45,45,45)")))])

;; ## Comparing Two Themes Side by Side
;;
;; `sk/arrange` composes independent plots in a CSS grid.
;; Each plot can have its own per-call theme.

(sk/arrange
 [(-> (base-views)
      (sk/plot {:title "Light"
                :theme {:bg "#FFFFFF" :grid "#EEEEEE" :font-size 8}
                :width 350 :height 250}))
  (-> (base-views)
      (sk/plot {:title "Dark"
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
;; - a keyword (named preset): `:set1`, `:set2`, `:dark2`, `:tableau10`, etc.
;; - a vector of hex strings: `["#E74C3C" "#3498DB" "#2ECC71"]`
;; - a map of `{category-value "#hex"}` for explicit assignment
;;
;; Palette works at every config level.

;; Per-call named palette:

(-> (base-views)
    (sk/plot {:palette :tableau10}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Per-call custom vector palette:

(-> (base-views)
    (sk/plot {:palette ["#E74C3C" "#3498DB" "#2ECC71"]}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Per-call explicit map palette:

(-> (base-views)
    (sk/plot {:palette {"setosa" "#FF6B6B"
                        "versicolor" "#4ECDC4"
                        "virginica" "#45B7D1"}}))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; Global palette via set-config!:

(sk/set-config! {:palette :pastel1})

(-> (base-views) (sk/plot))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

(sk/set-config! nil)

;; Thread-local palette via with-config:

(sk/with-config {:palette :accent}
  (-> (base-views) (sk/plot)))

(kind/test-last
 [(fn [v] (= 150 (:points (sk/svg-summary v))))])

;; ## Validation Control
;;
;; By default, `sk/sketch` validates the output against a Malli
;; schema and throws if the sketch is malformed.  This is controlled
;; by the `:validate` key.

;; Default behavior (validate = true) — a valid sketch passes silently:

(sk/sketch (base-views))

(kind/test-last
 [(fn [sketch]
    (and (map? sketch)
         (= 600 (:width sketch))))])

;; Disable validation with `:validate false`:

(sk/sketch (base-views) {:validate false})

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
;; | per-call opts | single call | none | `(sk/plot views {:width 800})` |
;; | `with-config` | lexical body | until body exits | `(sk/with-config {:width 800} ...)` |
;; | `set-config!` | global | until reset | `(sk/set-config! {:width 800})` |
;; | `napkinsketch.edn` | project | file on disk | `{:width 800}` in project root |
;; | library defaults | everywhere | built-in | `resources/napkinsketch-defaults.edn` |
;;
;; Precedence: per-call > with-config > set-config! > napkinsketch.edn > library defaults.
;;
;; Use `sk/config` at any time to see the resolved configuration.
