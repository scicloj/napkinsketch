(ns scicloj.napkinsketch.impl.defaults
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure2d.color :as c]))

;; ---- Palette and Theme ----

(defn c2d->rgba
  "Convert a clojure2d color (Vec4, 0-255 channels) to [r g b a] in 0-1 range."
  [color]
  (let [cc (c/to-color color)]
    [(/ (double (c/red cc)) 255.0)
     (/ (double (c/green cc)) 255.0)
     (/ (double (c/blue cc)) 255.0)
     (/ (double (c/alpha cc)) 255.0)]))

(def default-palette-name
  "Default categorical palette name (clojure2d palette keyword)."
  :set1)

(def ^:private palette-aliases
  "Short aliases for clojure2d palette names that differ from our old naming."
  {:tableau10 :tableau-10})

(defn- resolve-palette
  "Resolve a keyword to a clojure2d palette, trying aliases.
   Returns a non-empty palette vector, falling back to the default palette."
  [k]
  (let [pal (c/palette (get palette-aliases k k))]
    (if (seq pal) pal (c/palette default-palette-name))))

(def theme
  "Default theme: background color, grid color, and font size."
  {:bg "#EBEBEB" :grid "#FFFFFF" :font-size 8})

;; ---- Visual Defaults ----

(def defaults
  "Default configuration: layout dimensions, spacing, visual properties."
  {;; Layout
   :width 600 :height 400
   :margin 25 :margin-multi 30 :panel-size 200 :legend-width 100
   ;; Ticks
   :tick-spacing-x 60 :tick-spacing-y 40
   ;; Points
   :point-radius 2.5 :point-opacity 0.7
   :point-stroke "none" :point-stroke-width 0
   ;; Bars and lines
   :bar-opacity 0.7 :line-width 2 :grid-stroke-width 1.5
   ;; Annotations
   :annotation-stroke "#333" :annotation-dash [4 3] :band-opacity 0.15
   ;; Statistics
   :bin-method :sturges
   :domain-padding 0.05
   ;; Labels and titles
   :label-font-size 11 :title-font-size 13
   :label-offset 18 :title-offset 18
   ;; Facet strips
   :strip-font-size 10 :strip-height 16
   ;; Fallback
   :default-color "#333"})

;; ---- Column Keys ----

