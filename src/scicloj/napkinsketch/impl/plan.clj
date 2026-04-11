(ns scicloj.napkinsketch.impl.plan
  "Views-to-plan pipeline: domains, ticks, legends, layout, and grid inference.
   Takes resolved flat view maps (from resolve-sketch) and produces a Plan
   record with all geometry needed for rendering."
  (:require [wadogo.scale :as ws]
            [java-time.api :as jt]
            [tablecloth.api :as tc]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.casting :as casting]
            [clojure.string :as str]
            [scicloj.napkinsketch.impl.defaults :as defaults]
            [scicloj.napkinsketch.impl.resolve :as resolve]
            [scicloj.napkinsketch.impl.stat :as stat]
            [scicloj.napkinsketch.impl.scale :as scale]
            [scicloj.napkinsketch.impl.coord :as coord]
            [scicloj.napkinsketch.impl.position :as position]
            [scicloj.napkinsketch.impl.extract :as extract]
            [scicloj.napkinsketch.impl.layout :as layout]
            [scicloj.napkinsketch.impl.sketch-schema :as ss]))

;; ---- Domain Helpers ----

(defn collect-domain
  "Collect and merge domains from stat results along axis-key.
   Throws if some stat results contribute numeric domains and others
   contribute categorical domains -- mixing the two on one axis is
   ambiguous."
  [stat-results axis-key scale-spec]
  (let [parsed (keep (fn [sr]
                       (when-let [d (axis-key sr)]
                         {:vals (if (and (= 2 (count d)) (number? (first d)))
                                  d
                                  (mapv str d))
                          :numeric? (and (= 2 (count d)) (number? (first d)))}))
                     stat-results)
        types (distinct (map :numeric? parsed))]
    (when (seq parsed)
      (when (> (count types) 1)
        (throw (ex-info (str "Cannot merge numeric and categorical domains on " axis-key
                             ". Each view must use a consistent column type for this axis.")
                        {:axis axis-key
                         :domains (mapv :vals parsed)})))
      (let [vals (mapcat :vals parsed)]
        (if (number? (first vals))
          (scale/pad-domain [(reduce min vals) (reduce max vals)] scale-spec)
          (distinct vals))))))

(defn compute-global-y-domain
  "Compute global y-domain from position-adjusted layers.
   Reads pre-computed :y0/:y1 from stacked layers. Extends domain to
   include 0 for marks that draw from baseline (area, lollipop, value-bar).
   Clamps the lower bound to 0 for these marks so padding doesn't extend
   below the baseline."
  [layers scale-spec]
  (let [fill-layers (filter #(= :fill (:position %)) layers)
        stack-layers (filter #(= :stack (:position %)) layers)
        zero-baseline-marks #{:lollipop :value-bar :area}
        needs-zero? (some #(zero-baseline-marks (:mark %)) layers)
        extend-to-zero (fn [[lo hi]] [(min 0.0 (double lo)) (max 0.0 (double hi))])]
    (cond
      ;; Fill mode: normalized to [0, 1]
      (seq fill-layers)
      [0.0 1.0]

      ;; Stack mode: read pre-computed y0/y1 values from adjusted layers
      (seq stack-layers)
      (let [;; Stacked rect: collect all y0 and y1 values
            rect-vals (for [l stack-layers
                            :when (:categories l)
                            g (:groups l)
                            {:keys [y0 y1]} (:counts g)
                            v [y0 y1]
                            :when v]
                        v)
            ;; Stacked area: all ys and y0s (already accumulated)
            area-vals (for [l stack-layers
                            :when (and (not (:categories l)) (:groups l))
                            g (:groups l)
                            y (concat (:ys g) (or (:y0s g) []))]
                        y)
            ;; Other (non-stacked) layers: use their y-domain
            other-yd (mapcat (fn [l]
                               (when-not (#{:stack :fill} (:position l))
                                 (:y-domain l)))
                             layers)
            ;; Always include 0 -- stacked bars are drawn from the baseline
            all-vals (concat rect-vals area-vals other-yd [0])
            lo (double (reduce min all-vals))
            hi (double (reduce max all-vals))]
        (if (< lo hi)
          (scale/pad-domain [lo hi] scale-spec)
          [0 1]))

      ;; Normal: collect y-domains from layers
      :else
      (let [all-yds (keep :y-domain layers)
            vals (mapcat (fn [d]
                           (if (and (= 2 (count d)) (number? (first d)))
                             d (map str d)))
                         all-yds)]
        (when (seq vals)
          (if (number? (first vals))
            (let [raw-lo (reduce min vals)
                  raw-hi (reduce max vals)]
              (if needs-zero?
                ;; Baseline marks: include zero, only pad away from zero.
                (let [lo (min 0.0 raw-lo)
                      hi (max 0.0 raw-hi)
                      [plo phi] (scale/pad-domain [lo hi] scale-spec)]
                  [(if (>= raw-lo 0.0) 0.0 plo)
                   (if (<= raw-hi 0.0) 0.0 phi)])
                (scale/pad-domain [raw-lo raw-hi] scale-spec)))
            (distinct vals)))))))

;; ---- Tick Computation ----

(defn- merge-temporal-extents
  "Merge temporal extents from multiple views into a single [min max] pair."
  [extents]
  (let [extents (remove nil? extents)]
    (when (seq extents)
      [(apply jt/min (map first extents))
       (apply jt/max (map second extents))])))

(defn compute-ticks
  "Compute tick values and labels for a domain+pixel range, using wadogo transiently.
   When temporal-extent is provided (a [min max] pair of temporal objects),
   uses wadogo :datetime scale for calendar-aware ticks and formatting.
   When `scale-spec` contains `:breaks` (a vector of numbers), those
   exact values are used as ticks instead of the auto-computed ones
   -- ggplot2's `scale_*_continuous(breaks = ...)` equivalent."
  ([domain pixel-range scale-spec spacing]
   (compute-ticks domain pixel-range scale-spec spacing nil))
  ([domain pixel-range scale-spec spacing temporal-extent]
   (if (scale/categorical-domain? domain)
     (let [s (scale/make-scale domain pixel-range scale-spec)]
       {:values (vec (ws/ticks s))
        :labels (mapv defaults/fmt-category-label (ws/ticks s))
        :categorical? true})
     (let [n (scale/tick-count (Math/abs (double (- (second pixel-range) (first pixel-range)))) spacing)
           log? (= :log (:type scale-spec))
           user-breaks (:breaks scale-spec)]
       (cond
         ;; User-supplied breaks override everything — use the exact values
         ;; they asked for, labelled with the same format the scale uses.
         (and user-breaks (sequential? user-breaks) (seq user-breaks))
         (let [vs (vec user-breaks)
               labels (if log?
                        (vec (scale/format-log-ticks vs))
                        (let [s (scale/make-scale domain pixel-range scale-spec)]
                          (vec (scale/format-ticks s vs))))]
           {:values vs :labels labels :categorical? false})

         temporal-extent
         ;; Temporal: use wadogo :datetime scale for calendar-aware ticks
         (if (= (first temporal-extent) (second temporal-extent))
           ;; Single-value temporal domain: one tick at the single value
           {:values [(first domain)] :labels [(str (first temporal-extent))] :categorical? false}
           (let [dt-scale (ws/scale :datetime {:domain temporal-extent :range [0.0 1.0]})
                 dt-ticks (ws/ticks dt-scale n)
                 labels (vec (ws/format dt-scale dt-ticks))
                 values (mapv resolve/temporal->epoch-ms dt-ticks)]
             {:values values :labels labels :categorical? false}))

         log?
         ;; Log: use ggplot2-style 1-2-5 nice breaks
         (let [ticks (scale/log-ticks domain n)
               labels (scale/format-log-ticks ticks)]
           {:values (vec ticks) :labels (vec labels) :categorical? false})

         :else
         ;; Linear: use wadogo
         (let [s (scale/make-scale domain pixel-range scale-spec)
               ticks (ws/ticks s n)
               labels (scale/format-ticks s ticks)]
           {:values (vec ticks) :labels (vec labels) :categorical? false}))))))

;; ---- Per-Panel Resolution ----

(defn- filter-log-nonpositive
  "Filter rows with non-positive values on log-scaled axes.
   When :x-scale or :y-scale is {:type :log}, removes rows where
   the corresponding column has values <= 0 and prints a warning.
   Throws a clear error if log scale is applied to non-numeric data.
   Returns the resolved view with filtered :data."
  [rv]
  (let [ds (:data rv)]
    (if-not (tc/dataset? ds)
      rv
      (let [x-log? (= :log (:type (:x-scale rv)))
            y-log? (= :log (:type (:y-scale rv)))
            x-col (when (and x-log? (keyword? (:x rv))) (:x rv))
            y-col (when (and y-log? (keyword? (:y rv))) (:y rv))
            _ (when (and x-col (ds x-col)
                         (not (casting/numeric-type? (dtype/elemwise-datatype (ds x-col)))))
                (throw (ex-info (str "Log scale requires numeric data, but column " x-col " is non-numeric.")
                                {:column x-col :type (dtype/elemwise-datatype (ds x-col))})))
            _ (when (and y-col (ds y-col)
                         (not (casting/numeric-type? (dtype/elemwise-datatype (ds y-col)))))
                (throw (ex-info (str "Log scale requires numeric data, but column " y-col " is non-numeric.")
                                {:column y-col :type (dtype/elemwise-datatype (ds y-col))})))
            n-before (tc/row-count ds)
            ds (if (and x-col (ds x-col))
                 (tc/select-rows ds (dfn/> (ds x-col) 0))
                 ds)
            ds (if (and y-col (ds y-col))
                 (tc/select-rows ds (dfn/> (ds y-col) 0))
                 ds)
            n-after (tc/row-count ds)
            removed (- n-before n-after)]
        (when (pos? removed)
          (let [axes (cond
                       (and x-col y-col) (str x-col " and " y-col)
                       x-col (str x-col)
                       :else (str y-col))]
            (println (str "Warning: Removed " removed " rows containing non-positive values (log scale on " axes ")."))))
        (if (pos? removed)
          (assoc rv :data ds)
          rv)))))

(defn- numeric-col-ref
  "If the value at `k` in resolved-view `rv` is a column ref that exists in
   `ds` and has a numeric dtype, return the resolved column name; else nil."
  [rv ds k]
  (let [v (get rv k)]
    (when (and v (resolve/column-ref? v))
      (let [col (resolve/resolve-col-name ds v)]
        (when (and col (ds col)
                   (casting/numeric-type? (dtype/elemwise-datatype (ds col))))
          col)))))

(defn- aesthetic-col
  "Look up an aesthetic column from a resolved view's :data, handling
   keyword/string column-name mismatches transparently. Returns nil when
   the view has no :data, no value at `k`, or the column doesn't exist."
  [view k]
  (let [ds (:data view)
        ref (get view k)]
    (when (and ds ref)
      (let [resolved-name (resolve/resolve-col-name ds ref)]
        (get ds resolved-name)))))

(defn- filter-infinities
  "Filter rows containing non-finite values on numeric x/y and numeric
   aesthetic columns (color/size/alpha/ymin/ymax/fill). Removes rows where
   any of these columns contain nil, NaN, Inf, or -Inf and prints an
   appropriate warning. Non-numeric and non-referenced columns are skipped.
   Returns the resolved view with filtered :data."
  [rv]
  (let [ds (:data rv)]
    (if-not (tc/dataset? ds)
      rv
      (let [numeric-cols (distinct
                          (keep #(numeric-col-ref rv ds %)
                                [:x :y :color :size :alpha :ymin :ymax :fill]))
            n-before (tc/row-count ds)
            ;; First pass: drop missing (nil)
            ds (if (seq numeric-cols)
                 (tc/drop-missing ds numeric-cols)
                 ds)
            n-after-missing (tc/row-count ds)
            n-missing (- n-before n-after-missing)
            ;; Second pass: drop NaN/Inf on each numeric column
            ds (reduce (fn [ds col]
                         (tc/select-rows ds (dfn/finite? (ds col))))
                       ds
                       numeric-cols)
            n-after (tc/row-count ds)
            n-infinite (- n-after-missing n-after)
            removed (+ n-missing n-infinite)]
        (when (pos? removed)
          (let [parts (cond-> []
                        (pos? n-missing) (conj (str n-missing " missing (nil/NaN)"))
                        (pos? n-infinite) (conj (str n-infinite " non-finite (Inf/-Inf)")))]
            (println (str "Warning: Removed " removed " rows with non-finite values: "
                          (str/join ", " parts) "."))))
        (if (pos? removed)
          (assoc rv :data ds)
          rv)))))

(defn resolve-panel-views
  "Resolve views and compute stats for a group of views belonging to one panel.
   If pre-resolved views are provided, skips resolve-view.
   Returns {:resolved [...] :stat-results [...] :layers [...]}."
  [panel-views all-colors cfg & {:keys [resolved]}]
  (let [resolved (or resolved (mapv (comp filter-log-nonpositive filter-infinities resolve/resolve-view) panel-views))
        stat-results (mapv #(stat/compute-stat (assoc % :cfg (merge cfg (:cfg %)))) resolved)
        raw-layers (vec (map (fn [rv sr]
                               (-> (resolve/map->Layer (extract/extract-layer rv sr all-colors cfg))
                                   (assoc :y-domain (:y-domain sr)
                                          :x-domain (:x-domain sr))))
                             resolved stat-results))
        layers (position/apply-positions raw-layers)]
    {:resolved resolved :stat-results stat-results :layers layers}))

(defn- collect-colors
  "Resolve views and collect color categories across all views.
   Attaches :__resolved to each view for downstream re-use.
   Filters infinite values and non-positive values on log-scaled axes.
   Returns {:resolved-all :numeric-color? :all-colors :color-cols :tagged-views}."
  [non-ann-views]
  (let [resolved-all (mapv (comp filter-log-nonpositive filter-infinities resolve/resolve-view) non-ann-views)
        tagged-views (mapv (fn [v rv] (assoc v :__resolved rv)) non-ann-views resolved-all)
        numeric-color? (some #(= :numerical (:color-type %)) resolved-all)
        all-colors (when-not numeric-color?
                     (let [color-views (filter #(and (resolve/column-ref? (:color %))
                                                     (:data %)) resolved-all)]
                       (when (seq color-views)
                         (vec (distinct (remove nil? (mapcat #(aesthetic-col % :color) color-views)))))))
        color-cols (distinct (keep #(when (resolve/column-ref? (:color %)) (:color %)) resolved-all))]
    {:resolved-all resolved-all
     :numeric-color? numeric-color?
     :all-colors all-colors
     :color-cols color-cols
     :tagged-views tagged-views}))

(defn- warn-palette-wrap!
  "Warn once if the number of color categories exceeds the resolved
   palette's size, since the mod-index scheme will silently reuse
   colors and mask 'two categories with the same color' as a bug."
  [all-colors cfg]
  (when (seq all-colors)
    (let [palette (:palette cfg)
          n-cats (count all-colors)
          pal-size (cond
                     (map? palette) nil ;; explicit mapping — no wrap possible
                     (sequential? palette) (count palette)
                     (keyword? palette) (count (defaults/resolve-palette palette))
                     :else (count (defaults/resolve-palette defaults/default-palette-name)))]
      (when (and pal-size (> n-cats pal-size))
        (println (str "Warning: " n-cats " color categories exceeds palette size "
                      pal-size ". Colors will repeat. Use a larger palette via "
                      ":palette, or reduce the number of categories."))))))

(defn- adjust-fixed-aspect
  "Adjust panel dimensions for coord :fixed so that 1 data unit = 1 data unit
   on both axes. Shrinks the larger dimension to match the data aspect ratio."
  [pw ph x-domain y-domain]
  (let [x-range (- (double (second x-domain)) (double (first x-domain)))
        y-range (- (double (second y-domain)) (double (first y-domain)))]
    (if (or (<= x-range 0) (<= y-range 0))
      {:pw pw :ph ph}
      (let [data-ratio (/ x-range y-range)
            panel-ratio (/ pw ph)]
        (if (> panel-ratio data-ratio)
          {:pw (* ph data-ratio) :ph ph}
          {:pw pw :ph (/ pw data-ratio)})))))

(defn- resolve-labels
  "Resolve effective title and axis labels.
   Title comes from opts only; axis labels fall back to view-level :x-label/:y-label
   (set via sk/options), then scale :label, then auto-inferred column name."
  [non-ann-views x-vars y-vars x-scale-spec y-scale-spec
   title x-label y-label auto-label?]
  (let [view-x-label (:x-label (first non-ann-views))
        view-y-label (:y-label (first non-ann-views))]
    {:eff-title title
     :eff-x-label (or x-label
                      view-x-label
                      (:label x-scale-spec)
                      (when auto-label?
                        (when-let [x (first x-vars)] (defaults/fmt-name x))))
     :eff-y-label (or y-label
                      view-y-label
                      (:label y-scale-spec)
                      (when auto-label?
                        (when-let [y (first y-vars)]
                          (when (not= y (first x-vars))
                            (defaults/fmt-name y)))))}))

(defn- finite-vals
  "Concatenate a seq of column buffers into a single Clojure vector with
   nil/NaN/Inf stripped. Returns nil when the result is empty. Used to
   compute min/max for numeric-aesthetic legends without tripping over
   missing values or boolean-typed all-nil columns."
  [bufs]
  (let [out (into [] (comp cat (filter #(and (some? %) (number? %) (Double/isFinite (double %))))) bufs)]
    (when (seq out) out)))

(def ^:private monochrome-marks
  "Marks that draw strokes or polygons in a single solid color per group
   and therefore can't show a continuous color ramp along the mark
   itself. When a numeric :color mapping is supplied on one of these
   marks, the mark still renders in a single color (there is no
   per-vertex coloring). We emit the legend anyway -- readers can
   still see which value the line's representative color maps to --
   but we print a one-line warning so users realise the gradient they
   see in the legend is not what's painted on the line."
  #{:line :step :area :density :stacked-area :lm :loess})

(defn- warn-monochrome-numeric-color!
  "Warn once per plan when a numeric :color is paired with a mark that
   cannot render per-point colors. The user's color column is being
   used only to pick a single representative color; they may have
   meant to set `:color-type :categorical` to split into groups."
  [resolved-all]
  (when-let [affected (seq (filter #(and (= :numerical (:color-type %))
                                         (contains? monochrome-marks (:mark %)))
                                   resolved-all))]
    (let [marks (vec (distinct (map :mark affected)))]
      (println (str "Warning: " marks " with a numeric :color render as a "
                    "single line per group. The color column picks one "
                    "representative color from the gradient legend; use "
                    ":color-type :categorical to split into multiple lines.")))))

(defn- build-legend
  "Build legend from resolved views and color info. Returns nil when the
   legend would be empty (no data, or all nil/NaN in the color column).
   `opts-title` overrides the inferred column-name title (from a
   user-supplied `:color-label` plot option)."
  [resolved-all numeric-color? all-colors color-cols cfg opts-title]
  (let [grad-fn (:gradient-fn cfg)
        title (or opts-title (first color-cols))]
    (cond
      numeric-color?
      (let [color-views (filter #(and (resolve/column-ref? (:color %))
                                      (:data %)) resolved-all)
            all-bufs (map #(aesthetic-col % :color) color-views)]
        (when-let [all-vals (finite-vals all-bufs)]
          (let [c-min (dfn/reduce-min all-vals)
                c-max (dfn/reduce-max all-vals)
                n-stops 20]
            {:title title
             :type :continuous
             :min c-min :max c-max
             :color-scale (:color-scale cfg)
             :stops (vec (for [i (range n-stops)
                               :let [t (/ (double i) (dec n-stops))]]
                           {:t t :color (grad-fn t)}))})))
      (seq all-colors)
      {:title title
       :entries (vec (for [cat all-colors]
                       {:label (defaults/fmt-category-label cat)
                        :color (defaults/color-for all-colors cat (:palette cfg))}))})))

(defn- nice-legend-values
  "Generate ~n nicely-rounded tick-like values spanning [lo, hi].
   Delegates to wadogo's linear scale so the breaks are 1/2/5-aligned
   (e.g., [17, 83] → [20 40 60 80] rather than [17.0 33.5 50.0 66.5 83.0]).
   Falls back to evenly-spaced rounded values when wadogo returns fewer
   than two ticks."
  [lo hi n]
  (let [lo (double lo) hi (double hi)]
    (if (= lo hi)
      [lo]
      (let [s (ws/scale :linear {:domain [lo hi] :range [0.0 1.0]})
            nice (vec (ws/ticks s n))]
        (if (>= (count nice) 2)
          nice
          ;; Fallback: evenly-spaced with enough decimals to distinguish
          ;; adjacent values. Preserves backward-compatible behavior for
          ;; pathological inputs (tiny spans, NaN-ish).
          (let [step (/ (- hi lo) (dec n))
                decimals (if (pos? step)
                           (min 6 (max 1 (+ 1 (long (Math/ceil (- (Math/log10 step)))))))
                           1)
                factor (Math/pow 10.0 decimals)]
            (mapv (fn [i]
                    (let [v (+ lo (* i step))]
                      (/ (Math/round (* v factor)) factor)))
                  (range n))))))))

(defn- build-size-legend
  "Build size legend when :size maps to a numerical column. Returns nil
   when all values are nil/NaN (suppressing the legend).
   `opts-title` overrides the inferred column-name title (from a
   user-supplied `:size-label` plot option)."
  [resolved-all opts-title]
  (let [size-views (filter #(and (resolve/column-ref? (:size %))
                                 (nil? (:fixed-size %))
                                 (:data %)) resolved-all)]
    (when (seq size-views)
      (let [size-col (:size (first size-views))
            all-bufs (map #(aesthetic-col % :size) size-views)]
        (when-let [all-vals (finite-vals all-bufs)]
          (let [s-min (dfn/reduce-min all-vals)
                s-max (dfn/reduce-max all-vals)
                span (max 1e-6 (- (double s-max) (double s-min)))
                values (nice-legend-values s-min s-max 5)]
            {:title (or opts-title size-col)
             :type :size
             :min s-min :max s-max
             :entries (vec (for [v values]
                             {:value v
                              :radius (+ 2.0 (* 6.0 (/ (- (double v) (double s-min)) span)))}))}))))))

(defn- build-alpha-legend
  "Build alpha legend when :alpha maps to a numerical column. Returns nil
   when all values are nil/NaN (suppressing the legend).
   `opts-title` overrides the inferred column-name title (from a
   user-supplied `:alpha-label` plot option)."
  [resolved-all opts-title]
  (let [alpha-views (filter #(and (resolve/column-ref? (:alpha %))
                                  (nil? (:fixed-alpha %))
                                  (:data %)) resolved-all)]
    (when (seq alpha-views)
      (let [alpha-col (:alpha (first alpha-views))
            all-bufs (map #(aesthetic-col % :alpha) alpha-views)]
        (when-let [all-vals (finite-vals all-bufs)]
          (let [a-min (dfn/reduce-min all-vals)
                a-max (dfn/reduce-max all-vals)
                span (max 1e-6 (- (double a-max) (double a-min)))
                values (nice-legend-values a-min a-max 5)]
            {:title (or opts-title alpha-col)
             :type :alpha
             :min a-min :max a-max
             :entries (vec (for [v values]
                             {:value v
                              :alpha (+ 0.2 (* 0.8 (/ (- (double v) (double a-min)) span)))}))}))))))

;; ---- Main Entry Point ----

(def ^:private polar-supported-marks
  "Marks that render correctly under polar coordinates."
  #{:point :bar :rect :text :rug})

(defn- validate-polar-marks
  "Check that all resolved views use marks compatible with polar coordinates.
   Throws an ex-info with details when an unsupported mark is found."
  [resolved-views coord-type]
  (when (= coord-type :polar)
    (doseq [v resolved-views
            :let [m (:mark v)]
            :when (and m (not (polar-supported-marks m)))]
      (throw (ex-info (str "Mark :" (name m) " is not supported with polar coordinates. "
                           "Supported polar marks: " (sort polar-supported-marks))
                      {:mark m :supported polar-supported-marks})))))

(defn- warn-conflicting-specs
  "Warn when views disagree about scale or coord specs.
   Only the first view's specs are used -- conflicting specs are silently ignored
   without this warning."
  [views]
  (let [x-types (distinct (keep (comp :type :x-scale) views))
        y-types (distinct (keep (comp :type :y-scale) views))
        coords (distinct (keep :coord views))]
    (when (> (count x-types) 1)
      (println (str "Warning: Views have conflicting x-scale types " (vec x-types)
                    ". Using first view's scale: " (first x-types) ".")))
    (when (> (count y-types) 1)
      (println (str "Warning: Views have conflicting y-scale types " (vec y-types)
                    ". Using first view's scale: " (first y-types) ".")))
    (when (> (count coords) 1)
      (println (str "Warning: Views have conflicting coord types " (vec coords)
                    ". Using first view's coord: " (first coords) ".")))))

;; ================================================================
;; Grid Layout
;; ================================================================

(defn- infer-grid
  "Infer grid structure from view groups.
   Each view group = one panel. Grid position determined by:
   - Faceted views: :facet-col -> grid col, :facet-row -> grid row
   - Non-faceted views: :x column -> grid col, :y column -> grid row
   Returns {:grid-cols N :grid-rows N :panels [{:views [...] :row R :col C ...}]}"
  [entry-groups {:keys [grid-cols grid-rows] :as user-grid}]
  (let [;; Detect faceting: check if any view group has :facet-col or :facet-row
        first-views (mapv #(first (:views %)) entry-groups)
        has-facet-col? (some :facet-col first-views)
        has-facet-row? (some :facet-row first-views)
        faceted? (or has-facet-col? has-facet-row?)

        ;; Collect grid axis values
        col-vals (if faceted?
                   (vec (distinct (keep :facet-col first-views)))
                   (or grid-cols (vec (distinct (map :x first-views)))))
        row-vals (if faceted?
                   (vec (distinct (keep :facet-row first-views)))
                   (or grid-rows (vec (distinct (map :y first-views)))))
        ;; For non-faceted with single x/y, use nil sentinels
        col-vals (if (empty? col-vals) [nil] col-vals)
        row-vals (if (empty? row-vals) [nil] row-vals)

        ;; Position each entry
        stack-counts (atom {})
        panels (vec
                (for [eg entry-groups
                      :let [v (first (:views eg))
                            ;; Determine grid position
                            [ci col-label]
                            (if has-facet-col?
                              (let [fc (:facet-col v)
                                    i (.indexOf ^java.util.List col-vals fc)]
                                [(max 0 i) (str fc)])
                              (let [xv (:x v)
                                    i (.indexOf ^java.util.List col-vals xv)
                                    ;; Only show col-label when multiple columns exist
                                    ;; (avoids redundant x-label on single-column layouts)
                                    show-label? (> (count col-vals) 1)]
                                [(max 0 i) (when (and xv show-label?) (defaults/fmt-name xv))]))
                            [ri row-label]
                            (if has-facet-row?
                              (let [fr (:facet-row v)
                                    i (.indexOf ^java.util.List row-vals fr)]
                                [(max 0 i) (str fr)])
                              (let [yv (:y v)
                                    i (.indexOf ^java.util.List row-vals yv)
                                    ;; Only show row-label when multiple rows exist
                                    ;; (avoids redundant y-label on single-row facets)
                                    show-label? (> (count row-vals) 1)]
                                [(max 0 i) (when (and yv show-label?) (defaults/fmt-name yv))]))
                            ;; Stacking: when multiple view groups share the same
                            ;; grid position, offset each by one row below the base.
                            ;; sub=0 -> base position; sub>0 -> stacked below.
                            stack-key [ri ci]
                            sub (get @stack-counts stack-key 0)
                            _ (swap! stack-counts update stack-key (fnil inc 0))]]
                  {:views (:views eg)
                   :row (if (> sub 0) (+ (* ri (count col-vals)) sub) ri)
                   :col ci
                   :var-x (:x v) :var-y (:y v)
                   :col-label col-label
                   :row-label row-label}))
        ;; Compute actual grid dimensions
        max-row (if (seq panels) (inc (apply max (map :row panels))) 1)
        max-col (if (seq panels) (inc (apply max (map :col panels))) 1)
        layout-type (cond
                      faceted? :facet-grid
                      (and (= 1 max-row) (= 1 max-col)) :single
                      :else :multi-variable)]
    {:grid-cols max-col
     :grid-rows max-row
     :layout-type layout-type
     :x-vars (vec (distinct (map :x first-views)))
     :y-vars (vec (distinct (map :y first-views)))
     :facet-col-vals (when has-facet-col? col-vals)
     :facet-row-vals (when has-facet-row? row-vals)
     :panels panels}))

(defn- resolve-panel-domains
  "Given a panel-data map (with :stat-results, :layers, and :views),
   compute the oriented x/y domains, scale specs, and temporal extents.
   Applies the :coord :flip swap so downstream code doesn't have to.
   Does NOT compute ticks -- that happens after panel dimensions are
   known."
  [pd default-x-scale default-y-scale default-coord]
  (let [local-srs (:stat-results pd)
        local-layers (:layers pd)
        panel-view (first (:views pd))
        x-scale-spec (or (:x-scale panel-view) default-x-scale)
        y-scale-spec (or (:y-scale panel-view) default-y-scale)
        coord-type (or (:coord panel-view) default-coord)
        x-dom (or (:domain x-scale-spec)
                  (collect-domain local-srs :x-domain x-scale-spec))
        y-dom (or (:domain y-scale-spec)
                  (compute-global-y-domain local-layers y-scale-spec)
                  [0 1])
        [x-dom' y-dom'] (if (= coord-type :flip)
                          [y-dom x-dom]
                          [x-dom y-dom])
        [x-sspec' y-sspec'] (if (= coord-type :flip)
                              [y-scale-spec x-scale-spec]
                              [x-scale-spec y-scale-spec])
        resolved-views (:resolved pd)
        x-temp-ext (merge-temporal-extents (map :x-temporal-extent resolved-views))
        y-temp-ext (merge-temporal-extents (map :y-temporal-extent resolved-views))
        [x-te y-te] (if (= coord-type :flip)
                      [y-temp-ext x-temp-ext]
                      [x-temp-ext y-temp-ext])]
    {:x-dom x-dom'
     :y-dom y-dom'
     :x-scale x-sspec'
     :y-scale y-sspec'
     :coord coord-type
     :x-te x-te
     :y-te y-te
     :layers (or local-layers [])
     :row (:row pd)
     :col (:col pd)
     :row-label (:row-label pd)
     :col-label (:col-label pd)
     :var-x (:var-x pd)
     :var-y (:var-y pd)}))

(defn- finalize-panel
  "Given a pre-tick panel domain map and pixel dimensions, compute the
   tick sets for both axes and assemble the final panel map."
  [{:keys [x-dom y-dom x-scale y-scale coord x-te y-te
           layers row col row-label col-label var-x var-y]}
   pw ph m cfg annotations]
  (let [x-px [m (- pw m)]
        y-px [(- ph m) m]
        x-ticks (when x-dom (compute-ticks x-dom x-px x-scale (:tick-spacing-x cfg) x-te))
        y-ticks (when y-dom (compute-ticks y-dom y-px y-scale (:tick-spacing-y cfg) y-te))]
    (cond-> {:x-domain (vec (if (sequential? x-dom) x-dom [x-dom]))
             :y-domain (vec (if (sequential? y-dom) y-dom [y-dom]))
             :x-scale x-scale
             :y-scale y-scale
             :coord coord
             :x-ticks (or x-ticks {:values [] :labels [] :categorical? false})
             :y-ticks (or y-ticks {:values [] :labels [] :categorical? false})
             :layers layers
             :row row
             :col col}
      (seq annotations)
      (assoc :annotations
             (let [panel-anns
                   (filterv
                    (fn [a]
                      (and (or (nil? (:x a)) (= (:x a) var-x))
                           (or (nil? (:y a)) (= (:y a) var-y))))
                    annotations)]
               (mapv #(dissoc % :facet-col :facet-row :x :y) panel-anns)))
      row-label (assoc :row-label row-label)
      col-label (assoc :col-label col-label))))

(defn- build-fill-fallback-legend
  "If no color legend was built (no :color column), check for tile
   layers with computed fill ranges (:bin2d, :kde2d, or identity tiles
   with :fill). Returns a continuous legend map or nil."
  [panel-data resolved-all cfg]
  (let [stat-fill-range (some (fn [pd]
                                (some :fill-range (:stat-results pd)))
                              panel-data)
        stat-kind (when stat-fill-range
                    (some (fn [rv]
                            (when (#{:bin2d :kde2d} (:stat rv))
                              (:stat rv)))
                          resolved-all))
        view-fill-range (when-not stat-fill-range
                          (some (fn [rv]
                                  (when (and (= :tile (:mark rv)) (:fill rv) (:data rv))
                                    (let [vals ((:data rv) (:fill rv))]
                                      (when (seq vals)
                                        [(dfn/reduce-min vals) (dfn/reduce-max vals)]))))
                                resolved-all))
        [f-lo f-hi] (or stat-fill-range view-fill-range)]
    (when f-lo
      (let [grad-fn (:gradient-fn cfg)
            title (cond
                    (= stat-kind :bin2d) :count
                    (= stat-kind :kde2d) :relative-density
                    :else :fill)
            n-stops 20]
        {:title title
         :type :continuous
         :min f-lo :max f-hi
         :color-scale (:color-scale cfg)
         :stops (vec (for [i (range n-stops)
                           :let [t (/ (double i) (dec n-stops))]]
                       {:t t :color (grad-fn t)}))}))))

(defn views->plan
  "Pipeline: resolve views into a plan using entry-based grid layout.
   Each entry = one panel. Grid position from structural columns.

   New layout pipeline (2026-04-11): stats first, then scene → padding →
   dimensions, then per-panel ticks at the now-known panel dimensions.
   `:width`/`:height` are total SVG dimensions; panel dimensions are
   derived by subtracting layout overhead. See dev-notes/design-width-inference.md
   for the full design. `:panel-width`/`:panel-height` in opts are
   escape hatches that pin panel size on their axis."
  ([views] (views->plan views {}))
  ([views {:keys [x-label y-label title subtitle caption
                  scales legend-position grid-cols grid-rows] :as opts}]
   (let [cfg (defaults/resolve-config opts)
         cfg (assoc cfg :gradient-fn (defaults/resolve-gradient-fn (:color-scale cfg)))
         validate? (:validate cfg true)
         ;; Effective width/height: user opts override cfg. These are
         ;; total SVG dimensions under the new semantics.
         width (or (:width opts) (:width cfg))
         height (or (:height opts) (:height cfg))
         ;; Build the opts map that layout/compute-dims sees. It must
         ;; contain the effective :width, :height, and any explicit
         ;; :panel-width / :panel-height escape-hatch keys.
         layout-opts (assoc opts :width width :height height)
         views (if (map? views) [views] views)
         non-ann-views views

         ;; Group resolved views by source view index
         view-groups (vec
                      (for [[idx vs] (sort-by key (group-by :__entry-idx non-ann-views))]
                        {:entry-idx idx :views (vec vs)}))

         ;; Infer grid from view groups
         grid (infer-grid view-groups
                          (cond-> {}
                            grid-cols (assoc :grid-cols grid-cols)
                            grid-rows (assoc :grid-rows grid-rows)))
         {:keys [layout-type x-vars y-vars]} grid
         grid-rows-n (:grid-rows grid)
         grid-cols-n (:grid-cols grid)

         ;; Colors + warnings
         {:keys [resolved-all numeric-color? all-colors color-cols tagged-views]}
         (collect-colors non-ann-views)
         _ (warn-palette-wrap! all-colors cfg)
         _ (warn-monochrome-numeric-color! resolved-all)

         ;; Representative scale/coord (first view) for plot-level decisions
         default-x-scale {:type :linear}
         default-y-scale {:type :linear}
         default-coord :cartesian
         rep-x-scale (or (:x-scale (first non-ann-views)) default-x-scale)
         rep-y-scale (or (:y-scale (first non-ann-views)) default-y-scale)
         rep-coord (or (:coord (first non-ann-views)) default-coord)
         _ (validate-polar-marks resolved-all rep-coord)
         _ (warn-conflicting-specs non-ann-views)

         ;; Plot-level annotations
         annotations (mapv #(select-keys % [:mark :intercept :lo :hi :alpha :x :y])
                           (or (:annotations opts) []))

         ;; --- Phase 1: compute stats for every panel (no pixel math) ---
         tagged-by-idx (group-by :__entry-idx tagged-views)
         panel-data (mapv
                     (fn [pg]
                       (let [eidx (:__entry-idx (first (:views pg)))
                             panel-tagged (or (get tagged-by-idx eidx)
                                              (:views pg))
                             pre-resolved (mapv :__resolved panel-tagged)]
                         (if (seq panel-tagged)
                           (merge pg (resolve-panel-views panel-tagged all-colors cfg
                                                          :resolved pre-resolved))
                           pg)))
                     (:panels grid))

         ;; --- Phase 2: per-panel domains (still no pixel math) ---
         panel-domains (vec
                        (for [pd panel-data
                              :when (seq (:views pd))]
                          (resolve-panel-domains pd default-x-scale default-y-scale default-coord)))

         ;; Ridgeline swap: categories go on y, density on x. Swap
         ;; per-panel domains/scales/temporal extents before anything
         ;; reads them for layout or rendering.
         has-ridgeline? (some #(= :ridgeline (:mark %)) non-ann-views)
         panel-domains (if has-ridgeline?
                         (mapv (fn [d]
                                 (-> d
                                     (assoc :x-dom (:y-dom d) :y-dom (:x-dom d)
                                            :x-scale (:y-scale d) :y-scale (:x-scale d)
                                            :x-te (:y-te d) :y-te (:x-te d))))
                               panel-domains)
                         panel-domains)

         ;; --- Phase 3: labels, legends, and the three layout fns ---
         multi? (and (= layout-type :multi-variable) (> grid-cols-n 1) (> grid-rows-n 1))
         auto-label? (and (not multi?) (coord/show-ticks? rep-coord))
         {:keys [eff-title eff-x-label eff-y-label]}
         (resolve-labels non-ann-views x-vars y-vars rep-x-scale rep-y-scale
                         title x-label y-label auto-label?)
         swap-labels? (or (= rep-coord :flip) has-ridgeline?)
         [eff-x-label eff-y-label] (if swap-labels?
                                     [eff-y-label eff-x-label]
                                     [eff-x-label eff-y-label])

         ;; Legends -- depend on resolved views + cfg, not on pixel math.
         legend (build-legend resolved-all numeric-color? all-colors color-cols cfg (:color-label opts))
         legend (or legend (build-fill-fallback-legend panel-data resolved-all cfg))
         size-legend (build-size-legend resolved-all (:size-label opts))
         alpha-legend (build-alpha-legend resolved-all (:alpha-label opts))

         ;; Scene: everything compute-padding + compute-dims need to
         ;; know about the data and options, all data-derived or
         ;; opts-derived. No pixel math yet.
         scene (layout/compute-scene
                {:layout-type layout-type
                 :grid-rows grid-rows-n
                 :grid-cols grid-cols-n
                 :eff-title eff-title
                 :subtitle subtitle
                 :caption caption
                 :eff-x-label eff-x-label
                 :eff-y-label eff-y-label
                 :facet-row-vals (:facet-row-vals grid)
                 :facet-col-vals (:facet-col-vals grid)
                 :coord-type rep-coord
                 :panel-x-domains (mapv :x-dom panel-domains)
                 :panel-y-domains (mapv :y-dom panel-domains)
                 :x-scale-spec rep-x-scale
                 :y-scale-spec rep-y-scale
                 :x-temporal (some :x-te panel-domains)
                 :y-temporal (some :y-te panel-domains)
                 :panel-row-labels (mapv :row-label panel-domains)
                 :panel-col-labels (mapv :col-label panel-domains)
                 :legend legend
                 :size-legend size-legend
                 :alpha-legend alpha-legend})

         padding (layout/compute-padding scene cfg layout-opts)

         dims (layout/compute-dims scene padding cfg layout-opts)
         {:keys [pw ph total-w total-h]} dims

         ;; --- Phase 4: :coord :fixed aspect adjustment ---
         ;; When the user asked for a 1:1 data-unit aspect ratio, shrink
         ;; the larger panel axis so that one data unit on x equals one
         ;; data unit on y. This runs AFTER compute-dims so we have real
         ;; pw/ph to adjust. If the adjustment fires we also recompute
         ;; total-w/total-h to reflect the shrink.
         fixed-result (when (and (= rep-coord :fixed) (seq panel-domains))
                        (let [p1 (first panel-domains)
                              gx (:x-dom p1)
                              gy (:y-dom p1)]
                          (when (and (sequential? gx) (= 2 (count gx)) (number? (first gx))
                                     (sequential? gy) (= 2 (count gy)) (number? (first gy)))
                            (adjust-fixed-aspect pw ph gx gy))))
         [pw ph] (if fixed-result
                   [(:pw fixed-result) (:ph fixed-result)]
                   [pw ph])
         total-w (if fixed-result
                   (+ (:horiz-overhead dims) (* grid-cols-n pw))
                   total-w)
         total-h (if fixed-result
                   (+ (:vert-overhead dims) (* grid-rows-n ph))
                   total-h)

         ;; --- Phase 5: compute ticks at the final panel dimensions ---
         m (if multi? (:margin-multi cfg) (:margin cfg))
         panels (mapv #(finalize-panel % pw ph m cfg annotations) panel-domains)

         plan
         (resolve/map->Plan
          {:width width :height height :margin m
           :total-width total-w :total-height total-h
           :panel-width pw :panel-height ph
           :grid {:rows grid-rows-n :cols grid-cols-n}
           :layout-type layout-type
           :title eff-title :subtitle subtitle :caption caption
           :x-label eff-x-label :y-label eff-y-label
           :legend legend :size-legend size-legend :alpha-legend alpha-legend
           :legend-position (:legend-position padding)
           :panels panels
           :layout (select-keys padding [:x-label-pad :y-label-pad :title-pad
                                         :subtitle-pad :caption-pad
                                         :legend-w :legend-h :strip-h :strip-w])})]
     (when validate?
       (when-let [explanation (ss/explain plan)]
         (throw (ex-info "Plan does not conform to schema" {:explanation explanation}))))
     plan)))