(def column-keys
  "Set of keywords that can reference dataset columns in view maps."
  #{:x :y :color :size :alpha :shape :group :text :ymin :ymax})

;; ---- Shape Symbols ----

(def shape-syms
  "Available shape symbols for categorical shape mapping."
  [:circle :square :triangle :diamond])

(def legend-swatch-size
  "Side length of legend color swatches (square, in pixels)."
  8)

;; ---- Color Helpers ----

(defn hex->rgba
  "Convert any color representation to [r g b a] in 0-1 range.
   Accepts hex strings (#RGB, #RRGGBB, #RRGGBBAA, or without #),
   named color strings (\"red\", \"steelblue\"), keywords (:red, :darkblue),
   or any value that clojure2d.color/to-color understands."
  [color]
  (if (and (string? color) (not (.startsWith ^String color "#")))
    ;; Non-# string: try as hex first, then as named color keyword
    (let [cc (try (c/to-color color)
                  (catch NumberFormatException _ nil))]
      (if cc
        (c2d->rgba cc)
        (let [cc (c/to-color (keyword color))]
          (if cc
            (c2d->rgba cc)
            (throw (ex-info (str "Unknown color: \"" color
                                 "\". Use a hex string like \"#FF0000\" or a CSS color name like \"red\".")
                            {:color color}))))))
    (c2d->rgba color)))

(defn color-for
  "Look up the color for a categorical value from the palette.
   Returns [r g b a] in 0-1 range.
   palette can be: nil (default), a keyword (any clojure2d palette name),
   a vector of hex strings, or a map of {category-value color}."
  ([categories val]
   (color-for categories val nil))
  ([categories val palette]
   (let [idx (if categories (.indexOf ^java.util.List categories val) -1)
         idx (if (neg? idx) 0 idx)]
     (if (map? palette)
       ;; Explicit mapping: look up value, fall back to index in default palette
       (if-let [cv (get palette val)]
         (hex->rgba cv)
         (let [pal (resolve-palette default-palette-name)]
           (c2d->rgba (nth pal (mod idx (count pal))))))
       ;; Index-based: keyword → c/palette, vector → use directly, nil → default
       (cond
         (keyword? palette)
         (let [pal (resolve-palette palette)]
           (c2d->rgba (nth pal (mod idx (count pal)))))
         (sequential? palette)
         (hex->rgba (nth palette (mod idx (count palette))))
         :else
         (let [pal (resolve-palette default-palette-name)]
           (c2d->rgba (nth pal (mod idx (count pal))))))))))

;; ---- Continuous Color ----

(defn- wrap-gradient
  "Wrap a clojure2d gradient function to return [r g b a] in 0-1 range."
  [g]
  (fn [t] (c2d->rgba (g t))))

(def gradient-color
  "Default gradient function (dark blue → light blue, matching ggplot2).
   Takes t in [0,1], returns [r g b a] 0-1."
  (wrap-gradient (c/gradient [(c/to-color "#132B43") (c/to-color "#56B1F7")])))

(def diverging-color
  "Diverging gradient function (RdBu). Takes t in [0,1], returns [r g b a] 0-1."
  (wrap-gradient (c/gradient :grDevices/RdBu)))

(def ^:private gradient-aliases
  "Short aliases for common clojure2d gradient names."
  {:viridis :viridis/viridis :inferno :viridis/inferno
   :plasma :viridis/plasma :magma :viridis/magma
   :cividis :viridis/cividis :turbo :viridis/turbo
   :rocket :viridis/rocket :mako :viridis/mako
   :RdBu :grDevices/RdBu :RdYlBu :grDevices/RdYlBu
   :BrBG :grDevices/BrBG :coolwarm :pals/coolwarm})

(defn- resolve-gradient-name
  "Resolve a keyword to a clojure2d gradient, trying aliases then direct lookup."
  [k]
  (or (c/gradient (get gradient-aliases k k))
      (c/gradient k)))

(defn resolve-gradient-fn
  "Resolve a :color-scale option to a gradient function t→[r g b a] (0-1 range).
   nil or :sequential → dark blue to light blue (ggplot2 default).
   :diverging → RdBu.
   keyword → clojure2d gradient name (:inferno, :viridis/plasma, etc.).
   map {:low hex :mid hex :high hex} → custom 3-stop gradient.
   function → used directly."
  [color-scale]
  (cond
    (nil? color-scale) gradient-color
    (= :sequential color-scale) gradient-color
    (= :diverging color-scale) diverging-color
    (fn? color-scale) color-scale
    (keyword? color-scale)
    (if-let [g (resolve-gradient-name color-scale)]
      (wrap-gradient g)
      gradient-color)
    (map? color-scale)
    (let [{:keys [low mid high]
           :or {low "#B2182B" mid "#F7F7F7" high "#2166AC"}} color-scale
          g (c/gradient [(c/to-color low) (c/to-color mid) (c/to-color high)])]
      (wrap-gradient g))
    :else gradient-color))

(defn normalize-midpoint
  "Remap a value v from [vmin, vmax] to [0,1] with optional midpoint.
   Without midpoint: linear (v-vmin)/(vmax-vmin).
   With midpoint: values below midpoint → [0, 0.5], above → [0.5, 1.0]."
  [v vmin vmax midpoint]
  (if midpoint
    (let [v (double v) vmin (double vmin) vmax (double vmax) mid (double midpoint)]
      (cond
        (<= v vmin) 0.0
        (>= v vmax) 1.0
        (<= v mid) (if (<= mid vmin) 0.5 (* 0.5 (/ (- v vmin) (- mid vmin))))
        :else (if (>= mid vmax) 0.5 (+ 0.5 (* 0.5 (/ (- v mid) (- vmax mid)))))))
    (let [span (- (double vmax) (double vmin))]
      (if (<= span 0) 0.5 (/ (- (double v) (double vmin)) span)))))

;; ---- Name Formatting ----

(defn fmt-name
  "Format a keyword as a readable name: :sepal-length -> \"sepal length\"."
  [k]
  (str/replace (name k) #"[-_]" " "))

;; ---- Configuration Precedence Chain ----
;;
;; Resolved with precedence (highest to lowest):
;;   1. per-call opts (passed to sk/plot, sk/sketch, etc.)
;;   2. binding *config* (thread-local override)
;;   3. set-config! (global mutable state)
;;   4. napkinsketch.edn (project root or classpath)
;;   5. library defaults (napkinsketch-defaults.edn)

(def ^:private library-defaults
  "Library defaults loaded from napkinsketch-defaults.edn on classpath.
   Falls back to the static `defaults` and `theme` maps if the resource is missing."
  (delay
    (if-let [r (io/resource "napkinsketch-defaults.edn")]
      (edn/read-string (slurp r))
      (merge defaults {:theme theme}))))

(def ^:dynamic *config*
  "Dynamic var for thread-local config overrides.
   Bind to a map to override any config keys for the current thread.
   (binding [defaults/*config* {:theme {:bg \"#FFF\"}}] ...)"
  nil)

(defonce ^:private config-atom
  (atom nil))

(defn set-config!
  "Set global config overrides. Persists across calls until reset.
   (set-config! {:palette :dark2 :theme {:bg \"#FFFFFF\"}})
   (set-config! nil)  — reset to defaults"
  [m]
  (reset! config-atom m))

(def ^:private edn-cache
  "TTL cache for napkinsketch.edn (1 second)."
  (atom {:value nil :timestamp 0}))

(defn- read-napkinsketch-edn
  "Read napkinsketch.edn from classpath or the current working directory.
   Returns nil if the file does not exist. Cached with 1-second TTL."
  []
  (let [{:keys [value timestamp]} @edn-cache
        now (System/currentTimeMillis)]
    (if (< (- now timestamp) 1000)
      value
      (let [from-cp (io/resource "napkinsketch.edn")
            from-cwd (let [f (io/file "napkinsketch.edn")]
                       (when (.exists f) f))
            source (or from-cp from-cwd)
            v (when source (edn/read-string (slurp source)))]
        (reset! edn-cache {:value v :timestamp now})
        v))))

(def config-key-docs
  "Documentation metadata for configuration keys.
   Each entry maps a key to [category description]."
  {:width ["Layout" "Plot width in pixels"]
   :height ["Layout" "Plot height in pixels"]
   :margin ["Layout" "Margin around single-panel plots (pixels)"]
   :margin-multi ["Layout" "Margin around multi-panel plots (pixels)"]
   :panel-size ["Layout" "Default panel size for faceted/multi-variable grids"]
   :legend-width ["Layout" "Width reserved for the legend column"]
   :theme ["Theme" "Nested map {:bg :grid :font-size} — visual identity"]
   :label-font-size ["Typography" "Font size for axis labels"]
   :title-font-size ["Typography" "Font size for the plot title"]
   :strip-font-size ["Typography" "Font size for facet strip labels"]
   :point-radius ["Points" "Default point radius"]
   :point-opacity ["Points" "Default point opacity (0.0–1.0)"]
   :point-stroke ["Points" "Point border stroke color (\"none\" to disable)"]
   :point-stroke-width ["Points" "Point border stroke width"]
   :bar-opacity ["Bars & Lines" "Default bar fill opacity"]
   :line-width ["Bars & Lines" "Default line stroke width"]
   :grid-stroke-width ["Bars & Lines" "Grid line stroke width"]
   :annotation-stroke ["Annotations" "Stroke color for annotation marks"]
   :annotation-dash ["Annotations" "Dash pattern [dash gap] for annotation lines"]
   :band-opacity ["Annotations" "Opacity for confidence bands"]
   :tick-spacing-x ["Ticks" "Minimum pixel spacing between x-axis ticks"]
   :tick-spacing-y ["Ticks" "Minimum pixel spacing between y-axis ticks"]
   :bin-method ["Statistics" "Histogram bin count method (:sturges, :sqrt, :rice, :fd)"]
   :domain-padding ["Statistics" "Fractional padding added to numeric domains"]
   :label-offset ["Labels" "Pixel offset for axis labels from the axis"]
   :title-offset ["Labels" "Pixel offset for the title from the top"]
   :strip-height ["Labels" "Height of facet strip label bars"]
   :validate ["Behavior" "When true, validate sketches against Malli schema"]
   :default-color ["Behavior" "Fallback color when no color mapping is set"]})

(def per-call-key-docs
  "Documentation for per-call-only option keys (not in config).
   Accepted by sk/options, sk/sketch, and sk/plot but not part of the
   persistent configuration. Each entry maps a key to [category description]."
  {:title ["Content" "Plot title string"]
   :subtitle ["Content" "Plot subtitle string"]
   :caption ["Content" "Plot caption string (bottom)"]
   :x-label ["Content" "X-axis label (overrides inferred)"]
   :y-label ["Content" "Y-axis label (overrides inferred)"]
   :palette ["Color" "Categorical palette — keyword, vector, or map"]
   :color-scale ["Color" "Continuous color scale — :sequential, :diverging, or keyword"]
   :color-midpoint ["Color" "Center value for diverging color scales"]
   :legend-position ["Layout" "Legend placement — :right, :bottom, :top, or :none"]
   :tooltip ["Interaction" "Enable hover tooltips (truthy value)"]
   :brush ["Interaction" "Enable drag-to-select brush (truthy value)"]
   :format ["Output" "Render format — :svg (default)"]
   :config ["Config" "Nested config map merged into resolved config"]})

(defn config
  "Return the effective resolved configuration as a map.
   Merges: library defaults < napkinsketch.edn < set-config! < *config*.
   Useful for inspecting which values are in effect."
  []
  (let [base @library-defaults
        from-edn (read-napkinsketch-edn)
        from-atom @config-atom
        from-binding *config*]
    (cond-> base
      from-edn (merge from-edn)
      from-atom (merge from-atom)
      from-binding (merge from-binding))))

(defn resolve-config
  "Resolve config with per-call opts merged on top of the precedence chain.
   Per-call opts have the highest priority. Keys relevant to config are
   extracted; unknown keys are ignored."
  [per-call-opts]
  (let [cfg (config)]
    (if (seq per-call-opts)
      (let [{:keys [config width height palette theme
                    color-scale color-midpoint validate]} per-call-opts]
        (cond-> cfg
          config (merge config)
          width (assoc :width width)
          height (assoc :height height)
          palette (assoc :palette palette)
          theme (update :theme merge theme)
          (some? color-scale) (assoc :color-scale color-scale)
          (some? color-midpoint) (assoc :color-midpoint color-midpoint)
          (some? validate) (assoc :validate validate)))
      cfg)))
